package net.minecraft.client.render;

import java.awt.Color;
import java.awt.Dimension;
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
import net.minecraft.client.texture.TextureFX;
import net.minecraft.client.texture.TexturePackBase;
import net.minecraft.client.texture.TexturePackDefault;
import net.minecraft.client.texture.TexturePackList;
import net.minecraft.src.ThreadDownloadImageData;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

public class RenderEngine {

    public static boolean useMipmaps = false;
    private final HashMap<String, Integer> textureMap = new HashMap<>();
    private final HashMap<String, int[]> texturesCache = new HashMap<>();
    private final HashMap<Integer, BufferedImage> textureNameToImageMap = new HashMap<>();
    private final HashMap<Integer, Dimension> textureDimensionsMap = new HashMap<>();
    private final HashMap<String, byte[]> textureDataMap = new HashMap<>();
    private final ArrayList<TextureFX> textureList = new ArrayList<>();
    private final IntBuffer singleIntBuffer = GLAllocation.createDirectIntBuffer(1);
    private final Map urlToImageDataMap;
    private final GameSettings options;
    private final TexturePackList texturePack;
    private final BufferedImage missingTextureImage;
    private ByteBuffer imageData;
    private boolean clampTexture;
    private boolean blurTexture;
    private int terrainTextureId = -1;
    private int guiItemsTextureId = -1;
    private boolean hdTexturesInstalled = false;
    private int tickCounter = 0;
    private ByteBuffer[] mipImageDatas;
    private boolean dynamicTexturesUpdated = false;

    public RenderEngine(TexturePackList texturepacklist, GameSettings gamesettings) {
        this.allocateImageData(256);
        this.urlToImageDataMap = new HashMap();
        this.clampTexture = false;
        this.blurTexture = false;
        this.missingTextureImage = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        this.texturePack = texturepacklist;
        this.options = gamesettings;
        Graphics g = this.missingTextureImage.getGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 64, 64);
        g.setColor(Color.BLACK);
        g.drawString("Missing", 15, 15);
        g.dispose();
    }

    public int[] getTextureRGBArray(String path) {
        TexturePackBase texturepackbase = this.texturePack.selectedTexturePack;
        int[] texture = this.texturesCache.get(path);
        if (texture != null) {
            return texture;
        } else {
            try {
                if (path.startsWith("##")) {
                    texture = this.getRGBArray(this.unwrapImageByColumns(this.readTextureImage(texturepackbase.getResourceAsStream(path.substring(2)))));
                } else if (path.startsWith("%clamp%")) {
                    this.clampTexture = true;
                    texture = this.getRGBArray(this.readTextureImage(texturepackbase.getResourceAsStream(path.substring(7))));
                    this.clampTexture = false;
                } else if (path.startsWith("%blur%")) {
                    this.blurTexture = true;
                    texture = this.getRGBArray(this.readTextureImage(texturepackbase.getResourceAsStream(path.substring(6))));
                    this.blurTexture = false;
                } else {
                    InputStream inputstream = texturepackbase.getResourceAsStream(path);

                    if (inputstream == null) {
                        texture = this.getRGBArray(this.missingTextureImage);
                    } else {
                        texture = this.getRGBArray(this.readTextureImage(inputstream));
                    }
                }

                this.texturesCache.put(path, texture);
                return texture;
            } catch (IOException ioexception) {
                ioexception.printStackTrace();
                texture = this.getRGBArray(this.missingTextureImage);
                this.texturesCache.put(path, texture);
                return texture;
            }
        }
    }

    private int[] getRGBArray(BufferedImage bufferedimage) {
        int i = bufferedimage.getWidth();
        int j = bufferedimage.getHeight();
        int[] ai = new int[i * j];

        bufferedimage.getRGB(0, 0, i, j, ai, 0, i);
        return ai;
    }

    private int[] fillRGBArray(BufferedImage bufferedimage, int[] ai) {
        int i = bufferedimage.getWidth();
        int j = bufferedimage.getHeight();

        bufferedimage.getRGB(0, 0, i, j, ai, 0, i);
        return ai;
    }

    public int getTexture(String path) {
        TexturePackBase texturepackbase = this.texturePack.selectedTexturePack;
        Integer id = this.textureMap.get(path);

        if (id != null) {
            return id;
        } else {
            try {
                this.singleIntBuffer.clear();
                GLAllocation.generateTextureNames(this.singleIntBuffer);
                id = this.singleIntBuffer.get(0);
                if (path.startsWith("##")) {
                    this.setupTexture(this.unwrapImageByColumns(this.readTextureImage(texturepackbase.getResourceAsStream(path.substring(2)))), id);
                } else if (path.startsWith("%clamp%")) {
                    this.clampTexture = true;
                    this.setupTexture(this.readTextureImage(texturepackbase.getResourceAsStream(path.substring(7))), id);
                    this.clampTexture = false;
                } else if (path.startsWith("%blur%")) {
                    this.blurTexture = true;
                    this.setupTexture(this.readTextureImage(texturepackbase.getResourceAsStream(path.substring(6))), id);
                    this.blurTexture = false;
                } else {
                    InputStream inputstream = texturepackbase.getResourceAsStream(path);

                    if (inputstream == null) {
                        this.setupTexture(this.missingTextureImage, id);
                    } else {
                        if (path.equals("/assets/terrain.png")) {
                            this.terrainTextureId = id;
                        }

                        if (path.equals("/assets/gui/items.png")) {
                            this.guiItemsTextureId = id;
                        }

                        this.setupTexture(this.readTextureImage(inputstream), id);
                    }
                }

                this.textureMap.put(path, id);
                return id;
            } catch (IOException ioexception) {
                ioexception.printStackTrace();
                GLAllocation.generateTextureNames(this.singleIntBuffer);
                id = this.singleIntBuffer.get(0);
                this.setupTexture(this.missingTextureImage, id);
                this.textureMap.put(path, id);
                return id;
            }
        }
    }

    private BufferedImage unwrapImageByColumns(BufferedImage bufferedimage) {
        int i = bufferedimage.getWidth() / 16;
        BufferedImage bufferedimage1 = new BufferedImage(16, bufferedimage.getHeight() * i, 2);
        Graphics g = bufferedimage1.getGraphics();

        for (int j = 0; j < i; ++j) {
            g.drawImage(bufferedimage, -j * 16, j * bufferedimage.getHeight(), (ImageObserver) null);
        }

        g.dispose();
        return bufferedimage1;
    }

    public int allocateAndSetupTexture(BufferedImage bufferedimage) {
        this.singleIntBuffer.clear();
        GLAllocation.generateTextureNames(this.singleIntBuffer);
        int i = this.singleIntBuffer.get(0);
        this.setupTexture(bufferedimage, i);
        this.textureNameToImageMap.put(i, bufferedimage);
        return i;
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

    public void func_28150_a(int[] ai, int i, int j, int k) {
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

    public void deleteTexture(int i) {
        this.textureNameToImageMap.remove(i);
        this.singleIntBuffer.clear();
        this.singleIntBuffer.put(i);
        this.singleIntBuffer.flip();
        GL11.glDeleteTextures(this.singleIntBuffer);
    }

    public int getTextureForDownloadableImage(String s, String s1) {
        ThreadDownloadImageData threaddownloadimagedata = (ThreadDownloadImageData) this.urlToImageDataMap.get(s);

        if (threaddownloadimagedata != null && threaddownloadimagedata.image != null && !threaddownloadimagedata.textureSetupComplete) {
            if (threaddownloadimagedata.textureName < 0) {
                threaddownloadimagedata.textureName = this.allocateAndSetupTexture(threaddownloadimagedata.image);
            } else {
                this.setupTexture(threaddownloadimagedata.image, threaddownloadimagedata.textureName);
            }

            threaddownloadimagedata.textureSetupComplete = true;
        }

        return threaddownloadimagedata != null && threaddownloadimagedata.textureName >= 0 ? threaddownloadimagedata.textureName : (s1 == null ? -1 : this.getTexture(s1));
    }

    public ThreadDownloadImageData obtainImageData(String s, ImageBuffer imagebuffer) {
        ThreadDownloadImageData threaddownloadimagedata = (ThreadDownloadImageData) this.urlToImageDataMap.get(s);

        if (threaddownloadimagedata == null) {
            this.urlToImageDataMap.put(s, new ThreadDownloadImageData(s, imagebuffer));
        } else {
            ++threaddownloadimagedata.referenceCount;
        }

        return threaddownloadimagedata;
    }

    public void releaseImageData(String s) {
        ThreadDownloadImageData threaddownloadimagedata = (ThreadDownloadImageData) this.urlToImageDataMap.get(s);

        if (threaddownloadimagedata != null) {
            --threaddownloadimagedata.referenceCount;
            if (threaddownloadimagedata.referenceCount == 0) {
                if (threaddownloadimagedata.textureName >= 0) {
                    this.deleteTexture(threaddownloadimagedata.textureName);
                }

                this.urlToImageDataMap.remove(s);
            }
        }

    }

    public void registerTextureFX(TextureFX texturefx) {
        for (int i = 0; i < this.textureList.size(); ++i) {
            TextureFX tex = this.textureList.get(i);

            if (tex.tileImage == texturefx.tileImage && tex.iconIndex == texturefx.iconIndex) {
                this.textureList.remove(i);
                --i;
            }
        }

        this.textureList.add(texturefx);
        texturefx.onTick();
        this.dynamicTexturesUpdated = false;
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

    public void updateDynamicTextures() {
        this.checkHdTextures();
        ++this.tickCounter;
        this.terrainTextureId = this.getTexture(Minecraft.TERRAIN_TEXTURE);
        this.guiItemsTextureId = this.getTexture("/assets/gui/items.png");

        int i;
        TextureFX texturefx1;

        for (i = 0; i < this.textureList.size(); ++i) {
            texturefx1 = this.textureList.get(i);
            texturefx1.anaglyphEnabled = this.options.anaglyph;
            if (!texturefx1.getClass().getName().equals("ModTextureStatic") || !this.dynamicTexturesUpdated) {
                boolean tid = false;
                int ii;

                if (texturefx1.tileImage == 0) {
                    ii = this.terrainTextureId;
                } else {
                    ii = this.guiItemsTextureId;
                }

                Dimension dim = this.getTextureDimensions(ii);

                if (dim == null) {
                    throw new IllegalArgumentException("Unknown dimensions for texture id: " + i);
                }

                int tileWidth = dim.width / 16;
                int tileHeight = dim.height / 16;

                this.checkImageDataSize(dim.width);
                this.imageData.limit(0);
                boolean customOk = this.updateCustomTexture(texturefx1, this.imageData, dim.width / 16);

                if (!customOk || this.imageData.limit() > 0) {
                    boolean fastColor;

                    if (this.imageData.limit() <= 0) {
                        fastColor = this.updateDefaultTexture(texturefx1, this.imageData, dim.width / 16);
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
            texturefx1 = this.textureList.get(i);
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

    private int averageColor(int i, int j) {
        int k = (i & -16777216) >> 24 & 255;
        int l = (j & -16777216) >> 24 & 255;

        return (k + l >> 1 << 24) + ((i & 16711422) + (j & 16711422) >> 1);
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

    public void refreshTextures() {
        this.textureDataMap.clear();
        this.dynamicTexturesUpdated = false;
        Config.setFontRendererUpdated(false);
        TexturePackBase texturepackbase = this.texturePack.selectedTexturePack;
        for (int i : this.textureNameToImageMap.keySet()) {
            BufferedImage bufferedimage = this.textureNameToImageMap.get(i);
            this.setupTexture(bufferedimage, i);
        }

        ThreadDownloadImageData s1;
        Iterator iterator3;

        for (iterator3 = this.urlToImageDataMap.values().iterator(); iterator3.hasNext(); s1.textureSetupComplete = false) {
            s1 = (ThreadDownloadImageData) iterator3.next();
        }

        iterator3 = this.textureMap.keySet().iterator();

        BufferedImage bufferedImage;
        String s11;

        while (iterator3.hasNext()) {
            s11 = (String) iterator3.next();

            try {
                if (s11.startsWith("##")) {
                    bufferedImage = this.unwrapImageByColumns(this.readTextureImage(texturepackbase.getResourceAsStream(s11.substring(2))));
                } else if (s11.startsWith("%clamp%")) {
                    this.clampTexture = true;
                    bufferedImage = this.readTextureImage(texturepackbase.getResourceAsStream(s11.substring(7)));
                } else if (s11.startsWith("%blur%")) {
                    this.blurTexture = true;
                    bufferedImage = this.readTextureImage(texturepackbase.getResourceAsStream(s11.substring(6)));
                } else {
                    bufferedImage = this.readTextureImage(texturepackbase.getResourceAsStream(s11));
                }

                if (bufferedImage == null) {
                    break;
                }
                int j = this.textureMap.get(s11);

                this.setupTexture(bufferedImage, j);
                this.blurTexture = false;
                this.clampTexture = false;
            } catch (IOException ioexception) {
                ioexception.printStackTrace();
            }
        }

        iterator3 = this.texturesCache.keySet().iterator();

        while (iterator3.hasNext()) {
            s11 = (String) iterator3.next();

            try {
                if (s11.startsWith("##")) {
                    bufferedImage = this.unwrapImageByColumns(this.readTextureImage(texturepackbase.getResourceAsStream(s11.substring(2))));
                } else if (s11.startsWith("%clamp%")) {
                    this.clampTexture = true;
                    bufferedImage = this.readTextureImage(texturepackbase.getResourceAsStream(s11.substring(7)));
                } else if (s11.startsWith("%blur%")) {
                    this.blurTexture = true;
                    bufferedImage = this.readTextureImage(texturepackbase.getResourceAsStream(s11.substring(6)));
                } else {
                    bufferedImage = this.readTextureImage(texturepackbase.getResourceAsStream(s11));
                }

                this.fillRGBArray(bufferedImage, (int[]) ((int[]) this.texturesCache.get(s11)));
                this.blurTexture = false;
                this.clampTexture = false;
            } catch (IOException ioexception2) {
                ioexception2.printStackTrace();
            }
        }

    }

    private BufferedImage readTextureImage(InputStream inputstream) throws IOException {
        if (inputstream == null) {
            return null;
        }

        BufferedImage bufferedimage = ImageIO.read(inputstream);

        inputstream.close();
        return bufferedimage;
    }

    public void bindTexture(int i) {
        if (i >= 0) {
            GL11.glBindTexture(3553, i);
        }
    }

    private void setTextureDimension(int id, Dimension dim) {
        this.textureDimensionsMap.put(id, dim);
        if (id == this.terrainTextureId) {
            Config.setIconWidthTerrain(dim.width / 16);
            this.updateDinamicTextures(0, dim);
        }

        if (id == this.guiItemsTextureId) {
            Config.setIconWidthItems(dim.width / 16);
            this.updateDinamicTextures(1, dim);
        }

    }

    private Dimension getTextureDimensions(int id) {
        return this.textureDimensionsMap.get(id);
    }

    private void updateDinamicTextures(int texNum, Dimension dim) {
        this.checkHdTextures();

        for (int i = 0; i < this.textureList.size(); ++i) {
            TextureFX tex = this.textureList.get(i);

            if (tex.tileImage == texNum && tex instanceof TextureHDFX) {
                TextureHDFX texHD = (TextureHDFX) tex;

                texHD.setTexturePackBase(this.texturePack.selectedTexturePack);
                texHD.setTileWidth(dim.width / 16);
            }
        }

    }

    public boolean updateCustomTexture(TextureFX texturefx, ByteBuffer imgData, int tileWidth) {
        return texturefx.iconIndex == Block.waterStill.blockIndexInTexture
                ? (Config.isGeneratedWater() ? false
                        : this.updateCustomTexture(texturefx, "/custom_water_still.png", imgData, tileWidth, Config.isAnimatedWater(), 1))
                : (texturefx.iconIndex == Block.waterStill.blockIndexInTexture + 1 ? (Config.isGeneratedWater() ? false
                                : this.updateCustomTexture(texturefx, "/custom_water_flowing.png", imgData, tileWidth, Config.isAnimatedWater(), 1))
                        : (texturefx.iconIndex == Block.lavaStill.blockIndexInTexture ? (Config.isGeneratedLava() ? false
                                        : this.updateCustomTexture(texturefx, "/custom_lava_still.png", imgData, tileWidth, Config.isAnimatedLava(), 1))
                                : (texturefx.iconIndex == Block.lavaStill.blockIndexInTexture + 1 ? (Config.isGeneratedLava() ? false
                                                : this.updateCustomTexture(texturefx, "/custom_lava_flowing.png", imgData, tileWidth, Config.isAnimatedLava(), 1))
                                        : (texturefx.iconIndex == Block.portal.blockIndexInTexture ? this.updateCustomTexture(texturefx, "/custom_portal.png", imgData, tileWidth, Config.isAnimatedPortal(), 1)
                                                : (texturefx.iconIndex == Block.fire.blockIndexInTexture ? this.updateCustomTexture(texturefx, "/custom_fire_n_s.png", imgData, tileWidth, Config.isAnimatedFire(), 1)
                                                        : (texturefx.iconIndex == Block.fire.blockIndexInTexture + 16 ? this.updateCustomTexture(texturefx, "/custom_fire_e_w.png", imgData, tileWidth, Config.isAnimatedFire(), 1)
                                                                : false))))));
    }

    private boolean updateDefaultTexture(TextureFX texturefx, ByteBuffer imgData, int tileWidth) {
        return this.texturePack.selectedTexturePack instanceof TexturePackDefault ? false : (texturefx.iconIndex == Block.waterStill.blockIndexInTexture ? (Config.isGeneratedWater() ? false : this.updateDefaultTexture(texturefx, imgData, tileWidth, false, 1)) : (texturefx.iconIndex == Block.waterStill.blockIndexInTexture + 1 ? (Config.isGeneratedWater() ? false : this.updateDefaultTexture(texturefx, imgData, tileWidth, Config.isAnimatedWater(), 1)) : (texturefx.iconIndex == Block.lavaStill.blockIndexInTexture ? (Config.isGeneratedLava() ? false : this.updateDefaultTexture(texturefx, imgData, tileWidth, false, 1)) : (texturefx.iconIndex == Block.lavaStill.blockIndexInTexture + 1 ? (Config.isGeneratedLava() ? false : this.updateDefaultTexture(texturefx, imgData, tileWidth, Config.isAnimatedLava(), 3)) : false))));
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

    private byte[] getTerrainIconData(int tileNum, int tileWidth) {
        String tileIdStr = "Tile-" + tileNum;
        byte[] tileData = this.getCustomTextureData(tileIdStr, tileWidth);

        if (tileData != null) {
            return tileData;
        } else {
            byte[] terrainData = this.getCustomTextureData(Minecraft.TERRAIN_TEXTURE, tileWidth * 16);

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

    public byte[] getCustomTextureData(String imagePath, int tileWidth) {
        byte[] imageBytes = this.textureDataMap.get(imagePath);

        if (imageBytes == null) {
            if (this.textureDataMap.containsKey(imagePath)) {
                return null;
            }

            imageBytes = this.loadImage(imagePath, tileWidth);
            this.textureDataMap.put(imagePath, imageBytes);
        }

        return imageBytes;
    }

    private void setCustomTextureData(String imagePath, byte[] data) {
        this.textureDataMap.put(imagePath, data);
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

    private void checkImageDataSize(int width) {
        if (this.imageData != null) {
            int len = width * width * 4;

            if (this.imageData.capacity() >= len) {
                return;
            }
        }

        this.allocateImageData(width);
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

    private int getMaxMipmapLevel(int size) {
        int level;

        for (level = 0; size > 0; ++level) {
            size /= 2;
        }

        return level - 1;
    }

    private void copyScaled(byte[] buf, ByteBuffer dstBuf, int dstWidth) {
        int srcWidth = (int) Math.sqrt((double) (buf.length / 4));
        int scale = dstWidth / srcWidth;
        byte[] buf4 = new byte[4];
        int len = dstWidth * dstWidth;

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

    private boolean scalesWithFastColor(TextureFX texturefx) {
        return !texturefx.getClass().getName().equals("ModTextureStatic");
    }
}
