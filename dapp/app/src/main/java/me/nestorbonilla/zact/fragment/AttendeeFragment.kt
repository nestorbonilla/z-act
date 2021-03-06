package me.nestorbonilla.zact.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import me.nestorbonilla.zact.R
import me.nestorbonilla.zact.adapter.AttendeeAdapter
import me.nestorbonilla.zact.model.ActModel
import me.nestorbonilla.zact.room.ZactDao
import me.nestorbonilla.zact.room.ZactDatabase
import me.nestorbonilla.zact.service.ServiceBuilder
import me.nestorbonilla.zact.service.ZactService
import me.nestorbonilla.zact.viewmodel.ActViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AttendeeFragment : Fragment() {

    private var db: ZactDatabase? = null
    private var zactDao: ZactDao? = null

    private lateinit var actViewModel: ActViewModel
    private lateinit var adapter: AttendeeAdapter
    private lateinit var attendee_recyclerview: RecyclerView
    private lateinit var attendee_swipe: SwipeRefreshLayout
    private lateinit var attendee_empty: LinearLayoutCompat

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_attendee, container, false)

        db = ZactDatabase.getDatabase(requireContext())
        zactDao = db?.zactDao()

        attendee_recyclerview = root.findViewById(R.id.attendee_recyclerview)
        attendee_swipe = root.findViewById(R.id.attendee_swipe);
        attendee_empty = root.findViewById(R.id.attendee_empty)
        attendee_recyclerview.layoutManager = LinearLayoutManager(inflater.context, LinearLayoutManager.VERTICAL, false)
        adapter = AttendeeAdapter(inflater.context)
        actViewModel = ViewModelProviders.of(this).get(ActViewModel::class.java)
        actViewModel.getAllActs().observe(viewLifecycleOwner,
            Observer<List<ActModel>> {
                    t -> adapter.setActs(t!!)
                if (t.size == 0) {
                    attendee_recyclerview.isVisible = false
                    attendee_empty.isVisible = true
                    loadValuesFromApi()
                } else {
                    attendee_recyclerview.isVisible = true
                    attendee_empty.isVisible = false
                }
            }
        )
        attendee_recyclerview.adapter = adapter
        attendee_swipe.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        attendee_swipe.setOnRefreshListener( SwipeRefreshLayout.OnRefreshListener {
            loadValuesFromApi()
        })

        return root
    }

    private fun loadValuesFromApi() {
        val zactService = ServiceBuilder.buildService(ZactService::class.java)
        val requestCall = zactService.getActList()

        requestCall.enqueue(object: Callback<List<ActModel>> {
            override fun onResponse(call: Call<List<ActModel>>, response: Response<List<ActModel>>) {
                attendee_swipe.isRefreshing = false
                if (response.isSuccessful) {
                    var acts = response.body()
                    with(zactDao) {
                        if (acts != null) {
                            for (act in acts) {
                                var actDB = this?.getActByApiId(act._id)
                                if (actDB == null) {
                                    this?.insertAct(act)
                                } else {
                                    actDB.title = act.title
                                    actDB.publicInformation = act.publicInformation
                                    actDB.meetingPoint = act.meetingPoint
                                    actDB.meetingPointRadius = act.meetingPointRadius
                                    this?.updateAct(act)
                                }
                            }
                        }
                    }
                }
            }
            override fun onFailure(call: Call<List<ActModel>>, t: Throwable) {
            }
        })
    }

}