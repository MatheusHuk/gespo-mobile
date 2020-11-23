package br.com.bandtec.gespo.requests

import br.com.bandtec.gespo.model.TimeEntry
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface TimeEntryRequest {
    @GET("/work-schedules/employee?")
    fun getTimeEntriesByEmployee(
        @Header("Cookie") cookie:String,
        @Query("employeeId") id:Int
    ):Call<List<TimeEntry>>
}