package oathbreakers.voidheart.core.folia

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import oathbreakers.voidheart.core.folia.commands.registerEssentialCommands
import oathbreakers.voidheart.core.folia.config.CoreConfiguration
import oathbreakers.voidheart.scoreboard.ScoreboardManager
import org.bukkit.plugin.java.JavaPlugin

class CorePlugin(private val config: CoreConfiguration) : JavaPlugin() {
    private lateinit var scoreboardManager: ScoreboardManager

    override fun onEnable() {
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { cmds ->
            val registrar = cmds.registrar()
            registerEssentialCommands(registrar)
        }

        scoreboardManager = ScoreboardManager(this)
    }

    override fun onDisable() {
    }
}