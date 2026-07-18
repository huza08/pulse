package app.pulse.desktop.ui.components

import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val logFmt = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")

fun log(tag: String, msg: String) {
    println("[${LocalTime.now().format(logFmt)}] [$tag] $msg")
}
