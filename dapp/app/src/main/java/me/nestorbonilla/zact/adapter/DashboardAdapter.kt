package me.nestorbonilla.zact.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import kotlinx.android.synthetic.main.dashboard_item.view.*
import me.nestorbonilla.zact.R
import me.nestorbonilla.zact.activity.DashboardCreateActivity
import me.nestorbonilla.zact.activity.DashboardDetailActivity
import me.nestorbonilla.zact.fragment.DashboardFragment
import me.nestorbonilla.zact.model.ActModel
import me.nestorbonilla.zact.room.ZactDao
import me.nestorbonilla.zact.room.ZactDatabase

class DashboardAdapter(val context: Context):RecyclerView.Adapter<DashboardAdapter.DashboardHolder>() {

    private var acts: List<ActModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardHolder {
        return DashboardHolder(LayoutInflater.from(context).inflate(R.layout.dashboard_item, parent, false))
    }

    override fun getItemCount(): Int {
        return acts.size
    }

    fun setActs(acts: List<ActModel>) {
        this.acts = acts
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: DashboardHolder, position: Int) {
        holder?.title?.text = acts.get(position).title
        holder?.subtitle?.text = acts.get(position).description
        holder.image.setImageURI("https://maps.googleapis.com/maps/api/staticmap?center=8.994772,-79.522794&zoom=13&size=600x300&maptype=roadmap&key=AIzaSyAdet-b7ik-7xdThfIS_GhPShjavJSUz18");
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DashboardDetailActivity::class.java)
            context.startActivity(intent)
        }
    }


    class DashboardHolder(view: View):RecyclerView.ViewHolder(view) {
        val title = view.dashboard_title
        val subtitle = view.dashboard_subtitle
        val image:SimpleDraweeView = view.dashboard_image
    }
}