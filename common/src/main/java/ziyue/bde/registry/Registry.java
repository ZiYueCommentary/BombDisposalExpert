package ziyue.bde.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class Registry
{
    @ExpectPlatform
    public static boolean isFabric() {
        throw new AssertionError();
    }
}
