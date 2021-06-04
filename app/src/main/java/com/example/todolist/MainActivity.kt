package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.collections.ArrayList
import java.util.Timer
import kotlin.concurrent.schedule


private const val BASE_URL = "https://607fd2fea5be5d00176dc52b.mockapi.io/"

class MainActivity : AppCompatActivity(), TodoListAdapter.OnItemClickListener, TodoListAdapter.OnItemLongClickListener{

    lateinit var todoListAdapter: TodoListAdapter
    lateinit var linearLayoutManager: LinearLayoutManager

    //used for filtering lst
    var displayList = ArrayList<TodoListItem>()
    var actualList = ArrayList<TodoListItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        linearLayoutManager = LinearLayoutManager(this)
        recyclerview_todo_list.layoutManager = linearLayoutManager
        getTodoListData ()
    }

    override fun onResume() {
        //solves the issue of recycleview not refreshing properly
        Timer("SettingUp", false).schedule(2000) {
            getTodoListData()
        }
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        //load search menuitem
        menuInflater.inflate(R.menu.menu, menu)
        val menuItem = menu!!.findItem(R.id.search)

        if(menuItem!=null){

            val searchView = menuItem.actionView as SearchView

            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    TODO("Not yet implemented")
                    return true
                }
                //perform filtering as text is being keyed into the searchbar
                override fun onQueryTextChange(newText: String?): Boolean {
                    if(newText!!.isNotEmpty()) {
                        displayList.clear()
                        val search = newText.lowercase(Locale.getDefault())
                        actualList.forEach {

                            //enable searching for all three fields, id, task name and completion status
                            if ((it.id.toString() + " " + it.title.toString() + " " + it.completed.toString()).lowercase(Locale.getDefault()).contains(search)) {
                                displayList.add(it)
                            }
                            todoListAdapter.notifyDataSetChanged()
                        }
                    }else{
                        //restore the dataset to its original state when search textbox is empty
                        displayList.clear()
                        displayList.addAll(actualList)
                        todoListAdapter.notifyDataSetChanged()
                    }
                    return true
                }
            })
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    //populates data requested from mock.io
    private fun getTodoListData () {
        // Create Retrofit
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(TodoListApiServiceInterface::class.java)

        //link appropriate ApiService function via the retrofit builder class
        val retrofitData = retrofitBuilder.getData()

        //make http get request
        retrofitData.enqueue(object : Callback<List<TodoListItem>?> {
            override fun onResponse(
                call: Call<List<TodoListItem>?>,
                response: Response<List<TodoListItem>?>
            ) {
                val responseBody = response.body()!!

                //used for implementing filtering. actualList will not be manipulated and will be used as master data
                actualList = ArrayList(responseBody)

                //temp data that will be manipulated to match search criteria
                displayList = ArrayList(responseBody)

                //provide temp data i.e displayList match search criteria.
                todoListAdapter = TodoListAdapter(baseContext, displayList, this@MainActivity, this@MainActivity)
                todoListAdapter.notifyDataSetChanged()

                //plugin the adapter to the view to feed it data that will be displayed
                recyclerview_todo_list.adapter = todoListAdapter
            }

            override fun onFailure(call: Call<List<TodoListItem>?>, t: Throwable) {
                TODO("Not yet implemented")
                Log.d("MainActivity", "onFailure: " + t.message)
            }
        })
    }

    //this is a contract we have to adhere to, this interface is declared in TodoListAdapter
    override fun onItemClick(position: Int) {

        val intent = Intent(this, UpdateActivity::class.java)
        //pass string variables to next activity called
        intent.putExtra("id",todoListAdapter.todoList[position].id)
        intent.putExtra("title", todoListAdapter.todoList[position].title)
        intent.putExtra("completed", todoListAdapter.todoList[position].completed.toString())
        startActivity(intent)
    }

    //this is a contract we have to adhere to, this interface is declared in TodoListAdapter
    override fun onItemLongClick(position: Int) {

        deleteListItem(todoListAdapter.todoList[position].id)
//        displayList.removeAt(position+1)

        //solves the issue of deleted record still remaining in recycle view after long press delete.
        Timer("SettingUp", false).schedule(2000) {
            getTodoListData()
        }
    }

    //method linked to the add new task button
    fun btnAddTask(view: View) {
        val intent = Intent(this, AddNewActivity::class.java)
        startActivity(intent)
    }

    //Deletes an item in the list with given id
    private fun deleteListItem(id: String)
    {
       // Create Retrofit
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(TodoListApiServiceInterface::class.java)

        //calling the appropriate http service method to delete the data item
        val retrofitData = retrofitBuilder.deleteId(id)

        //perform the http request
        retrofitData.enqueue(object: Callback<TodoListItem> {

            override fun onResponse(call: Call<TodoListItem>, response: Response<TodoListItem>) {
                if (response.isSuccessful) {
                    var newlyCreatedDestination = response.body() // Use it or ignore it
                    Toast.makeText(this@MainActivity, "Successfully Deleted.", Toast.LENGTH_SHORT).show()


                } else {
                    Toast.makeText(this@MainActivity, "Failed to delete item. Data might not exist.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TodoListItem>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Failed to delete item", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //used for a hidden refresh button. Keeping it for testing
    fun btnRefresh(view: View) {
        Toast.makeText(this@MainActivity, "Data Refreshed.", Toast.LENGTH_SHORT).show()
        getTodoListData()
    }
}