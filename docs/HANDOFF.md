# Handoff

## Current State

Tulip is a partial Adin + Vengeance client for Minecraft 1.21.11/Fabric/Java 21. The source inventory contains 73 applicable Vengeance gameplay/render features when excluding the protected Trigger Bot and five Vengeance client/config/debug entries, and accounting for two source-only gameplay/render classes. Tulip currently has code counterparts for 43/73 (about 59%). This is a source-module coverage estimate only; it does not mean that 59% of the client is behavior-verified. None of the Vengeance ports should be treated as release-complete until runtime behavior and Mixin application are confirmed.

The current Java 21 compile and test suite have passed. The two test classes/four tests cover friend JSON membership, Criticals choice settings and keybind-code persistence. Runtime launch has not been verified in this handoff. Read `TODO.md` for the per-module checklist and `PORT_STATUS.md` for the full 80-entry Vengeance source inventory.

## What Exists

- Fabric client entrypoint and 44 registered concrete modules.
- Adin-style category/module/settings screen, with eased independent scrolling, clipping, compact-width navigation and setting controls.
- JSON settings/module state and UUID friend persistence.
- Vengeance behavior counterparts/adaptations for Aim Assist, Anti Miss, Auto Cart, Auto Lunge, Auto Pot, Criticals, Hit Cob, Hitboxes, STap, Shield Breaker, SpearKill, Sword Hotswap, Sword Swap, Throw Pot, Totem Hit, Velocity, WTap, Xbow Cart; Auto Firework, Auto Head Hitter, Keep Sprint, Snap Tap, Sprint; Auto Double Hand, Auto Drain, Auto Extinguish, Auto MLG, Auto Tool, Auto Web, Fast Exp, Fast Mine, Fast Place, Full Bright, ReBuff Notifier; ArrayList, Swing Speed, Outline ESP; Cart Key, Friends adaptation, Middle Click Friend, Pearl Key, Wind Charge Key; plus source-only Watermark adaptation. This list is code presence/registration, not a claim of runtime verification. See the status table for precise qualifications.
- Mixin AP/refmap and nine configured mixin/accessor classes.

## Preserved Components

- Adin identity and Adin-style GUI remain the primary client interface.
- The original Adin-derived `TriggerbotModule` remains registered and has not been replaced.
- Vengeance Trigger Bot is intentionally skipped and must remain excluded.

## What Remains

- Combat: Auto Crystal, Auto Mace, Breach Swap, Crystal Optimizer, Elytra HotSwap, Key Anchor, Key Crystal, Key Lava; evaluate Stun Cob (source-only).
- Player: Auto Crafter, Auto Refill, Cover Up, Ping Spoof, Trap Save.
- Misc: Fake Player, Hover Totem, Pearl Catch, Teams.
- HUD: Arrow ESP, Nametags, Notifications, Target HUD.
- Render: 2D ESP, 3D ESP, Aspect Ratio, Blur Test, Container Slots, Custom Outline, Target ESP, Trajectories; evaluate Dynamic Island (source-only).
- Runtime verification is still needed for all registered Vengeance integrations, all configured mixins, GUI/settings/config behavior and cleanup paths.
- Vengeance utility entries Client, ClientSettings, Debugger, RichModernGUI and Secrett ily remain intentionally excluded.

## Known Blockers and Risks

- No verified in-game launch/Mixin-application result is available yet; use a display-enabled environment if the Codespace is headless.
- Mixin AP resolution and compilation do not prove that injections apply at runtime.
- Multiple combat modules intercept `doAttack`; order/recursion and item-slot ownership require runtime validation before combining them.
- Only two narrow test classes/four tests exist; most module behavior and settings are untested.
- Auto Refill overlaps Adin Refill, which has distinct mouse-crossing/selected-hotbar behavior; compare both before porting.
- Reference Vengeance source is decompiled/obfuscated in many packages; preserve source-path mapping and inspect complete control flow.

## Important Decisions

The project is hybrid by design: keep the Adin GUI/client identity and Triggerbot, and adapt selected Vengeance behavior into the Tulip module/config lifecycle. Do not replace Adin with Vengeance or import its GUI. Utility/debug entries are excluded because they belong to Vengeance's own client/settings architecture and do not advance the protected client direction. See [ADR-001](decisions/ADR-001-client-direction.md).

## How to Continue

1. Combat.
2. Movement.
3. Player/Utility.
4. World/Misc (Vengeance currently registers no World-category module).
5. HUD.
6. Render.
7. Cleanup and testing.
8. Release verification.

Batch compatible modules, inspect their source/dependencies first, compile after a logical batch, and run tests when the batch is stable. Update `PORT_STATUS.md` and `TODO.md` immediately after verifying each batch.

## First Thing for the Next Developer/AI

Before editing, read these files in order: `HANDOFF.md`, `TODO.md`, `PORT_STATUS.md`, `ARCHITECTURE.md`, and `DEVELOPMENT.md`. Then select the next P1 TODO, inspect its full Vengeance source and any Adin overlap, and verify current working-tree/build state.
