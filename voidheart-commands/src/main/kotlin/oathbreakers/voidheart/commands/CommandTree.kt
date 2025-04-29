package oathbreakers.voidheart.commands

import net.kyori.adventure.audience.Audience

class CommandTree<C, A> where A : Audience, C : CommandContext<A> {
    private val root = CommandInfo<C, A>("", mutableListOf(), emptySet(), null)

    fun register(name: String, block: CommandBuilder<C, A>.() -> Unit) {
        val builder = CommandBuilder<C, A>(name)
        block(builder)

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
