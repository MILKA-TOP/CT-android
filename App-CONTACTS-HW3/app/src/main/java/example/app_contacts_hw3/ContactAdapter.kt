package example.app_contacts_hw3

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import example.app_contacts_hw3.databinding.ContactViewBinding

data class Contact(val name: String, val phoneNumber: String)

class ContactViewHolder(private val contactViewBinding: ContactViewBinding) :
    RecyclerView.ViewHolder(contactViewBinding.root) {


    fun bind(contact: Contact) {
        contactViewBinding.name.text = contact.name
        contactViewBinding.phone.text = contact.phoneNumber
    }
}

class ContactAdapter(
    private var contacts: List<Contact>,
    private val onClick: (Contact, ClickType) -> Unit
) : RecyclerView.Adapter<ContactViewHolder>() {

    private lateinit var itemPersonBinding: ContactViewBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {


        val layoutInflater = LayoutInflater.from(parent.context)
        itemPersonBinding = ContactViewBinding.inflate(layoutInflater, parent, false)
        val holder = ContactViewHolder(itemPersonBinding)
        itemPersonBinding.message.setOnClickListener {
            onClick(contacts[holder.adapterPosition], ClickType.MESSAGE_CLICK)
        }
        itemPersonBinding.name.setOnClickListener {
            onClick(contacts[holder.adapterPosition], ClickType.PHONE_CLICK)
        }
        itemPersonBinding.share.setOnClickListener {
            onClick(contacts[holder.adapterPosition], ClickType.SHARE_CLICK)
        }

        return holder

    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) =
        holder.bind(contacts[position])

    override fun getItemCount() = contacts.size

    fun setList(list: List<Contact>) {
        val productDiffUtilCallback =
            ContactDiffUtilCallback(contacts, list)
        val productDiffResult = DiffUtil.calculateDiff(productDiffUtilCallback)
        this.contacts = list
        productDiffResult.dispatchUpdatesTo(this)
    }

    fun getList() = contacts
}


class ContactDiffUtilCallback(
    private val oldList: List<Contact>,
    private val newList: List<Contact>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]

}

enum class ClickType {
    PHONE_CLICK, SHARE_CLICK, MESSAGE_CLICK
}