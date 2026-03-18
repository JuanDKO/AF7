package com.example.af7.data.repository

import com.example.af7.data.local.TodoDao
import com.example.af7.data.model.Todo
import com.example.af7.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TodoRepositoryImpl(
    private val todoDao: TodoDao,
    private val apiService: ApiService
) : TodoRepository {

    override val todos: Flow<List<Todo>> = todoDao.getAllTodos()

    override suspend fun refreshTodos() {
        withContext(Dispatchers.IO) {
            val remoteTodos = apiService.getTodos()
            todoDao.clearTodos()
            todoDao.insertAll(remoteTodos)
        }
    }

    override suspend fun updateTodoStatus(todoId: Int, isCompleted: Boolean) {
        withContext(Dispatchers.IO) {
            // Actualizamos local primero (UI fluida)
            todoDao.updateTodoStatus(todoId, isCompleted)
            // Sincronizamos con PostgreSQL
            apiService.updateTodoStatus(todoId, mapOf("completed" to isCompleted))
        }
    }

    override suspend fun addTodo(title: String) {
        withContext(Dispatchers.IO) {
            try {
                val newTodo = Todo(title = title)
                // Enviamos al servidor
                val savedTodo = apiService.createTodo(newTodo)
                // Lo guardamos en local si el servidor nos devuelve un ID válido
                todoDao.insertTodo(savedTodo)
            } catch (e: Exception) {
                e.printStackTrace()
                // Si el servidor no devuelve la tarea bien formateada, no pasa nada,
                // porque el ViewModel llamará a refreshTodos() justo después y se descargará.
            }
        }
    }
}