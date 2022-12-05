package day5

import java.io.File
import java.nio.file.Paths
import kotlin.test.assertEquals

val pattern = """move (\d+) from (\d+) to (\d+)""".toRegex()

fun main() {
    val path = Paths.get("").toAbsolutePath().toString()

//    val test1 = listOf("")
    val test1 = File("$path\\src\\main\\kotlin\\day5\\testInput.txt").readLines()

    assertEquals("CMZ", execute(test1))
    assertEquals("MCD", execute2(test1))

    println("Tests passed, attempting input")

    val input = File("$path\\src\\main\\kotlin\\day5\\input.txt").readLines()
    //Alternative to read whole file, use .readText()

    println("Final Result 1: ${execute(input)}")
    println("Final Result 2: ${execute2(input)}")
}

private fun parseInput(input: List<String>): Pair<List<ArrayDeque<Char>>, List<String>> {
    //Split stacks from instructions
    val (rawStacks, instructions) = input.partition { it.contains('[') }
    //This will eventually be our stacks
    val stacks = mutableListOf<ArrayDeque<Char>>()

    //Find the row with the most stacks
    val maxLength = rawStacks.map { it.length }.max()
    rawStacks.map { stack ->
        //First replace groups of 4 spaces with _
        val builder = StringBuilder(stack.replace("    ", "_"))
        //Then append _ on the end as needed
        for(i in 0 until (maxLength - stack.length) step 4) {
            builder.append("_")
        }
        //Now remove unnecessary chars
        builder.toString().replace("""([ \[\]])""".toRegex(), "")
    }.forEachIndexed { rowNum, row ->
        row.forEachIndexed{ colNum, crate ->
            //First row we need to initialize the stack
            if (rowNum == 0) {
                stacks.add(ArrayDeque())
            }

            //Now add any non _
            if (crate != '_') {
                stacks[colNum].addFirst(crate)
            }
        }
    }

    return Pair(stacks, instructions.subList(2, instructions.size))
}

private fun execute(input: List<String>): String {
    val (stacks, instructions) = parseInput(input)

    instructions.forEach { instruction ->
        val (amount, from, to) = pattern.matchEntire(instruction)!!.destructured.toList().map { it.toInt() }
        for (i in 0 until amount) {
            stacks[to-1].addLast(stacks[from-1].removeLast())
        }
    }

    return stacks.fold("") { result, element -> result + element.last() }
}

private fun execute2(input: List<String>): String {
    val (stacks, instructions) = parseInput(input)

    instructions.forEach { instruction ->
        val (amount, from, to) = pattern.matchEntire(instruction)!!.destructured.toList().map { it.toInt() }
        val moved = mutableListOf<Char>()
        for (i in 0 until amount) {
            moved.add(stacks[from-1].removeLast())
        }
        //Reverse them to keep the original order
        moved.reverse()
        moved.forEach { stacks[to-1].addLast(it) }
    }

    return stacks.fold("") { result, element -> result + element.last() }
}
