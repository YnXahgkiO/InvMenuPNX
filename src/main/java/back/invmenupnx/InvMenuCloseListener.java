package back.invmenupnx;

import org.powernukkitx.Player;
import org.powernukkitx.inventory.fake.FakeInventory;

@FunctionalInterface
public interface InvMenuCloseListener {

    void onClose(Player player, FakeInventory inventory);
}
