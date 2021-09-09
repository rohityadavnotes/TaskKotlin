package com.task.data.remote.picasso

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

object PicassoImageLoader {

    private val TAG: String = PicassoImageLoader::class.java.simpleName

    /**
     * Load picture
     *
     * @param context
     * @param url
     * @param placeholder
     * @param error
     * @param imageView
     */
    fun load(context: Context, url: String, @DrawableRes placeholder: Int, @DrawableRes error: Int, imageView: ImageView, picassoImageLoadingListener: PicassoImageLoadingListener) {
        Picasso.get()
            .load(url)
            .placeholder(placeholder)
            .error(error)
            .into(imageView, object : Callback {
                override fun onSuccess() {
                    picassoImageLoadingListener.imageLoadSuccess()
                }

                override fun onError(exception: Exception) {
                    picassoImageLoadingListener.imageLoadError(exception)
                }
            })
    }
}