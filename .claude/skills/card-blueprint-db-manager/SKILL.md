---
name: card-blueprint-db-manager
description: This skill should be used when you need to create filtered subsets of the card_blueprint_database.json file to resolve slug collisions during card mapping. It provides jq commands to filter by side (LIGHT/DARK) and exclude defensive shields, creating temporary filtered JSON files for use with cube-builder.
---

# Card Blueprint Database Manager

## Overview

The card blueprint database (`card_blueprint_database.json`) contains all SWCCG card data with slugified names for mapping. However, slug collisions occur when:
1. Light side and dark side cards have the same name (e.g., "Bespin" exists on both sides)
2. Defensive shields share slugs with regular cards

This skill provides jq-based filtering to create side-specific databases that exclude defensive shields, resolving these collisions during cube building.

## When to Use This Skill

Use this skill when:
- Building cube drafts with cards that have slug collisions
- Mapping card names to IDs fails due to duplicate slugs
- You need side-specific card databases (light only or dark only)
- You need to exclude defensive shields from mapping

## Card Database Structure

Each card in the database has these relevant fields:

```json
{
  "cardId": "101_2",
  "title": "Luke",
  "slug": "luke",
  "slugWithSetName": "luke-premiere-introductory-two-player-game",
  "side": "LIGHT",
  "cardCategory": "CHARACTER",
  "cardTypes": ["REBEL"]
}
```

**Key fields for filtering:**
- `side` - Either "LIGHT" or "DARK"
- `cardCategory` - Card category, including "DEFENSIVE_SHIELD"
- `cardTypes` - Array of types, including "DEFENSIVE_SHIELD"

## Filtering Commands

### Filter by Light Side (Exclude Defensive Shields)

Create a light side only database:

```bash
jq '[.[] | select(.side == "LIGHT" and .cardCategory != "DEFENSIVE_SHIELD")]' \
  src/gemp-swccg-cards/src/main/resources/card_blueprint_database.json \
  > src/gemp-swccg-cards/src/main/resources/card_blueprint_database_light.json
```

### Filter by Dark Side (Exclude Defensive Shields)

Create a dark side only database:

```bash
jq '[.[] | select(.side == "DARK" and .cardCategory != "DEFENSIVE_SHIELD")]' \
  src/gemp-swccg-cards/src/main/resources/card_blueprint_database.json \
  > src/gemp-swccg-cards/src/main/resources/card_blueprint_database_dark.json
```

### Filter Excluding All Defensive Shields

Create database without any defensive shields:

```bash
jq '[.[] | select(.cardCategory != "DEFENSIVE_SHIELD")]' \
  src/gemp-swccg-cards/src/main/resources/card_blueprint_database.json \
  > src/gemp-swccg-cards/src/main/resources/card_blueprint_database_no_shields.json
```

### Verify Filtering Results

Check card counts after filtering:

```bash
# Original database count
jq 'length' src/gemp-swccg-cards/src/main/resources/card_blueprint_database.json

# Light side count
jq 'length' src/gemp-swccg-cards/src/main/resources/card_blueprint_database_light.json

# Dark side count
jq 'length' src/gemp-swccg-cards/src/main/resources/card_blueprint_database_dark.json

# Verify no defensive shields
jq '[.[] | select(.cardCategory == "DEFENSIVE_SHIELD")] | length' \
  src/gemp-swccg-cards/src/main/resources/card_blueprint_database_light.json
# Should output: 0
```

## Integration with Cube Builder

When building cubes with slug collisions, modify the `map_card_names_to_ids` script to use filtered databases:

### Standard Workflow (No Collisions)

```bash
./bin/map_card_names_to_ids \
  $CUBE_DIR/cube_worlds_lightside.csv \
  $CUBE_DIR/cube_worlds_lightside_mapped.json
```

### Modified Workflow (With Slug Collisions)

**Step 1: Create filtered databases**

```bash
# Light side database (no defensive shields)
jq '[.[] | select(.side == "LIGHT" and .cardCategory != "DEFENSIVE_SHIELD")]' \
  src/gemp-swccg-cards/src/main/resources/card_blueprint_database.json \
  > src/gemp-swccg-cards/src/main/resources/card_blueprint_database_light.json

# Dark side database (no defensive shields)
jq '[.[] | select(.side == "DARK" and .cardCategory != "DEFENSIVE_SHIELD")]' \
  src/gemp-swccg-cards/src/main/resources/card_blueprint_database.json \
  > src/gemp-swccg-cards/src/main/resources/card_blueprint_database_dark.json
```

**Step 2: Modify map_card_names_to_ids script to accept database path**

The script needs to accept an optional third parameter for the database path:

```bash
./bin/map_card_names_to_ids \
  $CUBE_DIR/cube_worlds_lightside.csv \
  $CUBE_DIR/cube_worlds_lightside_mapped.json \
  src/gemp-swccg-cards/src/main/resources/card_blueprint_database_light.json
```

**Step 3: Map with appropriate database**

```bash
CUBE_DIR="src/gemp-swccg-server/src/main/resources/draft/cube_building/worlds_2025"

# Map light side with light database
./bin/map_card_names_to_ids \
  $CUBE_DIR/cube_worlds_lightside.csv \
  $CUBE_DIR/cube_worlds_lightside_mapped.json \
  src/gemp-swccg-cards/src/main/resources/card_blueprint_database_light.json

# Map dark side with dark database
./bin/map_card_names_to_ids \
  $CUBE_DIR/cube_worlds_darkside.csv \
  $CUBE_DIR/cube_worlds_darkside_mapped.json \
  src/gemp-swccg-cards/src/main/resources/card_blueprint_database_dark.json

# Default cards can use either (or no-shields version if needed)
./bin/map_card_names_to_ids \
  $CUBE_DIR/default_cards.csv \
  $CUBE_DIR/default_cards_mapped.json
```

## Complete Cube Building Example with Filtering

```bash
# 1. Regenerate main card database
./bin/gemp export-cards src/gemp-swccg-cards/src/main/resources/card_blueprint_database.json

# 2. Create filtered databases
jq '[.[] | select(.side == "LIGHT" and .cardCategory != "DEFENSIVE_SHIELD")]' \
  src/gemp-swccg-cards/src/main/resources/card_blueprint_database.json \
  > src/gemp-swccg-cards/src/main/resources/card_blueprint_database_light.json

jq '[.[] | select(.side == "DARK" and .cardCategory != "DEFENSIVE_SHIELD")]' \
  src/gemp-swccg-cards/src/main/resources/card_blueprint_database.json \
  > src/gemp-swccg-cards/src/main/resources/card_blueprint_database_dark.json

# 3. Verify filtering worked
echo "Light side cards (no shields):"
jq 'length' src/gemp-swccg-cards/src/main/resources/card_blueprint_database_light.json

echo "Dark side cards (no shields):"
jq 'length' src/gemp-swccg-cards/src/main/resources/card_blueprint_database_dark.json

# 4. Map cards with filtered databases
CUBE_DIR="src/gemp-swccg-server/src/main/resources/draft/cube_building/worlds_2025"

./bin/map_card_names_to_ids \
  $CUBE_DIR/cube_worlds_lightside.csv \
  $CUBE_DIR/cube_worlds_lightside_mapped.json \
  src/gemp-swccg-cards/src/main/resources/card_blueprint_database_light.json

./bin/map_card_names_to_ids \
  $CUBE_DIR/cube_worlds_darkside.csv \
  $CUBE_DIR/cube_worlds_darkside_mapped.json \
  src/gemp-swccg-cards/src/main/resources/card_blueprint_database_dark.json

./bin/map_card_names_to_ids \
  $CUBE_DIR/default_cards.csv \
  $CUBE_DIR/default_cards_mapped.json

# 5. Verify mapping success
echo "Light side mapping:"
cat $CUBE_DIR/cube_worlds_lightside_mapped_log.json | jq '.summary'

echo "Dark side mapping:"
cat $CUBE_DIR/cube_worlds_darkside_mapped_log.json | jq '.summary'

# 6. Generate cube configuration
python3 bin/create_cube $CUBE_DIR src/gemp-swccg-server/src/main/resources/draft/cubeWorlds2025.json

# 7. Deploy
./bin/gemp reload-fast
```

## Troubleshooting

### Issue: jq command not found

**Solution:** Install jq via Homebrew:
```bash
brew install jq
```

### Issue: Filtered database still has wrong cards

**Verification:**
```bash
# Check sides in filtered database
jq '[.[] | .side] | unique' card_blueprint_database_light.json
# Should output: ["LIGHT"]

# Check for defensive shields
jq '[.[] | select(.cardCategory == "DEFENSIVE_SHIELD")] | length' card_blueprint_database_light.json
# Should output: 0
```

### Issue: Mapping still finds wrong cards

**Cause:** Script still using main database instead of filtered one

**Solution:** Verify third parameter is being passed and used in the script:
```bash
# Check if script accepts database path parameter
grep -n "sys.argv\[3\]" bin/map_card_names_to_ids
```

If the script doesn't support the third parameter yet, it needs to be modified to accept it.

### Issue: Slug still has collisions after filtering

**Investigation:**
```bash
# Find cards with same slug in filtered database
jq -r '.[] | .slug' card_blueprint_database_light.json | sort | uniq -d

# For each duplicate slug, check which cards have it
jq '.[] | select(.slug == "duplicate-slug-here")' card_blueprint_database_light.json
```

**Common causes:**
- Multiple versions of same card (Premiere, Special Edition, Virtual)
- Same character with different subtitles

**Solution:** Use `slugWithSetName` for disambiguation or add set identifier to CSV.

## Cleanup

After successfully building the cube, you can optionally remove the temporary filtered databases:

```bash
rm src/gemp-swccg-cards/src/main/resources/card_blueprint_database_light.json
rm src/gemp-swccg-cards/src/main/resources/card_blueprint_database_dark.json
rm src/gemp-swccg-cards/src/main/resources/card_blueprint_database_no_shields.json
```

However, keeping them can speed up future cube builds with the same filtering requirements.

## File Locations

```
gemp-swccg/
├── src/gemp-swccg-cards/src/main/resources/
│   ├── card_blueprint_database.json              # Main database
│   ├── card_blueprint_database_light.json        # Light side filtered (temporary)
│   ├── card_blueprint_database_dark.json         # Dark side filtered (temporary)
│   └── card_blueprint_database_no_shields.json   # No shields (temporary)
│
├── bin/
│   └── map_card_names_to_ids                     # Mapping script (needs modification)
│
└── src/gemp-swccg-server/src/main/resources/draft/cube_building/
    └── <cube_name>/
        ├── cube_worlds_lightside.csv             # Light side cards
        ├── cube_worlds_darkside.csv              # Dark side cards
        └── default_cards.csv                     # Core cards
```

## Advanced Filtering

### Filter by Multiple Criteria

Combine filters for complex requirements:

```bash
# Light side characters only (no defensive shields)
jq '[.[] | select(.side == "LIGHT" and .cardCategory == "CHARACTER")]' \
  card_blueprint_database.json > light_characters.json

# Dark side locations only (no defensive shields)
jq '[.[] | select(.side == "DARK" and .cardCategory == "LOCATION")]' \
  card_blueprint_database.json > dark_locations.json

# Specific set only
jq '[.[] | select(.expansionSet == "PREMIERE")]' \
  card_blueprint_database.json > premiere_only.json
```

### Count Cards by Category

```bash
# Count cards by category for light side
jq '[.[] | select(.side == "LIGHT")] | group_by(.cardCategory) | map({category: .[0].cardCategory, count: length})' \
  card_blueprint_database.json

# Count defensive shields
jq '[.[] | select(.cardCategory == "DEFENSIVE_SHIELD")] | length' \
  card_blueprint_database.json
```

### Find Slug Collisions

```bash
# Find duplicate slugs across sides
jq -r '.[] | .slug' card_blueprint_database.json | sort | uniq -d > duplicate_slugs.txt

# For each duplicate, show which sides/categories have it
while read slug; do
  echo "Slug: $slug"
  jq ".[] | select(.slug == \"$slug\") | {cardId, title, side, cardCategory}" \
    card_blueprint_database.json
  echo "---"
done < duplicate_slugs.txt
```

## Resources

See `references/` directory for detailed jq filter examples and common slug collision patterns.
