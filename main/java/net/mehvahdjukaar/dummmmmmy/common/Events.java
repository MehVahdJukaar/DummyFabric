package net.mehvahdjukaar.dummmmmmy.common;

public class Events {
/*
    @SubscribeEvent
    public static void onEntityCriticalHit(CriticalHitEvent event) {
        if (event != null && event.getEntity() != null) {
            Entity target = event.getTarget();
            if (event.getDamageModifier() == 1.5 && target instanceof TargetDummyEntity) {
                TargetDummyEntity dummy = (TargetDummyEntity) target;
                dummy.critical = true;
            }
        }
    }

    public static boolean isScared(Entity entity){
        String name = entity.getType().getRegistryName().toString();
        return (entity instanceof AnimalEntity || Configs.cachedServer.WHITELIST.contains(name))
                && !Configs.cachedServer.BLACKLIST.contains(name);
    }

    public static boolean isScarecrowInRange(Entity entity, World world){
        return !world.getEntities(ModRegistry.TARGET_DUMMY.get(), entity.getBoundingBox().inflate(10),
                TargetDummyEntity::isScarecrow).isEmpty();
    }

    //prevents them from spawning
    @SubscribeEvent
    public static void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        if(!(event.getWorld()instanceof World))return;
        World world = event.getEntity().level;

        Entity entity = event.getEntity();
        if(isScared(entity)){
            if(isScarecrowInRange(entity, world)) event.setResult(Event.Result.DENY);
        }
    }

    //add goal
    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if(event.getWorld() == null)return;
        Entity e = event.getEntity();
        if (e instanceof CreatureEntity && isScared(e)) {

            CreatureEntity mob = (CreatureEntity) e;
            mob.goalSelector.addGoal(0, new AvoidEntityGoal<>(mob, TargetDummyEntity.class,
                    Configs.cachedServer.RADIUS, 1.0D, 1.3D, d -> ((TargetDummyEntity) d).isScarecrow()));

        }
    }
    */

}
