package moe.orangemc.nick.util;

import io.github.karlatemp.unsafeaccessor.Unsafe;
import moe.orangemc.plugincommons.utils.ReflectionUtil;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class NameTagUpdater {
    public static void setPlayerNameTag(Player player, String name) {
        Unsafe usf = Unsafe.getUnsafe();
        for(Player pl : Bukkit.getOnlinePlayers()){
            if(pl == player) {
                continue;
            }
            try {
                //REMOVES THE PLAYER
                Class<?> packetPlayOutPlayerInfo = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo");
                Class<?> enumAction = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
                Object enumRemove = ReflectionUtil.getEnum(enumAction, "REMOVE_PLAYER");
                Object enumAdd = ReflectionUtil.getEnum(enumAction, "ADD_PLAYER");
                Object removePlayerPacket = ReflectionUtil.newInstance(packetPlayOutPlayerInfo, new Class[]{enumAction, Collection.class}, enumRemove, Collections.singletonList(ReflectionUtil.invokeMethod(player, "getHandle")));
                Object playerConnection = ReflectionUtil.getField(ReflectionUtil.invokeMethod(pl, "getHandle"), "b");
                sendPacket(playerConnection, removePlayerPacket);

                //CHANGES THE PLAYER'S GAME PROFILE
                Object gp = ReflectionUtil.invokeMethod(player, "getProfile");
                try {
                    Field nameField = gp.getClass().getDeclaredField("name");
                    nameField.setAccessible(true);

                    long offset = usf.objectFieldOffset(Field.class, "modifiers");
                    usf.putInt(nameField, offset, nameField.getModifiers() & ~Modifier.FINAL);
//                    Field modifiersField = Field.class.getDeclaredField("modifiers");
//                    modifiersField.setAccessible(true);
//                    modifiersField.setInt(nameField, nameField.getModifiers() & ~Modifier.FINAL);

                    nameField.set(gp, name);
                } catch (IllegalAccessException | NoSuchFieldException ex) {
                    throw new IllegalStateException(ex);
                }
                //ADDS THE PLAYER

                Object addPlayerPacket = ReflectionUtil.newInstance(packetPlayOutPlayerInfo, new Class[]{enumAction, Collection.class}, enumAdd, Collections.singletonList(ReflectionUtil.invokeMethod(player, "getHandle")));
                sendPacket(playerConnection, addPlayerPacket);

                Class<?> destroyClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy");
                Object entityDestroyPacket = ReflectionUtil.newInstance(destroyClass, new Class[]{int[].class}, new int[] {player.getEntityId()});
                sendPacket(playerConnection, entityDestroyPacket);

                Class<?> spawnClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn");
                Class<?> entityHuman = Class.forName("net.minecraft.world.entity.player.EntityHuman");
                Object packetSpawn = ReflectionUtil.newInstance(spawnClass, new Class[]{entityHuman}, ReflectionUtil.invokeMethod(player, "getHandle"));
                sendPacket(playerConnection, packetSpawn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void sendPacket(Object connection, Object packet) throws Exception {
        ReflectionUtil.invokeMethod(connection, "sendPacket", new Class[]{Class.forName("net.minecraft.network.protocol.Packet")}, packet);
    }
}
