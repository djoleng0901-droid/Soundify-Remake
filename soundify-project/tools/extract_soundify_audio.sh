#!/usr/bin/env bash
set -Eeuo pipefail

# Extract the 21 Soundify Remake reference clips from the supplied video.
# Usage:
#   bash tools/extract_soundify_audio.sh [input-video] [output-directory]
#
# Requires ffmpeg and ffprobe on PATH.

INPUT="${1:-1000294062.mp4}"
OUTPUT="${2:-src/main/resources/assets/soundify_remake/sounds/video/reference}"
QUALITY="${OGG_QUALITY:-5}"

if ! command -v ffmpeg >/dev/null 2>&1; then
  echo "ERROR: ffmpeg is not installed or not on PATH." >&2
  exit 1
fi
if ! command -v ffprobe >/dev/null 2>&1; then
  echo "ERROR: ffprobe is not installed or not on PATH." >&2
  exit 1
fi
if [[ ! -f "$INPUT" ]]; then
  echo "ERROR: input video not found: $INPUT" >&2
  exit 1
fi

if ! ffprobe -v error -select_streams a:0 -show_entries stream=index -of csv=p=0 "$INPUT" | grep -q .; then
  echo "ERROR: no audio stream found in: $INPUT" >&2
  exit 1
fi

mkdir -p "$OUTPUT"

# Format: filename|start|end|description
CLIPS=(
  "01_explosion|00:00:00.970|00:00:01.920|Explosion"
  "02_death|00:00:06.900|00:00:07.800|Player death"
  "03_respawn|00:00:08.290|00:00:09.270|Player respawn"
  "04_killstreak_1|00:00:11.190|00:00:12.280|Killstreak first kill"
  "05_killstreak_2|00:00:12.490|00:00:13.230|Killstreak second kill"
  "06_killstreak_3|00:00:13.460|00:00:14.880|Killstreak third kill"
  "07_killstreak_4_plus|00:00:15.270|00:00:16.030|Killstreak fourth and later kills"
  "08_speed_effect|00:00:16.360|00:00:17.280|Speed potion effect"
  "09_slowness_effect|00:00:19.440|00:00:20.570|Slowness potion effect"
  "10_night_vision_effect|00:00:20.880|00:00:21.590|Night vision effect"
  "11_jump_boost_effect|00:00:22.200|00:00:22.870|Jump boost effect"
  "12_jump_with_boost|00:00:23.730|00:00:24.020|Jump while jump boost is active"
  "13_item_drop|00:00:26.670|00:00:27.100|Item drop"
  "14_mace_hotbar_select|00:00:27.270|00:00:27.630|Mace selected in hotbar"
  "15_ender_pearl_teleport|00:00:32.160|00:00:32.860|Ender pearl teleport"
  "16_xp_level_up|00:00:34.370|00:00:35.380|Experience level up"
  "17_golden_apple_effect|00:00:38.730|00:00:39.850|Golden apple or enchanted golden apple effect"
  "18_block_place|00:00:41.780|00:00:42.000|Block placement"
  "19_inventory_open|00:00:43.060|00:00:43.320|Inventory open"
  "20_inventory_close|00:00:44.210|00:00:44.480|Inventory close"
  "21_inventory_item_select|00:00:46.580|00:00:46.750|Inventory item select or drag"
)

count=0
for entry in "${CLIPS[@]}"; do
  IFS='|' read -r name start end description <<< "$entry"
  start_seconds=$(awk -F: '{ printf "%.3f", ($1 * 3600) + ($2 * 60) + $3 }' <<< "$start")
  end_seconds=$(awk -F: '{ printf "%.3f", ($1 * 3600) + ($2 * 60) + $3 }' <<< "$end")
  duration=$(awk -v s="$start_seconds" -v e="$end_seconds" 'BEGIN { printf "%.3f", e-s }')
  if awk -v d="$duration" 'BEGIN { exit !(d > 0) }'; then :; else
    echo "ERROR: invalid clip range for $name ($start-$end)" >&2
    exit 1
  fi

  target="$OUTPUT/$name.ogg"
  ffmpeg -hide_banner -loglevel error -y \
    -i "$INPUT" -ss "$start_seconds" -t "$duration" \
    -map 0:a:0 -vn -c:a libvorbis -q:a "$QUALITY" \
    -metadata title="$description" "$target"

  if [[ ! -s "$target" ]]; then
    echo "ERROR: extraction produced an empty file: $target" >&2
    exit 1
  fi
  count=$((count + 1))
  printf '[%02d/%02d] %s (%ss)\n' "$count" "${#CLIPS[@]}" "$target" "$duration"
done

echo
echo "Done: extracted $count OGG clips into $OUTPUT"
echo "Review the clips before wiring them to gameplay events."
