# ADR-001: Preserve Adin Identity and Adapt Vengeance Features

- Status: Accepted
- Date: 2026-10-04

## Context

Tulip is combining the Adin-derived client foundation with selected behavior from the Vengeance reference. The Adin-style GUI and original Adin Triggerbot are explicit protected requirements. Vengeance targets the same Minecraft 1.21.11/Fabric/Java 21 stack, but has its own module manager, event bus, GUI, keybind system, rendering utilities, and mixins.

## Decision

Keep Tulip's Adin-style screen, module/config/settings lifecycle, and Adin-derived Triggerbot. Port Vengeance behavior into Tulip modules and add only narrowly required 1.21.11-compatible hooks. Do not run both clients' managers, import Vengeance's GUI as the primary screen, or port Vengeance Trigger Bot.

## Consequences

- Each source behavior must be adapted to Tulip categories/settings/config and explicitly registered.
- Overlapping modules are one Tulip registration; reconcile against Adin and Vengeance before claiming equivalence.
- Mixins/refmap and actual runtime behavior require separate verification.
- Vengeance client/config/debug entries are not automatically useful Tulip modules.
- Migration remains partial until every applicable module is evaluated and the release checklist passes.
