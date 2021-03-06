package com.matt.forgehax.mods;

import com.matt.forgehax.asm.events.ApplyCollisionMotionEvent;
import com.matt.forgehax.asm.events.WaterMovementEvent;
import com.matt.forgehax.asm.events.PacketEvent;
import com.matt.forgehax.asm.reflection.FastReflection;
import com.matt.forgehax.util.command.Setting;
import com.matt.forgehax.util.mod.ToggleMod;
import com.matt.forgehax.util.mod.loader.RegisterMod;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@RegisterMod
public class AntiKnockbackMod extends ToggleMod {
    public final Setting<Double> multiplier_x = getCommandStub().builders().<Double>newSettingBuilder()
            .name("multiplier_x")
            .description("Multiplier for X axis")
            .defaultTo(0.D)
            .build();

    public final Setting<Double> multiplier_y = getCommandStub().builders().<Double>newSettingBuilder()
            .name("multiplier_y")
            .description("Multiplier for Y axis")
            .defaultTo(0.D)
            .build();

    public final Setting<Double> multiplier_z = getCommandStub().builders().<Double>newSettingBuilder()
            .name("multiplier_z")
            .description("Multiplier for Z axis")
            .defaultTo(0.D)
            .build();

    public AntiKnockbackMod() {
        super("AntiKnockback", false, "Removes knockback movement");
    }

    /**
     * Stops TNT and knockback velocity
     */
    @SubscribeEvent
    public void onPacketRecieved(PacketEvent.Incoming.Pre event) {
        if(event.getPacket() instanceof SPacketExplosion) {
            // for tnt knockback
            SPacketExplosion packet = (SPacketExplosion)event.getPacket();
            FastReflection.Fields.SPacketExplosion_motionX.set(packet, FastReflection.Fields.SPacketExplosion_motionX.get(packet) * multiplier_x.getAsFloat());
            FastReflection.Fields.SPacketExplosion_motionY.set(packet, FastReflection.Fields.SPacketExplosion_motionY.get(packet) * multiplier_y.getAsFloat());
            FastReflection.Fields.SPacketExplosion_motionZ.set(packet, FastReflection.Fields.SPacketExplosion_motionZ.get(packet) * multiplier_z.getAsFloat());
        }
        if(event.getPacket() instanceof SPacketEntityVelocity) {
            // for player knockback
            if(((SPacketEntityVelocity) event.getPacket()).getEntityID() == MC.player.getEntityId()) {
                double multiX = multiplier_x.getAsInteger();
                double multiY = multiplier_y.getAsInteger();
                double multiZ = multiplier_z.getAsInteger();
                if(multiX == 0 && multiY == 0 && multiZ == 0) {
                    event.setCanceled(true);
                } else {
                    SPacketEntityVelocity packet = (SPacketEntityVelocity)event.getPacket();
                    FastReflection.Fields.SPacketEntityVelocity_motionX.set(packet, (int)(FastReflection.Fields.SPacketEntityVelocity_motionX.get(packet) * multiX));
                    FastReflection.Fields.SPacketEntityVelocity_motionY.set(packet, (int)(FastReflection.Fields.SPacketEntityVelocity_motionY.get(packet) * multiY));
                    FastReflection.Fields.SPacketEntityVelocity_motionZ.set(packet, (int)(FastReflection.Fields.SPacketEntityVelocity_motionZ.get(packet) * multiZ));
                }
            }
        }
    }

    /**
     * Stops velocity from water
     */
    @SubscribeEvent
    public void onWaterMovementEvent(WaterMovementEvent event) {
        if(event.getEntity().equals(MC.player)) {
            Vec3d moveDir = event.getMoveDir().normalize();
            event.getEntity().motionX += (moveDir.xCoord * 0.014D) * multiplier_x.get();
            event.getEntity().motionY += (moveDir.yCoord * 0.014D) * multiplier_y.get();
            event.getEntity().motionZ += (moveDir.zCoord * 0.014D) * multiplier_z.get();
            event.setCanceled(true);
        }
    }

    /**
     * Stops velocity from collision
     */
    @SubscribeEvent
    public void onApplyCollisionMotion(ApplyCollisionMotionEvent event) {
        if(event.getEntity().equals(MC.player)) {
            event.getEntity().addVelocity(
                    event.getMotionX() * multiplier_x.get(),
                    event.getMotionY() * multiplier_y.get(),
                    event.getMotionZ() * multiplier_z.get()
            );
            event.setCanceled(true);
        }
    }
}
