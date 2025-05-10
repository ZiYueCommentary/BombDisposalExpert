package ziyue.bde.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TNTBlock;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ziyue.bde.BombDisposalExpert;

/**
 * @author ZiYueCommentary
 * @since 1.0.0
 */

@Mixin(TNTBlock.class)
public abstract class TNTBlockMixin extends Block
{
    public TNTBlockMixin(Properties p_49795_) {
        super(p_49795_);
    }

    @Inject(at = @At("HEAD"), method = "use", cancellable = true)
    private void beforeOnUse(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult p_225533_6_, CallbackInfoReturnable<ActionResultType> cir) {
        if (player.isHolding(Items.SHEARS)) {
            level.setBlock(pos, BombDisposalExpert.TNT_NO_GUNPOWDER.get().defaultBlockState(), 11);
            level.playSound(player, pos, SoundEvents.SHEEP_SHEAR, SoundCategory.BLOCKS, 1F, 1F);
            level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, new ItemStack(Items.GUNPOWDER)));
            player.getItemInHand(hand).hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            cir.setReturnValue(ActionResultType.SUCCESS);
        }
    }
}
