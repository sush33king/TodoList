package com.example.todolist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_add_new.*
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val BASE_URL = "https://607fd2fea5be5d00176dc52b.mockapi.io/"

class AddNewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new)
    }

    //event handler for the add new task button
    fun btnAddNewTask(view: View) {

        if (editTextTextPersonName.text.toString() == "")
        {
            Toast.makeText(this, "Task name cannot be empty. Pls try again.", Toast.LENGTH_SHORT).show()
            return;
        }
        else
        {
            addNewListItem(editTextTextPersonName.text.toString(), checkBox.isChecked)
            editTextTextPersonName.setText("")
        }
    }

    //adds a new record into the server via a post request
    private fun addNewListItem(title: String, completed: Boolean)
    {
        // Create Retrofit
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(TodoListApiServiceInterface::class.java)

        // link to the appropriate service method
        val retrofitData = retrofitBuilder.addNew(TodoListItem("123", title, completed))

        //execute the post request
        retrofitData.enqueue(object: Callback<TodoListItem> {

            override fun onResponse(call: Call<TodoListItem>, response: Response<TodoListItem>) {
                if (response.isSuccessful) {

                    var newlyCreatedDestination = response.body() // Use it or ignore it
                    Toast.makeText(this@AddNewActivity, "Successfully Added", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this@AddNewActivity, "Failed to add item", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TodoListItem>, t: Throwable) {
                Toast.makeText(this@AddNewActivity, "Failed to add item", Toast.LENGTH_SHORT).show()
            }
        })
    }
}