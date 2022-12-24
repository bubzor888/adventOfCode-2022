package day18

import java.io.File
import java.nio.file.Paths
import java.util.*
import kotlin.test.assertEquals

fun main() {
    val path = Paths.get("").toAbsolutePath().toString()

//    val test1 = listOf("")
    val test1 = File("$path\\src\\main\\kotlin\\day18\\testInput.txt").readLines()

    assertEquals(64, execute(test1))
    assertEquals(58, execute2(test1))

    println("Tests passed, attempting input")

    val input = File("$path\\src\\main\\kotlin\\day18\\input.txt").readLines()
    //Alternative to read whole file, use .readText()

    println("Final Result 1: ${execute(input)}")
//    println("Final Result 2: ${execute2(input)}")
}

private class Point(val x: Int, val y: Int, val z: Int) : Comparable<Point> {
    override fun compareTo(other: Point): Int {
        return when {
            x < other.x && y == other.y && z == other.z -> -1
            x == other.x && y < other.y && z == other.z -> -1
            x == other.x && y == other.y && z < other.z -> -1
            x > other.x && y == other.y && z == other.z -> 1
            x == other.x && y > other.y && z == other.z -> 1
            x == other.x && y == other.y && z > other.z -> 1
            else -> 0
        }
    }

    override fun equals(other: Any?): Boolean {
        return (other is Point)
                && this.x == other.x
                && this.y == other.y
                && this.z == other.z
    }

    override fun hashCode(): Int {
        return Objects.hash(x, y, z)
    }

}

private class Cube(x: Int, y: Int, z: Int) {
    val sides = mutableListOf<Set<Point>>()
    val exposedSides = mutableListOf<Set<Point>>()
    init {
        //Front
        sides.add(setOf(Point(x,y,z), Point(x+1,y,z), Point(x,y+1,z), Point(x+1,y+1,z)))
        //Bottom
        sides.add(setOf(Point(x,y,z), Point(x+1,y,z), Point(x,y,z+1), Point(x+1,y,z+1)))
        //Left
        sides.add(setOf(Point(x,y,z), Point(x,y+1,z), Point(x,y+1,z+1), Point(x,y,z+1)))
        //Right
        sides.add(setOf(Point(x+1,y,z), Point(x+1,y+1,z), Point(x+1,y+1,z+1), Point(x+1,y,z+1)))
        //Top
        sides.add(setOf(Point(x,y+1,z), Point(x+1,y+1,z), Point(x,y+1,z+1), Point(x+1,y+1,z+1)))
        //Back
        sides.add(setOf(Point(x,y+1,z+1), Point(x+1,y+1,z+1), Point(x,y,z+1), Point(x+1,y,z+1)))
        exposedSides.addAll(sides)
    }
}

private fun execute(input: List<String>): Int {
    val cubes = input.map { cube ->
        val (x,y,z) = cube.split(",").map { it.toInt() }
        Cube(x,y,z)
    }

    //Now remove touching sides
    cubes.forEach { cube ->
        cubes.minus(cube).forEach { otherCube ->
            cube.exposedSides.removeAll(otherCube.sides)
        }
    }

    return cubes.sumOf { cube -> cube.exposedSides.size }
}

private fun execute2(input: List<String>): Int {
    val cubes = input.map { cube ->
        val (x,y,z) = cube.split(",").map { it.toInt() }
        Cube(x,y,z)
    }

    //Now remove touching sides
    cubes.forEach { cube ->
        cubes.minus(cube).forEach { otherCube ->
            cube.exposedSides.removeAll(otherCube.sides)
        }
    }

    //Make a list of the remaining sides, they should be unique
    val sides = mutableListOf<Set<Point>>()
    cubes.forEach { cube -> sides.addAll(cube.exposedSides) }

    //Also make a set of all points in the remaining sides
    val points = mutableSetOf<Point>()
    sides.forEach { side -> points.addAll(side) }

    //Now we need to take away sides where all its points have a side bigger than it, and a side smaller than it
    //A side is less than another side if all of its points are less than another point
    val outerSides = sides.filter{ side ->
        if (side == setOf(Point(2,2,5), Point(3,2,5), Point(2,3,5), Point(3,3,5))) {
            println("found")
        }
        //Need to find another side where all points are less than in 1 direction, and greater than 1 direction
        val hasBigger = side.all { point ->
            val otherSide = side.minus(side)
//            otherSide.all { otherPoint ->
//
//            }
//            for (otherSide in sides.minus(side)) {
//                otherSide
//                if (otherSide.all)
//            }
            var found = false
            for (otherPoint in points.minus(point)) {
                if (point.compareTo(otherPoint) == 1) {
                    found = true
                    break
                }
            }
            found
        }
        var hasSmaller = side.all { point ->
            var found = false
            for (otherPoint in points.minus(point)) {
                if (point.compareTo(otherPoint) == -1) {
                    found = true
                    break
                }
            }
            found
        }
        !(hasBigger && hasSmaller)
    }

    return outerSides.size
}