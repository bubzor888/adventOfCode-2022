package day14

import java.io.File
import java.nio.file.Paths
import kotlin.test.assertEquals

fun main() {
    val path = Paths.get("").toAbsolutePath().toString()

//    val test1 = listOf("")
    val test1 = File("$path\\src\\main\\kotlin\\day14\\testInput.txt").readLines()

    assertEquals(24, execute(test1))
    assertEquals(93, execute2(test1))

    println("Tests passed, attempting input")

    val input = File("$path\\src\\main\\kotlin\\day14\\input.txt").readLines()
    //Alternative to read whole file, use .readText()

    println("Final Result 1: ${execute(input)}")
    println("Final Result 2: ${execute2(input)}")
}

private fun printCave(cave: MutableMap<Pair<Int, Int>, Char>) {
    cave[Pair(500,0)] = '+'
    for (y in 0..11) {
        for (x in 488..512) {
            if (y==11) {
                print("#")
            } else {
                print(cave.getOrDefault(Pair(x,y), '.'))
            }
        }
        println()
    }
}

private fun drawRock(input: List<String>): MutableMap<Pair<Int, Int>, Char> {
    val coordinates = mutableListOf<List<Pair<Int, Int>>>()
    input.forEach {
        coordinates.add(
            it.split(" -> ").map { points ->
                val l = points.split(",")
                Pair(l[0].toInt(), l[1].toInt())
            }
        )
    }

    val cave = mutableMapOf<Pair<Int, Int>, Char>()
    coordinates.forEach { path ->
        var fromPoint = path[0]
        for (i in 1 until path.size) {
            var toPoint = path[i]
            when (fromPoint.first == toPoint.first) {
                true -> when (fromPoint.second > toPoint.second) {
                    true -> {
                        //Vertically down
                        for (y in fromPoint.second downTo toPoint.second) {
                            cave[Pair(fromPoint.first, y)] = '#'
                        }
                    }
                    false -> {
                        //Vertically up
                        for (y in fromPoint.second..toPoint.second) {
                            cave[Pair(fromPoint.first, y)] = '#'
                        }
                    }
                }
                false -> when (fromPoint.first > toPoint.first) {
                    true -> {
                        //Horizontally left
                        for (x in fromPoint.first downTo toPoint.first) {
                            cave[Pair(x, fromPoint.second)] = '#'
                        }
                    }
                    false -> {
                        //Horizontally right
                        for (x in fromPoint.first..toPoint.first) {
                            cave[Pair(x, fromPoint.second)] = '#'
                        }
                    }
                }
            }
            fromPoint = toPoint
        }
    }
    return cave
}

private fun addSand(cave: MutableMap<Pair<Int, Int>, Char>, maxY: Int): Pair<Int, Int> {
    var sandPosition = Pair(500, 0)
    while (sandPosition.second < maxY) {
        sandPosition = when {
            cave[Pair(sandPosition.first, sandPosition.second+1)] == null -> {
                Pair(sandPosition.first, sandPosition.second+1)
            }
            cave[Pair(sandPosition.first-1, sandPosition.second+1)] == null -> {
                Pair(sandPosition.first-1, sandPosition.second+1)
            }
            cave[Pair(sandPosition.first+1, sandPosition.second+1)] == null -> {
                Pair(sandPosition.first+1, sandPosition.second+1)
            }
            else -> {
                return sandPosition
            }
        }
    }

    //If we got this far, then it would be past the rock, so return rockY+1
    return Pair(sandPosition.first, maxY+1)
}

private fun execute(input: List<String>): Int {
    val cave = drawRock(input)

    //Find the highest Y value of the rock
    val maxY = cave.map { it.key.second }.max()

    //Add sand until the sand return past the rock
    var sandCount = 0
    var newSand = addSand(cave, maxY)
    while (newSand.second <= maxY) {
        cave[newSand] = 'O'
        sandCount++
        newSand = addSand(cave, maxY)

//        println("Adding Sand $sandCount")
//        printCave(cave)
//        println()
    }

    return sandCount
}

private fun addSand2(cave: MutableMap<Pair<Int, Int>, Char>, floor: Int): Pair<Int, Int> {
    var sandPosition = Pair(500, 0)
    while (true) {
        sandPosition = when {
            cave[Pair(sandPosition.first, sandPosition.second+1)] == null && sandPosition.second+1 != floor -> {
                Pair(sandPosition.first, sandPosition.second+1)
            }
            cave[Pair(sandPosition.first-1, sandPosition.second+1)] == null && sandPosition.second+1 != floor -> {
                Pair(sandPosition.first-1, sandPosition.second+1)
            }
            cave[Pair(sandPosition.first+1, sandPosition.second+1)] == null && sandPosition.second+1 != floor -> {
                Pair(sandPosition.first+1, sandPosition.second+1)
            }
            else -> {
                return sandPosition
            }
        }
    }
}

private fun execute2(input: List<String>): Int {
    val cave = drawRock(input)
    val floor = cave.map { it.key.second }.max() + 2

    //Add sand until the sand return past the rock
    var sandCount = 1
    var newSand = addSand(cave, floor)
    while (newSand != Pair(500, 0)) {
        cave[newSand] = 'O'
        sandCount++
        newSand = addSand2(cave, floor)
    }

    return sandCount
}