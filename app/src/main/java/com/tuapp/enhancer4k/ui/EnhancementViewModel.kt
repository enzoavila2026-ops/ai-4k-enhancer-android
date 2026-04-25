package com.tuapp.enhancer4k.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tuapp.enhancer4k.model.SuperResolutionInterpreter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EnhancementUiState(
    val originalBitmap: Bitmap? = null,
    val enhancedBitmap: Bitmap? = null,
    val isProcessing: Boolean = false,
    val progress: Float = 0f,
    val error: String? = null
)

class EnhancementViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(EnhancementUiState())
    val uiState: StateFlow<EnhancementUiState> = _uiState.asStateFlow()

    private var interpreter: SuperResolutionInterpreter? = null

    init {
        // Cargar el modelo en segundo plano para no bloquear la interfaz
        viewModelScope.launch(Dispatchers.IO) {
            try {
                interpreter = SuperResolutionInterpreter(application)
                interpreter?.initialize() // carga desde assets (operación de I/O)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al cargar modelo: ${e.message}") }
            }
        }
    }

    fun selectImage(bitmap: Bitmap) {
        _uiState.update {
            it.copy(
                originalBitmap = bitmap,
                enhancedBitmap = null,
                error = null
            )
        }
    }

    fun enhanceImage() {
        val bitmap = _uiState.value.originalBitmap ?: return

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isProcessing = true, progress = 0f, error = null) }
            try {
                val enhanced = interpreter?.upscale(bitmap)
                    ?: throw IllegalStateException("Intérprete no disponible")
                _uiState.update {
                    it.copy(
                        enhancedBitmap = enhanced,
                        isProcessing = false,
                        progress = 1f
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Error desconocido",
                        isProcessing = false
                    )
                }
            }
        }
    }

    override fun onCleared() {
        interpreter?.close()
        super.onCleared()
    }
}
