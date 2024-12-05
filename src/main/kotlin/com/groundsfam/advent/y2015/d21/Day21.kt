package com.groundsfam.advent.y2015.d21

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import java.util.PriorityQueue
import kotlin.io.path.div
import kotlin.io.path.useLines
import kotlin.math.max

data class Stats(val hitPoints: Int, val damage: Int, val armor: Int)
data class Equipment(val cost: Int, val damage: Int, val armor: Int)
// indexes of equipment from their lists
data class AllEquipment(
    val weaponIdx: Int,
    val armorIdx: Int,
    val leftRingIdx: Int,
    val rightRingIdx: Int
) {
    val weapon = weapons[weaponIdx]
    val armor = armorList[armorIdx]
    val leftRing = rings[leftRingIdx]
    val rightRing = rings[rightRingIdx]
    val cost = weapon.cost + armor.cost + leftRing.cost + rightRing.cost
}

fun Stats.withEquipment(equipment: AllEquipment) = Stats(
    hitPoints,
    damage + equipment.weapon.damage + equipment.leftRing.damage + equipment.rightRing.damage,
    armor + equipment.armor.armor + equipment.leftRing.armor + equipment.rightRing.armor,
)

// all equipment sorted by cost
val weapons = listOf(
    Equipment(8, 4, 0),
    Equipment(10, 5, 0),
    Equipment(25, 6, 0),
    Equipment(40, 7, 0),
    Equipment(74, 8, 0),
)
val armorList = listOf(
    Equipment(0, 0, 0),
    Equipment(13, 0, 1),
    Equipment(31, 0, 2),
    Equipment(53, 0, 3),
    Equipment(75, 0, 4),
    Equipment(102, 0, 5),
)
val rings = listOf(
    Equipment(0, 0, 0),
    Equipment(0, 0, 0),
    Equipment(20, 0, 1),
    Equipment(25, 1, 0),
    Equipment(40, 0, 2),
    Equipment(50, 2, 0),
    Equipment(80, 0, 3),
    Equipment(100, 3, 0),
)
val empty = Equipment(0, 0, 0)

// either min cost to survive or max cost to lose
fun extremeCost(self: Stats, boss: Stats, survive: Boolean): Int {
    val visited = mutableSetOf<AllEquipment>()
    val initEquipment =
        if (survive) AllEquipment(0, 0, 0, 0)
        else AllEquipment(weapons.size - 1, armorList.size - 1, rings.size - 2, rings.size - 1)
    val comparator: Comparator<AllEquipment> =
        if (survive) compareBy { it.cost }
        else compareByDescending { it.cost }
    val queue = PriorityQueue(comparator)
    queue.add(initEquipment)

    while (queue.isNotEmpty()) {
        val equipment = queue.poll()
        if (!visited.add(equipment)) {
            continue
        }

        if (willSurvive(self.withEquipment(equipment), boss) == survive) {
            return equipment.cost
        }

        val (weaponIdx, armorIdx, leftRingIdx, rightRingIdx) = equipment
        val dx = if (survive) 1 else -1
        if (weaponIdx + dx in weapons.indices) {
            queue.add(equipment.copy(weaponIdx = weaponIdx + dx))
        }
        if (armorIdx + dx in armorList.indices) {
            queue.add(equipment.copy(armorIdx = armorIdx + dx))
        }
        if (leftRingIdx + dx in rings.indices) {
            queue.add(equipment.copy(leftRingIdx = leftRingIdx + dx))
        }
        // ensure right ring always costs less/more than left ring
        if (rightRingIdx + dx in rings.indices && rightRingIdx + dx != leftRingIdx) {
            queue.add(equipment.copy(rightRingIdx = rightRingIdx + dx))
        }
    }

    if (survive) {
        throw RuntimeException("Cannot survive with any equipment")
    } else {
        throw RuntimeException("Cannot lose with any equipment")
    }
}

fun willSurvive(self: Stats, boss: Stats): Boolean {
    val selfDamage = max(self.damage - boss.armor, 1)
    val bossDamage = max(boss.damage - self.armor, 1)
    val selfDeadAfter = (self.hitPoints + bossDamage - 1) / bossDamage
    val bossDeadAfter = (boss.hitPoints + selfDamage - 1) / selfDamage
    return selfDeadAfter >= bossDeadAfter
}

fun main() = timed {
    val self = Stats(100, 0, 0)
    val boss = (DATAPATH / "2015/day21.txt").useLines { lines ->
        val (hp, d, a) = lines.mapTo(mutableListOf()) { it.split(" ").last().toInt() }
        Stats(hp, d, a)
    }
    println("Part one: ${extremeCost(self, boss, true)}")
    println("Part two: ${extremeCost(self, boss, false)}")
}
