package com.example.af7.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.af7.data.model.Report
import com.example.af7.data.model.Todo
import com.example.af7.data.repository.TodoRepositoryImpl
import com.example.af7.utils.BluetoothScanner
import com.example.af7.utils.CustomNotificationManager
import com.example.af7.utils.PreferencesManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: TodoRepositoryImpl,
    val preferencesManager: PreferencesManager,
    private val notificationManager: CustomNotificationManager,
    val bluetoothScanner: BluetoothScanner
) : ViewModel() {

    val todos: StateFlow<List<Todo>> = repository.todos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reports: StateFlow<List<Report>> = repository.reports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorDialogMessage = MutableStateFlow<String?>(null)
    val errorDialogMessage: StateFlow<String?> = _errorDialogMessage.asStateFlow()

    init {
        refreshTodos()
        refreshReports()
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

    fun refreshReports() {
        viewModelScope.launch {
            try {
                repository.refreshReports()
            } catch (e: Exception) {
                _errorDialogMessage.value = "Error al cargar informes: ${e.localizedMessage}"
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
                repository.addTodo(title)
                notificationManager.showSyncNotification("Tarea Creada", "Se añadió: $title")
                refreshTodos()
            } catch (e: Exception) {
                _errorDialogMessage.value = "Error al crear tarea: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun submitErrorReport(description: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.addReport(description)
                notificationManager.showSyncNotification("Reporte", "Enviado con éxito")
                refreshReports()
            } catch (e: Exception) {
                _errorDialogMessage.value = "Error al enviar reporte: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateReportStatus(report: Report, isResolved: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateReportStatus(report.id, isResolved)
            } catch (e: Exception) {
                _errorDialogMessage.value = "No se pudo actualizar el informe"
            }
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