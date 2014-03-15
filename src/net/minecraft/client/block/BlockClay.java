package net.minecraft.client.block;

import java.util.Random;
import net.minecraft.client.block.Block;
import net.minecraft.src.Item;
import net.minecraft.src.Item;
import net.minecraft.src.Material;
import net.minecraft.src.Material;

public class BlockClay extends Block {

   public BlockClay(int var1, int var2) {
      super(var1, var2, Material.clay);
   }

   public int idDropped(int var1, Random var2) {
      return Item.clay.shiftedIndex;
   }

   public int quantityDropped(Random var1) {
      return 4;
   }
}
