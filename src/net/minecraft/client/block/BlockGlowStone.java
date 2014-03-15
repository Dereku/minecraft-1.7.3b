package net.minecraft.client.block;

import java.util.Random;
import net.minecraft.client.block.Block;
import net.minecraft.src.Item;
import net.minecraft.src.Item;
import net.minecraft.src.Material;
import net.minecraft.src.Material;

public class BlockGlowStone extends Block {

   public BlockGlowStone(int var1, int var2, Material var3) {
      super(var1, var2, var3);
   }

   public int quantityDropped(Random var1) {
      return 2 + var1.nextInt(3);
   }

   public int idDropped(int var1, Random var2) {
      return Item.lightStoneDust.shiftedIndex;
   }
}
