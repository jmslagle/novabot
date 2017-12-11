package notifier;

import core.DBManager;
import core.MessageListener;
import core.NotificationLimit;
import net.dv8tion.jda.core.entities.User;

import static core.MessageListener.config;
import static core.MessageListener.guild;

public class NotificationSender {

    void checkSupporterStatus(User user) {
        NotificationLimit limit = config.getNotificationLimit(guild.getMember(user));

        boolean passedChecks = true;

        int pokeCount = DBManager.countPokemon(user.getId(), config.countLocationsInLimits());
        if (pokeCount > limit.pokemonLimit){
            resetUser(user,limit);
            passedChecks = false;
        }

        if (passedChecks) {
            int presetCount = DBManager.countPresets(user.getId(), config.countLocationsInLimits());
            if (presetCount > limit.presetLimit) {
                resetUser(user,limit);
                passedChecks = false;

            }

            if (passedChecks) {
                int raidCount = DBManager.countRaids(user.getId(), config.countLocationsInLimits());
                if (raidCount > limit.raidLimit) {
                    resetUser(user,limit);
                }
            }
        }
    }

    void resetUser(User user, NotificationLimit newLimit) {
        DBManager.resetUser(user.getId());

        user.openPrivateChannel().queue(channel -> {
            channel.sendMessageFormat("Hi %s, I noticed that recently your supporter status has changed." +
                    " As a result I have cleared your settings. At your current level you can add up to %s to your settings.",user,newLimit.toWords()).queue();
        });

        if(config.loggingEnabled()){
            MessageListener.roleLog.sendMessageFormat("%s's supporter status has changed, requiring a reset of their settings. They have been informed via PM.",user).queue();
        }
    }
}
