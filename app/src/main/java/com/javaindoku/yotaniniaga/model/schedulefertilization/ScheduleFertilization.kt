package com.javaindoku.yotaniniaga.model.schedulefertilization

import org.threeten.bp.LocalDate

data class ScheduleFertilization (
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate  = LocalDate.now(),
    val title: String = ""
) {
}