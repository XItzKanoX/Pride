/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.utils;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntity;
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityLivingBase;
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityPlayerSP;
import net.ccbluex.liquidbounce.api.minecraft.network.IPacket;
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketPlayer;
import net.ccbluex.liquidbounce.api.minecraft.util.*;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Listenable;
import net.ccbluex.liquidbounce.event.PacketEvent;
import net.ccbluex.liquidbounce.event.TickEvent;
import net.ccbluex.liquidbounce.features.module.modules.combat.FastBow;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public final class RotationUtils extends MinecraftInstance implements Listenable {

    private static final Random random = new Random();

    private static int keepLength;
    private static int revTick;

    public static Rotation targetRotation;
    public static Rotation serverRotation = new Rotation(0F, 0F);

    public static boolean keepCurrentRotation = false;

    private static double x = random.nextDouble();
    private static double y = random.nextDouble();
    private static double z = random.nextDouble();

    public static Rotation OtherRotation(final IAxisAlignedBB bb,final WVec3 vec, final boolean predict,final boolean throughWalls, final float distance) {
        final WVec3 eyesPos = new WVec3(mc.getThePlayer().getPosX(), mc.getThePlayer().getEntityBoundingBox().getMinY() +
                mc.getThePlayer().getEyeHeight(), mc.getThePlayer().getPosZ());
        final WVec3 eyes = mc.getThePlayer().getPositionEyes(1F);
        VecRotation vecRotation = null;
        for(double xSearch = 0.15D; xSearch < 0.85D; xSearch += 0.1D) {
            for (double ySearch = 0.15D; ySearch < 1D; ySearch += 0.1D) {
                for (double zSearch = 0.15D; zSearch < 0.85D; zSearch += 0.1D) {
                    final WVec3 vec3 = new WVec3(bb.getMinX() + (bb.getMaxX() - bb.getMinX()) * xSearch,
                            bb.getMinY() + (bb.getMaxY() - bb.getMinY()) * ySearch, bb.getMinZ() + (bb.getMaxZ() - bb.getMinZ()) * zSearch);
                    final Rotation rotation = toRotation(vec3, predict);
                    final double vecDist = eyes.distanceTo(vec3);

                    if (vecDist > distance)
                        continue;

                    if(throughWalls || isVisible(vec3)) {
                        final VecRotation currentVec = new VecRotation(vec3, rotation);

                        if (vecRotation == null)
                            vecRotation = currentVec;
                    }
                }
            }
        }

        if(predict) eyesPos.addVector(mc.getThePlayer().getMotionX(), mc.getThePlayer().getMotionY(), mc.getThePlayer().getMotionZ());

        final double diffX = vec.getXCoord() - eyesPos.getXCoord();
        final double diffY = vec.getYCoord() - eyesPos.getYCoord();
        final double diffZ = vec.getZCoord() - eyesPos.getZCoord();

        return new Rotation(WMathHelper.wrapAngleTo180_float(
                (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F
        ), WMathHelper.wrapAngleTo180_float(
                (float) (-Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ))))
        ));


    }
    public static float getYawToEntity(final IEntityLivingBase entity) {
        final IEntityPlayerSP player = RotationUtils.mc.getThePlayer();
        return getYawBetween(player.getRotationYaw(), player.getPosX(), player.getPosZ(), entity.getPosX(), entity.getPosZ());
    }
    public static float getYawBetween(final float yaw, final double srcX, final double srcZ, final double destX, final double destZ) {
        final double xDist = destX - srcX;
        final double zDist = destZ - srcZ;
        final float var1 = (float)(StrictMath.atan2(zDist, xDist) * 180.0 / 3.141592653589793) - 90.0f;
        return yaw + MathHelper.wrapDegrees(var1 - yaw);
    }
    public static VecRotation lockView(final IAxisAlignedBB bb, final boolean outborder, final boolean random,
                                       final boolean predict, final boolean throughWalls, final float distance) {
        if (outborder) {
            final WVec3 vec3 = new WVec3(bb.getMinX() + (bb.getMaxX() - bb.getMinX()) * (x * 0.3 + 1.0), bb.getMinY() + (bb.getMaxY() - bb.getMinY()) * (y * 0.3 + 1.0), bb.getMinZ() + (bb.getMaxZ() - bb.getMinZ()) * (z * 0.3 + 1.0));
            return new VecRotation(vec3, toRotation(vec3, predict));
        }

        final WVec3 randomVec = new WVec3(bb.getMinX() + (bb.getMaxX() - bb.getMinX()) * x * 0.8, bb.getMinY() + (bb.getMaxY() - bb.getMinY()) * y * 0.8, bb.getMinZ() + (bb.getMaxZ() - bb.getMinZ()) * z * 0.8);
        final Rotation randomRotation = toRotation(randomVec, predict);

        final WVec3 eyes = mc.getThePlayer().getPositionEyes(1F);

        double xMin = 0.0D;
        double yMin = 0.0D;
        double zMin = 0.0D;
        double xMax = 0.0D;
        double yMax = 0.0D;
        double zMax = 0.0D;
        double xDist = 0.0D;
        double yDist = 0.0D;
        double zDist = 0.0D;
        VecRotation vecRotation = null;
        xMin = 0.45D; xMax = 0.55D; xDist = 0.0125D;
        yMin = 0.65D; yMax = 0.75D; yDist = 0.0125D;
        zMin = 0.45D; zMax = 0.55D; zDist = 0.0125D;
        for(double xSearch = xMin; xSearch < xMax; xSearch += xDist) {
            for (double ySearch = yMin; ySearch < yMax; ySearch += yDist) {
                for (double zSearch = zMin; zSearch < zMax; zSearch += zDist) {
                    final WVec3 vec3 = new WVec3(bb.getMinX() + (bb.getMaxX() - bb.getMinX()) * xSearch, bb.getMinY() + (bb.getMaxY() - bb.getMinY()) * ySearch, bb.getMinZ() + (bb.getMaxZ() - bb.getMinZ()) * zSearch);

                    final Rotation rotation = toRotation(vec3, predict);
                    final double vecDist = eyes.distanceTo(vec3);

                    if (vecDist > distance)
                        continue;

                    if (throughWalls || isVisible(vec3)) {
                        final VecRotation currentVec = new VecRotation(vec3, rotation);

                        if (vecRotation == null || (random ? getRotationDifference(currentVec.getRotation(), randomRotation) < getRotationDifference(vecRotation.getRotation(), randomRotation) : getRotationDifference(currentVec.getRotation()) < getRotationDifference(vecRotation.getRotation())))
                            vecRotation = currentVec;
                    }
                }
            }
        }

        return vecRotation;
    }

    public static net.ccbluex.liquidbounce.utils.VecRotation calculateCenter(final String calMode, final String randMode, final double randomRange, final AxisAlignedBB bb, final boolean predict, final boolean throughWalls) {

        /*if(outborder) {
            final Vec3 vec3 = new Vec3(bb.minX + (bb.maxX - bb.minX) * (x * 0.3 + 1.0), bb.minY + (bb.maxY - bb.minY) * (y * 0.3 + 1.0), bb.minZ + (bb.maxZ - bb.minZ) * (z * 0.3 + 1.0));
            return new net.ccbluex.liquidbounce.utils.VecRotation(vec3, toRotation(vec3, predict));
        }*/

        //final net.ccbluex.liquidbounce.utils.Rotation randomRotation = toRotation(randomVec, predict);

        net.ccbluex.liquidbounce.utils.VecRotation vecRotation = null;

        double xMin = 0.0D;
        double yMin = 0.0D;
        double zMin = 0.0D;
        double xMax = 0.0D;
        double yMax = 0.0D;
        double zMax = 0.0D;
        double xDist = 0.0D;
        double yDist = 0.0D;
        double zDist = 0.0D;

        xMin = 0.15D; xMax = 0.85D; xDist = 0.1D;
        yMin = 0.15D; yMax = 1.00D; yDist = 0.1D;
        zMin = 0.15D; zMax = 0.85D; zDist = 0.1D;

        WVec3 curVec3 = null;

        switch(calMode) {
            case "LiquidBounce":
                xMin = 0.15D; xMax = 0.85D; xDist = 0.1D;
                yMin = 0.15D; yMax = 1.00D; yDist = 0.1D;
                zMin = 0.15D; zMax = 0.85D; zDist = 0.1D;
                break;
            case "Full":
                xMin = 0.00D; xMax = 1.00D; xDist = 0.1D;
                yMin = 0.00D; yMax = 1.00D; yDist = 0.1D;
                zMin = 0.00D; zMax = 1.00D; zDist = 0.1D;
                break;
            case "HalfUp":
                xMin = 0.10D; xMax = 0.90D; xDist = 0.1D;
                yMin = 0.50D; yMax = 0.90D; yDist = 0.1D;
                zMin = 0.10D; zMax = 0.90D; zDist = 0.1D;
                break;
            case "HalfDown":
                xMin = 0.10D; xMax = 0.90D; xDist = 0.1D;
                yMin = 0.10D; yMax = 0.50D; yDist = 0.1D;
                zMin = 0.10D; zMax = 0.90D; zDist = 0.1D;
                break;
            case "CenterSimple":
                xMin = 0.45D; xMax = 0.55D; xDist = 0.0125D;
                yMin = 0.65D; yMax = 0.75D; yDist = 0.0125D;
                zMin = 0.45D; zMax = 0.55D; zDist = 0.0125D;
                break;
            case "CenterLine":
                xMin = 0.45D; xMax = 0.55D; xDist = 0.0125D;
                yMin = 0.10D; yMax = 0.90D; yDist = 0.1D;
                zMin = 0.45D; zMax = 0.55D; zDist = 0.0125D;
                break;
        }

        for(double xSearch = xMin; xSearch < xMax; xSearch += xDist) {
            for (double ySearch = yMin; ySearch < yMax; ySearch += yDist) {
                for (double zSearch = zMin; zSearch < zMax; zSearch += zDist) {
                    final WVec3 vec3 = new WVec3(bb.minX + (bb.maxX - bb.minX) * xSearch, bb.minY + (bb.maxY - bb.minY) * ySearch, bb.minZ + (bb.maxZ - bb.minZ) * zSearch);
                    final Rotation rotation = toRotation(vec3, predict);

                    if(throughWalls || isVisible(vec3)) {
                        final net.ccbluex.liquidbounce.utils.VecRotation currentVec = new net.ccbluex.liquidbounce.utils.VecRotation(vec3, rotation);

                        if (vecRotation == null || (getRotationDifference(currentVec.getRotation()) < getRotationDifference(vecRotation.getRotation()))) {
                            vecRotation = currentVec;
                            curVec3 = vec3;
                        }
                    }
                }
            }
        }

        if(vecRotation == null || randMode == "Off")
            return vecRotation;

        double rand1 = random.nextDouble();
        double rand2 = random.nextDouble();
        double rand3 = random.nextDouble();

        final double xRange = bb.maxX - bb.minX;
        final double yRange = bb.maxY - bb.minY;
        final double zRange = bb.maxZ - bb.minZ;
        double minRange = 999999.0D;

        if(xRange<=minRange) minRange = xRange;
        if(yRange<=minRange) minRange = yRange;
        if(zRange<=minRange) minRange = zRange;

        rand1 = rand1 * minRange * randomRange;
        rand2 = rand2 * minRange * randomRange;
        rand3 = rand3 * minRange * randomRange;

        final double xPrecent = minRange * randomRange / xRange;
        final double yPrecent = minRange * randomRange / yRange;
        final double zPrecent = minRange * randomRange / zRange;

        WVec3 randomVec3 = new WVec3(
                curVec3.getXCoord() - xPrecent * (curVec3.getXCoord() - bb.minX) + rand1,
                curVec3.getYCoord() - yPrecent * (curVec3.getYCoord() - bb.minY) + rand2,
                curVec3.getZCoord() - zPrecent * (curVec3.getZCoord() - bb.minZ) + rand3
        );
        switch(randMode) {
            case "Horizonal":
                randomVec3 = new WVec3(
                        curVec3.getXCoord() - xPrecent * (curVec3.getXCoord() - bb.minX) + rand1,
                        curVec3.getYCoord(),
                        curVec3.getZCoord() - zPrecent * (curVec3.getZCoord() - bb.minZ) + rand3
                );
                break;
            case "Vertical":
                randomVec3 = new WVec3(
                        curVec3.getXCoord(),
                        curVec3.getYCoord() - yPrecent * (curVec3.getYCoord() - bb.minY) + rand2,
                        curVec3.getZCoord()
                );
                break;
        }

        final Rotation randomRotation = toRotation(randomVec3, predict);

        vecRotation =  new net.ccbluex.liquidbounce.utils.VecRotation(randomVec3, randomRotation);

        return vecRotation;
    }
    public static Rotation getNewRotations(final WVec3 vec, final boolean predict) {
        final WVec3 eyesPos = new WVec3(mc2.player.posX, mc2.player.getEntityBoundingBox().minY +
                mc2.player.getEyeHeight(), mc2.player.posZ);
        final double diffX = vec.getXCoord() - eyesPos.getXCoord();
        final double diffY = vec.getYCoord() - eyesPos.getYCoord();
        final double diffZ = vec.getZCoord() - eyesPos.getZCoord();

        double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float)((- Math.atan2(diffY, dist)) * 180.0 / 3.141592653589793);
        return new Rotation(yaw, pitch);
    }
    public static Rotation getNCPRotations(final WVec3 vec, final boolean predict) {
        final WVec3 eyesPos = new WVec3(mc2.player.posX, mc2.player.getEntityBoundingBox().minY +
                mc2.player.getEyeHeight(), mc2.player.posZ);

        if(predict) eyesPos.addVector(mc2.player.motionX, mc2.player.motionY, mc2.player.motionZ);

        final double diffX = vec.getXCoord() - eyesPos.getXCoord();
        final double diffY = vec.getYCoord() - eyesPos.getYCoord();
        final double diffZ = vec.getZCoord() - eyesPos.getZCoord();
        double hypotenuse = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
        return new Rotation((float)(Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f, (float)(- Math.atan2(diffY, hypotenuse) * 180.0 / 3.141592653589793));
    }
    public static void setTargetRotationReverse(final Rotation rotation, final int keepLength, final int revTick) {
        if(Double.isNaN(rotation.getYaw()) || Double.isNaN(rotation.getPitch())
                || rotation.getPitch() > 90 || rotation.getPitch() < -90)
            return;

        rotation.fixedSensitivity(mc2.gameSettings.mouseSensitivity);
        targetRotation = rotation;
      RotationUtils.keepLength = keepLength;
      RotationUtils.revTick = revTick+1;
    }
    public static Rotation getRotationsNonLivingEntity(IEntity entity) {
        return RotationUtils.getRotations(entity.getPosX(), entity.getPosY() + (entity.getEntityBoundingBox().getMaxY()-entity.getEntityBoundingBox().getMinY())*0.5, entity.getPosZ());
    }
    public static Rotation getRotations(double posX, double posY, double posZ) {
        EntityPlayerSP player = (EntityPlayerSP) RotationUtils.mc.getThePlayer();
        assert player != null;
        double x = posX - player.posX;
        double y = posY - (player.posY + (double)player.getEyeHeight());
        double z = posZ - player.posZ;
        double dist = MathHelper.sqrt(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float)(-(Math.atan2(y, dist) * 180.0 / 3.141592653589793));
        return new Rotation(yaw,pitch);
    }

    /**
     * Face block
     *
     * @param blockPos target block
     */


    public static VecRotation faceBlock(final WBlockPos blockPos) {
        if (blockPos == null)
            return null;

        VecRotation vecRotation = null;

        for (double xSearch = 0.1D; xSearch < 0.9D; xSearch += 0.1D) {
            for (double ySearch = 0.1D; ySearch < 0.9D; ySearch += 0.1D) {
                for (double zSearch = 0.1D; zSearch < 0.9D; zSearch += 0.1D) {
                    final WVec3 eyesPos = new WVec3(mc.getThePlayer().getPosX(), mc.getThePlayer().getEntityBoundingBox().getMinY() + mc.getThePlayer().getEyeHeight(), mc.getThePlayer().getPosZ());
                    final WVec3 posVec = new WVec3(blockPos).addVector(xSearch, ySearch, zSearch);
                    final double dist = eyesPos.distanceTo(posVec);

                    final double diffX = posVec.getXCoord() - eyesPos.getXCoord();
                    final double diffY = posVec.getYCoord() - eyesPos.getYCoord();
                    final double diffZ = posVec.getZCoord() - eyesPos.getZCoord();

                    final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

                    final Rotation rotation = new Rotation(
                            WMathHelper.wrapAngleTo180_float((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F),
                            WMathHelper.wrapAngleTo180_float((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)))
                    );

                    final WVec3 rotationVector = getVectorForRotation(rotation);
                    final WVec3 vector = eyesPos.addVector(rotationVector.getXCoord() * dist, rotationVector.getYCoord() * dist,
                            rotationVector.getZCoord() * dist);
                    final IMovingObjectPosition obj = mc.getTheWorld().rayTraceBlocks(eyesPos, vector, false,
                            false, true);

                    if (obj != null && obj.getTypeOfHit() == IMovingObjectPosition.WMovingObjectType.BLOCK) {
                        final VecRotation currentVec = new VecRotation(posVec, rotation);

                        if (vecRotation == null || getRotationDifference(currentVec.getRotation()) < getRotationDifference(vecRotation.getRotation()))
                            vecRotation = currentVec;
                    }
                }
            }
        }

        return vecRotation;
    }

    /**
     * Face target with bow
     *
     * @param target      your enemy
     * @param silent      client side rotations
     * @param predict     predict new enemy position
     * @param predictSize predict size of predict
     */
    public static void faceBow(final IEntity target, final boolean silent, final boolean predict, final float predictSize) {
        final IEntityPlayerSP player = mc.getThePlayer();

        final double posX = target.getPosX() + (predict ? (target.getPosX() - target.getPrevPosX()) * predictSize : 0) - (player.getPosX() + (predict ? (player.getPosX() - player.getPrevPosX()) : 0));
        final double posY = target.getEntityBoundingBox().getMinY() + (predict ? (target.getEntityBoundingBox().getMinY() - target.getPrevPosY()) * predictSize : 0) + target.getEyeHeight() - 0.15 - (player.getEntityBoundingBox().getMinY() + (predict ? (player.getPosY() - player.getPrevPosY()) : 0)) - player.getEyeHeight();
        final double posZ = target.getPosZ() + (predict ? (target.getPosZ() - target.getPrevPosZ()) * predictSize : 0) - (player.getPosZ() + (predict ? (player.getPosZ() - player.getPrevPosZ()) : 0));
        final double posSqrt = Math.sqrt(posX * posX + posZ * posZ);

        float velocity = LiquidBounce.moduleManager.getModule(FastBow.class).getState() ? 1F : player.getItemInUseDuration() / 20F;
        velocity = (velocity * velocity + velocity * 2) / 3;

        if (velocity > 1) velocity = 1;

        final Rotation rotation = new Rotation(
                (float) (Math.atan2(posZ, posX) * 180 / Math.PI) - 90,
                (float) -Math.toDegrees(Math.atan((velocity * velocity - Math.sqrt(velocity * velocity * velocity * velocity - 0.006F * (0.006F * (posSqrt * posSqrt) + 2 * posY * (velocity * velocity)))) / (0.006F * posSqrt)))
        );

        if (silent)
            setTargetRotation(rotation);
        else
            limitAngleChange(new Rotation(player.getRotationYaw(), player.getRotationPitch()), rotation, 10 +
                    new Random().nextInt(6)).toPlayer(mc.getThePlayer());
    }

    /**
     * Translate vec to rotation
     *
     * @param vec     target vec
     * @param predict predict new location of your body
     * @return rotation
     */
    public static Rotation toRotation(final WVec3 vec, final boolean predict) {
        final WVec3 eyesPos = new WVec3(mc.getThePlayer().getPosX(), mc.getThePlayer().getEntityBoundingBox().getMinY() +
                mc.getThePlayer().getEyeHeight(), mc.getThePlayer().getPosZ());

        if (predict)
            eyesPos.addVector(mc.getThePlayer().getMotionX(), mc.getThePlayer().getMotionY(), mc.getThePlayer().getMotionZ());

        final double diffX = vec.getXCoord() - eyesPos.getXCoord();
        final double diffY = vec.getYCoord() - eyesPos.getYCoord();
        final double diffZ = vec.getZCoord() - eyesPos.getZCoord();

        return new Rotation(WMathHelper.wrapAngleTo180_float(
                (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F
        ), WMathHelper.wrapAngleTo180_float(
                (float) (-Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ))))
        ));
    }

    /**
     * Get the center of a box
     *
     * @param bb your box
     * @return center of box
     */
    public static WVec3 getCenter(final IAxisAlignedBB bb) {
        return new WVec3(bb.getMinX() + (bb.getMaxX() - bb.getMinX()) * 0.5, bb.getMinY() + (bb.getMaxY() - bb.getMinY()) * 0.5, bb.getMinZ() + (bb.getMaxZ() - bb.getMinZ()) * 0.5);
    }

    /**
     * Search good center
     *
     * @param bb           enemy box
     * @param outborder    outborder option
     * @param random       random option
     * @param predict      predict option
     * @param throughWalls throughWalls option
     * @return center
     */
    public static VecRotation searchCenter(final IAxisAlignedBB bb, final boolean outborder, final boolean random, final boolean predict, final boolean throughWalls, final float distance) {
        if (outborder) {
            final WVec3 vec3 = new WVec3(bb.getMinX() + (bb.getMaxX() - bb.getMinX()) * (x * 0.3 + 1.0), bb.getMinY() + (bb.getMaxY() - bb.getMinY()) * (y * 0.3 + 1.0), bb.getMinZ() + (bb.getMaxZ() - bb.getMinZ()) * (z * 0.3 + 1.0));
            return new VecRotation(vec3, toRotation(vec3, predict));
        }

        final WVec3 randomVec = new WVec3(bb.getMinX() + (bb.getMaxX() - bb.getMinX()) * x * 0.8, bb.getMinY() + (bb.getMaxY() - bb.getMinY()) * y * 0.8, bb.getMinZ() + (bb.getMaxZ() - bb.getMinZ()) * z * 0.8);
        final Rotation randomRotation = toRotation(randomVec, predict);

        final WVec3 eyes = mc.getThePlayer().getPositionEyes(1F);

        VecRotation vecRotation = null;

        for(double xSearch = 0.15D; xSearch < 0.85D; xSearch += 0.1D) {
            for (double ySearch = 0.15D; ySearch < 1D; ySearch += 0.1D) {
                for (double zSearch = 0.15D; zSearch < 0.85D; zSearch += 0.1D) {
                    final WVec3 vec3 = new WVec3(bb.getMinX() + (bb.getMaxX() - bb.getMinX()) * xSearch,
                            bb.getMinY() + (bb.getMaxY() - bb.getMinY()) * ySearch, bb.getMinZ() + (bb.getMaxZ() - bb.getMinZ()) * zSearch);
                    final Rotation rotation = toRotation(vec3, predict);
                    final double vecDist = eyes.distanceTo(vec3);

                    if (vecDist > distance)
                        continue;

                    if (throughWalls || isVisible(vec3)) {
                        final VecRotation currentVec = new VecRotation(vec3, rotation);

                        if (vecRotation == null || (random ? getRotationDifference(currentVec.getRotation(), randomRotation) < getRotationDifference(vecRotation.getRotation(), randomRotation) : getRotationDifference(currentVec.getRotation()) < getRotationDifference(vecRotation.getRotation())))
                            vecRotation = currentVec;
                    }
                }
            }
        }

        return vecRotation;
    }

    /**
     * Calculate difference between the client rotation and your entity
     *
     * @param entity your entity
     * @return difference between rotation
     */
    public static double getRotationDifference(final IEntity entity) {
        final Rotation rotation = toRotation(getCenter(entity.getEntityBoundingBox()), true);

        return getRotationDifference(rotation, new Rotation(mc.getThePlayer().getRotationYaw(), mc.getThePlayer().getRotationPitch()));
    }

    /**
     * Calculate difference between the server rotation and your rotation
     *
     * @param rotation your rotation
     * @return difference between rotation
     */
    public static double getRotationDifference(final Rotation rotation) {
        return serverRotation == null ? 0D : getRotationDifference(rotation, serverRotation);
    }

    /**
     * Calculate difference between two rotations
     *
     * @param a rotation
     * @param b rotation
     * @return difference between rotation
     */


    public static double getRotationDifference(final Rotation a, final Rotation b) {
        return Math.hypot(getAngleDifference(a.getYaw(), b.getYaw()), a.getPitch() - b.getPitch());
    }

    /**
     * Limit your rotation using a turn speed
     *
     * @param currentRotation your current rotation
     * @param targetRotation your goal rotation
     * @param turnSpeed your turn speed
     * @return limited rotation
     */
    @NotNull
    public static Rotation limitAngleChange(final Rotation currentRotation, final Rotation targetRotation, final float turnSpeed) {
        final float yawDifference = getAngleDifference(targetRotation.getYaw(), currentRotation.getYaw());
        final float pitchDifference = getAngleDifference(targetRotation.getPitch(), currentRotation.getPitch());

        return new Rotation(
                currentRotation.getYaw() + (yawDifference > turnSpeed ? turnSpeed : Math.max(yawDifference, -turnSpeed)),
                currentRotation.getPitch() + (pitchDifference > turnSpeed ? turnSpeed : Math.max(pitchDifference, -turnSpeed)
        ));
    }

    /**
     * Calculate difference between two angle points
     *
     * @param a angle point
     * @param b angle point
     * @return difference between angle points
     */
    public static float getAngleDifference(final float a, final float b) {
        return ((((a - b) % 360F) + 540F) % 360F) - 180F;
    }

    /**
     * Calculate rotation to vector
     *
     * @param rotation your rotation
     * @return target vector
     */
    public static WVec3 getVectorForRotation(final Rotation rotation) {
        float yawCos = (float) Math.cos(-rotation.getYaw() * 0.017453292F - (float) Math.PI);
        float yawSin = (float) Math.sin(-rotation.getYaw() * 0.017453292F - (float) Math.PI);
        float pitchCos = (float) -Math.cos(-rotation.getPitch() * 0.017453292F);
        float pitchSin = (float) Math.sin(-rotation.getPitch() * 0.017453292F);
        return new WVec3(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
    }

    public static Rotation getRotationFromEyeHasPrev(double x, double y, double z) {
        double xDiff = x - (mc2.player.prevPosX + (mc2.player.posX - mc2.player.prevPosX));
        double yDiff = y - ((mc2.player.prevPosY + (mc2.player.posY - mc2.player.prevPosY)) + (mc2.player.getEntityBoundingBox().maxY - mc2.player.getEntityBoundingBox().minY));
        double zDiff = z - (mc2.player.prevPosZ + (mc2.player.posZ - mc2.player.prevPosZ));
        final double dist = MathHelper.sqrt(xDiff * xDiff + zDiff * zDiff);
        return new Rotation((float) (Math.atan2(zDiff, xDiff) * 180D / Math.PI) - 90F, (float) -(Math.atan2(yDiff, dist) * 180D / Math.PI));
    }

    public static Rotation getRotationFromEyeHasPrev(IEntityLivingBase target) {
        final double x = (target.getPrevPosX() + (target.getPosX() - target.getPrevPosX()));
        final double y = (target.getPrevPosY() + (target.getPosY() - target.getPrevPosY()));
        final double z = (target.getPrevPosZ() + (target.getPosZ() - target.getPosZ()));
        return getRotationFromEyeHasPrev(x, y, z);
    }
    /**
     * Allows you to check if your crosshair is over your target entity
     *
     * @param targetEntity       your target entity
     * @param blockReachDistance your reach
     * @return if crosshair is over target
     */
    public static boolean isFaced(final IEntity targetEntity, double blockReachDistance) {
        return RaycastUtils.raycastEntity(blockReachDistance, entity -> targetEntity != null && targetEntity.equals(entity)) != null;
    }

    /**
     * Allows you to check if your enemy is behind a wall
     */
    public static boolean isVisible(final WVec3 vec3) {
        final WVec3 eyesPos = new WVec3(mc.getThePlayer().getPosX(), mc.getThePlayer().getEntityBoundingBox().getMinY() + mc.getThePlayer().getEyeHeight(), mc.getThePlayer().getPosZ());

        return mc.getTheWorld().rayTraceBlocks(eyesPos, vec3) == null;
    }

    /**
     * Handle minecraft tick
     *
     * @param event Tick event
     */
    @EventTarget
    public void onTick(final TickEvent event) {
        if(targetRotation != null) {
            keepLength--;

            if (keepLength <= 0)
                reset();
        }

        if(random.nextGaussian() > 0.8D) x = Math.random();
        if(random.nextGaussian() > 0.8D) y = Math.random();
        if(random.nextGaussian() > 0.8D) z = Math.random();
    }

    /**
     * Set your target rotation
     *
     * @param rotation your target rotation
     */
    public static void setTargetRotation(final Rotation rotation, final int keepLength) {
        if (Double.isNaN(rotation.getYaw()) || Double.isNaN(rotation.getPitch())
                || rotation.getPitch() > 90 || rotation.getPitch() < -90)
            return;

        rotation.fixedSensitivity(mc.getGameSettings().getMouseSensitivity());
        targetRotation = rotation;
        RotationUtils.keepLength = keepLength;
    }

    /**
     * Set your target rotation
     *
     * @param rotation your target rotation
     */
    public static void setTargetRotation(final Rotation rotation) {
        setTargetRotation(rotation, 0);
    }

    /**
     * Handle packet
     *
     * @param event Packet Event
     */
    @EventTarget
    public void onPacket(final PacketEvent event) {
        final IPacket packet = event.getPacket();

        if (classProvider.isCPacketPlayer(packet)) {
            final ICPacketPlayer packetPlayer = packet.asCPacketPlayer();

            if (targetRotation != null && !keepCurrentRotation && (targetRotation.getYaw() != serverRotation.getYaw() || targetRotation.getPitch() != serverRotation.getPitch())) {
                packetPlayer.setYaw(targetRotation.getYaw());
                packetPlayer.setPitch(targetRotation.getPitch());
                packetPlayer.setRotating(true);
            }

            if (packetPlayer.isRotating())
                serverRotation = new Rotation(packetPlayer.getYaw(), packetPlayer.getPitch());
        }
    }

    /**
     * Reset your target rotation
     */
    public static void reset() {
        keepLength = 0;
        targetRotation = null;
    }

    /**
     * @return YESSSS!!!
     */
    @Override
    public boolean handleEvents() {
        return true;
    }
}
