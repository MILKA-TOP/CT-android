package example.app_contacts_hw3

import android.app.Activity
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import example.app_contacts_hw3.databinding.ActivityMainBinding
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@Config(sdk = [Config.OLDEST_SDK])
@RunWith(RobolectricTestRunner::class)
class UnitTestWithoutPermission {


    private lateinit var mainActivity: Activity
    private lateinit var binding: ActivityMainBinding


    @Before
    fun init() {
        mainActivity =
            Robolectric.buildActivity(MainActivity::class.java).create().get()
        binding = ActivityMainBinding.inflate(mainActivity.layoutInflater)
    }


    // Самый полезный тест
    @Test
    fun checkTrue() {
        assert(true)
    }

    @Test
    fun activityNotNull() {
        assertNotNull(mainActivity)
    }

    @Test
    fun recyclerNotNull() {
        assertNotNull(binding.myRecyclerView)
    }

    @Test
    fun editTextNNotNull() {
        assertNotNull(binding.actionBar.editText)
    }

    @Test
    fun checkInvisibleEditText() {
        assert(binding.actionBar.editText.visibility == View.INVISIBLE)
    }

    @Test
    fun checkNullAdapterWithoutPermission() {
        assertNull(binding.myRecyclerView.adapter)
    }

}