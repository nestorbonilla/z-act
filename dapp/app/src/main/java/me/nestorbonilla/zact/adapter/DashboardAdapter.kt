package me.nestorbonilla.zact.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import kotlinx.android.synthetic.main.dashboard_item.view.*
import me.nestorbonilla.zact.R
import me.nestorbonilla.zact.fragment.DashboardFragment

class DashboardAdapter(val items:ArrayList<String>, val context: Context):RecyclerView.Adapter<DashboardAdapter.DashboardHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardHolder {
        return DashboardHolder(LayoutInflater.from(context).inflate(R.layout.dashboard_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: DashboardHolder, position: Int) {
        holder?.item?.text = items.get(position)
        holder.image.setImageURI("https://maps.googleapis.com/maps/api/staticmap?center=8.994772,-79.522794&zoom=13&size=600x300&maptype=roadmap&key=AIzaSyAdet-b7ik-7xdThfIS_GhPShjavJSUz18");
    }

    class DashboardHolder(view: View):RecyclerView.ViewHolder(view) {
        val item = view.dashboard_title
        val image:SimpleDraweeView = view.dashboard_image
    }
}