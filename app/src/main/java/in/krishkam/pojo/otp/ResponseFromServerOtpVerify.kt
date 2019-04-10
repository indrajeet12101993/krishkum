package`in`.krishkam.pojo.otp

data class ResponseFromServerOtpVerify(
        val response_code: String,
        val response_message: String,
        val result: MutableList<Result>
)