package `in`.krishkam.pojo.listnews

import java.io.Serializable

data class Result(
        val news_id: String,
        val hashtag_id: String,
        val title: String,
        val news_content: String,
        val news_image: String,
        val url: String,
        val created: String,
        val no_like: String,
        val no_comment: String,
        val fb_share: String,
        val wp_share: String,
        val Isliked: String,
        val source: String
):Serializable