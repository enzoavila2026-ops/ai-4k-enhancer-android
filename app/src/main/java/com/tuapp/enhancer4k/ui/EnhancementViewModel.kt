    init {
        try {
            interpreter = SuperResolutionInterpreter(application)
            interpreter?.initialize()
        } catch (e: Exception) {
            _uiState.update { it.copy(error = "Error al cargar modelo: ${e.message}") }
        }
    }
