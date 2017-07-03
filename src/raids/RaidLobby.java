package raids;

import core.DBManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
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

    String lobbyCode;

    RaidSpawn spawn;

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

            role.getManager().setName(String.format("raid-%s", lobbyCode)).queue();

            channelId = guild.getController().createTextChannel(String.format("raid-lobby-%s", lobbyCode)).complete().getId();

            channel = guild.getTextChannelById(channelId);

            channel.createPermissionOverride(guild.getPublicRole()).setDeny(Permission.MESSAGE_READ).queue();

            channel.createPermissionOverride(role).setAllow(Permission.MESSAGE_READ).queue();
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

        channel.sendMessageFormat("Welcome %s to the raid lobby!\nThere are now %s users in the lobby",member,memberIds.size()).queue();
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
            end();
        }
    }

    private TextChannel getChannel() {
        return guild.getTextChannelById(channelId);
    }

    public void end() {
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

        shutDownService.schedule(shutDownTask, 5, TimeUnit.MINUTES);
        shutDownService.shutdown();
    }

    public Message getStatusMessage(){
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle(String.format("Raid status for %s in %s - Lobby %s",
                spawn.properties.get("pkmn"),
                spawn.properties.get("city"),
                lobbyCode));

        embedBuilder.setThumbnail(spawn.getIcon());

        MessageBuilder messageBuilder = new MessageBuilder().setEmbed(embedBuilder.build());

        return messageBuilder.build();
    }
}
