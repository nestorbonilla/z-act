package me.nestorbonilla.zact.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import me.nestorbonilla.zact.R
import me.nestorbonilla.zact.model.ContactModel

class ContactFragment : Fragment() {

    private lateinit var contact: ContactModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //contact = ViewModelProviders.of(this).get(Contact::class.java)
        val root = inflater.inflate(R.layout.fragment_contact, container, false)
        //val textView: TextView = root.findViewById(R.id.text_contact)
        //contact.text.observe(viewLifecycleOwner, Observer {
        //    textView.text = it
        //})
        return root
    }
}