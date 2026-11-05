# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this repo is

This is the public API source for Thaumcraft (a Minecraft Forge mod), not a runnable mod. The Java files are meant to be copied into an addon's source tree under `thaumcraft/api/` (root `.java` files are in package `thaumcraft.api`, and each subfolder is its matching subpackage). Thaumcraft itself provides the implementation at runtime.

## Build

- `./gradlew compileJava` compiles the API against Forge `26.2-65.1.6` (ForgeGradle 7, Gradle 9.5.0 wrapper, Java 25 toolchain). The first run downloads and sets up Minecraft, which takes several minutes.
- `build.gradle` uses the repo root as the Java source directory, because the sources are not under `src/main/java`.
- There are no tests or lint config.
- The sources were written for Minecraft 1.12 / old Forge and do not yet compile against 26.2. Until they are ported, the compile fails with hundreds of errors. Use compile output to check porting progress.

## Architecture

**Static facade and runtime handler.** `ThaumcraftApi.java` is the main entry point: static registration methods for recipes, aspects, research locations, seeds, loot bags, warp, and so on. Methods that need live game state are delegated through `ThaumcraftApi.internalMethods`, an `IInternalMethodHandler` that starts as `DummyInternalMethodHandler` (no-op/null returns) and is replaced by Thaumcraft during its init. Calls to these methods are only meaningful after Thaumcraft passes `FMLPreInitializationEvent`. The thin wrappers `golems/GolemHelper.java` and `ThaumcraftInvHelper.java` / `ThaumcraftApiHelper.java` route through the same handler or provide utilities.

**Shared mutable state lives in `internal/CommonInternals`.** Static maps for recipe catalogs, object tags, entity tags, smelting bonuses, warp map, seeds, and JSON research locations. The public API writes into these; Thaumcraft reads them. `CommonInternals` is documented as not for addon use directly, so prefer the `ThaumcraftApi` methods.

**Aspects are registered through an event.** `aspects/AspectRegistryEvent` is a Forge event whose `register` field is an `AspectEventProxy` set by Thaumcraft. Addons subscribe to it to attach aspects to items (`registerObjectTag`, `registerComplexObjectTag`) and entities. Registering before Thaumcraft's own tags can produce odd results. `AspectList`, `Aspect`, and `AspectHelper` are the data types and generation logic behind this.

**Recipes.** `crafting/` holds the recipe types (arcane shaped/shapeless, infusion, crucible, misc). They are added via `ThaumcraftApi.add*Recipe(ResourceLocation, ...)`, which also places them in a catalog keyed by `ResourceLocation`. Research pages reference these catalog keys by name. Fake recipes (`addFakeCraftingRecipe`) are catalog entries with no crafting behavior, used only for display.

**Research is data-driven.** Research is defined in JSON under the addon's assets and registered with `ThaumcraftApi.registerResearchLocation` before postInit. `research/_example.json.txt` documents the schema (entries, stages, addenda, parents/siblings, requirements). `research/ScanningManager` and the `IScanThing` implementations (`ScanItem`, `ScanBlock`, `ScanEntity`, etc.) connect world scanning to research unlocks. `research/theorycraft/` defines the theorycraft card system, with `TheorycraftManager` as the registry and `ITheorycraftAid` as the extension point.

**Casters and foci.** `casters/` is a node-graph system. `FocusEngine` holds a static registry of focus elements (`IFocusElement`), each registered with an icon and color. `FocusNode`, `FocusPackage`, `FocusMod`, `FocusMedium`, and `FocusEffect` make up a focus tree that a caster (`ICaster`) executes. `CasterTriggerRegistry` maps triggers to effects.

**Golems and seals.** `golems/` lets addons register seals (`ISeal`, registered in preInit with `GolemHelper.registerSeal`), add golem tasks, and define golem traits (`EnumGolemTrait`). Seal entities and tasks are passed through `ISealEntity` and `golems/tasks/Task`.

**Capabilities.** `capabilities/` defines player-attached data (`IPlayerKnowledge`, `IPlayerWarp`) and `ThaumcraftCapabilities`, which holds the Forge capability handles.

**Items and blocks.** `items/` has interfaces that gear implements to interact with Thaumcraft mechanics (`IGoggles`, `IVisDiscountGear`, `IWarpingGear`, `IRechargable`, `IArchitect`, and so on). `ItemsTC` and `BlocksTC` are references to Thaumcraft's own items and blocks, not definitions.

**Vis, flux, and aura.** `aura/AuraHelper` is the chunk-level aura API. Vis and flux drain or add operations are routed through `internalMethods` from `IInternalMethodHandler`.

**Cross-cutting.** `aspects/IAspectContainer`, `IAspectSource`, `IEssentiaTransport`, and `IEssentiaContainerItem` let other mods' blocks and items participate in the essentia network. `entities/` has marker interfaces for Thaumcraft mobs. `damagesource/` has Thaumcraft's damage types. `potions/` contains the two custom potions.

## Conventions

- Tabs for indentation, Javadoc on public API methods, `@author Azanor` on the original files. Match the surrounding file's style; the code is not uniformly formatted.
- Public API changes affect every addon that compiles against it. Keep existing signatures, or add overloads, rather than changing or removing them.
- Version metadata is in `package-info.java` (`@API(... apiVersion = "6.0.2")`). Update it only when the API version itself changes.
- Commits in history are short, release-oriented messages (`update for b26`, `changes for b25`). The repo's most recent changes track Thaumcraft beta releases.
