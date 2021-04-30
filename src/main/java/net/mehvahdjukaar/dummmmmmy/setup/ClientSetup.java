package net.mehvahdjukaar.dummmmmmy.setup;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.mehvahdjukaar.dummmmmmy.client.NumberRenderer;
import net.mehvahdjukaar.dummmmmmy.client.TargetDummyRenderer;
import net.mehvahdjukaar.dummmmmmy.common.NetworkHandler;


public class ClientSetup implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
        NetworkHandler.registerClientReceivers();
        EntityRendererRegistry.INSTANCE.register(ModRegistry.TARGET_DUMMY, TargetDummyRenderer::new);
        EntityRendererRegistry.INSTANCE.register(ModRegistry.DUMMY_NUMBER, NumberRenderer::new);
    }
}
