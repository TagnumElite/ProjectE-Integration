package com.tagnumelite.projecteintegration.api.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PEIPlugin {
    /**
     * @return {@code String} The modid of the registered plugin
     */
    String value();
}
