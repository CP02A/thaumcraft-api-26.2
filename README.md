# Thaumcraft API (26.2)

Developer API for [Thaumcraft](https://github.com/Azanor/Thaumcraft), a Minecraft Forge mod. This repository contains the Java source for `thaumcraft.api`, which addon mods compile against to add aspects, recipes, research, casters and foci, golems and seals, and other Thaumcraft content.

This is not a standalone mod. The classes here are interfaces, data types, and static registration methods; the implementation lives in Thaumcraft itself and is wired in at runtime.

## Using the API

Add these sources to your addon's build as the `thaumcraft.api` package. The root-level `.java` files belong to `thaumcraft.api`, and each subfolder maps to its matching subpackage, so the directory layout must be preserved.

Thaumcraft must be a dependency of your mod so that its classes are on the compile classpath and it loads before your mod.

### Entry points

- **`ThaumcraftApi`**: the main static facade. Recipes, aspect tags, research locations, seeds, loot bags, warp, and seal registration are all reached from here.
- **`AspectRegistryEvent`**: a Forge event you subscribe to in order to assign aspects to items and entities. Use its `register` proxy, not the proxy's methods on a fresh instance.
- **`GolemHelper`**, **`FocusEngine`**, **`ScanningManager`**, **`TheorycraftManager`**: registries for golems, caster foci, world scanning, and theorycraft cards.

### Timing

- Register research JSON locations with `ThaumcraftApi.registerResearchLocation` before the postInit phase.
- Register golem seals during preInit, before Thaumcraft loads.
- Methods that touch live game state go through `ThaumcraftApi.internalMethods`. That handler is a no-op until Thaumcraft replaces it, so these calls only work once Thaumcraft is past `FMLPreInitializationEvent`.
- Assign aspects to your items after Thaumcraft has assigned its own, or the results can be unexpected.

### Research

Research is defined in JSON in your addon's assets and registered with `ThaumcraftApi.registerResearchLocation`. See [`research/_example.json.txt`](research/_example.json.txt) for the full schema, including stages, addenda, parents and siblings, and requirements.

## Building

```sh
./gradlew compileJava
```

This compiles the API against Forge `26.2-65.1.6` using ForgeGradle 7 and a Java 25 toolchain. The first run downloads and sets up Minecraft, which takes several minutes.

The sources were written for Minecraft 1.12 and have not yet been ported to 26.2, so the compile currently fails.

## Layout

| Path | Contents |
|---|---|
| `ThaumcraftApi.java` | Main API facade |
| `ThaumcraftApiHelper.java`, `ThaumcraftInvHelper.java`, `ThaumcraftMaterials.java`, `OreDictionaryEntries.java` | Helpers and constants |
| `aspects/` | Aspect types, aspect lists, registration event, and essentia container/transport interfaces |
| `crafting/` | Arcane, infusion, crucible, and misc recipe types |
| `research/` | Research data types, scanning, and theorycraft cards |
| `casters/` | Caster interface and focus node/package/element system |
| `golems/` | Golem helpers, seals, tasks, and traits |
| `capabilities/` | Player knowledge and warp capabilities |
| `items/` | Interfaces for gear and item behavior (goggles, warping gear, rechargeable items, and so on) |
| `aura/` | Aura helpers |
| `internal/` | Internal method handler and shared state. Used by Thaumcraft; addons should prefer the public API |
| `entities/`, `damagesource/`, `potions/` | Marker interfaces, damage sources, and custom potions |

## Compatibility

The API version is declared in [`package-info.java`](package-info.java) (currently `apiVersion = "6.0.2"`). Public signatures are kept stable across releases so existing addons keep compiling; new functionality is added as new methods or overloads.

## License

No license file is included in this repository. Check with the repository owner before reusing the code.
