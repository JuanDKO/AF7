package com.example.af7.data.repository

import com.example.af7.data.model.Todo
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    val todos: Flow<List<Todo>>
    suspend fun refreshTodos()
    suspend fun updateTodoStatus(todoId: Int, isCompleted: Boolean)
    suspend fun addTodo(title: String) // <--- Asegúrate de que esta línea esté aquí
}