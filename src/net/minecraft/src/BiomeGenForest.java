package net.minecraft.src;

import java.util.Random;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.EntityWolf;
import net.minecraft.src.SpawnListEntry;
import net.minecraft.src.WorldGenBigTree;
import net.minecraft.src.WorldGenForest;
import net.minecraft.src.WorldGenTrees;
import net.minecraft.src.WorldGenerator;

public class BiomeGenForest extends BiomeGenBase {

   public BiomeGenForest() {
      this.spawnableCreatureList.add(new SpawnListEntry(EntityWolf.class, 2));
   }

   public WorldGenerator getRandomWorldGenForTrees(Random var1) {
      return (WorldGenerator)(var1.nextInt(5) == 0?new WorldGenForest():(var1.nextInt(3) == 0?new WorldGenBigTree():new WorldGenTrees()));
   }
}
