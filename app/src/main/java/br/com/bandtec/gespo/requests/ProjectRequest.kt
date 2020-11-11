package br.com.bandtec.gespo.requests

import br.com.bandtec.gespo.model.Project
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ProjectRequest {
    @GET("/projects/employee?")
    fun getProjectsByEmployee(
        @Header("Cookie") cookie:String,
        @Query("id") id:Int
    ): Call<List<Project>>

}