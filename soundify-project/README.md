# Soundify Remake

Client-side Fabric sound mod for Minecraft Java 26.2.

## Included features

- Independent Soundify on/off and volume controls in the Mod Menu configuration screen
- Inventory open and close sounds
- Inventory cursor pickup/place/drag feedback
- Confirmed item-drop and block-placement sounds
- Ender-pearl teleport feedback after the player actually moves
- Player death and respawn transitions
- Speed, Slowness, Night Vision, and Jump Boost activation sounds
- Jump feedback while Jump Boost is active
- XP level-up and golden-apple completion sounds
- Server-authoritative killstreak sounds
- Existing UI, ambience, movement, and block sound resources

All gameplay listeners are client-side and narrowly scoped. The mod does not cancel vanilla actions.

## Install the reference audio before building

The 20 non-weapon audio clips from the supplied video are packaged separately as
`soundify-reference-audio-ready.zip`. Put that ZIP in this project directory
(the same directory as `build.gradle`). The Gradle resource task will run the
installer automatically before packaging.

You can also install the clips manually:

```bash
bash tools/install_reference_audio.sh soundify-reference-audio-ready.zip
```

To check that every mapped reference clip is present:

```bash
gradle verifyReferenceAudio
```

If the reference audio archive is absent, the ordinary build can still compile,
but the corresponding reference events will be silent. Run the verification
task before treating a JAR as a complete release.

## Resource checks

The CI build runs `verifySoundResources` to ensure that every bundled sound entry points to an existing OGG file. Reference-video clips are supplied separately and can be checked with `gradle verifyReferenceAudio` after installing the audio archive.

## Build

Requirements: JDK 25 and Gradle.

From this directory:

```bash
gradle clean build
```

The mod JAR is written to `build/libs/`.

## Configuration

Open Mod Menu, select **Soundify Remake**, then adjust the independent enable
toggle and volume controls. The setting is stored locally and does not change
Minecraft's master volume.
