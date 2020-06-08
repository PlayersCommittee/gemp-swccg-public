package com.gempukku.swccgo.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// The class represents a library of all the Star Wars CCG cards
// available in Gemp-Swccg.
//
public class SwccgCardBlueprintLibrary {
    private String[] _packageNames =
            new String[]{
                    ".light", ".dark"
            };
    private Map<String, SwccgCardBlueprint> _blueprintMap = new HashMap<String, SwccgCardBlueprint>();

    private Map<String, String> _blueprintMapping = new HashMap<String, String>();
    private Map<String, Set<String>> _fullBlueprintMapping = new HashMap<String, Set<String>>();

    public SwccgCardBlueprintLibrary() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(SwccgCardBlueprintLibrary.class.getResourceAsStream("/blueprintMapping.txt"), "UTF-8"));
            try {
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    if (!line.startsWith("#")) {
                        String[] split = line.split(",");
                        _blueprintMapping.put(split[0], split[1]);
                        addAlternatives(split[0], split[1]);
                    }
                }
            } finally {
                bufferedReader.close();
            }
        } catch (IOException exp) {
            throw new RuntimeException("Problem loading blueprint mapping", exp);
        }
    }

    public String getBaseBlueprintId(String blueprintId) {
        blueprintId = stripBlueprintModifiers(blueprintId);
        String base = _blueprintMapping.get(blueprintId);
        if (base != null)
            return base;
        return blueprintId;
    }

    private void addAlternatives(String newBlueprint, String existingBlueprint) {
        Set<String> existingAlternates = _fullBlueprintMapping.get(existingBlueprint);
        if (existingAlternates != null) {
            for (String existingAlternate : existingAlternates) {
                addAlternative(newBlueprint, existingAlternate);
                addAlternative(existingAlternate, newBlueprint);
            }
        }
        addAlternative(newBlueprint, existingBlueprint);
        addAlternative(existingBlueprint, newBlueprint);
    }

    private void addAlternative(String from, String to) {
        Set<String> list = _fullBlueprintMapping.get(from);
        if (list == null) {
            list = new HashSet<String>();
            _fullBlueprintMapping.put(from, list);
        }
        list.add(to);
    }

    public Set<String> getAllAlternates(String blueprintId) {
        return _fullBlueprintMapping.get(blueprintId);
    }

    public boolean hasAlternateInSet(String blueprintId, int setNo) {
        Set<String> alternatives = _fullBlueprintMapping.get(blueprintId);
        if (alternatives != null)
            for (String alternative : alternatives)
                if (alternative.startsWith(setNo + "_"))
                    return true;

        return false;
    }

    public SwccgCardBlueprint getSwccgoCardBlueprint(String blueprintId) {
        try {
            blueprintId = stripBlueprintModifiers(blueprintId);

            if (_blueprintMap.containsKey(blueprintId))
                return _blueprintMap.get(blueprintId);

            SwccgCardBlueprint blueprint = getBlueprint(blueprintId);
            _blueprintMap.put(blueprintId, blueprint);
            return blueprint;
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SwccgCardBlueprint getSwccgoCardBlueprintBack(String blueprintId) {
        try {
            blueprintId = stripBlueprintModifiers(blueprintId);

            if (!blueprintId.contains("_BACK")) {
                blueprintId = blueprintId + "_BACK";
            }

            if (_blueprintMap.containsKey(blueprintId))
                return _blueprintMap.get(blueprintId);

            SwccgCardBlueprint blueprint = getBlueprint(blueprintId);
            _blueprintMap.put(blueprintId, blueprint);
            return blueprint;
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String stripBlueprintModifiers(String blueprintId) {
        return blueprintId.replaceAll("\\*", "");
    }

    public boolean isFoil(String blueprintId) {
        return blueprintId.contains("*");
    }

    private SwccgCardBlueprint getBlueprint(String blueprintId) {
        if (_blueprintMapping.containsKey(blueprintId))
            return getBlueprint(_blueprintMapping.get(blueprintId));

        String[] blueprintParts = blueprintId.split("_");

        String setNumber = blueprintParts[0];
        String cardNumber = stripBlueprintModifiers(blueprintParts[1]);
        String backIndicator = blueprintParts.length > 2 ? blueprintParts[2] : null;

        for (String packageName : _packageNames) {
            SwccgCardBlueprint blueprint = null;
            try {
                blueprint = tryLoadingFromPackage(packageName, setNumber, cardNumber, backIndicator);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                throw new RuntimeException("Problem loading blueprint mapping for " + packageName + "," + setNumber + "," + cardNumber + "," + backIndicator);
            }
            if (blueprint != null)
                return blueprint;
        }

        return null;

        // TODO: throw new IllegalArgumentException("Didn't find card with blueprintId: " + blueprintId);
    }

    private SwccgCardBlueprint tryLoadingFromPackage(String packageName, String setNumber, String cardNumber, String backIndicator) throws IllegalAccessException, InstantiationException {
        try {
            String clazzName = "com.gempukku.swccgo.cards.set" + setNumber + packageName + ".Card" + setNumber + "_" + normalizeId(cardNumber);
            if (backIndicator != null && !backIndicator.isEmpty()) {
                clazzName = clazzName + "_" + backIndicator;
            }
            Class clazz = Class.forName(clazzName);
            return (SwccgCardBlueprint) clazz.newInstance();
        } catch (ClassNotFoundException e) {
            // Ignore
            return null;
        }
    }

    private String normalizeId(String blueprintPart) {
        int id = Integer.parseInt(blueprintPart);
        if (id < 10)
            return "00" + id;
        else if (id < 100)
            return "0" + id;
        else
            return String.valueOf(id);
    }
}
