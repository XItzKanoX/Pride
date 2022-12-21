/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.render

import com.mojang.realmsclient.gui.ChatFormatting
import me.utils.QQUtils
import me.utils.render.VisualUtils
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.blur.BlurBuffer
import net.ccbluex.liquidbounce.utils.render.Colors
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.ccbluex.liquidbounce.value.TextValue
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import java.awt.Color
import java.text.SimpleDateFormat
import java.util.*

@ModuleInfo(name = "HUD", description = "Toggles visibility of the HUD.", category = ModuleCategory.RENDER, array = false)
public class HUD : Module() {

    private val toggleMessageValue = BoolValue("DisplayToggleMessage", true)
    private val toggleSoundValue = ListValue("ToggleSound", arrayOf("None", "Default", "Custom"), "Custom")
    companion object {

     val shadowValue = ListValue(
        "ShadowMode", arrayOf(
             "Test",
            "LiquidBounce",
            "Outline",
            "Default",
            "Autumn"
        ), "LiquidBounce"
    )}

    val containerBackground = BoolValue("Container-Background", false)
    val animHotbarValue = BoolValue("AnimatedHotbar", true)
    val blackHotbarValue = BoolValue("BlackHotbar", true)
    val Hotbarblur = BoolValue("BlurHotabr", true)

    val inventoryParticle = BoolValue("InventoryParticle", false)
    private val blurValue = BoolValue("Blur", false)
    val Radius = IntegerValue("BlurRadius", 10 , 1 , 50 )

    val fontChatValue = BoolValue("FontChat", false)
    val logValue = ListValue("LogMode", arrayOf("idk", "141Sense", "Jello", "PowerX", "None", "Novoline"), "None")

    val ClientName = TextValue("ClientName", "Pride")
    val DevName = TextValue("DevName", "JieMo")
    val redValue = IntegerValue("NovolineRed", 255, 0, 255)
    val greenValue = IntegerValue("NovolineGreen", 255, 0, 255)
    val blueValue = IntegerValue("NovolineBlue", 255, 0, 255)

    val customColor = Color(redValue.get(), greenValue.get(), blueValue.get())
    private var hotBarX = 0F
    @EventTarget
    fun onRender2D(event: Render2DEvent?) {
        LiquidBounce.hud.render(false)
        when (logValue.get().toLowerCase()) {


            "novoline" -> {
                val time = SimpleDateFormat("HH:mm").format(Calendar.getInstance().time)
                Fonts.font35.drawString(
                        ClientName.get() + ChatFormatting.GRAY + " (" + ChatFormatting.WHITE + time + ChatFormatting.GRAY + ")",
                        3f,
                        4f,
                        customColor.rgb,
                        true
                )
            }


            "idk" -> {
                Fonts.fontSFUI35.drawStringWithShadow(ClientName.get() + "#0810",7, 10,   Color(255, 255, 255, 245).rgb)
            }
            "powerx" ->{
                Gui.drawRect(2, 1, 78, 18, Color(10, 10, 10, 180).rgb)
                Gui.drawRect(2, 1, 4, 18, Color(240, 240, 240, 245).rgb)
                Fonts.fontSFUI35.drawStringWithShadow(ClientName.get(), 7, 3,   Color(255, 255, 255, 245).rgb)
            }
            "jello" ->{
                Fonts.fontSFUI120 .drawString(ClientName.get(), 10.0f, 10f, VisualUtils.reAlpha(Colors.WHITE.c, 0.75f))
            }
            "141sense" ->{
                RenderUtils.drawRect(3.0f, 3.0f, 142.0f, 63.0f, Color(255, 255, 255, 120).rgb)
                Fonts.font80.drawCenteredString(ClientName.get(), 71.0f, 7.0f, java.awt.Color(0, 0, 0, 180).rgb)
                Fonts.font35.drawCenteredString("by" + DevName.get() , 71.0f, 25.0f, java.awt.Color(0, 0, 0, 180).rgb)
                Fonts.font80.drawString("_______________", 6.0f, 19.0f, java.awt.Color(0, 0, 0, 180).rgb)
                Fonts.font35.drawString("UserName:" + mc.thePlayer!!.name, 45.0f - Fonts.font35.getStringWidth("UserName:" + mc.thePlayer!!.name).toFloat() / 2.0f + 28.0f, 40.0f, java.awt.Color(0, 0, 0, 180).rgb)
                Fonts.font35.drawString("FPS:" + MinecraftInstance.mc.debugFPS, 45.0f - Fonts.font35.getStringWidth("FPS:" + MinecraftInstance.mc.debugFPS).toFloat() / 2.0f + 28.0f, 52.0f, java.awt.Color(0, 0, 0, 180).rgb)
            }
        }
        if (classProvider.isGuiHudDesigner(mc.currentScreen))
            return

        LiquidBounce.hud.render(false)
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        LiquidBounce.hud.update()
    }
    @EventTarget(ignoreCondition = true)
    fun onTick(event: TickEvent) {

        LiquidBounce.moduleManager.shouldNotify = toggleMessageValue.get()
        LiquidBounce.moduleManager.toggleSoundMode = toggleSoundValue.values.indexOf(toggleSoundValue.get())
    }
    @EventTarget
    fun onKey(event: KeyEvent) {
        LiquidBounce.hud.handleKey('a', event.key)
    }
    fun getAnimPos(pos: Float): Float {
        if (state && animHotbarValue.get()) hotBarX = net.ccbluex.liquidbounce.utils.AnimationUtils.animate(pos, hotBarX, 0.02F * RenderUtils.deltaTime.toFloat())
        else hotBarX = pos

        return hotBarX
    }

    init {
        state = true
    }

    @EventTarget(ignoreCondition = true)
    fun onScreen(event: ScreenEvent) {
        if (mc.theWorld == null || mc.thePlayer == null) return
        if (state && blurValue.get() && !mc.entityRenderer.isShaderActive() && event.guiScreen != null &&
                !(classProvider.isGuiChat(event.guiScreen) || classProvider.isGuiHudDesigner(event.guiScreen))) mc.entityRenderer.loadShader(classProvider.createResourceLocation(LiquidBounce.CLIENT_NAME.toLowerCase() + "/blur.json")) else if (mc.entityRenderer.shaderGroup != null &&
                mc.entityRenderer.shaderGroup!!.shaderGroupName.contains("pride/blur.json")) mc.entityRenderer.stopUseShader()
    }

    init {
        state = true
    }


}