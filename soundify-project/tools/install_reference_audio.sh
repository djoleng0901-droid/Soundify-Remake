#!/usr/bin/env bash
set -euo pipefail

if [[ $# -gt 0 ]]; then
  ARCHIVE="$1"
elif [[ -f "soundify-reference-audio-ready.zip" ]]; then
  ARCHIVE="soundify-reference-audio-ready.zip"
else
  ARCHIVE="soundify-audio-21.zip"
fi
DEST="src/main/resources/assets/soundify_remake/sounds/video/reference"

if [[ ! -f "$ARCHIVE" ]]; then
  echo "Audio archive not found: $ARCHIVE" >&2
  echo "Place soundify-reference-audio-ready.zip or soundify-audio-21.zip in the project root." >&2
  exit 1
fi

if ! command -v unzip >/dev/null 2>&1; then
  echo "unzip is required to install the reference audio." >&2
  exit 1
fi

mkdir -p "$DEST"

# These are the 20 non-weapon clips used by the client event mappings.
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

mapfile -t archive_entries < <(unzip -Z1 "$ARCHIVE")

for clip in "${clips[@]}"; do
  entry=""
  for candidate in "${archive_entries[@]}"; do
    if [[ "$candidate" == "$clip" || "$candidate" == */"$clip" ]]; then
      entry="$candidate"
      break
    fi
  done

  if [[ -z "$entry" ]]; then
    echo "Missing clip in archive: $clip" >&2
    exit 1
  fi

  unzip -p "$ARCHIVE" "$entry" > "$DEST/$clip"
  if [[ ! -s "$DEST/$clip" ]]; then
    echo "Extracted file is empty: $DEST/$clip" >&2
    exit 1
  fi
done

if command -v ffprobe >/dev/null 2>&1; then
  for clip in "${clips[@]}"; do
    ffprobe -v error -show_entries format=duration -of csv=p=0 "$DEST/$clip" >/dev/null
  done
  echo "Installed and validated ${#clips[@]} non-weapon reference OGG files in $DEST"
else
  echo "Installed ${#clips[@]} non-weapon reference OGG files in $DEST"
  echo "ffprobe not found; skipped audio validation."
fi
