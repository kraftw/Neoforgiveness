package net.kraftw.neoforgiveness;

import net.kraftw.neoforgiveness.datagen.ModRecipeProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@Mod(Neoforgiveness.MODID)
public class Neoforgiveness {
    public static final String MODID = "neoforgiveness";

    public Neoforgiveness(IEventBus modEventBus) {
        modEventBus.addListener(this::onGatherData);
    }

    private void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var packOutput = generator.getPackOutput();
        var lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new ModRecipeProvider(packOutput, lookupProvider));
    }
}
