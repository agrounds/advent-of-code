package advent.y2022.d07

sealed class Command
data class Cd(val arg: String) : Command()
data class Ls(val outputDirs: List<String>, val outputFiles: Map<String, Int>) : Command()

fun parseCommands(lines: Sequence<String>): List<Command> {
    val ret = mutableListOf<Command>()
    // in progress ls command data
    var lsOutputDirs: MutableList<String>? = null
    var lsOutputFiles: MutableMap<String, Int>? = null

    fun saveLsCommand() {
        if (lsOutputDirs != null || lsOutputFiles != null) {
            ret.add(Ls(lsOutputDirs ?: emptyList(), lsOutputFiles ?: emptyMap()))
        }
        lsOutputDirs = null
        lsOutputFiles = null
    }

    lines.forEach { line ->
        // when we see a command, check if there is ls output data to save off
        // this assumes that every ls command has some output
        if (line.startsWith("$")) {
            saveLsCommand()
        }
        when {
            // "$ ls" commands are ignored, because they are inferred from the file/directory data
            line.startsWith("$ cd ") ->
                ret.add(Cd(line.split(" ").last()))
            line.startsWith("dir ") -> {
                lsOutputDirs = (lsOutputDirs ?: mutableListOf()).apply {
                    add(line.split(" ").last())
                }
            }
            line.first() in "123456789" -> {
                lsOutputFiles = (lsOutputFiles ?: mutableMapOf()).apply {
                    val parts = line.split(" ")
                    this[parts.last()] = parts.first().toInt()
                }
            }
        }
    }
    saveLsCommand()
    return ret
}
