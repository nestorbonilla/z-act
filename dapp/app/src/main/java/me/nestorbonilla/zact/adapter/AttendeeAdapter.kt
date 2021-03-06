package me.nestorbonilla.zact.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import kotlinx.android.synthetic.main.creator_item.view.*
import me.nestorbonilla.zact.R
import me.nestorbonilla.zact.activity.AttendeeDetailActivity
import me.nestorbonilla.zact.model.ActModel

class AttendeeAdapter(val context: Context):RecyclerView.Adapter<AttendeeAdapter.DashboardHolder>() {

    private var acts: List<ActModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardHolder {
        return DashboardHolder(LayoutInflater.from(context).inflate(R.layout.creator_item, parent, false))
    }

    override fun getItemCount(): Int {
        return acts.size
    }

    fun setActs(acts: List<ActModel>) {
        this.acts = acts
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: DashboardHolder, position: Int) {
        holder.title?.text = acts.get(position).title
        holder.subtitle?.text = acts.get(position).publicInformation
        val key: String = context.getString(R.string.google_maps_key)
        var imageURI = "https://maps.googleapis.com/maps/api/staticmap?center=" + acts.get(position).meetingPoint + "&zoom=13&size=600x300&maptype=roadmap&key=" + key
        holder.image.setImageURI(imageURI)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, AttendeeDetailActivity::class.java)
            intent.putExtra("act_id", acts.get(position).id)
            context.startActivity(intent)
        }
    }


    class DashboardHolder(view: View):RecyclerView.ViewHolder(view) {
        val title = view.creator_title
        val subtitle = view.creator_subtitle
        val image:SimpleDraweeView = view.creator_image
    }
}