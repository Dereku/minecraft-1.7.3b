package net.minecraft.src;

import java.io.IOException;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.World;

public interface IChunkLoader {

   Chunk loadChunk(World var1, int var2, int var3) throws IOException;

   void saveChunk(World var1, Chunk var2) throws IOException;

   void saveExtraChunkData(World var1, Chunk var2) throws IOException;

   void func_814_a();

   void saveExtraData();
}
