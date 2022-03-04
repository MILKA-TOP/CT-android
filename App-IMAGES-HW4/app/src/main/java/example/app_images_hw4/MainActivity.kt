package example.app_images_hw4

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import example.app_images_hw4.databinding.ActivityMainBinding
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var imageMap = HashMap<String, ImageObject>()
    private lateinit var scope: CoroutineScope
    private lateinit var intentPicture: Intent
    private var util = Util()
    private var afterRotate = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        scope = CoroutineScope(Dispatchers.Main)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        val idList = imageMap.keys.toTypedArray()
        val objectList = imageMap.values.toTypedArray()
        outState.putStringArray(ID_LIST, idList)
        outState.putParcelableArray(OBJECT_LIST, objectList)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val idList = savedInstanceState.getStringArray(ID_LIST)
        afterRotate = true
        imageMap = if (idList!!.isEmpty()) {
            HashMap()
        } else {
            val objectList = savedInstanceState.getParcelableArray(OBJECT_LIST)!!.toList()
            idList.toList().zip(objectList).toMap() as HashMap<String, ImageObject>
        }
    }


    override fun onPause() {
        scope.cancel()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()

        if (imageMap.isNotEmpty()) {
            if (afterRotate)
                recyclerViewUsing(imageMap.values.toList())
            return
        }

        scope = CoroutineScope(Dispatchers.Main)
        scope.launch {

            if (imageMap.isEmpty()) {
                if (!download()) return@launch
            }

            for ((_, value) in imageMap) {
                val bitmap =
                    async(Dispatchers.Default) { util.getBitmapFromURL(value.smallImageLink) }
                value.smallUri =
                    util.saveImage(
                        bitmap.await(),
                        cacheDir,
                        this@MainActivity,
                        value.id,
                        ImageType.SMALL
                    )
            }
            recyclerViewUsing(imageMap.values.toList())
        }

    }


    private fun recyclerViewUsing(list: List<ImageObject>) {
        afterRotate = false

        val myRecyclerView = binding.recyclerView
        val viewManager = GridLayoutManager(this, 3)
        myRecyclerView.apply {
            layoutManager = viewManager
            adapter = ImageAdapter(list) {
                imageClick(it)
            }
        }
    }


    private fun imageClick(it: ImageObject) {

        intentPicture = Intent(this, BigPictureActivity::class.java).putExtra(IMAGE_OBJECT_DATA, it)
        startActivity(intentPicture)

        if (imageMap[it.id]!!.regularUri == null) imageMap[it.id]!!.regularUri =
            util.getUri(cacheDir, this@MainActivity, it.id, ImageType.REGULAR)
    }


    private fun getSugarJson(page: Int) =
        util.getJsonFromNet(
            page,
            "https://api.unsplash.com/search/photos?client_id=$TOKEN&query=Android theme&page="
        )


    private suspend fun download(): Boolean {
        util.sendToast(resources.getString(R.string.download_array), this@MainActivity)

        val firstPageString =
            withContext(Dispatchers.Default) { getSugarJson(1) }
        val secondPageString =
            withContext(Dispatchers.Default) { getSugarJson(2) }

        if (firstPageString.isEmpty() && secondPageString.isEmpty()) {
            util.sendToast(
                resources.getString(R.string.app_internet),
                this@MainActivity
            )
            return false
        }
        val imageList = util.getList(firstPageString) + util.getList(secondPageString)
        imageMap = imageList.map { it.id to it }.toMap() as HashMap<String, ImageObject>
        return true
    }
}