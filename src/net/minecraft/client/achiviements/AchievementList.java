package net.minecraft.client.achiviements;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.block.Block;
import net.minecraft.client.item.Item;

public class AchievementList {

   public static int minDisplayColumn;
   public static int minDisplayRow;
   public static int maxDisplayColumn;
   public static int maxDisplayRow;
   public static List achievementList = new ArrayList();
   public static Achievement openInventory = (new Achievement(0, "openInventory", 0, 0, Item.book, (Achievement)null)).setIndependent().registerAchievement();
   public static Achievement mineWood = (new Achievement(1, "mineWood", 2, 1, Block.wood, AchievementList.openInventory)).registerAchievement();
   public static Achievement buildWorkBench = (new Achievement(2, "buildWorkBench", 4, -1, Block.workbench, AchievementList.mineWood)).registerAchievement();
   public static Achievement buildPickaxe = (new Achievement(3, "buildPickaxe", 4, 2, Item.pickaxeWood, AchievementList.buildWorkBench)).registerAchievement();
   public static Achievement buildFurnace = (new Achievement(4, "buildFurnace", 3, 4, Block.stoneOvenActive, AchievementList.buildPickaxe)).registerAchievement();
   public static Achievement acquireIron = (new Achievement(5, "acquireIron", 1, 4, Item.ingotIron, AchievementList.buildFurnace)).registerAchievement();
   public static Achievement diamonds = (new Achievement(6, "diamonds", -1, 5, Item.diamond, AchievementList.acquireIron)).registerAchievement();
   public static Achievement buildHoe = (new Achievement(7, "buildHoe", 2, -3, Item.hoeWood, AchievementList.buildWorkBench)).registerAchievement();
   public static Achievement makeBread = (new Achievement(8, "makeBread", -1, -3, Item.bread, AchievementList.buildHoe)).registerAchievement();
   public static Achievement bakeCake = (new Achievement(9, "bakeCake", 0, -5, Item.cake, AchievementList.buildHoe)).registerAchievement();
   public static Achievement buildBetterPickaxe = (new Achievement(10, "buildBetterPickaxe", 6, 2, Item.pickaxeStone, AchievementList.buildPickaxe)).registerAchievement();
   public static Achievement cookFish = (new Achievement(11, "cookFish", 2, 6, Item.fishCooked, AchievementList.buildFurnace)).registerAchievement();
   public static Achievement onARail = (new Achievement(12, "onARail", 2, 3, Block.rail, AchievementList.acquireIron)).setSpecial().registerAchievement();
   public static Achievement buildSword = (new Achievement(13, "buildSword", 6, -1, Item.swordWood, AchievementList.buildWorkBench)).registerAchievement();
   public static Achievement killEnemy = (new Achievement(14, "killEnemy", 8, -1, Item.bone, AchievementList.buildSword)).registerAchievement();
   public static Achievement killCow = (new Achievement(15, "killCow", 7, -3, Item.leather, AchievementList.buildSword)).registerAchievement();
   public static Achievement flyPig = (new Achievement(16, "flyPig", 8, -4, Item.saddle, AchievementList.killCow)).setSpecial().registerAchievement();
   public static Achievement craftDiamondHoe = (new Achievement(17, "craftDiamondHoe", 3, -4, Item.hoeDiamond, AchievementList.buildHoe)).setSpecial().registerAchievement();


   public static void call() {}

   static {
      System.out.println(AchievementList.achievementList.size() + " achievements");
   }
}
