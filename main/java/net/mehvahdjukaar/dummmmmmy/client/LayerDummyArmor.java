package net.mehvahdjukaar.dummmmmmy.client;

import net.mehvahdjukaar.dummmmmmy.entity.TargetDummyEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;

public class LayerDummyArmor<T extends TargetDummyEntity, M extends HumanoidModel<T>, A extends TargetDummyModel<T>> extends HumanoidArmorLayer<T, M, A> {

    public LayerDummyArmor(RenderLayerParent<T, M> renderer, A modelLegs, A modelChest) {
        super(renderer, modelLegs, modelChest);
    }


    @Override
    public void setPartVisibility(A modelIn, EquipmentSlot slotIn) {
        modelIn.setAllVisible(false);
        //boolean flag = modelIn instanceof  TargetDummyModel;
        modelIn.rightLeg.visible = false;
        modelIn.standPlate.visible = false;
        switch (slotIn) {
            case HEAD -> modelIn.head.visible = true;
            case CHEST -> {
                modelIn.body.visible = true;
                modelIn.rightArm.visible = true;
                modelIn.leftArm.visible = true;
            }
            case LEGS -> {
                modelIn.body.visible = true;
                modelIn.leftLeg.visible = true;
            }
            case FEET -> {
                modelIn.leftLeg.visible = true;
                modelIn.body.visible = false;
            }
        }
    }

}