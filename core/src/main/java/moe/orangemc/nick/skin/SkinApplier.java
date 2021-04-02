package moe.orangemc.nick.skin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import moe.orangemc.luckyfish.plugincommons.utils.ReflectionUtil;
import moe.orangemc.nick.NickPlugin;
import moe.orangemc.nick.api.SkinProperty;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SkinApplier {
    private final Class<?> propertyClass;
    private final ProtocolManager pm = ProtocolLibrary.getProtocolManager();

    public SkinApplier() {
        Class<?> propertyClass;
        try {
            propertyClass = Class.forName("com.mojang.authlib.properties.Property");
        } catch (Exception e) {
            try {
                propertyClass = Class.forName("net.minecraft.util.com.mojang.authlib.properties.Property");
            } catch (Exception ex) {
                throw new IllegalStateException(ex);
            }
        }
        this.propertyClass = propertyClass;
    }

    public void applySkin(Player p, SkinProperty skinProperty) {
        if (skinProperty == null) {
            return;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(NickPlugin.getInstance(), () -> {
            try {
                Object entityPlayer = ReflectionUtil.invokeMethod(p, "getHandle");
                Object profile = ReflectionUtil.invokeMethod(entityPlayer, "getProfile");
                Object propertiesMap = ReflectionUtil.invokeMethod(profile, "getProperties");
                ReflectionUtil.invokeMethod(propertiesMap, "clear");

                Object property = ReflectionUtil.newInstance(propertyClass, "textures", skinProperty.getValue(), skinProperty.getSignature());
                ReflectionUtil.invokeMethod(propertiesMap, "put", new Class[]{Object.class, Object.class}, "textures", property);

                Bukkit.getScheduler().runTaskAsynchronously(NickPlugin.getInstance(), () -> updatePlayer(p));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void updatePlayer(Player p) {
        Entity vehicle = p.getVehicle();
        if (vehicle != null) {
            vehicle.removePassenger(p);
            Bukkit.getScheduler().scheduleSyncDelayedTask(NickPlugin.getInstance(), () -> vehicle.addPassenger(p));
        }

        List<Entity> passengers = new ArrayList<>();
        for (Entity entity : p.getPassengers()) {
            passengers.add(entity);
            p.removePassenger(entity);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(NickPlugin.getInstance(), () -> {
            for (Entity passenger : passengers) {
                p.addPassenger(passenger);
            }
        });

        for (Player pl : Bukkit.getOnlinePlayers()) {
            pl.hidePlayer(NickPlugin.getInstance(), p);
            pl.showPlayer(NickPlugin.getInstance(), p);
        }

        refreshPlayer(p);
    }

    private void refreshPlayer(Player p) {
        PacketContainer removePacket = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);

    }
}
