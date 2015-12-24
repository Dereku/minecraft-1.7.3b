package net.minecraft.item;

import net.minecraft.item.ItemBlock;

public class ItemPiston extends ItemBlock {

   public ItemPiston(int var1) {
      super(var1);
   }

   public int getPlacedBlockMetadata(int var1) {
      return 7;
   }
}
