package day21

import java.io.File
import java.nio.file.Paths
import kotlin.test.assertEquals

fun main() {
    val path = Paths.get("").toAbsolutePath().toString()

//    val test1 = listOf("")
    val test1 = File("$path\\src\\main\\kotlin\\day21\\testInput.txt").readLines()

    assertEquals(152, execute(test1))
    assertEquals(301, execute2(test1))

    println("Tests passed, attempting input")

    val input = File("$path\\src\\main\\kotlin\\day21\\input.txt").readLines()
    //Alternative to read whole file, use .readText()

    println("Final Result 1: ${execute(input)}")
    println("Final Result 2: ${execute2(input)}")
}

private abstract class Monkey(val name: String) {
    abstract fun isNumberReady(): Boolean
    abstract fun getNumber(): Long
}

private class ReadyMonkey(name: String, val value: Long) : Monkey(name) {
    override fun isNumberReady(): Boolean {
        return true
    }

    override fun getNumber(): Long {
        return value
    }
}

private class WaitingMonkey(name: String, val m1Name: String, val m2Name: String, val op: String) : Monkey(name) {
    var queued = false
    val dependentMonkeys = mutableListOf<String>()
    var m1Val : Long? = null
    var m2Val : Long? = null

    override fun isNumberReady(): Boolean {
        return m1Val != null && m2Val != null
    }

    override fun getNumber(): Long {
        //Only call after checking isValueReady()
        return when (op) {
            "+" -> m1Val!! + m2Val!!
            "-" -> m1Val!! - m2Val!!
            "*" -> m1Val!! * m2Val!!
            else -> m1Val!! / m2Val!!
        }
    }
}

private fun calculateMonkeyNumber(monkeys: Map<String, Monkey>, startMonkey: String): Long {
    //We only need to queue waiting monkeys
    val monkeyQueue = mutableListOf(monkeys[startMonkey]!! as WaitingMonkey)
    while (monkeyQueue.isNotEmpty()) {
        val monkey = monkeyQueue.removeFirst()

        if (monkey.isNumberReady()) {
            continue
        }

        val monkey1 = monkeys[monkey.m1Name]!!
        if (monkey1.isNumberReady()) {
            monkey.m1Val = monkey1.getNumber()
        } else {
            monkey1 as WaitingMonkey
            if (!monkey1.queued) {
                monkeyQueue.add(monkey1)
                monkey1.queued = true
            }
            monkey1.dependentMonkeys.add(monkey.name)
        }

        val monkey2 = monkeys[monkey.m2Name]!!
        if (monkey2.isNumberReady()) {
            monkey.m2Val = monkey2.getNumber()
        } else {
            monkey2 as WaitingMonkey
            if (!monkey2.queued) {
                monkeyQueue.add(monkey2)
                monkey2.queued = true
            }
            monkey2.dependentMonkeys.add(monkey.name)
        }

        if (monkey.isNumberReady()) {
            //Re-queue the monkeys waiting
            monkeyQueue.addAll(monkey.dependentMonkeys.map { monkeys[it]!! as WaitingMonkey })
        }
    }

    return monkeys[startMonkey]!!.getNumber()
}

private fun execute(input: List<String>): Long {

    val monkeys = input.associate {
        val (name, yell) = it.split(": ")
        when (yell.length > 3) {
            true -> {
                val (monkey1, op, monkey2) = yell.split(" ")
                name to WaitingMonkey(name, monkey1, monkey2, op)
            }
            false -> name to ReadyMonkey(name, yell.toLong())
        }
    }

    return calculateMonkeyNumber(monkeys, "root")
}

private fun containsHumn(monkeys: Map<String, Monkey>, startMonkey: String): Boolean {
    if (startMonkey == "humn") {
        return true
    }

    val result = mutableSetOf<String>()
    val queue = mutableListOf(monkeys[startMonkey])
    while (queue.isNotEmpty()) {
        val monkey = queue.removeFirst()
        if (monkey is WaitingMonkey) {
            if (!result.contains(monkey.m1Name)) {
                result.add(monkey.m1Name)
                queue.add(monkeys[monkey.m1Name]!!)
            }
            if (!result.contains(monkey.m2Name)) {
                result.add(monkey.m2Name)
                queue.add(monkeys[monkey.m2Name]!!)
            }
        }
    }
    return result.contains("humn")
}

private fun calcInReverse(monkeys: Map<String, Monkey>, monkey: String, answer: Long): Long {
    val currentMonkey = (monkeys[monkey] as WaitingMonkey)

    //Solving the equation will change for '-' and '/' depending on if "humn" is in the first or second group
    if (containsHumn(monkeys, currentMonkey.m1Name)) {
        //Since second group doesn't have "humn" we can calculate that normally
        val known = when (monkeys[currentMonkey.m2Name]!!.isNumberReady()) {
            true -> monkeys[currentMonkey.m2Name]!!.getNumber()
            false -> calculateMonkeyNumber(monkeys, currentMonkey.m2Name)
        }
        val nextAnswer = when (currentMonkey.op) {
            "+" -> answer - known
            "-" -> answer + known
            "*" -> answer / known
            else -> answer * known
        }

        return if (currentMonkey.m1Name == "humn") {
            nextAnswer
        } else {
            calcInReverse(monkeys, currentMonkey.m1Name, nextAnswer)
        }
    }

    val known = when (monkeys[currentMonkey.m1Name]!!.isNumberReady()) {
        true -> monkeys[currentMonkey.m1Name]!!.getNumber()
        false -> calculateMonkeyNumber(monkeys, currentMonkey.m1Name)
    }
    val nextAnswer = when (currentMonkey.op) {
        "+" -> answer - known
        "-" -> known - answer //Different since unknown is what's being subtracted
        "*" -> answer / known
        else -> known / answer //Different since unknown is the numerator
    }

    return if (currentMonkey.m2Name == "humn") {
        nextAnswer
    } else {
        calcInReverse(monkeys, currentMonkey.m2Name, nextAnswer)
    }
}

private fun execute2(input: List<String>): Long {
    val monkeys = input.associate {
        val (name, yell) = it.split(": ")
        when (yell.length > 3) {
            true -> {
                val (monkey1, op, monkey2) = yell.split(" ")
                name to WaitingMonkey(name, monkey1, monkey2, op)
            }
            false -> name to ReadyMonkey(name, yell.toLong())
        }
    }

    //First split the monkeys into 2 groups: the WaitingMonkeys for each side of root
    val group1 = (monkeys["root"] as WaitingMonkey).m1Name
    val group2 = (monkeys["root"] as WaitingMonkey).m2Name

    //See which group includes "humn" and calculate the other group first
    return if (containsHumn(monkeys, group1)) {
        calcInReverse(monkeys, group1, calculateMonkeyNumber(monkeys, group2))
    } else {
        calcInReverse(monkeys, group2, calculateMonkeyNumber(monkeys, group1))
    }
}