package net.minecraft.item;

import net.minecraft.client.block.Block;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.item.ItemTool;

public class ItemAxe extends ItemTool {

   private static Block[] blocksEffectiveAgainst = new Block[]{Block.planks, Block.bookShelf, Block.wood, Block.chest};


   protected ItemAxe(int var1, EnumToolMaterial var2) {
      super(var1, 3, var2, blocksEffectiveAgainst);
   }

}