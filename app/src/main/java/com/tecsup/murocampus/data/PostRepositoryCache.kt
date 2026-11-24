package com.tecsup.murocampus.data

import android.util.Log
import com.tecsup.murocampus.data.local.PostDao
import com.tecsup.murocampus.data.local.PostEntity
import com.tecsup.murocampus.data.local.aEntidad
import com.tecsup.murocampus.data.local.aPost
import com.tecsup.murocampus.model.Comentario
import com.tecsup.murocampus.model.NuevoPost
import com.tecsup.murocampus.model.Post
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class PostRepositoryCache(
    private val remoto: PostRepository,
    private val dao: PostDao
) : PostRepository {

    override suspend fun listar(): Result<List<Post>> =
        conCache(remoto.listar()) { dao.todos() }

    override suspend fun pagina(inicio: Int, limite: Int): Result<List<Post>> =
        conCache(remoto.pagina(inicio, limite)) { dao.pagina(inicio, limite) }

    override suspend fun porUsuario(userId: Int): Result<List<Post>> =
        conCache(remoto.porUsuario(userId)) { dao.porUsuario(userId) }

    override suspend fun detalle(id: Int): Result<Post> = remoto.detalle(id)

    override suspend fun comentarios(id: Int): Result<List<Comentario>> = remoto.comentarios(id)

    override suspend fun crear(nuevo: NuevoPost): Result<Post> = remoto.crear(nuevo)

    override suspend fun editarTitulo(id: Int, titulo: String): Result<Post> =
        remoto.editarTitulo(id, titulo).onSuccess { post ->
            enBaseDeDatos("actualizar") { dao.guardar(listOf(post.aEntidad())) }
        }

    override suspend fun borrar(id: Int): Result<Unit> =
        remoto.borrar(id).onSuccess {
            enBaseDeDatos("borrar") { dao.borrar(id) }
        }

    private suspend fun conCache(
        resultado: Result<List<Post>>,
        leerLocal: suspend () -> List<PostEntity>
    ): Result<List<Post>> {
        resultado.onSuccess { posts ->
            enBaseDeDatos("guardar") { dao.guardar(posts.map { it.aEntidad() }) }
            return resultado
        }
        val error = resultado.exceptionOrNull()
        if (error !is IOException) return resultado
        val guardados = enBaseDeDatos("leer") { leerLocal() }.orEmpty()
        return if (guardados.isEmpty()) {
            resultado
        } else {
            Result.success(guardados.map { it.aPost() })
        }
    }

    private suspend fun <T> enBaseDeDatos(accion: String, bloque: suspend () -> T): T? = try {
        bloque()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Log.w("MuroCampus", "Room no pudo $accion la caché", e)
        null
    }
}
