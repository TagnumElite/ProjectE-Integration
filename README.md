# ProjectE-Integration    
    
This mod will be the bridge between ProjectE and all ~~your~~ my favourite mods!    
    
PEI will be adding ProjectE conversion mapping support to these mods crafting mechanics.    
This means you will not have to manually set EMC for these mods recipes, instead, this mod will tell ProjectE about these recipes and what they mean. This means you can use ProjectE built-in commands to setEMC and reloadEMC on the fly without having to worry about having to manually set the EMC for all items affected.    
    
PEI will later on also be adding EMC to the mods Items that don't have emc assigned to it.    
    
## Mod Support:  
  
Mod Support is split into seperate sub mods:  
  
### Crafting:  
  
- [ArmorPlus](https://www.curseforge.com/minecraft/mc-mods/armorplus): Workbench (Normal, High-Tech, Ultimate and Champion)  
- [Artisan Worktables](https://www.curseforge.com/minecraft/mc-mods/artisan-worktables)  
- [Artisan Integrations]()  
- [Extended Crafting](https://www.curseforge.com/minecraft/mc-mods/extended-crafting): Combination, Compressor, Ender Crafting Table and Tiered Crafting Table
  
### Library:  
  
- [Charset Lib](https://www.curseforge.com/minecraft/mc-mods/charset-lib)  
- [LibVulpes](https://www.curseforge.com/minecraft/mc-mods/libvulpes)  
- [Reborn Core](https://www.curseforge.com/minecraft/mc-mods/reborncore)  
  
### Magic:  
  
- [Astral Sorcery](https://www.curseforge.com/minecraft/mc-mods/astral-sorcery): Altar, Grindstone, Starlight Infusion  
- [Blood Magic](https://www.curseforge.com/minecraft/mc-mods/blood-magic): Alchemy Array, Alchemy Table, Blood Altar and Tartaric Forge  
- [Botania](https://www.curseforge.com/minecraft/mc-mods/botania): Elven Trade, Petal Apothacary, Pure Daisy, Mana Infusion and Rune Altar  
- [Embers](https://www.curseforge.com/minecraft/mc-mods/embers): Alchemy, Melting, Mixing and Stamper
- [Embers Rekindled](https://www.curseforge.com/minecraft/mc-mods/embers-rekindled)  
- [ExtraBotany](https://www.curseforge.com/minecraft/mc-mods/extrabotany): Pedestal  
- [Mystical Agriculture](https://www.curseforge.com/minecraft/mc-mods/mystical-agriculture): Seed Reprocessor
- [Psi](https://www.curseforge.com/minecraft/mc-mods/psi)  
- [Thaumcraft](https://www.curseforge.com/minecraft/mc-mods/thaumcraft): Arcane, Crucible and Infusion
  
### Misc:  
  
- [Avaritia](https://www.curseforge.com/minecraft/mc-mods/avaritia-1-10): Exteme Crafting Table and Compressor  
- [Pam's HarvestCraft](https://www.curseforge.com/minecraft/mc-mods/pams-harvestcraft): Grinder, Presser, Water Filter and Market
- [Tinkers Construct](https://www.curseforge.com/minecraft/mc-mods/tinkers-construct): Alloying, Drying and Melting
- [Woot](https://www.curseforge.com/minecraft/mc-mods/woot)  
  
### Tech:  
  
- [Actually Additions](https://www.curseforge.com/minecraft/mc-mods/actually-additions): Crusher, Atomic Reconstructor, Empowerer  
- [Draconic Evolution](https://www.curseforge.com/minecraft/mc-mods/draconic-evolution): Fusion
- [EnderIO](https://www.curseforge.com/minecraft/mc-mods/ender-io): Alloy Smelter, Sag Mill, Slice and Splice, Vat and Soul Binder
- [Forestry](https://www.curseforge.com/minecraft/mc-mods/forestry): Carpenter, Fabricator, Fermenter, Moistener, Squezzer and Still
- [Industrial Craft](https://www.curseforge.com/minecraft/mc-mods/industrial-craft): Blast Furnace, Block Cutter, Centrifuge, Compressor, Extractor, Macerator, Metal Former and Ore Washer
- IC2 Classic    
- [Compact Machines](https://www.curseforge.com/minecraft/mc-mods/compact-machines): Miniaturization
- [NuclearCraft](https://www.curseforge.com/minecraft/mc-mods/nuclearcraft-mod)  
- [NuclearCraft: Overhauled](https://www.curseforge.com/minecraft/mc-mods/nuclearcraft-overhauled)  
- [Applied Energistics](https://www.curseforge.com/minecraft/mc-mods/applied-energistics-2): Grindstone and Inscriber  
- [Calculator](https://www.curseforge.com/minecraft/mc-mods/calculator)  
- [Mekanism](https://www.curseforge.com/minecraft/mc-mods/mekanism)
- [Immersive Engineering](https://www.curseforge.com/minecraft/mc-mods/immersive-engineering): Blast Furnace, Coke Oven, Crusher, Enginner Workbench, Kiln and Metal Press
- [Thermal Expansion](https://www.curseforge.com/minecraft/mc-mods/thermal-expansion): All Machines
- [Modular Machinery](https://www.curseforge.com/minecraft/mc-mods/modular-machinery)  
- [Tech Reborn](https://www.curseforge.com/minecraft/mc-mods/techreborn): Rolling Machine
    
### Not Supported

These mods won't be supported or not currently.

- Galaticraft (It would help if it was buildable on JitPack)


## Planned Features:    
 - CraftTweaker: This will allow modpack devs to add their own custom conversion to the ProjectE.    
    
## Lore:    
 This mod was inspired by Expanded Equivalence and was started because I tried to work on Expanded Equivalence by decided it would be better to make a separate mod that does everything without Core Modding.    
    
## Issues/Suggestions:    
 - Issues and Suggestions must be made on the GitHub Repo Issue tracker    
- Any comments with issues and/or suggestions will be ignored. (In fact. Comments have been disabled for this reason)    
    
#### Known (Currently) Unfixable Bugs:
 - Some items with subtypes don't seem to be working properly


## Development

I do not have a proper maven setup yet (just don't think I will need one).
I do support JitPack though.

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    provided 'com.tagnumelite:ProjectE-Integration:2.5.0.73'
}
```

## Info:

- 1.13.X: Support isn't planned
- 1.14.X: I may release a version at some point
- 1.15.X: I will start working on this version after ProjectE finalises their API and releases a 'release' build

## Permissions:    
 I don't really care unless used for malicious purposes then I will request it to be removed.    
~~Redistribution is forbidden unless used for modpacks by Curseforge/ATLauncher/Technic.~~    
Redistribute however you want, as long as you don't ask for payment to access the mod.
