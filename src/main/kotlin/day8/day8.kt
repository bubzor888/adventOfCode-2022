package day8

import java.io.File
import java.nio.file.Paths
import kotlin.test.assertEquals

fun main() {
    val path = Paths.get("").toAbsolutePath().toString()

//    val test1 = listOf("")
    val test1 = File("$path\\src\\main\\kotlin\\day8\\testInput.txt").readLines()

    assertEquals(21, execute(test1))
    assertEquals(8, execute2(test1))

    println("Tests passed, attempting input")

    val input = File("$path\\src\\main\\kotlin\\day8\\input.txt").readLines()
    //Alternative to read whole file, use .readText()

    println("Final Result 1: ${execute(input)}")
    println("Final Result 2: ${execute2(input)}")
}

private fun isVisibleOneDirection(line: Collection<Int>, tree: Int): Boolean {
    //Start assuming it's visible (for the empty list scenario) and check if a tree hides it
    return line.fold(true) { result, nextTree -> result && nextTree < tree }
}

private fun isVisible(grid: Map<Pair<Int, Int>, Int>, point: Pair<Int, Int>, value: Int): Boolean {
    //Check each side
    val left = grid.filter { it.key.first < point.first && it.key.second == point.second }.values
    val right = grid.filter { it.key.first > point.first && it.key.second == point.second }.values
    val top = grid.filter { it.key.first == point.first && it.key.second < point.second }.values
    val bottom = grid.filter { it.key.first == point.first && it.key.second > point.second }.values

    return isVisibleOneDirection(left, value) || isVisibleOneDirection(right, value) ||
            isVisibleOneDirection(top, value) || isVisibleOneDirection(bottom, value)

}

private fun execute(input: List<String>): Int {
    val grid = mutableMapOf<Pair<Int, Int>, Int>().withDefault { 0 }

    input.forEachIndexed { y, row ->
        row.forEachIndexed { x, h ->
            grid[Pair(x, y)] = h.digitToInt()
        }
    }

    return grid.map {
        when (isVisible(grid, it.key, it.value)) {
            true -> 1
            false -> 0
        }
    }.sum()
}

private fun countTrees(grid: Map<Pair<Int, Int>, Int>, tree: Pair<Int, Int>, value: Int, xChange: Int, yChange: Int): Int {
    var treeCount = 0
    var nextTree = Pair(tree.first+xChange, tree.second+yChange)
    var nextTreeValue = grid[nextTree]
    while (nextTreeValue != null) {
        treeCount++
        if (nextTreeValue >= value) {
            break
        }
        nextTree = Pair(nextTree.first+xChange, nextTree.second+yChange)
        nextTreeValue = grid[nextTree]
    }

    return treeCount
}

private fun calcScore(grid: Map<Pair<Int, Int>, Int>, point: Pair<Int, Int>, value: Int): Int {
    return countTrees(grid, point, value, -1, 0) *
            countTrees(grid, point, value, 1, 0) *
            countTrees(grid, point, value, 0, -1) *
            countTrees(grid, point, value, 0, 1)
}

private fun execute2(input: List<String>): Int {
    val grid = mutableMapOf<Pair<Int, Int>, Int>().withDefault { 0 }

    input.forEachIndexed { y, row ->
        row.forEachIndexed { x, h ->
            grid[Pair(x, y)] = h.digitToInt()
        }
    }

    return grid.map { calcScore(grid, it.key, it.value) }.max()
}