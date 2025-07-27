package me.testaccount666.serversystem.updates;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Getter
public enum UpdateCheckerType {
    DISABLED(DisabledUpdateChecker::new),
    HANGAR(HangarUpdateChecker::new),
    MAIN(MainUpdateChecker::new);

    private final Supplier<AbstractUpdateChecker> _factory;

    public static Optional<UpdateCheckerType> of(String name) {
        return Arrays.stream(values()).filter(type -> type.name().equalsIgnoreCase(name)).findFirst();
    }
}
