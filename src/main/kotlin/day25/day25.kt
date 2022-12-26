package day25

import java.io.File
import java.nio.file.Paths
import kotlin.test.assertEquals

fun main() {
    val path = Paths.get("").toAbsolutePath().toString()

//    val test1 = listOf("")
    val test1 = File("$path\\src\\main\\kotlin\\day25\\testInput.txt").readLines()

    assertEquals("2=-1=0", execute(test1))
//    assertEquals(0, execute2(test1))

    println("Tests passed, attempting input")

    val input = File("$path\\src\\main\\kotlin\\day25\\input.txt").readLines()
    //Alternative to read whole file, use .readText()

//    println("Final Result 1: ${execute(input)}")
//    println("Final Result 2: ${execute2(input)}")
}

private fun execute(input: List<String>): String {
    input.forEach {
        var sum = 0
        it.reversed().forEachIndexed{ i, ch ->
            when (ch) {
//                '2' -> sum + (2 * )
            }
        }
    }

    return ""
}

private fun execute2(input: List<String>): Int {
    return 0
}