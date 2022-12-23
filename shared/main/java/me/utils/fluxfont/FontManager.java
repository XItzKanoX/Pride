package me.utils.fluxfont;


import me.yarukon.font.GlyphPageFontRenderer;
import me.yarukon.font.Yarukon;

import java.awt.*;
import java.io.InputStream;

public class FontManager {

    static {
        new Yarukon();
    }

//    public static FontUtils normal = new FontUtils("arial.ttf", Font.PLAIN, 16, 7, false);
//    public static FontUtils normal2 = new FontUtils("arialbold.ttf", Font.PLAIN, 16, 7, false);
//    public static FontUtils big = new FontUtils("arial.ttf", Font.PLAIN, 24, 7, false);
//    public static FontUtils small = new FontUtils("roboto.ttf", Font.PLAIN, 16, 7, false);
//    public static FontUtils tiny = new FontUtils("roboto.ttf", Font.PLAIN, 14, 7, false);
//
//    //Mainmenu
//    public static FontUtils robotoL15 = new FontUtils("robotolight.ttf", Font.PLAIN, 15, 7, false);
//    public static FontUtils robotoL18 = new FontUtils("robotolight.ttf", Font.PLAIN, 18, 7, false);
//    public static FontUtils robotoL20 = new FontUtils("robotolight.ttf", Font.PLAIN, 20, 7, false);
//    public static FontUtils robotoL40 = new FontUtils("robotolight.ttf", Font.PLAIN, 40, 7, false);
//
//    public static FontUtils roboto12 = new FontUtils("roboto.ttf", Font.PLAIN, 12, 7, false);
//    public static FontUtils roboto15 = new FontUtils("roboto.ttf", Font.PLAIN, 15, 7, false);
//    public static FontUtils roboto16 = new FontUtils("roboto.ttf", Font.PLAIN, 16, 7, false);
//
//    public static FontUtils baloo16 = new FontUtils("baloo.ttf", Font.PLAIN, 16, 7, false);
//    public static FontUtils baloo17 = new FontUtils("baloo.ttf", Font.PLAIN, 17, 7, false);
//    public static FontUtils vision30 = new FontUtils("vision.otf", Font.BOLD, 30, 7, false, 5);
//    public static FontUtils icon10 = new FontUtils("icon.ttf", Font.PLAIN, 10, 7, false);
//    public static FontUtils icon14 = new FontUtils("icon.ttf", Font.PLAIN, 14, 7, false);
//    public static FontUtils icon15 = new FontUtils("icon.ttf", Font.PLAIN, 15, 7, false);
//    public static FontUtils icon16 = new FontUtils("icon.ttf", Font.PLAIN, 16, 7, false);
//    public static FontUtils icon18 = new FontUtils("icon.ttf", Font.PLAIN, 18, 7, false);
//    public static FontUtils icon20 = new FontUtils("icon.ttf", Font.PLAIN, 20, 7, false);
//    public static FontUtils icon25 = new FontUtils("icon.ttf", Font.PLAIN, 25, 7, false);
//    public static FontUtils icon30 = new FontUtils("icon.ttf", Font.PLAIN, 30, 7, false);
//    public static FontUtils icon40 = new FontUtils("icon.ttf", Font.PLAIN, 40, 7, false);
//
//    public static FontUtils icon35 = new FontUtils("icon2.ttf", Font.PLAIN, 35, 7, false);
//
//    public static FontUtils font1 = new FontUtils("Vision.otf", Font.PLAIN, 40, 7, false, 7);
//    public static FontUtils font2 = new FontUtils("comfortaa.ttf", Font.PLAIN, 15, 7, false);
//    public static FontUtils font3 = new FontUtils("comfortaa.ttf", Font.PLAIN, 10, 7, false);
//    public static FontUtils font4 = new FontUtils("icon.ttf", Font.PLAIN, 150, 7, false);
//    public static FontUtils font5 = new FontUtils("comfortaa.ttf", Font.PLAIN, 12, 7, false);
//
//    public static FontUtils sans13 = new FontUtils("sans.ttf", Font.PLAIN, 13, 7, false);
//    public static FontUtils sans14 = new FontUtils("sans.ttf", Font.PLAIN, 14, 7, false);
//    public static FontUtils sans16 = new FontUtils("sans.ttf", Font.PLAIN, 16, 7, false);
//    public static FontUtils sans18 = new FontUtils("sans.ttf", Font.PLAIN, 18, 7, false);
//    public static FontUtils hudetc = new FontUtils("arial.ttf", Font.PLAIN, 18, 7, false);
//    public static FontUtils productsans = new FontUtils("fluxfont.ttf", Font.PLAIN, 18, 7, false);
//    public static FontUtils productsans2 = new FontUtils("fluxfont.ttf", Font.PLAIN, 26, 7, false);
//    public static FontUtils sans20 = new FontUtils("sans.ttf", Font.PLAIN, 20, 7, false);
//    public static FontUtils sans24 = new FontUtils("sans.ttf", Font.PLAIN, 24, 7, false);
//
//    public static FontUtils poppinsBold20 = new FontUtils("poppinssemibold.ttf", Font.PLAIN, 20, 7, false);
//    public static FontUtils poppins16 = new FontUtils("poppinsregular.ttf", Font.PLAIN, 16, 7, false);
//    public static FontUtils poppins18 = new FontUtils("poppinsregular.ttf", Font.PLAIN, 18, 7, false);
//
//    public static FontUtils wqy18 = new FontUtils("wqy_microhei.ttf", Font.PLAIN, 18, 7, false);;
//    public static FontUtils wqy15 = new FontUtils("wqy_microhei.ttf", Font.PLAIN, 15 , 7, false);;
//
//    public static FontUtils tahoma9 = new FontUtils("tahoma.ttf", Font.PLAIN, 9, 7, false);
//    public static FontUtils tahoma11 = new FontUtils("tahoma.ttf", Font.PLAIN, 11, 7, false);
//    public static FontUtils tahoma11bold = new FontUtils("tahomabold.ttf", Font.BOLD, 11, 7, false);
//    public static FontUtils tahoma13 = new FontUtils("tahoma.ttf", Font.PLAIN, 13, 7, false);
//    public static FontUtils tahoma13bold = new FontUtils("tahomabold.ttf", Font.BOLD, 13, 7, false);
//    public static FontUtils productsans16 = new FontUtils("fluxfont.ttf", Font.PLAIN, 16, 7, false);
//
//    public static GlyphPageFontRenderer sans13_2 = GlyphPageFontRenderer.create(getFont("sans.ttf", 13), false);
//    public static GlyphPageFontRenderer sans16_2 = GlyphPageFontRenderer.create(getFont("sans.ttf", 16), false);
//    public static GlyphPageFontRenderer sans18_2 = GlyphPageFontRenderer.create(getFont("sans.ttf", 18), false);

    public static Font getFont(String name, int size) {
        Font font;
        try {
//            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("flux/font/"+name.toLowerCase())).getInputStream();
            InputStream is = FontManager.class.getResourceAsStream("/assets/minecraft/pride/font/" + name);
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            System.out.println("Error loading font " + name);
            font = new Font("arial", Font.PLAIN, size);
        }
        return font;
    }
}