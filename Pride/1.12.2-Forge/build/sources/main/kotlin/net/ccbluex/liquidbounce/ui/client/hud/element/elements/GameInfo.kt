package net.ccbluex.liquidbounce.ui.client.hud.element.elements
//Coarse_KK 
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.ServerUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import java.awt.Color


@ElementInfo(name = "GameInfo")
class GameInfo(x: Double = 10.0, y: Double = 10.0, scale: Float = 1F) : Element(x, y, scale) {

    private val redValue = IntegerValue("BackgroundRed", 255, 0, 255)
    private val greenValue = IntegerValue("BackgroundGreen", 255, 0, 255)
    private val blueValue = IntegerValue("BackgroundBlue", 255, 0, 255)
    private val alpha = IntegerValue("BackgroundAlpha", 20, 0, 255)
    private val rredValue = IntegerValue("RectRed", 255, 0, 255)
    private val rgreenValue = IntegerValue("RectGreen", 255, 0, 255)
    private val rblueValue = IntegerValue("RectBlue", 255, 0, 255)
    private val ralpha = IntegerValue("RectAlpha", 35,0, 255)
    private val textredValue = IntegerValue("TextRed", 255, 0, 255)
    private val textgreenValue = IntegerValue("TextRed", 244, 0, 255)
    private val textblueValue = IntegerValue("TextBlue", 255, 0, 255)
    private val textblueae = IntegerValue("Textalpha", 255, 0, 255)

    private var GameInfoRows = 0
//    var aura = LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura?
//    var target = aura!!.target
    /**
     * Draw element
     */


    override fun drawElement(): Border? {
        val image = ResourceLocation("pride/info.png")
        RenderUtils.drawShadowWithCustomAlpha(0F, 12.5F, 176F, 64F, 255F)
        RenderUtils.drawImage4(image,47F.toInt(), ((this.GameInfoRows * 18F + 14.toInt()).toInt()),10,10)
        RenderUtils.drawRect(0F, this.GameInfoRows * 18F + 12, 176F, this.GameInfoRows * 18F + 25F, Color(rredValue.get(), rgreenValue.get(), rblueValue.get(), ralpha.get()).rgb)
        Fonts.jello35.drawStringWithShadow("GameInfo", 3, this.GameInfoRows * 18 + 16, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
        Fonts.jello35.drawStringWithShadow("FPS:" + Minecraft.getDebugFPS(), 7, this.GameInfoRows * 18 + 32, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
        Fonts.jello35.drawStringWithShadow("Ping:" + EntityUtils.getPing(mc2.player).toString(), 7, this.GameInfoRows * 18 + 42, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
        Fonts.jello35.drawStringWithShadow("X:" + Text.DECIMAL_FORMAT.format(mc.thePlayer!!.posX) + " " + "Y:" + Text.DECIMAL_FORMAT.format(mc.thePlayer!!.posY) + " " + "Z:" + Text.DECIMAL_FORMAT.format(mc.thePlayer!!.posZ), 7, this.GameInfoRows * 18 + 53, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
        Fonts.jello35.drawStringWithShadow("ServerIP:" + ServerUtils.getRemoteIp(), 7, this.GameInfoRows * 18 + 62, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
//        Fonts.font35.drawStringWithShadow("Kills:" + getKills() ,7F, this.GameInfoRows * 18F + 68, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
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
