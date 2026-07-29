package back.invmenupnx.transaction;

import org.powernukkitx.Player;
import org.powernukkitx.event.inventory.ItemStackRequestActionEvent;
import org.powernukkitx.inventory.fake.FakeInventory;
import org.powernukkitx.item.Item;

public final class DeterministicInvMenuTransaction extends InvMenuTransaction {

    public DeterministicInvMenuTransaction(
            Player player,
            FakeInventory inventory,
            int slot,
            Item itemClicked,
            Item itemClickedWith,
            ItemStackRequestActionEvent event
    ) {
        super(player, inventory, slot, itemClicked, itemClickedWith, event);
    }

    @Override
    public InvMenuTransactionResult proceed() {
        return discard();
    }
}
