package `in`.krishkam.adapter

import `in`.krishkam.R
import `in`.krishkam.callback.InterfaceCommentForNewsSelectListner
import `in`.krishkam.pojo.allCommentListForNews.CommentDetail
import `in`.krishkam.utils.TimeUtils
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.custom_layout_comment.view.*
import kotlinx.android.synthetic.main.custom_layout_comment_for_another_user.view.*

class CustomAdapterForCommentSamachar(private val coomentList: MutableList<CommentDetail>, private val listner: InterfaceCommentForNewsSelectListner, var userId: String?) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ViewHolderRight) {

            holder.bindItemsRight(coomentList[position], listner)
        }
        if (holder is ViewHolderLeft) {
            holder.bindItemsleft(coomentList[position], listner)
        }

    }

    //Tag for tracking self message
    private val SELF: Int = 123

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // val v = LayoutInflater.from(parent.context).inflate(R.layout.custom_layout_comment, parent, false)

        var itemView: View
        if (viewType == SELF) {

            itemView = LayoutInflater.from(parent.context).inflate(R.layout.custom_layout_comment, parent, false)

            return ViewHolderLeft(itemView)
        } else {
            itemView = LayoutInflater.from(parent.context).inflate(R.layout.custom_layout_comment_for_another_user, parent, false)
            return ViewHolderRight(itemView)
        }

    }


    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return coomentList.size
    }

    override fun getItemViewType(position: Int): Int {
        //getting message object of current position
        val message = coomentList.get(position)

        //If its owner  id is  equals to the logged in user id
        return if (message.user_id.equals(userId)) {
            //Returning self
            SELF
        } else position
        //else returning position
    }

    //the class is hodling the list view
    class ViewHolderRight(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItemsRight(commentDetail: CommentDetail, listner: InterfaceCommentForNewsSelectListner) {
            if(!commentDetail.image.isNullOrEmpty()){
                Glide.with(itemView.context).load(commentDetail.image).into(itemView.imgView_proPic1)
            }

            val server_time_timestamp = TimeUtils.getServerTimeStampForComment(commentDetail.date)

            val relativeTime = TimeUtils.getTimeAgo(server_time_timestamp)
            itemView.user_flag.text = relativeTime
            itemView.user_name1.text = commentDetail.name
            itemView.user_comment1.text = commentDetail.comment

            itemView.user_flag.setOnClickListener {

                itemView.user_flag.visibility = View.INVISIBLE
            }
        }
    }

    //the class is hodling the list view
    class ViewHolderLeft(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItemsleft(commentDetail: CommentDetail, listner: InterfaceCommentForNewsSelectListner) {
            if(!commentDetail.image.isNullOrEmpty()){
                Glide.with(itemView.context).load(commentDetail.image).into(itemView.imgView_proPic)
            }

            val server_time_timestamp = TimeUtils.getServerTimeStampForComment(commentDetail.date)

            val relativeTime = TimeUtils.getTimeAgo(server_time_timestamp)
            itemView.user_trash.text = relativeTime
            itemView.user_name.text = commentDetail.name
            itemView.user_comment.text = commentDetail.comment

            itemView.user_trash.setOnClickListener {
                listner.postDelete(commentDetail, "delete")
            }
        }
    }
}