package me.nestorbonilla.zact.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import me.nestorbonilla.zact.R
import me.nestorbonilla.zact.adapter.AttendeeAdapter
import me.nestorbonilla.zact.model.ActModel
import me.nestorbonilla.zact.viewmodel.ActViewModel

class AttendeeFragment : Fragment() {

    //val items:ArrayList<String> = ArrayList()
    private lateinit var actViewModel: ActViewModel
    private lateinit var adapter: AttendeeAdapter
    private lateinit var attendee_recyclerview: RecyclerView
    private lateinit var attendee_empty: SimpleDraweeView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_attendee, container, false)
        //addItems()
        //ZactDatabase.get(activity!!.application).getZactDao().getActions()
        attendee_recyclerview = root.findViewById(R.id.attendee_recyclerview)
        attendee_empty = root.findViewById(R.id.attendee_empty)
        attendee_recyclerview.layoutManager = LinearLayoutManager(inflater.context, LinearLayoutManager.VERTICAL, false)
        adapter = AttendeeAdapter(inflater.context)
        actViewModel = ViewModelProviders.of(this).get(ActViewModel::class.java)
        actViewModel.getAllActs().observe(this,
            Observer<List<ActModel>> {
                t -> adapter.setActs(t!!)
                if (t.size == 0) {
                    attendee_recyclerview.isVisible = false
                    attendee_empty.isVisible = true
                } else {
                    attendee_recyclerview.isVisible = true
                    attendee_empty.isVisible = false
                }
            }
        )
        attendee_recyclerview.adapter = adapter
        return root
    }

}