package `in`.krishkam.pojo

data class ResponseFromServerWhatsApp(
        val response_code: String,
        val response_message: String,
        val result: String,
        val post_id: String
)