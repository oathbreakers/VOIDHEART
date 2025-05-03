package oathbreakers.voidheart.core.folia.commands.essentials

import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import oathbreakers.voidheart.commands.*
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent

private fun registerTeleportCommand(cmds: Commands) {
    cmds.register("teleport", "tp") {
        requiresPermission("voidheart.teleport")

        argument("target", ArgumentTypes.player()) {
            argument("target_b", ArgumentTypes.player()) {
                onExecute {
                    val targetA = getArgument<PlayerSelectorArgumentResolver>("target").resolve(source).firstOrNull()
                    val targetB = getArgument<PlayerSelectorArgumentResolver>("target_b").resolve(source).firstOrNull()
                    if (targetA == null || targetB == null) {
                        sender.sendRichMessage("<red>No player found matching the selector.")
                        return@onExecute
                    }

                    targetA.teleportAsync(targetB.location, PlayerTeleportEvent.TeleportCause.COMMAND)
                        .thenApply { successful ->
                            if (successful) {
                                sender.sendRichMessage("<green>Teleported ${targetA.name} to ${targetB.name}.")
                            } else {
                                sender.sendRichMessage("<red>Failed to teleport ${targetA.name} to ${targetB.name}.")
                            }
                        }
                }
            }

            argument("position", ArgumentTypes.blockPosition()) {
                onExecute {
                    val target = getArgument<PlayerSelectorArgumentResolver>("target").resolve(source).firstOrNull()
                    val position = getArgument<BlockPositionResolver>("position").resolve(source)
                    if (target == null) {
                        sender.sendRichMessage("<red>No player found matching the selector.")
                        return@onExecute
                    }

                    target.teleportAsync(
                        position.toLocation(target.world).apply { yaw = target.yaw; pitch = target.pitch },
                        PlayerTeleportEvent.TeleportCause.COMMAND
                    ).thenApply { successful ->
                        if (successful) {
                            sender.sendRichMessage("<green>Teleported ${target.name} to ${position.x()}, ${position.y()}, ${position.z()}.")
                        } else {
                            sender.sendRichMessage("<red>Failed to teleport ${target.name}.")
                        }
                    }
                }
            }

            onExecute {
                if (sender !is Player) {
                    sender.sendRichMessage("<red>Only players can use this command.")
                    return@onExecute
                }

                val target = getArgument<PlayerSelectorArgumentResolver>("target").resolve(source).firstOrNull()
                if (target == null) {
                    sender.sendRichMessage("<red>No player found matching the selector.")
                    return@onExecute
                }

                player.teleportAsync(target.location, PlayerTeleportEvent.TeleportCause.COMMAND)
                    .thenApply { successful ->
                        if (successful) {
                            sender.sendRichMessage("<green>Teleported to ${target.name}.")
                        } else {
                            sender.sendRichMessage("<red>Failed to teleport to ${target.name}.")
                        }
                    }
            }
        }

        argument("position", ArgumentTypes.blockPosition()) {
            onExecute {
                if (sender !is Player) {
                    sender.sendRichMessage("<red>Only players can use this command.")
                    return@onExecute
                }

                val position = getArgument<BlockPositionResolver>("position").resolve(source)
                player.teleportAsync(
                    position.toLocation(player.world).apply { yaw = player.yaw; pitch = player.pitch },
                    PlayerTeleportEvent.TeleportCause.COMMAND
                ).thenApply { successful ->
                    if (successful) {
                        sender.sendRichMessage("<green>Teleported to ${position.x()}, ${position.y()}, ${position.z()}.")
                    } else {
                        sender.sendRichMessage("<red>Failed to teleport.")
                    }
                }
            }
        }
    }
}

internal fun registerTeleportCommands(cmds: Commands) {
    registerTeleportCommand(cmds)
}