package me.entity303.serversystem.utils;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class MetaValue {

    public MetaValue(ServerSystem plugin) {
    }

    public MetadataValue getMetaValue(boolean value) {
        return new MetadataValue() {
            @Override
            public Object value() {
                return value;
            }

            @Override
            public int asInt() {
                return value? 1 : 0;
            }

            @Override
            public float asFloat() {
                return value? 1 : 0;
            }

            @Override
            public double asDouble() {
                return value? 1 : 0;
            }

            @Override
            public long asLong() {
                return value? 1 : 0;
            }

            @Override
            public short asShort() {
                return value? (short) 1 : (short) 0;
            }

            @Override
            public byte asByte() {
                return value? (byte) 1 : (byte) 0;
            }

            @Override
            public boolean asBoolean() {
                return value;
            }

            @Override
            public String asString() {
                return value? "true" : "false";
            }

            @Override
            public Plugin getOwningPlugin() {
                return Bukkit.getPluginManager().getPlugin("ServerSystem");
            }

            @Override
            public void invalidate() {

            }
        };
    }
}
