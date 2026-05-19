package back.invmenupnx.transaction;

import cn.nukkit.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public final class InvMenuTransactionResult {

    private final boolean cancelled;
    private @Nullable Consumer<Player> postCallback;

    private InvMenuTransactionResult(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public static InvMenuTransactionResult accepted() {
        return new InvMenuTransactionResult(false);
    }

    public static InvMenuTransactionResult cancelled() {
        return new InvMenuTransactionResult(true);
    }

    public InvMenuTransactionResult then(@Nullable Consumer<Player> callback) {
        if (callback == null) return this;
        if (this.postCallback == null) {
            this.postCallback = callback;
        } else {
            Consumer<Player> existing = this.postCallback;
            this.postCallback = p -> { existing.accept(p); callback.accept(p); };
        }
        return this;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public @Nullable Consumer<Player> getPostCallback() {
        return postCallback;
    }
}
