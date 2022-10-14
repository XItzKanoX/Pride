package me.ui.Fonts;




import me.ui.Fonts.font.Yarukon;

import java.awt.*;
import java.io.InputStream;

public class FontManager {

    static {
        new Yarukon();
    }

  /*  public static FontUtils normal = new FontUtils("Arial.ttf", Font.PLAIN, 16, 7, false);
    public static FontUtils normal2 = new FontUtils("ArialBold.ttf", Font.PLAIN, 16, 7, false);
    public static FontUtils big = new FontUtils("Arial.ttf", Font.PLAIN, 24, 7, false);
    public static FontUtils small = new FontUtils("Roboto.ttf", Font.PLAIN, 16, 7, false);
    public static FontUtils tiny = new FontUtils("Roboto.ttf", Font.PLAIN, 14, 7, false);
    public static FontUtils tahomaArrayList = new FontUtils("tahoma.ttf", Font.PLAIN, 18, 7, false);
    public static FontUtils tahomaWatermark = new FontUtils("tahoma.ttf", Font.PLAIN, 20, 7, false);

    //Mainmenu
    public static FontUtils robotoL15 = new FontUtils("RobotoLight.ttf", Font.PLAIN, 15, 7, false);
    public static FontUtils robotoL18 = new FontUtils("RobotoLight.ttf", Font.PLAIN, 18, 7, false);
    public static FontUtils robotoL20 = new FontUtils("RobotoLight.ttf", Font.PLAIN, 20, 7, false);
    public static FontUtils robotoL40 = new FontUtils("RobotoLight.ttf", Font.PLAIN, 40, 7, false);

    public static FontUtils roboto12 = new FontUtils("Roboto.ttf", Font.PLAIN, 12, 7, false);
    public static FontUtils roboto15 = new FontUtils("Roboto.ttf", Font.PLAIN, 15, 7, false);
    public static FontUtils roboto16 = new FontUtils("Roboto.ttf", Font.PLAIN, 16, 7, false);

    public static FontUtils baloo16 = new FontUtils("Baloo.ttf", Font.PLAIN, 16, 7, false);
    public static FontUtils baloo17 = new FontUtils("Baloo.ttf", Font.PLAIN, 17, 7, false);
    public static FontUtils vision30 = new FontUtils("Vision.otf", Font.BOLD, 30, 7, false, 5);
    public static FontUtils icon10 = new FontUtils("Icon.ttf", Font.PLAIN, 10, 7, false);
    public static FontUtils icon14 = new FontUtils("Icon.ttf", Font.PLAIN, 14, 7, false);
    public static FontUtils icon15 = new FontUtils("Icon.ttf", Font.PLAIN, 15, 7, false);
    public static FontUtils icon16 = new FontUtils("Icon.ttf", Font.PLAIN, 16, 7, false);
    public static FontUtils icon18 = new FontUtils("Icon.ttf", Font.PLAIN, 18, 7, false);
    public static FontUtils icon20 = new FontUtils("Icon.ttf", Font.PLAIN, 20, 7, false);
    public static FontUtils icon25 = new FontUtils("Icon.ttf", Font.PLAIN, 25, 7, false);
    public static FontUtils icon30 = new FontUtils("Icon.ttf", Font.PLAIN, 30, 7, false);
    public static FontUtils icon40 = new FontUtils("Icon.ttf", Font.PLAIN, 40, 7, false);

    public static FontUtils icon35 = new FontUtils("Icon2.ttf", Font.PLAIN, 35, 7, false);

    public static FontUtils font1 = new FontUtils("Vision.otf", Font.PLAIN, 40, 7, false, 7);
    public static FontUtils font2 = new FontUtils("Comfortaa.ttf", Font.PLAIN, 15, 7, false);
    public static FontUtils font3 = new FontUtils("Comfortaa.ttf", Font.PLAIN, 10, 7, false);
    public static FontUtils font4 = new FontUtils("Icon.ttf", Font.PLAIN, 150, 7, false);
    public static FontUtils font5 = new FontUtils("Comfortaa.ttf", Font.PLAIN, 12, 7, false);

    public static FontUtils sans13 = new FontUtils("sans.ttf", Font.PLAIN, 13, 7, false);
    public static FontUtils sans14 = new FontUtils("sans.ttf", Font.PLAIN, 14, 7, false);
    public static FontUtils sans16 = new FontUtils("sans.ttf", Font.PLAIN, 16, 7, false);
    public static FontUtils sans18 = new FontUtils("sans.ttf", Font.PLAIN, 18, 7, false);
    public static FontUtils hudetc = new FontUtils("Arial.ttf", Font.PLAIN, 18, 7, false);
    public static FontUtils productsans = new FontUtils("fluxfont.ttf", Font.PLAIN, 18, 7, false);
    public static FontUtils productsans2 = new FontUtils("fluxfont.ttf", Font.PLAIN, 26, 7, false);
    public static FontUtils sans20 = new FontUtils("sans.ttf", Font.PLAIN, 20, 7, false);
    public static FontUtils sans24 = new FontUtils("sans.ttf", Font.PLAIN, 24, 7, false);

    public static FontUtils poppinsBold20 = new FontUtils("PoppinsSemiBold.ttf", Font.PLAIN, 20, 7, false);
    public static FontUtils poppins16 = new FontUtils("PoppinsRegular.ttf", Font.PLAIN, 16, 7, false);
    public static FontUtils poppins18 = new FontUtils("PoppinsRegular.ttf", Font.PLAIN, 18, 7, false);

    public static FontUtils wqy18 = roboto15;
    public static FontUtils wqy15 = roboto15;

    public static FontUtils tahoma9 = new FontUtils("tahoma.ttf", Font.PLAIN, 9, 7, false);
    public static FontUtils tahoma11 = new FontUtils("tahoma.ttf", Font.PLAIN, 11, 7, false);
    public static FontUtils tahoma11bold = new FontUtils("tahomabold.ttf", Font.BOLD, 11, 7, false);
    public static FontUtils tahoma13 = new FontUtils("tahoma.ttf", Font.PLAIN, 13, 7, false);
    public static FontUtils tahoma13bold = new FontUtils("tahomabold.ttf", Font.BOLD, 13, 7, false);
    public static FontUtils productsans16 = new FontUtils("fluxfont.ttf", Font.PLAIN, 16, 7, false);*/

  public static FontUtils BoldFont30 = new FontUtils(getFont("BoldFont.ttf",30), Font.PLAIN, 30, 7, false);

    public static FontUtils poppins16 = new FontUtils(getFont("PoppinsRegular.ttf",16), Font.PLAIN, 16, 7, false);
    public static FontUtils poppins18 = new FontUtils(getFont("PoppinsRegular.ttf",18), Font.PLAIN, 18, 7, false);

    public static FontUtils siyuan14 = new FontUtils(getFont("siyuan.ttf",14), Font.PLAIN, 14, 7, false);
    public static FontUtils siyuan16 = new FontUtils(getFont("siyuan.ttf",16), Font.PLAIN, 16, 7, false);
    public static FontUtils siyuan18 = new FontUtils(getFont("siyuan.ttf",18), Font.PLAIN, 18, 7, false);
    public static FontUtils siyuan20 = new FontUtils(getFont("siyuan.ttf",20), Font.PLAIN, 20, 7, false);

    public static FontUtils productsans14 = new FontUtils(getFont("productsans.ttf",14), Font.PLAIN, 14, 7, false);
    public static FontUtils productsans16 = new FontUtils(getFont("productsans.ttf",16), Font.PLAIN, 16, 7, false);
    public static FontUtils productsans18 = new FontUtils(getFont("productsans.ttf",18), Font.PLAIN, 18, 7, false);
    public static FontUtils productsans20 = new FontUtils(getFont("productsans.ttf",20), Font.PLAIN, 20, 7, false);

    public static FontUtils SFREGULAR14 = new FontUtils(getFont("SFREGULAR.ttf",14), Font.PLAIN, 14, 7, false);
    public static FontUtils SFREGULAR16 = new FontUtils(getFont("SFREGULAR.ttf",16), Font.PLAIN, 16, 7, false);
    public static FontUtils SFREGULAR18 = new FontUtils(getFont("SFREGULAR.ttf",18), Font.PLAIN, 18, 7, false);
    public static FontUtils SFREGULAR20 = new FontUtils(getFont("SFREGULAR.ttf",20), Font.PLAIN, 20, 7, false);

    public static FontUtils PoppinsSemiBold14 = new FontUtils(getFont("PoppinsSemiBold.ttf",14), Font.PLAIN, 14, 7, false);
    public static FontUtils PoppinsSemiBold16 = new FontUtils(getFont("PoppinsSemiBold.ttf",16), Font.PLAIN, 16, 7, false);
    public static FontUtils PoppinsSemiBold18 = new FontUtils(getFont("PoppinsSemiBold.ttf",18), Font.PLAIN, 18, 7, false);
    public static FontUtils PoppinsSemiBold20 = new FontUtils(getFont("PoppinsSemiBold.ttf",20), Font.PLAIN, 20, 7, false);

    public static FontUtils FluxIcon16 = new FontUtils(getFont("icon.ttf",16), Font.PLAIN, 14, 7, false);
    public static FontUtils FluxIcon18 = new FontUtils(getFont("icon.ttf",18), Font.PLAIN, 14, 7, false);

    public static FontUtils icon10 = new FontUtils(getFont("fluxicon.ttf",10), Font.PLAIN, 14, 7, false);
    public static FontUtils icon20 = new FontUtils(getFont("fluxicon.ttf",20), Font.PLAIN, 50, 7, false);
    public static FontUtils icon18 = new FontUtils(getFont("fluxicon.ttf",20), Font.PLAIN, 50, 7, false);

    public static FontUtils icon14 = new FontUtils(getFont("fluxicon.ttf",14), Font.PLAIN, 14, 7, false);
    public static FontUtils icon50 = new FontUtils(getFont("fluxicon.ttf",50), Font.PLAIN, 50, 7, false);

    public static FontUtils HanabiIcon14 = new FontUtils(getFont("HanabiFont.ttf",14), Font.PLAIN, 14, 7, false);
    public static FontUtils HanabiIcon16 = new FontUtils(getFont("HanabiFont.ttf",16), Font.PLAIN, 16, 7, false);

    public static Font getFont(String name, int size) {
        Font font;
        try {
            InputStream is = FontManager.class.getResourceAsStream("/assets/minecraft/pride/font/" + name);
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            System.out.println("Error loading font " + name);
            font = new Font("Arial", Font.PLAIN, size);
        }
        return font;
    }
}