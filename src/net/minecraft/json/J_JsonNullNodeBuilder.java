package net.minecraft.json;

import net.minecraft.json.J_JsonNode;
import net.minecraft.json.J_JsonNodeBuilder;
import net.minecraft.json.J_JsonNodeFactories;

final class J_JsonNullNodeBuilder implements J_JsonNodeBuilder {

   public J_JsonNode buildNode() {
      return J_JsonNodeFactories.func_27310_a();
   }
}
