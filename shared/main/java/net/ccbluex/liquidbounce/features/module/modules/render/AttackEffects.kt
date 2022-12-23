/*
 Copyright Alan Wood 2021
 None of this code to be reused without my written permission
 Intellectual Rights owned by Alan Wood
 */

package net.ccbluex.liquidbounce.features.module.modules.render

import me.sound.SoundPlayer
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityLivingBase
import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.block.Block
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.effect.EntityLightningBolt
import net.minecraft.init.Blocks
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity
import net.minecraft.util.EnumParticleTypes

@ModuleInfo(name = "AttackEffects", description = "Rise.", category = ModuleCategory.RENDER)
class AttackEffects : Module() {
    val amount = IntegerValue("Amount", 5, 1, 20)

    private val sound = BoolValue("Sound", true)
    private val lightingSoundValue = BoolValue("LightingSound", true)
    private var lastAttackedEntity = 0
    var target: IEntityLivingBase? = null
    var target2: EntityLivingBase? = null


    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (event.targetEntity is IEntityLivingBase) target = event.targetEntity
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (event.isPre()) {
            if (target != null && target!!.hurtTime >= 3 && mc.thePlayer!!.getDistance(
                            target!!.posX,
                            target!!.posY,
                            target!!.posZ
                    ) < 10
            ) {
                if(mc.thePlayer!!.ticksExisted > 4){
                    when(atksound.get().toLowerCase()) {
                        "knock" ->{
                            SoundPlayer().playSound(SoundPlayer.SoundType.Crack, LiquidBounce.moduleManager.toggleVolume);
                        }
                        "skeet" -> {
                            SoundPlayer().playSound(SoundPlayer.SoundType.SKEET, LiquidBounce.moduleManager.toggleVolume);
                        }

                        "neko" -> {

                            SoundPlayer().playSound(SoundPlayer.SoundType.NEKO, LiquidBounce.moduleManager.toggleVolume);
                        }
                    }
                }
                if (mc.thePlayer!!.ticksExisted > 3) {


                    when (mode.get().toLowerCase()) {
                        "blood" -> {
                            var i = 0
                            while (i < amount.value) {
                                mc2.world.spawnParticle(
                                    EnumParticleTypes.BLOCK_CRACK,
                                    target!!.posX,
                                    target!!.posY + target!!.height - 0.75,
                                    target!!.posZ,
                                    0.0,
                                    0.0,
                                    0.0,
                                    Block.getStateId(Blocks.REDSTONE_BLOCK.defaultState)
                                )
                                i++
                            }
                            if (sound.get()) mc.soundHandler.playSound("minecraft:block.anvil.break", 1f)
                        }

                        "criticals" -> {
                            var i = 0
                            while (i < amount.value) {
                                mc2.effectRenderer.spawnEffectParticle( EnumParticleTypes.CRIT.particleID,target!!.posX, target!!.posY, target!!.posZ, target!!.posX, target!!.posY, target!!.posZ)
//                                mc2.effectRenderer.emitParticleAtEntity(target2, EnumParticleTypes.CRIT)
                                i++
                            }
                        }
                        "magic" -> {
                            var i = 0
                            while (i < amount.value) {
                                mc2.effectRenderer.spawnEffectParticle(EnumParticleTypes.CRIT_MAGIC.particleID, target!!.posX, target!!.posY, target!!.posZ, target!!.posX, target!!.posY, target!!.posZ)
//                                mc2.effectRenderer.emitParticleAtEntity(target2!!, EnumParticleTypes.CRIT_MAGIC)
                                i++
                            }
                        }
                        "lighting" -> {
                            mc.netHandler2.handleSpawnGlobalEntity(SPacketSpawnGlobalEntity(EntityLightningBolt(mc2.world,
                                    target!!.posX, target!!.posY, target!!.posZ, true)))
                            if(lightingSoundValue.get()) {
                                mc.soundHandler.playSound("entity.lightning.impact", 0.5f)
                            }
                        }
//                        "smoke" -> mc2.effectRenderer.emitParticleAtEntity(target2!!, EnumParticleTypes.SMOKE_NORMAL)
                        "smoke"-> mc2.effectRenderer.spawnEffectParticle(EnumParticleTypes.SMOKE_NORMAL.particleID, target!!.posX, target!!.posY, target!!.posZ, target!!.posX, target!!.posY, target!!.posZ)
//                        "water" -> mc2.effectRenderer.emitParticleAtEntity(target2!!, EnumParticleTypes.WATER_DROP)
                        "water" -> mc2.effectRenderer.spawnEffectParticle(EnumParticleTypes.WATER_DROP.particleID, target!!.posX, target!!.posY, target!!.posZ, target!!.posX, target!!.posY, target!!.posZ)
//                        "heart" -> mc2.effectRenderer.emitParticleAtEntity(target2!!, EnumParticleTypes.HEART)
                        "heart" -> mc2.effectRenderer.spawnEffectParticle(EnumParticleTypes.HEART.particleID, target!!.posX, target!!.posY, target!!.posZ, target!!.posX, target!!.posY, target!!.posZ)
//                        "fire" -> mc2.effectRenderer.emitParticleAtEntity(target2!!, EnumParticleTypes.LAVA)
                        "fire" -> mc2.effectRenderer.spawnEffectParticle(EnumParticleTypes.LAVA.particleID,target!!.posX, target!!.posY, target!!.posZ, target!!.posX, target!!.posY, target!!.posZ)

                    }
                }
                target = null
            }
        }
    }





    companion object {
        val  atksound = ListValue("AttackSound", arrayOf(
            "None",
            "Skeet",
            "Neko",
            "Knock"

        ), "None")
        val mode = ListValue(
            "Mode", arrayOf(
                "Blood",
                "Lighting",
                "Fire",
                "Heart",
                "Water"

            ), "Blood"
        )
    }
}