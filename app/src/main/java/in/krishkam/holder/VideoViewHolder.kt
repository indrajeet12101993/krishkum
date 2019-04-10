package `in`.krishkam.holder

import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.youtube.player.YouTubeThumbnailView
import kotlinx.android.synthetic.main.custom_recycler_video_fragment_list.view.*


class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


    var imgView_proPic: ImageView
    var iv_like_first: ImageView
    var iv_like_second: ImageView
    var iv_saved_post: ImageView
    var youutube_play_icon: ImageView
    var iv_saved_post_done: ImageView
    var tv_time: TextView
    var textViewUsername: TextView
    var tv_headline: TextView
    var tv_content: TextView
    var tv_source: TextView
    var tv_total_like: TextView
    var tv_total_whattsapp: TextView
    var tv_total_cooment: TextView
    var tv_total_facebook: TextView
    var tv_user_save: TextView
    var videoContainer: FrameLayout
    var linear_like: LinearLayout
    var linear_whatsapp: LinearLayout
    var linear_comment: LinearLayout
    var linear_facebook: LinearLayout
    var linear_saved_post: LinearLayout
    var tv_shikayat: LinearLayout
    var videoThumbnailImageView: YouTubeThumbnailView

    init {

        imgView_proPic = itemView.imgView_proPic
        iv_like_first = itemView.iv_like_first
        iv_like_second = itemView.iv_like_second
        iv_saved_post = itemView.iv_saved_post
        youutube_play_icon = itemView.youutube_play_icon
        iv_saved_post_done = itemView.iv_saved_post_done
        tv_time = itemView.tv_time
        textViewUsername = itemView.textViewUsername
        tv_headline = itemView.tv_headline
        tv_content = itemView.tv_content
        tv_source = itemView.tv_source
        tv_total_like = itemView.tv_total_like
        tv_source = itemView.tv_source
        tv_total_whattsapp = itemView.tv_total_whattsapp
        tv_total_cooment = itemView.tv_total_cooment
        tv_total_facebook = itemView.tv_total_facebook
        tv_user_save = itemView.tv_user_save
        videoContainer = itemView.video_container
        linear_like = itemView.linear_like
        linear_whatsapp = itemView.linear_whatsapp
        linear_facebook = itemView.linear_facebook
        linear_comment = itemView.linear_comment
        linear_saved_post = itemView.linear_saved_post
        tv_shikayat = itemView.tv_shikayat
        videoThumbnailImageView = itemView.video_thumbnail_image_view
    }


}