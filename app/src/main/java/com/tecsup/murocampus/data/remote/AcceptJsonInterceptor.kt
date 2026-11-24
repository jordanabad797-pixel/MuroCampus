package com.tecsup.murocampus.data.remote

import okhttp3.Interceptor
import okhttp3.Response

class AcceptJsonInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val peticion = chain.request().newBuilder()
            .header("Accept", "application/json")
            .build()
        return chain.proceed(peticion)
    }
}
