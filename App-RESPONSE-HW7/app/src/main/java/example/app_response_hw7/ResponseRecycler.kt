package example.app_response_hw7

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.moshi.Json
import example.app_response_hw7.databinding.ResponseObjectBinding
import android.content.Context

import androidx.recyclerview.widget.LinearLayoutManager




data class ResponseData(
    @field:Json(name = "userId") var userId: Int = 0,
    @field:Json(name = "id") var postId: Int = 0,
    @field:Json(name = "title") var postTitle: String? = "",
    @field:Json(name = "body") var postBody: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(userId)
        parcel.writeInt(postId)
        parcel.writeString(postTitle)
        parcel.writeString(postBody)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ResponseData> {
        override fun createFromParcel(parcel: Parcel): ResponseData {
            return ResponseData(parcel)
        }

        override fun newArray(size: Int): Array<ResponseData?> {
            return arrayOfNulls(size)
        }
    }
}

class ResponseViewHolder(private val responseViewBinding: ResponseObjectBinding) :
    RecyclerView.ViewHolder(responseViewBinding.root) {


    fun bind(responseData: ResponseData) {
        responseViewBinding.titleName.text = responseData.postTitle
        responseViewBinding.description.text = responseData.postBody
    }
}

class ResponseAdapter(
    private val responses: List<ResponseData>,
    private val onClick: (ResponseData) -> Unit
) : RecyclerView.Adapter<ResponseViewHolder>() {

    private lateinit var itemPersonBinding: ResponseObjectBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResponseViewHolder {


        val layoutInflater = LayoutInflater.from(parent.context)
        itemPersonBinding = ResponseObjectBinding.inflate(layoutInflater, parent, false)
        val holder = ResponseViewHolder(itemPersonBinding)
        itemPersonBinding.button.setOnClickListener {
            if (holder.adapterPosition != -1) onClick(responses[holder.adapterPosition])
        }

        return holder

    }

    override fun onBindViewHolder(holder: ResponseViewHolder, position: Int) =
        holder.bind(responses[position])

    override fun getItemCount() = responses.size


}

class CustomGridLayoutManager(context: Context?) : LinearLayoutManager(context) {
    private var isScrollEnabled = true
    fun setScrollEnabled(flag: Boolean) {
        isScrollEnabled = flag
    }

    override fun canScrollVertically(): Boolean {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically()
    }
}