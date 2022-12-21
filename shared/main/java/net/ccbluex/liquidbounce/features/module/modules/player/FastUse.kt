/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player

import me.utils.PacketUtils
import me.utils.player.PlayerUtil.isMoving
import me.utils.timer.TimeHelper
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.item.Item
import net.minecraft.item.ItemFood
import net.minecraft.item.ItemPotion
import net.minecraft.network.play.client.CPacketPlayer

@ModuleInfo(name = "FastUse", description = "Allows you to use items faster.", category = ModuleCategory.PLAYER)
class FastUse : Module() {

    private val modeValue = ListValue("Mode", arrayOf("Instant", "NCP", "AAC", "Custom", "Vulcan", "Hypixel", "Hyt"), "NCP")

    private val noMoveValue = BoolValue("NoMove", false)

    private val delayValue = IntegerValue("CustomDelay", 0, 0, 300)
    private val customSpeedValue = IntegerValue("CustomSpeed", 2, 1, 35)
    private val customTimer = FloatValue("CustomTimer", 1.1f, 0.5f, 2f)
    private val stopMoving = BoolValue("StopMoving", false)
    private val msTimer = MSTimer()
    private var usedTimer = false
    private var fixed = false
    private var packet = 0
    private val time = TimeHelper()

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if(event.isPre()) {
            if (mc2.player!!.inventory.getCurrentItem() != null) {
                if (mc.gameSettings.keyBindUseItem.isKeyDown && (!stopMoving.get() || !isMoving())) {
                    val heldItem: Item? = mc2.player!!.inventory.getCurrentItem().item
                    if (heldItem is ItemFood || heldItem is ItemPotion) {
                        fixed = false
                        when (modeValue.get().toLowerCase()) {
                            "vulcan" -> {
                                if (packet != 16) {
                                    if (mc.thePlayer!!.ticksExisted % 2 === 0) {
                                        mc.timer.timerSpeed = 0.33f
                                    } else {
                                        mc.timer.timerSpeed = 1f
                                    }
                                    PacketUtils.sendPacketNoEvent(CPacketPlayer(true))
                                    packet++
                                } else {
                                    mc.playerController.onStoppedUsingItem(mc.thePlayer!!)
                                    mc.gameSettings.keyBindUseItem.pressed = false
                                }
                            }
                            "hypixel" -> {
                                if (packet != 20) {
                                    mc.timer.timerSpeed = 0.5f
                                    mc.thePlayer!!.sendQueue.networkManager.sendPacket(classProvider.createCPacketPlayer(true))
                                    packet++
                                } else {
                                    mc.playerController.onStoppedUsingItem(mc.thePlayer!!)
                                }
                            }
                        }
                    }
                }
            }
            if (!mc.gameSettings.keyBindUseItem.isKeyDown) {
                if (!fixed) {
                    packet = 0
                    mc.timer.timerSpeed = 1f
                    fixed = true
                    time.reset()
                }
            }
        }
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (usedTimer) {
            mc.timer.timerSpeed = 1F
            usedTimer = false
        }

        if (!thePlayer.isUsingItem) {
            msTimer.reset()
            return
        }

        val usingItem = thePlayer.itemInUse!!.item

        if (classProvider.isItemFood(usingItem) || classProvider.isItemBucketMilk(usingItem) || classProvider.isItemPotion(usingItem)) {
            when (modeValue.get().toLowerCase()) {
                "instant" -> {
                    repeat(35) {
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayer(thePlayer.onGround))
                    }

                    mc.playerController.onStoppedUsingItem(thePlayer)
                }
                "hyt"->{
                    mc.timer.timerSpeed = 0.65F
                    usedTimer = true
                    if (thePlayer.onGround) {
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosLook(mc.thePlayer!!.posX, mc.thePlayer!!.posY + 0.0019, mc.thePlayer!!.posZ, thePlayer.rotationYaw, thePlayer.rotationPitch, mc.thePlayer!!.onGround))
                    }
                }
                "ncp" -> if (thePlayer.itemInUseDuration > 14) {
                    repeat(20) {
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayer(thePlayer.onGround))
                    }

                    mc.playerController.onStoppedUsingItem(thePlayer)
                }

                "aac" -> {
                    mc.timer.timerSpeed = 1.22F
                    usedTimer = true
                }
                
                "custom" -> {
                    mc.timer.timerSpeed = customTimer.get()
                    usedTimer = true

                    if (!msTimer.hasTimePassed(delayValue.get().toLong()))
                        return

                    repeat(customSpeedValue.get()) {
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayer(thePlayer.onGround))
                    }

                    msTimer.reset()
                }
            }
        }
    }

    @EventTarget
    fun onMove(event: MoveEvent?) {
        val thePlayer = mc.thePlayer

        if (thePlayer == null || event == null)
            return
        if (!state || !thePlayer.isUsingItem || !noMoveValue.get())
            return

        val usingItem = thePlayer.itemInUse!!.item

        if (classProvider.isItemFood(usingItem) || classProvider.isItemBucketMilk(usingItem) || classProvider.isItemPotion(usingItem))
            event.zero()
    }

    override fun onDisable() {
        if (usedTimer) {
            mc.timer.timerSpeed = 1F
            usedTimer = false
        }
    }

    override val tag: String?
        get() = modeValue.get()
}
