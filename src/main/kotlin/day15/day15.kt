package day15

import java.io.File
import java.nio.file.Paths
import javax.xml.crypto.dsig.keyinfo.KeyValue
import kotlin.math.abs
import kotlin.test.assertEquals

fun main() {
    val path = Paths.get("").toAbsolutePath().toString()

//    val test1 = listOf("")
    val test1 = File("$path\\src\\main\\kotlin\\day15\\testInput.txt").readLines()

    assertEquals(26, execute(test1, 10))
//    assertEquals(56000011, execute2(test1, 20))

    println("Tests passed, attempting input")

    val input = File("$path\\src\\main\\kotlin\\day15\\input.txt").readLines()
    //Alternative to read whole file, use .readText()

//    println("Final Result 1: ${execute(input, 2000000)}")
    println("Final Result 2: ${execute2(input, 4000000)}")
}

val pattern = """[^0-9-]+(-?\d+)[^0-9-]+(-?\d+)[^0-9-]+(-?\d+)[^0-9-]+(-?\d+)""".toRegex()

private fun distance1D(p1: Long, p2: Long): Long {
    return when (p1 > p2) {
        true -> abs(p1 - p2)
        false -> abs(p2 - p1)
    }
}

private fun distance2D(point1: Pair<Long, Long>, point2: Pair<Long, Long>): Long {
    return distance1D(point1.first, point2.first) + distance1D(point1.second, point2.second)
}

private fun execute(input: List<String>, y: Long): Int {
    var minX = Long.MAX_VALUE
    var maxX = Long.MIN_VALUE
    var minY = Long.MAX_VALUE
    var maxY = Long.MIN_VALUE
    val sensors = input.associate {
        val (sX, sY, bX, bY) = pattern.matchEntire(it)!!.destructured.toList().map { v -> v.toLong() }
        val sensor = Pair(sX, sY)
        val beacon = Pair(bX, bY)
        val distance = distance2D(sensor, beacon)
        if (sX - distance < minX) {
            minX = sX - distance
        } else if (sX + distance > maxX) {
            maxX = sX + distance
        }
        if (sY - distance < minY) {
            minY = sY - distance
        } else if (sY + distance > maxY) {
            maxY = sY + distance
        }
        sensor to beacon
    }

    //Now figure out blocked positions on given row
    var blockedCount = 0
    println(" minX: $minX manX: $maxX")
    println(" minY: $minY maxY: $maxY")
    for (x in minX..maxX) {
        val pos = Pair(x,y)
        if (pos !in sensors.keys && pos !in sensors.values) {
            //Only consider spots w/o sensors or beacons
            for (sensor in sensors) {
                if (distance2D(pos, sensor.key) <= distance2D(sensor.key, sensor.value)) {
                    blockedCount++
                    break;
                }
            }
        }
    }

    return blockedCount
}

private fun execute2(input: List<String>, xyMax: Long): Long {
    val sensors = input.map {
        val (sX, sY, bX, bY) = pattern.matchEntire(it)!!.destructured.toList().map { v -> v.toLong() }
        Pair(Pair(sX, sY), Pair(bX, bY))
    }.sortedWith (
        compareBy<Pair<Pair<Long, Long>, Pair<Long, Long>>> {
            //Sort by sensor X first
            it.first.first
        }.thenBy {
            //Then by sensor Y second
            it.first.second
        }
    )

//    val rows = mutableMapOf<Long, MutableSet<LongRange>>()
//    for (y in 0..xyMax) {
//        rows[y] = mutableSetOf(0..xyMax)
//    }
//    for (sensor in sensors) {
//        val distance = distance2D(sensor.key, sensor.value)
//        var xWidth = 0
//        var change = 1
//        for (y in sensor.key.second - distance..sensor.key.second + distance) {
//            val row = rows[y]
//            if (row != null) {
//                val range = row.find { it.contains(y) }
//                row.remove(range)
//                val xRange = y-xWidth..y+xWidth
//                row.subtract(xRange)
//                row.add(range.first)
//            }
//            xWidth += change
//            if (y == sensor.key.second) {
//                change = -1
//            }
//        }
//    }
    //Find the largest distance of a sensor
    var maxDistance = 0L
    for (sensor in sensors) {
        val distance = distance2D(sensor.first, sensor.second)
        if (distance > maxDistance) {
            maxDistance = distance
        }
    }

    var result: Pair<Long, Long>? = null
    for (y in 0..xyMax) {
        println("Row $y")
        for (x in 0..xyMax) {
            val pos = Pair(x, y)
            var inRange = false
            for (sensor in sensors) {
                //Check if
                if (distance2D(pos, sensor.first) <= distance2D(sensor.first, sensor.second)) {
                    inRange = true
                    break
                }
            }
            if (!inRange) {
                result = pos
                break
            }
        }
    }

    return if (result == null) {
        -1
    } else {
        println("Result: ${result.first}, ${result.second}")
        (result.first * 4000000) + result.second
    }
//    return 0
}