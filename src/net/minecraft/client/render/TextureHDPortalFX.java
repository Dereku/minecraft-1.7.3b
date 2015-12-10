package net.minecraft.client.render;

import java.util.Random;
import net.minecraft.client.block.Block;
import net.minecraft.src.Config;
import net.minecraft.src.MathHelper;
import net.minecraft.src.TextureFX;
import net.minecraft.src.TexturePackBase;

public class TextureHDPortalFX extends TextureFX implements TextureHDFX {

    private int tileWidth = 0;
    private int tickCounter;
    private byte[][] buffer;

    public TextureHDPortalFX() {
        super(Block.portal.blockIndexInTexture);
        this.tileWidth = 16;
        this.tickCounter = 0;
        this.setup();
    }

    public void setTileWidth(int tileWidth) {
        if (tileWidth > Config.getMaxDynamicTileWidth()) {
            tileWidth = Config.getMaxDynamicTileWidth();
        }

        this.tileWidth = tileWidth;
        this.setup();
        this.tickCounter = 0;
    }

    public void setTexturePackBase(TexturePackBase tpb) {}

    private void setup() {
        this.imageData = new byte[this.tileWidth * this.tileWidth * 4];
        this.buffer = new byte[32][this.tileWidth * this.tileWidth * 4];
        Random random = new Random(100L);

        for (int i = 0; i < 32; ++i) {
            for (int x = 0; x < this.tileWidth; ++x) {
                for (int y = 0; y < this.tileWidth; ++y) {
                    float f = 0.0F;

                    int i1;

                    for (i1 = 0; i1 < 2; ++i1) {
                        float j1 = (float) (i1 * (this.tileWidth / 2));
                        float k1 = (float) (i1 * (this.tileWidth / 2));
                        float l1 = ((float) x - j1) / (float) this.tileWidth * 2.0F;
                        float pos = ((float) y - k1) / (float) this.tileWidth * 2.0F;

                        if (l1 < -1.0F) {
                            l1 += 2.0F;
                        }

                        if (l1 >= 1.0F) {
                            l1 -= 2.0F;
                        }

                        if (pos < -1.0F) {
                            pos += 2.0F;
                        }

                        if (pos >= 1.0F) {
                            pos -= 2.0F;
                        }

                        float f5 = l1 * l1 + pos * pos;
                        float f6 = (float) Math.atan2((double) pos, (double) l1) + ((float) i / 32.0F * 3.141593F * 2.0F - f5 * 10.0F + (float) (i1 * 2)) * (float) (i1 * 2 - 1);

                        f6 = (MathHelper.sin(f6) + 1.0F) / 2.0F;
                        f6 /= f5 + 1.0F;
                        f += f6 * 0.5F;
                    }

                    f += random.nextFloat() * 0.1F;
                    i1 = (int) (f * 100.0F + 155.0F);
                    int ii = (int) (f * f * 200.0F + 55.0F);
                    int j = (int) (f * f * f * f * 255.0F);
                    int k = (int) (f * 100.0F + 155.0F);
                    int l = y * this.tileWidth + x;

                    this.buffer[i][l * 4 + 0] = (byte) ii;
                    this.buffer[i][l * 4 + 1] = (byte) j;
                    this.buffer[i][l * 4 + 2] = (byte) i1;
                    this.buffer[i][l * 4 + 3] = (byte) k;
                }
            }
        }

    }

    public void onTick() {
        if (!Config.isAnimatedPortal()) {
            this.imageData = null;
        }

        if (this.imageData != null) {
            ++this.tickCounter;
            byte[] abyte0 = this.buffer[this.tickCounter & 31];

            for (int i = 0; i < this.tileWidth * this.tileWidth; ++i) {
                int j = abyte0[i * 4 + 0] & 255;
                int k = abyte0[i * 4 + 1] & 255;
                int l = abyte0[i * 4 + 2] & 255;
                int i1 = abyte0[i * 4 + 3] & 255;

                if (this.anaglyphEnabled) {
                    int j1 = (j * 30 + k * 59 + l * 11) / 100;
                    int k1 = (j * 30 + k * 70) / 100;
                    int l1 = (j * 30 + l * 70) / 100;

                    j = j1;
                    k = k1;
                    l = l1;
                }

                this.imageData[i * 4 + 0] = (byte) j;
                this.imageData[i * 4 + 1] = (byte) k;
                this.imageData[i * 4 + 2] = (byte) l;
                this.imageData[i * 4 + 3] = (byte) i1;
            }

        }
    }
}
