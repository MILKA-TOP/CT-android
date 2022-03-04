package example.app_images_hw4

import android.app.Service
import android.content.Intent
import android.content.Intent.CATEGORY_DEFAULT
import android.os.Binder
import android.os.IBinder
import kotlinx.coroutines.*


class DownloadService : Service() {

    private lateinit var imageObject: ImageObject
    private lateinit var scope: CoroutineScope
    private val util = Util()
    private var downloadStatus = ServiceDownloadType.NULL

    override fun onBind(intent: Intent): IBinder {

        imageObject = intent.getParcelableExtra(IMAGE_OBJECT_DATA)!!

        imageDownload()

        return ImageBinder()
    }

    private fun imageDownload() {
        downloadStatus = ServiceDownloadType.WAITING
        scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            val bitmap =
                withContext(Dispatchers.IO) { Util().getBitmapFromURL(imageObject.regularImageLink) }

            if (bitmap != null) {
                val uri = util.saveImage(
                    bitmap,
                    cacheDir,
                    this@DownloadService,
                    imageObject.id,
                    ImageType.REGULAR
                )
                imageObject.regularUri = uri
                downloadStatus = ServiceDownloadType.COMPLETED
            } else downloadStatus = ServiceDownloadType.ERROR


            val respIntent = Intent()
            respIntent.action = INTENT_FILTER_SEND
            respIntent.addCategory(CATEGORY_DEFAULT)
            sendBroadcast(respIntent)

        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    inner class ImageBinder : Binder() {
        fun getMyService() = this@DownloadService
        fun getMyImageObject() = imageObject
        fun getMyDownloadStatus() = downloadStatus
    }
}