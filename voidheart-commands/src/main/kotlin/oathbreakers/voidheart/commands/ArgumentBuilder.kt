package oathbreakers.voidheart.commands

import net.kyori.adventure.audience.Audience

class ArgumentBuilder<C, A>(private val commandBuilder: CommandBuilder<C, A>) where A : Audience, C : CommandContext<A> {
    fun <T> argument(
        name: String,
        type: CommandArgument<A, T>,
        block: ArgumentBuilder<C, A>.() -> Unit = {}
    ): ArgumentBuilder<C, A> where T : Any {
        @Suppress("UNCHECKED_CAST")
        commandBuilder.args.add(name to type as CommandArgument<A, *>)
        block(this)
        return this
    }
}