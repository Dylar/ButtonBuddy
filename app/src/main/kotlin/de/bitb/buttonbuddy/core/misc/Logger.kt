package de.bitb.buttonbuddy.core.misc

import android.util.Log
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val PACKAGE_NAME = "de.bitb.buttonbuddy."
const val LOG_BORDER_BOT: String = "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"
const val LOG_BORDER_TOP: String = "---------------------------------------------------------------"

enum class PrintLevel { INFO, WARNING, ERROR, SYSTEM }

@Suppress("unused")
object Logger {
    val loggingActive: Boolean = true

    private var time: Long = 0

    @Suppress("unused")
    fun startTimer() {
        time = System.currentTimeMillis()
    }

    @Suppress("unused")
    fun printTimer(msg: String) {
        val inMillis = (System.currentTimeMillis() - time).toDouble()
        printLog("$msg (TIME: $inMillis)")
    }

    @SuppressWarnings("FunctionCouldBePrivate")
    fun <T : Any> printLog(vararg params: T, level: PrintLevel = PrintLevel.SYSTEM) {
        val log = createLog(params)
        printMessage(
            "\n${LOG_BORDER_TOP}" +
                    "\nTime:${log.timeStamp}" +
                    "\nParams:${log.params}",
            "\nThread:${log.thread}" +
                    "\nStack:${log.stack}" +
                    "\n$LOG_BORDER_BOT",
            level
        )
    }

    fun justPrint(message: String, level: PrintLevel = PrintLevel.SYSTEM) {
        val log = createLog(message)
        val tag = "\n${LOG_BORDER_TOP}" +
                "\nTime:${log.timeStamp}"
        val msg = "\nParams:${log.params}" +
                "\n$LOG_BORDER_BOT"
        printMessage(tag, msg, level)
    }

    private fun printMessage(tag: String, message: String, level: PrintLevel = PrintLevel.SYSTEM) {
        if (loggingActive) {
            when (level) {
                PrintLevel.INFO -> Log.i(tag, message)
                PrintLevel.WARNING -> Log.w(tag, message)
                PrintLevel.ERROR -> Log.e(tag, message)
                PrintLevel.SYSTEM -> println(tag + message)
            }
        }
    }

    private fun <TYPE : Any> createLog(vararg params: TYPE): LogData {
        return Thread.currentThread().let { thread ->
            val xParams = params
                .contentDeepToString()
                .removeArrayBrackets()

            val xThread = thread.name
                .removeArrayBrackets()
                .removePackage()

            val xStack = thread
                .stackTrace
                .filter(appClass())
                .asSequence()
                .drop(3)
                .map { "\n$it".removePackage() }
                .toList()
                .toTypedArray()
                .contentDeepToString()
                .removePackage()
                .removeArrayBrackets()

            LogData(xThread, xParams, xStack)
        }
    }
}

data class LogData(
    val thread: String,
    val params: String,
    val stack: String,
    val timeStamp: String = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
)

private fun appClass(): (StackTraceElement) -> Boolean = { it.className.contains(PACKAGE_NAME) }
private fun String.removePackage(): String = replace(PACKAGE_NAME, "")
private fun String.removeArrayBrackets(): String = replace("[", "").replace("]", "")
