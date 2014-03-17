package net.minecraft.client.item;

import net.minecraft.client.block.Block;
import net.minecraft.src.ColorizerFoliage;
import net.minecraft.src.ColorizerFoliage;
import net.minecraft.client.item.ItemBlock;

public class ItemLeaves extends ItemBlock {

   public ItemLeaves(int var1) {
      super(var1);
      this.setMaxDamage(0);
      this.setHasSubtypes(true);
   }

   public int getPlacedBlockMetadata(int var1) {
      return var1 | 8;
   }

   public int getIconFromDamage(int var1) {
      return Block.leaves.getBlockTextureFromSideAndMetadata(0, var1);
   }

   public int getColorFromDamage(int var1) {
      return (var1 & 1) == 1?ColorizerFoliage.getFoliageColorPine():((var1 & 2) == 2?ColorizerFoliage.getFoliageColorBirch():ColorizerFoliage.func_31073_c());
   }
}
