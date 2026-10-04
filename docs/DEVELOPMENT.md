# Development

## Requirements

- Minecraft **1.21.11 only**.
- Fabric Loader 0.18.4 and Fabric API 0.141.6+1.21.11.
- Java 21.
- Use the repository's Gradle wrapper; Loom is 1.13.6 and the wrapper is Gradle 8.14.3.
- No API keys or external credentials are required.

Codespaces may select Java 25 by default. Select Java 21 before invoking Gradle:

```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"
```

## Build and Test

```bash
./gradlew compileJava --no-daemon
./gradlew test --no-daemon
./gradlew build --no-daemon
```

Before a release or after broad integration, use a clean build:

```bash
./gradlew clean build --no-daemon
```

The JAR is written to `build/libs/`. Inspect `fabric.mod.json`, all registered module classes, `tulip-client.mixins.json`, and `tulip-client-refmap.json` inside the JAR. A successful compile alone is not runtime validation.

## Porting Workflow

1. Read `HANDOFF.md`, `TODO.md`, `PORT_STATUS.md`, `ARCHITECTURE.md` and this file before modifying code.
2. Inspect the Tulip implementation and the matching class in `/workspaces/vengeance-reference/Vengeance--1.0.0` (and the original Adin source for overlaps).
3. Identify settings, event/utility dependencies, mixins, packet/render hooks, state cleanup, and 1.21.11 mappings.
4. Adapt behavior to `Module`, `ModuleSetting`, `ClientConfig` and the existing Adin GUI; do not copy Vengeance's GUI/event bus as a replacement.
5. Register the instance in `TulipClient.onInitializeClient()` and expose functional settings through `getSettings()`.
6. Compile the logical batch with `./gradlew compileJava --no-daemon`; fix only failures caused by the batch.
7. Run `./gradlew test --no-daemon` after a stable batch, then perform runtime checks where available.
8. Update `PORT_STATUS.md` and `TODO.md` with verified status and blockers.
9. Review `git diff --check`, `git status`, test output, and generated artifact before committing.

## Compatibility and Protected Features

Do **not** upgrade Minecraft to 26.x or use Java 25. Keep Minecraft 1.21.11, Fabric, and Java 21. Preserve the Adin-style GUI and the original Adin-derived Triggerbot. Never register or merge Vengeance Trigger Bot. Do not report a module complete merely because a `.java` file exists or compilation passes.

## Runtime Verification

Launch in a display-enabled environment using the Gradle `runClient` task if the project run configuration supports it. Check the log for Mixin failures. Exercise GUI navigation, module enable/disable, settings persistence, input/slot/rotation cleanup, HUD bounds and relevant module behavior. This Codespace's graphical availability must be checked before claiming that an in-game launch was performed.
