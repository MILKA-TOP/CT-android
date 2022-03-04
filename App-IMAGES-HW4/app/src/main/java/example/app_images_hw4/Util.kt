package example.app_images_hw4

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.io.InputStream

import java.net.HttpURLConnection





/*
* Не бейте пожалуйста за такое, я понимаю, что нужно было использовать values/string.xml, просто
* из-за него все получается гораздо нагромождённее :)
* */

const val IMAGE_OBJECT_DATA = "IMAGE_OBJECT_DATA"
const val INTENT_FILTER_SEND = "INTENT_FILTER_SEND"
const val ID_LIST = "ID_LIST"
const val OBJECT_LIST = "OBJECT_LIST"
const val TOKEN = "cne74hhhiqxVlKSz0Il6QSLATm44flH_d0Uo4V0rABQ"


enum class ImageType {
    REGULAR, SMALL
}

enum class ServiceDownloadType {
    NULL, ERROR, COMPLETED, WAITING
}


class Util {


    fun getList(json: String): ArrayList<ImageObject> {
        val tempList = ArrayList<ImageObject>()
        val jObject = JSONObject(json)

        if (!jObject.has("results")) return tempList

        val array = jObject.getJSONArray("results")
        for (i in 0 until array.length()) {
            val urls = array.getJSONObject(i).getJSONObject("urls")
            val regularLink = urls.getString("regular")
            val smallLink = urls.getString("small")
            val descriptions = array.getJSONObject(i).getString("alt_description")
            val likesCount = array.getJSONObject(i).getInt("likes")
            val id = array.getJSONObject(i).getString("id")
            val author = array.getJSONObject(i).getJSONObject("user").getString("name")

            tempList.add(
                ImageObject(
                    regularLink,
                    smallLink,
                    descriptions,
                    likesCount,
                    author,
                    null, null, id
                )
            )
        }
        return tempList
    }

    fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()

            //После следующей строчки какие-то проблемы. Почему? Не знаю (((
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            null
        }
    }

    fun sendToast(str: String, context: Context) {
        Toast.makeText(
            context,
            str,
            Toast.LENGTH_SHORT
        ).show()
    }

    fun containsFileByUri(cacheDir: File, id: String, type: ImageType): Boolean {
        val imagesFolder = File(cacheDir, "images")
        imagesFolder.mkdirs()
        val file = File(imagesFolder, "$id$type.png")

        if (imagesFolder.walk().contains(file)) return true

        return false

    }

    fun getUri(cacheDir: File, context: Context, id: String, type: ImageType): Uri {
        val imagesFolder = File(cacheDir, "images")
        imagesFolder.mkdirs()
        val file = File(imagesFolder, "$id$type.png")

        return FileProvider.getUriForFile(context, "com.mydomain.fileprovider", file)
    }

    fun saveImage(
        image: Bitmap?,
        cacheDir: File,
        context: Context,
        id: String,
        type: ImageType
    ): Uri? {
        val imagesFolder = File(cacheDir, "images")
        var uri: Uri? = null
        try {
            imagesFolder.mkdirs()
            val file = File(imagesFolder, "$id$type.png")

            if (imagesFolder.walk().contains(file)) return FileProvider.getUriForFile(
                context,
                "com.mydomain.fileprovider",
                file
            )

            val stream = FileOutputStream(file)
            image!!.compress(Bitmap.CompressFormat.PNG, 90, stream)
            stream.flush()
            stream.close()
            uri = FileProvider.getUriForFile(context, "com.mydomain.fileprovider", file)
        } catch (e: IOException) {
            Log.d("CACHE_ERROR", "IOException while trying to write file for sharing: " + e.message)
        }
        return uri
    }

    fun getJsonFromNet(page: Int, url: String): String {
        return try {
            URL(url + page).openConnection().run {
                connect()
                getInputStream().bufferedReader().readLines().joinToString("")
            }
        } catch (_: Exception) {
            String()
        }
    }
}