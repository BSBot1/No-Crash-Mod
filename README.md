# No Crash (Fabric, Client-Side)

Client-side safety mod for Minecraft Java `1.21.11` on Fabric.

This repository now also contains a Meteor Client addon version in `meteor-addon/`.

## Features

- `Anti-Crash` master toggle for all protections below.
- `Anti-Guardian Crash`:
  - blocks `ELDER_GUARDIAN_EFFECT` game-state packets
  - blocks Elder Guardian particles
  - blocks Elder Guardian curse sound
- `Block Loud Noises`:
  - blocks loud sound events like dragon death/growl, wither spawn/death, end portal spawn, thunder, warden sonic boom/roar, raid horn
  - blocks loud world events like dragon death, wither spawn, end portal open, end gateway spawn, dragon resurrect, wither block break
- `Hide Long Nametags`:
  - hides names longer than 16 chars
  - hides names with too many legacy format codes (`§`)
  - hides heavily obfuscated or heavily underlined names
- `Interaction Bypass`:
  - lets you click through `interaction` entities instead of hitting them
- `RAM/Packet Guard`:
  - blocks oversized inventory/item packets before they are processed
  - shows an on-screen alert when blocked
  - tries `clear @s` automatically when possible
- `Structure Outline Guard`:
  - if a structure block would render a massive invisible-block outline, it falls back to box-only rendering
- `Entity Render Limit` slider:
  - range `0-1000`
  - `1001 = Infinite`
- `Particle Render Limit` slider:
  - range `0-10000`
  - `10001 = Infinite`
- `Block Entity Render Limit` slider:
  - range `0-1000`
  - `1001 = Infinite`
- `/cloop` client command loop:
  - `/cloop run <command>` starts a per-tick loop
  - `/cloop list` shows loops in creation order with state, visibility, uptime, tick count, and command
  - `/cloop pause <number|all>` pauses loop(s)
  - `/cloop resume <number|all>` resumes paused loop(s)
  - `/cloop stop <number|all>` stops and removes loop(s)
  - `/cloop hide <number|all>` hides matching loop output from chat
  - `/cloop show <number|all>` shows matching loop output again

## In-game menu

- Press `Insert` by default to open the No Crash settings screen.
- You can also open the same menu from the title screen button.
- You can rebind all keys in `Controls -> Key Binds -> No Crash`.
- Each toggle has its own separate keybind.

## Build

```bash
./gradlew build
```

This builds both projects:

- Fabric mod jar: `build/libs/`
- Meteor addon jar: `meteor-addon/build/libs/`

## Notes

- This mod is client-side only.
- It does not modify server behavior.
