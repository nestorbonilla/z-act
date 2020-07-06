package me.nestorbonilla.zact.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import kotlinx.android.synthetic.main.activity_creator_detail.*
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
    private lateinit var attendee_empty: SimpleDraweeView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_attendee, container, false)

        db = ZactDatabase.getDatabase(requireContext())
        zactDao = db?.zactDao()

        attendee_recyclerview = root.findViewById(R.id.attendee_recyclerview)
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
        return root
    }

    private fun loadValuesFromApi() {
        val zactService = ServiceBuilder.buildService(ZactService::class.java)
        val requestCall = zactService.getActList()

        requestCall.enqueue(object: Callback<List<ActModel>> {
            override fun onResponse(call: Call<List<ActModel>>, response: Response<List<ActModel>>) {
                if (response.isSuccessful) {
                    var acts = response.body()
                    with(zactDao) {
                        if (acts != null) {
                            Log.d("ZACT_DAPP", this?.getActList().toString())
                            //this?.insertActs(acts)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<List<ActModel>>, t: Throwable) {

            }
        })
    }

}