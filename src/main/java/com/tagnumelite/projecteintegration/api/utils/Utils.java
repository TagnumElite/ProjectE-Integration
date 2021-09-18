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
package com.tagnumelite.projecteintegration.api.utils;

import com.google.common.collect.ImmutableList;
import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.internal.lists.InputList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

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

    public static List<?> convertGrid(List<?> list) {
        ImmutableList.Builder<Object> ret = ImmutableList.builder();
        for (Object t : list) {
            if (t instanceof List) {
                ret.add(new InputList<Object>((List<?>) t));
            } else {
                ret.add(t);
            }
        }
        return ret.build();
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
