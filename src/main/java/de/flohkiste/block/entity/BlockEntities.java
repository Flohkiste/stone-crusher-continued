package de.flohkiste.block.entity;

import de.flohkiste.StoneCrusher;
import de.flohkiste.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BlockEntities {
    public static final BlockEntityType<StoneCrusherBlockEntity> STONE_CRUSHER_BLOCK_ENTITY = register("stone_crusher_block_entity", BlockEntityType.Builder.create(StoneCrusherBlockEntity::new, ModBlocks.STONE_CRUSHER).build(null));

    public static void registerModBlocks() {
        StoneCrusher.LOGGER.info("Registering Mod Block Entities for " + StoneCrusher.MOD_ID);
    }

    public static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> type) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(StoneCrusher.MOD_ID, name), type);
    }
}