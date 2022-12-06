package advent.y2020.d19

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class RuleTest : WordSpec ({
    "LiteralRule" should {
        "match itself" {
            LiteralRule("a").matches("a") shouldBe true
        }
        "not match wrong character" {
            LiteralRule("a").matches("b") shouldBe false
        }
        "not match when string is too long" {
            LiteralRule("a").matches("aa") shouldBe false
        }
        "not match when string is too short" {
            LiteralRule("a").matches("") shouldBe false
        }
    }

    "SequenceRule" should {
        val seqRule = SequenceRule(listOf(
            LiteralRule("a"),
            LiteralRule("b"),
            LiteralRule("a")
        ))

        "match a sequence of literals" {
            seqRule.matches("aba") shouldBe true
        }
        "not match when string is too long" {
            seqRule.matches("abaa") shouldBe false
        }
    }
})