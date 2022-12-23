/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketPlayer
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.inventory.GuiContainer
import org.lwjgl.input.Keyboard

@ModuleInfo(name = "InvMove", description = "Allows you to walk while an inventory is opened.", category = ModuleCategory.MOVEMENT)
class InventoryMove : Module() {
    private val noDetectableValue = BoolValue("NoDetectable", false)
    private val rotateValue = BoolValue("Rotate", true)
    val aacAdditionProValue = BoolValue("AACAdditionPro", false)
    private val noMoveClicksValue = BoolValue("NoMoveClicks", false)

    private val blinkPacketList = mutableListOf<ICPacketPlayer>()
    var lastInvOpen = false
        private set
    var invOpen = false
        private set

    private fun updateKeyState() {
        if (mc2.currentScreen != null && mc2.currentScreen !is GuiChat && (!noDetectableValue.get() || mc2.currentScreen !is GuiContainer)) {
            mc.gameSettings.keyBindForward.pressed = mc.gameSettings.isKeyDown(mc.gameSettings.keyBindForward)
            mc.gameSettings.keyBindBack.pressed = mc.gameSettings.isKeyDown(mc.gameSettings.keyBindBack)
            mc.gameSettings.keyBindRight.pressed = mc.gameSettings.isKeyDown(mc.gameSettings.keyBindRight)
            mc.gameSettings.keyBindLeft.pressed = mc.gameSettings.isKeyDown(mc.gameSettings.keyBindLeft)
            mc.gameSettings.keyBindJump.pressed = mc.gameSettings.isKeyDown(mc.gameSettings.keyBindJump)
            mc.gameSettings.keyBindSprint.pressed = mc.gameSettings.isKeyDown(mc.gameSettings.keyBindSprint)

            if (rotateValue.get()) {
                if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
                    if (mc.thePlayer!!.rotationPitch > -90) {
                        mc.thePlayer!!.rotationPitch -= 5
                    }
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
                    if (mc.thePlayer!!.rotationPitch < 90) {
                        mc.thePlayer!!.rotationPitch += 5
                    }
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
                    mc.thePlayer!!.rotationYaw -= 5
                }
                if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
                    mc.thePlayer!!.rotationYaw += 5
                }
            }
        }
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        updateKeyState()
    }

    @EventTarget
    fun onScreen(event: ScreenEvent) {
        updateKeyState()
    }

    @EventTarget
    fun onClick(event: ClickWindowEvent) {
        if (noMoveClicksValue.get() && MovementUtils.isMoving) {
            event.cancelEvent()
        }
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        blinkPacketList.clear()
        invOpen = false
        lastInvOpen = false
    }

    override fun onDisable() {
        if (!mc.gameSettings.isKeyDown(mc.gameSettings.keyBindForward) || mc.currentScreen != null) {
            mc.gameSettings.keyBindForward.pressed = false
        }
        if (!mc.gameSettings.isKeyDown(mc.gameSettings.keyBindBack) || mc.currentScreen != null) {
            mc.gameSettings.keyBindBack.pressed = false
        }
        if (!mc.gameSettings.isKeyDown(mc.gameSettings.keyBindRight) || mc.currentScreen != null) {
            mc.gameSettings.keyBindRight.pressed = false
        }
        if (!mc.gameSettings.isKeyDown(mc.gameSettings.keyBindLeft) || mc.currentScreen != null) {
            mc.gameSettings.keyBindLeft.pressed = false
        }
        if (!mc.gameSettings.isKeyDown(mc.gameSettings.keyBindJump) || mc.currentScreen != null) {
            mc.gameSettings.keyBindJump.pressed = false
        }
        if (!mc.gameSettings.isKeyDown(mc.gameSettings.keyBindSprint) || mc.currentScreen != null) {
            mc.gameSettings.keyBindSprint.pressed = false
        }

    }

    override val tag: String?
        get() = if (aacAdditionProValue.get()) "AACAdditionPro" else "Bypass"
}
