package moe.orangemc.nick;

import moe.orangemc.nick.api.NickManager;
import moe.orangemc.nick.api.SkinProperty;
import moe.orangemc.nick.database.NickDatabase;
import moe.orangemc.nick.skin.SkinHelper;

import org.bukkit.entity.Player;

import java.io.IOException;

public class NickManagerImpl implements NickManager {
    private final NickDatabase nickDatabase = NickPlugin.getNickDatabase();

    @Override
    public String getNick(Player player) {
        return nickDatabase.getNick(player.getUniqueId());
    }

    @Override
    public void setNick(Player player, String nick) {
        setNick(player, nick, true);
    }

    @Override
    public void resetNick(Player player) {
        nickDatabase.resetNick(player.getUniqueId());
        player.setPlayerListName(null);
        player.setDisplayName(null);
        try {
            SkinHelper.downloadAndApplyLocalSkin(player, player.getName());
        } catch (IOException e) {
            try {
                SkinHelper.downloadAndApplyMojangSkin(player, player.getName());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void setSkin(Player player, SkinProperty skin) {
        SkinHelper.applyCustomSkin(player, skin);
    }

    public void setNick(Player player, String nick, boolean save) {
        player.setPlayerListName(nick);
        player.setDisplayName(nick);
        try {
            SkinHelper.downloadAndApplyLocalSkin(player, nick);
        } catch (IOException e) {
            try {
                SkinHelper.downloadAndApplyMojangSkin(player, nick);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        if (save) {
            NickPlugin.getNickDatabase().setNick(player.getUniqueId(), nick);
        }
    }

}
