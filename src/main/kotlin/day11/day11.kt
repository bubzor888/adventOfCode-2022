package day11

import java.io.File
import java.nio.file.Paths
import kotlin.test.assertEquals

fun main() {
    val path = Paths.get("").toAbsolutePath().toString()

//    val test1 = listOf("")
    val test1 = File("$path\\src\\main\\kotlin\\day11\\testInput.txt").readLines()

    assertEquals(10605, execute(test1))
    assertEquals(2713310158L, execute2(test1))

    println("Tests passed, attempting input")

    val input = File("$path\\src\\main\\kotlin\\day11\\input.txt").readLines()
    //Alternative to read whole file, use .readText()

    println("Final Result 1: ${execute(input)}")
    println("Final Result 2: ${execute2(input)}")
}

private class Monkey(val items: MutableList<Long>, val test: Long, val trueMonkey: Int, val falseMonkey: Int, val worryOp: String, val worryChange: String) {
    var itemsInspected = 0L

    fun calcNewWorry(): Long {
        itemsInspected++
        val old = items.removeFirst()
        val change = when (worryChange) {
            "old" -> old
            else -> worryChange.toLong()
        }
        return when (worryOp) {
            "+" -> (old + change) / 3
            else -> (old * change) / 3
        }
    }

    fun calcNewWorry2(lcm: Long): Long {
        //Use least common multiple, as it maintains modulo
        itemsInspected++
        val old = items.removeFirst()
        val change = when (worryChange) {
            "old" -> old
            else -> worryChange.toLong()
        }
        return when (worryOp) {
            "+" -> (old + change) % lcm
            else -> (old * change) % lcm
        }
    }

    fun passItem(worry: Long): Int {
        return when (worry % test == 0L) {
            true -> trueMonkey
            false -> falseMonkey
        }
    }
}

private fun initialize(input: List<String>): List<Monkey> {
    val lines = input.iterator()
    val monkeys = mutableListOf<Monkey>()
    while (lines.hasNext()) {
        //Skip the blank line and monkey line
        lines.next()
        lines.next()
        val items = lines.next().substring(18).split(", ").map { it.toLong() }.toMutableList()
        val (worryOp, worryChange) = lines.next().substring(23).split(" ")
        val test = lines.next().substring(21).toLong()
        val trueMonkey = lines.next().substring(29).toInt()
        val falseMonkey = lines.next().substring(30).toInt()
        monkeys.add(Monkey(items, test, trueMonkey, falseMonkey, worryOp, worryChange))
    }
    return monkeys
}

private fun execute(input: List<String>): Long {
    val monkeys = initialize(input)

    for (round in 1..20) {
        for (monkey in monkeys.indices) {
            while(monkeys[monkey].items.isNotEmpty()) {
                val newWorry = monkeys[monkey].calcNewWorry()
                val newMonkey = monkeys[monkey].passItem(newWorry)
                monkeys[newMonkey].items.add(newWorry)
            }
        }
    }

    val itemsInspected = monkeys.map { it.itemsInspected }.sortedDescending()

    return itemsInspected[0] * itemsInspected[1]
}

private fun execute2(input: List<String>): Long {
    val monkeys = initialize(input)

    //Find least common multiple
    var lcm = monkeys.map { it.test }.fold(1L) { lcm, test -> lcm * test }

    for (round in 1..10000) {
        for (monkey in monkeys.indices) {
            while(monkeys[monkey].items.isNotEmpty()) {
                val newWorry = monkeys[monkey].calcNewWorry2(lcm)
                val newMonkey = monkeys[monkey].passItem(newWorry)
                monkeys[newMonkey].items.add(newWorry)
            }
        }
    }

    val itemsInspected = monkeys.map { it.itemsInspected }.sortedDescending()

    return itemsInspected[0] * itemsInspected[1]
}