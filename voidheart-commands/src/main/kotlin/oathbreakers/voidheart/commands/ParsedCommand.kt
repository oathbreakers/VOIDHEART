package oathbreakers.voidheart.commands

import net.kyori.adventure.audience.Audience

class ParsedCommand<C : CommandContext<A>, A : Audience>(
    private val info: CommandInfo<C, A>,
    private val context: C,
) {
    suspend fun execute() {
        info.executor!!(context)
    }
}