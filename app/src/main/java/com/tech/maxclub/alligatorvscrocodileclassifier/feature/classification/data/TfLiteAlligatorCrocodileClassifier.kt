package com.tech.maxclub.alligatorvscrocodileclassifier.feature.classification.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import com.tech.maxclub.alligatorvscrocodileclassifier.feature.classification.domain.AlligatorCrocodileClassifier
import com.tech.maxclub.alligatorvscrocodileclassifier.feature.classification.domain.ClassificationException
import com.tech.maxclub.alligatorvscrocodileclassifier.feature.classification.domain.ClassificationResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.lang.IllegalArgumentException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import javax.inject.Inject

class TfLiteAlligatorCrocodileClassifier @Inject constructor(
    @ApplicationContext private val context: Context,
) : AlligatorCrocodileClassifier {

    private var interpreter: Interpreter? = null

    @Throws(IOException::class)
    private fun setupInterpreter() {
        val fileDescriptor = context.assets.openFd(MODEL_PATH)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val modelBuffer = inputStream.channel.map(
            FileChannel.MapMode.READ_ONLY,
            fileDescriptor.startOffset,
            fileDescriptor.declaredLength
        )

        interpreter = Interpreter(modelBuffer)
    }

    @Throws(ClassificationException::class)
    override suspend fun classify(bitmap: Bitmap): ClassificationResult =
        withContext(Dispatchers.IO) {
            try {
                if (interpreter == null) {
                    setupInterpreter()
                }

                interpreter?.let { interpreter ->
                    val inputShape = interpreter.getInputTensor(0).shape()
                    val inputBuffer = convertBitmapToByteBuffer(bitmap, inputShape)

                    val outputShape = interpreter.getOutputTensor(0).shape()
                    val outputClassSize = outputShape[1]
                    val output = Array(1) { FloatArray(outputClassSize) }

                    interpreter.run(inputBuffer, output)

                    val scores = output[0]
                    val index = scores.indices.maxBy { scores[it] }
                    val score = scores[index]

                    TFLiteClassificationResult.entries[index].toClassificationResult(score)
                } ?: throw ClassificationException("The model is not configured.")
            } catch (e: Exception) {
                throw if (e is CancellationException) {
                    e
                } else {
                    e.printStackTrace()
                    ClassificationException(e.localizedMessage)
                }
            }
        }

    @Throws(IllegalArgumentException::class)
    private fun convertBitmapToByteBuffer(bitmap: Bitmap, shape: IntArray): ByteBuffer {
        val sizeX = shape[1]
        val sizeY = shape[2]
        val channels = shape[3]

        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, sizeX, sizeY, true)
            .copy(Bitmap.Config.ARGB_8888, false)

        val size = resizedBitmap.width * resizedBitmap.height * channels * Float.SIZE_BYTES
        val byteBuffer = ByteBuffer.allocateDirect(size)
        byteBuffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(resizedBitmap.width * resizedBitmap.height)
        resizedBitmap.getPixels(
            pixels, 0, resizedBitmap.width, 0, 0, resizedBitmap.width, resizedBitmap.height
        )

        for (pixelValue in pixels) {
            byteBuffer.putFloat(Color.red(pixelValue) / 255f)
            byteBuffer.putFloat(Color.green(pixelValue) / 255f)
            byteBuffer.putFloat(Color.blue(pixelValue) / 255f)
        }

        return byteBuffer
    }

    companion object {
        private const val MODEL_PATH = "alligator-vs-crocodile.tflite"
    }
}