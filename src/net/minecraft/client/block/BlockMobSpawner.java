package net.minecraft.client.block;

import java.util.Random;
import net.minecraft.client.block.BlockContainer;
import net.minecraft.src.Material;
import net.minecraft.src.Material;
import net.minecraft.world.tiles.TileEntity;
import net.minecraft.world.tiles.TileEntity;
import net.minecraft.world.tiles.TileEntityMobSpawner;
import net.minecraft.world.tiles.TileEntityMobSpawner;

public class BlockMobSpawner extends BlockContainer {

   protected BlockMobSpawner(int var1, int var2) {
      super(var1, var2, Material.rock);
   }

   protected TileEntity getBlockEntity() {
      return new TileEntityMobSpawner();
   }

   public int idDropped(int var1, Random var2) {
      return 0;
   }

   public int quantityDropped(Random var1) {
      return 0;
   }

   public boolean isOpaqueCube() {
      return false;
   }
}
