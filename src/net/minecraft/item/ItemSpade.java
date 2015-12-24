package net.minecraft.item;

import net.minecraft.client.block.Block;
import net.minecraft.src.EnumToolMaterial;

public class ItemSpade extends ItemTool {

    private static final Block[] blocksEffectiveAgainst = new Block[]{Block.grass, Block.dirt, Block.sand, Block.gravel, Block.snow, Block.blockSnow, Block.blockClay, Block.tilledField};

    public ItemSpade(int var1, EnumToolMaterial var2) {
        super(var1, 1, var2, blocksEffectiveAgainst);
    }

    @Override
    public boolean canHarvestBlock(Block var1) {
        return var1 == Block.snow ? true : var1 == Block.blockSnow;
    }

}
