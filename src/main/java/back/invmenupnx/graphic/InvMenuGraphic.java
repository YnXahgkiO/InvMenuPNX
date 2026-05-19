package back.invmenupnx.graphic;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.Nullable;

public interface InvMenuGraphic {

    Vector3 pickPosition(Player player);

    void send(Player player, Vector3 pos, @Nullable String title);

    void remove(Player player, Vector3 pos);

    int getAnimationDurationMs();
}
