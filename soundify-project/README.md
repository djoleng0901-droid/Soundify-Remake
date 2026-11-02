# Soundify Remake — clean rebuild scaffold

This is a **source project scaffold**, not an installable JAR yet.

## Included
- Soundify sound assets extracted from the existing 0.2.9 JAR
- Existing Minecraft sound-event resource mapping for review
- Fresh Fabric client entrypoint
- Gradle/Loom project structure

## Important build caveat
The exact Minecraft 26.2 Yarn mappings and Fabric API coordinates must be verified for the actual release. The placeholder values in `gradle.properties` may make Gradle fail until corrected. No gameplay event hooks or replacement GUI have been implemented in this scaffold yet.

## Build (Termux or PC)
Install JDK 25 and Gradle, then from this directory run:
`gradle build`

The resulting JAR, if the dependencies resolve and compilation succeeds, should appear under `build/libs/`.
