package com.tech.maxclub.alligatorvscrocodileclassifier.feature.classification.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
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

                val inputBuffer = preprocessImage(bitmap)
                val outputBuffer = ByteBuffer.allocateDirect(
                    OUTPUT_DTYPE_SIZE * TFLiteClassificationResult.entries.size
                )
                outputBuffer.order(ByteOrder.nativeOrder())

                if (interpreter != null) {
                    interpreter?.run(inputBuffer, outputBuffer)

                    outputBuffer.rewind()
                    val scores = FloatArray(TFLiteClassificationResult.entries.size)
                    outputBuffer.asFloatBuffer().get(scores)

                    val index = scores.indices.maxBy { scores[it] }
                    val score = scores[index]

                    TFLiteClassificationResult.entries[index].toClassificationResult(score)
                } else {
                    throw ClassificationException("The model is not configured.")
                }
            } catch (e: Exception) {
                throw if (e is CancellationException) {
                    e
                } else {
                    e.printStackTrace()
                    ClassificationException(e.localizedMessage)
                }
            }
        }

    override fun convertToBitmap(drawable: Drawable): Bitmap =
        drawable.toBitmap(
            width = BITMAP_WIDTH_PX,
            height = BITMAP_HEIGHT_PX,
        ).copy(Bitmap.Config.ARGB_8888, false)

    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(
            OUTPUT_DTYPE_SIZE * BITMAP_WIDTH_PX * BITMAP_HEIGHT_PX * IMG_CHANNELS
        )
        byteBuffer.order(ByteOrder.nativeOrder())
        byteBuffer.rewind()

        val pixels = IntArray(BITMAP_WIDTH_PX * BITMAP_HEIGHT_PX)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        for (pixelValue in pixels) {
            byteBuffer.putFloat((pixelValue shr 16 and 0xFF) * (1.0f / 255))
            byteBuffer.putFloat((pixelValue shr 8 and 0xFF) * (1.0f / 255))
            byteBuffer.putFloat((pixelValue and 0xFF) * (1.0f / 255))
        }

        return byteBuffer
    }

    companion object {
        private const val MODEL_PATH = "alligator-vs-crocodile.tflite"

        private const val BITMAP_WIDTH_PX = 224
        private const val BITMAP_HEIGHT_PX = 224

        private const val IMG_CHANNELS = 3
        private const val OUTPUT_DTYPE_SIZE = 4
    }
}