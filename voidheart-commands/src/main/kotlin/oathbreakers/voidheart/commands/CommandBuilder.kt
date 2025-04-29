package oathbreakers.voidheart.commands

import net.kyori.adventure.audience.Audience

class CommandBuilder<C, A>(
    internal val parentName: String?,
    val name: String
) where A : Audience, C : CommandContext<A> {
    internal val children = mutableListOf<CommandBuilder<C, A>>()
    internal val args = mutableSetOf<Pair<String, CommandArgument<A, *>>>()
    internal var executor: (suspend C.() -> Unit)? = null

    fun literal(name: String, block: CommandBuilder<C, A>.() -> Unit = {}): CommandBuilder<C, A> {
        children.add(CommandBuilder<C, A>(this.name, name).also(block))
        return this
    }

    fun <T> argument(
        name: String,
        type: CommandArgument<A, T>,
        block: ArgumentBuilder<C, A>.() -> Unit = {}
    ): ArgumentBuilder<C, A> where T : Any {
        @Suppress("UNCHECKED_CAST")
        args.add(name to type as CommandArgument<A, *>)
        return ArgumentBuilder(this).also(block)
    }

    fun onExecute(block: suspend C.() -> Unit) {
        if (executor == null) {
            executor = block
        } else {
            throw IllegalStateException("Set executor on command '$name' more than once")
        }
    }
}