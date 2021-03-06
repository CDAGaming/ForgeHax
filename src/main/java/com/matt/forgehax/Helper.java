package com.matt.forgehax;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.matt.forgehax.util.command.CommandGlobal;
import com.matt.forgehax.util.mod.loader.ModManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

/**
 * Created on 4/25/2017 by fr1kin
 */
public class Helper implements Globals {
    public static CommandGlobal getGlobalCommand() {
        return CommandGlobal.getInstance();
    }

    public static Minecraft getMinecraft() {
        return MC;
    }

    public static ModManager getModManager() {
        return ModManager.getInstance();
    }

    public static FileManager getFileManager() {
        return FileManager.getInstance();
    }

    public static Logger getLog() {
        return LOGGER;
    }

    public static EntityPlayerSP getLocalPlayer() {
        return MC.player;
    }

    public static WorldClient getWorld() {
        return MC.world;
    }

    public static NetworkManager getNetworkManager() {
        return FMLClientHandler.instance().getClientToServerNetworkManager();
    }

    public static void printMessageNaked(String startWith, String message, Style firstStyle, Style secondStyle) {
        if(getLocalPlayer() != null && !Strings.isNullOrEmpty(message)) {
            if(message.contains("\n")) {
                Scanner scanner = new Scanner(message);
                scanner.useDelimiter("\n");
                Style s1 = firstStyle;
                Style s2 = secondStyle;
                while (scanner.hasNext()) {
                    printMessageNaked(startWith, scanner.next(), s1, s2);
                    // alternate between colors each newline
                    Style cpy = s1;
                    s1 = s2;
                    s2 = cpy;
                }
            } else {
                TextComponentString string = new TextComponentString(startWith + message.replaceAll("\r", ""));
                string.setStyle(firstStyle);
                getLocalPlayer().sendMessage(string);
            }
        }
    }

    public static void printMessageNaked(String append, String message, Style style) {
        printMessageNaked(append, message, style, style);
    }

    public static void printMessageNaked(String append, String message) {
        printMessageNaked(append, message, new Style().setColor(TextFormatting.WHITE), new Style().setColor(TextFormatting.GRAY));
    }

    public static void printMessageNaked(String message) {
        printMessageNaked("", message);
    }

    // Will append '[FH] ' in front
    public static void printMessage(String message) {
        if(!Strings.isNullOrEmpty(message)) printMessageNaked("[FH] " + message);
    }

    public static void printStackTrace(Throwable t) {
        getLog().error(Throwables.getStackTraceAsString(t));
    }

    public static void handleThrowable(Throwable t) {
        getLog().error(String.format("[%s] %s", t.getClass().getSimpleName(), Strings.nullToEmpty(t.getMessage())));
        if (t.getCause() != null) handleThrowable(t.getCause());
        printStackTrace(t);
    }
}
