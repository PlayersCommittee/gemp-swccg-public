# Common Cube Building Issues and Solutions

This reference document provides detailed troubleshooting information for common issues encountered when building cube drafts.

## Playtesting Cards (Set 501) Issues

### Problem: Playtesting cards appear in draft despite exclusion attempts

**Symptoms:**
- Cards with ID pattern `501_*` appear in cube configuration
- Players see playtesting cards during draft
- Card database contains set 501 cards

**Root Causes:**
1. Card database (`card_blueprint_database.json`) was generated before exclusion filter was added
2. Mapping scripts ran with old database
3. Cube configuration generated from contaminated mappings

**Complete Solution:**
```bash
# 1. Verify CardDataExporter has exclusion filter (line 128)
grep -A 2 "set501" src/gemp-swccg-server/src/main/java/com/gempukku/swccgo/util/CardDataExporter.java
# Should show: .filter(p -> !p.toString().contains("/set501/")...

# 2. Regenerate card database
./bin/gemp export-cards src/gemp-swccg-cards/src/main/resources/card_blueprint_database.json

# 3. Verify exclusion worked
grep -c '"cardId": "501_' src/gemp-swccg-cards/src/main/resources/card_blueprint_database.json
# Must output: 0

# 4. Re-map ALL CSV files
CUBE_DIR="src/gemp-swccg-server/src/main/resources/draft/cube_building/<name>"
./bin/map_card_names_to_ids $CUBE_DIR/default_cards.csv $CUBE_DIR/default_cards_mapped.json
./bin/map_card_names_to_ids $CUBE_DIR/cube_worlds_lightside.csv $CUBE_DIR/cube_worlds_lightside_mapped.json
./bin/map_card_names_to_ids $CUBE_DIR/cube_worlds_darkside.csv $CUBE_DIR/cube_worlds_darkside_mapped.json

# 5. Verify mappings are clean
grep -c '"501_' $CUBE_DIR/default_cards_mapped.json
# Must output: 0

# 6. Regenerate cube config
python3 bin/create_cube $CUBE_DIR src/gemp-swccg-server/src/main/resources/draft/<output>.json

# 7. Final verification
grep -c '"501_' src/gemp-swccg-server/src/main/resources/draft/<output>.json
# Must output: 0
```

**Prevention:**
- Always regenerate card database before starting cube work
- Add verification steps to workflow
- Keep card database generation command in project documentation

## Card Mapping Issues

### Problem: Multiple matches warning for same card

**Symptom:**
```
Row 1, Col N/A: Multiple cards found, using first match: 5_164
```

**Cause:**
Card name exists in multiple sets (e.g., "Bespin" appears in Premiere, Cloud City, Virtual sets)

**How Mapping Works:**
1. Slugifies card name: "Bespin" → "bespin"
2. Searches for exact slug match
3. If multiple found, searches with set name: "bespin-premiere", "bespin-cloud-city"
4. If still multiple, selects first match

**Solution:**
```bash
# 1. Check the log to see what was selected
cat $CUBE_DIR/default_cards_mapped_log.json | grep -A 5 "Bespin"

# 2. Verify in card database
grep -i '"slug": "bespin"' src/gemp-swccg-cards/src/main/resources/card_blueprint_database.json

# 3. If wrong card selected, update CSV with set identifier
# Change: "Bespin"
# To: "Bespin (Cloud City)"
# Or use: "Bespin (V)" for virtual version
```

**Common Cards with Multiple Versions:**
- Locations (often reprinted)
- Generic characters (Stormtrooper, Rebel Trooper)
- Equipment (Blaster Rifle, Lightsaber)
- Virtual vs non-virtual versions

### Problem: Missing cards in log

**Symptom:**
```
Row 13, Col 1: 'Coruscant (SE)' (slug: 'coruscant-se', reason: not_found)
```

**Common Causes:**
1. Card doesn't exist in game
2. Misspelled card name
3. Wrong set identifier format
4. Card is playtesting only (now excluded)

**Solution Steps:**
```bash
# 1. Search for similar cards
grep -i "coruscant" src/gemp-swccg-cards/src/main/resources/card_blueprint_database.json | grep title

# 2. Check exact slug
grep '"slug": "coruscant' src/gemp-swccg-cards/src/main/resources/card_blueprint_database.json

# 3. Try alternative names
# "Coruscant (SE)" might be "Coruscant: Special Edition" or just "Coruscant"

# 4. Check if it's a playtesting card
# If it was in old database but not new, it was set 501
```

**Set Identifier Formats:**
- `(V)` - Virtual
- `(EP1)` - Episode 1
- `(SE)` - Special Edition (may not exist as separate card)

### Problem: Low success rate (<95%)

**Symptoms:**
```
Success rate: 87.50%
Missing: 23
```

**Investigation:**
```bash
# Review missing cards in log
cat $CUBE_DIR/*_mapped_log.json | grep -A 5 '"missing_cards"'

# Common patterns:
# - Multiple cards with same issue (systematic problem)
# - Random individual cards (data entry errors)
```

**Solutions:**
- **Systematic issues**: Fix card name format, set identifiers
- **Random issues**: Correct typos in CSV, find actual card names
- **Bulk missing**: CSV may have wrong column structure or encoding

## CSV Structure Issues

### Problem: Add-on cards not creating nested arrays

**Symptom:**
Multi-column CSV produces flat array instead of nested arrays

**Cause:**
Script detects single column when other columns are ALL empty

**Detection Logic:**
```python
# Script counts non-empty columns
non_empty_columns = count_columns_with_data(csv)
is_single_column = non_empty_columns <= 1

# If only first column has data → flat array
# If 2+ columns have data → nested arrays
```

**Solution:**
Ensure at least one row uses multiple columns:
```csv
Card 1,,        # Empty columns don't count
Card 2,Add-on   # This row triggers multi-column mode
Card 3,,        # Now treated as multi-column with empty add-ons
```

### Problem: CSV encoding issues

**Symptoms:**
- Weird characters in card names
- Mapping fails for cards with special characters
- Success rate unexpectedly low

**Solution:**
```bash
# Check file encoding
file -I $CUBE_DIR/default_cards.csv
# Should show: text/plain; charset=utf-8

# Convert if needed
iconv -f ISO-8859-1 -t UTF-8 old.csv > new.csv

# Remove BOM if present
sed '1s/^\xEF\xBB\xBF//' old.csv > new.csv
```

## Configuration Issues

### Problem: Draft doesn't appear in dropdown

**Checklist:**
1. Format registered in `swccgDrafts.json`?
2. `type` field matches dropdown `value`?
3. `location` path correct (relative to resources)?
4. Frontend file updated (`leagueAdmin.html`)?
5. Server rebuilt after changes?
6. Browser cache cleared?

**Debug Steps:**
```bash
# 1. Verify registration
cat src/gemp-swccg-server/src/main/resources/swccgDrafts.json

# 2. Verify frontend
grep -A 2 "cube_worlds_2025_draft" src/gemp-swccg-async/src/main/web/includes/admin/leagueAdmin.html

# 3. Check file exists
ls -la src/gemp-swccg-server/src/main/resources/draft/cubeWorlds2025.json

# 4. Rebuild
./bin/gemp reload-fast

# 5. Hard refresh browser
# Cmd+Shift+R (Mac) or Ctrl+Shift+R (Windows/Linux)
```

### Problem: Division by zero when accessing draft

**Full Error:**
```
Error response for /gemp-swccg-server/soloDraft/1765815973945
java.lang.ArithmeticException: / by zero
```

**Location:** `DefaultSoloDraft.java:66`

**Cause:**
```java
// WRONG - causes division by zero when objChoiceCountPerSide is 0
if (offset % _objChoiceCountPerSide == 0) {
    return stage - offset;
}
```

**Fix:**
```java
// CORRECT - checks for non-zero before modulo
if (_objChoiceCountPerSide > 0 && offset % _objChoiceCountPerSide == 0) {
    return stage - offset;
}
```

**Why It Happens:**
- Standard cubes have `objChoiceCountPerSide: 0` (no objective picks)
- Modulo operation attempts division by zero
- Only affects non-objective cube drafts

**Verification:**
```bash
# Check if fix is present
grep -A 1 "offset % _objChoiceCountPerSide" src/gemp-swccg-server/src/main/java/com/gempukku/swccgo/draft2/DefaultSoloDraft.java

# Should show:
# if (_objChoiceCountPerSide > 0 && offset % _objChoiceCountPerSide == 0) {
```

## Pack Structure Issues

### Problem: Add-on cards not appearing in draft

**Symptoms:**
- Multi-column CSV mapped correctly (nested arrays in JSON)
- Cube config has nested arrays in packs
- But players only receive primary card, not add-ons

**Cause:**
Using wrong choice type (`cubePick` instead of `cubePackObjPick`)

**Solution:**
```json
// WRONG
{
  "type": "cubePick",
  "data": {
    "packs": [["primary", "addon"]]
  }
}

// CORRECT
{
  "type": "cubePackObjPick",
  "data": {
    "packs": [["primary", "addon"]]
  }
}
```

**Verification:**
```bash
# Check choice types in config
grep '"type":' src/gemp-swccg-server/src/main/resources/draft/cubeWorlds2025.json

# Should show: "cubePackObjPick" for all choices
```

### Problem: Pack sizes incorrect in draft

**Symptoms:**
- Draft shows wrong number of pack choices
- Not showing expected 9 packs per round

**Cause:**
`count` parameter incorrect in configuration

**Check:**
```bash
# Verify count parameter
grep -A 5 '"count":' src/gemp-swccg-server/src/main/resources/draft/cubeWorlds2025.json

# Should show: "count": 9
```

**Fix:**
Update `bin/create_cube` line 69:
```python
pack_choice_count = 9  # Number shown per round
```

Then regenerate cube config.

## Server Build Issues

### Problem: Changes not appearing after rebuild

**Symptoms:**
- Made changes to cube config
- Ran rebuild command
- Changes still not visible

**Common Causes:**
1. Changed wrong file (check file path)
2. Build cached old version
3. Browser cached old version
4. Server didn't restart properly

**Solution:**
```bash
# 1. Verify file was actually changed
ls -la src/gemp-swccg-server/src/main/resources/draft/cubeWorlds2025.json

# 2. Force clean rebuild
docker exec gemp_swccg_app_1 sh -c "cd gemp-swccg-server && mvn clean install -DskipTests"

# 3. Restart server (not just reload)
./bin/gemp stop
./bin/gemp start

# 4. Clear browser cache completely
# Or use incognito/private window
```

### Problem: Build fails with "file not found"

**Symptoms:**
```
Could not find file: /opt/gemp-swccg/src/gemp-swccg-server/...
```

**Cause:**
File path in Docker container differs from host

**Solution:**
Files must be in correct location relative to Docker mount:
```
Host: /Users/you/gemp-swccg/src/...
Container: /opt/gemp-swccg/src/...
```

Verify with:
```bash
docker exec gemp_swccg_app_1 ls -la /opt/gemp-swccg/src/gemp-swccg-server/src/main/resources/draft/
```

## Performance Issues

### Problem: Card mapping takes very long

**Symptoms:**
- Script runs for several minutes
- Processing seems stuck

**Common Causes:**
1. Very large CSV files (>1000 cards per file)
2. Card database very large
3. System resource constraints

**Solutions:**
```bash
# Check CSV size
wc -l $CUBE_DIR/*.csv

# Check database size
ls -lh src/gemp-swccg-cards/src/main/resources/card_blueprint_database.json

# If database is suspiciously large (>5MB), may contain duplicates
# Regenerate it:
./bin/gemp export-cards src/gemp-swccg-cards/src/main/resources/card_blueprint_database.json
```

Normal times:
- Mapping 200 cards: ~5 seconds
- Mapping 400 cards: ~10 seconds
- Database generation: ~2 minutes

## Data Validation Issues

### Problem: Duplicate cards in cube

**Detection:**
```bash
# Check for duplicate card IDs in config
cat src/gemp-swccg-server/src/main/resources/draft/cubeWorlds2025.json | \
  grep -o '"[0-9_]*"' | sort | uniq -d

# If output appears, those are duplicate card IDs
```

**Common Causes:**
1. Duplicate entries in source CSV
2. Same card with different naming matched to same ID
3. Copy-paste errors in CSV

**Solution:**
```bash
# Find duplicates in source CSV
sort $CUBE_DIR/default_cards.csv | uniq -d

# Remove duplicates and regenerate
```

### Problem: Missing expected cards in final cube

**Symptoms:**
- Card appears in CSV
- Mapping log shows success
- But card not in final cube config

**Investigation:**
```bash
# 1. Verify card in mapped JSON
grep "card_id_here" $CUBE_DIR/default_cards_mapped.json

# 2. Check cube config
grep "card_id_here" src/gemp-swccg-server/src/main/resources/draft/cubeWorlds2025.json

# 3. If in mapping but not config, regenerate cube
python3 bin/create_cube $CUBE_DIR <output>.json
```

## Git/Version Control Issues

### Problem: Merge conflicts in cube files

**Common Conflicts:**
- `swccgDrafts.json` - Multiple cubes added
- `leagueAdmin.html` - Dropdown options
- Cube config files modified in both branches

**Resolution for swccgDrafts.json:**
```bash
# Keep both entries, ensure valid JSON
git checkout --theirs swccgDrafts.json  # Or --ours
# Then manually merge entries
```

**Best Practice:**
- Keep cube source files (CSVs) in version control
- Generated files (mapped JSON, config) can be .gitignored
- Regenerate after merge instead of merging generated files
