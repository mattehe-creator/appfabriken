package se.tmconnect.styrelserummet.underlag.exempel

import java.nio.file.Files
import java.nio.file.Path
import kotlin.system.exitProcess

fun skrivExempelHandingar(malKatalog: Path) {
    Files.createDirectories(malKatalog)
    Files.write(malKatalog.resolve("underhallsplan-exempel.pdf"), exempelUnderhallsplanPdf())
    Files.write(malKatalog.resolve("arsredovisning-exempel.pdf"), exempelArsredovisningPdf())
}

fun main(args: Array<String>) {
    if (args.isEmpty() || args[0].isBlank()) {
        System.err.println("Ange målkatalog som första argument.")
        exitProcess(1)
    }
    skrivExempelHandingar(Path.of(args[0]))
}
