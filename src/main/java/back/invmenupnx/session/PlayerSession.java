package back.invmenupnx.session;

import org.powernukkitx.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

public final class PlayerSession {

    private final UUID uuid;
    private @Nullable Object currentMenu;
    private @Nullable Consumer<Player> pendingThenCallback;

    public PlayerSession(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
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
