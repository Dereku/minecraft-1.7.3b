package net.minecraft.src;

import java.util.Random;
import net.minecraft.src.Block;
import net.minecraft.src.Material;

public class BlockStone extends Block {

   public BlockStone(int var1, int var2) {
      super(var1, var2, Material.rock);
   }

   public int idDropped(int var1, Random var2) {
      return Block.cobblestone.blockID;
   }
}
