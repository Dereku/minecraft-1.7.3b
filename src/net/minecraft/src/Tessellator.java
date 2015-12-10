package net.minecraft.src;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.Util;

public class Tessellator {

    private static boolean convertQuadsToTriangles = true;
    private static boolean tryVBO = false;
    private ByteBuffer byteBuffer;
    private IntBuffer intBuffer;
    private FloatBuffer floatBuffer;
    private int vertexCount = 0;
    private double textureU;
    private double textureV;
    private int color;
    private boolean hasColor = false;
    private boolean hasTexture = false;
    private boolean hasNormals = false;
    private int rawBufferIndex = 0;
    private int addedVertices = 0;
    private boolean isColorDisabled = false;
    private int drawMode;
    private double xOffset;
    private double yOffset;
    private double zOffset;
    private int normal;
    public static final int BUFFER_SIZE = 262144;
    public static volatile Tessellator instance = new Tessellator(262144);
    private boolean isDrawing = false;
    private boolean useVBO = false;
    private IntBuffer vertexBuffers;
    private int vboIndex = 0;
    private int vboCount = 10;
    private int bufferSize;
    private boolean renderingChunk = false;

    private Tessellator(int i) {
        this.bufferSize = i;
        this.byteBuffer = GLAllocation.createDirectByteBuffer(i * 4);
        this.intBuffer = this.byteBuffer.asIntBuffer();
        this.floatBuffer = this.byteBuffer.asFloatBuffer();
        this.useVBO = Tessellator.tryVBO && GLContext.getCapabilities().GL_ARB_vertex_buffer_object;
        if (this.useVBO) {
            this.vertexBuffers = GLAllocation.createDirectIntBuffer(this.vboCount);
            ARBVertexBufferObject.glGenBuffersARB(this.vertexBuffers);
        }
    }

    public void draw() {
        if (!this.isDrawing) {
            throw new IllegalStateException("Not tesselating!");
        } else {
            this.isDrawing = false;
            if (this.vertexCount > 0) {
                this.byteBuffer.position(0);
                this.byteBuffer.limit(this.rawBufferIndex * 4);
                GL11.glEnableClientState('聸');
                GL11.glEnableClientState('聶');
                GL11.glEnableClientState('聴');
                if (this.useVBO) {
                    this.vboIndex = (this.vboIndex + 1) % this.vboCount;
                    ARBVertexBufferObject.glBindBufferARB('裤', this.vertexBuffers.get(this.vboIndex));
                    ARBVertexBufferObject.glBufferDataARB('裤', this.byteBuffer, '裠');
                    GL11.glTexCoordPointer(2, 5126, 32, 12L);
                    GL11.glColorPointer(4, 5121, 32, 20L);
                    GL11.glVertexPointer(3, 5126, 32, 0L);
                } else {
                    this.floatBuffer.position(3);
                    GL11.glTexCoordPointer(2, 32, this.floatBuffer);
                    this.byteBuffer.position(20);
                    GL11.glColorPointer(4, true, 32, this.byteBuffer);
                    this.floatBuffer.position(0);
                    GL11.glVertexPointer(3, 32, this.floatBuffer);
                }
                if (this.drawMode == 7 && Tessellator.convertQuadsToTriangles) {
                    GL11.glDrawArrays(4, 0, this.vertexCount);
                } else {
                    GL11.glDrawArrays(this.drawMode, 0, this.vertexCount);
                }

                GL11.glDisableClientState('聸');
                GL11.glDisableClientState('聶');
                GL11.glDisableClientState('聴');
            }

            this.reset();
        }
    }

    private void reset() {
        this.vertexCount = 0;
        this.byteBuffer.clear();
        this.rawBufferIndex = 0;
        this.addedVertices = 0;
    }

    public void startDrawingQuads() {
        this.startDrawing(7);
    }

    public void startDrawing(int var1) {
        if (this.isDrawing) {
            throw new IllegalStateException("Already tesselating!");
        } else {
            this.isDrawing = true;
            this.reset();
            this.drawMode = var1;
            this.hasNormals = false;
            this.hasColor = false;
            this.hasTexture = false;
            this.isColorDisabled = false;
        }
    }

    public void setTextureUV(double var1, double var3) {
        this.hasTexture = true;
        this.textureU = var1;
        this.textureV = var3;
        if (!this.renderingChunk) {
            GL11.glTexCoord2f((float) var1, (float) var3);
        }
    }

    public void setColorOpaque_F(float var1, float var2, float var3) {
        this.setColorOpaque((int) (var1 * 255.0F), (int) (var2 * 255.0F), (int) (var3 * 255.0F));
    }

    public void setColorRGBA_F(float var1, float var2, float var3, float var4) {
        this.setColorRGBA((int) (var1 * 255.0F), (int) (var2 * 255.0F), (int) (var3 * 255.0F), (int) (var4 * 255.0F));
    }

    public void setColorOpaque(int var1, int var2, int var3) {
        this.setColorRGBA(var1, var2, var3, 255);
    }

    public void setColorRGBA(int i, int j, int k, int l) {
        if (!this.isColorDisabled) {
            if (i > 255) {
                i = 255;
            }

            if (j > 255) {
                j = 255;
            }

            if (k > 255) {
                k = 255;
            }

            if (l > 255) {
                l = 255;
            }

            if (i < 0) {
                i = 0;
            }

            if (j < 0) {
                j = 0;
            }

            if (k < 0) {
                k = 0;
            }

            if (l < 0) {
                l = 0;
            }

            this.hasColor = true;
            if (!this.renderingChunk) {
                GL11.glColor4ub((byte) i, (byte) j, (byte) k, (byte) l);
            } else if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                this.color = l << 24 | k << 16 | j << 8 | i;
            } else {
                this.color = i << 24 | j << 16 | k << 8 | l;
            }

        }
    }

    public void addVertexWithUV(double var1, double var3, double var5, double var7, double var9) {
        this.setTextureUV(var7, var9);
        this.addVertex(var1, var3, var5);
    }

    public void addVertex(double d, double d1, double d2) {
        if (!this.renderingChunk) {
            GL11.glVertex3f((float) (d + this.xOffset), (float) (d1 + this.yOffset), (float) (d2 + this.zOffset));
        } else {
            ++this.addedVertices;
            if (this.drawMode == 7 && Tessellator.convertQuadsToTriangles && this.addedVertices % 4 == 0) {
                for (int i = 0; i < 2; ++i) {
                    int j = 8 * (3 - i);

                    this.intBuffer.put(this.intBuffer.get(this.rawBufferIndex - j + 0));
                    this.intBuffer.put(this.intBuffer.get(this.rawBufferIndex - j + 1));
                    this.intBuffer.put(this.intBuffer.get(this.rawBufferIndex - j + 2));
                    this.intBuffer.put(this.intBuffer.get(this.rawBufferIndex - j + 3));
                    this.intBuffer.put(this.intBuffer.get(this.rawBufferIndex - j + 4));
                    this.intBuffer.put(this.intBuffer.get(this.rawBufferIndex - j + 5));
                    this.intBuffer.put(0);
                    this.intBuffer.put(0);
                    ++this.vertexCount;
                    this.rawBufferIndex += 8;
                }
            }

            this.intBuffer.put(Float.floatToRawIntBits((float) (d + this.xOffset)));
            this.intBuffer.put(Float.floatToRawIntBits((float) (d1 + this.yOffset)));
            this.intBuffer.put(Float.floatToRawIntBits((float) (d2 + this.zOffset)));
            this.intBuffer.put(Float.floatToRawIntBits((float) this.textureU));
            this.intBuffer.put(Float.floatToRawIntBits((float) this.textureV));
            this.intBuffer.put(this.color);
            this.intBuffer.put(0);
            this.intBuffer.put(0);
            this.rawBufferIndex += 8;
            ++this.vertexCount;
            if (this.renderingChunk && this.addedVertices % 4 == 0 && this.rawBufferIndex >= this.bufferSize - 32) {
                this.draw();
                this.isDrawing = true;
            }
        }
    }

    public void setColorOpaque_I(int var1) {
        int var2 = var1 >> 16 & 255;
        int var3 = var1 >> 8 & 255;
        int var4 = var1 & 255;
        this.setColorOpaque(var2, var3, var4);
    }

    public void setColorRGBA_I(int var1, int var2) {
        int var3 = var1 >> 16 & 255;
        int var4 = var1 >> 8 & 255;
        int var5 = var1 & 255;
        this.setColorRGBA(var3, var4, var5, var2);
    }

    public void disableColor() {
        this.isColorDisabled = true;
    }

    public void setNormal(float f, float f1, float f2) {
        if (!this.isDrawing) {
            System.out.println("But...");
        }

        this.hasNormals = true;
        byte byte0 = (byte) ((int) (f * 128.0F));
        byte byte1 = (byte) ((int) (f1 * 127.0F));
        byte byte2 = (byte) ((int) (f2 * 127.0F));

        if (!this.renderingChunk) {
            GL11.glNormal3b(byte0, byte1, byte2);
        } else {
            System.out.println("ERROR: NORMALS IN CHUNK MODE !!!");
        }
    }

    public void setTranslationD(double var1, double var3, double var5) {
        this.xOffset = var1;
        this.yOffset = var3;
        this.zOffset = var5;
    }

    public void setTranslationF(float var1, float var2, float var3) {
        this.xOffset += (double) var1;
        this.yOffset += (double) var2;
        this.zOffset += (double) var3;
    }
    
    public void setRenderingChunk(boolean flag) {
        this.renderingChunk = flag;
    }

    private void checkOpenGlError() {
        int i = GL11.glGetError();

        if (i != 0) {
            String err = "OpenGL Error: " + i + " " + Util.translateGLErrorString(i);
            Exception e = new Exception(err);

            e.printStackTrace();
        }

    }

}
