package me.entity303.serversystem.utils;

import org.bukkit.Bukkit;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class MetaValue {

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
            return this._value;
        }

        @Override
        public int asInt() {
            return this._value? 1 : 0;
        }

        @Override
        public float asFloat() {
            return this._value? 1 : 0;
        }

        @Override
        public double asDouble() {
            return this._value? 1 : 0;
        }

        @Override
        public long asLong() {
            return this._value? 1 : 0;
        }

        @Override
        public short asShort() {
            return (short) (this._value? 1 : 0);
        }

        @Override
        public byte asByte() {
            return (byte) (this._value? 1 : 0);
        }

        @Override
        public boolean asBoolean() {
            return this._value;
        }

        @Override
        public String asString() {
            return this._value? "true" : "false";
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
