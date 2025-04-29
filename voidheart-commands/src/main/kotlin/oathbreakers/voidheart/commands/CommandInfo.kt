package oathbreakers.voidheart.commands

import net.kyori.adventure.audience.Audience

class CommandInfo<C, A>(
    val name: String,
    val children: MutableList<CommandInfo<C, A>>,
    val args: Set<Pair<String, CommandArgument<A, *>>>,
    val executor: (suspend C.() -> Unit)?
) where A : Audience, C : CommandContext<A> {
    fun findChild(name: String): CommandInfo<C, A>? = children.find {
        it.name.equals(name, ignoreCase = true)
    }
}