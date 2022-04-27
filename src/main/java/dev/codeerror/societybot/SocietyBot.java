package dev.codeerror.societybot;

import dev.codeerror.societybot.audio.PlayerManager;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.lang.management.ManagementFactory;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Scanner;

public class SocietyBot implements EventListener {

    private static final String consolePrefix = "[SocietyBot] ";
    private static final char prefix = '>';

    public static void main(String[] args) throws LoginException, InterruptedException {

        System.out.println("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=");
        System.out.println("  SocietyBot v1.1 - Console Logging Interface\n");
        System.out.println("          Created By: CodeError#0001\n");
        System.out.println("            \"we live in a society.\"");
        System.out.println("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=\n");

        String token;
        OnlineStatus status = OnlineStatus.ONLINE;
        Activity.ActivityType activityType = Activity.ActivityType.WATCHING;
        String activity = "society devolve.";

        if (args.length >= 1) {

            token = args[0];

            if (args.length > 1) {
                if (args[1].equalsIgnoreCase("idle")) {
                    status = OnlineStatus.IDLE;
                } else if (args[1].equalsIgnoreCase("dnd")) {
                    status = OnlineStatus.DO_NOT_DISTURB;
                } else if (args[1].equalsIgnoreCase("invisible")) {
                    status = OnlineStatus.INVISIBLE;
                }
            }

            if (args.length > 2) {
                if (args[2].equalsIgnoreCase("default")) {
                    activityType = Activity.ActivityType.DEFAULT;
                } else if (args[2].equalsIgnoreCase("streaming")) {
                    activityType = Activity.ActivityType.STREAMING;
                } else if (args[2].equalsIgnoreCase("listening")) {
                    activityType = Activity.ActivityType.LISTENING;
                }
            }

            if (args.length > 3) {
                StringBuilder activityBuilder = new StringBuilder();
                for (int i = 3; i < args.length; i++) {
                    activityBuilder.append(args[i]).append(" ");
                }
                activity = new String(activityBuilder).trim();
            }

        } else {

            System.out.print(consolePrefix + "Please enter bot access token: ");

            Scanner input = new Scanner(System.in);
            token = input.nextLine();
            input.close();

            System.out.println();

        }

        System.out.println(consolePrefix + "OnlineStatus: " + status);
        System.out.println(consolePrefix + "ActivityType: " + activityType);
        System.out.println(consolePrefix + "Activity: \"" + activity + "\"");

        JDABuilder.createLight(token)
                .enableIntents(
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_BANS,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS
                )
                .disableIntents(
                        GatewayIntent.GUILD_EMOJIS,
                        GatewayIntent.GUILD_WEBHOOKS,
                        GatewayIntent.GUILD_INVITES,
                        GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.GUILD_MESSAGE_TYPING,
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                        GatewayIntent.DIRECT_MESSAGE_TYPING
                )
                .enableCache(CacheFlag.VOICE_STATE, CacheFlag.MEMBER_OVERRIDES)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setBulkDeleteSplittingEnabled(false)
                .setStatus(status)
                .setActivity(Activity.of(activityType, activity, "https://twitch.tv/codeerrortv"))
                .addEventListeners(new SocietyBot())
                .build().awaitReady();

    }

    @Override
    public void onEvent(@NotNull GenericEvent e) {

        if (e instanceof ReadyEvent) {

            for (Guild guild : e.getJDA().getGuilds()) {
                if (guild.getRolesByName("DJ", false).isEmpty()) {
                    guild.createRole().setName("DJ").setHoisted(false).setMentionable(false).complete();
                }
            }

            System.out.println(consolePrefix + "Logged in as " + e.getJDA().getSelfUser().getAsTag());

        } else if (e instanceof GuildJoinEvent event) {

            System.out.println(consolePrefix + "Joined guild \"" + event.getGuild().getName() + "\" (" + event.getGuild().getId() + ")");

        } else if (e instanceof GuildLeaveEvent event) {

            System.out.println(consolePrefix + "Left guild: \"" + event.getGuild().getName() + "\" (" + event.getGuild().getId() + ")");

        } else if (e instanceof GuildMessageReceivedEvent event) {

            if (event.getAuthor().isBot() || event.getMember() == null) return;

            String msg = event.getMessage().getContentRaw();
            Member sender = event.getMember();
            TextChannel channel = event.getChannel();
            Role djRole = event.getGuild().getRolesByName("DJ", false).get(0);

            if (sender.getRoles().contains(djRole) || sender.hasPermission(Permission.ADMINISTRATOR) || sender.getId().equals("191640313016745984")) {

                if (msg.equals(prefix + "about") || (msg.equals(prefix + "info"))) {

                    OffsetDateTime timestamp = event.getMessage().getTimeCreated();
                    EmbedBuilder embed = new EmbedBuilder();
                    User selfUser = event.getJDA().getSelfUser();
                    Member selfMember = event.getGuild().getSelfMember();

                    embed.setColor(new Color(255, 51, 51));
                    embed.setFooter("SocietyBot");
                    embed.setTimestamp(timestamp);
                    embed.setThumbnail(selfUser.getEffectiveAvatarUrl());
                    embed.setTitle("SocietyBot  -  About");

                    embed.appendDescription("**Version:**  `1.1`\n");
                    embed.appendDescription("**Author:**  <@191640313016745984>  (`CodeError#0001`)\n\n");

                    embed.appendDescription("Currently logged in as **" + selfUser.getAsTag() + "**.\n\n");

                    long uptimeMillis = ManagementFactory.getRuntimeMXBean().getUptime();
                    long seconds = uptimeMillis / 1000;
                    long minutes = seconds / 60;
                    long hours = minutes / 60;
                    long days = hours / 24;
                    String uptime = days + "d " + hours % 24 + "h " + minutes % 60 + "m " + seconds % 60 + "s";
                    embed.appendDescription("**Uptime:**  `" + uptime + "`\n");
                    embed.appendDescription("**Latency:**  `" + event.getJDA().getGatewayPing() + "ms`\n\n");

                    embed.appendDescription("**User Mention:**  <@" + selfUser.getId() + ">\n");
                    embed.appendDescription("**User ID:**  `" + selfUser.getId() + "`\n");
                    embed.appendDescription("**User Created:**  <t:" + selfUser.getTimeCreated().toEpochSecond() + ":R>\n\n");

                    embed.appendDescription("**Server Join Date:**  <t:" + selfMember.getTimeJoined().toEpochSecond() + ":R>\n");
                    embed.appendDescription("**Assigned Roles:**  ");
                    if (!selfMember.getRoles().isEmpty()) {
                        for (Role role : selfMember.getRoles()) {
                            embed.appendDescription("<@&" + role.getId() + ">  ");
                        }
                    } else {
                        embed.appendDescription("`None`");
                    }
                    embed.appendDescription("\n");

                    channel.sendMessageEmbeds(embed.build()).queue(message -> message.editMessageComponents(
                            ActionRow.of(
                                    Button.link("https://discord.com/api/oauth2/authorize?client_id=919757594971738142&permissions=8&scope=bot", "Invite"),
                                    Button.link("https://github.com/CodeTheDev/societybot", "View Source Code")
                            )
                    ).queue());

                } else if (msg.contains(prefix + "leaveguild") && sender.getId().equals("191640313016745984")) {

                    if (msg.indexOf(prefix + "leaveguild") > 0) return;

                    String[] args = msg.split(" ");
                    if (args.length == 2) {
                        Guild targetGuild = event.getJDA().getGuildById(args[1]);

                        if (targetGuild == null) {
                            channel.sendMessage(":x:  Cannot leave target guild `" + args[1] + "`. Bot is either not in target guild or target guild ID is invalid.").queue();
                            return;
                        }

                        targetGuild.leave().queue();
                        channel.sendMessage(":white_check_mark:  Left target guild  **__" + targetGuild.getName() + "__**  (`" + targetGuild.getId() + "`) successfully!").queue();
                    }

                } else if (msg.equals(prefix + "listguilds") && sender.getId().equals("191640313016745984")) {

                    List<Guild> guilds = event.getJDA().getGuilds();

                    StringBuilder responseBuilder = new StringBuilder();
                    for (Guild guild : guilds) {
                        responseBuilder.append("**__").append(guild.getName()).append("__**  (`").append(guild.getId()).append("`)\n");
                    }
                    String response = new String(responseBuilder);

                    channel.sendMessage(response).queue();

                } else if (msg.equals(prefix + "join") || msg.equals(prefix + "connect")) {

                    GuildVoiceState senderVoice = event.getMember().getVoiceState();

                    if (senderVoice == null) {
                        channel.sendMessage(":x:  Cannot connect to voice channel. Sender is not in a voice channel.").queue();
                        return;
                    }

                    VoiceChannel vc = senderVoice.getChannel();
                    if (vc == null) {
                        channel.sendMessage(":x:  Cannot connect to voice channel. Sender is not in a voice channel.").queue();
                        return;
                    }

                    AudioManager audio = event.getGuild().getAudioManager();
                    if (audio.isConnected()) {
                        channel.sendMessageFormat(":x:  Already connected to a voice channel (**%s**).", vc.getName()).queue();
                        return;
                    }

                    audio.openAudioConnection(vc);
                    channel.sendMessageFormat(":loud_sound:  Joined voice channel **%s**.", vc.getName()).queue();

                } else if (msg.equals(prefix + "disconnect") || msg.equals(prefix + "dc") || msg.equals(prefix + "leave")) {

                    PlayerManager manager = PlayerManager.getInstance();
                    GuildVoiceState senderVoice = event.getMember().getVoiceState();

                    if (senderVoice == null) {
                        channel.sendMessage(":x:  Cannot disconnect bot. Sender is not in my voice channel.").queue();
                        return;
                    }

                    VoiceChannel vc = senderVoice.getChannel();
                    if (vc == null) {
                        channel.sendMessage(":x:  Cannot disconnect bot. Sender is not in my voice channel.").queue();
                        return;
                    }

                    AudioManager audio = event.getGuild().getAudioManager();
                    if (!audio.isConnected()) {
                        channel.sendMessage(":x:  Cannot disconnect bot. Bot is already disconnected.").queue();
                        return;
                    }
                    if (!vc.getMembers().contains(sender)) {
                        channel.sendMessage(":x:  Cannot disconnect bot. Sender is not in my voice channel.").queue();
                        return;
                    }

                    manager.getGuildMusicManager(event.getGuild()).player.destroy();
                    audio.closeAudioConnection();
                    channel.sendMessageFormat(":mute:  Disconnected from voice channel **%s**.", vc.getName()).queue();

                } else if (msg.contains(prefix + "play")) {

                    if (msg.indexOf(prefix + "play") > 0) return;

                    PlayerManager manager = PlayerManager.getInstance();
                    GuildVoiceState senderVoice = event.getMember().getVoiceState();

                    if (senderVoice == null) {
                        channel.sendMessage(":x:  Cannot queue track. Sender is not in my voice channel.").queue();
                        return;
                    }

                    VoiceChannel vc = senderVoice.getChannel();
                    if (vc == null) {
                        channel.sendMessage(":x:  Cannot queue track. Sender is not in my voice channel.").queue();
                        return;
                    }
                    if (!vc.getMembers().contains(sender)) {
                        channel.sendMessage(":x:  Cannot queue track. Sender is not in my voice channel.").queue();
                        return;
                    }

                    AudioManager audio = event.getGuild().getAudioManager();
                    if (!audio.isConnected()) audio.openAudioConnection(vc);

                    if (manager.getGuildMusicManager(event.getGuild()).player.isPaused()) {
                        manager.getGuildMusicManager(event.getGuild()).player.setPaused(false);
                        channel.sendMessage(":arrow_forward:  Resumed paused track.").queue();
                        return;
                    }

                    String[] args = msg.split(" ");
                    if (args.length == 2) {
                        if (manager.getGuildMusicManager(event.getGuild()).player.getVolume() != 50) {
                            manager.load(channel, args[1]);
                        } else {
                            manager.load(channel, args[1]);
                            manager.getGuildMusicManager(event.getGuild()).player.setVolume(50);
                        }
                    }

                } else if (msg.equals(prefix + "pause")) {

                    PlayerManager manager = PlayerManager.getInstance();
                    GuildVoiceState senderVoice = event.getMember().getVoiceState();

                    if (senderVoice == null) {
                        channel.sendMessage(":x:  Cannot pause. Sender is not in my voice channel.").queue();
                        return;
                    }

                    VoiceChannel vc = senderVoice.getChannel();
                    if (vc == null) {
                        channel.sendMessage(":x:  Cannot pause. Sender is not in my voice channel.").queue();
                        return;
                    }

                    AudioManager audio = event.getGuild().getAudioManager();
                    if (!audio.isConnected()) {
                        channel.sendMessage(":x:  Cannot pause. Bot is not connected.").queue();
                        return;
                    }
                    if (!vc.getMembers().contains(sender)) {
                        channel.sendMessage(":x:  Cannot pause. Sender is not in my voice channel.").queue();
                        return;
                    }

                    manager.getGuildMusicManager(event.getGuild()).player.setPaused(true);
                    channel.sendMessage(":pause_button:  Paused playing track.").queue();

                } else if (msg.equals(prefix + "stop")) {

                    PlayerManager manager = PlayerManager.getInstance();
                    GuildVoiceState senderVoice = event.getMember().getVoiceState();

                    if (senderVoice == null) {
                        channel.sendMessage(":x:  Cannot stop. Sender is not in my voice channel.").queue();
                        return;
                    }

                    VoiceChannel vc = senderVoice.getChannel();
                    if (vc == null) {
                        channel.sendMessage(":x:  Cannot stop. Sender is not in my voice channel.").queue();
                        return;
                    }

                    AudioManager audio = event.getGuild().getAudioManager();
                    if (!audio.isConnected()) {
                        channel.sendMessage(":x:  Cannot stop. Bot is not connected.").queue();
                        return;
                    }
                    if (!vc.getMembers().contains(sender)) {
                        channel.sendMessage(":x:  Cannot stop. Sender is not in my voice channel.").queue();
                        return;
                    }

                    manager.getGuildMusicManager(event.getGuild()).player.stopTrack();
                    channel.sendMessage(":stop_button:  Stopped playing track.").queue();

                } else if (msg.equals(prefix + "skip")) {

                    PlayerManager manager = PlayerManager.getInstance();
                    GuildVoiceState senderVoice = event.getMember().getVoiceState();

                    if (senderVoice == null) {
                        channel.sendMessage(":x:  Cannot skip. Sender is not in my voice channel.").queue();
                        return;
                    }

                    VoiceChannel vc = senderVoice.getChannel();
                    if (vc == null) {
                        channel.sendMessage(":x:  Cannot skip. Sender is not in my voice channel.").queue();
                        return;
                    }

                    AudioManager audio = event.getGuild().getAudioManager();
                    if (!audio.isConnected()) {
                        channel.sendMessage(":x:  Cannot skip. Bot is not connected.").queue();
                        return;
                    }
                    if (!vc.getMembers().contains(sender)) {
                        channel.sendMessage(":x:  Cannot skip. Sender is not in my voice channel.").queue();
                        return;
                    }

                    manager.getGuildMusicManager(event.getGuild()).scheduler.nextTrack();
                    channel.sendMessage(":fast_forward:  Skipped to next track in queue.").queue();

                } else if (msg.contains(prefix + "volume")) {

                    if (msg.indexOf(prefix + "volume") > 0) return;

                    PlayerManager manager = PlayerManager.getInstance();
                    GuildVoiceState senderVoice = event.getMember().getVoiceState();

                    if (senderVoice == null) {
                        channel.sendMessage(":x:  Cannot adjust volume. Sender is not in my voice channel.").queue();
                        return;
                    }

                    VoiceChannel vc = senderVoice.getChannel();
                    if (vc == null) {
                        channel.sendMessage(":x:  Cannot adjust volume. Sender is not in my voice channel.").queue();
                        return;
                    }

                    AudioManager audio = event.getGuild().getAudioManager();
                    if (!audio.isConnected()) {
                        channel.sendMessage(":x:  Cannot adjust volume. Bot is not connected.").queue();
                        return;
                    }
                    if (!vc.getMembers().contains(sender)) {
                        channel.sendMessage(":x:  Cannot adjust volume. Sender is not in my voice channel.").queue();
                        return;
                    }

                    String[] args = msg.split(" ");
                    if (args.length == 2) {
                        int volume = Integer.parseInt(args[1]);
                        if (volume > 100 && (!sender.hasPermission(Permission.ADMINISTRATOR) || !sender.getId().equals("191640313016745984"))) volume = 100;
                        manager.getGuildMusicManager(event.getGuild()).player.setVolume(volume);
                        channel.sendMessageFormat(":sound:  Set volume to **%d**.", volume).queue();
                    }

                }

            } else {

                channel.sendMessage(":x:  You don't have permission to use that! You need the `DJ` role or the `Administrator` permission node.").queue();

            }

        }

    }

}
