package net.minecraft.client.block;

import java.util.Random;
import net.minecraft.client.block.BlockSand;
import net.minecraft.item.Item;
import net.minecraft.item.Item;

public class BlockGravel extends BlockSand {

   public BlockGravel(int var1, int var2) {
      super(var1, var2);
   }

   public int idDropped(int var1, Random var2) {
      return var2.nextInt(10) == 0?Item.flint.shiftedIndex:this.blockID;
   }
}
