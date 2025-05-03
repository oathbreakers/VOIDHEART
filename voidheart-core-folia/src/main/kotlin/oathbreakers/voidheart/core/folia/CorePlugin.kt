package oathbreakers.voidheart.core.folia

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import oathbreakers.voidheart.core.folia.commands.registerEssentialCommands
import oathbreakers.voidheart.core.folia.config.CoreConfiguration
import org.bukkit.plugin.java.JavaPlugin

class CorePlugin(private val config: CoreConfiguration) : JavaPlugin() {
    override fun onEnable() {
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { cmds ->
            val registrar = cmds.registrar()
            registerEssentialCommands(registrar)
        }
    }

    override fun onDisable() {
    }
}