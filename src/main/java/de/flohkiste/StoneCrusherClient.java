package de.flohkiste;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import de.flohkiste.screen.StoneCrusherScreen;

public class StoneCrusherClient implements ClientModInitializer {
    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient() {
        StoneCrusher.LOGGER.info("Initializing " + StoneCrusher.MOD_ID + " client");
        HandledScreens.register(StoneCrusher.STONE_CRUSHER_SCREEN_HANDLER, StoneCrusherScreen::new);
    }
}