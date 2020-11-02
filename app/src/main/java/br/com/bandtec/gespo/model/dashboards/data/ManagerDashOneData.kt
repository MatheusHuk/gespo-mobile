package br.com.bandtec.gespo.model.dashboards.data

import com.google.gson.annotations.SerializedName

data class ManagerDashOneData(
    @SerializedName("Project.name")
    val projectName: String,
    @SerializedName("EmployeeMetrics.totalAmountWork")
    val totalAmountWork: Double,
    @SerializedName("EmployeeMetrics.totalAmountProvisioning")
    val totalAmountProvisioning: Double
)