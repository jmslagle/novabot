package notifier;

import net.dv8tion.jda.core.entities.*;
import core.*;

class UserNotifier implements Runnable
{
    public UserNotifier(final User user, final Message message) {
        if (MessageListener.cbrSightings.getMember(user).getRoles().contains("Admin")) {
            return;
        }
        if (!user.hasPrivateChannel()) {
            user.openPrivateChannel().complete();
        }
        user.getPrivateChannel().sendMessage(message).queue();
    }

    @Override
    public void run() {
    }
}
