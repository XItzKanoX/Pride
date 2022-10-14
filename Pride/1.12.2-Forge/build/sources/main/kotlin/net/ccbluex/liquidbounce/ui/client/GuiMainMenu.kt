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
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import java.awt.Color

class GuiMainMenu : WrappedGuiScreen() {
    var arrayList = ArrayList<me.ui.IGuiButton>()
    override fun initGui() {
        val defaultHeight = representedScreen.height / 4 + 48

        representedScreen.buttonList.add(classProvider.createGuiButton(100, representedScreen.width / 2 - 49, defaultHeight + 24 *  3, 120, 20, "AltManager"))
        representedScreen.buttonList.add(classProvider.createGuiButton(102, representedScreen.width / 2 - 49, defaultHeight + 24 * 2 , 120, 20, "Background"))

        representedScreen.buttonList.add(classProvider.createGuiButton(1, representedScreen.width / 2 - 49, defaultHeight  , 120, 20, "SinglePlayer"))
        representedScreen.buttonList.add(classProvider.createGuiButton(2, representedScreen.width / 2 - 49, defaultHeight + 24 , 120, 20,"MulitPlayer"))
        // Minecraft Realms
        //		this.buttonList.add(new classProvider.createGuiButton(14, this.width / 2 - 100, j + 24 * 2, I18n.format("menu.online", new Object[0])));
        representedScreen.buttonList.add(classProvider.createGuiButton(4, representedScreen.width / 2 - 49, defaultHeight + 24 * 4, 120, 20, "Quit"))
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        representedScreen.drawBackground(0)

        var length = 2
        val list = LiquidBounce.UPDATE_LIST

        for ((i, _: String) in list.withIndex()) {
            length += if (i <= 0) Fonts.font40.fontHeight + 2 else Fonts.font35.fontHeight + 2
        }

        RenderUtils.drawRoundRect(0F, 0F, 115F, length + 2F, 3F, Color(0, 0, 0, 100).rgb)

        length = 2

        for ((i, _: String) in list.withIndex()) {
            length += if (i <= 0) {
                Fonts.font40.drawString(list[i], 2, length, Color(255, 255, 255, 255).rgb)
                Fonts.font40.fontHeight + 2
            } else {
                Fonts.font35.drawString(list[i], 2, length, Color(255, 255, 255, 255).rgb)
                Fonts.font35.fontHeight + 2
            }
        }

        representedScreen.superDrawScreen(mouseX, mouseY, partialTicks)
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