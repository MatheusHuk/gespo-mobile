package br.com.bandtec.gespo.model

import java.time.LocalDate

data class TimeEntry(
    val id:Int,
    val creationDate:LocalDate,
    val amountHours:Double,
    val project:Project
)