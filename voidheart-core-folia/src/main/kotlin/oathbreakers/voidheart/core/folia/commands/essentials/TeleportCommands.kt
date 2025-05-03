package oathbreakers.voidheart.core.folia.commands.essentials

import com.mojang.brigadier.Command
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent

private fun registerTeleportCommand(cmds: Commands) {
    val rootCommand = Commands.literal("teleport")
        .requires { source -> source.executor is Player && source.sender.hasPermission("voidheart.teleport") }
        .then(Commands.argument("target", ArgumentTypes.player()).executes { ctx ->
            val targetResolver = ctx.getArgument("target", PlayerSelectorArgumentResolver::class.java)
            val target = targetResolver.resolve(ctx.source).firstOrNull()
            if (target == null) {
                ctx.source.sender.sendRichMessage("<red>No player found matching the selector.")
                return@executes -1
            }

            ctx.source.executor?.teleportAsync(target.location, PlayerTeleportEvent.TeleportCause.COMMAND)
                ?.thenApply { successful ->
                    if (successful) {
                        ctx.source.sender.sendRichMessage("<green>Teleported to ${target.name}.")
                    } else {
                        ctx.source.sender.sendRichMessage("<red>Failed to teleport to ${target.name}.")
                    }
                }
            return@executes Command.SINGLE_SUCCESS
        })
        .build()

    cmds.register(rootCommand)
    cmds.register(Commands.literal("tp").redirect(rootCommand).build())
}

internal fun registerTeleportCommands(cmds: Commands) {
    registerTeleportCommand(cmds)
}