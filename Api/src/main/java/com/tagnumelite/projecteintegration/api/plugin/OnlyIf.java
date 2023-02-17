/*
 * Copyright (c) 2019-2020 TagnumElite
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.tagnumelite.projecteintegration.api.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Plugin annotation that denotes that the plugin is only applicable when patterns return true.
 * @see PEIPlugin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OnlyIf {
    /**
     * Uses {@link net.minecraftforge.fml.common.versioning.VersionParser},
     * {@link net.minecraftforge.fml.common.versioning.VersionRange}
     * and {@link net.minecraftforge.fml.common.versioning.DefaultArtifactVersion} to use
     * Apache Maven rules
     *
     * @return A version range string as per the version range specification
     * @see <a href="https://maven.apache.org/enforcer/enforcer-rules/versionRanges.html">Version Range Specification</a>
     */
    String version() default "";

    /**
     * Checks if the mod version starts with given value
     *
     * @return A string to compare the beginning of with
     */
    String versionStartsWith() default "";

    /**
     * Checks if the mod versions ends with the given value
     *
     * If string begins with {@code '!'} then value will be inverse
     *
     * @return A string to compare the end of with
     */
    String versionEndsWith() default "";
}
