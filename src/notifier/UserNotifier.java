package notifier;

import net.dv8tion.jda.core.entities.*;
import core.*;

class UserNotifier implements Runnable
{
    private final User user;
    private final Message message;

    public UserNotifier(final User user, final Message message) {
        this.user = user;
        this.message = message;
    }

    @Override
    public void run() {
        if (MessageListener.guild.getMember(user).getRoles().contains("Admin")) {
            return;
        }
        if (!user.hasPrivateChannel()) {
            user.openPrivateChannel().complete();
        }
        user.getPrivateChannel().sendMessage(message).queue();
    }
}
