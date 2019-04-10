package `in`.krishkam.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import `in`.krishkam.R
import `in`.krishkam.callback.InterfaceStateSelectListner
import `in`.krishkam.pojo.state.Result

class CustomAdapterForState( private val stateList: MutableList<Result>,  private val listner: InterfaceStateSelectListner) : RecyclerView.Adapter<CustomAdapterForState.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAdapterForState.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.state_list, parent, false)
        return ViewHolder(v)
    }


    //this method is binding the data on the list
    override fun onBindViewHolder(holder: CustomAdapterForState.ViewHolder, position: Int) {
        holder.bindItems(stateList[position],listner)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return stateList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(result: Result, listner: InterfaceStateSelectListner) {
            val textViewName = itemView.findViewById(R.id.tv_name) as TextView
            textViewName.text = result.name

            itemView.setOnClickListener {
                listner.onItemClickState(result)
            }

        }
    }
}