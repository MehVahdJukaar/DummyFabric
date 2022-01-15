
package net.mehvahdjukaar.dummmmmmy.common;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.mehvahdjukaar.dummmmmmy.DummmmmmyMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;


public class NetworkHandler {

    public static final ResourceLocation DAMAGE_PACKET_ID = new ResourceLocation(DummmmmmyMod.MOD_ID, "packet_damage");
    public static final ResourceLocation EQUIP_PACKET_ID = new ResourceLocation(DummmmmmyMod.MOD_ID, "packet_sync_equip");

    public static void registerServerReceivers() {
        //ServerPlayNetworking.registerGlobalReceiver(DAMAGE_PACKET_ID, NetworkHandler::packetDamageHandler);
    }

    public static void sendPacketDamage(Entity entity, float shake) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entity.getId());
        buf.writeFloat(shake);
        for (ServerPlayer player : PlayerLookup.tracking(entity)) {
            ServerPlayNetworking.send(player, DAMAGE_PACKET_ID, buf);
        }
    }

    public static void sendPacketSyncEquip(Entity entity, int slot, ItemStack item) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entity.getId());
        buf.writeInt(slot);
        buf.writeItem(item);
        for (ServerPlayer player : PlayerLookup.tracking(entity)) {
            ServerPlayNetworking.send(player, EQUIP_PACKET_ID, buf);

        }
    }


}
