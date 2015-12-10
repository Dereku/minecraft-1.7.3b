package net.minecraft.client.render;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.block.Block;
import net.minecraft.src.Config;
import net.minecraft.src.GLAllocation;
import net.minecraft.src.GameSettings;
import net.minecraft.src.ImageBuffer;
import net.minecraft.src.TextureFX;
import net.minecraft.src.TexturePackBase;
import net.minecraft.src.TexturePackDefault;
import net.minecraft.src.TexturePackList;
import net.minecraft.src.ThreadDownloadImageData;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.Dimension;

public class RenderEngine {

    public static boolean useMipmaps = false;
    private final HashMap textureMap = new HashMap();
    private final HashMap field_28151_c = new HashMap();
    private final HashMap textureNameToImageMap = new HashMap();
    private final IntBuffer singleIntBuffer = GLAllocation.createDirectIntBuffer(1);
    private ByteBuffer imageData;
    private final List textureList;
    private final Map urlToImageDataMap;
    private final GameSettings options;
    private boolean clampTexture;
    private boolean blurTexture;
    private final TexturePackList texturePack;
    private final BufferedImage missingTextureImage;
    private int terrainTextureId = -1;
    private int guiItemsTextureId = -1;
    private boolean hdTexturesInstalled = false;
    private final Map textureDimensionsMap = new HashMap();
    private final Map textureDataMap = new HashMap();
    private int tickCounter = 0;
    private ByteBuffer[] mipImageDatas;
    private boolean dynamicTexturesUpdated = false;


   public RenderEngine(TexturePackList texturepacklist, GameSettings gamesettings) {
       this.allocateImageData(256);
       this.textureList = new ArrayList();
       this.urlToImageDataMap = new HashMap();
       this.clampTexture = false;
       this.blurTexture = false;
       this.missingTextureImage = new BufferedImage(64, 64, 2);
       this.texturePack = texturepacklist;
       this.options = gamesettings;
       Graphics missingTexture = this.missingTextureImage.getGraphics();

       missingTexture.setColor(Color.BLACK);
       missingTexture.fillRect(0, 0, 64, 64);
       missingTexture.setColor(Color.MAGENTA);
       missingTexture.fillRect(0, 0, 32, 32);
       missingTexture.fillRect(32, 32, 64, 64);
       missingTexture.dispose();
   }
   
    private void allocateImageData(int width) {
        int imgLen = width * width * 4;

        this.imageData = GLAllocation.createDirectByteBuffer(imgLen);
        ArrayList list = new ArrayList();

        for (int mipWidth = width / 2; mipWidth > 0; mipWidth /= 2) {
            int mipLen = mipWidth * mipWidth * 4;
            ByteBuffer buf = GLAllocation.createDirectByteBuffer(mipLen);

            list.add(buf);
        }

        this.mipImageDatas = (ByteBuffer[]) ((ByteBuffer[]) list.toArray(new ByteBuffer[list.size()]));
    }

    public int[] getTextureContents(String s) {
        TexturePackBase texturepackbase = this.texturePack.selectedTexturePack;
        int[] ai = (int[]) ((int[]) this.field_28151_c.get(s));

        if (ai != null) {
            return ai;
        } else {
            int[] ai2;

            try {
                if (s.startsWith("##")) {
                    ai2 = this.getImageContentsAndAllocate(this.unwrapImageByColumns(this.readTextureImage(texturepackbase.getResourceAsStream(s.substring(2)))));
                } else if (s.startsWith("%clamp%")) {
                    this.clampTexture = true;
                    ai2 = this.getImageContentsAndAllocate(this.readTextureImage(texturepackbase.getResourceAsStream(s.substring(7))));
                    this.clampTexture = false;
                } else if (s.startsWith("%blur%")) {
                    this.blurTexture = true;
                    ai2 = this.getImageContentsAndAllocate(this.readTextureImage(texturepackbase.getResourceAsStream(s.substring(6))));
                    this.blurTexture = false;
                } else {
                    InputStream inputstream = texturepackbase.getResourceAsStream(s);

                    if (inputstream == null) {
                        ai2 = this.getImageContentsAndAllocate(this.missingTextureImage);
                    } else {
                        ai2 = this.getImageContentsAndAllocate(this.readTextureImage(inputstream));
                    }
                }

                this.field_28151_c.put(s, ai2);
                return ai2;
            } catch (IOException ioexception) {
                ioexception.printStackTrace();
                ai2 = this.getImageContentsAndAllocate(this.missingTextureImage);
                this.field_28151_c.put(s, ai2);
                return ai2;
            }
        }
    }

   private int[] getImageContentsAndAllocate(BufferedImage var1) {
      int var2 = var1.getWidth();
      int var3 = var1.getHeight();
      int[] var4 = new int[var2 * var3];
      var1.getRGB(0, 0, var2, var3, var4, 0, var2);
      return var4;
   }

   private int[] getImageContents(BufferedImage var1, int[] var2) {
      int var3 = var1.getWidth();
      int var4 = var1.getHeight();
      var1.getRGB(0, 0, var3, var4, var2, 0, var3);
      return var2;
   }

   public int getTexture(String var1) {
      TexturePackBase var2 = this.texturePack.selectedTexturePack;
      Integer var3 = (Integer)this.textureMap.get(var1);
      if(var3 != null) {
         return var3;
      } else {
         try {
            this.singleIntBuffer.clear();
            GLAllocation.generateTextureNames(this.singleIntBuffer);
            int var6 = this.singleIntBuffer.get(0);
            if(var1.startsWith("##")) {
               this.setupTexture(this.unwrapImageByColumns(this.readTextureImage(var2.getResourceAsStream(var1.substring(2)))), var6);
            } else if(var1.startsWith("%clamp%")) {
               this.clampTexture = true;
               this.setupTexture(this.readTextureImage(var2.getResourceAsStream(var1.substring(7))), var6);
               this.clampTexture = false;
            } else if(var1.startsWith("%blur%")) {
               this.blurTexture = true;
               this.setupTexture(this.readTextureImage(var2.getResourceAsStream(var1.substring(6))), var6);
               this.blurTexture = false;
            } else {
               InputStream var7 = var2.getResourceAsStream(var1);
               if(var7 == null) {
                  this.setupTexture(this.missingTextureImage, var6);
                   if (var1.equals("/assets/terrain.png")) {
                       this.terrainTextureId = var3;
                   }

                   if (var1.equals("/assets/gui/items.png")) {
                       this.guiItemsTextureId = var3;
                   }

                   this.setupTexture(this.readTextureImage(var7), var3);
               }
            }

            this.textureMap.put(var1, var6);
            return var6;
         } catch (IOException var5) {
            var5.printStackTrace();
            GLAllocation.generateTextureNames(this.singleIntBuffer);
            int var4 = this.singleIntBuffer.get(0);
            this.setupTexture(this.missingTextureImage, var4);
            this.textureMap.put(var1, var4);
            return var4;
         }
      }
   }

   private BufferedImage unwrapImageByColumns(BufferedImage var1) {
      int var2 = var1.getWidth() / 16;
      BufferedImage var3 = new BufferedImage(16, var1.getHeight() * var2, 2);
      Graphics var4 = var3.getGraphics();

      for(int var5 = 0; var5 < var2; ++var5) {
         var4.drawImage(var1, -var5 * 16, var5 * var1.getHeight(), (ImageObserver)null);
      }

      var4.dispose();
      return var3;
   }

   public int allocateAndSetupTexture(BufferedImage var1) {
      this.singleIntBuffer.clear();
      GLAllocation.generateTextureNames(this.singleIntBuffer);
      int var2 = this.singleIntBuffer.get(0);
      this.setupTexture(var1, var2);
      this.textureNameToImageMap.put(var2, var1);
      return var2;
   }

    public void setupTexture(BufferedImage bufferedimage, int i) {
        GL11.glBindTexture(3553, i);
        RenderEngine.useMipmaps = Config.isUseMipmaps();
        int width;
        int height;

        if (RenderEngine.useMipmaps && i != this.guiItemsTextureId) {
            width = Config.getMipmapType();
            GL11.glTexParameteri(3553, 10241, width);
            GL11.glTexParameteri(3553, 10240, 9728);
            if (GLContext.getCapabilities().OpenGL12) {
                GL11.glTexParameteri(3553, '脼', 0);
                height = Config.getMipmapLevel();
                if (height >= 4) {
                    int ai = Math.min(bufferedimage.getWidth(), bufferedimage.getHeight());

                    height = this.getMaxMipmapLevel(ai) - 4;
                    if (height < 0) {
                        height = 0;
                    }
                }

                GL11.glTexParameteri(3553, '脽', height);
            }
        } else {
            GL11.glTexParameteri(3553, 10241, 9728);
            GL11.glTexParameteri(3553, 10240, 9728);
        }

        if (this.blurTexture) {
            GL11.glTexParameteri(3553, 10241, 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
        }

        if (this.clampTexture) {
            GL11.glTexParameteri(3553, 10242, 10496);
            GL11.glTexParameteri(3553, 10243, 10496);
        } else {
            GL11.glTexParameteri(3553, 10242, 10497);
            GL11.glTexParameteri(3553, 10243, 10497);
        }

        width = bufferedimage.getWidth();
        height = bufferedimage.getHeight();
        this.setTextureDimension(i, new Dimension(width, height));
        int[] aint = new int[width * height];
        byte[] byteBuf = new byte[width * height * 4];

        bufferedimage.getRGB(0, 0, width, height, aint, 0, width);

        for (int l = 0; l < aint.length; ++l) {
            int alpha = aint[l] >> 24 & 255;
            int red = aint[l] >> 16 & 255;
            int green = aint[l] >> 8 & 255;
            int blue = aint[l] & 255;

            if (this.options != null && this.options.anaglyph) {
                int j3 = (red * 30 + green * 59 + blue * 11) / 100;
                int l3 = (red * 30 + green * 70) / 100;
                int j4 = (red * 30 + blue * 70) / 100;

                red = j3;
                green = l3;
                blue = j4;
            }

            if (alpha == 0) {
                red = 255;
                green = 255;
                blue = 255;
            }

            byteBuf[l * 4 + 0] = (byte) red;
            byteBuf[l * 4 + 1] = (byte) green;
            byteBuf[l * 4 + 2] = (byte) blue;
            byteBuf[l * 4 + 3] = (byte) alpha;
        }

        this.checkImageDataSize(width);
        this.imageData.clear();
        this.imageData.put(byteBuf);
        this.imageData.position(0).limit(byteBuf.length);
        GL11.glTexImage2D(3553, 0, 6408, width, height, 0, 6408, 5121, this.imageData);
        if (RenderEngine.useMipmaps) {
            this.generateMipMaps(this.imageData, width, height);
        }
    }
    
    private int getMaxMipmapLevel(int size) {
        int level;

        for (level = 0; size > 0; ++level) {
            size /= 2;
        }

        return level - 1;
    }
    
    private void checkImageDataSize(int width) {
        if (this.imageData != null) {
            int len = width * width * 4;

            if (this.imageData.capacity() >= len) {
                return;
            }
        }

        this.allocateImageData(width);
    }
    
    private void setTextureDimension(int id, Dimension dim) {
        this.textureDimensionsMap.put(new Integer(id), dim);
        if (id == this.terrainTextureId) {
            Config.setIconWidthTerrain(dim.getWidth() / 16);
            this.updateDinamicTextures(0, dim);
        }

        if (id == this.guiItemsTextureId) {
            Config.setIconWidthItems(dim.getWidth() / 16);
            this.updateDinamicTextures(1, dim);
        }

    }
    
    private void updateDinamicTextures(int texNum, Dimension dim) {
        this.checkHdTextures();

        for (int i = 0; i < this.textureList.size(); ++i) {
            TextureFX tex = (TextureFX) this.textureList.get(i);

            if (tex.tileImage == texNum && tex instanceof TextureHDFX) {
                TextureHDFX texHD = (TextureHDFX) tex;

                texHD.setTexturePackBase(this.texturePack.selectedTexturePack);
                texHD.setTileWidth(dim.getWidth() / 16);
            }
        }

    }
    
    public void checkHdTextures() {
        if (!this.hdTexturesInstalled) {
            Minecraft mc = Config.getMinecraft();

            if (mc != null) {
                this.registerTextureFX(new TextureHDLavaFX());
                this.registerTextureFX(new TextureHDWaterFX());
                this.registerTextureFX(new TextureHDPortalFX());
                this.registerTextureFX(new TextureHDCompassFX(mc));
                this.registerTextureFX(new TextureHDWatchFX(mc));
                this.registerTextureFX(new TextureHDWaterFlowFX());
                this.registerTextureFX(new TextureHDLavaFlowFX());
                this.registerTextureFX(new TextureHDFlamesFX(0));
                this.registerTextureFX(new TextureHDFlamesFX(1));
                this.hdTexturesInstalled = true;
            }
        }
    }

    private void generateMipMaps(ByteBuffer data, int width, int height) {
        ByteBuffer parMipData = data;

        for (int level = 1; level <= 16; ++level) {
            int parWidth = width >> level - 1;
            int mipWidth = width >> level;
            int mipHeight = height >> level;

            if (mipWidth <= 0 || mipHeight <= 0) {
                break;
            }

            ByteBuffer mipData = this.mipImageDatas[level - 1];

            for (int mipX = 0; mipX < mipWidth; ++mipX) {
                for (int mipY = 0; mipY < mipHeight; ++mipY) {
                    int p1 = parMipData.getInt((mipX * 2 + 0 + (mipY * 2 + 0) * parWidth) * 4);
                    int p2 = parMipData.getInt((mipX * 2 + 1 + (mipY * 2 + 0) * parWidth) * 4);
                    int p3 = parMipData.getInt((mipX * 2 + 1 + (mipY * 2 + 1) * parWidth) * 4);
                    int p4 = parMipData.getInt((mipX * 2 + 0 + (mipY * 2 + 1) * parWidth) * 4);
                    int pixel = this.weightedAverageColor(p1, p2, p3, p4);

                    mipData.putInt((mipX + mipY * mipWidth) * 4, pixel);
                }
            }

            GL11.glTexImage2D(3553, level, 6408, mipWidth, mipHeight, 0, 6408, 5121, mipData);
            parMipData = mipData;
        }

    }
    
    private int weightedAverageColor(int c1, int c2, int c3, int c4) {
        int cx1 = this.weightedAverageColor(c1, c2);
        int cx2 = this.weightedAverageColor(c3, c4);
        int cx = this.weightedAverageColor(cx1, cx2);

        return cx;
    }

    private int weightedAverageColor(int c1, int c2) {
        int a1 = (c1 & -16777216) >> 24 & 255;
        int a2 = (c2 & -16777216) >> 24 & 255;
        int ax = (a1 + a2) / 2;

        if (a1 == 0 && a2 == 0) {
            a1 = 1;
            a2 = 1;
        } else {
            if (a1 == 0) {
                c1 = c2;
                ax /= 2;
            }

            if (a2 == 0) {
                c2 = c1;
                ax /= 2;
            }
        }

        int r1 = (c1 >> 16 & 255) * a1;
        int g1 = (c1 >> 8 & 255) * a1;
        int b1 = (c1 & 255) * a1;
        int r2 = (c2 >> 16 & 255) * a2;
        int g2 = (c2 >> 8 & 255) * a2;
        int b2 = (c2 & 255) * a2;
        int rx = (r1 + r2) / (a1 + a2);
        int gx = (g1 + g2) / (a1 + a2);
        int bx = (b1 + b2) / (a1 + a2);

        return ax << 24 | rx << 16 | gx << 8 | bx;
    }
    
    public void createTextureFromBytes(int[] ai, int i, int j, int k) {
        GL11.glBindTexture(3553, k);
        if (RenderEngine.useMipmaps) {
            GL11.glTexParameteri(3553, 10241, 9986);
            GL11.glTexParameteri(3553, 10240, 9728);
        } else {
            GL11.glTexParameteri(3553, 10241, 9728);
            GL11.glTexParameteri(3553, 10240, 9728);
        }

        if (this.blurTexture) {
            GL11.glTexParameteri(3553, 10241, 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
        }

        if (this.clampTexture) {
            GL11.glTexParameteri(3553, 10242, 10496);
            GL11.glTexParameteri(3553, 10243, 10496);
        } else {
            GL11.glTexParameteri(3553, 10242, 10497);
            GL11.glTexParameteri(3553, 10243, 10497);
        }

        byte[] abyte0 = new byte[i * j * 4];

        for (int l = 0; l < ai.length; ++l) {
            int i1 = ai[l] >> 24 & 255;
            int j1 = ai[l] >> 16 & 255;
            int k1 = ai[l] >> 8 & 255;
            int l1 = ai[l] & 255;

            if (this.options != null && this.options.anaglyph) {
                int i2 = (j1 * 30 + k1 * 59 + l1 * 11) / 100;
                int j2 = (j1 * 30 + k1 * 70) / 100;
                int k2 = (j1 * 30 + l1 * 70) / 100;

                j1 = i2;
                k1 = j2;
                l1 = k2;
            }

            abyte0[l * 4 + 0] = (byte) j1;
            abyte0[l * 4 + 1] = (byte) k1;
            abyte0[l * 4 + 2] = (byte) l1;
            abyte0[l * 4 + 3] = (byte) i1;
        }

        this.imageData.clear();
        this.imageData.put(abyte0);
        this.imageData.position(0).limit(abyte0.length);
        GL11.glTexSubImage2D(3553, 0, 0, 0, i, j, 6408, 5121, this.imageData);
    }

   public void deleteTexture(int var1) {
      this.textureNameToImageMap.remove(var1);
      this.singleIntBuffer.clear();
      this.singleIntBuffer.put(var1);
      this.singleIntBuffer.flip();
      GL11.glDeleteTextures(this.singleIntBuffer);
   }

   public int getTextureForDownloadableImage(String var1, String var2) {
      ThreadDownloadImageData var3 = (ThreadDownloadImageData)this.urlToImageDataMap.get(var1);
      if(var3 != null && var3.image != null && !var3.textureSetupComplete) {
         if(var3.textureName < 0) {
            var3.textureName = this.allocateAndSetupTexture(var3.image);
         } else {
            this.setupTexture(var3.image, var3.textureName);
         }

         var3.textureSetupComplete = true;
      }

      return var3 != null && var3.textureName >= 0?var3.textureName:(var2 == null?-1:this.getTexture(var2));
   }

   public ThreadDownloadImageData obtainImageData(String var1, ImageBuffer var2) {
      ThreadDownloadImageData var3 = (ThreadDownloadImageData)this.urlToImageDataMap.get(var1);
      if(var3 == null) {
         this.urlToImageDataMap.put(var1, new ThreadDownloadImageData(var1, var2));
      } else {
         ++var3.referenceCount;
      }

      return var3;
   }

   public void releaseImageData(String var1) {
      ThreadDownloadImageData var2 = (ThreadDownloadImageData)this.urlToImageDataMap.get(var1);
      if(var2 != null) {
         --var2.referenceCount;
         if(var2.referenceCount == 0) {
            if(var2.textureName >= 0) {
               this.deleteTexture(var2.textureName);
            }

            this.urlToImageDataMap.remove(var1);
         }
      }

   }

    public void registerTextureFX(TextureFX texturefx) {
        for (int i = 0; i < this.textureList.size(); ++i) {
            TextureFX tex = (TextureFX) this.textureList.get(i);

            if (tex.tileImage == texturefx.tileImage && tex.iconIndex == texturefx.iconIndex) {
                this.textureList.remove(i);
                --i;
                Config.dbg("Texture removed: " + tex + ", image: " + tex.tileImage + ", index: " + tex.iconIndex);
            }
        }

        this.textureList.add(texturefx);
        texturefx.onTick();
        Config.dbg("Texture registered: " + texturefx + ", image: " + texturefx.tileImage + ", index: " + texturefx.iconIndex);
        this.dynamicTexturesUpdated = false;
    }

    public void updateDynamicTextures() {
        this.checkHdTextures();
        ++this.tickCounter;
        this.terrainTextureId = this.getTexture("/assets/terrain.png");
        this.guiItemsTextureId = this.getTexture("/assets/gui/items.png");

        int i;
        TextureFX texturefx1;

        for (i = 0; i < this.textureList.size(); ++i) {
            texturefx1 = (TextureFX) this.textureList.get(i);
            texturefx1.anaglyphEnabled = this.options.anaglyph;
            if (!texturefx1.getClass().getName().equals("ModTextureStatic") || !this.dynamicTexturesUpdated) {
                int ii;

                if (texturefx1.tileImage == 0) {
                    ii = this.terrainTextureId;
                } else {
                    ii = this.guiItemsTextureId;
                }

                Dimension dim = this.getTextureDimensions(ii);

                if (dim == null) {
                    throw new IllegalArgumentException("Unknown dimensions for texture id: " + ii);
                }

                int tileWidth = dim.getWidth() / 16;
                int tileHeight = dim.getHeight() / 16;

                this.checkImageDataSize(dim.getWidth());
                this.imageData.limit(0);
                boolean customOk = this.updateCustomTexture(texturefx1, this.imageData, dim.getWidth() / 16);

                if (!customOk || this.imageData.limit() > 0) {
                    boolean fastColor;

                    if (this.imageData.limit() <= 0) {
                        fastColor = this.updateDefaultTexture(texturefx1, this.imageData, dim.getWidth() / 16);
                        if (fastColor && this.imageData.limit() <= 0) {
                            continue;
                        }
                    }

                    if (this.imageData.limit() <= 0) {
                        texturefx1.onTick();
                        if (texturefx1.imageData == null) {
                            continue;
                        }

                        int j = tileWidth * tileHeight * 4;

                        if (texturefx1.imageData.length == j) {
                            this.imageData.clear();
                            this.imageData.put(texturefx1.imageData);
                            this.imageData.position(0).limit(texturefx1.imageData.length);
                        } else {
                            this.copyScaled(texturefx1.imageData, this.imageData, tileWidth);
                        }
                    }

                    texturefx1.bindImage(this);
                    fastColor = this.scalesWithFastColor(texturefx1);

                    for (int ix = 0; ix < texturefx1.tileSize; ++ix) {
                        for (int iy = 0; iy < texturefx1.tileSize; ++iy) {
                            int xOffset = texturefx1.iconIndex % 16 * tileWidth + ix * tileWidth;
                            int yOffset = texturefx1.iconIndex / 16 * tileHeight + iy * tileHeight;

                            GL11.glTexSubImage2D(3553, 0, xOffset, yOffset, tileWidth, tileHeight, 6408, 5121, this.imageData);
                            if (RenderEngine.useMipmaps && ix == 0 && iy == 0) {
                                this.generateMipMapsSub(xOffset, yOffset, tileWidth, tileHeight, this.imageData, texturefx1.tileSize, fastColor);
                            }
                        }
                    }
                }
            }
        }

        this.dynamicTexturesUpdated = true;

        for (i = 0; i < this.textureList.size(); ++i) {
            texturefx1 = (TextureFX) this.textureList.get(i);
            if (texturefx1.textureId > 0) {
                this.imageData.clear();
                this.imageData.put(texturefx1.imageData);
                this.imageData.position(0).limit(texturefx1.imageData.length);
                GL11.glBindTexture(3553, texturefx1.textureId);
                GL11.glTexSubImage2D(3553, 0, 0, 0, 16, 16, 6408, 5121, this.imageData);
                if (RenderEngine.useMipmaps) {
                    this.generateMipMapsSub(0, 0, 16, 16, this.imageData, texturefx1.tileSize, false);
                }
            }
        }

    }
    
    public boolean updateCustomTexture(TextureFX texturefx, ByteBuffer imgData, int tileWidth) {
        return texturefx.iconIndex == Block.waterStill.blockIndexInTexture ? 
                (Config.isGeneratedWater() ? false : 
                this.updateCustomTexture(texturefx, "/custom_water_still.png", imgData, tileWidth, Config.isAnimatedWater(), 1)) : 
                (texturefx.iconIndex == Block.waterStill.blockIndexInTexture + 1 ? (Config.isGeneratedWater() ? false : 
                this.updateCustomTexture(texturefx, "/custom_water_flowing.png", imgData, tileWidth, Config.isAnimatedWater(), 1)) : 
                (texturefx.iconIndex == Block.lavaStill.blockIndexInTexture ? (Config.isGeneratedLava() ? false : 
                this.updateCustomTexture(texturefx, "/custom_lava_still.png", imgData, tileWidth, Config.isAnimatedLava(), 1)) : 
                (texturefx.iconIndex == Block.lavaStill.blockIndexInTexture + 1 ? (Config.isGeneratedLava() ? false : 
                this.updateCustomTexture(texturefx, "/custom_lava_flowing.png", imgData, tileWidth, Config.isAnimatedLava(), 1)) : 
                (texturefx.iconIndex == Block.portal.blockIndexInTexture ? this.updateCustomTexture(texturefx, "/custom_portal.png", imgData, tileWidth, Config.isAnimatedPortal(), 1) : 
                (texturefx.iconIndex == Block.fire.blockIndexInTexture ? this.updateCustomTexture(texturefx, "/custom_fire_n_s.png", imgData, tileWidth, Config.isAnimatedFire(), 1) : 
                (texturefx.iconIndex == Block.fire.blockIndexInTexture + 16 ? this.updateCustomTexture(texturefx, "/custom_fire_e_w.png", imgData, tileWidth, Config.isAnimatedFire(), 1) : 
                false))))));
    }
    
    private boolean updateDefaultTexture(TextureFX texturefx, ByteBuffer imgData, int tileWidth) {
        return this.texturePack.selectedTexturePack instanceof TexturePackDefault ? false : 
                (texturefx.iconIndex == Block.waterStill.blockIndexInTexture ? (Config.isGeneratedWater() ? false : 
                this.updateDefaultTexture(texturefx, imgData, tileWidth, false, 1)) : 
                (texturefx.iconIndex == Block.waterStill.blockIndexInTexture + 1 ? (Config.isGeneratedWater() ? false : 
                this.updateDefaultTexture(texturefx, imgData, tileWidth, Config.isAnimatedWater(), 1)) : 
                (texturefx.iconIndex == Block.lavaStill.blockIndexInTexture ? (Config.isGeneratedLava() ? false : 
                this.updateDefaultTexture(texturefx, imgData, tileWidth, false, 1)) : 
                (texturefx.iconIndex == Block.lavaStill.blockIndexInTexture + 1 ? (Config.isGeneratedLava() ? false : 
                this.updateDefaultTexture(texturefx, imgData, tileWidth, Config.isAnimatedLava(), 3)) : false))));
    }
    
    private boolean updateCustomTexture(TextureFX texturefx, String imagePath, ByteBuffer imgData, int tileWidth, boolean animated, int animDiv) {
        byte[] imageBytes = this.getCustomTextureData(imagePath, tileWidth);

        if (imageBytes == null) {
            return false;
        } else if (!animated && this.dynamicTexturesUpdated) {
            return true;
        } else {
            int imgLen = tileWidth * tileWidth * 4;
            int imgCount = imageBytes.length / imgLen;
            int imgNum = this.tickCounter / animDiv % imgCount;
            int offset = 0;

            if (animated) {
                offset = imgLen * imgNum;
            }

            imgData.clear();
            imgData.put(imageBytes, offset, imgLen);
            imgData.position(0).limit(imgLen);
            return true;
        }
    }
    
    private boolean updateDefaultTexture(TextureFX texturefx, ByteBuffer imgData, int tileWidth, boolean scrolling, int scrollDiv) {
        int iconIndex = texturefx.iconIndex;

        if (!scrolling && this.dynamicTexturesUpdated) {
            return true;
        } else {
            byte[] tileData = this.getTerrainIconData(iconIndex, tileWidth);

            if (tileData == null) {
                return false;
            } else {
                imgData.clear();
                int imgLen = tileData.length;

                if (scrolling) {
                    int movNum = tileWidth - this.tickCounter / scrollDiv % tileWidth;
                    int offset = movNum * tileWidth * 4;

                    imgData.put(tileData, offset, imgLen - offset);
                    imgData.put(tileData, 0, offset);
                } else {
                    imgData.put(tileData, 0, imgLen);
                }

                imgData.position(0).limit(imgLen);
                return true;
            }
        }
    }

    private byte[] getTerrainIconData(int tileNum, int tileWidth) {
        String tileIdStr = "Tile-" + tileNum;
        byte[] tileData = this.getCustomTextureData(tileIdStr, tileWidth);

        if (tileData != null) {
            return tileData;
        } else {
            byte[] terrainData = this.getCustomTextureData("/assets/terrain.png", tileWidth * 16);

            if (terrainData == null) {
                return null;
            } else {
                tileData = new byte[tileWidth * tileWidth * 4];
                int tx = tileNum % 16;
                int ty = tileNum / 16;
                int xMin = tx * tileWidth;
                int yMin = ty * tileWidth;
                int i = xMin + tileWidth;

                i = yMin + tileWidth;

                for (int y = 0; y < tileWidth; ++y) {
                    int ys = yMin + y;

                    for (int x = 0; x < tileWidth; ++x) {
                        int xs = xMin + x;
                        int posSrc = 4 * (xs + ys * tileWidth * 16);
                        int posDst = 4 * (x + y * tileWidth);

                        tileData[posDst + 0] = terrainData[posSrc + 0];
                        tileData[posDst + 1] = terrainData[posSrc + 1];
                        tileData[posDst + 2] = terrainData[posSrc + 2];
                        tileData[posDst + 3] = terrainData[posSrc + 3];
                    }
                }

                this.setCustomTextureData(tileIdStr, tileData);
                return tileData;
            }
        }
    }
    private void setCustomTextureData(String imagePath, byte[] data) {
        this.textureDataMap.put(imagePath, data);
    }
    
    public byte[] getCustomTextureData(String imagePath, int tileWidth) {
        byte[] imageBytes = (byte[]) ((byte[]) this.textureDataMap.get(imagePath));

        if (imageBytes == null) {
            if (this.textureDataMap.containsKey(imagePath)) {
                return null;
            }

            imageBytes = this.loadImage(imagePath, tileWidth);
            this.textureDataMap.put(imagePath, imageBytes);
        }

        return imageBytes;
    }
    
    private byte[] loadImage(String name, int targetWidth) {
        try {
            TexturePackBase e = this.texturePack.selectedTexturePack;

            if (e == null) {
                return null;
            } else {
                InputStream in = e.getResourceAsStream(name);

                if (in == null) {
                    return null;
                } else {
                    BufferedImage image = this.readTextureImage(in);

                    if (image == null) {
                        return null;
                    } else {
                        if (targetWidth > 0 && image.getWidth() != targetWidth) {
                            double width = (double) (image.getHeight() / image.getWidth());
                            int ai = (int) ((double) targetWidth * width);

                            image = scaleBufferedImage(image, targetWidth, ai);
                        }

                        int i = image.getWidth();
                        int height = image.getHeight();
                        int[] aint = new int[i * height];
                        byte[] byteBuf = new byte[i * height * 4];

                        image.getRGB(0, 0, i, height, aint, 0, i);

                        for (int l = 0; l < aint.length; ++l) {
                            int alpha = aint[l] >> 24 & 255;
                            int red = aint[l] >> 16 & 255;
                            int green = aint[l] >> 8 & 255;
                            int blue = aint[l] & 255;

                            if (this.options != null && this.options.anaglyph) {
                                int j3 = (red * 30 + green * 59 + blue * 11) / 100;
                                int l3 = (red * 30 + green * 70) / 100;
                                int j4 = (red * 30 + blue * 70) / 100;

                                red = j3;
                                green = l3;
                                blue = j4;
                            }

                            byteBuf[l * 4 + 0] = (byte) red;
                            byteBuf[l * 4 + 1] = (byte) green;
                            byteBuf[l * 4 + 2] = (byte) blue;
                            byteBuf[l * 4 + 3] = (byte) alpha;
                        }

                        return byteBuf;
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }
    
    public static BufferedImage scaleBufferedImage(BufferedImage image, int width, int height) {
        BufferedImage scaledImage = new BufferedImage(width, height, 2);
        Graphics2D gr = scaledImage.createGraphics();

        gr.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        gr.drawImage(image, 0, 0, width, height, (ImageObserver) null);
        return scaledImage;
    }
    
    private Dimension getTextureDimensions(int id) {
        return (Dimension) this.textureDimensionsMap.get(new Integer(id));
    }
    
    private boolean scalesWithFastColor(TextureFX texturefx) {
        return !texturefx.getClass().getName().equals("ModTextureStatic");
    }
    
    private void copyScaled(byte[] buf, ByteBuffer dstBuf, int dstWidth) {
        int srcWidth = (int) Math.sqrt((double) (buf.length / 4));
        int scale = dstWidth / srcWidth;
        byte[] buf4 = new byte[4];

        dstBuf.clear();
        if (scale > 1) {
            for (int y = 0; y < srcWidth; ++y) {
                int yMul = y * srcWidth;
                int ty = y * scale;
                int tyMul = ty * dstWidth;

                for (int x = 0; x < srcWidth; ++x) {
                    int srcPos = (x + yMul) * 4;

                    buf4[0] = buf[srcPos];
                    buf4[1] = buf[srcPos + 1];
                    buf4[2] = buf[srcPos + 2];
                    buf4[3] = buf[srcPos + 3];
                    int tx = x * scale;
                    int dstPosBase = tx + tyMul;

                    for (int tdy = 0; tdy < scale; ++tdy) {
                        int dstPosY = dstPosBase + tdy * dstWidth;

                        dstBuf.position(dstPosY * 4);

                        for (int tdx = 0; tdx < scale; ++tdx) {
                            dstBuf.put(buf4);
                        }
                    }
                }
            }
        }

        dstBuf.position(0).limit(dstWidth * dstWidth * 4);
    }
    
    private void generateMipMapsSub(int xOffset, int yOffset, int width, int height, ByteBuffer data, int numTiles, boolean fastColor) {
        ByteBuffer parMipData = data;

        for (int level = 1; level <= 16; ++level) {
            int parWidth = width >> level - 1;
            int mipWidth = width >> level;
            int mipHeight = height >> level;
            int xMipOffset = xOffset >> level;
            int yMipOffset = yOffset >> level;

            if (mipWidth <= 0 || mipHeight <= 0) {
                break;
            }

            ByteBuffer mipData = this.mipImageDatas[level - 1];

            int ix;
            int iy;
            int dx;
            int dy;

            for (ix = 0; ix < mipWidth; ++ix) {
                for (iy = 0; iy < mipHeight; ++iy) {
                    dx = parMipData.getInt((ix * 2 + 0 + (iy * 2 + 0) * parWidth) * 4);
                    dy = parMipData.getInt((ix * 2 + 1 + (iy * 2 + 0) * parWidth) * 4);
                    int p3 = parMipData.getInt((ix * 2 + 1 + (iy * 2 + 1) * parWidth) * 4);
                    int p4 = parMipData.getInt((ix * 2 + 0 + (iy * 2 + 1) * parWidth) * 4);
                    int pixel;

                    if (fastColor) {
                        pixel = this.averageColor(this.averageColor(dx, dy), this.averageColor(p3, p4));
                    } else {
                        pixel = this.weightedAverageColor(dx, dy, p3, p4);
                    }

                    mipData.putInt((ix + iy * mipWidth) * 4, pixel);
                }
            }

            for (ix = 0; ix < numTiles; ++ix) {
                for (iy = 0; iy < numTiles; ++iy) {
                    dx = ix * mipWidth;
                    dy = iy * mipHeight;
                    GL11.glTexSubImage2D(3553, level, xMipOffset + dx, yMipOffset + dy, mipWidth, mipHeight, 6408, 5121, mipData);
                }
            }

            parMipData = mipData;
        }

    }

   private int averageColor(int var1, int var2) {
      int var3 = (var1 & -16777216) >> 24 & 255;
      int var4 = (var2 & -16777216) >> 24 & 255;
      return (var3 + var4 >> 1 << 24) + ((var1 & 16711422) + (var2 & 16711422) >> 1);
   }

   public void refreshTextures() {
      TexturePackBase var1 = this.texturePack.selectedTexturePack;
      Iterator var2 = this.textureNameToImageMap.keySet().iterator();

      BufferedImage var4;
      while(var2.hasNext()) {
         int var3 = ((Integer)var2.next());
         var4 = (BufferedImage)this.textureNameToImageMap.get(var3);
         this.setupTexture(var4, var3);
      }

      ThreadDownloadImageData var8;
      for(var2 = this.urlToImageDataMap.values().iterator(); var2.hasNext(); var8.textureSetupComplete = false) {
         var8 = (ThreadDownloadImageData)var2.next();
      }

      var2 = this.textureMap.keySet().iterator();

      String var9;
      while(var2.hasNext()) {
         var9 = (String)var2.next();

         try {
            if(var9.startsWith("##")) {
               var4 = this.unwrapImageByColumns(this.readTextureImage(var1.getResourceAsStream(var9.substring(2))));
            } else if(var9.startsWith("%clamp%")) {
               this.clampTexture = true;
               var4 = this.readTextureImage(var1.getResourceAsStream(var9.substring(7)));
            } else if(var9.startsWith("%blur%")) {
               this.blurTexture = true;
               var4 = this.readTextureImage(var1.getResourceAsStream(var9.substring(6)));
            } else {
               var4 = this.readTextureImage(var1.getResourceAsStream(var9));
            }

            int var5 = ((Integer)this.textureMap.get(var9)).intValue();
            this.setupTexture(var4, var5);
            this.blurTexture = false;
            this.clampTexture = false;
         } catch (IOException var7) {
            var7.printStackTrace();
         }
      }

      var2 = this.field_28151_c.keySet().iterator();

      while(var2.hasNext()) {
         var9 = (String)var2.next();

         try {
            if(var9.startsWith("##")) {
               var4 = this.unwrapImageByColumns(this.readTextureImage(var1.getResourceAsStream(var9.substring(2))));
            } else if(var9.startsWith("%clamp%")) {
               this.clampTexture = true;
               var4 = this.readTextureImage(var1.getResourceAsStream(var9.substring(7)));
            } else if(var9.startsWith("%blur%")) {
               this.blurTexture = true;
               var4 = this.readTextureImage(var1.getResourceAsStream(var9.substring(6)));
            } else {
               var4 = this.readTextureImage(var1.getResourceAsStream(var9));
            }

            this.getImageContents(var4, (int[])this.field_28151_c.get(var9));
            this.blurTexture = false;
            this.clampTexture = false;
         } catch (IOException var6) {
            var6.printStackTrace();
         }
      }

   }

   private BufferedImage readTextureImage(InputStream var1) throws IOException {
      BufferedImage var2 = ImageIO.read(var1);
      var1.close();
      return var2;
   }

   public void bindTexture(int var1) {
      if(var1 >= 0) {
         GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, var1);
      }
   }

}
