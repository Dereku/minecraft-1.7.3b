package net.minecraft.src;

import java.util.Random;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.world.WorldGenBigTree;
import net.minecraft.world.WorldGenTrees;
import net.minecraft.world.WorldGenerator;

public class BiomeGenRainforest extends BiomeGenBase {

   public WorldGenerator getRandomWorldGenForTrees(Random var1) {
      return (WorldGenerator)(var1.nextInt(3) == 0?new WorldGenBigTree():new WorldGenTrees());
   }
}
