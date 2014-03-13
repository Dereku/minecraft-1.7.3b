package net.minecraft.src;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import net.minecraft.src.J_InvalidSyntaxException;
import net.minecraft.src.J_JsonListenerToJdomAdapter;
import net.minecraft.src.J_JsonRootNode;
import net.minecraft.src.J_SajParser;

public final class J_JdomParser {

   public J_JsonRootNode Parse(Reader var1) throws J_InvalidSyntaxException, IOException {
      J_JsonListenerToJdomAdapter var2 = new J_JsonListenerToJdomAdapter();
      (new J_SajParser()).func_27463_a(var1, var2);
      return var2.func_27208_a();
   }

   public J_JsonRootNode parse(String var1) throws J_InvalidSyntaxException {
      try {
         J_JsonRootNode var2 = this.Parse(new StringReader(var1));
         return var2;
      } catch (IOException var4) {
         throw new RuntimeException("Coding failure in Argo:  StringWriter gave an IOException", var4);
      }
   }
}
