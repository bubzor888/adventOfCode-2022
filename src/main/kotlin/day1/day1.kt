package day1

import java.io.File
import java.nio.file.Paths
import kotlin.test.assertEquals

fun main() {
    val path = Paths.get("").toAbsolutePath().toString()

//    val test1 = listOf("")
    val test1 = File("$path\\src\\main\\kotlin\\day1\\testInput.txt").readLines()

    assertEquals(24000, execute(test1))
    assertEquals(45000, execute2(test1))

    println("Tests passed, attempting input")

    val input = File("$path\\src\\main\\kotlin\\day1\\input.txt").readLines()
    //Alternative to read whole file, use .readText()

    println("Final Result 1: ${execute(input)}")
    println("Final Result 2: ${execute2(input)}")


}

private fun execute(input: List<String>): Int {
    var sum = 0
    var max = 0
    input.forEach {
        if (it.isBlank()) {
            if (max < sum)
                max = sum
            sum = 0
        } else {
            sum += it.toInt()
        }
    }
    return max
}

private fun execute2(input: List<String>): Int {
    val sums = mutableListOf<Int>()
    var sum = 0
    input.forEach {
        if (it.isBlank()) {
            sums.add(sum)
            sum = 0
        } else {
            sum += it.toInt()
        }
    }
    sums.add(sum)
    sums.sortDescending()
    return sums.foldIndexed(0) {
        index, sum, amount ->
            when (index < 3){
                true -> sum + amount
                false -> sum
            }
    }
}