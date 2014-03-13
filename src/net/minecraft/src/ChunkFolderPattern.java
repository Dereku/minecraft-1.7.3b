package net.minecraft.src;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.src.Empty2;

class ChunkFolderPattern implements FileFilter {

   public static final Pattern folderRegexPattern = Pattern.compile("[0-9a-z]|([0-9a-z][0-9a-z])");


   private ChunkFolderPattern() {}

   public boolean accept(File var1) {
      if(var1.isDirectory()) {
         Matcher var2 = folderRegexPattern.matcher(var1.getName());
         return var2.matches();
      } else {
         return false;
      }
   }

   // $FF: synthetic method
   ChunkFolderPattern(Empty2 var1) {
      this();
   }

}
