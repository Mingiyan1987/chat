package ru.basanov.javacore.api.window;


import org.jetbrains.annotations.Nullable;

import javax.jcr.Session;

public interface Chat {

    void init();

    void shutdown();

    boolean login();

    boolean logout();

    boolean status();

    boolean save();

    @Nullable
    Session session = null;

}
