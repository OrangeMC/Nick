package moe.orangemc.nick;

import moe.orangemc.nick.api.NickHelper;
import moe.orangemc.nick.command.NickCommand;
import moe.orangemc.nick.database.NickDatabase;
import moe.orangemc.nick.listener.PlayerEventListener;

import org.bukkit.plugin.java.JavaPlugin;

public final class NickPlugin extends JavaPlugin {
    private static NickPlugin instance;
    private static NickManagerImpl nickManager;
    private static NickDatabase nickDatabase;
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        saveDefaultConfig();
        nickDatabase = new NickDatabase(getConfig().getString("mongodb.host"));
        nickManager = new NickManagerImpl();
        NickHelper.setNickManager(nickManager);

        getCommand("nick").setExecutor(new NickCommand());
        getServer().getPluginManager().registerEvents(new PlayerEventListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static NickPlugin getInstance() {
        return instance;
    }

    public static NickManagerImpl getNickManager() {
        return nickManager;
    }

    public static NickDatabase getNickDatabase() {
        return nickDatabase;
    }
}
