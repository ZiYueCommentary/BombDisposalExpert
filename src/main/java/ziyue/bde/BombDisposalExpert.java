package ziyue.bde;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

@Mod(modid = BombDisposalExpert.MOD_ID)
public class BombDisposalExpert
{
    public static final String MOD_ID = "bde";

    public static final Logger LOGGER = LogManager.getLogger();

    public static final Block TNT_NO_GUNPOWDER = new Block(Material.TNT, MapColor.RED) {
        @Override
        public boolean isFlammable(IBlockAccess p_isFlammable_1_, BlockPos p_isFlammable_2_, EnumFacing p_isFlammable_3_) {
            return true;
        }

        @Override
        public int getFlammability(IBlockAccess p_getFlammability_1_, BlockPos p_getFlammability_2_, EnumFacing p_getFlammability_3_) {
            return 10;
        }

        @Override
        public int getFireSpreadSpeed(IBlockAccess p_getFireSpreadSpeed_1_, BlockPos p_getFireSpreadSpeed_2_, EnumFacing p_getFireSpreadSpeed_3_) {
            return 5;
        }

        @Override
        public SoundType getSoundType(IBlockState p_getSoundType_1_, World p_getSoundType_2_, BlockPos p_getSoundType_3_, @Nullable Entity p_getSoundType_4_) {
            return SoundType.PLANT;
        }
    }.setCreativeTab(CreativeTabs.REDSTONE).setRegistryName("tnt_no_gunpowder").setTranslationKey("tnt_no_gunpowder");
    public static final Item TNT_NO_GUNPOWDER_ITEM = new ItemBlock(TNT_NO_GUNPOWDER).setRegistryName("tnt_no_gunpowder");

    @EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("Bomb Disposal Expert! Made by ZiYueCommentary.");
    }
}
