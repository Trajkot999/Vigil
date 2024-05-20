package me.trajkot.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.trajkot.Vigil;
import me.trajkot.player.VigilPlayer;
import me.trajkot.player.VigilNPCManager;
import org.apache.commons.lang3.RandomUtils;
import org.bukkit.Bukkit;

public class PacketListener {

    public Vigil vigil;

    public PacketListener(Vigil vigil) {
        this.vigil = vigil;
    }

    public void registerPacketListener() {
        Vigil.protocolManager.addPacketListener(new PacketAdapter(vigil, ListenerPriority.HIGH, PacketType.Play.Client.USE_ENTITY) {

            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer packet = event.getPacket();

                if(Vigil.vigilPlayerManager.getVigilPlayer(event.getPlayer()) != null) {
                    VigilNPCManager npcManager = Vigil.vigilPlayerManager.getVigilPlayer(event.getPlayer()).getVigilNPCManager();
                    npcManager.lastAttackTime = System.currentTimeMillis();

                    if(Vigil.vigilPlayerManager.getVigilPlayer(event.getPlayer()).getVigilNPCManager().npc != null && packet.getIntegers().read(0) == Vigil.vigilPlayerManager.getVigilPlayer(event.getPlayer()).getVigilNPCManager().npc.getId()) npcManager.Flag();

                    if(Vigil.vigilConfig.getConfig().getBoolean("npc.bypass-antibot-remove")) {
                        if(npcManager.bypassAntibotRemove++ > RandomUtils.nextInt(400, 900)) {
                            Vigil.vigilPlayerManager.getVigilPlayer(event.getPlayer()).getVigilNPCManager().bypassAntibotRemove();
                            npcManager.bypassAntibotRemove = 0;
                        }
                    }
                }
            }
        });

        Vigil.protocolManager.addPacketListener(new PacketAdapter(vigil, ListenerPriority.HIGH, PacketType.Play.Client.FLYING, PacketType.Play.Client.POSITION_LOOK, PacketType.Play.Client.POSITION, PacketType.Play.Client.LOOK) {

            @Override
            public void onPacketReceiving(PacketEvent event) {
                if(Vigil.vigilPlayerManager.getVigilPlayer(event.getPlayer()) != null) {
                    VigilPlayer vigilPlayer = Vigil.vigilPlayerManager.getVigilPlayer(event.getPlayer());

                    if(vigilPlayer != null) {
                        VigilNPCManager npcManager = vigilPlayer.getVigilNPCManager();

                        if(System.currentTimeMillis() - npcManager.lastAttackTime < RandomUtils.nextInt(2500, 4000)) {
                            Vigil.vigilPlayerManager.getVigilPlayer(event.getPlayer()).getVigilNPCManager().moveNPC(0.5f, 2.5f);

                            if(Vigil.vigilConfig.getConfig().getBoolean("npc.can-change-visibility")) Vigil.vigilPlayerManager.getVigilPlayer(event.getPlayer()).getVigilNPCManager().setNPCVisible();
                        } else {
                            if(Vigil.vigilConfig.getConfig().getBoolean("npc.can-change-visibility")) Vigil.vigilPlayerManager.getVigilPlayer(event.getPlayer()).getVigilNPCManager().setNPCInvisible();

                            float y = vigilPlayer.getPlayer().getLocation().getPitch() < -50 ? 7f : 4.5f;

                            if(Vigil.vigilConfig.getConfig().getBoolean("npc.bypass-antibot-radius")) {
                                Vigil.vigilPlayerManager.getVigilPlayer(event.getPlayer()).getVigilNPCManager().moveNPC(2f, 12.5f);
                            } else Vigil.vigilPlayerManager.getVigilPlayer(event.getPlayer()).getVigilNPCManager().moveNPC(y, 1.5f);
                        }
                    }
                }
            }
        });
    }
}