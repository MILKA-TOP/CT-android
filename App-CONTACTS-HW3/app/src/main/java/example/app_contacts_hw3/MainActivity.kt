package example.app_contacts_hw3

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import example.app_contacts_hw3.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var listSize = -1
    private lateinit var contactListUsing: List<Contact>
    private lateinit var contactListMain: List<Contact>
    private lateinit var mAdapter: ContactAdapter
    private var showToastCount = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermissions()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            0 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showList()
                } else {
                    sendToast(resources.getString(R.string.contact_permission))
                }
                return
            }
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this, // Контекст
                arrayOf(
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.SEND_SMS
                ), // Что спрашиваем
                0
            ) // Пользовательская константа для уникальности запроса
        } else {
            showList()
        }
    }

    private fun showList() {
        checkTextBar()

        recyclerDraw()

    }

    private fun recyclerDraw() {
        val myRecyclerView = binding.myRecyclerView
        val viewManager = LinearLayoutManager(this)
        contactListUsing = fetchAllContacts()
        contactListMain = contactListUsing
        mAdapter = ContactAdapter(contactListUsing) { it: Contact, a: ClickType ->
            openContactJournal(it, a)
        }
        myRecyclerView.apply {
            layoutManager = viewManager
            listSize = contactListUsing.size
            adapter = mAdapter
        }
        if (!showToastCount) showItemCount()
        showToastCount = true
        with(binding.actionBar.editText) {
            visibility = View.VISIBLE
            text.clear()
        }
    }

    private fun checkTextBar() {
        binding.actionBar.editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (contactListMain.isNullOrEmpty()) return

                if (s.isNullOrEmpty()) recyclerDraw()
                else {
                    val tempContacts = ArrayList<Contact>()
                    for (contact in contactListMain) {
                        if (regexFormat(contact.phoneNumber)
                                .contains(regexFormat(s.toString()))
                        ) tempContacts.add(contact)
                    }
                    contactListUsing = tempContacts.toList()

                    mAdapter.setList(contactListUsing)
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

    }

    private fun showItemCount() {
        if (listSize == -1) sendToast(resources.getString(R.string.error_contacts))
        if (listSize == 1) sendToast(resources.getString(R.string.one_contact))
        else sendToast(resources.getString(R.string.more_contacts).format(listSize))
    }

    private fun openContactJournal(it: Contact, type: ClickType) {

        when (type) {
            ClickType.PHONE_CLICK -> phoneJournalFunction(it)
            ClickType.SHARE_CLICK -> shareJournalFunction(it)
            ClickType.MESSAGE_CLICK -> messageJournalFunction(it)
        }
    }

    private fun contactMessage(it: Contact) =
        resources.getString(R.string.sms_body).format(it.name, it.phoneNumber)

    private fun shareJournalFunction(it: Contact) {
        val intent = Intent(Intent.ACTION_SEND).setType("*/*")
        intent.putExtra(Intent.EXTRA_TEXT, contactMessage(it))
        startActivity(Intent.createChooser(intent, resources.getString(R.string.share_title)))
    }

    private fun messageJournalFunction(it: Contact) {

        val permissionStatus =
            ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            val messageBody = "smsto:" + it.phoneNumber +
                    "?subject=" + Uri.encode("") +
                    "&body=" + Uri.encode(contactMessage(it))
            val intent =
                Intent(Intent.ACTION_SENDTO, Uri.parse(messageBody))
            intent.putExtra(
                Intent.EXTRA_TEXT, contactMessage(it)
            )
            startActivity(intent)
        } else {
            sendToast(resources.getString(R.string.sms_permission))
        }


    }

    private fun phoneJournalFunction(it: Contact) {
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + it.phoneNumber)))
    }

    private fun sendToast(message: String) {
        Toast.makeText(
            this@MainActivity,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    fun updateList(list: List<Contact>) {
        contactListMain = list
        contactListUsing = list
        mAdapter.setList(contactListUsing)
    }

}