package net.mehvahdjukaar.dummmmmmy.setup;

import net.mehvahdjukaar.dummmmmmy.DummmmmmyMod;
import net.mehvahdjukaar.dummmmmmy.entity.DummyNumberEntity;
import net.mehvahdjukaar.dummmmmmy.entity.TargetDummyEntity;
import net.mehvahdjukaar.dummmmmmy.item.TargetDummyItem;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

@SuppressWarnings("unused")
public class ModRegistry {


    public static void init() {

        Registry.register(Registry.ITEM, new ResourceLocation(DummmmmmyMod.MOD_ID, "target_dummy_placer"), DUMMY_ITEM);

        Registry.register(Registry.ENTITY_TYPE, new ResourceLocation(DummmmmmyMod.MOD_ID, TARGET_DUMMY_NAME), TARGET_DUMMY);
        Registry.register(Registry.ENTITY_TYPE, new ResourceLocation(DummmmmmyMod.MOD_ID, DUMMY_NUMBER_NAME), DUMMY_NUMBER);
    }


    public static final String TARGET_DUMMY_NAME = "target_dummy";
    public static final EntityType<TargetDummyEntity> TARGET_DUMMY = EntityType.Builder.<TargetDummyEntity>of(
            TargetDummyEntity::new, MobCategory.MISC)
                    .sized(0.6f, 2f)
            .build(TARGET_DUMMY_NAME);

    public static final String DUMMY_NUMBER_NAME = "dummy_number";
    public static final EntityType<DummyNumberEntity> DUMMY_NUMBER = EntityType.Builder.<DummyNumberEntity>of(
            DummyNumberEntity::new, MobCategory.MISC)
                    .clientTrackingRange(16)
                    .sized(0.6f, 1.8f)
            .build(DUMMY_NUMBER_NAME);

    public static final Item DUMMY_ITEM = new TargetDummyItem(new Item.Properties().
            tab(CreativeModeTab.TAB_COMBAT).stacksTo(16));


}