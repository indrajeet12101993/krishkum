package `in`.krishkam.pojo.AllPostFeed

data class ResponseFromServerAllfeed(
        val response_code: String,
        val response_message: String,
        val post: MutableList<Post>
)