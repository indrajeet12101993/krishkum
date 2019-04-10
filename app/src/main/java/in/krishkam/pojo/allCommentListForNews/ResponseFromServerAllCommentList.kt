package `in`.krishkam.pojo.allCommentListForNews

data class ResponseFromServerAllCommentList(
        val response_code: String,
        val response_message: String,
        val comment_detail: List<CommentDetail>,
        val date: String
)