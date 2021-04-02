package moe.orangemc.nick;

import moe.orangemc.nick.api.NickManager;
import moe.orangemc.nick.api.SkinProperty;
import moe.orangemc.orangeroot.perms.enumeration.Group;

import org.bukkit.entity.Player;

public class NickManagerImpl implements NickManager {

    @Override
    public String getNick(Player player) {
        return null;
    }

    @Override
    public Group getNickedGroup(Player player) {
        return null;
    }

    @Override
    public SkinProperty getSkin(Player player) {
        return null;
    }

    @Override
    public void setNick(Player player, String nick) {

    }

    @Override
    public void setNickedGroup(Player player, Group nickedGroup) {

    }

    @Override
    public void setSkin(Player player, SkinProperty skin) {

    }
}
