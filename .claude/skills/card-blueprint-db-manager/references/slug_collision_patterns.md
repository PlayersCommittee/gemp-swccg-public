# Slug Collision Patterns

This document describes common patterns of slug collisions in the card blueprint database and how to resolve them.

## Why Slug Collisions Occur

Slugs are created by converting card titles to lowercase and replacing special characters with hyphens. Collisions happen when:

1. **Same card name on both sides** - Many locations exist for both Light and Dark (e.g., "Bespin", "Tatooine")
2. **Defensive shields share names** - Defensive shields often have same titles as regular cards
3. **Multiple versions** - Same card in different sets (Premiere, Special Edition, Virtual)
4. **Generic names** - Common terms like "Blaster", "Lightsaber", "Trooper"

## Collision Type 1: Light/Dark Same Name

### Examples

**Location: Bespin**
- Light side: Bespin (Cloud City)
- Dark side: Bespin (Cloud City)
- Slug: `bespin`

**Location: Tatooine**
- Light side: Tatooine (Premiere)
- Dark side: Tatooine (Premiere)
- Slug: `tatooine`

**Character: Rebel Trooper / Stormtrooper**
- Light side: Rebel Trooper
- Dark side: Stormtrooper
- Different slugs but conceptually similar

### Resolution Strategy

Filter by side before mapping:

```bash
# For light side CSVs
jq '[.[] | select(.side == "LIGHT")]' card_blueprint_database.json > db_light.json

# For dark side CSVs
jq '[.[] | select(.side == "DARK")]' card_blueprint_database.json > db_dark.json
```

This ensures when mapping "Bespin" from a light side CSV, only the light side Bespin is found.

## Collision Type 2: Defensive Shields

### Examples

**Aim High**
- Regular card: "Aim High" (maneuver)
- Defensive shield: "Aim High"
- Slug: `aim-high`

**Don't Do That Again**
- Regular card: "Don't Do That Again" (interrupt)
- Defensive shield: "Don't Do That Again"
- Slug: `dont-do-that-again`

### Resolution Strategy

Exclude defensive shields from all mapping databases:

```bash
# Filter out defensive shields
jq '[.[] | select(.cardCategory != "DEFENSIVE_SHIELD")]' card_blueprint_database.json > db_no_shields.json

# Combined: light side, no defensive shields
jq '[.[] | select(.side == "LIGHT" and .cardCategory != "DEFENSIVE_SHIELD")]' card_blueprint_database.json > db_light.json
```

Defensive shields are typically not included in cube drafts, so this exclusion is appropriate.

## Collision Type 3: Multiple Set Versions

### Examples

**Luke Skywalker**
- Luke (Premiere)
- Luke Skywalker (A New Hope)
- Luke Skywalker, Jedi Knight (Jabba's Palace)
- Slug variations: `luke`, `luke-skywalker`, `luke-skywalker-jedi-knight`

**Darth Vader**
- Darth Vader (Premiere)
- Darth Vader (Special Edition)
- Darth Vader, Dark Lord (Virtual)
- Slug: `darth-vader`, `darth-vader-dark-lord`

### Resolution Strategy

Use `slugWithSetName` field for disambiguation:

```bash
# Find all Luke cards
jq '.[] | select(.slug | startswith("luke")) | {cardId, title, slug, slugWithSetName, expansionSet}' card_blueprint_database.json
```

Or specify set in CSV:
```csv
Luke (Premiere)
Luke Skywalker (A New Hope)
```

The mapping script should try exact slug match first, then slug+set.

## Collision Type 4: Generic Equipment/Weapons

### Examples

**Blaster**
- Blaster (Premiere)
- Blaster (Special Edition)
- Blaster Rifle
- Blaster Pistol
- Slug: `blaster`, `blaster-rifle`, `blaster-pistol`

**Lightsaber**
- Lightsaber (Premiere)
- Luke's Lightsaber
- Vader's Lightsaber
- Obi-Wan's Lightsaber
- Slug variations based on subtitle

### Resolution Strategy

Be specific in CSV entries:

```csv
Blaster Rifle
Blaster Pistol
Luke's Lightsaber
```

Avoid generic "Blaster" or "Lightsaber" unless you specifically want the Premiere basic version.

## Detecting Collisions

### Find All Duplicate Slugs

```bash
# List all slugs that appear more than once
jq -r '.[] | .slug' card_blueprint_database.json | sort | uniq -d
```

### Analyze Specific Collision

```bash
# For a specific slug, show all cards
SLUG="bespin"
jq ".[] | select(.slug == \"$SLUG\") | {cardId, title, side, cardCategory, expansionSet}" card_blueprint_database.json
```

### Count Collision Types

```bash
# Count light/dark collisions (same slug, different sides)
jq 'group_by(.slug) | map(select(length > 1 and ([.[].side] | unique | length > 1))) | length' card_blueprint_database.json

# Count defensive shield collisions
jq 'group_by(.slug) | map(select(length > 1 and ([.[].cardCategory] | unique | contains(["DEFENSIVE_SHIELD"])))) | length' card_blueprint_database.json
```

## Complete Collision Resolution Workflow

### Step 1: Analyze Collisions

```bash
# Generate collision report
jq -r '.[] | .slug' card_blueprint_database.json | sort | uniq -d > duplicate_slugs.txt

# For each duplicate, show details
while read slug; do
  echo "=== $slug ==="
  jq ".[] | select(.slug == \"$slug\") | {cardId, title, side, cardCategory}" card_blueprint_database.json
  echo ""
done < duplicate_slugs.txt > collision_report.txt
```

### Step 2: Create Filtered Databases

```bash
# Light side, no defensive shields
jq '[.[] | select(.side == "LIGHT" and .cardCategory != "DEFENSIVE_SHIELD")]' \
  card_blueprint_database.json \
  > card_blueprint_database_light.json

# Dark side, no defensive shields
jq '[.[] | select(.side == "DARK" and .cardCategory != "DEFENSIVE_SHIELD")]' \
  card_blueprint_database.json \
  > card_blueprint_database_dark.json
```

### Step 3: Verify Resolution

```bash
# Check for remaining collisions in filtered database
jq -r '.[] | .slug' card_blueprint_database_light.json | sort | uniq -d

# If output is empty, no collisions remain
```

### Step 4: Use Filtered Databases for Mapping

```bash
# Map light side CSV with light database
./bin/map_card_names_to_ids \
  cube_worlds_lightside.csv \
  cube_worlds_lightside_mapped.json \
  card_blueprint_database_light.json

# Map dark side CSV with dark database
./bin/map_card_names_to_ids \
  cube_worlds_darkside.csv \
  cube_worlds_darkside_mapped.json \
  card_blueprint_database_dark.json
```

## Real-World Examples

### Example 1: Tatooine Location

**Problem:** CSV contains "Tatooine", but it exists on both sides.

**Analysis:**
```bash
jq '.[] | select(.slug == "tatooine")' card_blueprint_database.json
```

**Output shows:**
- `101_15` - Tatooine (Light Side, Premiere)
- `101_16` - Tatooine (Dark Side, Premiere)

**Resolution:**
- For light side CSV: Use `card_blueprint_database_light.json` → maps to `101_15`
- For dark side CSV: Use `card_blueprint_database_dark.json` → maps to `101_16`

### Example 2: Aim High Collision

**Problem:** CSV contains "Aim High", which exists as both card and defensive shield.

**Analysis:**
```bash
jq '.[] | select(.slug == "aim-high")' card_blueprint_database.json
```

**Output shows:**
- `7_123` - Aim High (Light Side, Interrupt, Special Edition)
- `13_45` - Aim High (Light Side, Defensive Shield)

**Resolution:**
Use database with shields excluded:
```bash
jq '[.[] | select(.cardCategory != "DEFENSIVE_SHIELD")]' card_blueprint_database.json
```

Result: Only `7_123` (the interrupt) is found.

### Example 3: Luke Skywalker Variants

**Problem:** CSV contains "Luke Skywalker", but multiple versions exist.

**Analysis:**
```bash
jq '.[] | select(.slug | contains("luke"))' card_blueprint_database.json | jq '{cardId, title, slug}'
```

**Output shows:**
- `101_2` - Luke (slug: `luke`)
- `4_23` - Luke Skywalker (slug: `luke-skywalker`)
- `7_45` - Luke Skywalker, Jedi Knight (slug: `luke-skywalker-jedi-knight`)

**Resolution:**
Be specific in CSV:
```csv
Luke                              # Maps to 101_2
Luke Skywalker                    # Maps to 4_23
Luke Skywalker, Jedi Knight       # Maps to 7_45
```

Or use set specifiers:
```csv
Luke (Premiere)                   # Maps to 101_2
Luke Skywalker (A New Hope)       # Maps to 4_23
```

## Best Practices

1. **Always filter by side** when building side-specific cubes
2. **Always exclude defensive shields** unless specifically needed
3. **Be specific in CSVs** - use full card names with subtitles
4. **Verify mapping logs** - check `*_mapped_log.json` for warnings
5. **Test filtered databases** - count cards to ensure filtering worked
6. **Keep filtered databases** - reuse for future cube builds

## Troubleshooting

### Problem: Still Getting Wrong Card

**Check if filter worked:**
```bash
# Verify side in filtered database
jq '[.[] | .side] | unique' card_blueprint_database_light.json
# Should output: ["LIGHT"]

# Check if specific card is present
jq '.[] | select(.cardId == "WRONG_CARD_ID")' card_blueprint_database_light.json
# Should output nothing if card is dark side
```

### Problem: Mapping Script Not Using Filtered Database

**Check script parameter:**
```bash
# Script should accept third parameter for database path
grep -A 5 "sys.argv" bin/map_card_names_to_ids

# Look for something like:
# database_path = sys.argv[3] if len(sys.argv) > 3 else "default_path"
```

If not present, script needs modification to accept custom database path.

### Problem: Too Many Collisions Remain

**Find remaining collisions:**
```bash
jq -r '.[] | .slug' card_blueprint_database_light.json | sort | uniq -d
```

**For each remaining collision:**
```bash
SLUG="duplicate-slug"
jq ".[] | select(.slug == \"$SLUG\")" card_blueprint_database_light.json
```

Likely causes:
- Multiple versions of same card (different sets)
- Generic names (need more specific CSV entries)
- Objective/non-objective versions

**Resolution:** Update CSV with more specific names or set specifiers.
