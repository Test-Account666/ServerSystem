package me.entity303.serversystem.silentinventory;

import com.google.common.collect.Iterators;
import net.minecraft.server.v1_9_R2.EnumDirection;

import java.util.Iterator;


public enum EnumDirectionList_v1_9_R2 implements Iterable<EnumDirection> {
    HORIZONTAL(EnumDirection.EnumDirectionLimit.HORIZONTAL), VERTICAL(EnumDirection.EnumDirectionLimit.VERTICAL);

    private final EnumDirection.EnumDirectionLimit list;

    EnumDirectionList_v1_9_R2(EnumDirection.EnumDirectionLimit list) {
        this.list = list;
    }

    public Iterator<EnumDirection> iterator() {
        return Iterators.forArray(this.list.a());
    }
}
