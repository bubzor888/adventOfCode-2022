package day12

import java.io.File
import java.nio.file.Paths
import kotlin.test.assertEquals

fun main() {
    val path = Paths.get("").toAbsolutePath().toString()

//    val test1 = listOf("")
    val test1 = File("$path\\src\\main\\kotlin\\day12\\testInput.txt").readLines()

    assertEquals(31, execute(test1))
    assertEquals(29, execute2(test1))

    println("Tests passed, attempting input")

    val input = File("$path\\src\\main\\kotlin\\day12\\input.txt").readLines()
    //Alternative to read whole file, use .readText()

    println("Final Result 1: ${execute(input)}")
    println("Final Result 2: ${execute2(input)}")
}

private class Node(val elevation: Char, var startNodeIndex: Int = -1) {
    var distance = Integer.MAX_VALUE
    var steps = 0
    var visited = false
    val connections = mutableListOf<Node>()

    fun getWeight(): Int {
        //Convert the elevation to a weight where a=1 and z=26
        //Start (S) is 1 and End (E) is 26
        return when (elevation) {
            'S' -> 1
            'E' -> 26
            else -> elevation.code - 'a'.code + 1
        }
    }

    fun clone(): Node {
        val clone = Node(elevation)
        clone.connections.addAll(connections)
        return clone
    }
}

private fun initialize(input: List<String>): MutableList<Node> {
    val nodes = mutableMapOf<Pair<Int, Int>, Node>()
    //First just make all the nodes
    var aCount = 0
    input.forEachIndexed { y, row ->
        row.forEachIndexed { x, elevation ->
            //Let's number the 'a'/'S' for part 2
            when (elevation == 'S' || elevation == 'a') {
                true -> {
                    nodes[Pair(x,y)] = Node(elevation, aCount)
                    aCount++
                }
                false -> nodes[Pair(x,y)] = Node(elevation)
            }
        }
    }
    //Now look at weights and form connections such that you can't go up more than 1
    nodes.forEach {
        //Check up/down/left/right
        val up = nodes[Pair(it.key.first, it.key.second-1)]
        if (up != null && (up.getWeight() - 1 <= it.value.getWeight())) {
            it.value.connections.add(up)
        }
        val down = nodes[Pair(it.key.first, it.key.second+1)]
        if (down != null && (down.getWeight() - 1 <= it.value.getWeight())) {
            it.value.connections.add(down)
        }
        val left = nodes[Pair(it.key.first-1, it.key.second)]
        if (left != null && (left.getWeight() - 1 <= it.value.getWeight())) {
            it.value.connections.add(left)
        }
        val right = nodes[Pair(it.key.first+1, it.key.second)]
        if (right != null && (right.getWeight() - 1 <= it.value.getWeight())) {
            it.value.connections.add(right)
        }
    }

    return nodes.values.toMutableList()
}

private fun shortestPath(nodes: MutableList<Node>, startNode: Node): Int {
    var currentNode = startNode
    while (nodes.isNotEmpty()) {
        //If we reach the 'E' we can stop
        if (currentNode.elevation == 'E') {
            return currentNode.steps
        }

        //Recalculate distance of adjacent nodes
        currentNode.connections.forEach {
            if (!it.visited) {
                val newDistance = currentNode.distance + it.getWeight()
                if (newDistance < it.distance) {
                    it.distance = newDistance
                    it.steps = currentNode.steps+1
                }
            }
        }
        currentNode.visited = true
        nodes.remove(currentNode)

        //Find the lowest unvisted node and continue
        nodes.sortBy { it.distance }
        currentNode = nodes.first()
    }


    return -1
}

private fun execute(input: List<String>): Int {
    val nodes = initialize(input)
    //Start Dijkstra's algorithm
    //Find the start node and set its distance to zero
    val startNode = nodes.find { it.elevation == 'S' }!!
    startNode.distance = 0

    return shortestPath(nodes, startNode)
}

private fun execute2(input: List<String>): Int {
    //Lets fine all the start nodes we need to use
    return initialize(input).filter { it.startNodeIndex > -1 }.map { it.startNodeIndex }.map { startIndex ->
        //Initialize again to get fresh nodes
        val nodes = initialize(input)
        val startNode = nodes.find { startIndex == it.startNodeIndex }!!
        startNode.distance = 0
        shortestPath(nodes, startNode)
    }.min()
}