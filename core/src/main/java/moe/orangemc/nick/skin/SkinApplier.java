package moe.orangemc.nick.skin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.google.common.hash.Hashing;
import moe.orangemc.nick.NickPlugin;
import moe.orangemc.nick.api.SkinProperty;
import moe.orangemc.plugincommons.utils.ReflectionUtil;
import nl.matsv.viabackwards.protocol.protocol1_15_2to1_16.Protocol1_15_2To1_16;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.protocol.ProtocolRegistry;
import us.myles.ViaVersion.api.protocol.ProtocolVersion;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_15to1_14_4.ClientboundPackets1_15;

import org.bukkit.Bukkit;
import org.bukkit.WorldType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class SkinApplier {
    private final Class<?> propertyClass;
    private final ProtocolManager pm = ProtocolLibrary.getProtocolManager();
    private final boolean useViaBackwards;

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

        useViaBackwards = Bukkit.getPluginManager().isPluginEnabled("ViaBackwards")
                && ProtocolRegistry.SERVER_PROTOCOL >= ProtocolVersion.v1_16.getVersion();
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

        Bukkit.getScheduler().scheduleSyncDelayedTask(NickPlugin.getInstance(), () -> {
            for (Player pl : Bukkit.getOnlinePlayers()) {
                pl.hidePlayer(NickPlugin.getInstance(), p);
                pl.showPlayer(NickPlugin.getInstance(), p);
            }
        });

        refreshPlayer(p);
    }

    @SuppressWarnings("unchecked")
    private void refreshPlayer(Player p) {
        try {
//            PlayerInfoData playerInfoData = new PlayerInfoData(WrappedGameProfile.fromPlayer(p), 0, EnumWrappers.NativeGameMode.fromBukkit(p.getGameMode()), WrappedChatComponent.fromText(p.getDisplayName()));
//            List<PlayerInfoData> playerInfoDataList = new ArrayList<>();
//            playerInfoDataList.add(playerInfoData);
            Class<?> ppopi = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo");
            Class<?> enumAction = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo$EnumPlayerInfoAction");

            Enum<?> remove = ReflectionUtil.getEnum(enumAction, "REMOVE_PLAYER");
            Enum<?> add = ReflectionUtil.getEnum(enumAction, "ADD_PLAYER");

            List<?> ep = Arrays.asList(ReflectionUtil.invokeMethod(p, "getHandle"));
            Object removePacket = ReflectionUtil.newInstance(ppopi, new Class[]{enumAction, Collection.class}, remove, ep);
            Object addPacket = ReflectionUtil.newInstance(ppopi, new Class[]{enumAction, Collection.class}, add, ep);

//            PacketContainer removePacket = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
//            removePacket.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
//            removePacket.getPlayerInfoDataLists().write(0, playerInfoDataList);
//
//            PacketContainer addPacket = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
//            addPacket.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
//            addPacket.getPlayerInfoDataLists().write(0, playerInfoDataList);

            long seedEncrypted = Hashing.sha256().hashString(String.valueOf(p.getWorld().getSeed()), StandardCharsets.UTF_8).asLong();

            PacketContainer respawnPacket = new PacketContainer(PacketType.Play.Server.RESPAWN);
            respawnPacket.getDimensions().write(0, p.getWorld().getEnvironment().getId());
            respawnPacket.getWorldKeys().write(0, p.getWorld());
            respawnPacket.getGameModes().write(0, EnumWrappers.NativeGameMode.fromBukkit(p.getGameMode()));
            respawnPacket.getGameModes().write(1, EnumWrappers.NativeGameMode.fromBukkit(p.getGameMode()));
            respawnPacket.getLongs().write(0, seedEncrypted);
            respawnPacket.getBooleans().write(0, false).write(1, p.getWorld().getWorldType() == WorldType.FLAT).write(2, true);

            PacketContainer positionPacket = new PacketContainer(PacketType.Play.Server.POSITION);
            positionPacket.getDoubles().write(0, p.getLocation().getX())
                    .write(1, p.getLocation().getY())
                    .write(2, p.getLocation().getZ());
            positionPacket.getFloat().write(0, p.getLocation().getYaw())
                    .write(1, p.getLocation().getPitch());

            Class<Enum> flagsClass;
            try {
                flagsClass = (Class<Enum>) Class.forName("net.minecraft.network.protocol.game.PacketPlayOutPosition$EnumPlayerTeleportFlags");
            } catch (Exception e) {
                try {
                    flagsClass = (Class<Enum>) Class.forName("net.minecraft.server." + ReflectionUtil.getServerVersion() + ".PacketPlayOutPosition$EnumPlayerTeleportFlags");
                } catch (Exception e1) {
                    e1.addSuppressed(e);
                    throw e1;
                }
            }
            positionPacket.getSets(EnumWrappers.getGenericConverter(null, flagsClass)).write(0, new HashSet<>());
            positionPacket.getIntegers().write(0, 0);
            positionPacket.getBooleans().write(0, false);

            PacketContainer slotPacket = new PacketContainer(PacketType.Play.Server.SET_SLOT);
            slotPacket.getIntegers().write(0, p.getInventory().getHeldItemSlot());

//            pm.sendServerPacket(p, removePacket);
//            pm.sendServerPacket(p, addPacket);

            Object playerConnection = ReflectionUtil.getField(ReflectionUtil.invokeMethod(p, "getHandle"), "b");
            ReflectionUtil.invokeMethod(playerConnection, "sendPacket", new Class[]{Class.forName("net.minecraft.network.protocol.Packet")}, removePacket);
            ReflectionUtil.invokeMethod(playerConnection, "sendPacket", new Class[]{Class.forName("net.minecraft.network.protocol.Packet")}, addPacket);

            boolean sendViaProtocolLib = true;

            if (useViaBackwards) {
                UserConnection connection = Via.getManager().getConnection(p.getUniqueId());
                if (connection != null && connection.getProtocolInfo() != null && connection.getProtocolInfo().getProtocolVersion() < ProtocolVersion.v1_16.getVersion()) {
                    PacketWrapper packet = new PacketWrapper(ClientboundPackets1_15.RESPAWN.ordinal(), null, connection);
                    packet.write(Type.INT, p.getWorld().getEnvironment().getId());
                    packet.write(Type.LONG, seedEncrypted);
                    packet.write(Type.UNSIGNED_BYTE, (short) p.getGameMode().getValue());
                    packet.write(Type.STRING, (boolean) ReflectionUtil.invokeMethod(ReflectionUtil.invokeMethod(p.getWorld(), "getHandle"), "isFlatWorld") ? "flat" : "default");
                    packet.send(Protocol1_15_2To1_16.class, true, true);
                    sendViaProtocolLib = false;
                }
            }
            if (sendViaProtocolLib) {
                pm.sendServerPacket(p, respawnPacket);
            }

            final Object entityPlayer = ReflectionUtil.invokeMethod(p, "getHandle");

            ReflectionUtil.invokeMethod(entityPlayer, "updateAbilities");

            pm.sendServerPacket(p, positionPacket);
            pm.sendServerPacket(p, slotPacket);

            ReflectionUtil.invokeMethod(p, "updateScaledHealth");
            p.updateInventory();
            ReflectionUtil.invokeMethod(entityPlayer, "triggerHealthUpdate");

            if (p.isOp()) {
                Bukkit.getScheduler().runTask(NickPlugin.getInstance(), () -> {
                    p.setOp(false);
                    p.setOp(true);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
