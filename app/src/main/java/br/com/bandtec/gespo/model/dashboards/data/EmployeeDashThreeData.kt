package br.com.bandtec.gespo.model.dashboards.data

import com.google.gson.annotations.SerializedName

data class EmployeeDashThreeData(
    @SerializedName("Project.name")
    val projectName: String,
    @SerializedName("Employee.count")
    val employeeCount: Int
)