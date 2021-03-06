package com.matt.forgehax.mods;

import com.matt.forgehax.events.LocalPlayerUpdateEvent;
import com.matt.forgehax.util.command.Setting;
import com.matt.forgehax.util.key.Bindings;
import com.matt.forgehax.util.mod.ToggleMod;
import com.matt.forgehax.util.mod.loader.RegisterMod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.matt.forgehax.Helper.getLocalPlayer;

@RegisterMod
public class AutoSprintMod extends ToggleMod {
    private boolean isBound = false;

    public static final String[] modes = new String[] {"ALWAYS", "LEGIT"};

    public final Setting<String> mode = getCommandStub().builders().<String>newSettingBuilder()
            .name("mode")
            .description("Sprint mode")
            .defaultTo(modes[0])
            .build();

    public AutoSprintMod() {
        super("AutoSprint", false, "Automatically sprints");
    }

    private void startSprinting() {
        switch (mode.get().toUpperCase()) {
            case "ALWAYS":
                if(!getLocalPlayer().isCollidedHorizontally)
                    getLocalPlayer().setSprinting(true);
                break;
            default:
            case "LEGIT":
                if (!isBound) {
                    Bindings.sprint.bind();
                    isBound = true;
                }
                if (!Bindings.sprint.getBinding().isKeyDown())
                    Bindings.sprint.setPressed(true);
                break;
        }
    }

    private void stopSprinting() {
        if(isBound) {
            Bindings.sprint.setPressed(false);
            Bindings.sprint.unbind();
            isBound = false;
        }
    }

    /**
     * Stop sprinting when the mod is disabled
     */
    @Override
    public void onDisabled() {
        stopSprinting();
    }

    /**
     * Start sprinting every update tick
     */
    @SubscribeEvent
    public void onUpdate(LocalPlayerUpdateEvent event) {
        if(event.getEntityLiving().moveForward > 0 &&
                !event.getEntityLiving().isCollidedHorizontally &&
                !event.getEntityLiving().isSneaking()) {
            startSprinting();
        }
    }
}
