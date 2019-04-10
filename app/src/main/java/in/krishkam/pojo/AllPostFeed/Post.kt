package `in`.krishkam.pojo.AllPostFeed

import `in`.krishkam.holder.VideoBinder
import java.io.Serializable

 class Post : Serializable{

        lateinit var post_id: String
        lateinit var  user_id: String
         lateinit var headline: String
         lateinit var content: String
         lateinit var post_image: String
         lateinit var hashtag_id: String
         lateinit var no_like: String
         lateinit var no_comment: String
         lateinit var created: String
         lateinit var no_fb_share: String
         lateinit var link: String
         lateinit var post_type: String
         lateinit var username: String
         lateinit var uimage: String
         lateinit var Isliked: String
         lateinit var IsSavedpost: String
         lateinit var no_share: String
         lateinit var source: String
         lateinit var is_reported: String
         lateinit var news_id: String
         lateinit var title: String
         lateinit var news_content: String
         lateinit var news_image: String
         lateinit var url: String
         lateinit var blog_id: String
         lateinit var image: String
         lateinit var image1: String
         lateinit var image2: String
         lateinit var image3: String
         @Transient
         var binder:VideoBinder=VideoBinder(this@Post)


}








