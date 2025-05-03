package oathbreakers.voidheart.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

inline fun <Src, Self> ArgumentBuilder<Src, Self>.literal(
    name: String,
    block: LiteralArgumentBuilder<Src>.() -> Unit
) where Self : ArgumentBuilder<Src, Self> {
    val builder = LiteralArgumentBuilder.literal<Src>(name)
    block(builder)
    then(builder)
}

inline fun <Src, Self, T> ArgumentBuilder<Src, Self>.argument(
    name: String,
    argumentType: ArgumentType<T>,
    block: RequiredArgumentBuilder<Src, T>.() -> Unit
) where Self : ArgumentBuilder<Src, Self> {
    val builder = RequiredArgumentBuilder.argument<Src, T>(name, argumentType)
    block(builder)
    then(builder)
}

inline fun <Src, Self> ArgumentBuilder<Src, Self>.onExecute(crossinline block: CommandContext<Src>.() -> Unit) where Self : ArgumentBuilder<Src, Self> {
    executes { ctx -> block(ctx); Command.SINGLE_SUCCESS }
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

inline fun <reified T> CommandContext<*>.getArgument(name: String): T where T : Any {
    return getArgument(name, T::class.java)
}

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
