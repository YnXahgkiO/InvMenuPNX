package back.invmenupnx.fake;

import cn.nukkit.Player;
import cn.nukkit.inventory.fake.FakeInventory;
import cn.nukkit.inventory.fake.FakeInventoryType;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import back.invmenupnx.graphic.InvMenuGraphic;
import back.invmenupnx.network.PlayerNetwork;
import back.invmenupnx.session.PlayerSessionManager;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class InvMenuFakeInventory extends FakeInventory {

    private final FakeInventoryType inventoryKind;
    private final InvMenuGraphic graphic;
    private final Map<UUID, Vector3> placedAt = new ConcurrentHashMap<>();
    private Consumer<Player> capturedCloseHandler;

    public InvMenuFakeInventory(@NotNull FakeInventoryType type, @NotNull String title, int size, @NotNull InvMenuGraphic graphic) {
        super(type, title, size);
        this.inventoryKind = type;
        this.graphic = graphic;
    }

    @Override
    public void setOnCloseHandler(Consumer<Player> handler) {
        super.setOnCloseHandler(handler);
        this.capturedCloseHandler = handler;
    }

    @Override
    public void onOpen(Player player) {
        if (inventoryKind == FakeInventoryType.ENTITY) {
            super.onOpen(player);
            return;
        }

        player.setFakeInventoryOpen(true);

        Vector3 pos = graphic.pickPosition(player);
        placedAt.put(player.getUniqueId(), pos);
        graphic.send(player, pos, getTitle());

        PlayerNetwork network = PlayerSessionManager.getOrCreate(player).getNetwork();
        network.waitForAck(() -> {
            if (!player.isOnline() || player.getWindowId(this) == -1) {
                placedAt.remove(player.getUniqueId());
                graphic.remove(player, pos);
                player.setFakeInventoryOpen(false);
                return;
            }
            this.getViewers().add(player);
            sendOpenContainer(player, pos);
            this.sendContents(player);
        });
    }

    @Override
    public void onClose(Player player) {
        if (inventoryKind == FakeInventoryType.ENTITY) {
            super.onClose(player);
            return;
        }

        ContainerClosePacket closePacket = new ContainerClosePacket();
        closePacket.windowId = player.getWindowId(this);
        closePacket.wasServerInitiated = player.getClosingWindowId() != closePacket.windowId;
        closePacket.type = getType();
        player.dataPacket(closePacket);

        Vector3 pos = placedAt.remove(player.getUniqueId());
        if (pos != null) {
            graphic.remove(player, pos);
        }

        this.getViewers().remove(player);
        player.setFakeInventoryOpen(false);

        if (capturedCloseHandler != null) {
            capturedCloseHandler.accept(player);
        }
    }

    private void sendOpenContainer(Player player, Vector3 pos) {
        ContainerOpenPacket pk = new ContainerOpenPacket();
        pk.windowId = player.getWindowId(this);
        pk.type = getType().getNetworkType();
        pk.x = pos.getFloorX();
        pk.y = pos.getFloorY();
        pk.z = pos.getFloorZ();
        player.dataPacket(pk);
    }
}
