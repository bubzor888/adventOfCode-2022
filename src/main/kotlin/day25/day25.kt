package day25

import java.io.File
import java.nio.file.Paths
import kotlin.math.pow
import kotlin.test.assertEquals

fun main() {
    val path = Paths.get("").toAbsolutePath().toString()

//    val test1 = listOf("")
    val test1 = File("$path\\src\\main\\kotlin\\day25\\testInput.txt").readLines()

    assertEquals("1",decimalToSnafu(1))
    assertEquals("2",decimalToSnafu(2))
    assertEquals("1=",decimalToSnafu(3))
    assertEquals("1-",decimalToSnafu(4))
    assertEquals("10",decimalToSnafu(5))
    assertEquals("11",decimalToSnafu(6))
    assertEquals("12",decimalToSnafu(7))
    assertEquals("2=",decimalToSnafu(8))
    assertEquals("2-",decimalToSnafu(9))
    assertEquals("20",decimalToSnafu(10))
    assertEquals("1=0",decimalToSnafu(15))
    assertEquals("1-0",decimalToSnafu(20))
    assertEquals("1=11-2",decimalToSnafu(2022))
    assertEquals("1121-1110-1=0",decimalToSnafu(314159265))

    assertEquals("2=-1=0", execute(test1))

    println("Tests passed, attempting input")

    val input = File("$path\\src\\main\\kotlin\\day25\\input.txt").readLines()
    //Alternative to read whole file, use .readText()

    println("Final Result 1: ${execute(input)}")
}

private fun longPow(base: Long, exponent: Int): Long {
    var result = 1L
    for (i in 1..exponent) {
        result *= base
    }
    return result
}
private fun snafuToDecimal(snafu: String): Long {
    var num = 0L
    snafu.reversed().forEachIndexed{ i, ch ->
        num += when (ch) {
            '2' -> 2 * longPow(5, i)
            '1' -> longPow(5, i)
            '-' -> -1 * longPow(5, i)
            '=' -> -2 * longPow(5, i)
            else -> 0
        }
    }
    println("$snafu -> $num")
    return num
}

private fun decimalToSnafu(num: Long): String {
    println("converting $num")
    var result = ""
    //Find the upper limit power wise
    var p = 0
    var max = 2L
    while (num > max) {
        p++
        max += 2 * longPow(5, p)
    }

    var remaining = num
    while (p >= 0) {
        val currentPow = longPow(5, p)
        val nextPow = if (p-1 > 0) longPow(5, p-1) else 0L
        if (remaining > max - currentPow) {
            result += "2"
            remaining -= 2 * currentPow
        } else if (remaining < -1 * (max - currentPow)) {
            result += "="
            remaining += 2 * currentPow
        } else if (remaining > max - (2 * currentPow)) {
            result += "1"
            remaining -= currentPow
        } else if (remaining < -1 * (max - (2 * currentPow))) {
            result += "-"
            remaining += currentPow
        } else {
            result += "0"
            //No change to remaining
        }

        max -= 2 * currentPow
        p--
    }

    return result
}

private fun execute(input: List<String>): String {
    return decimalToSnafu(input.sumOf { snafuToDecimal(it) })
}