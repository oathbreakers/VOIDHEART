package oathbreakers.voidheart.commands

import net.kyori.adventure.audience.Audience

open class CommandContext<A : Audience>(
    val audience: A,
) {
    internal val parsedArgs = mutableMapOf<String, Any>()

    @Suppress("UNCHECKED_CAST")
    fun <T> get(name: String): T = parsedArgs[name]!! as T
}