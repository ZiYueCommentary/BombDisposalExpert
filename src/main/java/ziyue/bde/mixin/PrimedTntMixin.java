package ziyue.bde.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import ziyue.bde.BombDisposalExpert;

/**
 * @author ZiYueCommentary
 * @since 1.0.0
 */

@Mixin(PrimedTnt.class)
public abstract class PrimedTntMixin extends Entity implements TraceableEntity
{
    public PrimedTntMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    // No @Inject required.
    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.getItemInHand(hand).is(BombDisposalExpert.DEFUSER)) {
            player.getItemInHand(hand).hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            if (this.level() instanceof ServerLevel serverLevel) {
                this.spawnAtLocation(serverLevel, new ItemStack(BombDisposalExpert.TNT_NO_GUNPOWDER.get()));
                this.kill(serverLevel);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
