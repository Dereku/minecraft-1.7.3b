package net.minecraft.client.item;

import net.minecraft.client.block.Block;
import net.minecraft.client.block.BlockStep;
import net.minecraft.client.item.ItemBlock;
import net.minecraft.client.item.ItemStack;

public class ItemSlab extends ItemBlock {

   public ItemSlab(int var1) {
      super(var1);
      this.setMaxDamage(0);
      this.setHasSubtypes(true);
   }

   public int getIconFromDamage(int var1) {
      return Block.stairSingle.getBlockTextureFromSideAndMetadata(2, var1);
   }

   public int getPlacedBlockMetadata(int var1) {
      return var1;
   }

   public String getItemNameIS(ItemStack var1) {
      return super.getItemName() + "." + BlockStep.field_22037_a[var1.getItemDamage()];
   }
}
