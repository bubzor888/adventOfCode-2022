package day2

import java.io.File
import java.nio.file.Paths
import kotlin.test.assertEquals

fun main() {
    val path = Paths.get("").toAbsolutePath().toString()

//    val test1 = listOf("")
    val test1 = File("$path\\src\\main\\kotlin\\day2\\testInput.txt").readLines()

    assertEquals(15, execute(test1))
    assertEquals(12, execute2(test1))

    println("Tests passed, attempting input")

    val input = File("$path\\src\\main\\kotlin\\day2\\input.txt").readLines()
    //Alternative to read whole file, use .readText()

    println("Final Result 1: ${execute(input)}")
    println("Final Result 2: ${execute2(input)}")
}

private fun scoreGame(them: String, me: String): Int {
    return when {
        them == "A" && me == "X" -> 3
        them == "A" && me == "Y" -> 6
        them == "A" && me == "Z" -> 0
        them == "B" && me == "X" -> 0
        them == "B" && me == "Y" -> 3
        them == "B" && me == "Z" -> 6
        them == "C" && me == "X" -> 6
        them == "C" && me == "Y" -> 0
        them == "C" && me == "Z" -> 3
        else -> 0
    }
}

private fun scorePlay(me:String): Int {
    return when(me) {
        "X" -> 1
        "Y" -> 2
        "Z" -> 3
        else -> 0
    }
}

private fun findPlay(them: String, result: String): String {
    return when {
        them == "A" && result == "X" -> "Z"
        them == "A" && result == "Y" -> "X"
        them == "A" && result == "Z" -> "Y"
        them == "B" && result == "X" -> "X"
        them == "B" && result == "Y" -> "Y"
        them == "B" && result == "Z" -> "Z"
        them == "C" && result == "X" -> "Y"
        them == "C" && result == "Y" -> "Z"
        them == "C" && result == "Z" -> "X"
        else -> ""
    }
}

private fun execute(input: List<String>): Int {
    val pattern = """(\w) (\w)""".toRegex()
    return input.sumOf {
        val (them, me) = pattern.matchEntire(it)!!.destructured
        scoreGame(them, me) + scorePlay(me)
    }
}

private fun execute2(input: List<String>): Int {
    val pattern = """(\w) (\w)""".toRegex()
    return input.sumOf {
        val (them, result) = pattern.matchEntire(it)!!.destructured
        val me = findPlay(them, result)
        scoreGame(them, me) + scorePlay(me)
    }
}