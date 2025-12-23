package com.gempukku.swccgo.util;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility to export all card data from Java card implementations to JSON.
 *
 * This tool instantiates all card blueprints using SwccgCardBlueprintLibrary
 * and exports their properties to a JSON file for use in external tools.
 */
public class CardDataExporter {

    private static final String CARDS_BASE_PATH = "gemp-swccg-cards/src/main/java/com/gempukku/swccgo/cards";
    private static final Pattern CARD_FILE_PATTERN = Pattern.compile("Card(\\d+)_(\\d+)(?:_(.+))?\\.java$");

    public static void main(String[] args) {
        System.out.println("GEMP-SWCCG Card Data Exporter");
        System.out.println("=============================\n");

        String outputPath = args.length > 0 ? args[0] : "card_data.json";

        try {
            CardDataExporter exporter = new CardDataExporter();
            List<Map<String, Object>> cardData = exporter.exportAllCards();
            exporter.writeToJson(cardData, outputPath);

            System.out.println("\nExport complete!");
            System.out.println("Total cards exported: " + cardData.size());
            System.out.println("Output file: " + outputPath);

        } catch (Exception e) {
            System.err.println("Error during export: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Scans the cards directory and exports all card blueprints.
     */
    public List<Map<String, Object>> exportAllCards() throws IOException {
        SwccgCardBlueprintLibrary library = new SwccgCardBlueprintLibrary();
        List<String> cardIds = findAllCardIds();
        List<Map<String, Object>> allCardData = new ArrayList<>();

        System.out.println("Found " + cardIds.size() + " card files to process...\n");

        // First pass: determine which card titles need -v suffix
        System.out.println("Analyzing card versions...");
        Set<String> titlesNeedingVSuffix = determineTitlesNeedingVirtualSuffix(library, cardIds);
        System.out.println("Found " + titlesNeedingVSuffix.size() + " card titles with both virtual and non-virtual versions\n");

        int successCount = 0;
        int failCount = 0;

        for (String cardId : cardIds) {
            try {
                SwccgCardBlueprint blueprint = library.getSwccgoCardBlueprint(cardId);

                if (blueprint != null) {
                    Map<String, Object> cardDataMap = extractCardData(cardId, blueprint, titlesNeedingVSuffix);
                    allCardData.add(cardDataMap);
                    successCount++;

                    if (successCount % 500 == 0) {
                        System.out.println("Processed " + successCount + " cards...");
                    }
                } else {
                    System.err.println("WARNING: Failed to load blueprint for card ID: " + cardId);
                    failCount++;
                }

            } catch (Exception e) {
                System.err.println("ERROR processing card " + cardId + ": " + e.getMessage());
                failCount++;
            }
        }

        System.out.println("\nProcessing complete:");
        System.out.println("  Success: " + successCount);
        System.out.println("  Failed: " + failCount);

        return allCardData;
    }

    /**
     * Determines which card titles have both virtual (SET_X with rarity V) and non-virtual versions.
     */
    private Set<String> determineTitlesNeedingVirtualSuffix(SwccgCardBlueprintLibrary library, List<String> cardIds) {
        Map<String, Boolean> hasVirtualVersion = new HashMap<>();
        Map<String, Boolean> hasNonVirtualVersion = new HashMap<>();

        for (String cardId : cardIds) {
            try {
                SwccgCardBlueprint blueprint = library.getSwccgoCardBlueprint(cardId);
                if (blueprint == null) continue;

                String title = blueprint.getTitle();
                ExpansionSet expansionSet = blueprint.getExpansionSet();
                Rarity rarity = blueprint.getRarity();

                // Determine if this is a virtual card
                boolean isVirtualSet = expansionSet != null && expansionSet.name().startsWith("SET_");
                boolean isVirtual = isVirtualSet && (rarity != null && rarity == Rarity.V);

                // Determine if this is a non-virtual card (physical sets or Legacy reprints)
                boolean isNonVirtual = !isVirtualSet || (rarity != null && rarity != Rarity.V);

                if (isVirtual) {
                    hasVirtualVersion.put(title, true);
                }
                if (isNonVirtual) {
                    hasNonVirtualVersion.put(title, true);
                }
            } catch (Exception e) {
                // Skip cards that fail to load
            }
        }

        // Return titles that have BOTH virtual and non-virtual versions
        Set<String> needsSuffix = new HashSet<>();
        for (String title : hasVirtualVersion.keySet()) {
            if (hasNonVirtualVersion.containsKey(title)) {
                needsSuffix.add(title);
            }
        }

        return needsSuffix;
    }

    /**
     * Scans the file system to find all card Java files and extracts their IDs.
     */
    private List<String> findAllCardIds() throws IOException {
        List<String> cardIds = new ArrayList<>();

        // Find the src directory relative to current working directory
        String currentDir = System.getProperty("user.dir");
        Path cardsPath = Paths.get(currentDir, "src", CARDS_BASE_PATH);

        // If we're already in the src directory, adjust path
        if (!Files.exists(cardsPath)) {
            cardsPath = Paths.get(currentDir, CARDS_BASE_PATH);
        }

        // If still not found, try parent directory
        if (!Files.exists(cardsPath)) {
            cardsPath = Paths.get(currentDir, "..", "src", CARDS_BASE_PATH);
        }

        if (!Files.exists(cardsPath)) {
            throw new IOException("Could not find cards directory at: " + cardsPath);
        }

        System.out.println("Scanning cards directory: " + cardsPath);

        try (Stream<Path> paths = Files.walk(cardsPath)) {
            cardIds = paths
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".java"))
                // Exclude playtesting set (set501)
                .filter(p -> !p.toString().contains("/set501/") && !p.toString().contains("\\set501\\"))
                .map(p -> p.getFileName().toString())
                .map(this::extractCardId)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
        }

        return cardIds;
    }

    /**
     * Extracts card ID from filename (e.g., "Card1_045.java" -> "1_45")
     */
    private String extractCardId(String filename) {
        Matcher matcher = CARD_FILE_PATTERN.matcher(filename);
        if (matcher.find()) {
            String setNum = matcher.group(1);
            String cardNum = matcher.group(2);
            String suffix = matcher.group(3);

            // Remove leading zeros from card number
            int cardNumber = Integer.parseInt(cardNum);

            if (suffix != null && !suffix.isEmpty()) {
                return setNum + "_" + cardNumber + "_" + suffix;
            } else {
                return setNum + "_" + cardNumber;
            }
        }
        return null;
    }

    /**
     * Extracts all relevant data from a card blueprint.
     */
    private Map<String, Object> extractCardData(String cardId, SwccgCardBlueprint blueprint, Set<String> titlesNeedingVSuffix) {
        Map<String, Object> data = new LinkedHashMap<>();

        // Basic identification
        data.put("cardId", cardId);
        String title = blueprint.getTitle();
        data.put("title", title);

        // Generate base slug
        String baseSlug = slugify(title);

        // Get expansion set and rarity for determining virtual suffix
        ExpansionSet expansionSet = blueprint.getExpansionSet();
        Rarity rarity = blueprint.getRarity();

        // Only add -v suffix if this card title has both virtual and non-virtual versions
        // AND this specific card is the virtual version
        boolean isVirtualSet = expansionSet != null && expansionSet.name().startsWith("SET_");
        boolean isVirtual = isVirtualSet && (rarity != null && rarity == Rarity.V);
        boolean needsVSuffix = titlesNeedingVSuffix.contains(title) && isVirtual;
        String slugSuffix = needsVSuffix ? "-v" : "";

        data.put("slug", baseSlug + slugSuffix);

        // Get expansion set name for slugWithSetName
        if (expansionSet != null) {
            String setName = expansionSet.getHumanReadable();
            // Add -v before the set name
            data.put("slugWithSetName", baseSlug + slugSuffix + "-" + slugify(setName));
        } else {
            // Fallback to just the title slug if no expansion set
            data.put("slugWithSetName", baseSlug + slugSuffix);
        }

        data.put("side", safeToString(blueprint.getSide()));
        data.put("uniqueness", safeToString(blueprint.getUniqueness()));
        data.put("expansionSet", safeToString(expansionSet));
        data.put("rarity", safeToString(blueprint.getRarity()));

        // Card categories and types
        data.put("cardCategory", safeToString(blueprint.getCardCategory()));
        Set<CardType> cardTypes = blueprint.getCardTypes();
        if (cardTypes != null && !cardTypes.isEmpty()) {
            data.put("cardTypes", cardTypes.stream()
                .map(Object::toString)
                .collect(Collectors.toList()));
        }

        CardSubtype subtype = blueprint.getCardSubtype();
        if (subtype != null) {
            data.put("cardSubtype", subtype.toString());
        }

        // Text fields
        putIfNotNull(data, "lore", safeGetString(() -> blueprint.getLore()));
        putIfNotNull(data, "gameText", safeGetString(() -> blueprint.getGameText()));
        putIfNotNull(data, "testingText", safeGetString(() -> blueprint.getTestingText()));

        // Location-specific game text (only for locations)
        putIfNotNull(data, "locationDarkSideGameText", safeGetString(() -> blueprint.getLocationDarkSideGameText()));
        putIfNotNull(data, "locationLightSideGameText", safeGetString(() -> blueprint.getLocationLightSideGameText()));

        // Stats - only include if card has the attribute (wrapped safely)
        putIfHasAttribute(data, "deployCost", safeGetFloat(() -> blueprint.getDeployCost()));
        putIfHasAttribute(data, "destiny", safeGetFloat(() -> blueprint.getDestiny()));

        if (blueprint.hasPowerAttribute()) {
            data.put("power", blueprint.getPower());
        }
        if (blueprint.hasAbilityAttribute()) {
            data.put("ability", blueprint.getAbility());
        }
        if (blueprint.hasForfeitAttribute()) {
            data.put("forfeit", blueprint.getForfeit());
        }
        if (blueprint.hasArmorAttribute()) {
            data.put("armor", blueprint.getArmor());
        }
        if (blueprint.hasManeuverAttribute()) {
            data.put("maneuver", blueprint.getManeuver());
        }
        if (blueprint.hasHyperspeedAttribute()) {
            data.put("hyperspeed", blueprint.getHyperspeed());
        }
        if (blueprint.hasLandspeedAttribute()) {
            data.put("landspeed", blueprint.getLandspeed());
        }
        if (blueprint.hasPoliticsAttribute()) {
            data.put("politics", blueprint.getPolitics());
        }
        if (blueprint.hasSpecialDefenseValueAttribute()) {
            data.put("defenseValue", blueprint.getSpecialDefenseValue());
        }
        if (blueprint.hasFerocityAttribute()) {
            data.put("ferocity", blueprint.getFerocity());
        }

        // Capacity stats (safely - some cards throw exceptions)
        Integer pilotCapacity = safeGetInt(() -> blueprint.getPilotCapacity());
        if (pilotCapacity != null && pilotCapacity > 0) data.put("pilotCapacity", pilotCapacity);

        Integer passengerCapacity = safeGetInt(() -> blueprint.getPassengerCapacity());
        if (passengerCapacity != null && passengerCapacity > 0) data.put("passengerCapacity", passengerCapacity);

        Integer astromechCapacity = safeGetInt(() -> blueprint.getAstromechCapacity());
        if (astromechCapacity != null && astromechCapacity > 0) data.put("astromechCapacity", astromechCapacity);

        Integer vehicleCapacity = safeGetInt(() -> blueprint.getVehicleCapacity());
        if (vehicleCapacity != null && vehicleCapacity > 0) data.put("vehicleCapacity", vehicleCapacity);

        // Icons - iterate through all Icon enum values to find which ones this card has
        List<Map<String, Object>> icons = new ArrayList<>();
        for (Icon icon : Icon.values()) {
            int count = blueprint.getIconCount(icon);
            if (count > 0) {
                Map<String, Object> iconData = new HashMap<>();
                iconData.put("icon", icon.toString());
                iconData.put("count", count);
                icons.add(iconData);
            }
        }
        if (!icons.isEmpty()) {
            data.put("icons", icons);
        }

        // Keywords - iterate through all Keyword enum values
        List<String> keywords = new ArrayList<>();
        for (Keyword keyword : Keyword.values()) {
            if (blueprint.hasKeyword(keyword)) {
                keywords.add(keyword.toString());
            }
        }
        if (!keywords.isEmpty()) {
            data.put("keywords", keywords);
        }

        // Personas
        Set<Persona> personas = blueprint.getPersonas();
        if (personas != null && !personas.isEmpty()) {
            data.put("personas", personas.stream()
                .map(Object::toString)
                .collect(Collectors.toList()));
        }

        // Model Types
        List<ModelType> modelTypes = blueprint.getModelTypes();
        if (modelTypes != null && !modelTypes.isEmpty()) {
            data.put("modelTypes", modelTypes.stream()
                .map(Object::toString)
                .collect(Collectors.toList()));
        }

        // Species
        if (blueprint.hasSpeciesAttribute()) {
            Species species = blueprint.getSpecies();
            if (species != null) {
                data.put("species", species.toString());
            }
        }

        // Special flags
        if (blueprint.isComboCard()) {
            data.put("isComboCard", true);
        }
        if (blueprint.isFrontOfDoubleSidedCard()) {
            data.put("isFrontOfDoubleSidedCard", true);
        }
        if (blueprint.hasVirtualSuffix()) {
            data.put("hasVirtualSuffix", true);
        }
        if (blueprint.hasAlternateImageSuffix()) {
            data.put("hasAlternateImageSuffix", true);
        }
        if (blueprint.isDoesNotCountTowardDeckLimit()) {
            data.put("doesNotCountTowardDeckLimit", true);
        }
        if (blueprint.isMayNotBePlacedInReserveDeck()) {
            data.put("mayNotBePlacedInReserveDeck", true);
        }
        if (blueprint.isMovesLikeCharacter()) {
            data.put("movesLikeCharacter", true);
        }
        if (blueprint.isDeploysLikeStarfighter()) {
            data.put("deploysLikeStarfighter", true);
        }
        if (blueprint.isMovesLikeStarfighter()) {
            data.put("movesLikeStarfighter", true);
        }

        return data;
    }

    /**
     * Safely converts an object to string, handling nulls.
     */
    private String safeToString(Object obj) {
        return obj != null ? obj.toString() : null;
    }

    /**
     * Adds a key-value pair to the map only if the value is not null.
     */
    private void putIfNotNull(Map<String, Object> map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }

    /**
     * Adds a Float/Number attribute to the map only if it's not null.
     */
    private void putIfHasAttribute(Map<String, Object> map, String key, Float value) {
        if (value != null) {
            map.put(key, value);
        }
    }

    /**
     * Safely calls a supplier that returns a String, catching any exceptions.
     * Some card methods throw exceptions when called on incompatible card types.
     */
    private String safeGetString(java.util.function.Supplier<String> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Safely calls a supplier that returns an Integer, catching any exceptions.
     * Some card methods throw exceptions when called on incompatible card types.
     */
    private Integer safeGetInt(java.util.function.Supplier<Integer> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Safely calls a supplier that returns a Float, catching any exceptions.
     * Some card methods throw exceptions when called on incompatible card types.
     */
    private Float safeGetFloat(java.util.function.Supplier<Float> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Writes the card data to a JSON file.
     */
    private void writeToJson(List<Map<String, Object>> cardData, String outputPath) throws IOException {
        Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

        try (FileWriter writer = new FileWriter(outputPath)) {
            gson.toJson(cardData, writer);
        }

        // Calculate file size
        File outputFile = new File(outputPath);
        long fileSizeKB = outputFile.length() / 1024;
        System.out.println("Output file size: " + fileSizeKB + " KB");
    }

    /**
     * Converts a string to a URL-friendly slug.
     * Converts to lowercase, replaces spaces and special characters with hyphens,
     * and removes consecutive hyphens.
     *
     * @param text the text to slugify
     * @return the slugified text
     */
    private String slugify(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        return text
            .toLowerCase()
            // Replace apostrophes and quotes with empty string
            .replaceAll("['\"`]", "")
            // Replace spaces, underscores, and other non-alphanumeric characters with hyphens
            .replaceAll("[^a-z0-9]+", "-")
            // Remove leading/trailing hyphens
            .replaceAll("^-+|-+$", "");
    }
}
