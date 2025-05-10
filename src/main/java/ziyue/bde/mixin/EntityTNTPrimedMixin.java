package ziyue.bde.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import ziyue.bde.BombDisposalExpert;

@Mixin(EntityTNTPrimed.class)
public abstract class EntityTNTPrimedMixin extends Entity
{
    public EntityTNTPrimedMixin(World p_i1582_1_) {
        super(p_i1582_1_);
    }

    @Override
    public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d p_184199_2_, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        if (itemStack.getItem() == Items.SHEARS) {
            itemStack.damageItem(1, player);
            if (!world.isRemote) {
                this.dropItem(BombDisposalExpert.TNT_NO_GUNPOWDER_ITEM, 1);
                this.onKillCommand();
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }
}
