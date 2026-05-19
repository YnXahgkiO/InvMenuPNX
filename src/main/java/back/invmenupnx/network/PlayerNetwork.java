package back.invmenupnx.network;

import java.util.ArrayDeque;
import java.util.concurrent.atomic.AtomicLong;

import back.invmenupnx.InvMenuPlugin;
import cn.nukkit.Player;
import cn.nukkit.network.protocol.NetworkStackLatencyPacket;

public final class PlayerNetwork {

    private static final AtomicLong TIMESTAMP_GEN = new AtomicLong(1);
    private static final int FALLBACK_TICKS = 8;

    private final Player player;
    private final ArrayDeque<Entry> queue = new ArrayDeque<>();

    private record Entry(long timestamp, Runnable then) {}

    public PlayerNetwork(Player player) {
        this.player = player;
    }

    public void waitForAck(Runnable then) {
        long ts = TIMESTAMP_GEN.getAndIncrement();
        queue.add(new Entry(ts, then));

        NetworkStackLatencyPacket pk = new NetworkStackLatencyPacket();
        pk.timestamp = ts;
        pk.unknownBool = true;
        player.dataPacket(pk);

        InvMenuPlugin.getInstance().getServer().getScheduler().scheduleDelayedTask(
                InvMenuPlugin.getInstance(),
                () -> fireIfPending(ts),
                FALLBACK_TICKS
        );
    }

    public void onAck(long timestamp) {
        Entry front = queue.peek();
        if (front != null && front.timestamp() == timestamp) {
            queue.poll();
            front.then().run();
        }
    }

    private void fireIfPending(long timestamp) {
        Entry front = queue.peek();
        if (front != null && front.timestamp() == timestamp) {
            queue.poll();
            front.then().run();
        }
    }

    public void drop() {
        queue.clear();
    }
}
