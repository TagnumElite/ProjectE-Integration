package com.tagnumelite.projecteintegration.compat;

import com.robotgryphon.compactcrafting.Registration;
import com.robotgryphon.compactcrafting.api.components.IRecipeBlockComponent;
import com.robotgryphon.compactcrafting.recipes.MiniaturizationRecipe;
import com.robotgryphon.compactcrafting.recipes.components.BlockComponent;
import com.tagnumelite.projecteintegration.PEIntegration;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.mapper.recipe.INSSFakeGroupManager;
import moze_intel.projecte.api.mapper.recipe.IRecipeTypeMapper;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NSSFake;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.Tuple;

import java.util.*;

@RecipeTypeMapper(requiredMods = {"compactcrafting"}, priority = 1)
public class CompactCraftingMapper implements IRecipeTypeMapper {
    @Override
    public String getName() {
        return "CompactCraftingMiniturizationMapper";
    }

    @Override
    public String getDescription() {
        return "Mapper for Compact Crafint miniturization recipes";
    }

    @Override
    public boolean canHandle(IRecipeType<?> iRecipeType) {
        return iRecipeType == Registration.MINIATURIZATION_RECIPE_TYPE;
    }

    @Override
    public boolean handleRecipe(IMappingCollector<NormalizedSimpleStack, Long> mapper, IRecipe<?> iRecipe, INSSFakeGroupManager _fgm) {
        MiniaturizationRecipe recipe = (MiniaturizationRecipe) iRecipe;
        IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();

        // We assume recipes require one single item as catalyst
        ingredientMap.addIngredient(NSSItem.createItem(recipe.getCatalyst()), 1);

        for (String componentKey : recipe.getComponentKeys()) {
            Optional<IRecipeBlockComponent> requiredBlock = recipe.getRecipeBlockComponent(componentKey);
            requiredBlock.ifPresent(iBlockComponent -> {
                if (iBlockComponent instanceof BlockComponent) {
                    BlockComponent blockComponent = (BlockComponent) iBlockComponent;
                    Item blockItem = blockComponent.getBlock().asItem();
                    if (blockItem != Items.AIR) ingredientMap.addIngredient(NSSItem.createItem(new ItemStack(blockItem)), 1);
                }
            });
        }

        ItemStack[] outputs = recipe.getOutputs();
        if (outputs.length == 1) {
            ItemStack output = outputs[0];
            mapper.addConversion(output.getCount(), NSSItem.createItem(output), ingredientMap.getMap());
            return true;
        } else if (outputs.length > 1) {
            // If the recipe contains multiple outputs, here comes the special stuff.
            NormalizedSimpleStack nssFake = NSSFake.create("Fake NSS for Compact Crafting Mini Recipe");
            
            IngredientMap<NormalizedSimpleStack> outputInputIng = new IngredientMap<>();
            outputInputIng.addIngredient(nssFake, 1);
            Map<NormalizedSimpleStack, Integer> outputInputMap = outputInputIng.getMap();
            
            for (ItemStack output : outputs) {
                mapper.addConversion(output.getCount(), NSSItem.createItem(output), outputInputMap);
            }
            // TODO: Decide whether it better to outputs.length or total outputs for each conversions
            mapper.addConversion(outputs.length, nssFake, ingredientMap.getMap());
            return true;
        } else {
            PEIntegration.LOGGER.warn("");
            return false;
        }
    }
}
