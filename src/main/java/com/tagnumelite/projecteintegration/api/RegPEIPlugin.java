package com.tagnumelite.projecteintegration.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RegPEIPlugin {
  /** @return {@code String} The modid of the registered plugin */
  String modid();
}
