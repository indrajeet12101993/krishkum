package `in`.krishkam.pojo.city

data class ResponseFromServerCity(
        val response_code: String,
        val response_message: String,
        val result: MutableList<Result>
)