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
    assertEquals(56000011, execute2(test1, 20))

    println("Tests passed, attempting input")

    val input = File("$path\\src\\main\\kotlin\\day15\\input.txt").readLines()
    //Alternative to read whole file, use .readText()

    println("Final Result 1: ${execute(input, 2000000)}")
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
    val sensors = input.associate {
        val (sX, sY, bX, bY) = pattern.matchEntire(it)!!.destructured.toList().map { v -> v.toLong() }
        val sensor = Pair(sX, sY)
        val beacon = Pair(bX, bY)
        sensor to distance2D(sensor, beacon)
    }

    for (y in 0..xyMax) {
        val row = mutableListOf<LongRange>()
        for (sensor in sensors) {
            //Check if the y value of the sensor is relevant based on distance
            val distance = distance1D(y, sensor.key.second)
            if (distance < sensor.value) {
                val xSpan = sensor.value - distance
                var xRange = sensor.key.first-xSpan..sensor.key.first+xSpan
                if (xRange.last < 0) {
                    continue
                } else if (xRange.first < 0) {
                    xRange = 0..sensor.key.first+xSpan
                }
                row.add(xRange)
            }
        }
        //Sort the ranges by starting digit
        val sorted = row.sortedBy { it.first }

        //Now see if anything is missing
        var newRange = 0L..0L
        for (range in sorted) {
            if (range.first !in newRange) {
                //Found it
                return ((range.first-1) * 4000000) + y
            } else if (range.last > newRange.last) {
                newRange = newRange.first..range.last
            }
        }
    }

    return 0L
}