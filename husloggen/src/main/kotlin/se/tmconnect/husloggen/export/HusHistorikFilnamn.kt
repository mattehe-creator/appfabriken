package se.tmconnect.husloggen.export

import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val filDatum = DateTimeFormatter.ofPattern("yyyy-MM-dd")

fun husHistorikPdfFilnamn(datum: LocalDate = LocalDate.now()): String =
    "husets-historik-${datum.format(filDatum)}.pdf"

fun husHistorikCsvFilnamn(datum: LocalDate = LocalDate.now()): String =
    "husets-historik-${datum.format(filDatum)}.csv"
