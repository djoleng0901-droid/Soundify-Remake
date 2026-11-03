#!/usr/bin/env bash
set -euo pipefail

ARCHIVE="${1:-soundify-audio-21.zip}"
DEST="src/main/resources/assets/soundify_remake/sounds/video/reference"

if [[ ! -f "$ARCHIVE" ]]; then
  echo "Archive not found: $ARCHIVE" >&2
  echo "Place soundify-audio-21.zip in the soundify-project directory or pass its path." >&2
  exit 1
fi

if ! command -v unzip >/dev/null 2>&1; then
  echo "unzip is required to install the reference audio." >&2
  exit 1
fi

mkdir -p "$DEST"

clips=(
  01_explosion.ogg
  02_death.ogg
  03_respawn.ogg
  04_killstreak_1.ogg
  05_killstreak_2.ogg
  06_killstreak_3.ogg
  07_killstreak_4_plus.ogg
  08_speed_effect.ogg
  09_slowness_effect.ogg
  10_night_vision_effect.ogg
  11_jump_boost_effect.ogg
  12_jump_with_boost.ogg
  13_item_drop.ogg
  15_ender_pearl_teleport.ogg
  16_xp_level_up.ogg
  17_golden_apple_effect.ogg
  18_block_place.ogg
  19_inventory_open.ogg
  20_inventory_close.ogg
  21_inventory_item_select.ogg
)

for clip in "${clips[@]}"; do
  entry="soundify-audio-21/$clip"
  if ! unzip -Z1 "$ARCHIVE" "$entry" >/dev/null 2>&1; then
    echo "Missing clip in archive: $entry" >&2
    exit 1
  fi
  unzip -p "$ARCHIVE" "$entry" > "$DEST/$clip"
  if [[ ! -s "$DEST/$clip" ]]; then
    echo "Extracted file is empty: $DEST/$clip" >&2
    exit 1
  fi
done

echo "Installed ${#clips[@]} non-weapon reference audio clips into:"
echo "$DEST"
if command -v ffprobe >/dev/null 2>&1; then
  for clip in "${clips[@]}"; do
    ffprobe -v error -show_entries format=duration -of csv=p=0 "$DEST/$clip" >/dev/null
  done
  echo "All installed OGG files passed ffprobe validation."
else
  echo "ffprobe not found; skipped audio validation."
fi
