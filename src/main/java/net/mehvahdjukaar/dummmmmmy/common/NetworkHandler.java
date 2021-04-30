
package net.mehvahdjukaar.dummmmmmy.common;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.mehvahdjukaar.dummmmmmy.DummmmmmyMod;
import net.mehvahdjukaar.dummmmmmy.entity.TargetDummyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


public class NetworkHandler {

    private static final ResourceLocation DAMAGE_PACKET_ID = new ResourceLocation(DummmmmmyMod.MOD_ID,"packet_damage");
    private static final ResourceLocation EQUIP_PACKET_ID = new ResourceLocation(DummmmmmyMod.MOD_ID,"packet_sync_equip");

    public static void registerServerReceivers(){
        //ServerPlayNetworking.registerGlobalReceiver(DAMAGE_PACKET_ID, NetworkHandler::packetDamageHandler);

    }

    public static void registerClientReceivers(){
        ClientPlayNetworking.registerGlobalReceiver(DAMAGE_PACKET_ID, NetworkHandler::packetDamageHandler);
        ClientPlayNetworking.registerGlobalReceiver(EQUIP_PACKET_ID, NetworkHandler::packetSyncEquipHandler);
    }

    public static void sendPacketDamage(Entity entity, float shake){
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entity.getId());
        buf.writeFloat(shake);
        for (ServerPlayer player : PlayerLookup.tracking(entity)) {
            ServerPlayNetworking.send(player,DAMAGE_PACKET_ID, buf);
        }

    }

    private static void packetDamageHandler(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender){
        int id = buf.readInt();
        float shake = buf.readFloat();
        client.execute(()->{

            Entity entity = Minecraft.getInstance().level.getEntity(id);
            if (entity instanceof TargetDummyEntity) {
                TargetDummyEntity dummy = (TargetDummyEntity) entity;
                dummy.animationPosition = shake;
            }
        });
    }

    public static void sendPacketSyncEquip(Entity entity, int slot, ItemStack item){
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entity.getId());
        buf.writeInt(slot);
        buf.writeItem(item);
        for (ServerPlayer player : PlayerLookup.tracking(entity)) {
            ServerPlayNetworking.send(player,EQUIP_PACKET_ID, buf);
        }
    }

    private static void packetSyncEquipHandler(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender){
        int id = buf.readInt();
        int slot = buf.readInt();
        ItemStack item = buf.readItem();
        client.execute(()->{
            Entity entity = Minecraft.getInstance().level.getEntity(id);
            if (entity instanceof TargetDummyEntity) {
                TargetDummyEntity dummy = (TargetDummyEntity) entity;
                dummy.setItemSlot(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, slot), item);
            }
        });
    }




	/*
	public static SimpleChannel INSTANCE;
	private static int ID = 0;
	private static final String PROTOCOL_VERSION = "1";
	public static int nextID() {
		return ID++;
	}


	public static void registerMessages() {
		INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(DummmmmmyMod.MOD_ID, "dummychannel"), () -> PROTOCOL_VERSION,
				PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

		INSTANCE.registerMessage(nextID(), PacketDamageNumber.class, PacketDamageNumber::toBytes, PacketDamageNumber::new,
				PacketDamageNumber::handle);

		INSTANCE.registerMessage(nextID(), PacketSyncEquip.class, PacketSyncEquip::toBytes, PacketSyncEquip::new,
				PacketSyncEquip::handle);


		INSTANCE.registerMessage(nextID(), PacketChangeSkin.class, PacketChangeSkin::toBytes, PacketChangeSkin::new,
				PacketChangeSkin::handle);

	}


	private interface Message{}
	public static void sendToAllTracking(Entity entity, ServerWorld world, Message message) {
		world.getChunkSource().broadcast(entity, INSTANCE.toVanillaPacket(message, NetworkDirection.PLAY_TO_CLIENT));
	}



	public static class PacketChangeSkin implements Message{
		private final int entityID;
		private final boolean skin;

		public PacketChangeSkin(PacketBuffer buf) {
			this.entityID = buf.readInt();
		    this.skin = buf.readBoolean();
		}

		public PacketChangeSkin(int entityId, boolean skin) {
			this.entityID = entityId;
			this.skin = skin;
		}

		public void toBytes(PacketBuffer buf) {
			buf.writeInt(this.entityID);
		    buf.writeBoolean(this.skin);  
		}

		public void handle(Supplier<NetworkEvent.Context> ctx) {
			ctx.get().enqueueWork(() -> {
				Entity entity = Minecraft.getInstance().level.getEntity(this.entityID);
				if (entity instanceof TargetDummyEntity) {
					TargetDummyEntity dummy = (TargetDummyEntity) entity;
					dummy.sheared=this.skin;
				}
			});
			ctx.get().setPacketHandled(true);
		}
	}
*/
	
}
