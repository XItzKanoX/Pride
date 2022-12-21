/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 *
 * This code belongs to WYSI-Foundation. Please give credits when using this in your repository.
 */
package net.ccbluex.liquidbounce.features.module.modules.world

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.minecraft.client.Minecraft
import net.minecraft.network.play.server.SPacketTimeUpdate


@ModuleInfo(name = "Ambience", description = "Change your world time and weather client-side.", category = ModuleCategory.WORLD)
class Ambience : Module() {
    private val timeModeValue = ListValue("Time", arrayOf("Static", "Cycle"), "Static")
    private val cycleSpeedValue = IntegerValue("CycleSpeed", 1, -24, 24 )
    private val worldTimeValue = IntegerValue("Time", 12000, 0, 24000)
    private val weatherModeValue = ListValue("Weather", arrayOf("Clear", "Rain", "NoModification"), "Clear")
    private val rainStrengthValue = FloatValue("RainStrength", 0.1F, 0.01F, 1F)
    private val tagValue = ListValue("Tag", arrayOf("TimeOnly", "Simplified", "Detailed", "None"), "TimeOnly")

    private var timeCycle = 0L

    override fun onEnable() {
        timeCycle = 0L
    }

    @EventTarget

    fun onPacket(event: PacketEvent) {
        if (classProvider.isSPacketTimeUpdate(event.packet))
            event.cancelEvent()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (timeModeValue.get().equals("static", true))
            Minecraft.getMinecraft().world.worldTime = this.worldTimeValue.get().toLong()
        else {
            mc2.world.worldTime = timeCycle
            timeCycle += (cycleSpeedValue.get() * 10).toLong()

            if (timeCycle > 24000L) timeCycle = 0L
            if (timeCycle < 0L) timeCycle = 24000L
        }

        if (!weatherModeValue.get().equals("nomodification", true))
            mc2.world.setRainStrength(if (weatherModeValue.get().equals("clear", true)) 0F else rainStrengthValue.get())
    }

    override val tag: String?
        get() = when (tagValue.get().toLowerCase()) {
            "timeonly" -> if (timeModeValue.get().equals("static", true)) worldTimeValue.get().toString() else timeCycle.toString()
            "simplified" -> "${if (timeModeValue.get().equals("static", true)) worldTimeValue.get().toString() else timeCycle.toString()}, ${weatherModeValue.get()}"
            "detailed" -> "Time: ${if (timeModeValue.get().equals("static", true)) worldTimeValue.get().toString() else "Cycle, ${timeCycle.toString()}"}, Weather: ${weatherModeValue.get()}"
            else -> null
        }
}