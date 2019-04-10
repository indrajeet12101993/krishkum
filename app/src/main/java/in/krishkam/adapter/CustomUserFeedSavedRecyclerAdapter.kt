package `in`.krishkam.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubeThumbnailLoader
import com.google.android.youtube.player.YouTubeThumbnailView
import kotlinx.android.synthetic.main.custom_recycler_video_fragment_list.view.*
import `in`.krishkam.R
import `in`.krishkam.callback.InterFaceAllfeedVideoSelectListner
import `in`.krishkam.callback.InterfaceAllfeedPostSelectlistner
import `in`.krishkam.pojo.AllPostFeed.Post
import `in`.krishkam.utils.TimeUtils
import android.app.Activity



class CustomUserFeedSavedRecyclerAdapter(val userFeedList: MutableList<Post>, private val listner: InterFaceAllfeedVideoSelectListner) : RecyclerView.Adapter<CustomUserFeedSavedRecyclerAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomUserFeedSavedRecyclerAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.custom_recycler_video_fragment_list, parent, false)



        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: CustomUserFeedSavedRecyclerAdapter.ViewHolder, position: Int) {
        holder.bindItems(userFeedList[position], listner, holder.adapterPosition)

    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return userFeedList.size

    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var updateLike: Int? = null
        private var save_flag: Boolean = false
        fun bindItems(post: Post, listner: InterFaceAllfeedVideoSelectListner, adapterPosition: Int) {
            Glide.with(itemView.context).load(post.uimage).into(itemView.imgView_proPic)

            val server_time_timestamp =TimeUtils.getServerTimeStamp(post.created)

            val relativeTime = TimeUtils.getTimeAgo(server_time_timestamp)
            itemView. tv_time.text = relativeTime
            itemView.textViewUsername.text = post.username
            itemView.tv_headline.text = post.headline
            itemView.tv_content.text = post.content
            itemView.tv_source.text = "Youtube.com"

            // click on linar like
            itemView.tv_total_like.text = post.no_like
            itemView.iv_like_second.visibility = View.VISIBLE
            if (post.Isliked.equals("0")) {
                itemView.iv_like_first.visibility = View.VISIBLE
                itemView.iv_like_second.visibility = View.INVISIBLE
            } else {
                itemView.iv_like_second.visibility = View.VISIBLE
                itemView.iv_like_first.visibility = View.INVISIBLE
            }
            updateLike = post.no_like.toInt()

            itemView.linear_like.setOnClickListener {

                if (itemView.iv_like_first.visibility == View.VISIBLE) {
                    itemView.iv_like_second.visibility = View.VISIBLE
                    itemView.iv_like_first.visibility = View.INVISIBLE
                    val like: Int = updateLike!! + 1
                    val Totallike: String = like.toString()
                    itemView.tv_total_like.text = Totallike
                    listner.userPostSelectPostLike(post, "1")
                } else {
                    itemView.iv_like_second.visibility = View.INVISIBLE
                    itemView.iv_like_first.visibility = View.VISIBLE
                    //val updateLike  =post.no_like.toInt()-1
                    //  val like: Int = updateLike
                    //  val Totallike: String = like.toString()
                    itemView.tv_total_like.text = updateLike.toString()
                    listner.userPostSelectPostLike(post, "0")
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
                listner.userPostSelectfacebook(post,adapterPosition)
            }

            // save post
            if(post.IsSavedpost.equals("0")){
                // when post is not save
                itemView.iv_saved_post.visibility= View.VISIBLE
                itemView.iv_saved_post_done.visibility= View.INVISIBLE
                itemView.tv_user_save.text = "सेव पोस्ट"
                Glide.with(itemView.context).load(R.drawable.bookmark_icon).into(itemView.iv_saved_post)


            }
            else{
                // when post is saved
                itemView.iv_saved_post.visibility= View.INVISIBLE
                itemView.iv_saved_post_done.visibility= View.VISIBLE
                itemView.tv_user_save.text = "सेव्ड पोस्ट "
                Glide.with(itemView.context).load(R.drawable.saved_icon).into(itemView.iv_saved_post_done)
            }
            itemView.linear_saved_post.setOnClickListener{
                // when post is saved for if and else user don?t save the post
                if(itemView.iv_saved_post.visibility== View.VISIBLE){
                    itemView.iv_saved_post.visibility= View.INVISIBLE
                    itemView.iv_saved_post_done.visibility= View.VISIBLE
                    itemView.tv_user_save.text = "सेव्ड पोस्ट "
                    Glide.with(itemView.context).load(R.drawable.saved_icon).into(itemView.iv_saved_post_done)
                    listner.userPostSelectSavedPost(post,adapterPosition,"save")

                }else{
                    itemView.iv_saved_post.visibility= View.VISIBLE
                    itemView.tv_user_save.text = "सेव पोस्ट"
                    itemView.iv_saved_post_done.visibility= View.INVISIBLE
                    Glide.with(itemView.context).load(R.drawable.bookmark_icon).into(itemView.iv_saved_post)
                    listner.userPostSelectSavedPost(post,adapterPosition,"remove")
                }

            }


//            // when user clicks on video icon
//            itemView.iv_video_play.setOnClickListener {
//                listner.userPostSelectVideoId(post)
//
//            }








//            itemView.iv_postimage.initialize("AIzaSyAeygVuWu26XAOibEKmW-GTiSlRLT3vDTA", object : YouTubeThumbnailView.OnInitializedListener {
//                override fun onInitializationSuccess(youTubeThumbnailView: YouTubeThumbnailView, youTubeThumbnailLoader: YouTubeThumbnailLoader) {
//                    //when initialization is sucess, set the video id to thumbnail to load
//                    youTubeThumbnailLoader.setVideo(post.link)
//
//                    youTubeThumbnailLoader.setOnThumbnailLoadedListener(object : YouTubeThumbnailLoader.OnThumbnailLoadedListener {
//                        override fun onThumbnailLoaded(youTubeThumbnailView: YouTubeThumbnailView, s: String) {
//                            //when thumbnail loaded successfully release the thumbnail loader as we are showing thumbnail in adapter
//                            //  youTubeThumbnailLoader.release()
//                            youTubeThumbnailLoader.setVideo(post.link)
//
//                        }
//
//                        override fun onThumbnailError(youTubeThumbnailView: YouTubeThumbnailView, errorReason: YouTubeThumbnailLoader.ErrorReason) {
//                            //print or show error when thumbnail load failed
//                            //  Log.e(TAG, "Youtube Thumbnail Error")
//                        }
//                    })
//                }
//
//                override fun onInitializationFailure(youTubeThumbnailView: YouTubeThumbnailView, youTubeInitializationResult: YouTubeInitializationResult) {
//                    //print or show error when initialization failed
//                    //  Log.e(TAG, "Youtube Initialization Failure")
//
//                }
//            })
        }
        // api call for user registration


    }

}