package ziyue.bde.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import ziyue.bde.Main;

@Mixin(PrimedTnt.class)
public abstract class PrimedTntMixin extends Entity
{
    public PrimedTntMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand interactionHand) {
        if(player.isHolding(Items.SHEARS)) {
            this.kill();
            this.spawnAtLocation(Main.TNT.get().asItem());
            return InteractionResult.SUCCESS;
        }else return InteractionResult.PASS;
    }
}
