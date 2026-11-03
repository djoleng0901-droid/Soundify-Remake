# Extracting the 21 Soundify reference audio clips

This repository includes a timestamp-based FFmpeg script for the 21 labeled audio moments in the reference video.

## Requirements

- FFmpeg with the `libvorbis` encoder
- `ffprobe` (included with most FFmpeg distributions)

## Run from the `soundify-project` directory

Place the source video at `1000294062.mp4`, then run:

```bash
bash tools/extract_soundify_audio.sh
```

Or pass custom paths:

```bash
bash tools/extract_soundify_audio.sh /path/to/video.mp4 /path/to/output
```

By default, the generated OGG files are written to:

```text
src/main/resources/assets/soundify_remake/sounds/video/reference/
```

The script creates 21 separately named OGG files. It does not modify `sounds.json` or register gameplay triggers. Those mappings must be reviewed and implemented separately so that each sound plays only for its intended Minecraft action.

## Install the prepared reference clips

If you already have the prepared `soundify-audio-21.zip` archive, place it in the
`soundify-project` directory and run:

```bash
bash tools/install_reference_audio.sh
```

You can also pass the archive path explicitly:

```bash
bash tools/install_reference_audio.sh /path/to/soundify-audio-21.zip
```

The installer copies the 20 non-weapon reference clips into the resource directory
used by `sounds.json` and validates them with `ffprobe` when available. It
intentionally leaves out the Mace-selection clip.
