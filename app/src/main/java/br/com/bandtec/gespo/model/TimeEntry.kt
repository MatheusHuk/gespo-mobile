package br.com.bandtec.gespo.model

data class TimeEntry(
    val id:Int,
    val creationDate:IntArray,
    val amountHours:Double,
    val project:Project
)