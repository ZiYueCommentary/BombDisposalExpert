package ziyue.bde.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ziyue.bde.BombDisposalExpert;
import ziyue.bde.ModBlocks;

import static ziyue.bde.BombDisposalExpert.CREEPER_IS_NEUTRALIZED;

/**
 * @author ZiYueCommentary
 * @since 2.0.0
 */

@Mixin(Creeper.class)
public abstract class CreeperMixin extends Monster
{
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

    protected CreeperMixin(net.minecraft.world.entity.EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Shadow
    public abstract void setTarget(@Nullable LivingEntity target);

    @Inject(at = @At("HEAD"), method = "tick", cancellable = true)
    private void beforeTick(CallbackInfo ci) {
        if (this.getData(CREEPER_IS_NEUTRALIZED)) {
            super.tick();
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "mobInteract", cancellable = true)
    private void beforeMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (this.getData(CREEPER_IS_NEUTRALIZED)) {
            if (itemStack.is(Items.GUNPOWDER)) {
                itemStack.setCount(itemStack.getCount() - 1);
                this.playSound(SoundEvents.GRASS_PLACE, 1.0F, 1.0F);
                this.setData(CREEPER_IS_NEUTRALIZED, false);
                cir.setReturnValue(InteractionResult.SUCCESS);
                return;
            }
            cir.setReturnValue(InteractionResult.PASS);
            return;
        }
        if (player.getItemInHand(hand).is(ModBlocks.DEFUSER)) {
            this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
            if (this.level() instanceof ServerLevel level) {
                this.spawnAtLocation(level, new ItemStack(Items.GUNPOWDER));
            }
            itemStack.hurtAndBreak(1, player, hand.asEquipmentSlot());
            this.setTarget(null);
            this.setData(CREEPER_IS_NEUTRALIZED, true);
            this.entityData.set(DATA_SWELL_DIR, -1);
            this.entityData.set(DATA_IS_IGNITED, false);
            this.entityData.set(DATA_IS_POWERED, false);
            this.oldSwell = 0;
            this.swell = 0;
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }

    @Inject(at = @At("HEAD"), method = "setTarget", cancellable = true)
    private void beforeSetTarget(LivingEntity target, CallbackInfo ci) {
        if (this.getData(CREEPER_IS_NEUTRALIZED)) {
            if (target instanceof Player) ci.cancel();
        }
    }
}
