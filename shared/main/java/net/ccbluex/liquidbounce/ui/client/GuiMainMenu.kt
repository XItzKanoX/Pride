/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.ui.client


import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.client.gui.IGuiButton
import net.ccbluex.liquidbounce.api.util.WrappedGuiScreen
import net.ccbluex.liquidbounce.ui.client.altmanager.GuiAltManager
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.RandomImgUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.Translate
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import java.awt.Color
import java.util.*

class GuiMainMenu : WrappedGuiScreen() {
    var arrayList = ArrayList<me.ui.IGuiButton>()
    var translate: Translate? = null
    private var currentX = 0f
    private var currentY = 0f
    override fun initGui() {

        arrayList.clear()

        arrayList.add(me.ui.IGuiButton("Single Player", classProvider.createGuiSelectWorld(this.representedScreen), "single.png"))
        arrayList.add(me.ui.IGuiButton(" Mulit Player", classProvider.createGuiMultiplayer(this.representedScreen), "multi.png"))
        arrayList.add(me.ui.IGuiButton(" BackGround", classProvider.wrapGuiScreen(GuiBackground(this.representedScreen)), "background.png"))
        arrayList.add(me.ui.IGuiButton("Alt Manager", classProvider.wrapGuiScreen(GuiAltManager(this.representedScreen)), "alt.png"))
        arrayList.add(me.ui.IGuiButton("Options", classProvider.createGuiOptions(this.representedScreen, mc.gameSettings), "option.png"))
//        arrayList.add(me.ui.IGuiButton("Quit Game", null, "quit.png"))
//        translate = Translate(0f, 0f).


    }



    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        val h = representedScreen.height
        val w = representedScreen.width
        val sr = ScaledResolution(mc2)
        val res = ScaledResolution(mc2  );
        val xDiff: Float = ((mouseX - h / 2).toFloat() - this.currentX) / res.scaleFactor.toFloat()
        val yDiff: Float = ((mouseY - w / 2).toFloat() - this.currentY) / res.scaleFactor.toFloat()
        this.currentX += xDiff * 0.3f
        this.currentY += yDiff * 0.3f
        GlStateManager.translate(this.currentX / 30.0f, this.currentY / 15.0f, 0.0f)
        RenderUtils.drawImage4(RandomImgUtils.getBackGround(), -30, -30, res.scaledWidth + 60, res.scaledHeight + 60)
        GlStateManager.translate(-this.currentX / 30.0f, -this.currentY / 15.0f, 0.0f)

//        representedScreen.drawBackground(0)

        var length = 2
        val list = LiquidBounce.UPDATE_LIST

//        for ((i, _: String) in list.withIndex()) {
//            length += if (i <= 0) Fonts.font40.fontHeight + 2 else Fonts.font35.fontHeight + 2
//        }

        var width = representedScreen.width
        var height = representedScreen.height
        length = 2
        var defaultHeight = representedScreen.height / 4 + 48
        Fonts.bold40.drawString(
            "Welcome, User",
            width - Fonts.bold40.getStringWidth("Welcome, User") - 4f,
            height - 12f,
            Color(255, 255, 255, 200).rgb,
            true
        )
        Fonts.bold40.drawString("Pride Minecraft 1.12.2", 4f, height - 12f, Color(255, 255, 255, 200).rgb, true)





//        for ((i, _: String) in list.withIndex()) {
//            length += if (i <= 0) {
//                Fonts.ComfortaaRegular35.drawString(list[i], 2F, length.toFloat(), Color(255, 255, 255, 255).rgb, true)
//                Fonts.ComfortaaRegular35.fontHeight + 2
//            } else {
//                Fonts.ComfortaaRegular35.drawString(list[i], 2F, length.toFloat(), Color(255, 255, 255, 255).rgb, true)
//                Fonts.ComfortaaRegular35.fontHeight + 2
//            }
//        }

        for (button in arrayList) {

            button.draw(width / 2  - 60, defaultHeight - 24 , mouseX, mouseY)

            representedScreen.superDrawScreen(mouseX, mouseY, partialTicks)
            defaultHeight += 28

        }
//        RenderUtils.drawRect(width / 2 - 300f, defaultHeight + 24 * 2f,0f , 0f,Color(255, 255 ,255, 140))
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        for (button in arrayList) {
            button.onClick()
        }
    }



    override fun actionPerformed(button: IGuiButton) {
        when (button.id) {
            0 -> mc.displayGuiScreen(classProvider.createGuiOptions(this.representedScreen, mc.gameSettings))
            1 -> mc.displayGuiScreen(classProvider.createGuiSelectWorld(this.representedScreen))
            2 -> mc.displayGuiScreen(classProvider.createGuiMultiplayer(this.representedScreen))
            4 -> mc.shutdown()
            100 -> mc.displayGuiScreen(classProvider.wrapGuiScreen(GuiAltManager(this.representedScreen)))
            101 -> mc.displayGuiScreen(classProvider.wrapGuiScreen(GuiServerStatus(this.representedScreen)))
            102 -> mc.displayGuiScreen(classProvider.wrapGuiScreen(GuiBackground(this.representedScreen)))
            103 -> mc.displayGuiScreen(classProvider.wrapGuiScreen(GuiModsMenu(this.representedScreen)))
            108 -> mc.displayGuiScreen(classProvider.wrapGuiScreen(GuiContributors(this.representedScreen)))
        }
    }
}