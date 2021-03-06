package com.matt.forgehax.asm.events;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created on 11/10/2016 by fr1kin
 */
public class BlockRenderEvent extends Event {
    private final BlockPos pos;
    private final IBlockState state;
    private final IBlockAccess access;
    private final VertexBuffer buffer;

    public BlockRenderEvent(BlockPos pos, IBlockState state, IBlockAccess access, VertexBuffer buffer) {
        this.pos = pos;
        this.state = state;
        this.access = access;
        this.buffer = buffer;
    }

    public BlockPos getPos() {
        return pos;
    }

    public IBlockAccess getAccess() {
        return access;
    }

    public IBlockState getState() {
        return state;
    }

    public VertexBuffer getBuffer() {
        return buffer;
    }
}
