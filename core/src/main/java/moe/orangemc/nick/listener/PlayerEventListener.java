package moe.orangemc.nick.listener;

import moe.orangemc.nick.NickPlugin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerEventListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        String nicked = NickPlugin.getNickManager().getNick(p);
        if (nicked != null) {
            NickPlugin.getNickManager().setNick(p, nicked, false);
        }
    }
}
