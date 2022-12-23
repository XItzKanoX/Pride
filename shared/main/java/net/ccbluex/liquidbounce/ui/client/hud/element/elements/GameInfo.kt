package net.ccbluex.liquidbounce.ui.client.hud.element.elements
//Coarse_KK 
import me.utils.render.ColorUtils2
import me.utils.render.VisualUtils
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.color.Gident
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.features.module.modules.render.HUD
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.InfosUtils.Recorder
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.InfosUtils.Recorder.killCounts
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.ServerUtils
import net.ccbluex.liquidbounce.utils.blur.BlurBuffer
import net.ccbluex.liquidbounce.utils.render.Palette
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.Minecraft
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


@ElementInfo(name = "GameInfo")
class GameInfo(x: Double = 10.0, y: Double = 10.0, scale: Float = 1F) : Element(x, y, scale) {
    private val GameInfo = ListValue("Mode", arrayOf("Default", "Shadow", "Old", "LoseLine", "Flux", "Normal", "Round"), "Default")

    private val alphaValue = IntegerValue("Round-Background-Alpha", 180, 0, 255)
    private val raduiValue = IntegerValue("Round-RoundRadiu", 2, 0, 10)
    private val blur = BoolValue("Old-Blur", true)
    private val RectA = IntegerValue("Old-RectA", 120, 0, 255)
    private val textredValue = IntegerValue("Old-TextRed", 255, 0, 255)
    private val textgreenValue = IntegerValue("Old-TextRed", 244, 0, 255)
    private val textblueValue = IntegerValue("Old-TextBlue", 255, 0, 255)
    private val textblueae = IntegerValue("Old-Textalpha", 255, 0, 255)
    private val brightnessValue = FloatValue("Brightness", 1f, 0f, 1f)
    private val redValue = IntegerValue("Text-R", 255, 0, 255)
    private val greenValue = IntegerValue("Text-G", 255, 0, 255)
    private val blueValue = IntegerValue("Text-B", 255, 0, 255)
    private val radiusValue = FloatValue("Normal-Radius", 4.25f, 0f, 10f)
    private val bgredValue = IntegerValue("Normal-Bg-R", 255, 0, 255)
    private val bggreenValue = IntegerValue("Normal-Bg-G", 255, 0, 255)
    private val bgblueValue = IntegerValue("Normal-Bg-B", 255, 0, 255)
    private val bgalphaValue = IntegerValue("Normal-Bg-Alpha", 150, 0, 255)

    val blurValue = BoolValue("Normal-Blur", true)

    val lineValue = BoolValue("Normal-Line", true)
    private val newRainbowIndex = IntegerValue("NewRainbowOffset", 1, 1, 50)
    private val astolfoRainbowOffset = IntegerValue("AstolfoOffset", 5, 1, 20)
    private val astolfoclient = IntegerValue("AstolfoRange", 109, 1, 765)
    private val astolfoRainbowIndex = IntegerValue("AstolfoIndex", 109, 1, 300)
    private val saturationValue = FloatValue("Saturation", 0.9f, 0f, 1f)
    private val colorModeValue = ListValue("Color", arrayOf("Custom", "Rainbow", "Fade", "Astolfo", "NewRainbow","Gident"), "Custom")
    private val distanceValue = IntegerValue("Distance", 0, 0, 400)
    private val gradientAmountValue = IntegerValue("Gradient-Amount", 25, 1, 50)
    private var fontValue = FontValue("Font", Fonts.fontSFUI35)
    private var GameInfoRows = 0
    private val DATE_FORMAT = SimpleDateFormat("HH:mm:ss")



    override fun drawElement(): Border {
        val icon = Fonts.flux
        val fontHeight = Fonts.font40.fontHeight
        val floatX = renderX.toFloat()
        val floatY = renderY.toFloat()
        val barLength1 = (163f).toDouble()
        val colorMode = colorModeValue.get()
        val color = Color(redValue.get(), greenValue.get(), blueValue.get(), 192).rgb
        var Borderx1 = 0
        var Bordery1 = 0
        var Borderx2 = 0
        var Bordery2 = 0

        if (GameInfo.get().equals("Default", true)) {
            Borderx1 += 0
            Bordery1 +=this.GameInfoRows* 18 + 12
            Borderx2 +=176
            Bordery2 +=78
            if(blur.get()) {
                GL11.glTranslated(-renderX, -renderY, 0.0)
//            GL11.glPushMatrix()
                BlurBuffer.blurArea(floatX, floatY + 8  , 176F, 70F )
//            GL11.glPopMatrix()
                GL11.glTranslated(renderX, renderY, 0.0)
            }
            RenderUtils.drawRect(0F, this.GameInfoRows * 18F + 25F, 176F, 80F, Color(redValue.get(), greenValue.get(), blueValue.get(), 0).rgb)
            RenderUtils.drawShadowWithCustomAlpha(0F, 12.5F, 176F, 64F, 255F)
            RenderUtils.drawRect(0F, 11.0F, 176F, 77F , Color(20, 19, 18, 170).rgb)
            fontValue.get().drawStringWithShadow("Session Info", 3, this.GameInfoRows * 18 + 14, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
            fontValue.get().drawStringWithShadow("FPS:" + Minecraft.getDebugFPS(), 7, this.GameInfoRows * 18 + 26, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
            fontValue.get().drawStringWithShadow("Ping:" + EntityUtils.getPing(mc2.player).toString(), 7, this.GameInfoRows * 18 + 36, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
            fontValue.get().drawStringWithShadow("X:" + Text.DECIMAL_FORMAT.format(mc.thePlayer!!.posX) + " " + "Y:" + Text.DECIMAL_FORMAT.format(mc.thePlayer!!.posY) + " " + "Z:" + Text.DECIMAL_FORMAT.format(mc.thePlayer!!.posZ), 7, this.GameInfoRows * 18 + 47, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
            fontValue.get().drawStringWithShadow("ServerIP:" + ServerUtils.getRemoteIp(), 7, this.GameInfoRows * 18 + 56, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
            fontValue.get().drawStringWithShadow("Kills:" + killCounts, 7, this.GameInfoRows * 18 + 68, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
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
                            Color(Gident.redValue.get(), Gident.greenValue.get(), Gident.blueValue.get()),
                            Color(Gident.redValue2.get(), Gident.greenValue2.get(), Gident.blueValue2.get(),1),
                            (Math.abs(
                                System.currentTimeMillis() / Gident.gidentspeed.get().toDouble() + i * distanceValue.get()
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
                            Color(Gident.redValue.get(), Gident.greenValue.get(), Gident.blueValue.get()),
                            Color(Gident.redValue2.get(), Gident.greenValue2.get(), Gident.blueValue2.get(), 1),
                            (Math.abs(
                                System.currentTimeMillis() / Gident.gidentspeed.get().toDouble() + i * distanceValue.get()
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
        if(GameInfo.get().equals("Flux", true)){
            if (blur.get()){
                GL11.glTranslated(-renderX, -renderY, 0.0)
                BlurBuffer.blurRoundArea(floatX, floatY + 10    , 120F, 65F, 7 )
                GL11.glTranslated(renderX, renderY, 0.0)
            }
            Borderx1 += 0
            Bordery1 +=this.GameInfoRows * 18 + 12
            Borderx2 +=150
            Bordery2 +=80
            RenderUtils.drawCircleRect(0F, 10F, 120F, 75F, 7f, Color(0, 0, 0, 120).rgb)
            Fonts.flux.drawString("F", 5F, 16F, -1)
            Fonts.font35.drawString("Play Time: ${DATE_FORMAT.format(Date(System.currentTimeMillis() - Recorder.startTime - 8000L * 3600L))}", 20F, 15F, -1)

            Fonts.flux.drawString("G", 5F, 31F, -1)
            Fonts.font35.drawString("Kills: $killCounts", 20F, 30F, -1)

            Fonts.flux.drawString("H", 5F, 46F, -1)
            Fonts.font35.drawString("Win / Total: ${Recorder.win} / ${Recorder.totalPlayed}", 20F, 45F, -1)

            Fonts.flux.drawString("I", 5F, 61F, -1)
            Fonts.font35.drawString("Speed:" + MovementUtils.getBlockSpeed(mc2.player) + "/bps", 20F, 60F, -1)
        }
        if (GameInfo.get().equals("Shadow", true)) {
            Borderx1 += 0
            Bordery1 +=this.GameInfoRows* 18 + 12
            Borderx2 +=176
            Bordery2 +=80
            if(blur.get()) {
                GL11.glTranslated(-renderX, -renderY, 0.0)
//            GL11.glPushMatrix()
                BlurBuffer.blurArea(floatX, floatY + 11  , 176F, 70F )
//            GL11.glPopMatrix()
                GL11.glTranslated(renderX, renderY, 0.0)
            }
            fontValue.get().drawStringWithShadow("Session Info", 3, this.GameInfoRows * 18 + 14, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
            fontValue.get().drawStringWithShadow("FPS:" + Minecraft.getDebugFPS(), 7, this.GameInfoRows * 18 + 26, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
            fontValue.get().drawStringWithShadow("Ping:" + EntityUtils.getPing(mc2.player).toString(), 7, this.GameInfoRows * 18 + 36, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
            fontValue.get().drawStringWithShadow("X:" + Text.DECIMAL_FORMAT.format(mc.thePlayer!!.posX) + " " + "Y:" + Text.DECIMAL_FORMAT.format(mc.thePlayer!!.posY) + " " + "Z:" + Text.DECIMAL_FORMAT.format(mc.thePlayer!!.posZ), 7, this.GameInfoRows * 18 + 47, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
            fontValue.get().drawStringWithShadow("ServerIP:" + ServerUtils.getRemoteIp(), 7, this.GameInfoRows * 18 + 56, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
            fontValue.get().drawStringWithShadow("Kills:" + killCounts, 7, this.GameInfoRows * 18 + 68, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
            RenderUtils.drawRect(0F, this.GameInfoRows * 18F + 25F, 176F, 80F, Color(redValue.get(), greenValue.get(), blueValue.get(), 0).rgb)
            RenderUtils.drawShadowWithCustomAlpha(0F, 11.0F, 176F, 70F , 255F)
//            RenderUtils.drawShadow(0, 11, 176F, 70F)
            Border(0F, this.GameInfoRows * 18F + 12F, 176F, 80F)
        }
        if(GameInfo.get().equals("Old", true)){
            Borderx1 += 0
            Bordery1 +=this.GameInfoRows * 18 + 12
            Borderx2 +=150
            Bordery2 +=80
            if(blur.get()){
                GL11.glTranslated(-renderX, -renderY, 0.0)
                BlurBuffer.blurArea(renderX.toFloat(), renderY.toFloat()+8,156F,78F )
                GL11.glTranslated(renderX, renderY, 0.0)
            }
            RenderUtils.drawShadowWithCustomAlpha(0F, 8F, 155F, 76F, 200F)
            RenderUtils.drawRect(0F, 8F, 156F,85F, Color(41, 41, 41, RectA.get()).rgb)
            icon.drawString("c", 47F, 2.5F + fontHeight + 6F, color)
            Fonts.bold35.drawStringWithShadow("Session Info", (50F + icon.getStringWidth("u")).toInt(),
                (this.GameInfoRows * 18F + 16).toInt(), Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
            Fonts.bold30.drawStringWithShadow("Speed: " + MovementUtils.getBlockSpeed(mc2.player),
                (5F + icon.getStringWidth("b")).toInt(),
                (this.GameInfoRows * 20F + 30).toInt(), Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
            Fonts.bold30.drawStringWithShadow("FPS: " + Minecraft.getDebugFPS(), (5F + icon.getStringWidth("e")).toInt(),
                (this.GameInfoRows * 20F + 43).toInt(), Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
            Fonts.bold30.drawStringWithShadow("Kills: " + killCounts, (5F + icon.getStringWidth("G")).toInt(),
                (this.GameInfoRows * 20F + 54).toInt(), Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
            Fonts.bold30.drawStringWithShadow("Played Time: ${DATE_FORMAT.format(Date(System.currentTimeMillis() - Recorder.startTime - 8000L * 3600L))}" ,
                (5F + icon.getStringWidth("G")).toInt(),
                (this.GameInfoRows * 20F + 66).toInt(), Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
        }
        if(GameInfo.get().equals("LoseLine", true)){
            if (blur.get()){
                GL11.glTranslated(-renderX, -renderY, 0.0)
                BlurBuffer.blurArea(floatX, floatY + 10.5F  , 150F, 70F )
                GL11.glTranslated(renderX, renderY, 0.0)
            }
            Borderx1 += 0
            Bordery1 +=this.GameInfoRows * 18 + 12
            Borderx2 +=150
            Bordery2 +=80
            RenderUtils.drawShadowWithCustomAlpha(0F, 10.5F, 150F, 70F, 200F)
            RenderUtils.drawRect(0F,11F,150F,12F, ColorUtils2.hslRainbow(10, indexOffset = 30).rgb)
            RenderUtils.drawRect(0F, this.GameInfoRows * 18F + 12, 150F, this.GameInfoRows * 18F + 25F, Color(0, 0, 0, 100).rgb)
            RenderUtils.drawRect(0F, this.GameInfoRows * 18F + 25F, 150F, 80F, Color(0, 0, 0, 100).rgb)
            icon.drawString("c", 3F, 2.5F + fontHeight + 6F, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
            icon.drawString("m", 3F, 15.9F + fontHeight + 6F, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
            icon.drawString("f", 3F, 28.5F + fontHeight + 6F, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
            icon.drawString("a", 3F, 39.5F + fontHeight + 6F, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
            icon.drawString("x", 3F, 52F + fontHeight + 6F, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
            fontValue.get().drawStringWithShadow("Session Info", (5F + icon.getStringWidth("u")).toInt(),
                (this.GameInfoRows * 18F + 16).toInt(), Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
            fontValue.get().drawStringWithShadow("BPS: " + calculateBPS(),
                (5F + icon.getStringWidth("b")).toInt(),
                (this.GameInfoRows * 18F + 30).toInt(), Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
            fontValue.get().drawStringWithShadow("FPS: " + Minecraft.getDebugFPS(), (5F + icon.getStringWidth("e")).toInt(),
                (this.GameInfoRows * 18F + 43).toInt(), Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
            fontValue.get().drawStringWithShadow("Kills: " +killCounts, (5F + icon.getStringWidth("G")).toInt(),
                (this.GameInfoRows * 18F + 54).toInt(), Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
            fontValue.get().drawStringWithShadow("Played Time: ${DATE_FORMAT.format(Date(System.currentTimeMillis() - Recorder.startTime - 8000L * 3600L))}" ,
                (5F + icon.getStringWidth("G")).toInt(),
                (this.GameInfoRows * 18F + 66).toInt(), Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
        }
        if(GameInfo.get().equals("Normal" , true)){


            val fontRenderer = fontValue.get()

            val y2 = fontRenderer.fontHeight * 5 + 11.0.toInt()
            val x2 = 140.0.toInt()
            Borderx1 += -2
            Bordery1 += -2
            Borderx2 += x2
            Bordery2 += y2

            val DATE_FORMAT = SimpleDateFormat("HH:mm:ss")
            if (blurValue.get()){

                GL11.glTranslated(-renderX, -renderY, 0.0)
                GL11.glPushMatrix()
                BlurBuffer.blurRoundArea(renderX.toFloat(), renderY.toFloat(), x2.toFloat(), y2.toFloat(), radiusValue.get().toInt())
                GL11.glPopMatrix()
                GL11.glTranslated(renderX, renderY, 0.0)
            }
//        BlurUtils.blurAreaRounded(-2f, -2f, x2.toFloat(), y2.toFloat(), radiusValue.get(),blurStrength.get())
            RenderUtils.drawShadowWithCustomAlpha(-2f,-2f, (x2.toInt()+2).toFloat(), (y2.toInt()+2).toFloat(),255f)
            RenderUtils.drawRoundedRect(-2f, -2f, x2.toFloat(), y2.toFloat(),
                radiusValue.get().toInt(), Color(bgredValue.get(), bggreenValue.get(), bgblueValue.get(), bgalphaValue.get()).rgb)
            if(lineValue.get()) {

                val hud = LiquidBounce.moduleManager.getModule(HUD::class.java) as HUD
                RenderUtils.drawGradientSideways(
                    2.44,
                    fontRenderer.fontHeight + 2.5 + 0.0,
                    138.0 + -2.44f,
                    fontRenderer.fontHeight + 2.5 + 1.16f,Color(Gident.redValue.get(),Gident.greenValue.get(),Gident.blueValue.get()).rgb, Color(Gident.redValue2.get(),Gident.greenValue2.get(),Gident.blueValue2.get()).rgb)
            }


            fontRenderer.drawCenteredString("Session Info", x2.toFloat() / 2f, 3f, Color.WHITE.rgb, true)
            fontRenderer.drawStringWithShadow("Play Time: ${DATE_FORMAT.format(Date(System.currentTimeMillis() - Recorder.startTime - 8000L * 3600L))}", 2, (fontRenderer.fontHeight + 8f).toInt(), Color.WHITE.rgb)
            fontRenderer.drawStringWithShadow("Players Killed: " + killCounts, 2,
                (fontRenderer.fontHeight * 2 + 8f).toInt(), Color.WHITE.rgb)
            fontRenderer.drawStringWithShadow("Win: " + Recorder.totalPlayed, 2,
                (fontRenderer.fontHeight * 3 + 8f).toInt(), Color.WHITE.rgb)
            fontRenderer.drawStringWithShadow("Total: " +Recorder.totalPlayed , 2,
                (fontRenderer.fontHeight * 4 + 8f).toInt(), Color.WHITE.rgb)
            return Border(-2f, -2f, x2.toFloat(), y2.toFloat())

        }
        if(GameInfo.get().equals("Round" , true)){
            val font = Fonts.bold30
            Borderx1 += 0
            Bordery1 += 0
            Borderx2 += 150
            Bordery2 += 3 + fontHeight + font.fontHeight * 3 + 30
            var HM = 0
            var S = 0
            var M = 0
            var H = 0
            HM += 1
            if (HM == 120){
                S += 1
                HM = 0
            }
            if (S == 60){
                M += 1
                S = 0
            }
            if (M == 60){
                H += 1
                M = 0
            }

            val color = Color.WHITE.rgb
            val fontHeight = Fonts.bold30.fontHeight
            val format = DecimalFormat("#.##")
            if(blur.get()) {
                GL11.glTranslated(-renderX, -renderY, 0.0)
                GL11.glPushMatrix()
                BlurBuffer.blurRoundArea(
                    renderX.toFloat(),
                    renderY.toFloat(),
                    150F,
                    3F + fontHeight + font.fontHeight * 3 + 30F,
                    radiusValue.get().toInt()
                )
                GL11.glPopMatrix()
                GL11.glTranslated(renderX, renderY, 0.0)
            }
            VisualUtils.drawCircleRect(
                0F,
                0F,
                150F,
                3F + fontHeight + font.fontHeight * 3 + 30F,
                raduiValue.get().toFloat(),
                Color(0,0,0, alphaValue.get()).rgb
            )
            // title
            Fonts.bold40.drawString("Session State", 5F, 3F, color)

            font.drawString("Played Time", 7F, 3F + fontHeight + 5F, color)
            font.drawString("Speed", 7F, 3F + fontHeight + font.fontHeight + 10F, color)
            font.drawString("Ping", 7F , 3F + fontHeight + font.fontHeight * 2 + 15F, color)
            font.drawString("Kills", 7F, 3F + fontHeight + font.fontHeight * 3 + 20F, color)


            // info
            font.drawString(
                "${H}h ${M}m ${S}s",
                150 - font.getStringWidth("${H}h ${M}m ${S}s") - 5F,
                3F + fontHeight + 5F,
                color
            )

            font.drawString(
                format.format(MovementUtils.bps),
                150 - font.getStringWidth(format.format(MovementUtils.bps)) - 5F,
                3F + fontHeight + font.fontHeight + 10F,
                color
            )

            if (Minecraft.getMinecraft().isSingleplayer) {
                font.drawString(
                    "0ms (Singleplayer)",
                    150 - font.getStringWidth("0ms (Singleplayer)") - 5F,
                    3F + fontHeight + font.fontHeight * 2 + 15F,
                    color
                )
            } else font.drawString(
                mc.netHandler.getPlayerInfo(Minecraft.getMinecraft().player.uniqueID)!!.responseTime.toString(),
                150 - font.getStringWidth(mc.netHandler.getPlayerInfo(Minecraft.getMinecraft().player.uniqueID)!!.responseTime.toString()) - 5F,
                3F + fontHeight + font.fontHeight * 2 + 15F,
                color
            )
            font.drawString(
                "${KillAura.killCounts}",
                150 - font.getStringWidth("${KillAura.killCounts}") - 5F,
                3F + fontHeight + font.fontHeight * 3 + 20F,
                color
            )
        }
        return   Border(Borderx1.toFloat(), Bordery1.toFloat(), Borderx2.toFloat(), Bordery2.toFloat())
    }
    fun calculateBPS(): Double {
        if(mc.thePlayer != null) {
            val bps = Math.hypot(
                mc.thePlayer!!.posX - mc.thePlayer!!.prevPosX,
                mc.thePlayer!!.posZ - mc.thePlayer!!.prevPosZ
            ) * mc.timer.timerSpeed * 20
            return Math.round(bps * 100.0) / 100.0
        }else{
            return 0.00
        }

    }
}
