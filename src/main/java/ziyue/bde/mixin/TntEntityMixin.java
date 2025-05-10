package ziyue.bde.mixin;

import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import ziyue.bde.BombDisposalExpert;

import static net.minecraft.entity.LivingEntity.getSlotForHand;

/**
 * @author ZiYueCommentary
 * @since 1.0.0
 */

@Mixin(TntEntity.class)
public abstract class TntEntityMixin extends Entity implements Ownable
{
    public TntEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    // In fact, we don't need @Inject here.
    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (player.isHolding(Items.SHEARS)) {
            player.getStackInHand(hand).damage(1, player, getSlotForHand(hand));
            this.dropStack(new ItemStack(BombDisposalExpert.TNT_NO_GUNPOWDER));
            this.kill();
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}
