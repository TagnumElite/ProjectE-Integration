package com.tagnumelite.projecteintegration.addons;

import com.tagnumelite.projecteintegration.api.recipe.IRecipeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.neoforged.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FAIL-SAFE Create integration.
 *
 * Purpose:
 * - Prevent Create integration from crashing EMC calculation
 * - Preserve vanilla + all other mod EMC values
 *
 * This implementation intentionally does not map Create recipes.
 */
public class CreateAddon implements IRecipeMapper<Object> {

    private static final Logger LOGGER = LogManager.getLogger("ProjectE-Integration|Create");

    @Override
    public String getName() {
        return "Create (Disabled)";
    }

    @Override
    public String getDescription() {
        return "Create integration disabled to prevent EMC calculation failures.";
    }

    @Override
    public NSSOutput getOutput(Object recipe) {
        // No output mapping
        return NSSOutput.EMPTY;
    }

    @Override
    public NSSInput getInput(Object recipe) {
        // No input mapping; mark as unsuccessful
        return new NSSInput(new Object2IntOpenHashMap<>(), false);
    }

    @Override
    public boolean convertRecipe(Object recipe) {
        // If Create isn't loaded, do nothing.
        if (!ModList.get().isLoaded("create")) {
            return false;
        }

        try {
            // Intentionally do not convert Create recipes.
            return false;
        } catch (Throwable t) {
            // MUST NOT throw; throwing here can nuke the whole EMC graph
            LOGGER.error("Prevented Create recipe from crashing EMC calculation", t);
            return false;
        }
    }
}
