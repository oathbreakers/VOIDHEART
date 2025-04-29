package oathbreakers.voidheart.commands.impl

import net.kyori.adventure.audience.Audience
import oathbreakers.voidheart.commands.CommandArgument
import oathbreakers.voidheart.commands.CommandBuffer

class StringCommandArgument<A> : CommandArgument<A, String> where A : Audience {
    override suspend fun parse(
        audience: A,
        buffer: CommandBuffer
    ): CommandArgument.ArgumentResult<String> {
        val value = buffer.pop() ?: return CommandArgument.ArgumentResult.Failure("End of input")
        return CommandArgument.ArgumentResult.Success(value)
    }

    override suspend fun complete(
        audience: A,
        buffer: CommandBuffer
    ): List<String> = emptyList()
}