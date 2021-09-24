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
import com.tagnumelite.projecteintegration.api.ConversionProvider;
import com.tagnumelite.projecteintegration.api.IConversionProvider;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.data.CustomConversionProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Map;

public class PEICustomConversionProvider extends CustomConversionProvider {
    protected PEICustomConversionProvider(DataGenerator generator) {
        super(generator);
    }

    private static final Type CONVERSION_PROVIDER_TYPE = Type.getType(ConversionProvider.class);

    @Override
    protected void addCustomConversions() {
        for (Map.Entry<IConversionProvider, String> entry : getConversionProviders().entrySet()) {
            ResourceLocation resourceLocation = new ResourceLocation(entry.getValue(), entry.getValue()+"_default");
            PEIntegration.debugLog("Add custom conversions for {}", resourceLocation);
            CustomConversionBuilder builder = createConversionBuilder(resourceLocation);
            entry.getKey().convert(builder);
        }
    }

    private Map<IConversionProvider, String> getConversionProviders() {
        ModList modList = ModList.get();
        Map<IConversionProvider, String> conversionProviders = new HashMap<>();
        for (ModFileScanData scanData : modList.getAllScanData()) {
            for (ModFileScanData.AnnotationData data : scanData.getAnnotations()) {
                if (CONVERSION_PROVIDER_TYPE.equals(data.getAnnotationType())) {
                    IConversionProvider provider = createInstance(data.getMemberName());
                    if (provider != null) {
                        Map<String, Object> annotationData = data.getAnnotationData();
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

    private IConversionProvider createInstance(String className) {
        try {
            return Class.forName(className).asSubclass(IConversionProvider.class).newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            PEIntegration.LOGGER.error("Failed to load conversion provider: {}", className, e);
        }
        return null;
    }
}
