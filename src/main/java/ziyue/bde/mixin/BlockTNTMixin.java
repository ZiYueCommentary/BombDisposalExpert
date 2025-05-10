package ziyue.bde.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ziyue.bde.BombDisposalExpert;

@Mixin(BlockTNT.class)
public abstract class BlockTNTMixin extends Block
{
    public BlockTNTMixin(Material p_i46399_1_, MapColor p_i46399_2_) {
        super(p_i46399_1_, p_i46399_2_);
    }

    @Inject(at = @At("HEAD"), method = "onBlockActivated", cancellable = true)
    public void beforeOnBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing p_180639_6_, float p_180639_7_, float p_180639_8_, float p_180639_9_, CallbackInfoReturnable<Boolean> cir) {
        ItemStack itemStack = player.getHeldItem(hand);
        if (itemStack.getItem() == Items.SHEARS) {
            world.playSound(player, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1F, 1F);
            itemStack.damageItem(1, player);
            if (!world.isRemote) {
                world.setBlockState(pos, BombDisposalExpert.TNT_NO_GUNPOWDER.getDefaultState());
                world.spawnEntity(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, new ItemStack(Items.GUNPOWDER)));
            }
            cir.setReturnValue(true);
        }
    }
}
