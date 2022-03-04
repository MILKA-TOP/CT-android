package example.app_contacts_hw3

import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import androidx.test.rule.GrantPermissionRule
import example.app_contacts_hw3.databinding.ActivityMainBinding
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@Config(sdk = [Config.OLDEST_SDK])
@RunWith(RobolectricTestRunner::class)
class UnitTestWithPermission {


    private lateinit var mainActivity: MainActivity
    private lateinit var adapter: ContactAdapter
    private lateinit var editText: EditText

    @get:Rule
    val permissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.READ_CONTACTS)

    @Before
    fun init() {
        mainActivity =
            Robolectric.buildActivity(MainActivity::class.java).create().start().get()
        //binding = ActivityMainBinding.inflate(mainActivity.layoutInflater)
        //assertNotNull(binding.myRecyclerView)
        assertNotNull(mainActivity.binding.myRecyclerView)
        //editText = mainActivity.findViewById<EditText>(R.id.myRecyclerView)
        editText = mainActivity.binding.actionBar.editText
        //adapter =
        //    mainActivity.findViewById<RecyclerView>(R.id.myRecyclerView).adapter as ContactAdapter
        adapter = mainActivity.binding.myRecyclerView.adapter as ContactAdapter
    }



    @Test
    fun fullListEmptyText() {
        mainActivity.updateList(FULL_CONTACT_LIST)
        editText.setText("")
        assertEquals(
            FULL_CONTACT_LIST.sortedBy { it.phoneNumber },
            adapter.getList().sortedBy { it.phoneNumber })
    }

    @Test
    fun fullListAlpText() {
        mainActivity.updateList(FULL_CONTACT_LIST)
        editText.setText("ПРОДАМ ГАРАЖ")
        assertEquals(
            FULL_CONTACT_LIST.sortedBy { it.phoneNumber },
            adapter.getList().sortedBy { it.phoneNumber })
    }

    @Test
    fun fullListSame67Text() {
        mainActivity.updateList(FULL_CONTACT_LIST)
        editText.setText("67")
        assertEquals(
            SAME_TEXT_67_CONTACT_LIST.sortedBy { it.phoneNumber },
            adapter.getList().sortedBy { it.phoneNumber })
    }


    @Test
    fun sameListSameText() {
        mainActivity.updateList(SAME_LIST)
        editText.setText("12")
        assertEquals(
            SAME_LIST.sortedBy { it.phoneNumber },
            adapter.getList().sortedBy { it.phoneNumber })
    }

    @Test
    fun sameListBadText() {
        mainActivity.updateList(SAME_LIST)
        assertEquals(SAME_LIST, adapter.getList())
        editText.setText("9863576406")
        assertEquals(EMPTY_CONTACT_LIST, adapter.getList())
    }

    @Test
    fun bigContactsCountExtremeEmptyText() {
        val allContacts = ArrayList<Contact>()
        for (i in 0..BIGVAL) {
            allContacts.add(Contact("$i", "$i"))
        }
        mainActivity.updateList(allContacts as List<Contact>)
        editText.setText("")
        assertEquals(
            allContacts.sortedBy { it.phoneNumber },
            adapter.getList().sortedBy { it.phoneNumber })
    }

    @Test
    fun bigContactsCountExtremeBadText() {
        val allContacts = ArrayList<Contact>()
        for (i in 0..BIGVAL) {
            allContacts.add(Contact("$i", "$i"))
        }
        mainActivity.updateList(allContacts as List<Contact>)
        editText.setText((BIGVAL + 1).toString())
        assertEquals(EMPTY_CONTACT_LIST, adapter.getList())
    }
}