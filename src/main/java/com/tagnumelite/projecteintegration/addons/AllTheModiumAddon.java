/*
 * Copyright (c) 2019-2026 TagnumElite
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

package com.tagnumelite.projecteintegration.addons;

import com.tagnumelite.projecteintegration.api.conversion.AConversionProvider;
import com.tagnumelite.projecteintegration.api.conversion.ConversionProvider;
import com.thevortex.allthemodium.registry.ModRegistry;
import moze_intel.projecte.api.data.CustomConversionBuilder;

//TODO: Relook at all the EMC values
public class AllTheModiumAddon {
    @ConversionProvider("allthemodium")
    public static final class ATMConversionProvider extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.before(ModRegistry.ATM_SMITHING.get(), 24000)
                    .before(ModRegistry.VIB_SMITHING.get(), 24000 * 2)
                    .before(ModRegistry.UNO_SMITHING.get(), 24000 * 4)
                    .before(ModRegistry.ALLTHEMODIUM_INGOT.get(), 122800)
                    .before(ModRegistry.VIBRANIUM_INGOT.get(), 122800 * 2)
                    .before(ModRegistry.UNOBTAINIUM_INGOT.get(), 122800 * 4)
                    .before(ModRegistry.ANCIENT_CAVEVINES.get(), 16)
                    .before(ModRegistry.PIGLICH_HEART.get(), 1024)
                    .before(ModRegistry.ANCIENT_STONE.get(), 8)
                    .before(ModRegistry.ANCIENT_DIRT.get(), 8)
                    .before(ModRegistry.ANCIENT_GRASS.get(), 8)
                    .before(ModRegistry.ANCIENT_PODZOL.get(), 8);
        }
    }
}
