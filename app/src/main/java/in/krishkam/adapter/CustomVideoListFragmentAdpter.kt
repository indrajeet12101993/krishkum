package `in`.krishkam.adapter


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.custom_recycler_video_fragment_list.view.*
import `in`.krishkam.R
import `in`.krishkam.callback.IFragmentManager
import `in`.krishkam.pojo.AllPostFeed.Post
import`in`.krishkam.callback.InterFaceAllfeedVideoSelectListner
import `in`.krishkam.holder.VideoViewHolder
import `in`.krishkam.utils.TimeUtils
import android.text.Html



class CustomVideoListFragmentAdpter(val userFeedList: MutableList<Post>,  var listner: InterFaceAllfeedVideoSelectListner,
                                    var fragmentManager: IFragmentManager) : RecyclerView.Adapter<VideoViewHolder>() {


    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.custom_recycler_video_fragment_list, parent, false)

        return VideoViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
     //   holder.bindItems(userFeedList[position], listner, holder.adapterPosition)

    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return userFeedList.size

    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var updateLike: Int? = null

        var like: Int? = null



        fun bindItems(post: Post, listner: InterFaceAllfeedVideoSelectListner, adapterPosition: Int) {
            Glide.with(itemView.context).load(post.uimage).into(itemView.imgView_proPic)

            val server_time_timestamp = TimeUtils.getServerTimeStamp(post.created)

            val relativeTime = TimeUtils.getTimeAgo(server_time_timestamp)
            itemView.tv_time.text = relativeTime
            itemView.textViewUsername.text = post.username
            itemView.tv_headline.text = post.headline
            itemView.tv_content.text = Html.fromHtml(post.content)
         //   itemView.tv_content.setText(post.content)
          //  itemView.tv_source.text = "Youtube.com"

            // click on linar like
            itemView.tv_total_like.text = post.no_like
            if (post.Isliked.equals("0")) {
                itemView.iv_like_first.visibility = View.VISIBLE
                itemView.iv_like_second.visibility = View.INVISIBLE
            } else {
                itemView.iv_like_second.visibility = View.VISIBLE
                itemView.iv_like_first.visibility = View.INVISIBLE
            }


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
                listner.userPostSelectWhatsApp(post, adapterPosition)

            }
            //comment
            itemView.tv_total_cooment.text = post.no_comment
            itemView.linear_comment.setOnClickListener {

                listner.userPostSelectComment(post, adapterPosition)

            }
            // shikayat
            itemView.tv_shikayat.setOnClickListener {

                listner.userPostSelectSikayat(post)
            }
            //facebook
            itemView.tv_total_facebook.text = post.no_fb_share
            itemView.linear_facebook.setOnClickListener {
                listner.userPostSelectfacebook(post, adapterPosition)
            }

            // save post
            if (post.IsSavedpost.equals("0")) {
                // when post is not save
                itemView.iv_saved_post.visibility = View.VISIBLE
                itemView.iv_saved_post_done.visibility = View.INVISIBLE
                itemView.tv_user_save.text = "सेव पोस्ट"
                Glide.with(itemView.context).load(R.drawable.bookmark_icon).into(itemView.iv_saved_post)


            } else {
                // when post is saved
                itemView.iv_saved_post.visibility = View.INVISIBLE
                itemView.iv_saved_post_done.visibility = View.VISIBLE
                itemView.tv_user_save.text = "सेव्ड पोस्ट "
                Glide.with(itemView.context).load(R.drawable.saved_icon).into(itemView.iv_saved_post_done)
            }
            itemView.linear_saved_post.setOnClickListener {
                // when post is saved for if and else user don?t save the post
                if (itemView.iv_saved_post.visibility == View.VISIBLE) {
                    itemView.iv_saved_post.visibility = View.INVISIBLE
                    itemView.iv_saved_post_done.visibility = View.VISIBLE
                    itemView.tv_user_save.text = "सेव्ड पोस्ट "
                    Glide.with(itemView.context).load(R.drawable.saved_icon).into(itemView.iv_saved_post_done)
                    listner.userPostSelectSavedPost(post, adapterPosition, "save")

                } else {
                    itemView.iv_saved_post.visibility = View.VISIBLE
                    itemView.tv_user_save.text = "सेव पोस्ट"
                    itemView.iv_saved_post_done.visibility = View.INVISIBLE
                    Glide.with(itemView.context).load(R.drawable.bookmark_icon).into(itemView.iv_saved_post)
                    listner.userPostSelectSavedPost(post, adapterPosition, "remove")
                }

            }

            itemView. tv_content.visibility=View.VISIBLE
            itemView.tv_content.text = Html.fromHtml(post.content)
            itemView.tv_content.setOnClickListener {
//
//                if(  itemView. tv_content.visibility==View.VISIBLE){
//                    itemView. tv_content_long.visibility=View.VISIBLE
//                    itemView.tv_content_long.text = Html.fromHtml(post.content)
//                    itemView. tv_content.visibility=View.INVISIBLE
//                }
//                else{
//                    itemView. tv_content_long.visibility=View.INVISIBLE
//                    itemView.tv_content.text = Html.fromHtml(post.content)
//                    itemView. tv_content.visibility=View.VISIBLE
//                }

            }


//            // when user clicks on video icon
//            itemView.iv_video_play.setOnClickListener {
//                listner.userPostSelectVideoId(post)
//
//            }


//            val display = (itemView.context as AppCompatActivity).windowManager.defaultDisplay;
//            val outMetrics = DisplayMetrics();
//            display.getMetrics(outMetrics);
//
//            val density = (itemView.context as AppCompatActivity).resources.displayMetrics.density;
//            val dpHeight = outMetrics.heightPixels / density
//            val dpWidth = outMetrics.widthPixels / density
//            val actualwidh = dpWidth - 10F

//            itemView.mWebView.setWebViewClient(object : WebViewClient() {
//                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
//                    return false
//                }
//            })
//
//            var frameVideo: String = "<html><body><br><iframe width=\"$actualwidh\" height=\"200\" src=\"https://www.youtube.com/embed/${post.link}\" frameborder=\"0\" allowfullscreen></iframe></body></html>"
//            val webSettings = itemView.mWebView.getSettings()
//            webSettings.setJavaScriptEnabled(true)
//            itemView.mWebView.settings.setLoadWithOverviewMode(true)
//            itemView.mWebView.loadData(frameVideo, "text/html", "utf-8")

        }



    }

    override fun onViewAttachedToWindow(holder: VideoViewHolder) {
        super.onViewAttachedToWindow(holder)
        val position = holder.position
        val video:Post = userFeedList.get(position)
        video.binder.bind(holder, fragmentManager,listner)
    }

    override fun onViewDetachedFromWindow(holder: VideoViewHolder) {
        super.onViewDetachedFromWindow(holder)
        val position = holder.position
        if(position>0){
            val video = userFeedList.get(position)
            video.binder.unBind(holder, fragmentManager,listner)
        }

    }

}