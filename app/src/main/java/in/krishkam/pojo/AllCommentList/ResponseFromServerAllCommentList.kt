package `in`.krishkam.pojo.AllCommentList

data class ResponseFromServerAllCommentList(
        val response_code: String,
        val response_message: String,
        val comment_detail: List<CommentDetail>,
        val date: String
)