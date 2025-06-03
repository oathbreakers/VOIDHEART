package oathbreakers.voidheart.scoreboard

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class ScoreboardManager(plugin: JavaPlugin, updateSpeedTicks: Long = 10) : Listener {
    private val scoreboardMap = ConcurrentHashMap<UUID, Scoreboard>()

    init {
        plugin.server.globalRegionScheduler.runAtFixedRate(plugin, { task ->
            if (task.isCancelled) {
                return@runAtFixedRate
            }

            scoreboardMap.forEach { (_, scoreboard) ->
                scoreboard.update()
            }
        }, 20, updateSpeedTicks)

        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    fun getScoreboard(player: Player): Scoreboard {
        return scoreboardMap.computeIfAbsent(player.uniqueId) { Scoreboard(player) }
    }

    @EventHandler
    internal fun onPlayerJoin(event: PlayerJoinEvent) {
        scoreboardMap[event.player.uniqueId] = Scoreboard(event.player)
    }

    @EventHandler
    internal fun onPlayerQuit(event: PlayerQuitEvent) {
        scoreboardMap.remove(event.player.uniqueId)
    }
}