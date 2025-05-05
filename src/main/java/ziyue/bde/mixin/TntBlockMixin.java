package ziyue.bde.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TntBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
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

@Mixin(TntBlock.class)
public abstract class TntBlockMixin extends Block
{
	public TntBlockMixin(Settings settings) {
		super(settings);
	}

	@Inject(at = @At("HEAD"), method = "onUse", cancellable = true)
	private void beforeOnUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
		if (player.isHolding(Items.SHEARS)) {
			world.setBlockState(pos, BombDisposalExpert.TNT_NO_GUNPOWDER.getDefaultState());
			world.playSound(null, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS);
			world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, new ItemStack(Items.GUNPOWDER)));
			player.getStackInHand(hand).damage(1, player, p -> p.sendToolBreakStatus(hand));
			cir.setReturnValue(ActionResult.SUCCESS);
		}
	}
}