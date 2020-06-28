package me.nestorbonilla.zact.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_tutorial.*
import me.nestorbonilla.zact.R
import me.nestorbonilla.zact.adapter.TutorialAdapter
import me.nestorbonilla.zact.fragment.TutorialFragment

class TutorialActivity: AppCompatActivity() {

    val fragment1 = TutorialFragment()
    val fragment2 = TutorialFragment()
    val fragment3 = TutorialFragment()
    lateinit var adapter : TutorialAdapter
    lateinit var activity : Activity

    lateinit var preference : SharedPreferences
    val pref_show_intro = "INTRO"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)
        activity = this
        preference = getSharedPreferences("INTRO_SLIDER" , Context.MODE_PRIVATE)

        fragment1.setTitle("Welcome")
        fragment2.setTitle("To CodeAndroid")
        fragment3.setTitle("YouTube Channel")

        adapter = TutorialAdapter(supportFragmentManager)
        adapter.list.add(fragment1)
        adapter.list.add(fragment2)
        adapter.list.add(fragment3)

        view_pager.adapter = adapter
        btn_next.setOnClickListener {
            view_pager.currentItem++
        }

        btn_skip.setOnClickListener { goToDashboard() }

        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if(position == adapter.list.size-1){
                    //lastPage
                    btn_next.text = "DONE"
                    btn_next.setOnClickListener {
                        goToDashboard()
                    }
                }else{
                    //has next
                    btn_next.text = "NEXT"
                    btn_next.setOnClickListener {
                        view_pager.currentItem++
                    }
                }

                when(view_pager.currentItem){
                    0->{
                        indicator1.setTextColor(Color.BLACK)
                        indicator2.setTextColor(Color.GRAY)
                        indicator3.setTextColor(Color.GRAY)
                    }
                    1->{
                        indicator1.setTextColor(Color.GRAY)
                        indicator2.setTextColor(Color.BLACK)
                        indicator3.setTextColor(Color.GRAY)
                    }
                    2->{
                        indicator1.setTextColor(Color.GRAY)
                        indicator2.setTextColor(Color.GRAY)
                        indicator3.setTextColor(Color.BLACK)
                    }
                }

            }

        })

    }

    fun goToDashboard(){
        val editor = preference.edit()
        editor.putBoolean(pref_show_intro,false)
        editor.apply()
        finish()
    }
}