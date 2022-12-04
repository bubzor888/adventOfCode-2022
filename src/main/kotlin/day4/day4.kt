package day4

import java.io.File
import java.nio.file.Paths
import kotlin.test.assertEquals

fun main() {
    val path = Paths.get("").toAbsolutePath().toString()

//    val test1 = listOf("")
    val test1 = File("$path\\src\\main\\kotlin\\day4\\testInput.txt").readLines()

    assertEquals(2, execute(test1))
    assertEquals(4, execute2(test1))

    println("Tests passed, attempting input")

    val input = File("$path\\src\\main\\kotlin\\day4\\input.txt").readLines()
    //Alternative to read whole file, use .readText()

    println("Final Result 1: ${execute(input)}")
    println("Final Result 2: ${execute2(input)}")
}

private fun checkContains(smallStart: Int, smallEnd: Int, largeStart: Int, largeEnd: Int): Int {
    return when (smallStart >= largeStart && smallEnd <= largeEnd) {
        true -> 1
        false -> 0
    }
}

private fun execute(input: List<String>): Int {
    val pattern = """(\d+)-(\d+),(\d+)-(\d+)""".toRegex()
    return input.sumOf { pair ->
        val (start1, end1, start2, end2) = pattern.matchEntire(pair)!!.destructured.toList().map { it.toInt() }
        when ((end1 - start1) <= (end2 - start2)) {
            true -> checkContains(start1, end1, start2, end2)
            false -> checkContains(start2, end2, start1, end1)
        }
    }
}

private fun checkOverlap(smallStart: Int, smallEnd: Int, largeStart: Int, largeEnd: Int): Int {
    for (i in smallStart..smallEnd) {
        if (i in largeStart..largeEnd) {
            return 1
        }
    }
    return 0
}

private fun execute2(input: List<String>): Int {
    val pattern = """(\d+)-(\d+),(\d+)-(\d+)""".toRegex()
    return input.sumOf { pair ->
        val (start1, end1, start2, end2) = pattern.matchEntire(pair)!!.destructured.toList().map { it.toInt() }
        when (start1 < start2) {
            true -> checkOverlap(start1, end1, start2, end2)
            false -> checkOverlap(start2, end2, start1, end1)
        }
    }
}