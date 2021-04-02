package moe.orangemc.nick.api;

import moe.orangemc.orangeroot.perms.enumeration.Group;

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
    public static Group getNickedGroup(Player player) {
        return nickManager.getNickedGroup(player);
    }

    public static void setNick(Player player, String nick) {
        nickManager.setNick(player, nick);
    }
    public static void setNickedGroup(Player player, Group nickedGroup) {
        nickManager.setNickedGroup(player, nickedGroup);
    }

    public static SkinProperty getSkin(Player player) {
        return nickManager.getSkin(player);
    }

    public static void setSkin(Player player, SkinProperty skin) {
        nickManager.setSkin(player, skin);
    }
}
