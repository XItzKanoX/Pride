/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue

@ModuleInfo(name = "Criticals", description = "Automatically deals critical hits.", category = ModuleCategory.COMBAT)
class Criticals : Module() {

    val modeValue = ListValue("Mode", arrayOf("HuaYuTing","sb","NewPacket","HytSpartan","AAC4Hyt", "Lite", "HytTest", "HytVulcan",  "Vulcan", "vulcanfake", "Spartan", "StarPacket", "Packet", "NcpPacket", "NoGround", "Hop", "TPHop", "Jump", "LowJump", "Visual"), "Packet")
    val delayValue = IntegerValue("Delay", 0, 0, 500)
    private val lookValue = BoolValue("SendC06", false)
    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)
    var n = 0
    var attacks = 0
    val msTimer = MSTimer()
    private fun sendCriticalPacket(
        xOffset: Double = 0.0,
        yOffset: Double = 0.0,
        zOffset: Double = 0.0,
        ground: Boolean
    ) {
        val x = mc.thePlayer!!.posX + xOffset
        val y = mc.thePlayer!!.posY + yOffset
        val z = mc.thePlayer!!.posZ + zOffset
        if (lookValue.get()) {
            mc.netHandler.addToSendQueue(
                classProvider.createCPacketPlayerPosLook(
                    x,
                    y,
                    z,
                    mc.thePlayer!!.rotationYaw,
                    mc.thePlayer!!.rotationPitch,
                    ground
                )
            )
        } else {
            mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y, z, ground))
        }
    }


    override fun onEnable() {
        if (modeValue.get().equals("NoGround", ignoreCase = true))
            mc.thePlayer!!.jump()
    }

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (classProvider.isEntityLivingBase(event.targetEntity)) {
            val thePlayer = mc.thePlayer ?: return
            val entity = event.targetEntity!!.asEntityLivingBase()

            if (!thePlayer.onGround || thePlayer.isOnLadder || thePlayer.isInWeb || thePlayer.isInWater ||
                thePlayer.isInLava || thePlayer.ridingEntity != null || entity.hurtTime > hurtTimeValue.get() ||
                LiquidBounce.moduleManager[Fly::class.java].state || !msTimer.hasTimePassed(delayValue.get().toLong()))
                return

            val x = thePlayer.posX
            val y = thePlayer.posY
            val z = thePlayer.posZ

            when (modeValue.get().toLowerCase()) {
                "huayuting" -> {
                    attacks++
                    val normalOffset = doubleArrayOf(0.12, 0.023, 0.3)
                    if (attacks > 2) {
                        thePlayer.sprinting = false
                        thePlayer.serverSprintState = false
                        for (offSet in normalOffset) {
                            sendCriticalPacket(yOffset = offSet, ground = false)
                            attacks = 0
                        }
                    }else if(attacks == 0){
                        thePlayer.sprinting = true
                        thePlayer.serverSprintState = true
                    }
                }

                "sb"->{
                    n = this.attacks;
                    this.attacks = n + 1;
                    attacks++
                    if (attacks >= 5) {
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 0.0114514, z, false))
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 0.001919, z, false))
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 0.00, z, false))
                        this.attacks = 0;
                    }
                }
                "vulcanfake" -> {
                    val motionX: Double
                    val motionZ: Double
                    if (MovementUtils.isMoving) {
                        motionX = mc.thePlayer!!.motionX
                        motionZ = mc.thePlayer!!.motionZ
                    } else {
                        motionX = 0.00
                        motionZ = 0.00
                    }
                    sendCriticalPacket(xOffset = motionX / 3, yOffset = 0.20000004768372, zOffset = motionZ / 3, ground = false)
                    sendCriticalPacket(xOffset = motionX / 1.5, yOffset = 0.12160004615784, zOffset = motionZ / 1.5, ground = false)
                }
                "hyttest" -> {
                     mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(
                        mc.thePlayer!!.posX,
                        mc.thePlayer!!.posY + 0.00001100134977413,
                        mc.thePlayer!!.posZ,
                        false
                    ))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(
                        mc.thePlayer!!.posX,
                        mc.thePlayer!!.posY + 0.00000000013487744,
                        mc.thePlayer!!.posZ,
                        false
                    ))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(
                        mc.thePlayer!!.posX,
                        mc.thePlayer!!.posY + 0.00000571003114589,
                        mc.thePlayer!!.posZ,
                        false
                    ))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(
                        mc.thePlayer!!.posX,
                        mc.thePlayer!!.posY + 0.00000001578887744,
                        mc.thePlayer!!.posZ,
                        false
                    ))
                }
                "hytvulcan" -> {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y +  0.000511322554, z, false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 0.00011119999543618, z, false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 0.00006221,z, false))
                }
                "vulcan" -> {
                    attacks++
                    if (attacks > 6) {
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x,y+0.2,z,false))
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x,y+0.1216,z,false))
                        attacks = 0
                    }
                }
                "aac4hyt"->{
                    attacks++
                    if (attacks > 5) {
                        sendCriticalPacket(yOffset = 0.0114514, ground = false)
                        sendCriticalPacket(yOffset = 0.0019 ,ground = false)
                        sendCriticalPacket(yOffset = 0.000001 ,ground = false)
                        attacks = 0
                    }
                }
                "hytspartan" -> {
                    n = this.attacks;
                    this.attacks = n + 1;
                    if (this.attacks <= 6){

                    }else{
                        mc.netHandler.addToSendQueue(MinecraftInstance.classProvider.createCPacketPlayerPosition(x, y + 0.01, z, false));
                        mc.netHandler.addToSendQueue(MinecraftInstance.classProvider.createCPacketPlayerPosition(x, y + 1.0E-10, z, false));
                        mc.netHandler.addToSendQueue(MinecraftInstance.classProvider.createCPacketPlayerPosition(x, y + 0.114514, z, false));
                        this.attacks = 0;
                    }
                }
                "spartan" -> {
                    val motionX: Double
                    val motionZ: Double
                    if (MovementUtils.isMoving) {
                        motionX = mc.thePlayer!!.motionX
                        motionZ = mc.thePlayer!!.motionZ
                    } else {
                        motionX = 0.00
                        motionZ = 0.00
                    }
                    sendCriticalPacket(xOffset = motionX / 3, yOffset = 0.20000004768372, zOffset = motionZ / 3, ground = false)
                    sendCriticalPacket(xOffset = motionX / 1.5, yOffset = 0.12160004615784, zOffset = motionZ / 1.5, ground = false)
                }
                "starpacket" -> {
                    sendCriticalPacket(yOffset = 0.0000066666, ground = false) // ����ţ�ƻ���
                    sendCriticalPacket(yOffset = 0.00000078, ground = false) // ����Ǹ輧��
                    sendCriticalPacket(yOffset = 0.00000114514, ground = false) // ̫����
                    sendCriticalPacket(ground = false)
                }
                "newpacket" -> {
                    sendCriticalPacket(yOffset = 0.05250000001304, ground = true)
                    sendCriticalPacket(yOffset = 0.00150000001304, ground = false)
                    sendCriticalPacket(yOffset = 0.01400000001304, ground = false)
                    sendCriticalPacket(yOffset = 0.00150000001304, ground = false)
                }
                "lite" -> {
                    sendCriticalPacket(yOffset = 0.015626, ground = false)
                    sendCriticalPacket(yOffset = 0.00000000343, ground = false)
                }
                "packet" -> {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 0.0625, z, true))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y, z, false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 1.1E-5, z, false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y, z, false))
                    thePlayer.onCriticalHit(entity)
                }
                "ncppacket" -> {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 0.11, z, false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 0.1100013579, z, false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 0.0000013579, z, false))
                    thePlayer.onCriticalHit(entity)
                }

                "hop" -> {
                    thePlayer.motionY = 0.1
                    thePlayer.fallDistance = 0.1f
                    thePlayer.onGround = false
                }

                "tphop" -> {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 0.02, z, false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 0.01, z, false))
                    thePlayer.setPosition(x, y + 0.01, z)
                }
                "jump" -> thePlayer.motionY = 0.42
                "lowjump" -> thePlayer.motionY = 0.3425
                "visual" -> thePlayer.onCriticalHit(entity)
            }

            msTimer.reset()
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (classProvider.isCPacketPlayer(packet) && modeValue.get().equals("NoGround", ignoreCase = true))
            packet.asCPacketPlayer().onGround = false
    }

    override val tag: String?
        get() = modeValue.get()
}
