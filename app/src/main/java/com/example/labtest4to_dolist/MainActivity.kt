package com.example.labtest4to_dolist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        adapter = TaskAdapter(
            onItemClick = { clickedTask ->
                Toast.makeText(
                    this@MainActivity,
                    "Clicked task: ${clickedTask.name}",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onDeleteClick = { deletedTask ->
                val alertDialogBuilder = AlertDialog.Builder(this@MainActivity)
                alertDialogBuilder.setTitle("Delete Task")
                alertDialogBuilder.setMessage("Are you sure you want to delete this task?")
                alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                    taskViewModel.delete(deletedTask)
                    Toast.makeText(
                        this@MainActivity,
                        "Task deleted successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                alertDialogBuilder.setNegativeButton("No") { _, _ -> }
                alertDialogBuilder.show()
            },
            onUpdateClick = { updatedTask ->
                val updateDialogBuilder = AlertDialog.Builder(this@MainActivity)
                updateDialogBuilder.setTitle("Update Task")
                val input = EditText(this@MainActivity)
                input.setText(updatedTask.name)
                updateDialogBuilder.setView(input)
                updateDialogBuilder.setPositiveButton("Update") { _, _ ->
                    val updatedTaskName = input.text.toString().trim()
                    if (updatedTaskName.isNotEmpty()) {
                        updatedTask.name = updatedTaskName
                        taskViewModel.update(updatedTask)
                        Toast.makeText(
                            this@MainActivity,
                            "Task updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Task name cannot be empty",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                updateDialogBuilder.setNegativeButton("Cancel") { _, _ -> }
                updateDialogBuilder.show()
            }
        )

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewTasks)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        taskViewModel.getAllTasks().observe(this) { tasks ->
            adapter.submitList(tasks)
        }

        val btnAddTask = findViewById<Button>(R.id.btnAddTask)
        btnAddTask.setOnClickListener {
            showPriorityDialog()
        }

        val btnFilter = findViewById<Button>(R.id.btnFilter)
        btnFilter.setOnClickListener {
            showFilterDialog()
        }


        val etSearch = findViewById<EditText>(R.id.etSearch)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.searchTasks(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                // Not used
            }
        })


    }

    private fun showPriorityDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_priority, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Select Priority")
            .setPositiveButton("Add") { dialog, _ ->
                val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroupPriority)
                val checkedRadioButtonId = radioGroup.checkedRadioButtonId
                val selectedRadioButton = dialogView.findViewById<RadioButton>(checkedRadioButtonId)

                val priority = when (checkedRadioButtonId) {
                    R.id.radio_high -> "High"
                    R.id.radio_normal -> "Normal"
                    R.id.radio_low -> "Low"
                    else -> "Normal" // Default to normal priority if nothing selected
                }
                addTask(priority)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        dialogBuilder.show()
    }

    private fun showFilterDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Filter Tasks")
        val filterOptions = arrayOf("All", "High Priority", "Normal Priority", "Low Priority")

        // Map dialog options to database values
        val priorityMap = mapOf(
            "All" to "", // Empty string for no filter
            "High Priority" to "High",
            "Normal Priority" to "Normal",
            "Low Priority" to "Low"
        )

        dialogBuilder.setSingleChoiceItems(filterOptions, -1) { dialog, which ->
            val selectedFilter = filterOptions[which]
            val priority = priorityMap[selectedFilter] ?: ""
            filterTasks(priority)
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        dialogBuilder.show()
    }





    private fun filterTasks(priority: String) {
        taskViewModel.getTasksByPriority(priority).observe(this) { tasks ->
            adapter.submitList(tasks)
        }
    }


    private fun addTask(priority: String) {
        val tskN = findViewById<EditText>(R.id.etTaskName)
        val taskName = tskN.text.toString().trim()
        if (taskName.isNotEmpty()) {
            val task = Task(name = taskName, priority = priority)
            taskViewModel.insert(task)
            tskN.text.clear()
        } else {
            Toast.makeText(this@MainActivity, "Please enter a task name", Toast.LENGTH_SHORT).show()
        }
    }
}