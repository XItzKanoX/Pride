package me.ui;


//import me.ui.Fonts.FontManager;

import me.utils.render.VisualUtils;
import net.ccbluex.liquidbounce.api.minecraft.client.gui.IGuiScreen;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static net.ccbluex.liquidbounce.utils.MinecraftInstance.mc;

public class IGuiButton {
    String name;
    int x;
    int y;
    final int delta = RenderUtils.deltaTime;
    final float speedDelta = 0.01F * delta;
    IGuiScreen guiScreen;
    float displayTime = System.currentTimeMillis();
    float nowTime = System.currentTimeMillis();
    public boolean isHovered = false;
    private final float moveX = 0F;
    float animeTime = 5f;
    float time = 5f;
    public float hoverAni = 0;
    String icon;
    public IGuiButton(String name, IGuiScreen guiScreen, String icon){
        this.name=name;
        this.guiScreen=guiScreen;
        this.icon=icon;
    }

    public void draw(int x,int y,int mouseX,int mouseY){
        this.x=x;
        this.y=y;
        isHovered=isHovered(x,y,x+120,y+18,mouseX,mouseY);
        hoverAni = (float) RenderUtils.getAnimationState(hoverAni, isHovered ? 255f : 0f, 500);
        int stringColor = new Color(255,255,255,240).getRGB();
        float finalAni = clampValue(hoverAni / 100f, 0, 1);
//        RenderUtils.drawRoundRect(x-1,y-1,x+121,y+19,new Color(62,61,64).getRGB());
        if(hoverAni > 1f) {
            Color color = new Color(180, 180, 180);
            GL11.glPushMatrix();
            VisualUtils.color(color.darker().getRGB());
            GL11.glPopMatrix();
            stringColor = color.darker().getRGB();
        }else{


        }



        RenderUtils.drawRoundedRect((float) x, y, x + 120, y + 18, 6, new Color(255,255,255,35).getRGB());
//        BlurBuffer.blurArea2(x, y, x + 120, y + 18);
        Fonts.bold40.drawStringWithShadow(name, (int) (x + 120 / 3.5f), (int) (y + (18 / 2f) - (Fonts.bold40.getFontHeight() / 2f)), stringColor);

    }

    public void onClick() {
        if(isHovered) {
            if (guiScreen == null) {
                Minecraft.getMinecraft().shutdown();
            } else {
                mc.displayGuiScreen(guiScreen);
            }
        }
    }


    public boolean isHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
    }

    public static float clampValue(final float value, final float floor, final float cap) {
        if (value < floor) {
            return floor;
        }

        return Math.min(value, cap);
    }
}
