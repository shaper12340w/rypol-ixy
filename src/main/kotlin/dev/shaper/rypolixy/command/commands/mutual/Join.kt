package dev.shaper.rypolixy.command.commands.mutual

import dev.kord.common.annotation.KordVoice
import dev.kord.rest.builder.message.EmbedBuilder
import dev.shaper.rypolixy.command.types.ContextType
import dev.shaper.rypolixy.command.types.MutualCommand
import dev.shaper.rypolixy.command.types.TextCommand
import dev.shaper.rypolixy.config.Client
import dev.shaper.rypolixy.utils.discord.ContextManager.Companion.channel
import dev.shaper.rypolixy.utils.discord.ContextManager.Companion.getGuild
import dev.shaper.rypolixy.utils.discord.ContextManager.Companion.getMember
import dev.shaper.rypolixy.utils.discord.Colors
import dev.shaper.rypolixy.utils.discord.ContextManager.Companion.guildId
import dev.shaper.rypolixy.utils.discord.EmbedFrame
import dev.shaper.rypolixy.utils.discord.ResponseManager.Companion.sendRespond
import dev.shaper.rypolixy.utils.discord.ResponseType


class Join(private val client: Client): MutualCommand {

    override val name       : String
        get()          = "join"

    override val description: String
        get()          = "join to voice channel"

    override val commandType: TextCommand.CommandType
        get()          = TextCommand.CommandType(prefix = null, suffix = null, equals = null)

    override val enabled    : Boolean
        get() = true

    override val isInteractive: Boolean
        get() = true

    @OptIn(KordVoice::class)
    override suspend fun execute(context: ContextType, res: TextCommand.ResponseData?) {

        suspend fun errorMessage(text:String) = context.sendRespond(ResponseType.NORMAL,EmbedFrame.error(text,null))
        try {
            val state = context.getMember().getVoiceState()
            if(state.getChannelOrNull() == null) {
                errorMessage("음성 채널에 입장해주세요")
            } else {
                val findPlayer = client.lavaClient.sessions[context.guildId]
                val channel = state.getChannelOrNull()
                if(findPlayer != null)
                    errorMessage("이미 입장해있습니다")
                else {
                    client.lavaClient.connect(channel!!,context.channel)
                    context.sendRespond(ResponseType.NORMAL, EmbedBuilder().apply {
                        title = "🔊 Join"
                        description = "<#${channel!!.asChannel().id}>에 입장했습니다"
                        color = Colors.YELLOW
                    })
                }

            }
        }
        catch (ex: Exception) { errorMessage("음성 채널에 입장해주세요") }

    }

}