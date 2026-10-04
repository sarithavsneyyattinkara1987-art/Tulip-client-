# Vengeance Port Status

This is the definitive source-to-Tulip inventory. Vengeance source root: `/workspaces/vengeance-reference/Vengeance--1.0.0`; Java sources are under `src/main/java/com/vengeance/vengeanceclient`. Tulip target: `/workspaces/tulip-new-client`.

**Status rule:** no port is marked COMPLETE because this audit has no in-game runtime verification. PARTIAL means a corresponding Tulip class is registered and compiled, but behavior/settings/lifecycle/mixin application remain unverified. `Tulip registered` refers to the current `TulipClient.onInitializeClient()` registry, not Vengeance's manager. Source paths below are relative to `.../vengeanceclient/`.

Counts: 44 Tulip registrations (43 Vengeance counterparts/adaptations plus the protected Adin Triggerbot); 42 of the 77 Vengeance-manager entries have Tulip counterparts, plus Watermark from an unregistered Vengeance class. Excluding the protected Vengeance Trigger Bot and five Vengeance client/config/debug entries, the audited feature denominator is 73 (71 manager entries + Stun Cob and Dynamic Island); 43/73 = 59% have code counterparts. This measures code coverage only, not verified behavior or release readiness.

## Combat

| Module | Category | Status | Source | Tulip registered | Settings | Notes |
| --- | --- | --- | --- | --- | --- | --- |
| Aim Assist | Combat | PARTIAL | `r/q/q/za.java` | Yes | Source options exposed | Target selection/eased rotation; compare with Adin AimAssist and runtime-test. |
| Anti Miss | Combat | PARTIAL | `r/q/q/zb.java` | Yes | None in source | `doAttack` hook cancels miss; runtime hook unverified. |
| Auto Cart | Combat | PARTIAL | `r/q/q/zc.java` | Yes | Auto Switch | Bow release and rail/cart sequence; state transitions unverified. |
| Auto Crystal | Combat | TODO | `r/q/q/zd.java` | No | N/A | Crystal placement/explosion automation not ported. |
| Auto Mace | Combat | TODO | `r/q/q/ze.java` | No | N/A | Falling arc, enchantment choice, optional stun-slam and recursive attacks not ported. |
| Auto Pot | Combat | PARTIAL | `r/q/q/zf.java` | Yes | Health/cooldown/rotation/distance/ground | Potion and pitch/swap state machine compiles; runtime behavior unverified. |
| Breach Swap | Combat | TODO | `r/q/q/zg.java` | No | N/A | Attack-event recursive `doAttack` workflow not ported. |
| Criticals | Combat | PARTIAL | `r/q/q/zh.java` | Yes | Vanilla/Watchdog Old/Mospixel | Interaction-manager hook and source modes compile; packet behavior unverified. |
| Crystal Optimizer | Combat | TODO | `r/q/q/zi.java` | No | N/A | Crystal optimization/render/packet behavior not ported. |
| Elytra HotSwap | Combat | TODO | `r/q/q/zj.java` | No | N/A | Chest equipment and multistage inventory swap not ported. |
| Hit Cob | Combat | PARTIAL | `r/q/q/zk.java` | Yes | Source settings exposed | Attack prediction/web placement compiled; in-world behavior unverified. |
| Hitboxes | Combat | PARTIAL | `r/q/q/zm.java` | Yes | Expansion | Entity-box/raycast-scope hooks compile; runtime guard behavior unverified. |
| Key Anchor | Combat | TODO | `r/q/q/zn.java` | No | N/A | Anchor place/charge/explode sequence not ported. |
| Key Crystal | Combat | TODO | `r/q/q/zo.java` | No | N/A | Obsidian/crystal place/explode state machine not ported. |
| Key Lava | Combat | TODO | `r/q/q/zp.java` | No | N/A | Lava bucket cycle not ported. |
| STap | Combat | PARTIAL | `r/q/q/zq.java` | Yes | Delay/chance/ground | Timed backward-key pulse is wired to attack hook; runtime/input conflicts unverified. |
| Shield Breaker | Combat | PARTIAL | `r/q/q/zs.java` | Yes | Source options exposed | Axe/facing/retry state machine and attack hook compile; combined-module behavior unverified. |
| SpearKill | Combat | PARTIAL | `r/q/q/zt.java` | Yes | Target/lunge/charge settings | Target lock/rotation/velocity launch compile; source equivalence unverified. |
| Auto Lunge | Combat | PARTIAL | `r/q/q/zu.java` | Yes | Switch-back delay | Enchanted spear hotbar selection and restore compile; runtime lookup unverified. |
| Sword Hotswap | Combat | PARTIAL | `r/q/q/zw.java` | Yes | Key/delay/switch-back | Sword-to-shield key swap compiles; release/restore flow needs test. |
| Sword Swap | Combat | PARTIAL | `r/q/q/zx.java` | Yes | Switch delay | Pre-attack sword selection wired; priority with other attack modules unverified. |
| Throw Pot | Combat | PARTIAL | `r/q/q/zy.java` | Yes | Key/delay/health/multi/look/switch | Potion key/multithrow workflow compiles; timing and item-use semantics unverified. |
| Totem Hit | Combat | PARTIAL | `r/q/q/zz.java` | Yes | Source settings exposed | Pre-attack swap compiled; combined slot ownership unverified. |
| Trigger Bot | Combat | INTENTIONALLY SKIPPED | `r/q/q/zaa.java` | No | N/A | Protected requirement: do not port/register; Adin Triggerbot remains. |
| Velocity | Combat | PARTIAL | `r/q/q/zab.java` | Yes | Chance/key/fire toggles | Local velocity packet hook and jump reset compile; runtime packet semantics unverified. |
| WTap | Combat | PARTIAL | `r/q/q/zac.java` | Yes | Chance/delay/ground | Attack-hook forward release compiles; physical key handling unverified. |
| Xbow Cart | Combat | PARTIAL | `r/q/q/zad.java` | Yes | Key/delay | Rail/cart/flint/crossbow stages compile; interruption/inventory behavior unverified. |
| Stun Cob | Combat | TODO (source-only) | `r/q/q/zv.java` | No | N/A | Exists in source but Vengeance manager does not instantiate it; assess separately. |

## Movement

| Module | Category | Status | Source | Tulip registered | Settings | Notes |
| --- | --- | --- | --- | --- | --- | --- |
| Auto Firework | Movement | PARTIAL | `r/q/s/za.java` | Yes | Flying/gapple/armor/switch-back/delay | Elytra use behavior compiles; runtime cooldown and restore unverified. |
| Auto Head Hitter | Movement | PARTIAL | `r/q/s/zb.java` | Yes | Jump delay/holding space | Tick logic compiles; collision/ground behavior unverified. |
| Keep Sprint | Movement | PARTIAL | `r/q/s/zc.java` | Yes | None in source | PlayerEntity post-attack hook compiles; runtime behavior unverified. |
| Snap Tap | Movement | PARTIAL | `r/q/s/zd.java` | Yes | None in source | KeyBinding timestamp arbitration mixin compiles; opposing key semantics need runtime test. |
| Sprint | Movement | RETAINED FROM ADIN | `r/q/s/ze.java` | Yes | Existing Tulip toggle | Existing sprint module retained; compare full conditions against V source in-game. |

## Player

| Module | Category | Status | Source | Tulip registered | Settings | Notes |
| --- | --- | --- | --- | --- | --- | --- |
| Auto Crafter | Player | TODO | `r/q/t/za.java` | No | N/A | Recipe planning and crafting-screen slot transactions not ported. |
| Auto Double Hand | Player | PARTIAL | `r/q/t/zb.java` | Yes | Inventory/slot/health | Hotbar Totem switching compiles; inventory-screen behavior unverified. |
| Auto Drain | Player | PARTIAL | `r/q/t/zc.java` | Yes | Cooldown/switch delay | Still-water bucket cycle compiles; flow/hit/restore behavior unverified. |
| Auto Extinguish | Player | PARTIAL | `r/q/t/zd.java` | Yes | Delay/pickup/rotate/slot | Fire response state machine compiles; runtime restore unverified. |
| Auto MLG | Player | PARTIAL | `r/q/t/ze.java` | Yes | Fall distance/pickup | Falling-water state machine compiles; landing timing unverified. |
| Auto Refill | Player | TODO | `r/q/t/zf.java` | No | N/A | Vengeance inventory/hotkey/hover modes not ported; Adin Refill is more elaborate and must be reconciled. |
| Auto Tool | Player | PARTIAL | `r/q/t/zg.java` | Yes | Delay/restore/sneak/durability | Best-tool implementation and source settings compile; item scoring/restore need test. |
| Auto Web | Player | PARTIAL | `r/q/t/zh.java` | Yes | Fall distance/restore | Simplified web placement adaptation; compare full source behavior and test. |
| Cover Up | Player | TODO | `r/q/t/zi.java` | No | N/A | Rotation and multi-web placement not ported. |
| Fast Exp | Player | PARTIAL | `r/q/t/zj.java` | Yes | Chance | XP bottle use compiles; spam/cooldown behavior unverified. |
| Fast Mine | Player | PARTIAL | `r/q/t/zk.java` | Yes | Speed | Local breaking-speed hook compiles; runtime effect unverified. |
| Fast Place | Player | PARTIAL | `r/q/t/zl.java` | Yes | Blocks-only/delay | Cooldown accessor compiles; actual placement timing unverified. |
| Ping Spoof | Player | TODO | `r/q/t/zm.java` | No | N/A | Connection packet behavior not ported. |
| ReBuff Notifier | Player | PARTIAL | `r/q/t/zn.java` | Yes | Sound/notification/volume | Status-effect transition notifications compile; in-game sound/message unverified. |
| Trap Save | Player | TODO | `r/q/t/zo.java` | No | N/A | Trap/entity/armor-stand scan not ported. |

## Render / HUD

| Module | Category | Status | Source | Tulip registered | Settings | Notes |
| --- | --- | --- | --- | --- | --- | --- |
| 2D ESP | Render | TODO | `r/q/u/zh.java` | No | N/A | Entity projection/box rendering not ported. |
| 3D ESP | Render | TODO | `r/q/u/zi.java` | No | N/A | World-space rendering not ported. |
| ArrayList | HUD | PARTIAL | `r/q/u/za.java` | Yes | Position/font/alpha/rounded/bar/shadow | Enabled-module HUD compiles; appearance/overlap unverified. |
| Arrow ESP | HUD | TODO | `r/q/u/zb.java` | No | N/A | Player direction indicators not ported. |
| Aspect Ratio | Render | TODO | `r/q/u/zc.java` | No | N/A | Projection matrix hook not ported. |
| Blur Test | Render | TODO | `r/q/u/zd.java` | No | N/A | Shader test not ported. |
| Container Slots | Render | TODO | `r/q/u/ze.java` | No | N/A | Handled-screen slot overlay not ported. |
| Custom Outline | Render | TODO | `r/q/u/zf.java` | No | N/A | Shader/framebuffer outline not ported. |
| Dynamic Island | HUD | TODO (source-only) | `r/q/u/zg.java` | No | N/A | Source class exists but Vengeance manager does not instantiate it. |
| Full Bright | Render | PARTIAL | `r/q/u/zj.java` | Yes | Night Vision | Gamma/night vision behavior compiles; gamma/effect restore unverified. |
| Nametags | HUD | TODO | `r/q/u/zk.java` | No | N/A | Reconcile with Adin Nametags; world-space labels/items not ported. |
| Notifications | HUD | TODO | `r/q/u/zl.java` | No | N/A | Notification manager/rendering not ported. |
| Outline ESP | Render | PARTIAL | `r/q/u/zm.java` | Yes | Source filters | Vanilla glow/filter behavior compiles; custom scoreboard coloring intentionally omitted. |
| Swing Speed | HUD/Render | PARTIAL | `r/q/u/zn.java` | Yes | Duration | LivingEntity return hook compiles; runtime target/range unverified. |
| Target ESP | Render | TODO | `r/q/u/zo.java` | No | N/A | Target rendering not ported. |
| Target HUD | HUD | TODO | `r/q/u/zp.java` | No | N/A | Needs common target provider and HUD layout; not ported. |
| Trajectories | Render | TODO | `r/q/u/zq.java` | No | N/A | Projectile path simulation/rendering not ported. |
| Watermark | HUD | PARTIAL (source-only) | `r/q/u/zr.java` | Yes | Position/alpha/title/user/FPS/time/coords/ping | Tulip identity retained; Vengeance did not register its own Watermark class. |

## Misc

| Module | Category | Status | Source | Tulip registered | Settings | Notes |
| --- | --- | --- | --- | --- | --- | --- |
| Cart Key | Misc | PARTIAL | `r/q/r/za.java` | Yes | Key/delays/silent/switch/bow | Multi-stage item behavior compiles; abort and placement conditions unverified. |
| Fake Player | Misc | TODO | `r/q/r/zb.java` | No | N/A | Single-player-only entity helper not ported. |
| Friends | Misc | RETAINED FROM ADIN | `r/q/r/zc.java` | Yes | Dynamic UUID roster | Adin-derived persistence retained; Vengeance's GUI is intentionally not imported. |
| Hover Totem | Misc | TODO | `r/q/r/zd.java` | No | N/A | Needs focused inventory-slot hook; not ported. |
| Middle Click Friend | Misc | PARTIAL | `r/q/r/ze.java` | Yes | None in source | GLFW crosshair add/remove compiles; runtime click/filter behavior unverified. |
| Pearl Catch | Misc | TODO | `r/q/r/zf.java` | No | N/A | Pearl/wind charge timing sequence not ported. |
| Pearl Key | Misc | PARTIAL | `r/q/r/zg.java` | Yes | Key/throw delay/switch delay | Configurable key/use/restore compiles; runtime input/cooldown unverified. |
| Teams | Misc | TODO | `r/q/r/zh.java` | No | N/A | Teammate filtering exists only in select modules; no shared Teams module. |
| Wind Charge Key | Misc | PARTIAL | `r/q/r/zi.java` | Yes | Key/throw delay/switch delay | Configurable use/restore compiles; runtime input/cooldown unverified. |

## Vengeance Client / Utility Entries

| Module | Category | Status | Source | Tulip registered | Settings | Reason |
| --- | --- | --- | --- | --- | --- | --- |
| Client | Client | INTENTIONALLY SKIPPED | `r/q/p/za.java` | No | N/A | Vengeance-specific client identity/settings; conflicts with Adin identity. |
| ClientSettings | Client | INTENTIONALLY SKIPPED | `r/q/p/zb.java` | No | N/A | Vengeance GUI theme settings are not needed by the preserved Adin GUI. |
| Debugger | Client | INTENTIONALLY SKIPPED | `r/q/p/zc.java` | No | N/A | Developer packet debugger is not user-facing client functionality. |
| RichModernGUI | Client | INTENTIONALLY SKIPPED | `r/q/p/zd.java` | No | N/A | Replacing the required Adin GUI is prohibited. |
| Secrett ily | Client | INTENTIONALLY SKIPPED | `r/q/p/ze.java` | No | N/A | Vengeance-specific joke/secret entry is not part of the client direction. |

## Overlap / Protected Behavior

- **Triggerbot:** `TriggerbotModule` in Tulip is the retained Adin-derived implementation. Vengeance `Trigger Bot` is intentionally not registered or merged.
- **Sprint:** Tulip's existing sprint behavior is retained; compare source details before declaring equivalence.
- **Friends:** Adin-derived UUID persistence is retained and exposed through Tulip settings; Vengeance's separate GUI is excluded.
- **Aim Assist, Auto Cart, Shield Breaker, Watermark, Nametags, Auto Crystal and other overlaps:** use a single Tulip registration; inspect corresponding Adin source before claiming the selected implementation is more complete. The inactive Adin reference remains under `/workspaces/Tulip-client-/downloads/adin-0.1`.
- **Watermark:** source exists but is not created by Vengeance's own manager; Tulip adapts data/settings to Adin identity.

## Mixins

Tulip config declares 9 mixin/accessor classes in `src/main/resources/tulip-client.mixins.json`: `MinecraftClientAccessor`, `KeyBindingMixin`, `ClientPlayerInteractionManagerMixin`, `PlayerEntityMixin`, `LivingEntityMixin`, `EntityMixin`, `ProjectileUtilMixin`, `MinecraftClientMixin`, and `ClientPlayNetworkHandlerMixin`. Loom's Mixin AP/refmap resolves their current Yarn targets at compile time. None have been confirmed in a running client. See [ARCHITECTURE.md](ARCHITECTURE.md) for ownership and [TODO.md](TODO.md) for runtime checks.
