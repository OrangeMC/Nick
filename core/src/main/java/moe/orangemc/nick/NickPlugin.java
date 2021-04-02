package moe.orangemc.nick;

import org.bukkit.plugin.java.JavaPlugin;

public final class NickPlugin extends JavaPlugin {
    private static NickPlugin instance;
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static NickPlugin getInstance() {
        return instance;
    }
}
