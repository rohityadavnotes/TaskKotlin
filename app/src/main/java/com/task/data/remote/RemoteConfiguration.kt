package com.task.data.remote

object RemoteConfiguration {

    /*
     * Note :  We can set timeouts settings on the underlying HTTP client.
     * If we don’t specify a client, Retrofit will create one with default connect and read timeouts.
     * By default, Retrofit uses the following timeouts :
     *                                                      Connection timeout: 10 seconds
     *                                                      Read timeout: 10 seconds
     *                                                      Write timeout: 10 seconds
     */
    const val HTTP_CONNECT_TIMEOUT                  = 1
    const val HTTP_READ_TIMEOUT                     = 30
    const val HTTP_WRITE_TIMEOUT                    = 15


    /* Network Base Config */
    const val HOST_URL                              = "https://backend24.000webhostapp.com"

    /* Note : If you use Retrofit 2 then add / at the end of base url. if use Retrofit 1 so remove it */
    const val BASE_URL                              = "https://backend24.000webhostapp.com/"

    const val API_KEY                               = "AIzaSyOzB818x55FASHvX4JuGQciR9lv7q"
    const val BEARER_AUTHENTICATION_TOKEN           = "AIzaSyOzB818x55FASHvX4JuGQciR9lv7q"

    /*
     * End points
     * Note : If you use Retrofit 1 then add / at START.
     */
    const val EMPLOYEE                              = "Json/employee.json"
    const val EMPLOYEE_LIST                         = "Json/employeeList.json"

    const val UPLOAD                                = "UploadFile/upload_multipart_file.php"

    const val SIGN_UP                               = "RestApi/middleware/AppSignUp.php"
    const val SIGN_IN                               = "RestApi/middleware/SignIn.php"
    const val SOCIAL_SIGN_IN                        = "RestApi/middleware/SocialSignIn.php"
    const val GET_USERS                             = "RestApi/middleware/GetUsers.php"
}