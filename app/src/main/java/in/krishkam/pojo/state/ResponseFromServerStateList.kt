package `in`.krishkam.pojo.state

data class ResponseFromServerStateList(
        val response_code: String,
        val response_message: String,
        val result: MutableList<Result>
)