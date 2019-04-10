package `in`.krishkam.Fragment


import android.os.Bundle
import android.support.v4.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import `in`.krishkam.R



class UserMyPostFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view: View=inflater.inflate(R.layout.fragment_user_my_post, container, false)
       // view. rv_user_feed_list.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)


        //crating an arraylist to store users using the data class user

//        val users = ArrayList<User>()
//
//        //adding some dummy data to the list
//        users.add(User("अभिषेक मिश्रा ", "गुरुग्राम हरियाणा.2 घण्टे पहले "))
//        users.add(User("अभिषेक मिश्रा ", "गुरुग्राम हरियाणा.2 घण्टे पहले "))
//        users.add(User("अभिषेक मिश्रा ", "गुरुग्राम हरियाणा.2 घण्टे पहले "))
//        users.add(User("अभिषेक मिश्रा ", "गुरुग्राम हरियाणा.2 घण्टे पहले "))
//        users.add(User("अभिषेक मिश्रा ", "गुरुग्राम हरियाणा.2 घण्टे पहले "))
//        users.add(User("अभिषेक मिश्रा ", "गुरुग्राम हरियाणा.2 घण्टे पहले "))
//        users.add(User("अभिषेक मिश्रा ", "गुरुग्राम हरियाणा.2 घण्टे पहले "))
//        users.add(User("अभिषेक मिश्रा ", "गुरुग्राम हरियाणा.2 घण्टे पहले "))
//        users.add(User("अभिषेक मिश्रा ", "गुरुग्राम हरियाणा.2 घण्टे पहले "))
//        users.add(User("अभिषेक मिश्रा ", "गुरुग्राम हरियाणा.2 घण्टे पहले "))
//        users.add(User("अभिषेक मिश्रा ", "गुरुग्राम हरियाणा.2 घण्टे पहले "))
//
//
//        //creating our adapter
//        val adapter = CustomVideoFragmentAdapter(users,context!!)
//
//        //now adding the adapter to recyclerview
//        view. rv_user_feed_list.adapter = adapter
        return view
    }


}
