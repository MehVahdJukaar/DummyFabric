package net.mehvahdjukaar.dummmmmmy.mixin;

import net.mehvahdjukaar.dummmmmmy.common.MixinHelper;
import net.mehvahdjukaar.dummmmmmy.entity.TargetDummyEntity;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingHurtMixin extends Entity {
	public LivingHurtMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(at = @At("HEAD"), method = "actuallyHurt")
	private void actuallyHurt(DamageSource source, float amount, CallbackInfo info) {
		if(!this.isInvulnerableTo(source)){
			//MixinHelper.damageDummy(this,source,amount);
		}
	}
}
