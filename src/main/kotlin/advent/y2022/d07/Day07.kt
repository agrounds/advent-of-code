package advent.y2022.d07

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

sealed class File {
    abstract val name: String
}
data class RegularFile(override val name: String, val size: Int) : File() {
    override fun toString(): String = "RegularFile($name, $size)"
}
data class Directory(override val name: String, var entries: List<File>,
                     val parent: Directory?, var totalSize: Int? = null) : File() {
    override fun toString(): String = "Directory($name)"
}

fun buildFileTree(commands: List<Command>): Directory {
    val root = Directory("/", emptyList(), null)
    var workingDir: Directory = root

    commands.forEach { command ->
        when (command) {
            is Cd -> {
                workingDir = when (command.arg) {
                    ".." -> workingDir.parent!!
                    "/" -> root
                    else ->
                        workingDir.entries.filterIsInstance<Directory>().first { it.name == command.arg }
                }
            }

            is Ls -> {
                workingDir.entries = command.outputFiles.mapTo(mutableListOf()) { (name, size) ->
                    RegularFile(name, size)
                } + command.outputDirs.map { Directory(it, emptyList(), workingDir) }
            }
        }
    }

    return root
}

// side effect: fills in the totalSize attribute of all directories in the file tree
fun sumDirectorySizes(fileTree: Directory, maxSize: Int): Int =
    fileTree.entries.filterIsInstance<Directory>()
        .sumOf { sumDirectorySizes(it, maxSize) }
        .let {
            fileTree.totalSize = fileTree.entries.sumOf { entry ->
                when (entry) {
                    is RegularFile -> entry.size
                    is Directory -> entry.totalSize ?: 0
                }
            }
            if (fileTree.totalSize!! <= maxSize)
                it + fileTree.totalSize!!
            else
                it
        }

// assumes totalSizes are already populated
fun findDirectoryToDelete(fileTree: Directory, spaceToFree: Int): Int? =
    fileTree.entries.asSequence()
        .filterIsInstance<Directory>()
        .map { findDirectoryToDelete(it, spaceToFree) }
        .plus(fileTree.totalSize)
        .filter { it != null && it >= spaceToFree }
        .filterNotNull()
        .minOrNull()




fun main() {
    val fileTree = (DATAPATH / "2022/day07.txt").useLines { lines ->
        parseCommands(lines)
    }
        .let(::buildFileTree)
    sumDirectorySizes(fileTree, 100_000)
        .also { println("Part one: $it" )}
    val spaceRequred = 30_000_000
    val totalDisk = 70_000_000
    val spaceToFree = spaceRequred - (totalDisk - fileTree.totalSize!!)
    findDirectoryToDelete(fileTree, spaceToFree)
        .also { println("Part two: $it") }
}
