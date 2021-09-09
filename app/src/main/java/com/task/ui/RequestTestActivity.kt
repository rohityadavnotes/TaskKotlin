package com.task.ui

import android.os.Bundle
import com.task.R
import com.task.databinding.ActivityRequestTestBinding
import com.task.ui.base.BaseActivity
import io.reactivex.disposables.Disposable
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.task.data.remote.ApiService;
import com.task.data.remote.ApiServiceGenerator;
import com.task.data.remote.RemoteConfiguration;
import com.task.data.remote.glide.GlideCacheUtil;
import com.task.data.remote.glide.GlideImageLoader;
import com.task.data.remote.glide.GlideImageLoadingListener;
import com.task.data.remote.picasso.PicassoImageLoader;
import com.task.data.remote.picasso.PicassoImageLoadingListener;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

class RequestTestActivity : BaseActivity<ActivityRequestTestBinding>() {

    companion object {
        private val TAG = RequestTestActivity::class.java.simpleName
    }

    private lateinit var subscribe: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getViewBinding(): ActivityRequestTestBinding {
        return ActivityRequestTestBinding.inflate(layoutInflater)
    }

    override fun initializeObject() {
    }

    override fun initializeToolBar() {
    }

    override fun initializeCallbackListener() {
        GlideCacheUtil.clearAllCache(this)
        GlideImageLoader.load(
            this,
            "https://backend24.000webhostapp.com/Json/profile.jpg",
            R.drawable.user_placeholder,
            R.drawable.error_placeholder,
            binding.imageView,
            object : GlideImageLoadingListener {
                override fun imageLoadSuccess() {
                    binding.imageLoadingProgressBar.visibility = View.GONE
                }

                override fun imageLoadError() {
                    binding.imageLoadingProgressBar.visibility = View.GONE
                }
            })

        PicassoImageLoader.load(
            this,
            "https://backend24.000webhostapp.com/Json/profile.jpg",
            R.drawable.user_placeholder,
            R.drawable.error_placeholder,
            binding.imageView,
            object : PicassoImageLoadingListener {
                override fun imageLoadSuccess() {
                    binding.imageLoadingProgressBar.visibility = View.GONE
                }

                override fun imageLoadError(exception: Exception?) {
                    Toast.makeText(applicationContext, "An error occurred", Toast.LENGTH_SHORT).show()
                    binding.imageLoadingProgressBar.visibility = View.GONE
                }
            })
    }

    override fun addTextChangedListener() {
    }

    override fun setOnClickListener() {
        binding.requestMaterialButton.setOnClickListener {
            getSingleEmployee()
        }

        binding.cancelRequestMaterialButton.setOnClickListener {
            dispose(subscribe)
        }
    }

    private fun getSingleEmployee() {
        val apiService = ApiServiceGenerator.createService(this@RequestTestActivity, ApiService::class.java)

        val observable: Observable<Response<JsonObject>> = apiService.getEmployee()
        val observer: Observer<Response<JsonObject>> = object : Observer<Response<JsonObject>> {
            override fun onSubscribe(disposable: Disposable) {
                binding.requestProgressBar.visibility = View.VISIBLE
                subscribe = disposable
            }

            override fun onError(e: Throwable) {
                binding.requestProgressBar.visibility = View.GONE
                binding.responseTextView.text = e.message
            }

            override fun onNext(response: Response<JsonObject>) {
                binding.requestProgressBar.visibility = View.GONE
                if (response != null) {
                    if (response.body() != null && response.isSuccessful()) {
                        binding.responseTextView.setText(response.body().toString())
                    }
                }
            }

            override fun onComplete() {
            }
        }
        observable
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    private fun getPage() {
        val apiService = ApiServiceGenerator.createService(this@RequestTestActivity, ApiService::class.java)

        val observable: Observable<Response<JsonObject>> = apiService.getPage(RemoteConfiguration.API_KEY, "1")
        val observer: Observer<Response<JsonObject>> = object : Observer<Response<JsonObject>> {
            override fun onSubscribe(disposable: Disposable) {
                binding.requestProgressBar.visibility = View.VISIBLE
                subscribe = disposable
            }

            override fun onError(e: Throwable) {
                binding.requestProgressBar.visibility = View.GONE
                binding.responseTextView.text = e.message
            }

            override fun onNext(response: Response<JsonObject>) {
                binding.requestProgressBar.visibility = View.GONE
                if (response != null) {
                    if (response.body() != null && response.isSuccessful()) {
                        binding.responseTextView.setText(response.body().toString())
                    }
                }
            }

            override fun onComplete() {
            }
        }
        observable
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    override fun onDestroy() {
        super.onDestroy()
        dispose(subscribe)
    }

    /**
     * unsubscribe
     *
     * @param disposable subscription information
     */
    fun dispose(disposable: Disposable?) {
        if (disposable != null && !disposable.isDisposed) {
            disposable.dispose()
            Log.e(TAG, "Call dispose(Disposable disposable)")

            binding.requestProgressBar.visibility = View.GONE
            binding.responseTextView.text = "CANCEL"
        }
    }
}