package ziyue.bde.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author ZiYueCommentary
 * @since 2.0.0
 */

@Mixin(Creeper.class)
public abstract class CreeperMixin extends Monster
{
    @Shadow
    public abstract void setTarget(@Nullable LivingEntity p_149691_);

    @Shadow
    @Final
    private static EntityDataAccessor<Integer> DATA_SWELL_DIR;

    @Shadow
    @Final
    private static EntityDataAccessor<Boolean> DATA_IS_POWERED;

    @Shadow
    @Final
    private static EntityDataAccessor<Boolean> DATA_IS_IGNITED;

    @Shadow
    private int oldSwell;

    @Shadow
    private int swell;

    protected CreeperMixin(EntityType<? extends Monster> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }

    @Unique
    private static final EntityDataAccessor<Boolean> DATA_NEUTRALIZED = SynchedEntityData.defineId(CreeperMixin.class, EntityDataSerializers.BOOLEAN);

    @Inject(at = @At("TAIL"), method = "defineSynchedData")
    private void afterDefineSynchedData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(DATA_NEUTRALIZED, false);
    }

    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData")
    private void afterAddAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        tag.putBoolean("neutralized", this.entityData.get(DATA_NEUTRALIZED));
    }

    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData")
    private void afterReadAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        this.entityData.set(DATA_NEUTRALIZED, tag.getBoolean("neutralized").orElse(false));
    }

    @Inject(at = @At("HEAD"), method = "tick", cancellable = true)
    private void beforeTick(CallbackInfo ci) {
        if (this.entityData.get(DATA_NEUTRALIZED)) {
            super.tick();
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "mobInteract", cancellable = true)
    private void beforeMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (this.entityData.get(DATA_NEUTRALIZED)) {
            if (itemstack.is(Items.GUNPOWDER)) {
                itemstack.setCount(itemstack.getCount() - 1);
                this.playSound(SoundEvents.GRASS_PLACE);
                this.entityData.set(DATA_NEUTRALIZED, false);
                cir.setReturnValue(InteractionResult.SUCCESS);
                return;
            }
            cir.setReturnValue(InteractionResult.PASS);
            return;
        }
        if (player.isHolding(Items.SHEARS)) {
            this.playSound(SoundEvents.SHEEP_SHEAR);
            if (!this.level().isClientSide()) {
                this.spawnAtLocation((ServerLevel) this.level(), new ItemStack(Items.GUNPOWDER));
            }
            itemstack.hurtAndBreak(1, player, getSlotForHand(hand));
            this.setTarget(null);
            this.entityData.set(DATA_NEUTRALIZED, true);
            this.entityData.set(DATA_SWELL_DIR, -1);
            this.entityData.set(DATA_IS_POWERED, false);
            this.entityData.set(DATA_IS_IGNITED, false);
            this.oldSwell = this.swell = 0;
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }

    @Inject(at = @At("HEAD"), method = "setTarget", cancellable = true)
    private void beforeSetTarget(LivingEntity entity, CallbackInfo ci) {
        if (this.entityData.get(DATA_NEUTRALIZED)) {
            if (entity instanceof Player) ci.cancel();
        }
    }
}
