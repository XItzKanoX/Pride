/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package net.ccbluex.liquidbounce.api.minecraft.client.render.texture

import net.ccbluex.liquidbounce.api.minecraft.util.IResourceLocation
import net.minecraft.util.ResourceLocation

interface ITextureManager {
    fun loadTexture(textureLocation: IResourceLocation, textureObj: IAbstractTexture): Boolean
    fun bindTexture(image: IResourceLocation)
    fun bindTexture2(image: ResourceLocation)
    }
