package net.mehvahdjukaar.dummmmmmy.setup;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.mehvahdjukaar.dummmmmmy.entity.TargetDummyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;


public class ModSetup {

    public static void init() {
        FabricDefaultAttributeRegistry.register(ModRegistry.TARGET_DUMMY, TargetDummyEntity.setCustomAttributes());
        //DefaultAttributeRegistryAccessor.getRegistry().put(ModRegistry.TARGET_DUMMY, TargetDummyEntity.setCustomAttributes().build());
        //DefaultAttributeRegistryAccessor.getRegistry().put(ModRegistry.DUMMY_NUMBER, DummyNumberEntity.setCustomAttributes().build());

        //NetworkHandler.registerServerReceivers();
        //NetworkHandler.registerMessages();

        DispenserBlock.registerBehavior(ModRegistry.DUMMY_ITEM, new SpawnDummyBehavior());
    }

    public static class SpawnDummyBehavior implements DispenseItemBehavior {
        @Override
        public ItemStack dispense(BlockSource dispenser, ItemStack itemStack) {


            Direction direction = dispenser.getBlockState().getValue(DispenserBlock.FACING);
            BlockPos pos = dispenser.getPos().relative(direction);

            TargetDummyEntity dummy = new TargetDummyEntity(dispenser.getLevel());
            float f = direction.toYRot();
            dummy.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, f, 0.0F);

            //EntityType.applyItemNBT(world, context.getPlayer(), dummy, itemstack.getTag());
            dispenser.getLevel().addFreshEntity(dummy);

            itemStack.shrink(1);

            dispenser.getLevel().levelEvent(1000, dispenser.getPos(), 0);

            return itemStack;
        }


    }

}
