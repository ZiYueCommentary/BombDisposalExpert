package ziyue.bde.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import ziyue.bde.BombDisposalExpert;

/**
 * @author ZiYueCommentary
 * @since 1.0.0
 */

@Mixin(TNTEntity.class)
public abstract class TNTEntityMixin extends Entity
{
    public TNTEntityMixin(EntityType<?> p_i48580_1_, World p_i48580_2_) {
        super(p_i48580_1_, p_i48580_2_);
    }

    // No @Inject required.
    @Override
    public ActionResultType interact(PlayerEntity player, Hand hand) {
        if (player.isHolding(Items.SHEARS)) {
            player.getItemInHand(hand).hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            this.spawnAtLocation(new ItemStack(BombDisposalExpert.TNT_NO_GUNPOWDER.get()));
            this.kill();
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }
}
