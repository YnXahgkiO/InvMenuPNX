package back.invmenupnx;

import cn.nukkit.Player;
import cn.nukkit.inventory.fake.FakeInventory;
import cn.nukkit.inventory.fake.FakeInventoryType;
import back.invmenupnx.fake.InvMenuFakeInventory;
import back.invmenupnx.graphic.InvMenuGraphic;
import back.invmenupnx.session.PlayerSession;
import back.invmenupnx.session.PlayerSessionManager;
import back.invmenupnx.transaction.DeterministicInvMenuTransaction;
import back.invmenupnx.transaction.InvMenuTransaction;
import back.invmenupnx.transaction.InvMenuTransactionResult;
import back.invmenupnx.type.CustomInvMenuType;
import back.invmenupnx.type.InvMenuType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public final class InvMenu {

    public static InvMenu create(InvMenuType type) {
        return new InvMenu(type.getPnxType(), type.getSize(), type.getId(), type.createGraphic());
    }

    public static InvMenu create(CustomInvMenuType type) {
        return new InvMenu(type.getPnxType(), type.getSize(), type.getId(), type.createGraphic());
    }

    public static InvMenu create(String typeId) {
        InvMenuType builtin = InvMenuHandler.getType(typeId);
        if (builtin != null) return create(builtin);

        CustomInvMenuType custom = InvMenuHandler.getCustomType(typeId);
        if (custom != null) return create(custom);

        throw new IllegalArgumentException("Unknown InvMenu type: " + typeId);
    }

    public static InvMenuListener readonly() {
        return InvMenuTransaction::discard;
    }

    public static InvMenuListener readonly(@Nullable InvMenuListener listener) {
        if (listener == null) return readonly();
        return txn -> {
            DeterministicInvMenuTransaction det = new DeterministicInvMenuTransaction(
                    txn.getPlayer(), txn.getInventory(), txn.getSlot(),
                    txn.getItemClicked(), txn.getItemClickedWith(), txn.getAction()
            );
            InvMenuTransactionResult result = listener.onTransaction(det);
            return InvMenuTransactionResult.cancelled().then(result.getPostCallback());
        };
    }

    private final FakeInventory inventory;
    private @Nullable InvMenuListener listener;
    private @Nullable InvMenuCloseListener closeListener;

    private InvMenu(FakeInventoryType pnxType, int size, String defaultTitle, InvMenuGraphic graphic) {
        this.inventory = new InvMenuFakeInventory(pnxType, defaultTitle, size, graphic);
        installItemHandler();
        installCloseHandler();
    }

    public InvMenu setTitle(String title) {
        this.inventory.setTitle(title);
        return this;
    }

    public InvMenu setListener(@Nullable InvMenuListener listener) {
        this.listener = listener;
        return this;
    }

    public InvMenu setCloseListener(@Nullable InvMenuCloseListener closeListener) {
        this.closeListener = closeListener;
        return this;
    }

    public FakeInventory getInventory() {
        return inventory;
    }

    public void send(Player player) {
        send(player, null, null);
    }

    public void send(Player player, @Nullable String title) {
        send(player, title, null);
    }

    public void send(Player player, @Nullable SendCallback callback) {
        send(player, null, callback);
    }

    public void send(Player player, @Nullable String title, @Nullable SendCallback callback) {
        if (title != null) {
            this.inventory.setTitle(title);
        }

        int windowId = player.addWindow(this.inventory);
        boolean success = windowId != -1;

        if (success) {
            PlayerSession session = PlayerSessionManager.getOrCreate(player);
            session.setCurrentMenu(this);
        }

        if (callback != null) {
            callback.onSend(success);
        }
    }

    public void send(Collection<? extends Player> players) {
        for (Player player : players) {
            send(player);
        }
    }

    public void close(Player player) {
        player.removeWindow(this.inventory);
    }

    public void closeAll() {
        for (Player viewer : List.copyOf(inventory.getViewers())) {
            close(viewer);
        }
    }

    private void installItemHandler() {
        this.inventory.setDefaultItemHandler((inv, slot, oldItem, newItem, event) -> {
            if (listener == null) {
                event.setCancelled(true);
                return;
            }

            Player player = event.getPlayer();
            InvMenuTransaction txn = new InvMenuTransaction(player, inv, slot, oldItem, newItem, event);
            InvMenuTransactionResult result = listener.onTransaction(txn);

            if (result.isCancelled()) {
                event.setCancelled(true);
            }

            Consumer<Player> postCallback = result.getPostCallback();
            if (postCallback != null) {
                PlayerSessionManager.getOrCreate(player).setPendingCallback(postCallback);
            }
        });
    }

    private void installCloseHandler() {
        this.inventory.setOnCloseHandler(player -> {
            PlayerSession session = PlayerSessionManager.get(player);
            if (session == null || session.getCurrentMenu() != this) {
                return;
            }
            if (closeListener != null) {
                closeListener.onClose(player, this.inventory);
            }
            session.firePendingCallback(player);
            session.setCurrentMenu(null);
        });
    }
}
