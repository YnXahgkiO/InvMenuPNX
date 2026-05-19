package back.invmenupnx.type;

import cn.nukkit.inventory.fake.FakeInventoryType;
import back.invmenupnx.graphic.BlockActorInvMenuGraphic;
import back.invmenupnx.graphic.InvMenuGraphic;
import org.jetbrains.annotations.NotNull;

public final class CustomInvMenuType {

    private final String id;
    private final FakeInventoryType pnxType;
    private final int size;
    private final InvMenuGraphic graphic;

    public CustomInvMenuType(@NotNull String id, @NotNull FakeInventoryType pnxType, int size) {
        this(id, pnxType, size, BlockActorInvMenuGraphic.forType(pnxType));
    }

    public CustomInvMenuType(@NotNull String id, @NotNull FakeInventoryType pnxType, int size, @NotNull InvMenuGraphic graphic) {
        this.id = id.toLowerCase();
        this.pnxType = pnxType;
        this.size = size;
        this.graphic = graphic;
    }

    public InvMenuGraphic createGraphic() {
        return graphic;
    }

    public String getId() {
        return id;
    }

    public FakeInventoryType getPnxType() {
        return pnxType;
    }

    public int getSize() {
        return size;
    }
}
