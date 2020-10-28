package br.com.bandtec.gespo.model.dashboards.data

import com.google.gson.annotations.SerializedName

data class ManagerDashTwoData(
    @SerializedName("Project.name")
    val projectName: String,
    @SerializedName("EmployeeMetrics.totalHoursWork")
    val totalHoursWork: Double,
    @SerializedName("EmployeeMetrics.totalHoursProvisioning")
    val totalHoursProvisioning: Double
)