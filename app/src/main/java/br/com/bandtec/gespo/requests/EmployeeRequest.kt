package br.com.bandtec.gespo.requests

import br.com.bandtec.gespo.model.Employee
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface EmployeeRequest {
    @GET("/user?")
    fun getEmployee(
        @Header("cookie") cookie: String,
        @Query("id") id:Int

    ) : Call<Employee>
}