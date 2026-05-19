package back.invmenupnx;

import back.invmenupnx.type.CustomInvMenuType;
import back.invmenupnx.type.InvMenuType;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class InvMenuHandler {

    private static InvMenuPlugin plugin;
    private static final Map<String, InvMenuType> builtinTypes = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final Map<String, CustomInvMenuType> customTypes = Collections.synchronizedMap(new LinkedHashMap<>());
    private static volatile boolean registered = false;

    private InvMenuHandler() {}

    static synchronized void init(InvMenuPlugin invMenuPlugin) {
        if (registered) return;
        plugin = invMenuPlugin;

        for (InvMenuType type : InvMenuType.values()) {
            builtinTypes.put(type.getId(), type);
        }

        plugin.getServer().getPluginManager().registerEvents(new InvMenuEventHandler(), plugin);
        registered = true;
    }

    public static void registerCustomType(CustomInvMenuType type) {
        customTypes.put(type.getId(), type);
    }

    public static InvMenuType getType(String id) {
        return builtinTypes.get(id.toLowerCase());
    }

    public static CustomInvMenuType getCustomType(String id) {
        return customTypes.get(id.toLowerCase());
    }

    public static boolean isRegistered() {
        return registered;
    }

    public static InvMenuPlugin getPlugin() {
        return plugin;
    }
}
