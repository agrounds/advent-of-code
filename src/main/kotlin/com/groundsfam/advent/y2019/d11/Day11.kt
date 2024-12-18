package com.groundsfam.advent.y2019.d11

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.Direction
import com.groundsfam.advent.points.*
import com.groundsfam.advent.timed
import com.groundsfam.advent.y2019.IntCodeComputer
import com.groundsfam.advent.y2019.IntCodeState
import com.groundsfam.advent.y2019.readProgram
import kotlin.io.path.div

class PaintingRobot(program: List<Long>) {
    private val panel = mutableMapOf<Point, Long>()
    private val painted = mutableSetOf<Point>()

    private var pos = Point(0, 0)
    private var dir = Direction.UP
    private val computer = IntCodeComputer(program)

    val numPainted
        get() = painted.size

    fun reset() {
        panel.clear()
        painted.clear()
        pos = Point(0, 0)
        dir = Direction.UP
        computer.reset()
    }

    fun run(startOnWhite: Boolean) {
        panel[pos] = if (startOnWhite) 1 else 0

        while (computer.state != IntCodeState.FINISHED) {
            computer.sendInput(panel[pos] ?: 0)
            computer.runProgram()
            val (color, turn) = computer.getAllOutput().also {
                if (it.size != 2) throw RuntimeException("Invalid output: $it")
            }
            panel[pos] = color
            painted.add(pos)
            dir = if (turn == 0L) dir.ccw else dir.cw
            pos = pos.go(dir)
        }
    }

    fun panelString(): String {
        val xs = panel.keys.let { points ->
            points.minOf { it.x }..points.maxOf { it.x }
        }
        val ys = panel.keys.let { points ->
            points.minOf { it.y }..points.maxOf { it.y }
        }
        return ys.joinToString("\n") { y ->
            xs.joinToString("") { x ->
                if (panel[Point(x, y)] == 1L) "#"
                else "."
            }
        }
    }
}

fun main() = timed {
    val robot = (DATAPATH / "2019/day11.txt").readProgram()
        .let(::PaintingRobot)
    robot.run(false)
    println("Part one: ${robot.numPainted}")
    robot.reset()
    robot.run(true)
    println("Part two:")
    println(robot.panelString())
}
