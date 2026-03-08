# No Crash (Fabric, Client-Side)

Client-side safety mod for Minecraft Java `1.21.11` on Fabric.

## Current fix

- Blocks `ELDER_GUARDIAN_EFFECT` game-state packets when enabled.
- Prevents extreme jumpscare/particle spam from being rendered on the client.

## In-game menu

- Press `Insert` by default to open the No Crash settings screen.
- You can rebind this in `Controls -> Key Binds -> No Crash`.
- The menu lets you toggle:
  - Global `Anti-Crash`
  - `Anti-Guardian Crash`
  - `Block Loud Noises`
  - `Hide Long Nametags`
  - `Interaction Bypass`

## Build

```bash
./gradlew build
```

The built jar is created in `build/libs/`.

## Notes

- This mod is client-side only.
- It does not modify server behavior.
