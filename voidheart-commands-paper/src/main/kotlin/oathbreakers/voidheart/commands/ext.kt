package oathbreakers.voidheart.commands

import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

inline fun Commands.register(
    name: String,
    vararg aliases: String,
    block: LiteralArgumentBuilder<CommandSourceStack>.() -> Unit
) {
    val builder = LiteralArgumentBuilder.literal<CommandSourceStack>(name)
    block(builder)

    val node = builder.build()
    register(node)

    aliases.forEach { alias ->
        register(LiteralArgumentBuilder.literal<CommandSourceStack>(alias).redirect(node).build())
    }
}

fun <Self> ArgumentBuilder<CommandSourceStack, Self>.requiresConsole() where Self : ArgumentBuilder<CommandSourceStack, Self> {
    val existingRequirement = this.requirement
    requires { src -> existingRequirement.test(src) && src.sender is ConsoleCommandSender }
}

fun <Self> ArgumentBuilder<CommandSourceStack, Self>.requiresPlayer() where Self : ArgumentBuilder<CommandSourceStack, Self> {
    val existingRequirement = this.requirement
    requires { src -> existingRequirement.test(src) && src.sender is Player }
}

fun <Self> ArgumentBuilder<CommandSourceStack, Self>.requiresPermission(permission: String) where Self : ArgumentBuilder<CommandSourceStack, Self> {
    val existingRequirement = this.requirement
    requires { src -> existingRequirement.test(src) && src.sender.hasPermission(permission) }
}