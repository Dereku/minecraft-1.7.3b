package net.minecraft.json;

import java.util.Arrays;
import java.util.Map;
import net.minecraft.json.J_JsonArray;
import net.minecraft.json.J_JsonConstants;
import net.minecraft.json.J_JsonNode;
import net.minecraft.json.J_JsonNumberNode;
import net.minecraft.json.J_JsonObject;
import net.minecraft.json.J_JsonRootNode;
import net.minecraft.json.J_JsonStringNode;

public final class J_JsonNodeFactories {

   public static J_JsonNode func_27310_a() {
      return J_JsonConstants.NULL;
   }

   public static J_JsonNode func_27313_b() {
      return J_JsonConstants.TRUE;
   }

   public static J_JsonNode func_27314_c() {
      return J_JsonConstants.FALSE;
   }

   public static J_JsonStringNode func_27316_a(String var0) {
      return new J_JsonStringNode(var0);
   }

   public static J_JsonNode func_27311_b(String var0) {
      return new J_JsonNumberNode(var0);
   }

   public static J_JsonRootNode func_27309_a(Iterable var0) {
      return new J_JsonArray(var0);
   }

   public static J_JsonRootNode func_27315_a(J_JsonNode ... var0) {
      return func_27309_a(Arrays.asList(var0));
   }

   public static J_JsonRootNode func_27312_a(Map var0) {
      return new J_JsonObject(var0);
   }
}
