
package net.mehvahdjukaar.dummmmmmy.entity;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.mehvahdjukaar.dummmmmmy.DummmmmmyMod;
import net.mehvahdjukaar.dummmmmmy.setup.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;


public class DummyNumberEntity extends Entity {
    public static final List<Float> POSITIONS = new ArrayList<>(Arrays.asList(0f, -0.25f, 0.12f, -0.12f, 0.25f));
    protected static final int MAXAGE = 35;

    public int age;
    public float number = 69420;
    protected float speed = 1;
    public float dy = 0;
    public float prevDy = 0;
    public int color = 0xffffffff;
    public float dx = 0;
    public float prevDx = 0;
    public float speedx = 0;
    public float fadeout = -1;
    public float prevFadeout = -1;
    private int type = -1; //used for location in array
    protected final Random rand = new Random();

    private final Collection<UUID> targetPlayers = new HashSet<>();

    public DummyNumberEntity(EntityType<DummyNumberEntity> type, Level world) {
        super(type, world);
    }

    public DummyNumberEntity(float number, int color, int type, Level world, Collection<UUID> players) {
        this(ModRegistry.DUMMY_NUMBER, world);
        this.number = number;
        this.color = color;
        this.type = type;
        this.targetPlayers.clear();
        this.targetPlayers.addAll(players);
    }

    public void setAnimation(int i) {
        if (i != -1) {
            this.speedx = POSITIONS.get(i % POSITIONS.size());
        } else {
            //this.speedx = (this.rand.nextFloat() - 0.5f) / 2f;
            this.speedx = POSITIONS.get(this.rand.nextInt(POSITIONS.size()));
        }
    }

    //have to give him some attributes or server will throw errors

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20D);
    }

    public static final ResourceLocation SPAWN_NUMBER_PACKET_ID = new ResourceLocation(DummmmmmyMod.MOD_ID, "packet_spawn_number");

    @Override
    public Packet<?> getAddEntityPacket() {
        FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        packet.writeDouble(getX());
        packet.writeDouble(getY());
        packet.writeDouble(getZ());
        packet.writeInt(this.getId());
        packet.writeFloat(this.number);
        packet.writeInt(this.color);
        packet.writeInt(this.type);
        packet.writeInt(this.targetPlayers.size());
        this.targetPlayers.forEach(packet::writeUUID);
        return ServerPlayNetworking.createS2CPacket(SPAWN_NUMBER_PACKET_ID, packet);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        // super.readAdditional(compound);
        this.number = compound.getFloat("Number");
        this.color = compound.getInt("Color");
        this.age = compound.getInt("Age");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        // super.writeAdditional(compound);
        compound.putFloat("Number", this.number);
        compound.putInt("Color", this.color);
        compound.putInt("Age", this.age);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public void tick() {
        if (this.age++ > MAXAGE || this.getY() < -64.0D) {
            this.remove(RemovalReason.DISCARDED);
        }
        if (this.level.isClientSide) {
            float lenght = 6;
            this.prevFadeout = this.fadeout;
            this.fadeout = this.age > (MAXAGE - lenght) ? ((float) MAXAGE - this.age) / lenght : 1;


            // this.forceSetPosition(this.getPosX(), this.getPosY() + (this.speed / 2),
            // this.getPosZ());
            this.prevDy = this.dy;
            this.dy += this.speed;
            this.prevDx = this.dx;
            this.dx += this.speedx;
            // this.speed / 500d;
            //spawn numbers in a sort of elliple centered on his torso
            if (Math.sqrt(Math.pow(this.dx * 1.5, 2) + Math.pow(this.dy - 1, 2)) < 1.9 - 1) {

                speed = speed / 2;
            } else {
                speed = 0;
                speedx = 0;
            }
        }
    }

    public float getNumber() {
        return this.number;
    }

    @Override
    public boolean causeFallDamage(float f, float g, DamageSource damageSource) {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }


    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    public void setNoGravity(boolean ignored) {
        super.setNoGravity(true);
    }

    public boolean canPlayerSee(Player player) {
        return player == null || this.targetPlayers.contains(player.getUUID());
    }
}
