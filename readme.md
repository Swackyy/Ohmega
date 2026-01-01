# Ohmega
A modernized and lightweight data-driven accessory API for Minecraft, versions 1.18+

This mod aims to be a performant and stable alternative to other accessory API mods, while maintaining a plentiful amount of useful features, both for users and developers

> [!NOTE]
> The Fabric version requires the 'Fabric API' and 'Forge Config API Port' mods to function

## Download Links
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/ohmega)
- [Modrinth](https://modrinth.com/mod/ohmega)

---

## For developers
**[View the Source Code here](https://github.com/Swackyy/Ohmega)**

**[Check out the Wiki](https://github.com/Swackyy/Ohmega/wiki)**

---

## Contributing
If you wish to contribute to the mod, you can do it in multiple ways! Either:
- Create an issue report, detailing that a bug be fixed, or a desire for a new feature, or to improve on an existing one
- Submit a pull request to the GitHub repository

### How to generate IDE runs
1. Clone the repository into a local directory, and open with your IDE.
2. Load the gradle project (depends on IDE), this will also generate both Fabric and NeoForge run configurations.
3. To generate Forge run configurations run `./gradlew genIntellijRuns --refresh-dependencies -p loader/forge`

### How to build into JAR
For your desired Mod Loader, run the command `./gradlew build -p loader/<loaderName>`

---
## Other links
[Report issues here](https://github.com/Swackyy/Ohmega/issues)

[Or join the Discord!](https://discord.gg/B9669WDmZk)