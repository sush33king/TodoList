package com.example.todolist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.todo_item.view.*

class TodoListAdapter(val context: Context,
                      val todoList: List<TodoListItem>,
                      private val listener: OnItemClickListener,
                      private val listenerPressed: OnItemLongClickListener
): RecyclerView.Adapter<TodoListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView),
    View.OnClickListener, View.OnLongClickListener{
        lateinit var id: TextView
        lateinit var title: TextView
        lateinit var completed: TextView
        lateinit var img: ImageView

        init{

            //link the views to the reponsible handlers in the adapters
            id = itemView.text_view_1
            title = itemView.text_view_2
            completed = itemView.text_view_3
            img = itemView.image_view

            itemView.setOnClickListener(this)
            itemView.image_delete.setOnLongClickListener(this)
        }
        //implement the abstract method
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
        //implement the abstract method
        override fun onLongClick(v: View?): Boolean {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listenerPressed.onItemLongClick(position)
            }
            return true
        }
    }

    //created and interface so that the activity that is using/instantiating this adapter will be responsible to implement the itemClickListener.
    //This way, the adapter manages to decouple the clickListener from itself making it more reusable.
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    //similar concept to OnItemClickListener
    interface OnItemLongClickListener {
        fun onItemLongClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var itemView = LayoutInflater.from(context).inflate(R.layout.todo_item, parent,false)
        return ViewHolder(itemView)
    }

    //assigning the values to the view handlers, who will pass them on the the actual views
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.id.text = todoList[position].id.toString()
        holder.title.text = todoList[position].title.toString()
        holder.completed.text = todoList[position].completed.toString()
        //set image indicating completion status
        if(todoList[position].completed)
            holder.img.setImageResource(R.drawable.ic_baseline_check_circle_24)
        else
            holder.img.setImageResource(R.drawable.ic_baseline_pending_actions_24)

    }

    override fun getItemCount(): Int {
        return todoList.size
    }

}