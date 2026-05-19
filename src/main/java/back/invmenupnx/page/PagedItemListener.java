package back.invmenupnx.page;

import cn.nukkit.item.Item;
import back.invmenupnx.transaction.InvMenuTransaction;
import back.invmenupnx.transaction.InvMenuTransactionResult;

@FunctionalInterface
public interface PagedItemListener {

    InvMenuTransactionResult onItem(InvMenuTransaction transaction, int itemIndex, Item item);
}
