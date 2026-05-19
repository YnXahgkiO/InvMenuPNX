package back.invmenupnx.session;

import cn.nukkit.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerSessionManager {

    private static final Map<UUID, PlayerSession> sessions = new ConcurrentHashMap<>();

    private PlayerSessionManager() {}

    public static void create(Player player) {
        sessions.put(player.getUniqueId(), new PlayerSession(player.getUniqueId(), player));
    }

    public static void destroy(Player player) {
        sessions.remove(player.getUniqueId());
    }

    public static @Nullable PlayerSession get(Player player) {
        return sessions.get(player.getUniqueId());
    }

    public static PlayerSession getOrCreate(Player player) {
        return sessions.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerSession(uuid, player));
    }
}
