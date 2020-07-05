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
import me.nestorbonilla.zact.activity.CreatorDetailActivity
import me.nestorbonilla.zact.adapter.CreatorAdapter
import me.nestorbonilla.zact.model.ActModel
import me.nestorbonilla.zact.viewmodel.ActViewModel

class CreatorFragment : Fragment() {

    private lateinit var actViewModel: ActViewModel
    private lateinit var adapter: CreatorAdapter
    private lateinit var creator_recyclerview: RecyclerView
    private lateinit var creator_empty: SimpleDraweeView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_creator, container, false)
        creator_recyclerview = root.findViewById(R.id.creator_recyclerview)
        creator_empty = root.findViewById(R.id.creator_empty)
        creator_recyclerview.layoutManager = LinearLayoutManager(inflater.context, LinearLayoutManager.VERTICAL, false)
        adapter = CreatorAdapter(inflater.context)
        actViewModel = ViewModelProviders.of(this).get(ActViewModel::class.java)
        actViewModel.getAllActs().observe(viewLifecycleOwner,
            Observer<List<ActModel>> {
                    t -> adapter.setActs(t!!)
                if (t.size == 0) {
                    creator_recyclerview.isVisible = false
                    creator_empty.isVisible = true
                } else {
                    creator_recyclerview.isVisible = true
                    creator_empty.isVisible = false
                }
            }
        )
        creator_recyclerview.adapter = adapter

        val dashboard_fab: FloatingActionButton = root.findViewById(R.id.creator_fab)
        dashboard_fab.setOnClickListener{
            val intent = Intent(inflater.context, CreatorDetailActivity::class.java)
            startActivity(intent)
        }
        return root
    }

}