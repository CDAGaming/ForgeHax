package com.matt.forgehax.util.mod.loader;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.matt.forgehax.Globals;
import com.matt.forgehax.util.mod.BaseMod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created on 5/16/2017 by fr1kin
 */
public class ModManager implements Globals {
    private static final ModManager INSTANCE = new ModManager();

    public static ModManager getInstance() {
        return INSTANCE;
    }

    private final Map<String, BaseMod> mods = Maps.newTreeMap(String.CASE_INSENSITIVE_ORDER);

    private final Set<Class<? extends BaseMod>> foundClasses = Sets.newHashSet();

    public void registerMod(@Nonnull BaseMod mod) {
        if(foundClasses.contains(mod.getClass()))
            mods.put(mod.getModName(), mod);
        else
            LOGGER.warn(String.format("Not registering mod \"%s\" because its class is missing in foundClasses", mod.getModName()));
    }

    public void unregisterMod(@Nonnull BaseMod mod) {
        // mod.getClass() should be in foundClasses
        if(mods.remove(mod.getModName()) != null) mod.unload();
    }

    public void unregisterAll() {
        forEach(this::unregisterMod);
    }

    public void refreshMods() {
        forEach(mod -> {
            mod.unload();
            mod.load();
        });
    }

    public void addClass(Class<? extends BaseMod> clazz) {
        foundClasses.add(clazz);
    }

    public void addClassesInPackage(String pkg) {
        LOGGER.info("Search for mods inside \"" + pkg + "\"");
        foundClasses.addAll(ForgeHaxModLoader.getClassesInPackage(pkg));
    }

    public void loadClasses() {
        ForgeHaxModLoader.loadClasses(foundClasses).forEach(this::registerMod);
    }

    public void reloadClasses() {
        LOGGER.info("Reloading mods");
        unregisterAll();
        loadClasses();
        forEach(BaseMod::load);
    }

    public void forEach(final Consumer<BaseMod> consumer) {
        mods.forEach((k, v) -> consumer.accept(v));
    }

    @Nullable
    public BaseMod getMod(String mod) {
        return mods.get(mod);
    }

    public Collection<BaseMod> getMods() {
        return Collections.unmodifiableCollection(mods.values());
    }
}
