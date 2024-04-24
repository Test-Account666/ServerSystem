package me.entity303.serversystem.utils;

public interface IMorpher {

    @SuppressWarnings({ "MethodCanBeVariableArityMethod", "NewMethodNamingConvention" })
    Object invoke(Object[] args);
}
