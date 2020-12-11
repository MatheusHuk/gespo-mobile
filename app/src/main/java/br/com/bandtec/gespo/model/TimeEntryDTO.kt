package br.com.bandtec.gespo.model

data class TimeEntryDTO(
    val employee: Employee,
    val creationDate: IntArray,
    val amountHours: Double,
    val project:Project,
    val dsWork: String
)