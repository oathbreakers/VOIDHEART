package oathbreakers.voidheart.core.folia.paper

import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap
import io.papermc.paper.plugin.bootstrap.PluginProviderContext
import oathbreakers.voidheart.core.folia.CorePlugin
import oathbreakers.voidheart.core.folia.config.ConfigurationHelper
import oathbreakers.voidheart.core.folia.config.CoreConfiguration
import oathbreakers.voidheart.core.folia.db.ExposedHelper
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import kotlin.system.exitProcess

@Suppress("UnstableApiUsage")
class PluginBootstrap : PluginBootstrap {
    private lateinit var config: CoreConfiguration

    override fun bootstrap(context: BootstrapContext) = try {
        config = ConfigurationHelper.load(File(context.dataDirectory.toFile(), "config.toml"), CoreConfiguration())
        ExposedHelper.setup(config)
    } catch (t: Throwable) {
        t.printStackTrace()
        exitProcess(-1)
    }

    override fun createPlugin(context: PluginProviderContext): JavaPlugin {
        return CorePlugin(config)
    }
}