package net.mehvahdjukaar.dummmmmmy.client;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.mehvahdjukaar.dummmmmmy.DummmmmmyMod;
import net.mehvahdjukaar.dummmmmmy.common.Configs;
import net.mehvahdjukaar.dummmmmmy.entity.TargetDummyEntity;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;

public class TargetDummyRenderer extends HumanoidMobRenderer<TargetDummyEntity, TargetDummyModel<TargetDummyEntity>> {

    public TargetDummyRenderer(EntityRenderDispatcher renderManagerIn, EntityRendererRegistry.Context context) {
        super(renderManagerIn, new TargetDummyModel<>(), 0);
        this.addLayer(new LayerDummyArmor<>(this, new TargetDummyModel<>(EquipmentSlot.LEGS), new TargetDummyModel<>(EquipmentSlot.CHEST)));
    }

    private static final ResourceLocation TEXT_0 = new ResourceLocation(DummmmmmyMod.MOD_ID+":textures/dummy.png");
    private static final ResourceLocation TEXT_1 = new ResourceLocation(DummmmmmyMod.MOD_ID+":textures/dummy_1.png");
    private static final ResourceLocation TEXT_2 = new ResourceLocation(DummmmmmyMod.MOD_ID+":textures/dummy_2.png");
    private static final ResourceLocation TEXT_0_S = new ResourceLocation(DummmmmmyMod.MOD_ID+":textures/dummy_h.png");
    private static final ResourceLocation TEXT_1_S = new ResourceLocation(DummmmmmyMod.MOD_ID+":textures/dummy_1_h.png");
    private static final ResourceLocation TEXT_2_S = new ResourceLocation(DummmmmmyMod.MOD_ID+":textures/dummy_2_h.png");


    @Override
    public ResourceLocation getTextureLocation(TargetDummyEntity entity) {
        boolean s = entity.sheared;
        switch (Configs.SKIN){
            default:
            case 0:
                return s?TEXT_0_S:TEXT_0;
            case 1:
                return s?TEXT_1_S:TEXT_1;
            case 2:
                return s?TEXT_2_S:TEXT_2;
        }
    }
}
