package com.tecsup.murocampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.murocampus.data.PostRepository
import com.tecsup.murocampus.model.Comentario
import com.tecsup.murocampus.model.NuevoPost
import com.tecsup.murocampus.model.Post
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DetalleUi(val post: Post, val comentarios: List<Comentario>)

private const val TAMANO_PAGINA = 20

class PostsViewModel(private val repo: PostRepository) : ViewModel() {

    private val _lista = MutableStateFlow<UiState<List<Post>>>(UiState.Cargando)
    val lista: StateFlow<UiState<List<Post>>> = _lista.asStateFlow()

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    val visibles: StateFlow<UiState<List<Post>>> = combine(_lista, _busqueda) { estado, texto ->
        if (estado is UiState.Exito && texto.isNotBlank()) {
            UiState.Exito(estado.datos.filter { it.title.contains(texto.trim(), ignoreCase = true) })
        } else {
            estado
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState.Cargando)

    private val _usuarioFiltro = MutableStateFlow<Int?>(null)
    val usuarioFiltro: StateFlow<Int?> = _usuarioFiltro.asStateFlow()

    private val _hayMas = MutableStateFlow(false)
    val hayMas: StateFlow<Boolean> = _hayMas.asStateFlow()

    private val _cargandoMas = MutableStateFlow(false)
    val cargandoMas: StateFlow<Boolean> = _cargandoMas.asStateFlow()

    private val _errorMas = MutableStateFlow<String?>(null)
    val errorMas: StateFlow<String?> = _errorMas.asStateFlow()

    private var siguienteInicio = 0

    private val _detalle = MutableStateFlow<UiState<DetalleUi>>(UiState.Cargando)
    val detalle: StateFlow<UiState<DetalleUi>> = _detalle.asStateFlow()

    private val _creado = MutableStateFlow<UiState<Post>?>(null)
    val creado: StateFlow<UiState<Post>?> = _creado.asStateFlow()

    private val _borrado = MutableStateFlow<UiState<Int>?>(null)
    val borrado: StateFlow<UiState<Int>?> = _borrado.asStateFlow()

    private val _editado = MutableStateFlow<UiState<Post>?>(null)
    val editado: StateFlow<UiState<Post>?> = _editado.asStateFlow()

    private var trabajoLista: Job? = null
    private var trabajoDetalle: Job? = null

    init { cargar() }

    fun cargar() {
        trabajoLista?.cancel()
        trabajoLista = viewModelScope.launch {
            _lista.value = UiState.Cargando
            _hayMas.value = false
            _cargandoMas.value = false
            _errorMas.value = null
            val userId = _usuarioFiltro.value
            if (userId == null) {
                repo.pagina(0, TAMANO_PAGINA)
                    .onSuccess {
                        _lista.value = UiState.Exito(it)
                        siguienteInicio = it.size
                        _hayMas.value = it.size == TAMANO_PAGINA
                    }
                    .onFailure { _lista.value = UiState.Error(mensajeDe(it)) }
            } else {
                repo.porUsuario(userId)
                    .onSuccess { _lista.value = UiState.Exito(it) }
                    .onFailure { _lista.value = UiState.Error(mensajeDe(it)) }
            }
        }
    }

    fun cargarMas() {
        if (_lista.value !is UiState.Exito || !_hayMas.value || _cargandoMas.value) return
        trabajoLista = viewModelScope.launch {
            _cargandoMas.value = true
            _errorMas.value = null
            repo.pagina(siguienteInicio, TAMANO_PAGINA)
                .onSuccess { nuevos ->
                    val actual = _lista.value
                    if (actual is UiState.Exito) {
                        _lista.value = UiState.Exito((actual.datos + nuevos).distinctBy { it.id })
                    }
                    siguienteInicio += nuevos.size
                    _hayMas.value = nuevos.size == TAMANO_PAGINA
                }
                .onFailure { _errorMas.value = mensajeDe(it) }
            _cargandoMas.value = false
        }
    }

    fun buscar(texto: String) {
        _busqueda.value = texto
    }

    fun filtrar(userId: Int?) {
        _usuarioFiltro.value = userId
        cargar()
    }

    fun abrir(id: Int) {
        trabajoDetalle?.cancel()
        _borrado.value = null
        trabajoDetalle = viewModelScope.launch {
            _detalle.value = UiState.Cargando
            val (resultadoPost, resultadoComentarios) = coroutineScope {
                val post = async { repo.detalle(id) }
                val comentarios = async { repo.comentarios(id) }
                post.await() to comentarios.await()
            }
            val post = resultadoPost.getOrElse {
                _detalle.value = UiState.Error(mensajeDe(it))
                return@launch
            }
            val comentarios = resultadoComentarios.getOrElse {
                _detalle.value = UiState.Error(mensajeDe(it))
                return@launch
            }
            _detalle.value = UiState.Exito(DetalleUi(post, comentarios))
        }
    }

    fun publicar(titulo: String, texto: String) {
        viewModelScope.launch {
            _creado.value = UiState.Cargando
            repo.crear(NuevoPost(userId = 1, title = titulo.trim(), body = texto.trim()))
                .onSuccess { _creado.value = UiState.Exito(it) }
                .onFailure { _creado.value = UiState.Error(mensajeDe(it)) }
        }
    }

    fun reiniciarCreacion() {
        _creado.value = null
    }

    fun editarTitulo(id: Int, titulo: String) {
        viewModelScope.launch {
            _editado.value = UiState.Cargando
            repo.editarTitulo(id, titulo.trim())
                .onSuccess { actualizado ->
                    val lista = _lista.value
                    if (lista is UiState.Exito) {
                        _lista.value = UiState.Exito(lista.datos.map { if (it.id == id) actualizado else it })
                    }
                    val detalle = _detalle.value
                    if (detalle is UiState.Exito && detalle.datos.post.id == id) {
                        _detalle.value = UiState.Exito(detalle.datos.copy(post = actualizado))
                    }
                    _editado.value = UiState.Exito(actualizado)
                }
                .onFailure { _editado.value = UiState.Error(mensajeDe(it)) }
        }
    }

    fun reiniciarEdicion() {
        _editado.value = null
    }

    fun borrar(id: Int) {
        viewModelScope.launch {
            _borrado.value = UiState.Cargando
            repo.borrar(id)
                .onSuccess {
                    val actual = _lista.value
                    if (actual is UiState.Exito) {
                        _lista.value = UiState.Exito(actual.datos.filterNot { it.id == id })
                    }
                    _borrado.value = UiState.Exito(id)
                }
                .onFailure { _borrado.value = UiState.Error(mensajeDe(it)) }
        }
    }

    fun reiniciarBorrado() {
        _borrado.value = null
    }
}
