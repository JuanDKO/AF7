package com.example.af7.data.repository

import com.example.af7.data.model.Report
import com.example.af7.data.model.Todo
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    val todos: Flow<List<Todo>>
    val reports: Flow<List<Report>>
    suspend fun refreshTodos()
    suspend fun updateTodoStatus(todoId: Int, isCompleted: Boolean)
    suspend fun addTodo(title: String)
    suspend fun refreshReports()
    suspend fun addReport(description: String)
    suspend fun updateReportStatus(reportId: Int, isResolved: Boolean)
}