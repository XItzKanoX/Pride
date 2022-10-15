package net.ccbluex.liquidbounce.ui.client.hud.element.elements
//Coarse_KK

import me.utils.render.VisualUtils
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityLivingBase
import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.modules.combat.AutoLFix
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura.Companion.killCounts
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.InfosUtils.Recorder
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.InfosUtils.Recorder.syncEntity
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FontValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.client.Minecraft
import java.awt.Color
import java.text.SimpleDateFormat
import java.util.*


@ElementInfo(name = "GameInfo2")
class GameInfo2(x: Double = 5.0, y: Double = 87.0, scale: Float = 1F) : Element(x, y, scale) {


    private val RectA = IntegerValue("RectA", 255, 0, 255)
    private val textredValue = IntegerValue("TextRed", 255, 0, 255)
    private val textgreenValue = IntegerValue("TextGreen", 244, 0, 255)
    private val textblueValue = IntegerValue("TextBlue", 255, 0, 255)
    private val shadowValue = BoolValue("Shadow", true)
    private val fontValue = FontValue("Font", Fonts.bold35)
    private var GameInfoRows = 0
    private val DATE_FORMAT = SimpleDateFormat("HH:mm:ss")
    var aura = LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura?
    var target = aura!!.target
    var killCounts = 0
    /**
     * Draw element
     */
//    var kills = LiquidBounce.moduleManager.getModule(AutoLFix::class.java) as AutoLFix?

    override fun drawElement(): Border {


        val icon = Fonts.flux
        val color = Color.WHITE.rgb
        val fontHeight = Fonts.font40.fontHeight
        val fontRenderer = fontValue.get()

        if(shadowValue.get()){
            RenderUtils.drawShadowWithCustomAlpha(0F, 8F, 155F, 76F, 200F)
        }
        RenderUtils.drawRoundRect(0F, 8F, 156F,85F, Color(41, 41, 41, RectA.get()).rgb)
        icon.drawString("c", 47F, 2.5F + fontHeight + 6F, color)
        Fonts.bold35.drawStringWithShadow("Game Info", (50F + icon.getStringWidth("u")).toInt(),
            (this.GameInfoRows * 18F + 16).toInt(), Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
        Fonts.bold30.drawStringWithShadow("Speed: " + MovementUtils.getBlockSpeed(mc2.player),
            (5F + icon.getStringWidth("b")).toInt(),
            (this.GameInfoRows * 20F + 30).toInt(), Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
        Fonts.bold30.drawStringWithShadow("FPS: " + Minecraft.getDebugFPS(), (5F + icon.getStringWidth("e")).toInt(),
            (this.GameInfoRows * 20F + 43).toInt(), Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
        Fonts.bold30.drawStringWithShadow("Kills" + killCounts , (5F + icon.getStringWidth("G")).toInt(),
            (this.GameInfoRows * 20F + 54).toInt(), Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
        Fonts.bold30.drawStringWithShadow("Played Time: ${DATE_FORMAT.format(Date(System.currentTimeMillis() - Recorder.startTime - 8000L * 3600L))}" ,
            (5F + icon.getStringWidth("G")).toInt(),
            (this.GameInfoRows * 20F + 66).toInt(), Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)

        return Border(0F, this.GameInfoRows * 18F + 12F, 150F, 80F)
    }

    @EventTarget
    fun onAttack(event: AttackEvent) {
        target = event.targetEntity as IEntityLivingBase?
    }

    @EventTarget
    private fun onUpdate(event: UpdateEvent) {
        if (!!.isDead) {
            ClientUtils.displayChatMessage("get kills")
            killCounts += 1
            target = null
        }
    }




}
