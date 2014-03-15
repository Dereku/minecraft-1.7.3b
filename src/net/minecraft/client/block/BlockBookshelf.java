package net.minecraft.client.block;

import java.util.Random;
import net.minecraft.client.block.Block;
import net.minecraft.src.Material;
import net.minecraft.src.Material;

public class BlockBookshelf extends Block {

   public BlockBookshelf(int var1, int var2) {
      super(var1, var2, Material.wood);
   }

   public int getBlockTextureFromSide(int var1) {
      return var1 <= 1?4:this.blockIndexInTexture;
   }

   public int quantityDropped(Random var1) {
      return 0;
   }
}
