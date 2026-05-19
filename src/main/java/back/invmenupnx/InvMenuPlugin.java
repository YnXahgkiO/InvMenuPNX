package back.invmenupnx;

import cn.nukkit.plugin.PluginBase;

public final class InvMenuPlugin extends PluginBase {

    private static InvMenuPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        InvMenuHandler.init(this);
        getLogger().info("InvMenuPNX enabled — fake inventory menus ready.");
    }

    @Override
    public void onDisable() {
        getLogger().info("InvMenuPNX disabled.");
    }

    public static InvMenuPlugin getInstance() {
        return instance;
    }
}
