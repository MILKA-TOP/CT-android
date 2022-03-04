package example.app_response_hw7

import retrofit2.Call
import retrofit2.http.*

interface ResponseService {

    @GET("posts")
    fun getAllResponses(): Call<List<ResponseData>>

    @DELETE("posts/{id}")
    fun deletePost(
        @Path("id") id: Int
    ): Call<ResponseData>

    @POST("/posts")
    fun getNewPost(
        @Body body: ResponseData,
    ): Call<ResponseData>


}