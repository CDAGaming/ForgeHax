package com.matt.forgehax.util.entity;

import com.matt.forgehax.Globals;
import com.matt.forgehax.asm.reflection.FastReflection;
import com.matt.forgehax.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import static com.matt.forgehax.Helper.*;

public class EntityUtils implements Globals {
    public static boolean isBatsDisabled = false;

    /**
     * Checks if the mob could be possibly hostile towards us (we can't detect their attack target easily)
     * Current entities:
     *                  PigZombie: Aggressive if arms are raised, when arms are put down a internal timer is slowly ticked down from 400
     *                  Wolf: Aggressive if the owner isn't the local player and the wolf is angry
     *                  Enderman: Aggressive if making screaming sounds
     */
    public static boolean isMobAggressive(Entity entity) {
        if(entity instanceof EntityPigZombie) {
            // arms raised = aggressive, angry = either game or we have set the anger cooldown
            if(((EntityPigZombie) entity).isArmsRaised() || ((EntityPigZombie) entity).isAngry()) {
                if(!((EntityPigZombie) entity).isAngry()) {
                    // set pigmens anger to 400 if it hasn't been angered already
                    FastReflection.Fields.EntityPigZombie_angerLevel.set((EntityPigZombie)entity, 400);
                }
                return true;
            }
        } else if(entity instanceof EntityWolf) {
            return ((EntityWolf) entity).isAngry() &&
                    !MC.player.equals(((EntityWolf) entity).getOwner());
        } else if(entity instanceof EntityEnderman) {
            return ((EntityEnderman) entity).isScreaming();
        }
        return false;
    }

    /**
     * Check if the mob is an instance of EntityLivingBase
     */
    public static boolean isLiving(Entity entity) {
        return entity instanceof EntityLivingBase;
    }

    /**
     * If the entity is a player
     */
    public static boolean isPlayer(Entity entity) {
        return entity instanceof EntityPlayer;
    }

    public static boolean isValidEntity(Entity entity) {
        Entity riding = getLocalPlayer().getRidingEntity();
        return entity.ticksExisted > 1 && entity.getEntityId() != -100 && (riding == null || !riding.equals(entity));
    }

    public static boolean isAlive(Entity entity) {
        return isLiving(entity) && !entity.isDead && ((EntityLivingBase)(entity)).getHealth() > 0;
    }

    /**
     * If the mob by default wont attack the player, but will if the player attacks it
     */
    public static boolean isNeutralMob(Entity entity) {
        return entity instanceof EntityPigZombie ||
                entity instanceof EntityWolf ||
                entity instanceof EntityEnderman;
    }

    /**
     * If the mob is friendly (not aggressive)
     */
    public static boolean isFriendlyMob(Entity entity) {
        return (entity.isCreatureType(EnumCreatureType.CREATURE, false) && !EntityUtils.isNeutralMob(entity)) ||
                (entity.isCreatureType(EnumCreatureType.AMBIENT, false) && !isBatsDisabled) ||
                entity instanceof EntityVillager ||
                entity instanceof EntityIronGolem ||
                (isNeutralMob(entity) && !EntityUtils.isMobAggressive(entity));
    }

    /**
     * If the mob is hostile
     */
    public static boolean isHostileMob(Entity entity) {
        return (entity.isCreatureType(EnumCreatureType.MONSTER, false) && !EntityUtils.isNeutralMob(entity)) ||
                EntityUtils.isMobAggressive(entity);
    }

    /**
     * Find the entities interpolated amount
     */
    public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
        return new Vec3d(
                (entity.posX - entity.lastTickPosX) * x,
                (entity.posY - entity.lastTickPosY) * y,
                (entity.posZ - entity.lastTickPosZ) * z
        );
    }
    public static Vec3d getInterpolatedAmount(Entity entity, Vec3d vec) {
        return getInterpolatedAmount(entity, vec.xCoord, vec.yCoord, vec.zCoord);
    }
    public static Vec3d getInterpolatedAmount(Entity entity, double ticks) {
        return getInterpolatedAmount(entity, ticks, ticks, ticks);
    }

    /**
     * Find the entities interpolated position
     */
    public static Vec3d getInterpolatedPos(Entity entity, float ticks) {
        return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(getInterpolatedAmount(entity, ticks));
    }

    /**
     * Find the entities interpolated eye position
     */
    public static Vec3d getInterpolatedEyePos(Entity entity, float ticks) {
        return getInterpolatedPos(entity, ticks).addVector(0, entity.getEyeHeight(), 0);
    }

    /**
     * Get entities eye position
     */
    public static Vec3d getEyePos(Entity entity) {
        return new Vec3d(
                entity.posX,
                entity.posY + entity.getEyeHeight(),
                entity.posZ
        );
    }

    /**
     * Find the center of the entities hit box
     */
    public static Vec3d getOBBCenter(Entity entity) {
        AxisAlignedBB obb = entity.getEntityBoundingBox();
        return new Vec3d(
                (obb.maxX + obb.minX) / 2.D,
                (obb.maxY + obb.minY) / 2.D,
                (obb.maxZ + obb.minZ) / 2.D
        );
    }

    /**
     * Create a trace
     */
    public static RayTraceResult traceEntity(World world, Vec3d start, Vec3d end, List<Entity> filter) {
		RayTraceResult result = null;
		double hitDistance = -1;

		for (Object obj : world.loadedEntityList) {
			Entity entity = (Entity) obj;

			if (filter.contains(entity))
				continue;

			double distance = start.distanceTo(entity.getPositionVector());
			RayTraceResult trace = entity.getEntityBoundingBox().calculateIntercept(start, end);

			if (trace != null && (hitDistance == -1 || distance < hitDistance)) {
				hitDistance = distance;
				result = trace;
				result.entityHit = entity;
			}
		}

		return result;
	}

    /**
     * Find the entities draw color
     */
    public static int getDrawColor(EntityLivingBase living) {
        if(LocalPlayerUtils.isTargetEntity(living)) {
            return Utils.Colors.WHITE;
        } else if(isPlayer(living)) {
            if(PlayerUtils.isFriend((EntityPlayer)living))
                return Utils.Colors.GREEN;
            else
                return Utils.Colors.RED;
        } else if(isHostileMob(living)) {
            return Utils.Colors.ORANGE;
        } else if(isFriendlyMob(living)) {
            return Utils.Colors.GREEN;
        } else {
            return Utils.Colors.WHITE;
        }
    }
}
