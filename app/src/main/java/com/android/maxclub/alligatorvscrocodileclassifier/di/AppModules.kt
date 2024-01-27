package com.android.maxclub.alligatorvscrocodileclassifier.di

import com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.data.TfLiteAlligatorCrocodileClassifier
import com.android.maxclub.alligatorvscrocodileclassifier.feature.classification.domain.AlligatorCrocodileClassifier
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AlligatorCrocodileClassifierModule {

    @Binds
    @Singleton
    abstract fun bindAlligatorCrocodileClassifier(
        classifier: TfLiteAlligatorCrocodileClassifier
    ): AlligatorCrocodileClassifier
}