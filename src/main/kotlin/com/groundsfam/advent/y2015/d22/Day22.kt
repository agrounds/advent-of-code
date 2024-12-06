package com.groundsfam.advent.y2015.d22

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import java.util.PriorityQueue
import kotlin.io.path.div
import kotlin.io.path.useLines
import kotlin.math.max

enum class Spell(val manaCost: Int) {
    MAGIC_MISSILE(53),
    DRAIN(73),
    SHIELD(113),
    POISON(173),
    RECHARGE(229),
}

data class Boss(val hp: Int, val damage: Int)

data class Player(val hp: Int, val armor: Int, val mana: Int)

data class State(val player: Player, val boss: Boss, val shieldTimer: Int, val poisonTimer: Int, val rechargeTimer: Int, val manaSpent: Int)

fun State.doEffects(): State {
    var (player, boss, shieldTimer, poisonTimer, rechargeTimer, _) = this
    if (shieldTimer > 0) {
        shieldTimer--
        if (shieldTimer == 0) {
            player = player.copy(armor = 0)
        }
    }
    if (poisonTimer > 0) {
        boss = boss.copy(hp = boss.hp - 3)
        poisonTimer--
    }
    if (rechargeTimer > 0) {
        player = player.copy(mana = player.mana + 101)
        rechargeTimer--
    }
    return State(player, boss, shieldTimer, poisonTimer, rechargeTimer, manaSpent)
}

fun State.castSpell(spell: Spell): State? {
    val newMana = player.mana - spell.manaCost
    if (newMana < 0) return null

    return when(spell) {
        Spell.MAGIC_MISSILE -> {
            val newPlayer = player.copy(mana = newMana)
            val newBoss = boss.copy(hp = boss.hp - 4)
            copy(player = newPlayer, boss = newBoss, manaSpent = manaSpent + spell.manaCost)
        }
        Spell.DRAIN -> {
            val newPlayer = player.copy(hp = player.hp + 2, mana = newMana)
            val newBoss = boss.copy(hp = boss.hp - 2)
            copy(player = newPlayer, boss = newBoss, manaSpent = manaSpent + spell.manaCost)
        }
        Spell.SHIELD -> {
            if (shieldTimer == 0) {
                val newPlayer = player.copy(armor = 7, mana = newMana)
                copy(player = newPlayer, shieldTimer = 6, manaSpent = manaSpent + spell.manaCost)
            } else {
                null
            }
        }
        Spell.POISON -> {
            if (poisonTimer == 0) {
                val newPlayer = player.copy(mana = newMana)
                copy(player = newPlayer, poisonTimer = 6, manaSpent = manaSpent + spell.manaCost)
            } else {
                null
            }
        }
        Spell.RECHARGE -> {
            if (rechargeTimer == 0) {
                val newPlayer = player.copy(mana = newMana)
                copy(player = newPlayer, rechargeTimer = 5, manaSpent = manaSpent + spell.manaCost)
            } else {
                null
            }
        }
    }
}

fun State.bossAttack(): State = copy(
    player = player.copy(hp = player.hp - max(boss.damage - player.armor, 1))
)

fun minMana(initBoss: Boss, hardMode: Boolean): Int {
    val initPlayer = Player(50, 0, 500)
    val initState = State(initPlayer, initBoss, 0, 0, 0, 0)
    val visited = mutableSetOf<State>()
    val queue = PriorityQueue<State>(compareBy { it.manaSpent })
    queue.add(initState)

    while (queue.isNotEmpty()) {
        var state = queue.poll()
        if (!visited.add(state)) {
            continue
        }

        // player turn
        if (hardMode) {
            if (state.boss.hp <= 0) {
                return state.manaSpent
            }
            state = state.copy(player = state.player.copy(hp = state.player.hp - 1))
            if (state.player.hp <= 0) {
                continue
            }
        }
        state = state.doEffects()
        if (state.boss.hp <= 0) {
            return state.manaSpent
        }
        if (state.player.hp <= 0) {
            continue
        }

        Spell.entries.forEach { spell ->
            state.castSpell(spell)?.also {
                // boss turn
                val nextState = it
                    .doEffects()
                    .bossAttack()
                queue.add(nextState)
            }
        }
    }

    throw RuntimeException("Cannot beat boss")
}

fun main() = timed {
    val boss = (DATAPATH / "2015/day22.txt").useLines { lines ->
        val (hp, damage) = lines.mapTo(mutableListOf()) { it.split(" ").last().toInt() }
        Boss(hp, damage)
    }
    println("Part one: ${minMana(boss, false)}")
    println("Part two: ${minMana(boss, true)}")
}
