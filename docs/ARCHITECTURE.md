# Architecture

Tulip is one Fabric client, not two clients running side-by-side. Its direction is hybrid: retain the Adin GUI/client identity and original Adin-derived Triggerbot, while adapting selected Vengeance behavior to Tulip's module/config system and Minecraft 1.21.11 mappings.

```text
Fabric Loader (Minecraft 1.21.11 / Java 21)
└── TulipClient : ClientModInitializer
    ├── registers Module instances
    ├── loads ClientConfig
    ├── ClientTickEvents.END_CLIENT_TICK
    │   ├── module.onTick(client)
    │   └── AdinStyleScreen key
    ├── HudElementRegistry
    │   ├── WatermarkModule
    │   └── ArrayListModule
    ├── AdinStyleScreen
    │   ├── ModuleCategory sidebar
    │   ├── registered modules and enable toggles
    │   └── ModuleSetting controls
    ├── ClientConfig (config/tulip-client.json)
    └── Mixins + MinecraftClientAccessor
        ├── attack hooks
        ├── movement/combat/entity hooks
        └── item-use/packet hooks
```

## Important Classes

- `src/main/java/dev/tulip/client/TulipClient.java` — client entrypoint, module registry construction, config load, HUD registration, global tick dispatch, and GUI key.
- `module/Module.java` — shared name/category/enabled lifecycle, static registry, `onEnable`, `onDisable`, and tick callback contract.
- `module/ModuleCategory.java` — Combat, Movement, Player, Render, World, Misc, HUD and Utility categories. World is presently empty.
- `module/ModuleSetting.java` — toggle, normalized slider, choice and raw GLFW keybind settings; setting values are serialized by the config system.
- `config/ClientConfig.java` — module enable/settings and friend-list JSON persistence in Fabric's config directory.
- `gui/AdinStyleScreen.java` — preserved Adin-style screen; category/module/settings scrolling, scissor clipping, compact single-pane view, slider/toggle/choice/keybind interaction.
- `friends/FriendManager.java` — UUID-based friend roster and JSON representation.
- `module/TriggerbotModule.java` — protected Adin-derived Triggerbot implementation. Do not replace it with Vengeance Trigger Bot.

## Module Lifecycle and Registration

`TulipClient.onInitializeClient()` constructs and registers each concrete module before `ClientConfig.load()`. `Module.register` appends instances to the process-wide registry; category filters and the GUI enumerate that registry. Client end-tick calls every registered module's `onTick`; each module must check enabled state and required client/player/world/screen state. `setEnabled` calls `onEnable`/`onDisable` on transitions. Those methods must release any temporarily owned key, slot, rotation, gamma, or other state.

A Java class existing is not sufficient for a port. Check the full chain: source → instance → registration → category → GUI → settings/config → lifecycle → runtime behavior. Current registrations are listed in `TulipClient.java`; the audit is in `PORT_STATUS.md`.

## Settings and Configuration

Settings are returned by `Module.getSettings()`. Toggle state is Boolean, slider state is normalized to `[0,1]`, choice state is a string, and keybind state is an integer GLFW key or encoded mouse button. GUI interaction and `ClientConfig` persist settings by module name and setting label. Config data is stored at Fabric's config path as `tulip-client.json`; friend UUID/name records are stored alongside module data.

When adding a new setting type, update the setting model, GUI display/input, config serialization and config loading together. Test old/missing/invalid setting values. Keybinds poll GLFW from a client tick and require a live window; do not use them from a server thread.

## GUI Navigation and Rendering

The GUI remains Adin-style. The sidebar selects a `ModuleCategory`, the module pane selects/toggles registered modules, and the settings pane edits visible settings. Module, setting and category panes scroll independently with eased offsets and scissor clipping. On compact widths the GUI switches between modules and settings rather than rendering negative-width columns. Preserve this layout and visual language; only make the bounds/ergonomics changes necessary for the growing module list.

HUD rendering uses Fabric `HudElementRegistry` rather than importing Vengeance's renderer or GUI. The current element invokes Watermark and ArrayList modules. Render modules must guard player/world/screen state and be checked for overlap/performance.

## Tick, Attack, and Packet Hooks

- `ClientTickEvents.END_CLIENT_TICK` is the shared module tick surface.
- `MinecraftClientMixin` hooks `doAttack()` for Anti Miss and pre-attack module coordination (WTap, STap, Sword Swap, Totem Hit, Shield Breaker).
- `ClientPlayerInteractionManagerMixin` provides the attack interaction hook used by Criticals/Hit Cob.
- `PlayerEntityMixin` handles keep-sprint and local block-break speed.
- `ClientPlayNetworkHandlerMixin` observes local velocity packets for Velocity.
- `EntityMixin` handles scoped bounding-box expansion and glow filtering.
- `ProjectileUtilMixin` opens/closes the Hitboxes raycast guard.
- `LivingEntityMixin` adjusts swing duration for Swing Speed.
- `KeyBindingMixin` implements Snap Tap movement-key ordering.
- `MinecraftClientAccessor` exposes item-use cooldown plus `doItemUse` and `doAttack` invokers.

`build.gradle` enables Loom's Mixin AP and `tulip-client-refmap.json`. Compile-time mapping does not prove a runtime injection applies: every configured target must be checked during a client launch. Keep injections narrowly scoped and avoid duplicate event handling/recursive `doAttack` calls.

## Utility Boundaries

The active code is currently organized in `module/`, `config/`, `friends/`, `gui/`, and `mixin/`. There is no separate `setting/` or `util/` package because current shared setting behavior is owned by `ModuleSetting` and friend state by `FriendManager`. Add a utility abstraction only when multiple verified modules share the same behavior.
