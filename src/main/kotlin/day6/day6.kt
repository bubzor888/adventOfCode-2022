package day6

import java.io.File
import java.nio.file.Paths
import kotlin.test.assertEquals

fun main() {
    val path = Paths.get("").toAbsolutePath().toString()

    assertEquals(7, execute("mjqjpqmgbljsphdztnvjfqwrcgsmlb"))
    assertEquals(5, execute("bvwbjplbgvbhsrlpgdmjqwftvncz"))
    assertEquals(6, execute("nppdvjthqldpwncqszvftbrmjlhg"))
    assertEquals(10, execute("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg"))
    assertEquals(11, execute("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw"))

    assertEquals(19, execute2("mjqjpqmgbljsphdztnvjfqwrcgsmlb"))
    assertEquals(23, execute2("bvwbjplbgvbhsrlpgdmjqwftvncz"))
    assertEquals(23, execute2("nppdvjthqldpwncqszvftbrmjlhg"))
    assertEquals(29, execute2("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg"))
    assertEquals(26, execute2("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw"))

    println("Tests passed, attempting input")

    val input = File("$path\\src\\main\\kotlin\\day6\\input.txt").readText()

    println("Final Result 1: ${execute(input)}")
    println("Final Result 2: ${execute2(input)}")
}

private fun execute(input: String): Int {
    val marker = ArrayDeque<Char>()
    input.forEachIndexed { i, ch ->
        marker.addLast(ch)
        if (marker.size > 4) {
            marker.removeFirst()
        }

        //If we convert to a set and the size doesn't change, we're good
        if (marker.size == 4 && marker.toSet().size == 4) {
            return i+1
        }

    }
    return -1
}

private fun execute2(input: String): Int {
    val marker = ArrayDeque<Char>()
    input.forEachIndexed { i, ch ->
        marker.addLast(ch)
        if (marker.size > 14) {
            marker.removeFirst()
        }

        //If we convert to a set and the size doesn't change, we're good
        if (marker.size == 14 && marker.toSet().size == 14) {
            return i+1
        }

    }
    return -1
}