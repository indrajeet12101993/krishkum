package `in`.krishkam.callback


import `in`.krishkam.pojo.listBlog.Result

interface InterfaceAllfeedPostSelectlistner {



    fun userPostSelectReadMore(post: Result)
    fun userPostSelectPostLike(post: Result, like: String)
    fun userPostSelectWhatsApp(post:Result,position:Int)
    fun userPostSelectComment(post:Result,position:Int)
    fun userPostSelectSikayat(post:Result)
    fun userPostSelectfacebook(post: Result,position: Int)
    fun userPostSelectSavedPost(post: Result,position: Int,type:String)
}