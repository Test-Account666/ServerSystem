package me.entity303.serversystem.utils.interceptors;

import me.entity303.serversystem.main.ServerSystem;
import me.entity303.serversystem.utils.IMorpher;
import net.bytebuddy.implementation.bind.annotation.*;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class SaveDataInterceptor {
    private final ServerSystem _plugin;

    public SaveDataInterceptor(ServerSystem plugin) {
        this._plugin = plugin;
    }

    @SuppressWarnings("NewMethodNamingConvention")
    @RuntimeType
    public Object intercept(@This Object obj, @AllArguments Object[] allArguments, @Morph IMorpher morpher, @SuperMethod Method method) {
        this._plugin.GetVersionStuff().GetSaveData().SaveData((Player) obj);
        return null;
    }
}
