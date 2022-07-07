/*
 * Copyright (c) 2019-2021 TagnumElite
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

package com.tagnumelite.projecteintegration.datagen;

import com.tagnumelite.projecteintegration.PEIntegration;
import com.tagnumelite.projecteintegration.api.conversion.AConversionProvider;
import com.tagnumelite.projecteintegration.api.conversion.ConversionProvider;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.data.CustomConversionProvider;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Map;

public class PEICustomConversionProvider extends CustomConversionProvider {
    private static final Type CONVERSION_PROVIDER_TYPE = Type.getType(ConversionProvider.class);

    protected PEICustomConversionProvider(DataGenerator generator) {
        super(generator);
    }

    private static NormalizedSimpleStack gemTag(String gem) {
        return tag("forge:gems/" + gem);
    }

    private static NormalizedSimpleStack tag(String tag) {
        return NSSItem.createTag(new ResourceLocation(tag));
    }

    // BELOW COPIED FROM: https://github.com/sinkillerj/ProjectE/blob/c0e58894bddef8c090c39dd29143e08932022833/src/datagen/java/moze_intel/projecte/common/PECustomConversionProvider.java#L279-L290
    private static NormalizedSimpleStack ingotTag(String ingot) {
        return tag("forge:ingots/" + ingot);
    }

    @Override
    protected void addCustomConversions() {
        createConversionBuilder(new ResourceLocation(PEIntegration.MODID, "pei_metals"))
                .before(ingotTag("zinc"), 128)
                .before(new FluidStack(Fluids.WATER, 250), 1);

        for (Map.Entry<AConversionProvider, String> entry : getConversionProviders().entrySet()) {
            ResourceLocation resourceLocation = new ResourceLocation(entry.getValue(), entry.getValue() + "_default");
            PEIntegration.debugLog("Add custom conversions for {}", resourceLocation);
            CustomConversionBuilder builder = createConversionBuilder(resourceLocation);
            entry.getKey().convert(builder);
        }
    }

    private Map<AConversionProvider, String> getConversionProviders() {
        ModList modList = ModList.get();
        Map<AConversionProvider, String> conversionProviders = new HashMap<>();
        for (ModFileScanData scanData : modList.getAllScanData()) {
            for (ModFileScanData.AnnotationData data : scanData.getAnnotations()) {
                if (CONVERSION_PROVIDER_TYPE.equals(data.annotationType())) {
                    AConversionProvider provider = createInstance(data.memberName());
                    if (provider != null) {
                        Map<String, Object> annotationData = data.annotationData();
                        if (!annotationData.containsKey("value")) {
                            continue;
                        }
                        conversionProviders.put(provider, (String) annotationData.get("value"));
                    }
                }
            }
        }
        return conversionProviders;
    }

    private AConversionProvider createInstance(String className) {
        try {
            return Class.forName(className).asSubclass(AConversionProvider.class).newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            PEIntegration.LOGGER.error("Failed to load conversion provider: {}", className, e);
        }
        return null;
    }
}
