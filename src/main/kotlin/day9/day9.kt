package day9

import java.io.File
import java.nio.file.Paths
import kotlin.math.abs
import kotlin.test.assertEquals

fun main() {
    val path = Paths.get("").toAbsolutePath().toString()

//    val test1 = listOf("")
    val test1 = File("$path\\src\\main\\kotlin\\day9\\testInput.txt").readLines()

//    assertEquals(13, execute(test1))
//    assertEquals(1, execute2(test1))

    val test2 = File("$path\\src\\main\\kotlin\\day9\\testInput2.txt").readLines()

    assertEquals(36, execute2(test2))

    println("Tests passed, attempting input")

    val input = File("$path\\src\\main\\kotlin\\day9\\input.txt").readLines()
    //Alternative to read whole file, use .readText()

    println("Final Result 1: ${execute(input)}")
    println("Final Result 2: ${execute2(input)}")
}

private fun execute(input: List<String>): Int {
    var head = Pair(0, 0)
    var prevHead: Pair<Int, Int>
    var tail = Pair(0, 0)
    val tailPositions = mutableSetOf(Pair(0, 0))

    input.forEach {command ->
        val (dir, amount) = command.split(" ")
        for (i in 0 until amount.toInt()) {
            prevHead = head
            when (dir) {
                "R" -> {
                    head = Pair(head.first + 1, head.second)
                    if (abs(head.first - tail.first) > 1) {
                        tail = prevHead
                    }
                }
                "L" -> {
                    head = Pair(head.first - 1, head.second)
                    if (abs(tail.first - head.first) > 1) {
                        tail = prevHead
                    }
                }
                "U" -> {
                    head = Pair(head.first, head.second + 1)
                    if (abs(head.second - tail.second) > 1) {
                        tail = prevHead
                    }
                }
                "D" -> {
                    head = Pair(head.first, head.second - 1)
                    if (abs(tail.second - head.second) > 1) {
                        tail = prevHead
                    }
                }
            }
            tailPositions.add(tail)
        }
    }

    return tailPositions.size
}

private fun moveKnot(point1: Pair<Int, Int>, point2: Pair<Int, Int>): Pair<Int, Int> {
    return when {
        abs(point1.first - point2.first) > 1 -> Pair(point1.first-1, point2.second)
        abs(point2.first - point1.first) > 1 -> Pair(point1.first+1, point2.second)
        abs(point1.second - point2.second) > 1 -> Pair(point2.first, point1.second-1)
        abs(point2.second - point1.second) > 1 -> Pair(point2.first, point1.second+1)
        else -> point2
    }
}

private fun execute2(input: List<String>): Int {
    //rope[0] will be head, rope[9] will be tail
    val rope = mutableListOf<Pair<Int, Int>>()
    var prevRope: MutableList<Pair<Int, Int>>
    for (i in 0 until 10) {
        rope.add(Pair(0,0))
    }
    val tailPositions = mutableSetOf(Pair(0, 0))

    input.forEach {command ->
        val (dir, amount) = command.split(" ")
        for (i in 0 until amount.toInt()) {
            prevRope = rope.toMutableList()
            when (dir) {
                "R" -> {
                    rope[0] = Pair(rope[0].first + 1, rope[0].second)
                    for(j in 0 until 9) {
                        rope[j+1] = moveKnot(rope[j], rope[j+1])
                    }
                }
                "L" -> {
                    rope[0] = Pair(rope[0].first - 1, rope[0].second)
                    for(j in 0 until 9) {
                        rope[j+1] = moveKnot(rope[j], rope[j+1])
                    }
                }
                "U" -> {
                    rope[0] = Pair(rope[0].first, rope[0].second + 1)
                    for(j in 0 until 9) {
                        rope[j+1] = moveKnot(rope[j], rope[j+1])
                    }
                }
                "D" -> {
                    rope[0] = Pair(rope[0].first, rope[0].second - 1)
                    for(j in 0 until 9) {
                        rope[j+1] = moveKnot(rope[j], rope[j+1])
                    }
                }
            }
            tailPositions.add(rope[9])
//            println("$rope")
        }
        printRope(rope)
    }



    return tailPositions.size
}

private fun printRope(rope: List<Pair<Int, Int>>) {
    val output = mutableMapOf<Pair<Int, Int>, Char>()
    var minX = -5
    var maxX = 5
    var minY = -5
    var maxY = 5
    rope.forEachIndexed { i, it ->
        if (i == 0) {
            output[it] = 'H'
        } else {
            output.putIfAbsent(it, i.toString().first())
        }
        if (it.first < minX) minX = it.first
        if (it.first > maxX) maxX = it.first
        if (it.second < minY) minY = it.second
        if (it.second > maxY) maxY = it.second
    }

    for (y in maxY downTo minY) {
        for (x in minX..maxX) {
            print(output.getOrDefault(Pair(x,y), '.'))
        }
        println()
    }
    println()
}

private fun printTails(tails: List<Pair<Int, Int>>) {
    val output = mutableMapOf<Pair<Int, Int>, Char>()
    var minX = 0
    var maxX = 0
    var minY = 0
    var maxY = 0
    tails.forEach {
        output[it] = '#'
        if (it.first < minX) minX = it.first
        if (it.first > maxX) maxX = it.first
        if (it.second < minY) minY = it.second
        if (it.second > maxY) maxY = it.second
    }

    for (y in maxY downTo minY) {
        for (x in minX..maxX) {
            print(output.getOrDefault(Pair(x,y), '.'))
        }
        println()
    }
}