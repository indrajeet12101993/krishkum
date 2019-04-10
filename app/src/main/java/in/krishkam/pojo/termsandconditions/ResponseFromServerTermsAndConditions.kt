package `in`.krishkam.pojo.termsandconditions

data class ResponseFromServerTermsAndConditions(
        val response_code: String,
        val response_message: String,
        val result: List<Result>
)