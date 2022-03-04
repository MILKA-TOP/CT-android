package example.app_images_hw4

import android.content.*
import android.content.Intent.CATEGORY_DEFAULT
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import example.app_images_hw4.databinding.BigPictureBinding
import example.app_images_hw4.databinding.ErrorPictureBinding
import example.app_images_hw4.databinding.LoadingBinding


class BigPictureActivity : AppCompatActivity() {
    private lateinit var binding: BigPictureBinding
    private var imageObject: ImageObject? = null
    private var util = Util()
    private var broadcastReceiver = ShowImageReceiver()
    private lateinit var binderBridge: DownloadService.ImageBinder
    private lateinit var intentService: Intent
    private var myService: DownloadService? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BigPictureBinding.inflate(layoutInflater)
        setContentView(LoadingBinding.inflate(layoutInflater).root)
        imageObject = intent.getParcelableExtra(IMAGE_OBJECT_DATA)

        if (util.containsFileByUri(cacheDir, imageObject!!.id, ImageType.REGULAR))
            drawActivity(imageObject!!)
        else uploadInitImage()
    }


    inner class ShowImageReceiver() : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val downloadStatus = binderBridge.getMyDownloadStatus()
            if (downloadStatus == ServiceDownloadType.COMPLETED) drawActivity(binderBridge.getMyImageObject())
            else if (downloadStatus == ServiceDownloadType.ERROR) {
                makeSadActivity()
            }
        }
    }

    private fun uploadInitImage() {
        intentService =
            Intent(this, DownloadService::class.java).putExtra(IMAGE_OBJECT_DATA, imageObject)
        startService(intentService)
        bindService(intentService, boundServiceConnection, BIND_AUTO_CREATE)


        val intentFilter = IntentFilter(INTENT_FILTER_SEND)
        intentFilter.addCategory(CATEGORY_DEFAULT)
        registerReceiver(broadcastReceiver, intentFilter)
    }


    private fun makeSadActivity() {
        setContentView(ErrorPictureBinding.inflate(layoutInflater).root)
    }

    private fun drawActivity(imageObject: ImageObject) {

        if (imageObject.regularUri == null) {
            imageObject.regularUri = util.getUri(cacheDir, this, imageObject.id, ImageType.REGULAR)
        }

        binding.imageBig.setImageURI(imageObject.regularUri)
        binding.like.text = imageObject.likesCount.toString()
        binding.description.text = imageObject.description
        binding.author.text = imageObject.author
        setContentView(binding.root)
    }


    override fun onDestroy() {
        if (this::intentService.isInitialized) stopService(intentService)
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(IMAGE_OBJECT_DATA, imageObject)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        imageObject = savedInstanceState.getParcelable(IMAGE_OBJECT_DATA)
    }


    private val boundServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            binderBridge = service as DownloadService.ImageBinder
            myService = binderBridge.getMyService()
            val downloadStatus = binderBridge.getMyDownloadStatus()
            if (downloadStatus == ServiceDownloadType.COMPLETED) {
                val broadcastIntent = Intent()
                broadcastIntent.action = INTENT_FILTER_SEND
                broadcastIntent.addCategory(CATEGORY_DEFAULT)
                sendBroadcast(broadcastIntent)
            } else if (downloadStatus == ServiceDownloadType.ERROR) {
                makeSadActivity()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            myService = null
        }
    }
}