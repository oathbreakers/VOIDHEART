package oathbreakers.voidheart.commands

import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Entity
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

inline val CommandContext<CommandSourceStack>.sender: CommandSender
    get() = source.sender
inline val CommandContext<CommandSourceStack>.executor: Entity?
    get() = source.executor
inline val CommandContext<CommandSourceStack>.player: Player
    get() = source.executor!! as Player
