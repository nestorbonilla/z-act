package me.nestorbonilla.zact.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import kotlinx.android.synthetic.main.contact_item.view.*
import kotlinx.android.synthetic.main.dashboard_item.view.*
import me.nestorbonilla.zact.R
import me.nestorbonilla.zact.activity.DashboardDetailActivity
import me.nestorbonilla.zact.model.ActModel
import me.nestorbonilla.zact.model.ContactModel

class ContactAdapter(val context: Context): RecyclerView.Adapter<ContactAdapter.ContactHolder>() {

    private var contacts: List<ContactModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        return ContactHolder(LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false))
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    fun setActs(contacts: List<ContactModel>) {
        this.contacts = contacts
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        holder?.title?.text = contacts.get(position).name
        holder?.subtitle?.text = contacts.get(position).address
        holder.image.setImageURI("https://maps.googleapis.com/maps/api/staticmap?center=8.994772,-79.522794&zoom=13&size=600x300&maptype=roadmap&key=AIzaSyAdet-b7ik-7xdThfIS_GhPShjavJSUz18");
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DashboardDetailActivity::class.java)
            context.startActivity(intent)
        }
    }


    class ContactHolder(view: View): RecyclerView.ViewHolder(view) {
        val title = view.contact_title
        val subtitle = view.contact_subtitle
        val image: SimpleDraweeView = view.dashboard_image
    }
}