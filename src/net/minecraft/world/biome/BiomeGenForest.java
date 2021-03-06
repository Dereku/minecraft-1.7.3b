package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.entity.EntityWolf;
import net.minecraft.entity.EntityWolf;
import net.minecraft.src.SpawnListEntry;
import net.minecraft.src.SpawnListEntry;
import net.minecraft.world.WorldGenBigTree;
import net.minecraft.world.WorldGenForest;
import net.minecraft.world.WorldGenTrees;
import net.minecraft.world.WorldGenerator;

public class BiomeGenForest extends BiomeGenBase {

   public BiomeGenForest() {
      this.spawnableCreatureList.add(new SpawnListEntry(EntityWolf.class, 2));
   }

   public WorldGenerator getRandomWorldGenForTrees(Random var1) {
      return (WorldGenerator)(var1.nextInt(5) == 0?new WorldGenForest():(var1.nextInt(3) == 0?new WorldGenBigTree():new WorldGenTrees()));
   }
}
