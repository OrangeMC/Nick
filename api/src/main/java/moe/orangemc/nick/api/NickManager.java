package moe.orangemc.nick.api;

import moe.orangemc.orangeroot.perms.enumeration.Group;

import org.bukkit.entity.Player;

public interface NickManager {
    String getNick(Player player);
    Group getNickedGroup(Player player);
    SkinProperty getSkin(Player player);

    void setNick(Player player, String nick);
    void setNickedGroup(Player player, Group nickedGroup);
    void setSkin(Player player, SkinProperty skin);
}
