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
