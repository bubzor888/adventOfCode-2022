package day16

import java.io.File
import java.nio.file.Paths
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

    println("Final Result 1: ${execute(input)}")
//    println("Final Result 2: ${execute2(input)}")
}

private val pattern = """Valve (\w\w) has flow rate=(\d+); tunnels? leads? to valves? ([\w, ]+)""".toRegex()

private class Valve(val name: String, val rate: Int, val connections: List<String>)

private class ValvesState(val valve: String, val minutesLeft: Int, val totalPressure: Int, val remainingValves: Set<String>)

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
    val valveQueue = mutableListOf(ValvesState("AA",30, 0, valves.filter { it.value.rate > 0 }.keys))
    val possiblePaths = mutableListOf<ValvesState>()
    while (valveQueue.isNotEmpty()) {
        val state = valveQueue.removeFirst()
        val distances = calculateDistances(valves, state.valve)
        //We only have to consider valves we can travel to and open in time
        val children = state.remainingValves.filter { state.minutesLeft - distances[it]!! - 1 > 0}
        if (children.isEmpty()) {
            //No children means we reached 30 minutes
            //TODO can i figure out if a similar state is already in the queue?
            possiblePaths.add(state)
        } else {
            children.forEach {
                val minutesLeft = state.minutesLeft - distances[it]!! - 1
                val pressure = state.totalPressure + (minutesLeft * valves[it]!!.rate)
                val newValves = state.remainingValves.toMutableSet()
                newValves.remove(it)
                valveQueue.add(ValvesState(it, minutesLeft, pressure, newValves))
            }
        }
    }

    return possiblePaths.maxOf { it.totalPressure }
}

private fun execute2(input: List<String>): Int {
    return 0
}