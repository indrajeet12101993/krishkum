package `in`.krishkam.callback

import `in`.krishkam.pojo.AllCommentList.CommentDetail

interface InterfaceCommentSelectListner {

    fun postDelete(commentDetail: CommentDetail, s: String)
}