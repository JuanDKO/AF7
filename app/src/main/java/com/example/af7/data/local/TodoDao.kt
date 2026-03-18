package com.example.af7.data.local

import androidx.room.*
import com.example.af7.data.model.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos ORDER BY id DESC")
    fun getAllTodos(): Flow<List<Todo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: Todo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(todos: List<Todo>)

    @Query("UPDATE todos SET completed = :isCompleted WHERE id = :todoId")
    suspend fun updateTodoStatus(todoId: Int, isCompleted: Boolean)

    @Query("DELETE FROM todos")
    suspend fun clearTodos()
}
