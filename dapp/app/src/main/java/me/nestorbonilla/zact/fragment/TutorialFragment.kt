package me.nestorbonilla.zact.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import me.nestorbonilla.zact.R
import kotlinx.android.synthetic.main.fragment_tutorial.*

class TutorialFragment: Fragment() {

    var pageTitle : String = ""
    var pageSubtitle : String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tutorial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fragment_tutorial_title.text = pageTitle
        fragment_tutorial_subtitle.text = pageSubtitle
    }

    fun setTitle(title : String){
        pageTitle = title
    }

    fun setSubitle(subtitle : String){
        pageSubtitle = subtitle
    }

}