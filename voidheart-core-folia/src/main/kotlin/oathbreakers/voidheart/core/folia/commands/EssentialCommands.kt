package oathbreakers.voidheart.core.folia.commands

import io.papermc.paper.command.brigadier.Commands
import oathbreakers.voidheart.core.folia.commands.essentials.registerTeleportCommands

fun registerEssentialCommands(cmds: Commands) {
    registerTeleportCommands(cmds)
}