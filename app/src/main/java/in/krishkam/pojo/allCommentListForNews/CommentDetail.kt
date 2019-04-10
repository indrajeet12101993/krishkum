package `in`.krishkam.pojo.allCommentListForNews

data class CommentDetail(
        val id: String,
        val user_id: String,
        val news_id: String,
        val comment: String,
        val created: String,
        val is_reported: String,
        val blog_id: String,
        val date: String,
        val name: String,
        val state: String,
        val city: String,
        val mobile: String,
        val village: String,
        val otp: String,
        val image: String,
        val is_blocked: String,
        val commentid: String
)