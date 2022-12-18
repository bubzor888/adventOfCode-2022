package day17

import java.io.File
import java.nio.file.Paths
import kotlin.test.assertEquals

fun main() {
    val path = Paths.get("").toAbsolutePath().toString()

//    val test1 = listOf("")
    val test1 = File("$path\\src\\main\\kotlin\\day17\\testInput.txt").readText()

    assertEquals(3068, execute(test1))
//    assertEquals(1514285714288, execute2(test1))

    println("Tests passed, attempting input")

    val input = File("$path\\src\\main\\kotlin\\day17\\input.txt").readText()
    //Alternative to read whole file, use .readText()

    println("Final Result 1: ${execute(input)}")
    println("Final Result 2: ${execute2(input)}")
}

enum class Shape {
    //Consider each shape in a 4x4 grid. Base is the bottom left of that grid
    //The points are which points in that 4x4 are part of the shape
    MINUS {
        override fun getPoints(base: Pair<Long, Long>): List<Pair<Long, Long>> {
            return listOf(Pair(base.first, base.second), Pair(base.first+1, base.second), Pair(base.first+2, base.second), Pair(base.first+3, base.second))
        }
    },
    PLUS {
        override fun getPoints(base: Pair<Long, Long>): List<Pair<Long, Long>> {
            return listOf(Pair(base.first, base.second+1), Pair(base.first+1, base.second), Pair(base.first+1, base.second+1), Pair(base.first+1, base.second+2), Pair(base.first+2, base.second+1))
        }
    },
    L {
        override fun getPoints(base: Pair<Long, Long>): List<Pair<Long, Long>> {
            return listOf(Pair(base.first, base.second), Pair(base.first+1, base.second), Pair(base.first+2, base.second), Pair(base.first+2, base.second+1), Pair(base.first+2, base.second+2))
        }
    },
    LINE {
        override fun getPoints(base: Pair<Long, Long>): List<Pair<Long, Long>> {
            return listOf(Pair(base.first, base.second), Pair(base.first, base.second+1), Pair(base.first, base.second+2), Pair(base.first, base.second+3))
        }
    },
    BOX {
        override fun getPoints(base: Pair<Long, Long>): List<Pair<Long, Long>> {
            return listOf(Pair(base.first, base.second), Pair(base.first+1, base.second), Pair(base.first, base.second+1), Pair(base.first+1, base.second+1))
        }
    };

    abstract fun getPoints(base: Pair<Long, Long>): List<Pair<Long, Long>>
}

private class GameState(input: String) {
    private val shapeQueue = mutableListOf(Shape.MINUS, Shape.PLUS, Shape.L, Shape.LINE, Shape.BOX)
    private val pushQueue = mutableListOf<Int>()

    //Denotes if the next move is a push or downward
    private var moveIsPush = true

    private var currentShape = listOf<Pair<Long, Long>>()

    //Tower only tracks positions of rocks, so towerY is highest Y of a rock
    private val tower = mutableSetOf<Pair<Long, Long>>()

    //Only public vars we need is the rock height and at rest
    var towerY = 0L
    var shapeAtRest = false

    //Expose extra stuff for part 2
    val moveList = mutableListOf<List<Pair<Long, Long>>>()
    val towerYList = mutableListOf<Long>()

    init {
        input.forEach {
            when (it == '>') {
                true -> pushQueue.add(1)
                false -> pushQueue.add(-1)
            }
        }
    }

    private fun nextPush(): Int {
        val push = pushQueue.removeFirst()
        pushQueue.add(push)
        return push
    }

    fun newShape() {
        val shape = shapeQueue.removeFirst()
        shapeQueue.add(shape)
        //New shapes get inserted at (3, towerY+4)
        currentShape = shape.getPoints(Pair(3, towerY+4))
        shapeAtRest = false
    }

    fun moveShape() {
        //Return false once the shape comes to rest
        when (moveIsPush) {
            true -> {
                val xChange = nextPush()
                val possibleMove = currentShape.map { Pair(it.first + xChange, it.second) }
                if (possibleMove.fold(true) { validMove, position -> validMove && (position.first in 1..7 && position !in tower) }) {
                    currentShape = possibleMove
                }
                moveIsPush = false
            }
            false -> {
                val possibleMove = currentShape.map { Pair(it.first, it.second - 1) }
                if (possibleMove.fold(true) { isValid, position -> isValid && (position.second > 0 && position !in tower) }) {
                    currentShape = possibleMove
                } else {
                    tower.addAll(currentShape)
                    moveList.add(currentShape)
                    val shapeY = currentShape.maxOf { it.second }
                    towerY = if (shapeY > towerY) shapeY else towerY
                    towerYList.add(towerY)
                    shapeAtRest = true
                }
                moveIsPush = true
            }
        }
    }
}

private fun execute(input: String): Long {
    //Convert > and < to +1 and -1 for x direction
    val game = GameState(input)

    for (rock in 1..2022) {
        game.newShape()
        while (!game.shapeAtRest) {
            game.moveShape()
        }
    }

    return game.towerY
}

private fun execute2(input: String): Long {
    val game = GameState(input)

    //First play the game for a bit
    for (i in 1..10000) {
        game.newShape()
        while (!game.shapeAtRest) {
            game.moveShape()
        }
    }

    //Now lets look for a repeat
    val startNum = 3000
    var setSize = 20
    while (true) {
        //Start with a 20 move set and expand until we find a match
        //We need to normalize the Ys based on the first block
        val set1Offset = game.moveList[startNum][0].second
        val set1 = game.moveList.subList(startNum, startNum+setSize).map { shape -> shape.map { Pair(it.first, it.second - set1Offset) } }

        val set2Offset = game.moveList[startNum+setSize][0].second
        val set2 = game.moveList.subList(startNum+setSize, startNum+(2*setSize)).map { shape -> shape.map { Pair(it.first, it.second - set2Offset) } }
        if (set1 == set2) {
            break;
        }
        setSize++
    }

    val yDiff = game.towerYList[startNum+setSize] - game.towerYList[startNum]
    //Trace back to the move when the repeats started
    val start = startNum % setSize
    val remainder = (1000000000000 - start) % setSize
    val extraSets = (1000000000000 / setSize) - 1
    //Rerun the game, going up to startNum + 1 set + remainder
    val newGame = GameState(input)
    for (i in 1..start + setSize + remainder) {
        newGame.newShape()
        while (!newGame.shapeAtRest) {
            newGame.moveShape()
        }
    }

    return newGame.towerY + (yDiff * extraSets)
}
