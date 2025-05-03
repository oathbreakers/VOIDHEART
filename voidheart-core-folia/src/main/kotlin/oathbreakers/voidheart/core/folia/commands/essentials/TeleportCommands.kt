package oathbreakers.voidheart.core.folia.commands.essentials

import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import oathbreakers.voidheart.commands.*
import org.bukkit.event.player.PlayerTeleportEvent

private fun registerTeleportCommand(cmds: Commands) {
    cmds.register("teleport", "tp") {
        requiresPlayer()
        requiresPermission("voidheart.teleport")

        argument("target", ArgumentTypes.player()) {
            onExecute {
                val targetResolver = getArgument<PlayerSelectorArgumentResolver>("target")
                val target = targetResolver.resolve(source).firstOrNull()
                if (target == null) {
                    source.sender.sendRichMessage("<red>No player found matching the selector.")
                    return@onExecute
                }

                source.executor?.teleportAsync(target.location, PlayerTeleportEvent.TeleportCause.COMMAND)
                    ?.thenApply { successful ->
                        if (successful) {
                            source.sender.sendRichMessage("<green>Teleported to ${target.name}.")
                        } else {
                            source.sender.sendRichMessage("<red>Failed to teleport to ${target.name}.")
                        }
                    }
            }
        }
    }
}

internal fun registerTeleportCommands(cmds: Commands) {
    registerTeleportCommand(cmds)
}