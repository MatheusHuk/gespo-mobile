package br.com.bandtec.gespo.model.dashboards.data

import com.google.gson.annotations.SerializedName

data class EmployeeDashTwoData(
    @SerializedName("Project.name")
    val projectName: String,
    @SerializedName("EmployeeMetrics.totalHoursWork")
    val totalHoursWork: Double,
    @SerializedName("EmployeeMetrics.totalHoursProvisioning")
    val totalHoursProvisioning: Double
)