package com.example.todolist

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface TodoListApiServiceInterface {
    @GET("todo")
    fun getData():Call<List<TodoListItem>>

    @GET("todo/{id}")
    fun searchId(@Path("id") id: String): Call<TodoListItem>

    @FormUrlEncoded
    @PUT("todo/{id}")
    fun updateList(
        @Path("id") id:String,
        @Field("title") title: String,
        @Field("completed") completed: Boolean
    ): Call<TodoListItem>

    @POST("todo")
    fun addNew(@Body newTodoListItem: TodoListItem): Call<TodoListItem>

    @DELETE("todo/{id}")
    fun deleteId(@Path("id") id: String): Call<TodoListItem>
}