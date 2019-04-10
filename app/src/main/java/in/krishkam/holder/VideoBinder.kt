package `in`.krishkam.holder

import `in`.krishkam.R
import `in`.krishkam.callback.IFragmentManager
import `in`.krishkam.callback.InterFaceAllfeedVideoSelectListner
import `in`.krishkam.constants.AppConstants
import `in`.krishkam.pojo.AllPostFeed.Post
import `in`.krishkam.utils.TimeUtils
import android.content.Intent
import android.net.Uri
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.google.android.youtube.player.*


class VideoBinder( var post: Post) {

    private val TAG = VideoBinder::class.java.simpleName
    private val HACK_ID_PREFIX = 12331293 //some random number
    private val YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v="
    private var youTubePlayerFragment: YouTubePlayerSupportFragment? = null
    var youTubePlayer1: YouTubePlayer?=null
    private var isFullScreen = false
    var readyForLoadingYoutubeThumbnail: Boolean = true





    fun bind(videoViewHolder: VideoViewHolder, fragmentManager: IFragmentManager, listner: InterFaceAllfeedVideoSelectListner) {

        /*  initialize the thumbnail image view , we need to pass Developer Key */

        if(readyForLoadingYoutubeThumbnail) {
            readyForLoadingYoutubeThumbnail= false
            videoViewHolder.videoThumbnailImageView.initialize(AppConstants.KEY, object : YouTubeThumbnailView.OnInitializedListener {
                override fun onInitializationSuccess(youTubeThumbnailView: YouTubeThumbnailView, youTubeThumbnailLoader: YouTubeThumbnailLoader) {
                    //when initialization is sucess, set the video id to thumbnail to load
                    youTubeThumbnailLoader.setVideo(post.link)

                    youTubeThumbnailLoader.setOnThumbnailLoadedListener(object : YouTubeThumbnailLoader.OnThumbnailLoadedListener {
                        override fun onThumbnailLoaded(youTubeThumbnailView: YouTubeThumbnailView, s: String) {
                            //when thumbnail loaded successfully release the thumbnail loader as we are showing thumbnail in adapter
                            youTubeThumbnailLoader.release()
                        }

                        override fun onThumbnailError(youTubeThumbnailView: YouTubeThumbnailView, errorReason: YouTubeThumbnailLoader.ErrorReason) {
                            //print or show error when thumbnail load failed
                            Log.e(TAG, "Youtube Thumbnail Error")
                        }
                    })
                    readyForLoadingYoutubeThumbnail = true;
                }

                override fun onInitializationFailure(youTubeThumbnailView: YouTubeThumbnailView, youTubeInitializationResult: YouTubeInitializationResult) {
                    //print or show error when initialization failed
                    Log.e(TAG, "Youtube Initialization Failure")
                    readyForLoadingYoutubeThumbnail = true;
                }
            })
        }


        bindVideo(videoViewHolder, fragmentManager)
        bindTitle(videoViewHolder,listner)
        bindDescription(videoViewHolder)
    }

    private fun bindVideo(viewHolder: VideoViewHolder, fragmentManager: IFragmentManager) {
        Glide.with(viewHolder.itemView.context).load(post.uimage).into(viewHolder.imgView_proPic)

        val view:View = viewHolder.itemView.findViewWithTag(viewHolder.itemView.context.getString(R.string.video_component_tag)) as View
        if (view != null) {

            view.id = HACK_ID_PREFIX + viewHolder.adapterPosition
        }
        handleClick(viewHolder, fragmentManager)
    }

    private fun bindTitle(videoViewHolder: VideoViewHolder, listner: InterFaceAllfeedVideoSelectListner) {
         var updateLike: Int? = null
        val server_time_timestamp = TimeUtils.getServerTimeStamp(post.created)
        val relativeTime = TimeUtils.getTimeAgo(server_time_timestamp)
        videoViewHolder.tv_time.text = relativeTime
        videoViewHolder.textViewUsername.text = post.username
        videoViewHolder.tv_headline.text = post.headline
        videoViewHolder.tv_content.text = Html.fromHtml(post.content)
        videoViewHolder.tv_source.text = "Youtube.com"
        var like: Int? = null

        // click on linar like
        videoViewHolder.tv_total_like.text = post.no_like
        if (post.Isliked.equals("0")) {
            videoViewHolder.iv_like_first.visibility = View.VISIBLE
            videoViewHolder.iv_like_second.visibility = View.INVISIBLE
        } else {
            videoViewHolder.iv_like_second.visibility = View.VISIBLE
            videoViewHolder.iv_like_first.visibility = View.INVISIBLE
        }
        updateLike = post.no_like.toInt()

        videoViewHolder.linear_like.setOnClickListener {







            if (post.Isliked.equals("1")) {
                updateLike = post.no_like.toInt()
                if (videoViewHolder.iv_like_second.visibility == View.VISIBLE) {
                    videoViewHolder.iv_like_second.visibility = View.INVISIBLE
                    videoViewHolder.iv_like_first.visibility = View.VISIBLE
                    like = updateLike!! - 1
                    val Totallike: String = like.toString()
                    videoViewHolder.tv_total_like.text = Totallike
                    listner.userPostSelectPostLike(post, "0")
                    return@setOnClickListener
                }
                if (videoViewHolder.iv_like_first.visibility == View.VISIBLE) {
                    videoViewHolder.iv_like_first.visibility = View.INVISIBLE
                    videoViewHolder.iv_like_second.visibility = View.VISIBLE
                    val like1: Int = like!! + 1
                    val Totallike: String = like1.toString()
                    videoViewHolder.tv_total_like.text = Totallike
                    listner.userPostSelectPostLike(post, "1")
                    return@setOnClickListener

                }
            }
            if (post.Isliked.equals("0")) {
                updateLike = post.no_like.toInt()
                if (videoViewHolder.iv_like_first.visibility == View.VISIBLE) {
                    videoViewHolder.iv_like_second.visibility = View.VISIBLE
                    videoViewHolder.iv_like_first.visibility = View.INVISIBLE
                    like = updateLike!! + 1
                    val Totallike: String = like.toString()
                    videoViewHolder.tv_total_like.text = Totallike
                    listner.userPostSelectPostLike(post, "1")
                    return@setOnClickListener
                } else {
                    videoViewHolder.iv_like_second.visibility = View.INVISIBLE
                    videoViewHolder.iv_like_first.visibility = View.VISIBLE
                    //val updateLike  =post.no_like.toInt()-1
                    //  val like: Int = updateLike
                    //  val Totallike: String = like.toString()
                    val like1: Int = like!! - 1
                    val Totallike: String = like1.toString()
                    videoViewHolder.tv_total_like.text = updateLike.toString()
                    listner.userPostSelectPostLike(post, "0")
                    return@setOnClickListener
                }
            }




        }
        //whatsapp share
        videoViewHolder.tv_total_whattsapp.text = post.no_share
        videoViewHolder.linear_whatsapp.setOnClickListener {


            videoViewHolder.tv_total_whattsapp.text = (post.no_share.toInt()+1).toString()
            listner.userPostSelectWhatsApp(post, videoViewHolder.adapterPosition)

        }
        //comment
        videoViewHolder.tv_total_cooment.text = post.no_comment
        videoViewHolder.linear_comment.setOnClickListener {

            listner.userPostSelectComment(post, videoViewHolder.adapterPosition)

        }
        // shikayat
        videoViewHolder.tv_shikayat.setOnClickListener {

            listner.userPostSelectSikayat(post)
        }
        //facebook
        videoViewHolder.tv_total_facebook.text = post.no_fb_share
        videoViewHolder.linear_facebook.setOnClickListener {
            videoViewHolder.tv_total_facebook.text = (post.no_fb_share.toInt()+1).toString()
            listner.userPostSelectfacebook(post, videoViewHolder.adapterPosition)
        }

        // save post
        if (post.IsSavedpost.equals("0")) {
            // when post is not save
            videoViewHolder.iv_saved_post.visibility = View.VISIBLE
            videoViewHolder.iv_saved_post_done.visibility = View.INVISIBLE
            videoViewHolder.tv_user_save.text = "सेव पोस्ट"
            Glide.with(videoViewHolder.itemView.context).load(R.drawable.bookmark_icon).into(videoViewHolder.iv_saved_post)


        } else {
            // when post is saved
            videoViewHolder.iv_saved_post.visibility = View.INVISIBLE
            videoViewHolder.iv_saved_post_done.visibility = View.VISIBLE
            videoViewHolder.tv_user_save.text = "सेव्ड पोस्ट "
            Glide.with(videoViewHolder.itemView.context).load(R.drawable.saved_icon).into(videoViewHolder.iv_saved_post_done)
        }
        videoViewHolder.linear_saved_post.setOnClickListener {
            // when post is saved for if and else user don?t save the post
            if (videoViewHolder.iv_saved_post.visibility == View.VISIBLE) {
                videoViewHolder.iv_saved_post.visibility = View.INVISIBLE
                videoViewHolder.iv_saved_post_done.visibility = View.VISIBLE
                videoViewHolder.tv_user_save.text = "सेव्ड पोस्ट "
                Glide.with(videoViewHolder.itemView.context).load(R.drawable.saved_icon).into(videoViewHolder.iv_saved_post_done)
                listner.userPostSelectSavedPost(post,videoViewHolder.adapterPosition, "save")

            } else {
                videoViewHolder.iv_saved_post.visibility = View.VISIBLE
                videoViewHolder.tv_user_save.text = "सेव पोस्ट"
                videoViewHolder.iv_saved_post_done.visibility = View.INVISIBLE
                Glide.with(videoViewHolder.itemView.context).load(R.drawable.bookmark_icon).into(videoViewHolder.iv_saved_post)
                listner.userPostSelectSavedPost(post, videoViewHolder.adapterPosition, "remove")
            }

        }

    }

    private fun bindDescription(videoViewHolder: VideoViewHolder) {

    }

    private fun handleClick(viewHolder: VideoViewHolder, fragmentManager: IFragmentManager) {
        viewHolder.youutube_play_icon.setOnClickListener(View.OnClickListener { view ->
//            if (TextUtils.isEmpty(post.link)) {
//                return@OnClickListener
//            }
            if (!YouTubeIntents.isYouTubeInstalled(view.context) || YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(view.context) != YouTubeInitializationResult.SUCCESS) {
                if (YouTubeIntents.canResolvePlayVideoIntent(view.context)) {
                    fragmentManager.getSupportFragment().startActivity(YouTubeIntents.createPlayVideoIntent(view.context, post.link))
                    return@OnClickListener
                }
                val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_BASE_URL + post.link))
                fragmentManager.getSupportFragment().startActivity(viewIntent)
                return@OnClickListener
            }
            if (viewHolder.videoContainer.childCount == 0) {
                if (youTubePlayerFragment == null) {
                    youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance()
                }
                if (youTubePlayerFragment!!.isAdded) {
                    if (youTubePlayer1 != null) {
                        try {
                            youTubePlayer1!!.pause()
                            youTubePlayer1!!.release()
                        } catch (e: Exception) {
                            if (youTubePlayer1 != null) {
                                try {
                                    youTubePlayer1!!.release()
                                } catch (ignore: Exception) {
                                }

                            }
                        }

                       youTubePlayer1 = null
                    }

                    fragmentManager.getSupportFragmentManager()
                            .beginTransaction()
                            .remove(youTubePlayerFragment!!)
                            .commit()
                    fragmentManager.getSupportFragmentManager()
                            .executePendingTransactions()
                    youTubePlayerFragment = null
                }
                if (youTubePlayerFragment == null) {
                    youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance()
                }
                fragmentManager.getSupportFragmentManager()
                        .beginTransaction()
                        .add(HACK_ID_PREFIX + viewHolder.adapterPosition, youTubePlayerFragment!!)
                       .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .commit()
                youTubePlayerFragment!!.initialize(AppConstants.KEY,
                        object : YouTubePlayer.OnInitializedListener {
                            override fun onInitializationSuccess(provider: YouTubePlayer.Provider,
                                                                 youTubePlayer: YouTubePlayer, b: Boolean) {
                                youTubePlayer1 = youTubePlayer
                               // youTubePlayer1!!.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
                                youTubePlayer1!!.loadVideo(post.link)
                                youTubePlayer1!!.fullscreenControlFlags = 0
                                youTubePlayer1!!.setOnFullscreenListener(YouTubePlayer.OnFullscreenListener { b -> isFullScreen = b })
                            }

                            override fun onInitializationFailure(provider: YouTubePlayer.Provider,
                                                                 youTubeInitializationResult: YouTubeInitializationResult) {
                                Log.e(VideoBinder::class.java.simpleName, youTubeInitializationResult.name)
                                if (YouTubeIntents.canResolvePlayVideoIntent(
                                                fragmentManager.getSupportFragment().context)) {
                                    fragmentManager.getSupportFragment()
                                            .startActivity(YouTubeIntents.createPlayVideoIntent(
                                                    fragmentManager.getSupportFragment().context,
                                                    post.link))
                                    return
                                }
                                val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_BASE_URL + post.link))
                                fragmentManager.getSupportFragment().startActivity(viewIntent)
                            }
                        })
            }
        })
    }

    fun unBind(videoViewHolder: VideoViewHolder, fragmentManager: IFragmentManager, listner: InterFaceAllfeedVideoSelectListner) {
        if (videoViewHolder.videoContainer.childCount > 0) {
            if (youTubePlayerFragment != null && youTubePlayerFragment!!.isAdded) {
                if (youTubePlayer1 != null) {
                    try {
                        youTubePlayer1!!.pause()
                        youTubePlayer1!!.release()
                    } catch (e: Exception) {
                        if (youTubePlayer1 != null) {
                            try {
                                youTubePlayer1!!.release()
                            } catch (ignore: Exception) {
                            }

                        }
                    }

                    youTubePlayer1 = null
                }

                fragmentManager.getSupportFragmentManager()
                        .beginTransaction()
                        .remove(youTubePlayerFragment!!)
                        .commit()
                fragmentManager.getSupportFragmentManager()
                        .executePendingTransactions()
                youTubePlayerFragment = null
            }
        }
    }
}