# Relic-Fuse 26.1 migration notes

This mod (`hbs_relicfuse`) was migrated from Minecraft 1.21.1 to 26.1.2 (Java Fabric + NeoForge multiloader) following the Foundation `updatePack-26.1` patterns. Because the mod's own content is empty, the placeholder example scaffolding shipped with the template was **stripped** rather than ported, leaving a minimal skeleton that compiles and loads on 26.1. See `VANILLA_API_PATCHES_26.1.md` and `UPDATE_GUIDE_26.1.md` for the underlying API details.

## Build / version bumps

`gradle.properties` was bumped to match Foundation: `minecraft_version=26.1.2`, `java_version=25`, `neoforge_version=26.1.2.21-beta`, `neo_form_version=26.1.2-1`, `parchment_minecraft=1.21.10` / `parchment_version=2025.10.12`, `fabric_version=0.146.1+26.1.2`, `fabric_loader_version=0.19.2`, `loom_version=1.14-SNAPSHOT`, `balm_version=26.1.2.5`, `balm_version_range=[26.1.2.5,)`, `forge_version=64.0.4`, and the version ranges (`minecraft_version_range=[26.1-,26.2)`, `neoforge_version_range=[26.1,)`, `forge_*_range=[64,)`). A new `fabric_minecraft_version_range=~26.1-` was added. `foundation_version` stays `1.8.10-SNAPSHOT` (the migrated Foundation artifact coordinate is `hbs_foundation-<module>-26.1.2:1.8.10-SNAPSHOT`, resolved from `mavenLocal`).

Root `build.gradle` moddev plugin `2.0.49-beta` → `2.0.141`; Gradle wrapper `8.12` → `9.3.0`; `common/build.gradle` mixin `0.8.5` → `0.8.7`; `buildSrc/.../multiloader-common.gradle` `expandProps` gained `fabric_minecraft_version_range`.

## Loader plugins — the "Failed to find official mojang mappings for 26.1.2" fix

26.1 is the first **unobfuscated** Minecraft, so nothing is remapped. The Fabric module was still on the remapping Loom plugin with a `mappings` block, which is exactly what throws `Failed to find official mojang mappings for <version>`. `fabric/build.gradle` was rewritten to mirror Foundation:

- plugin `id 'fabric-loom'` → `id 'net.fabricmc.fabric-loom'` (the non-remap variant). The version stays pinned in the root `build.gradle` via `id 'fabric-loom' version "${loom_version}" apply(false)`.
- the entire `mappings loom.layered { officialMojangMappings(); parchment(...) }` block was **deleted** — there is no remap step, so there are no mappings to apply.
- `modImplementation` → `implementation` for `fabric-loader`, `fabric-api`, `balm-fabric`, and the `hbs_foundation-fabric` dependency (the `mod`-prefixed configs only feed the now-absent remap step).
- the `loom { accessWidenerPath ... }` block was removed (an `.accesswidener` makes Loom fail with "Expected official namespace" in this setup; the NeoForge access transformer covers the one protected member).

`neoforge/build.gradle` (ModDevGradle was already the right plugin) got the two 26.1 edits: the `parchment { minecraftVersion/mappingsVersion }` block → `parchment.parchmentArtifact = "org.parchmentmc.data:parchment-${parchment_minecraft}:${parchment_version}@zip"`, and the datagen run type `data()` → `clientData()` (the `data` run type was split into `clientData`/`serverData`). `settings.gradle` foojay-resolver `0.8.0` → `1.0.0` for JDK 25 toolchain resolution.

## Capability conflict consuming hbs_foundation

After the Loom fix, `:fabric` failed configuration with:

```
Cannot select module with conflict on capability 'com.holybuckets.foundation:hbs_foundation:unspecified'
  also provided by [...hbs_foundation-common-26.1.2 (runtimeElements)]
```

`hbs_foundation`'s `multiloader-common.gradle` declares a **shared capability** `com.holybuckets.foundation:hbs_foundation` on every published variant (common/fabric/neoforge). The loader module carried two of them at once: `foundation-fabric` (its own dependency) plus `foundation-common` leaking in through `:common`'s `implementation` dependency. It only surfaced now because pre-migration the loader used `modImplementation` (a separate Loom remap configuration), whereas non-remap Loom uses plain `implementation`, putting both variants in one configuration. Balm does not hit this — it declares no shared cross-variant capability.

Fix (`common/build.gradle`): the `hbs_foundation-common` dependency was changed from `implementation` to **`compileOnly`**. The loader jars already merge the common classes in (the `commonJava` source-sharing compiles common sources into `foundation-fabric` / `foundation-neoforge`), so the loaders get foundation's common classes from their loader-specific artifact; `:common` only needs `foundation-common` at compile time. `balm-common` stays `implementation` — the loader genuinely needs it on the classpath and it does not conflict.

(The `Fabric Loom: The mixin annotation is no longer enabled by default…` line is a non-fatal warning, identical to Foundation's build; left as-is.)

## JDK 25 IDE wiring

`.idea/gradle.xml` was updated to run the project on JDK 25 (per the Foundation `26.1-java25-migration-notes.md` `.idea/gradle.xml` section): `gradleJvm` `#JAVA_HOME` → `25`, `distributionType DEFAULT_WRAPPED` added, the `buildSrc` composite build registered, and the module set corrected (`forge` → `neoforge`, `buildSrc` added). The `gradleJvm` value must match the registered JDK 25 SDK name in IntelliJ's Project Structure.

## Resources

`pack.mcmeta` pack_format `34` → `64`. `fabric.mod.json` dependencies rewritten to the templated/mirrored shape: `fabricloader >=${fabric_loader_version}`, `fabric-api "*"`, `minecraft ${fabric_minecraft_version_range}`, `java >=${java_version}`, `balm >=${balm_version}` (was `balm-fabric`), `hbs_foundation >=${foundation_version_min}`. `neoforge.mods.toml` already templated on the gradle properties, so it picked the new values up automatically. Dead, unreferenced `hbs_template.mixins.json` / `hbs_template.fabric.mixins.json` were removed.

## Balm 26.1 API — entrypoints rewired

Balm 26.1 replaced the old `Balm.initialize(id, ctx, Runnable)` + static holder registration (`Balm.getBlocks()`, `BalmBlocks`, `BalmItems`, `BalmMenus`, `Balm.getBlockEntities()`, `BalmClient.getRenderers()`, …) with `Balm.initializeMod(id, LoadContext, Consumer<BalmRegistrars>)` and typed registrars. The four entrypoints and both common-init classes were updated accordingly:

- `RelicFuseMainForge` / `RelicFuseMainForgeClient`: constructor now `(ModContainer, IEventBus)`; `new NeoForgeLoadContext(modContainer, modEventBus)` (package `balm.neoforge.platform.runtime`); `Balm.initializeMod(...)` / `BalmClient.initializeMod(...)`.
- `RelicFuseMainFabric` / `RelicFuseMainFabricClient`: `FabricLoadContext.INSTANCE` (package `balm.fabric.platform.runtime`) replaces `EmptyLoadContext`; `initializeMod(...)`.
- `CommonClass.init(BalmRegistrars registrars)` and `CommonClassClient.initClient(BalmClientRegistrars registrars)` — signatures changed from no-arg; the example block/item/blockEntity/menu/renderer/screen registration calls were removed.
- `TemplateConfig`: config annotations moved `balm.api.config.reflection.*` → `balm.platform.config.reflection.*`.

The dependent-mod double-registration of Foundation's Balm event bridges (`BalmEventRegister.registerEvents()` / `ClientBalmEventRegister.registerEvents()`) was dropped — Foundation registers those itself, and Relic-Fuse's `RelicFuseMain` still hooks server-start through Foundation's `EventRegistrar`.

## Stripped example scaffolding (deleted)

`block/ModBlocks`, `block/EmptyBlock`, `block/be/ModBlockEntities`, `block/be/TemplateBlockEntity`, `item/ModItems`, `item/TemplateBlockItem`, `menu/ModMenus`, `menu/TemplateChestEntityMenu`, `client/ModRenderers`, `client/IBewlrRenderer`, `client/render/SimpleBlockEntityRenderer`, `client/screen/CountingChestScreen`, `client/screen/ModScreens`, and `networking/{BlockStateUpdatesMessage, BlockStateUpdatesMessageHandler, Codecs, Handlers}`. When real content is added, re-introduce registration through the registrar API (`registrars.blocks(...)`, `registrars.items(...)`, `registrars.blockEntityTypes(...)`, `registrars.menus(...)`, client `registrars.blockEntityRenderers(...)` / `registrars.screens(...)`) as done in Foundation's `ModBlocks` / `ModItems` / `ModBlockEntities`.

## Kept infrastructure

`Constants`, `LoggerProject`, `CommonClass`, `CommonClassClient`, `RelicFuseMain`, `TemplateConfig`, `command/CommandList` (Foundation `CommandRegistry`-based, unchanged), `platform/*` (proxy/service-loader), and the mixins. The unused `getVersionType()` example log line was removed from the common `MixinMinecraft` (that class is still compiled even though it is not listed in any `*.mixins.json`).

## Not verified

No Gradle build was run in this environment (no JDK 25 / blocked Maven mirrors). Changes are static-review only and assume the migrated Foundation 26.1 artifact is available in `mavenLocal`.
