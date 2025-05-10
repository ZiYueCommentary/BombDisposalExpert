package ziyue.bde.mixin;

import net.minecraft.client.render.entity.feature.SkinOverlayOwner;
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

/**
 * @author ZiYueCommentary
 * @since 2.0.0
 */

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin extends HostileEntity implements SkinOverlayOwner
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

    protected CreeperEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private static final TrackedData<Boolean> NEUTRALIZED = DataTracker.registerData(CreeperEntityMixin.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Inject(at = @At("TAIL"), method = "initDataTracker")
    private void afterInitDataTracker(CallbackInfo ci) {
        this.dataTracker.startTracking(NEUTRALIZED, false);
    }

    @Inject(at = @At("TAIL"), method = "writeCustomDataToNbt")
    private void afterWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("neutralized", this.dataTracker.get(NEUTRALIZED));
    }

    @Inject(at = @At("TAIL"), method = "readCustomDataFromNbt")
    private void afterReadCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
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
            if (itemStack.getItem() == Items.GUNPOWDER) {
                itemStack.setCount(itemStack.getCount() - 1);
                this.playSound(SoundEvents.BLOCK_GRASS_PLACE, 1.0F, 1.0F);
                this.dataTracker.set(NEUTRALIZED, false);
                cir.setReturnValue(ActionResult.SUCCESS);
                return;
            }
            cir.setReturnValue(ActionResult.PASS);
            return;
        }
        if (itemStack.getItem() == Items.SHEARS) {
            this.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0F, 1.0F);
            this.dropStack(new ItemStack(Items.GUNPOWDER));
            itemStack.damage(1, player, p -> p.sendToolBreakStatus(hand));
            this.setTarget(null);
            this.dataTracker.set(NEUTRALIZED, true);
            this.dataTracker.set(FUSE_SPEED, -1);
            this.dataTracker.set(IGNITED, false);
            this.lastFuseTime = this.currentFuseTime = 0;
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }

    @Override
    public @Nullable LivingEntity getTarget() {
        if (this.dataTracker.get(NEUTRALIZED)) {
            if (super.getTarget() instanceof PlayerEntity) return null;
        }
        return super.getTarget();
    }
}
