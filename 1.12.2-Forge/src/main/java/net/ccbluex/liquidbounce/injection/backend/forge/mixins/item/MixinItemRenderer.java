/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.item;

import me.utils.Debug;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura;
import net.ccbluex.liquidbounce.features.module.modules.render.AntiBlind;
import net.ccbluex.liquidbounce.features.module.modules.render.OldHitting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.utils.Debug.*;

@Mixin(ItemRenderer.class)
@SideOnly(Side.CLIENT)
public abstract class MixinItemRenderer {

    @Shadow
    @Final
    private Minecraft mc;
    @Shadow
    private ItemStack itemStackOffHand;
    private float equippedProgress;

    private float prevEquippedProgress;
    @Shadow
    private ItemStack itemStackMainHand;

    @Shadow
    protected abstract void renderMapFirstPerson(float p_187463_1_, float p_187463_2_, float p_187463_3_);

    @Shadow
    protected abstract void transformFirstPerson(EnumHandSide hand, float swingProgress);


    @Shadow
    protected abstract void transformEatFirstPerson(float p_187454_1_, EnumHandSide hand, ItemStack stack);

    @Shadow
    protected abstract void renderArmFirstPerson(float p_187456_1_, float p_187456_2_, EnumHandSide p_187456_3_);

    @Shadow
    protected abstract void renderMapFirstPersonSide(float p_187465_1_, EnumHandSide hand, float p_187465_3_, ItemStack stack);

    @Shadow
    protected abstract void transformSideFirstPerson(EnumHandSide hand, float p_187459_2_);

    @Shadow
    public abstract void renderItemSide(EntityLivingBase entitylivingbaseIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform, boolean leftHanded);
    private void func_178096_b(float p_178096_1_, float p_178096_2_) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, p_178096_1_ * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(p_178096_2_ * p_178096_2_ * (float) Math.PI);
        float var4 = MathHelper.sin(MathHelper.sqrt(p_178096_2_) * (float) Math.PI);
        GlStateManager.rotate(var3 * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var4 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(var4 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(OldHitting.Scale.get(), OldHitting.Scale.get(), OldHitting.Scale.get());
    }
    private void strange(float lul, float lol) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        float var26 = MathHelper.sin(lol * lul * 3.1415927f);
        float var27 = MathHelper.cos(MathHelper.sqrt(lul) * (float) Math.PI);
        float var28 = MathHelper.abs(MathHelper.sqrt(lul) * (float) Math.PI);
        GlStateManager.rotate(var26 * var27, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var28 * 15.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(var27 * 10.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(OldHitting.Scale.get(), OldHitting.Scale.get(), OldHitting.Scale.get());
    }
    private void func_178103_d() {
        GlStateManager.translate(-0.5F, 0.2F, 0.0F);
        GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
    }
    /**
     *
     *
     * @author
     * @reason
     */
    @Overwrite
    public void renderItemInFirstPerson(AbstractClientPlayer player, float p_187457_2_, float p_187457_3_, EnumHand hand, float p_187457_5_, ItemStack stack, float p_187457_7_) {
        boolean flag = hand == EnumHand.MAIN_HAND;
        EnumHandSide enumhandside = flag ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
        GlStateManager.pushMatrix();
        AbstractClientPlayer abstractclientplayer = this.mc.player;
        if (LiquidBounce.moduleManager.getModule(OldHitting.class).getState()) {
            GlStateManager.scale(OldHitting.Scale.get(), OldHitting.Scale.get(), OldHitting.Scale.get());
            GL11.glTranslated(OldHitting.itemPosX.get().doubleValue(), OldHitting.itemPosY.get().doubleValue(), OldHitting.itemPosZ.get().doubleValue());
        }

        float f8 = abstractclientplayer.getSwingProgress(p_187457_5_);
        OldHitting ot = (OldHitting)LiquidBounce.moduleManager.getModule(OldHitting.class);
        KillAura aura = (KillAura)LiquidBounce.moduleManager.getModule(KillAura.class);
//
//        GlStateManager.enableRescaleNormal();
//        GlStateManager.pushMatrix();

        if (LiquidBounce.moduleManager.getModule(OldHitting.class).getState()) {
//            GlStateManager.scale(OldHitting.Scale.get(), OldHitting.Scale.get(), OldHitting.Scale.get());
            GL11.glTranslated(OldHitting.itemPosX.get().doubleValue(), OldHitting.itemPosY.get().doubleValue(), OldHitting.itemPosZ.get().doubleValue());
        }
        if (stack.isEmpty()) {
            if (flag && !player.isInvisible()) {
                this.renderArmFirstPerson(p_187457_7_, p_187457_5_, enumhandside);
            }
        } else if (stack.getItem() == Items.FILLED_MAP) {
            if (flag && this.itemStackOffHand.isEmpty()) {
                this.renderMapFirstPerson(p_187457_3_, p_187457_7_, p_187457_5_);
            } else {
                this.renderMapFirstPersonSide(p_187457_7_, enumhandside, p_187457_5_, stack);
            }
        } else {
            boolean flag1 = enumhandside == EnumHandSide.RIGHT;

            if (player.isHandActive() && player.getItemInUseCount() > 0 && player.getActiveHand() == hand) {
                int j = flag1 ? 1 : -1;

                switch (stack.getItemUseAction()) {
                    case NONE:
                        this.transformSideFirstPerson(enumhandside, p_187457_7_);
                        break;

                    case EAT:
                    case DRINK:
                        this.transformEatFirstPerson(p_187457_2_, enumhandside, stack);
                        this.transformSideFirstPerson(enumhandside, p_187457_7_);
                        break;

                    case BLOCK:
                        this.transformSideFirstPerson(enumhandside, p_187457_7_);

                        break;

                    case BOW:
                        this.transformSideFirstPerson(enumhandside, p_187457_7_);
                        GlStateManager.translate((float) j * -0.2785682F, 0.18344387F, 0.15731531F);
                        GlStateManager.rotate(-13.935F, 1.0F, 0.0F, 0.0F);
                        GlStateManager.rotate((float) j * 35.3F, 0.0F, 1.0F, 0.0F);
                        GlStateManager.rotate((float) j * -9.785F, 0.0F, 0.0F, 1.0F);
                        float f5 = (float) stack.getMaxItemUseDuration() - ((float) this.mc.player.getItemInUseCount() - p_187457_2_ + 1.0F);
                        float f6 = f5 / 20.0F;
                        f6 = (f6 * f6 + f6 * 2.0F) / 3.0F;

                        if (f6 > 1.0F) {
                            f6 = 1.0F;
                        }

                        if (f6 > 0.1F) {
                            float f7 = MathHelper.sin((f5 - 0.1F) * 1.3F);
                            float f3 = f6 - 0.1F;
                            float f4 = f7 * f3;
                            GlStateManager.translate(f4 * 0.0F, f4 * 0.004F, f4 * 0.0F);
                        }

                        GlStateManager.translate(f6 * 0.0F, f6 * 0.0F, f6 * 0.04F);
                        GlStateManager.scale(1.0F, 1.0F, 1.0F + f6 * 0.2F);
                        GlStateManager.rotate((float) j * 45.0F, 0.0F, -1.0F, 0.0F);
                }
            } else {
                if(ot.getState()){
                    if (aura.getBlockingStatus() || /*(player.getActiveItemStack() == itemStackOffHand || thePlayerisBlocking ||*/ mc.gameSettings.keyBindUseItem.pressed && (!ot.getOnlySword().get() || itemStackMainHand.getItem() instanceof ItemSword)) {
                        final String z = ot.getModeValue().get();
                        float f1 = abstractclientplayer.getSwingProgress(p_187457_7_);
                        float var2 = 1.0f - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * p_187457_7_);
                        float var4 = this.mc.player.getSwingProgress(p_187457_7_);
                        float var15 = MathHelper.sin(MathHelper.sqrt(var4) * 3.1415927f);
                        switch(z){
                            case "Strange": {
                                this.strange( p_187457_7_, p_187457_5_);
                                this.func_178103_d();
                                break;
                            }
                            case "Reverse": {
                                this.func_178096_b( p_187457_7_, p_187457_5_);

                                this.func_178103_d();

                                break;
                            }
                            case"ETB" :{
                                ETB(enumhandside, p_187457_7_, p_187457_5_);
                                break;
                            }
                            case"MineCraft" :{
                                Debug.func_178096_b(enumhandside, p_187457_7_, p_187457_5_);
                                break;
                            }
                            case"Test" :{
                                Test(enumhandside, p_187457_7_, p_187457_5_);
                                break;
                            }
                            case"SigmaOld" :{
                                sigmaold(enumhandside, p_187457_7_, p_187457_5_);
                                break;
                            }
                            case "Jello": {
                                jello(enumhandside, p_187457_7_, p_187457_5_);

                            }
                            case"Zoom" :{
                                Zoom(enumhandside, p_187457_7_, p_187457_5_);
                                break;
                            }

                            case "SideDown": {
                                transformSideFirstPersonBlock(enumhandside, p_187457_7_, p_187457_5_);
                                break;
                            }

                            case "Push": {
                                Push(enumhandside, p_187457_7_, p_187457_5_);
                                break;
                            }
                        }


                    } else {
                        float f = -0.4F * MathHelper.sin(MathHelper.sqrt(p_187457_5_) * (float) Math.PI);
                        float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt(p_187457_5_) * ((float) Math.PI * 2F));
                        float f2 = -0.2F * MathHelper.sin(p_187457_5_ * (float) Math.PI);
                        int i = flag1 ? 1 : -1;
                        GlStateManager.translate((float) i * f, f1, f2);

//                        doItemRenderGLTranslate();
//                        doItemRenderGLScale();
                        GlStateManager.scale(OldHitting.Scale.get(), OldHitting.Scale.get(), OldHitting.Scale.get());
                        this.transformSideFirstPerson(enumhandside, p_187457_7_);
                        this.transformFirstPerson(enumhandside, p_187457_5_);
                    }
                } else {
                    this.transformSideFirstPerson(enumhandside, p_187457_7_);
                    this.transformFirstPerson(enumhandside, p_187457_5_);
                }

            }
            if(ot.getState()){
                if (stack.getItem() instanceof ItemShield && itemStackMainHand.getItem() instanceof ItemSword) {
                    GlStateManager.popMatrix();
                    return;
                }
                this.renderItemSide(player, stack, flag1 ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !flag1);
            } else {
                this.renderItemSide(player, stack, flag1 ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !flag1);
            }

        }

        GlStateManager.popMatrix();
        if (LiquidBounce.moduleManager.getModule(OldHitting.class).getState()) {
//            GlStateManager.scale(-OldHitting.Scale.get(), -OldHitting.Scale.get(), -OldHitting.Scale.get());
            GL11.glTranslated(-OldHitting.itemPosX.get().doubleValue(), -OldHitting.itemPosY.get().doubleValue(), -OldHitting.itemPosZ.get().doubleValue());
        }
    }


    /**
     * @author CCBlueX
     */

    @Inject(method = "renderFireInFirstPerson", at = @At("HEAD"), cancellable = true)
    private void renderFireInFirstPerson(final CallbackInfo callbackInfo) {
        final AntiBlind antiBlind = (AntiBlind) LiquidBounce.moduleManager.getModule(AntiBlind.class);

        if (antiBlind.getState() && antiBlind.getFireEffect().get()) callbackInfo.cancel();
    }
}