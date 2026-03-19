package com.example.af7.data.repository

import com.example.af7.data.local.ReportDao
import com.example.af7.data.local.TodoDao
import com.example.af7.data.model.Report
import com.example.af7.data.model.Todo
import com.example.af7.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TodoRepositoryImpl(
    private val todoDao: TodoDao,
    private val reportDao: ReportDao,
    private val apiService: ApiService
) : TodoRepository {

    override val todos: Flow<List<Todo>> = todoDao.getAllTodos()
    override val reports: Flow<List<Report>> = reportDao.getAllReports()

    override suspend fun refreshTodos() {
        withContext(Dispatchers.IO) {
            val remoteTodos = apiService.getTodos()
            todoDao.clearTodos()
            todoDao.insertAll(remoteTodos)
        }
    }

    override suspend fun updateTodoStatus(todoId: Int, isCompleted: Boolean) {
        withContext(Dispatchers.IO) {
            todoDao.updateTodoStatus(todoId, isCompleted)
            apiService.updateTodoStatus(todoId, mapOf("completed" to isCompleted))
        }
    }

    override suspend fun addTodo(title: String) {
        withContext(Dispatchers.IO) {
            try {
                val newTodo = Todo(title = title)
                val savedTodo = apiService.createTodo(newTodo)
                todoDao.insertTodo(savedTodo)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun refreshReports() {
        withContext(Dispatchers.IO) {
            val remoteReports = apiService.getReports()
            reportDao.clearReports()
            reportDao.insertAll(remoteReports)
        }
    }

    override suspend fun addReport(description: String) {
        withContext(Dispatchers.IO) {
            try {
                val newReport = Report(description = description)
                val savedReport = apiService.createReport(newReport)
                reportDao.insertReport(savedReport)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun updateReportStatus(reportId: Int, isResolved: Boolean) {
        withContext(Dispatchers.IO) {
            reportDao.updateReportStatus(reportId, isResolved)
            apiService.updateReportStatus(reportId, mapOf("resolved" to isResolved))
        }
    }
}