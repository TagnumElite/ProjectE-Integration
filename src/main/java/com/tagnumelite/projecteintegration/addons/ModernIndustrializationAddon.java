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

import aztech.modern_industrialization.MIFluids;
import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.materials.MIMaterials;
import aztech.modern_industrialization.materials.part.MIParts;
import com.tagnumelite.projecteintegration.api.conversion.AConversionProvider;
import com.tagnumelite.projecteintegration.api.conversion.ConversionProvider;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

public class ModernIndustrializationAddon {
    protected static final String MODID = "modern_industrialization";

    protected static String NAME(String name) {
        return "ModernIndustrialization"+name+"Mapper";
    }

    public static abstract class AMIMachineRecipeMapper extends ARecipeTypeMapper<MachineRecipe> {
        @Override
        public NSSOutput getOutput(MachineRecipe recipe) {
            List<MachineRecipe.ItemOutput> items = recipe.itemOutputs.stream().filter(i -> i.probability() >= 1f).toList();
            List<MachineRecipe.FluidOutput> fluids = recipe.fluidOutputs.stream().filter(f -> f.probability() >= 1f).toList();

            //TODO: We should be doing this but it's causing problems because of all the fluid recipes
            //if (items.size() + fluids.size() == 1) { // Instead of mapping single output recipes, just process the item itself
            //    if (items.size() == 1) return new NSSOutput(items.getFirst().getStack());
            //    MachineRecipe.FluidOutput fluid = fluids.getFirst();
            //    return new NSSOutput(new FluidStack(fluid.fluid(), Math.toIntExact(fluid.amount())));
            //}

            NSSOutput.Builder builder = getOutputBuilder();

            items.forEach(itemOutput -> builder.addItem(itemOutput.getStack()));
            fluids.forEach(fluidOutput -> builder.addFluid(Math.toIntExact(fluidOutput.amount()), fluidOutput.fluid()));

            return builder.build();
        }

        @Override
        public NSSInput getInput(MachineRecipe recipe) {
            NSSInput.Builder builder = getInputBuilder();

            recipe.itemInputs.stream().filter(i -> i.probability() >= 1f)
                    .forEach(i -> builder.addIngredient(i.amount(), i.ingredient()));
            recipe.fluidInputs.stream().filter(i -> i.probability() >= 1f)
                    .forEach(f -> builder.addFluid(Math.toIntExact(f.amount()), f.fluid()));
            
            return builder.build();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MIAssemblyMapper extends AMIMachineRecipeMapper {
        @Override
        public String getName() {
            return NAME("Assembly");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == MIMachineRecipeTypes.ASSEMBLER;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MICentrifugeMapper extends AMIMachineRecipeMapper {
        @Override
        public String getName() {
            return NAME("Centrifuge");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == MIMachineRecipeTypes.CENTRIFUGE;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MIChemicalReactorMapper extends AMIMachineRecipeMapper {
        @Override
        public String getName() {
            return NAME("ChemicalReactor");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == MIMachineRecipeTypes.CHEMICAL_REACTOR;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MICompressorMapper extends AMIMachineRecipeMapper {
        @Override
        public String getName() {
            return NAME("Compressor");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == MIMachineRecipeTypes.COMPRESSOR;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MICuttingMapper extends AMIMachineRecipeMapper {
        @Override
        public String getName() {
            return NAME("Cutting");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == MIMachineRecipeTypes.CUTTING_MACHINE;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MIDistilleryMapper extends AMIMachineRecipeMapper {
        @Override
        public String getName() {
            return NAME("Distillery");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == MIMachineRecipeTypes.DISTILLERY;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MIElectrolyzerMapper extends AMIMachineRecipeMapper {
        @Override
        public String getName() {
            return NAME("Electrolyzer");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == MIMachineRecipeTypes.ELECTROLYZER;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MIMaceratorMapper extends AMIMachineRecipeMapper {
        @Override
        public String getName() {
            return NAME("Macerator");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == MIMachineRecipeTypes.MACERATOR;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MIMixerMapper extends AMIMachineRecipeMapper {
        @Override
        public String getName() {
            return NAME("Mixer");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == MIMachineRecipeTypes.MIXER;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MIPackerMapper extends AMIMachineRecipeMapper {
        @Override
        public String getName() {
            return NAME("Packer");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == MIMachineRecipeTypes.PACKER;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MIPolarizerMapper extends AMIMachineRecipeMapper {
        @Override
        public String getName() {
            return NAME("Polarizer");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == MIMachineRecipeTypes.POLARIZER;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MIUnpackerMapper extends AMIMachineRecipeMapper {
        @Override
        public String getName() {
            return NAME("Unpacker");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == MIMachineRecipeTypes.UNPACKER;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MIWiremillMapper extends AMIMachineRecipeMapper {
        @Override
        public String getName() {
            return NAME("Wiremill");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == MIMachineRecipeTypes.WIREMILL;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MIBlastFurnaceMapper extends AMIMachineRecipeMapper {
        @Override
        public String getName() {
            return NAME("BlastFurnace");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == MIMachineRecipeTypes.BLAST_FURNACE;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MICoveOvenMapper extends AMIMachineRecipeMapper {
        @Override
        public String getName() {
            return NAME("CokeOven");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == MIMachineRecipeTypes.COKE_OVEN;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MIDistillationTowerMapper extends AMIMachineRecipeMapper {
        @Override
        public String getName() {
            return NAME("DistillationTower");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == MIMachineRecipeTypes.DISTILLATION_TOWER;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MIFusionReactorMapper extends AMIMachineRecipeMapper {
        @Override
        public String getName() {
            return NAME("FusionReactor");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == MIMachineRecipeTypes.FUSION_REACTOR;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MIHeatExchangerMapper extends AMIMachineRecipeMapper {
        @Override
        public String getName() {
            return NAME("HeatExchanger");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == MIMachineRecipeTypes.HEAT_EXCHANGER;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MIImplosionCompressorMapper extends AMIMachineRecipeMapper {
        @Override
        public String getName() {
            return NAME("ImplosionCompressor");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == MIMachineRecipeTypes.IMPLOSION_COMPRESSOR;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MIPressurizerMapper extends AMIMachineRecipeMapper {
        @Override
        public String getName() {
            return NAME("Pressurizer");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == MIMachineRecipeTypes.PRESSURIZER;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MIQuarryMapper extends AMIMachineRecipeMapper {
        @Override
        public String getName() {
            return NAME("Quarry");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == MIMachineRecipeTypes.QUARRY;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MIVacuumFreezerMapper extends AMIMachineRecipeMapper {
        @Override
        public String getName() {
            return NAME("VacuumFreezer");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == MIMachineRecipeTypes.VACUUM_FREEZER;
        }
    }

    @ConversionProvider(MODID)
    public static class MIConversionProvider extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.before(MIMaterials.TITANIUM.getPart(MIParts.INGOT), 512)
                    .before(MIMaterials.CHROMIUM.getPart(MIParts.INGOT), 512)
                    .before(MIMaterials.ANTIMONY.getPart(MIParts.INGOT), 512)
                    .before(MIMaterials.IRIDIUM.getPart(MIParts.INGOT), 512)
                    .before(MIMaterials.MANGANESE.getPart(MIParts.DUST), 32)
                    .before(MIMaterials.MONAZITE.getPart(MIParts.DUST), 32)
                    .before(MIMaterials.BAUXITE.getPart(MIParts.DUST), 32)
                    .before(MIFluids.SHALE_OIL.asFluid(), 1)
                    .before(MIFluids.CRUDE_OIL.asFluid(), 1)
                    .before(MIFluids.LUBRICANT.asFluid(), 1);
        }
    }
}
