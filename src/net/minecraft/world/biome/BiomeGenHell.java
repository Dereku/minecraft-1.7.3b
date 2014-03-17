package net.minecraft.world.biome;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.entity.EntityGhast;
import net.minecraft.entity.EntityGhast;
import net.minecraft.entity.EntityPigZombie;
import net.minecraft.entity.EntityPigZombie;
import net.minecraft.src.SpawnListEntry;
import net.minecraft.src.SpawnListEntry;

public class BiomeGenHell extends BiomeGenBase {

   public BiomeGenHell() {
      this.spawnableMonsterList.clear();
      this.spawnableCreatureList.clear();
      this.spawnableWaterCreatureList.clear();
      this.spawnableMonsterList.add(new SpawnListEntry(EntityGhast.class, 10));
      this.spawnableMonsterList.add(new SpawnListEntry(EntityPigZombie.class, 10));
   }
}
