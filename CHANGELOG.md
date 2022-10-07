# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [6.0.5]
### Ported
- Support for [Alchemistry]
- Support for [Corail Woodcutter]
- Support for [Farming for Blockheads]
- Support for [FTB Industrial Contraptions] (Canning is disabled)

## [6.0.4]
### Updated
- [Immersive Engineering] addon to 1.19.2
- 
## [6.0.3]
### Ported
- Fix for BlueSkies from 1.18.2

## [6.0.1]
### Updated
- Support for 1.19.2
- Addons: [Applied Energistics], [Ars Nouveau], [Blue Skies], [Extended Crafting], [Mystical Agriculture]

### Removed
- Support for [Botania], [Chipped], [Create], [Immersive Engineering], [Mana and Artifice], Pam's Harvest Craft, Thermal, [Touhou Little Maid]
- DataGen for [Blue Skies]

## [5.3.2]
### Added
- Partial Support for [Blue Skies] recipes and emc values

### Changed
- Using Parchment mappings

## [5.3.1]
### Added
- Support for [Ars Nouveau] recipes and emc to items

## [5.3.0]
### Added
- Support for more [Create] recipes

### Changed
- Separated the mappers for [Chipped]

## [5.2.4]
### Added
- Support for recipes from [Chipped]

## [5.2.3]
### Added
- WIP Support for [Mana And Artifice]
- Support for [Create] Haunting fan

## [5.2.2] version bump

## [5.2.1]
### Fixed
- Default conversions for fluix dust in [Applied Energistics]

## [5.2.0]
### Added
- Re-Added Support for [Touhou Little Maid] Altar crafting

### Fixed
- Support for [Create] as they change the result type from getRollableResults.

## [5.1.0]
### Added
- Support for [Create] Mechanical Crafter
- NSSOutput.Builder for building NSSOutput with multiple outputs
- Support for [Thermal] Machines

### Changed
- Use pattern matching in Utils

### Fixed
- Fixed the url for updateJSON in the mods.toml

### Removed
- Unused code in CreateAddon

## [5.0.3]
### Fixed
- Fixed support for [PneumaticCraft: Repressurized] 3.3.0

## 5.0.2 - version bump

## [5.0.1]
### Fixed
- Fixed support for [Farmers Delight] 1.1.2

## [5.0.0]
### Fixed
- Added support for 1.18.2

## [4.4.4]
### Fixed
- Updated support for newer ProjectE by not using their, now removed, DEV_ENVIRONMENT var.

## [4.4.3]
### Added
- Support for [MrCrayfish's Vehicle Mod] Fluid Extractor and Fluid Mixer

## [4.4.2]
### Added
- EMC for Pams Harvestcraft Core, Crops, Food Extended items

## [4.4.1]
### Added
- Support for [Touhou Little Maid] altar crafting

## [4.4.0]
### Added
- Support for [Industrial Foregoing]
- A base class for Custom Mappers and IRecipe mappers.
- Shortcut for NSSOutput

### Changed
- Made getDesc an optional override for ARecipeTypeMapper.
- Renamed [Extended Crafting] mappers
- Moved RecipeTypeMapper getDesc to base class

### Removed
- All overridden getDesc in all addons.

## [4.3.0]
### Fixed
- Updated support for [Compact Crafting], now requires version 1.0.0+

## [4.2.0]
### Added
- [Tinkers Construct] Alloying, Casting and Melting recipes mapped (except material recipes as NBT is difficult)
- Support for mapping a single output with multiple variants
- Support for mapping a list of Fluids as a single ingredient

## [4.1.7]
### Fixed
- [Astral Sorcery] Infusion recipe mapper was using client only method

### Changed
- [Astral Sorcery] Altar recipe mapper now uses temp fix (bandage)

## [4.1.6]
### Changed
- Farmers delight now requires version 5.0+ because it changed package paths.

### Fixed
- Utils.mapOutputs is now skipping empty outputs.

## [4.1.5]
### Fixed
- Support for [Create] Mixer, I was using the wrong recipe type.

## [4.1.4]
### Added
- Support for dusts tags to AConversionProvider
- [Immersive Engineering] Add Default EMC values. Currently manually mapping Treaded Planks, this must change eventually.

## [4.1.3]
### Added
- Gave EMC to some [Farmers Delight] crops
- Fake Random number generator
- Support for [Blood Magic] (Alchemy Table, Alchemy Array, Alchemical Reaction Chamber, Blood Altar, Tartaric Forge)
- The ability to make custom recipe mappers
- Support for [Ice and Fire] (Dragonforge)

### Changed
- Moved `AConversionProvider` and `ConversionProvider` to subpackage `conversion`
- Moved `APEIRecipeMapper` to subpackage `recipe`
- Moved `NSSInput` and `NSSOutput` to subpackage `nss`
- Renamed `APEIRecipeMapper` to `ARecipeTypeMapper`
- A whole codebase cleanup
- Move lots of functions from `ARecipeTypeMapper` to `Utils`

## [4.1.2]
### Added
- Support for [Psi] (Trick)
- Support for [Mystical Agriculture] (Infusion, Seed Reprocessor)

### Fixed
- Fixed [Astral Sorcery] marble not being assigned emc
- Changelog version links incorrect

## [4.1.1]
### Added
- Support for [Astral Sorcery] (Altar, Block Transmutation, Liquid Infusion, Well)

## [4.1.0]
### Added
- DataGen for EMC provision from addons

### Changed
- Renamed compact to addons and all mapper parent classes to suffix Addon

## [4.0.9]
### Added
- Support for [Botania] (Elven Trade, Mana Infusion, Petal Apothecary, Pure Daisy, Rune Alter, Terra Plate)

## [4.0.8]
### Added
- Support for [PneumaticCraft: Repressurized] (Amadron, Assembly, Explosion Crafting, Heat Frame Cooling, Pressure Chamber, Refinery, Thermopneumatic Plant)
- Added convertIngredient for FluidStack

## [4.0.7]
### Added
- Support for [Farmers Delight] (Cooking Pot, Cutting Board)

### Fixed
- Support for [Ex Nihilo Sequentia]

## [4.0.6]
### Added 
- GitHub actions for publishing builds to GitHub and CurseForge
- Support for [Ex Nihilo Sequentia] (Crucible, Fluid Item Transformation, Fluid On Top, Hammer, Sieve)

### Removed
- CurseGradle, it just suddenly broke.

## [4.0.5]
### Added
- Support for [Create] (Basin, Compacting, Crushing, Cutting, Milling, Mixing, Pressing, Splashing, Deploying, Sandpaper Polishing)
- The ability to designate empty recipe output

### Fixed
- Mapper errors don't cause ProjectE to fail mapping everything.
- Empty recipe outputs fail gracefully
- [Create] recipe outputs now report as Empty when it should

## [4.0.4]
### Fixed
- [Applied Energistics] inscriber mapper didn't know the deference between inscribing and pressing recipes.

### Removed
- Support for [Immersive Engineering] Squeezer and Arc Furnace,  these mappers were causing others to fail.

## [4.0.3]
### Added
- Javadoc is now published to gh-pages branch
- Support for [Extended Crafting] Compressor

## [4.0.2]
### Added
- Support for [Applied Energistics] (Grinder, Inscriber)
- Support for [Draconic Evolution] (Fusion)
- Support for [Immersive Engineering] (Alloy, Arc Furnace, Blast Furnace, Coke Oven, Crusher, Metal Press, Mixer, Sawmill, Squeezer)
- Support for [Woot] (Anvil, Fluid Converter, Infuser) I will add Dye Squeezer later when brain no hurty anymore.

### Changed
- Project icon was replaced with placeholder

### Removed
- Support for [Extended Crafting] Compressor, for now. Just ironning some kinks out.

## [4.0.1]
### Added
- Support for [Compact Crafting] miniaturization

## [4.0.0]
### Added
- Support for [Extended Crafting]

### Changed
- Project was scrubbed clean and restarted

### Removed
- All mod support
- All API
- The seperated projects

## [3.0.0-OLD]
### Added
- Added Intellij files
- Support for Chisel
    - Maps all groups registered, takes the first item in group as the primary
- Support for Galaticraft and Galaticraft-Planets
    - Circuit Fabricator, Compressor and NASA workbench
    - Set EMC for Meteoric Iron Raw (512), Desh Ore (256), Dense Ice (1) Ilmenite Ore (1024)
- Support for GregTechCE
    - All recipe maps, Coke Oven and Primitive Blast Furnace
- Support for NuclearCraft Overhauled
    - All machines I could find
- Support for PneumaticCraft
    - Amadron (Static Only), Assembly (Drill, Laser), Basic Thermopneumatic Processing Plant, Explosion, Heat Frame Cooling,
    Pressure Chamber, Refinery
- Proper API and javadoc jars for publishing
- Added loads of javadocs to api
- Added message of server join/world load to notify of updates and failed plugins and mappers
- Added support for JitPack build and mapped my domain `tagnumelite.com`
- Added `update.json` for version fetching
- Added task to update `update.json` with current version
- Added getMapperName to ConfigHelper
- Add Kiln support to Immersive Engineering
- Added option to ignore slag output in Blast Furnace for Immersive Engineering
- Added support for the Fermenter, Mixer, Refinery and Squeezer to Immersive Engineering
- Added option to ignore creosote output from Coke Oven
- Added `IngredientHandlers` for 'ItemStack', 'FluidStack', 'Item', 'Block', 'OreDict', 'IIngredient', 'BlockState' and 'SizedObject'.
- Added `Debug` option to PEIApi config

### Changed
- Moved from Travis to GitHub Actions
- Updated the gitignore
- Split the Mod into two projects: `ProjectE-Integration` and `Plugins`
- Mod logic and the API were moved into `ProjectE-Integration`
- Plugins were moved into their own category inside `Plugins`
- Moved IC2 Classic support from IC plugin into it's own plugin 
- Renamed IC plugin to IndustrialCraft
- Moved all API files into the api source set
- Moved lots of mod logic into the api
- Updated README
- Moved Plugin annotation handling to its own class
- Updated optional mods on cursegradle
- Refactored some classes
- Skip plugin load before try catch
- Updated Applied Energistics Plugin
    - Add in world fluid and seed crafting alongside condenser singularity and matter_ball
- Moved Util input handling code to IngredientHandler
- Config category uses getCategory function
- Config names now escape extra characters
- Change ArmorPlus Plugin to use addRecipe not AddConversion
- Change the order mappers are added in Artisan Worktables
- Change Chisel Group Name
- Split the tables into two for Extended Crafting
- Rename Mixer to FluidMixer in Embers plugin
- ImmersiveEngineeringPlugin: Rename Multiblock to MultiBlock, fixed Enginner type
- APEIPlugin modid is fetched from PEIPlugin annotation and now throws an exception if not available
- Moved CraftTweaker compatiblity into own folder and removed debug messages
- Renamed `DISABLE` to `DISABLED` in PEIntegration.java
- Renamed PEIApi `LOG` to `LOGGER`
- Completely changed IngredientHandler internals
- Cleaned up forEach loop in legacy NuclearCraft plugin
- Implemented `Callable` interface for APEIPlugin.

### Removed
- Harvestcraft jar, we fetch from jitpack now
- Utils.createOutputs: It was old and for something completely unknown now
- Arguments from APEIPlugin constructor
- Debug log from Modular Machinery plugin
- Misc Category from config, was never used.
- TTerrag second maven, was removed

### Fixed
- Fixed plugins
    - Modular Machinery, NuclearCraft
- API version was mc version not mod version
- Fixed Extended Crafting: Some inputs were a list and couldn't be converted
- Fixed Pam's HarvestCraft plugin: The output list should work properly now. Old code was bogging it down
- Fixed Immersive Engineering: MultiBlock recipe didn't have correct output amounts

## [2.5.0] - 2019-08-11
### Fixed
- NuclearCraft support was fixed and updated

## [2.4.0] - 2019-08-09
### Added
- Added support for Multi Output recipes

### Changed
- Made the mod only server side compatible
- Made some plugins use multi-output conversions
- Fix debug log in libVulpes

## [2.3.1] - 2019-07-21
### Changed
- Renamed ThermalExpansion to Thermal-Expansion in cursegradle
- Fix Extended Crafting support, was using the wrong function

## [2.3.0] - 2019-06-29
### Added
- PEIPlugin `addEMC` function for OreDictionary
- PEIMapper shortcut constructors
- Support for IC2 Classic
    - Compressor, Extractor, Macerator, Saw Mill, Furnace, Mass Fab Amplifier
- Support for libVulpes
    - All machines registered through their api

### Changed
- Stop printing stacktrace in PreInitialization
- Moved SizedObject and SizedIngredient
- Artisan Worktables now uses Fluid Input in conversion

## [2.2.13] - 2019-05-29
### Added
- Support for Modular Machinery
    - All dynamic machines

### Changed
- Print stacktrace on error
- Fixed NuclearCraft recipe fetching

## [2.2.12] - 2019-05-08
### Added
- SizedObjects and SizedIngredient to api
- Support for Artisan Worktables
    - All tiers of all worktables

### Fixed
- Mekanica versions was wrong
- Mekanism infuse `type` was changed to `getType`

## [2.2.11] - 2019-04-20
### Added
- Added `mekanica` to supported versions

## [2.2.10] - 2019-04-14
### Added
- Support for PSI
    - Tricks

## [2.2.9] - 2019-04-13
### Added
- Logo and .xcf file to repo

### Changed
- Update README
- Re-enabled required version for Mekanism
- Updated Mekanism

### Removed
- Logo from mod

## [2.2.8] - 2019-04-04
### Changed
- Exception to Throwable in PEIntegration

### Removed
- Unused import in Utils

## [2.2.7] - 2019-04-03
### Changed
- Disabled required versions for mods

## [2.2.6] - 2019-04-03
### Changed
- Error logging in serverAboutToStart is now more verbose

## [2.2.5] - 2019-04-03
### Fixed
- Mekanism Support

## [2.2.4] - 2019-04-02
### Added
- Support for CraftTweaker
    - You can now add your own conversions to ProjectE

## [2.2.3] - 2019-04-01
### Added
- `makeChangelog` task back
- Support for Mekanism
    - Chemical Crystallizer, Chemical Dissolution Chamber, Chemical Infuser, Chemical Injection Chamber, Chemical Oxiduzer,
    Chemical Washer, Combiner, Crusher, Electrolytic Separator, Energized Smelter, Enrichment Chamber, Metallurgic Infuser,
    Osmium Compressor, Precision Sawmill, Pressurized Reaction Chamber, Purification Chamber, Rotatry Condensentrator,
    Solar Neutron Activator, Thermal Evaporation

### Changed
- Version number when running in a CI is now '.*' instead of '+*'

### Removed
- A unused import from Charset plugin

## [2.2.2] - 2019-03-31
### Added
- Added `throws Exception` to plugin setup
- Added support for Woot
    - Anvil

### Changed
- Changed Charset file to gradle dependency

## [2.2.1] - 2019-03-29
### Added
- Support for Charset
    - Charset Crafting from Charset-Lib

## [2.2.0] - 2019-03-29
### Added
- Added EMC to EnderIO items
    - Infinity Dust (32), Brown Dye (1) amd Green Dye (1) 

### Changed
- Renamed CurseForge artifacts
- Moved from `Embers Rekindled` to `Embers`
- Used correct functions in Embers plugin

### Removed
- Support for Embers Rekindled
    - Dawnstone Anvil and Heating Coil

## [2.1.1] - 2019-03-26
### Changed
- Renamed Pam's mod jar
- Souls from EnderIO are actually mapped

## [2.1.0] - 2019-03-26
### Added
- PEIPlugin `addEMC` function for OreDictionaries
- `Utils.isSameItem` function to compare 2 ItemStacks
- Support for Pam's HarvestCraft
    - Market, Grinder, Presser and Water Filter

### Changed
- PEIPlugin `addEMC` functions now check for null EMC and objects
- PEIMapper copy ItemStacks to prevent modification

### Removed
- Unused import in `PluginSonarCore`

## [2.0.2] - 2019-03-26
### Changed
- PEIMapper `conversion_proxy.addConversion` is now surrounded by a try catch clause
- Renamed `PluginEmbers` to `PluginEmbersRekindled`

## [2.0.1] - 2019-03-26
### Changed
- Plugin and Mapper setup is not surrounded by try catch clause

## [2.0.0] - 2019-03-25
### Added
- Travis CI building and testing
- Gradle Task to store version to file
- Gradle apiJar and javadocJar
- Setup function to Plugins to add all EMC objects and mappers
- PEIApi for api functionality
- PEIMapper for ease of use and extra functionality
- PEIPlugin abstract class for plugins to extend
- Added some Utils for converting list of Objects into an IngredientMap
- Added support for 18 more mods
    - Astral Sorcery: Altar, Grindstone and Starlight Infusion
    - Blood Magic: Alchemy Array, Alchemy Table, Blood Altar and Tartaric Forge
    - Botania: Elven Trade, Petal Apothecary, Pure Daisy, Mana Infusion and Rune Altar
    - Calculator: Algorithm Separator, Atomic Calculator, Calculator, Conductor Mast, Extraction Chamber, Fabrication
    Chamber, Flawless Calculator, Precision Calculator, Processing Chamber, Reassembly Chamber, Restoration Chamber,
    Scientific Calculator and Stone Separator
    - Compact Machines: Miniaturization
    - Draconic Evolution: Fusion
    - Embers: Alchemy, Dawnstone Anvil, Heating Coil, Melting, Mixing and Stamper
    - EnderIO: Souls, Alloy Smelter, Sag Mill, Slice and Splice, Vat and Soul Binder
    - Extra Botany: Pedestal
    - Forestry: Carpenter, Fabricator, Fermenter, Moistener, Squeezer and Still
    - IndustrialCraft 2: Blast Furnace, Block Cutter, Centrifuge, Compressor, Extractor, Macerator, Metal Former (Cutting/
    Extruding/Rolling) and Ore Washing
    - Immersive Engineering: Blast Furnace, Coke Oven, Crusher, Engineers Workbench, Kiln and Metal Press
    - Mystical Agriculture: Seed Processor
    - RebornCore: All recipes registered through the RecipeHandler
    - SonarCore: Does nothing on it's own, is there for other plugins to extend
    - TConstruct: Alloying, Drying and Melting
    - TechReborn: Rolling Machine and everything else through RebornCore
    - Thaumcraft: Arcane, Crucible and Infusion
    - Thermal Expansion: Brewer, Centrifuge, Charger, Compactor, Crucible, Enchanter, Furnace, Insolator, Precipitator,
    Pulverizer, Refinery, Sawmill, Smelter and Transposer

### Changed
- Updated README
- Added build number to project version if available
- Move static strings to variables in gradle.properties
- Register EMC for objects at Post Initialization
- Moved some Reference data to PEIApi
- PEIPlugin annotation renamed to RegPEIPlugin
- ConfigHelper and Utils moved to API

### Removed
- `IPlugin.add(EMC|Conversions|Blacklist|Transmutation)`
- Config option to load EMC conversions on postInit
- IPlugin

Added support for 18 more mods

## [1.5.0] - 2019-02-26
### Added
- Config option to disable mod
- Config option to load EMC conversions during postInit instead of serverAboutToStart
- Clear caches once EMC conversions are loaded
- Support for Avaritia
    - Compressor and Extreme Crafting Table

### Changed
- Only load EMC conversions once instead of each server start/world load
- Added cache for Ingredients to save on calculation
- Config comment on each NuclearCraft machine

## [1.4.0] - 2019-02-26
### Added
- Logo
- Support for NuclearCraft
    - All machines

## [1.3.0] - 2019-02-24
### Added
- Initial README
- Support for Applied Energistics 2
    - Grinder and Inscriber
- Timings to each event to track performance

### Changed
- Renamed postInit to serverAboutToStart to reflect actual event

### Removed
- Changelog Gradle plugin
- makeChangelog gradle task

## [1.2.0] - 2019-02-21
### Added
- Support for ArmorPlus
    - Workbench (Normal, High-Tech, Ultimate and Champion)
- Added IBlacklistProxy and ITransmutationProxy to plugins

### Changed
- Removed MC version from CurseForge files

## [1.1.0] - 2019-02-13
### Added
- GitHub issue templates for bug reports and suggestions
- CurseGradle support for uploading to CurseForge
- Created deobf Jar and source jar on output
- Jar Signing for all jars
- makeChangelog task for gradle
- Debug config option
- Config for all plugins

### Changed
- Plugins are now loaded by Annotation by fetching ASMData

## [1.0.0] - 2019-02-06
### Added
- Added support for Actually Additions
    - Crusher, Empowerer and Reconstructor
- Added support for Extended Crafting
    - Combintation Core, Compressor, Ender Crafter and Tiered Tables

[6.0.5]: https://github.com/TagnumElite/ProjectE-Integration/compare/v6.0.5...HEAD
[6.0.4]: https://github.com/TagnumElite/ProjectE-Integration/compare/v6.0.4...v6.0.3
[6.0.3]: https://github.com/TagnumElite/ProjectE-Integration/compare/v6.0.1...v6.0.3
[6.0.1]: https://github.com/TagnumElite/ProjectE-Integration/compare/v5.3.2...v6.0.1
[5.3.2]: https://github.com/TagnumElite/ProjectE-Integration/compare/v5.3.1...v5.3.2
[5.3.1]: https://github.com/TagnumElite/ProjectE-Integration/compare/v5.3.0...v5.3.1
[5.3.0]: https://github.com/TagnumElite/ProjectE-Integration/compare/v5.2.4...v5.3.0
[5.2.4]: https://github.com/TagnumElite/ProjectE-Integration/compare/v5.2.3...v5.2.4
[5.2.3]: https://github.com/TagnumElite/ProjectE-Integration/compare/v5.2.2...v5.2.3
[5.2.2]: https://github.com/TagnumElite/ProjectE-Integration/compare/v5.2.0...v5.2.2
[5.2.0]: https://github.com/TagnumElite/ProjectE-Integration/compare/v5.1.0...v5.2.0
[5.1.0]: https://github.com/TagnumElite/ProjectE-Integration/compare/v5.0.3...v5.1.0
[5.0.3]: https://github.com/TagnumElite/ProjectE-Integration/compare/v5.0.1...v5.0.3
[5.0.1]: https://github.com/TagnumElite/ProjectE-Integration/compare/v5.0.0...v5.0.1
[5.0.0]: https://github.com/TagnumElite/ProjectE-Integration/compare/v4.4.4...v5.0.0
[4.4.4]: https://github.com/TagnumElite/ProjectE-Integration/compare/v4.4.3...v4.4.4
[4.4.3]: https://github.com/TagnumElite/ProjectE-Integration/compare/v4.4.2...v4.4.3
[4.4.2]: https://github.com/TagnumElite/ProjectE-Integration/compare/v4.4.1...v4.4.2
[4.4.1]: https://github.com/TagnumElite/ProjectE-Integration/compare/v4.4.0...v4.4.1
[4.4.0]: https://github.com/TagnumElite/ProjectE-Integration/compare/v4.3.0...v4.4.0
[4.3.0]: https://github.com/TagnumElite/ProjectE-Integration/compare/v4.2.0...v4.3.0
[4.2.0]: https://github.com/TagnumElite/ProjectE-Integration/compare/v4.1.7...v4.2.0
[4.1.7]: https://github.com/TagnumElite/ProjectE-Integration/compare/v4.1.6...v4.1.7
[4.1.6]: https://github.com/TagnumElite/ProjectE-Integration/compare/v4.1.5...v4.1.6
[4.1.5]: https://github.com/TagnumElite/ProjectE-Integration/compare/v4.1.4...v4.1.5
[4.1.4]: https://github.com/TagnumElite/ProjectE-Integration/compare/v4.1.3...v4.1.4
[4.1.3]: https://github.com/TagnumElite/ProjectE-Integration/compare/v4.1.2...v4.1.3
[4.1.2]: https://github.com/TagnumElite/ProjectE-Integration/compare/v4.1.1...v4.1.2
[4.1.1]: https://github.com/TagnumElite/ProjectE-Integration/compare/v4.1.0...v4.1.1
[4.1.0]: https://github.com/TagnumElite/ProjectE-Integration/compare/v4.0.9...v4.1.0
[4.0.9]: https://github.com/TagnumElite/ProjectE-Integration/compare/v4.0.8...v4.0.9
[4.0.8]: https://github.com/TagnumElite/ProjectE-Integration/compare/v4.0.7...v4.0.8
[4.0.7]: https://github.com/TagnumElite/ProjectE-Integration/compare/v4.0.6...v4.0.7
[4.0.6]: https://github.com/TagnumElite/ProjectE-Integration/compare/4.0.5...v4.0.6
[4.0.5]: https://github.com/TagnumElite/ProjectE-Integration/compare/4.0.4...4.0.5
[4.0.4]: https://github.com/TagnumElite/ProjectE-Integration/compare/4.0.3...4.0.4
[4.0.3]: https://github.com/TagnumElite/ProjectE-Integration/compare/4.0.2...4.0.3
[4.0.2]: https://github.com/TagnumElite/ProjectE-Integration/compare/4.0.1...4.0.2
[4.0.1]: https://github.com/TagnumElite/ProjectE-Integration/compare/4.0.0...4.0.1
[4.0.0]: https://github.com/TagnumElite/ProjectE-Integration/compare/3.0.0...4.0.0
[3.0.0]: https://github.com/TagnumElite/ProjectE-Integration/compare/2.5.0...3.0.0
[2.5.0]: https://github.com/TagnumElite/ProjectE-Integration/compare/2.4.0...2.5.0
[2.4.0]: https://github.com/TagnumElite/ProjectE-Integration/compare/2.3.1...2.4.0
[2.3.1]: https://github.com/TagnumElite/ProjectE-Integration/compare/2.3.0...2.3.1
[2.3.0]: https://github.com/TagnumElite/ProjectE-Integration/compare/2.2.13...2.3.0
[2.2.13]: https://github.com/TagnumElite/ProjectE-Integration/compare/2.2.12...2.2.13
[2.2.12]: https://github.com/TagnumElite/ProjectE-Integration/compare/2.2.11...2.2.12
[2.2.11]: https://github.com/TagnumElite/ProjectE-Integration/compare/2.2.10...2.2.11
[2.2.10]: https://github.com/TagnumElite/ProjectE-Integration/compare/2.2.9...2.2.10
[2.2.9]: https://github.com/TagnumElite/ProjectE-Integration/compare/2.2.8...2.2.9
[2.2.8]: https://github.com/TagnumElite/ProjectE-Integration/compare/2.2.7...2.2.8
[2.2.7]: https://github.com/TagnumElite/ProjectE-Integration/compare/2.2.6...2.2.7
[2.2.6]: https://github.com/TagnumElite/ProjectE-Integration/compare/2.2.5...2.2.6
[2.2.5]: https://github.com/TagnumElite/ProjectE-Integration/compare/2.2.4...2.2.5
[2.2.4]: https://github.com/TagnumElite/ProjectE-Integration/compare/2.2.3...2.2.4
[2.2.3]: https://github.com/TagnumElite/ProjectE-Integration/compare/2.2.2...2.2.3
[2.2.2]: https://github.com/TagnumElite/ProjectE-Integration/compare/2.2.1...2.2.2
[2.2.1]: https://github.com/TagnumElite/ProjectE-Integration/compare/2.2.0...2.2.1
[2.2.0]: https://github.com/TagnumElite/ProjectE-Integration/compare/2.1.1...2.2.0
[2.1.1]: https://github.com/TagnumElite/ProjectE-Integration/compare/2.1.0...2.1.1
[2.1.0]: https://github.com/TagnumElite/ProjectE-Integration/compare/2.0.2...2.1.0
[2.0.2]: https://github.com/TagnumElite/ProjectE-Integration/compare/2.0.1...2.0.2
[2.0.1]: https://github.com/TagnumElite/ProjectE-Integration/compare/2.0.0...2.0.1
[2.0.0]: https://github.com/TagnumElite/ProjectE-Integration/compare/1.5.0...2.0.0
[1.5.0]: https://github.com/TagnumElite/ProjectE-Integration/compare/1.4.0...1.5.0
[1.4.0]: https://github.com/TagnumElite/ProjectE-Integration/compare/1.3.0...1.4.0
[1.3.0]: https://github.com/TagnumElite/ProjectE-Integration/compare/1.2.0...1.3.0
[1.2.0]: https://github.com/TagnumElite/ProjectE-Integration/compare/1.1.0...1.2.0
[1.1.0]: https://github.com/TagnumElite/ProjectE-Integration/compare/1.0.0...1.1.0
[1.0.0]: https://github.com/TagnumElite/ProjectE-Integration/releases/tag/1.0.0

<!-- MODS -->
[Applied Energistics]: https://www.curseforge.com/minecraft/mc-mods/applied-energistics-2
[Ars Nouveau]: https://www.curseforge.com/minecraft/mc-mods/ars-nouveau
[Astral Sorcery]: https://www.curseforge.com/minecraft/mc-mods/astral-sorcery
[Blood Magic]: https://www.curseforge.com/minecraft/mc-mods/blood-magic
[Botania]: https://www.curseforge.com/minecraft/mc-mods/botania
[Chipped]: https://www.curseforge.com/minecraft/mc-mods/chipped
[Compact Crafting]: https://www.curseforge.com/minecraft/mc-mods/compact-crafting
[Create]: https://www.curseforge.com/minecraft/mc-mods/create
[Ex Nihilo Sequentia]: https://www.curseforge.com/minecraft/mc-mods/ex-nihilo-sequentia
[Extended Crafting]: https://www.curseforge.com/minecraft/mc-mods/extended-crafting
[Farmers Delight]: https://www.curseforge.com/minecraft/mc-mods/farmers-delight
[Immersive Engineering]: https://www.curseforge.com/minecraft/mc-mods/immersive-engineering
[Industrial Foregoing]: https://www.curseforge.com/minecraft/mc-mods/industrial-foregoing
[Mana And Artifice]: https://www.curseforge.com/minecraft/mc-mods/mana-and-artifice
[MrCrayfish's Vehicle Mod]: https://www.curseforge.com/minecraft/mc-mods/mrcrayfishs-vehicle-mod
[Mystical Agriculture]: https://www.curseforge.com/minecraft/mc-mods/mystical-agriculture
[PneumaticCraft: Repressurized]: https://www.curseforge.com/minecraft/mc-mods/pneumaticcraft-repressurized
[Psi]: https://www.curseforge.com/minecraft/mc-mods/psi
[Tinkers Construct]: https://www.curseforge.com/minecraft/mc-mods/tinkers-construct
[Touhou Little Maid]: https://www.curseforge.com/minecraft/mc-mods/touhou-little-maid
[Woot]: https://www.curseforge.com/minecraft/mc-mods/woot
