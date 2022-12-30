package day24

import java.io.File
import java.nio.file.Paths
import kotlin.test.assertEquals

fun main() {
    val path = Paths.get("").toAbsolutePath().toString()

//    val test1 = listOf("")
    val test1 = File("$path\\src\\main\\kotlin\\day24\\testInput.txt").readLines()

    assertEquals(18, execute(test1))
//    assertEquals(0, execute2(test1))

    println("Tests passed, attempting input")

    val input = File("$path\\src\\main\\kotlin\\day24\\input.txt").readLines()
    //Alternative to read whole file, use .readText()

    println("Final Result 1: ${execute(input)}")
//    println("Final Result 2: ${execute2(input)}")
}

private class Blizzard(input: List<String>) {
    val rights = mutableListOf<Pair<Int, Int>>()
    val lefts = mutableListOf<Pair<Int, Int>>()
    val ups = mutableListOf<Pair<Int, Int>>()
    val downs = mutableListOf<Pair<Int, Int>>()
    val walls = mutableListOf<Pair<Int, Int>>()
    private val xMax : Int
    private val yMax : Int
    init {
        xMax = input[0].length - 1
        yMax = input.size - 1

        input.forEachIndexed { y, row ->
            row.forEachIndexed { x, ch ->
                if (y == 0 && ch == '.') {
                    //Starting position
                }
                when (ch) {
                    '>' -> rights.add(Pair(x,y))
                    '<' -> lefts.add(Pair(x,y))
                    '^' -> ups.add((Pair(x,y)))
                    'v' -> downs.add(Pair(x,y))
                    '#' -> walls.add(Pair(x,y))
                }
            }
        }
    }

    fun move() {
        for (i in rights.indices) {
            rights[i] = when (rights[i].first + 1 < xMax) {
                true -> Pair(rights[i].first + 1, rights[i].second)
                false -> Pair(1, rights[i].second)
            }
        }
        for (i in lefts.indices) {
            lefts[i] = when (lefts[i].first - 1 > 0) {
                true -> Pair(lefts[i].first - 1, lefts[i].second)
                false -> Pair(xMax-1, lefts[i].second)
            }
        }
        for (i in ups.indices) {
            ups[i] = when (ups[i].second - 1 > 0) {
                true -> Pair(ups[i].first, ups[i].second - 1)
                false -> Pair(ups[i].first, yMax-1)
            }
        }
        for (i in downs.indices) {
            downs[i] = when (downs[i].second + 1 < yMax) {
                true -> Pair(downs[i].first, downs[i].second + 1)
                false -> Pair(downs[i].first, yMax)
            }
        }
    }

    fun isOpen(position: Pair<Int, Int>): Boolean {
        return position.second >= 0 &&
                !rights.contains(position) &&
                !lefts.contains(position) &&
                !ups.contains(position) &&
                !downs.contains(position) &&
                !walls.contains(position)
    }

    fun print(position: Pair<Int, Int>, goal: Pair<Int, Int>) {
        for (y in 0..yMax) {
            for (x in 0..xMax) {
                if (Pair(x,y) == position) {
                    print("E")
                } else if (Pair(x,y) == goal) {
                    print('G')
                } else if (walls.contains(Pair(x,y))) {
                    print("#")
                } else if (rights.contains(Pair(x,y))) {
                    print('>')
                } else if (lefts.contains(Pair(x,y))) {
                    print('<')
                } else if (ups.contains(Pair(x,y))) {
                    print('^')
                } else if (downs.contains(Pair(x,y))) {
                    print('v')
                } else {
                    print('.')
                }
            }
            println()
        }
        println()
    }
}

private fun possiblePositions(position: Pair<Int, Int>, blizzard: Blizzard): List<Pair<Int, Int>> {
    val result = mutableListOf<Pair<Int, Int>>()
    if (blizzard.isOpen(position)) {
        result.add(position)
    }
    if (blizzard.isOpen(Pair(position.first + 1, position.second))) {
        result.add(Pair(position.first + 1, position.second))
    }
    if (blizzard.isOpen(Pair(position.first - 1, position.second))) {
        result.add(Pair(position.first - 1, position.second))
    }
    if (blizzard.isOpen(Pair(position.first, position.second + 1))) {
        result.add(Pair(position.first, position.second + 1))
    }
    if (blizzard.isOpen(Pair(position.first, position.second - 1))) {
        result.add(Pair(position.first, position.second - 1))
    }

    return result
}

private fun execute(input: List<String>): Int {
    val blizzard = Blizzard(input)

    var start = Pair(-1,-1)
    var goal = Pair(-1,-1)
    input[0].forEachIndexed{ x, ch ->
        if (ch == '.') {
            start = Pair(x, 0)
        }
    }
    input.last().forEachIndexed { x, ch ->
        if (ch == '.') {
            goal = Pair(x, input.size - 1)
        }
    }

    var steps = 1
    var positions = mutableSetOf(start)
    do {
        blizzard.move()
        val nextPositions = mutableSetOf<Pair<Int, Int>>()
        for (position in positions) {
            if (position == goal) {
                break;
            } else {
                nextPositions.addAll(possiblePositions(position, blizzard))
            }
        }
        if (nextPositions.contains(goal)) {
            break
            println()
        }

        positions = nextPositions
        steps++
    } while (positions.isNotEmpty())

    return steps
}

private fun execute2(input: List<String>): Int {
    return 0
}