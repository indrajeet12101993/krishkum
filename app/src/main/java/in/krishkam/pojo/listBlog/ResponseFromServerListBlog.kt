package `in`.krishkam.pojo.listBlog

data class ResponseFromServerListBlog(
        val response_code: String,
        val response_message: String,
        val result: List<Result>
)