package com.task.data.remote.glide

import android.content.Context
import android.os.Looper
import android.text.TextUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import java.io.File
import java.math.BigDecimal

object GlideCacheUtil {

    /**
     * Clear image disk cache
     */
    fun clearDiskCache(context: Context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Thread { Glide.get(context.applicationContext).clearDiskCache() }.start()
            } else {
                Glide.get(context.applicationContext).clearDiskCache()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Clear image memory cache
     */
    fun clearMemoryCache(context: Context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Glide.get(context.applicationContext).clearMemory()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Clear all caches of pictures
     */
    fun clearAllCache(context: Context) {
        clearDiskCache(context)
        clearMemoryCache(context)
        deleteFolderFile(
            context.applicationContext.externalCacheDir.toString() + ExternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR,
            true
        )
    }

    /**
     * Get the image cache size caused by Glide
     * @return cacheSize
     */
    fun getCacheSize(context: Context): String {
        try {
            return getReadableFileSize(getFolderSize(File(context.applicationContext.cacheDir.toString() + "/" + InternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR)).toDouble())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * Get the sum of the size of all files in the specified folder
     * @param file file
     * @return file size
     * @throws Exception
     */
    @Throws(Exception::class)
    fun getFolderSize(file: File): Long {
        var size: Long = 0
        try {
            val fileList = file.listFiles()
            for (aFileList in fileList) {
                size = if (aFileList.isDirectory) {
                    size + getFolderSize(aFileList)
                } else {
                    size + aFileList.length()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }

    /**
     * Delete files in the specified directory
     * @param filePath file directory
     * @param deleteThisPath
     */
    fun deleteFolderFile(filePath: String?, deleteThisPath: Boolean) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                val file = File(filePath)
                if (file.isDirectory) {
                    val files = file.listFiles()
                    for (file1 in files) {
                        deleteFolderFile(file1.absolutePath, true)
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory) {
                        file.delete()
                    } else {
                        if (file.listFiles().size == 0) {
                            file.delete()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Get the file size in a human-readable string.
     *
     * @param size the size passed in
     * @return formatting unit returns the value after formatting
     */
    fun getReadableFileSize(size: Double): String {
        val kiloByte = size / 1024
        if (kiloByte < 1) {
            return "$size Byte"
        }
        val megaByte = kiloByte / 1024
        if (megaByte < 1) {
            val result1 = BigDecimal(java.lang.Double.toString(kiloByte))
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " KB"
        }
        val gigaByte = megaByte / 1024
        if (gigaByte < 1) {
            val result2 = BigDecimal(java.lang.Double.toString(megaByte))
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " MB"
        }
        val teraBytes = gigaByte / 1024
        if (teraBytes < 1) {
            val result3 = BigDecimal(java.lang.Double.toString(gigaByte))
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " GB"
        }
        val result4 = BigDecimal(teraBytes)
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " TB"
    }
}