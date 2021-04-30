package net.mehvahdjukaar.dummmmmmy.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public class LayerDummyArmor<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends HumanoidArmorLayer<T, M, A > {
    /*public LayerDummyArmor(IEntityRenderer<LivingEntity, BipedModel<LivingEntity>> p_i50936_1_, BipedModel<LivingEntity> p_i50936_2_,
                           BipedModel<LivingEntity> p_i50936_3_) {
        super(p_i50936_1_, new TargetDummyRenderer(0.5F, 0, 64, 32, -0.01f), new TargetDummyRenderer(1.0F, 0, 64, 32, -0.01f));
    }*/


    public LayerDummyArmor(RenderLayerParent<T, M> renderer, A modelLegs, A modelChest) {


        //super(p_i50936_1_, (A) new TargetDummyModel<T>(0.5F, 0, 64, 32, -0.01f), (A) new TargetDummyModel<T>(1.0F, 0, 64, 32, -0.01f));
        super(renderer,modelLegs,modelChest);

    }


    @Override
    public void setPartVisibility(A modelIn, EquipmentSlot slotIn) {
        modelIn.setAllVisible(false);
        //boolean flag = modelIn instanceof  TargetDummyModel;
        modelIn.rightLeg.visible = false;
        switch (slotIn) {
            case HEAD:
                modelIn.head.visible = true;
                break;
            case CHEST:
                modelIn.body.visible = true;
                modelIn.rightArm.visible = true;
                modelIn.leftArm.visible = true;
                break;
            case LEGS:
                modelIn.body.visible = true;
                modelIn.leftLeg.visible = true;
                break;
            case FEET:
                modelIn.leftLeg.visible = true;
                modelIn.body.visible = false;
        }
    }

}