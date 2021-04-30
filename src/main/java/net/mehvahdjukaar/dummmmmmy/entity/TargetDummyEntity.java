
package net.mehvahdjukaar.dummmmmmy.entity;

import net.mehvahdjukaar.dummmmmmy.common.Configs;
import net.mehvahdjukaar.dummmmmmy.common.NetworkHandler;
import net.mehvahdjukaar.dummmmmmy.setup.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TargetDummyEntity extends Mob{

	public float prevLimbswing=0;
	public float prevShakeAmount=0;

	public float shakeAmount=0; // used to have an independent start for the animation, otherwise the phase of
									// the animation depends ont he damage dealt
									// used to calculate the whole damage in one tick, in case there are multiple
									// sources

	public float lastDamage;
	public int lastDamageTick;
	public int firstDamageTick; // indicates when we started taking damage and if != 0 it also means that we are
								// currently recording damage taken
	public float damageTaken;
	public boolean critical = false; //has just been hit by critical?
	public MobAttribute mobType = MobAttribute.UNDEFINED; //0=undefined, 1=undead, 2=water, 3= illager
	private final List<ServerPlayer> currentlyAttacking = new ArrayList<>();//players to which display dps message
	private int mynumberpos = 0;
	public boolean sheared = false;
	//public DummyNumberEntity.CustomEntity myLittleNumber;
	private final NonNullList<ItemStack> inventoryArmor = NonNullList.withSize(4, ItemStack.EMPTY);



	public TargetDummyEntity(EntityType<TargetDummyEntity> type, Level world) {
		super(type, world);
		xpReward = 0;
		//so can take all sorts of damage
		//setNoAI(true);
		Arrays.fill(this.armorDropChances, 1.1f);

	}

	public TargetDummyEntity(Level world) {
		this(ModRegistry.TARGET_DUMMY, world);
	}



	/*

	public TargetDummyEntity(FMLPlayMessages.SpawnEntity packet, Level world) {
		this(ModRegistry.TARGET_DUMMY, world);
	}

	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	//stuff needed by client only?
	//called when entity is first spawned/loaded
	@Override
	public void writeSpawnData(PacketBuffer buffer) {
		buffer.writeBoolean(this.sheared);
		//hijacking this method to do some other server calculations. there's probably an event just for this but I haven't found it
		this.updateOnLoadServer();

	}

	//called when entity is first spawned/loaded
	@Override
	public void readSpawnData(PacketBuffer additionalData) {
		this.sheared = additionalData.readBoolean();
		//and this as well to do some other client calculations
		this.updateOnLoadClient();
	}*/

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putInt("Type", this.mobType.ordinal());
		tag.putInt("NumberPos", this.mynumberpos);
		tag.putBoolean("Sheared", this.sheared);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.mobType = MobAttribute.values()[tag.getInt("Type")];
		this.mynumberpos = tag.getInt("NumberPos");
		this.sheared = tag.getBoolean("Sheared");
	}

	public void updateOnLoadClient() {
		float r = this.yRot;
		this.yHeadRotO = this.yHeadRot = r;
		this.yRotO = r;
		this.yBodyRotO = this.yBodyRot = r;

	}
	public void updateOnLoadServer(){
		this.applyEquipmentModifiers();
	}


	// dress it up! :D
	@Override
	public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
		boolean invchanged = false;
		if (!player.isSpectator() && player.abilities.mayBuild) {
			ItemStack itemstack = player.getItemInHand(hand);
			EquipmentSlot equipmentslottype = getEquipmentSlotForItem(itemstack);

			Item item = itemstack.getItem();

			//special items
			if(item instanceof BannerItem || this.isPumpkin(item)){
				equipmentslottype = EquipmentSlot.HEAD;
			}


			// empty hand -> unequip
			if (itemstack.isEmpty() && hand == InteractionHand.MAIN_HAND) {
				equipmentslottype = this.getClickedSlot(vec);
				if (this.hasItemInSlot(equipmentslottype)) {
					if(player.level.isClientSide)return InteractionResult.CONSUME;
					this.unequipArmor(player, equipmentslottype, itemstack, hand);
					invchanged = true;

				}
			}
			// armor item in hand -> equip/swap
			else if (equipmentslottype.getType() == EquipmentSlot.Type.ARMOR) {
				if(player.level.isClientSide)return InteractionResult.CONSUME;
				this.equipArmor(player, equipmentslottype, itemstack, hand);
				invchanged = true;

			}
			//remove sack
			else if (item instanceof ShearsItem) {
				if (!this.sheared) {
					if(player.level.isClientSide)return InteractionResult.CONSUME;
					this.sheared = true;
					if (!this.level.isClientSide) {
						//NetworkHandler.sendToAllTracking(this, (ServerLevel) this.level,new NetworkHandler.PacketChangeSkin(this.getId(), true));
					}
					return InteractionResult.SUCCESS;
				}
			}


			if (invchanged) {
				if (!this.level.isClientSide) {
					NetworkHandler.sendPacketSyncEquip(this,equipmentslottype.getIndex(), itemstack);
				}
				//this.applyEquipmentModifiers();
				return InteractionResult.SUCCESS;
			}


		}
		return InteractionResult.PASS;
	}

	private void unequipArmor(Player player, EquipmentSlot slot, ItemStack stack, InteractionHand hand) {
		// set slot to stack which is empty stack
		ItemStack itemstack = this.getItemBySlot(slot);
		ItemStack itemstack2 = itemstack.copy();

		player.setItemInHand(hand, itemstack2);
		this.setItemSlot(slot, stack);

		//this.applyEquipmentModifiers();
		//now done here^
		this.getAttributes().removeAttributeModifiers(itemstack2.getAttributeModifiers(slot));
		//clear mob type
		if(slot==EquipmentSlot.HEAD)this.mobType = MobAttribute.UNDEFINED;

	}

	private void equipArmor(Player player, EquipmentSlot slot, ItemStack stack, InteractionHand hand) {
		ItemStack currentItem = this.getItemBySlot(slot);
		ItemStack newItem = stack.copy();
		newItem.setCount(1);

		player.setItemInHand(hand, ItemUtils.createFilledResult(stack.copy(), player, currentItem, player.isCreative()));

		this.playEquipSound(newItem);
		this.setItemSlot(slot, newItem);

		//this.applyEquipmentModifiers();
		//now done here^
		this.getAttributes().addTransientAttributeModifiers(newItem.getAttributeModifiers(slot));
		if(slot==EquipmentSlot.HEAD) {
			//add mob type
			if (this.isUndeadSkull(newItem)) this.mobType = MobAttribute.UNDEAD;
			else if (newItem.getItem() == Items.TURTLE_HELMET) this.mobType = MobAttribute.WATER;
			else if (newItem.getItem() == Items.DRAGON_HEAD) this.mobType = MobAttribute.ARTHROPOD;
			else if (ItemStack.matches(newItem, Raid.getLeaderBannerInstance())) this.mobType = MobAttribute.ILLAGER;
			else if (this.isPumpkin(newItem.getItem())) this.mobType = MobAttribute.SCARECROW;
			else this.mobType = MobAttribute.UNDEFINED;
		}
	}

	private boolean isPumpkin(Item item){
		if(item instanceof BlockItem){
			Block block = ((BlockItem) item).getBlock();
			String name = item.getName(new ItemStack(item)).getString();
			return block instanceof CarvedPumpkinBlock || name.contains("pumpkin") || name.contains("jack_o");
		}
		return false;
	}

	private boolean isUndeadSkull(ItemStack itemstack){
		Item i =itemstack.getItem();
		return i == Items.WITHER_SKELETON_SKULL ||
				i == Items.SKELETON_SKULL ||
				i == Items.ZOMBIE_HEAD;
	}

	public boolean isScarecrow(){
		return this.mobType== MobAttribute.SCARECROW;
	}

	private EquipmentSlot getClickedSlot(Vec3 p_190772_1_) {
		EquipmentSlot equipmentslottype = EquipmentSlot.MAINHAND;
		double d0 = p_190772_1_.y;
		EquipmentSlot equipmentslottype1 = EquipmentSlot.FEET;
		if (d0 >= 0.1D && d0 < 0.1D + ( 0.45D) && this.hasItemInSlot(equipmentslottype1)) {
			equipmentslottype = EquipmentSlot.FEET;
		} else if (d0 >= 0.9D + ( 0.0D) && d0 < 0.9D + ( 0.7D) && this.hasItemInSlot(EquipmentSlot.CHEST)) {
			equipmentslottype = EquipmentSlot.CHEST;
		} else if (d0 >= 0.4D && d0 < 0.4D + ( 0.8D) && this.hasItemInSlot(EquipmentSlot.LEGS)) {
			equipmentslottype = EquipmentSlot.LEGS;
		} else if (d0 >= 1.6D && this.hasItemInSlot(EquipmentSlot.HEAD)) {
			equipmentslottype = EquipmentSlot.HEAD;
		}
		return equipmentslottype;
	}

	public void applyEquipmentModifiers() {
		//living entity code here. apparently every entity does this check every tick.
		//trying instead to run it only when needed instead
		if (!this.level.isClientSide) {
			for (EquipmentSlot equipmentslottype : EquipmentSlot.values()) {
				ItemStack itemstack;
				if (equipmentslottype.getType()== EquipmentSlot.Type.ARMOR) {
					itemstack = this.inventoryArmor.get(equipmentslottype.getIndex());

					ItemStack itemstack1 = this.getItemBySlot(equipmentslottype);
					if (!ItemStack.matches(itemstack1, itemstack)) {
						if (!itemstack1.equals(itemstack))
							//send packet
							NetworkHandler.sendPacketSyncEquip(this,equipmentslottype.getIndex(), itemstack);

						if (!itemstack.isEmpty()) {
							this.getAttributes().removeAttributeModifiers(itemstack.getAttributeModifiers(equipmentslottype));
						}
						if (!itemstack1.isEmpty()) {
							this.getAttributes().addTransientAttributeModifiers(itemstack1.getAttributeModifiers(equipmentslottype));
						}
					}
				}
			}
		}
	}


	public void dropEquipment() {
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (slot.getType() != EquipmentSlot.Type.ARMOR) {
				continue;
			}
			ItemStack armor = getItemBySlot(slot);
			if (!armor.isEmpty()) {
				this.spawnAtLocation(armor, 1.0f);
			}
		}
	}

	public void dismantle(boolean drops) {
		if (!this.level.isClientSide) {
			if (drops) {
				this.dropEquipment();
				this.spawnAtLocation(ModRegistry.DUMMY_ITEM, 1);
			}
			this.level.playSound(null, this.getX(), this.getY(), this.getZ(), this.getDeathSound(),
					this.getSoundSource(), 1.0F, 1.0F);

			((ServerLevel) this.level).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.defaultBlockState()),
					this.getX(), this.getY(0.6666666666666666D), this.getZ(), 10,  (this.getBbWidth() / 4.0F),
					(this.getBbHeight() / 4.0F),  (this.getBbWidth() / 4.0F), 0.05D);
			this.remove();
		}
	}

	@Override
	public void kill() {
	  this.dismantle(true);
	}

	public int getColorFromDamageSource(DamageSource source){

		if(this.critical) return Configs.DAMAGE_CRIT;
		if(source == DamageSource.DRAGON_BREATH) return Configs.DAMAGE_DRAGON;
		if(source == DamageSource.WITHER)return  Configs.DAMAGE_WITHER;
		if(source.msgId.equals("explosion") || source.msgId.equals("explosion.player"))
			return Configs.DAMAGE_EXPLOSION;
		if(source.msgId.equals("indirectMagic"))return Configs.DAMAGE_IND_MAGIC;
		if(source.msgId.equals("trident"))return Configs.DAMAGE_TRIDENT;
		if(source == DamageSource.GENERIC)return Configs.DAMAGE_GENERIC;

		if(source == DamageSource.MAGIC){
			//would really like to detect poison damage but i don't think there's simple way

			return Configs.DAMAGE_MAGIC;
		}
		if (source == DamageSource.HOT_FLOOR || source == DamageSource.LAVA || source == DamageSource.ON_FIRE
				|| source ==DamageSource.IN_FIRE) return Configs.DAMAGE_FIRE;
		if (source == DamageSource.LIGHTNING_BOLT) return Configs.DAMAGE_LIGHTNING;

		if (source == DamageSource.CACTUS || source ==DamageSource.SWEET_BERRY_BUSH)return Configs.DAMAGE_CACTUS;

		return 0xffffff;
	}

	@Override
	public boolean hurt(DamageSource source, float damage) {

		if (this.isInvulnerableTo(source)) return false;

		//super.attackEntityFrom(source, damage);

		//not immune to void damage, immune to drown, wall
		if(source == DamageSource.OUT_OF_WORLD){ this.remove();return true;}
		if(source == DamageSource.DROWN || source == DamageSource.IN_WALL) return false;
		//workaround for wither boss, otherwise it would keep targeting the dummy forever
		if(source.getDirectEntity() instanceof WitherBoss || source.getEntity() instanceof WitherBoss){
			this.dismantle(true);
			return true;

		}

		//lots of living entity code here
		if (source.isFire() && this.hasEffect(MobEffects.FIRE_RESISTANCE)) return false;

	   if ((source == DamageSource.ANVIL || source == DamageSource.FALLING_BLOCK) && !this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
		//this.getItemStackFromSlot(EquipmentSlotType.HEAD).damageItem((int)(damage * 4.0F + this.rand.nextFloat() * damage * 2.0F), this, (p_213341_0_) -> p_213341_0_.sendBreakAnimation(EquipmentSlotType.HEAD));
		damage *= 0.75F;
		}


		// dismantling + adding players to dps message list
		if (source.msgId.equals("player") || source.getEntity() instanceof Player) {
			Player player = (Player) source.getEntity();
			if (!level.isClientSide) {
				ServerPlayer sp = (ServerPlayer) player;
				if (!this.currentlyAttacking.contains(sp)) {
					this.currentlyAttacking.add(sp);
				}

			}
			// shift-leftclick with empty hand dismantles
			if (player.isShiftKeyDown() && player.getMainHandItem().isEmpty()) {
				dismantle(!player.isCreative());
				return false;
			}
		}


		//vanilla livingentity code down here

		//check if i'm on invulnerability frame
		if ((float)this.invulnerableTime > 10.0F) {

			//check if i received damage greater that previous. if not do nothing cause bigger damage overrides smaller

			//currently instant damage tipped arrows do not work cause of this vanilla code. (pot damage overrides arrow since thay happen in same tick...)
			//Awesome game design right here mojang!
			if (damage <= this.lastDamage) {
			   return false;

			}
			//if true deal that damage minus the one i just inflicted.

			float ld = this.lastDamage;
			this.lastDamage = damage;
			damage = damage - ld;

		 } else {
			//if i'm not on invulnerability frame deal normal damage and reset cooldowns

			this.lastDamage = damage;
			this.invulnerableTime = 20;
			this.hurtDuration = 10;

			//don't know what this does. probably sends a packet of some sort. seems to be related to red overlay so I disabled
			//this.world.setEntityState(this, (byte)2);
		 }

		//set to 0 to disable red glow that happens when hurt
		this.hurtTime = 0;//this.maxHurtTime;



		// calculate the ACTUAL damage done after armor n stuff

		if (!level.isClientSide) {
			//damage = mixinLastDamage; //ForgeHooks.onLivingHurt(this, source, damage);

			if (damage > 0) {
				damage = this.getDamageAfterArmorAbsorb(source, damage);
				damage = this.getDamageAfterMagicAbsorb(source, damage);
				float f1 = damage;
				damage = Math.max(damage - this.getAbsorptionAmount(), 0.0F);
				this.setAbsorptionAmount(this.getAbsorptionAmount() - (f1 - damage));
			}
		}
		// magic code ^


		// damage in the same tick, add it
		if (lastDamageTick == this.tickCount) {
			lastDamage += damage;
			this.animationPosition += damage ;
			this.animationPosition = Math.min(this.animationPosition, 60f);
		} else {
			// OUCH :(
			this.animationPosition =  Math.min(damage, 60f);
			lastDamage = damage;
			lastDamageTick = this.tickCount;
		}


		if (!this.level.isClientSide) {
			//custom update packet

			NetworkHandler.sendPacketDamage(this,this.animationPosition);

			// damage numebrssss
			int color = getColorFromDamageSource(source);
			DummyNumberEntity number = new DummyNumberEntity(damage, color, this.mynumberpos++, this.level);
			number.moveTo(this.getX(), this.getY()+1, this.getZ(), 0.0F, 0.0F);
			this.level.addFreshEntity(number);

			this.critical=false;



			this.damageTaken += damage;
			if (firstDamageTick == 0) {
				firstDamageTick = this.tickCount;
			}
		}
		return true;
	}



	@Override
	public void tick() {

		BlockPos onPos = this.getOnPos();

		//check if on stable ground. used for automation
		if (this.level != null && this.level.getGameTime() % 20L == 0L && !this.level.isClientSide) {
			if (level.isEmptyBlock(onPos)) {
				this.dismantle(true);
				return;
			}
		}

		this.setNoGravity(true);
		this.level.getBlockState(onPos).getBlock().stepOn(this.level,onPos,this);


		//used for fire damage, poison damage etc.
		//so you can damage it like any mob

		this.baseTick();


		//living tick stuff. dont remove
		/*
		Vector3d vector3d = this.getMotion();
		double d1 = vector3d.x;
		double d3 = vector3d.y;
		double d5 = vector3d.z;
		if (Math.abs(vector3d.x) < 0.003D) {
			d1 = 0.0D;
		}
		if (Math.abs(vector3d.y) < 0.003D) {
			d3 = 0.0D;
		}
		if (Math.abs(vector3d.z) < 0.003D) {
			d5 = 0.0D;
		}

		this.setMotion(d1, d3, d5);*/

		this.level.getProfiler().push("travel");
		this.travel(new Vec3(this.xxa, this.yya, this.zza));
		this.level.getProfiler().pop();


		this.level.getProfiler().push("push");
		this.pushEntities();
		this.level.getProfiler().pop();
		//end living tick stuff



		if(this.level.isClientSide) {
			//set to 0 to disable red glow that happens when hurt
			this.hurtTime = 0;//this.maxHurtTime;
			this.prevShakeAmount = this.shakeAmount;
			this.prevLimbswing = this.animationPosition;
			//client animation
			if (this.animationPosition > 0) {

				this.shakeAmount++;
				this.animationPosition -= 0.8f;
				if (this.animationPosition <= 0) {
					this.shakeAmount = 0;
					this.animationPosition = 0;
				}
			}

		}
		else {
			this.setSharedFlag(6, this.isGlowing());
			if (!this.glowing) {
				boolean flag = this.hasEffect(MobEffects.GLOWING);
				if (this.getSharedFlag(6) != flag) {
					this.setSharedFlag(6, flag);
				}
			}

			if(this.damageTaken > 0){
				// DPS!
				//&& this.ticksExisted - lastDamageTick >60 for static

				//am i being attacked?

				boolean isdynamic = Configs.DYNAMIC_DPS;
				boolean flag = isdynamic? (this.tickCount == lastDamageTick+1) : (this.tickCount - lastDamageTick) >60;


				//only show damage after second damage tick
				if (flag && firstDamageTick < lastDamageTick) {

					// it's not actual DPS but "damage per tick scaled to seconds".. but meh.
					float seconds = (lastDamageTick - firstDamageTick) / 20f + 1;
					float dps = damageTaken / seconds;
					for (ServerPlayer p : this.currentlyAttacking) {
						if(p.distanceTo(this)<64)
							p.displayClientMessage(new TranslatableComponent("message.dummmmmmy.dps", new DecimalFormat("#.##").format(dps)), true);
					}

				}
				//out of combat. reset variables
				if(this.tickCount - lastDamageTick >60){
					this.currentlyAttacking.clear();
					this.damageTaken = 0;
					this.firstDamageTick = 0;
				}
			}
		}


	}


	@Override
	public void setDeltaMovement(Vec3 motionIn) {}

	@Override
	public void knockback(float strength, double ratioX, double ratioZ) { }

	@Override
	public boolean isPushedByFluid() {
		return false;
	}

	@Override
	public boolean canBreatheUnderwater() {
	  return true;
	}

	@Override
	protected boolean isImmobile() {
		return true;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public boolean isPickable() {
		return super.isPickable();
	}

	@Override
	public boolean causeFallDamage(float l, float d) {
		return false;
	}

	@Override
	protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {}

	@Override
	public void setNoGravity(boolean ignored) {
		super.setNoGravity(true);
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHitIn) {}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return SoundEvents.ARMOR_STAND_HIT;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.ARMOR_STAND_BREAK;
	}



	@Override
	public MobType getMobType() {
		return this.mobType.get();
	}

	public static AttributeSupplier.Builder setCustomAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.FOLLOW_RANGE, 16.0D)
				.add(Attributes.MOVEMENT_SPEED, 0D)
				.add(Attributes.MAX_HEALTH, 20D)
				.add(Attributes.ARMOR, 0D)
				.add(Attributes.ATTACK_DAMAGE, 0D)
				.add(Attributes.FLYING_SPEED, 0D);
	}



	private enum MobAttribute{
		UNDEFINED,
		UNDEAD,
		WATER,
		ILLAGER,
		ARTHROPOD,
		SCARECROW;

		public MobType get(){
			switch (this){
				default:
				case UNDEFINED:
					return MobType.UNDEFINED;
				case UNDEAD:
					return MobType.UNDEAD;
				case WATER:
					return MobType.WATER;
				case ILLAGER:
					return MobType.ILLAGER;
				case ARTHROPOD:
					return MobType.ARTHROPOD;
			}
		}
	}


}