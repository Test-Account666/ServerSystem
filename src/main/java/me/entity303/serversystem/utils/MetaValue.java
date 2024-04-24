package me.entity303.serversystem.utils;

import me.entity303.serversystem.main.ServerSystem;
import org.bukkit.Bukkit;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class MetaValue {

    public MetaValue(ServerSystem plugin) {
    }

    public MetadataValue GetMetaValue(boolean value) {
        return new MyMetadataValue(value);
    }

    private static class MyMetadataValue implements MetadataValue {
        private final boolean _value;

        public MyMetadataValue(boolean value) {
            this._value = value;
        }

        @Override
        public Object value() {
            return _value;
        }

        @Override
        public int asInt() {
            return _value? 1 : 0;
        }

        @Override
        public float asFloat() {
            return _value? 1 : 0;
        }

        @Override
        public double asDouble() {
            return _value? 1 : 0;
        }

        @Override
        public long asLong() {
            return _value? 1 : 0;
        }

        @Override
        public short asShort() {
            return (short) (_value? 1 : 0);
        }

        @Override
        public byte asByte() {
            return (byte) (_value? 1 : 0);
        }

        @Override
        public boolean asBoolean() {
            return _value;
        }

        @Override
        public String asString() {
            return _value? "true" : "false";
        }

        @Override
        public Plugin getOwningPlugin() {
            return Bukkit.getPluginManager().getPlugin("ServerSystem");
        }

        @Override
        public void invalidate() {

        }
    }
}
