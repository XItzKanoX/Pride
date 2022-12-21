package net.ccbluex.liquidbounce.features.module.modules.movement


import me.utils.PacketUtils.sendPacketNoEvent
import me.utils.player.PlayerUtil
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.enums.EnumFacingType
import net.ccbluex.liquidbounce.api.enums.ItemType
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketHeldItemChange
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketPlayerDigging
import net.ccbluex.liquidbounce.api.minecraft.util.IEnumFacing
import net.ccbluex.liquidbounce.api.minecraft.util.IScaledResolution
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.item.ArmorPiece
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.init.Items
import net.minecraft.item.ItemBow
import net.minecraft.item.ItemStack
import net.minecraft.network.play.client.CPacketHeldItemChange
import net.minecraft.network.play.client.CPacketPlayer

import net.minecraft.util.EnumFacing
import java.awt.Color

@ModuleInfo(name = "BowJump", category = ModuleCategory.MOVEMENT, description = "new")
class BowJump : Module() {
    private val boostValue = FloatValue("Boost", 4.25f, 0f, 10f)
    private val heightValue = FloatValue("Height", 0.42f, 0f, 10f)
    private val timerValue = FloatValue("Timer", 1f, 0.1f, 10f)
    private val delayBeforeLaunch = IntegerValue("DelayBeforeArrowLaunch", 1, 1, 20)
    private val AutoDisable = BoolValue("AutoDisable", true)
    private val renderValue = BoolValue("RenderStatus", true)
    private var bowState = 0
    private var lastPlayerTick: Long = 0
    private var lastSlot = -1
    override fun onEnable() {
        if (mc.thePlayer == null) return
        bowState = 0
        lastPlayerTick = -1
        lastSlot = mc.thePlayer!!.inventory.currentItem
        strafe(0f)
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        if (mc.thePlayer!!.onGround && bowState < 3) event.cancelEvent()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (classProvider.isCPacketHeldItemChange(packet)) {
            lastSlot = packet.asCPacketHeldItemChange().slotId
            event.cancelEvent()
        }
        if (classProvider.isCPacketPlayer(packet)) {
            if (bowState < 3) packet.asCPacketPlayer().ismoving= false
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mc.timer.timerSpeed = 1f
        var forceDisable = false
        when (bowState) {
            0 -> {
                val slot = bowSlot
                if (slot < 0 || !mc.thePlayer!!.inventory.mainInventory.contains(classProvider.createItemStack(classProvider.getItemEnum(ItemType.BOWL)))) {
                    LiquidBounce.hud.addNotification(Notification("Bow Jump"," No arrows or bow found in your inventory!", NotifyType.WARNING, 3500, 1500))
                    forceDisable = true
                    bowState = 5
                } else if (lastPlayerTick == -1L) {
                    mc.thePlayer!!.inventoryContainer.getSlot(slot + 36).stack
                    if (lastSlot != slot) sendPacketNoEvent(CPacketHeldItemChange(slot))
                    mc.netHandler.addToSendQueue(
                        classProvider.createCPacketPlayerBlockPlacement(
                            WBlockPos(-1, -1, -1),
                            255,
                            mc.thePlayer!!.inventoryContainer.getSlot(slot + 36).stack,
                            0F,
                            0F,
                            0F
                        )
                    )
                    lastPlayerTick = mc.thePlayer!!.ticksExisted.toLong()
                    bowState = 1
                }
            }
            1 -> {
                val reSlot = bowSlot
                if (mc.thePlayer!!.ticksExisted - lastPlayerTick > delayBeforeLaunch.get()) {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerLook(mc.thePlayer!!.rotationYaw, -90F, mc.thePlayer!!.onGround))
                    mc.netHandler.addToSendQueue(
                        classProvider.createCPacketPlayerDigging(
                            ICPacketPlayerDigging.WAction.RELEASE_USE_ITEM,
                            WBlockPos.ORIGIN,
                            classProvider.getEnumFacing(EnumFacingType.DOWN)
                        )
                    )
                    if (lastSlot != reSlot)  mc.netHandler.addToSendQueue(classProvider.createCPacketHeldItemChange(lastSlot))
                    bowState = 2
                }
            }
            2 -> if (mc.thePlayer!!.hurtTime > 0) bowState = 3
            3 -> {
                strafe(boostValue.get())
                mc.thePlayer!!.motionY = heightValue.get().toDouble()
                bowState = 4
                lastPlayerTick = mc.thePlayer!!.ticksExisted.toLong()
            }
            4 -> {
                mc.timer.timerSpeed = timerValue.get()
                if (mc.thePlayer!!.onGround && mc.thePlayer!!.ticksExisted - lastPlayerTick >= 1) bowState = 5
            }
        }
        if (bowState < 3) {
            mc2.player.movementInput.moveStrafe = 0f
            mc2.player.movementInput.moveStrafe = 0f
        }
        if (bowState == 5 && (AutoDisable.get() || forceDisable)) state = false
    }

    @EventTarget
    fun onWorld() {
        state = false //prevent weird things
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1.0f
        mc.thePlayer!!.speedInAir = 0.02f
    }

    private val bowSlot: Int
        get() {
            for (i in 36..44) {
                val stack = mc.thePlayer!!.inventoryContainer.getSlot(i).stack
                if (stack != null && stack.item is ItemBow) {
                    return i - 36
                }
            }
            return -1
        }

    @EventTarget
    fun onRender2D() {
        if (!renderValue.get()) return
        val scaledRes = classProvider.createScaledResolution(mc)
        val width = bowState.toFloat() / 5f * 60f
        Fonts.font25.drawCenteredString(
            bowStatus,
            scaledRes.scaledWidth / 2f,
            scaledRes.scaledHeight / 2f + 14f,
            -1,
            true
        )
        RenderUtils.drawRect(
            scaledRes.scaledWidth / 2f - 31f,
            scaledRes.scaledHeight / 2f + 25f,
            scaledRes.scaledWidth / 2f + 31f,
            scaledRes.scaledHeight / 2f + 29f,
            -0x60000000
        )
        RenderUtils.drawRect(
            scaledRes.scaledWidth / 2f - 30f,
            scaledRes.scaledHeight / 2f + 26f,
            scaledRes.scaledWidth / 2f - 30f + width,
            scaledRes.scaledHeight / 2f + 28f,
            statusColor
        )
    }

    private val bowStatus: String
        get() = when (bowState) {
            0 -> "Idle..."
            1 -> "Preparing..."
            2 -> "Waiting for damage..."
            3, 4 -> "Boost!"
            else -> "Task completed."
        }
    private val statusColor: Color
        get() {
            return when (bowState) {
                0 -> Color(21, 21, 21)
                1 -> Color(48, 48, 48)
                2 -> Color.yellow
                3, 4 -> Color.green
                else -> Color(0, 111, 255)
            }
        }
}