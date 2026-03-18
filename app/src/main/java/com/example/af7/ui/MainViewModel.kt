package com.example.af7.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.af7.data.model.Todo
import com.example.af7.data.repository.TodoRepositoryImpl
import com.example.af7.utils.BluetoothScanner
import com.example.af7.utils.CustomNotificationManager
import com.example.af7.utils.PreferencesManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: TodoRepositoryImpl,
    val preferencesManager: PreferencesManager,
    private val notificationManager: CustomNotificationManager,
    val bluetoothScanner: BluetoothScanner
) : ViewModel() {

    // Observamos las tareas directamente del repositorio
    val todos: StateFlow<List<Todo>> = repository.todos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorDialogMessage = MutableStateFlow<String?>(null)
    val errorDialogMessage: StateFlow<String?> = _errorDialogMessage.asStateFlow()

    init {
        // Carga inicial
        refreshTodos()
    }

    fun refreshTodos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.refreshTodos()
            } catch (e: Exception) {
                _errorDialogMessage.value = "Error de red: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateTodoStatus(todo: Todo, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateTodoStatus(todo.id, isCompleted)
            } catch (e: Exception) {
                _errorDialogMessage.value = "No se pudo actualizar en PostgreSQL"
            }
        }
    }

    fun addTodo(title: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Enviamos la orden de crear la tarea
                repository.addTodo(title)
                notificationManager.showSyncNotification("Tarea Creada", "Se añadió: $title")

                // 2. TRUCO: Forzamos la descarga de PostgreSQL para que la UI se actualice con los datos reales
                refreshTodos()

            } catch (e: Exception) {
                _errorDialogMessage.value = "Error al crear tarea: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun submitErrorReport(report: String) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000)
            _isLoading.value = false
            notificationManager.showSyncNotification("Reporte", "Enviado con éxito")
        }
    }

    fun dismissErrorDialog() { _errorDialogMessage.value = null }
}

// LA FACTORY: Muy importante que tenga los 4 parámetros
class MainViewModelFactory(
    private val repository: TodoRepositoryImpl,
    private val preferencesManager: PreferencesManager,
    private val notificationManager: CustomNotificationManager,
    private val bluetoothScanner: BluetoothScanner
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository, preferencesManager, notificationManager, bluetoothScanner) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}