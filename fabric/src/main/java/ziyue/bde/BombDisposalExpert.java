package ziyue.bde;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BombDisposalExpert implements ModInitializer
{
    public static final String MOD_ID = "bde";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModBlocks.initialize();
        LOGGER.info("Bomb Disposal Expert! Made by ZiYueCommentary.");
    }
}
