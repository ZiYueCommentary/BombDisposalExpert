package ziyue.bde.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.IChargeableMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

/**
 * @author ZiYueCommentary
 * @since 2.0.0
 */

@Mixin(CreeperEntity.class)
public abstract class CreeperMixin extends MonsterEntity implements IChargeableMob
{
    protected CreeperMixin(EntityType<? extends MonsterEntity> p_i48553_1_, World p_i48553_2_) {
        super(p_i48553_1_, p_i48553_2_);
    }

    @Shadow @Final private static DataParameter<Integer> DATA_SWELL_DIR;

    @Shadow @Final private static DataParameter<Boolean> DATA_IS_POWERED;

    @Shadow @Final private static DataParameter<Boolean> DATA_IS_IGNITED;

    @Shadow private int oldSwell;

    @Shadow private int swell;

    @Unique
    private static final DataParameter<Boolean> DATA_NEUTRALIZED = EntityDataManager.defineId(CreeperMixin.class, DataSerializers.BOOLEAN);

    @Inject(at = @At("TAIL"), method = "defineSynchedData")
    private void afterDefineSynchedData(CallbackInfo ci) {
        this.entityData.define(DATA_NEUTRALIZED, false);
    }

    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData")
    private void afterAddAdditionalSaveData(CompoundNBT tag, CallbackInfo ci) {
        tag.putBoolean("neutralized", this.entityData.get(DATA_NEUTRALIZED));
    }

    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData")
    private void afterReadAdditionalSaveData(CompoundNBT tag, CallbackInfo ci) {
        this.entityData.set(DATA_NEUTRALIZED, tag.getBoolean("neutralized"));
    }

    @Inject(at = @At("HEAD"), method = "tick", cancellable = true)
    private void beforeTick(CallbackInfo ci) {
        if (this.entityData.get(DATA_NEUTRALIZED)) {
            super.tick();
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "mobInteract", cancellable = true)
    private void beforeMobInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResultType> cir) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (this.entityData.get(DATA_NEUTRALIZED)) {
            if (itemstack.getItem() == Items.GUNPOWDER) {
                itemstack.setCount(itemstack.getCount() - 1);
                this.playSound(SoundEvents.GRASS_PLACE, 1F, 1F);
                this.entityData.set(DATA_NEUTRALIZED, false);
                cir.setReturnValue(ActionResultType.SUCCESS);
                return;
            }
            cir.setReturnValue(ActionResultType.PASS);
            return;
        }
        if (player.isHolding(Items.SHEARS)) {
            this.playSound(SoundEvents.SHEEP_SHEAR, 1F, 1F);
            this.spawnAtLocation(new ItemStack(Items.GUNPOWDER));
            itemstack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            this.setTarget(null);
            this.entityData.set(DATA_NEUTRALIZED, true);
            this.entityData.set(DATA_SWELL_DIR, -1);
            this.entityData.set(DATA_IS_POWERED, false);
            this.entityData.set(DATA_IS_IGNITED, false);
            this.oldSwell = this.swell = 0;
            cir.setReturnValue(ActionResultType.SUCCESS);
        }
    }

    @Nullable
    @Override
    public LivingEntity getTarget() {
        if (this.entityData.get(DATA_NEUTRALIZED)) {
            if (super.getTarget() instanceof PlayerEntity) return null;
        }
        return super.getTarget();
    }
}
