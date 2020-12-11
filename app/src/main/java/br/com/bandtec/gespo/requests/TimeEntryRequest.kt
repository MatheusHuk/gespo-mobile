package br.com.bandtec.gespo.requests

import br.com.bandtec.gespo.model.TimeEntry
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import br.com.bandtec.gespo.model.TimeEntryDTO
import retrofit2.Callback
import retrofit2.http.*

interface TimeEntryRequest {
    @GET("/work-schedules/employee?")
    fun getTimeEntriesByEmployee(
        @Header("Cookie") cookie:String,
        @Query("employeeId") id:Int
    ):Call<List<TimeEntry>>

    @DELETE("/work-schedules?")
    fun deleteTimeEntryById(
        @Header("Cookie") cookie:String,
        @Query("id") id:Int
    ):Call<ResponseBody>

    @GET("/work-schedules/filter?")
    fun getTimeEntriesByFilters(
        @Header("Cookie") cookie: String,
        @Query("projectId") projectId:Int,
        @Query("employeeId") id:Int,
        @Query("date") date: String?
    ):Call<List<TimeEntry>>

    @POST("/work-schedules")
    fun createTimeEntry(
        @Header("Cookie") cookie: String,
        @Body timeEntryDTO: List<TimeEntryDTO>
    ):Call<ResponseBody>


}