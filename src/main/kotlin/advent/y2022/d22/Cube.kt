package advent.y2022.d22

import advent.Point

// HELPFUL FACT: Cubes are orientable! Therefore adjacent edges will _always_
// be in "opposition" when all faces are clockwise-oriented in the original map.
// No need to store orientation of edges!

// edges order:
//   0 <-> right
//   1 <-> bottom
//   2 <-> left
//   3 <-> top
data class Edge(val adjFace: Int, val adjFaceEdge: Int)
data class Face(val upperLeft: Point, val edges: List<Edge>)

private data class MutableFace(val id: Int, val upperLeft: Point, val edges: MutableList<Edge?>) {
    fun toFace() = Face(upperLeft, edges.filterNotNull())
}


fun parseCube(map: List<String>): List<Face> {
    val faces = findFaces(map)

    // kick off process with faces that are actually adjacent on the given map
    faces.forEach { face ->
        val (x, y) = face.upperLeft
        val adjPoints = map.sideLen.let { sl ->
            listOf(Point(x + sl, y), Point(x, y + sl), Point(x - sl, y), Point(x, y - sl))
        }

        adjPoints.forEachIndexed { i, adjPoint ->
            faces.indexOfFirst { it.upperLeft == adjPoint }.let {
                if (it != -1) face.edges[i] = Edge(it, (i + 2).mod(4))
            }
        }
    }

    fun Int.prev() = (this - 1).mod(4)
    fun Int.next() = (this + 1).mod(4)

    // fill in edges
    while (faces.any { face -> face.edges.any { it == null } }) {
        faces.forEach { face ->
            val frozenEdges = face.edges.toList()  // we will be modifying face.edges, so loop over frozen copy
            frozenEdges.forEachIndexed { edgeNum, edge ->
                if (edge != null) {
                    // try to use adjacent face to fill in missing edges
                    if (face.edges[edgeNum.prev()] == null) {
                        faces[edge.adjFace].edges[edge.adjFaceEdge.next()]?.let { adjNextEdge ->
                            face.edges[edgeNum.prev()] = Edge(adjNextEdge.adjFace, adjNextEdge.adjFaceEdge.next())
                        }
                    }
                    if (face.edges[edgeNum.next()] == null) {
                        faces[edge.adjFace].edges[edge.adjFaceEdge.prev()]?.let { adjPrevEdge ->
                            face.edges[edgeNum.next()] = Edge(adjPrevEdge.adjFace, adjPrevEdge.adjFaceEdge.prev())
                        }
                    }
                }
            }
        }
    }

    return faces.map { it.toFace() }
}

private fun findFaces(map: List<String>): List<MutableFace> {
    val ret = mutableListOf<MutableFace>()

    (map.indices step map.sideLen).forEach { y ->
        (map[y].indices step map.sideLen).forEach { x ->
            if (map[y][x] in ".#") {
                val edges = mutableListOf<Edge?>().apply {
                    repeat(4) {
                        add(null)
                    }
                }
                ret.add(MutableFace(ret.size, Point(x, y), edges))
            }
        }
    }

    return ret
}

// side length of the cube
// assumption: the cube has been cut to fit in a 3s x 4s rectangle, s = sideLen
private val List<String>.sideLen
    get() = minOf(size, this.maxOf { it.length }) / 3
