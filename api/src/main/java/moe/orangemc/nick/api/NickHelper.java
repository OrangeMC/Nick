package moe.orangemc.nick.api;

import org.bukkit.entity.Player;

public class NickHelper {
    private static NickManager nickManager;

    public static void setNickManager(NickManager nickManager) {
        if (NickHelper.nickManager != null) {
            throw new IllegalStateException("nickManager can only set once");
        }

        NickHelper.nickManager = nickManager;
    }

    public static NickManager getNickManager() {
        return nickManager;
    }

    public static String getNick(Player player) {
        return nickManager.getNick(player);
    }

    public static void setNick(Player player, String nick) {
        nickManager.setNick(player, nick);
    }

    public static void setSkin(Player player, SkinProperty skin) {
        nickManager.setSkin(player, skin);
    }
}
