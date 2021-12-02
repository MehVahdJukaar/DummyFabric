package net.mehvahdjukaar.dummmmmmy;

import net.fabricmc.api.ModInitializer;
import net.mehvahdjukaar.dummmmmmy.setup.ModRegistry;
import net.mehvahdjukaar.dummmmmmy.setup.ModSetup;
import net.minecraft.resources.ResourceLocation;

public class DummmmmmyMod implements ModInitializer {

	public static ResourceLocation res(String name){
		return new ResourceLocation(MOD_ID, name);
	}

	public static final String MOD_ID = "dummmmmmy";

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		//System.out.println("Hello Fabric world!");


		ModRegistry.init();
		ModSetup.init();

	}

}
