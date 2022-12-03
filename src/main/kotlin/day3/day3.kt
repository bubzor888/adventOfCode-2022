package day3

import java.io.File
import java.nio.file.Paths
import kotlin.test.assertEquals

fun main() {
    val path = Paths.get("").toAbsolutePath().toString()

//    val test1 = listOf("")
    val test1 = File("$path\\src\\main\\kotlin\\day3\\testInput.txt").readLines()

    assertEquals(157, execute(test1))
    assertEquals(70, execute2(test1))

    println("Tests passed, attempting input")

    val input = File("$path\\src\\main\\kotlin\\day3\\input.txt").readLines()
    //Alternative to read whole file, use .readText()

    println("Final Result 1: ${execute(input)}")
    println("Final Result 2: ${execute2(input)}")
}

private fun convertToPriority(item: Char): Int {
    // Char decimal vals don't match up, so subtract (a.code+1) for 1-26
    // and then (A.code+1) + 26 for 27-52
    return when (item.isLowerCase()) {
        true -> item.code - 'a'.code + 1
        false -> item.code - 'A'.code + 27
    }
}

private fun execute(input: List<String>): Int {
    return input.sumOf {
        val rut1 = it.substring(0, it.length / 2)
        val rut2 = it.substring(it.length / 2)
        val overlap = rut1.toCharArray().intersect(rut2.toCharArray().asIterable().toSet())
        convertToPriority(overlap.first())
    }
}

private fun execute2(input: List<String>): Int {
    return input.chunked(3).sumOf { group ->
        convertToPriority(
            group.map { it.toCharArray().toSet() }.reduce { acc, next ->
                acc.intersect(next.asIterable().toSet())
            }.first()
        )
    }
}