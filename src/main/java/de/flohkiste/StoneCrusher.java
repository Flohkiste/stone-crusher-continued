package de.flohkiste;

import de.flohkiste.block.ModBlocks;
import de.flohkiste.block.entity.BlockEntities;
import de.flohkiste.screen.StoneCrusherScreenHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoneCrusher implements ModInitializer {
	public static final String MOD_ID = "stonecrusher";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final ScreenHandlerType<StoneCrusherScreenHandler> STONE_CRUSHER_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, Identifier.of(MOD_ID, "stone_crusher"), new ScreenHandlerType<>(StoneCrusherScreenHandler::new, FeatureSet.empty()));

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing " + MOD_ID);
		ModBlocks.registerModBlocks();
		BlockEntities.registerModBlocks();
	}
}