package oathbreakers.voidheart.scoreboard

import io.papermc.paper.adventure.AdventureComponent
import net.kyori.adventure.text.Component
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.numbers.BlankFormat
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.*
import net.minecraft.world.scores.DisplaySlot
import net.minecraft.world.scores.Objective
import net.minecraft.world.scores.criteria.ObjectiveCriteria
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.*
import net.minecraft.world.scores.Scoreboard as MojangScoreboard

class Scoreboard internal constructor(val owner: Player) {
    private val mojangScoreboard = MojangScoreboard()
    private val team = mojangScoreboard.addPlayerTeam("vh-scoreboard-${owner.uniqueId}").also {
        it.players.add(owner.name)
        it.color = ChatFormatting.WHITE
    }
    private val rootObjective: Objective = mojangScoreboard.addObjective(
        "vh-obj-${owner.uniqueId}",
        ObjectiveCriteria.DUMMY,
        AdventureComponent(Component.text("Loading...")).deepConverted(),
        ObjectiveCriteria.RenderType.INTEGER,
        true,
        null
    )

    private var content: ScoreboardContent? = null
        set(value) {
            if (value == null) {
                if (this.content != null) {
                    remove()
                } else {
                    this.content = null
                }
            } else {
                if (this.content == null) {
                    initialize()
                }
                this.content = value
            }
        }

    private val entries = Array(15) { idx ->
        ScoreboardEntry(this, "line_$idx", 16 - idx, Component.text(""))
    }

    init {
        if (content != null) {
            initialize()
            update()
        }
    }

    private fun initialize() {
        sendPacket(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true))
        sendPacket(ClientboundSetObjectivePacket(rootObjective, ClientboundSetObjectivePacket.METHOD_ADD))
        sendPacket(ClientboundSetDisplayObjectivePacket(DisplaySlot.SIDEBAR, rootObjective))
    }

    private fun sendPacket(packet: Packet<*>) {
        (owner as CraftPlayer).handle.connection.send(packet)
    }

    internal fun update() {
        content?.let { content ->
            content.preUpdate()
            updateTitle(content.getTitle())
            updateLines(content.getLines())
        }
    }

    private fun updateTitle(title: Component) {
        rootObjective.displayName = AdventureComponent(title).deepConverted()
        sendPacket(ClientboundSetObjectivePacket(rootObjective, ClientboundSetObjectivePacket.METHOD_CHANGE))
    }

    private fun updateLines(lines: List<Component>) {
        // Ensure the client removes any entries it will no longer need
        // We do this first to minimize flickering
        if (lines.size < entries.size) {
            entries.slice(lines.size until entries.size).forEachIndexed { idx, entry ->
                entry.remove()
            }
        }

        lines.forEachIndexed { index, line ->
            if (index >= entries.size) {
                // Silently ignore anything else
                return@forEachIndexed
            }

            val entry = entries[index]
            entry.content = line
            entry.send()
        }
    }

    fun remove() {
        entries.forEach { it.remove() }
        sendPacket(ClientboundSetPlayerTeamPacket.createRemovePacket(team))
        sendPacket(ClientboundSetObjectivePacket(rootObjective, ClientboundSetObjectivePacket.METHOD_REMOVE))
    }

    internal fun sendScorePacket(entryId: String, score: Int, display: Component) {
        sendPacket(
            ClientboundSetScorePacket(
                entryId,
                rootObjective.name,
                score,
                Optional.of(AdventureComponent(display).deepConverted()),
                Optional.of(BlankFormat.INSTANCE)
            )
        )
    }

    internal fun removeScorePacket(entryId: String) {
        sendPacket(ClientboundResetScorePacket(entryId, rootObjective.name))
    }
}
