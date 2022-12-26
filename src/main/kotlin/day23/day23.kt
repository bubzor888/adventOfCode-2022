package day23

import java.io.File
import java.nio.file.Paths
import kotlin.math.abs
import kotlin.test.assertEquals

fun main() {
    val path = Paths.get("").toAbsolutePath().toString()

//    val test1 = listOf("")
    val test1 = File("$path\\src\\main\\kotlin\\day23\\testInput.txt").readLines()

    assertEquals(110, execute(test1))
    assertEquals(20, execute2(test1))

    println("Tests passed, attempting input")

    val input = File("$path\\src\\main\\kotlin\\day23\\input.txt").readLines()
    //Alternative to read whole file, use .readText()

    println("Final Result 1: ${execute(input)}")
    println("Final Result 2: ${execute2(input)}")
}

private enum class Direction {
    NORTH, SOUTH, WEST, EAST, ALL
}

private fun adjacentSpots(spot: Pair<Int, Int>, direction: Direction): List<Pair<Int, Int>> {
    val spots = mutableListOf<Pair<Int, Int>>()

    when (direction) {
        Direction.NORTH -> {
            spots.add(Pair(spot.first - 1, spot.second - 1))
            spots.add(Pair(spot.first, spot.second - 1))
            spots.add(Pair(spot.first + 1, spot.second - 1))
        }
        Direction.SOUTH -> {
            spots.add(Pair(spot.first - 1, spot.second + 1))
            spots.add(Pair(spot.first, spot.second + 1))
            spots.add(Pair(spot.first + 1, spot.second + 1))
        }
        Direction.WEST -> {
            spots.add(Pair(spot.first - 1, spot.second - 1))
            spots.add(Pair(spot.first - 1, spot.second))
            spots.add(Pair(spot.first - 1, spot.second + 1))
        }
        Direction.EAST -> {
            spots.add(Pair(spot.first + 1, spot.second - 1))
            spots.add(Pair(spot.first + 1, spot.second))
            spots.add(Pair(spot.first + 1, spot.second + 1))
        }
        Direction.ALL -> {
            spots.add(Pair(spot.first - 1, spot.second - 1))
            spots.add(Pair(spot.first - 1, spot.second))
            spots.add(Pair(spot.first - 1, spot.second + 1))
            spots.add(Pair(spot.first, spot.second - 1))
            spots.add(Pair(spot.first, spot.second + 1))
            spots.add(Pair(spot.first + 1, spot.second - 1))
            spots.add(Pair(spot.first + 1, spot.second))
            spots.add(Pair(spot.first + 1, spot.second + 1))
        }
    }

    return spots
}

private fun execute(input: List<String>): Int {
    val elves = mutableMapOf<Pair<Int, Int>, Char>()
    input.forEachIndexed { y, row ->
        row.forEachIndexed { x, ch ->
            if (ch == '#') {
                elves[Pair(x,y)] = '#'
            }
        }
    }

    val directionList = mutableListOf(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST)

    for (i in 1..10) {
        val proposedMoves = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>>()
        elves.keys.forEach { elf ->
            //Only need to move elves with another elf adjacent
            if (adjacentSpots(elf, Direction.ALL).intersect(elves.keys).isNotEmpty()) {
                for (direction in directionList) {
                    if (adjacentSpots(elf, direction).intersect(elves.keys).isEmpty()) {
                        //The spot to move to is always middle of the 3 adjacent spots
                        proposedMoves[elf] = adjacentSpots(elf, direction)[1]
                        break;
                    }
                }
            }
        }
        proposedMoves.forEach { move ->
            //List.minus() uses === and List.contains uses ==
            if (!proposedMoves.values.minus(move.value).contains(move.value)) {
                elves.remove(move.key)
                elves[move.value] = '#'
            }
        }

        //Cycle the directions
        directionList.add(directionList.removeFirst())
    }

    //Calculate min/maxes
    var xMax = 0
    var xMin = 0
    var yMax = 0
    var yMin = 0
    elves.keys.forEach { pos ->
        xMax = if (pos.first > xMax) pos.first else xMax
        yMax = if (pos.second > yMax) pos.second else yMax
        xMin = if (pos.first < xMin) pos.first else xMin
        yMin = if (pos.second < yMin) pos.second else yMin
    }


    //Add 1 to account for the zero row/column
    return ((abs(xMin) + xMax + 1) * (abs(yMin) + yMax + 1)) - elves.keys.size
}

private fun execute2(input: List<String>): Int {
    val elves = mutableMapOf<Pair<Int, Int>, Char>()
    input.forEachIndexed { y, row ->
        row.forEachIndexed { x, ch ->
            if (ch == '#') {
                elves[Pair(x,y)] = '#'
            }
        }
    }

    val directionList = mutableListOf(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST)
    var round = 1
    while (true) {
        val proposedMoves = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>>()
        elves.keys.forEach { elf ->
            //Only need to move elves with another elf adjacent
            if (adjacentSpots(elf, Direction.ALL).intersect(elves.keys).isNotEmpty()) {
                for (direction in directionList) {
                    if (adjacentSpots(elf, direction).intersect(elves.keys).isEmpty()) {
                        //The spot to move to is always middle of the 3 adjacent spots
                        proposedMoves[elf] = adjacentSpots(elf, direction)[1]
                        break;
                    }
                }
            }
        }

        if (proposedMoves.isEmpty()) {
            break;
        }
        proposedMoves.forEach { move ->
            //List.minus() uses === and List.contains uses ==
            if (!proposedMoves.values.minus(move.value).contains(move.value)) {
                elves.remove(move.key)
                elves[move.value] = '#'
            }
        }

        //Cycle the directions
        directionList.add(directionList.removeFirst())
        round++
    }

    //Add 1 to account for the zero row/column
    return round
}