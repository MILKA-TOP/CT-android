package example.app_response_hw7

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import example.app_response_hw7.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var binding: ActivityMainBinding
    private lateinit var responseAdapter: ResponseAdapter
    private lateinit var customGridLayoutManager: CustomGridLayoutManager
    private var responseList = ArrayList<ResponseData>()
    private var nextPostId = 0
    private var deletedNumber = -1
    private var isClickableButtons = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        customGridLayoutManager = CustomGridLayoutManager(this)
        setContentView(binding.root)

        if (savedInstanceState == null) startDownloadList()

    }

    private fun startDownloadList() {
        with(binding) {
            refresh.visibility = View.INVISIBLE
        }
        openProgressBar()
        FakeApp.instance.responseService.getAllResponses().enqueue(ControllerList())
    }

    private fun startPostResponse() {
        val intent = Intent(this, InputResponseDataActivity::class.java)
        resultLauncher.launch(intent)
    }

    private var resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            addNewPost(data!!)
        }
    }

    private fun deletePost(it: ResponseData) {
        deletedNumber = responseList.indexOf(it)
        openProgressBar()
        FakeApp.instance.responseService.deletePost(it.postId).enqueue(ControllerData())
    }


    private fun addNewPost(data: Intent) {
        val title = data.getStringExtra(TITLE).toString()
        val body = data.getStringExtra(BODY).toString()
        val newResponseData = ResponseData(1, nextPostId++, title, body)
        openProgressBar()
        FakeApp.instance.responseService.getNewPost(
            newResponseData
        ).enqueue(ControllerData())
    }

    private fun recyclerViewUsing() {
        val myRecyclerView = binding.myRecyclerView
        myRecyclerView.apply {
            layoutManager = customGridLayoutManager
            adapter = responseAdapter
        }
        closeProgressBar()
    }

    private fun updateRecyclerView() {
        responseAdapter = ResponseAdapter(responseList) {
            deletePost(it)
        }
        recyclerViewUsing()
    }


    inner class ControllerList : Callback<List<ResponseData>> {
        override fun onResponse(
            call: Call<List<ResponseData>>,
            response: Response<List<ResponseData>>
        ) {
            if (response.body() == null) return

            responseList.clear()
            responseList.addAll(response.body()!!.subList(0, 25))
            nextPostId = responseList.last().postId + 1
            updateRecyclerView()
        }

        override fun onFailure(call: Call<List<ResponseData>>, t: Throwable) {
            Toast.makeText(this@MainActivity, t.toString(), Toast.LENGTH_SHORT).show()
            Log.d(ERROR_FAIL, t.toString())
            if (responseList.isEmpty()) binding.refresh.visibility = View.VISIBLE
            closeProgressBar()
            FakeApp.instance.onCreate()
        }
    }

    inner class ControllerData() : Callback<ResponseData> {

        override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {

            when (call.request().method()) {
                DELETE -> {
                    if (deletedNumber == -1) return

                    responseList.removeAt(deletedNumber)
                    responseAdapter.notifyItemRemoved(deletedNumber)
                    deletedNumber = -1

                    if (responseList.isEmpty()) binding.refresh.visibility = View.VISIBLE
                }
                POST -> {
                    if (response.body() == null) return

                    responseList.add(response.body()!!)
                    responseAdapter.notifyItemInserted(responseList.size)

                    if (responseList.isNotEmpty()) binding.refresh.visibility = View.INVISIBLE
                }
            }

            closeProgressBar()
            Toast.makeText(this@MainActivity, response.toString(), Toast.LENGTH_SHORT).show()
        }

        override fun onFailure(call: Call<ResponseData>, t: Throwable) {
            Log.d(ERROR_FAIL, t.toString())
            Toast.makeText(this@MainActivity, t.toString(), Toast.LENGTH_SHORT).show()
            closeProgressBar()
            FakeApp.instance.onCreate()
        }

    }

    private fun openProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        isClickableButtons = false
        customGridLayoutManager.setScrollEnabled(false)
    }

    private fun closeProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
        isClickableButtons = true
        customGridLayoutManager.setScrollEnabled(true)
    }

    override fun onSaveInstanceState(outState: Bundle) {

        outState.putParcelableArrayList(RESPONSE_LIST, responseList)
        outState.putInt(NEXT_POSITION_ID, nextPostId)
        outState.putBoolean(IS_CLICABLE_BUTTONS, isClickableButtons)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        responseList = savedInstanceState.getParcelableArrayList(RESPONSE_LIST)!!
        nextPostId = savedInstanceState.getInt(NEXT_POSITION_ID)
        isClickableButtons = savedInstanceState.getBoolean(IS_CLICABLE_BUTTONS)
        customGridLayoutManager.setScrollEnabled(isClickableButtons)
        if (!isClickableButtons) openProgressBar()

        updateRecyclerView()
    }

    override fun onClick(p0: View?) {
        if (p0 == null) return
        if (!isClickableButtons) return

        when (p0.id) {
            R.id.refresh -> startDownloadList()
            R.id.refresh_tool_bar -> startDownloadList()
            R.id.plus_response -> startPostResponse()
        }
    }


}