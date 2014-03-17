package net.minecraft.src;

import net.minecraft.client.block.Block;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.src.EnumSkyBlock;
import net.minecraft.world.World;

public class MetadataChunkBlock {

   public final EnumSkyBlock blockEnum;
   public int minX;
   public int minY;
   public int minZ;
   public int maxX;
   public int maxY;
   public int maxZ;


   public MetadataChunkBlock(EnumSkyBlock var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.blockEnum = var1;
      this.minX = var2;
      this.minY = var3;
      this.minZ = var4;
      this.maxX = var5;
      this.maxY = var6;
      this.maxZ = var7;
   }

   public void updateChunkLighting(World var1) {
      int var2 = this.maxX - this.minX + 1;
      int var3 = this.maxY - this.minY + 1;
      int var4 = this.maxZ - this.minZ + 1;
      int var5 = var2 * var3 * var4;
      if(var5 > '\u8000') {
         System.out.println("Light too large, skipping!");
      } else {
         int var6 = 0;
         int var7 = 0;
         boolean var8 = false;
         boolean var9 = false;

         for(int var10 = this.minX; var10 <= this.maxX; ++var10) {
            for(int var11 = this.minZ; var11 <= this.maxZ; ++var11) {
               int var12 = var10 >> 4;
               int var13 = var11 >> 4;
               boolean var14 = false;
               if(var8 && var12 == var6 && var13 == var7) {
                  var14 = var9;
               } else {
                  var14 = var1.doChunksNearChunkExist(var10, 0, var11, 1);
                  if(var14) {
                     Chunk var15 = var1.getChunkFromChunkCoords(var10 >> 4, var11 >> 4);
                     if(var15.func_21167_h()) {
                        var14 = false;
                     }
                  }

                  var9 = var14;
                  var6 = var12;
                  var7 = var13;
               }

               if(var14) {
                  if(this.minY < 0) {
                     this.minY = 0;
                  }

                  if(this.maxY >= 128) {
                     this.maxY = 127;
                  }

                  for(int var27 = this.minY; var27 <= this.maxY; ++var27) {
                     int var16 = var1.getSavedLightValue(this.blockEnum, var10, var27, var11);
                     boolean var17 = false;
                     int var18 = var1.getBlockId(var10, var27, var11);
                     int var19 = Block.lightOpacity[var18];
                     if(var19 == 0) {
                        var19 = 1;
                     }

                     int var20 = 0;
                     if(this.blockEnum == EnumSkyBlock.Sky) {
                        if(var1.canExistingBlockSeeTheSky(var10, var27, var11)) {
                           var20 = 15;
                        }
                     } else if(this.blockEnum == EnumSkyBlock.Block) {
                        var20 = Block.lightValue[var18];
                     }

                     int var21;
                     int var28;
                     if(var19 >= 15 && var20 == 0) {
                        var28 = 0;
                     } else {
                        var21 = var1.getSavedLightValue(this.blockEnum, var10 - 1, var27, var11);
                        int var22 = var1.getSavedLightValue(this.blockEnum, var10 + 1, var27, var11);
                        int var23 = var1.getSavedLightValue(this.blockEnum, var10, var27 - 1, var11);
                        int var24 = var1.getSavedLightValue(this.blockEnum, var10, var27 + 1, var11);
                        int var25 = var1.getSavedLightValue(this.blockEnum, var10, var27, var11 - 1);
                        int var26 = var1.getSavedLightValue(this.blockEnum, var10, var27, var11 + 1);
                        var28 = var21;
                        if(var22 > var21) {
                           var28 = var22;
                        }

                        if(var23 > var28) {
                           var28 = var23;
                        }

                        if(var24 > var28) {
                           var28 = var24;
                        }

                        if(var25 > var28) {
                           var28 = var25;
                        }

                        if(var26 > var28) {
                           var28 = var26;
                        }

                        var28 -= var19;
                        if(var28 < 0) {
                           var28 = 0;
                        }

                        if(var20 > var28) {
                           var28 = var20;
                        }
                     }

                     if(var16 != var28) {
                        var1.setLightValue(this.blockEnum, var10, var27, var11, var28);
                        var21 = var28 - 1;
                        if(var21 < 0) {
                           var21 = 0;
                        }

                        var1.neighborLightPropagationChanged(this.blockEnum, var10 - 1, var27, var11, var21);
                        var1.neighborLightPropagationChanged(this.blockEnum, var10, var27 - 1, var11, var21);
                        var1.neighborLightPropagationChanged(this.blockEnum, var10, var27, var11 - 1, var21);
                        if(var10 + 1 >= this.maxX) {
                           var1.neighborLightPropagationChanged(this.blockEnum, var10 + 1, var27, var11, var21);
                        }

                        if(var27 + 1 >= this.maxY) {
                           var1.neighborLightPropagationChanged(this.blockEnum, var10, var27 + 1, var11, var21);
                        }

                        if(var11 + 1 >= this.maxZ) {
                           var1.neighborLightPropagationChanged(this.blockEnum, var10, var27, var11 + 1, var21);
                        }
                     }
                  }
               }
            }
         }

      }
   }

   public boolean func_866_a(int var1, int var2, int var3, int var4, int var5, int var6) {
      if(var1 >= this.minX && var2 >= this.minY && var3 >= this.minZ && var4 <= this.maxX && var5 <= this.maxY && var6 <= this.maxZ) {
         return true;
      } else {
         byte var7 = 1;
         if(var1 >= this.minX - var7 && var2 >= this.minY - var7 && var3 >= this.minZ - var7 && var4 <= this.maxX + var7 && var5 <= this.maxY + var7 && var6 <= this.maxZ + var7) {
            int var8 = this.maxX - this.minX;
            int var9 = this.maxY - this.minY;
            int var10 = this.maxZ - this.minZ;
            if(var1 > this.minX) {
               var1 = this.minX;
            }

            if(var2 > this.minY) {
               var2 = this.minY;
            }

            if(var3 > this.minZ) {
               var3 = this.minZ;
            }

            if(var4 < this.maxX) {
               var4 = this.maxX;
            }

            if(var5 < this.maxY) {
               var5 = this.maxY;
            }

            if(var6 < this.maxZ) {
               var6 = this.maxZ;
            }

            int var11 = var4 - var1;
            int var12 = var5 - var2;
            int var13 = var6 - var3;
            int var14 = var8 * var9 * var10;
            int var15 = var11 * var12 * var13;
            if(var15 - var14 <= 2) {
               this.minX = var1;
               this.minY = var2;
               this.minZ = var3;
               this.maxX = var4;
               this.maxY = var5;
               this.maxZ = var6;
               return true;
            }
         }

         return false;
      }
   }
}
