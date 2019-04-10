package `in`.krishkam.callback

import `in`.krishkam.pojo.allCommentListForNews.CommentDetail

interface InterfaceCommentForNewsSelectListner {
    fun postDelete(commentDetail: CommentDetail, s: String)
}