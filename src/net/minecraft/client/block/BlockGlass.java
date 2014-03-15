package net.minecraft.client.block;

import java.util.Random;
import net.minecraft.client.block.BlockBreakable;
import net.minecraft.src.Material;
import net.minecraft.src.Material;

public class BlockGlass extends BlockBreakable {

   public BlockGlass(int var1, int var2, Material var3, boolean var4) {
      super(var1, var2, var3, var4);
   }

   public int quantityDropped(Random var1) {
      return 0;
   }

   public int getRenderBlockPass() {
      return 0;
   }
}
