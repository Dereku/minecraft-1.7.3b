package net.minecraft.world.chunk;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChunkFolderPattern implements FileFilter {

   public static final Pattern folderRegexPattern = Pattern.compile("[0-9a-z]|([0-9a-z][0-9a-z])");


   public ChunkFolderPattern() {}

   @Override
   public boolean accept(File var1) {
      if(var1.isDirectory()) {
         Matcher var2 = folderRegexPattern.matcher(var1.getName());
         return var2.matches();
      } else {
         return false;
      }
   }
}
