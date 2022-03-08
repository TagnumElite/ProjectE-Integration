/*
 * Copyright (c) 2019-2022 TagnumElite
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
import moze_intel.projecte.api.data.CustomConversionBuilder;

public class PamsHarvestcraftAddon {
    public static final String MODID_FOOD_CORE = "pamhc2foodcore";
    public static final String MODID_FOOD_EXPANDED = "pamhc2foodexpanded";
    public static final String MODID_CROPS = "pamhc2crops";

    @ConversionProvider(MODID_FOOD_CORE)
    public static class PHCFoodCoreConversionProvider extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.comment("Sets default conversions for Pam's HarvestCraft: Food Core")
                    .before(forgeTag("salt"), 1)
                    .before(forgeTag("salt/salt"), 1)
                    .before(forgeTag("water/freshwater"), 1);
        }
    }

    @ConversionProvider(MODID_FOOD_EXPANDED)
    public static class PHCFoodExpandedConversionProvider extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.comment("Sets default conversions for Pam's HarvestCraft: Crops")
                    .before(forgeTag("rawfish"), 64)
                    .before(forgeTag("rawmeats/rawtofishitem"), 64)
                    .before(forgeTag("rawfish/rawtofishitem"), 64);
        }
    }

    @ConversionProvider(MODID_CROPS)
    public static class PHCCropsConversionProvider extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.comment("Sets default conversions for Pam's HarvestCraft: Crops")
                    .before(forgeTag("crops"), 16)
                    .before(forgeTag("seeds"), 16);
        }
    }
}
