package net.minecraft.src;

import java.io.IOException;
import java.io.InputStream;
import net.minecraft.src.MusInputStream;
import paulscode.sound.codecs.CodecJOrbis;

public class CodecMus extends CodecJOrbis {

   protected InputStream openInputStream() {
      try {
         return new MusInputStream(this, this.url, this.urlConnection.getInputStream());
      } catch (IOException e) {
         return null;
      }
   }
}
