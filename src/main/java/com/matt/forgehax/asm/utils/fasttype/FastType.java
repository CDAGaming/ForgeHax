package com.matt.forgehax.asm.utils.fasttype;

import com.google.common.collect.Lists;
import com.matt.forgehax.asm.utils.name.IName;
import joptsimple.internal.Strings;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created on 5/25/2017 by fr1kin
 */

public abstract class FastType<T> {
    protected final Class<?> insideClass;
    protected final IName<String> name;

    protected T type = null;

    protected boolean lookupFailed = false;
    protected AtomicBoolean printOnce = new AtomicBoolean(false);

    public FastType(Class<?> insideClass, IName<String> name) {
        Objects.requireNonNull(insideClass);
        this.insideClass = insideClass;
        this.name = name;
    }

    public boolean isError() {
        return printOnce.get();
    }

    protected boolean attemptLookup() throws Exception {
        if(!lookupFailed) {
            if (type == null) {
                type = lookup();
                lookupFailed = (type == null);
            }
            return !lookupFailed;
        } else return true; // previous attempt failed, trying again wont work
    }

    /**
     * Reflection lookup
     */
    protected abstract T lookup() throws Exception;
}
