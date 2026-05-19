package back.invmenupnx;

import cn.nukkit.Player;
import cn.nukkit.inventory.fake.FakeInventory;

@FunctionalInterface
public interface InvMenuCloseListener {

    void onClose(Player player, FakeInventory inventory);
}
