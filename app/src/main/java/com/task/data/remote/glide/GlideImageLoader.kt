package com.task.data.remote.glide

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

object GlideImageLoader {

    private val TAG: String = GlideImageLoader::class.java.simpleName

    /**
     * Load picture
     *
     * @param context
     * @param url
     * @param placeholder
     * @param error
     * @param imageView
     */
    fun load(context: Context, url: String, @DrawableRes placeholder: Int, @DrawableRes error: Int, imageView: ImageView, glideImageLoadingListener: GlideImageLoadingListener) {
        Glide.with(context)
            .load(url)
            .fitCenter()
            .placeholder(placeholder)
            .error(error)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>?, isFirstResource: Boolean): Boolean {
                    glideImageLoadingListener.imageLoadError()
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    glideImageLoadingListener.imageLoadSuccess()
                    return false
                }
            })
            .into(imageView)
    }
}