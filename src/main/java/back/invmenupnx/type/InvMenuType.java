package back.invmenupnx.type;

import cn.nukkit.inventory.fake.FakeInventoryType;
import back.invmenupnx.graphic.BlockActorInvMenuGraphic;
import back.invmenupnx.graphic.InvMenuGraphic;

public enum InvMenuType {

    CHEST(FakeInventoryType.CHEST, 27, "chest"),
    DOUBLE_CHEST(FakeInventoryType.DOUBLE_CHEST, 54, "double_chest"),
    HOPPER(FakeInventoryType.HOPPER, 5, "hopper"),
    ENDER_CHEST(FakeInventoryType.ENDER_CHEST, 27, "ender_chest"),
    FURNACE(FakeInventoryType.FURNACE, 3, "furnace"),
    BREWING_STAND(FakeInventoryType.BREWING_STAND, 5, "brewing_stand"),
    DISPENSER(FakeInventoryType.DISPENSER, 9, "dispenser"),
    DROPPER(FakeInventoryType.DROPPER, 9, "dropper"),
    SHULKER_BOX(FakeInventoryType.SHULKER_BOX, 27, "shulker_box"),
    WORKBENCH(FakeInventoryType.WORKBENCH, 9, "workbench");

    private final FakeInventoryType pnxType;
    private final int size;
    private final String id;

    InvMenuType(FakeInventoryType pnxType, int size, String id) {
        this.pnxType = pnxType;
        this.size = size;
        this.id = id;
    }

    public InvMenuGraphic createGraphic() {
        return BlockActorInvMenuGraphic.forType(pnxType);
    }

    public FakeInventoryType getPnxType() {
        return pnxType;
    }

    public int getSize() {
        return size;
    }

    public String getId() {
        return id;
    }
}
