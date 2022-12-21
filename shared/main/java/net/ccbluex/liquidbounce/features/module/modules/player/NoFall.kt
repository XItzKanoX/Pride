/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.enums.BlockType
import net.ccbluex.liquidbounce.api.enums.EnumFacingType
import net.ccbluex.liquidbounce.api.enums.ItemType
import net.ccbluex.liquidbounce.api.minecraft.network.IPacket
import net.ccbluex.liquidbounce.api.minecraft.potion.PotionType
import net.ccbluex.liquidbounce.api.minecraft.util.IAxisAlignedBB
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos
import net.ccbluex.liquidbounce.api.minecraft.util.WVec3
import net.ccbluex.liquidbounce.api.minecraft.util.WVec3i
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.render.FreeCam
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.VecRotation
import net.ccbluex.liquidbounce.utils.block.BlockUtils
import net.ccbluex.liquidbounce.utils.block.BlockUtils.collideBlock
import net.ccbluex.liquidbounce.utils.misc.FallingPlayer
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.utils.timer.TickTimer
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.server.SPacketEntityVelocity
import net.minecraft.network.play.server.SPacketPlayerPosLook
import java.util.concurrent.LinkedBlockingQueue
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.math.sqrt

@ModuleInfo(name = "NoFall", description = "Prevents you from taking fall damage.", category = ModuleCategory.PLAYER)
class NoFall : Module() {
    @JvmField
    val modeValue = ListValue("Mode", arrayOf("Vulcan", "AAC4","SpoofGround", "NoGround", "Packet",  "LoyisaAAC4.4.2",  "aac5.0.14","AAC4.4.X-Flag", "MLG", "AAC", "LAAC", "AAC3.3.11", "AAC3.3.15", "Spartan", "CubeCraft", "Hypixel"), "SpoofGround")
    private val minFallDistance = FloatValue("MinMLGHeight", 5f, 2f, 50f)
    private val spartanTimer = TickTimer()
    private val mlgTimer = TickTimer()
    private var currentState = 0
    private var aac4Fakelag = false
    private var jumped = false
    private var isDmgFalling = false
    private var currentMlgRotation: VecRotation? = null
    private var currentMlgItemIndex = 0
    private var currentMlgBlock: WBlockPos? = null
    private var fakelag = false
    private var packetmodify = false
    private val aac4FlagCooldown = MSTimer()
    private var aac4FlagCount = 0
    private val packets = LinkedBlockingQueue<IPacket>()
    private var oldaacState = 0
    private var packetModify = false
    private var aac5doFlag = false
    private var aac5Check = false
    private var aac5Timer = 0

    private var needSpoof = false
    private var packet1Count = 0

    private var matrixIsFall = false
    private var matrixCanSpoof = false
    private var matrixFallTicks = 0
    private var matrixLastMotionY = 0.0

    private var matrixFlagWait = 0

    private var wasTimer = false
    private var matrixSend = false
    //Vulcan
    var canNoFall = false
    var nextSpoof = false
    var doSpoof = false
    var worldChange = false
    var count = 0

    override fun onEnable() {
        if (modeValue.get().equals("AAC4", ignoreCase = true)) {
            fakelag = false
            packetmodify = false
        }
        canNoFall = false
        nextSpoof =false
        doSpoof =false
        worldChange = true
        count = 0
    }

    @EventTarget(ignoreCondition = true)
    fun onUpdate(event: UpdateEvent?) {
        if (mc.thePlayer!!.onGround)
            jumped = false

        if (mc.thePlayer!!.motionY > 0)
            jumped = true

        if (!state || LiquidBounce.moduleManager.getModule(FreeCam::class.java)!!.state)
            return

        if (collideBlock(mc.thePlayer!!.entityBoundingBox, classProvider::isBlockLiquid) ||
                collideBlock(classProvider.createAxisAlignedBB(mc.thePlayer!!.entityBoundingBox.maxX, mc.thePlayer!!.entityBoundingBox.maxY, mc.thePlayer!!.entityBoundingBox.maxZ, mc.thePlayer!!.entityBoundingBox.minX, mc.thePlayer!!.entityBoundingBox.minY - 0.01, mc.thePlayer!!.entityBoundingBox.minZ), classProvider::isBlockLiquid))
            return

        when (modeValue.get().toLowerCase()) {
            "packet" -> {
                if (mc.thePlayer!!.fallDistance > 2f) {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayer(true))
                }
            }
            "cubecraft" -> if (mc.thePlayer!!.fallDistance > 2f) {
                mc.thePlayer!!.onGround = false
                mc.thePlayer!!.sendQueue.addToSendQueue(classProvider.createCPacketPlayer(true))
            }
            "aac" -> {
                if (mc.thePlayer!!.fallDistance > 2f) {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayer(true))
                    currentState = 2
                } else if (currentState == 2 && mc.thePlayer!!.fallDistance < 2) {
                    mc.thePlayer!!.motionY = 0.1
                    currentState = 3
                    return
                }
                when (currentState) {
                    3 -> {
                        mc.thePlayer!!.motionY = 0.1
                        currentState = 4
                    }
                    4 -> {
                        mc.thePlayer!!.motionY = 0.1
                        currentState = 5
                    }
                    5 -> {
                        mc.thePlayer!!.motionY = 0.1
                        currentState = 1
                    }
                }
            }
            "laac" -> if (!jumped && mc.thePlayer!!.onGround && !mc.thePlayer!!.isOnLadder && !mc.thePlayer!!.isInWater
                    && !mc.thePlayer!!.isInWeb) mc.thePlayer!!.motionY = (-6).toDouble()
            "aac3.3.11" -> if (mc.thePlayer!!.fallDistance > 2) {
                mc.thePlayer!!.motionZ = 0.0
                mc.thePlayer!!.motionX = mc.thePlayer!!.motionZ
                mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(mc.thePlayer!!.posX,
                        mc.thePlayer!!.posY - 10E-4, mc.thePlayer!!.posZ, mc.thePlayer!!.onGround))
                mc.netHandler.addToSendQueue(classProvider.createCPacketPlayer(true))
            }
            "aac3.3.15" -> if (mc.thePlayer!!.fallDistance > 2) {
                if (!mc.isIntegratedServerRunning) mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(mc.thePlayer!!.posX, Double.NaN, mc.thePlayer!!.posZ, false))
                mc.thePlayer!!.fallDistance = (-9999).toFloat()
            }
            "spartan" -> {
                spartanTimer.update()
                if (mc.thePlayer!!.fallDistance > 1.5 && spartanTimer.hasTimePassed(10)) {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(mc.thePlayer!!.posX,
                            mc.thePlayer!!.posY + 10, mc.thePlayer!!.posZ, true))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(mc.thePlayer!!.posX,
                            mc.thePlayer!!.posY - 10, mc.thePlayer!!.posZ, true))
                    spartanTimer.reset()
                }
            }

            "aac5.0.4","loyisaaac4.4.2" -> {
                if (mc.thePlayer!!.fallDistance > 3) {
                    isDmgFalling = true
                }
                if (modeValue.get() == "LoyisaAAC4.4.2") {
                    if(aac4FlagCount>=3 || aac4FlagCooldown.hasTimePassed(1500L)) {
                        return
                    }
                    if(!aac4FlagCooldown.hasTimePassed(1500L) && (mc.thePlayer!!.onGround || mc.thePlayer!!.fallDistance < 0.5)) {
                        mc.thePlayer!!.motionX = 0.0
                        mc.thePlayer!!.motionZ = 0.0
                        mc.thePlayer!!.onGround = false
                        mc.thePlayer!!.jumpMovementFactor = 0.00f
                    }
                }
            }
            "aac5.0.14" -> {
                var offsetYs = 0.0
                aac5Check = false
                while (mc.thePlayer!!.motionY - 1.5 < offsetYs) {
                    val blockPos = WBlockPos(mc.thePlayer!!.posX, mc.thePlayer!!.posY + offsetYs, mc.thePlayer!!.posZ)
                    val block = BlockUtils.getBlock(blockPos)
                    val axisAlignedBB = block!!.getCollisionBoundingBox(mc.theWorld!!, blockPos, BlockUtils.getState(blockPos)!!)
                    if (axisAlignedBB != null) {
                        offsetYs = -999.9
                        aac5Check = true
                    }
                    offsetYs -= 0.5
                }
                if (mc.thePlayer!!.onGround) {
                    mc.thePlayer!!.fallDistance = -2f
                    aac5Check = false
                }
                if (aac5Timer > 0) {
                    aac5Timer -= 1
                }
                if (aac5Check && mc.thePlayer!!.fallDistance > 2.5 && !mc.thePlayer!!.onGround) {
                    aac5doFlag = true
                    aac5Timer = 18
                } else {
                    if (aac5Timer < 2) aac5doFlag = false
                }
                if (aac5doFlag) {
                    if (mc.thePlayer!!.onGround) {
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(mc.thePlayer!!.posX, mc.thePlayer!!.posY + 0.5, mc.thePlayer!!.posZ, true))
                    } else {
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(mc.thePlayer!!.posX, mc.thePlayer!!.posY + 0.42, mc.thePlayer!!.posZ, true))
                    }
                }
            }
            "vulcan" -> {
                if(worldChange){
                    if(mc.thePlayer!!.onGround){
                        worldChange = true
                    }
                    return
                }
                if(!canNoFall && mc.thePlayer!!.fallDistance >3.25){
                    canNoFall = true
                } else if (mc.thePlayer!!.onGround){
                    canNoFall = false
                }
                if(nextSpoof){
                    mc.thePlayer!!.motionY = -0.1
                    MovementUtils.strafe(0.343f)
                    nextSpoof = false
                }
                if(mc.thePlayer!!.fallDistance > 3.65){
                    mc.thePlayer!!.fallDistance =0f
                    count = if(mc.isIntegratedServerRunning) 2 else 1
                    doSpoof = true
                    nextSpoof = true
                }
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (mc.thePlayer!!.isInWater){
            return
        }
        val packet = event.packet
        val mode = modeValue.get()
        if (classProvider.isSPacketEntityVelocity(event.packet)) {
            if (mode.equals("AAC4.4.X-Flag", ignoreCase = true) && mc.thePlayer!!.fallDistance > 1.8) {
                event.packet.asSPacketEntityVelocity().motionY = (event.packet.asSPacketEntityVelocity().motionY * -0.1).toInt()
            }
        }
        if (classProvider.isSPacketPlayerPosLook(event.packet)) {
            if (mode.equals("LoyisaAAC4.4.2", ignoreCase = true)) {
                aac4FlagCount++
                if (matrixFlagWait > 0) {
                    aac4FlagCooldown.reset()
                    aac4FlagCount = 1
                    event.cancelEvent()
                }
            }
        }

        if (mode.equals("AAC4", ignoreCase = true)) {
            if (classProvider.isCPacketPlayer(packet) && fakelag) {
                event.cancelEvent()
                if (packetmodify) {
                    (packet.asCPacketPlayer()).onGround = true
                    packetmodify = false
                }
                packets.add(packet)
            }
        }

        if (mode.equals("Vulcan", true) && classProvider.isCPacketPlayer(packet) && doSpoof && count >0) {
            --count
            if(count == 0){
                doSpoof = false
            }
            packet.asCPacketPlayer().onGround = true
            packet.asCPacketPlayer().y = ((mc.thePlayer!!.posY * 2).roundToInt() /2).toDouble()
            mc.thePlayer!!.setPosition(mc.thePlayer!!.posX, packet.asCPacketPlayer().y, mc.thePlayer!!.posZ)
        }

        if (classProvider.isCPacketPlayer(packet)) {
            val playerPacket = packet.asCPacketPlayer()

            if (mode.equals("SpoofGround", ignoreCase = true)) playerPacket.onGround = true
            if (mode.equals("NoGround", ignoreCase = true)) playerPacket.onGround = false
            if (mode.equals("Hypixel", ignoreCase = true)
                    && mc.thePlayer != null && mc.thePlayer!!.fallDistance > 1.5) playerPacket.onGround = mc.thePlayer!!.ticksExisted % 2 == 0
        }
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        if (collideBlock(mc.thePlayer!!.entityBoundingBox, classProvider::isBlockLiquid) || collideBlock(classProvider.createAxisAlignedBB(mc.thePlayer!!.entityBoundingBox.maxX, mc.thePlayer!!.entityBoundingBox.maxY, mc.thePlayer!!.entityBoundingBox.maxZ, mc.thePlayer!!.entityBoundingBox.minX, mc.thePlayer!!.entityBoundingBox.minY - 0.01, mc.thePlayer!!.entityBoundingBox.minZ), classProvider::isBlockLiquid))
            return

        if (modeValue.get().equals("laac", ignoreCase = true)) {
            if (!jumped && !mc.thePlayer!!.onGround && !mc.thePlayer!!.isOnLadder && !mc.thePlayer!!.isInWater && !mc.thePlayer!!.isInWeb && mc.thePlayer!!.motionY < 0.0) {
                event.x = 0.0
                event.z = 0.0
            }
        }
    }

    @EventTarget
    private fun onMotionUpdate(event: MotionEvent) {
        if (mc.thePlayer!!.isInWater){
            return
        }
        if (modeValue.get().equals("AAC4", ignoreCase = true)) {
            val eventState = event.eventState
            if (eventState === EventState.PRE) {
                if (!inVoid()) {
                    if (fakelag) {
                        fakelag = false
                        if (packets.size > 0) {
                            for (packet in packets) {
                                mc.thePlayer!!.sendQueue.addToSendQueue(packet)
                            }
                            packets.clear()
                        }
                    }
                    return
                }
                if (mc.thePlayer!!.onGround && fakelag) {
                    fakelag = false
                    if (packets.size > 0) {
                        for (packet in packets) {
                            mc.thePlayer!!.sendQueue.addToSendQueue(packet)
                        }
                        packets.clear()
                    }
                    return
                }
                if (mc.thePlayer!!.fallDistance > 3 && fakelag) {
                    packetmodify = true
                    mc.thePlayer!!.fallDistance = 0f
                }
                if (inAir(4.0, 1.0)) {
                    return
                }
                if (!fakelag) {
                    fakelag = true
                }
            }
        }

        if (modeValue.get().equals("MLG", ignoreCase = true)){
            if (event.eventState == EventState.PRE) {
                currentMlgRotation = null

                mlgTimer.update()

                if (!mlgTimer.hasTimePassed(10))
                    return

                if (mc.thePlayer!!.fallDistance > minFallDistance.get()) {
                    val fallingPlayer = FallingPlayer(
                            mc.thePlayer!!.posX,
                            mc.thePlayer!!.posY,
                            mc.thePlayer!!.posZ,
                            mc.thePlayer!!.motionX,
                            mc.thePlayer!!.motionY,
                            mc.thePlayer!!.motionZ,
                            mc.thePlayer!!.rotationYaw,
                            mc.thePlayer!!.moveStrafing,
                            mc.thePlayer!!.moveForward
                    )

                    val maxDist: Double = mc.playerController.blockReachDistance + 1.5

                    val collision = fallingPlayer.findCollision(ceil(1.0 / mc.thePlayer!!.motionY * -maxDist).toInt())
                            ?: return

                    var ok: Boolean = WVec3(mc.thePlayer!!.posX, mc.thePlayer!!.posY + mc.thePlayer!!.eyeHeight, mc.thePlayer!!.posZ).distanceTo(WVec3(collision.pos).addVector(0.5, 0.5, 0.5)) < mc.playerController.blockReachDistance + sqrt(0.75)

                    if (mc.thePlayer!!.motionY < collision.pos.y + 1 - mc.thePlayer!!.posY) {
                        ok = true
                    }

                    if (!ok)
                        return

                    var index = -1

                    for (i in 36..44) {
                        val itemStack = mc.thePlayer!!.inventoryContainer.getSlot(i).stack

                        if (itemStack != null && (itemStack.item == classProvider.getItemEnum(ItemType.WATER_BUCKET) || classProvider.isItemBlock(itemStack.item) && (itemStack.item?.asItemBlock())?.block == classProvider.getBlockEnum(BlockType.WEB))) {
                            index = i - 36

                            if (mc.thePlayer!!.inventory.currentItem == index)
                                break
                        }
                    }
                    if (index == -1)
                        return

                    currentMlgItemIndex = index
                    currentMlgBlock = collision.pos

                    if (mc.thePlayer!!.inventory.currentItem != index) {
                        mc.thePlayer!!.sendQueue.addToSendQueue(classProvider.createCPacketHeldItemChange(index))
                    }

                    currentMlgRotation = RotationUtils.faceBlock(collision.pos)
                    currentMlgRotation!!.rotation.toPlayer(mc.thePlayer!!)
                }
            } else if (currentMlgRotation != null) {
                val stack = mc.thePlayer!!.inventory.getStackInSlot(currentMlgItemIndex + 36)

                if (classProvider.isItemBucket(stack!!.item)) {
                    mc.playerController.sendUseItem(mc.thePlayer!!, mc.theWorld!!, stack)
                } else {
                    val dirVec: WVec3i = classProvider.getEnumFacing(EnumFacingType.UP).directionVec

                    if (mc.playerController.sendUseItem(mc.thePlayer!!, mc.theWorld!!, stack)) {
                        mlgTimer.reset()
                    }
                }
                if (mc.thePlayer!!.inventory.currentItem != currentMlgItemIndex)
                    mc.thePlayer!!.sendQueue.addToSendQueue(classProvider.createCPacketHeldItemChange(mc.thePlayer!!.inventory.currentItem))
            }
        }

    }

    fun isBlockUnder(): Boolean {
        if (mc.thePlayer!!.posY < 0.0) {
            return false
        } else {
            var off = 0
            while (true) {
                if (off >= mc.thePlayer!!.posY.toInt() + 2) {
                    return false
                }
                val bb: IAxisAlignedBB = mc.thePlayer!!.entityBoundingBox.offset(0.0, (-off).toDouble(), 0.0)
                if (!mc.theWorld!!.getCollidingBoundingBoxes(mc.thePlayer!!, bb).isEmpty()) {
                    return true
                }
                off += 2
            }
        }
    }

    fun getJumpEffect(): Int {
        return if (mc.thePlayer!!.isPotionActive(classProvider.getPotionEnum(PotionType.JUMP))) mc.thePlayer!!.getActivePotionEffect(classProvider.getPotionEnum(PotionType.JUMP))!!.amplifier + 1 else 0
    }

    fun inVoid(): Boolean {
        if (mc.thePlayer!!.posY < 0) {
            return false
        }
        var off = 0
        while (off < mc.thePlayer!!.posY + 2) {
            val bb: IAxisAlignedBB = classProvider.createAxisAlignedBB(mc.thePlayer!!.posX, mc.thePlayer!!.posY, mc.thePlayer!!.posZ, mc.thePlayer!!.posX, off.toDouble(), mc.thePlayer!!.posZ)
            if (!mc.theWorld!!.getCollidingBoundingBoxes(mc.thePlayer!!, bb).isEmpty()) {
                return true
            }
            off += 2
        }
        return false
    }

    fun inAir(height: Double, plus: Double): Boolean {
        if (mc.thePlayer!!.posY < 0) return false
        var off = 0
        while (off < height) {
            val bb: IAxisAlignedBB = classProvider.createAxisAlignedBB(mc.thePlayer!!.posX, mc.thePlayer!!.posY, mc.thePlayer!!.posZ, mc.thePlayer!!.posX, mc.thePlayer!!.posY - off, mc.thePlayer!!.posZ)
            if (!mc.theWorld!!.getCollidingBoundingBoxes(mc.thePlayer!!, bb).isEmpty()) {
                return true
            }
            off += plus.toInt()
        }
        return false
    }

    @EventTarget(ignoreCondition = true)
    fun onJump(event: JumpEvent?) {
        jumped = true
    }

    @EventTarget
    fun onWorld(event: WorldEvent){
        worldChange = true
    }

    override val tag: String
        get() = modeValue.get()
}