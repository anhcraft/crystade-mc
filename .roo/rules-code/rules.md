# Crystade MC — System Rules

> **Single source of truth.** All code in this monorepo MUST conform to these rules.

---

## 1. Project Identity

| Property | Value |
|----------|-------|
| **Group** | `dev.anhcraft` |
| **Root project** | `crystade-mc` |
| **Language** | Java |
| **Build system** | Gradle Kotlin DSL (no Groovy) |
| **Gradle version** | 9.x (wrapper) |
| **License** | GPL v3.0 |

This monorepo builds Minecraft server plugins that integrate [Crystade](https://crystade.com) — a SaaS platform for cron jobs, monitoring, and health checks.

---

## 2. Module Layout

Each platform gets its own Gradle submodule under the root project.

### 2.1 Package Convention

Every module MUST use a unified package naming scheme:

```
dev.anhcraft.crystade.<platform>
```

| Module directory | Platform | Package | Java target |
|------------------|----------|---------|-------------|
| `spigot/` | Bukkit/Spigot/Paper 1.14+ | `dev.anhcraft.crystade.spigot` | 11 |
| `velocity/` | Velocity 3.1.1+ | `dev.anhcraft.crystade.velocity` | 11 |

### 2.2 Main Class Convention

Every module MUST ship a main plugin class conforming to its platform:

| Platform | Main class | Base type |
|----------|-----------|-----------|
| Spigot | `CrystadeSpigot` | `org.bukkit.plugin.java.JavaPlugin` |
| Velocity | `CrystadeVelocity` | (plain class, Guice-constructed) |

- Spigot: `public final class CrystadeSpigot extends JavaPlugin`
- Velocity: `public class CrystadeVelocity` with `@Inject` on the logger field and `@Subscribe` on lifecycle events.

### 2.3 Directory Structure (per module)

```
<module>/
├── build.gradle.kts
├── gradle.properties          (optional, for group/version overrides)
└── src/
    └── main/
        ├── java/
        │   └── dev/
        │       └── anhcraft/
        │           └── crystade/
        │               └── <platform>/
        │                   ├── <MainClass>.java
        │                   ├── config/        (push-config model & loader)
        │                   ├── push/          (push-config engine)
        │                   └── ...
        └── resources/
            └── <plugin-metadata-file>
            └── <plugin-config>
```

---

## 3. Build Rules

### 3.1 Root `build.gradle.kts`

- Apply `java` plugin.
- Configure JUnit 5 for tests ONLY (no platform-specific deps at root).
- Set `group = "dev.anhcraft"` and `version`.
- Do NOT define submodule-specific dependencies here.

### 3.2 Per-Module `build.gradle.kts`

- **Platform API dependencies MUST be `compileOnly`** — they are provided at runtime by the server.
- Set `java.toolchain.languageVersion = JavaLanguageVersion.of(11)`.
- Use `processResources` to expand `${version}` (and any other tokens) in plugin metadata files.

**Spigot requirements:**
```kotlin
plugins {
    id("java-library")
}
dependencies {
    compileOnly("org.spigotmc:spigot-api:1.14-R0.1-SNAPSHOT")
}
```

**Velocity requirements:**
```kotlin
plugins {
    id("java-library")
    id("xyz.jpenilla.run-velocity") version "3.0.2"  // for local dev
}
dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.1.1")
}
```

### 3.3 Dependency Policy

- **Prefer platform-bundled libraries.** Do not shade or bundle external libraries unless strictly necessary.
- If an external library is absolutely required:
  1. Justify it in a comment.
  2. Shade and relocate it (e.g., using Gradle Shadow).
  3. Never expose shaded types in public API.

---

## 4. Code Conventions

### 4.1 General

- **Classes not designed for inheritance MUST be `final`.**
- **Favor composition over inheritance.**
- **Avoid static mutable state.** Use instance fields on the main plugin class.
- **Use SLF4J / `java.util.logging` through the platform's logger** — never `System.out.println`.
- **Null-safety:** Methods returning nullable values MUST be annotated `@Nullable`. Prefer `@NotNull` on parameters by default.
- **Immutability:** Config DTOs, event payloads, and value objects MUST be immutable where possible.

### 4.2 Platform Idioms

| Concern | Spigot | Velocity |
|---------|--------|----------|
| Lifecycle init | `onEnable()` / `onDisable()` | `@Subscribe` on `ProxyInitializeEvent` |
| DI | Manual / none | Guice (`@Inject`) |
| Logger | `getLogger()` (from `JavaPlugin`) | `@Inject private Logger logger` |
| Config | `getConfig()` (YAML) | Custom (TOML/HOCON or YAML via SnakeYAML) |
| Scheduling | `BukkitScheduler` | `VelocityScheduler` |

### 4.3 Naming

| Element | Convention |
|---------|-----------|
| Classes | `PascalCase` |
| Methods & fields | `camelCase` |
| Constants | `UPPER_SNAKE_CASE` |
| Packages | lowercase, single word preferred |
| Module directory | lowercase, matches platform name |

---

## 5. Feature Guidelines

### 5.1 Push Configuration

Follows the [push-config-example](https://github.com/crystade/push-config-example) specification.

### 5.2 Feature Parity

- **Every platform module MUST implement all features** listed in the README compatibility table.
- When adding a feature, implement it in **all** modules in the same PR unless explicitly scoped otherwise.

### 5.3 Common Logic

- If logic is truly platform-agnostic, extract it into a `common/` module (Gradle submodule) that both platform modules depend on with `implementation`.
- The `common/` module MUST NOT depend on any platform API.
- Do NOT create a common module prematurely — duplication is better than a wrong abstraction.

---

## 6. Testing

- **Unit tests:** JUnit 5, placed in `src/test/java/` per module.
- **No platform mocking framework required** — keep logic testable without starting a server.
