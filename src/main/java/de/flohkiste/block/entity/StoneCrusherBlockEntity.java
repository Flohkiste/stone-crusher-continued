package de.flohkiste.block.entity;

import de.flohkiste.StoneCrusher;
import de.flohkiste.block.custom.StoneCrusherBlock;
import de.flohkiste.screen.StoneCrusherScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.IntStream;

public class StoneCrusherBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory, SidedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
    public static final int INPUT_SLOT = 0;
    public static final int FUEL_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;
    int burnTime = 0;
    int fuelTime = 0;
    int crushTime = 0;
    int crushTimeTotal = 100;
    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> StoneCrusherBlockEntity.this.burnTime;
                case 1 -> StoneCrusherBlockEntity.this.fuelTime;
                case 2 -> StoneCrusherBlockEntity.this.crushTime;
                case 3 -> StoneCrusherBlockEntity.this.crushTimeTotal;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> StoneCrusherBlockEntity.this.burnTime = value;
                case 1 -> StoneCrusherBlockEntity.this.fuelTime = value;
                case 2 -> StoneCrusherBlockEntity.this.crushTime = value;
                case 3 -> StoneCrusherBlockEntity.this.crushTimeTotal = value;
            }

        }

        @Override
        public int size() {
            return 4;
        }
    };

    public StoneCrusherBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.STONE_CRUSHER_BLOCK_ENTITY, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.inventory, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, this.inventory, registryLookup);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Text getDisplayName() {
        return Text.of("Stone Crusher");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new StoneCrusherScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    public static void tick(World world, BlockPos pos, BlockState state, StoneCrusherBlockEntity blockEntity) {

        /*ItemStack inputItems = blockEntity.inventory.get(INPUT_SLOT);
        if (!inputItems.isEmpty()) {
            blockEntity.crushTime ++;
            if (blockEntity.crushTime >= blockEntity.crushTimeTotal) {
                blockEntity.crushTime = 0;
                inputItems.decrement(1);
                ItemStack outputItems = blockEntity.inventory.get(OUTPUT_SLOT);

                if (outputItems.isEmpty()) {
                   outputItems = blockEntity.inventory.set(OUTPUT_SLOT, new ItemStack(Items.SAND, 1));
                } else {
                    outputItems.increment(1);
                }

                blockEntity.inventory.set(OUTPUT_SLOT, outputItems);

            }
        }*/
        boolean isDirty = false;
        boolean isBurning = blockEntity.isBurning();

        if (blockEntity.isBurning()) {
            -- blockEntity.burnTime;
        }

        ItemStack inputItems = blockEntity.inventory.get(INPUT_SLOT);
        ItemStack fuelItems = blockEntity.inventory.get(FUEL_SLOT);
        ItemStack outputItems = blockEntity.inventory.get(OUTPUT_SLOT);

        boolean isInputEmpty = inputItems.isEmpty();
        boolean isFuelEmpty = fuelItems.isEmpty();


        if (blockEntity.isBurning() || !isInputEmpty && !isFuelEmpty) {
            if (!blockEntity.isBurning() && isValidRecipe(inputItems, outputItems)) {
                isDirty = true;
                blockEntity.burnFuel();
            }

            if (blockEntity.isBurning() && isValidRecipe(inputItems, outputItems)) {
                ++ blockEntity.crushTime;
                if (blockEntity.crushTime >= blockEntity.crushTimeTotal) {
                    blockEntity.crush();
                    isDirty = true;
                }
            } else {
                blockEntity.crushTime = 0;
            }
        } else if(!blockEntity.isBurning() && blockEntity.crushTime > 0) {
            blockEntity.crushTime = MathHelper.clamp(blockEntity.crushTime - 2, 0, blockEntity.crushTimeTotal);
        }

        if (isBurning != blockEntity.isBurning()) {
            isDirty = true;
            state = (BlockState) state.with(StoneCrusherBlock.LIT, blockEntity.isBurning());
            world.setBlockState(pos, state, 3);
        }

        if (isDirty) {
            blockEntity.markDirty();
        }

    }

    private void crush() {
        crushTime = 0;
        ItemStack inputItems = inventory.get(INPUT_SLOT);
        ItemStack outputItems = inventory.get(OUTPUT_SLOT);

        if (outputItems.isEmpty()) {
            outputItems = new ItemStack(getRecipeOutput(inputItems), 1);
        } else {
            outputItems.increment(1);
        }

        inputItems.decrement(1);

        inventory.set(INPUT_SLOT, inputItems);
        inventory.set(OUTPUT_SLOT, outputItems);
    }

    private void burnFuel() {
        ItemStack fuelItems = inventory.get(FUEL_SLOT);
        this.fuelTime = getFuelTime(fuelItems);
        this.burnTime = this.fuelTime;
        fuelItems.decrement(1);
        inventory.set(FUEL_SLOT, fuelItems);
    }

    private static boolean isValidRecipe(ItemStack inputItems, ItemStack outputItems) {
        return canAcceptRecipeOutput(inputItems, outputItems) && getRecipeOutput(inputItems) != Items.AIR;
    }

    private static boolean canAcceptRecipeOutput(ItemStack inputItems, ItemStack outputItems) {
        if (outputItems.isEmpty()) {
            return true;
        }

        if (outputItems.getCount() + 1 > outputItems.getMaxCount()){
            return false;
        }

        if (getRecipeOutput(inputItems) != outputItems.getItem()) {
            return false;
        }

        return true;
    }

    public static Item getRecipeOutput(ItemStack inputItems) {
        Item inputItem = inputItems.getItem();
        if (inputItem == Items.STONE) {
            return Items.COBBLESTONE;
        } else if (inputItem == Items.COBBLESTONE) {
            return Items.GRAVEL;
        } else if (inputItem == Items.GRAVEL) {
            return Items.SAND;
        } else {
            return Items.AIR;
        }
    }

    private int getFuelTime(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        } else {
            Item item = fuel.getItem();
            return (Integer)AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(item, 0);
        }
    }

    private boolean isBurning() {
        return this.burnTime > 0;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return IntStream.range(0, getItems().size()).toArray();
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if (dir == Direction.UP) {
            return slot == INPUT_SLOT;
        } else {
            return slot == FUEL_SLOT && getFuelTime(stack) != 0;
        }


        //return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (slot == OUTPUT_SLOT) {
            return true;
        }
        return false;
    }
}
