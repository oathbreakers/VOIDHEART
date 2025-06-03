package oathbreakers.voidheart.scoreboard

import net.kyori.adventure.text.Component

internal class ScoreboardEntry(
    private val scoreboard: Scoreboard,
    val entryId: String,
    val score: Int,
    var content: Component
) {
    private var sent = false

    fun send() {
        scoreboard.sendScorePacket(entryId, score, content)
        sent = true
    }

    fun remove() {
        if (sent) {
            scoreboard.removeScorePacket(entryId)
            sent = false
        }
    }
}
