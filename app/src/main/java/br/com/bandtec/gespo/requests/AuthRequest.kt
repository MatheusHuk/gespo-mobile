package br.com.bandtec.gespo.requests

import br.com.bandtec.gespo.model.Employee
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AuthRequest {
    @GET("/user/login?")
    fun login(
        @Query("cpf") cpf:String,
        @Query("password") pass:String
    ):Call<Employee>
}