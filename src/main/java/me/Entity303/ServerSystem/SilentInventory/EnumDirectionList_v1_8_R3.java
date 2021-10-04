package me.Entity303.ServerSystem.SilentInventory;

import com.google.common.collect.Iterators;
import net.minecraft.server.v1_8_R3.EnumDirection;

import java.util.Iterator;


public enum EnumDirectionList_v1_8_R3 implements Iterable<EnumDirection> {
    HORIZONTAL(EnumDirection.EnumDirectionLimit.HORIZONTAL), VERTICAL(EnumDirection.EnumDirectionLimit.VERTICAL);

    private final EnumDirection.EnumDirectionLimit list;

    EnumDirectionList_v1_8_R3(EnumDirection.EnumDirectionLimit list) {
        this.list = list;
    }

    public Iterator<EnumDirection> iterator() {
        return Iterators.forArray(this.list.a());
    }
}
