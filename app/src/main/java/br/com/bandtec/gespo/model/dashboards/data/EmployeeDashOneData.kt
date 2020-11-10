package br.com.bandtec.gespo.model.dashboards.data

import com.google.gson.annotations.SerializedName

data class EmployeeDashOneData(
    @SerializedName("Project.name")
    val projectName: String,
    @SerializedName("EmployeeMetrics.totalHoursWork")
    val totalAmountWork: Double
)