package `in`.krishkam.callback

import`in`.krishkam.pojo.AllPostFeed.Post

interface InterFaceAllfeedVideoSelectListner {
    fun userPostSelectReadMore(post: Post)
    fun userPostSelectVideoId(post: Post)
    fun userPostSelectPostLike(post: Post, like: String)
    fun userPostSelectWhatsApp(post: Post, position:Int)
    fun userPostSelectComment(post: Post, position:Int)
    fun userPostSelectSikayat(post: Post)
    fun userPostSelectfacebook(post: Post, position: Int)
    fun userPostSelectSavedPost(post: Post, position: Int, type:String)
}