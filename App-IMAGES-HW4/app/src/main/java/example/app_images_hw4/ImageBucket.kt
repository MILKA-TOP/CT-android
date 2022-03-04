package example.app_images_hw4

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import example.app_images_hw4.databinding.ImageBucketBinding


data class ImageObject(
    val regularImageLink: String,
    val smallImageLink: String,
    val description: String,
    val likesCount: Int,
    val author: String,
    var regularUri: Uri?,
    var smallUri: Uri?,
    var id: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readParcelable(Uri::class.java.classLoader),
        parcel.readParcelable(Uri::class.java.classLoader),
        parcel.readString().toString()
        )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(regularImageLink)
        parcel.writeString(smallImageLink)
        parcel.writeString(description)
        parcel.writeInt(likesCount)
        parcel.writeString(author)
        parcel.writeParcelable(regularUri, flags)
        parcel.writeParcelable(smallUri, flags)
        parcel.writeString(id)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ImageObject> {
        override fun createFromParcel(parcel: Parcel): ImageObject {
            return ImageObject(parcel)
        }

        override fun newArray(size: Int): Array<ImageObject?> {
            return arrayOfNulls(size)
        }
    }

}

class ImageViewHolder(private val imageBucketViewBinding: ImageBucketBinding) :
    RecyclerView.ViewHolder(imageBucketViewBinding.root) {

    fun bind(image: ImageObject) {
        //Picasso.get().load(image.smallImageLink).into(imageBucketViewBinding.image)
        imageBucketViewBinding.image.setImageURI(image.smallUri)
    }
}

class ImageAdapter(
    private val list: List<ImageObject>,
    private val onClick: (ImageObject) -> Unit
) :
    RecyclerView.Adapter<ImageViewHolder>() {

    private lateinit var itemPersonBinding: ImageBucketBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        itemPersonBinding = ImageBucketBinding.inflate(layoutInflater, parent, false)
        val holder = ImageViewHolder(itemPersonBinding)


        itemPersonBinding.image.setOnClickListener {
            onClick(list[holder.adapterPosition])
        }

        return holder
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) =
        holder.bind(list[position])

    override fun getItemCount() = list.size

}