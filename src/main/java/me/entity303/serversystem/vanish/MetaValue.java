package me.entity303.serversystem.vanish;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class MetaValue {

    public MetaValue(ServerSystem plugin) {
    }

    public MetadataValue getMetaValue(boolean vanish) {
        return new MetadataValue() {
            @Override
            public Object value() {
                return vanish;
            }

            @Override
            public int asInt() {
                return vanish ? 1 : 0;
            }

            @Override
            public float asFloat() {
                return vanish ? 1 : 0;
            }

            @Override
            public double asDouble() {
                return vanish ? 1 : 0;
            }

            @Override
            public long asLong() {
                return vanish ? 1 : 0;
            }

            @Override
            public short asShort() {
                return vanish ? (short) 1 : (short) 0;
            }

            @Override
            public byte asByte() {
                return vanish ? (byte) 1 : (byte) 0;
            }

            @Override
            public boolean asBoolean() {
                return vanish;
            }

            @Override
            public String asString() {
                return vanish ? "true" : "false";
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
