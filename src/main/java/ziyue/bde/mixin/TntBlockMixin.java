package ziyue.bde.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ziyue.bde.BombDisposalExpert;

/**
 * @author ZiYueCommentary
 * @since 1.0.0
 */

@Mixin(TntBlock.class)
public abstract class TntBlockMixin extends Block
{
    public TntBlockMixin(Properties p_49795_) {
        super(p_49795_);
    }

    @Inject(at = @At("HEAD"), method = "useItemOn", cancellable = true)
    private void beforeOnUse(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (player.getItemInHand(hand).is(BombDisposalExpert.DEFUSER)) {
            level.setBlock(pos, BombDisposalExpert.TNT_NO_GUNPOWDER.get().defaultBlockState(), 11);
            level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS);
            level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, new ItemStack(Items.GUNPOWDER)));
            player.getItemInHand(hand).hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
