/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/laoshuikaixue/FDPClient
 */
package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketPlayerDigging
import net.ccbluex.liquidbounce.api.minecraft.potion.PotionType
import net.ccbluex.liquidbounce.api.minecraft.util.IEnumFacing
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.init.Blocks
import net.minecraft.init.MobEffects

@ModuleInfo("SpeedMine", description = "1145", category = ModuleCategory.WORLD)
class SpeedMine : Module() {
    private val modeValue = ListValue("Mode", arrayOf("Hypixel", "Packet", "NewPacket", "NewPacket2", "MoonX", "Tenacity"),"NewPacket")
    private val breakSpeedValue = FloatValue("BreakSpeed", 1.2F, 1F, 1.5F)
    private val tenacitySpeedValue = FloatValue("Tenacity-Speed", 1.5F, 1F, 3F)

    private var bzs = false
    private var bzx = 0.0F
    var blockPos: WBlockPos? = null
    private var facing: IEnumFacing? = null
    private var pos: WBlockPos? = null
    private var boost = false
    private var damage = 0f

    override fun onDisable() {
        mc.thePlayer!!.removePotionEffectClient(classProvider.getPotionEnum(PotionType.DIG_SLOWDOWN).id)
    }

    @EventTarget
    fun onMotion(event: MotionEvent){
        if(modeValue.get().equals("Tenacity", true)){
            if (event.eventState == EventState.PRE) {
                mc.playerController.blockHitDelay = 0
                if (pos != null && boost) {
                    val blockState = mc.theWorld!!.getBlockState(pos!!) ?: return
                    try {
                        damage += blockState.block.getPlayerRelativeBlockHardness(
                            mc.thePlayer!!,
                            mc.theWorld!!,
                            pos!!
                        ) * tenacitySpeedValue.get()
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        return
                    }
                    if (damage >= 1) {
                        try {
                            mc.theWorld!!.setBlockState(pos, Blocks.AIR.defaultState, 11)
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                            return
                        }
                        mc.netHandler.addToSendQueue(
                            classProvider.createCPacketPlayerDigging(
                                ICPacketPlayerDigging.WAction.STOP_DESTROY_BLOCK,
                                pos!!,
                                facing!!
                            )
                        )
                        damage = 0f
                        boost = false
                    }
                }
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if(modeValue.get() == "Hypixel") {
            if (classProvider.isCPacketPlayerDigging(event.packet) && !mc.playerController.extendedReach()
                && mc.playerController != null) {
                val packet = event.packet.asCPacketPlayerDigging()
                if (packet.action == ICPacketPlayerDigging.WAction.START_DESTROY_BLOCK) {
                    bzs = true
                    blockPos = packet.position
                    facing = packet.facing
                    bzx = 0.0f
                } else if (packet.action == ICPacketPlayerDigging.WAction.ABORT_DESTROY_BLOCK
                    || packet.action == ICPacketPlayerDigging.WAction.STOP_DESTROY_BLOCK) {
                    bzs = false
                    blockPos = null
                    facing = null
                }
            }
        }
        if(modeValue.get().equals("Tenacity", true)) {
            if (classProvider.isCPacketPlayerDigging(event.packet)) {
                val packet = event.packet.asCPacketPlayerDigging()
                if (packet.action == ICPacketPlayerDigging.WAction.START_DESTROY_BLOCK) {
                    boost = true
                    pos = packet.position
                    facing = packet.facing
                    damage = 0f
                } else if (packet.action == ICPacketPlayerDigging.WAction.ABORT_DESTROY_BLOCK || packet.action == ICPacketPlayerDigging.WAction.STOP_DESTROY_BLOCK) {
                    boost = false
                    pos = null
                    facing = null
                }
            }
        }
    }

    @EventTarget
    private fun onUpdate(e: UpdateEvent) {
        when(modeValue.get()) {
            "Packet" -> {
                if(mc.playerController.curBlockDamageMP in 0.1F..0.19F)
                    mc.playerController.curBlockDamageMP += 0.1F
                if(mc.playerController.curBlockDamageMP in 0.4F..0.49F)
                    mc.playerController.curBlockDamageMP += 0.1F
                if(mc.playerController.curBlockDamageMP in 0.8F..0.89F)
                    mc.playerController.curBlockDamageMP += 0.9F
            }
            "NewPacket" -> {
                if(mc.playerController.curBlockDamageMP == 0.1F)
                    mc.playerController.curBlockDamageMP += 0.1F
                if(mc.playerController.curBlockDamageMP == 0.4F)
                    mc.playerController.curBlockDamageMP += 0.1F
                if(mc.playerController.curBlockDamageMP == 0.7F)
                    mc.playerController.curBlockDamageMP += 0.1F
            }
            "NewPacket2" -> {
                if(mc.playerController.curBlockDamageMP == 0.2F)
                    mc.playerController.curBlockDamageMP += 0.1F
                if(mc.playerController.curBlockDamageMP == 0.4F)
                    mc.playerController.curBlockDamageMP += 0.1F
                if(mc.playerController.curBlockDamageMP == 0.6F)
                    mc.playerController.curBlockDamageMP += 0.1F
                if(mc.playerController.curBlockDamageMP == 0.8F)
                    mc.playerController.curBlockDamageMP += 0.2F
            }
            "Hypixel" -> {
                if (mc.playerController.extendedReach()) {
                    mc.playerController.blockHitDelay = 0
                } else if (bzs) {
                    val block = mc.theWorld!!.getBlockState(blockPos!!).block
                    bzx += (block.getPlayerRelativeBlockHardness(mc.thePlayer!!, mc.theWorld!!, blockPos!!).toDouble() * breakSpeedValue.get()).toFloat()
                    if (bzx >= 1.0F) {
                        mc.theWorld!!.setBlockState(blockPos, Blocks.AIR.defaultState, 11)
                        mc.netHandler.networkManager.sendPacket(
                            classProvider.createCPacketPlayerDigging(
                                ICPacketPlayerDigging.WAction.STOP_DESTROY_BLOCK, blockPos!!, facing!!
                            )
                        )
                        bzx = 0.0F
                        bzs = false
                    }
                }
            }
            "MoonX"-> {
                mc.playerController.blockHitDelay = 0
//                    val item = mc.thePlayer.currentEquippedItem == null
                mc.thePlayer!!.addPotionEffect(
                    classProvider.createPotionEffect(
                        classProvider.getPotionEnum(PotionType.DIG_SPEED).id, 100,
                        (if (mc.thePlayer!!.heldItem == null) 1 else 0)
                    )
                )
            }
        }
    }

    override val tag: String
        get() = modeValue.get()
}