package example.app_response_base_hw8

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import example.app_response_base_hw8.*
import example.app_response_base_hw8.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@ObsoleteCoroutinesApi
class MainActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var binding: ActivityMainBinding
    private lateinit var responseAdapter: ResponseAdapter
    private lateinit var customGridLayoutManager: CustomGridLayoutManager
    private var responseList = ArrayList<ResponseData>()
    private var nextPostId = 0
    private var deletedNumber = -1
    private var isClickableButtons = true
    private var scope = CoroutineScope(newSingleThreadContext(THREAD_NAME))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        customGridLayoutManager = CustomGridLayoutManager(this)
        setContentView(binding.root)

        checkDatabase()
    }

    private fun startDownloadList() {
        binding.refresh.visibility = View.INVISIBLE
        openProgressBar()
        MainApp.instance.responseService.getAllResponses().enqueue(ControllerList())
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

        if (deletedNumber == -1) return

        deleteResponseDatabase(it)
        responseList.removeAt(deletedNumber)
        responseAdapter.notifyItemRemoved(deletedNumber)
        deletedNumber = -1

        if (responseList.isEmpty()) binding.refresh.visibility = View.VISIBLE
    }


    private fun addNewPost(data: Intent) {
        val title = data.getStringExtra(TITLE).toString()
        val body = data.getStringExtra(BODY).toString()
        val newResponseData = ResponseData(++nextPostId, 1, title, body)
        putResponseDatabase(newResponseData)
        responseList.add(newResponseData)
        responseAdapter.notifyItemInserted(responseList.size - 1)
        binding.refresh.visibility = View.INVISIBLE
    }

    private fun clearAllList() {
        responseList.clear()
        binding.refresh.visibility = View.VISIBLE
        nextPostId = 0
        updateRecyclerView()
        clearDatabase()
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
            if (isClickableButtons) deletePost(it)
        }
        recyclerViewUsing()
    }


    inner class ControllerList : Callback<List<ResponseData>> {
        override fun onResponse(
            call: Call<List<ResponseData>>,
            response: Response<List<ResponseData>>
        ) {
            if (response.body() == null) return
            Log.d("CONNECTED", "GOOD")
            responseList.clear()
            responseList.addAll(response.body()!!.subList(0, 12))
            Log.d("lisT", responseList.toString())
            nextPostId = responseList.last().postId + 1
            updateRecyclerView()
            putAllDatabase()
        }

        override fun onFailure(call: Call<List<ResponseData>>, t: Throwable) {
            Toast.makeText(this@MainActivity, t.toString(), Toast.LENGTH_SHORT).show()
            Log.d(ERROR_FAIL, t.toString())
            if (responseList.isEmpty()) binding.refresh.visibility = View.VISIBLE
            closeProgressBar()
            MainApp.instance.retrofitReconnection()
        }
    }


    private fun putAllDatabase() {
        scope.launch {
            MainApp.instance.dataBase.responseDao()?.clearDb()
            MainApp.instance.dataBase.responseDao()?.insertAll(responseList.toList())
        }
    }

    private fun clearDatabase() {
        scope.launch {
            MainApp.instance.dataBase.responseDao()?.clearDb()
        }
    }

    private fun putResponseDatabase(it: ResponseData) {
        scope.launch {
            with(MainApp.instance.dataBase.responseDao()) {
                while (this?.getById(it.postId)!!) {
                    it.postId++
                }
                this.insertResponse(it)
            }
        }
    }

    private fun deleteResponseDatabase(it: ResponseData) {
        scope.launch {
            MainApp.instance.dataBase.responseDao()?.deleteResponse(it)
        }
    }

    private fun checkDatabase() {
        scope.launch {
            responseList =
                MainApp.instance.dataBase.responseDao()?.getAll() as ArrayList<ResponseData>
            updateRecyclerView()
            if (responseList.isEmpty()) binding.refresh.visibility = View.VISIBLE

            nextPostId = if (responseList.isEmpty()) 0
            else responseList.last().postId + 1
        }
    }


    private fun openProgressBar() {
        binding.progressBar.visibility = ProgressBar.VISIBLE
        isClickableButtons = false
        customGridLayoutManager.setScrollEnabled(false)
        binding.refresh.visibility = View.INVISIBLE
    }

    private fun closeProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
        isClickableButtons = true
        customGridLayoutManager.setScrollEnabled(true)
    }


    override fun onClick(p0: View?) {
        if (p0 == null) return
        if (!isClickableButtons) return

        when (p0.id) {
            R.id.refresh -> startDownloadList()
            R.id.refresh_tool_bar -> startDownloadList()
            R.id.plus_response -> startPostResponse()
            R.id.clear_list -> clearAllList()
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }


}