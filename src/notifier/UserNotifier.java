package notifier;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

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
        if (!user.hasPrivateChannel()) {
            user.openPrivateChannel().complete();
        }
        user.openPrivateChannel().queue(success -> success.sendMessage(message).queue());
    }
}
