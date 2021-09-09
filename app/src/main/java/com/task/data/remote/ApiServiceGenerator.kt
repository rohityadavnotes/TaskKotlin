package com.task.data.remote

import android.content.Context
import com.task.data.remote.ApiClient.getClient

object ApiServiceGenerator {
    /**
     * Retrofit Service Generator class which initializes the calling ApiService
     *
     * @param context -  getApplicationContext.
     * @param serviceClass -  The Retrofit Service Interface class.
     */
    fun <S> createService(context: Context?, serviceClass: Class<S>?): S {
        return getClient(RemoteConfiguration.BASE_URL)!!.create(serviceClass)
    }
}