package dev.shaper.rypolixy.command.types

import dev.shaper.rypolixy.command.types.TextCommand.ResponseData
import dev.shaper.rypolixy.config.Client
import dev.kord.core.event.message.MessageCreateEvent

class CommandManager(private val client: Client) {
    val textCommand         = HashMap<String, TextCommand>        ()
    val mutualCommand       = HashMap<String, MutualCommand>      ()
    val messageCommand      = HashMap<String, MessageCommand>     ()
    val interactionCommand  = HashMap<String, InteractionCommand> ()

    suspend fun executeTextCommand(event:MessageCreateEvent) {
        val input = event.message.content
        if (input.startsWith(TextCommand.commonPrefix)) {
            val regex           = Regex("^${TextCommand.commonPrefix}(\\w+)\\s*(.*)")
            val commandRegex    = Regex("\\S+")
            val optionRegex     = Regex("--(\\w+)")
            val matchResult     = regex.find(input)

            if (matchResult != null) {
                val keyword = matchResult.groupValues[1]
                val command = commandRegex.find(matchResult.groupValues[2])!!.value
                val options = optionRegex.findAll(matchResult.groupValues[2]).map{ it.groupValues[1] }.toList()

                if (textCommand[keyword] != null)
                    textCommand[keyword]?.execute(event, ResponseData(keyword, command, options))

                if(mutualCommand[keyword] != null)
                    mutualCommand[keyword]?.execute(ContextType.Message(event), ResponseData(keyword, command, options))

            }

        }
    }


    suspend fun registerInteractionCommand(){
        interactionCommand.filterValues{ it.enabled ?: false }.forEach { (key,command) ->
            client.kord.createGlobalChatInputCommand(command.name,command.description){
                command.setup(this)
            }
        }

        mutualCommand.filterValues{ it.enabled ?: false }.filterValues { it.isInteractive } .forEach { (key,command) ->
            client.kord.createGlobalChatInputCommand(command.name,command.description){
                command.setup(this)
            }
        }

    }

    fun collectCommands(commands:Iterable<CommandStructure>){
        commands.forEach{ command ->
            when(command) {
                is MutualCommand        -> mutualCommand       [command.name] = command
                is TextCommand          -> textCommand         [command.name] = command
                is MessageCommand       -> messageCommand      [command.name] = command
                is InteractionCommand   -> interactionCommand  [command.name] = command
            }
        }
    }

}





