package com.task.data.remote.picasso

interface PicassoImageLoadingListener {
    fun imageLoadSuccess()
    fun imageLoadError(exception: Exception?)
}