package `in`.krishkam.pojo.listBlog

import java.io.Serializable

data class Result(
        val blog_id: String,
        val hashtag_id: String,
        val title: String,
        val content: String,
        val image: String,
        val image1: String,
        val image2: String,
        val image3: String,
        val created: String,
        val no_like: String,
        val no_comment: String,
        val fb_share: String,
        val wp_share: String,
        val Isliked: String
):Serializable