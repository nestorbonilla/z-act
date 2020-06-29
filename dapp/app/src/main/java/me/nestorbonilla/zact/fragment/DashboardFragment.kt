package me.nestorbonilla.zact.fragment

import android.content.Intent
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import me.nestorbonilla.zact.R
import me.nestorbonilla.zact.activity.DashboardCreateActivity
import me.nestorbonilla.zact.adapter.DashboardAdapter
import me.nestorbonilla.zact.model.ActModel
import me.nestorbonilla.zact.room.ZactDatabase
import me.nestorbonilla.zact.viewmodel.ActViewModel

class DashboardFragment : Fragment() {

    //val items:ArrayList<String> = ArrayList()
    private lateinit var actViewModel: ActViewModel
    private lateinit var adapter: DashboardAdapter
    private lateinit var dashboard_recyclerview: RecyclerView
    private lateinit var dashboard_empty: SimpleDraweeView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        //addItems()
        //ZactDatabase.get(activity!!.application).getZactDao().getActions()
        dashboard_recyclerview = root.findViewById(R.id.dashboard_recyclerview)
        dashboard_empty = root.findViewById(R.id.dashboard_empty)
        dashboard_recyclerview.layoutManager = LinearLayoutManager(inflater.context, LinearLayoutManager.VERTICAL, false)
        adapter = DashboardAdapter(inflater.context)
        actViewModel = ViewModelProviders.of(this).get(ActViewModel::class.java)
        actViewModel.getAllActs().observe(this,
            Observer<List<ActModel>> {
                t -> adapter.setActs(t!!)
                if (t.size == 0) {
                    dashboard_recyclerview.isVisible = false
                    dashboard_empty.isVisible = true
                }
            }
        )
        dashboard_recyclerview.adapter = adapter

        val dashboard_fab: FloatingActionButton = root.findViewById(R.id.dashboard_fab)
        dashboard_fab.setOnClickListener{
            val intent = Intent(inflater.context, DashboardCreateActivity::class.java)
            startActivity(intent)
        }
        return root
    }

    override fun onResume() {
        super.onResume()
        actViewModel.getAllActs().observe(this,
            Observer<List<ActModel>> {
                    t -> adapter.setActs(t!!)
                if (t.size == 0) {
                    dashboard_recyclerview.isVisible = false
                    dashboard_empty.isVisible = true
                }
            }
        )
    }

    
}