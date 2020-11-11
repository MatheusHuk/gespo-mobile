package br.com.bandtec.gespo.model

data class Project(
    val id: Int,
    val name:String,
    val costCenter:CostCenter,
    val manager:Employee
)