package com.example.af7.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.af7.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val todos by viewModel.todos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showReportDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                SmallFloatingActionButton(
                    onClick = { showReportDialog = true },
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(Icons.Default.Warning, contentDescription = "Reportar Error")
                }
                FloatingActionButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Tarea")
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (todos.isEmpty()) {
                Text("No hay tareas.", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(todos) { todo ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = todo.completed,
                                    onCheckedChange = { isChecked ->
                                        viewModel.updateTodoStatus(todo, isChecked)
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = todo.title,
                                    color = if (todo.completed) Color.Gray else Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogos de Reporte y Creación
    if (showReportDialog) {
        var reportText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Reportar Error") },
            text = {
                OutlinedTextField(value = reportText, onValueChange = { reportText = it }, label = { Text("Describe el problema") })
            },
            confirmButton = {
                TextButton(onClick = { viewModel.submitErrorReport(reportText); showReportDialog = false }) { Text("Enviar") }
            }
        )
    }

    if (showAddDialog) {
        var newTaskTitle by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Nueva Tarea") },
            text = {
                OutlinedTextField(value = newTaskTitle, onValueChange = { newTaskTitle = it }, label = { Text("Título") })
            },
            confirmButton = {
                Button(onClick = {
                    if (newTaskTitle.isNotBlank()) {
                        viewModel.addTodo(newTaskTitle)
                        showAddDialog = false
                    }
                }) { Text("Guardar") }
            }
        )
    }
}