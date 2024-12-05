package com.groundsfam.advent.y2015.d21

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import java.util.PriorityQueue
import kotlin.io.path.div
import kotlin.io.path.useLines
import kotlin.math.max

data class Stats(val hitPoints: Int, val damage: Int, val armor: Int)
data class Equipment(val cost: Int, val damage: Int, val armor: Int)
data class AllEquipment(val weapon: Equipment, val armor: Equipment, val leftRing: Equipment, val rightRing: Equipment) {
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
    Equipment(13, 0, 1),
    Equipment(31, 0, 2),
    Equipment(53, 0, 3),
    Equipment(75, 0, 4),
    Equipment(102, 0, 5),
)
val rings = listOf(
    Equipment(20, 0, 1),
    Equipment(25, 1, 0),
    Equipment(40, 0, 2),
    Equipment(50, 2, 0),
    Equipment(80, 0, 3),
    Equipment(100, 3, 0),
)
val empty = Equipment(0, 0, 0)

fun minCostSurvive(self: Stats, boss: Stats): Int {
    val visited = mutableSetOf<AllEquipment>()
    val queue = PriorityQueue<AllEquipment>(compareBy { it.cost })
    queue.add(AllEquipment(empty, empty, empty, empty))

    while (queue.isNotEmpty()) {
        val equipment = queue.poll()
        if (!visited.add(equipment)) {
            continue
        }

        if (willSurvive(self.withEquipment(equipment), boss)) {
            return equipment.cost
        }

        val (weapon, armor, leftRing, rightRing) = equipment
        weapons.firstOrNull { it.cost > weapon.cost }?.also {
            queue.add(equipment.copy(weapon = it))
        }
        armorList.firstOrNull { it.cost > armor.cost }?.also {
            queue.add(equipment.copy(armor = it))
        }
        rings.firstOrNull { it.cost > leftRing.cost }?.also {
            queue.add(equipment.copy(leftRing = it))
        }
        // ensure right ring always costs less than left ring
        rings.firstOrNull { it.cost in (rightRing.cost + 1) until leftRing.cost }?.also {
            queue.add(equipment.copy(rightRing = it))
        }
    }

    throw RuntimeException("Cannot survive with any equipment")
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
    println("Part one: ${minCostSurvive(self, boss)}")
}
