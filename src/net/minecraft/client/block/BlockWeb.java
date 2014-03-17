package net.minecraft.client.block;

import java.util.Random;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.client.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity;
import net.minecraft.src.Item;
import net.minecraft.src.Item;
import net.minecraft.src.Material;
import net.minecraft.src.Material;
import net.minecraft.world.World;
import net.minecraft.world.World;

public class BlockWeb extends Block {

   public BlockWeb(int var1, int var2) {
      super(var1, var2, Material.web);
   }

   public void onEntityCollidedWithBlock(World var1, int var2, int var3, int var4, Entity var5) {
      var5.isInWeb = true;
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public AxisAlignedBB getCollisionBoundingBoxFromPool(World var1, int var2, int var3, int var4) {
      return null;
   }

   public int getRenderType() {
      return 1;
   }

   public boolean renderAsNormalBlock() {
      return false;
   }

   public int idDropped(int var1, Random var2) {
      return Item.silk.shiftedIndex;
   }
}
