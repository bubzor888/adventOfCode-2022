package day22

import java.io.File
import java.nio.file.Paths
import kotlin.test.assertEquals

fun main() {
    val path = Paths.get("").toAbsolutePath().toString()

//    val test1 = listOf("")
    val test1 = File("$path\\src\\main\\kotlin\\day22\\testInput.txt").readLines()

    assertEquals(6032, execute(test1, v1 = true))
    assertEquals(5031, execute(test1, v1 = false))

    println("Tests passed, attempting input")

    val input = File("$path\\src\\main\\kotlin\\day22\\input.txt").readLines()
    //Alternative to read whole file, use .readText()

    println("Final Result 1: ${execute(input, v1 = true)}")
//    println("Final Result 2: ${execute(input, v1 = false)}")
}

private class Position(var xy: Pair<Int, Int>, var facing: Int)

private class BoardMap(input: List<String>) {
    val map = mutableMapOf<Pair<Int, Int>, Char>()
    var maxX = 0
    var maxY = 0
    var position : Position
    init {
        //Remove the last 2 rows since they aren't the map
        input.subList(0, input.size-2).forEachIndexed { y, row ->
            row.forEachIndexed { x, ch ->
                //Add 1 to eliminate the zero row/column
                if (ch != ' ') {
                    map[Pair(x+1, y+1)] = ch

                    if (x+1 > maxX) maxX = x+1
                    if (y+1 > maxY) maxY = y+1
                }
            }
        }

        //Find the first open til in row 1
        var startX = 0
        for (x in 1..maxX) {
            if (map.getOrDefault(Pair(x, 1), ' ') == '.') {
                startX = x
                break
            }
        }
        position = Position(Pair(startX, 1), 0)
    }

    fun changeFacing(s: String) {
        position.facing = when (s) {
            "R" -> if (position.facing + 1 > 3) 0 else position.facing + 1
            "L" -> if (position.facing -1 < 0) 3 else position.facing -1
            else -> position.facing
        }
    }

    fun takeSteps(steps: Int, v1: Boolean) {
        for (i in 1..steps) {
            val nextXY = if (v1) findNextXY() else findNextXY2()
            if (nextXY.second == '.') {
                position.xy = nextXY.first
            } else {
                //Hit a wall
                break
            }
        }
    }

    private fun findNextXY(): Pair<Pair<Int, Int>, Char> {
        val takeStep : (Pair<Int, Int>) -> Pair<Int, Int>
        val boardEdge : Pair<Int, Int>
        when (position.facing) {
            0 -> {
                takeStep = { it: Pair<Int, Int> -> Pair(it.first + 1, it.second) }
                boardEdge = Pair(1, position.xy.second)
            }
            1 -> {
                takeStep = { it: Pair<Int, Int> -> Pair(it.first, it.second + 1) }
                boardEdge = Pair(position.xy.first, 1)
            }
            2 -> {
                takeStep =  { it: Pair<Int, Int> -> Pair(it.first - 1, it.second) }
                boardEdge = Pair(maxX, position.xy.second)
            }
            else -> {
                takeStep = { it: Pair<Int, Int> -> Pair(it.first, it.second - 1) }
                boardEdge = Pair(position.xy.first, maxY)
            }
        }

        var nextXY = takeStep(position.xy)
        return when (map[nextXY] != null) {
            true -> Pair(nextXY, map[nextXY]!!)
            false -> {
                nextXY = boardEdge
                while (map[nextXY] == null) {
                    nextXY = takeStep(nextXY)
                }
                Pair(nextXY, map[nextXY]!!)
            }
        }
    }

    private fun findNextXY2(): Pair<Pair<Int, Int>, Char> {
        return Pair(Pair(1,1), '.')
    }
}


private fun execute(input: List<String>, v1: Boolean): Int {
    val boardMap = BoardMap(input)

    //Split out the commands
    val moves = """(\d+)""".toRegex().findAll(input.last()).toList().map { it.value.toInt() }.iterator()
    val turns = """([RL])""".toRegex().findAll(input.last()).toList().map { it.value }.iterator()

    var isMoveNext = true
    while (moves.hasNext() || turns.hasNext()) {
        if (isMoveNext) {
            boardMap.takeSteps(moves.next(), v1)
        } else {
            boardMap.changeFacing(turns.next())
        }

        isMoveNext = !isMoveNext
    }

    return (boardMap.position.xy.second * 1000) + (boardMap.position.xy.first * 4) + boardMap.position.facing
}