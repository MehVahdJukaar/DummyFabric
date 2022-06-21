package net.mehvahdjukaar.dummmmmmy.setup;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.impl.networking.ClientSidePacketRegistryImpl;
import net.mehvahdjukaar.dummmmmmy.DummmmmmyMod;
import net.mehvahdjukaar.dummmmmmy.client.NumberRenderer;
import net.mehvahdjukaar.dummmmmmy.client.TargetDummyModel;
import net.mehvahdjukaar.dummmmmmy.client.TargetDummyRenderer;
import net.mehvahdjukaar.dummmmmmy.common.NetworkHandler;
import net.mehvahdjukaar.dummmmmmy.entity.DummyNumberEntity;
import net.mehvahdjukaar.dummmmmmy.entity.TargetDummyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.*;


public class ClientHandler implements ClientModInitializer {


    public static Map<ModelLayerLocation, LayerDefinition> LAYERS = new HashMap<>();

    @Override
    public void onInitializeClient() {
        registerClientReceivers();
        EntityRendererRegistry.register(ModRegistry.TARGET_DUMMY, TargetDummyRenderer::new);
        EntityRendererRegistry.register(ModRegistry.DUMMY_NUMBER, NumberRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(DUMMY_BODY, () -> TargetDummyModel.createMesh(0, 64));
        EntityModelLayerRegistry.registerModelLayer(DUMMY_ARMOR_OUTER, () -> TargetDummyModel.createMesh(1, 32));
        EntityModelLayerRegistry.registerModelLayer(DUMMY_ARMOR_INNER, () -> TargetDummyModel.createMesh(0.5f, 32));
    }

    private static ModelLayerLocation loc(String name) {
        return new ModelLayerLocation(DummmmmmyMod.res(name), name);
    }

    public static ModelLayerLocation DUMMY_BODY = loc("dummy");
    public static ModelLayerLocation DUMMY_ARMOR_OUTER = loc("dummy_armor_outer");
    public static ModelLayerLocation DUMMY_ARMOR_INNER = loc("dummy_armor_inner");


    public static void registerClientReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(NetworkHandler.DAMAGE_PACKET_ID, ClientHandler::packetDamageHandler);
        ClientPlayNetworking.registerGlobalReceiver(NetworkHandler.EQUIP_PACKET_ID, ClientHandler::packetSyncEquipHandler);
        ClientPlayNetworking.registerGlobalReceiver(NetworkHandler.SPAWN_NUMBER_PACKET_ID, ClientHandler::packetSpawnDummy);
    }

    private static void packetSpawnDummy(Minecraft client, ClientPacketListener clientPacketListener, FriendlyByteBuf packet, PacketSender packetSender) {
        double x = packet.readDouble();
        double y = packet.readDouble();
        double z = packet.readDouble();

        int entityId = packet.readInt();
        float number = packet.readFloat();
        int color = packet.readInt();
        int type = packet.readInt();

        int size = packet.readInt();
        Set<UUID> set = new HashSet<>();
        for (int j = 0; j < size; j++) {
            set.add(packet.readUUID());
        }

        client.execute(() -> {
            DummyNumberEntity numberEntity = new DummyNumberEntity(number, color, type, Minecraft.getInstance().level, set);
            numberEntity.setId(entityId);
            numberEntity.setPos(x, y, z);
            numberEntity.setAnimation(type);
            Minecraft.getInstance().level.putNonPlayerEntity(entityId, numberEntity);
        });
    }

    private static void packetSyncEquipHandler(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        int id = buf.readInt();
        int slot = buf.readInt();
        ItemStack item = buf.readItem();
        client.execute(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(id);
            if (entity instanceof TargetDummyEntity dummy) {
                dummy.setItemSlot(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, slot), item);
            }
        });
    }

    private static void packetDamageHandler(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        int id = buf.readInt();
        float shake = buf.readFloat();
        client.execute(() -> {

            Entity entity = Minecraft.getInstance().level.getEntity(id);
            if (entity instanceof TargetDummyEntity dummy) {
                dummy.animationPosition = shake;
            }
        });
    }

}
