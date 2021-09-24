# ProjectE-Integration (PEI)
This mod will be the bridge between [ProjectE] and all ~~my~~ your favourite mods!

[![Build](https://github.com/TagnumElite/ProjectE-Integration/actions/workflows/gradle_build.yml/badge.svg?branch=1.16.x)](https://github.com/TagnumElite/ProjectE-Integration/actions/workflows/gradle_build.yml)
[![Downloads](http://cf.way2muchnoise.eu/projecte-integration.svg)](https://www.curseforge.com/minecraft/mc-mods/projecte-integration)

## NOTE:
> README IS OUTDATED. Everything below this line will be changed probably in the future.
    
PEI will be adding [ProjectE] conversion mapping support to these mods crafting mechanics.    
This means you will not have to manually set EMC for these mods recipes, instead, this mod will tell [ProjectE] about
these recipes and what they mean. This means you can use [ProjectE] built-in commands to setEMC and reloadEMC on the
fly without having to worry about having to manually set the EMC for all items affected.    
    
PEI will later on also be adding EMC to the mods Items that don't have emc assigned to it.    
    
## Lore:
This mod was inspired by [Expanded Equivalence] and was started because I tried to work on [Expanded Equivalence] by
decided it would be better to make a separate mod that does everything without Core Modding.    

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

## Permissions:    

You may include this in a private or public modpack as long as you give credit.
You don't need to ask for permission.

[wiki]: https://github.com/TagnumElite/ProjectE-Integration/wiki
[ProjectE]: https://www.curseforge.com/minecraft/mc-mods/projecte
[Expanded Equivalence]: https://www.curseforge.com/minecraft/mc-mods/expanded-equivalence
