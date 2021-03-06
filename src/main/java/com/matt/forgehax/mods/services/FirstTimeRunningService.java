package com.matt.forgehax.mods.services;

import com.matt.forgehax.FileManager;
import com.matt.forgehax.ForgeHax;
import com.matt.forgehax.events.LocalPlayerUpdateEvent;
import com.matt.forgehax.util.mod.ServiceMod;
import com.matt.forgehax.util.mod.loader.RegisterMod;
import joptsimple.internal.Strings;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

import static com.matt.forgehax.Helper.getModManager;
import static com.matt.forgehax.Helper.printMessageNaked;

/**
 * Created on 6/14/2017 by fr1kin
 */
@RegisterMod
public class FirstTimeRunningService extends ServiceMod {
    private static final File STARTUP_ONCE = FileManager.getInstance().getFileInConfigDirectory(".once");

    private static final String getOnceFileVersion() {
        if(STARTUP_ONCE.exists()) try {
            return new String(Files.readAllBytes(STARTUP_ONCE.toPath()));
        } catch (Throwable t) {}
        return Strings.EMPTY;
    }

    public FirstTimeRunningService() {
        super("FirstTimeRunningService");
    }

    @SubscribeEvent
    public void onLocalPlayerUpdate(LocalPlayerUpdateEvent event) {
        if(!Objects.equals(ForgeHax.MOD_VERSION, getOnceFileVersion())) {
            printMessageNaked(ForgeHax.getWelcomeMessage());
            try {
                Files.write(STARTUP_ONCE.toPath(), ForgeHax.MOD_VERSION.getBytes());
            } catch (IOException e) {
                ;
            }
        }
        getModManager().unregisterMod(this);
    }
}
