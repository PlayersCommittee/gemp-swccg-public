# JQ Filter Examples for Card Blueprint Database

This reference provides comprehensive jq command examples for filtering and analyzing the card blueprint database.

## Basic Filtering

### By Side

```bash
# Light side only
jq '[.[] | select(.side == "LIGHT")]' card_blueprint_database.json

# Dark side only
jq '[.[] | select(.side == "DARK")]' card_blueprint_database.json
```

### By Card Category

```bash
# Characters only
jq '[.[] | select(.cardCategory == "CHARACTER")]' card_blueprint_database.json

# Locations only
jq '[.[] | select(.cardCategory == "LOCATION")]' card_blueprint_database.json

# Interrupts only
jq '[.[] | select(.cardCategory == "INTERRUPT")]' card_blueprint_database.json

# Starships only
jq '[.[] | select(.cardCategory == "STARSHIP")]' card_blueprint_database.json

# Defensive shields only
jq '[.[] | select(.cardCategory == "DEFENSIVE_SHIELD")]' card_blueprint_database.json
```

### Exclude Defensive Shields

```bash
# All cards except defensive shields
jq '[.[] | select(.cardCategory != "DEFENSIVE_SHIELD")]' card_blueprint_database.json

# Light side without defensive shields
jq '[.[] | select(.side == "LIGHT" and .cardCategory != "DEFENSIVE_SHIELD")]' card_blueprint_database.json

# Dark side without defensive shields
jq '[.[] | select(.side == "DARK" and .cardCategory != "DEFENSIVE_SHIELD")]' card_blueprint_database.json
```

## Combined Filters

### By Side and Category

```bash
# Light side characters
jq '[.[] | select(.side == "LIGHT" and .cardCategory == "CHARACTER")]' card_blueprint_database.json

# Dark side starships
jq '[.[] | select(.side == "DARK" and .cardCategory == "STARSHIP")]' card_blueprint_database.json

# Light side locations (no defensive shields)
jq '[.[] | select(.side == "LIGHT" and .cardCategory == "LOCATION")]' card_blueprint_database.json
```

### By Expansion Set

```bash
# Premiere set only
jq '[.[] | select(.expansionSet == "PREMIERE")]' card_blueprint_database.json

# Special Edition set only
jq '[.[] | select(.expansionSet == "SPECIAL_EDITION")]' card_blueprint_database.json

# Multiple sets (Premiere or Special Edition)
jq '[.[] | select(.expansionSet == "PREMIERE" or .expansionSet == "SPECIAL_EDITION")]' card_blueprint_database.json
```

### By Uniqueness

```bash
# Unique cards only
jq '[.[] | select(.uniqueness == "UNIQUE")]' card_blueprint_database.json

# Non-unique cards only
jq '[.[] | select(.uniqueness == "RESTRICTED_3")]' card_blueprint_database.json
```

## Analysis Commands

### Count Cards

```bash
# Total card count
jq 'length' card_blueprint_database.json

# Light side count
jq '[.[] | select(.side == "LIGHT")] | length' card_blueprint_database.json

# Dark side count
jq '[.[] | select(.side == "DARK")] | length' card_blueprint_database.json

# Defensive shields count
jq '[.[] | select(.cardCategory == "DEFENSIVE_SHIELD")] | length' card_blueprint_database.json

# Characters count
jq '[.[] | select(.cardCategory == "CHARACTER")] | length' card_blueprint_database.json
```

### Group and Count by Category

```bash
# Count by category
jq 'group_by(.cardCategory) | map({category: .[0].cardCategory, count: length})' card_blueprint_database.json

# Count by side
jq 'group_by(.side) | map({side: .[0].side, count: length})' card_blueprint_database.json

# Count by expansion set
jq 'group_by(.expansionSet) | map({set: .[0].expansionSet, count: length})' card_blueprint_database.json

# Count light side by category
jq '[.[] | select(.side == "LIGHT")] | group_by(.cardCategory) | map({category: .[0].cardCategory, count: length})' card_blueprint_database.json
```

### Find Duplicates

```bash
# Find duplicate slugs
jq -r '.[] | .slug' card_blueprint_database.json | sort | uniq -d

# Count duplicate slugs
jq -r '.[] | .slug' card_blueprint_database.json | sort | uniq -d | wc -l

# Find duplicate card titles
jq -r '.[] | .title' card_blueprint_database.json | sort | uniq -d
```

### Analyze Specific Slug

```bash
# Find all cards with specific slug
jq '.[] | select(.slug == "bespin")' card_blueprint_database.json

# Show just ID, title, side for specific slug
jq '.[] | select(.slug == "bespin") | {cardId, title, side, cardCategory}' card_blueprint_database.json

# Count how many cards share a slug
jq '[.[] | select(.slug == "bespin")] | length' card_blueprint_database.json
```

## Output Formatting

### Extract Specific Fields

```bash
# Card IDs only
jq -r '.[] | .cardId' card_blueprint_database.json

# Titles only
jq -r '.[] | .title' card_blueprint_database.json

# Slugs only
jq -r '.[] | .slug' card_blueprint_database.json

# ID and title pairs
jq -r '.[] | "\(.cardId): \(.title)"' card_blueprint_database.json

# CSV format: ID, title, side, category
jq -r '.[] | [.cardId, .title, .side, .cardCategory] | @csv' card_blueprint_database.json
```

### Pretty Print Specific Cards

```bash
# Show first 5 cards
jq '.[0:5]' card_blueprint_database.json

# Show specific card by ID
jq '.[] | select(.cardId == "101_2")' card_blueprint_database.json

# Show cards in range
jq '[.[] | select(.cardId | startswith("101_"))]' card_blueprint_database.json
```

## Search and Filter

### By Title Pattern

```bash
# Cards with "Luke" in title
jq '.[] | select(.title | contains("Luke"))' card_blueprint_database.json

# Cards starting with "Darth"
jq '.[] | select(.title | startswith("Darth"))' card_blueprint_database.json

# Case-insensitive search
jq '.[] | select(.title | ascii_downcase | contains("vader"))' card_blueprint_database.json
```

### By Icon

```bash
# Cards with specific icon
jq '.[] | select(.icons[] | .icon == "PILOT")' card_blueprint_database.json

# Cards with multiple icons
jq '.[] | select(.icons | length > 2)' card_blueprint_database.json

# Cards with no icons
jq '.[] | select(.icons | length == 0)' card_blueprint_database.json
```

### By Stats

```bash
# Characters with power 5 or higher
jq '.[] | select(.cardCategory == "CHARACTER" and .power >= 5)' card_blueprint_database.json

# Cards with destiny 7
jq '.[] | select(.destiny == 7)' card_blueprint_database.json

# Characters with ability 4+
jq '.[] | select(.cardCategory == "CHARACTER" and .ability >= 4)' card_blueprint_database.json
```

## Slug Collision Detection

### Find All Slug Collisions

```bash
# Find all slugs that appear multiple times
jq -r '.[] | .slug' card_blueprint_database.json | sort | uniq -d > duplicate_slugs.txt

# For each duplicate slug, show all cards
while read slug; do
  echo "=== Slug: $slug ==="
  jq ".[] | select(.slug == \"$slug\") | {cardId, title, side, cardCategory, expansionSet}" card_blueprint_database.json
  echo ""
done < duplicate_slugs.txt
```

### Light/Dark Collisions

```bash
# Find slugs that exist on both sides
jq 'group_by(.slug) | map(select(length > 1)) | map({slug: .[0].slug, sides: map(.side) | unique, cards: map({cardId, title, side})})' card_blueprint_database.json | jq '.[] | select(.sides | length > 1)'
```

### Defensive Shield Collisions

```bash
# Find defensive shields with same slug as regular cards
jq 'group_by(.slug) | map(select(length > 1)) | map({slug: .[0].slug, categories: map(.cardCategory) | unique, cards: map({cardId, title, cardCategory})})' card_blueprint_database.json | jq '.[] | select(.categories | contains(["DEFENSIVE_SHIELD"]))'
```

## Practical Filtering Workflows

### Create Side-Specific Databases

```bash
# Light side only (no defensive shields) - save to file
jq '[.[] | select(.side == "LIGHT" and .cardCategory != "DEFENSIVE_SHIELD")]' \
  card_blueprint_database.json \
  > card_blueprint_database_light.json

# Dark side only (no defensive shields) - save to file
jq '[.[] | select(.side == "DARK" and .cardCategory != "DEFENSIVE_SHIELD")]' \
  card_blueprint_database.json \
  > card_blueprint_database_dark.json
```

### Verify Filtered Databases

```bash
# Verify only LIGHT side in filtered database
jq '[.[] | .side] | unique' card_blueprint_database_light.json
# Should output: ["LIGHT"]

# Verify no defensive shields
jq '[.[] | select(.cardCategory == "DEFENSIVE_SHIELD")] | length' card_blueprint_database_light.json
# Should output: 0

# Count cards in filtered database
jq 'length' card_blueprint_database_light.json
```

### Create Custom Subsets

```bash
# Premiere light side characters only
jq '[.[] | select(.side == "LIGHT" and .cardCategory == "CHARACTER" and .expansionSet == "PREMIERE")]' \
  card_blueprint_database.json \
  > premiere_light_characters.json

# All unique starships
jq '[.[] | select(.cardCategory == "STARSHIP" and .uniqueness == "UNIQUE")]' \
  card_blueprint_database.json \
  > unique_starships.json

# All virtual cards
jq '[.[] | select(.expansionSet | startswith("VIRTUAL"))]' \
  card_blueprint_database.json \
  > virtual_cards.json
```

## Performance Tips

### Use Streaming for Large Files

For very large database files, use streaming:

```bash
# Count without loading entire file into memory
jq -c '.[]' card_blueprint_database.json | wc -l

# Extract field from each card without loading entire array
jq -c '.[] | {cardId, title}' card_blueprint_database.json > cards_compact.json
```

### Combine Operations

```bash
# Instead of multiple jq calls, combine them:
# SLOW:
jq '.[] | select(.side == "LIGHT")' db.json | jq 'select(.cardCategory == "CHARACTER")'

# FAST:
jq '.[] | select(.side == "LIGHT" and .cardCategory == "CHARACTER")' db.json
```

## Error Handling

### Check File Validity

```bash
# Validate JSON syntax
jq empty card_blueprint_database.json
# If no output, file is valid

# Check if it's an array
jq 'type' card_blueprint_database.json
# Should output: "array"

# Check if array is not empty
jq 'length > 0' card_blueprint_database.json
# Should output: true
```

### Debug Filter Issues

```bash
# Show first result to debug filter
jq '[.[] | select(.side == "LIGHT")] | .[0]' card_blueprint_database.json

# Count results before outputting
jq '[.[] | select(.side == "LIGHT")] | length' card_blueprint_database.json

# Show unique values for a field
jq '[.[] | .side] | unique' card_blueprint_database.json
```

## Common Pitfalls

### Forgetting to Wrap in Array

```bash
# WRONG - outputs separate objects
jq '.[] | select(.side == "LIGHT")' db.json

# CORRECT - outputs single array
jq '[.[] | select(.side == "LIGHT")]' db.json
```

### String vs Number Comparison

```bash
# If destiny is stored as string, this won't work:
jq '.[] | select(.destiny > 5)' db.json

# Need to convert to number:
jq '.[] | select(.destiny | tonumber > 5)' db.json
```

### Null Field Handling

```bash
# If field might be null, check first:
jq '.[] | select(.power != null and .power >= 5)' db.json

# Or use alternative operator:
jq '.[] | select((.power // 0) >= 5)' db.json
```
