package raids;

import core.DBManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static core.MessageListener.*;
import static net.dv8tion.jda.core.utils.SimpleLog.Level.INFO;

/**
 * Created by Owner on 2/07/2017.
 */
public class RaidLobby {

    String roleId = null;
    String channelId = null;

    public String lobbyCode;

    public RaidSpawn spawn;

    HashSet<String> memberIds = new HashSet<>();

    static SimpleLog raidLobbyLog = SimpleLog.getLog("raid-lobbies");

    ScheduledExecutorService shutDownService = null;

    public RaidLobby(RaidSpawn raidSpawn, String lobbyCode){
        this.spawn = raidSpawn;
        this.lobbyCode = lobbyCode;
    }

    public void joinLobby(String userId){
        if(spawn.raidEnd.before(DBManager.getCurrentTime())) return;

        TextChannel channel = null;

        if(memberIds.size() == 0 && shutDownService == null){
            roleId = guild.getController().createRole().complete().getId();

            Role role = guild.getRoleById(roleId);

            role.getManager().setName(String.format("raid-%s", lobbyCode)).queue(success -> role.getManager().setMentionable(true).queue());

            channelId = guild.getController().createTextChannel(String.format("raid-lobby-%s", lobbyCode)).complete().getId();

            channel = guild.getTextChannelById(channelId);

            channel.createPermissionOverride(guild.getPublicRole()).setDeny(Permission.MESSAGE_READ).queue();

            channel.createPermissionOverride(role).setAllow(Permission.MESSAGE_READ,Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY).queue();
            channel.createPermissionOverride(guild.getRoleById(config.novabotRole())).setAllow(Permission.MESSAGE_READ,Permission.MESSAGE_WRITE).complete();

            raidLobbyLog.log(INFO,String.format("First join for lobbyCode %s, created channel",lobbyCode));
        }

        Member member = guild.getMemberById(userId);

        guild.getController().addRolesToMember(member,guild.getRoleById(roleId)).queue();

        memberIds.add(userId);

        if(channel == null){
            channel = guild.getTextChannelById(channelId);
        }

        if(shutDownService != null){
            raidLobbyLog.log(INFO,"Cancelling lobby shutdown");
            shutDownService.shutdown();
            shutDownService = null;
        }

        channel.sendMessageFormat("Welcome %s to the raid lobby!\nThere are now %s users in the lobby.",member,memberIds.size()).queue();
        channel.sendMessage(getStatusMessage()).queue();
    }

    public Role getRole() {
        return guild.getRoleById(roleId);
    }

    public int memberCount() {
        return memberIds.size();
    }

    public void leaveLobby(String id) {
        guild.getController().removeRolesFromMember(guild.getMemberById(id),getRole()).queue();
        memberIds.remove(id);
        getChannel().sendMessageFormat("%s left the lobby, there are now %s users in the lobby.",guild.getMemberById(id),memberCount()).queue();

        if(memberCount() == 0){
            end(10);
        }
    }

    public TextChannel getChannel() {
        return guild.getTextChannelById(channelId);
    }

    public void end(int delay) {
        if(channelId == null || roleId == null) return;

        if(memberCount() == 0){
            getChannel().sendMessage("There are no users in the lobby, it will be closed in 5 minutes").queue();
        }else{
            getChannel().sendMessageFormat("%s, the raid has ended and the raid lobby will be closed in 5 minutes",getRole()).queue();
        }

        Runnable shutDownTask = () -> {
            getChannel().delete().queue();
            getRole().delete().queue();
            raidLobbyLog.log(INFO, String.format("Ended raid lobby %s", lobbyCode));
            lobbyManager.activeLobbies.remove(lobbyCode);
            //TODO: remove all emojis and edit messages so its clear this raid is ended
        };

        shutDownService = Executors.newSingleThreadScheduledExecutor();

        shutDownService.schedule(shutDownTask, delay, TimeUnit.MINUTES);
        shutDownService.shutdown();
    }

    public Message getStatusMessage(){
        EmbedBuilder embedBuilder = new EmbedBuilder();

        if(spawn.bossId != 0) {
            embedBuilder.setTitle(String.format("Raid status for %s in %s - Lobby %s",
                    spawn.properties.get("pkmn"),
                    spawn.properties.get("city"),
                    lobbyCode));

            embedBuilder.setDescription("Type `!status` to see this message again, and `!help` to see all available raid lobby commands.");

            embedBuilder.addField("Lobby Members", String.valueOf(memberCount()), false);
            embedBuilder.addField("Address", String.format("%s %s, %s",
                    spawn.properties.get("street_num"),
                    spawn.properties.get("street"),
                    spawn.properties.get("city")
            ), false);
            embedBuilder.addField("Gym Name", spawn.properties.get("gym_name"), false);
            embedBuilder.addField("Raid End Time", String.format("%s (%s)",
                    spawn.properties.get("24h_end"),
                    spawn.timeLeft(spawn.raidEnd)),
                    false);
            embedBuilder.addField("Boss Moveset", String.format("%s - %s", spawn.move_1, spawn.move_2), false);

            String weaknessEmoteStr = "";

            for (String s : Raid.getBossWeaknessEmotes(spawn.bossId)) {
                Emote emote = Raid.emotes.get(s);
                weaknessEmoteStr += emote.getAsMention();
            }

            embedBuilder.addField("Weak To", weaknessEmoteStr, true);

            String strengthEmoteStr = "";

            for (String s : Raid.getBossStrengthsEmote(spawn.bossId)) {
                strengthEmoteStr += Raid.emotes.get(s).getAsMention();
            }

            embedBuilder.addField("Strong Against", strengthEmoteStr, true);

            embedBuilder.setThumbnail(spawn.getIcon());
            embedBuilder.setImage(spawn.getImage());
        }else{
            embedBuilder.setTitle(String.format("Raid status for level %s egg in %s - Lobby %s",
                    spawn.properties.get("level"),
                    spawn.properties.get("city"),
                    lobbyCode));

            embedBuilder.setDescription("Type `!status` to see this message again, and `!help` to see all available raid lobby commands.");

            embedBuilder.addField("Lobby Members", String.valueOf(memberCount()), false);
            embedBuilder.addField("Address", String.format("%s %s, %s",
                    spawn.properties.get("street_num"),
                    spawn.properties.get("street"),
                    spawn.properties.get("city")
            ), false);
            embedBuilder.addField("Gym Name", spawn.properties.get("gym_name"), false);
            embedBuilder.addField("Raid Start Time", String.format("%s (%s)",
                    spawn.properties.get("24h_start"),
                    spawn.timeLeft(spawn.battleStart)),
                    false);

            embedBuilder.setThumbnail(spawn.getIcon());
            embedBuilder.setImage(spawn.getImage());
        }

        MessageBuilder messageBuilder = new MessageBuilder().setEmbed(embedBuilder.build());

        return messageBuilder.build();
    }

    public Message getBossInfoMessage() {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle(String.format("%s - Level %s raid",spawn.properties.get("pkmn"),spawn.properties.get("level")));
        embedBuilder.addField("CP",spawn.properties.get("cp"),false);
        embedBuilder.addField("Moveset",String.format("%s - %s",spawn.move_1,spawn.move_2),false);
        String weaknessEmoteStr = "";

        for (String s : Raid.getBossWeaknessEmotes(spawn.bossId)) {
            weaknessEmoteStr += Raid.emotes.get(s).getAsMention();
        }

        embedBuilder.addField("Weak To",weaknessEmoteStr,true);

        String strengthEmoteStr = "";

        for (String s : Raid.getBossStrengthsEmote(spawn.bossId)) {
            strengthEmoteStr += Raid.emotes.get(s).getAsMention();
        }

        embedBuilder.addField("Strong Against",strengthEmoteStr,true);

        embedBuilder.setThumbnail(spawn.getIcon());

        MessageBuilder messageBuilder = new MessageBuilder().setEmbed(embedBuilder.build());

        return messageBuilder.build();
    }

    public String getTeamMessage() {
        String str = String.format("There are %s users in this raid team:\n\n", memberCount());

        for (String memberId : memberIds) {
            str += String.format("  %s%n",guild.getMemberById(memberId).getEffectiveName());
        }

        return str;
    }

    public boolean containsUser(String id) {
        return memberIds.contains(id);
    }

    public void alertEggHatched() {
        getChannel().sendMessageFormat("%s the raid egg has hatched into a %s!",getRole(),spawn.properties.get("pkmn")).queue();
        getChannel().sendMessage(getStatusMessage()).queue();
    }

    public void alertRaidNearlyOver() {
        getChannel().sendMessageFormat("%s the raid is going to end in %s!",getRole(),spawn.timeLeft(spawn.raidEnd)).queue();
    }
}
