package com.example.af7.data.remote

import com.example.af7.data.model.Report
import com.example.af7.data.model.Todo
import retrofit2.http.*

interface ApiService {
    @GET("todos")
    suspend fun getTodos(): List<Todo>

    @POST("todos")
    suspend fun createTodo(@Body todo: Todo): Todo

    @PATCH("todos/{id}")
    suspend fun updateTodoStatus(
        @Path("id") id: Int,
        @Body updates: Map<String, Boolean>
    )

    @GET("reports")
    suspend fun getReports(): List<Report>

    @POST("reports")
    suspend fun createReport(@Body report: Report): Report

    @PATCH("reports/{id}")
    suspend fun updateReportStatus(
        @Path("id") id: Int,
        @Body updates: Map<String, Boolean>
    )
}