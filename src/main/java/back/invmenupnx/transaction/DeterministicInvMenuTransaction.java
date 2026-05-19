package back.invmenupnx.transaction;

import cn.nukkit.Player;
import cn.nukkit.event.inventory.ItemStackRequestActionEvent;
import cn.nukkit.inventory.fake.FakeInventory;
import cn.nukkit.item.Item;

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
