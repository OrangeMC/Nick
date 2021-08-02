package moe.orangemc.nick.command;

import moe.orangemc.nick.NickPlugin;
import moe.orangemc.orangeroot.perms.enumeration.Group;
import moe.orangemc.plugincommons.PluginCommons;
import moe.orangemc.plugincommons.language.LanguageManager;
import org.jetbrains.annotations.NotNull;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NickCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only for players");
            return true;
        }
        LanguageManager languageManager = PluginCommons.getLanguageManager(NickPlugin.getInstance());

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + languageManager.getTranslationBySender(sender, "command.nick.usage"));
            return true;
        }

        Player p = (Player) sender;
        if (args[0].equals("reset")) {
            NickPlugin.getNickManager().resetNick(p);
        } else {
            NickPlugin.getNickManager().setNick(p, args[0]);
        }
        p.sendMessage(ChatColor.GREEN + languageManager.getTranslationBySender(sender, "command.nick.successful"));

        return true;
    }
}
