package oathbreakers.voidheart.commands

import kotlinx.coroutines.runBlocking
import net.kyori.adventure.audience.Audience

class CommandTree<C, A> where A : Audience, C : CommandContext<A> {
    private val root = CommandInfo<C, A>("", mutableListOf(), emptySet(), null)

    private fun checkAmbiguity(builder: CommandBuilder<C, A>): Result<Unit> {
        val formattedName = if (builder.parentName != null) {
            "${builder.parentName} ${builder.name}"
        } else {
            builder.name
        }

        val subCommandNames = builder.children.map { it.name }.toSet()
        if (subCommandNames.size != builder.children.size) {
            return Result.failure(IllegalStateException("Command '$formattedName' has two or more subcommands with the same name"))
        }

        if (builder.args.isNotEmpty() && builder.children.isNotEmpty()) {
            return Result.failure(IllegalStateException("Command '$formattedName' has both arguments and subcommands, which results in ambiguous execution"))
        }

        builder.children.forEach {
            val childResult = checkAmbiguity(it)
            if (childResult.isFailure) return childResult
        }

        return Result.success(Unit)
    }

    fun register(name: String, block: CommandBuilder<C, A>.() -> Unit) {
        val builder = CommandBuilder<C, A>(null, name)
        block(builder)

        val potentiallyAmbiguous = checkAmbiguity(builder)
        potentiallyAmbiguous.getOrThrow()

        fun mapChildren(builder: CommandBuilder<C, A>): MutableList<CommandInfo<C, A>> {
            return builder.children.map { child ->
                CommandInfo(child.name, mapChildren(child), child.args, child.executor)
            }.toMutableList()
        }

        val children = mapChildren(builder)
        val command = CommandInfo(builder.name, children, builder.args, builder.executor)
        root.children.add(command)
    }

    suspend fun parse(context: C, text: String): ParsedCommand<C, A>? {
        val buffer = CommandBuffer(text)
        val rootCommand = root.findChild(buffer.pop() ?: return null) ?: return null

        var requestedCommand = rootCommand
        while (buffer.hasNext()) {
            val nextCommand = requestedCommand.findChild(buffer.peek()!!)
            if (nextCommand != null) {
                buffer.pop()
                requestedCommand = nextCommand
            } else {
                break
            }
        }

        val parsedArgs = mutableMapOf<String, Any>()
        for ((argName, argType) in requestedCommand.args) {
            val result = argType.parse(context.audience, buffer)
            when (result) {
                is CommandArgument.ArgumentResult.Success -> {
                    parsedArgs[argName] = result.value
                }

                // TODO: Better error handling
                is CommandArgument.ArgumentResult.Failure -> throw IllegalArgumentException("Failed to parse argument $argName: ${result.reason}")
            }
        }

        context.parsedArgs.putAll(parsedArgs)
        return ParsedCommand(requestedCommand, context)
    }
}
