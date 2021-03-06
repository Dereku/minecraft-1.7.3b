package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.entity.EntityWolf;
import net.minecraft.entity.EntityWolf;
import net.minecraft.src.SpawnListEntry;
import net.minecraft.src.SpawnListEntry;
import net.minecraft.world.WorldGenTaiga1;
import net.minecraft.world.WorldGenTaiga2;
import net.minecraft.world.WorldGenerator;

public class BiomeGenTaiga extends BiomeGenBase {

   public BiomeGenTaiga() {
      this.spawnableCreatureList.add(new SpawnListEntry(EntityWolf.class, 2));
   }

   public WorldGenerator getRandomWorldGenForTrees(Random var1) {
      return (WorldGenerator)(var1.nextInt(3) == 0?new WorldGenTaiga1():new WorldGenTaiga2());
   }
}
