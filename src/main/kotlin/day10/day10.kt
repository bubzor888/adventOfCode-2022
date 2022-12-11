package day10

import java.io.File
import java.nio.file.Paths
import java.util.*
import kotlin.test.assertEquals

fun main() {
    val path = Paths.get("").toAbsolutePath().toString()

//    val test1 = listOf("")
    val test1 = File("$path\\src\\main\\kotlin\\day10\\testInput.txt").readLines()

    assertEquals(13140, execute(test1.toMutableList()))
    execute2(test1.toMutableList())

    println("Tests passed, attempting input")

    val input = File("$path\\src\\main\\kotlin\\day10\\input.txt").readLines()
    //Alternative to read whole file, use .readText()

    println("Final Result 1: ${execute(input.toMutableList())}")
    println("Final Result 2: ${execute2(input.toMutableList())}")
}

private fun execute(input: MutableList<String>): Int {
    var cycle = 1
    var register = 1
    var signalSum = 0
    var processingValue = Optional.empty<Int>()
    while (input.isNotEmpty()) {
        if ((cycle - 20) % 40 == 0) {
            signalSum += cycle * register
        }

        if (processingValue.isPresent) {
            register += processingValue.get()
            processingValue = Optional.empty()
        } else {
            val commands = input.removeFirst().split(" ")
            if (commands[0] == "addx") {
                processingValue = Optional.of(commands[1].toInt())
            }
            // Else "noop", do nothing
        }
        cycle++
    }
    return signalSum
}

private fun execute2(input: MutableList<String>) {
    var register = 1
    var rowCurser = 0
    var processingValue = Optional.empty<Int>()
    while (input.isNotEmpty()) {
        when (rowCurser - register in -1..1) {
            true -> print("#")
            false -> print(".")
        }
        rowCurser++
        if (rowCurser == 40) {
            rowCurser = 0
            println()
        }

        if (processingValue.isPresent) {
            register += processingValue.get()
            processingValue = Optional.empty()
        } else {
            val commands = input.removeFirst().split(" ")
            if (commands[0] == "addx") {
                processingValue = Optional.of(commands[1].toInt())
            }
            // Else "noop", do nothing
        }
    }

}