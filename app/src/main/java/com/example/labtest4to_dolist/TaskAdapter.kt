package com.example.labtest4to_dolist


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView


class TaskAdapter(
    private val onItemClick: (Task) -> Unit,
    private val onDeleteClick: (Task) -> Unit,
    private val onUpdateClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var tasks: List<Task> = emptyList()
    private var filteredTasks: List<Task> = emptyList() // Add filteredTasks property

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        // Inflate layout for individual task item
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = filteredTasks[position] // Use filteredTasks instead of tasks
        holder.bind(currentTask,position)
    }

    override fun getItemCount(): Int {
        return filteredTasks.size // Return the size of filteredTasks
    }

    fun submitList(taskList: List<Task>) {
        tasks = taskList
        filterTasks("") // Call filterTasks with empty query to initialize filteredTasks
    }

    fun filterTasks(priority: String) {
        filteredTasks = if (priority.isBlank() || priority == "All") {
            tasks // Return all tasks when priority is blank or "All" is selected
        } else {
            tasks.filter { it.priority.equals(priority, ignoreCase = true) } // Filter tasks by priority
        }
        notifyDataSetChanged() // Notify RecyclerView of data change
    }

    fun searchTasks(query: String) {
        filteredTasks = if (query.isBlank()) {
            tasks // Return all tasks when the query is blank
        } else {
            tasks.filter { it.name.contains(query, ignoreCase = true) } // Filter tasks by name
        }
        notifyDataSetChanged() // Notify RecyclerView of data change
    }




    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val task = filteredTasks[position] // Use filteredTasks instead of tasks
                    onItemClick(task)
                }
            }

            // Set up click listener for delete button
            itemView.findViewById<Button>(R.id.btnDelete).setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val task = filteredTasks[position] // Use filteredTasks instead of tasks
                    onDeleteClick(task)
                }
            }

            // Set up click listener for update button
            itemView.findViewById<Button>(R.id.btnUpdate).setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val task = filteredTasks[position] // Use filteredTasks instead of tasks
                    onUpdateClick(task)
                }
            }
        }

        fun bind(task: Task, position: Int) {
            val taskNameTextView: TextView = itemView.findViewById(R.id.taskNameTextView)
            taskNameTextView.text = task.name

            val priority = task.priority // Get the priority of the current task
            val color = getTaskColor(task.priority) // Get the color based on the priority
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, color))
        }

        fun getTaskColor(priority: String): Int {
            return when (priority.lowercase()) {
                "high" -> R.color.red // Define 'red' color resource in colors.xml
                "normal" -> R.color.green // Define 'green' color resource in colors.xml
                "low" -> R.color.blue // Define 'blue' color resource in colors.xml
                else -> R.color.green // Default color or handle unknown priorities
            }
        }


    }
}