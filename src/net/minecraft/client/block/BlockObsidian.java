package net.minecraft.client.block;

import java.util.Random;
import net.minecraft.client.block.Block;
import net.minecraft.client.block.BlockStone;

public class BlockObsidian extends BlockStone {

   public BlockObsidian(int var1, int var2) {
      super(var1, var2);
   }

   public int quantityDropped(Random var1) {
      return 1;
   }

   public int idDropped(int var1, Random var2) {
      return Block.obsidian.blockID;
   }
}
