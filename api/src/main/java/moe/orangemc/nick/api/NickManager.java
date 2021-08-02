package moe.orangemc.nick.api;

import org.bukkit.entity.Player;

public interface NickManager {
    String getNick(Player player);

    void setNick(Player player, String nick);
    void resetNick(Player player);
    void setSkin(Player player, SkinProperty skin);
}
