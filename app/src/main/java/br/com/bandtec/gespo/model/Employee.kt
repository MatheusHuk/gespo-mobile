package br.com.bandtec.gespo.model

data class Employee(
    val id:Int,
    val cpf:String,
    val name:String,
    val password:String,
    val email:String,
    val hourValue:Double,
    val office:Office
)