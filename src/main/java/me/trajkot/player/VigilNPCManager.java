package me.trajkot.player;

import com.mojang.authlib.GameProfile;
import me.trajkot.Vigil;
import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.lang3.RandomUtils;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import java.util.Random;
import java.util.UUID;
import me.trajkot.util.VigilAlertUtil;
import me.trajkot.util.VigilPunishUtil;

public class VigilNPCManager {

    public final VigilPlayer player;

    private final boolean punish;
    private boolean sendFirstAlert = false;
    private int vl;
    private final int maxVl;
    private final int firstAlertVl;
    private final int nextAlertVl;
    private int alertVl;

    public int bypassAntibotRemove;

    public EntityPlayer npc;

    private boolean isInvisible;

    public long lastAttackTime;

    public VigilNPCManager(VigilPlayer player) {
        this.player = player;

        this.punish = Vigil.vigilConfig.getConfig().getBoolean("punishments.punishment-enabled");
        this.maxVl = Vigil.vigilConfig.getConfig().getInt("punishments.punishment-max-vl");

        this.firstAlertVl = Vigil.vigilConfig.getConfig().getInt("alerts.vl-to-first-alert");
        this.nextAlertVl = Vigil.vigilConfig.getConfig().getInt("alerts.vl-to-next-alert");
    }

    public void Flag() {
        vl++;

        if(vl < maxVl) {
            if(vl >= firstAlertVl) {
                if(!sendFirstAlert) {
                    VigilAlertUtil.sendVigilAlert(player, "&b%player% &7failed &bForce Field &7x%vl%", vl);
                    sendFirstAlert = true;
                } else {
                    if(alertVl++ >= firstAlertVl + nextAlertVl) {
                        VigilAlertUtil.sendVigilAlert(player, "&b%player% &7failed &bForce Field &7x%vl%", vl);
                        alertVl = 0;
                    }
                }
            }
        } else {
            if(punish) VigilPunishUtil.punishVigilPlayer(player, Vigil.INSTANCE);
            else {
                if(alertVl++ >= firstAlertVl + nextAlertVl) {
                    VigilAlertUtil.sendVigilAlert(player, "&b%player% &7failed &bForce Field &7x%vl%", vl);
                    alertVl = 0;
                }
            }
        }
    }

    public void createNPC(String name, double y) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), name);
        WorldServer world = ((CraftWorld) player.getPlayer().getWorld()).getHandle();
        npc = new EntityPlayer(MinecraftServer.getServer(), world, profile, new PlayerInteractManager(world));
        npc.setLocation(player.getPlayer().getLocation().getX(), player.getPlayer().getLocation().getY() + y, player.getPlayer().getLocation().getZ(), player.getPlayer().getLocation().getYaw(), player.getPlayer().getLocation().getPitch());

        if(Vigil.vigilConfig.getConfig().getBoolean("npc.bypass-antibot-data")) {
            npc.collidesWithEntities = true;
            npc.playerInteractManager.setGameMode(WorldSettings.EnumGamemode.SURVIVAL);
            npc.ping = RandomUtils.nextInt(70, 230);
            npc.setAirTicks(RandomUtils.nextInt(0, 10));
            npc.onGround = npc.getAirTicks() > 0;
        }

        if(Vigil.vigilConfig.getConfig().getBoolean("npc.can-change-visibility")) {
            npc.setInvisible(true);
            isInvisible = true;
        } else {
            ((CraftPlayer) player.getPlayer()).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(npc.getId(), 4, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.GOLD_HELMET))));
        }

        ((CraftPlayer) player.getPlayer()).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
        ((CraftPlayer) player.getPlayer()).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
        ((CraftPlayer) player.getPlayer()).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER));
    }

    public void moveNPC(double yPos, float distance) {
        if(npc != null) {
            double radians = Math.toRadians(player.getPlayer().getLocation().getYaw() + 180);

            double x = player.getPlayer().getLocation().getX() - Math.sin(radians) * distance;
            double y = player.getPlayer().getLocation().getY() + yPos;
            double z = player.getPlayer().getLocation().getZ() + Math.cos(radians) * distance;

            ((CraftPlayer) player.getPlayer()).getHandle().playerConnection.sendPacket(
                    new PacketPlayOutEntityTeleport(
                            npc.getId(),
                            ((int) x * 32),
                            ((int) y * 32),
                            ((int) z * 32),
                            (byte) 4,
                            (byte) 4,
                            player.getPlayer().isOnGround()));
        }
    }

    public void setNPCInvisible() {
        if(npc != null) {
            if(!isInvisible) {
                npc.setInvisible(true);
                isInvisible = true;
                ((CraftPlayer) player.getPlayer()).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
                ((CraftPlayer) player.getPlayer()).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
            }
        }
    }

    public void setNPCVisible() {
        if(npc != null) {
            if(isInvisible) {
                npc.setInvisible(false);
                isInvisible = false;
                ((CraftPlayer) player.getPlayer()).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(npc.getId(), 4, CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.GOLD_HELMET))));
                ((CraftPlayer) player.getPlayer()).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
                ((CraftPlayer) player.getPlayer()).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
            }
        }
    }

    public void bypassAntibotRemove() {
        if(npc != null) {
            ((CraftPlayer) player.getPlayer()).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
            ((CraftPlayer) player.getPlayer()).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
            ((CraftPlayer) player.getPlayer()).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER));

            if(Vigil.vigilConfig.getConfig().getBoolean("npc.bypass-antibot-data")) {
                npc.collidesWithEntities = true;
                npc.playerInteractManager.setGameMode(WorldSettings.EnumGamemode.SURVIVAL);
                npc.ping = RandomUtils.nextInt(70, 230);
                npc.setAirTicks(RandomUtils.nextInt(0, 10));
                npc.onGround = npc.getAirTicks() > 0;
            }
        }
    }
    public String randomName(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        return sb.toString();
    }
}