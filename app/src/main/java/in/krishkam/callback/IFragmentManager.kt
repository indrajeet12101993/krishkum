package `in`.krishkam.callback

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

interface IFragmentManager {
     fun getSupportFragmentManager(): FragmentManager
     fun getSupportFragment(): Fragment
}