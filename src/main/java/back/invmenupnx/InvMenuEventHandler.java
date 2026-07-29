package back.invmenupnx;

import org.powernukkitx.Player;
import org.powernukkitx.event.EventHandler;
import org.powernukkitx.event.EventPriority;
import org.powernukkitx.event.Listener;
import org.powernukkitx.event.player.PlayerJoinEvent;
import org.powernukkitx.event.player.PlayerQuitEvent;
import back.invmenupnx.session.PlayerSession;
import back.invmenupnx.session.PlayerSessionManager;

public final class InvMenuEventHandler implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        PlayerSessionManager.create(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerSession session = PlayerSessionManager.get(player);
        if (session != null) {
            session.firePendingCallback(player);
        }
        PlayerSessionManager.destroy(player);
    }
}
