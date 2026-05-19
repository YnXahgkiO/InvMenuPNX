package back.invmenupnx.session;

import cn.nukkit.Player;
import back.invmenupnx.network.PlayerNetwork;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

public final class PlayerSession {

    private final UUID uuid;
    private final PlayerNetwork network;
    private @Nullable Object currentMenu;
    private @Nullable Consumer<Player> pendingThenCallback;

    public PlayerSession(UUID uuid, Player player) {
        this.uuid = uuid;
        this.network = new PlayerNetwork(player);
    }

    public UUID getUuid() {
        return uuid;
    }

    public PlayerNetwork getNetwork() {
        return network;
    }

    public void setCurrentMenu(@Nullable Object menu) {
        this.currentMenu = menu;
    }

    public @Nullable Object getCurrentMenu() {
        return currentMenu;
    }

    public void setPendingCallback(@Nullable Consumer<Player> callback) {
        this.pendingThenCallback = callback;
    }

    public void firePendingCallback(Player player) {
        Consumer<Player> cb = this.pendingThenCallback;
        this.pendingThenCallback = null;
        if (cb != null) {
            cb.accept(player);
        }
    }

    public boolean hasPendingCallback() {
        return pendingThenCallback != null;
    }
}
