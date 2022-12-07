package day7

import java.io.File
import java.nio.file.Paths
import kotlin.test.assertEquals

fun main() {
    val path = Paths.get("").toAbsolutePath().toString()

//    val test1 = listOf("")
    val test1 = File("$path\\src\\main\\kotlin\\day7\\testInput.txt").readLines()

    assertEquals(95437, execute(test1))
    assertEquals(24933642, execute2(test1))

    println("Tests passed, attempting input")

    val input = File("$path\\src\\main\\kotlin\\day7\\input.txt").readLines()
    //Alternative to read whole file, use .readText()

    println("Final Result 1: ${execute(input)}")
    println("Final Result 2: ${execute2(input)}")
}

open class Item (val name: String) {
    open fun calculateSize(): Long {
        return 0L
    }
}

class FileItem(name: String, private val size: Long) : Item(name) {
    override fun calculateSize(): Long {
        return size
    }
}

class DirectoryItem(name: String, val parent: Item) : Item(name) {
    private val children = mutableSetOf<Item>()
    private var size = -1L

    override fun calculateSize(): Long {
        //Only calculate the size once
        if (size == -1L) {
            size = children.sumOf { it.calculateSize() }
        }
        return size
    }

    fun addChild(newChild: Item) {
        children.add(newChild)
    }

    fun findChild(child: String): Item? {
        return children.find { it.name == child }
    }
}

private fun initialize(input: List<String>): List<DirectoryItem> {
    //Make a generic item as the root's parent
    val root = DirectoryItem("/", Item("/"))
    var currentDir = root

    //Keep a list of directories for easy iterating later
    val directories = mutableListOf(root)

    var lineNum = 1
    while (lineNum < input.size) {
        val commandLine = input[lineNum].split(" ")
        lineNum++
        when (commandLine[1]) {
            "cd" -> {
                currentDir = when (commandLine[2]) {
                    ".." -> currentDir.parent as DirectoryItem
                    else -> currentDir.findChild(commandLine[2]) as DirectoryItem
                }
            }
            "ls" ->
                while (lineNum < input.size && !input[lineNum].startsWith('$')) {
                    val fileLine = input[lineNum].split(" ")
                    when (fileLine[0] == "dir") {
                        true -> {
                            val newDir = DirectoryItem(fileLine[1], currentDir)
                            currentDir.addChild(newDir)
                            directories.add(newDir)
                        }
                        false -> currentDir.addChild(FileItem(fileLine[1], fileLine[0].toLong()))
                    }
                    lineNum++
                }
        }
    }

    return directories
}

private fun execute(input: List<String>): Long {
    return initialize(input).map{ it.calculateSize() }.filter{ it <= 100000L }.sumOf{ it }
}

private fun execute2(input: List<String>): Long {
    val directories = initialize(input)
    val neededSpace = 30000000 - (70000000L - directories[0].calculateSize())

    return directories.map{ it.calculateSize() }.filter{ it >= neededSpace }.min()
}