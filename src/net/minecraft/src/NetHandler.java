package net.minecraft.src;

import net.minecraft.src.Packet;
import net.minecraft.src.Packet100OpenWindow;
import net.minecraft.src.Packet101CloseWindow;
import net.minecraft.src.Packet102WindowClick;
import net.minecraft.src.Packet103SetSlot;
import net.minecraft.src.Packet104WindowItems;
import net.minecraft.src.Packet105UpdateProgressbar;
import net.minecraft.src.Packet106Transaction;
import net.minecraft.src.Packet10Flying;
import net.minecraft.src.Packet130UpdateSign;
import net.minecraft.src.Packet131MapData;
import net.minecraft.src.Packet14BlockDig;
import net.minecraft.src.Packet15Place;
import net.minecraft.src.Packet16BlockItemSwitch;
import net.minecraft.src.Packet17Sleep;
import net.minecraft.src.Packet18Animation;
import net.minecraft.src.Packet19EntityAction;
import net.minecraft.src.Packet1Login;
import net.minecraft.src.Packet200Statistic;
import net.minecraft.src.Packet20NamedEntitySpawn;
import net.minecraft.src.Packet21PickupSpawn;
import net.minecraft.src.Packet22Collect;
import net.minecraft.src.Packet23VehicleSpawn;
import net.minecraft.src.Packet24MobSpawn;
import net.minecraft.src.Packet255KickDisconnect;
import net.minecraft.src.Packet25EntityPainting;
import net.minecraft.src.Packet27Position;
import net.minecraft.src.Packet28EntityVelocity;
import net.minecraft.src.Packet29DestroyEntity;
import net.minecraft.src.Packet2Handshake;
import net.minecraft.src.Packet30Entity;
import net.minecraft.src.Packet34EntityTeleport;
import net.minecraft.src.Packet38EntityStatus;
import net.minecraft.src.Packet39AttachEntity;
import net.minecraft.src.Packet3Chat;
import net.minecraft.src.Packet40EntityMetadata;
import net.minecraft.src.Packet4UpdateTime;
import net.minecraft.src.Packet50PreChunk;
import net.minecraft.src.Packet51MapChunk;
import net.minecraft.src.Packet52MultiBlockChange;
import net.minecraft.src.Packet53BlockChange;
import net.minecraft.src.Packet54PlayNoteBlock;
import net.minecraft.src.Packet5PlayerInventory;
import net.minecraft.src.Packet60Explosion;
import net.minecraft.src.Packet61DoorChange;
import net.minecraft.src.Packet6SpawnPosition;
import net.minecraft.src.Packet70Bed;
import net.minecraft.src.Packet71Weather;
import net.minecraft.src.Packet7UseEntity;
import net.minecraft.src.Packet8UpdateHealth;
import net.minecraft.src.Packet9Respawn;

public abstract class NetHandler {

   public abstract boolean isServerHandler();

   public void handleMapChunk(Packet51MapChunk var1) {}

   public void registerPacket(Packet var1) {}

   public void handleErrorMessage(String var1, Object[] var2) {}

   public void handleKickDisconnect(Packet255KickDisconnect var1) {
      this.registerPacket(var1);
   }

   public void handleLogin(Packet1Login var1) {
      this.registerPacket(var1);
   }

   public void handleFlying(Packet10Flying var1) {
      this.registerPacket(var1);
   }

   public void handleMultiBlockChange(Packet52MultiBlockChange var1) {
      this.registerPacket(var1);
   }

   public void handleBlockDig(Packet14BlockDig var1) {
      this.registerPacket(var1);
   }

   public void handleBlockChange(Packet53BlockChange var1) {
      this.registerPacket(var1);
   }

   public void handlePreChunk(Packet50PreChunk var1) {
      this.registerPacket(var1);
   }

   public void handleNamedEntitySpawn(Packet20NamedEntitySpawn var1) {
      this.registerPacket(var1);
   }

   public void handleEntity(Packet30Entity var1) {
      this.registerPacket(var1);
   }

   public void handleEntityTeleport(Packet34EntityTeleport var1) {
      this.registerPacket(var1);
   }

   public void handlePlace(Packet15Place var1) {
      this.registerPacket(var1);
   }

   public void handleBlockItemSwitch(Packet16BlockItemSwitch var1) {
      this.registerPacket(var1);
   }

   public void handleDestroyEntity(Packet29DestroyEntity var1) {
      this.registerPacket(var1);
   }

   public void handlePickupSpawn(Packet21PickupSpawn var1) {
      this.registerPacket(var1);
   }

   public void handleCollect(Packet22Collect var1) {
      this.registerPacket(var1);
   }

   public void handleChat(Packet3Chat var1) {
      this.registerPacket(var1);
   }

   public void handleVehicleSpawn(Packet23VehicleSpawn var1) {
      this.registerPacket(var1);
   }

   public void handleArmAnimation(Packet18Animation var1) {
      this.registerPacket(var1);
   }

   public void handleEntityAction(Packet19EntityAction var1) {
      this.registerPacket(var1);
   }

   public void handleHandshake(Packet2Handshake var1) {
      this.registerPacket(var1);
   }

   public void handleMobSpawn(Packet24MobSpawn var1) {
      this.registerPacket(var1);
   }

   public void handleUpdateTime(Packet4UpdateTime var1) {
      this.registerPacket(var1);
   }

   public void handleSpawnPosition(Packet6SpawnPosition var1) {
      this.registerPacket(var1);
   }

   public void handleEntityVelocity(Packet28EntityVelocity var1) {
      this.registerPacket(var1);
   }

   public void handleEntityMetadata(Packet40EntityMetadata var1) {
      this.registerPacket(var1);
   }

   public void handleAttachEntity(Packet39AttachEntity var1) {
      this.registerPacket(var1);
   }

   public void handleUseEntity(Packet7UseEntity var1) {
      this.registerPacket(var1);
   }

   public void handleEntityStatus(Packet38EntityStatus var1) {
      this.registerPacket(var1);
   }

   public void handleHealth(Packet8UpdateHealth var1) {
      this.registerPacket(var1);
   }

   public void handleRespawn(Packet9Respawn var1) {
      this.registerPacket(var1);
   }

   public void handleExplosion(Packet60Explosion var1) {
      this.registerPacket(var1);
   }

   public void handleOpenWindow(Packet100OpenWindow var1) {
      this.registerPacket(var1);
   }

   public void handleCloseWindow(Packet101CloseWindow var1) {
      this.registerPacket(var1);
   }

   public void handleWindowClick(Packet102WindowClick var1) {
      this.registerPacket(var1);
   }

   public void handleSetSlot(Packet103SetSlot var1) {
      this.registerPacket(var1);
   }

   public void handleWindowItems(Packet104WindowItems var1) {
      this.registerPacket(var1);
   }

   public void handleUpdateSign(Packet130UpdateSign var1) {
      this.registerPacket(var1);
   }

   public void handleCraftingProgress(Packet105UpdateProgressbar var1) {
      this.registerPacket(var1);
   }

   public void handlePlayerInventory(Packet5PlayerInventory var1) {
      this.registerPacket(var1);
   }

   public void handleContainerTransaction(Packet106Transaction var1) {
      this.registerPacket(var1);
   }

   public void handleEntityPainting(Packet25EntityPainting var1) {
      this.registerPacket(var1);
   }

   public void handleNotePlay(Packet54PlayNoteBlock var1) {
      this.registerPacket(var1);
   }

   public void handleStatistic(Packet200Statistic var1) {
      this.registerPacket(var1);
   }

   public void handleSleep(Packet17Sleep var1) {
      this.registerPacket(var1);
   }

   public void handlePosition(Packet27Position var1) {
      this.registerPacket(var1);
   }

   public void handleBedUpdate(Packet70Bed var1) {
      this.registerPacket(var1);
   }

   public void handleWeather(Packet71Weather var1) {
      this.registerPacket(var1);
   }

   public void processItemData(Packet131MapData var1) {
      this.registerPacket(var1);
   }

   public void handleAuxSFX(Packet61DoorChange var1) {
      this.registerPacket(var1);
   }
}
