# Vengeance Source Inventory and Port Status

Source inspected: `/workspaces/vengeance-reference/Vengeance--1.0.0`.

The manager is `src/main/java/com/vengeance/vengeanceclient/r/zc.java`. It constructs 77 entries: Combat (27), Movement (5), Player (15), Render (16), Misc (9), and Client/utility (5). Three more module subclasses exist but are not instantiated by that manager: Stun Cob, Dynamic Island, and Watermark. HUD below is split from Vengeance's Render category by behavior. Vengeance defines no World-category module.

Current integration count: 28 of the 76 non-Trigger-Bot manager entries have corresponding Tulip integrations, plus Watermark from the unregistered source classes. The Vengeance Trigger Bot remains excluded by design. Forty-eight manager entries and two other source-only classes remain unported.

## Registered Source Inventory

Names are from module constructors. Source paths are relative to `src/main/java/com/vengeance/vengeanceclient/r/q/`.

### Combat

Source package `q/`: Aim Assist (`za`), Anti Miss (`zb`), Auto Cart (`zc`), Auto Crystal (`zd`), Auto Lunge (`zu`), Auto Mace (`ze`), Auto Pot (`zf`), Breach Swap (`zg`), Criticals (`zh`), Crystal Optimizer (`zi`), Elytra HotSwap (`zj`), Hit Cob (`zk`), Hitboxes (`zm`), Key Anchor (`zn`), Key Crystal (`zo`), Key Lava (`zp`), STap (`zq`), Shield Breaker (`zs`), SpearKill (`zt`), Sword Hotswap (`zw`), Sword Swap (`zx`), Throw Pot (`zy`), Totem Hit (`zz`), Trigger Bot (`zaa`), Velocity (`zab`), WTap (`zac`), Xbow cart (`zad`).

### Movement

Source package `s/`: Auto Firework (`za`), Auto Head Hitter (`zb`), Keep Sprint (`zc`), Snap Tap (`zd`), Sprint (`ze`).

### Player

Source package `t/`: Auto Crafter (`za`), Auto Double Hand (`zb`), Auto Drain (`zc`), Auto Extinguish (`zd`), Auto MLG (`ze`), Auto Refill (`zf`), Auto Tool (`zg`), Auto Web (`zh`), Cover Up (`zi`), Fast Exp (`zj`), Fast Mine (`zk`), Fast Place (`zl`), Ping Spoof (`zm`), ReBuff Notifier (`zn`), Trap Save (`zo`).

### Render

Source package `u/`: 2D ESP (`zh`), 3D ESP (`zi`), Aspect Ratio (`zc`), BlurTest (`zd`), Container Slots (`ze`), Custom Outline (`zf`), Full Bright (`zj`), Outline ESP (`zm`), Trajectories (`zq`), Target ESP (`zo`).

### HUD

Source package `u/`: ArrayList (`za`), Arrow ESP (`zb`), Nametags (`zk`), Notifications (`zl`), Swing Speed (`zn`), Target HUD (`zp`).

### Misc

Source package `r/`: Cart Key (`za`), Fake Player (`zb`), Friends (`zc`), Hover Totem (`zd`), Middle Click Friend (`ze`), Pearl Catch (`zf`), Pearl Key (`zg`), Teams (`zh`), Wind Charge Key (`zi`).

### Utility / Client

Source package `p/`: Client (`za`), ClientSettings (`zb`), Debugger (`zc`), RichModernGUI (`zd`), Secrett ily (`ze`). These are Vengeance's client/config/debug entries, not gameplay features.

### Source Classes Not Registered by Vengeance

- Stun Cob: `q/q/zv.java`; target prediction and web placement behavior exists in source but the manager does not instantiate it.
- Dynamic Island: `q/u/zg.java`; render module source exists but the manager does not instantiate it.
- Watermark: `q/u/zr.java`; render module source exists but the manager does not instantiate it.

## Tulip Integration

Tulip instantiates each item below in `TulipClient.onInitializeClient`; each uses the existing module setting/config/GUI path.

| Tulip module | Vengeance source | Integration status |
| --- | --- | --- |
| Sprint | `q/s/ze.java` | Retained Tulip implementation; same always-sprint behavior. |
| Keep Sprint | `q/s/zc.java`, `mixin/PlayerEntityMixin.java` | Adapted to the source's post-attack PlayerEntity hook. |
| Auto Head Hitter | `q/s/zb.java` | Ported tick, jump-delay, and holding-space settings. |
| Auto Firework | `q/s/za.java` | Ported elytra, flying, respect-item, hotbar selection, and switch-back behavior/settings. |
| Auto Double Hand | `q/t/zb.java` | Ported health/inventory switching, slot, and threshold settings. |
| Auto Drain | `q/t/zc.java` | Ported still-water detection, empty-bucket use, cobweb guard, cooldown and restore timing. |
| Auto MLG | `q/t/ze.java` | Ported fall-distance water placement/pickup state machine and restore behavior. |
| Auto Extinguish | `q/t/zd.java` | Ported randomized delay, water bucket placement/pickup, pitch and slot restore settings. |
| Fast Exp | `q/t/zj.java` | Ported configurable chance-triggered XP bottle use through the source accessor. |
| ReBuff Notifier | `q/t/zn.java` | Ported Speed/Strength expiry detection, sound, volume and action-bar notification. |
| Auto Tool | `q/t/zg.java` | Existing implementation expanded with Vengeance delay, restore, sneak, durability, and threshold settings. |
| Friends | `q/r/zc.java`, Adin `friends/FriendList.java` | Adapted into a persistent UUID roster exposed as removable settings in the Adin-style module pane. |
| Middle Click Friend | `q/r/ze.java` | Ported crosshair-player middle-click add/remove with persisted UUIDs and chat feedback. |
| Fast Place | `q/t/zl.java`, `mixin/MinecraftClientAccessor.java` | Adapted to the actual `itemUseCooldown` accessor, blocks-only and delay settings. |
| Fast Mine | `q/t/zk.java`, `mixin/PlayerEntityMixin.java` | Ported configurable local-player block-breaking multiplier. |
| Full Bright | `q/u/zj.java` | Existing implementation expanded to Vengeance gamma and repeat night-vision effect behavior. |
| Criticals | `q/q/zh.java`, `mixin/ClientPlayerInteractionManagerMixin.java` | Adapted to the source's pre-attack hook and all three source modes/settings. |
| ArrayList | `q/u/za.java` | Ported enabled-module HUD with source settings for position, font scale, background alpha, rounded corners, accent width, and shadow. |
| Watermark | `q/u/zr.java` | Source-only Vengeance feature adapted to Adin identity, position, transparency and title/user/FPS/time/coords/ping toggles; custom color and free-text editing are not implemented. |
| Swing Speed | `q/u/zn.java`, `mixin/LivingEntityMixin.java` | Ported configurable local-player swing duration through the source-verified LivingEntity hook. |
| Hitboxes | `q/q/zm.java`, `mixin/EntityMixin.java`, `mixin/ProjectileUtilMixin.java` | Ported configurable player-box expansion scoped to entity raycasts, matching the source's ThreadLocal guard. |
| Outline ESP | `q/u/zm.java`, `mixin/EntityMixin.java` | Ported source filtering/settings through vanilla glow outlines; custom per-category scoreboard colors are intentionally not used. |
| Hit Cob | `q/q/zk.java`, `mixin/ClientPlayerInteractionManagerMixin.java` | Ported attack-triggered target prediction and cobweb placement with yaw/pitch/slot restoration. |
| Velocity | `q/q/zab.java`, `mixin/ClientPlayNetworkHandlerMixin.java` | Ported configurable jump-reset response to local velocity packets. |
| Anti Miss | `q/q/zb.java`, `mixin/MinecraftClientMixin.java` | Ported miss-click cancellation at the source's `doAttack` entry point. |
| WTap | `q/q/zac.java`, `mixin/MinecraftClientMixin.java` | Ported source chance/ground settings and timed forward-key release with physical-key restoration. |
| Sword Swap | `q/q/zx.java`, `mixin/MinecraftClientMixin.java` | Ported pre-attack sword selection, source exclusions and delayed slot restore. |
| Totem Hit | `q/q/zz.java`, `mixin/MinecraftClientMixin.java` | Ported pre-attack sword selection when holding a totem and delayed restore. |
| Shield Breaker | `q/q/zs.java`, `mixin/MinecraftClientMixin.java` | Ported shield/facing/friend checks, axe selection, timing, repeat hit and slot-restore settings. |

The Vengeance Trigger Bot is intentionally not ported. The existing Adin-derived Triggerbot source remains registered and unchanged by this integration. The Adin-style Tulip screen remains the main GUI; it now clips independently scrolling categories/modules/settings and uses a single-pane modules/settings flow on compact viewports.

## Overlap Check

The original Adin reference under `downloads/adin-0.1` has AimAssist, AutoAnchor, AutoCart, AutoCrystal, ShieldBreaker, Triggerbot, HUD ModuleList/Notifications/Watermark, Friends, Sprint, AutoTotem, KeyPearl, Refill, and Nametags. Tulip retains its existing Triggerbot and Sprint, maps Auto Double Hand to Adin AutoTotem, integrates Vengeance Shield Breaker rather than duplicating Adin's class, and adapts Adin's persistent Friends list into the module settings pane. The ArrayList and Watermark implementations above are adapted from Vengeance, not copies of Adin's animated ModuleList or logo watermark. No second Triggerbot was registered. Other Adin modules remain pending port/selection; AutoAnchor and Key Anchor are distinct source implementations and have not been duplicated in Tulip.

## Not Yet Ported

These are registered Vengeance modules, not completed integrations. Reasons below identify the missing behavior surface; source files must be adapted and tested before registration.

- Combat target/aim/input behavior: Aim Assist (`q/za`, mouse/rotation/input handling) and STap (`q/zq`, attack plus movement-key sequencing).
- Combat item/attack automation: Auto Cart (`q/zc`) and Xbow cart (`q/zad`, multi-stage rail/cart/bow sequencing); Auto Crystal (`q/zd`), Key Crystal (`q/zo`), Key Anchor (`q/zn`), and Crystal Optimizer (`q/zi`, placement/explosion/raycast/packet coordination); Auto Mace (`q/ze`), Breach Swap (`q/zg`), Sword Hotswap (`q/zw`), Auto Lunge (`q/zu`), SpearKill (`q/zt`), Elytra HotSwap (`q/zj`), Auto Pot (`q/zf`), Throw Pot (`q/zy`), and Key Lava (`q/zp`, attack-event or multistage inventory/rotation logic).
- Combat modules not instantiated by the Vengeance manager: Stun Cob (`q/zv`, predicted target movement and placement).
- Movement input: Snap Tap (`s/zd`) depends on ordered key-down/key-up interception and stateful last-key arbitration through Vengeance's Keyboard/Keybinding mixins; no equivalent event timeline exists in Tulip yet.
- Player inventory/world automation: Auto Crafter (`t/za`, recipe planner and crafting-screen slot transactions); Auto Refill (`t/zf`, hotkey, potion-component classification, screen state, and quick-move transactions); Auto Web (`t/zh`) and Cover Up (`t/zi`, placement and rotation); Ping Spoof (`t/zm`, connection-level packet timing); Trap Save (`t/zo`, entity/armor-stand scanning and prediction).
- Misc input/social helpers: Cart Key (`r/za`), Pearl Key (`r/zg`), Wind Charge Key (`r/zi`), and Pearl Catch (`r/zf`) require configurable keybinds and/or multi-stage item sequencing; Teams (`r/zh`) needs target-filter integration beyond the protected Triggerbot; Hover Totem (`r/zd`) needs inventory hover tracking; Fake Player (`r/zb`) is single-player entity construction.
- Render and HUD: 2D ESP (`u/zh`), 3D ESP (`u/zi`), Arrow ESP (`u/zb`), Target ESP (`u/zo`), and Trajectories (`u/zq`) require world-render/projection paths; Aspect Ratio (`u/zc`) requires a GameRenderer projection mixin; Blur Test (`u/zd`) and Custom Outline (`u/zf`) require shader/framebuffer plumbing; Container Slots (`u/ze`) requires handled-screen hooks; Nametags (`u/zk`) requires world-space text/item rendering; Target HUD (`u/zp`) needs a target selection/render contract; Notifications (`u/zl`) needs the notification/event infrastructure.
- Vengeance source-only, unregistered render module: Dynamic Island (`u/zg`) is not active in Vengeance's manager and remains unported. Watermark (`u/zr`) is also source-only in Vengeance but is now integrated as a Tulip HUD module.
- Utility/client entries: Client (`p/za`), ClientSettings (`p/zb`), Debugger (`p/zc`), RichModernGUI (`p/zd`), and Secrett ily (`p/ze`) belong to Vengeance's own settings/GUI/debug infrastructure and are excluded to preserve Adin identity and GUI.

## Mixin Audit

Vengeance declares 21 mixins in `vengeance-client.mixins.json`; 22 Java mixin/accessor classes exist because `WindowMixin.java` is not declared. Tulip does not copy Vengeance's whole mixin set. Active, AP-verified hooks are interaction-manager attack, PlayerEntity keep-sprint/block speed, LivingEntity swing duration, Entity bounding-box/glow, ProjectileUtil raycast scope, MinecraftClient attack/item-use accessors, and ClientPlayNetworkHandler velocity packet handling. The mixin AP emits `tulip-client-refmap.json`; the unrelated Vengeance mixin targets remain pending with their owning modules. The original Adin reference has separate mixin sources; the active Tulip foundation had no mixin config before this integration.