package `in`.krishkam.adapter


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.customadapterforsearchsuggestions.view.*
import `in`.krishkam.R
import `in`.krishkam.callback.InterfaceSearchSuggestions
import `in`.krishkam.pojo.searchsuggestion.Result

class CustomSuggestionSearchAdapter(val userSuggestions: MutableList<Result>, private val listner: InterfaceSearchSuggestions) : RecyclerView.Adapter<CustomSuggestionSearchAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomSuggestionSearchAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.customadapterforsearchsuggestions, parent, false)

        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: CustomSuggestionSearchAdapter.ViewHolder, position: Int) {

        holder.bindItems(userSuggestions[position],listner)



    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return userSuggestions.size

    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        fun bindItems(result: Result, listner: InterfaceSearchSuggestions) {

            itemView.  tv_name.text = result.name
            itemView.setOnClickListener {

               listner.searchname(result.name)
            }


        }
    }
}