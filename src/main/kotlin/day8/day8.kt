package day8

import java.io.File
import java.nio.file.Paths
import kotlin.test.assertEquals

fun main() {
    val path = Paths.get("").toAbsolutePath().toString()

//    val test1 = listOf("")
    val test1 = File("$path\\src\\main\\kotlin\\day8\\testInput.txt").readLines()

    assertEquals(21, execute(test1))
//    assertEquals(0, execute2(test1))

    println("Tests passed, attempting input")

    val input = File("$path\\src\\main\\kotlin\\day8\\input.txt").readLines()
    //Alternative to read whole file, use .readText()

//    println("Final Result 1: ${execute(input)}")
//    println("Final Result 2: ${execute2(input)}")
}

private fun checkVisibility(grid: Map<Pair<Int, Int>, Int>, sightLine: List<Int>, tree: Int): Boolean {
    return sightLine.fold(true) { result, nextTree -> result && nextTree < tree }
}

private fun execute(input: List<String>): Int {
    val grid = mutableMapOf<Pair<Int, Int>, Int>().withDefault { 0 }
    val maxX = input[0].length
    val maxY = input.size

    input.forEachIndexed { y, row ->
        row.forEachIndexed { x, h ->
            grid[Pair(x, y)] = h.digitToInt()
        }
    }

    var totalVisible = 0



    return totalVisible
}

private fun execute2(input: List<String>): Int {
    return 0
}