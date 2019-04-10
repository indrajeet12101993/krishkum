package `in`.krishkam.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.customadapterforsearchsuggestions.view.*
import `in`.krishkam.R
import `in`.krishkam.callback.InterfaceSearchSuggestions
import `in`.krishkam.pojo.hashTagTrending.Post

class CustomAdapterForHashtagList(val userSuggestions: MutableList<Post>, private val listner: InterfaceSearchSuggestions) : RecyclerView.Adapter<CustomAdapterForHashtagList.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAdapterForHashtagList.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.customadapterforhashtaglayout, parent, false)

        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: CustomAdapterForHashtagList.ViewHolder, position: Int) {

        holder.bindItems(userSuggestions[position],listner)



    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {


        if(userSuggestions.size>6){
            return 6
        }
        else{
            return userSuggestions.size
        }


    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        fun bindItems(result: Post, listner: InterfaceSearchSuggestions) {

            itemView.  tv_name.text = result.name
            if(result.is_trending.equals("1")){
                itemView. tv_suugestions.visibility= View.VISIBLE
            }
            else{
                itemView. tv_suugestions.visibility= View.INVISIBLE
            }
            itemView.setOnClickListener {

                listner.searchname(result.name)
            }


        }
    }
}