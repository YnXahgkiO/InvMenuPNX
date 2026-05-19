package back.invmenupnx;

import back.invmenupnx.transaction.InvMenuTransaction;
import back.invmenupnx.transaction.InvMenuTransactionResult;

@FunctionalInterface
public interface InvMenuListener {

    InvMenuTransactionResult onTransaction(InvMenuTransaction transaction);
}
