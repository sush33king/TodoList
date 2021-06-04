package com.example.todolist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_new.*
import kotlinx.android.synthetic.main.todo_item.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://607fd2fea5be5d00176dc52b.mockapi.io/"
var itemId = ""

class UpdateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        //populate values
        itemId = intent.getStringExtra("id").toString()
        editTextTextPersonName.setText(intent.getStringExtra("title").toString())
        checkBox.setChecked(intent.getStringExtra("completed").toBoolean())
    }

    fun btnUpdateTask(view: View) {

        if (editTextTextPersonName.text.toString() == "")
        {
            Toast.makeText(this, "Task name cannot be empty. Pls try again.", Toast.LENGTH_SHORT).show()
            return;
        }
        else
        {
            updateListItem(itemId, editTextTextPersonName.text.toString(), checkBox.isChecked)
            finish()
        }
    }

    fun btnDeleteTask(view: View) {
        deleteListItem(itemId)
        finish()
    }

    private fun updateListItem(id: String, title: String, completed: Boolean)
    {
//        Toast.makeText(this@UpdateActivity, "in update me", Toast.LENGTH_SHORT).show()

        val newItem = TodoListItem(id, title, completed)

        // Create Retrofit
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(TodoListApiServiceInterface::class.java)

        val retrofitData = retrofitBuilder.updateList(id, title, completed)

        retrofitData.enqueue(object: Callback<TodoListItem> {

            override fun onResponse(call: Call<TodoListItem>, response: Response<TodoListItem>) {
                if (response.isSuccessful) {
                    var newlyCreatedDestination = response.body() // Use it or ignore it
                    Toast.makeText(this@UpdateActivity, "Successfully Updated", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this@UpdateActivity, "Failed to update item", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TodoListItem>, t: Throwable) {
                Toast.makeText(this@UpdateActivity, "Failed to update item", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //repeated code, if there was more time, code refactoring and consolidation should be focused on.
    //uses the http delete method to remove record from the server
    private fun deleteListItem(id: String)
    {
        Toast.makeText(this@UpdateActivity, "in delete me", Toast.LENGTH_SHORT).show()

        // Create Retrofit
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(TodoListApiServiceInterface::class.java)

        val retrofitData = retrofitBuilder.deleteId(id)

        retrofitData.enqueue(object: Callback<TodoListItem> {

            override fun onResponse(call: Call<TodoListItem>, response: Response<TodoListItem>) {
                if (response.isSuccessful) {
                    var newlyCreatedDestination = response.body() // Use it or ignore it
                    Toast.makeText(this@UpdateActivity, "Successfully Deleted.", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this@UpdateActivity, "Failed to delete item. Data might not exist.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TodoListItem>, t: Throwable) {
                Toast.makeText(this@UpdateActivity, "Failed to delete item", Toast.LENGTH_SHORT).show()
            }
        })
    }

}