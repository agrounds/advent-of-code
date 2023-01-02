package advent

data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point) = Point(x + other.x, y + other.y)
}

data class Point3(val x: Int, val y: Int, val z: Int) {
    operator fun plus(other: Point3) = Point3(x + other.x, y + other.y, z + other.z)
}

fun Point3.adjacents(): List<Point3> = listOf(
    Point3(1, 0, 0) + this,
    Point3(-1, 0, 0) + this,
    Point3(0, 1, 0) + this,
    Point3(0, -1, 0) + this,
    Point3(0, 0, 1) + this,
    Point3(0, 0, -1) + this,
)
