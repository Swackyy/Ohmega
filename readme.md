# Ohmega
A modernized and lightweight Accessory API for Minecraft, versions 1.18+

This mod is a performant, maintained and stable alternative to other Accessory API mods.

## Download Links
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/ohmega)
- [Modrinth](https://modrinth.com/mod/ohmega)

Sources Jars also provided there

---

## For modders
**[View the Source Code here](https://github.com/Swackyy/Ohmega)**

**[Check out the Wiki](https://github.com/Swackyy/Ohmega/wiki)**

### Cloning Repo Instructions

#### How to build in IDE

1. Clone the repository into a local directory, and open with your IDE.
2. (Optional) Open the `gradle.properties` file and modify the number in `org.gradle.jvmargs` to specify the amount of RAM to run the game with
3. Load the gradle project (depends on IDE), this will also generate both NeoForge and Fabric run configurations.
4. To generate Forge run configurations run `./gradlew genIntellijRuns --refresh-dependencies -p loader/forge`.

#### How to build into Jar

For your desired Mod Loader, run the corresponding command below:
> **Forge**: `./gradlew build -p loader/forge`

> **NeoForge**: `./gradlew build -p loader/neoforge`

> **Fabric**: `./gradlew build -p loader/fabric`

---
## Other links
[Report issues here](https://github.com/Swackyy/Ohmega/issues)

[Join the Discord!](https://discord.gg/B9669WDmZk)