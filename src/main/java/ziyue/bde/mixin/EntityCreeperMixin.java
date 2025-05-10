package ziyue.bde.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(EntityCreeper.class)
public abstract class EntityCreeperMixin extends EntityMob
{
    @Shadow
    @Final
    private static DataParameter<Boolean> IGNITED;

    @Shadow
    private int lastActiveTime;

    @Shadow
    private int timeSinceIgnited;

    @Shadow
    private int fuseTime;

    @Shadow
    @Final
    private static DataParameter<Integer> STATE;

    public EntityCreeperMixin(World p_i1738_1_) {
        super(p_i1738_1_);
    }

    @Unique
    private static final DataParameter<Boolean> NEUTRALIZED = EntityDataManager.createKey(EntityCreeperMixin.class, DataSerializers.BOOLEAN);

    @Inject(at = @At("TAIL"), method = "entityInit")
    private void afterEntityInit(CallbackInfo ci) {
        this.dataManager.register(NEUTRALIZED, false);
    }

    @Inject(at = @At("TAIL"), method = "writeEntityToNBT")
    private void afterWriteEntityToNBT(NBTTagCompound compound, CallbackInfo ci) {
        compound.setBoolean("neutralized", this.dataManager.get(NEUTRALIZED));
    }

    @Inject(at = @At("TAIL"), method = "readEntityFromNBT")
    private void afterReadEntityToNBT(NBTTagCompound compound, CallbackInfo ci) {
        this.dataManager.set(NEUTRALIZED, compound.getBoolean("neutralized"));
    }

    @Inject(at = @At("HEAD"), method = "processInteract", cancellable = true)
    private void beforeProcessInteract(EntityPlayer player, EnumHand hand, CallbackInfoReturnable<Boolean> cir) {
        ItemStack itemStack = player.getHeldItem(hand);
        if (this.dataManager.get(NEUTRALIZED)) {
            if (itemStack.getItem() == Items.GUNPOWDER) {
                itemStack.setCount(itemStack.getCount() - 1);
                this.playSound(SoundEvents.BLOCK_GRASS_PLACE, 1F, 1F);
                this.dataManager.set(NEUTRALIZED, false);
                cir.setReturnValue(true);
                return;
            }
            cir.setReturnValue(false);
            return;
        }
        if (itemStack.getItem() == Items.SHEARS) {
            this.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1F, 1F);
            itemStack.damageItem(1, player);
            if (!world.isRemote) {
                this.dropItem(Items.GUNPOWDER, 1);
            }
            this.dataManager.set(NEUTRALIZED, true);
            this.dataManager.set(IGNITED, false);
            this.dataManager.set(STATE, -1);
            this.lastActiveTime = this.timeSinceIgnited = 0;
            this.fuseTime = 30;
            cir.setReturnValue(true);
        }
    }

    @Nullable
    @Override
    public EntityLivingBase getAttackTarget() {
        if (this.dataManager.get(NEUTRALIZED) && super.getAttackTarget() instanceof EntityPlayer) {
            return null;
        } else {
            return super.getAttackTarget();
        }
    }
}
