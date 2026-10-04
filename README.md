# 🌷 Tulip Client

Tulip is a Fabric client for Minecraft 1.21.11 that preserves the Adin-style interface and original Adin-derived Triggerbot while progressively adapting functionality from the Vengeance reference into one maintainable module system.

## Features

The source currently registers modules for combat, movement, player utility, miscellaneous input, render, and HUD. Integrated Vengeance counterparts include Aim Assist, Anti Miss, Auto Cart, Auto Lunge, Auto Pot, Criticals, Hit Cob, Hitboxes, STap, Shield Breaker, SpearKill, Sword Hotswap, Sword Swap, Throw Pot, Totem Hit, Velocity, WTap, Xbow Cart, Auto Firework, Auto Head Hitter, Keep Sprint, Snap Tap, Auto Double Hand, Auto Drain, Auto Extinguish, Auto MLG, Auto Tool, Auto Web, Fast Exp, Fast Mine, Fast Place, Full Bright, ReBuff Notifier, ArrayList, Swing Speed, Outline ESP, Cart Key, Pearl Key, Wind Charge Key, and a source-only Watermark adaptation. Sprint and Friends retain/adapt Adin behavior. These are partial integrations until runtime behavior is verified; see the port ledger.

The Adin-style GUI supports category/module/settings navigation, scrolling, clipping and a compact layout. Configuration persists module states, settings, and friend UUIDs. Middle Click Friend is also registered.

## Compatibility

- Minecraft 1.21.11 only
- Fabric Loader 0.18.4
- Fabric API 0.141.6+1.21.11
- Java 21
- Fabric Loom 1.13.6 / Gradle wrapper 8.14.3

Minecraft 26.x is not the target.

## Architecture

Tulip uses one Fabric client entrypoint and adapts Vengeance behavior to the existing module, setting, config and Mixin structure. Vengeance's GUI and event bus are not imported as the primary architecture. The original Adin Triggerbot is protected; Vengeance Trigger Bot is excluded.

## Build

Codespaces may default to Java 25. Select Java 21 first:

```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"

./gradlew compileJava --no-daemon
./gradlew test --no-daemon
./gradlew build --no-daemon
```

For a clean release build, run `./gradlew clean build --no-daemon`. The output JAR is in `build/libs/`.

## Development Status

Vengeance migration is ongoing. The latest source audit estimates 59% code-counterpart coverage across applicable Vengeance gameplay/render features; this is not a behavioral completion percentage. Runtime launch and most module behaviors remain unverified. Do not treat compilation success as project completion.

## Project Documentation

- [TODO checklist](docs/TODO.md)
- [Vengeance port status](docs/PORT_STATUS.md)
- [Architecture](docs/ARCHITECTURE.md)
- [Development workflow](docs/DEVELOPMENT.md)
- [Next-developer handoff](docs/HANDOFF.md)
- [ADR-001: client direction](docs/decisions/ADR-001-client-direction.md)

## Contributing / Forking

Fork the repository, select Java 21, and read `docs/HANDOFF.md`, `docs/TODO.md`, `docs/PORT_STATUS.md`, `docs/ARCHITECTURE.md`, and `docs/DEVELOPMENT.md` before changing code. Port related modules in batches, inspect the corresponding upstream source and Adin overlap, keep settings functional, register modules explicitly, validate Mixin mappings, and update the status documents after testing.

## Triggerbot

Tulip retains the original Adin-derived Triggerbot. The Vengeance Trigger Bot is intentionally not used and must not replace or merge into it.

## Credits and Disclaimer

Vengeance reference source is under `/workspaces/vengeance-reference/Vengeance--1.0.0`; Adin reference material is under `/workspaces/Tulip-client-/downloads/adin-0.1`. The referenced project metadata declares CC0-1.0. Tulip does not claim authorship of upstream code; retain upstream attribution and license terms when redistributing. Client modifications may violate individual server rules; follow the rules of the server being used.
