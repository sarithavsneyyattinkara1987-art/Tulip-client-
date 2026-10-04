# Tulip Client TODO

This is the primary continuation checklist. Module status is cross-referenced with [PORT_STATUS.md](PORT_STATUS.md). A registered module is not release-complete until its behavior, settings, cleanup, configuration, and mixin hooks are verified in-game.

Status markers: `[x]` verified complete; `[ ]` remaining; `[~]` partial or awaiting runtime verification; `[!]` blocked.

## P0 — Critical / Must Finish

- [~] **Runtime smoke test** — Source compiles on Java 21, but Minecraft has not been launched in this environment. Check client launch, Mixin application, screen opening, module toggles, and log/crash output. Blocker: this Codespace may lack a graphical display. Next: run `./gradlew runClient --no-daemon` in a display-enabled environment.
- [~] **Mixin startup verification** — Mixin AP refmap is generated and compile-time targets resolve. Actual injections/accessors have not been confirmed during a game launch. Next: inspect startup logs for all 9 configured mixins and exercise each hook.
- [~] **Config round-trip** — Module/settings and friend persistence code exists; automated coverage is limited to friend list and Criticals choice setting. Next: test all setting kinds, enabled states, keybinds, malformed config recovery, and module disable cleanup.
- [~] **Adin identity and protected module** — Adin title/GUI and Adin-derived Triggerbot remain registered. Verify visible identity and Triggerbot behavior after the full client starts. Vengeance Trigger Bot remains intentionally excluded.
- [ ] **Feature-complete gate** — 29 registered Vengeance gameplay modules plus two source-only modules remain TODO; see category checklists below. Current estimated source-module integration coverage is 59%, not behavioral completion.
- [ ] **Repository release gate** — No release/versioning process or end-user install test is documented yet. Add this only after runtime validation and upstream-license review.

## P1 — Combat

| Module | State | Source | Remaining / dependencies | Next action |
| --- | --- | --- | --- | --- |
| Aim Assist | [~] PARTIAL | Vengeance `r/q/q/za.java`; Tulip `AimAssistModule.java` | Target and rotation behavior is implemented; compare against Adin AimAssist and validate visibility/FOV/settings in-game. | Exercise player/mob, friend, wall, FOV and hitbox cases. |
| Anti Miss | [~] PARTIAL | `r/q/q/zb.java`; `MinecraftClientMixin.java` | Hook is registered; verify it cancels only misses and does not interfere with other attack hooks. | Test block/entity/miss clicks at runtime. |
| Auto Cart | [~] PARTIAL | `r/q/q/zc.java`; `AutoCartModule.java` | Bow/release, rail/cart use, restoration and valid placement assumptions need in-game confirmation. | Compare target and stage transitions with source. |
| Auto Crystal | [ ] TODO | `r/q/q/zd.java` | No Tulip class; needs key/placement/explosion logic and interaction/packet sequencing. | Read complete source and dependencies; design a cancellable state machine. |
| Auto Lunge | [~] PARTIAL | `r/q/q/zu.java`; `AutoLungeModule.java` | Spear/enchantment identification and delayed slot restoration are not runtime-tested. | Test enchantment lookup and release/disable/world-change cleanup. |
| Auto Mace | [ ] TODO | `r/q/q/ze.java` | No Tulip class; source tracks falling arc, Density/Breach mace selection, attack delay, optional stun-slam and nested attacks. | Port only after defining recursion-safe attack orchestration. |
| Auto Pot | [~] PARTIAL | `r/q/q/zf.java`; `AutoPotModule.java` | Potion detection and pitch/switch rotation are implemented; match source interpolation/timing and verify health/distance guards. | Test potion variants, speed endpoints, interruption and restore. |
| Breach Swap | [ ] TODO | `r/q/q/zg.java` | No Tulip class; recursive `doAttack` guard, attack-event target and silent/non-silent switch-back. | Reuse a shared attack coordinator; test no recursive/double attack. |
| Criticals | [~] PARTIAL | `r/q/q/zh.java`; interaction-manager mixin | Three source modes and mode setting exist; behavior is exploit/anti-cheat sensitive and not runtime-verified. | Confirm packet sequence and mode cleanup in a controlled world. |
| Crystal Optimizer | [ ] TODO | `r/q/q/zi.java` | No Tulip class; needs crystal removal/placement render or packet behavior and likely more hooks. | Inspect full dependencies, then isolate client-only changes. |
| Elytra HotSwap | [ ] TODO | `r/q/q/zj.java` | No Tulip class; multistage armor equip/inventory transaction and silent swap behavior. | Port with restore guarantees and verify inventory sync. |
| Hit Cob | [~] PARTIAL | `r/q/q/zk.java`; interaction-manager mixin | Prediction, web placement and orientation/slot restore are implemented but untested in-game. | Test predicted target positions and all early exits. |
| Hitboxes | [~] PARTIAL | `r/q/q/zm.java`; Entity and ProjectileUtil mixins | Scoped box expansion is implemented; verify raycast guard is always cleared on exceptions. | Test entity hit selection and exception paths. |
| Key Anchor | [ ] TODO | `r/q/q/zn.java` | No Tulip class; repeated anchor place/charge/explode and key state. | Port full state machine and restore slot/settings. |
| Key Crystal | [ ] TODO | `r/q/q/zo.java` | No Tulip class; multi-stage crystal/obsidian placement and explosion. | Port with range/validity checks and cancellation handling. |
| Key Lava | [ ] TODO | `r/q/q/zp.java` | No Tulip class; bucket placement/pickup cycle. | Port timing and world-state transitions. |
| STap | [~] PARTIAL | `r/q/q/zq.java`; MinecraftClient attack mixin | Timed backward-key pulse implemented; physical-key ownership and repeated attack timing need runtime verification. | Test key held/released and module disable during pulse. |
| Shield Breaker | [~] PARTIAL | `r/q/q/zs.java`; MinecraftClient attack mixin | State machine/settings exist; recursive hook and target/facing conditions need runtime tests. | Verify attack cancellation, retry pacing and slot restore. |
| SpearKill | [~] PARTIAL | `r/q/q/zt.java`; `SpearKillModule.java` | Target lock, friend/team filter, aim and launch are implemented; target selection/rotation restore differ potentially from source. | Compare source behavior in-game and test release/target death. |
| Sword Hotswap | [~] PARTIAL | `r/q/q/zw.java`; `SwordHotswapModule.java` | Key-triggered shield swap and restore are implemented; held-key/release state needs manual validation. | Verify switch-back when key is released before/after delay. |
| Sword Swap | [~] PARTIAL | `r/q/q/zx.java`; attack mixin | Pre-attack weapon selection and delayed restore are implemented; verify ordering with Shield Breaker and Totem Hit. | Test combined modules and ensure only one slot owner at a time. |
| Throw Pot | [~] PARTIAL | `r/q/q/zy.java`; `ThrowPotModule.java` | Keybind, potion detection, multi-throw and restore are implemented; test keyboard/mouse binding and potion use timing. | Exercise one/multiple potions and disabled-mid-cycle cleanup. |
| Totem Hit | [~] PARTIAL | `r/q/q/zz.java`; attack mixin | Source-backed swap behavior is implemented; verify its interaction with Sword Swap/Shield Breaker. | Test concurrent module priority and slot restoration. |
| Trigger Bot | [!] INTENTIONALLY SKIPPED | `r/q/q/zaa.java` | Explicitly excluded by project requirements. | Do not port or register. Keep Adin Triggerbot. |
| Velocity | [~] PARTIAL | `r/q/q/zab.java`; ClientPlayNetworkHandler mixin | Packet-triggered jump reset and settings exist; packet hook/conditions need runtime verification. | Test local versus remote packets, ground/fire/key filters. |
| WTap | [~] PARTIAL | `r/q/q/zac.java`; attack mixin | Forward-key release and restore exist; validate player key ownership and combined attack hooks. | Test held physical W and disable during release. |
| Xbow Cart | [~] PARTIAL | `r/q/q/zad.java`; `XbowCartModule.java` | Rail/cart/flint/crossbow sequence exists; verify absent items, cooldowns, screen/disable interruptions. | Test every stage and inventory-slot restoration expectations. |
| Stun Cob | [ ] TODO, source-only | `r/q/q/zv.java` | Source class is not instantiated by Vengeance's manager; prediction and web placement. | Evaluate independently; do not count as a registered Vengeance feature. |

## P2 — Movement

| Module | State | Source | Remaining / dependencies | Next action |
| --- | --- | --- | --- | --- |
| Sprint | [~] RETAINED FROM ADIN | `r/q/s/ze.java`; `SprintModule.java` | Existing simple sprint module is registered; runtime semantics need check against source and vanilla input. | Test water, sneak, climb, vehicle and screens. |
| Keep Sprint | [~] PARTIAL | `r/q/s/zc.java`; PlayerEntity mixin | Post-attack hook is wired; validate sprint restoration ordering. | Test attack and knockback states. |
| Auto Head Hitter | [~] PARTIAL | `r/q/s/zb.java`; `AutoHeadHitterModule.java` | Tick/setting logic exists; verify collision and jump-delay behavior. | Test low ceilings and movement states. |
| Auto Firework | [~] PARTIAL | `r/q/s/za.java`; `AutoFireworkModule.java` | Source settings and use/switch-back flow exist; test elytra durability, gliding and held use. | Verify item cooldown and slot restoration. |
| Snap Tap | [~] PARTIAL | `r/q/s/zd.java`; KeyBinding mixin | Timestamp arbitration and hooks compile; requires keyboard runtime tests and mixin confirmation. | Test opposing pairs and key release/disable. |

## P3 — Player / Utility

| Module | State | Source | Remaining / dependencies | Next action |
| --- | --- | --- | --- | --- |
| Auto Double Hand | [~] PARTIAL | `r/q/t/zb.java`; `AutoDoubleHandModule.java` | Hotbar totem switching implemented; source inventory-screen slot semantics differ and need testing. | Test empty Totem Slot and restore when leaving inventory. |
| Auto Drain | [~] PARTIAL | `r/q/t/zc.java`; `AutoDrainModule.java` | Still-water/bucket state machine exists; verify fluid hit detection and cobweb guard. | Test source/flowing water and interruption. |
| Auto Extinguish | [~] PARTIAL | `r/q/t/zd.java`; `AutoExtinguishModule.java` | Bucket/pitch state machine and settings exist; test fire transitions, delay bounds and restore. | Verify disable/world change and pickup timing. |
| Auto MLG | [~] PARTIAL | `r/q/t/ze.java`; `AutoMlgModule.java` | Falling-distance placement/pickup state machine exists; verify landing timing and water pickup. | Test fall distances and no-water/creative cases. |
| Auto Refill | [ ] TODO | `r/q/t/zf.java` | No Tulip class. Vengeance hover mode needs focused-slot access; hotkey mode opens inventory and quick-moves classified potions/carts. | Reconcile with Adin Refill, then port shared config/settings and accessor safely. |
| Auto Tool | [~] PARTIAL | `r/q/t/zg.java`; `AutoToolModule.java` | Source settings and best-tool selection exist; verify scoring, previous slot and durability handling. | Test blocks/tools/durability in-game. |
| Auto Web | [~] PARTIAL | `r/q/t/zh.java`; `AutoWebModule.java` | Current implementation is a simplified fall/feet placement adaptation; compare all original settings/activation semantics. | Read full source and test placement/restore behavior. |
| Cover Up | [ ] TODO | `r/q/t/zi.java` | No Tulip class; rotation and multi-web placement sequence. | Port source state machine with guaranteed rotation/slot restoration. |
| Fast Exp | [~] PARTIAL | `r/q/t/zj.java`; `FastExpModule.java` | Chance/use loop exists; ensure timing cannot spam beyond source behavior. | Compare tick frequency, cooldown and random chance. |
| Fast Mine | [~] PARTIAL | `r/q/t/zk.java`; PlayerEntity mixin | Local block-speed multiplier wired; verify mixin in-game and keep intended range. | Test mining speed and disabled multiplier. |
| Fast Place | [~] PARTIAL | `r/q/t/zl.java`; MinecraftClient accessor | Use-cooldown accessor and settings exist; test actual placement timing and disable reset. | Verify Mixin accessor in a client session. |
| Ping Spoof | [ ] TODO | `r/q/t/zm.java` | No Tulip class; connection packet timing/interception. | Read packet flow and assess server/network side effects before port. |
| ReBuff Notifier | [~] PARTIAL | `r/q/t/zn.java`; `ReBuffNotifierModule.java` | Effect expiry, sound and action-bar notification implemented; verify transition edges/volume. | Test Strength/Speed disappearance and re-enable. |
| Trap Save | [ ] TODO | `r/q/t/zo.java` | No Tulip class; entity/armor-stand trap scan and prediction. | Port source detection with bounded scan cost. |

## P4 — World / Misc

Vengeance registers no module in a World category. Tulip's `WORLD` category currently has no registered module.

| Module | State | Source | Remaining / dependencies | Next action |
| --- | --- | --- | --- | --- |
| Cart Key | [~] PARTIAL | `r/q/r/za.java`; `CartKeyModule.java` | Multi-stage placement/key/bow behavior implemented; placement and silent-switch behavior need runtime comparison. | Test all options and abort states. |
| Fake Player | [ ] TODO | `r/q/r/zb.java` | No Tulip class; source is limited to single-player fake entity construction. | Inspect lifecycle and ensure entity cleanup on disable/world change. |
| Friends | [~] RETAINED FROM ADIN | `r/q/r/zc.java`; `FriendManager.java`, `FriendsModule.java` | Persistent UUID roster is integrated; Vengeance's own Friends screen is intentionally absent. | Verify config round-trip and GUI add/remove path. |
| Hover Totem | [ ] TODO | `r/q/r/zd.java` | No Tulip class; requires hovered inventory slot/screen hook. | Add narrow handled-screen accessor and test inventory lifecycle. |
| Middle Click Friend | [~] PARTIAL | `r/q/r/ze.java`; `MiddleClickFriendModule.java` | Crosshair/entity click toggle exists; test GLFW middle-click and feedback. | Verify add/remove plus friend consumers. |
| Pearl Catch | [ ] TODO | `r/q/r/zf.java` | No Tulip class; multi-step pearl then wind-charge action. | Port timing/cooldown and interruption handling. |
| Pearl Key | [~] PARTIAL | `r/q/r/zg.java`; `PearlKeyModule.java` | Keybind, cooldown, use and slot restore exist; manual input/cooldown test remains. | Test default/rebound key and delayed restore. |
| Teams | [ ] TODO | `r/q/r/zh.java` | No standalone module; teammate check is currently embedded in selected target logic. | Extract shared team-filter utility and wire target modules consistently. |
| Wind Charge Key | [~] PARTIAL | `r/q/r/zi.java`; `WindChargeKeyModule.java` | Keybind, item cooldown and slot restore exist; runtime test needed. | Test rebind, cooldown and disable mid-delay. |

## P5 — HUD

| Module | State | Source | Remaining / dependencies | Next action |
| --- | --- | --- | --- | --- |
| ArrayList | [~] PARTIAL | `r/q/u/za.java`; `ArrayListModule.java` | Enabled-module HUD/settings exist; compare sizing/position with V source and test small screens. | Verify no HUD overlap and toggle animations if desired. |
| Arrow ESP | [ ] TODO | `r/q/u/zb.java` | No Tulip class; world projection/entity iteration. | Port rendering after shared world-HUD utilities exist. |
| Nametags | [ ] TODO | `r/q/u/zk.java` | No Tulip class; world-space text/items and projection hooks. | Reconcile with Adin Nametags; port one implementation, not duplicates. |
| Notifications | [ ] TODO | `r/q/u/zl.java` | No Tulip class; notification queue/render lifecycle. | Create shared notification service and connect module events. |
| Swing Speed | [~] PARTIAL | `r/q/u/zn.java`; LivingEntity mixin | Configurable local swing duration hook exists; runtime target and duration range need testing. | Test players/entities and disable restoration semantics. |
| Target HUD | [ ] TODO | `r/q/u/zp.java` | No Tulip class; needs a common combat-target provider and HUD positioning. | Reconcile with Adin target display before porting. |

## P6 — Render

| Module | State | Source | Remaining / dependencies | Next action |
| --- | --- | --- | --- | --- |
| 2D ESP | [ ] TODO | `r/q/u/zh.java` | No Tulip class; projection and box rendering. | Reuse a tested entity projection utility. |
| 3D ESP | [ ] TODO | `r/q/u/zi.java` | No Tulip class; world render pipeline. | Port after render callback/coordinate conventions are documented. |
| Aspect Ratio | [ ] TODO | `r/q/u/zc.java` | No Tulip class; projection matrix mixin. | Validate exact 1.21.11 GameRenderer target before adding hook. |
| Blur Test | [ ] TODO | `r/q/u/zd.java` | No Tulip class; shader/framebuffer infrastructure. | Decide whether development-only test should be excluded. |
| Container Slots | [ ] TODO | `r/q/u/ze.java` | No Tulip class; handled-screen render hook. | Add scoped screen hook after validating target. |
| Custom Outline | [ ] TODO | `r/q/u/zf.java` | No Tulip class; post-processing/shader pipeline. | Port only with owned shader resources and runtime validation. |
| Full Bright | [~] PARTIAL | `r/q/u/zj.java`; `FullBrightModule.java` | Gamma and night vision exist; verify original gamma restoration and effect ownership. | Test gamma restoration after disable/world exit. |
| Outline ESP | [~] PARTIAL | `r/q/u/zm.java`; Entity mixin | Vanilla glow/filter implementation exists; custom scoreboard color path intentionally omitted. | Verify outline filters and avoid mutating server/team state. |
| Target ESP | [ ] TODO | `r/q/u/zo.java` | No Tulip class; target selection/rendering. | Reuse shared target provider and verify frame cost. |
| Trajectories | [ ] TODO | `r/q/u/zq.java` | No Tulip class; projectile physics/world raycasts. | Port against 1.21.11 projectile/shape APIs and test performance. |
| Dynamic Island | [ ] TODO, source-only | `r/q/u/zg.java` | Source class exists but Vengeance does not register it. | Evaluate source utility before deciding whether to port. |
| Watermark | [~] PARTIAL, source-only | `r/q/u/zr.java`; `WatermarkModule.java` | Tulip adaptation exists; preserves Adin identity rather than Vengeance name/logo. | Runtime-check wrapping, positioning, privacy and HUD overlap. |

## P7 — Architecture / Cleanup

- [ ] **Registration audit** — 44 module registrations match 44 concrete classes today. Add a test/lint check so new classes cannot silently remain unregistered.
- [ ] **Settings/config** — Test keybind, choice, slider, toggle and dynamic Friends settings through save/load, malformed JSON and migrations.
- [ ] **Keybind capture** — Test keyboard and mouse capture, Escape cancellation, rebinding and localized display names.
- [ ] **GUI** — Manual desktop and small-screen checks for category scrolling, clipping, settings access, input capture and layout bounds.
- [ ] **Duplicate architecture** — Review repeated key-use state machines, item-slot restoration, target selection and settings serialization for shared abstractions after behavioral validation.
- [ ] **Mixin cleanup** — Runtime-check all 9 declared mixin/accessor classes, refmap, target descriptors and ordering. Keep hooks narrowly scoped.
- [ ] **Errors/resources** — Add actionable config/log errors; verify temporary item/pitch/key state is restored on disconnect/world unload.
- [ ] **Source organization** — Current simple module package is coherent; only add `setting/` or `util/` packages when a real shared abstraction is justified.
- [ ] **Compatibility** — Keep Minecraft 1.21.11, Yarn `1.21.11+build.6`, Fabric Loader 0.18.4, Fabric API 0.141.6+1.21.11, Java 21, Loom 1.13.6.

## P8 — Testing

- [~] Compilation: Java 21 compile passed before the documentation edits; rerun after all changes.
- [~] Unit tests: currently 2 test classes / 4 tests, covering FriendManager JSON membership, Criticals choice settings and keybind-code persistence only.
- [ ] Runtime launch: not yet confirmed in this session; test on a display-enabled client.
- [ ] GUI: open/close, category/module/settings navigation, scroll clipping and small resolutions.
- [ ] Settings/config: each setting type, persistence and invalid config.
- [ ] Combat: test each attack hook, combined-module priority, packet handling, slot restoration and protected Triggerbot.
- [ ] Movement/player: test user input/keybind conflicts, item cooldown, use/restore timing and world/disconnect cleanup.
- [ ] Render/HUD: confirm overlays are visible, bounded, non-overlapping and disabled cleanly.
- [ ] Crashes/mixins: inspect client logs for injection failures and verify all mixins applied.
- [ ] Final gates: `./gradlew clean build --no-daemon`, inspect JAR metadata/refmap/classes.

## Final Release Checklist

- [ ] All required gameplay modules are ported or explicitly accepted as excluded.
- [ ] Adin GUI and original Adin Triggerbot are preserved.
- [ ] Every module has verified source → construction → registration → GUI/settings → enable/disable → behavior.
- [ ] All settings persist and affect behavior.
- [ ] All mixins apply at runtime on Minecraft 1.21.11.
- [ ] Runtime launch and desktop/small-screen GUI checks pass.
- [ ] No stale build logs, generated artifacts, credentials or downloaded reference archives are tracked.
- [ ] README, PORT_STATUS and HANDOFF match the tested code.
- [ ] Java 21 `clean build` passes and the produced JAR is inspected.
- [ ] Upstream licensing/credits are reviewed before publishing a fork or release.
