package oathbreakers.voidheart.commands

import net.kyori.adventure.audience.Audience
import oathbreakers.voidheart.commands.impl.StringCommandArgument

interface CommandArgument<A, T> where A : Audience, T : Any {
    sealed class ArgumentResult<T> {
        data class Success<T>(val value: T) : ArgumentResult<T>()
        data class Failure<T>(val reason: String) : ArgumentResult<T>()
    }

    suspend fun parse(audience: A, buffer: CommandBuffer): ArgumentResult<T>

    suspend fun complete(audience: A, buffer: CommandBuffer): List<String>

    companion object {
        fun <A> stringArgument() where A : Audience = StringCommandArgument<A>()
    }
}
