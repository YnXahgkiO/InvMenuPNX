package back.invmenupnx.template;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import back.invmenupnx.InvMenu;
import back.invmenupnx.InvMenuPlugin;
import back.invmenupnx.transaction.InvMenuTransactionResult;
import back.invmenupnx.type.InvMenuType;
import back.invmenupnx.util.ItemBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public final class ConfirmMenu {

    private static Item defaultConfirmItem() {
        return ItemBuilder.of(ItemID.LIME_DYE).name("§a§lCONFIRM").lore("§7Click to confirm").build();
    }

    private static Item defaultCancelItem() {
        return ItemBuilder.of(ItemID.RED_DYE).name("§c§lCANCEL").lore("§7Click to cancel").build();
    }

    private static Item defaultFillerItem() {
        return ItemBuilder.of(ItemID.STAINED_GLASS_PANE, 7).name("§r").build();
    }

    public static ConfirmMenu create() {
        return new ConfirmMenu();
    }

    private String title = "§8Confirm";
    private @Nullable Item questionItem = null;
    private @Nullable Consumer<Player> onConfirm = null;
    private @Nullable Consumer<Player> onCancel = null;

    private ConfirmMenu() {}

    public ConfirmMenu setTitle(String title) {
        this.title = title;
        return this;
    }

    public ConfirmMenu setQuestion(Item item) {
        this.questionItem = item;
        return this;
    }

    public ConfirmMenu onConfirm(Consumer<Player> callback) {
        this.onConfirm = callback;
        return this;
    }

    public ConfirmMenu onCancel(Consumer<Player> callback) {
        this.onCancel = callback;
        return this;
    }

    public void send(Player player) {
        InvMenu menu = InvMenu.create(InvMenuType.HOPPER).setTitle(title);

        menu.getInventory().setItem(0, defaultConfirmItem());
        menu.getInventory().setItem(1, defaultFillerItem());
        menu.getInventory().setItem(2, questionItem != null ? questionItem : defaultFillerItem());
        menu.getInventory().setItem(3, defaultFillerItem());
        menu.getInventory().setItem(4, defaultCancelItem());

        final Consumer<Player> confirmCallback = onConfirm;
        final Consumer<Player> cancelCallback = onCancel;
        final AtomicBoolean processed = new AtomicBoolean(false);

        menu.setListener(txn -> {
            int slot = txn.getSlot();
            Player p = txn.getPlayer();

            if (slot == 0 && processed.compareAndSet(false, true)) {
                p.getServer().getScheduler().scheduleDelayedTask(
                        InvMenuPlugin.getInstance(), () -> { if (p.isOnline()) menu.close(p); }, 1);
                return txn.discard().then(confirmCallback);
            } else if (slot == 4 && processed.compareAndSet(false, true)) {
                p.getServer().getScheduler().scheduleDelayedTask(
                        InvMenuPlugin.getInstance(), () -> { if (p.isOnline()) menu.close(p); }, 1);
                return txn.discard().then(cancelCallback);
            }
            return txn.discard();
        });

        menu.setCloseListener((p, inv) -> {
            if (processed.compareAndSet(false, true)) {
                if (cancelCallback != null) cancelCallback.accept(p);
            }
        });

        menu.send(player);
    }
}
