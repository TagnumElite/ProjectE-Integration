package com.tagnumelite.projecteintegration.api.utils;

import com.tagnumelite.projecteintegration.api.PEIApi;
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
