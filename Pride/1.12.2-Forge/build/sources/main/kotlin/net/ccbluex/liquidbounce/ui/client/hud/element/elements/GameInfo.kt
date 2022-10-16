package net.ccbluex.liquidbounce.ui.client.hud.element.elements
//Coarse_KK 
import me.utils.render.VisualUtils
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.combat.AutoLFix
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.ServerUtils
import net.ccbluex.liquidbounce.utils.render.Palette
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.FontValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import java.awt.Color


@ElementInfo(name = "GameInfo")
class GameInfo(x: Double = 10.0, y: Double = 10.0, scale: Float = 1F) : Element(x, y, scale) {

    private val alpha = IntegerValue("BackgroundAlpha", 20, 0, 255)
    private val textredValue = IntegerValue("TextRed", 255, 0, 255)
    private val textgreenValue = IntegerValue("TextRed", 244, 0, 255)
    private val textblueValue = IntegerValue("TextBlue", 255, 0, 255)
    private val textblueae = IntegerValue("Textalpha", 255, 0, 255)
    private val modeValue = ListValue("Background", arrayOf("Default"), "Default")
    private val brightnessValue = FloatValue("Brightness", 1f, 0f, 1f)
    private val redValue = IntegerValue("Text-R", 255, 0, 255)
    private val greenValue = IntegerValue("Text-G", 255, 0, 255)
    private val blueValue = IntegerValue("Text-B", 255, 0, 255)
    private val colorRedValue2 = IntegerValue("Text-R2", 0, 0, 255)
    private val colorGreenValue2 = IntegerValue("Text-G2", 111, 0, 255)
    private val colorBlueValue2 = IntegerValue("Text-B2", 255, 0, 255)
    private val gidentspeed = IntegerValue("GidentSpeed", 100, 1, 1000)
    private val newRainbowIndex = IntegerValue("NewRainbowOffset", 1, 1, 50)
    private val astolfoRainbowOffset = IntegerValue("AstolfoOffset", 5, 1, 20)
    private val astolfoclient = IntegerValue("AstolfoRange", 109, 1, 765)
    private val astolfoRainbowIndex = IntegerValue("AstolfoIndex", 109, 1, 300)
    private val saturationValue = FloatValue("Saturation", 0.9f, 0f, 1f)
    private val colorModeValue = ListValue("Color", arrayOf("Custom", "Rainbow", "Fade", "Astolfo", "NewRainbow","Gident"), "Custom")
    private val distanceValue = IntegerValue("Distance", 0, 0, 400)
    private val gradientAmountValue = IntegerValue("Gradient-Amount", 25, 1, 50)
    private var fontValue = FontValue("Font", Fonts.jello35)
    private var GameInfoRows = 0
//    var aura = LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura?
//    var target = aura!!.target
    /**
     * Draw element
     */


    override fun drawElement(): Border? {
//        val image = ResourceLocation("pride/info.png")
        var kills = LiquidBounce.moduleManager.getModule(AutoLFix::class.java) as AutoLFix?
        var length2 = 4.5f
        val barLength1 = (163f).toDouble()
        val colorMode = colorModeValue.get()
        val color = Color(redValue.get(), greenValue.get(), blueValue.get()).rgb
        if (modeValue.get().equals("Default", true)) {
            RenderUtils.drawRect(0F, 11.0F, 176F, 77F , Color(20, 19, 18, 170).rgb)
            for (i in 0 until gradientAmountValue.get()) {
                val barStart = i.toDouble() / gradientAmountValue.get().toDouble() * barLength1
                val barEnd = (i + 1).toDouble() / gradientAmountValue.get().toDouble() * barLength1

                RenderUtils.drawGradientSideways(
                    8 + barStart  -8 , 10.0, 8 + barEnd + 5, 11.0,
                    when {
                        colorMode.equals("Fade", ignoreCase = true) -> Palette.fade2(
                            Color(
                                redValue.get(),
                                greenValue.get(),
                                blueValue.get()
                            ), i * distanceValue.get(), 1000
                        ).rgb
                        colorMode.equals("Astolfo", ignoreCase = true) -> VisualUtils.Astolfo(
                            i * distanceValue.get(),
                            saturationValue.get(),
                            brightnessValue.get(),
                            astolfoRainbowOffset.get(),
                            astolfoRainbowIndex.get(),
                            astolfoclient.get().toFloat()
                        )
                        colorMode.equals(
                            "Gident",
                            ignoreCase = true
                        ) -> VisualUtils.getGradientOffset(
                            Color(redValue.get(), greenValue.get(), blueValue.get()),
                            Color(colorRedValue2.get(), colorGreenValue2.get(), colorBlueValue2.get(), 1),
                            (Math.abs(
                                System.currentTimeMillis() / gidentspeed.get().toDouble() + i * distanceValue.get()
                            ) / 10)
                        ).rgb
                        colorMode.equals(
                            "NewRainbow",
                            ignoreCase = true
                        ) -> VisualUtils.getRainbow(
                            i * distanceValue.get(),
                            newRainbowIndex.get(),
                            saturationValue.get(),
                            brightnessValue.get()
                        )

                        else -> color
                    },
                    when {
                        colorMode.equals("Fade", ignoreCase = true) -> Palette.fade2(
                            Color(
                                redValue.get(),
                                greenValue.get(),
                                blueValue.get()
                            ), i * distanceValue.get(), 1000
                        ).rgb
                        colorMode.equals("Astolfo", ignoreCase = true) -> VisualUtils.Astolfo(
                            i * distanceValue.get(),
                            saturationValue.get(),
                            brightnessValue.get(),
                            astolfoRainbowOffset.get(),
                            astolfoRainbowIndex.get(),
                            astolfoclient.get().toFloat()
                        )
                        colorMode.equals(
                            "Gident",
                            ignoreCase = true
                        ) -> VisualUtils.getGradientOffset(
                            Color(redValue.get(), greenValue.get(), blueValue.get()),
                            Color(colorRedValue2.get(), colorGreenValue2.get(), colorBlueValue2.get(), 1),
                            (Math.abs(
                                System.currentTimeMillis() / gidentspeed.get().toDouble() + i * distanceValue.get()
                            ) / 10)
                        ).rgb
                        colorMode.equals(
                            "NewRainbow",
                            ignoreCase = true
                        ) -> VisualUtils.getRainbow(
                            i * distanceValue.get(),
                            newRainbowIndex.get(),
                            saturationValue.get(),
                            brightnessValue.get()
                        )
                        else -> color
                    }
                )
            }
        }












        RenderUtils.drawShadowWithCustomAlpha(0F, 12.5F, 176F, 64F, 255F)
//        RenderUtils.drawImage4(image,47F.toInt(), ((this.GameInfoRows * 18F + 14.toInt()).toInt()),10,10)
//        RenderUtils.drawRect(0F, this.GameInfoRows * 18F + 12, 176F, this.GameInfoRows * 18F + 25F, Color(rredValue.get(), rgreenValue.get(), rblueValue.get(), ralpha.get()).rgb)
        fontValue.get().drawStringWithShadow("Game Info", 3, this.GameInfoRows * 18 + 14, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
        fontValue.get().drawStringWithShadow("FPS:" + Minecraft.getDebugFPS(), 7, this.GameInfoRows * 18 + 26, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
        fontValue.get().drawStringWithShadow("Ping:" + EntityUtils.getPing(mc2.player).toString(), 7, this.GameInfoRows * 18 + 36, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
        fontValue.get().drawStringWithShadow("X:" + Text.DECIMAL_FORMAT.format(mc.thePlayer!!.posX) + " " + "Y:" + Text.DECIMAL_FORMAT.format(mc.thePlayer!!.posY) + " " + "Z:" + Text.DECIMAL_FORMAT.format(mc.thePlayer!!.posZ), 7, this.GameInfoRows * 18 + 47, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
        fontValue.get().drawStringWithShadow("ServerIP:" + ServerUtils.getRemoteIp(), 7, this.GameInfoRows * 18 + 56, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
        fontValue.get().drawStringWithShadow("Kills:" + kills!!.kill , 7, this.GameInfoRows * 18 + 68, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
        RenderUtils.drawRect(0F, this.GameInfoRows * 18F + 25F, 176F, 80F, Color(redValue.get(), greenValue.get(), blueValue.get(), alpha.get()).rgb)
        return Border(0F, this.GameInfoRows * 18F + 12F, 176F, 80F)
    }

//    private fun getKills(): Int {
//        var kills = 0
//
//        if (target != null) {
//            if (target!!.isDead) {
//                kills = kills +1
//                target = null
//            }
//        }
//
//        return kills
//    }


}
