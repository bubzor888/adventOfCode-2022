package day13

import kotlinx.serialization.json.*
import java.io.File
import java.nio.file.Paths
import kotlin.test.assertEquals

fun main() {
    val path = Paths.get("").toAbsolutePath().toString()

//    val test1 = listOf("")
    val test1 = File("$path\\src\\main\\kotlin\\day13\\testInput.txt").readLines()

    assertEquals(13, execute(test1))
    assertEquals(140, execute2(test1))

    println("Tests passed, attempting input")

    val input = File("$path\\src\\main\\kotlin\\day13\\input.txt").readLines()
    //Alternative to read whole file, use .readText()

    println("Final Result 1: ${execute(input)}")
    println("Final Result 2: ${execute2(input)}")
}

private fun compareValues(left: JsonElement?, right: JsonElement?): Int {
    //Return 1 for valid, -1 for invalid, and 0 for need to compare next
    return when {
        left == null && right == null -> 0
        left == null -> 1
        right == null -> -1
        left is JsonPrimitive && right is JsonPrimitive -> {
            //Both integers, compare
            when {
                left.int < right.int -> 1
                left.int > right.int -> -1
                else -> 0
            }
        }
        left is JsonArray && right is JsonArray -> {
            var i = 0
            var result: Int
            do {
                result = compareValues(left.getOrNull(i), right.getOrNull(i))
                i++
            } while (result == 0 && i < left.size && i < right.size)

            if (result == 0) {
                result = when {
                    left.size < right.size -> 1
                    left.size > right.size -> -1
                    else -> 0
                }
            }

            result
        }
        left is JsonPrimitive && right is JsonArray -> compareValues(JsonArray(listOf(left)), right)
        left is JsonArray && right is JsonPrimitive -> compareValues(left, JsonArray(listOf(right)))
        else -> 0
    }
}

private fun execute(input: List<String>): Int {
    val iterator = input.iterator()
    val correctIndexes = mutableListOf<Int>()
    var index = 1
    while (iterator.hasNext()) {
        //Skip first blank line
        iterator.next()
        val result = compareValues(Json.parseToJsonElement(iterator.next()), Json.parseToJsonElement(iterator.next()))
        if (result == 1) {
            correctIndexes.add(index)
        }
        index++
    }
//    println(compareValues(Json.parseToJsonElement("[[4,4],4,4]"), Json.parseToJsonElement("[[4,4],4,4,4]")))

    return correctIndexes.sum()
}

private fun execute2(input: List<String>): Int {
    //First remove blank lines and add the extra packets
    val rawData = mutableListOf<String>()
    rawData.addAll(input.filter { it.isNotEmpty() })
    rawData.add("[[2]]")
    rawData.add("[[6]]")

    val packets = rawData.map { Json.parseToJsonElement(it) }.sortedWith { left, right ->
        -1 * compareValues(
            left,
            right
        )
    }

    var decoderKey = 0
    packets.forEachIndexed{ index, jsonElement ->
        if (jsonElement.toString() == "[[2]]") {
            decoderKey = index + 1
        } else if (jsonElement.toString() == "[[6]]") {
            decoderKey *= (index + 1)
        }
    }

    return decoderKey
}