package com.task.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.rilixtech.widget.countrycodepicker.CountryCodePicker
import com.task.data.remote.ApiServiceGenerator
import com.task.data.remote.RequestUtils
import com.task.databinding.ActivitySignUpBinding
import com.task.utilities.permissionutils.ManagePermission
import com.task.utilities.permissionutils.PermissionName
import com.task.utilities.string.StringUtils
import android.os.Bundle
import android.widget.Toast
import com.task.R
import com.task.model.BaseResponse
import com.task.model.Data
import com.task.ui.base.BaseActivity
import com.task.utilities.ActivityUtils
import com.task.utilities.ValidationUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import android.text.Editable
import android.text.TextWatcher
import com.task.data.remote.ApiService
import io.reactivex.Observable
import io.reactivex.Observer
import retrofit2.Response
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.CheckBox;
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull;
import com.task.BuildConfig;
import com.task.utilities.FileProviderUtils;
import com.task.utilities.ImageAndVideoUtils;
import com.task.utilities.ImplicitIntentUtils;
import com.task.utilities.MemoryUnitUtils;
import com.task.utilities.RealPathUtils;
import com.task.utilities.SharedFileUtils;
import com.task.utilities.compress.ConfigureCompression;
import com.task.utilities.permissionutils.PermissionDialog;
import java.io.File;
import java.util.Objects;

class SignUpActivity : BaseActivity<ActivitySignUpBinding>() {

    companion object {
        private val TAG = SignUpActivity::class.java.simpleName
    }

    private lateinit var pictureRealPathString: String
    private lateinit var firstNameString: String
    private lateinit var lastNameString: String
    private lateinit var genderString: String
    private var countryCodeString = "91"
    private lateinit var phoneNumberString: String
    private lateinit var emailString: String
    private lateinit var passwordString: String
    private lateinit var confirmPasswordString: String
    private lateinit var fcmTokenString: String

    private var isTermsAndConditionsAccept = false

    private val SELECT_IMAGE_REQUEST_CODE = 1001

    private val MULTIPLE_PERMISSION_REQUEST_CODE = 1002
    private val MULTIPLE_PERMISSIONS_FROM_SETTING_REQUEST_CODE = 1003
    private val MULTIPLE_PERMISSIONS = arrayOf(
        PermissionName.CAMERA,
        PermissionName.READ_EXTERNAL_STORAGE
    )

    private lateinit var managePermission: ManagePermission
    private var imageUri: Uri? = null

    var galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK)
        {
            val data: Intent? = result.data

            if (data != null)
            {
                /* Here read store permission require */
                imageUri = data.data

                if (imageUri != null)
                {
                    val realPath: String = RealPathUtils.getRealPath(this, imageUri)

                    val oldFile = File(realPath)
                    println("=========================OLD===========================" + MemoryUnitUtils.getReadableFileSize(oldFile.length().toDouble()))

                    /* this file is store in a File externalFilesDir = context.getExternalFilesDir("Compress"); directory */
                    val newFile: File = ConfigureCompression.getInstance(this).compressToFile(oldFile)
                    println("=========================NEW===========================" + MemoryUnitUtils.getReadableFileSize(newFile.length().toDouble()))

                    pictureRealPathString = newFile.absolutePath
                    val bitmap: Bitmap = BitmapFactory.decodeFile(pictureRealPathString)
                    binding.profilePictureCircleImageView.setImageBitmap(bitmap)

                    Toast.makeText(applicationContext, "" + newFile, Toast.LENGTH_SHORT).show()
                }
            }
        }
        else if (result.resultCode == Activity.RESULT_CANCELED)
        {
            Toast.makeText(applicationContext, "User cancelled select image", Toast.LENGTH_SHORT).show()
        }
        else
        {
            Toast.makeText(applicationContext, "Sorry! Failed to select image", Toast.LENGTH_SHORT).show()
        }
    }

    var cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK)
        {
            val realPath: String = RealPathUtils.getRealPath(this, imageUri)

            val oldFile = File(realPath)
            println("=========================OLD===========================" + MemoryUnitUtils.getReadableFileSize(oldFile.length().toDouble()))

            /* this file is store in a File externalFilesDir = context.getExternalFilesDir("Compress"); directory */
            val newFile: File = ConfigureCompression.getInstance(this).compressToFile(oldFile)
            println("=========================NEW===========================" + MemoryUnitUtils.getReadableFileSize(newFile.length().toDouble()))

            pictureRealPathString = newFile.absolutePath
            val bitmap: Bitmap = BitmapFactory.decodeFile(pictureRealPathString)
            binding.profilePictureCircleImageView.setImageBitmap(bitmap)

            Toast.makeText(applicationContext, "" + oldFile, Toast.LENGTH_LONG).show()
        }
        else if (result.resultCode == Activity.RESULT_CANCELED)
        {
            if (Build.VERSION.SDK_INT >= 29)
            {
                val contentResolver: ContentResolver = contentResolver
                val updateContentValue = ContentValues()
                updateContentValue.put(MediaStore.Images.Media.IS_PENDING, 1)
                imageUri?.let {
                    contentResolver.update(it, updateContentValue, null, null)
                }
            }
            Toast.makeText(applicationContext, "User cancelled capture image", Toast.LENGTH_SHORT).show()
        }
        else
        {
            Toast.makeText(applicationContext, "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getViewBinding(): ActivitySignUpBinding {
        return ActivitySignUpBinding.inflate(layoutInflater)
    }

    override fun initializeObject() {
        managePermission = ManagePermission(this@SignUpActivity);
    }

    override fun initializeToolBar() {
    }

    override fun initializeCallbackListener() {
    }

    override fun addTextChangedListener() {
        /* Clear RadioGroup, unchecked all the RadioButton */
        binding.genderRadioGroup.clearCheck()

        /* Add the Listener to the RadioGroup */
        binding.genderRadioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            /**
             * The flow will come here when
             * any of the radio buttons in the radioGroup
             * has been clicked
             */

            //radioButton = (RadioButton)group.findViewById(checkedId);
        })

        binding.firstNameTextInputLayout.getEditText()?.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(text: CharSequence, start: Int, count: Int, after: Int)
                {
                    if (text.length < 1)
                    {
                        binding.firstNameTextInputLayout.setErrorEnabled(true)
                        binding.firstNameTextInputLayout.setError(getString(R.string.first_name_message_one))
                    }
                    else if (text.length > 0)
                    {
                        binding.firstNameTextInputLayout.setError(null)
                        binding.firstNameTextInputLayout.setErrorEnabled(false)
                    }
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                }

                override fun afterTextChanged(s: Editable) {
                }
        })

        binding.lastNameTextInputLayout.getEditText()?.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(text: CharSequence, start: Int, count: Int, after: Int) {
                    if (text.length < 1)
                    {
                        binding.lastNameTextInputLayout.setErrorEnabled(true)
                        binding.lastNameTextInputLayout.setError(getString(R.string.last_name_message_one))
                    }
                    else if (text.length > 0)
                    {
                        binding.lastNameTextInputLayout.setError(null)
                        binding.lastNameTextInputLayout.setErrorEnabled(false)
                    }
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                }

                override fun afterTextChanged(s: Editable) {
                }
        })

        binding.countryCodePicker.setOnCountryChangeListener(CountryCodePicker.OnCountryChangeListener { selectedCountry ->
            countryCodeString = selectedCountry.phoneCode
        })

        binding.phoneNumberTextInputLayout.getEditText()?.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(text: CharSequence, start: Int, count: Int, after: Int) {
                    if (text.length < 1)
                    {
                        binding.phoneNumberTextInputLayout.setErrorEnabled(true)
                        binding.phoneNumberTextInputLayout.setError(getString(R.string.phone_number_message_one))
                    }
                    else if (text.length > 0)
                    {
                        binding.phoneNumberTextInputLayout.setError(null)
                        binding.phoneNumberTextInputLayout.setErrorEnabled(false)
                    }
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                }

                override fun afterTextChanged(s: Editable) {
                    val phoneValidCode = ValidationUtils.isPhoneNumberValid(countryCodeString, Objects.requireNonNull(binding.phoneNumberTextInputEditText.getText()).toString())
                    if (phoneValidCode > 0)
                    {
                        if (phoneValidCode == 1)
                        {
                            binding.phoneNumberTextInputLayout.setError(getString(R.string.phone_number_message_one))
                        }
                        else if (phoneValidCode == 2)
                        {
                            binding.phoneNumberTextInputLayout.setError(getString(R.string.phone_number_message_two))
                        }
                        else if (phoneValidCode == 3)
                        {
                            binding.phoneNumberTextInputLayout.setError(getString(R.string.phone_number_message_three))
                        }
                    }
                }
        })

        binding.emailTextInputLayout.getEditText()?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(text: CharSequence, start: Int, count: Int, after: Int) {
                if (text.length < 1)
                {
                    binding.emailTextInputLayout.setErrorEnabled(true)
                    binding.emailTextInputLayout.setError(getString(R.string.email_message_one))
                }
                else if (text.length > 0)
                {
                    binding.emailTextInputLayout.setError(null)
                    binding.emailTextInputLayout.setErrorEnabled(false)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                val emailValidCode = ValidationUtils.isValidEmail(Objects.requireNonNull(binding.emailTextInputEditText.getText()).toString())
                if (emailValidCode > 0)
                {
                    if (emailValidCode == 1)
                    {
                        binding.emailTextInputLayout.setError(getString(R.string.email_message_one))
                    }
                    else if (emailValidCode == 2)
                    {
                        binding.emailTextInputLayout.setError(getString(R.string.email_message_two))
                    }
                }
            }
        })

        binding.passwordTextInputLayout.getEditText()?.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(text: CharSequence, start: Int, count: Int, after: Int) {
                    if (text.length < 1)
                    {
                        binding.passwordTextInputLayout.setErrorEnabled(true)
                        binding.passwordTextInputLayout.setError(getString(R.string.password_message_one))
                    }
                    else if (text.length > 0)
                    {
                        binding.passwordTextInputLayout.setError(null)
                        binding.passwordTextInputLayout.setErrorEnabled(false)
                    }
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                }

                override fun afterTextChanged(s: Editable) {
                    val passwordValidCode = ValidationUtils.isValidPassword(Objects.requireNonNull(binding.passwordTextInputEditText.getText()).toString())
                    if (passwordValidCode > 0)
                    {
                        if (passwordValidCode == 1)
                        {
                            binding.passwordTextInputLayout.setError(getString(R.string.password_message_one))
                        }
                        else if (passwordValidCode == 2)
                        {
                            binding.passwordTextInputLayout.setError(getString(R.string.password_message_two))
                        }
                        else if (passwordValidCode == 3)
                        {
                            binding.passwordTextInputLayout.setError(getString(R.string.password_message_three))
                        }
                        else if (passwordValidCode == 4)
                        {
                            binding.passwordTextInputLayout.setError(getString(R.string.password_message_four))
                        }
                        else if (passwordValidCode == 5)
                        {
                            binding.passwordTextInputLayout.setError(getString(R.string.password_message_five))
                        }
                        else if (passwordValidCode == 6)
                        {
                            binding.passwordTextInputLayout.setError(getString(R.string.password_message_six))
                        }
                        else if (passwordValidCode == 7)
                        {
                            binding.passwordTextInputLayout.setError(getString(R.string.password_message_seven))
                        }
                        else if (passwordValidCode == 8)
                        {
                            binding.passwordTextInputLayout.setError(getString(R.string.password_message_eight))
                        }
                    }
                }
        })

        binding.confirmPasswordTextInputLayout.getEditText()?.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(text: CharSequence, start: Int, count: Int, after: Int) {
                    if (text.length < 1)
                    {
                        binding.confirmPasswordTextInputLayout.setErrorEnabled(true)
                        binding.confirmPasswordTextInputLayout.setError(getString(R.string.confirm_password_message_one))
                    }
                    else if (text.length > 0)
                    {
                        binding.confirmPasswordTextInputLayout.setError(null)
                        binding.confirmPasswordTextInputLayout.setErrorEnabled(false)
                    }
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                }

                override fun afterTextChanged(s: Editable) {
                    val confirmPasswordValidCode = ValidationUtils.isValidConfirmPassword(binding.passwordTextInputEditText.getText().toString().trim(), binding.confirmPasswordTextInputEditText.getText().toString().trim())
                    if (confirmPasswordValidCode > 0)
                    {
                        if (confirmPasswordValidCode == 1)
                        {
                            binding.confirmPasswordTextInputLayout.setError(getString(R.string.password_message_one))
                        }
                        else if (confirmPasswordValidCode == 2)
                        {
                            binding.confirmPasswordTextInputLayout.setError(getString(R.string.confirm_password_message_one))
                        }
                        else if (confirmPasswordValidCode == 3)
                        {
                            binding.confirmPasswordTextInputLayout.setError(getString(R.string.confirm_password_message_two))
                        }
                        else if (confirmPasswordValidCode == 4)
                        {
                            binding.confirmPasswordTextInputLayout.setError(getString(R.string.confirm_password_message_three))
                        }
                    }
                }
        })
    }

    override fun setOnClickListener() {
        binding.selectProfilePictureFloatingActionButton.setOnClickListener {
            if (managePermission.hasPermission(MULTIPLE_PERMISSIONS))
            {
                Log.e(TAG, "permission already granted")
                showDialog(this@SignUpActivity)
            }
            else
            {
                Log.e(TAG, "permission is not granted, request for permission")
                ActivityCompat.requestPermissions(
                    this@SignUpActivity,
                    MULTIPLE_PERMISSIONS,
                    MULTIPLE_PERMISSION_REQUEST_CODE)
            }
        }

        binding.termsConditionAndPrivacyPolicyCheckBox.setOnClickListener {
            isTermsAndConditionsAccept = (it as CheckBox).isChecked()
        }

        binding.termsConditionTextView.setOnClickListener {
        }

        binding.privacyPolicyTextView.setOnClickListener {
        }

        binding.appSignUpMaterialButton.setOnClickListener {
            signUp()
        }

        binding.signInLinkTextView.setOnClickListener {
            launchSignInScreen()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode)
        {
            MULTIPLE_PERMISSION_REQUEST_CODE -> if (grantResults.size > 0)
            {
                var i = 0
                while (i < grantResults.size)
                {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                    {
                        val permission = permissions[i]
                        if (permission.equals(PermissionName.CAMERA, ignoreCase = true))
                        {
                            val showRationale = managePermission.shouldShowRequestPermissionRationale(permission)
                            if (showRationale)
                            {
                                Log.e(TAG, "camera permission denied")
                                ActivityCompat.requestPermissions(
                                    this@SignUpActivity,
                                    MULTIPLE_PERMISSIONS,
                                    MULTIPLE_PERMISSION_REQUEST_CODE)
                                return
                            }
                            else
                            {
                                Log.e(TAG, "camera permission denied and don't ask for it again")
                                PermissionDialog.permissionDeniedWithNeverAskAgain(
                                    this@SignUpActivity,
                                    R.drawable.permission_ic_camera,
                                    "Camera Permission",
                                    "Kindly allow Camera Permission from Settings, without this permission the app is unable to provide create camera feature. Please turn on permissions at [Setting] -> [Permissions]>",
                                    permission,
                                    MULTIPLE_PERMISSIONS_FROM_SETTING_REQUEST_CODE)
                                return
                            }
                        }
                        if (permission.equals(PermissionName.READ_EXTERNAL_STORAGE, ignoreCase = true)) {
                            val showRationale = managePermission.shouldShowRequestPermissionRationale(permission)
                            if (showRationale)
                            {
                                Log.e(TAG, "manage external storage permission denied")
                                ActivityCompat.requestPermissions(
                                    this@SignUpActivity,
                                    MULTIPLE_PERMISSIONS,
                                    MULTIPLE_PERMISSION_REQUEST_CODE)
                                return
                            }
                            else
                            {
                                Log.e(TAG, "manage external storage permission denied and don't ask for it again")
                                PermissionDialog.permissionDeniedWithNeverAskAgain(
                                    this@SignUpActivity,
                                    R.drawable.permission_ic_storage,
                                    "Read Storage Permission",
                                    "Kindly allow Read Storage Permission from Settings, without this permission the app is unable to provide file read feature. Please turn on permissions at [Setting] -> [Permissions]>",
                                    permission,
                                    MULTIPLE_PERMISSIONS_FROM_SETTING_REQUEST_CODE)
                                return
                            }
                        }
                    }
                    i++
                }
                Log.e(TAG, "all permission granted, do the task")
                showDialog(this@SignUpActivity)
            }
            else
            {
                Log.e(TAG, "Unknown Error")
            }
            else -> throw RuntimeException("unhandled permissions request code: $requestCode")
        }
    }

    private fun galleryIntent() {
        ImplicitIntentUtils.actionPickIntent(1, galleryLauncher)
    }

    private fun cameraIntent(customDirectoryName: String, extension: String, fileName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            val sourceUri: Uri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)

            val contentResolver = contentResolver

            val contentValues = ContentValues()
            contentValues.put(MediaStore.Images.Media.TITLE, fileName)
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName + extension)
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

            /* Add the date meta data to ensure the image is added at the front of the gallery */
            val millis = System.currentTimeMillis()

            contentValues.put(MediaStore.Images.Media.DATE_ADDED, millis / 1000L)
            contentValues.put(MediaStore.Images.Media.DATE_MODIFIED, millis / 1000L)
            contentValues.put(MediaStore.Images.Media.DATE_TAKEN, millis)
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES.toString() + "/" + customDirectoryName)
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)

            imageUri = contentResolver.insert(sourceUri, contentValues)
        }
        else
        {
            val directory: File = SharedFileUtils.createDirectory(customDirectoryName, Environment.DIRECTORY_PICTURES)
            val file: File = SharedFileUtils.createFile(directory, extension, fileName)
            imageUri = FileProviderUtils.getFileUri(applicationContext, file, BuildConfig.APPLICATION_ID)

            imageUri = FileProviderUtils.getFileUri(applicationContext, file, BuildConfig.APPLICATION_ID)
        }

        ImageAndVideoUtils.cameraIntent(1, imageUri, this@SignUpActivity, cameraLauncher)
    }

    private fun showDialog(activity: Activity) {
        val option: AlertDialog.Builder = AlertDialog.Builder(activity)
        option.setTitle("Select Action")
        val pictureDialogItems = arrayOf(
            "Capture photo from camera",
            "Select photo from gallery",
            "Cancel"
        )

        option.setItems(pictureDialogItems,
            DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    0 -> cameraIntent("AppName", ".jpg",""+System.currentTimeMillis())
                    1 -> galleryIntent()
                    2 -> dialog.dismiss()
                }
            })
        option.show()
    }

    private fun signUp() {
        val progressDialog: ProgressDialog
        progressDialog = ProgressDialog(this@SignUpActivity)
        progressDialog.setTitle("Loading")
        progressDialog.setMessage("Please wait...")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)

        firstNameString = binding.firstNameTextInputEditText.getText().toString()
        lastNameString = binding.lastNameTextInputEditText.getText().toString()

        /*
         * Get the Radio Button which is set
         * If no Radio Button is set, -1 will be returned
         */
        val selectedId: Int = binding.genderRadioGroup.getCheckedRadioButtonId()
        genderString =
            if(selectedId == -1) {
                ""
            }
            else
            {
                val radioButton: RadioButton = binding.genderRadioGroup.findViewById(selectedId) as RadioButton
                radioButton.getText().toString()
            }

        phoneNumberString = binding.phoneNumberTextInputEditText.getText().toString()
        emailString = binding.emailTextInputEditText.getText().toString()
        passwordString = binding.passwordTextInputEditText.getText().toString()
        confirmPasswordString = binding.confirmPasswordTextInputEditText.getText().toString()
        fcmTokenString = "NJjMmJkNDAxCnBhY2thZ2VOYW1lPWNvbS5jYXJ0by5hZHZhbmNlZC5rb3RsaW4Kb25saW5lT"

        if (validation(pictureRealPathString, firstNameString, lastNameString, genderString, countryCodeString, phoneNumberString, emailString, passwordString, confirmPasswordString, isTermsAndConditionsAccept, fcmTokenString) == null)
        {
            val apiService = ApiServiceGenerator.createService(this@SignUpActivity, ApiService::class.java)

            val profilePic = RequestUtils.createMultipartBody("profilePic", pictureRealPathString)
            val firstNameRequestBody = RequestUtils.createRequestBodyForString(firstNameString)
            val lastNameRequestBody = RequestUtils.createRequestBodyForString(lastNameString)
            val genderRequestBody = RequestUtils.createRequestBodyForString(genderString)
            val countryCodeRequestBody = RequestUtils.createRequestBodyForString(countryCodeString)
            val phoneNumberRequestBody = RequestUtils.createRequestBodyForString(phoneNumberString)
            val emailRequestBody = RequestUtils.createRequestBodyForString(emailString)
            val passwordRequestBody = RequestUtils.createRequestBodyForString(passwordString)
            val fcmTokenRequestBody = RequestUtils.createRequestBodyForString(fcmTokenString)

            val observable: Observable<Response<BaseResponse<Data>>> = apiService.signUp(profilePic, firstNameRequestBody, lastNameRequestBody, genderRequestBody, countryCodeRequestBody, phoneNumberRequestBody, emailRequestBody, passwordRequestBody, fcmTokenRequestBody)
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
            Toast.makeText(applicationContext, validation(pictureRealPathString, firstNameString, lastNameString, genderString, countryCodeString, phoneNumberString, emailString, passwordString, confirmPasswordString, isTermsAndConditionsAccept, fcmTokenString), Toast.LENGTH_SHORT).show()
        }
    }

    private fun validation(pictureRealPath: String, firstName: String, lastName: String, gender: String, countryCode: String, phoneNumber: String, email: String, password: String, confirmPassword: String, isTermsAndConditionsAccept: Boolean, fcmToken: String): String? {
        val phoneValidCode = ValidationUtils.isPhoneNumberValid(countryCode, phoneNumber)
        val emailValidCode = ValidationUtils.isValidEmail(email)
        val passwordValidCode = ValidationUtils.isValidPassword(password)
        val confirmPasswordValidCode = ValidationUtils.isValidConfirmPassword(password, confirmPassword)

        if (StringUtils.isBlank(pictureRealPath))
        {
            return getString(R.string.picture_message_one)
        }
        else if (StringUtils.isBlank(firstName))
        {
            return getString(R.string.first_name_message_one)
        }
        else if (StringUtils.isBlank(lastName))
        {
            return getString(R.string.last_name_message_one)
        }
        else if (StringUtils.isBlank(gender))
        {
            return getString(R.string.gender_message_one)
        }
        else if (phoneValidCode > 0)
        {
            if (phoneValidCode == 1)
            {
                return getString(R.string.phone_number_message_one)
            }
            else if (phoneValidCode == 2)
            {
                return getString(R.string.phone_number_message_two)
            }
            else if (phoneValidCode == 3)
            {
                return getString(R.string.phone_number_message_three)
            }
        }
        else if (emailValidCode > 0)
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
        else if (confirmPasswordValidCode > 0)
        {
            if (confirmPasswordValidCode == 1)
            {
                return getString(R.string.password_message_one)
            }
            else if (confirmPasswordValidCode == 2)
            {
                return getString(R.string.confirm_password_message_one)
            }
            else if (confirmPasswordValidCode == 3)
            {
                return getString(R.string.confirm_password_message_two)
            }
            else if (confirmPasswordValidCode == 4)
            {
                return getString(R.string.confirm_password_message_three)
            }
        }
        else if (!isTermsAndConditionsAccept)
        {
            return getString(R.string.accept_term_and_condition_message)
        }
        return null
    }

    private fun launchSignInScreen() {
        ActivityUtils.launchActivity(this@SignUpActivity, SignInActivity::class.java)
    }
}