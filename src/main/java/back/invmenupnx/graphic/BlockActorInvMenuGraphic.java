package back.invmenupnx.graphic;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.blockentity.BlockEntityID;
import cn.nukkit.inventory.fake.FakeInventoryType;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.BlockEntityDataPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class BlockActorInvMenuGraphic implements InvMenuGraphic {

    public static BlockActorInvMenuGraphic forType(@NotNull FakeInventoryType type) {
        return switch (type) {
            case CHEST -> new BlockActorInvMenuGraphic(BlockID.CHEST, BlockEntityID.CHEST, false);
            case DOUBLE_CHEST -> new BlockActorInvMenuGraphic(BlockID.CHEST, BlockEntityID.CHEST, true);
            case ENDER_CHEST -> new BlockActorInvMenuGraphic(BlockID.ENDER_CHEST, BlockEntityID.ENDER_CHEST, false);
            case SHULKER_BOX -> new BlockActorInvMenuGraphic(BlockID.UNDYED_SHULKER_BOX, BlockEntityID.SHULKER_BOX, false);
            case HOPPER -> new BlockActorInvMenuGraphic(BlockID.HOPPER, BlockEntityID.HOPPER, false);
            case DISPENSER -> new BlockActorInvMenuGraphic(BlockID.DISPENSER, BlockEntityID.DISPENSER, false);
            case DROPPER -> new BlockActorInvMenuGraphic(BlockID.DROPPER, BlockEntityID.DROPPER, false);
            case FURNACE -> new BlockActorInvMenuGraphic(BlockID.FURNACE, BlockEntityID.FURNACE, false);
            case BREWING_STAND -> new BlockActorInvMenuGraphic(BlockID.BREWING_STAND, BlockEntityID.BREWING_STAND, false);
            case WORKBENCH -> new BlockActorInvMenuGraphic(BlockID.CRAFTING_TABLE, BlockEntityID.CHEST, false);
            default -> new BlockActorInvMenuGraphic(BlockID.CHEST, BlockEntityID.CHEST, false);
        };
    }

    private static final int[][] HORIZONTAL = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    private final String blockId;
    private final String tileId;
    private final boolean isDouble;
    private final Map<UUID, List<Vector3>> barriers = new ConcurrentHashMap<>();

    public BlockActorInvMenuGraphic(@NotNull String blockId, @NotNull String tileId, boolean isDouble) {
        this.blockId = blockId;
        this.tileId = tileId;
        this.isDouble = isDouble;
    }

    @Override
    public Vector3 pickPosition(Player player) {
        Vector3 dir = player.getDirectionVector();
        dir.y = 0;
        dir.x *= -(1 + player.getWidth());
        dir.z *= -(1 + player.getWidth());
        return player.getPosition().add(dir);
    }

    @Override
    public void send(Player player, Vector3 pos, @Nullable String title) {
        sendBlock(player, pos, title);
        if (isDouble) {
            sendBlock(player, pos.add(1, 0, 0), title);
        } else if (blockId.equals(BlockID.CHEST)) {
            placeChestBarriers(player, pos);
        }
    }

    @Override
    public void remove(Player player, Vector3 pos) {
        restoreBlock(player, pos);
        if (isDouble) {
            restoreBlock(player, pos.add(1, 0, 0));
        }
        List<Vector3> placed = barriers.remove(player.getUniqueId());
        if (placed != null) {
            for (Vector3 adj : placed) {
                restoreBlockWithEntity(player, adj);
            }
        }
    }

    @Override
    public int getAnimationDurationMs() {
        return 0;
    }

    private void sendBlock(Player player, Vector3 pos, @Nullable String title) {
        UpdateBlockPacket blockPk = new UpdateBlockPacket();
        blockPk.blockRuntimeId = Block.get(blockId).getRuntimeId();
        blockPk.flags = UpdateBlockPacket.FLAG_NETWORK;
        blockPk.x = pos.getFloorX();
        blockPk.y = pos.getFloorY();
        blockPk.z = pos.getFloorZ();
        player.dataPacket(blockPk);

        BlockEntityDataPacket dataPk = new BlockEntityDataPacket();
        dataPk.x = pos.getFloorX();
        dataPk.y = pos.getFloorY();
        dataPk.z = pos.getFloorZ();
        dataPk.namedTag = new CompoundTag()
                .putString("id", tileId)
                .putInt("x", pos.getFloorX())
                .putInt("y", pos.getFloorY())
                .putInt("z", pos.getFloorZ())
                .putBoolean("isMovable", true)
                .putString("CustomName", title != null ? title : "");
        player.dataPacket(dataPk);
    }

    private void restoreBlock(Player player, Vector3 pos) {
        Block original = player.getLevel().getBlock(pos);
        UpdateBlockPacket pk = new UpdateBlockPacket();
        pk.blockRuntimeId = original.getRuntimeId();
        pk.flags = UpdateBlockPacket.FLAG_NETWORK;
        pk.x = pos.getFloorX();
        pk.y = pos.getFloorY();
        pk.z = pos.getFloorZ();
        player.dataPacket(pk);
    }

    private void restoreBlockWithEntity(Player player, Vector3 pos) {
        restoreBlock(player, pos);
        BlockEntity be = player.getLevel().getBlockEntity(pos);
        if (be != null) {
            BlockEntityDataPacket pk = new BlockEntityDataPacket();
            pk.x = pos.getFloorX();
            pk.y = pos.getFloorY();
            pk.z = pos.getFloorZ();
            pk.namedTag = be.namedTag;
            player.dataPacket(pk);
        }
    }

    private void placeChestBarriers(Player player, Vector3 pos) {
        List<Vector3> placed = new ArrayList<>();
        for (int[] off : HORIZONTAL) {
            Vector3 adj = new Vector3(pos.x + off[0], pos.y, pos.z + off[1]);
            BlockEntity be = player.getLevel().getBlockEntity(adj);
            if (be instanceof BlockEntityChest chest && chest.isPaired()) {
                UpdateBlockPacket pk = new UpdateBlockPacket();
                pk.blockRuntimeId = Block.get(BlockID.BARRIER).getRuntimeId();
                pk.flags = UpdateBlockPacket.FLAG_NETWORK;
                pk.x = adj.getFloorX();
                pk.y = adj.getFloorY();
                pk.z = adj.getFloorZ();
                player.dataPacket(pk);
                placed.add(adj);
            }
        }
        if (!placed.isEmpty()) {
            barriers.put(player.getUniqueId(), placed);
        }
    }
}
