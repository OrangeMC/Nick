package moe.orangemc.nick;

import moe.orangemc.nick.api.NickManager;
import moe.orangemc.nick.api.SkinProperty;
import moe.orangemc.nick.database.NickDatabase;
import moe.orangemc.nick.skin.SkinHelper;
import moe.orangemc.nick.util.NameTagUpdater;

import org.bukkit.entity.Player;

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
        String originName = nickDatabase.getOriginName(player.getUniqueId());
        nickDatabase.resetNick(player.getUniqueId());
        player.setPlayerListName(null);
        player.setDisplayName(null);
        try {
            SkinHelper.downloadAndApplyLocalSkin(player, originName);
        } catch (Exception e) {
            try {
                SkinHelper.downloadAndApplyMojangSkin(player, originName);
            } catch (Exception ex) {
                ex.addSuppressed(e);
                ex.printStackTrace();
            }
        }
        NameTagUpdater.setPlayerNameTag(player, originName);
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
        } catch (Exception e) {
            try {
                SkinHelper.downloadAndApplyMojangSkin(player, nick);
            } catch (Exception ex) {
                ex.addSuppressed(e);
                ex.printStackTrace();
            }
        }
        if (save) {
            NickPlugin.getNickDatabase().setNick(player.getUniqueId(), nick, player.getName());
        }
        NameTagUpdater.setPlayerNameTag(player, nick);
    }
}
