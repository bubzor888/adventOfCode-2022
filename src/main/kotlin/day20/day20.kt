package day20

import java.io.File
import java.nio.file.Paths
import kotlin.math.abs
import kotlin.test.assertEquals

fun main() {
    val path = Paths.get("").toAbsolutePath().toString()

//    val test1 = listOf("")
    val test1 = File("$path\\src\\main\\kotlin\\day20\\testInput.txt").readLines()

    assertEquals(3, execute(test1))
    assertEquals(1623178306, execute2(test1))

    println("Tests passed, attempting input")

    val input = File("$path\\src\\main\\kotlin\\day20\\input.txt").readLines()
    //Alternative to read whole file, use .readText()

    println("Final Result 1: ${execute(input)}")
    println("Final Result 2: ${execute2(input)}")
}

private class Node(val value: Long, val listSize: Int) {
    var prev : Node? = null
    var next : Node? = null

    fun move() {
        if (value > 0) {
            moveForward()
        } else if (value < 0) {
            moveBackward()
        }
        //Zero doesn't need to move
    }

    private fun moveForward() {
        for (i in 1..value % (listSize-1)) {
            //Figure out my refs but don't set them yet
            val myPrev = next
            val myNext = next?.next
            //Fix the refs of the nodes around where I'm going
            next?.prev = prev
            prev?.next = next
            next?.next?.prev = this
            next?.next = this

            //Now set mine
            prev = myPrev
            next = myNext
        }
    }

    private fun moveBackward() {
        for (i in 1..abs(value) % (listSize-1)) {
            //Figure out my refs but don't set them yet
            val myPrev = prev?.prev
            val myNext = prev
            //Fix the refs of the nodes around where I'm going
            prev?.next = next
            next?.prev = prev
            prev?.prev?.next = this
            prev?.prev = this
            //Now set mine
            prev = myPrev
            next = myNext
        }
    }
}

private fun printState(first: Node, count: Int) {
    var currentNode = first
    for (i in 1..count) {
//        print("${it.prev?.value} <- ${it.value} -> ${it.next?.value}, ")
        print("${currentNode.value}  ")
        currentNode = currentNode.next!!
    }
    println()
}

private fun execute(input: List<String>): Long {
    val moveQueue = input.map {
        Node(it.toLong(), input.size)
    }

    //Now populate the prev/next values
    for (i in moveQueue.indices) {
        if (i == 0) {
            moveQueue.last().next = moveQueue.first()
            moveQueue.first().prev = moveQueue.last()
        } else {
            moveQueue[i - 1].next = moveQueue[i]
            moveQueue[i].prev = moveQueue[i - 1]
        }
    }

    //Execute the moves
    moveQueue.forEach {
        it.move()
    }

    var currentNode = moveQueue.find { it.value == 0L }
    var sum = 0L
    for (i in 1..3000) {
        currentNode = currentNode?.next
        when (i) {
            1000 -> sum += currentNode!!.value
            2000 -> sum += currentNode!!.value
            3000 -> sum += currentNode!!.value
        }
    }

    return sum
}

private fun execute2(input: List<String>): Long {
    val moveQueue = input.map {
        Node(it.toLong() * 811589153, input.size)
    }

    //Now populate the prev/next values
    for (i in moveQueue.indices) {
        if (i == 0) {
            moveQueue.last().next = moveQueue.first()
            moveQueue.first().prev = moveQueue.last()
        } else {
            moveQueue[i - 1].next = moveQueue[i]
            moveQueue[i].prev = moveQueue[i - 1]
        }
    }

    //Execute the moves
    for (i in 1..10) {
        moveQueue.forEach {
            it.move()
        }
    }

    var currentNode = moveQueue.find { it.value == 0L }
    var sum = 0L
    for (i in 1..3000) {
        currentNode = currentNode?.next
        when (i) {
            1000 -> sum += currentNode!!.value
            2000 -> sum += currentNode!!.value
            3000 -> sum += currentNode!!.value
        }
    }

    return sum
}