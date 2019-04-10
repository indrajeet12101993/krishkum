package `in`.krishkam.pojo.hashTagTrending

data class ResponseFromServerHashTag(
        val response_code: String,
        val response_message: String,
        val post: MutableList<Post>
)