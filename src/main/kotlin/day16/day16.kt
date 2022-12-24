package day16

import java.io.File
import java.nio.file.Paths
import java.util.*
import kotlin.math.min
import kotlin.test.assertEquals

fun main() {
    val path = Paths.get("").toAbsolutePath().toString()

//    val test1 = listOf("")
    val test1 = File("$path\\src\\main\\kotlin\\day16\\testInput.txt").readLines()

    assertEquals(1651, execute(test1))
//    assertEquals(1707, execute2(test1))

    println("Tests passed, attempting input")

    val input = File("$path\\src\\main\\kotlin\\day16\\input.txt").readLines()
    //Alternative to read whole file, use .readText()

//    println("Final Result 1: ${execute(input)}")
    println("Final Result 2: ${execute2(input)}")
}

private val pattern = """Valve (\w\w) has flow rate=(\d+); tunnels? leads? to valves? ([\w, ]+)""".toRegex()

private class Valve(val name: String, val rate: Int, val connections: List<String>)

private class ValvesState(val valve: String, val minutesLeft: Int, val totalPressure: Int, val remainingValves: Set<String>)

private class ValvesState2(val me: String, val elephant: String, val myMinutes: Int, val elephantMinutes: Int, val totalPressure: Int, val remainingValves: Set<String>, val visitedValves: Set<String>) {
    override fun equals(other: Any?): Boolean {
        return (other is ValvesState2)
                && this.visitedValves == other.visitedValves
                && this.totalPressure == other.totalPressure
                && (this.myMinutes + this.elephantMinutes) == other.myMinutes + other.elephantMinutes
    }

    override fun hashCode(): Int {
        return Objects.hash(visitedValves, totalPressure, myMinutes+elephantMinutes)
    }
}

private fun calculateDistances(valves: Map<String, Valve>, startValve: String): Map<String, Int> {
    //Start by using Dijkstra's algorithm to calculate distance to closed valves
    val unvisitedNodes = valves.toMutableMap()
    val distances = valves.keys.associateWith { Int.MAX_VALUE }.toMutableMap()
    //Set start node's distance is zero
    distances[startValve] = 0

    var currentNode : Pair<String, Int>
    while (unvisitedNodes.isNotEmpty()) {
        //Find the lowest unvisited node
        currentNode = distances.toList().filter { it.first in unvisitedNodes.keys }.sortedBy { (_, value) -> value}.first()

        //Recalculate distance of adjacent nodes
        valves[currentNode.first]!!.connections.forEach {
            val newDistance = currentNode.second + 1
            if (newDistance < distances[it]!!) {
                distances[it] = newDistance
            }
        }
        unvisitedNodes.remove(currentNode.first)
    }

    return distances
}

private fun execute(input: List<String>): Int {
    val valves = input.associate {
        val (name, rate, connections) = pattern.matchEntire(it)!!.destructured
        name to Valve(name, rate.toInt(), connections.split(", "))
    }

    //Do breath first search on all the valves
    val valveQueue = mutableSetOf(ValvesState("AA",30, 0, valves.filter { it.value.rate > 0 }.keys))
    val possiblePressure = mutableListOf<Int>()
    while (valveQueue.isNotEmpty()) {
        val state = valveQueue.toList().first()
        valveQueue.remove(state)
        val distances = calculateDistances(valves, state.valve)
        //We only have to consider valves we can travel to and open in time
        val children = state.remainingValves.filter { state.minutesLeft - distances[it]!! - 1 > 0 }
        if (children.isEmpty()) {
            //No children means we can't get anywhere else in the time left
            possiblePressure.add(state.totalPressure)
        } else {
            children.forEach {
                val minutesLeft = state.minutesLeft - distances[it]!! - 1
                val pressure = state.totalPressure + (minutesLeft * valves[it]!!.rate)
                val newValves = state.remainingValves.minus(it)
                valveQueue.add(ValvesState(it, minutesLeft, pressure, newValves))
            }
        }
    }

    return possiblePressure.max()
}

private fun execute2(input: List<String>): Int {
    val valves = input.associate {
        val (name, rate, connections) = pattern.matchEntire(it)!!.destructured
        name to Valve(name, rate.toInt(), connections.split(", "))
    }

    //Do breath first search on all the valves
    val valveQueue = mutableSetOf(ValvesState2("AA", "AA",26, 26,0, valves.filter { it.value.rate > 0 }.keys, setOf()))
    val possiblePressure = mutableListOf<Int>()
    while (valveQueue.isNotEmpty()) {
        val state = valveQueue.toList().first()
        valveQueue.remove(state)
        val distanceToMe = calculateDistances(valves, state.me)
        val distanceToElephant = calculateDistances(valves, state.elephant)
        //We only have to consider valves we can travel to and open in time
        val myChildren = state.remainingValves.filter { state.myMinutes - distanceToMe[it]!! - 1 > 0 && distanceToMe[it]!! < 10 }
        val elephantChildren = state.remainingValves.filter { state.elephantMinutes - distanceToElephant[it]!! - 1 > 0 && distanceToElephant[it]!! < 10 }
        if (myChildren.isEmpty() && elephantChildren.isEmpty()) {
            //No children means we can't get anywhere else in the time left
            possiblePressure.add(state.totalPressure)
            if (possiblePressure.size % 10000 == 0)
                println("Finished: ${possiblePressure.size}")
            if (possiblePressure.size > 50000) {
                break
            }
        } else if (myChildren.isEmpty()) {
            //I'm done but the elephant is still going
            elephantChildren.forEach {
                val newMinutes = state.elephantMinutes - distanceToElephant[it]!! - 1
                val newPressure = state.totalPressure + (newMinutes * valves[it]!!.rate)
                val newValves = state.remainingValves.minus(it)
                valveQueue.add(ValvesState2(state.me, it, state.myMinutes, newMinutes, newPressure, newValves, state.visitedValves.plus(it)))
            }
        } else if (elephantChildren.isEmpty()) {
            //Elephant is done but I'm still going
            myChildren.forEach {
                val newMinutes = state.myMinutes - distanceToMe[it]!! - 1
                val newPressure = state.totalPressure + (newMinutes * valves[it]!!.rate)
                val newValves = state.remainingValves.minus(it)
                valveQueue.add(ValvesState2(it, state.elephant, newMinutes, state.elephantMinutes, newPressure, newValves, state.visitedValves.plus(it)))
            }
        } else {
            //Both still going
            myChildren.forEach {myValve ->
                elephantChildren.minus(myValve).forEach {elephantValve ->
                    val myMinutes = state.myMinutes - distanceToMe[myValve]!! - 1
                    val elephantMinutes = state.elephantMinutes - distanceToElephant[elephantValve]!! - 1
                    val pressure = state.totalPressure + (myMinutes * valves[myValve]!!.rate) + (elephantMinutes * valves[elephantValve]!!.rate)
                    val newValves = state.remainingValves.minus(setOf(myValve, elephantValve))
                    valveQueue.add(ValvesState2(myValve, elephantValve, myMinutes, elephantMinutes, pressure, newValves, state.visitedValves.plus(setOf(myValve, elephantValve))))
                }
            }
        }
    }
    println("${possiblePressure.size}")

    return possiblePressure.max()
}