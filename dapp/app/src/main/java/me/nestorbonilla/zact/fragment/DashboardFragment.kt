package me.nestorbonilla.zact.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_dashboard.*
import me.nestorbonilla.zact.R
import me.nestorbonilla.zact.activity.DashboardDetailActivity
import me.nestorbonilla.zact.adapter.DashboardAdapter
import me.nestorbonilla.zact.viewmodel.DashboardViewModel

class DashboardFragment : Fragment() {

    val items:ArrayList<String> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        addItems()
        val dashboard_recyclerview: RecyclerView = root.findViewById(R.id.dashboard_recyclerview)
        dashboard_recyclerview.layoutManager = LinearLayoutManager(inflater.context, LinearLayoutManager.VERTICAL, false)
        dashboard_recyclerview.adapter = DashboardAdapter(items, inflater.context)

        val dashboard_fab: FloatingActionButton = root.findViewById(R.id.dashboard_fab)
        dashboard_fab.setOnClickListener{
            val intent = Intent(inflater.context, DashboardDetailActivity::class.java)
            startActivity(intent)
        }
        return root
    }

    private fun addItems() {
        items.add("Element A")
        items.add("Element B")
        items.add("Element C")
        items.add("Element D")
        items.add("Element E")
    }
}