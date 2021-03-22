package com.gempukku.swccgo.game;

import com.gempukku.swccgo.cards.packs.SetRarity;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.util.MultipleComparator;

import java.util.*;

public class SortAndFilterCards {
    public <T extends CardItem> List<T> process(String filter, Collection<T> items, SwccgCardBlueprintLibrary cardLibrary, SwccgoFormatLibrary formatLibrary, Map<String, SetRarity> rarities) {
        if (filter == null) {
            filter = "";
        }
        String[] filterParams = filter.split(" ");

        String product = getProductFilter(filterParams);

        Side side = getSideFilter(filterParams);
        String formatFilter = getFormatFilter(filterParams);
        CardCategory cardCategory = getCardCategoryFilter(filterParams);
        CardType cardType = getCardTypeFilter(filterParams);
        CardSubtype cardSubtype = getCardSubtypeFilter(filterParams);
        String[] sets = getSetFilter(filterParams);
        Set<Rarity> rarity = getRarityFilter(filterParams);

        List<String> titleWords = getTitleWords(filterParams);
        List<String> loreWords = getLoreWords(filterParams);
        List<String> gametextWords = getGametextWords(filterParams);
        Set<Icon> icons = getEnumFilter(Icon.values(), Icon.class, "icon", Collections.<Icon>emptySet(), filterParams);
        Set<Persona> personas = getEnumFilter(Persona.values(), Persona.class, "persona", Collections.<Persona>emptySet(), filterParams);

        List<T> result = new ArrayList<T>();

        for (T item : items) {
            String blueprintId = item.getBlueprintId();

            // Determine if the item matches the filters
            if (acceptsFilters(cardLibrary, formatLibrary, rarities, blueprintId, side, product, rarity, formatFilter, sets, cardCategory, cardType, cardSubtype, titleWords, loreWords, gametextWords, icons, personas, filterParams))
                result.add(item);
        }

        String sort = getSort(filterParams);
        if (sort == null || sort.isEmpty()) {
            sort = "name";
        }

        final String[] sortSplit = sort.split(",");

        MultipleComparator<CardItem> comparators = new MultipleComparator<CardItem>();
        for (String oneSort : sortSplit) {
            if ("name".equals(oneSort))
                comparators.addComparator(new PacksFirstComparator(new NameComparator(cardLibrary)));
            if ("set".equals(oneSort))
                comparators.addComparator(new PacksFirstComparator(new SetComparator()));
            if ("cardType".equals(oneSort))
                comparators.addComparator(new PacksFirstComparator(new CardTypeComparator(cardLibrary)));
            if ("cardCategory".equals(oneSort))
                comparators.addComparator(new PacksFirstComparator(new CardCategoryComparator(cardLibrary)));
        }

        Collections.sort(result, comparators);

        return result;
    }

    private boolean acceptsFilters(
            SwccgCardBlueprintLibrary library, SwccgoFormatLibrary formatLibrary, Map<String, SetRarity> rarities, String blueprintId, Side side, String product, Set<Rarity> rarity, String format,
            String[] sets, CardCategory cardCategory, CardType cardType, CardSubtype cardSubtype, List<String> titleWords, List<String> loreWords, List<String> gametextWords, Set<Icon> icons,
            Set<Persona> personas, String[] filterParams) {
        if (isPack(blueprintId)) {
            if (product == null || "pack".equals(product))
                return true;
        } else {
            if (product == null
                    || "card".equals(product)
                    || ("foil".equals(product) && isFoil(blueprintId))
                    || ("nonFoil".equals(product) && !isFoil(blueprintId))) {
                try {
                    String blueprintIdToCheck = blueprintId;

                    while (blueprintIdToCheck != null) {
                        SwccgCardBlueprint blueprint = library.getSwccgoCardBlueprint(blueprintIdToCheck);
                        if (blueprint != null && !isAlwaysExcluded(blueprintId, library))
                            if (side == null || blueprint.getSide() == side)
                                if (rarity == null || isRarity(blueprintId, rarity, library, rarities))
                                    if (format == null || (format.equals("all") && !isLegacy(blueprintId, library)) || isInFormat(blueprintId, format, formatLibrary))
                                        if (sets == null || isInSets(blueprintId, sets, library, formatLibrary))
                                            if (cardCategory == null || blueprint.getCardCategory() == cardCategory)
                                                if (cardType == null || blueprint.isCardType(cardType))
                                                    if (cardSubtype == null || blueprint.getCardSubtype() == cardSubtype)
                                                        if (containsAllWordsInTitle(blueprint, titleWords))
                                                            if (containsAllWordsInLore(blueprint, loreWords))
                                                                if (containsAllWordsInGameText(blueprint, gametextWords))
                                                                    if (containsAllIcons(blueprint, icons))
                                                                        if (containsAllPersonas(blueprint, personas))
                                                                            if (acceptedByDestinyFilters(blueprint, filterParams))
                                                                                if (acceptedByPowerFilters(blueprint, filterParams))
                                                                                    if (acceptedByAbilityFilters(blueprint, filterParams))
                                                                                        if (acceptedByDeployFilters(blueprint, filterParams))
                                                                                            if (acceptedByForfeitFilters(blueprint, filterParams))
                                                                                                if (acceptedByArmorFilters(blueprint, filterParams))
                                                                                                    if (acceptedByDefenseValueFilters(blueprint, filterParams))
                                                                                                        if (acceptedByManeuverFilters(blueprint, filterParams))
                                                                                                            if (acceptedByHyperspeedFilters(blueprint, filterParams))
                                                                                                                if (acceptedByLandspeedFilters(blueprint, filterParams))
                                                                                                                    return true;

                        // Determine if blueprint for other side of card needs to be checked
                        if (blueprint == null || !blueprint.isFrontOfDoubleSidedCard() || blueprintIdToCheck.contains("_BACK"))
                            blueprintIdToCheck = null;
                        else
                            blueprintIdToCheck = blueprintIdToCheck + "_BACK";
                    }
                }
                catch (IllegalArgumentException ex) {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Gets the side to filter based on the filter params.
     * @param filterParams the filter params
     * @return the side, or null if no filtering based on side
     */
    private Side getSideFilter(String[] filterParams) {
        for (String filterParam : filterParams) {
            if (filterParam.startsWith("side:"))
                return Side.valueOf(filterParam.substring("side:".length()));
        }
        return null;
    }

    /**
     * Gets the card category to filter based on the filter params.
     * @param filterParams the filter params
     * @return the card category, or null if no filtering based on card category
     */
    private CardCategory getCardCategoryFilter(String[] filterParams) {
        for (String filterParam : filterParams) {
            if (filterParam.startsWith("cardType:")) {
                String filterValue = filterParam.substring("cardType:".length());
                if (filterValue.startsWith("ADMIRALS_ORDER")) {
                    return CardCategory.ADMIRALS_ORDER;
                }
                if (filterValue.startsWith("CHARACTER")) {
                    return CardCategory.CHARACTER;
                }
                if (filterValue.startsWith("CREATURE")) {
                    return CardCategory.CREATURE;
                }
                if (filterValue.startsWith("DEFENSIVE_SHIELD")) {
                    return CardCategory.DEFENSIVE_SHIELD;
                }
                if (filterValue.startsWith("DEVICE")) {
                    return CardCategory.DEVICE;
                }
                if (filterValue.startsWith("EFFECT")) {
                    return CardCategory.EFFECT;
                }
                if (filterValue.startsWith("EPIC_EVENT")) {
                    return CardCategory.EPIC_EVENT;
                }
                if (filterValue.startsWith("GAME_AID")) {
                    return CardCategory.GAME_AID;
                }
                if (filterValue.startsWith("INTERRUPT")) {
                    return CardCategory.INTERRUPT;
                }
                if (filterValue.startsWith("JEDI_TEST")) {
                    return CardCategory.JEDI_TEST;
                }
                if (filterValue.startsWith("LOCATION")) {
                    return CardCategory.LOCATION;
                }
                if (filterValue.startsWith("OBJECTIVE")) {
                    return CardCategory.OBJECTIVE;
                }
                if (filterValue.startsWith("PODRACER")) {
                    return CardCategory.PODRACER;
                }
                if (filterValue.startsWith("STARSHIP")) {
                    return CardCategory.STARSHIP;
                }
                if (filterValue.startsWith("VEHICLE")) {
                    return CardCategory.VEHICLE;
                }
                if (filterValue.startsWith("WEAPON")) {
                    return CardCategory.WEAPON;
                }
            }
        }
        return null;
    }

    /**
     * Gets the card type to filter based on the filter params.
     * @param filterParams the filter params
     * @return the card type, or null if no filtering based on card type
     */
    private CardType getCardTypeFilter(String[] filterParams) {
        for (String filterParam : filterParams) {
            if (filterParam.startsWith("cardType:")) {
                String filterValue = filterParam.substring("cardType:".length());
                if (filterValue.startsWith("ADMIRALS_ORDER")) {
                    return CardType.ADMIRALS_ORDER;
                }
                if (filterValue.startsWith("CHARACTER_ALIEN")) {
                    return CardType.ALIEN;
                }
                if (filterValue.startsWith("CHARACTER_DARK_JEDI_MASTER")) {
                    return CardType.DARK_JEDI_MASTER;
                }
                if (filterValue.startsWith("CHARACTER_DROID")) {
                    return CardType.DROID;
                }
                if (filterValue.startsWith("CHARACTER_FIRST_ORDER")) {
                    return CardType.FIRST_ORDER;
                }
                if (filterValue.startsWith("CHARACTER_IMPERIAL")) {
                    return CardType.IMPERIAL;
                }
                if (filterValue.startsWith("CHARACTER_JEDI_MASTER")) {
                    return CardType.JEDI_MASTER;
                }
                if (filterValue.startsWith("CHARACTER_REBEL")) {
                    return CardType.REBEL;
                }
                if (filterValue.startsWith("CHARACTER_REPUBLIC")) {
                    return CardType.REPUBLIC;
                }
                if (filterValue.startsWith("CHARACTER_RESISTANCE")) {
                    return CardType.RESISTANCE;
                }
                if (filterValue.startsWith("CHARACTER_SITH")) {
                    return CardType.SITH;
                }
                if (filterValue.startsWith("CREATURE")) {
                    return CardType.CREATURE;
                }
                if (filterValue.startsWith("DEFENSIVE_SHIELD")) {
                    return CardType.DEFENSIVE_SHIELD;
                }
                if (filterValue.startsWith("DEVICE")) {
                    return CardType.DEVICE;
                }
                if (filterValue.startsWith("EFFECT")) {
                    return CardType.EFFECT;
                }
                if (filterValue.startsWith("EPIC_EVENT")) {
                    return CardType.EPIC_EVENT;
                }
                if (filterValue.startsWith("INTERRUPT")) {
                    return CardType.INTERRUPT;
                }
                if (filterValue.startsWith("JEDI_TEST")) {
                    return CardType.JEDI_TEST;
                }
                if (filterValue.startsWith("LOCATION")) {
                    return CardType.LOCATION;
                }
                if (filterValue.startsWith("OBJECTIVE")) {
                    return CardType.OBJECTIVE;
                }
                if (filterValue.startsWith("PODRACER")) {
                    return CardType.PODRACER;
                }
                if (filterValue.startsWith("STARSHIP")) {
                    return CardType.STARSHIP;
                }
                if (filterValue.startsWith("VEHICLE")) {
                    return CardType.VEHICLE;
                }
                if (filterValue.startsWith("WEAPON")) {
                    return CardType.WEAPON;
                }
            }
        }
        return null;
    }

    /**
     * Gets the card subtype to filter based on the filter params.
     * @param filterParams the filter params
     * @return the card subtype, or null if no filtering based on card type
     */
    private CardSubtype getCardSubtypeFilter(String[] filterParams) {
        for (String filterParam : filterParams) {
            if (filterParam.startsWith("cardType:")) {
                String filterValue = filterParam.substring("cardType:".length());
                if (filterValue.startsWith("EFFECT_NO_SUBTYPE")) {
                    return CardSubtype._;
                }
                if (filterValue.startsWith("EFFECT_IMMEDIATE")) {
                    return CardSubtype.IMMEDIATE;
                }
                if (filterValue.startsWith("EFFECT_MOBILE")) {
                    return CardSubtype.MOBILE;
                }
                if (filterValue.startsWith("EFFECT_POLITICAL")) {
                    return CardSubtype.POLITICAL;
                }
                if (filterValue.startsWith("EFFECT_STARTING")) {
                    return CardSubtype.STARTING;
                }
                if (filterValue.startsWith("EFFECT_UTINNI")) {
                    return CardSubtype.UTINNI;
                }
                if (filterValue.startsWith("INTERRUPT_LOST") && !filterValue.startsWith("INTERRUPT_LOST_OR_STARTING")) {
                    return CardSubtype.LOST;
                }
                if (filterValue.startsWith("INTERRUPT_LOST_OR_STARTING")) {
                    return CardSubtype.LOST_OR_STARTING;
                }
                if (filterValue.startsWith("INTERRUPT_USED") && !filterValue.startsWith("INTERRUPT_USED_OR_LOST") && !filterValue.startsWith("INTERRUPT_USED_OR_STARTING")) {
                    return CardSubtype.USED;
                }
                if (filterValue.startsWith("INTERRUPT_USED_OR_LOST")) {
                    return CardSubtype.USED_OR_LOST;
                }
                if (filterValue.startsWith("INTERRUPT_USED_OR_STARTING")) {
                    return CardSubtype.USED_OR_STARTING;
                }
                if (filterValue.startsWith("INTERRUPT_STARTING")) {
                    return CardSubtype.STARTING;
                }
                if (filterValue.startsWith("LOCATION_SECTOR")) {
                    return CardSubtype.SECTOR;
                }
                if (filterValue.startsWith("LOCATION_SITE")) {
                    return CardSubtype.SITE;
                }
                if (filterValue.startsWith("LOCATION_SYSTEM")) {
                    return CardSubtype.SYSTEM;
                }
                if (filterValue.startsWith("STARSHIP_CAPITAL")) {
                    return CardSubtype.CAPITAL;
                }
                if (filterValue.startsWith("STARSHIP_SQUADRON")) {
                    return CardSubtype.SQUADRON;
                }
                if (filterValue.startsWith("STARSHIP_STARFIGHTER")) {
                    return CardSubtype.STARFIGHTER;
                }
                if (filterValue.startsWith("VEHICLE_COMBAT")) {
                    return CardSubtype.COMBAT;
                }
                if (filterValue.startsWith("VEHICLE_CREATURE")) {
                    return CardSubtype.CREATURE;
                }
                if (filterValue.startsWith("VEHICLE_SHUTTLE")) {
                    return CardSubtype.SHUTTLE;
                }
                if (filterValue.startsWith("VEHICLE_TRANSPORT")) {
                    return CardSubtype.TRANSPORT;
                }
                if (filterValue.startsWith("VEHICLE_TRANSPORT")) {
                    return CardSubtype.TRANSPORT;
                }
                if (filterValue.startsWith("WEAPON_ARTILLERY")) {
                    return CardSubtype.ARTILLERY;
                }
                if (filterValue.startsWith("WEAPON_AUTOMATED")) {
                    return CardSubtype.AUTOMATED;
                }
                if (filterValue.startsWith("WEAPON_CHARACTER")) {
                    return CardSubtype.CHARACTER;
                }
                if (filterValue.startsWith("WEAPON_DEATH_STAR") && !filterValue.startsWith("WEAPON_DEATH_STAR_II")) {
                    return CardSubtype.DEATH_STAR;
                }
                if (filterValue.startsWith("WEAPON_DEATH_STAR_II")) {
                    return CardSubtype.DEATH_STAR_II;
                }
                if (filterValue.startsWith("WEAPON_STARSHIP")) {
                    return CardSubtype.STARSHIP;
                }
                if (filterValue.startsWith("WEAPON_VEHICLE")) {
                    return CardSubtype.VEHICLE;
                }
            }
        }
        return null;
    }

    /**
     * Gets the format to filter based on the filter params.
     * @param filterParams the filter params
     * @return the format, or null if no filtering based on format
     */
    private String getFormatFilter(String[] filterParams) {
        for (String filterParam : filterParams) {
            if (filterParam.startsWith("format:"))
                return filterParam.substring("format:".length());
        }
        return null;
    }

    /**
     * Gets the sets to filter based on the filter params.
     * @param filterParams the filter params
     * @return the sets, or null if no filtering based on sets
     */
    private String[] getSetFilter(String[] filterParams) {
        String setStr = getSetNumber(filterParams);
        String[] sets = null;
        if (setStr != null)
            sets = setStr.split(",");
        return sets;
    }

    /**
     * Gets the list of words in title to filter based on the filter params.
     * @param filterParams the filter params
     * @return list of words, or empty list if no filtering based on title
     */
    private List<String> getTitleWords(String[] filterParams) {
        List<String> result = new LinkedList<String>();
        for (String filterParam : filterParams) {
            if (filterParam.startsWith("name:"))
                result.add(filterParam.substring("name:".length()));
        }
        return result;
    }

    /**
     * Gets the list of words in lore to filter based on the filter params.
     * @param filterParams the filter params
     * @return list of words, or empty list if no filtering based on lore
     */
    private List<String> getLoreWords(String[] filterParams) {
        List<String> result = new LinkedList<String>();
        for (String filterParam : filterParams) {
            if (filterParam.startsWith("lore:"))
                result.add(filterParam.substring("lore:".length()));
        }
        return result;
    }

    /**
     * Gets the list of words in gametext to filter based on the filter params.
     * @param filterParams the filter params
     * @return list of words, or empty list if no filtering based on gametext
     */
    private List<String> getGametextWords(String[] filterParams) {
        List<String> result = new LinkedList<String>();
        for (String filterParam : filterParams) {
            if (filterParam.startsWith("gametext:"))
                result.add(filterParam.substring("gametext:".length()));
        }
        return result;
    }

    /**
     * Gets the item type (pack, card, etc.) to filter based on the filter params.
     * @param filterParams the filter params
     * @return the item type to filter, or null if no filtering based on item type
     */
    private String getProductFilter(String[] filterParams) {
        for (String filterParam : filterParams) {
            if (filterParam.startsWith("product:"))
                return filterParam.substring("product:".length());
        }
        return null;
    }

    /**
     * Gets the rarity value to filter based on the filter params.
     * @param filterParams the filter params
     * @return the rarity to filter, or null if no filtering based on rarity
     */
    private Set<Rarity> getRarityFilter(String[] filterParams) {
        Set<Rarity> rarities = new HashSet<Rarity>();
        for (String filterParam : filterParams) {
            if (filterParam.startsWith("rarity:")) {
                String rarityParam = filterParam.substring("rarity:".length());
                switch(rarityParam) {
                    case "":
                        return null;
                    case "C_ALL":
                        rarities.add(Rarity.getRarityFromString("C"));
                        rarities.add(Rarity.getRarityFromString("C1"));
                        rarities.add(Rarity.getRarityFromString("C2"));
                        rarities.add(Rarity.getRarityFromString("C3"));
                        break;
                    case "U_ALL":
                        rarities.add(Rarity.getRarityFromString("U"));
                        rarities.add(Rarity.getRarityFromString("U1"));
                        rarities.add(Rarity.getRarityFromString("U2"));
                        break;
                    case "R_ALL":
                        rarities.add(Rarity.getRarityFromString("R"));
                        rarities.add(Rarity.getRarityFromString("R1"));
                        rarities.add(Rarity.getRarityFromString("R2"));
                        break;
                    default:
                        rarities.add(Rarity.getRarityFromString(rarityParam));
                        break;
                }
            }
        }
        return rarities;
    }

    /**
     * Determines if the card has the specified rarity.
     * @param blueprintId the blueprint id of the card
     * @param rarity the rarity
     * @param library the blueprint library
     * @param rarities the blueprint library
     * @return true or false
     */
    private boolean isRarity(String blueprintId, Set<Rarity> rarity, SwccgCardBlueprintLibrary library, Map<String, SetRarity> rarities) {
        if (rarity.isEmpty())
            return true;
        if (blueprintId.contains("_")) {
            SetRarity setRarity = rarities.get(blueprintId.substring(0, blueprintId.indexOf("_")));
            try {
                if (setRarity != null && rarity != null && rarity.contains(setRarity.getCardRarity(library.stripBlueprintModifiers(blueprintId))))
                    return true;
            }
            catch (NullPointerException e) {
                throw new NullPointerException("blueprintId: " + blueprintId);
            }
            return false;
        }
        return true;
    }

    /**
     * Determines if the card is valid in the specified format.
     * @param blueprintId the blueprint id of the card
     * @param formatCode the format code
     * @param formatLibrary the format library
     * @return true or false
     */
    private boolean isInFormat(String blueprintId, String formatCode, SwccgoFormatLibrary formatLibrary) {
        SwccgFormat format = formatLibrary.getFormat(formatCode);
        if (format != null) {
            try {
                format.validateCard(blueprintId, false);
                return true;
            } catch (DeckInvalidException exp) {
                return false;
            }
        }
        return false;
    }

    private boolean isInSets(String blueprintId, String[] sets, SwccgCardBlueprintLibrary library, SwccgoFormatLibrary formatLibrary) {
        for (String set : sets) {
            SwccgFormat format = formatLibrary.getFormat(set);
            if (format != null) {
                try {
                    format.validateCard(blueprintId, false);
                    return true;
                } catch (DeckInvalidException exp) {
                    return false;
                }
            } else {
                if (blueprintId.startsWith(set + "_") || library.hasAlternateInSet(blueprintId, Integer.parseInt(set)))
                    return true;
            }
        }

        return false;
    }

    /**
     * Gets the set number string from the filter params.
     * @param filterParams the filter params
     * @return the set number string
     */
    private String getSetNumber(String[] filterParams) {
        for (String filterParam : filterParams) {
            if (filterParam.startsWith("set:"))
                return filterParam.substring("set:".length());
        }
        return null;
    }

    /**
     * Gets the sorting to use based on the filter params.
     * @param filterParams the filter params
     * @return the sorting to use, or null if no sorting
     */
    private String getSort(String[] filterParams) {
        for (String filterParam : filterParams) {
            if (filterParam.startsWith("sort:"))
                return filterParam.substring("sort:".length());
        }
        return null;
    }

    /**
     * Determines if the card contains all the specified icons.
     * @param blueprint the card blueprint
     * @param icons the icons
     * @return true or false
     */
    private boolean containsAllIcons(SwccgCardBlueprint blueprint, Set<Icon> icons) {
        for (Icon icon : icons) {
            if (blueprint == null || !blueprint.hasIcon(icon))
                return false;
        }
        return true;
    }

    /**
     * Determines if the card contains all the specified personas.
     * @param blueprint the card blueprint
     * @param personas the personas
     * @return true or false
     */
    private boolean containsAllPersonas(SwccgCardBlueprint blueprint, Set<Persona> personas) {
        if (blueprint == null)
            return false;

        SwccgBuiltInCardBlueprint permanentWeapon = blueprint.getPermanentWeapon(null);
        List<SwccgBuiltInCardBlueprint> permanentsAboard = blueprint.getPermanentsAboard(null);

        for (Persona persona : personas) {
            if (blueprint.hasPersona(persona))
                continue;

            if (permanentWeapon != null && permanentWeapon.hasPersona(null, persona))
                continue;

            boolean foundMatch = false;
            for (SwccgBuiltInCardBlueprint permanentAboard : permanentsAboard) {
                if (permanentAboard.hasPersona(null, persona)) {
                    foundMatch = true;
                    break;
                }
            }
            if (foundMatch)
                continue;

            return false;
        }
        return true;
    }

    /**
     * Determines if the card contains all the specified words in its title.
     * @param blueprint the card blueprint
     * @param words the words
     * @return true or false
     */
    private boolean containsAllWordsInTitle(SwccgCardBlueprint blueprint, List<String> words) {
        if (blueprint == null)
            return false;

        for (String word : words) {
            if (!GameUtils.getFullName(blueprint).toLowerCase().contains(word.toLowerCase())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if the card contains all the specified words in its lore.
     * @param blueprint the card blueprint
     * @param words the words
     * @return true or false
     */
    private boolean containsAllWordsInLore(SwccgCardBlueprint blueprint, List<String> words) {
        if (blueprint == null)
            return false;

        for (String word : words) {
            if (blueprint.getLore() == null || !blueprint.getLore().toLowerCase().contains(word.toLowerCase()))
                return false;
        }
        return true;
    }

    /**
     * Determines if the card contains all the specified words in its game text.
     * @param blueprint the card blueprint
     * @param words the words
     * @return true or false
     */
    private boolean containsAllWordsInGameText(SwccgCardBlueprint blueprint, List<String> words) {
        if (blueprint == null)
            return false;

        for (String word : words) {
            if (blueprint.getCardCategory() == CardCategory.LOCATION) {
                if ((blueprint.getLocationDarkSideGameText() == null || !blueprint.getLocationDarkSideGameText().toLowerCase().contains(word.toLowerCase()))
                        && (blueprint.getLocationLightSideGameText() == null || !blueprint.getLocationLightSideGameText().toLowerCase().contains(word.toLowerCase())))
                    return false;
            }
            else {
                if (blueprint.getGameText() == null || !blueprint.getGameText().toLowerCase().contains(word.toLowerCase()))
                    return false;
            }
        }
        return true;
    }

    /**
     * Gets the enum values to filter on based when the input filter are identified by the value of the enum.
     * @param enumValues the enum values
     * @param enumType the enum type
     * @param prefix the input filter prefix
     * @param defaultResult the default result
     * @param filterParams the input filter params
     * @return a set of enums of the enum type
     */
    private <T extends Enum> Set<T> getEnumFilter(T[] enumValues, Class<T> enumType, String prefix, Set<T> defaultResult, String[] filterParams) {
        for (String filterParam : filterParams) {
            if (filterParam.startsWith(prefix + ":")) {
                String values = filterParam.substring((prefix + ":").length());
                if (values.startsWith("-")) {
                    values = values.substring(1);
                    Set<T> cardTypes = new HashSet<T>(Arrays.asList(enumValues));
                    for (String v : values.split(",")) {
                        T t = (T) Enum.valueOf(enumType, v);
                        if (t != null)
                            cardTypes.remove((T) t);
                    }
                    return cardTypes;
                } else {
                    Set<T> cardTypes = new HashSet<T>();
                    for (String v : values.split(","))
                        try {
                            cardTypes.add((T) Enum.valueOf(enumType, v));
                        } catch (Exception e) {
                            // Just catch exception if enum is not found
                        }
                    return cardTypes;
                }
            }
        }
        return defaultResult;
    }

    /**
     * Determines if the card is accepted by the destiny filters
     * @param blueprint the card blueprint
     * @param filterParams the filter params
     * @return true or false
     */
    private boolean acceptedByDestinyFilters(SwccgCardBlueprint blueprint, String[] filterParams) {
        if (blueprint == null)
            return false;

        String destinyCompare = null;
        String destiny = null;

        for (String filterParam : filterParams) {
            if (filterParam.startsWith("destinyCompare:"))
                destinyCompare = filterParam.substring("destinyCompare:".length());
            else if (filterParam.startsWith("destiny:"))
                destiny = filterParam.substring("destiny:".length());
        }

        if (destinyCompare == null || destiny == null)
            return true;

        Float destinyAsFloat = Float.parseFloat(destiny);
        Float blueprintDestiny = blueprint.getDestiny();
        Float blueprintDestiny2 = blueprint.getAlternateDestiny();

        return (isAttributeValueAccepted(destinyCompare, destinyAsFloat, blueprintDestiny)
                || isAttributeValueAccepted(destinyCompare, destinyAsFloat, blueprintDestiny2));
    }

    /**
     * Determines if the card is accepted by the power filters
     * @param blueprint the card blueprint
     * @param filterParams the filter params
     * @return true or false
     */
    private boolean acceptedByPowerFilters(SwccgCardBlueprint blueprint, String[] filterParams) {
        if (blueprint == null)
            return false;

        String powerCompare = null;
        String power = null;

        for (String filterParam : filterParams) {
            if (filterParam.startsWith("powerCompare:"))
                powerCompare = filterParam.substring("powerCompare:".length());
            else if (filterParam.startsWith("power:"))
                power = filterParam.substring("power:".length());
        }

        if (powerCompare == null || power == null)
            return true;

        if (!blueprint.hasPowerAttribute())
            return false;

        Float powerAsFloat = Float.parseFloat(power);
        Float blueprintPower = blueprint.getPower();

        return isAttributeValueAccepted(powerCompare, powerAsFloat, blueprintPower);
    }

    /**
     * Determines if the card is accepted by the ability filters
     * @param blueprint the card blueprint
     * @param filterParams the filter params
     * @return true or false
     */
    private boolean acceptedByAbilityFilters(SwccgCardBlueprint blueprint, String[] filterParams) {
        if (blueprint == null)
            return false;

        String abilityCompare = null;
        String ability = null;

        for (String filterParam : filterParams) {
            if (filterParam.startsWith("abilityCompare:"))
                abilityCompare = filterParam.substring("abilityCompare:".length());
            else if (filterParam.startsWith("ability:"))
                ability = filterParam.substring("ability:".length());
        }

        if (abilityCompare == null || ability == null)
            return true;

        if (!blueprint.hasAbilityAttribute())
            return false;

        Float abilityAsFloat = Float.parseFloat(ability);
        Float blueprintAbility = blueprint.getAbility();

        return isAttributeValueAccepted(abilityCompare, abilityAsFloat, blueprintAbility);
    }

    /**
     * Determines if the card is accepted by the deploy cost filters
     * @param blueprint the card blueprint
     * @param filterParams the filter params
     * @return true or false
     */
    private boolean acceptedByDeployFilters(SwccgCardBlueprint blueprint, String[] filterParams) {
        if (blueprint == null)
            return false;

        String deployCompare = null;
        String deploy = null;

        for (String filterParam : filterParams) {
            if (filterParam.startsWith("deployCompare:"))
                deployCompare = filterParam.substring("deployCompare:".length());
            else if (filterParam.startsWith("deploy:"))
                deploy = filterParam.substring("deploy:".length());
        }

        if (deployCompare == null || deploy == null)
            return true;

        if (!blueprint.isCardTypeDeployed())
            return false;

        Float deployCostAsFloat = Float.parseFloat(deploy);
        Float blueprintDeployCost = blueprint.getDeployCost();

        return isAttributeValueAccepted(deployCompare, deployCostAsFloat, blueprintDeployCost);
    }

    /**
     * Determines if the card is accepted by the forfeit value filters
     * @param blueprint the card blueprint
     * @param filterParams the filter params
     * @return true or false
     */
    private boolean acceptedByForfeitFilters(SwccgCardBlueprint blueprint, String[] filterParams) {
        if (blueprint == null)
            return false;

        String forfeitCompare = null;
        String forfeit = null;

        for (String filterParam : filterParams) {
            if (filterParam.startsWith("forfeitCompare:"))
                forfeitCompare = filterParam.substring("forfeitCompare:".length());
            else if (filterParam.startsWith("forfeit:"))
                forfeit = filterParam.substring("forfeit:".length());
        }

        if (forfeitCompare == null || forfeit == null)
            return true;

        if (!blueprint.hasForfeitAttribute())
            return false;

        Float forfeitAsFloat = Float.parseFloat(forfeit);
        Float blueprintForfeit = blueprint.getForfeit();

        return isAttributeValueAccepted(forfeitCompare, forfeitAsFloat, blueprintForfeit);
    }

    /**
     * Determines if the card is accepted by the armor filters
     * @param blueprint the card blueprint
     * @param filterParams the filter params
     * @return true or false
     */
    private boolean acceptedByArmorFilters(SwccgCardBlueprint blueprint, String[] filterParams) {
        if (blueprint == null)
            return false;

        String armorCompare = null;
        String armor = null;

        for (String filterParam : filterParams) {
            if (filterParam.startsWith("armorCompare:"))
                armorCompare = filterParam.substring("armorCompare:".length());
            else if (filterParam.startsWith("armor:"))
                armor = filterParam.substring("armor:".length());
        }

        if (armorCompare == null || armor == null)
            return true;

        if (!blueprint.hasArmorAttribute())
            return false;

        Float armorAsFloat = Float.parseFloat(armor);
        Float blueprintArmor = blueprint.getArmor();

        return isAttributeValueAccepted(armorCompare, armorAsFloat, blueprintArmor);
    }

    /**
     * Determines if the card is accepted by the defense value filters
     * @param blueprint the card blueprint
     * @param filterParams the filter params
     * @return true or false
     */
    private boolean acceptedByDefenseValueFilters(SwccgCardBlueprint blueprint, String[] filterParams) {
        if (blueprint == null)
            return false;

        String defenseValueCompare = null;
        String defenseValue = null;

        for (String filterParam : filterParams) {
            if (filterParam.startsWith("defenseValueCompare:"))
                defenseValueCompare = filterParam.substring("defenseValueCompare:".length());
            else if (filterParam.startsWith("defenseValue:"))
                defenseValue = filterParam.substring("defenseValue:".length());
        }

        if (defenseValueCompare == null || defenseValue == null)
            return true;

        Float defenseValueAsFloat = Float.parseFloat(defenseValue);
        Float blueprintDefenseValue = null;

        // Use largest defense value in blueprint

        if (blueprint.hasSpecialDefenseValueAttribute()) {
            blueprintDefenseValue = blueprint.getSpecialDefenseValue();
        }

        if (blueprint.hasAbilityAttribute()) {
            Float blueprintAbility = blueprint.getAbility();
            if (blueprintDefenseValue == null
                    || (blueprintAbility != null && blueprintAbility > blueprintDefenseValue)) {
                blueprintDefenseValue = blueprintAbility;
            }
        }

        if (blueprint.hasArmorAttribute()) {
            Float blueprintArmor = blueprint.getArmor();
            if (blueprintDefenseValue == null
                    || (blueprintArmor != null && blueprintArmor > blueprintDefenseValue)) {
                blueprintDefenseValue = blueprintArmor;
            }
        }

        if (blueprint.hasManeuverAttribute()) {
            Float blueprintManeuver = blueprint.getManeuver();
            if (blueprintDefenseValue == null
                    || (blueprintManeuver != null && blueprintManeuver > blueprintDefenseValue)) {
                blueprintDefenseValue = blueprintManeuver;
            }
        }

        return isAttributeValueAccepted(defenseValueCompare, defenseValueAsFloat, blueprintDefenseValue);
    }

    /**
     * Determines if the card is accepted by the maneuver filters
     * @param blueprint the card blueprint
     * @param filterParams the filter params
     * @return true or false
     */
    private boolean acceptedByManeuverFilters(SwccgCardBlueprint blueprint, String[] filterParams) {
        if (blueprint == null)
            return false;

        String maneuverCompare = null;
        String maneuver = null;

        for (String filterParam : filterParams) {
            if (filterParam.startsWith("maneuverCompare:"))
                maneuverCompare = filterParam.substring("maneuverCompare:".length());
            else if (filterParam.startsWith("maneuver:"))
                maneuver = filterParam.substring("maneuver:".length());
        }

        if (maneuverCompare == null || maneuver == null)
            return true;

        if (!blueprint.hasManeuverAttribute())
            return false;

        Float maneuverAsFloat = Float.parseFloat(maneuver);
        Float blueprintManeuver = blueprint.getManeuver();

        return isAttributeValueAccepted(maneuverCompare, maneuverAsFloat, blueprintManeuver);
    }

    /**
     * Determines if the card is accepted by the hyperspeed filters
     * @param blueprint the card blueprint
     * @param filterParams the filter params
     * @return true or false
     */
    private boolean acceptedByHyperspeedFilters(SwccgCardBlueprint blueprint, String[] filterParams) {
        if (blueprint == null)
            return false;

        String hyperspeedCompare = null;
        String hyperspeed = null;

        for (String filterParam : filterParams) {
            if (filterParam.startsWith("hyperspeedCompare:"))
                hyperspeedCompare = filterParam.substring("hyperspeedCompare:".length());
            else if (filterParam.startsWith("hyperspeed:"))
                hyperspeed = filterParam.substring("hyperspeed:".length());
        }

        if (hyperspeedCompare == null || hyperspeed == null)
            return true;

        if (!blueprint.hasHyperspeedAttribute())
            return false;

        Float hyperspeedAsFloat = Float.parseFloat(hyperspeed);
        Float blueprintHyperspeed = blueprint.getHyperspeed();

        return isAttributeValueAccepted(hyperspeedCompare, hyperspeedAsFloat, blueprintHyperspeed);
    }

    /**
     * Determines if the card is accepted by the landspeed filters
     * @param blueprint the card blueprint
     * @param filterParams the filter params
     * @return true or false
     */
    private boolean acceptedByLandspeedFilters(SwccgCardBlueprint blueprint, String[] filterParams) {
        if (blueprint == null)
            return false;

        String landspeedCompare = null;
        String landspeed = null;

        for (String filterParam : filterParams) {
            if (filterParam.startsWith("landspeedCompare:"))
                landspeedCompare = filterParam.substring("landspeedCompare:".length());
            else if (filterParam.startsWith("landspeed:"))
                landspeed = filterParam.substring("landspeed:".length());
        }

        if (landspeedCompare == null || landspeed == null)
            return true;

        if (!blueprint.hasLandspeedAttribute())
            return false;

        Float landspeedAsFloat = Float.parseFloat(landspeed);
        Float blueprintLandspeed = blueprint.getLandspeed();

        return isAttributeValueAccepted(landspeedCompare, landspeedAsFloat, blueprintLandspeed);
    }

    /**
     * Determines if the blueprint attribute value is accepted by the filter comparison.
     * @param compareType the compare type
     * @param filterValue the filter value
     * @param blueprintValue the card blueprint value
     * @return true or false
     */
    private static boolean isAttributeValueAccepted(String compareType, Float filterValue, Float blueprintValue) {
        if (blueprintValue == null)
            return false;

        if ("EQUALS".equals(compareType) || "GREATER_THAN_OR_EQUAL_TO".equals(compareType) || "LESS_THAN_OR_EQUAL_TO".equals(compareType)) {
            if (blueprintValue.equals(filterValue))
                return true;
        }
        if ("GREATER_THAN".equals(compareType) || "GREATER_THAN_OR_EQUAL_TO".equals(compareType)) {
            if (blueprintValue > filterValue)
                return true;
        }
        if ("LESS_THAN".equals(compareType) || "LESS_THAN_OR_EQUAL_TO".equals(compareType)) {
            if (blueprintValue < filterValue)
                return true;
        }
        return false;
    }

    /**
     * Determines if the blueprint id is for either a box or a pack.
     * @param blueprintId the blueprint id
     * @return true or false
     */
    private static boolean isPack(String blueprintId) {
        return !blueprintId.contains("_");
    }

    /**
     * Determines if the blueprint id is for a legacy card
     * @param blueprintId the blueprint id
     * @return true or false
     */
    private static boolean isLegacy(String blueprintId, SwccgCardBlueprintLibrary library) {
        return library.getSwccgoCardBlueprint(blueprintId) != null && library.getSwccgoCardBlueprint(blueprintId).isLegacy();
    }

    /**
     * Determines if the blueprint id is for a card that is always hidden from the deck builder
     * @param blueprintId the blueprint id
     * @return true or false
     */
    private static boolean isAlwaysExcluded(String blueprintId, SwccgCardBlueprintLibrary library) {
        return library.getSwccgoCardBlueprint(blueprintId) != null && library.getSwccgoCardBlueprint(blueprintId).excludeFromDeckBuilder();
    }

    /**
     * Determines if the blueprint id is for a foil.
     * @param blueprintId the blueprint id
     * @return true or false
     */
    private static boolean isFoil(String blueprintId) {
        return blueprintId.endsWith("*");
    }

    private static class PacksFirstComparator implements Comparator<CardItem> {
        private Comparator<CardItem> _cardComparator;

        private PacksFirstComparator(Comparator<CardItem> cardComparator) {
            _cardComparator = cardComparator;
        }

        @Override
        public int compare(CardItem o1, CardItem o2) {
            final boolean pack1 = isPack(o1.getBlueprintId());
            final boolean pack2 = isPack(o2.getBlueprintId());
            if (pack1 && pack2)
                return o1.getBlueprintId().compareTo(o2.getBlueprintId());
            else if (pack1)
                return -1;
            else if (pack2)
                return 1;
            else
                return _cardComparator.compare(o1, o2);
        }
    }

    /**
     * Sorts cards by card title.
     */
    private static class NameComparator implements Comparator<CardItem> {
        private SwccgCardBlueprintLibrary _library;

        private NameComparator(SwccgCardBlueprintLibrary library) {
            _library = library;
        }

        @Override
        public int compare(CardItem o1, CardItem o2) {
            return GameUtils.getFullName(_library.getSwccgoCardBlueprint(o1.getBlueprintId())).compareTo(GameUtils.getFullName(_library.getSwccgoCardBlueprint(o2.getBlueprintId())));
        }
    }

    /**
     * Sorts cards by card title.
     */
    private static class SetComparator implements Comparator<CardItem> {

        @Override
        public int compare(CardItem o1, CardItem o2) {
            int setNo1 = Integer.parseInt(o1.getBlueprintId().split("_")[0]);
            int setNo2 = Integer.parseInt(o2.getBlueprintId().split("_")[0]);

            if (setNo1 < setNo2)
                return -1;
            else if (setNo1 > setNo2)
                return 1;
            else
                return 0;
        }
    }

    /**
     * Sorts cards by card type.
     */
    private static class CardTypeComparator implements Comparator<CardItem> {
        private SwccgCardBlueprintLibrary _library;

        private CardTypeComparator(SwccgCardBlueprintLibrary library) {
            _library = library;
        }

        @Override
        public int compare(CardItem o1, CardItem o2) {
            CardCategory cardCategory1 = _library.getSwccgoCardBlueprint(o1.getBlueprintId()).getCardCategory();
            Set<CardType> cardTypes1 = _library.getSwccgoCardBlueprint(o1.getBlueprintId()).getCardTypes();
            CardCategory cardCategory2 = _library.getSwccgoCardBlueprint(o2.getBlueprintId()).getCardCategory();
            Set<CardType> cardTypes2 = _library.getSwccgoCardBlueprint(o2.getBlueprintId()).getCardTypes();

            if (cardCategory1.ordinal() < cardCategory2.ordinal())
                return -1;
            if (cardCategory1.ordinal() > cardCategory2.ordinal())
                return 1;

            int lowestCardType1 = Integer.MAX_VALUE;
            for (CardType cardType : cardTypes1) {
                lowestCardType1 = Math.min(lowestCardType1, cardType.ordinal());
            }

            int lowestCardType2 = Integer.MAX_VALUE;
            for (CardType cardType : cardTypes2) {
                lowestCardType2 = Math.min(lowestCardType2, cardType.ordinal());
            }

            if (lowestCardType1 < lowestCardType2)
                return -1;
            if (lowestCardType1 > lowestCardType2)
                return 1;

            return 0;
        }
    }

    /**
     * Sorts cards by card category.
     */
    private static class CardCategoryComparator implements Comparator<CardItem> {
        private SwccgCardBlueprintLibrary _library;

        private CardCategoryComparator(SwccgCardBlueprintLibrary library) {
            _library = library;
        }

        @Override
        public int compare(CardItem o1, CardItem o2) {
            CardCategory cardCategory1 = _library.getSwccgoCardBlueprint(o1.getBlueprintId()).getCardCategory();
            CardCategory cardCategory2 = _library.getSwccgoCardBlueprint(o2.getBlueprintId()).getCardCategory();

            if (cardCategory1.ordinal() < cardCategory2.ordinal())
                return -1;
            if (cardCategory1.ordinal() > cardCategory2.ordinal())
                return 1;

            return 0;
        }
    }
}
