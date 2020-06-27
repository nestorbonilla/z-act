package me.nestorbonilla.zact.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        //addItems()
        //ZactDatabase.get(activity!!.application).getZactDao().getActions()
        val dashboard_recyclerview: RecyclerView = root.findViewById(R.id.dashboard_recyclerview)
        dashboard_recyclerview.layoutManager = LinearLayoutManager(inflater.context, LinearLayoutManager.VERTICAL, false)
        adapter = DashboardAdapter(inflater.context)
        actViewModel = ViewModelProviders.of(this).get(ActViewModel::class.java)
        actViewModel.getAllActs().observe(this,
            Observer<List<ActModel>> { t -> adapter.setActs(t!!) })
        dashboard_recyclerview.adapter = adapter

        val dashboard_fab: FloatingActionButton = root.findViewById(R.id.dashboard_fab)
        dashboard_fab.setOnClickListener{
            val intent = Intent(inflater.context, DashboardCreateActivity::class.java)
            startActivity(intent)
        }
        return root
    }

    
}