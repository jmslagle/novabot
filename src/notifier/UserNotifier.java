package notifier;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import static core.MessageListener.WHITE_GREEN_CHECK;
import static core.MessageListener.config;

class UserNotifier implements Runnable
{
    private final User user;
    private final Message message;
    private final boolean raid;

    public UserNotifier(final User user, final Message message, boolean raid) {
        this.user = user;
        this.message = message;
        this.raid = raid;
    }

    @Override
    public void run() {
        if (!user.hasPrivateChannel()) {
            user.openPrivateChannel().complete();
        }
        user.openPrivateChannel().queue(success -> success.sendMessage(message).queue(m -> {
            if(raid && config.isRaidOrganisationEnabled()){
                m.addReaction(WHITE_GREEN_CHECK).queue();
            }
        }));
    }
}
