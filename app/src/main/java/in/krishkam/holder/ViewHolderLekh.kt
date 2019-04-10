package `in`.krishkam.holder

import `in`.krishkam.callback.InterFaceAllfeedVideoSelectListner
import `in`.krishkam.callback.InterfaceAllfeedPostSelectlistner
import `in`.krishkam.pojo.AllPostFeed.Post
import `in`.krishkam.pojo.listBlog.Result
import `in`.krishkam.utils.TimeUtils
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.View
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.custom_lekh_fragment_recycler.view.*

class ViewHolderLekh(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var updateLike: Int? = null
    var like: Int? = null
    fun bindItems(post: Post, listner: InterFaceAllfeedVideoSelectListner, adapterPosition: Int) {

        Glide.with(itemView.context).load(post.image).into(itemView.iv_lekh_view)

        itemView.tv_headline.text = post.title
        itemView.tv_content.text = Html.fromHtml(post.content)
        //  itemView.tv_content.setText(post.content)
        itemView.tv_hashtag.text = post.hashtag_id
        val server_time_timestamp = TimeUtils.getServerTimeStamp(post.created)

        val relativeTime = TimeUtils.getTimeAgo(server_time_timestamp)
        itemView. tv_time.text = relativeTime
        // click on linar like
        itemView.tv_total_like.text = post.no_like
        if (post.Isliked.equals("0")) {
            itemView.iv_like_first.visibility = View.VISIBLE
            itemView.iv_like_second.visibility = View.INVISIBLE
        } else {
            itemView.iv_like_second.visibility = View.VISIBLE
            itemView.iv_like_first.visibility = View.INVISIBLE
        }
        updateLike = post.no_like.toInt()

        itemView.linear_like.setOnClickListener {

            if (post.Isliked.equals("1")) {
                updateLike = post.no_like.toInt()
                if (itemView.iv_like_second.visibility == View.VISIBLE) {
                    itemView.iv_like_second.visibility = View.INVISIBLE
                    itemView.iv_like_first.visibility = View.VISIBLE
                    like = updateLike!! - 1
                    val Totallike: String = like.toString()
                    itemView.tv_total_like.text = Totallike
                    listner.userPostSelectPostLike(post, "0")
                    return@setOnClickListener
                }
                if (itemView.iv_like_first.visibility == View.VISIBLE) {
                    itemView.iv_like_first.visibility = View.INVISIBLE
                    itemView.iv_like_second.visibility = View.VISIBLE
                    val like1: Int = like!! + 1
                    val Totallike: String = like1.toString()
                    itemView.tv_total_like.text = Totallike
                    listner.userPostSelectPostLike(post, "1")
                    return@setOnClickListener

                }
            }
            if (post.Isliked.equals("0")) {
                updateLike = post.no_like.toInt()
                if (itemView.iv_like_first.visibility == View.VISIBLE) {
                    itemView.iv_like_second.visibility = View.VISIBLE
                    itemView.iv_like_first.visibility = View.INVISIBLE
                    like = updateLike!! + 1
                    val Totallike: String = like.toString()
                    itemView.tv_total_like.text = Totallike
                    listner.userPostSelectPostLike(post, "1")
                    return@setOnClickListener
                } else {
                    itemView.iv_like_second.visibility = View.INVISIBLE
                    itemView.iv_like_first.visibility = View.VISIBLE
                    //val updateLike  =post.no_like.toInt()-1
                    //  val like: Int = updateLike
                    //  val Totallike: String = like.toString()
                    val like1: Int = like!! - 1
                    val Totallike: String = like1.toString()
                    itemView.tv_total_like.text = updateLike.toString()
                    listner.userPostSelectPostLike(post, "0")
                    return@setOnClickListener
                }
            }


        }
        //whatsapp share
        itemView.tv_total_whattsapp.text = post.no_share
        itemView.linear_whatsapp.setOnClickListener {
            itemView.tv_total_whattsapp.text = (post.no_share.toInt()+1).toString()

            listner.userPostSelectWhatsApp(post, adapterPosition)

        }
        //comment
        itemView.tv_total_cooment.text = post.no_comment
        itemView.linear_comment.setOnClickListener {

            listner.userPostSelectComment(post, adapterPosition)

        }

        //facebook
        itemView.tv_total_facebook.text = post.no_fb_share
        itemView.linear_facebook.setOnClickListener {
            itemView.tv_total_facebook.text = (post.no_fb_share.toInt()+1).toString()
            listner.userPostSelectfacebook(post, adapterPosition)
        }

        itemView.readmore.setOnClickListener {
            listner.userPostSelectReadMore(post)
        }


    }
}