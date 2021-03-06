package net.minecraft.world;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import net.minecraft.world.chunk.ChunkCoordinates;
import net.minecraft.world.chunk.ChunkProviderClient;
import net.minecraft.entity.Entity;
import net.minecraft.src.IChunkProvider;
import net.minecraft.src.IWorldAccess;
import net.minecraft.src.MCHash;
import net.minecraft.network.NetClientHandler;
import net.minecraft.network.packets.Packet255KickDisconnect;
import net.minecraft.src.SaveHandlerMP;

public class WorldClient extends World {

    private final LinkedList blocksToReceive = new LinkedList();
    private final NetClientHandler sendQueue;
    private ChunkProviderClient chunkProviderClient;
    private final MCHash entityHashSet = new MCHash();
    private final Set<Entity> entityList = new HashSet<>();
    private final Set<Entity> entitySpawnQueue = new HashSet<>();

    public WorldClient(NetClientHandler var1, long var2, int var4) {
        super(new SaveHandlerMP(), "MpServer", WorldProvider.getProviderForDimension(var4), var2);
        this.sendQueue = var1;
        this.setSpawnPoint(new ChunkCoordinates(8, 64, 8));
        this.mapStorage = var1.mapStorage;
    }

    @Override
    public void tick() {
        this.setWorldTime(this.getWorldTime() + 1L);
        int var1 = this.calculateSkylightSubtracted(1.0F);
        int var2;
        if (var1 != this.skylightSubtracted) {
            this.skylightSubtracted = var1;
            for (var2 = 0; var2 < this.worldAccesses.size(); ++var2) {
                ((IWorldAccess) this.worldAccesses.get(var2)).updateAllRenderers();
            }
        }

        for (var2 = 0; var2 < 10 && !this.entitySpawnQueue.isEmpty(); ++var2) {
            Entity var3 = (Entity) this.entitySpawnQueue.iterator().next();
            if (!this.loadedEntityList.contains(var3)) {
                this.entityJoinedWorld(var3);
            }
        }

        this.sendQueue.processReadPackets();

        for (var2 = 0; var2 < this.blocksToReceive.size(); ++var2) {
            WorldBlockPositionType var4 = (WorldBlockPositionType) this.blocksToReceive.get(var2);
            if (--var4.acceptCountdown == 0) {
                super.setBlockAndMetadata(var4.posX, var4.posY, var4.posZ, var4.blockID, var4.metadata);
                super.markBlockNeedsUpdate(var4.posX, var4.posY, var4.posZ);
                this.blocksToReceive.remove(var2--);
            }
        }

    }

    public void invalidateBlockReceiveRegion(int var1, int var2, int var3, int var4, int var5, int var6) {
        for (int var7 = 0; var7 < this.blocksToReceive.size(); ++var7) {
            WorldBlockPositionType var8 = (WorldBlockPositionType) this.blocksToReceive.get(var7);
            if (var8.posX >= var1 && var8.posY >= var2 && var8.posZ >= var3 && var8.posX <= var4 && var8.posY <= var5 && var8.posZ <= var6) {
                this.blocksToReceive.remove(var7--);
            }
        }

    }

    @Override
    protected IChunkProvider getChunkProvider() {
        return this.chunkProviderClient != null ? this.chunkProviderClient : (this.chunkProviderClient = new ChunkProviderClient(this));
    }

    @Override
    public void setSpawnLocation() {
        this.setSpawnPoint(new ChunkCoordinates(8, 64, 8));
    }

    @Override
    protected void updateBlocksAndPlayCaveSounds() {
    }

    @Override
    public void scheduleBlockUpdate(int var1, int var2, int var3, int var4, int var5) {
    }

    @Override
    public boolean tickUpdates(boolean var1) {
        return false;
    }

    public void doPreChunk(int var1, int var2, boolean var3) {
        if (var3) {
            this.chunkProviderClient.loadChunk(var1, var2);
        } else {
            this.chunkProviderClient.unloadChunk(var1, var2);
        }

        if (!var3) {
            this.markBlocksDirty(var1 * 16, 0, var2 * 16, var1 * 16 + 15, 128, var2 * 16 + 15);
        }

    }

    @Override
    public boolean entityJoinedWorld(Entity var1) {
        boolean var2 = super.entityJoinedWorld(var1);
        this.entityList.add(var1);
        if (!var2) {
            this.entitySpawnQueue.add(var1);
        }

        return var2;
    }

    @Override
    public void setEntityDead(Entity var1) {
        super.setEntityDead(var1);
        this.entityList.remove(var1);
    }

    @Override
    protected void obtainEntitySkin(Entity var1) {
        super.obtainEntitySkin(var1);
        if (this.entitySpawnQueue.contains(var1)) {
            this.entitySpawnQueue.remove(var1);
        }

    }

    @Override
    protected void releaseEntitySkin(Entity var1) {
        super.releaseEntitySkin(var1);
        if (this.entityList.contains(var1)) {
            this.entitySpawnQueue.add(var1);
        }

    }

    public void addEntity(int var1, Entity var2) {
        Entity var3 = this.getEntity(var1);
        if (var3 != null) {
            this.setEntityDead(var3);
        }

        this.entityList.add(var2);
        var2.entityId = var1;
        if (!this.entityJoinedWorld(var2)) {
            this.entitySpawnQueue.add(var2);
        }

        this.entityHashSet.addKey(var1, var2);
    }

    public Entity getEntity(int var1) {
        return (Entity) this.entityHashSet.lookup(var1);
    }

    public Entity removeEntityFromWorld(int var1) {
        Entity var2 = (Entity) this.entityHashSet.removeObject(var1);
        if (var2 != null) {
            this.setEntityDead(var2);
        }
        return var2;
    }

    @Override
    public boolean setBlockMetadata(int var1, int var2, int var3, int var4) {
        int var5 = this.getBlockId(var1, var2, var3);
        int var6 = this.getBlockMetadata(var1, var2, var3);
        if (super.setBlockMetadata(var1, var2, var3, var4)) {
            this.blocksToReceive.add(new WorldBlockPositionType(this, var1, var2, var3, var5, var6));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean setBlockAndMetadata(int var1, int var2, int var3, int var4, int var5) {
        int var6 = this.getBlockId(var1, var2, var3);
        int var7 = this.getBlockMetadata(var1, var2, var3);
        if (super.setBlockAndMetadata(var1, var2, var3, var4, var5)) {
            this.blocksToReceive.add(new WorldBlockPositionType(this, var1, var2, var3, var6, var7));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean setBlock(int var1, int var2, int var3, int var4) {
        int block = this.getBlockId(var1, var2, var3);
        int meta = this.getBlockMetadata(var1, var2, var3);
        if (super.setBlock(var1, var2, var3, var4)) {
            this.blocksToReceive.add(new WorldBlockPositionType(this, var1, var2, var3, block, meta));
            return true;
        } else {
            return false;
        }
    }

    public boolean setBlockAndMetadataAndInvalidate(int var1, int var2, int var3, int var4, int var5) {
        this.invalidateBlockReceiveRegion(var1, var2, var3, var1, var2, var3);
        if (super.setBlockAndMetadata(var1, var2, var3, var4, var5)) {
            this.notifyBlockChange(var1, var2, var3, var4);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void sendQuittingDisconnectingPacket() {
        this.sendQueue.sendPacket(new Packet255KickDisconnect("Quitting"));
    }

    @Override
    protected void updateWeather() {
        if (!this.worldProvider.hasNoSky) {
            if (this.lastLightningBolt > 0) {
                --this.lastLightningBolt;
            }

            this.prevRainingStrength = this.rainingStrength;
            if (this.worldInfo.getIsRaining()) {
                this.rainingStrength = (float) ((double) this.rainingStrength + 0.01D);
            } else {
                this.rainingStrength = (float) ((double) this.rainingStrength - 0.01D);
            }

            if (this.rainingStrength < 0.0F) {
                this.rainingStrength = 0.0F;
            }

            if (this.rainingStrength > 1.0F) {
                this.rainingStrength = 1.0F;
            }

            this.prevThunderingStrength = this.thunderingStrength;
            if (this.worldInfo.getIsThundering()) {
                this.thunderingStrength = (float) ((double) this.thunderingStrength + 0.01D);
            } else {
                this.thunderingStrength = (float) ((double) this.thunderingStrength - 0.01D);
            }

            if (this.thunderingStrength < 0.0F) {
                this.thunderingStrength = 0.0F;
            }

            if (this.thunderingStrength > 1.0F) {
                this.thunderingStrength = 1.0F;
            }

        }
    }
}
