package com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.domain.AlligatorCrocodileClassifier
import com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.domain.ClassificationException
import com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.domain.ClassificationResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import java.io.IOException
import javax.inject.Inject

class TfLiteAlligatorCrocodileClassifier @Inject constructor(
    @ApplicationContext private val context: Context,
) : AlligatorCrocodileClassifier {

    private var classifier: ImageClassifier? = null

    @Throws(IOException::class)
    private fun setupClassifier() {
//        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
//            .setScoreThreshold(.5f)
//            .setMaxResults(1)
//
//        classifier =
//            ImageClassifier.createFromFileAndOptions(context, MODEL_PATH, optionsBuilder.build())
    }

    @Throws(ClassificationException::class)
    override suspend fun classify(bitmap: Bitmap): ClassificationResult =
        withContext(Dispatchers.IO) {
            try {
                if (classifier == null) {
                    setupClassifier()
                }

                val imageProcessor = ImageProcessor.Builder().build()
                val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))

                val imageProcessingOptions = ImageProcessingOptions.builder()
                    .setOrientation(ImageProcessingOptions.Orientation.RIGHT_TOP)
                    .build()

                val results = classifier?.classify(tensorImage, imageProcessingOptions)

                results?.flatMap { classifications ->
                    classifications.categories.map { category ->
                        when (category.label) {
                            ALLIGATOR_LABEL -> ClassificationResult.Alligator(category.score)
                            CROCODILE_LABEL -> ClassificationResult.Crocodile(category.score)
                            else -> ClassificationResult.Unknown(category.score)
                        }
                    }
                }?.firstOrNull() ?: ClassificationResult.Unknown(1f)
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

    companion object {
        private const val MODEL_PATH = "model.tflite"

        private const val BITMAP_WIDTH_PX = 100
        private const val BITMAP_HEIGHT_PX = 100

        private const val ALLIGATOR_LABEL = "0"
        private const val CROCODILE_LABEL = "1"
    }
}