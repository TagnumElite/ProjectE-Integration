package com.tagnumelite.projecteintegration.api.utils;

import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.internal.sized.SizedIngredient;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.ClassUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Utils {
    /**
     * Compare {@code ItemStack}s against each other
     *
     * @param stack1 {@code ItemStack} The stack to compare against
     * @param stack2 {@code ItemStack} The stack to compare with
     * @return {@code boolean} Whether or not they are the same item
     */
    public static boolean isSameItem(ItemStack stack1, ItemStack stack2) {
        return stack2.getItem() == stack1.getItem()
            && (stack2.getItemDamage() == 32767 || stack2.getItemDamage() == stack1.getItemDamage());
    }

    /**
     * @param outputs
     * @return
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Object> createOutputs(Object output, Object... outputs) {
        ArrayList<Object> ret = new ArrayList<>();
        ArrayList<Object> l = new ArrayList<>();

        l.add(output);

        if (outputs != null && outputs.length > 0) {
            l.addAll(Arrays.asList(outputs));
        }

        for (Object t : l) {
            if (t instanceof List) {
                ret.addAll((Collection<? extends Object>) t);
            } else if (t instanceof Object[]) {
                ret.addAll(Arrays.asList(t));
            } else {
                ret.add(t);
            }
        }

        l.clear();

        return ret;
    }

    public static IngredientMap<Object> createInputs(Object... inputs) {
        return getInputsFirst(new IngredientMap<>(), inputs);
    }

    public static IngredientMap<Object> getInputsFirst(IngredientMap<Object> ingredients, Object... inputs) {
        for (Object input : inputs) {
            if (input == null)
                continue;

            if (input instanceof ItemStack) {
                if (((ItemStack) input).isEmpty())
                    continue;

                ingredients.addIngredient(input, ((ItemStack) input).getCount());
            } else if (input instanceof Item || input instanceof Block || input instanceof String
                || input.getClass().equals(Object.class)) {
                ingredients.addIngredient(input, 1);
            } else if (input instanceof FluidStack) {
                if (((FluidStack) input).amount <= 0)
                    continue;

                ingredients.addIngredient(input, ((FluidStack) input).amount);
            } else if (input instanceof List) {
                List<?> inputt = (List<?>) input;
                if (inputt.isEmpty())
                    continue;

                if (inputt.size() == 1) {
                    getInputsFirst(ingredients, inputt.get(0));
                    continue;
                }

                ingredients.addIngredient(PEIApi.getList((List<?>) input), 1);
            } else if (input instanceof Ingredient) {
                if (input == Ingredient.EMPTY)
                    continue;

                ingredients.addIngredient(PEIApi.getIngredient((Ingredient) input), 1);
            } else if (input instanceof SizedIngredient) {
                SizedIngredient inp = (SizedIngredient) input;
                if (inp.object == Ingredient.EMPTY)
                    continue;

                ingredients.addIngredient(PEIApi.getIngredient(inp.object), inp.amount);
            } else if (input instanceof Object[]) {
                PEIApi.LOG.debug("Found Array within Array: {} within {}", input, inputs);
                getInputsSecond(ingredients, (Object[]) input);
            } else {
                PEIApi.LOG.warn("Unknown Input: {} ({})", input, ClassUtils.getPackageCanonicalName(input.getClass()));
                continue;
            }
        }

        return ingredients;
    }

    public static IngredientMap<Object> getInputsSecond(IngredientMap<Object> ingredients, Object... inputs) {
        for (Object input : inputs) {
            if (input == null)
                continue;

            if (input instanceof ItemStack) {
                if (((ItemStack) input).isEmpty())
                    continue;

                ingredients.addIngredient(input, ((ItemStack) input).getCount());
            } else if (input instanceof Item || input instanceof Block || input instanceof String
                || input.getClass().equals(Object.class)) {
                ingredients.addIngredient(input, 1);
            } else if (input instanceof FluidStack) {
                if (((FluidStack) input).amount <= 0)
                    continue;

                ingredients.addIngredient(input, ((FluidStack) input).amount);
            } else if (input instanceof List) {
                List<?> inputt = (List<?>) input;
                if (inputt.isEmpty())
                    continue;

                if (inputt.size() == 1) {
                    getInputsFirst(ingredients, inputt.get(0));
                    continue;
                }

                ingredients.addIngredient(PEIApi.getList((List<?>) input), 1);
            } else if (input instanceof Ingredient) {
                if (input == Ingredient.EMPTY)
                    continue;

                ingredients.addIngredient(PEIApi.getIngredient((Ingredient) input), 1);
            } else if (input instanceof Object[]) {
                PEIApi.LOG.debug("Found Array within Array: {} within {}", input, inputs);
                getInputsFirst(ingredients, Arrays.asList(input));
            } else {
                PEIApi.LOG.warn("Unknown Input: {} ({})", input, ClassUtils.getPackageCanonicalName(input.getClass()));
                continue;
            }
        }

        return ingredients;
    }

    /**
     * @param component ITextComponent to {@link ITextComponent#appendSibling(ITextComponent)} with the component from {@link #prefixComponent()}.
     * @return Returns the {@link ITextComponent} from {@link #prefixComponent()} with sibling 'component'
     */
    public static ITextComponent prefixComponent(ITextComponent component) {
        return prefixComponent().appendSibling(component);
    }

    /**
     * @return A text component with msg {@code [ProjectE Integration]: } and extra formatting
     */
    public static TextComponentString prefixComponent() {
        return new TextComponentString("[" + TextFormatting.DARK_AQUA + PEIApi.NAME + TextFormatting.RESET + "]: ");
    }
}
