package net.minecraft.client.achiviements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class AchievementMap {

   public static AchievementMap instance = new AchievementMap();
   private Map guidMap = new HashMap();


   private AchievementMap() {
//      try {
//         BufferedReader var1 = new BufferedReader(new InputStreamReader(AchievementMap.class.getResourceAsStream("/achievement/map.txt")));
//
//         String var2;
//         while((var2 = var1.readLine()) != null) {
//            String[] var3 = var2.split(",");
//            int var4 = Integer.parseInt(var3[0]);
//            this.guidMap.put(Integer.valueOf(var4), var3[1]);
//         }
//
//         var1.close();
//      } catch (IOException | NumberFormatException var5) {
//         var5.printStackTrace();
//      }

   }

   public static String getGuid(int var0) {
      return (String)instance.guidMap.get(Integer.valueOf(var0));
   }

}
