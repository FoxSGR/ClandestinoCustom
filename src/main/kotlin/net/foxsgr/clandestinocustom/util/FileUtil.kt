package net.foxsgr.clandestinocustom.util

import java.io.File

fun withoutExtension(fileName: String): String {
    return File(fileName).nameWithoutExtension
}

fun contentFromFile(file: File): String {
    return file.readText()
}
