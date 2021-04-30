package net.mehvahdjukaar.dummmmmmy;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.mehvahdjukaar.dummmmmmy.entity.TargetDummyEntity;
import net.mehvahdjukaar.dummmmmmy.setup.ModRegistry;

public class DummmmmmyMod implements ModInitializer {

	public static final String MOD_ID = "dummmmmmy";

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		System.out.println("Hello Fabric world!");

		FabricDefaultAttributeRegistry.register(ModRegistry.TARGET_DUMMY, TargetDummyEntity.setCustomAttributes());


		ModRegistry.init();
	}

}
