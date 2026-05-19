package back.invmenupnx;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.network.protocol.NetworkStackLatencyPacket;
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
            session.getNetwork().drop();
            session.firePendingCallback(player);
        }
        PlayerSessionManager.destroy(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDataPacketReceive(DataPacketReceiveEvent event) {
        if (!(event.getPacket() instanceof NetworkStackLatencyPacket pk)) return;
        PlayerSession session = PlayerSessionManager.get(event.getPlayer());
        if (session != null) {
            session.getNetwork().onAck(pk.timestamp);
        }
    }
}
