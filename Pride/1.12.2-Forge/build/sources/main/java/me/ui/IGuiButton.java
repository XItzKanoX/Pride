package me.ui;


import me.ui.Fonts.FontManager;
import net.ccbluex.liquidbounce.api.minecraft.client.gui.IGuiScreen;
import net.ccbluex.liquidbounce.api.util.WrappedGuiScreen;
import net.ccbluex.liquidbounce.utils.Colors;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;

public class IGuiButton {
    String name;
    int x;
    int y;
    WrappedGuiScreen guiScreen;
    public boolean isHovered = false;
    public float hoverAni = 0;
    String icon;
    public IGuiButton(String name, WrappedGuiScreen guiScreen, String icon){
        this.name=name;
        this.guiScreen=guiScreen;
        this.icon=icon;
    }

    public void draw(int x,int y,int mouseX,int mouseY){
        this.x=x;
        this.y=y;
        isHovered=isHovered(x,y,x+120,y+18,mouseX,mouseY);
        hoverAni = (float) RenderUtils.getAnimationState(hoverAni, isHovered ? 255f : 0f, 500);
        float finalAni = clampValue(hoverAni / 100f, 0, 1);
        RenderUtils.drawRoundRect(x-1,y-1,x+121,y+19,new Color(62,61,64).getRGB());
        if(hoverAni > 1f) {
            RenderUtils.drawRoundRect(x-1,y-1,x+121,y+19, 3, RenderUtils.reAlpha(Colors.getColor(new Color(0xFFE76F)), finalAni));
//            RenderUtils.drawRoundRect(x-1,y-1,x+121,y+19, RenderUtils.reAlpha(Colors.getColor(new Color(0xFFE76F)), finalAni));
        }else{
            RenderUtils.drawRoundRect(x-1,y-1,x+121,y+19,3 , new Color(62,61,64).getRGB());
//            RenderUtils.drawRoundRect(x-1,y-1,x+121,y+19,new Color(62,61,64).getRGB());
        }
        RenderUtils.drawRoundRect(x,y,x+120,y+18,new Color(22, 22, 22, 255).getRGB());
        FontManager.SFREGULAR18.drawCenteredStringWithShadow(name,x  + 120 / 2f, (float) (y + (18 / 2f) - (FontManager.SFREGULAR18.getHeight(name) / 2f)), net.ccbluex.liquidbounce.utils.render.Colors.WHITE.c);

    }
    
    public void onClick() {

        if(isHovered) {
            if (guiScreen == null) {
                Minecraft.getMinecraft().shutdown();

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
