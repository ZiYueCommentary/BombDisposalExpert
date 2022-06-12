package ziyue.bde.mappings;

import me.shedaniel.architectury.platform.forge.EventBuses;
import net.minecraftforge.eventbus.api.IEventBus;

public class ForgeUtilities
{
	public static void registerModEventBus(String modId, IEventBus eventBus) {
		EventBuses.registerModEventBus(modId, eventBus);
	}
}
