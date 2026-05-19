package back.invmenupnx.transaction;

import cn.nukkit.Player;
import cn.nukkit.event.inventory.ItemStackRequestActionEvent;
import cn.nukkit.inventory.fake.FakeInventory;
import cn.nukkit.item.Item;

public class InvMenuTransaction {

    private final Player player;
    private final FakeInventory inventory;
    private final int slot;
    private final Item itemClicked;
    private final Item itemClickedWith;
    private final ItemStackRequestActionEvent event;

    public InvMenuTransaction(
            Player player,
            FakeInventory inventory,
            int slot,
            Item itemClicked,
            Item itemClickedWith,
            ItemStackRequestActionEvent event
    ) {
        this.player = player;
        this.inventory = inventory;
        this.slot = slot;
        this.itemClicked = itemClicked;
        this.itemClickedWith = itemClickedWith;
        this.event = event;
    }

    public Player getPlayer() {
        return player;
    }

    public FakeInventory getInventory() {
        return inventory;
    }

    public int getSlot() {
        return slot;
    }

    public Item getItemClicked() {
        return itemClicked;
    }

    public Item getItemClickedWith() {
        return itemClickedWith;
    }

    public ItemStackRequestActionEvent getAction() {
        return event;
    }

    public InvMenuTransactionResult proceed() {
        return InvMenuTransactionResult.accepted();
    }

    public InvMenuTransactionResult discard() {
        return InvMenuTransactionResult.cancelled();
    }
}
