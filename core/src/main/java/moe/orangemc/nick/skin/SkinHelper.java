package moe.orangemc.nick.skin;

import moe.orangemc.nick.api.SkinProperty;

import org.bukkit.entity.Player;

import java.io.IOException;

public class SkinHelper {
    private static final SkinApplier skinApplier = new SkinApplier();
    private static final SkinDownloader skinDownloader = new SkinDownloader();

    public static void downloadAndApplyMojangSkin(Player p, String skinName) throws IOException {
        skinApplier.applySkin(p, skinDownloader.fetchMojangSkin(skinName));
    }

    public static void downloadAndApplyLocalSkin(Player p, String skinName) throws IOException {
        skinApplier.applySkin(p, skinDownloader.fetchLocalSkin(skinName));
    }

    public static void applyCustomSkin(Player p, SkinProperty skinProperty) {
        skinApplier.applySkin(p, skinProperty);
    }
}
