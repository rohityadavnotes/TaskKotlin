package com.task.ui

import android.os.Bundle
import android.widget.Toast
import com.task.R
import com.task.databinding.ActivitySignInBinding
import com.task.model.BaseResponse
import com.task.model.Data
import com.task.ui.base.BaseActivity
import com.task.utilities.ActivityUtils
import com.task.utilities.ValidationUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import android.app.ProgressDialog
import android.text.Editable
import android.text.TextWatcher
import com.task.data.remote.ApiService
import com.task.data.remote.ApiServiceGenerator.createService
import io.reactivex.Observable
import io.reactivex.Observer
import retrofit2.Response

class SignInActivity : BaseActivity<ActivitySignInBinding>() {

    companion object {
        private val TAG = SignInActivity::class.java.simpleName
    }

    private lateinit var emailString: String
    private lateinit var passwordString: String
    private val fcmTokenString = "NJjMmJkNDAxCnBhY2thZ2VOYW1lPWNvbS5jYXJ0by5hZHZhbmNlZC5rb3RsaW4Kb25saW5lT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getViewBinding(): ActivitySignInBinding {
        return ActivitySignInBinding.inflate(layoutInflater)
    }

    override fun initializeObject() {
    }

    override fun initializeToolBar() {
    }

    override fun initializeCallbackListener() {
    }

    override fun addTextChangedListener() {
        binding.emailTextInputLayout.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length < 1)
                {
                    binding.emailTextInputLayout.isErrorEnabled = true
                    binding.emailTextInputLayout.error = getString(R.string.email_message_one)
                }
                else if (s.length > 0)
                {
                    binding.emailTextInputLayout.error = null
                    binding.emailTextInputLayout.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
                val emailValidCode = ValidationUtils.isValidEmail(binding.emailTextInputEditText.text.toString())
                if (emailValidCode > 0)
                {
                    if (emailValidCode == 1)
                    {
                        binding.emailTextInputLayout.error = getString(R.string.email_message_one)
                    }
                    else if (emailValidCode == 2)
                    {
                        binding.emailTextInputLayout.error = getString(R.string.email_message_two)
                    }
                }
            }
        })

        binding.passwordTextInputLayout.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length < 1)
                {
                    binding.passwordTextInputLayout.isErrorEnabled = true
                    binding.passwordTextInputLayout.error = getString(R.string.password_message_one)
                }
                else if (s.length > 0)
                {
                    binding.passwordTextInputLayout.error = null
                    binding.passwordTextInputLayout.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
                val passwordValidCode = ValidationUtils.isValidPassword(binding.passwordTextInputEditText.text.toString())
                if (passwordValidCode > 0)
                {
                    if (passwordValidCode == 1)
                    {
                        binding.passwordTextInputLayout.error = getString(R.string.password_message_one)
                    }
                    else if (passwordValidCode == 2)
                    {
                        binding.passwordTextInputLayout.error = getString(R.string.password_message_two)
                    }
                    else if (passwordValidCode == 3)
                    {
                        binding.passwordTextInputLayout.error = getString(R.string.password_message_three)
                    }
                    else if (passwordValidCode == 4)
                    {
                        binding.passwordTextInputLayout.error = getString(R.string.password_message_four)
                    }
                    else if (passwordValidCode == 5)
                    {
                        binding.passwordTextInputLayout.error = getString(R.string.password_message_five)
                    }
                    else if (passwordValidCode == 6)
                    {
                        binding.passwordTextInputLayout.error = getString(R.string.password_message_six)
                    }
                    else if (passwordValidCode == 7)
                    {
                        binding.passwordTextInputLayout.error = getString(R.string.password_message_seven)
                    }
                    else if (passwordValidCode == 8)
                    {
                        binding.passwordTextInputLayout.error = getString(R.string.password_message_eight)
                    }
                }
            }
        })
    }

    override fun setOnClickListener() {
        binding.appSignInMaterialButton.setOnClickListener {
            appSignIn()
        }

        binding.appSignUpLinkMaterialButton.setOnClickListener {
            launchSignUpScreen()
        }
    }

    fun appSignIn() {
        val progressDialog: ProgressDialog
        progressDialog = ProgressDialog(this@SignInActivity)
        progressDialog.setTitle("Loading")
        progressDialog.setMessage("Please wait...")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)

        emailString             = binding.emailTextInputEditText.getText().toString();
        passwordString          = binding.passwordTextInputEditText.getText().toString();

        if (validation(emailString, passwordString) == null)
        {
            val apiService = createService(this@SignInActivity, ApiService::class.java)

            val observable: Observable<Response<BaseResponse<Data>>> = apiService.signIn(emailString, passwordString, fcmTokenString)
            val observer: Observer<Response<BaseResponse<Data>>> = object : Observer<Response<BaseResponse<Data>>> {
                override fun onSubscribe(disposable: Disposable) {
                    progressDialog.show();
                }

                override fun onNext(response: Response<BaseResponse<Data>>) {
                    progressDialog.dismiss();
                    if (response != null) {
                        if (response.body() != null && response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), response.body()?.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                override fun onError(e: Throwable) {
                    progressDialog.dismiss();
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show();
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
        else
        {
            Toast.makeText(applicationContext, validation(emailString, passwordString), Toast.LENGTH_SHORT).show()
        }
    }

    fun launchSignUpScreen() {
        ActivityUtils.launchActivity(this@SignInActivity, SignUpActivity::class.java)
    }

    fun validation(email: String?, password: String?): String?
    {
        val emailValidCode = ValidationUtils.isValidEmail(email)
        val passwordValidCode = ValidationUtils.isValidPassword(password)

        if (emailValidCode > 0)
        {
            if (emailValidCode == 1)
            {
                return getString(R.string.email_message_one)
            }
            else if (emailValidCode == 2)
            {
                return getString(R.string.email_message_two)
            }
        }
        else if (passwordValidCode > 0)
        {
            if (passwordValidCode == 1)
            {
                return getString(R.string.password_message_one)
            }
            else if (passwordValidCode == 2)
            {
                return getString(R.string.password_message_two)
            }
            else if (passwordValidCode == 3)
            {
                return getString(R.string.password_message_three)
            }
            else if (passwordValidCode == 4)
            {
                return getString(R.string.password_message_four)
            }
            else if (passwordValidCode == 5)
            {
                return getString(R.string.password_message_five)
            }
            else if (passwordValidCode == 6)
            {
                return getString(R.string.password_message_six)
            }
            else if (passwordValidCode == 7)
            {
                return getString(R.string.password_message_seven)
            }
            else if (passwordValidCode == 8)
            {
                return getString(R.string.password_message_eight)
            }
        }
        return null
    }
}