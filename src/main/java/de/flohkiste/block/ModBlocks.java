package de.flohkiste.block;

import de.flohkiste.StoneCrusher;
import de.flohkiste.block.custom.StoneCrusherBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block STONE_CRUSHER = registerBlock("stone_crusher_block", new StoneCrusherBlock(Block.Settings.create().strength(4f)
            .requiresTool().sounds(BlockSoundGroup.DEEPSLATE)));

    public static void registerModBlocks() {
        StoneCrusher.LOGGER.info("Registering Mod Blocks for " + StoneCrusher.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(fabricItemGroupEntries -> {
            fabricItemGroupEntries.add(STONE_CRUSHER);
        });
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(StoneCrusher.MOD_ID, name), new BlockItem(block, new Item.Settings()));
    }

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(StoneCrusher.MOD_ID, name), block);
    }
}
