package net.minecraft.src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class ChatAllowedCharacters {

    public static final String allowedCharacters = getAllowedCharacters();
    public static final char[] disallowedCharactersInNamesArray = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};

    private static String getAllowedCharacters() {
        StringBuilder sb;
        InputStreamReader isr = new InputStreamReader(ChatAllowedCharacters.class.getResourceAsStream("/font.txt"), Charset.forName("UTF-8"));
        try (BufferedReader var1 = new BufferedReader(isr)) {
            String tmp;
            sb = new StringBuilder();
            while ((tmp = var1.readLine()) != null) {
                if (!tmp.startsWith("#")) {
                    sb.append(tmp);
                }
            } 
        } catch (Exception var3) {
            return "";
        }
        
        return sb.toString();
    }

}
