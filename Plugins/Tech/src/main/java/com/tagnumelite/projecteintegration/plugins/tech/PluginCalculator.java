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
package com.tagnumelite.projecteintegration.plugins.tech;

import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import com.tagnumelite.projecteintegration.plugins.library.PluginSonarCore.SonarMapper;
import sonar.calculator.mod.common.recipes.*;

@PEIPlugin("calculator")
public class PluginCalculator extends APEIPlugin {
    @Override
    public void setup() {
        addMapper(new AlgorithmSeparatorMapper());
        addMapper(new AtomicCalculatorMapper());
        addMapper(new CalculatorMapper());
        addMapper(new ConductorMastMapper());
        addMapper(new ExtractionChamberMapper());
        addMapper(new FabricationChamberMapper());
        addMapper(new FlawlessCalculatorMapper());
        addMapper(new PrecisionChamberMapper());
        addMapper(new ProcessingChamberMapper());
        addMapper(new ReassemblyChamberMapper());
        addMapper(new RestorationChamberMapper());
        addMapper(new ScientificCalculatorMapper());
        addMapper(new StoneSeparatorMapper());
    }

    private static class AlgorithmSeparatorMapper extends SonarMapper {
        public AlgorithmSeparatorMapper() {
            super("Algorithm Separator");
        }

        @Override
        public void setup() {
            AlgorithmSeparatorRecipes.instance().getRecipes().forEach(this::addRecipe);
        }
    }

    private static class AtomicCalculatorMapper extends SonarMapper {
        public AtomicCalculatorMapper() {
            super("Atomic Calculator");
        }

        @Override
        public void setup() {
            AtomicCalculatorRecipes.instance().getRecipes().forEach(this::addRecipe);
        }
    }

    private static class ConductorMastMapper extends SonarMapper {
        public ConductorMastMapper() {
            super("Conductor Mast");
        }

        @Override
        public void setup() {
            ConductorMastRecipes.instance().getRecipes().forEach(this::addRecipe);
        }
    }

    private static class CalculatorMapper extends SonarMapper {
        public CalculatorMapper() {
            super("Calculator");
        }

        @Override
        public void setup() {
            CalculatorRecipes.instance().getRecipes().forEach(this::addRecipe);
        }
    }

    private static class ExtractionChamberMapper extends SonarMapper {
        public ExtractionChamberMapper() {
            super("Extraction Chamber");
        }

        @Override
        public void setup() {
            ExtractionChamberRecipes.instance().getRecipes().forEach(this::addRecipe);
        }
    }

    private static class FabricationChamberMapper extends SonarMapper {
        public FabricationChamberMapper() {
            super("Fabrication Chamber");
        }

        @Override
        public void setup() {
            FabricationChamberRecipes.instance().getRecipes().forEach(this::addRecipe);
        }
    }

    private static class FlawlessCalculatorMapper extends SonarMapper {
        public FlawlessCalculatorMapper() {
            super("Flawless Calculator");
        }

        @Override
        public void setup() {
            FlawlessCalculatorRecipes.instance().getRecipes().forEach(this::addRecipe);
        }
    }

    private static class PrecisionChamberMapper extends SonarMapper {
        public PrecisionChamberMapper() {
            super("Precision Chamber");
        }

        @Override
        public void setup() {
            PrecisionChamberRecipes.instance().getRecipes().forEach(this::addRecipe);
        }
    }

    private static class ProcessingChamberMapper extends SonarMapper {
        public ProcessingChamberMapper() {
            super("Processing Chamber");
        }

        @Override
        public void setup() {
            ProcessingChamberRecipes.instance().getRecipes().forEach(this::addRecipe);
        }
    }

    private static class ReassemblyChamberMapper extends SonarMapper {
        public ReassemblyChamberMapper() {
            super("Reassembly Chamber");
        }

        @Override
        public void setup() {
            ReassemblyChamberRecipes.instance().getRecipes().forEach(this::addRecipe);
        }
    }

    private static class RestorationChamberMapper extends SonarMapper {
        public RestorationChamberMapper() {
            super("Restoration Chamber");
        }

        @Override
        public void setup() {
            RestorationChamberRecipes.instance().getRecipes().forEach(this::addRecipe);
        }
    }

    private static class ScientificCalculatorMapper extends SonarMapper {
        public ScientificCalculatorMapper() {
            super("Scientific Calculator");
        }

        @Override
        public void setup() {
            ScientificRecipes.instance().getRecipes().forEach(this::addRecipe);
        }
    }

    private static class StoneSeparatorMapper extends SonarMapper {
        public StoneSeparatorMapper() {
            super("Stone Separator");
        }

        @Override
        public void setup() {
            StoneSeparatorRecipes.instance().getRecipes().forEach(this::addRecipe);
        }
    }
}
