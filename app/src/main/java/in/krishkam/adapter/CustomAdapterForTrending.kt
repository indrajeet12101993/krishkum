package `in`.krishkam.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.customadapterfortrending.view.*


import `in`.krishkam.R
import `in`.krishkam.callback.InterfaceSearchSuggestions
import `in`.krishkam.pojo.hashTagTrending.Post

class CustomAdapterForTrending(val userSuggestions: MutableList<Post>, private val listner: InterfaceSearchSuggestions) : RecyclerView.Adapter<CustomAdapterForTrending.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAdapterForTrending.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.customadapterfortrending, parent, false)

        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: CustomAdapterForTrending.ViewHolder, position: Int) {

        holder.bindItems(userSuggestions[position],listner)



    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return userSuggestions.size

    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        fun bindItems(result: Post, listner: InterfaceSearchSuggestions) {


            Glide.with(itemView.context).load(result.image).into( itemView.iv_suggestion)
            itemView.tv_name.text = result.name

            itemView.setOnClickListener {

                listner.searchname(result.name)
            }


        }
    }
}