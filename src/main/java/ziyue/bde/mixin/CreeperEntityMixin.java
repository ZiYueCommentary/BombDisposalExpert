package ziyue.bde.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ziyue.bde.BombDisposalExpert;

/**
 * @author ZiYueCommentary
 * @since 2.0.0
 */

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin extends HostileEntity
{
    @Shadow
    @Final
    private static TrackedData<Integer> FUSE_SPEED;

    @Shadow
    @Final
    private static TrackedData<Boolean> IGNITED;

    @Shadow
    private int lastFuseTime;

    @Shadow
    private int currentFuseTime;

    @Shadow
    public abstract void setTarget(@Nullable LivingEntity target);

    protected CreeperEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private static final TrackedData<Boolean> NEUTRALIZED = DataTracker.registerData(CreeperEntityMixin.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Inject(at = @At("TAIL"), method = "initDataTracker")
    private void afterInitDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(NEUTRALIZED, false);
    }

    @Inject(at = @At("TAIL"), method = "writeCustomDataToNbt")
    private void afterWriteCustomData(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("neutralized", this.dataTracker.get(NEUTRALIZED));
    }

    @Inject(at = @At("TAIL"), method = "readCustomDataFromNbt")
    private void afterReadCustomData(NbtCompound nbt, CallbackInfo ci) {
        this.dataTracker.set(NEUTRALIZED, nbt.getBoolean("neutralized"));
    }

    @Inject(at = @At("HEAD"), method = "tick", cancellable = true)
    private void beforeTick(CallbackInfo ci) {
        if (this.dataTracker.get(NEUTRALIZED)) {
            super.tick();
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "interactMob", cancellable = true)
    private void beforeInteractMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (this.dataTracker.get(NEUTRALIZED)) {
            if (itemStack.isOf(Items.GUNPOWDER)) {
                itemStack.setCount(itemStack.getCount() - 1);
                this.playSound(SoundEvents.BLOCK_GRASS_PLACE, 1.0F, 1.0F);
                this.dataTracker.set(NEUTRALIZED, false);
                cir.setReturnValue(ActionResult.SUCCESS);
                return;
            }
            cir.setReturnValue(ActionResult.PASS);
            return;
        }
        if (player.getStackInHand(hand).isIn(BombDisposalExpert.DEFUSER)) {
            this.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0F, 1.0F);
            if (this.getWorld() instanceof ServerWorld world) {
                this.dropStack(world, new ItemStack(Items.GUNPOWDER));
            }
            itemStack.damage(1, player, getSlotForHand(hand));
            this.setTarget(null);
            this.dataTracker.set(NEUTRALIZED, true);
            this.dataTracker.set(FUSE_SPEED, -1);
            this.dataTracker.set(IGNITED, false);
            this.lastFuseTime = 0;
            this.currentFuseTime = 0;
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }

    @Inject(at = @At("HEAD"), method = "setTarget", cancellable = true)
    private void beforeSetTarget(LivingEntity target, CallbackInfo ci) {
        if (this.dataTracker.get(NEUTRALIZED)) {
            if (target instanceof PlayerEntity) ci.cancel();
        }
    }
}
