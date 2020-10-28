package br.com.bandtec.gespo.requests

import br.com.bandtec.gespo.model.dashboards.ManagerDashOne
import br.com.bandtec.gespo.model.dashboards.ManagerDashThree
import br.com.bandtec.gespo.model.dashboards.ManagerDashTwo
import retrofit2.Call
import retrofit2.http.GET

interface DashRequest {
    @GET("/cubejs-api/v1/load?query={\"measures\":[\"EmployeeMetrics.totalAmountWork\",\"EmployeeMetrics.totalAmountProvisioning\"],\"timeDimensions\":[{\"dimension\":\"EmployeeMetrics.actionDate\"}],\"dimensions\":[\"Project.name\"]}")
    fun getManagerDashOne(): Call<ManagerDashOne>

    @GET("/cubejs-api/v1/load?query={\"measures\":[\"EmployeeMetrics.totalHoursWork\",\"EmployeeMetrics.totalHoursProvisioning\"],\"timeDimensions\":[{\"dimension\":\"EmployeeMetrics.actionDate\"}],\"dimensions\":[\"Project.name\"]}")
    fun getManagerDashTwo(): Call<ManagerDashTwo>

    @GET("/cubejs-api/v1/load?query={\"measures\":[\"Employee.count\"],\"timeDimensions\":[],\"dimensions\":[\"Project.name\"]}")
    fun getManagerDashThree(): Call<ManagerDashThree>
}