package com.groundsfam.advent.y2023.d25

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import java.util.PriorityQueue
import kotlin.io.path.div
import kotlin.io.path.useLines

fun cutWires(neighborMap: Map<String, Set<String>>): Long {
    fun shortestPath(from: String, to: String): List<String> {
        val queue = PriorityQueue<List<String>>( compareBy { it.size } )
        val visited = mutableSetOf<String>()
        queue.add(listOf(from))

        while (queue.isNotEmpty()) {
            val path = queue.poll()
            val node = path.last()
            if (node == to) return path
            if (!visited.add(node)) continue

            neighborMap[node]!!.forEach { nextNode ->
                if (nextNode !in visited) {
                    queue.add(path + nextNode)
                }
            }
        }
        throw RuntimeException("No path between $from and $to")
    }

    // heuristic: the three wires to cut are the ones most likely to be in the shortest path
    // between two random nodes
    // if we're unlucky and get the wrong wires, ret will remain zero and the loop will rerun
    val nodes = neighborMap.keys
    var ret = 0L
    while (ret == 0L) {
        val edgeCounts = mutableMapOf<Pair<String, String>, Int>()
        repeat(100) {
            val path = shortestPath(nodes.random(), nodes.random())
            (0 until path.size - 2).forEach { i ->
                val (a, b) = listOf(path[i], path[i + 1]).sorted()
                edgeCounts[a to b] = (edgeCounts[a to b] ?: 0) + 1
            }
        }
        val cutWires = edgeCounts.entries
            .sortedByDescending { it.value }
            .take(3)
            .mapTo(mutableSetOf()) { it.key }

        val connectedComponent = mutableSetOf<String>()
        val queue = ArrayDeque<String>()
        queue.add(nodes.first())
        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            connectedComponent.add(node)
            neighborMap[node]!!.forEach { n ->
                if (n !in connectedComponent && Pair(node, n) !in cutWires && Pair(n, node) !in cutWires) {
                    queue.add(n)
                }
            }
        }

        val componentSize = connectedComponent.size
        ret = componentSize.toLong() * (nodes.size - componentSize)
    }
    return ret
}

fun main() = timed {
    val neighborMap: Map<String, Set<String>> = (DATAPATH / "2023/day25.txt").useLines { lines ->
        val neighbors = mutableMapOf<String, MutableSet<String>>()
        lines.forEach { line ->
            val parts = line.split(":? ".toRegex())
            val from = parts[0]
            (1 until parts.size).forEach { i ->
                val to = parts[i]
                listOf(Pair(from, to), Pair(to, from)).forEach { (a, b) ->
                    val set = neighbors[a] ?: mutableSetOf<String>().also { neighbors[a] = it }
                    set.add(b)
                }
            }
        }
        neighbors
    }
    println("Part one: ${cutWires(neighborMap)}")
}
