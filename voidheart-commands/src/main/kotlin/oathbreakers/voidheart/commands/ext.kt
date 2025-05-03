package oathbreakers.voidheart.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode

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

inline fun <reified T> CommandContext<*>.getArgument(name: String): T where T : Any {
    return getArgument(name, T::class.java)
}

inline fun <S> brigadierCommand(
    name: String,
    block: LiteralArgumentBuilder<S>.() -> Unit
): LiteralCommandNode<S> {
    val builder = LiteralArgumentBuilder.literal<S>(name)
    block(builder)
    return builder.build()
}
