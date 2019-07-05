package com.gempukku.swccgo.filters;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.game.state.*;
import com.gempukku.swccgo.game.state.actions.PlayCardState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.effects.RespondableWeaponFiringEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.TargetingActionUtils;

import java.util.*;


/**
 * This class is a static class that contains all the Filters used in Gemp-Swccg.
 */
public class Filters {
    private static final Map<CardCategory, Filter> _categoryFilterMap = new HashMap<CardCategory, Filter>();
    private static final Map<CardType, Filter> _typeFilterMap = new HashMap<CardType, Filter>();
    private static final Map<CardSubtype, Filter> _subTypeFilterMap = new HashMap<CardSubtype, Filter>();
    private static final Map<Zone, Filter> _zoneFilterMap = new HashMap<Zone, Filter>();
    private static final Map<Side, Filter> _sideFilterMap = new HashMap<Side, Filter>();
    private static final Map<Icon, Filter> _iconFilterMap = new HashMap<Icon, Filter>();
    private static final Map<Keyword, Filter> _keywordFilterMap = new HashMap<Keyword, Filter>();
    private static final Map<Species, Filter> _speciesFilterMap = new HashMap<Species, Filter>();
    private static final Map<Persona, Filter> _personaFilterMap = new HashMap<Persona, Filter>();
    private static final Map<ModelType, Filter> _modelTypeFilterMap = new HashMap<ModelType, Filter>();
    private static final Map<Agenda, Filter> _agendaFilterMap = new HashMap<Agenda, Filter>();

    //
    //
    // This section defines Filter shortcuts.
    //
    //

    static {
        for (CardCategory cardCategory : CardCategory.values())
            _categoryFilterMap.put(cardCategory, category(cardCategory));
        for (CardType cardType : CardType.values())
            _typeFilterMap.put(cardType, type(cardType));
        for (CardSubtype cardSubtype : CardSubtype.values())
            _subTypeFilterMap.put(cardSubtype, subtype(cardSubtype));
        for (Zone zone : Zone.values())
            _zoneFilterMap.put(zone, zone(zone));
        for (Side side : Side.values())
            _sideFilterMap.put(side, side(side));
        for (Icon icon : Icon.values())
            _iconFilterMap.put(icon, icon(icon));
        for (Keyword keyword : Keyword.values())
            _keywordFilterMap.put(keyword, keyword(keyword));
        for (Species species : Species.values())
            _speciesFilterMap.put(species, species(species));
        for (Persona persona : Persona.values())
            _personaFilterMap.put(persona, persona(persona));
        for (ModelType modelType : ModelType.values())
            _modelTypeFilterMap.put(modelType, modelType(modelType));
        for (Agenda agenda : Agenda.values())
            _agendaFilterMap.put(agenda, agenda(agenda));

        // Some simple shortcuts for filters

    }

    private static Filter changeToFilter(Filterable filter) {
        if (filter instanceof Filter)
            return (Filter) filter;
        else if (filter instanceof PhysicalCard)
            return Filters.sameCardId((PhysicalCard) filter);
        else if (filter instanceof CardCategory)
            return _categoryFilterMap.get(filter);
        else if (filter instanceof CardType)
            return _typeFilterMap.get(filter);
        else if (filter instanceof CardSubtype)
            return _subTypeFilterMap.get(filter);
        else if (filter instanceof Icon)
            return _iconFilterMap.get(filter);
        else if (filter instanceof Keyword)
            return _keywordFilterMap.get(filter);
        else if (filter instanceof Persona)
            return _personaFilterMap.get(filter);
        else if (filter instanceof Species)
            return _speciesFilterMap.get(filter);
        else if (filter instanceof Side)
            return _sideFilterMap.get(filter);
        else if (filter instanceof Zone)
            return _zoneFilterMap.get(filter);
        else if (filter instanceof ModelType)
            return _modelTypeFilterMap.get(filter);
        else if (filter instanceof Agenda)
            return _agendaFilterMap.get(filter);
        else
            throw new IllegalArgumentException("Unknown type of filterable: " + filter);

    }


    //
    //
    // This section defines Filters based on uniqueness.
    //
    //

    /**
     * Filter that accepts cards that are unique.
     */
    public static final Filter unique = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            Uniqueness uniqueness = modifiersQuerying.getUniqueness(gameState, physicalCard);
            return uniqueness!=null && uniqueness==Uniqueness.UNIQUE;
        }
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
            Uniqueness uniqueness = builtInCardBlueprint.getUniqueness();
            return uniqueness!=null && uniqueness==Uniqueness.UNIQUE;
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter unique() {
        return unique;
    }

    /**
     * Filter that accepts cards that are restricted.
     */
    public static final Filter restricted = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            Uniqueness uniqueness = modifiersQuerying.getUniqueness(gameState, physicalCard);
            return uniqueness!=null && !uniqueness.isPerSystem() && uniqueness.getValue()>1;
        }
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
            Uniqueness uniqueness = builtInCardBlueprint.getUniqueness();
            return uniqueness!=null && !uniqueness.isPerSystem() && uniqueness.getValue()>1;
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter restricted() {
        return restricted;
    }

    /**
     * Filter that accepts cards that are non-unique.
     */
    public static final Filter non_unique = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            Uniqueness uniqueness = modifiersQuerying.getUniqueness(gameState, physicalCard);
            return uniqueness==null || uniqueness!=Uniqueness.UNIQUE;
        }
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
            Uniqueness uniqueness = builtInCardBlueprint.getUniqueness();
            return uniqueness==null || uniqueness!=Uniqueness.UNIQUE;
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter nonUnique() {
        return non_unique;
    }

    /**
     * Filter that accepts cards that are generic.
     */
    public static final Filter generic = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            Uniqueness uniqueness = modifiersQuerying.getUniqueness(gameState, physicalCard);
            return uniqueness != null && uniqueness.isPerSystem();
        }
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
            Uniqueness uniqueness = builtInCardBlueprint.getUniqueness();
            return uniqueness != null && uniqueness.isPerSystem();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter generic() {
        return generic;
    }

    /**
     * Filter that accepts cards that are generic.
     */
    public static final Filter perSystemUniqueness = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            Uniqueness uniqueness = modifiersQuerying.getUniqueness(gameState, physicalCard);
            return uniqueness != null && uniqueness.isPerSystem();
        }
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
            Uniqueness uniqueness = builtInCardBlueprint.getUniqueness();
            return uniqueness != null && uniqueness.isPerSystem();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter perSystemUniqueness() {
        return perSystemUniqueness;
    }


    /**
     * Filter that accepts cards that have the specified uniqueness.
     *
     * @param uniqueness the uniqueness
     * @return Filter
     */
    public static Filter uniqueness(final Uniqueness uniqueness) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.getUniqueness(gameState, physicalCard) == uniqueness;
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                return builtInCardBlueprint.getUniqueness() == uniqueness;
            }
        };
    }

    //
    //
    // This section defines Filters based on general card features.
    //
    //

    /**
     * Filter that accepts cards the specified side of the Force.
     *
     * @param side the side of the Force
     * @return Filter
     */
    private static Filter side(final Side side) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return gameState.getSide(physicalCard.getOwner()) == side;
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                return side(side).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    // Gets a filter representing the cards owned by the specified player.

    /**
     * Filter that accepts cards owned by the specified player.
     *
     * @param playerId the player
     * @return Filter
     */
    public static Filter owner(final String playerId) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return playerId.equals(physicalCard.getOwner());
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                return playerId.equals(builtInCardBlueprint.getPhysicalCard(gameState.getGame()).getOwner());
            }
            @Override
            public boolean acceptsIgnoringOwner(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return true;
            }
        };
    }

    /**
     * Filter that accepts cards owned by the specified player.
     *
     * @param yourPlayerId the player
     * @return Filter
     */
    public static Filter your(final String yourPlayerId) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return yourPlayerId.equals(physicalCard.getOwner());
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                return yourPlayerId.equals(builtInCardBlueprint.getPhysicalCard(gameState.getGame()).getOwner());
            }
            @Override
            public boolean acceptsIgnoringOwner(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return true;
            }
        };
    }

    /**
     * Filter that accepts cards owned by the same owner as the input card.
     *
     * @param sourceCard the card
     * @return Filter
     */
    public static Filter your(PhysicalCard sourceCard) {
        final Integer permSourceCardId = sourceCard.getPermanentCardId();
        if (sourceCard.getBlueprint().getCardCategory() == CardCategory.LOCATION) {
            throw new UnsupportedOperationException("Location " + GameUtils.getFullName(sourceCard) + " should not be calling this method");
        }
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);

                return sourceCard.getOwner().equals(physicalCard.getOwner());
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);

                return sourceCard.getOwner().equals(builtInCardBlueprint.getPhysicalCard(gameState.getGame()).getOwner());
            }
            @Override
            public boolean acceptsIgnoringOwner(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return true;
            }
        };
    }

    /**
     * Filter that accepts cards owned by the opponent of the specified player.
     *
     * @param yourPlayerId the player
     * @return Filter
     */
    public static Filter opponents(final String yourPlayerId) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return !yourPlayerId.equals(physicalCard.getOwner());
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                return !yourPlayerId.equals(builtInCardBlueprint.getPhysicalCard(gameState.getGame()).getOwner());
            }
            @Override
            public boolean acceptsIgnoringOwner(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return true;
            }
        };
    }

    /**
     * Filter that accepts cards owned by the opponent of the owner as the input card.
     *
     * @param sourceCard the card
     * @return Filter
     */
    public static Filter opponents(PhysicalCard sourceCard) {
        if (sourceCard.getBlueprint().getCardCategory() == CardCategory.LOCATION) {
            throw new UnsupportedOperationException("Location " + GameUtils.getFullName(sourceCard) + " should not be calling this method");
        }
        final Integer permSourceCardId = sourceCard.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);

                return !sourceCard.getOwner().equals(physicalCard.getOwner());
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);

                return !sourceCard.getOwner().equals(builtInCardBlueprint.getPhysicalCard(gameState.getGame()).getOwner());
            }
            @Override
            public boolean acceptsIgnoringOwner(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return true;
            }
        };
    }

    /**
     * Filter that accepts cards that have the specified keyword.
     *
     * @param keyword the keyword
     * @return Filter
     */
    private static Filter keyword(final Keyword keyword) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.hasKeyword(gameState, physicalCard, keyword);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                return builtInCardBlueprint.hasKeyword(keyword);
            }
        };
    }

    /**
     * Filter that accepts cards that have the specified icon.
     *
     * @param icon the icon
     * @return Filter
     */
    private static Filter icon(final Icon icon) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.hasIcon(gameState, physicalCard, icon);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                return builtInCardBlueprint.isWeapon() && icon==Icon.PERMANENT_WEAPON;
            }
        };
    }

    /**
     * Filter that accepts cards that have the specified number of the specified icon.
     *
     * @param icon the icon
     * @return Filter
     */
    public static Filter iconCount(final Icon icon, final int count) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.getIconCount(gameState, physicalCard, icon) == count;
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                return builtInCardBlueprint.isWeapon() && icon==Icon.PERMANENT_WEAPON && count == 1;
            }
        };
    }

    /**
     * Filter that accepts cards that have less than the specified number of the specified icon.
     *
     * @param icon the icon
     * @return Filter
     */
    public static Filter iconCountLessThan(final Icon icon, final int count) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.getIconCount(gameState, physicalCard, icon) < count;
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                return builtInCardBlueprint.isWeapon() && icon==Icon.PERMANENT_WEAPON && count > 1;
            }
        };
    }

    /**
     * Filter that accepts cards that have a specified political agenda.
     *
     * @param agenda the agenda
     * @return Filter
     */
    private static Filter agenda(final Agenda agenda) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.hasAgenda(gameState, physicalCard, agenda);
            }
        };
    }

    /**
     * Filter that accepts cards that have a matching political agenda to the specified card.
     *
     * @param card the card
     * @return Filter
     */
    public static Filter hasMatchingAgenda(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);

                List<Agenda> agendasList = modifiersQuerying.getAgendas(gameState, card);
                if (!agendasList.isEmpty()) {
                    List<Agenda> agendasList2 = modifiersQuerying.getAgendas(gameState, physicalCard);
                    for (Agenda agenda2 : agendasList2) {
                        if (agendasList.contains(agenda2)) {
                            return true;
                        }
                    }
                }
                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that are in the specified card category.
     *
     * @param category the card category
     * @return Filter
     */
    private static Filter category(final CardCategory category) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return (physicalCard.getBlueprint().getCardCategory() == category);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                return (builtInCardBlueprint.isWeapon() && category == CardCategory.WEAPON);
            }
        };
    }

    /**
     * Filter that accepts cards that are the specified card type.
     *
     * @param type the card type
     * @return Filter
     */
    public static Filter type(final CardType type) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return (physicalCard.getBlueprint().isCardType(type) && !physicalCard.isDejarikHologramAtHolosite());
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                return (builtInCardBlueprint.isWeapon() && type == CardType.WEAPON);
            }
        };
    }

    /**
     * Filter that accepts cards that are the specified card subtype.
     *
     * @param subtype the card subtype
     * @return Filter
     */
    private static Filter subtype(final CardSubtype subtype) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return (physicalCard.getBlueprint().getCardSubtype() == subtype);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                if (builtInCardBlueprint.isWeapon()) {
                    if (subtype == CardSubtype.CHARACTER) {
                        return builtInCardBlueprint.getPhysicalCard(gameState.getGame()).getBlueprint().getCardCategory() == CardCategory.CHARACTER;
                    }
                    if (subtype == CardSubtype.STARSHIP) {
                        return builtInCardBlueprint.getPhysicalCard(gameState.getGame()).getBlueprint().getCardCategory() == CardCategory.STARSHIP;
                    }
                    if (subtype == CardSubtype.VEHICLE) {
                        return builtInCardBlueprint.getPhysicalCard(gameState.getGame()).getBlueprint().getCardCategory() == CardCategory.VEHICLE;
                    }
                }
                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that are of the specified species.
     *
     * @param species the species
     * @return Filter
     */
    public static Filter species(final Species species) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.isSpecies(gameState, physicalCard, species);
            }
        };
    }

    /**
     * Filter that accepts cards that are the specified persona.
     *
     * @param persona the persona
     * @return Filter
     */
    public static Filter persona(final Persona persona) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.hasPersona(gameState, physicalCard, persona);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                return builtInCardBlueprint.hasPersona(gameState.getGame(), persona);
            }
        };
    }

    /**
     * Filter that accepts cards that are in the specified zone (top of zone included).
     *
     * @param zone the zone
     * @return Filter
     */
    private static Filter zone(final Zone zone) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                Zone cardZone = physicalCard.getZone();
                // Top of zone and zone itself are combined for this filter
                if (GameUtils.getZoneFromZoneTop(cardZone) == GameUtils.getZoneFromZoneTop(zone))
                    return true;
                else
                    return false;
            }
        };
    }

    /**
     * Filter that accepts cards that are in the specified zone (top of zone included) owned by the specified player.
     *
     * @param zone the zone
     * @param zoneOwner the zone owner
     * @return Filter
     */
    public static Filter zoneOfPlayer(final Zone zone, final String zoneOwner) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                Zone cardZone = physicalCard.getZone();
                // Top of zone and zone itself are combined for this filter
                if (physicalCard.getZoneOwner().equals(zoneOwner)
                        && GameUtils.getZoneFromZoneTop(cardZone) == GameUtils.getZoneFromZoneTop(zone))
                    return true;
                else
                    return false;
            }
        };
    }


    /**
     * Filter that accepts cards that are the specified model/type.
     *
     * @param modelType the model/type
     * @return Filter
     */
    private static Filter modelType(final ModelType modelType) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                boolean isSquadron = Filters.squadron.accepts(gameState, modifiersQuerying, physicalCard);
                int count = 0;
                for (ModelType curModelType : physicalCard.getBlueprint().getModelTypes()) {
                    if (modelType == ModelType._ANY_ || curModelType == modelType) {
                        count++;
                    }
                    else if (isSquadron) {
                        return false;
                    }
                }
                return count > 0;
            }
            @Override
            public int acceptsCount(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                int count = 0;
                for (ModelType curModelType : physicalCard.getBlueprint().getModelTypes()) {
                    if (modelType == ModelType._ANY_ || curModelType == modelType) {
                        count++;
                    }
                }
                return count;
            }
            @Override
            public boolean acceptsSingleModelType(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, ModelType modelTypeToCheck) {
                return (modelTypeToCheck == modelType) && acceptsCount(gameState, modifiersQuerying, physicalCard) > 0;
            }
        };
    }

    /**
     * Filter that accepts cards that are TIEs.
     *
     * @return Filter
     */
    public static Filter tie() {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return acceptsCount(gameState, modifiersQuerying, physicalCard) > 0;
            }
            @Override
            public int acceptsCount(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                int totalCount = 0;
                totalCount += Filters.modelType(ModelType.TIE_ADVANCED_X1).acceptsCount(gameState, modifiersQuerying, physicalCard);
                totalCount += Filters.modelType(ModelType.TIE_AD).acceptsCount(gameState, modifiersQuerying, physicalCard);
                totalCount += Filters.modelType(ModelType.TIE_DEFENDER).acceptsCount(gameState, modifiersQuerying, physicalCard);
                totalCount += Filters.modelType(ModelType.TIE_INTERCEPTOR).acceptsCount(gameState, modifiersQuerying, physicalCard);
                totalCount += Filters.modelType(ModelType.TIE_LN).acceptsCount(gameState, modifiersQuerying, physicalCard);
                totalCount += Filters.modelType(ModelType.TIE_RC).acceptsCount(gameState, modifiersQuerying, physicalCard);
                totalCount += Filters.modelType(ModelType.TIE_SA).acceptsCount(gameState, modifiersQuerying, physicalCard);
                totalCount += Filters.modelType(ModelType.TIE_SF).acceptsCount(gameState, modifiersQuerying, physicalCard);
                totalCount += Filters.modelType(ModelType.TIE_SR).acceptsCount(gameState, modifiersQuerying, physicalCard);
                totalCount += Filters.modelType(ModelType.TIE_VN).acceptsCount(gameState, modifiersQuerying, physicalCard);
                return totalCount;
            }
        };
    }

    /**
     * Filter that accepts cards that are TIEs and have less than or equal to the specified number of TIE components.
     *
     * @param count the count
     * @return Filter
     */
    public static Filter tieCountNoMoreThan(final int count) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                int tieCount = Filters.tie().acceptsCount(gameState, modifiersQuerying, physicalCard);
                return tieCount > 0 && tieCount <= count;
            }
            @Override
            public int acceptsCount(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                int tieCount = Filters.tie().acceptsCount(gameState, modifiersQuerying, physicalCard);
                if (tieCount > 0 && tieCount <= count) {
                    return tieCount;
                }
                return 0;
            }
        };
    }

    /**
     * Filter that accepts cards that have the specified title.
     * For combo cards, each title is checked.
     *
     * @param title the title
     * @return Filter
     */
    public static Filter title(final String title) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                for (String cardTitle : physicalCard.getTitles()) {
                    if (cardTitle.equalsIgnoreCase(title)) {
                        return true;
                    }
                }
                return false;
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                return builtInCardBlueprint.getTitle(gameState.getGame()).equals(title);
            }
        };
    }

    /**
     * Filter that accepts cards that have a title containing the specified word or phrase.
     * For combo cards, each title is checked.
     *
     * @param wordOrPhrase the word or phase
     * @return Filter
     */
    public static Filter titleContains(final String wordOrPhrase) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                for (String cardTitle : physicalCard.getTitles()) {
                    if (containsWordOrPhrase(cardTitle, wordOrPhrase)) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that have lore containing the specified word or phrase.
     *
     * @param wordOrPhrase the word or phase
     * @return Filter
     */
    public static Filter loreContains(final String wordOrPhrase) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                String cardLore = physicalCard.getBlueprint().getLore();

                return containsWordOrPhrase(cardLore, wordOrPhrase);
            }
        };
    }

    /**
     * Filter that accepts cards that have game text containing the specified word or phrase.
     *
     * @param wordOrPhrase the word or phase
     * @return Filter
     */
    public static Filter gameTextContains(final String wordOrPhrase) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() == CardCategory.LOCATION) {
                    return containsWordOrPhrase(physicalCard.getBlueprint().getLocationDarkSideGameText(), wordOrPhrase)
                            || containsWordOrPhrase(physicalCard.getBlueprint().getLocationLightSideGameText(), wordOrPhrase);
                }

                String cardGameText = physicalCard.getBlueprint().getGameText();

                return containsWordOrPhrase(cardGameText, wordOrPhrase);
            }
        };
    }

    /**
     * Filter that accepts cards that have their game text canceled.
     */
    public static final Filter isGameTextCanceled = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.isGameTextCanceled();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter isGameTextCanceled() {
        return isGameTextCanceled;
    }

    /**
     * Filter that accepts cards that have the same title as the specified card (including the card itself).
     * For combo cards, each title is checked.
     *
     * @param card the card with the title to match
     * @return Filter
     */
    public static Filter sameTitle(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);

                return sameTitleAs(card, true).accepts(gameState, modifiersQuerying, physicalCard);
            }
        };
    }

    /**
     * Filter that accepts cards that have the same title as the specified card (but not including the card itself).
     * For combo cards, each title is checked.
     *
     * @param card the card with the title to match
     * @return Filter
     */
    public static Filter sameTitleAs(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);

                return sameTitleAs(card, false).accepts(gameState, modifiersQuerying, physicalCard);
            }
        };
    }

    /**
     * Filter that accepts cards that have the same title as card accepted by the specified filter (but not including the card itself).
     * For combo cards, each title is checked.
     *
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter sameTitleAs(PhysicalCard source, final Filter filters) {
        return sameTitleAs(source, null, filters);
    }

    /**
     * Filter that accepts cards that have the same title as card accepted by the specified filter (but not including the card itself).
     * For combo cards, each title is checked.
     *
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @param filters the filters
     * @return Filter
     */
    public static Filter sameTitleAs(PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final Filter filters) {
        final Integer permSourceCardId = source != null ? source.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard source = gameState.findCardById(permSourceCardId);

                Collection<PhysicalCard> cards = Filters.filterActive(gameState.getGame(), source, spotOverrides, filters);
                for (PhysicalCard card : cards) {
                    if (sameTitleAs(card).accepts(gameState, modifiersQuerying, physicalCard)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that have the same title as the specified card. Optionally can include or exclude
     * the card itself from being accepted. For combo cards, each title is checked.
     *
     * @param card the card with the title to match
     * @param sameCardMatches true to consider the card itself accepted, or false to only consider other cards
     * @return Filter
     */
    public static Filter sameTitleAs(PhysicalCard card, final boolean sameCardMatches) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);

                if (Filters.sameCardId(card).accepts(gameState, modifiersQuerying, physicalCard))
                    return sameCardMatches;

                return modifiersQuerying.cardTitlesMatch(gameState, card, physicalCard);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);

                if (Filters.sameCardId(builtInCardBlueprint.getPhysicalCard(gameState.getGame())).accepts(gameState, modifiersQuerying, card))
                    return sameCardMatches;

                for (String cardTitle : card.getTitles()) {
                    if (Filters.title(cardTitle).accepts(gameState, modifiersQuerying, builtInCardBlueprint)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that have the same title as cards stacked on the specified card.
     * For combo cards, each title is checked.
     *
     * @param card the card with the title to match
     * @return Filter
     */
    public static Filter sameTitleAsStackedOn(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);

                Collection<PhysicalCard> stackedCards = gameState.getStackedCards(card);
                for (PhysicalCard stackedCard : stackedCards) {
                    if (sameTitleAs(stackedCard).accepts(gameState, modifiersQuerying, physicalCard)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that have the same title as cards stacked on a card accepted by the specified filter.
     * For combo cards, each title is checked.
     *
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter sameTitleAsStackedOn(PhysicalCard source, final Filter filters) {
        final Integer permSourceCardId = source != null ? source.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);

                Collection<PhysicalCard> cards = Filters.filterActive(gameState.getGame(), source, filters);
                for (PhysicalCard card : cards) {
                    if (Filters.sameTitleAsStackedOn(card).accepts(gameState, modifiersQuerying, physicalCard))
                        return true;
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that have a persona defined.
     */
    public static final Filter hasPersona = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return !modifiersQuerying.getPersonas(gameState, physicalCard).isEmpty();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter hasPersona() {
        return hasPersona;
    }

    /**
     * Filter that accepts cards that have the same persona as the specified card.
     *
     * @param card the card with the persona to match
     * @return Filter
     */
    public static Filter samePersonaAs(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);

                for (Persona persona : modifiersQuerying.getPersonas(gameState, card)) {
                    if (modifiersQuerying.hasPersona(gameState, physicalCard, persona)) {
                        return true;
                    }
                }
                return false;
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);

                for (Persona persona : modifiersQuerying.getPersonas(gameState, card)) {
                    if (builtInCardBlueprint.hasPersona(gameState.getGame(), persona)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that have the same persona as the specified built-in.
     *
     * @param builtIn the card built-in with the persona to match
     * @return Filter
     */
    public static Filter samePersonaAs(final SwccgBuiltInCardBlueprint builtIn) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                for (Persona persona : builtIn.getPersonas(gameState.getGame())) {
                    if (modifiersQuerying.hasPersona(gameState, physicalCard, persona)) {
                        return true;
                    }
                }
                return false;
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                for (Persona persona : builtIn.getPersonas(gameState.getGame())) {
                    if (builtInCardBlueprint.hasPersona(gameState.getGame(), persona)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that are missing an expansion icon compared to the specified card..
     *
     * @param card the card with expansion icons to check
     * @return Filter
     */
    public static Filter isMissingExpansionIconComparedTo(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);

                SwccgCardBlueprint cardBlueprint = card.getBlueprint();
                SwccgCardBlueprint physicalCardBlueprint = physicalCard.getBlueprint();
                for (Icon icon : Icon.values()) {
                    if (icon.isExpansionIcon() && cardBlueprint.hasIcon(icon) && !physicalCardBlueprint.hasIcon(icon)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that have a species defined.
     */
    public static final Filter hasSpecies = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.getBlueprint().hasSpeciesAttribute() && physicalCard.getBlueprint().getSpecies() != null;
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter hasSpecies() {
        return hasSpecies;
    }


    /**
     * Filter that accepts cards that are considered "on table".
     */
    public static final Filter onTable = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.getZone() != null && (physicalCard.getZone().isInPlay() || physicalCard.getZone()==Zone.CONVERTED_LOCATIONS);
        }
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
            return onTable.accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter onTable() {
        return onTable;
    }

    /**
     * Filter that accepts cards that are in process of leaving the table.
     */
    public static final Filter isLeavingTable = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.isLeavingTable();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter isLeavingTable() {
        return isLeavingTable;
    }

    /**
     * Filter that accepts cards that are in the specified player's hand.
     *
     * @param playerId the player
     * @return Filter
     */
    public static Filter inHand(final String playerId) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return physicalCard.getZone()==Zone.HAND && physicalCard.getZoneOwner().equals(playerId);
            }
        };
    }

    /**
     * Filter that accepts cards that are in either player's Lost Pile.
     */
    public static final Filter inLostPile = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return GameUtils.getZoneFromZoneTop(physicalCard.getZone())==Zone.LOST_PILE;
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter inLostPile() {
        return inLostPile;
    }

    /**
     * Filter that accepts cards that are in the specified player's Lost Pile.
     *
     * @param playerId the player
     * @return Filter
     */
    public static Filter inLostPile(final String playerId) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return GameUtils.getZoneFromZoneTop(physicalCard.getZone())==Zone.LOST_PILE && physicalCard.getZoneOwner().equals(playerId);
            }
        };
    }

    /**
     * Filter that accepts the card that is the bottom card of the specified player's Lost Pile.
     *
     * @param playerId the player
     * @return Filter
     */
    public static Filter bottomOfLostPile(final String playerId) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return gameState.getBottomOfCardPile(playerId, Zone.LOST_PILE) == physicalCard;
            }
        };
    }

    /**
     * Filter that accepts cards that are in the specified player's Used Pile.
     *
     * @param playerId the player
     * @return Filter
     */
    public static Filter inUsedPile(final String playerId) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return GameUtils.getZoneFromZoneTop(physicalCard.getZone())==Zone.USED_PILE && physicalCard.getZoneOwner().equals(playerId);
            }
        };
    }

    /**
     * Filter that accepts cards that may not be placed in Reserve Deck.
     */
    public static final Filter mayNotBePlacedInReserveDeck = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.getBlueprint().isMayNotBePlacedInReserveDeck();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter mayNotBePlacedInReserveDeck() {
        return mayNotBePlacedInReserveDeck;
    }

    /**
     * Filter that accepts cards that are out of play.
     */
    public static final Filter outOfPlay = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.getZone()==Zone.OUT_OF_PLAY;
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter outOfPlay() {
        return outOfPlay;
    }

    /**
     * Filter that accepts cards that are in the specified player's hand and there are duplicates in that player's hand.
     *
     * @param playerId the player
     * @return Filter
     */
    public static Filter duplicatesOfInHand(final String playerId) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getZone()!=Zone.HAND || !physicalCard.getZoneOwner().equals(playerId))
                    return false;

                List<? extends PhysicalCard> hand = gameState.getHand(playerId);
                for (PhysicalCard cardInHand : hand) {
                    if (sameTitleAs(cardInHand).accepts(gameState, modifiersQuerying, physicalCard)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that are in the specified player's hand and there are three or more of in that player's hand.
     *
     * @param playerId the player
     * @return Filter
     */
    public static Filter threeOrMoreOfInHand(final String playerId) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getZone()!=Zone.HAND || !physicalCard.getZoneOwner().equals(playerId))
                    return false;

                List<? extends PhysicalCard> hand = gameState.getHand(playerId);
                int numMatches = 0;
                for (PhysicalCard cardInHand : hand) {
                    if (sameTitleAs(cardInHand).accepts(gameState, modifiersQuerying, physicalCard)) {
                        numMatches++;
                    }
                }
                return numMatches >=2;
            }
        };
    }

    /**
     * Filter that accepts cards that may deploy as if from hand.
     */
    public static final Filter canDeployAsIfFromHand = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.mayDeployAsIfFromHand(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter canDeployAsIfFromHand() {
        return canDeployAsIfFromHand;
    }

    /**
     * Filter that accepts cards that are in the specified player's sabacc hand.
     *
     * @param playerId the player
     * @return Filter
     */
    public static Filter inSabaccHand(final String playerId) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return (physicalCard.getZone()==Zone.SABACC_HAND || physicalCard.getZone()==Zone.REVEALED_SABACC_HAND)
                        && physicalCard.getZoneOwner().equals(playerId);
            }
        };
    }

    /**
     * Filter that accepts cards that may have destiny number cloned in sabacc by player when not in sabacc hand.
     * @param playerId the player
     * @return Filter
     */
    public static Filter mayBeClonedInSabacc(final String playerId) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.mayHaveDestinyNumberClonedInSabacc(gameState, physicalCard, playerId);
            }
        };
    }

    /**
     * Filter that accepts cards that are always immune to a specified card title.
     *
     * @param title the card title
     * @return Filter
     */
    public static Filter alwaysImmuneToCardTitle(final String title) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return physicalCard.getBlueprint().isImmuneToCardTitle(title);
            }
        };
    }

    /**
     * Filter that accepts cards that are immune to a specified card title.
     *
     * @param title the card title
     * @return Filter
     */
    public static Filter immuneToCardTitle(final String title) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.isImmuneToCardTitle(gameState, physicalCard, title);
            }
        };
    }

    /**
     * Filter that accepts locations that are affected by Revolution.
     */
    public static final Filter affectedByRevolution = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.isRotatedLocation(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter affectedByRevolution() {
        return affectedByRevolution;
    }

    /**
     * Filter that accepts cards that are crossed-over.
     */
    public static final Filter crossedOver = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.isCrossedOver();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter crossedOver() {
        return crossedOver;
    }

    /**
     * Filter that accepts cards that are stolen.
     */
    public static final Filter stolen = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.isStolen();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter stolen() {
        return stolen;
    }

    /**
     * Filter that accepts cards that are stolen.
     */
    public static final Filter grantedToBePlacedOnOwnersPoliticalEffect = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.grantedMayBePlaceOnOwnersPoliticalEffect(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter grantedToBePlacedOnOwnersPoliticalEffect() {
        return grantedToBePlacedOnOwnersPoliticalEffect;
    }

    /**
     * Filter that accepts cards that have the specified modified game text type.
     *
     * @param modifyGameTextType the modify game text type
     * @return Filter
     */
    public static Filter hasGameTextModification(final ModifyGameTextType modifyGameTextType) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.hasGameTextModification(gameState, physicalCard, modifyGameTextType);
            }
        };
    }

    /**
     * Filter that accepts cards that may be deployed instead of a starfighter using Combat Response.
     */
    public static final Filter mayDeployInsteadOfStarfighterUsingCombatResponse = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.mayDeployInsteadOfStarfighterUsingCombatResponse(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter mayDeployInsteadOfStarfighterUsingCombatResponse() {
        return mayDeployInsteadOfStarfighterUsingCombatResponse;
    }

    /**
     * Filter that accepts cards that may be deployed with the specified pilot instead of a matching starfighter using Combat Response.
     *
     * @param pilot the pilot
     * @return Filter
     */
    public static Filter mayDeployWithInsteadOfMatchingStarfighterUsingCombatResponse(PhysicalCard pilot) {
        final Integer permPilotCardId = pilot.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard pilot = gameState.findCardByPermanentId(permPilotCardId);

                return Filters.starship.accepts(gameState, modifiersQuerying, physicalCard)
                        && modifiersQuerying.mayDeployWithInsteadOfMatchingStarfighterUsingCombatResponse(gameState, pilot, physicalCard);
            }
        };
    }

    //
    //
    // This section defines Filters based on deploy cost.
    //
    //

    /**
     * Filter that accepts cards that deploy for free.
     */
    public static final Filter deploysForFree = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.deploysForFree(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter deploysForFree() {
        return deploysForFree;
    }

    /**
     * Filter that accepts cards that have a deploy cost <= X.
     *
     * @param cost the value of X
     * @return Filter
     */
    public static Filter deployCostLessThanOrEqualTo(final float cost) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.hasDeployCostLessThanOrEqualTo(gameState, physicalCard, cost);
            }
        };
    }

    /**
     * Filter that accepts cards that have a printed deploy cost >= X.
     *
     * @param cost the value of X
     * @return Filter
     */
    public static Filter printedDeployCostMoreThanOrEqualTo(final float cost) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return physicalCard.getBlueprint().getDeployCost() >= cost;
            }
        };
    }


    //
    //
    // This section defines Filters based on forfeit value.
    //
    //

    /**
     * Filter that accepts cards that may be forfeited in battle.
     */
    public static final Filter mayBeForfeited = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.mayBeForfeitedInBattle(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter mayBeForfeited() {
        return mayBeForfeited;
    }

    /**
     * Filter that accepts cards that must be forfeited in battle.
     */
    public static final Filter mustBeForfeited = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return Filters.or(Filters.hit, Keyword.MUST_BE_FORFEITED_IN_BATTLE).accepts(gameState, modifiersQuerying, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter mustBeForfeited() {
        return mustBeForfeited;
    }

    /**
     * Filter that accepts cards that must be forfeited in battle before other characters.
     */
    public static final Filter mustBeForfeitedBeforeOtherCharacters = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.hasKeyword(gameState, physicalCard, Keyword.MUST_BE_FORFEITED_BEFORE_OTHER_CHARACTERS);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter mustBeForfeitedBeforeOtherCharacters() {
        return mustBeForfeitedBeforeOtherCharacters;
    }

    /**
     * Filter that accepts cards that have a forfeit value = X.
     *
     * @param value the value of X
     * @return Filter
     */
    public static Filter forfeitValueEqualTo(final float value) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.hasForfeitValueEqualTo(gameState, physicalCard, value);
            }
        };
    }

    /**
     * Filter that accepts cards that have a forfeit value > X.
     *
     * @param value the value of X
     * @return Filter
     */
    public static Filter forfeitValueMoreThan(final float value) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.hasForfeitValueMoreThan(gameState, physicalCard, value);
            }
        };
    }

    /**
     * Filter that accepts cards that may have there forfeit reduced.
     */
    public static final Filter forfeitMayBeReduced = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.hasForfeitValueMoreThan(gameState, physicalCard, 0)
                    && !modifiersQuerying.isProhibitedFromHavingForfeitReduced(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter forfeitMayBeReduced() {
        return forfeitMayBeReduced;
    }


    //
    //
    // This section defines Filters based on destiny.
    //
    // Note: Be sure to use RefreshPrintedDestinyValuesEffect before using destiny filters
    // to give the owner of R2D2 a chance to choose R2D2's current destiny value.
    //

    /**
     * Filter that accepts cards that have a destiny value < X.
     *
     * Note: Be sure to use RefreshPrintedDestinyValuesEffect before using destiny filters
     *       to give the owner of R2D2 a chance to choose R2D2's current destiny value.
     *
     * @param value the value of X
     * @return Filter
     */
    public static Filter destinyLessThan(final int value) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.hasDestinyLessThan(gameState, physicalCard, value);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                return destinyLessThan(value).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    /**
     * Filter that accepts cards that have a destiny value <= X.
     *
     * Note: Be sure to use RefreshPrintedDestinyValuesEffect before using destiny filters
     *       to give the owner of R2D2 a chance to choose R2D2's current destiny value.
     *
     * @param value the value of X
     * @return Filter
     */
    public static Filter destinyLessThanOrEqualTo(final int value) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.hasDestinyLessThanOrEqualTo(gameState, physicalCard, value);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                return destinyLessThanOrEqualTo(value).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    /**
     * Filter that accepts cards that have a destiny value = X.
     *
     * Note: Be sure to use RefreshPrintedDestinyValuesEffect before using destiny filters
     *       to give the owner of R2D2 a chance to choose R2D2's current destiny value.
     *
     * @param value the value of X
     * @return Filter
     */
    public static Filter destinyEqualTo(final float value) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.hasDestinyEqualTo(gameState, physicalCard, value);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                return destinyEqualTo(value).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    /**
     * Filter that accepts cards that have multiple destiny values, such as R2D2.
     */
    public static final Filter multipleDestinyValues = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.getBlueprint().getDestiny() != null && !physicalCard.getBlueprint().getDestiny().equals(physicalCard.getBlueprint().getAlternateDestiny());
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter multipleDestinyValues() {
        return multipleDestinyValues;
    }


    //
    //
    // This section defines Filters based on power.
    //
    //

    /**
     * Filter that accepts cards that have an power value defined in blueprint.
     */
    public static final Filter hasPowerDefined = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.getBlueprint().hasPowerAttribute();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter hasPowerDefined() {
        return hasPowerDefined;
    }

    /**
     * Filter that accepts cards that have power < X.
     *
     * @param power the value of X
     * @return Filter
     */
    public static Filter powerLessThan(final float power) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.hasPowerLessThan(gameState, physicalCard, power);
            }
        };
    }

    /**
     * Filter that accepts cards that have power <= X.
     *
     * @param power the value of X
     * @return Filter
     */
    public static Filter powerLessThanOrEqualTo(final float power) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.getPower(gameState, physicalCard) <= power;
            }
        };
    }

    /**
     * Filter that accepts cards that have power = X.
     *
     * @param power the value of X
     * @return Filter
     */
    public static Filter powerEqualTo(final float power) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.hasPowerEqualTo(gameState, physicalCard, power);
            }
        };
    }


    //
    //
    // This section defines Filters based on defense value.
    //
    //
    /**
     * Filter that accepts cards that have ability > X.
     *
     * @param defense_value the value of X
     * @return Filter
     */
    public static Filter defenseValueMoreThanOrEqualTo(final float defense_value) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.getDefenseValue(gameState, physicalCard) >= defense_value;
            }
        };
    }


    //
    //
    // This section defines Filters based on ability.
    //
    //

    /**
     * Filter that accepts cards that have an ability value defined in blueprint.
     */
    public static final Filter hasAbilityDefined = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.getBlueprint().hasAbilityAttribute();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter hasAbilityDefined() {
        return hasAbilityDefined;
    }

    /**
     * Filter that accepts cards that have ability.
     */
    public static final Filter hasAbility = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.hasAbility(gameState, physicalCard, false);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter hasAbility() {
        return hasAbility;
    }

    /**
     * Filter that accepts cards that have ability when deployed using Dejarik Rules.
     */
    public static final Filter hasAbilityWhenUsingDejarikRules = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return Filters.or(Filters.dejarik, Filters.hologram).accepts(gameState, modifiersQuerying, physicalCard)
                    && modifiersQuerying.getDestiny(gameState, physicalCard) > 0;
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter hasAbilityWhenUsingDejarikRules() {
        return hasAbilityWhenUsingDejarikRules;
    }

    /**
     * Filter that accepts cards that have ability (or have a permanent pilot with ability).
     */
    public static final Filter hasAbilityOrHasPermanentPilotWithAbility = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.hasAbility(gameState, physicalCard, true);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter hasAbilityOrHasPermanentPilotWithAbility() {
        return hasAbilityOrHasPermanentPilotWithAbility;
    }

    /**
     * Filter that accepts cards that have ability < X.
     *
     * @param ability the value of X
     * @return Filter
     */
    public static Filter abilityLessThan(final float ability) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.hasAbilityLessThan(gameState, physicalCard, ability);
            }
        };
    }

    /**
     * Filter that accepts cards that have ability <= X.
     *
     * @param ability the value of X
     * @return Filter
     */
    public static Filter abilityLessThanOrEqualTo(final float ability) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.getAbility(gameState, physicalCard) <= ability;
            }
        };
    }

    /**
     * Filter that accepts cards that have a printed ability = X.
     *
     * @param ability the value of X
     * @return Filter
     */
    public static Filter printedAbilityEqualTo(final float ability) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (!physicalCard.getBlueprint().hasAbilityAttribute())
                    return false;

                return physicalCard.getBlueprint().getAbility() == ability;
            }
        };
    }

    /**
     * Filter that accepts cards that have ability = X.
     *
     * @param ability the value of X
     * @return Filter
     */
    public static Filter abilityEqualTo(final float ability) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.hasAbilityEqualTo(gameState, physicalCard, ability);
            }
        };
    }

    /**
     * Filter that accepts cards that have ability >= X.
     *
     * @param ability the value of X
     * @return Filter
     */
    public static Filter abilityMoreThanOrEqualTo(final float ability) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.getAbility(gameState, physicalCard) >= ability;
            }
        };
    }

    /**
     * Filter that accepts cards that have ability > X.
     *
     * @param ability the value of X
     * @return Filter
     */
    public static Filter abilityMoreThan(final float ability) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.hasAbilityMoreThan(gameState, physicalCard, ability, false);
            }
        };
    }

    /**
     * Filter that accepts cards that have ability > X.
     *
     * @param ability the value of X
     * @param includePermPilots true if ability of permanent pilots is included, otherwise false
     * @return Filter
     */
    public static Filter abilityMoreThan(final float ability, final boolean includePermPilots) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.hasAbilityMoreThan(gameState, physicalCard, ability, includePermPilots);
            }
        };
    }

    /**
     * Filter that accepts cards are are the highest ability character owned by the player.
     *
     * @param source the card that is requesting this filter
     * @param playerId the player owning the character
     * @return Filter
     */
    public static Filter highestAbilityCharacter(PhysicalCard source, final String playerId) {
        final Integer permSourceCardId = source != null ? source.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);

                return modifiersQuerying.isPlayersHighestAbilityCharacter(gameState, source, physicalCard, playerId);
            }
        };
    }

    /**
     * Filter that accepts cards are are not excluded from being the highest ability character from the perspective of
     * the source card.
     * @param source the card that is requesting this filter
     * @return Filter
     */
    public static Filter notExcludedFromBeingHighestAbilityCharacter(PhysicalCard source) {
        final Integer permSourceCardId = source != null ? source.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);

                return !modifiersQuerying.excludedFromBeingHighestAbilityCharacter(gameState, source, physicalCard);
            }
        };
    }

    /**
     * Filter that accepts cards are are not prevented applying ability for Sense or Alter destiny.
     */
    public static final Filter notPreventedFromApplyingAbilityForSenseAlterDestiny = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return !modifiersQuerying.mayNotApplyAbilityForSenseOrAlterDestiny(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter notPreventedFromApplyingAbilityForSenseAlterDestiny() {
        return notPreventedFromApplyingAbilityForSenseAlterDestiny;
    }

    /**
     * Filter that accepts cards that have their ability-1 permanent pilot replaced.
     */
    public static final Filter isAbility1PermanentPilotReplaced = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.isAbility1PermanentPilotReplaced(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter isAbility1PermanentPilotReplaced() {
        return isAbility1PermanentPilotReplaced;
    }

    //
    //
    // This section defines Filters based on politics.
    //
    //

    /**
     * Filter that accepts cards that have politics = X.
     *
     * @param politics the value of X
     * @return Filter
     */
    public static Filter politicsEqualTo(final float politics) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.hasPoliticsEqualTo(gameState, physicalCard, politics);
            }
        };
    }

    /**
     * Filter that accepts cards that have politics > X.
     *
     * @param politics the value of X
     * @return Filter
     */
    public static Filter politicsMoreThan(final int politics) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.hasPoliticsMoreThan(gameState, physicalCard, politics);
            }
        };
    }

    //
    //
    // This section defines Filters based on maneuver.
    //
    //

    /**
     * Filter that accepts cards that have a maneuver value defined in blueprint.
     */
    public static final Filter hasManeuverDefined = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.getBlueprint().hasManeuverAttribute()
                    && physicalCard.getBlueprint().getManeuver() != null;
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter hasManeuverDefined() {
        return hasManeuverDefined;
    }

    /**
     * Filter that accepts cards that have maneuver.
     */
    public static final Filter hasManeuver = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.hasManeuver(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter hasManeuver() {
        return hasManeuver;
    }


    /**
     * Filter that accepts cards that have maneuver > X.
     *
     * @param maneuver the value of X
     * @return Filter
     */
    public static Filter maneuverMoreThan(final int maneuver) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.hasManeuverMoreThan(gameState, physicalCard, maneuver);
            }
        };
    }

    /**
     * Filter that accepts cards that have higher maneuver than the specified card.
     *
     * @param card a card
     * @return Filter
     */
    public static Filter maneuverHigherThanManeuverOf(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);

                return modifiersQuerying.hasManeuverMoreThan(gameState, physicalCard, modifiersQuerying.getManeuver(gameState, card));
            }
        };
    }

    /**
     * Filter that accepts cards that have lower maneuver than a card accepted by the specified filter.
     *
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter maneuverLowerThanManeuverOf(PhysicalCard source, final Filter filters) {
        final Integer permSourceCardId = source != null ? source.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);

                if (!physicalCard.getBlueprint().hasManeuverAttribute())
                    return false;

                Float highestManeuver = null;
                Collection<PhysicalCard> cards = Filters.filterActive(gameState.getGame(), source, filters);
                for (PhysicalCard card : cards) {
                    float maneuver = modifiersQuerying.getManeuver(gameState, card);
                    if (highestManeuver == null || highestManeuver < maneuver) {
                        highestManeuver = maneuver;
                    }
                }

                if (highestManeuver == null)
                    return false;

                float curManeuver = modifiersQuerying.getManeuver(gameState, physicalCard);

                return (curManeuver < highestManeuver);
            }
        };
    }

    //
    //
    // This section defines Filters based on armor.
    //
    //

    /**
     * Filter that accepts cards that have an armor value defined in blueprint.
     */
    public static final Filter hasArmorDefined = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.getBlueprint().hasArmorAttribute()
                    && physicalCard.getBlueprint().getArmor() != null;
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter hasArmorDefined() {
        return hasArmorDefined;
    }

    /**
     * Filter that accepts cards that have armor.
     */
    public static final Filter hasArmor = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.hasArmor(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter hasArmor() {
        return hasArmor;
    }

    /**
     * Filter that accepts cards that have no armor.
     */
    public static final Filter hasNoArmor = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return !modifiersQuerying.hasArmor(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter hasNoArmor() {
        return hasNoArmor;
    }


    //
    //
    // This section defines Filters based on hyperspeed.
    //
    //

    /**
     * Filter that accepts cards that have a hyperdrive.
     */
    public static final Filter hasHyperdrive = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return !modifiersQuerying.hasNoHyperdrive(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter hasHyperdrive() {
        return hasHyperdrive;
    }

    /**
     * Filter that accepts cards that have no hyperdrive.
     */
    public static final Filter hasNoHyperdrive = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.hasNoHyperdrive(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter hasNoHyperdrive() {
        return hasNoHyperdrive;
    }

    //
    //
    // This section defines Filters based on landspeed.
    //
    //

    /**
     * Filter that accepts cards that have landspeed.
     */
    public static final Filter hasLandspeed = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.hasLandspeed(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter hasLandspeed() {
        return hasLandspeed;
    }

    /**
     * Filter that accepts cards that have landspeed > X.
     *
     * @param landspeed the value of X
     * @return Filter
     */
    public static Filter landspeedMoreThan(final int landspeed) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.hasLandspeedMoreThan(gameState, physicalCard, landspeed);
            }
        };
    }

    //
    //
    // This section defines Filters based on immunity to attrition.
    //
    //

    /**
     * Filter that accepts cards that have any immunity to attrition.
     */
    public static final Filter hasAnyImmunityToAttrition = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.hasAnyImmunityToAttrition(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter hasAnyImmunityToAttrition() {
        return hasAnyImmunityToAttrition;
    }

    /**
     * Filter that accepts cards that already have immunity to attrition (when ignoring immunity granted by specified card).
     * @param sourceToIgnore the card
     * @return Filter
     */
    public static Filter alreadyHasImmunityToAttrition(PhysicalCard sourceToIgnore) {
        final Integer permSourceToIgnoreCardId = sourceToIgnore.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard sourceToIgnore = gameState.findCardByPermanentId(permSourceToIgnoreCardId);

                return modifiersQuerying.alreadyHasImmunityToAttrition(gameState, physicalCard, sourceToIgnore);
            }
        };
    }

    /**
     * Filter that accepts cards that are not prevented from satisfying attrition.
     */
    public static final Filter notPreventedFromSatisfyingAttrition = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return !modifiersQuerying.cannotSatisfyAttrition(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter notPreventedFromSatisfyingAttrition() {
        return notPreventedFromSatisfyingAttrition;
    }


    //
    //
    // This section defines Filters used for Force retrieval.
    //
    //

    /**
     * Filter that accepts cards that are not prevented from contributing to Force retrieval.
     */
    public static final Filter mayContributeToForceRetrieval = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return !modifiersQuerying.mayNotContributeToForceRetrieval(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter mayContributeToForceRetrieval() {
        return mayContributeToForceRetrieval;
    }

    /**
     * Filter that accepts cards that are not prevented from contributing to Force retrieval.
     */
    public static Filter playersCardsAtLocationMayContributeToForceRetrieval(final String playerId) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return !modifiersQuerying.playersCardsAtLocationMayNotContributeToForceRetrieval(gameState, physicalCard, playerId);
            }
        };
    };

    //
    //
    // This section defines Filters used for locations.
    //
    //

    /**
     * Filter that accepts cards that are locations occupied by the specified player.
     *
     * @param playerId the player
     * @return Filter
     */
    public static Filter occupies(final String playerId) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.occupiesLocation(gameState, physicalCard, playerId);
            }
        };
    }

    /**
     * Filter that accepts cards that are locations occupied by the specified player.
     *
     * @param playerId the player
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @return Filter
     */
    public static Filter occupies(final String playerId, final Map<InactiveReason, Boolean> spotOverrides) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.occupiesLocation(gameState, physicalCard, playerId, spotOverrides);
            }
        };
    }

    /**
     * Filter that accepts cards that are locations occupied by the specified player with cards accepted by the specified
     * filter.
     *
     * @param playerId the player
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter occupiesWith(final String playerId, PhysicalCard source, final Filter filters) {
        return Filters.and(Filters.occupies(playerId), Filters.sameLocationAs(source, Filters.and(Filters.owner(playerId), filters)));
    }

    /**
     * Filter that accepts cards that are locations occupied by the specified player with cards accepted by the specified
     * filter.
     *
     * @param playerId the player
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @param filters the filters
     * @return Filter
     */
    public static Filter occupiesWith(final String playerId, PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final Filter filters) {
        return Filters.and(Filters.occupies(playerId, spotOverrides), Filters.sameLocationAs(source, spotOverrides, Filters.and(Filters.owner(playerId), filters)));
    }

    /**
     * Filter that accepts cards that are unoccupied locations.
     */
    public static final Filter unoccupied = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return !modifiersQuerying.occupiesLocation(gameState, physicalCard, gameState.getDarkPlayer())
                    && !modifiersQuerying.occupiesLocation(gameState, physicalCard, gameState.getLightPlayer());
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter unoccupied() {
        return unoccupied;
    }

    /**
     * Filter that accepts cards that are locations controlled by the specified player.
     *
     * @param playerId the player
     * @return Filter
     */
    public static Filter controls(final String playerId) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.controlsLocation(gameState, physicalCard, playerId);
            }
        };
    }

    /**
     * Filter that accepts cards that are locations controlled by the specified player.
     *
     * @param playerId the player
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @return Filter
     */
    public static Filter controls(final String playerId, final Map<InactiveReason, Boolean> spotOverrides) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.controlsLocation(gameState, physicalCard, playerId, spotOverrides);
            }
        };
    }

    /**
     * Filter that accepts cards that are locations controlled for the purposes of Force draining by the specified player.
     *
     * @param playerId the player
     * @return Filter
     */
    public static Filter controlsForForceDrain(final String playerId) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getZone()!=Zone.LOCATIONS)
                    return false;

                if (Filters.canSpot(gameState.getGame(), null, SpotOverride.INCLUDE_UNDERCOVER,
                        Filters.and(Filters.opponents(playerId), Filters.undercover_spy, Filters.at(physicalCard))))
                    return false;

                if (modifiersQuerying.controlsLocation(gameState, physicalCard, playerId))
                    return true;

                if (modifiersQuerying.occupiesLocation(gameState, physicalCard, gameState.getOpponent(playerId)))
                    return false;

                if (Filters.canSpot(gameState.getGame(), null,
                        Filters.and(Filters.owner(playerId), Filters.mayForceDrain, Filters.at(physicalCard))))
                    return true;

                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that are locations controlled by the specified player with cards accepted by the specified
     * filter.
     *
     * @param playerId the player
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter controlsWith(final String playerId, PhysicalCard source, final Filter filters) {
        return Filters.and(Filters.controls(playerId), Filters.sameLocationAs(source, Filters.and(Filters.owner(playerId), filters)));
    }

    /**
     * Filter that accepts cards that are battleground locations.
     */
    public static final Filter battleground = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.isBattleground(gameState, physicalCard, null);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter battleground() {
        return battleground;
    }

    /**
     * Filter that accepts cards that are battleground locations (ignoring Force icons added from source card).
     *
     * @param source the card that is performing this query
     * @return Filter
     */
    public static Filter battlegroundIgnoringForceIconsAddedFromCard(PhysicalCard source) {
        final Integer permSourceToIgnoreCardId = source.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard source = gameState.findCardByPermanentId(permSourceToIgnoreCardId);
                return modifiersQuerying.isBattleground(gameState, physicalCard, source);
            }
        };
    }

    /**
     * Filter that accepts a card that is the attack location.
     */
    public static final Filter attackLocation = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return gameState.isDuringAttack() &&
                    gameState.getAttackState().getAttackLocation().getCardId() == physicalCard.getCardId();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter attackLocation() {
        return attackLocation;
    }

    /**
     * Filter that accepts a card that is the battle location.
     */
    public static final Filter battleLocation = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.isBattleLocation(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter battleLocation() {
        return battleLocation;
    }

    /**
     * Filter that accepts a card that is the Force drain location.
     */
    public static final Filter forceDrainLocation = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.isForceDrainLocation(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter forceDrainLocation() {
        return forceDrainLocation;
    }

    /**
     * Filter that accepts cards that are locations that the specified starship may deploy as landed to.
     *
     * @param starship a starship
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @return Filter
     */
    public static Filter locationStarshipMayDeployAsLanded(PhysicalCard starship, final DeploymentRestrictionsOption deploymentRestrictionsOption) {
        final Integer permStarshipCardId = starship.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard starship = gameState.findCardByPermanentId(permStarshipCardId);

                return modifiersQuerying.isLocationStarshipMayDeployToAsLanded(gameState, physicalCard, starship, deploymentRestrictionsOption);
            }
        };
    }


    //
    //
    //
    // Filters for "other locations"
    //
    //
    //

    /**
     * Filter that accepts locations that are not location the specified card is "at" (or not the same location if the specified
     * card is a location).
     *
     * @param card a card
     * @return Filter
     */
    public static Filter otherLocation(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION)
                    return false;

                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                PhysicalCard location = modifiersQuerying.getLocationHere(gameState, card);

                return location != null
                        && !Filters.sameCardId(location).accepts(gameState, modifiersQuerying, physicalCard);
            }
        };
    }

    //
    //
    //
    // Filters for "same location"
    //
    //
    //

    /**
     * Filter that accepts a card that is the location the specified card is "at", or the location itself.
     *
     * @param card a card
     * @return Filter
     */
    public static Filter sameLocation(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION)
                    return false;

                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                PhysicalCard location = modifiersQuerying.getLocationHere(gameState, card);

                return location != null
                        && Filters.sameCardId(location).accepts(gameState, modifiersQuerying, physicalCard);
            }
        };
    }

    /**
     * Filter that accepts cards that are locations that a card accepted by the specified filter is "at", or the locations
     * themselves.
     *
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter sameLocationAs(PhysicalCard source, final Filter filters) {
        return sameLocationAs(source, (Map<InactiveReason, Boolean>) null, filters);
    }

    /**
     * Filter that accepts cards that are locations that a card accepted by the specified filter is "at", or the locations
     * themselves.
     *
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @param filters the filters
     * @return Filter
     */
    public static Filter sameLocationAs(PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final Filter filters) {
        final Integer permSourceCardId = source != null ? source.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION)
                    return false;

                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);
                Filter filterToUse = Filters.or(filters, Filters.hasPermanentAboard(filters), Filters.hasPermanentWeapon(filters));
                Collection<PhysicalCard> locations = modifiersQuerying.getLocationsHere(gameState, filterActive(gameState.getGame(), source, spotOverrides, filterToUse));

                if (locations.contains(physicalCard))
                    return true;

                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that are locations that a card accepted by the specified filter is "at", or the locations
     * themselves.
     *
     * @param source the card that is performing this query
     * @param targetingReason the reason the source card is targeting
     * @param filters the filters
     * @return Filter
     */
    public static Filter sameLocationAs(PhysicalCard source, final TargetingReason targetingReason, final Filter filters) {
        final Integer permSourceCardId = source != null ? source.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION)
                    return false;

                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);
                Filter filterToUse = Filters.or(filters, Filters.hasPermanentAboard(filters), Filters.hasPermanentWeapon(filters));
                Collection<PhysicalCard> locations = modifiersQuerying.getLocationsHere(gameState, filterActive(gameState.getGame(), source, null, targetingReason, filterToUse));

                if (locations.contains(physicalCard))
                    return true;

                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that are "at" the same location that the specified card is "at", or "at" the specified
     * location.
     *
     * @param card a card
     * @return Filter
     */
    public static Filter atSameLocation(PhysicalCard card) {
        return Filters.at(Filters.sameLocation(card));
    }

    /**
     * Filter that accepts a card that is the sector the specified card is "at", or the sector itself.
     *
     * @param card a card
     * @return Filter
     */
    public static Filter sameSector(PhysicalCard card) {
        return Filters.and(Filters.sector, Filters.sameLocation(card));
    }

    /**
     * Filter that accepts cards that are sectors that a card accepted by the specified filter is "at", or the sectors
     * themselves.
     *
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter sameSectorAs(PhysicalCard source, final Filter filters) {
        return sameSectorAs(source, null, filters);
    }

    /**
     * Filter that accepts cards that are sectors that a card accepted by the specified filter is "at", or the sectors
     * themselves.
     *
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @param filters the filters
     * @return Filter
     */
    public static Filter sameSectorAs(PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final Filter filters) {
        return Filters.and(Filters.sector, Filters.sameLocationAs(source, spotOverrides, filters));
    }

    /**
     * Filter that accepts cards that are "at" the same sector that the specified card is "at", or "at" the specified
     * sector.
     *
     * @param card a card
     * @return Filter
     */
    public static Filter atSameSector(PhysicalCard card) {
        return Filters.at(Filters.sameSector(card));
    }

    /**
     * Filter that accepts a card that is the site the specified card is "at", or the location itself.
     *
     * @param card a card
     * @return Filter
     */
    public static Filter sameSite(PhysicalCard card) {
        return Filters.and(Filters.site, Filters.sameLocation(card));
    }

    /**
     * Filter that accepts cards that are sites that a card accepted by the specified filter is "at", or the sites
     * themselves.
     *
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter sameSiteAs(PhysicalCard source, final Filter filters) {
        return sameSiteAs(source, null, filters);
    }

    /**
     * Filter that accepts cards that are sites that a card accepted by the specified filter is "at", or the sites
     * themselves.
     *
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @param filters the filters
     * @return Filter
     */
    public static Filter sameSiteAs(PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final Filter filters) {
        return Filters.and(Filters.site, Filters.sameLocationAs(source, spotOverrides, filters));
    }

    /**
     * Filter that accepts cards that are "at" the same site that the specified card is "at", or "at" the specified
     * site.
     *
     * @param card a card
     * @return Filter
     */
    public static Filter atSameSite(PhysicalCard card) {
        return Filters.at(Filters.sameSite(card));
    }

    /**
     * Filter that accepts a card that is the system the specified card is "at", or the system itself.
     *
     * @param card a card
     * @return Filter
     */
    public static Filter sameSystem(PhysicalCard card) {
        return Filters.and(Filters.system, Filters.sameLocation(card));
    }

    /**
     * Filter that accepts cards that are systems that a card accepted by the specified filter is "at", or the systems
     * themselves.
     *
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter sameSystemAs(PhysicalCard source, final Filter filters) {
        return sameSystemAs(source, null, filters);
    }

    /**
     * Filter that accepts cards that are systems that a card accepted by the specified filter is "at", or the systems
     * themselves.
     *
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @param filters the filters
     * @return Filter
     */
    public static Filter sameSystemAs(PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final Filter filters) {
        return Filters.and(Filters.system, Filters.sameLocationAs(source, spotOverrides, filters));
    }

    /**
     * Filter that accepts cards that are "at" the same system that the specified card is "at", or "at" the specified
     * system.
     *
     * @param card a card
     * @return Filter
     */
    public static Filter atSameSystem(PhysicalCard card) {
        return Filters.at(Filters.sameSystem(card));
    }

    /**
     * Filter that accepts a card that is the system or sector the specified card is "at", or the system or sector itself.
     *
     * @param card a card
     * @return Filter
     */
    public static Filter sameSystemOrSector(PhysicalCard card) {
        return Filters.and(Filters.system_or_sector, Filters.sameLocation(card));
    }

    /**
     * Filter that accepts cards that are systems or sectors that a card accepted by the specified filter is "at", or the
     * systems or sectors themselves.
     *
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter sameSystemOrSectorAs(PhysicalCard source, final Filter filters) {
        return sameSystemOrSectorAs(source, null, filters);
    }

    /**
     * Filter that accepts cards that are systems or sectors that a card accepted by the specified filter is "at", or the
     * systems or sectors themselves.
     *
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @param filters the filters
     * @return Filter
     */
    public static Filter sameSystemOrSectorAs(PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final Filter filters) {
        return Filters.and(Filters.system_or_sector, Filters.sameLocationAs(source, spotOverrides, filters));
    }

    /**
     * Filter that accepts cards that are "at" the same system or sector that the specified card is "at", or "at" the specified
     * system or sector.
     *
     * @param card a card
     * @return Filter
     */
    public static Filter atSameSystemOrSector(PhysicalCard card) {
        return Filters.at(Filters.sameSystemOrSector(card));
    }

    //
    //
    //
    // Filters for "another location"
    //
    //
    //

    /**
     * Filter that accepts a card that is a different location than the specified card is "at", or another location than
     * the location itself.
     *
     * @param card a card
     * @return Filter
     */
    public static Filter anotherLocation(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION)
                    return false;

                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                PhysicalCard location = modifiersQuerying.getLocationHere(gameState, card);

                return location != null
                        && !Filters.sameCardId(location).accepts(gameState, modifiersQuerying, physicalCard);
            }
        };
    }

    //
    //
    //
    // Filters for "related location"
    //
    //
    //

    /**
     * Filter that accepts cards that are either related locations to the specified card, or related locations to the
     * location the specified card is "at".
     *
     * @param card a card
     * @return Filter
     */
    public static Filter relatedLocation(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION)
                    return false;

                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                boolean isStarshipOrVehicle = Filters.or(Filters.starship, Filters.vehicle).accepts(gameState, modifiersQuerying, card);
                PhysicalCard location = modifiersQuerying.getLocationHere(gameState, card);

                // If input card is a starship or vehicle, check if the current card is a starship or vehicle site of
                // that starship or vehicle.
                if (isStarshipOrVehicle
                        && modifiersQuerying.isRelatedStarshipOrVehicleSite(gameState, card, physicalCard))
                    return true;

                if (location != null
                        && modifiersQuerying.isRelatedLocations(gameState, location, physicalCard))
                    return true;

                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that are either related locations to any of the specified cards, or related locations to any of the
     * locations the specified cards are "at".
     *
     * @param inputCards cards
     * @return Filter
     */
    public static Filter relatedLocation(Collection<PhysicalCard> inputCards) {
        final List<Integer> inputPermCardIds = new LinkedList<Integer>();
        for (PhysicalCard inputCard : inputCards) {
            inputPermCardIds.add(inputCard.getPermanentCardId());
        }
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION)
                    return false;

                final List<PhysicalCard> inputCards = new LinkedList<PhysicalCard>();
                for (Integer inputPermCardId : inputPermCardIds) {
                    inputCards.add(gameState.findCardByPermanentId(inputPermCardId));
                }

                Collection<PhysicalCard> starshipsOrVehicles = Filters.filter(inputCards, gameState.getGame(), Filters.or(Filters.starship, Filters.vehicle));
                for (PhysicalCard starshipOrVehicle : starshipsOrVehicles) {
                    if (modifiersQuerying.isRelatedStarshipOrVehicleSite(gameState, starshipOrVehicle, physicalCard))
                        return true;
                }

                Collection<PhysicalCard> locations = modifiersQuerying.getLocationsHere(gameState, inputCards);
                for (PhysicalCard location : locations) {
                    if (modifiersQuerying.isRelatedLocations(gameState, location, physicalCard))
                        return true;
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that are either related locations to cards that are accepted by the specified filter,
     * or related locations to the locations that a card accepted by the specified filter is "at".
     *
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter relatedLocationTo(PhysicalCard source, final Filter filters) {
        return relatedLocationTo(source, null, filters);
    }

    /**
     * Filter that accepts cards that are either related locations to cards that are accepted by the specified filter,
     * or related locations to the locations that a card accepted by the specified filter is "at".
     *
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @param filters the filters
     * @return Filter
     */
    public static Filter relatedLocationTo(PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final Filter filters) {
        final Integer permSourceCardId = source != null ? source.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION)
                    return false;

                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);

                Collection<PhysicalCard> cards = new HashSet<PhysicalCard>();
                cards.addAll(filterActive(gameState.getGame(), source, spotOverrides, Filters.and(Filters.or(Filters.starship, Filters.vehicle), filters)));
                Filter filterToUse = Filters.or(filters, Filters.hasPermanentAboard(filters), Filters.hasPermanentWeapon(filters));
                cards.addAll(modifiersQuerying.getLocationsHere(gameState, filterActive(gameState.getGame(), source, spotOverrides, filterToUse)));

                for (PhysicalCard card : cards) {
                    if (Filters.relatedLocation(card).accepts(gameState, modifiersQuerying, physicalCard)) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that are either related asteroid sectors to the specified card, or related asteroid sectors to the
     * location the specified card is "at".
     *
     * @param card a card
     * @return Filter
     */
    public static Filter relatedAsteroidSector(PhysicalCard card) {
        return Filters.and(Filters.asteroid_sector, Filters.relatedLocation(card));
    }

    /**
     * Filter that accepts cards that are either related asteroid sectors to cards that are accepted by the specified filter,
     * or related asteroid sectors to the locations that a card accepted by the specified filter is "at".
     *
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter relatedAsteroidSectorTo(PhysicalCard source, final Filter filters) {
        return relatedAsteroidSectorTo(source, null, filters);
    }

    /**
     * Filter that accepts cards that are either related asteroid sectors to cards that are accepted by the specified filter,
     * or related asteroid sectors to the locations that a card accepted by the specified filter is "at".
     *
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @param filters the filters
     * @return Filter
     */
    public static Filter relatedAsteroidSectorTo(PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final Filter filters) {
        return Filters.and(Filters.asteroid_sector, Filters.relatedLocationTo(source, spotOverrides, filters));
    }

    /**
     * Filter that accepts cards that are either related cloud sectors to the specified card, or related cloud sectors to the
     * location the specified card is "at".
     *
     * @param card a card
     * @return Filter
     */
    public static Filter relatedCloudSector(PhysicalCard card) {
        return Filters.and(Filters.cloud_sector, Filters.relatedLocation(card));
    }

    /**
     * Filter that accepts cards that are either related cloud sectors to cards that are accepted by the specified filter,
     * or related cloud sectors to the locations that a card accepted by the specified filter is "at".
     *
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter relatedCloudSectorTo(PhysicalCard source, final Filter filters) {
        return relatedCloudSectorTo(source, null, filters);
    }

    /**
     * Filter that accepts cards that are either related cloud sectors to cards that are accepted by the specified filter,
     * or related cloud sectors to the locations that a card accepted by the specified filter is "at".
     *
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @param filters the filters
     * @return Filter
     */
    public static Filter relatedCloudSectorTo(PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final Filter filters) {
        return Filters.and(Filters.cloud_sector, Filters.relatedLocationTo(source, spotOverrides, filters));
    }

    /**
     * Filter that accepts cards that are either related sectors to the specified card, or related sectors to the
     * location the specified card is "at".
     *
     * @param card a card
     * @return Filter
     */
    public static Filter relatedSector(PhysicalCard card) {
        return Filters.and(Filters.sector, Filters.relatedLocation(card));
    }

    /**
     * Filter that accepts cards that are either related sectors to cards that are accepted by the specified filter,
     * or related sectors to the locations that a card accepted by the specified filter is "at".
     *
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter relatedSectorTo(PhysicalCard source, final Filter filters) {
        return relatedSectorTo(source, null, filters);
    }

    /**
     * Filter that accepts cards that are either related sectors to cards that are accepted by the specified filter,
     * or related sectors to the locations that a card accepted by the specified filter is "at".
     *
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @param filters the filters
     * @return Filter
     */
    public static Filter relatedSectorTo(PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final Filter filters) {
        return Filters.and(Filters.sector, Filters.relatedLocationTo(source, spotOverrides, filters));
    }

    /**
     * Filter that accepts cards that are either related sites to the specified card, or related sites to the
     * location the specified card is "at".
     *
     * @param card a card
     * @return Filter
     */
    public static Filter relatedSite(PhysicalCard card) {
        return Filters.and(Filters.site, Filters.relatedLocation(card));
    }

    /**
     * Filter that accepts cards that are either related sites to cards that are accepted by the specified filter,
     * or related sites to the locations that a card accepted by the specified filter is "at".
     *
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter relatedSiteTo(PhysicalCard source, final Filter filters) {
        return relatedSiteTo(source, null, filters);
    }

    /**
     * Filter that accepts cards that are either related sites to cards that are accepted by the specified filter,
     * or related sites to the locations that a card accepted by the specified filter is "at".
     *
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @param filters the filters
     * @return Filter
     */
    public static Filter relatedSiteTo(PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final Filter filters) {
        return Filters.and(Filters.site, Filters.relatedLocationTo(source, spotOverrides, filters));
    }

    /**
     * Filter that accepts cards that are either related systems to the specified card, or related systems to the
     * location the specified card is "at".
     *
     * @param card a card
     * @return Filter
     */
    public static Filter relatedSystem(PhysicalCard card) {
        return Filters.and(Filters.system, Filters.relatedLocation(card));
    }

    /**
     * Filter that accepts cards that are either related systems to cards that are accepted by the specified filter,
     * or related systems to the locations that a card accepted by the specified filter is "at".
     *
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter relatedSystemTo(PhysicalCard source, final Filter filters) {
        return relatedSystemTo(source, null, filters);
    }

    /**
     * Filter that accepts cards that are either related systems to cards that are accepted by the specified filter,
     * or related systems to the locations that a card accepted by the specified filter is "at".
     *
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @param filters the filters
     * @return Filter
     */
    public static Filter relatedSystemTo(PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final Filter filters) {
        return Filters.and(Filters.system, Filters.relatedLocationTo(source, spotOverrides, filters));
    }

    /**
     * Filter that accepts cards that are either related systems or sectors to the specified card, or related systems or sectors to the
     * location the specified card is "at".
     *
     * @param card a card
     * @return Filter
     */
    public static Filter relatedSystemOrSector(PhysicalCard card) {
        return Filters.and(Filters.system_or_sector, Filters.relatedLocation(card));
    }

    /**
     * Filter that accepts cards that are either related systems or sectors to cards that are accepted by the specified filter,
     * or related systems or sectors to the locations that a card accepted by the specified filter is "at".
     *
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter relatedSystemOrSectorTo(PhysicalCard source, final Filter filters) {
        return relatedSystemOrSectorTo(source, null, filters);
    }

    /**
     * Filter that accepts cards that are either related systems or sectors to cards that are accepted by the specified filter,
     * or related systems or sectors to the locations that a card accepted by the specified filter is "at".
     *
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @param filters the filters
     * @return Filter
     */
    public static Filter relatedSystemOrSectorTo(PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final Filter filters) {
        return Filters.and(Filters.system_or_sector, Filters.relatedLocationTo(source, spotOverrides, filters));
    }

    /**
     * Filter that accepts card the card that is Big One related to the specified card, or related Big One to the
     * location the specified card is "at".
     *
     * @param card a card
     * @return Filter
     */
    public static Filter relatedBigOne(PhysicalCard card) {
        return Filters.and(Filters.Big_One, Filters.relatedLocation(card));
    }

    //
    //
    //
    // Filters for "nearest related"
    //
    //
    //

    /**
     * Filter that accept cards that are the nearest related sector to the specified system location (or the system
     * location that the specified card is "at").
     *
     * @param card a card
     * @return Filter
     */
    public static Filter nearestRelatedSector(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardSubtype() != CardSubtype.SECTOR)
                    return false;

                String relatedSystemName = physicalCard.getPartOfSystem() != null ? physicalCard.getPartOfSystem() : physicalCard.getSystemOrbited();
                if (relatedSystemName == null)
                    return false;

                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                PhysicalCard location = modifiersQuerying.getLocationHere(gameState, card);
                if (location == null
                        || location.getBlueprint().getCardSubtype() != CardSubtype.SYSTEM)
                    return false;

                if (!location.getTitle().equals(relatedSystemName))
                    return false;

                // Check if the two locations are next to each other
                int systemIndex = location.getLocationZoneIndex();
                int sectorIndex = physicalCard.getLocationZoneIndex();

                return Math.abs(sectorIndex - systemIndex) == 1;
            }
        };
    }

    /**
     * Filter that accept cards that are the nearest related asteroid sector to the specified system location (or the system
     * location that the specified card is "at").
     *
     * @param card a card
     * @return Filter
     */
    public static Filter nearestRelatedAsteroidSector(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (!physicalCard.getBlueprint().hasKeyword(Keyword.ASTEROID))
                    return false;

                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return nearestRelatedSector(card).accepts(gameState, modifiersQuerying, physicalCard);
            }
        };
    }

    /**
     * Filter that accept cards that are the nearest related cloud sector to the specified system location (or the system
     * location that the specified card is "at").
     *
     * @param card a card
     * @return Filter
     */
    public static Filter nearestRelatedCloudSector(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (!physicalCard.getBlueprint().hasKeyword(Keyword.CLOUD_SECTOR))
                    return false;

                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return nearestRelatedSector(card).accepts(gameState, modifiersQuerying, physicalCard);
            }
        };
    }

    /**
     * Filter that accept cards that are the nearest related Death Star II sector to the specified system location (or the system
     * location that the specified card is "at").
     *
     * @param card a card
     * @return Filter
     */
    public static Filter nearestRelatedDeathStarIISector(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (!Filters.Death_Star_II_sector.accepts(gameState, modifiersQuerying, physicalCard))
                    return false;

                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return nearestRelatedSector(card).accepts(gameState, modifiersQuerying, physicalCard);
            }
        };
    }

    /**
     * Filter that accept cards that are the nearest related exterior site to the specified site (or the site that the
     * specified card is "at").
     *
     * @param card a card
     * @return Filter
     */
    public static Filter nearestRelatedExteriorSite(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (!Filters.exterior_site.accepts(gameState, modifiersQuerying, physicalCard))
                    return false;

                String relatedSystemName = physicalCard.getPartOfSystem();
                if (relatedSystemName == null)
                    return false;

                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                PhysicalCard location = modifiersQuerying.getLocationHere(gameState, card);
                if (location == null
                        || location.getBlueprint().getCardSubtype() != CardSubtype.SITE
                        || !relatedSystemName.equals(location.getPartOfSystem()))
                    return false;

                int distance = Math.abs(location.getLocationZoneIndex() - physicalCard.getLocationZoneIndex());
                if (distance == 1) {
                    return true;
                }
                // Check if there is another related exterior site closer (on either side)
                Collection<PhysicalCard> locationsToCheck = Filters.filterTopLocationsOnTable(gameState.getGame(), Filters.and(Filters.exterior_site, Filters.relatedSite(location)));
                for (PhysicalCard locationToCheck : locationsToCheck) {
                    int curDistance = Math.abs(location.getLocationZoneIndex() - physicalCard.getLocationZoneIndex());
                    if (curDistance < distance) {
                        return false;
                    }
                }

                return true;
            }
        };
    }


    //
    //
    //
    // Filters for "adjacent sector"
    //
    //
    //

    /**
     * Filter that accepts cards that are either adjacent sectors to the specified card, or adjacent sectors to the
     * site the specified card is "at".
     *
     * @param card a card
     * @return Filter
     */
    public static Filter adjacentSector(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardSubtype() != CardSubtype.SECTOR)
                    return false;

                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                PhysicalCard location = modifiersQuerying.getLocationHere(gameState, card);

                if (location == null
                        || location.getBlueprint().getCardSubtype() != CardSubtype.SECTOR)
                    return false;

                return modifiersQuerying.isAdjacentSectors(gameState, location, physicalCard);
            }
        };
    }

    /**
     * Filter that accepts cards that are either adjacent sectors to cards that are accepted by the specified filter,
     * or adjacent sectors to the sector that a card accepted by the specified filter is "at".
     *
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter adjacentSectorTo(PhysicalCard source, final Filter filters) {
        return adjacentSectorTo(source, null, filters);
    }

    /**
     * Filter that accepts cards that are either adjacent sectors to cards that are accepted by the specified filter,
     * or adjacent sectors to the sectors that a card accepted by the specified filter is "at".
     *
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @param filters the filters
     * @return Filter
     */
    public static Filter adjacentSectorTo(PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final Filter filters) {
        final Integer permSourceCardId = source != null ? source.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardSubtype() != CardSubtype.SECTOR)
                    return false;

                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);
                Filter filterToUse = Filters.or(filters, Filters.hasPermanentAboard(filters), Filters.hasPermanentWeapon(filters));
                Collection<PhysicalCard> locations = modifiersQuerying.getLocationsHere(gameState, filterActive(gameState.getGame(), source, spotOverrides, filterToUse));

                for (PhysicalCard location : locations) {
                    if (location.getBlueprint().getCardSubtype() == CardSubtype.SECTOR
                            && modifiersQuerying.isAdjacentSectors(gameState, location, physicalCard)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    //
    //
    //
    // Filters for "up to X sectors away"
    //
    //
    //

    /**
     * Filter that accepts cards that are either sectors up to X sectors away from the specified card, or sectors up to X sectors
     * away from the sector the specified card is "at".
     *
     * @param card a card
     * @return Filter
     */
    public static Filter sectorWithinDistance(PhysicalCard card, final int range) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardSubtype() != CardSubtype.SECTOR)
                    return false;

                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                PhysicalCard location = modifiersQuerying.getLocationHere(gameState, card);

                if (location == null
                        || location.getBlueprint().getCardSubtype() != CardSubtype.SECTOR)
                    return false;

                Integer distance = modifiersQuerying.getDistanceBetweenSectors(gameState, location, physicalCard);

                return distance != null && distance <= range;
            }
        };
    }

    //
    //
    //
    // Filters for "adjacent site"
    //
    //
    //

    /**
     * Filter that accepts cards that are either adjacent sites to the specified card, or adjacent sites to the
     * site the specified card is "at".
     *
     * @param card a card
     * @return Filter
     */
    public static Filter adjacentSite(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardSubtype() != CardSubtype.SITE)
                    return false;

                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                PhysicalCard location = modifiersQuerying.getLocationHere(gameState, card);

                if (location == null
                        || location.getBlueprint().getCardSubtype() != CardSubtype.SITE)
                    return false;

                return modifiersQuerying.isAdjacentSites(gameState, location, physicalCard);
            }
        };
    }

    /**
     * Filter that accepts cards that are either adjacent sites to cards that are accepted by the specified filter,
     * or adjacent sites to the sites that a card accepted by the specified filter is "at".
     *
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter adjacentSiteTo(PhysicalCard source, final Filter filters) {
        return adjacentSiteTo(source, null, filters);
    }

    /**
     * Filter that accepts cards that are either adjacent sites to cards that are accepted by the specified filter,
     * or adjacent sites to the sites that a card accepted by the specified filter is "at".
     *
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @param filters the filters
     * @return Filter
     */
    public static Filter adjacentSiteTo(PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final Filter filters) {
        final Integer permSourceCardId = source != null ? source.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardSubtype() != CardSubtype.SITE)
                    return false;

                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);
                Filter filterToUse = Filters.or(filters, Filters.hasPermanentAboard(filters), Filters.hasPermanentWeapon(filters));
                Collection<PhysicalCard> locations = modifiersQuerying.getLocationsHere(gameState, filterActive(gameState.getGame(), source, spotOverrides, filterToUse));

                for (PhysicalCard location : locations) {
                    if (location.getBlueprint().getCardSubtype() == CardSubtype.SITE
                            && modifiersQuerying.isAdjacentSites(gameState, location, physicalCard)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    //
    //
    //
    // Filters for "up to X sites away"
    //
    //
    //

    /**
     * Filter that accepts cards that are either sites up to X sites away from the specified card, or sites up to X sites
     * away from the site the specified card is "at".
     *
     * @param card a card
     * @return Filter
     */
    public static Filter siteWithinDistance(PhysicalCard card, final int range) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardSubtype() != CardSubtype.SITE)
                    return false;

                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                PhysicalCard location = modifiersQuerying.getLocationHere(gameState, card);

                if (location == null
                        || location.getBlueprint().getCardSubtype() != CardSubtype.SITE)
                    return false;

                Integer distance = modifiersQuerying.getDistanceBetweenSites(gameState, location, physicalCard);

                return distance != null && distance <= range;
            }
        };
    }

    //
    //
    //
    // Filters for "same or adjacent location"
    //
    //
    //

    /**
     * Filter that accepts cards that are either:  The specified card itself and adjacent sites to the specified card if
     * the specified card is a site, otherwise the site the specified card is "at" and adjacent sites to the site the specified
     * card is "at".
     *
     * @param card a card
     * @return Filter
     */
    public static Filter sameOrAdjacentSite(PhysicalCard card) {
        return Filters.and(Filters.site, Filters.or(Filters.sameSite(card), Filters.adjacentSite(card)));
    }

    /**
     * Filter that accepts cards that are either:  The sites themselves and their adjacent sites for cards accepted by the
     * specified filter that are sites, otherwise the sites and their adjacent sites that cards accepted by the specified
     * filter that are not sites are "at".
     *
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter sameOrAdjacentSiteAs(PhysicalCard source, final Filter filters) {
        return sameOrAdjacentSiteAs(source, null, filters);
    }

    /**
     * Filter that accepts cards that are either:  The sites themselves and their adjacent sites for cards accepted by the
     * specified filter that are sites, otherwise the sites and their adjacent sites that cards accepted by the specified
     * filter that are not sites are "at".
     *
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @param filters the filters
     * @return Filter
     */
    public static Filter sameOrAdjacentSiteAs(PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final Filter filters) {
        return Filters.and(Filters.site, Filters.or(Filters.sameSiteAs(source, spotOverrides, filters), Filters.adjacentSiteTo(source, spotOverrides, filters)));
    }

    /**
     * Filter that accepts cards that are either:  "At" the specified site itself and "at" adjacent sites to the specified card if
     * the specified card is a site, otherwise "at" the site the specified card is "at" and "at" adjacent sites to the site the specified
     * card is "at".
     *
     * @param card a card
     * @return Filter
     */
    public static Filter atSameOrAdjacentSite(PhysicalCard card) {
        return Filters.at(Filters.sameOrAdjacentSite(card));
    }

    //
    //
    //
    // Filters for "same or related location"
    //
    //
    //

    /**
     * Filter that accepts cards that are either:  The specified card itself and related sites to the specified card if
     * the specified card is a site, otherwise the site the specified card is "at" and related sites to the location the
     * specified card is "at".
     *
     * @param card a card
     * @return Filter
     */
    public static Filter sameOrRelatedSite(PhysicalCard card) {
        return Filters.and(Filters.site, Filters.or(Filters.sameSite(card), Filters.relatedSite(card)));
    }

    /**
     * Filter that accepts cards that are either:  The sites themselves and their related sites for cards accepted by the
     * specified filter that are sites, otherwise the sites and their related sites that cards accepted by the specified
     * filter that are not sites are "at".
     *
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter sameOrRelatedSiteAs(PhysicalCard source, final Filter filters) {
        return sameOrRelatedSiteAs(source, null, filters);
    }

    /**
     * Filter that accepts cards that are either:  The sites themselves and their related sites for cards accepted by the
     * specified filter that are sites, otherwise the sites and their related sites that cards accepted by the specified
     * filter that are not sites are "at".
     *
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @param filters the filters
     * @return Filter
     */
    public static Filter sameOrRelatedSiteAs(PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final Filter filters) {
        return Filters.and(Filters.site, Filters.or(Filters.sameSiteAs(source, spotOverrides, filters), Filters.relatedSiteTo(source, spotOverrides, filters)));
    }

    /**
     * Filter that accepts cards that are either:  "At" the site itself and "at" related sites to the specified site if
     * the specified card is a site, otherwise "at" the site the specified card is "at" and "at" related sites
     * to the location the specified card is "at".
     *
     * @param card a card
     * @return Filter
     */
    public static Filter atSameOrRelatedSite(PhysicalCard card) {
        return Filters.at(Filters.sameOrRelatedSite(card));
    }

    /**
     * Filter that accepts cards that are either:  The specified card itself and related locations to the specified card if
     * the specified card is a location, otherwise the location the specified card is "at" and related locations to the location
     * the specified card is "at".
     *
     * @param card a card
     * @return Filter
     */
    public static Filter sameOrRelatedLocation(PhysicalCard card) {
        return Filters.and(Filters.location, Filters.or(Filters.sameLocation(card), Filters.relatedLocation(card)));
    }

    /**
     * Filter that accepts cards that are either:  The locations themselves and their related locations for cards accepted by the
     * specified filter that are locations, otherwise the locations and their related locations that cards accepted by the specified
     * filter that are not locations are "at".
     *
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter sameOrRelatedLocationAs(PhysicalCard source, final Filter filters) {
        return sameOrRelatedLocationAs(source, null, filters);
    }

    /**
     * Filter that accepts cards that are either:  The locations themselves and their related locations for cards accepted by the
     * specified filter that are locations, otherwise the locations and their related locations that cards accepted by the specified
     * filter that are not locations are "at".
     *
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @param filters the filters
     * @return Filter
     */
    public static Filter sameOrRelatedLocationAs(PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final Filter filters) {
        return Filters.and(Filters.location, Filters.or(Filters.sameLocationAs(source, spotOverrides, filters), Filters.relatedLocationTo(source, spotOverrides, filters)));
    }

    /**
     * Filter that accepts cards that are either:  "At" the card itself and "at" related locations to the specified card if
     * the specified card is a location, otherwise "at" the location the specified card is "at" and "at" related locations
     * to the location the specified card is "at".
     *
     * @param card a card
     * @return Filter
     */
    public static Filter atSameOrRelatedLocation(PhysicalCard card) {
        return Filters.at(Filters.sameOrRelatedLocation(card));
    }

    /**
     * Filter that accepts cards that are either:  "At" the system itself and "at" related system to the specified card if
     * the specified card is a location, otherwise "at" the system the specified card is "at" and "at" related system
     * to the location the specified card is "at".
     *
     * @param card a card
     * @return Filter
     */
    public static Filter atSameOrRelatedSystem(PhysicalCard card) {
        return Filters.at(Filters.and(Filters.system, Filters.sameOrRelatedLocation(card)));
    }


    //
    //
    //
    // Filters for "present"
    //
    //
    //

    /**
     * Filter that accepts cards that are "present" at the location if the specified card is a location,
     * or "present" at the location the specified card is "at".
     *
     * @param card a card
     * @return Filter
     */
    public static Filter present(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                PhysicalCard location = modifiersQuerying.getLocationHere(gameState, card);

                if (location == null)
                    return false;

                PhysicalCard location2 = modifiersQuerying.getLocationThatCardIsPresentAt(gameState, physicalCard);

                return location2 != null
                        && Filters.sameCardId(location).accepts(gameState, modifiersQuerying, location2);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return builtInCardBlueprint.isWeapon()
                        && present(card).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    /**
     * Filter that accepts cards that are "present" at locations accepted by the specified filter.
     *
     * @param filters the filters
     * @return Filter
     */
    public static Filter presentAt(final Filter filters) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard location = modifiersQuerying.getLocationThatCardIsPresentAt(gameState, physicalCard);
                return location != null
                        && filters.accepts(gameState, modifiersQuerying, location);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                return builtInCardBlueprint.isWeapon()
                        && presentAt(filters).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    /**
     * Filter that accepts cards that are locations that the specified card is "present" at.
     *
     * @param card a card
     * @return Filter
     */
    public static Filter wherePresent(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION)
                    return false;

                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                PhysicalCard location = modifiersQuerying.getLocationThatCardIsPresentAt(gameState, card);

                return location != null
                        && Filters.sameCardId(location).accepts(gameState, modifiersQuerying, physicalCard);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return builtInCardBlueprint.isWeapon()
                        && wherePresent(card).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    /**
     * Filter that accepts cards that are locations that have a card accepted by the specified filter "present".
     *
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter wherePresent(PhysicalCard source, final Filter filters) {
        return wherePresent(source, (Map<InactiveReason, Boolean>) null, filters);
    }

    /**
     * Filter that accepts cards that are locations that have a card accepted by the specified filter "present".
     *
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @param filters the filters
     * @return Filter
     */
    public static Filter wherePresent(PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final Filter filters) {
        final Integer permSourceCardId = source != null ? source.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION)
                    return false;

                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);
                Filter filterToUse = Filters.or(filters, Filters.hasPermanentWeapon(filters));
                Collection<PhysicalCard> locations = modifiersQuerying.getLocationsThatCardsArePresentAt(gameState, Filters.filterActive(gameState.getGame(), source, spotOverrides, filterToUse));

                if (locations.contains(physicalCard))
                    return true;

                return false;
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);
                return builtInCardBlueprint.isWeapon()
                        && wherePresent(source, spotOverrides, filters).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    /**
     * Filter that accepts cards that are locations that have a card accepted by the specified filter "present".
     *
     * @param source the card that is performing this query
     * @param targetingReason the reason the source card is targeting
     * @param filters the filters
     * @return Filter
     */
    public static Filter wherePresent(PhysicalCard source, final TargetingReason targetingReason, final Filter filters) {
        final Integer permSourceCardId = source != null ? source.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION)
                    return false;

                Filter filterToUse = Filters.or(filters, Filters.hasPermanentWeapon(filters));
                Collection<PhysicalCard> locations = modifiersQuerying.getLocationsThatCardsArePresentAt(gameState, Filters.filterActive(gameState.getGame(), source, null, targetingReason, filterToUse));

                if (locations.contains(physicalCard))
                    return true;

                return false;
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);
                return builtInCardBlueprint.isWeapon()
                        && wherePresent(source, targetingReason, filters).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    //
    //
    //
    // Filters for "present with"
    //
    //
    //

    /**
     * Filter that accepts cards that are "present with" the specified card.
     *
     * @param card a card
     * @return Filter
     */
    public static Filter presentWith(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return modifiersQuerying.isPresentWith(gameState, physicalCard, card);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return builtInCardBlueprint.isWeapon()
                        && presentWith(card).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    /**
     * Filter that accepts cards that are "present with" a card accepted by the specified filter.
     *
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter presentWith(PhysicalCard source, final Filter filters) {
        return presentWith(source, 1, filters);
    }

    /**
     * Filter that accepts cards that are "present with" at least a specified number of cards accepted by the specified filter.
     *
     * @param source the card that is performing this query
     * @param count the number of cards
     * @param filters the filters
     * @return Filter
     */
    public static Filter presentWith(PhysicalCard source, final int count, final Filter filters) {
        return presentWith(source, null, count, filters);
    }

    /**
     * Filter that accepts cards that are "present with" a card accepted by the specified filter.
     *
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @param filters the filters
     * @return Filter
     */
    public static Filter presentWith(PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final Filter filters) {
        return presentWith(source, spotOverrides, 1, filters);
    }

    /**
     * Filter that accepts cards that are "present with" at least a specified number of cards accepted by the specified filter.
     *
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @param count the number of cards
     * @param filters the filters
     * @return Filter
     */
    public static Filter presentWith(PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final int count, final Filter filters) {
        final Integer permSourceCardId = source != null ? source.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                // Check if card is present at the same card that as permanents aboard that match the filter
                PhysicalCard presentAt = modifiersQuerying.getCardIsPresentAt(gameState, physicalCard);
                if (presentAt == null)
                    return false;

                if (Filters.hasPermanentAboard(filters).accepts(gameState, modifiersQuerying, presentAt))
                    return true;

                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);
                Filter filterToUse = Filters.or(filters, Filters.hasPermanentWeapon(filters));
                Collection<PhysicalCard> cards = Filters.filterActive(gameState.getGame(), source, spotOverrides, filterToUse);

                int numPresentWith = 0;
                for (PhysicalCard card : cards) {
                    if (modifiersQuerying.isPresentWith(gameState, card, physicalCard)) {
                        numPresentWith++;
                        if (numPresentWith >= count) {
                            return true;
                        }
                    }
                }

                return false;
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);
                return builtInCardBlueprint.isWeapon()
                        && presentWith(source, spotOverrides, count, filters).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    //
    //
    //
    // Filters for "here"
    //
    //
    //

    /**
     * Filter that, if the specified card is a location, accepts cards that are the location itself and cards "at" at the location,
     * otherwise, if the specified card is not a location, accepts cards that are the location the specified card is "at" and cards "at"
     * that location (including the specified card itself).
     *
     * @param card a card
     * @return Filter
     */
    public static Filter here(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                PhysicalCard location = modifiersQuerying.getLocationHere(gameState, card);

                if (location == null)
                    return false;

                PhysicalCard location2 = modifiersQuerying.getLocationHere(gameState, physicalCard);

                return location2 != null
                        && Filters.sameCardId(location).accepts(gameState, modifiersQuerying, location2);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return here(card).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    //
    //
    //
    // Filters for "with"
    //
    //
    //

    /**
     * Filter that accepts cards that are "with" the specified card.
     *
     * @param card a card
     * @return Filter
     */
    public static Filter with(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return modifiersQuerying.isWith(gameState, physicalCard, card);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return with(card).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    /**
     * Filter that accepts cards that are "with" a card accepted by the specified filter.
     *
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter with(PhysicalCard source, final Filter filters) {
        return with(source, null, filters);
    }

    /**
     * Filter that accepts cards that are "with" a card accepted by the specified filter.
     *
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @param filters the filters
     * @return Filter
     */
    public static Filter with(PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final Filter filters) {
        final Integer permSourceCardId = source != null ? source.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);
                Filter filterToUse = Filters.or(filters, Filters.hasPermanentAboard(filters), Filters.hasPermanentWeapon(filters));
                Collection<PhysicalCard> cards = Filters.filterActive(gameState.getGame(), source, spotOverrides, filterToUse);

                for (PhysicalCard card : cards) {
                    if (modifiersQuerying.isWith(gameState, card, physicalCard))
                        return true;
                }

                return false;
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);
                return with(source, spotOverrides, filters).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    //
    //
    //
    // Filters for "at"
    //
    //
    //

    /**
     * Filter that accepts cards that are "at" at the specified location.
     *
     * @param card a location
     * @return Filter
     */
    public static Filter at(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                if (card.getBlueprint().getCardCategory() != CardCategory.LOCATION)
                    return false;

                PhysicalCard location = modifiersQuerying.getLocationThatCardIsAt(gameState, physicalCard);

                return location != null
                        && Filters.sameCardId(location).accepts(gameState, modifiersQuerying, card);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return at(card).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    /**
     * Filter that accepts cards that are "at" locations accepted by the specified filter.
     *
     * @param filter the filter
     * @return Filter
     */
    public static Filter at(final Filter filter) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard location = modifiersQuerying.getLocationThatCardIsAt(gameState, physicalCard);
                return location != null
                        && filter.accepts(gameState, modifiersQuerying, location);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                return at(filter).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    /**
     * Filter that accepts cards that are "at" a planet of the specified name.
     *
     * @param name the planet name
     * @return Filter
     */
    public static Filter at(final String name) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                String planet = modifiersQuerying.getSystemThatCardIsAt(gameState, physicalCard);
                return planet != null
                        && planet.equals(name);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                return at(name).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    //
    //
    //
    // Filters for "on"
    //
    //
    //

    /**
     * Filter that accepts cards that are "on" a planet (or system) of the specified name.
     *
     * @param name the system name
     * @return Filter
     */
    public static Filter on(final String name) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                String system = modifiersQuerying.getSystemThatCardIsOn(gameState, physicalCard);
                return system != null
                        && system.equals(name);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                return on(name).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    //
    //
    //
    // Filters for "at same planet" or "on same planet"
    //
    //
    //

    /**
     * Filter that accepts cards that are "at" the same planet as the specified card.
     *
     * @param card a card
     * @return Filter
     */
    public static Filter atSamePlanet(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                if (Filters.sameCardId(card).accepts(gameState, modifiersQuerying, physicalCard))
                    return false;

                String planet = modifiersQuerying.getSystemThatCardIsAt(gameState, card);

                if (planet == null)
                    return false;

                String planet2 = modifiersQuerying.getSystemThatCardIsAt(gameState, physicalCard);

                return planet2 != null
                        && planet.equals(planet2);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return atSamePlanet(card).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    /**
     * Filter that accepts cards that are "on" the same planet as the specified card.
     *
     * @param card a card
     * @return Filter
     */
    public static Filter onSamePlanet(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                String planet = modifiersQuerying.getSystemThatCardIsOn(gameState, card);

                if (planet == null)
                    return false;

                String planet2 = modifiersQuerying.getSystemThatCardIsOn(gameState, physicalCard);

                return planet2 != null
                        && planet.equals(planet2);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return onSamePlanet(card).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    /**
     * Filter that accepts cards that are "on" the same planet as a card accepted by the specified filter.
     *
     * @param source the card that is performing this query
     * @param filter the filter
     * @return Filter
     */
    public static Filter onSamePlanetAs(PhysicalCard source, final Filter filter) {
        return onSamePlanetAs(source, null, filter);
    }

    /**
     * Filter that accepts cards that are "on" the same planet as a card accepted by the specified filter.
     *
     * @param filter the filter
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @return Filter
     */
    public static Filter onSamePlanetAs(PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final Filter filter) {
        final Integer permSourceCardId = source != null ? source.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);
                return Filters.canSpot(gameState.getGame(), source, spotOverrides, Filters.and(filter, Filters.onSamePlanet(physicalCard)));
            }
        };
    }

    //
    //
    //
    // Filters for "attached"
    //
    //
    //

    /**
     * Filter that accepts cards that are "attached to" the specified card.
     *
     * @param card a card
     * @return Filter
     */
    public static Filter attachedTo(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                PhysicalCard attachedTo = physicalCard.getAttachedTo();
                return attachedTo != null
                        && Filters.sameCardId(attachedTo).accepts(gameState, modifiersQuerying, card);
            }
        };
    }

    /**
     * Filter that accepts cards that are "attached to" a card accepted by the specified filter.
     *
     * @param filter the filter
     * @return Filter
     */
    public static Filter attachedTo(final Filter filter) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard attachedTo = physicalCard.getAttachedTo();
                return attachedTo != null
                        && filter.accepts(gameState, modifiersQuerying, attachedTo);
            }
        };
    }

    /**
     * Filter that accepts cards that are "attached to" a card (recursive checking).
     *
     * @param card a card
     * @return Filter
     */
    public static Filter attachedToWithRecursiveChecking(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard attachedTo = physicalCard.getAttachedTo();
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                while (attachedTo != null) {
                    if (Filters.sameCardId(card).accepts(gameState, modifiersQuerying, attachedTo))
                        return true;

                    attachedTo = attachedTo.getAttachedTo();
                }
                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that are "attached to" a card accepted by the specified filter (recursive checking).
     *
     * @param filter the filter
     * @return Filter
     */
    public static Filter attachedToWithRecursiveChecking(final Filter filter) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard attachedTo = physicalCard.getAttachedTo();
                while (attachedTo != null) {
                    if (filter.accepts(gameState, modifiersQuerying, attachedTo))
                        return true;

                    attachedTo = attachedTo.getAttachedTo();
                }
                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that the specified card is "attached to".
     *
     * @param card a card
     * @return Filter
     */
    public static Filter hasAttached(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                PhysicalCard attachedTo = card.getAttachedTo();
                return attachedTo != null
                        && Filters.sameCardId(attachedTo).accepts(gameState, modifiersQuerying, physicalCard);
            }
        };
    }

    /**
     * Filter that accepts cards that the specified card is "attached to" (recursive checking).
     *
     * @param card a card
     * @return Filter
     */
    public static Filter hasAttachedWithRecursiveChecking(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                PhysicalCard attachedTo = card.getAttachedTo();
                while (attachedTo != null) {
                    if (Filters.sameCardId(attachedTo).accepts(gameState, modifiersQuerying, physicalCard))
                        return true;

                    attachedTo = attachedTo.getAttachedTo();
                }
                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that cards accepted by the specified filter are "attached to".
     *
     * @param filter the filter
     * @return Filter
     */
    public static Filter hasAttached(final Filter filter) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return Filters.canSpot(gameState.getGame(), null, Filters.and(filter, Filters.attachedTo(physicalCard)));
            }
        };
    }

    /**
     * Filter that accepts cards that cards accepted by the specified filter are "attached to" (recursive checking).
     *
     * @param filter the filter
     * @return Filter
     */
    public static Filter hasAttachedWithRecursiveChecking(final Filter filter) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return !Filters.filterCount(gameState.getAllAttachedRecursively(physicalCard), gameState.getGame(), 1, filter).isEmpty();
            }
        };
    }

    /**
     * Filter that accepts cards that are "trained by" a card accepted by the specified filter.
     *
     * @param filter the filter
     * @return Filter
     */
    public static Filter trainedBy(final Filter filter) {
        return Filters.hasAttached(filter);
    }

    /**
     * Filter that accepts cards that are "armed with" the specified card.
     *
     * @param card the card
     * @return Filter
     */
    public static Filter armedWith(final PhysicalCard card) {
        return armedWith(Filters.sameCardId(card));
    }

    /**
     * Filter that accepts cards that are "armed with" weapons (including permanent weapons) accepted by the specified filter.
     *
     * @param filter the filter
     * @return Filter
     */
    public static Filter armedWith(final Filter filter) {
        return armedWith(filter, 1);
    }

    /**
     * Filter that accepts cards that are "armed with" at least a specified number of weapons (including permanent weapons)
     * accepted by the specified filter.
     *
     * @param filter the filter
     * @param numWeapons the number of weapons
     * @return Filter
     */
    public static Filter armedWith(final Filter filter, final int numWeapons) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                int foundSoFar = 0;
                SwccgBuiltInCardBlueprint permWeapon = modifiersQuerying.getPermanentWeapon(gameState, physicalCard);
                if (permWeapon != null) {
                    if (Filters.and(filter).accepts(gameState, modifiersQuerying, permWeapon)) {
                        foundSoFar++;
                        if (foundSoFar >= numWeapons) {
                            return true;
                        }
                    }
                }

                Collection<PhysicalCard> attachedWeapons = Filters.filter(gameState.getAttachedCards(physicalCard), gameState.getGame(), Filters.and(Filters.weapon, filter));
                for (PhysicalCard attachedWeapon : attachedWeapons) {
                    // Verify weapon card is active (stolen weapon that cannot be used does not count)
                    if (gameState.isCardInPlayActive(attachedWeapon, true, true, true, false, false, true, true, false)) {
                        foundSoFar++;
                        if (foundSoFar >= numWeapons) {
                            return true;
                        }
                    }
                }

                return false;
            }
        };
    }


    //
    //
    //
    // Filters for "aboard"
    //
    //
    //

    /**
     * Filter that accepts cards that are "aboard" the specified starship or vehicle, including any related
     * starship or vehicle sites.
     *
     * @param card a starship or vehicle
     * @return Filter
     */
    public static Filter aboard(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return modifiersQuerying.isAboard(gameState, physicalCard, card, false, true);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                if ((builtInCardBlueprint.isPilot() || builtInCardBlueprint.isAstromech())
                        && Filters.sameCardId(builtInCardBlueprint.getPhysicalCard(gameState.getGame())).accepts(gameState, modifiersQuerying, card)) {
                    return true;
                }
                return builtInCardBlueprint.isWeapon()
                        && aboard(card).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    /**
     * Filter that accepts cards that are "aboard" starships or vehicles accepted by the specified filter, including any related
     * starship or vehicle sites.
     *
     * @param filters the filters
     * @return Filter
     */
    public static Filter aboard(final Filter filters) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                Collection<PhysicalCard> starshipsAndVehicles = Filters.filterAllOnTable(gameState.getGame(), filters);

                for (PhysicalCard starshipOrVehicle : starshipsAndVehicles) {
                    if (Filters.aboard(starshipOrVehicle).accepts(gameState, modifiersQuerying, physicalCard))
                        return true;
                }

                return false;
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                if ((builtInCardBlueprint.isPilot() || builtInCardBlueprint.isAstromech())
                        && Filters.and(filters).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()))) {
                    return true;
                }
                return builtInCardBlueprint.isWeapon()
                        && aboard(filters).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    /**
     * Filter that accepts cards that are "aboard" starships or vehicles accepted by the specified filter, including aboard its cargo, but
     * excluding any related starship or vehicle sites.
     *
     * @param filters the filters
     * @return Filter
     */
    public static Filter aboardOrAboardCargoOf(final Filter filters) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                Collection<PhysicalCard> starshipsAndVehicles = Filters.filterAllOnTable(gameState.getGame(), filters);

                for (PhysicalCard starshipOrVehicle : starshipsAndVehicles) {
                    if (Filters.aboardOrAboardCargoOf(starshipOrVehicle).accepts(gameState, modifiersQuerying, physicalCard))
                        return true;
                }

                return false;
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                if ((builtInCardBlueprint.isPilot() || builtInCardBlueprint.isAstromech())
                        && Filters.and(filters).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()))) {
                    return true;
                }
                return aboardOrAboardCargoOf(filters).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    /**
     * Filter that accepts cards that are "aboard" the specified starship or vehicle, excluding any related
     * starship or vehicle sites.
     *
     * @param card a starship or vehicle
     * @return Filter
     */
    public static Filter aboardExceptRelatedSites(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return modifiersQuerying.isAboard(gameState, physicalCard, card, false, false);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                if ((builtInCardBlueprint.isPilot() || builtInCardBlueprint.isAstromech())
                        && Filters.sameCardId(builtInCardBlueprint.getPhysicalCard(gameState.getGame())).accepts(gameState, modifiersQuerying, card)) {
                    return true;
                }
                return builtInCardBlueprint.isWeapon()
                        && aboardExceptRelatedSites(card).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    /**
     * Filter that accepts cards that are "aboard" the specified starship or vehicle, including aboard its cargo, but
     * excluding any related starship or vehicle sites.
     *
     * @param card a starship or vehicle
     * @return Filter
     */
    public static Filter aboardOrAboardCargoOf(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return modifiersQuerying.isAboard(gameState, physicalCard, card, true, false);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                if ((builtInCardBlueprint.isPilot() || builtInCardBlueprint.isAstromech())
                        && Filters.sameCardId(builtInCardBlueprint.getPhysicalCard(gameState.getGame())).accepts(gameState, modifiersQuerying, card)) {
                    return true;
                }
                return aboardOrAboardCargoOf(card).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    /**
     * Filter that accepts cards that are "aboard" the starship or vehicle of the specified persona, including any
     * related starship or vehicle sites.
     *
     * @param persona a starship or vehicle persona
     * @return Filter
     */
    public static Filter aboardStarshipOrVehicleOfPersona(final Persona persona) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (modifiersQuerying.isAtStarshipSiteOrVehicleSiteOfPersona(gameState, physicalCard, persona))
                    return true;

                // Check if card is attached to a starship or vehicle of persona
                PhysicalCard attachedTo = physicalCard.getAttachedTo();
                while (attachedTo != null) {
                    if (Filters.persona(persona).accepts(gameState, modifiersQuerying, attachedTo)) {
                        return true;
                    }
                    attachedTo = attachedTo.getAttachedTo();
                }
                return false;
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                if (builtInCardBlueprint.isWeapon()) {
                    return aboardStarshipOrVehicleOfPersona(persona).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
                }
                if (builtInCardBlueprint.isPilot() || builtInCardBlueprint.isAstromech()) {
                    return Filters.persona(persona).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
                }
                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that are "aboard" any starship.
     */
    public static final Filter aboardAnyStarship = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            if (modifiersQuerying.isAtStarshipSite(gameState, physicalCard))
                return true;

            // Check if card is attached to a starship
            PhysicalCard attachedTo = physicalCard.getAttachedTo();
            while (attachedTo != null) {
                if (attachedTo.getBlueprint().getCardCategory() == CardCategory.STARSHIP) {
                    return true;
                }
                attachedTo = attachedTo.getAttachedTo();
            }
            return false;
        }
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
            if (builtInCardBlueprint.isWeapon()) {
                return aboardAnyStarship.accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
            if (builtInCardBlueprint.isPilot() || builtInCardBlueprint.isAstromech()) {
                return Filters.starship.accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
            return false;
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter aboardAnyStarship() {
        return aboardAnyStarship;
    }

    /**
     * Filter that accepts cards that are "aboard" any vehicle.
     */
    public static final Filter aboardAnyVehicle = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            if (modifiersQuerying.isAtVehicleSite(gameState, physicalCard))
                return true;

            // Check if card is attached to a vehicle
            PhysicalCard attachedTo = physicalCard.getAttachedTo();
            while (attachedTo != null) {
                if (attachedTo.getBlueprint().getCardCategory() == CardCategory.VEHICLE) {
                    return true;
                }
                attachedTo = attachedTo.getAttachedTo();
            }
            return false;
        }
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
            if (builtInCardBlueprint.isWeapon()) {
                return aboardAnyVehicle.accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
            if (builtInCardBlueprint.isPilot() || builtInCardBlueprint.isAstromech()) {
                return Filters.vehicle.accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
            return false;
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter aboardAnyVehicle() {
        return aboardAnyVehicle;
    }

    /**
     * Filter that accepts cards that have the specified card "aboard", including at any related
     * starship or vehicle sites.
     *
     * @param card a card
     * @return Filter
     */
    public static Filter hasAboard(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return modifiersQuerying.isAboard(gameState, card, physicalCard, false, true);
            }
        };
    }

    /**
     * Filter that accepts cards that have a card accepted by the specified filter "aboard", including at any related
     * starship or vehicle sites.
     *
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter hasAboard(PhysicalCard source, final Filter filters) {
        return hasAboard(source, null, filters);
    }

    /**
     * Filter that accepts cards that have a card (or permanent built-in) accepted by the specified filter "aboard", including at any related
     * starship or vehicle sites.
     *
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @param filters the filters
     * @return Filter
     */
    public static Filter hasAboard(PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final Filter filters) {
        final Integer permSourceCardId = source != null ? source.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (Filters.hasPermanentAboard(filters).accepts(gameState, modifiersQuerying, physicalCard))
                    return true;

                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);
                Collection<PhysicalCard> cards = Filters.filterActive(gameState.getGame(), source, spotOverrides, filters);

                for (PhysicalCard card : cards) {
                    if (Filters.hasAboard(card).accepts(gameState, modifiersQuerying, physicalCard))
                        return true;
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that have the specified card "aboard", excluding at any related
     * starship or vehicle sites.
     *
     * @param card a card
     * @return Filter
     */
    public static Filter hasAboardExceptRelatedSites(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return modifiersQuerying.isAboard(gameState, card, physicalCard, false, false);
            }
        };
    }

    /**
     * Filter that accepts cards that have a card accepted by the specified filter "aboard", excluding at any related
     * starship or vehicle sites.
     *
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter hasAboardExceptRelatedSites(PhysicalCard source, final Filter filters) {
        return hasAboardExceptRelatedSites(source, null, filters);
    }

    /**
     * Filter that accepts cards that have a card (or permanent built-in) accepted by the specified filter "aboard", excluding at any related
     * starship or vehicle sites.
     *
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @param filters the filters
     * @return Filter
     */
    public static Filter hasAboardExceptRelatedSites(PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final Filter filters) {
        final Integer permSourceCardId = source != null ? source.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (Filters.hasPermanentAboard(filters).accepts(gameState, modifiersQuerying, physicalCard))
                    return true;

                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);
                Collection<PhysicalCard> cards = Filters.filterActive(gameState.getGame(), source, spotOverrides, filters);

                for (PhysicalCard card : cards) {
                    if (Filters.hasAboardExceptRelatedSites(card).accepts(gameState, modifiersQuerying, physicalCard))
                        return true;
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that have the specified card "aboard", include aboard cargo, but excluding at any related
     * starship or vehicle sites.
     *
     * @param card a card
     * @return Filter
     */
    public static Filter hasAboardIncludingAboardCargoExceptRelatedSites(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return modifiersQuerying.isAboard(gameState, card, physicalCard, true, false);
            }
        };
    }

    /**
     * Filter that accepts cards that has built-in permanent pilots or passengers accepted by the specified filter.
     *
     * @param filters the filters
     * @return Filter
     */
    public static Filter hasPermanentAboard(final Filter filters) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                List<SwccgBuiltInCardBlueprint> permanentsAboard = modifiersQuerying.getPermanentsAboard(gameState, physicalCard);
                for (SwccgBuiltInCardBlueprint permanentAboard : permanentsAboard) {
                    if (Filters.and(filters).accepts(gameState, modifiersQuerying, permanentAboard)) {
                        return true;
                    }
                }

                return false;
            }
        };
    }


    //
    //
    //
    // Filters for pilots, drivers, and passengers
    //
    //
    //

    /**
     * Filter that accepts cards that are starships and vehicles (except transport vehicles, which are driven),
     * that are piloted.
     */
    public static final Filter piloted = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return !Filters.transport_vehicle.accepts(gameState, modifiersQuerying, physicalCard) && modifiersQuerying.isPiloted(gameState, physicalCard, false);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter piloted() {
        return piloted;
    }

    /**
     * Filter that accepts cards that are starships that are piloted for the purpose of taking off.
     */
    public static final Filter pilotedForTakeOff = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.isPiloted(gameState, physicalCard, true);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter pilotedForTakeOff() {
        return pilotedForTakeOff;
    }

    /**
     * Filter that accepts cards that are starships and vehicles (except transport vehicles, which are driven),
     * that are unpiloted.
     */
    public static final Filter unpiloted = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return !modifiersQuerying.isPiloted(gameState, physicalCard, false);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter unpiloted() {
        return unpiloted;
    }

    /**
     * Filter that accepts cards transport vehicles that are driven.
     */
    public static final Filter driven = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return Filters.transport_vehicle.accepts(gameState, modifiersQuerying, physicalCard) && modifiersQuerying.isPiloted(gameState, physicalCard, false);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter driven() {
        return driven;
    }

    /**
     * Filter that accepts cards that are starships and vehicles that are landed.
     */
    public static final Filter landed = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.isLanded(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter landed() {
        return landed;
    }

    /**
     * Filter that accepts cards that are piloting the specified card (except transport vehicles, which are driven).
     *
     * @param card a starship or vehicle
     * @return Filter
     */
    public static Filter piloting(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                boolean isPiloted = modifiersQuerying.isPiloted(gameState, card, false);

                if (!isPiloted)
                    return false;

                PhysicalCard pilotOf = modifiersQuerying.getIsPilotOf(gameState, physicalCard);
                return pilotOf != null
                        && Filters.sameCardId(pilotOf).accepts(gameState, modifiersQuerying, card);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return builtInCardBlueprint.isPilot()
                        && Filters.sameCardId(card).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    /**
     * Filter that accepts cards that are piloting cards (except transport vehicles, which are driven) accepted by the
     * specified filter.
     *
     * @param filters the filters
     * @return Filter
     */
    public static Filter piloting(final Filter filters) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard pilotOf = modifiersQuerying.getIsPilotOf(gameState, physicalCard);
                if (pilotOf == null)
                    return false;

                if (!Filters.and(filters).accepts(gameState, modifiersQuerying, pilotOf))
                    return false;

                return Filters.piloted.accepts(gameState, modifiersQuerying, pilotOf);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                return builtInCardBlueprint.isPilot()
                        && Filters.and(filters).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    /**
     * Filter that accepts cards (except transport vehicles, which are driven) piloted by the specified card.
     *
     * @param card a card
     * @return Filter
     */
    public static Filter hasPiloting(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return Filters.piloting(physicalCard).accepts(gameState, modifiersQuerying, card);
            }
        };
    }

    /**
     * Filter that accepts cards (except transport vehicles, which are driven) piloted by a card (or permanent built-in) accepted by the specified filter.
     *
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter hasPiloting(PhysicalCard source, final Filter filters) {
        return hasPiloting(source, null, filters);
    }

    /**
     * Filter that accepts cards (except transport vehicles, which are driven) piloted by a card (or permanent built-in) accepted by the specified filter.
     *
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @param filters the filters
     * @return Filter
     */
    public static Filter hasPiloting(PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final Filter filters) {
        final Integer permSourceCardId = source != null ? source.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (!modifiersQuerying.isPiloted(gameState, physicalCard, false))
                    return false;

                if (Filters.hasPermanentPilot(filters).accepts(gameState, modifiersQuerying, physicalCard))
                    return true;

                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);
                Collection<PhysicalCard> cards = Filters.filterActive(gameState.getGame(), source, spotOverrides, filters);

                for (PhysicalCard card : cards) {
                    PhysicalCard pilotOf = modifiersQuerying.getIsPilotOf(gameState, card);
                    if (pilotOf != null
                            && Filters.sameCardId(pilotOf).accepts(gameState, modifiersQuerying, physicalCard)) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that have a permanent pilot.
     */
    public static final Filter hasPermanentPilot = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.hasPermanentPilot(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter hasPermanentPilot() {
        return hasPermanentPilot;
    }

    /**
     * Filter that accepts cards that has a built-in permanent pilot accepted by the specified filter.
     *
     * @param filters the filters
     * @return Filter
     */
    public static Filter hasPermanentPilot(final Filter filters) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                List<SwccgBuiltInCardBlueprint> permanentPilots = modifiersQuerying.getPermanentPilotsAboard(gameState, physicalCard);
                for (SwccgBuiltInCardBlueprint permanentPilot : permanentPilots) {
                    if (Filters.and(filters).accepts(gameState, modifiersQuerying, permanentPilot)) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that are driving the specified card.
     *
     * @param card a transport vehicle
     * @return Filter
     */
    public static Filter driving(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                boolean isPiloted = modifiersQuerying.isPiloted(gameState, card, false);

                if (!isPiloted)
                    return false;

                PhysicalCard driverOf = modifiersQuerying.getIsDriverOf(gameState, physicalCard);
                return driverOf != null
                        && Filters.sameCardId(driverOf).accepts(gameState, modifiersQuerying, card);
            }
        };
    }

    /**
     * Filter that accepts cards that are driving cards (transport vehicles) accepted by the specified filter.
     *
     * @param filters the filters
     * @return Filter
     */
    public static Filter driving(final Filter filters) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard driverOf = modifiersQuerying.getIsDriverOf(gameState, physicalCard);
                if (driverOf == null)
                    return false;

                if (!Filters.and(filters).accepts(gameState, modifiersQuerying, driverOf))
                    return false;

                return Filters.driven.accepts(gameState, modifiersQuerying, driverOf);
            }
        };
    }

    /**
     * Filter that accepts cards (transport vehicles) driven by the specified card.
     *
     * @param card a card
     * @return Filter
     */
    public static Filter hasDriving(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return Filters.driving(physicalCard).accepts(gameState, modifiersQuerying, card);
            }
        };
    }

    /**
     * Filter that accepts cards (transport vehicles) driven by a card accepted by the specified filter.
     *
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter hasDriving(PhysicalCard source, final Filter filters) {
        return hasDriving(source, null, filters);
    }

    /**
     * Filter that accepts cards (transport vehicles) driven by a card accepted by the specified filter.
     *
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @param filters the filters
     * @return Filter
     */
    public static Filter hasDriving(PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final Filter filters) {
        final Integer permSourceCardId = source != null ? source.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (!modifiersQuerying.isPiloted(gameState, physicalCard, false))
                    return false;

                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);
                Collection<PhysicalCard> cards = Filters.filterActive(gameState.getGame(), source, spotOverrides, filters);

                for (PhysicalCard card : cards) {
                    if (Filters.hasDriving(card).accepts(gameState, modifiersQuerying, physicalCard))
                        return true;
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that are aboard the specified card as a passenger.
     *
     * @param card a starship or vehicle
     * @return Filter
     */
    public static Filter aboardAsPassenger(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                Collection<PhysicalCard> passengers = gameState.getPassengerCardsAboard(card);

                if (passengers.contains(physicalCard))
                    return true;

                return false;
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return builtInCardBlueprint.isAstromech()
                        && Filters.sameCardId(card).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    /**
     * Filter that accepts cards that are aboard a card accepted by the specified filter as a passenger.
     *
     * @param filters the filters
     * @return Filter
     */
    public static Filter aboardAsPassenger(final Filter filters) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                Collection<PhysicalCard> cards = Filters.filterAllOnTable(gameState.getGame(), filters);

                for (PhysicalCard card : cards) {
                    if (Filters.aboardAsPassenger(card).accepts(gameState, modifiersQuerying, physicalCard))
                        return true;
                }

                return false;
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                return builtInCardBlueprint.isAstromech()
                        && Filters.and(filters).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    /**
     * Filter that accepts cards that have the specified card aboard as a passenger.
     *
     * @param card a card
     * @return Filter
     */
    public static Filter hasPassenger(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return Filters.aboardAsPassenger(physicalCard).accepts(gameState, modifiersQuerying, card);
            }
        };
    }

    /**
     * Filter that accepts cards that have a card (or permanent built-in) accepted by the specified filter aboard as a passenger.
     *
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter hasPassenger(PhysicalCard source, final Filter filters) {
        return hasPassenger(source, null, filters);
    }

    /**
     * Filter that accepts cards that have a card (or permanent built-in) accepted by the specified filter aboard as a passenger.
     *
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @param filters the filters
     * @return Filter
     */
    public static Filter hasPassenger(PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final Filter filters) {
        final Integer permSourceCardId = source != null ? source.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (Filters.hasPermanentAstromech(filters).accepts(gameState, modifiersQuerying, physicalCard))
                    return true;

                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);
                Collection<PhysicalCard> cards = Filters.filterActive(gameState.getGame(), source, spotOverrides, filters);

                for (PhysicalCard card : cards) {
                    if (Filters.hasPassenger(card).accepts(gameState, modifiersQuerying, physicalCard))
                        return true;
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that have a permanent astromech.
     */
    public static final Filter hasPermanentAstromech = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.hasPermanentAstromech(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter hasPermanentAstromech() {
        return hasPermanentAstromech;
    }

    /**
     * Filter that accepts cards that has a built-in permanent astromech accepted by the specified filter.
     *
     * @param filters the filters
     * @return Filter
     */
    public static Filter hasPermanentAstromech(final Filter filters) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                List<SwccgBuiltInCardBlueprint> permanentAstromechs = modifiersQuerying.getPermanentAstromechsAboard(gameState, physicalCard);
                for (SwccgBuiltInCardBlueprint permanentAstromech : permanentAstromechs) {
                    if (Filters.and(filters).accepts(gameState, modifiersQuerying, permanentAstromech)) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    //
    //
    //
    // Filters for toward
    //
    //
    //

    /**
     * Filter that accepts locations that are toward the destination location (or card "at" the destination location)
     * from the starting point location (or card "at" the starting point location).
     *
     * @param startingPoint the starting point location (or card "at" the starting point location)
     * @param destination the destination location (or card "at" the destination location)
     * @return Filter
     */
    public static Filter toward(PhysicalCard startingPoint, PhysicalCard destination) {
        final Integer permStartingPointCardId = startingPoint.getPermanentCardId();
        final Integer permDestinationCardId = destination.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION) {
                    return false;
                }

                PhysicalCard startingPoint = gameState.findCardByPermanentId(permStartingPointCardId);
                PhysicalCard destination = gameState.findCardByPermanentId(permDestinationCardId);
                PhysicalCard startingPointLocation = modifiersQuerying.getLocationHere(gameState, startingPoint);
                PhysicalCard destinationLocation = modifiersQuerying.getLocationHere(gameState, destination);

                // Check if location is the destination location
                if (Filters.sameCardId(destinationLocation).accepts(gameState, modifiersQuerying, physicalCard)) {
                    return true;
                }

                List<PhysicalCard> sitesBetween = modifiersQuerying.getSitesBetween(gameState, startingPointLocation, destinationLocation);
                if (sitesBetween != null) {
                    // Check if location between source location and destination location
                    for (PhysicalCard siteBetween : sitesBetween) {
                        if (Filters.sameCardId(siteBetween).accepts(gameState, modifiersQuerying, physicalCard)) {
                            return true;
                        }
                    }
                }

                List<PhysicalCard> sectorsBetween = modifiersQuerying.getSectorsBetween(gameState, startingPointLocation, destinationLocation);
                if (sectorsBetween != null) {
                    // Check if location between source location and destination location
                    for (PhysicalCard sectorBetween : sectorsBetween) {
                        if (Filters.sameCardId(sectorBetween).accepts(gameState, modifiersQuerying, physicalCard)) {
                            return true;
                        }
                    }
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts locations that are toward a location (or card "at" a location) accepted by the destinationFilter
     * from the source card's location.
     *
     * @param source the card that is performing this query
     * @param destinationFilter the destination filter
     * @return Filter
     */
    public static Filter toward(PhysicalCard source, final Filter destinationFilter) {
        final Integer permSourceCardId = source != null ? source.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION) {
                    return false;
                }

                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);
                Collection<PhysicalCard> destinations = Filters.filterTopLocationsOnTable(gameState.getGame(), Filters.sameLocationAs(source, destinationFilter));
                for (PhysicalCard destination : destinations) {
                    if (Filters.toward(source, destination).accepts(gameState, modifiersQuerying, physicalCard)) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    //
    //
    //
    // Filters for movement
    //
    //
    //

    /**
     * Filter that accepts cards that can be moved by the specified Bog-wing.
     *
     * @param bogWing the Bog-wing
     * @return Filter
     */
    public static Filter characterCanBeMovedByBogWing(final PhysicalCard bogWing) {
        final Integer permBogWingCardId = bogWing != null ? bogWing.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.CHARACTER)
                    return false;

                PhysicalCard bogWing = gameState.findCardByPermanentId(permBogWingCardId);
                Filter siteFilter = Filters.and(Filters.locationCanBeRelocatedTo(bogWing, true, false, true, 0, false), Filters.siteWithinDistance(physicalCard, 2));
                return Filters.canBeRelocatedToLocation(siteFilter, true, false, true, 0, false).accepts(gameState, modifiersQuerying, physicalCard);
            }
        };
    }

    /**
     * Filter that accepts cards that can perform a "move away" action to a location accepted by the move target filter.
     *
     * @param playerId the player to move the card
     * @param forFree true if the movement is to be free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required to perform the movement
     * @param moveTargetFilter the move target filter
     * @return Filter
     */
    public static Filter movableAsMoveAway(final String playerId, final boolean forFree, final float changeInCost, final Filter moveTargetFilter) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return physicalCard.getBlueprint().getMoveAwayAction(playerId, gameState.getGame(), physicalCard, forFree, changeInCost, false, moveTargetFilter) != null;
            }
        };
    }

    /**
     * Filter that accepts cards that can perform an additional "regular move" action.
     *
     * @param playerId the player to move the card
     * @return Filter
     */
    public static Filter movableAsAdditionalMove(final String playerId) {
        return movableAsRegularMove(playerId, false, 0, true, Filters.any);
    }

    /**
     * Filter that accepts cards that can perform a "regular move" action to a location accepted by the move target filter.
     *
     * @param playerId the player to move the card
     * @param forFree true if the movement is to be free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required to perform the movement
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @param moveTargetFilter the move target filter
     * @return Filter
     */
    public static Filter movableAsRegularMove(final String playerId, final boolean forFree, final float changeInCost, final boolean asAdditionalMove, final Filter moveTargetFilter) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return physicalCard.getBlueprint().getRegularMoveAction(playerId, gameState.getGame(), physicalCard, forFree, changeInCost, true, asAdditionalMove, moveTargetFilter) != null;
            }
        };
    }

    /**
     * Filter that accepts cards that can perform a "move using landspeed" action to a location accepted by the move target filter.
     *
     * @param playerId the player to move the card
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @param asMoveAway true if moving as a move away, otherwise false
     * @param forFree true if the movement is to be free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required to perform the movement
     * @param landspeedOverride the specified landspeed to use for this movement, or null if using normal landspeed
     * @param moveTargetFilter the move target filter
     * @return Filter
     */
    public static Filter movableAsRegularMoveUsingLandspeed(final String playerId, final boolean asReact, final boolean asMoveAway, final boolean forFree, final float changeInCost, final Integer landspeedOverride, final Filter moveTargetFilter) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return physicalCard.getBlueprint().getMoveUsingLandspeedAction(playerId, gameState.getGame(), physicalCard, forFree, changeInCost, asReact, asMoveAway, true, false, landspeedOverride, moveTargetFilter) != null;
            }
        };
    }

    /**
     * Filter that accepts cards that can move to a location using landspeed.
     *
     * @param playerId the player to move the card
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @param asMoveAway true if moving as a move away, otherwise false
     * @param forFree true if the movement is to be free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required to perform the movement
     * @return Filter
     */
    public static Filter canMoveUsingLandspeed(final String playerId, final boolean asReact, final boolean asMoveAway, final boolean forFree, final float changeInCost) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return Filters.canSpotFromTopLocationsOnTable(gameState.getGame(), Filters.canMoveToUsingLandspeed(playerId, physicalCard, asReact, asMoveAway, forFree, changeInCost, null));
            }
        };
    }

    /**
     * Filter that accepts locations that the specified card can move to using landspeed.
     *
     * @param playerId the player to move the card
     * @param cardToMove the card to move
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @param asMoveAway true if moving as a move away, otherwise false
     * @param forFree true if the movement is to be free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required to perform the movement
     * @param landspeedOverride the specified landspeed to use for this movement, or null if using normal landspeed
     * @return Filter
     */
    public static Filter canMoveToUsingLandspeed(final String playerId, PhysicalCard cardToMove, final boolean asReact, final boolean asMoveAway, final boolean forFree, final float changeInCost, final Integer landspeedOverride) {
        final Integer permCardToMoveCardId = cardToMove.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard cardToMove = gameState.findCardByPermanentId(permCardToMoveCardId);

                // 1) Check that character (or card that moves like a character), creature, or vehicle to move is physically at a site
                PhysicalCard currentAtLocation = cardToMove.getAtLocation();

                // If moving away or as a react, card can disembark first
                // (unless during the move as 'react', in which case the card must disembark first before moving to another location)
                if ((asReact || asMoveAway) && cardToMove.getAttachedTo() != null && !gameState.isDuringMoveAsReact()) {
                    if (!modifiersQuerying.mayOnlyMoveUsingLandspeed(gameState, cardToMove)) {
                        currentAtLocation = cardToMove.getAttachedTo().getAtLocation();
                    }
                }

                if (currentAtLocation == null
                        || currentAtLocation.getBlueprint().getCardSubtype() != CardSubtype.SITE
                        || physicalCard.getBlueprint().getCardSubtype() != CardSubtype.SITE
                        || Filters.sameCardId(currentAtLocation).accepts(gameState, modifiersQuerying, physicalCard)
                        || (!cardToMove.getBlueprint().isMovesLikeCharacter()
                            && !Filters.or(Filters.character, Filters.creature, Filters.vehicle).accepts(gameState, modifiersQuerying, cardToMove))) {
                    return false;
                }

                // 2) If vehicle, check if it is piloted.
                if (cardToMove.getBlueprint().getCardCategory() == CardCategory.VEHICLE
                        && !modifiersQuerying.isPiloted(gameState, cardToMove, false)) {
                    return false;
                }

                // 3) Determine if card has enough landspeed to reach the destination site
                Integer landspeedRequired = modifiersQuerying.getLandspeedRequired(gameState, cardToMove, physicalCard);
                if (landspeedRequired == null) {
                    return false;
                }

                float landspeed = modifiersQuerying.getLandspeed(gameState, cardToMove);
                if (landspeedOverride != null && landspeedOverride > landspeed && !modifiersQuerying.isProhibitedFromHavingLandspeedIncreased(gameState, cardToMove)) {
                    landspeed = landspeedOverride;
                }

                if (landspeed < landspeedRequired) {
                    return false;
                }

                // 4) Check that card is allowed to move from current site to destination site (and from/to each site along the way)
                if (modifiersQuerying.mayNotMoveFromLocationToLocationUsingLandspeed(gameState, cardToMove, currentAtLocation, physicalCard, asReact)) {
                    return false;
                }

                // 5) Check that there is enough Force available to use for this move
                if (!forFree) {
                    if (modifiersQuerying.getForceAvailableToUse(gameState, playerId)
                            < modifiersQuerying.getMoveUsingLandspeedCost(gameState, cardToMove, currentAtLocation, physicalCard, asReact, changeInCost)) {
                        return false;
                    }
                }

                return true;
            }
        };
    }

    /**
     * Filter that accepts cards that can move to a location using hyperspeed.
     *
     * @param playerId the player to move the card
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @param forFree true if the movement is to be free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required to perform the movement
     * @return Filter
     */
    public static Filter canMoveUsingHyperspeed(final String playerId, final boolean asReact, final boolean forFree, final float changeInCost) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return Filters.canSpotFromTopLocationsOnTable(gameState.getGame(), Filters.canMoveToUsingHyperspeed(playerId, physicalCard, asReact, forFree, changeInCost));
            }
        };
    }

    /**
     * Filter that accepts locations that the specified card can move to using hyperspeed.
     *
     * @param playerId the player to move the card
     * @param cardToMove the card to move
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @param forFree true if the movement is to be free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required to perform the movement
     * @return Filter
     */
    public static Filter canMoveToUsingHyperspeed(final String playerId, PhysicalCard cardToMove, final boolean asReact, final boolean forFree, final float changeInCost) {
        final Integer permCardToMoveCardId = cardToMove.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard cardToMove = gameState.findCardByPermanentId(permCardToMoveCardId);

                // 1) Check that card to move is a mobile system, or is a starship that is present at a system and the
                // location to move to is a different system
                PhysicalCard currentAtLocation = cardToMove.getAtLocation();

                // If moving a react, card can disembark first
                // (unless during the move as 'react', in which case the card must disembark first before moving to another location)
                if (asReact && cardToMove.getAttachedTo() != null && !gameState.isDuringMoveAsReact()) {
                    currentAtLocation = cardToMove.getAttachedTo().getAtLocation();
                }

                if (!Filters.mobile_system.accepts(gameState, modifiersQuerying, cardToMove)
                        && (cardToMove.getBlueprint().getCardCategory() != CardCategory.STARSHIP
                            || currentAtLocation == null
                            || currentAtLocation.getBlueprint().getCardSubtype() != CardSubtype.SYSTEM
                            || physicalCard.getBlueprint().getCardSubtype() != CardSubtype.SYSTEM
                            || Filters.sameCardId(currentAtLocation).accepts(gameState, modifiersQuerying, physicalCard))) {
                    return false;
                }

                // 2) Check if the from/to locations are a mobile system and the planet it is orbiting (since this is not a "hyperspeed" movement)
                // Or two mobile systems orbiting the same system
                if (cardToMove.getBlueprint().getCardCategory() == CardCategory.STARSHIP
                        && (physicalCard.getTitle().equals(currentAtLocation.getSystemOrbited())
                                || currentAtLocation.getTitle().equals(physicalCard.getSystemOrbited())
                                || currentAtLocation.getSystemOrbited() != null && currentAtLocation.getSystemOrbited().equals(physicalCard.getSystemOrbited()))) {
                    return false;
                }

                // 3) Check if card can move using hyperspeed and has hyperdrive
                if (modifiersQuerying.hasNoHyperdrive(gameState, cardToMove)
                        || modifiersQuerying.mayNotMoveUsingHyperspeed(gameState, cardToMove)) {
                    return false;
                }

                // 4) Check that starship is allowed to move from location and to location
                if (cardToMove.getBlueprint().getCardCategory() == CardCategory.STARSHIP
                        && modifiersQuerying.mayNotMoveFromLocationToLocationUsingHyperspeed(gameState, cardToMove, currentAtLocation, physicalCard, asReact)) {
                    return false;
                }

                // 5) If starship, check that it is piloted and has astromech or nav computer aboard
                if (cardToMove.getBlueprint().getCardCategory() == CardCategory.STARSHIP
                        && (!modifiersQuerying.isPiloted(gameState, cardToMove, false)
                            || !modifiersQuerying.hasAstromechOrNavComputer(gameState, cardToMove))) {
                    return false;
                }

                // 6) Check that starship has enough hyperspeed
                float hyperspeed = modifiersQuerying.getHyperspeed(gameState, cardToMove, currentAtLocation, physicalCard);
                if (hyperspeed == 0)
                    return false;

                int hyperspeedNeeded = Math.abs(currentAtLocation.getParsec() - physicalCard.getParsec());
                if (hyperspeed < hyperspeedNeeded)
                    return false;

                // 7) Check that there is enough Force available to use for this move
                if (!forFree) {
                    if (modifiersQuerying.getForceAvailableToUse(gameState, playerId)
                            < modifiersQuerying.getMoveUsingHyperspeedCost(gameState, cardToMove, currentAtLocation, physicalCard, asReact, changeInCost)) {
                        return false;
                    }
                }

                return true;
            }
        };
    }

    /**
     * Filter that accepts locations that the specified card can move to without using hyperspeed (i.e. between mobile system and orbited system).
     *
     * @param playerId the player to move the card
     * @param cardToMove the card to move
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @param forFree true if the movement is to be free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required to perform the movement
     * @return Filter
     */
    public static Filter canMoveToWithoutUsingHyperspeed(final String playerId, PhysicalCard cardToMove, final boolean asReact, final boolean forFree, final float changeInCost) {
        final Integer permCardToMoveCardId = cardToMove.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard cardToMove = gameState.findCardByPermanentId(permCardToMoveCardId);

                // 1) Check that card to move is a starship that is present at a system and the location to move to is a different system
                PhysicalCard currentAtLocation = cardToMove.getAtLocation();

                // If moving a react, card can disembark first
                // (unless during the move as 'react', in which case the card must disembark first before moving to another location)
                if (asReact && cardToMove.getAttachedTo() != null && !gameState.isDuringMoveAsReact()) {
                    currentAtLocation = cardToMove.getAttachedTo().getAtLocation();
                }

                if ((cardToMove.getBlueprint().getCardCategory() != CardCategory.STARSHIP
                        && !cardToMove.getBlueprint().isDeploysAndMovesLikeStarfighter())
                        || currentAtLocation == null
                        || currentAtLocation.getBlueprint().getCardSubtype() != CardSubtype.SYSTEM
                        || physicalCard.getBlueprint().getCardSubtype() != CardSubtype.SYSTEM
                        || Filters.sameCardId(currentAtLocation).accepts(gameState, modifiersQuerying, physicalCard)) {
                    return false;
                }

                // 2) Check if the from/to locations are a mobile system and the planet it is orbiting, or two mobile systems
                // orbiting the same system
                if (!physicalCard.getTitle().equals(currentAtLocation.getSystemOrbited())
                        && !currentAtLocation.getTitle().equals(physicalCard.getSystemOrbited())
                        && (currentAtLocation.getSystemOrbited() == null || !currentAtLocation.getSystemOrbited().equals(physicalCard.getSystemOrbited()))) {
                    return false;
                }

                // 3) Check that starship is allowed to move from location and to location
                if (modifiersQuerying.mayNotMoveFromLocationToLocationWithoutUsingHyperspeed(gameState, cardToMove, currentAtLocation, physicalCard, asReact)) {
                    return false;
                }

                // 4) Check that starship is piloted
                if (cardToMove.getBlueprint().getCardCategory() != CardCategory.CREATURE
                        && !modifiersQuerying.isPiloted(gameState, cardToMove, false)) {
                    return false;
                }

                // 5) Check that there is enough Force available to use for this move
                if (!forFree) {
                    if (modifiersQuerying.getForceAvailableToUse(gameState, playerId)
                            < modifiersQuerying.getMoveWithoutUsingHyperspeedCost(gameState, cardToMove, currentAtLocation, physicalCard, asReact, changeInCost)) {
                        return false;
                    }
                }

                return true;
            }
        };
    }

    /**
     * Filter that accepts locations that the specified card can move to using sector movement.
     *
     * @param playerId the player to move the card
     * @param cardToMove the card to move
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @param forFree true if the movement is to be free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required to perform the movement
     * @return Filter
     */
    public static Filter canMoveToUsingSectorMovement(final String playerId, PhysicalCard cardToMove, final boolean asReact, final boolean forFree, final float changeInCost) {
        final Integer permCardToMoveCardId = cardToMove.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard cardToMove = gameState.findCardByPermanentId(permCardToMoveCardId);

                PhysicalCard currentAtLocation = cardToMove.getAtLocation();

                // If moving a react, card can disembark first
                // (unless during the move as 'react', in which case the card must disembark first before moving to another location)
                if (asReact && cardToMove.getAttachedTo() != null && !gameState.isDuringMoveAsReact()) {
                    currentAtLocation = cardToMove.getAttachedTo().getAtLocation();
                }

                if (currentAtLocation == null || Filters.sameCardId(currentAtLocation).accepts(gameState, modifiersQuerying, physicalCard)) {
                    return false;
                }

                boolean validDestination = false;
                
                // 1) Check if card to move is a starship at a system and the location to move to is the nearest related asteroid sector
                if ((cardToMove.getBlueprint().getCardCategory() == CardCategory.STARSHIP || cardToMove.getBlueprint().isDeploysAndMovesLikeStarfighter())
                        && currentAtLocation.getBlueprint().getCardSubtype() == CardSubtype.SYSTEM
                        && Filters.nearestRelatedAsteroidSector(currentAtLocation).accepts(gameState, modifiersQuerying, physicalCard)) {
                    validDestination = true;
                }

                // 2) Check if card to move is a starship at a system and the location to move to is the nearest related cloud sector
                if (!validDestination
                        && currentAtLocation.getBlueprint().getCardSubtype() == CardSubtype.SYSTEM
                        && Filters.or(Filters.starfighter, Filters.deploysAndMovesLikeStarfighter).accepts(gameState, modifiersQuerying, cardToMove)
                        && Filters.nearestRelatedCloudSector(currentAtLocation).accepts(gameState, modifiersQuerying, physicalCard)) {
                    validDestination = true;
                }

                // 3) Check if card to move is a starfigher at a system and the location to move to is the nearest related Death Star II sector
                if (!validDestination
                        && currentAtLocation.getBlueprint().getCardSubtype() == CardSubtype.SYSTEM
                        && Filters.starfighter.accepts(gameState, modifiersQuerying, cardToMove)
                        && Filters.nearestRelatedDeathStarIISector(currentAtLocation).accepts(gameState, modifiersQuerying, physicalCard)) {
                    validDestination = true;
                }

                // 4) Check if card move is a starship at nearest related sector to the system location
                if (!validDestination
                        && (cardToMove.getBlueprint().getCardCategory() == CardCategory.STARSHIP || cardToMove.getBlueprint().isDeploysAndMovesLikeStarfighter())
                        && physicalCard.getBlueprint().getCardSubtype() == CardSubtype.SYSTEM
                        && Filters.nearestRelatedSector(physicalCard).accepts(gameState, modifiersQuerying, currentAtLocation)) {
                    validDestination = true;
                }

                // 5) Check if card is starship or vehicle at sector and location is adjacent sector (or within 2 sectors
                //    (except Death Star II sectors) if starfighter or moves like starfighter)
                if (!validDestination
                        && currentAtLocation.getBlueprint().getCardSubtype() == CardSubtype.SECTOR
                        && physicalCard.getBlueprint().getCardSubtype() == CardSubtype.SECTOR
                        && Filters.or(Filters.starship, Filters.deploysAndMovesLikeStarfighter, Filters.and(Filters.vehicle, Filters.hasLandspeed)).accepts(gameState, modifiersQuerying, cardToMove)) {

                    int range = 1;
                    if (!Filters.Death_Star_II_sector.accepts(gameState, modifiersQuerying, currentAtLocation)) {
                        if (Filters.or(Filters.starfighter, Filters.deploysAndMovesLikeStarfighter).accepts(gameState, modifiersQuerying, cardToMove)) {
                            range = 2;
                        } else if (Filters.cloud_sector.accepts(gameState, modifiersQuerying, currentAtLocation)
                                && Filters.cloud_sector.accepts(gameState, modifiersQuerying, physicalCard)
                                && Filters.deploysAndMovesLikeStarfighterAtCloudSectors.accepts(gameState, modifiersQuerying, cardToMove)) {
                            range = 2;
                        }
                    }

                    if (Filters.sectorWithinDistance(currentAtLocation, range).accepts(gameState, modifiersQuerying, physicalCard)) {
                        validDestination = true;
                    }
                }

                // 6) Check if location is a valid destination
                if (!validDestination) {
                    return false;
                }

                // 7) Check that card is allowed to move from location and to location
                if (modifiersQuerying.mayNotMoveFromLocationToLocationUsingSectorMovement(gameState, cardToMove, currentAtLocation, physicalCard, asReact)) {
                    return false;
                }

                // 7) Check that it is piloted
                if (cardToMove.getBlueprint().getCardCategory() != CardCategory.CREATURE
                        && !modifiersQuerying.isPiloted(gameState, cardToMove, false)) {
                    return false;
                }

                // 8) Check that there is enough Force available to use for this move
                if (!forFree) {
                    if (modifiersQuerying.getForceAvailableToUse(gameState, playerId)
                            < modifiersQuerying.getMoveUsingSectorMovementCost(gameState, cardToMove, currentAtLocation, physicalCard, asReact, changeInCost)) {
                        return false;
                    }
                }

                return true;
            }
        };
    }

    /**
     * Filter that accepts cards that can attempt to 'escape' Death Star II while it is being 'blown away'.
     *
     * @param playerId the player to move the card
     * @return Filter
     */
    public static Filter movableToEscapeDeathStarII(final String playerId) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (Filters.and(Filters.owner(playerId), Filters.starfighter, Filters.at(Filters.Death_Star_II_sector)).accepts(gameState, modifiersQuerying, physicalCard)) {
                    return physicalCard.getBlueprint().getMoveUsingEscapeFromDeathStarIIMovementAction(playerId, gameState.getGame(), physicalCard) != null;
                }
                return false;
            }
        };
    }

    /**
     * Filter that accepts locations that the specified card can move to by landing.
     *
     * @param playerId the player to move the card
     * @param cardToMove the card to move
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @param forFree true if the movement is to be free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required to perform the movement
     * @return Filter
     */
    public static Filter canLandToLocation(final String playerId, PhysicalCard cardToMove, final boolean asReact, final boolean forFree, final float changeInCost) {
        final Integer permCardToMoveCardId = cardToMove.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard cardToMove = gameState.findCardByPermanentId(permCardToMoveCardId);

                PhysicalCard currentAtLocation = cardToMove.getAtLocation();

                // If moving a react, card can disembark first
                // (unless during the move as 'react', in which case the card must disembark first before moving to another location)
                if (asReact && cardToMove.getAttachedTo() != null && !gameState.isDuringMoveAsReact()) {
                    currentAtLocation = cardToMove.getAttachedTo().getAtLocation();
                }

                if (currentAtLocation == null || Filters.sameCardId(currentAtLocation).accepts(gameState, modifiersQuerying, physicalCard)) {
                    return false;
                }

                boolean validDestination = false;

                // Check if card to move is present with a starship that has a related starship site that can be landed at
                if (Filters.or(Filters.starfighter, Filters.deploysAndMovesLikeStarfighter, Filters.squadron).accepts(gameState, modifiersQuerying, cardToMove)
                        && Filters.and(Filters.starshipSiteToShuttleTransferLandAndTakeOffAtForFreeInsteadOfRelatedStarship(playerId),
                        Filters.relatedSiteTo(null, Filters.and(Filters.your(cardToMove), Filters.capital_starship, Filters.presentWith(cardToMove)))).accepts(gameState, modifiersQuerying, physicalCard)) {
                    validDestination = true;
                }
                // Check if card to move is a starfighter or squadron at a system and the location to move to is a
                // related exterior site.
                else if (currentAtLocation.getBlueprint().getCardSubtype() == CardSubtype.SYSTEM
                        && currentAtLocation.getTitle().equals(physicalCard.getPartOfSystem())
                        && Filters.exterior_site.accepts(gameState, modifiersQuerying, physicalCard)
                        && Filters.or(Filters.starfighter, Filters.deploysAndMovesLikeStarfighter, Filters.squadron).accepts(gameState, modifiersQuerying, cardToMove)) {
                    // Check if no sectors in between
                    List<PhysicalCard> sectorsBetween = modifiersQuerying.getSectorsBetween(gameState, currentAtLocation, physicalCard);
                    validDestination = (sectorsBetween != null && sectorsBetween.isEmpty());
                }
                // Check if card to move is at a cloud sector and the location to move to is a related exterior site.
                else if (currentAtLocation.getBlueprint().hasKeyword(Keyword.CLOUD_SECTOR)
                        && currentAtLocation.getPartOfSystem() != null && currentAtLocation.getPartOfSystem().equals(physicalCard.getPartOfSystem())
                        && Filters.exterior_site.accepts(gameState, modifiersQuerying, physicalCard)
                        && Filters.or(Filters.starfighter, Filters.deploysAndMovesLikeStarfighter, Filters.deploysAndMovesLikeStarfighterAtCloudSectors, Filters.squadron).accepts(gameState, modifiersQuerying, cardToMove)) {
                    // Check if no sectors in between
                    List<PhysicalCard> sectorsBetween = modifiersQuerying.getSectorsBetween(gameState, currentAtLocation, physicalCard);
                    validDestination = (sectorsBetween != null && sectorsBetween.isEmpty());
                }
                // Check if card to move is at a system and there is a starship docking bay related to a starship that is at that system
                else if (currentAtLocation.getBlueprint().getCardSubtype() == CardSubtype.SYSTEM
                        && Filters.or(Filters.starfighter, Filters.deploysAndMovesLikeStarfighter, Filters.squadron).accepts(gameState, modifiersQuerying, cardToMove)
                        && Filters.and(Filters.starship_site, Filters.docking_bay, Filters.relatedSiteTo(null, Filters.and(Filters.starship, Filters.present(currentAtLocation)))).accepts(gameState, modifiersQuerying, physicalCard)) {
                    validDestination = true;
                }
                // Check if card to move is at a Big One and the location to move to is the related site.
                else if (Filters.Big_One.accepts(gameState, modifiersQuerying, currentAtLocation)
                        && Filters.and(Filters.Big_One_Asteroid_Cave_Or_Space_Slug_Belly, Filters.relatedSite(currentAtLocation)).accepts(gameState, modifiersQuerying, physicalCard)
                        && Filters.or(Filters.starfighter, Filters.deploysAndMovesLikeStarfighter, Filters.squadron).accepts(gameState, modifiersQuerying, cardToMove)) {
                    validDestination = true;
                }

                // 1) Check if location is a valid destination
                if (!validDestination) {
                    return false;
                }

                // 2) Check that card is allowed to move from location and to location
                if (modifiersQuerying.mayNotLandFromLocationToLocation(gameState, cardToMove, currentAtLocation, physicalCard, asReact)) {
                    return false;
                }

                // 3) Check that it is piloted
                if (cardToMove.getBlueprint().getCardCategory() != CardCategory.CREATURE
                        && !modifiersQuerying.isPiloted(gameState, cardToMove, false)) {
                    return false;
                }

                // 4) Check that there is enough Force available to use for this move
                if (!forFree) {
                    if (modifiersQuerying.getForceAvailableToUse(gameState, playerId)
                            < modifiersQuerying.getLandingCost(gameState, cardToMove, currentAtLocation, physicalCard, asReact, changeInCost)) {
                        return false;
                    }
                }

                return true;
            }
        };
    }

    /**
     * Filter that accepts locations that the specified card can move to by taking off.
     *
     * @param playerId the player to move the card
     * @param cardToMove the card to move
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @param forFree true if the movement is to be free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required to perform the movement
     * @return Filter
     */
    public static Filter canTakeOffToLocation(final String playerId, PhysicalCard cardToMove, final boolean asReact, final boolean forFree, final float changeInCost) {
        final Integer permCardToMoveCardId = cardToMove.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard cardToMove = gameState.findCardByPermanentId(permCardToMoveCardId);

                PhysicalCard currentAtLocation = cardToMove.getAtLocation();

                // If moving a react, card can disembark first
                // (unless during the move as 'react', in which case the card must disembark first before moving to another location)
                if (asReact && cardToMove.getAttachedTo() != null && !gameState.isDuringMoveAsReact()) {
                    currentAtLocation = cardToMove.getAttachedTo().getAtLocation();
                }

                if (currentAtLocation == null || Filters.sameCardId(currentAtLocation).accepts(gameState, modifiersQuerying, physicalCard)) {
                    return false;
                }

                boolean validDestination = false;

                // Check if destination is location is where the starship related to the starship site is present at
                if (physicalCard.getBlueprint().getCardCategory() == CardCategory.LOCATION
                        && Filters.or(Filters.starfighter, Filters.deploysAndMovesLikeStarfighter, Filters.squadron).accepts(gameState, modifiersQuerying, cardToMove)
                        && Filters.starshipSiteToShuttleTransferLandAndTakeOffAtForFreeInsteadOfRelatedStarship(cardToMove.getOwner()).accepts(gameState, modifiersQuerying, currentAtLocation)) {
                    validDestination = Filters.siteOfStarshipOrVehicle(Filters.present(physicalCard)).accepts(gameState, modifiersQuerying, currentAtLocation);
                }
                // Check if card to move is a starfighter or squadron at an exterior site and the location to move to is the
                // related system.
                else if (physicalCard.getBlueprint().getCardSubtype() == CardSubtype.SYSTEM
                        && physicalCard.getTitle().equals(currentAtLocation.getPartOfSystem())
                        && Filters.exterior_site.accepts(gameState, modifiersQuerying, currentAtLocation)
                        && Filters.or(Filters.starfighter, Filters.deploysAndMovesLikeStarfighter, Filters.squadron).accepts(gameState, modifiersQuerying, cardToMove)) {
                    // Check if no sectors in between
                    validDestination = modifiersQuerying.getSectorsBetween(gameState, currentAtLocation, physicalCard).isEmpty();
                }
                // Check if card to move is at an exterior site and the location to move to is a related cloud sector.
                else if (physicalCard.getBlueprint().hasKeyword(Keyword.CLOUD_SECTOR)
                        && physicalCard.getPartOfSystem() != null && physicalCard.getPartOfSystem().equals(currentAtLocation.getPartOfSystem())
                        && Filters.exterior_site.accepts(gameState, modifiersQuerying, currentAtLocation)
                        && Filters.or(Filters.starfighter, Filters.deploysAndMovesLikeStarfighter, Filters.deploysAndMovesLikeStarfighterAtCloudSectors, Filters.squadron).accepts(gameState, modifiersQuerying, cardToMove)) {
                    // Check if no sectors in between
                    validDestination = modifiersQuerying.getSectorsBetween(gameState, currentAtLocation, physicalCard).isEmpty();
                }
                // Check if the location is a system and card to move is at a starship docking bay related to a starship at that system
                else if (physicalCard.getBlueprint().getCardSubtype() == CardSubtype.SYSTEM
                        && Filters.or(Filters.starfighter, Filters.deploysAndMovesLikeStarfighter, Filters.squadron).accepts(gameState, modifiersQuerying, cardToMove)
                        && Filters.and(Filters.starship_site, Filters.docking_bay, Filters.relatedSiteTo(null, Filters.and(Filters.starship, Filters.present(physicalCard)))).accepts(gameState, modifiersQuerying, currentAtLocation)) {
                    validDestination = true;
                }
                // Check if card to move is at a Big One site and the location to move to is the related Big One.
                else if (Filters.Big_One.accepts(gameState, modifiersQuerying, physicalCard)
                        && Filters.and(Filters.Big_One_Asteroid_Cave_Or_Space_Slug_Belly, Filters.relatedSite(physicalCard)).accepts(gameState, modifiersQuerying, currentAtLocation)
                        && Filters.or(Filters.starfighter, Filters.deploysAndMovesLikeStarfighter, Filters.squadron).accepts(gameState, modifiersQuerying, cardToMove)) {
                    validDestination = true;
                }

                // 1) Check if location is a valid destination
                if (!validDestination) {
                    return false;
                }

                // 2) Check that card is allowed to move from location and to location
                if (modifiersQuerying.mayNotTakeOffFromLocationToLocation(gameState, cardToMove, currentAtLocation, physicalCard, asReact)) {
                    return false;
                }

                // 3) Check that it is piloted
                if (cardToMove.getBlueprint().getCardCategory() != CardCategory.CREATURE
                        && !modifiersQuerying.isPiloted(gameState, cardToMove, true)) {
                    return false;
                }

                // 4) Check that there is enough Force available to use for this move
                if (!forFree) {
                    if (modifiersQuerying.getForceAvailableToUse(gameState, playerId)
                            < modifiersQuerying.getTakeOffCost(gameState, cardToMove, currentAtLocation, physicalCard, asReact, changeInCost)) {
                        return false;
                    }
                }

                return true;
            }
        };
    }

    /**
     * Filter that accepts locations that the specified card can move to at start of a Bombing Run.
     *
     * @param playerId the player to move the card
     * @param cardToMove the card to move
     * @return Filter
     */
    public static Filter canMoveToLocationToStartBombingRun(final String playerId, PhysicalCard cardToMove, final boolean forFree, final float changeInCost) {
        final Integer permCardToMoveCardId = cardToMove.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard cardToMove = gameState.findCardByPermanentId(permCardToMoveCardId);

                // 1) Check the card is at the location that player controls
                PhysicalCard currentAtLocation = cardToMove.getAtLocation();
                if (currentAtLocation == null || !Filters.and(Filters.system, Filters.controls(playerId)).accepts(gameState, modifiersQuerying, currentAtLocation)) {
                    return false;
                }

                // 2) Check if location is a related site with Bombing Run attached
                if (!Filters.and(Filters.hasAttached(Filters.Bombing_Run), Filters.relatedSite(currentAtLocation)).accepts(gameState, modifiersQuerying, physicalCard)) {
                    return false;
                }

                // 3) Check that card is allowed to move from location and to location
                if (modifiersQuerying.mayNotMoveFromLocationToLocationToStartBombingRun(gameState, cardToMove, currentAtLocation, physicalCard)) {
                    return false;
                }

                // 4) Check that it is piloted
                if (!modifiersQuerying.isPiloted(gameState, cardToMove, false)) {
                    return false;
                }

                // 5) Check that there is enough Force available to use for this move
                if (!forFree) {
                    if (modifiersQuerying.getForceAvailableToUse(gameState, playerId)
                            < modifiersQuerying.getMoveToStartBombingRunCost(gameState, cardToMove, currentAtLocation, physicalCard, changeInCost)) {
                        return false;
                    }
                }

                return true;
            }
        };
    }

    /**
     * Filter that accepts cards that can perform a "move at start of Attack Run" action.
     *
     * @param playerId the player to move the card
     * @return Filter
     */
    public static Filter canMoveAtStartOfAttackRun(final String playerId) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return physicalCard.getBlueprint().getMoveAtStartOfAttackRunAction(playerId, gameState.getGame(), physicalCard) != null;
            }
        };
    }

    /**
     * Filter that accepts locations that the specified card can move to at start of an Attack Run.
     *
     * @param playerId the player to move the card
     * @param cardToMove the card to move
     * @return Filter
     */
    public static Filter canMoveToLocationAtStartOfAttackRun(final String playerId, PhysicalCard cardToMove) {
        final Integer permCardToMoveCardId = cardToMove.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard cardToMove = gameState.findCardByPermanentId(permCardToMoveCardId);

                // 1) Check the card is at the Death Star system
                PhysicalCard currentAtLocation = cardToMove.getAtLocation();
                if (currentAtLocation == null || !Filters.Death_Star_system.accepts(gameState, modifiersQuerying, currentAtLocation)) {
                    return false;
                }

                // 2) Check if location is Death Star: Trench
                if (!Filters.Death_Star_Trench.accepts(gameState, modifiersQuerying, physicalCard)) {
                    return false;
                }

                // 3) Check that card is allowed to move from location and to location
                if (modifiersQuerying.mayNotMoveFromLocationToLocationAtStartOfAttackRun(gameState, cardToMove, currentAtLocation, physicalCard)) {
                    return false;
                }

                // 4) Check that it is piloted
                if (!modifiersQuerying.isPiloted(gameState, cardToMove, false)) {
                    return false;
                }

                return true;
            }
        };
    }

    /**
     * Filter that accepts cards that can perform a "move at end of Attack Run" action.
     *
     * @param playerId the player to move the card
     * @return Filter
     */
    public static Filter canMoveAtEndOfAttackRun(final String playerId) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return physicalCard.getBlueprint().getMoveAtEndOfAttackRunAction(playerId, gameState.getGame(), physicalCard) != null;
            }
        };
    }

    /**
     * Filter that accepts locations that the specified card can move to at end of an Attack Run.
     *
     * @param playerId the player to move the card
     * @param cardToMove the card to move
     * @return Filter
     */
    public static Filter canMoveToLocationAtEndOfAttackRun(final String playerId, PhysicalCard cardToMove) {
        final Integer permCardToMoveCardId = cardToMove.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard cardToMove = gameState.findCardByPermanentId(permCardToMoveCardId);

                // 1) Check the card is at the Death Star system
                PhysicalCard currentAtLocation = cardToMove.getAtLocation();
                if (currentAtLocation == null || !Filters.Death_Star_Trench.accepts(gameState, modifiersQuerying, currentAtLocation)) {
                    return false;
                }

                // 2) Check if location is Death Star system
                if (!Filters.Death_Star_system.accepts(gameState, modifiersQuerying, physicalCard)) {
                    return false;
                }

                // 3) Check that card is allowed to move from location and to location
                if (modifiersQuerying.mayNotMoveFromLocationToLocationAtEndOfAttackRun(gameState, cardToMove, currentAtLocation, physicalCard)) {
                    return false;
                }

                return true;
            }
        };
    }

    /**
     * Filter that accepts locations that the specified card can move to at end of a Bombing Run.
     *
     * @param playerId the player to move the card
     * @param cardToMove the card to move
     * @return Filter
     */
    public static Filter canMoveToLocationToEndBombingRun(final String playerId, PhysicalCard cardToMove) {
        final Integer permCardToMoveCardId = cardToMove.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard cardToMove = gameState.findCardByPermanentId(permCardToMoveCardId);

                if (!cardToMove.isMakingBombingRun()) {
                    return false;
                }
                // 1) Check the location is the related system
                PhysicalCard currentAtLocation = cardToMove.getAtLocation();
                if (currentAtLocation == null || !Filters.relatedSystem(cardToMove).accepts(gameState, modifiersQuerying, physicalCard)) {
                    return false;
                }

                // 2) Check that card is allowed to move from location and to location
                if (modifiersQuerying.mayNotMoveFromLocationToLocationToEndBombingRun(gameState, cardToMove, currentAtLocation, physicalCard)) {
                    return false;
                }

                // 3) Check that it is piloted
                if (!modifiersQuerying.isPiloted(gameState, cardToMove, false)) {
                    return false;
                }

                return true;
            }
        };
    }

    /**
     * Filter that accepts starship sites that that the player can shuttle, transfer, land, and take off at instead of
     * the related starship.
     *
     * @param playerId the player
     * @return Filter
     */
    public static Filter starshipSiteToShuttleTransferLandAndTakeOffAtForFreeInsteadOfRelatedStarship(final String playerId) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (!physicalCard.getBlueprint().hasIcon(Icon.STARSHIP_SITE)) {
                    return false;
                }
                return modifiersQuerying.mayShuttleTransferLandAndTakeOffForFreeAtInsteadOfRelatedStarship(gameState, playerId, physicalCard);
            }
        };
    }

    /**
     * Filter that accepts cards that the specified card can move to by shuttling.
     *
     * @param playerId the player to move the card
     * @param cardToMove the card to move
     * @param forFree true if the movement is to be free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required to perform the movement
     * @return Filter
     */
    public static Filter canShuttleTo(final String playerId, PhysicalCard cardToMove, final boolean forFree, final float changeInCost) {
        final Integer permCardToMoveCardId = cardToMove.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard cardToMove = gameState.findCardByPermanentId(permCardToMoveCardId);

                // Check if current location is exterior site, and this card is capital starship at related system, or vice versa
                PhysicalCard atLocation = cardToMove.getAtLocation();
                PhysicalCard attachedTo = cardToMove.getAttachedTo();
                if ((atLocation == null && attachedTo == null)
                        || (cardToMove.getBlueprint().getCardCategory() != CardCategory.CHARACTER
                        && cardToMove.getBlueprint().getCardCategory() != CardCategory.VEHICLE)) {
                    return false;
                }

                boolean validDestination = false;
                PhysicalCard fromLocation = null;
                PhysicalCard toLocation = null;

                if (atLocation != null) {

                    if (physicalCard.getBlueprint().getCardCategory() == CardCategory.LOCATION
                            && modifiersQuerying.mayShuttleDirectlyFromLocationToLocation(gameState, cardToMove, atLocation, physicalCard)) {
                        validDestination = true;
                        fromLocation = atLocation;
                        toLocation = physicalCard;
                    }
                    else if (atLocation.getBlueprint().hasIcon(Icon.EXTERIOR_SITE)) {

                        // Check if shuttling from exterior site to capital starship (or related starship site that may be shuttled to)
                        if (!atLocation.getBlueprint().hasIcon(Icon.STARSHIP_SITE)) {

                            Filter yourCapitalStarshipAtRelatedSystem = Filters.and(Filters.your(cardToMove), Filters.capital_starship, Filters.presentAt(Filters.relatedSystem(atLocation)));

                            // Check that destination is capital starship at related system (or related starship site that may be shuttled to)
                            if (!yourCapitalStarshipAtRelatedSystem.accepts(gameState, modifiersQuerying, physicalCard)
                                    && !Filters.and(Filters.starshipSiteToShuttleTransferLandAndTakeOffAtForFreeInsteadOfRelatedStarship(cardToMove.getOwner()),
                                    Filters.relatedSiteTo(null, yourCapitalStarshipAtRelatedSystem)).accepts(gameState, modifiersQuerying, physicalCard)) {
                                return false;
                            }

                            if (physicalCard.getBlueprint().getCardCategory() == CardCategory.LOCATION) {
                                validDestination = true;
                                toLocation = physicalCard;
                            } else {
                                // Check that starship has sufficent capacity
                                if (cardToMove.getBlueprint().getCardCategory() == CardCategory.CHARACTER || cardToMove.getBlueprint().isMovesLikeCharacter()) {
                                    Filter filter = Filters.or(Filters.hasAvailablePilotCapacity(cardToMove), Filters.hasAvailablePassengerCapacity(cardToMove));
                                    if (filter.accepts(gameState, modifiersQuerying, physicalCard)) {
                                        validDestination = true;
                                    }
                                }
                                if (cardToMove.getBlueprint().getCardCategory() == CardCategory.VEHICLE || cardToMove.getBlueprint().isVehicleSlotOfStarshipCompatible()) {
                                    if (Filters.hasAvailableVehicleCapacity(cardToMove).accepts(gameState, modifiersQuerying, physicalCard)) {
                                        validDestination = true;
                                    }
                                }
                                if (Filters.or(Filters.starfighter, Filters.squadron, Filters.TIE).accepts(gameState, modifiersQuerying, cardToMove)) {
                                    if (Filters.hasAvailableStarfighterOrTIECapacity(cardToMove).accepts(gameState, modifiersQuerying, physicalCard)) {
                                        validDestination = true;
                                    }
                                }
                                if (Filters.capital_starship.accepts(gameState, modifiersQuerying, cardToMove)) {
                                    if (Filters.hasAvailableCapitalStarshipCapacity(cardToMove).accepts(gameState, modifiersQuerying, physicalCard)) {
                                        validDestination = true;
                                    }
                                }
                                toLocation = physicalCard.getAtLocation();
                            }
                            fromLocation = atLocation;
                        }
                        // Check if shuttling from starship site (instead of related capital starship) to exterior site
                        else if (Filters.starshipSiteToShuttleTransferLandAndTakeOffAtForFreeInsteadOfRelatedStarship(cardToMove.getOwner()).accepts(gameState, modifiersQuerying, atLocation)) {

                            // Check that destination is an exterior site
                            if (Filters.exterior_site.accepts(gameState, modifiersQuerying, physicalCard)) {
                                Filter yourCapitalStarshipAtRelatedSystem = Filters.and(Filters.your(cardToMove), Filters.capital_starship, Filters.presentAt(Filters.relatedSystem(physicalCard)));
                                if (Filters.siteOfStarshipOrVehicle(yourCapitalStarshipAtRelatedSystem).accepts(gameState, modifiersQuerying, atLocation)) {
                                    validDestination = true;
                                }
                            }

                            fromLocation = atLocation;
                            toLocation = physicalCard;
                        }
                    }
                }
                // Check if shuttling from capital starship to exterior site
                else if (attachedTo.getBlueprint().getCardSubtype() == CardSubtype.CAPITAL
                        && (cardToMove.isPilotOf() || cardToMove.isPassengerOf() || cardToMove.isInCargoHoldAsVehicle() || cardToMove.isInCargoHoldAsStarfighterOrTIE() || cardToMove.isInCargoHoldAsCapitalStarship())
                        && attachedTo.getAtLocation() != null && attachedTo.getAtLocation().getBlueprint().getCardSubtype() == CardSubtype.SYSTEM) {

                    // Check that destination is related exterior site to the system the capital starship is at
                    if (Filters.and(Filters.exterior_site, Filters.relatedSite(attachedTo.getAtLocation())).accepts(gameState, modifiersQuerying, physicalCard)) {
                        validDestination = true;
                    }

                    fromLocation = attachedTo.getAtLocation();
                    toLocation = physicalCard;
                }

                // 1) Check if destination is valid
                if (!validDestination) {
                    return false;
                }

                // 2) Check that card is allowed to shuttle from location to location
                if (modifiersQuerying.mayNotShuttleFromLocationToLocation(gameState, cardToMove, fromLocation, toLocation)) {
                    return false;
                }

                // 3) Check that there is enough Force available to use for this move
                if (!forFree) {
                    if (modifiersQuerying.getForceAvailableToUse(gameState, playerId)
                            < modifiersQuerying.getShuttleCost(gameState, cardToMove, fromLocation, toLocation, changeInCost)) {
                        return false;
                    }
                }

                return true;
            }
        };
    }

    /**
     * Filter that accepts characters that can be shuttled up using the shuttle vehicle to a starship accepted by
     * the starship filter.
     *
     * @param shuttleVehicle the shuttle vehicle
     * @param starshipFilter the starship filter
     * @return Filter
     */
    public static Filter canShuttleUpUsingShuttleVehicle(PhysicalCard shuttleVehicle, final Filter starshipFilter) {
        final Integer permShuttleVehicleCardId = shuttleVehicle.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard shuttleVehicle = gameState.findCardByPermanentId(permShuttleVehicleCardId);

                // Check if shuttle vehicle is physically at an exterior site
                PhysicalCard atLocation = shuttleVehicle.getAtLocation();
                if (atLocation == null || !Filters.exterior_site.accepts(gameState, modifiersQuerying, atLocation)
                        || physicalCard.getBlueprint().getCardCategory() != CardCategory.CHARACTER) {
                    return false;
                }

                // Check that there is a related system
                PhysicalCard relatedSystem = Filters.findFirstFromTopLocationsOnTable(gameState.getGame(), Filters.relatedSystem(atLocation));
                if (relatedSystem == null) {
                    return false;
                }

                // Check if character can move (and has not already performed a regular move this turn)
                if (modifiersQuerying.mayNotMove(gameState, physicalCard)
                        || modifiersQuerying.hasPerformedRegularMoveThisTurn(physicalCard)) {
                    return false;
                }

                // Check if card is a passenger of the shuttle vehicle
                if (Filters.aboardAsPassenger(shuttleVehicle).accepts(gameState, modifiersQuerying, physicalCard)) {

                    // Check that card is allowed to shuttle from location to location
                    if (!modifiersQuerying.mayNotShuttleFromLocationToLocation(gameState, physicalCard, atLocation, relatedSystem)) {

                        // Get starships at related system
                        Collection<PhysicalCard> starships = Filters.filterActive(gameState.getGame(), null,
                                Filters.and(Filters.your(shuttleVehicle), Filters.or(Filters.capital_starship, Filters.starfighter), starshipFilter, Filters.present(relatedSystem)));

                        // Check if character can be shuttled to a starship
                        for (PhysicalCard starship : starships) {

                            // Check that starship is a valid destination
                            if (physicalCard.getBlueprint().getValidMoveTargetFilter(physicalCard.getOwner(), gameState.getGame(), physicalCard, false).accepts(gameState, modifiersQuerying, starship)) {
                                Filter filter = Filters.or(Filters.hasAvailablePilotCapacity(physicalCard), Filters.hasAvailablePassengerCapacity(physicalCard));
                                if (Filters.and(filter).accepts(gameState, modifiersQuerying, starship)) {
                                    return true;
                                }
                            }
                        }
                    }
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts characters that can be shuttled down using the shuttle vehicle from a starship accepted by
     * the starship filter.
     *
     * @param shuttleVehicle the shuttle vehicle
     * @param starshipFilter the starship filter
     * @return Filter
     */
    public static Filter canShuttleDownUsingShuttleVehicle(PhysicalCard shuttleVehicle, final Filter starshipFilter) {
        final Integer permShuttleVehicleCardId = shuttleVehicle.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard shuttleVehicle = gameState.findCardByPermanentId(permShuttleVehicleCardId);

                // Check if shuttle vehicle is physically at an exterior site
                PhysicalCard atLocation = shuttleVehicle.getAtLocation();
                if (atLocation == null || !Filters.exterior_site.accepts(gameState, modifiersQuerying, atLocation)
                        || physicalCard.getBlueprint().getCardCategory() != CardCategory.CHARACTER
                        || !physicalCard.getOwner().equals(shuttleVehicle.getOwner())) {
                    return false;
                }

                // Check that there is a related system
                PhysicalCard relatedSystem = Filters.findFirstFromTopLocationsOnTable(gameState.getGame(), Filters.relatedSystem(atLocation));
                if (relatedSystem == null) {
                    return false;
                }

                // Check if character can move (and has not already performed a regular move this turn)
                if (modifiersQuerying.mayNotMove(gameState, physicalCard)
                        || modifiersQuerying.hasPerformedRegularMoveThisTurn(physicalCard)) {
                    return false;
                }

                // Get starships at related system
                Collection<PhysicalCard> starships = Filters.filterActive(gameState.getGame(), null,
                        Filters.and(Filters.your(shuttleVehicle), Filters.or(Filters.capital_starship, Filters.starfighter), starshipFilter, Filters.present(relatedSystem)));

                // Check if card is aboard a starship (not including at related starship sites) at the related system
                for (PhysicalCard starship : starships) {
                    if (modifiersQuerying.isAboard(gameState, physicalCard, starship, false, false)) {

                        // Check that card is allowed to shuttle from location to location
                        if (!modifiersQuerying.mayNotShuttleFromLocationToLocation(gameState, physicalCard, relatedSystem, atLocation)) {

                            // Check if character can be shuttled using shuttle vehicle
                            if (physicalCard.getBlueprint().getValidMoveTargetFilter(physicalCard.getOwner(), gameState.getGame(), physicalCard, false).accepts(gameState, modifiersQuerying, shuttleVehicle)) {
                                if (Filters.hasAvailablePassengerCapacity(physicalCard).accepts(gameState, modifiersQuerying, shuttleVehicle)) {
                                    return true;
                                }
                            }
                        }
                    }
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that the specified card can move to by moving to the related starship or vehicle from
     * a related starship or vehicle site.
     *
     * @param playerId the player to move the card
     * @param cardToMove the card to move
     * @return Filter
     */
    public static Filter canMoveToRelatedStarshipOrVehicle(final String playerId, PhysicalCard cardToMove) {
        final Integer permCardToMoveCardId = cardToMove.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard cardToMove = gameState.findCardByPermanentId(permCardToMoveCardId);

                PhysicalCard currentAtLocation = cardToMove.getAtLocation();
                if (currentAtLocation == null
                        || (!currentAtLocation.getBlueprint().hasIcon(Icon.STARSHIP_SITE)
                        && !currentAtLocation.getBlueprint().hasIcon(Icon.VEHICLE_SITE))) {
                    return false;
                }

                // 1) Check that destination is player's starship or vehicle
                if (!cardToMove.getOwner().equals(physicalCard.getOwner())
                        || (physicalCard.getBlueprint().getCardCategory() != CardCategory.STARSHIP
                        && physicalCard.getBlueprint().getCardCategory() != CardCategory.VEHICLE))
                    return false;

                boolean validDestination = false;

                // 2) Check the starship/vehicle is related to the site (or has the related starship/vehicle aboard)
                if (Filters.siteOfStarshipOrVehicle(physicalCard).accepts(gameState, modifiersQuerying, currentAtLocation)) {
                    validDestination = true;
                }
                else if (physicalCard.getBlueprint().getCardCategory() == CardCategory.STARSHIP) {
                    Collection<PhysicalCard> vehiclesAboard = Filters.filterAllOnTable(gameState.getGame(), Filters.and(Filters.vehicle, Filters.aboard(physicalCard)));
                    for (PhysicalCard vehicleAboard : vehiclesAboard) {
                        if (Filters.siteOfStarshipOrVehicle(vehicleAboard).accepts(gameState, modifiersQuerying, currentAtLocation)) {
                            validDestination = true;
                            break;
                        }
                    }
                }

                if (!validDestination)
                    return false;

                // 3) Check that card is allowed to move from starship/vehicle site to starship/vehicle
                if (modifiersQuerying.mayNotMoveFromSiteToRelatedStarshipOrVehicle(gameState, cardToMove, currentAtLocation, physicalCard)) {
                    return false;
                }

                // 4) Check that starship/vehicle has sufficent capacity
                if (cardToMove.getBlueprint().getCardCategory() == CardCategory.CHARACTER || cardToMove.getBlueprint().isMovesLikeCharacter()) {
                    Filter filter = Filters.or(Filters.hasAvailablePilotCapacity(cardToMove), Filters.hasAvailablePassengerCapacity(cardToMove));
                    if (Filters.and(filter).accepts(gameState, modifiersQuerying, physicalCard)) {
                        return true;
                    }
                }
                if (cardToMove.getBlueprint().getCardCategory() == CardCategory.VEHICLE || cardToMove.getBlueprint().isVehicleSlotOfStarshipCompatible()) {
                    if (Filters.hasAvailableVehicleCapacity(cardToMove).accepts(gameState, modifiersQuerying, physicalCard)) {
                        return true;
                    }
                }
                if (Filters.or(Filters.starfighter, Filters.squadron, Filters.TIE).accepts(gameState, modifiersQuerying, cardToMove)) {
                    if (Filters.hasAvailableStarfighterOrTIECapacity(cardToMove).accepts(gameState, modifiersQuerying, physicalCard)) {
                        return true;
                    }
                }
                if (Filters.capital_starship.accepts(gameState, modifiersQuerying, cardToMove)) {
                    if (Filters.hasAvailableCapitalStarshipCapacity(cardToMove).accepts(gameState, modifiersQuerying, physicalCard)) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that the specified card can move to by moving to a related starship or vehicle site from
     * the related starship or vehicle.
     *
     * @param playerId the player to move the card
     * @param cardToMove the card to move
     * @return Filter
     */
    public static Filter canMoveToRelatedStarshipOrVehicleSite(final String playerId, PhysicalCard cardToMove) {
        final Integer permCardToMoveCardId = cardToMove.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard cardToMove = gameState.findCardByPermanentId(permCardToMoveCardId);

                PhysicalCard currentStarshipOrVehicle = cardToMove.getAttachedTo();
                if (currentStarshipOrVehicle == null
                        || (currentStarshipOrVehicle.getBlueprint().getCardCategory() != CardCategory.STARSHIP
                        && currentStarshipOrVehicle.getBlueprint().getCardCategory() != CardCategory.VEHICLE)
                        || (!cardToMove.isPilotOf() && !cardToMove.isPassengerOf()
                        && !cardToMove.isInCargoHoldAsVehicle() && !cardToMove.isInCargoHoldAsStarfighterOrTIE() && !cardToMove.isInCargoHoldAsCapitalStarship())) {
                    return false;
                }

                boolean validDestination = false;

                // 1) Check that the destination is a related starship/vehicle site (or a vehicle site of a vehicle aboard)
                if (Filters.siteOfStarshipOrVehicle(currentStarshipOrVehicle).accepts(gameState, modifiersQuerying, physicalCard)) {
                    validDestination = true;
                }
                else if (currentStarshipOrVehicle.getBlueprint().getCardCategory() == CardCategory.STARSHIP) {
                    Collection<PhysicalCard> vehiclesAboard = Filters.filterAllOnTable(gameState.getGame(), Filters.and(Filters.vehicle, Filters.aboardExceptRelatedSites(currentStarshipOrVehicle)));
                    for (PhysicalCard vehicleAboard : vehiclesAboard) {
                        if (Filters.siteOfStarshipOrVehicle(vehicleAboard).accepts(gameState, modifiersQuerying, physicalCard)) {
                            validDestination = true;
                            break;
                        }
                    }
                }

                if (!validDestination)
                    return false;

                // 2) Check that card is allowed to move from starship/vehicle to starship/vehicle site
                if (modifiersQuerying.mayNotMoveFromStarshipOrVehicleToRelatedStarshipOrVehicleSite(gameState, cardToMove, currentStarshipOrVehicle, physicalCard)) {
                    return false;
                }

                return true;
            }
        };
    }

    /**
     * Filter that accepts sites that the specified card can enter the starship or vehicle site from
     * the site the related starship or vehicle card is present at.
     *
     * @param playerId the player to move the card
     * @param cardToMove the card to move
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @param forFree true if the movement is to be free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required to perform the movement
     * @return Filter
     */
    public static Filter canEnterStarshipOrVehicleSite(final String playerId, PhysicalCard cardToMove, final boolean asReact, final boolean forFree, final float changeInCost) {
        final Integer permCardToMoveCardId = cardToMove.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard cardToMove = gameState.findCardByPermanentId(permCardToMoveCardId);
                PhysicalCard currentAtLocation = cardToMove.getAtLocation();

                // If moving a react, card can disembark first
                // (unless during the move as 'react', in which case the card must disembark first before moving to another location)
                if (asReact && cardToMove.getAttachedTo() != null && !gameState.isDuringMoveAsReact()) {
                    currentAtLocation = cardToMove.getAttachedTo().getAtLocation();
                }

                if (currentAtLocation == null || Filters.sameCardId(currentAtLocation).accepts(gameState, modifiersQuerying, physicalCard)
                        || currentAtLocation.getBlueprint().getCardSubtype() != CardSubtype.SITE
                        || (!physicalCard.getBlueprint().hasIcon(Icon.STARSHIP_SITE) && !physicalCard.getBlueprint().hasIcon(Icon.VEHICLE_SITE))) {
                    return false;
                }

                boolean validDestination = false;

                // 1) Check that the destination is a starship/vehicle site of a starship/vehicle present at the current site
                Collection<PhysicalCard> starshipsOrVehicles = Filters.filterAllOnTable(gameState.getGame(),
                        Filters.and(Filters.or(Filters.starship, Filters.vehicle), Filters.present(currentAtLocation)));
                for (PhysicalCard starshipOrVehicle : starshipsOrVehicles) {
                    if (Filters.siteOfStarshipOrVehicle(starshipOrVehicle).accepts(gameState, modifiersQuerying, physicalCard)) {
                        validDestination = true;
                        break;
                    }
                }

                if (!validDestination)
                    return false;

                // 2) Check that card is allowed to enter starship/vehicle site from site
                if (modifiersQuerying.mayNotEnterStarshipOrVehicleSite(gameState, cardToMove, currentAtLocation, physicalCard, asReact)) {
                    return false;
                }

                // 3) Check that there is enough Force available to use for this move
                if (!forFree) {
                    if (modifiersQuerying.getForceAvailableToUse(gameState, playerId)
                            < modifiersQuerying.getEnterStarshipOrVehicleSiteCost(gameState, cardToMove, currentAtLocation, physicalCard, asReact, changeInCost)) {
                        return false;
                    }
                }

                return true;
            }
        };
    }

    /**
     * Filter that accepts sites that the specified card can exit the starship or vehicle site to
     * the site the related starship or vehicle card is present at.
     *
     * @param playerId the player to move the card
     * @param cardToMove the card to move
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @param forFree true if the movement is to be free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required to perform the movement
     * @return Filter
     */
    public static Filter canExitStarshipOrVehicleSite(final String playerId, PhysicalCard cardToMove, final boolean asReact, final boolean forFree, final float changeInCost) {
        final Integer permCardToMoveCardId = cardToMove.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard cardToMove = gameState.findCardByPermanentId(permCardToMoveCardId);
                PhysicalCard currentAtLocation = cardToMove.getAtLocation();

                // If moving a react, card can disembark first
                // (unless during the move as 'react', in which case the card must disembark first before moving to another location)
                if (asReact && cardToMove.getAttachedTo() != null && !gameState.isDuringMoveAsReact()) {
                    currentAtLocation = cardToMove.getAttachedTo().getAtLocation();
                }

                if (currentAtLocation == null || Filters.sameCardId(currentAtLocation).accepts(gameState, modifiersQuerying, physicalCard)
                        || (!currentAtLocation.getBlueprint().hasIcon(Icon.STARSHIP_SITE) && !currentAtLocation.getBlueprint().hasIcon(Icon.VEHICLE_SITE))
                        || physicalCard.getBlueprint().getCardSubtype() != CardSubtype.SITE) {
                    return false;
                }

                boolean validDestination = false;

                // 1) Check that the destination is the site that the starship/vehicle related to the current site is present at
                Collection<PhysicalCard> starshipsOrVehicles = Filters.filterAllOnTable(gameState.getGame(),
                        Filters.and(Filters.or(Filters.starship, Filters.vehicle), Filters.present(physicalCard)));
                for (PhysicalCard starshipOrVehicle : starshipsOrVehicles) {
                    if (Filters.siteOfStarshipOrVehicle(starshipOrVehicle).accepts(gameState, modifiersQuerying, currentAtLocation)) {
                        validDestination = true;
                        break;
                    }
                }

                if (!validDestination)
                    return false;

                // 2) Check that card is allowed to exit starship/vehicle site to site
                if (modifiersQuerying.mayNotExitStarshipOrVehicleSite(gameState, cardToMove, currentAtLocation, physicalCard, asReact)) {
                    return false;
                }

                // 3) Check that there is enough Force available to use for this move
                if (!forFree) {
                    if (modifiersQuerying.getForceAvailableToUse(gameState, playerId)
                            < modifiersQuerying.getExitStarshipOrVehicleSiteCost(gameState, cardToMove, currentAtLocation, physicalCard, asReact, changeInCost)) {
                        return false;
                    }
                }

                return true;
            }
        };
    }

    /**
     * Filter that accepts cards that the specified card can shipdock with.
     *
     * @param starship a starship
     * @param forFree true if the movement is to be free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required to perform the movement
     * @return Filter
     */
    public static Filter canShipdockWith(PhysicalCard starship, final boolean forFree, final float changeInCost) {
        final Integer permStarshipCardId = starship.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard starship = gameState.findCardByPermanentId(permStarshipCardId);
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.STARSHIP
                        || !starship.getOwner().equals(physicalCard.getOwner()))
                    return false;

                // 1) Check that cards are present with each other at a system or sector
                if (starship.getAtLocation() == null
                        || !Filters.system_or_sector.accepts(gameState, modifiersQuerying, starship.getAtLocation())
                        || !modifiersQuerying.isPresentWith(gameState, starship, physicalCard)) {
                    return false;
                }

                // 2) Check that at least one of the two cards has ship-docking capability and at least one of them is piloted
                if (starship.getBlueprint().getCardSubtype() != CardSubtype.CAPITAL && !modifiersQuerying.hasKeyword(gameState, starship, Keyword.SHIP_DOCKING_CAPABILITY)
                        && physicalCard.getBlueprint().getCardSubtype() != CardSubtype.CAPITAL && !modifiersQuerying.hasKeyword(gameState, physicalCard, Keyword.SHIP_DOCKING_CAPABILITY)) {
                    return false;
                }

                if (!modifiersQuerying.isPiloted(gameState, starship, false) && !modifiersQuerying.isPiloted(gameState, physicalCard, false)) {
                    return false;
                }

                // 3) Check that the other starship can move
                if (modifiersQuerying.mayNotMove(gameState, physicalCard)) {
                    return false;
                }

                // 4) Check that there is enough Force available to use for this move
                if (!forFree) {
                    if (modifiersQuerying.getForceAvailableToUse(gameState, starship.getOwner())
                            < modifiersQuerying.getShipdockingCost(gameState, starship, physicalCard, changeInCost)) {
                        return false;
                    }
                }

                return true;
            }
        };
    }

    /**
     * Filter that accepts cards that the specified card can move to using location text.
     *
     * @param cardToMove the card to move
     * @param forFree true if the movement is to be free, otherwise false
     * @param baseCost base cost in amount of Force required to perform the movement
     * @param changeInCost change in amount of Force (can be positive or negative) required to perform the movement
     * @return Filter
     */
    public static Filter canMoveToUsingLocationText(PhysicalCard cardToMove, final boolean forFree, final float baseCost, final float changeInCost) {
        final Integer permCardToMoveCardId = cardToMove.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard cardToMove = gameState.findCardByPermanentId(permCardToMoveCardId);

                // 1) Check that destination is a location (or player's starship or vehicle)
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION
                        && (!cardToMove.getOwner().equals(physicalCard.getOwner())
                        || (physicalCard.getBlueprint().getCardCategory() != CardCategory.STARSHIP
                        && physicalCard.getBlueprint().getCardCategory() != CardCategory.VEHICLE))) {
                    return false;
                }

                // 2) Check that the card can move
                if (modifiersQuerying.mayNotMove(gameState, cardToMove)) {
                    return false;
                }

                // 3) Check destination is valid for card to move to
                if (!cardToMove.getBlueprint().getValidMoveTargetFilter(cardToMove.getOwner(), gameState.getGame(), cardToMove, false).accepts(gameState, modifiersQuerying, physicalCard)) {
                    return false;
                }

                PhysicalCard currentLocation = modifiersQuerying.getLocationHere(gameState, cardToMove);
                PhysicalCard destinationLocation = modifiersQuerying.getLocationHere(gameState, physicalCard);
                if (currentLocation == null || destinationLocation == null || Filters.sameCardId(currentLocation).accepts(gameState, modifiersQuerying, destinationLocation)) {
                    return false;
                }

                // 4) Check that card is allowed to move from location to location
                if (modifiersQuerying.mayNotMoveFromLocationToLocationUsingLocationText(gameState, cardToMove, currentLocation, destinationLocation)) {
                    return false;
                }

                // 5) Check that starship/vehicle has sufficent capacity
                if (physicalCard.getBlueprint().getCardCategory() == CardCategory.STARSHIP
                        || physicalCard.getBlueprint().getCardCategory() == CardCategory.VEHICLE) {

                    boolean validDestination = false;

                    if (cardToMove.getBlueprint().getCardCategory() == CardCategory.CHARACTER || cardToMove.getBlueprint().isMovesLikeCharacter()) {
                        Filter filter = Filters.or(Filters.hasAvailablePilotCapacity(cardToMove), Filters.hasAvailablePassengerCapacity(cardToMove));
                        if (Filters.and(filter).accepts(gameState, modifiersQuerying, physicalCard)) {
                            validDestination = true;
                        }
                    }
                    if (cardToMove.getBlueprint().getCardCategory() == CardCategory.VEHICLE || cardToMove.getBlueprint().isVehicleSlotOfStarshipCompatible()) {
                        if (Filters.hasAvailableVehicleCapacity(cardToMove).accepts(gameState, modifiersQuerying, physicalCard)) {
                            validDestination = true;
                        }
                    }
                    if (Filters.or(Filters.starfighter, Filters.squadron, Filters.TIE).accepts(gameState, modifiersQuerying, cardToMove)) {
                        if (Filters.hasAvailableStarfighterOrTIECapacity(cardToMove).accepts(gameState, modifiersQuerying, physicalCard)) {
                            validDestination = true;
                        }
                    }
                    if (Filters.capital_starship.accepts(gameState, modifiersQuerying, cardToMove)) {
                        if (Filters.hasAvailableCapitalStarshipCapacity(cardToMove).accepts(gameState, modifiersQuerying, physicalCard)) {
                            validDestination = true;
                        }
                    }

                    if (!validDestination) {
                        return false;
                    }
                }

                // 6) Check that there is enough Force available to use for this move
                if (!forFree) {
                    if (modifiersQuerying.getForceAvailableToUse(gameState, cardToMove.getOwner())
                            < modifiersQuerying.getMoveUsingLocationTextCost(gameState, cardToMove, currentLocation, destinationLocation, baseCost, changeInCost)) {
                        return false;
                    }
                }

                return true;
            }
        };
    }

    /**
     * Filter that accepts docking bays that the specified card can move to using docking bay transit.
     *
     * @param cardToMove the card to move
     * @param forFree true if the movement is to be free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required to perform the movement
     * @return Filter
     */
    public static Filter canMoveToUsingDockingBayTransit(PhysicalCard cardToMove, final boolean forFree, final float changeInCost) {
        final Integer permCardToMoveCardId = cardToMove.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard cardToMove = gameState.findCardByPermanentId(permCardToMoveCardId);

                PhysicalCard currentDockingBay = cardToMove.getAtLocation();
                if (currentDockingBay == null || !Filters.docking_bay.accepts(gameState, modifiersQuerying, currentDockingBay)) {
                    return false;
                }

                // 1) Check that destination is another docking bay
                if (Filters.sameCardId(physicalCard).accepts(gameState, modifiersQuerying, currentDockingBay)
                        || !Filters.docking_bay.accepts(gameState, modifiersQuerying, physicalCard)) {
                    return false;
                }

                // 2) Check that the card can move
                if (modifiersQuerying.mayNotMove(gameState, cardToMove)) {
                    return false;
                }

                // 3) Check destination is valid for card to move to
                if (!cardToMove.getBlueprint().getValidMoveTargetFilter(cardToMove.getOwner(), gameState.getGame(), cardToMove, false).accepts(gameState, modifiersQuerying, physicalCard)) {
                    return false;
                }

                // 4) Check that card is allowed to move from location to location
                if (modifiersQuerying.mayNotMoveFromLocationToLocationUsingDockingBayTransit(gameState, cardToMove, currentDockingBay, physicalCard)) {
                    return false;
                }

                // 5) Check that there is enough Force available to use for this move
                if (!forFree) {
                    if (modifiersQuerying.getForceAvailableToUse(gameState, cardToMove.getOwner())
                            < modifiersQuerying.getDockingBayTransitCost(gameState, cardToMove, currentDockingBay, physicalCard, changeInCost)) {
                        return false;
                    }
                }

                return true;
            }
        };
    }

    /**
     * Filter that accepts cards that the specified card can move to by embarking.
     *
     * @param playerId the player to move the card
     * @param cardToMove the card to move
     * @param forFree true if the movement is to be free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required to perform the movement
     * @return Filter
     */
    public static Filter canEmbarkTo(final String playerId, PhysicalCard cardToMove, final boolean forFree, final float changeInCost) {
        final Integer permCardToMoveCardId = cardToMove.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard cardToMove = gameState.findCardByPermanentId(permCardToMoveCardId);

                PhysicalCard atLocation = cardToMove.getAtLocation();
                PhysicalCard attachedTo = cardToMove.getAttachedTo();
                if (atLocation == null && attachedTo == null)
                    return false;

                // 1) Check that destination is owner's card.
                if (!cardToMove.getOwner().equals(physicalCard.getOwner()))
                    return false;

                // 2) Check the destination is not attached to the card
                if (gameState.getAllAttachedRecursively(cardToMove).contains(physicalCard))
                    return false;

                boolean validDestination = false;

                // 3) If physically at a location, check if the destination is present with the card and has sufficent capacity
                // (or if destination is a related starship site to a starship present with that may be embarked to)
                if (atLocation != null) {

                    // Check that destination is capital starship at related system (or related starship site that may be shuttled to)
                    if (!modifiersQuerying.isPresentWith(gameState, cardToMove, physicalCard, true)) {
                        return false;
                    }

                    // Check that starship/vehicle has sufficent capacity
                    if (cardToMove.getBlueprint().getCardCategory() == CardCategory.CHARACTER || cardToMove.getBlueprint().isMovesLikeCharacter()) {
                        Filter filter = Filters.or(Filters.hasAvailablePilotCapacity(cardToMove), Filters.hasAvailablePassengerCapacity(cardToMove));
                        if (Filters.and(filter).accepts(gameState, modifiersQuerying, physicalCard)) {
                            validDestination = true;
                        }
                    }
                    if (cardToMove.getBlueprint().getCardCategory() == CardCategory.VEHICLE || cardToMove.getBlueprint().isVehicleSlotOfStarshipCompatible()) {
                        if (Filters.hasAvailableVehicleCapacity(cardToMove).accepts(gameState, modifiersQuerying, physicalCard)) {
                            validDestination = true;
                        }
                    }
                    if (Filters.or(Filters.starfighter, Filters.squadron, Filters.TIE).accepts(gameState, modifiersQuerying, cardToMove)) {
                        if (Filters.hasAvailableStarfighterOrTIECapacity(cardToMove).accepts(gameState, modifiersQuerying, physicalCard)) {
                            validDestination = true;
                        }
                    }
                    if (Filters.capital_starship.accepts(gameState, modifiersQuerying, cardToMove)) {
                        if (Filters.hasAvailableCapitalStarshipCapacity(cardToMove).accepts(gameState, modifiersQuerying, physicalCard)) {
                            validDestination = true;
                        }
                    }
                }

                // 4) Check if moving a character from the "bridge" of a starship/vehicle to a starship or vehicle in the cargo hold of a that starship/vehicle.
                if (attachedTo != null && (cardToMove.isPilotOf() || cardToMove.isPassengerOf())) {
                    // Check if destination is in the cargo hold of the starship/vehicle this card is on the bridge of
                    if (physicalCard.getAttachedTo() != null && Filters.sameCardId(physicalCard.getAttachedTo()).accepts(gameState, modifiersQuerying, attachedTo)
                            && (physicalCard.isInCargoHoldAsStarfighterOrTIE() || physicalCard.isInCargoHoldAsVehicle() || physicalCard.isInCargoHoldAsCapitalStarship())) {

                        // Check that starship/vehicle has sufficent capacity
                        if (cardToMove.getBlueprint().getCardCategory() == CardCategory.CHARACTER || cardToMove.getBlueprint().isMovesLikeCharacter()) {
                            Filter filter = Filters.or(Filters.hasAvailablePilotCapacity(cardToMove), Filters.hasAvailablePassengerCapacity(cardToMove));
                            if (Filters.and(filter).accepts(gameState, modifiersQuerying, physicalCard)) {
                                validDestination = true;
                            }
                        }
                    }
                }

                if (!validDestination)
                    return false;

                // 5) Check that card is allowed to move from location to location
                PhysicalCard currentLocation = modifiersQuerying.getLocationHere(gameState, cardToMove);
                PhysicalCard destinationLocation = modifiersQuerying.getLocationHere(gameState, physicalCard);
                if (!Filters.sameCardId(currentLocation).accepts(gameState, modifiersQuerying, destinationLocation)
                        && modifiersQuerying.mayNotEmbarkFromLocationToLocation(gameState, cardToMove, currentLocation, destinationLocation)) {
                    return false;
                }

                // 6) Check that there is enough force in force pile for this move
                if (!forFree && physicalCard.isCrashed()) {
                    if (modifiersQuerying.getForceAvailableToUse(gameState, playerId)
                            < modifiersQuerying.getEmbarkingCost(gameState, cardToMove, physicalCard, changeInCost))
                        return false;
                }

                return true;
            }
        };
    }

    /**
     * Filter that accepts cards that the specified card can move to by disembarking.
     *
     * @param playerId the player to move the card
     * @param cardToMove the card to move
     * @param forFree true if the movement is to be free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required to perform the movement
     * @return Filter
     */
    public static Filter canDisembarkTo(final String playerId, PhysicalCard cardToMove, final boolean forFree, final float changeInCost) {
        final Integer permCardToMoveCardId = cardToMove.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard cardToMove = gameState.findCardByPermanentId(permCardToMoveCardId);

                PhysicalCard atLocation = cardToMove.getAtLocation();
                PhysicalCard attachedTo = cardToMove.getAttachedTo();
                if (atLocation == null && attachedTo == null)
                    return false;

                // 1) Check that destination is owner's card or location.
                if (!cardToMove.getOwner().equals(physicalCard.getOwner()) && physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION)
                    return false;

                boolean validDestination = false;

                if (attachedTo != null) {

                    PhysicalCard attachedToIsPresentAtLoc = modifiersQuerying.getLocationThatCardIsPresentAt(gameState, attachedTo);
                    PhysicalCard attachedToIsAttachedTo = attachedTo.getAttachedTo();

                    // 3) If aboard a card at a location
                    if (attachedToIsPresentAtLoc != null) {
                        // Check if same location
                        if (!Filters.sameCardId(attachedToIsPresentAtLoc).accepts(gameState, modifiersQuerying, physicalCard)) {
                            return false;
                        }

                        // Check if destination for character or "moves like a character" is a site
                        if ((cardToMove.getBlueprint().getCardCategory() == CardCategory.CHARACTER || cardToMove.getBlueprint().isMovesLikeCharacter())
                                && attachedToIsPresentAtLoc.getBlueprint().getCardSubtype() == CardSubtype.SITE) {
                            validDestination = true;
                        }

                        // Check if card is a vehicle or starship
                        if (cardToMove.getBlueprint().getCardCategory() == CardCategory.VEHICLE
                                || cardToMove.getBlueprint().getCardCategory() == CardCategory.STARSHIP) {
                            validDestination = true;
                        }
                    }

                    // 4) Check if moving a character from the "bridge" of a starship or vehicle in a cargo hold to the "bridge" of the carrying starship/vehicle.
                    if (attachedToIsAttachedTo != null
                            && (cardToMove.isPilotOf() || cardToMove.isPassengerOf())
                            && (attachedTo.isInCargoHoldAsVehicle() || attachedTo.isInCargoHoldAsStarfighterOrTIE() || attachedTo.isInCargoHoldAsCapitalStarship())) {

                        // Check if same starship/vehicle
                        if (!Filters.sameCardId(attachedToIsAttachedTo).accepts(gameState, modifiersQuerying, physicalCard)) {
                            return false;
                        }

                        // Check that starship/vehicle has sufficent capacity
                        if (cardToMove.getBlueprint().getCardCategory() == CardCategory.CHARACTER || cardToMove.getBlueprint().isMovesLikeCharacter()) {
                            Filter filter = Filters.or(Filters.hasAvailablePilotCapacity(cardToMove), Filters.hasAvailablePassengerCapacity(cardToMove));
                            if (Filters.and(filter).accepts(gameState, modifiersQuerying, physicalCard)) {
                                validDestination = true;
                            }
                        }
                    }
                }

                if (!validDestination)
                    return false;

                // 5) Check that card is allowed to move from location to location
                PhysicalCard currentLocation = modifiersQuerying.getLocationHere(gameState, cardToMove);
                PhysicalCard destinationLocation = modifiersQuerying.getLocationHere(gameState, physicalCard);
                if (!Filters.sameCardId(currentLocation).accepts(gameState, modifiersQuerying, destinationLocation)
                        && modifiersQuerying.mayNotDisembarkFromLocationToLocation(gameState, cardToMove, currentLocation, destinationLocation)) {
                    return false;
                }

                // 6) Check that there is enough force in force pile for this move
                if (!forFree && attachedTo.isCrashed()) {
                    if (modifiersQuerying.getForceAvailableToUse(gameState, playerId)
                            < modifiersQuerying.getDisembarkingCost(gameState, cardToMove, physicalCard, changeInCost))
                        return false;
                }

                return true;
            }
        };
    }

    /**
     * Filter that accepts cards that can be relocated to a specified location.
     *
     * @param location the location
     * @param baseCost the base cost (as defined by the card performing the relocation)
     * @return Filter
     */
    public static Filter canBeRelocatedToLocation(PhysicalCard location, final float baseCost) {
        return canBeRelocatedToLocation(Filters.sameCardId(location), baseCost);
    }

    /**
     * Filter that accepts cards that can be relocated to a location accepted by the specified location filter.
     *
     * @param locationFilter the location filter
     * @param baseCost the base cost (as defined by the card performing the relocation)
     * @return Filter
     */
    public static Filter canBeRelocatedToLocation(final Filter locationFilter, final float baseCost) {
        return canBeRelocatedToLocation(locationFilter, false, false, false, baseCost, false);
    }

    /**
     * Filter that accepts cards that can be relocated to a location accepted by the specified location filter for free.
     *
     * @param location the location
     * @param forFree true if the movement is to be free, otherwise false
     * @param baseCost the base cost (as defined by the card performing the relocation)
     * @return Filter
     */
    public static Filter canBeRelocatedToLocation(final PhysicalCard location, final boolean forFree, final float baseCost) {
        return canBeRelocatedToLocation(Filters.sameCardId(location), false, false, forFree, baseCost, false);
    }

    /**
     * Filter that accepts cards that can be relocated to a location accepted by the specified location filter for free.
     *
     * @param locationFilter the location filter
     * @param forFree true if the movement is to be free, otherwise false
     * @param baseCost the base cost (as defined by the card performing the relocation)
     * @return Filter
     */
    public static Filter canBeRelocatedToLocation(final Filter locationFilter, final boolean forFree, final float baseCost) {
        return canBeRelocatedToLocation(locationFilter, false, false, forFree, baseCost, false);
    }

    /**
     * Filter that accepts cards that can be relocated to a location accepted by the specified location filter.
     *
     * @param location the location
     * @param allowDagobah true if relocating from/to Dagobah locations is allowed, otherwise false
     * @param allowEscort true if relocating captive escort is allowed, otherwise false
     * @param forFree true if the movement is to be free, otherwise false
     * @param baseCost the base cost (as defined by the card performing the relocation)
     * @return Filter
     */
    public static Filter canBeRelocatedToLocation(PhysicalCard location, final boolean allowDagobah, final boolean allowEscort, final boolean forFree, final float baseCost, final boolean allowAhchTo) {
        return canBeRelocatedToLocation(Filters.sameCardId(location), allowDagobah, allowEscort, forFree, baseCost, allowAhchTo);
    }

    /**
     * Filter that accepts cards that can be relocated to a location accepted by the specified location filter.
     *
     * @param locationFilter the location filter
     * @param allowDagobah true if relocating from/to Dagobah locations is allowed, otherwise false
     * @param allowEscort true if relocating captive escort is allowed, otherwise false
     * @param forFree true if the movement is to be free, otherwise false
     * @param baseCost the base cost (as defined by the card performing the relocation)
     * @param allowAhchTo true if relocating from/to Ahch-To locations is allowed, otherwise false
     * @return Filter
     */
    public static Filter canBeRelocatedToLocation(final Filter locationFilter, final boolean allowDagobah, final boolean allowEscort, final boolean forFree, final float baseCost, final boolean allowAhchTo) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.CHARACTER
                        && physicalCard.getBlueprint().getCardCategory() != CardCategory.VEHICLE
                        && physicalCard.getBlueprint().getCardCategory() != CardCategory.STARSHIP)
                    return false;

                // 1) Check if card is at a location
                PhysicalCard currentLocation = modifiersQuerying.getLocationThatCardIsAt(gameState, physicalCard);
                if (currentLocation == null)
                    return false;

                // 2) Check if card can move
                if (modifiersQuerying.mayNotMove(gameState, physicalCard)) {
                    return false;
                }

                // 3) Check if escorting a captive
                if (!allowEscort && Filters.escort.accepts(gameState, modifiersQuerying, physicalCard)) {
                    return false;
                }

                // Check locations accepted by filter
                Collection<PhysicalCard> otherLocations = Filters.filterTopLocationsOnTable(gameState.getGame(), Filters.and(Filters.not(Filters.sameCardId(currentLocation)), locationFilter));
                for (PhysicalCard otherLocation : otherLocations) {

                    // 4) Check destination is valid for card to be relocated to
                    if (physicalCard.getBlueprint().getValidMoveTargetFilter(physicalCard.getOwner(), gameState.getGame(), physicalCard, false).accepts(gameState, modifiersQuerying, otherLocation)) {
                        if (!modifiersQuerying.mayNotRelocateFromLocationToLocation(gameState, physicalCard, currentLocation, otherLocation, allowDagobah, allowAhchTo)) {

                            // 5) Check that there is enough Force available to use for this move
                            if (forFree
                                    || (modifiersQuerying.getForceAvailableToUse(gameState, physicalCard.getOwner())
                                    >= modifiersQuerying.getRelocateBetweenLocationsCost(gameState, physicalCard, currentLocation, physicalCard, baseCost))) {
                                return true;
                            }
                        }
                    }
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts locations the specified card can be relocated to.
     *
     * @param cardToMove the card to be relocated
     * @param baseCost the base cost (as defined by the card performing the relocation)
     * @return Filter
     */
    public static Filter locationCanBeRelocatedTo(PhysicalCard cardToMove, final float baseCost) {
        return locationCanBeRelocatedTo(cardToMove, false, false, false, baseCost, false);
    }

    /**
     * Filter that accepts locations the specified card can be relocated to.
     *
     * @param cardToMove the card to be relocated
     * @param forFree true if the movement is to be free, otherwise false
     * @param baseCost the base cost (as defined by the card performing the relocation)
     * @return Filter
     */
    public static Filter locationCanBeRelocatedTo(PhysicalCard cardToMove, final boolean forFree, final float baseCost) {
        return locationCanBeRelocatedTo(cardToMove, false, false, forFree, baseCost, false);
    }

    /**
     * Filter that accepts locations the specified card can be relocated to.
     *
     * @param cardToMove the card to be relocated
     * @param allowDagobah true if relocating from/to Dagobah locations is allowed, otherwise false
     * @param allowEscort true if relocating captive escort is allowed, otherwise false
     * @param forFree true if the movement is to be free, otherwise false
     * @param baseCost the base cost (as defined by the card performing the relocation)
     * @param allowAhchTo true if relocating from/to Ahch-To locations is allowed, otherwise false
     * @return Filter
     */
    public static Filter locationCanBeRelocatedTo(PhysicalCard cardToMove, final boolean allowDagobah, final boolean allowEscort, final boolean forFree, final float baseCost, final boolean allowAhchTo) {
        final Integer permCardToMoveCardId = cardToMove.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard cardToMove = gameState.findCardByPermanentId(permCardToMoveCardId);

                if ((cardToMove.getBlueprint().getCardCategory() != CardCategory.CHARACTER
                        && cardToMove.getBlueprint().getCardCategory() != CardCategory.VEHICLE
                        && cardToMove.getBlueprint().getCardCategory() != CardCategory.STARSHIP)
                        || physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION)
                    return false;

                // 1) Check if card is at a location (and the destination is a different location) or on Weather Vane
                PhysicalCard currentLocation = modifiersQuerying.getLocationThatCardIsAt(gameState, cardToMove);
                boolean isOnWeatherVane = cardToMove.getStackedOn() != null && Filters.Weather_Vane.accepts(gameState, modifiersQuerying, cardToMove.getStackedOn());
                if (!isOnWeatherVane && (currentLocation == null || Filters.sameCardId(physicalCard).accepts(gameState, modifiersQuerying, currentLocation)))
                    return false;

                // 2) Check if card can move
                if (modifiersQuerying.mayNotMove(gameState, cardToMove)) {
                    return false;
                }

                // 3) Check if escorting a captive
                if (!allowEscort && Filters.escort.accepts(gameState, modifiersQuerying, cardToMove)) {
                    return false;
                }

                // 4) Check destination is valid for card to be relocated to
                if (cardToMove.getBlueprint().getValidMoveTargetFilter(cardToMove.getOwner(), gameState.getGame(), cardToMove, false).accepts(gameState, modifiersQuerying, physicalCard)) {
                    if (isOnWeatherVane || !modifiersQuerying.mayNotRelocateFromLocationToLocation(gameState, cardToMove, currentLocation, physicalCard, allowDagobah, allowAhchTo)) {

                        // 5) Check that there is enough Force available to use for this move
                        if (forFree
                                || isOnWeatherVane
                                || (modifiersQuerying.getForceAvailableToUse(gameState, cardToMove.getOwner())
                                >= modifiersQuerying.getRelocateBetweenLocationsCost(gameState, cardToMove, currentLocation, physicalCard, baseCost))) {
                            return true;
                        }
                    }
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts locations that the specified card can be placed at.
     * @param cardToPlace the card to place
     * @return Filter
     */
    public static Filter locationCanBePlacedAt(PhysicalCard cardToPlace) {
        final Integer permCardToPlaceCardId = cardToPlace.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard cardToPlace = gameState.findCardByPermanentId(permCardToPlaceCardId);
                if (!Filters.or(Filters.character, Filters.device, Filters.starship, Filters.weapon).accepts(gameState, modifiersQuerying, cardToPlace))
                    return false;

                if (modifiersQuerying.isUniquenessOnTableLimitReached(gameState, cardToPlace))
                    return false;

                return Filters.and(Filters.location, cardToPlace.getBlueprint().getValidPlaceCardTargetFilter(gameState.getGame(), cardToPlace)).accepts(gameState, modifiersQuerying, physicalCard);
            }
        };
    }


    //
    //
    // Filters for capacities
    //
    //

    /**
     * Filter that accepts cards that have any available pilot or passenger capacity.
     */
    public static final Filter hasAnyAvailablePilotOrPassengerCapacity = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            if (physicalCard.getBlueprint().getCardCategory() != CardCategory.VEHICLE
                    && physicalCard.getBlueprint().getCardCategory() != CardCategory.STARSHIP)
                return false;

            return gameState.getAvailablePilotCapacity(modifiersQuerying, physicalCard, null) > 0
                    || gameState.getAvailablePassengerCapacity(modifiersQuerying, physicalCard, null) > 0
                    || gameState.getAvailablePassengerCapacityForAstromech(modifiersQuerying, physicalCard, null) > 0;
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter hasAnyAvailablePilotOrPassengerCapacity() {
        return hasAnyAvailablePilotOrPassengerCapacity;
    }

    /**
     * Filter that accepts cards that have available pilot capacity for the specified card.
     *
     * @param card a card
     * @return Filter
     */
    public static Filter hasAvailablePilotCapacity(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.VEHICLE
                        && physicalCard.getBlueprint().getCardCategory() != CardCategory.STARSHIP)
                    return false;

                PhysicalCard card = gameState.findCardByPermanentId(permCardId);

                if (card.isUndercover())
                    return false;

                if (card.getBlueprint().getCardCategory() != CardCategory.CHARACTER)
                    return false;

                if (!physicalCard.getBlueprint().getValidPilotFilter(physicalCard.getOwner(), gameState.getGame(), physicalCard, false).accepts(gameState, modifiersQuerying, card))
                    return false;

                return gameState.getAvailablePilotCapacity(modifiersQuerying, physicalCard, card) >= 1;
            }
        };
    }

    /**
     * Filter that accepts cards that have available passenger capacity for the specified card.
     *
     * @param card a card
     * @return Filter
     */
    public static Filter hasAvailablePassengerCapacity(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.DEVICE
                        && physicalCard.getBlueprint().getCardCategory() != CardCategory.VEHICLE
                        && physicalCard.getBlueprint().getCardCategory() != CardCategory.STARSHIP)
                    return false;

                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                if (card.isUndercover())
                    return false;

                if (card.getBlueprint().getCardCategory() != CardCategory.CHARACTER)
                    return false;

                if (Filters.astromech_droid.accepts(gameState, modifiersQuerying, card)) {
                    return gameState.getAvailablePassengerCapacityForAstromech(modifiersQuerying, physicalCard, card) >= 1;
                } else {
                    return gameState.getAvailablePassengerCapacity(modifiersQuerying, physicalCard, card) >= 1;
                }
            }
        };
    }

    /**
     * Filter that accepts cards that have available vehicle capacity for the specified card.
     *
     * @param card a card
     * @return Filter
     */
    public static Filter hasAvailableVehicleCapacity(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.STARSHIP
                        && physicalCard.getBlueprint().getCardCategory() != CardCategory.VEHICLE)
                    return false;

                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                if (card.getBlueprint().getCardCategory() != CardCategory.VEHICLE
                        && !card.getBlueprint().isVehicleSlotOfStarshipCompatible())
                    return false;

                Filter vehicleCapacityFilter = physicalCard.getBlueprint().getVehicleCapacityFilter();
                if (!vehicleCapacityFilter.accepts(gameState, modifiersQuerying, card))
                    return false;

                return gameState.getAvailableVehicleCapacity(physicalCard) >= 1;
            }
        };
    }

    /**
     * Filter that accepts cards that have available starfighter or TIE capacity for the specified card.
     *
     * @param card a card
     * @return Filter
     */
    public static Filter hasAvailableStarfighterOrTIECapacity(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.STARSHIP)
                    return false;

                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                if (!Filters.or(Filters.starfighter, Filters.squadron, Filters.TIE).accepts(gameState, modifiersQuerying, card))
                    return false;

                Filter starfighterOrTIECapacityFilter = physicalCard.getBlueprint().getStarfighterOrTIECapacityFilter();
                if (!starfighterOrTIECapacityFilter.accepts(gameState, modifiersQuerying, card))
                    return false;

                int slotsRequired = starfighterOrTIECapacityFilter.acceptsCount(gameState, modifiersQuerying, card);

                return gameState.getAvailableStarfighterOrTIECapacity(physicalCard, card) >= slotsRequired;
            }
        };
    }

    /**
     * Filter that accepts cards that have available capital starship capacity for the specified card.
     *
     * @param card a card
     * @return Filter
     */
    public static Filter hasAvailableCapitalStarshipCapacity(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.STARSHIP)
                    return false;

                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                if (!Filters.capital_starship.accepts(gameState, modifiersQuerying, card))
                    return false;

                Filter capitalStarshipCapacityFilter = physicalCard.getBlueprint().getCapitalStarshipCapacityFilter();
                if (!capitalStarshipCapacityFilter.accepts(gameState, modifiersQuerying, card))
                    return false;

                int slotsRequired = capitalStarshipCapacityFilter.acceptsCount(gameState, modifiersQuerying, card);

                return gameState.getAvailableCapitalStarshipCapacity(physicalCard, card) >= slotsRequired;
            }
        };
    }

    //
    //
    // Filters for alone
    //
    //

    /**
     * Filter that accepts cards that are alone.
     */
    public static final Filter alone = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.isAlone(gameState, physicalCard);
        }
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
            PhysicalCard card = builtInCardBlueprint.getPhysicalCard(gameState.getGame());
            List<SwccgBuiltInCardBlueprint> permanentsAboard = modifiersQuerying.getPermanentsAboard(gameState, card);

            if (permanentsAboard.size() > 1) {
                return false;
            }

            PhysicalCard location = modifiersQuerying.getLocationThatCardIsAt(gameState, card);
            if (location == null)
                return false;

            if (Filters.canSpot(gameState.getGame(), null, Filters.and(Filters.not(Filters.sameCardId(card)), Filters.at(location),
                    Filters.owner(card.getOwner()), Filters.or(Filters.character, Filters.abilityMoreThan(0, true)))))
                return false;

            return true;
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter alone() {
        return alone;
    }

    /**
     * Filter that accepts cards that have permanent pilots that are alone.
     */
    public static final Filter permanentPilotAlone = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.hasPermanentPilotAlone(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter permanentPilotAlone() {
        return permanentPilotAlone;
    }

    /**
     * Filter that accepts cards that are characters that are alone or that have permanent pilots that are alone.
     */
    public static final Filter characterOrPermanentPilotAlone = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.isCharacterAlone(gameState, physicalCard)
                    || modifiersQuerying.hasPermanentPilotAlone(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter characterOrPermanentPilotAlone() {
        return characterOrPermanentPilotAlone;
    }

    //
    //
    // Filters for matching pairs
    //
    //

    /**
     * Filter that accepts cards that are matching characters to the specified weapon.
     *
     * @param card a weapon
     * @return Filter
     */
    public static Filter matchingCharacter(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.CHARACTER)
                    return false;

                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return modifiersQuerying.isMatchingPair(gameState, physicalCard, card);
            }
        };
    }

    /**
     * Filter that accepts cards that are matching pilots to the specified starship or vehicle.
     *
     * @param card a starship or vehicle
     * @return Filter
     */
    public static Filter matchingPilot(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (!Filters.pilot.accepts(gameState, modifiersQuerying, physicalCard))
                    return false;

                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return modifiersQuerying.isMatchingPair(gameState, physicalCard, card);
            }
        };
    }

    /**
     * Filter that accepts cards that are matching starships to the specified character.
     *
     * @param card a character
     * @return Filter
     */
    public static Filter matchingStarship(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.STARSHIP)
                    return false;

                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return modifiersQuerying.isMatchingPair(gameState, card, physicalCard);
            }
        };
    }

    /**
     * Filter that accepts cards that are matching vehicles to the specified character.
     *
     * @param card a character
     * @return Filter
     */
    public static Filter matchingVehicle(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.VEHICLE)
                    return false;

                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return modifiersQuerying.isMatchingPair(gameState, card, physicalCard);
            }
        };
    }

    /**
     * Filter that accepts cards that are matching weapons to the specified character.
     *
     * @param card a character
     * @return Filter
     */
    public static Filter matchingWeaponForCharacter(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.WEAPON)
                    return false;

                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return modifiersQuerying.isMatchingPair(gameState, card, physicalCard);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return builtInCardBlueprint.isWeapon()
                        && Filters.sameCardId(card).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    /**
     * Filter that accepts cards that are starships or vehicles that have a matching pilot aboard.
     *
     * @param source the card that is performing this query
     * @return Filter
     */
    public static Filter hasMatchingPilotAboard(PhysicalCard source) {
        final Integer permSourceCardId = source != null ? source.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.STARSHIP
                        && physicalCard.getBlueprint().getCardCategory() != CardCategory.VEHICLE)
                    return false;

                if (modifiersQuerying.hasPermanentPilot(gameState, physicalCard))
                    return true;

                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);
                return hasAboard(source, Filters.matchingPilot(physicalCard)).accepts(gameState, modifiersQuerying, physicalCard);
            }
        };
    }

    /**
     * Filter that accepts cards that are operatives on their matching planet.
     */
    public static final Filter operativeOnMatchingPlanet = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            if (!Filters.operative.accepts(gameState, modifiersQuerying, physicalCard))
                return false;

            return Filters.on(physicalCard.getBlueprint().getMatchingSystem()).accepts(gameState, modifiersQuerying, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter operativeOnMatchingPlanet() {
        return operativeOnMatchingPlanet;
    }

    /**
     * Filter that accepts cards that are matching operatives to the Renegade planet.
     */
    public static final Filter matchingOperativeToRenegadePlanet = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            if (!Filters.operative.accepts(gameState, modifiersQuerying, physicalCard))
                return false;

            return physicalCard.getBlueprint().getMatchingSystem().equals(gameState.getRenegadePlanet());
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter matchingOperativeToRenegadePlanet() {
        return matchingOperativeToRenegadePlanet;
    }

    /**
     * Filter that accepts cards that are matching operatives to the Subjugated planet.
     */
    public static final Filter matchingOperativeToSubjugatedPlanet = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            if (!Filters.operative.accepts(gameState, modifiersQuerying, physicalCard))
                return false;

            return physicalCard.getBlueprint().getMatchingSystem().equals(gameState.getSubjugatedPlanet());
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter matchingOperativeToSubjugatedPlanet() {
        return matchingOperativeToSubjugatedPlanet;
    }


    //
    //
    // Filters for parsec and orbit
    //
    //

    /**
     * Filter that accepts cards that are planet systems that deploy in a specific range (inclusive) of parsec numbers.
     *
     * @param minParsec the lower limit of the parsec range
     * @param maxParsec the upper limit of the parsec range
     * @return Filter
     */
    public static Filter planetSystemInParsecRange(final int minParsec, final int maxParsec) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (!Filters.planet_system.accepts(gameState, modifiersQuerying, physicalCard))
                    return false;

                return physicalCard.getBlueprint().getParsec() >= minParsec
                        && physicalCard.getBlueprint().getParsec() <= maxParsec;
            }
        };
    }

    /**
     * Filter that accepts cards that systems at the specified parsec number.
     *
     * @param parsec the parsec number
     * @return Filter
     */
    public static Filter systemAtParsec(final int parsec) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardSubtype() != CardSubtype.SYSTEM)
                    return false;

                return physicalCard.getParsec() == parsec;
            }
        };
    }

    /**
     * Filter that accepts cards that are the systems orbited by the specified mobile system.
     *
     * @param card the mobile system
     * @return Filter
     */
    public static Filter isOrbitedBy(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (!Filters.system.accepts(gameState, modifiersQuerying, physicalCard))
                    return false;

                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return card.getSystemOrbited() != null
                        && Filters.title(card.getSystemOrbited()).accepts(gameState, modifiersQuerying, physicalCard);
            }
        };
    }

    /**
     * Filter that accepts cards that are the systems orbited by a mobile system accepted by the filter.
     *
     * @param filter the filter
     * @return Filter
     */
    public static Filter isOrbitedBy(final Filter filter) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (!Filters.system.accepts(gameState, modifiersQuerying, physicalCard))
                    return false;

                Collection<PhysicalCard> cards = Filters.filterTopLocationsOnTable(gameState.getGame(), filter);

                for (PhysicalCard card : cards) {
                    if (card.getSystemOrbited() != null
                            && Filters.title(card.getSystemOrbited()).accepts(gameState, modifiersQuerying, physicalCard)) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that are the mobile systems or asteroid sectors (and Big One: Asteroid Cave) orbiting the
     * planet system with the specified title.
     *
     * @param title the planet system title
     * @return Filter
     */
    public static Filter isOrbiting(final String title) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (!Filters.or(Filters.mobile_system, Filters.asteroid_sector, Filters.Big_One_Asteroid_Cave_Or_Space_Slug_Belly).accepts(gameState, modifiersQuerying, physicalCard))
                    return false;

                return physicalCard.getSystemOrbited() != null && title.equals(physicalCard.getSystemOrbited());
            }
        };
    }

    /**
     * Filter that accepts cards that are within (inclusive) a specified number of parsecs of the source card.
     *
     * @param card the card to be within specified number of parsecs of
     * @param numParsecs the number of parsecs from to be within range of source card
     * @return Filter
     */
    public static Filter withinParsecsOf(PhysicalCard card, final int numParsecs) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);

                Integer parsecNumber = null;
                PhysicalCard location = modifiersQuerying.getLocationHere(gameState, card);
                if (location != null) {
                    if (Filters.system.accepts(gameState, modifiersQuerying, location)) {
                        parsecNumber = location.getParsec();
                    }
                }

                if (parsecNumber == null)
                    return false;

                PhysicalCard curLocation = modifiersQuerying.getLocationHere(gameState, physicalCard);
                if (Filters.system.accepts(gameState, modifiersQuerying, curLocation)) {
                    return (Math.abs(parsecNumber - curLocation.getParsec()) <= numParsecs);
                }

                return false;
            }
        };
    }

    //
    //
    // Filters for converting characters
    //
    //

    /**
     * Filter that accepts characters that may be converted (replaced) by opponent.
     */
    public static final Filter mayBeReplacedByOpponent = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.mayBeReplacedByOpponent(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter mayBeReplacedByOpponent() {
        return mayBeReplacedByOpponent;
    }

    //
    //
    // Filters for converted locations
    //
    //

    /**
     * Filter that accepts cards that are the current top locations that can be converted by raising a converted location
     * to the top.
     *
     * @param playerId the player
     * @return Filter
     */
    public static Filter canBeConvertedByRaisingLocationToTop(final String playerId) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION)
                    return false;

                if (physicalCard.getZone() != Zone.LOCATIONS)
                    return false;

                if (physicalCard.isBlownAway())
                    return false;

                if (modifiersQuerying.cannotBeConverted(gameState, physicalCard))
                    return false;

                List<PhysicalCard> convertedLocations = gameState.getConvertedLocationsUnderTopLocation(physicalCard);
                for (PhysicalCard convertedLocation : convertedLocations) {
                    if (!convertedLocation.getOwner().equals(physicalCard.getOwner())) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that are the current top locations that can be converted by the specified player by raising
     * a converted location to the top.
     *
     * @param playerId the player
     * @return Filter
     */
    public static Filter canBeConvertedByRaisingYourLocationToTop(final String playerId) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION)
                    return false;

                if (physicalCard.getZone() != Zone.LOCATIONS)
                    return false;

                if (physicalCard.getOwner().equals(playerId))
                    return false;

                if (physicalCard.isBlownAway())
                    return false;

                if (modifiersQuerying.cannotBeConverted(gameState, physicalCard))
                    return false;

                List<PhysicalCard> convertedLocations = gameState.getConvertedLocationsUnderTopLocation(physicalCard);
                for (PhysicalCard convertedLocation : convertedLocations) {
                    if (convertedLocation.getOwner().equals(playerId)) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that can be converted by the specified player deploying another card.
     *
     * @param playerId the player to deploy a card
     * @return Filter
     */
    public static Filter canBeConvertedByDeployment(final String playerId) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getOwner().equals(playerId))
                    return false;

                if (modifiersQuerying.cannotBeConverted(gameState, physicalCard))
                    return false;

                if (physicalCard.getBlueprint().getCardCategory() == CardCategory.LOCATION) {

                    if (physicalCard.getZone() != Zone.LOCATIONS)
                        return false;

                    if (physicalCard.isBlownAway() || physicalCard.isCollapsed())
                        return false;

                    return true;
                }

                if (modifiersQuerying.canBeConvertedByDeployment(gameState, physicalCard, playerId))
                    return true;

                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that are the current top locations that can be converted by deploying the specified location.
     *
     * @param locationToConvertWith the location
     * @return Filter
     */
    public static Filter canBeConvertedByDeployment(PhysicalCard locationToConvertWith) {
        final Integer permLocationToConvertWithCardId = locationToConvertWith.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard locationToConvertWith = gameState.findCardByPermanentId(permLocationToConvertWithCardId);
                Uniqueness uniqueness = modifiersQuerying.getUniqueness(gameState, locationToConvertWith);

                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION)
                    return false;

                if (physicalCard.getZone() != Zone.LOCATIONS)
                    return false;

                if (physicalCard.isBlownAway() || physicalCard.isCollapsed())
                    return false;

                if (physicalCard.getOwner().equals(locationToConvertWith.getOwner()))
                    return false;

                if (uniqueness != modifiersQuerying.getUniqueness(gameState, physicalCard))
                    return false;

                if ((!Filters.holosite.accepts(gameState, modifiersQuerying, locationToConvertWith) || !Filters.holosite.accepts(gameState, modifiersQuerying, physicalCard))
                        && (!Filters.Big_One_Asteroid_Cave_Or_Space_Slug_Belly.accepts(gameState, modifiersQuerying, locationToConvertWith) || !Filters.Big_One_Asteroid_Cave_Or_Space_Slug_Belly.accepts(gameState, modifiersQuerying, physicalCard))
                        && !Filters.sameTitleAs(locationToConvertWith).accepts(gameState, modifiersQuerying, physicalCard))
                    return false;

                if (modifiersQuerying.cannotBeConverted(gameState, physicalCard))
                    return false;

                return true;
            }
        };
    }

    /**
     * Filter that accepts cards that are the current top collapsed locations that can be rebuilt by deploying the specified location.
     *
     * @param locationToRebuildWith the location
     * @return Filter
     */
    public static Filter canBeRebuiltByDeployment(PhysicalCard locationToRebuildWith) {
        final Integer permLocationToRebuildWithCardId = locationToRebuildWith.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard locationToRebuildWith = gameState.findCardByPermanentId(permLocationToRebuildWithCardId);

                Uniqueness uniqueness = modifiersQuerying.getUniqueness(gameState, locationToRebuildWith);

                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION)
                    return false;

                if (physicalCard.getZone() != Zone.LOCATIONS)
                    return false;

                if (!physicalCard.isCollapsed())
                    return false;

                if (uniqueness != modifiersQuerying.getUniqueness(gameState, physicalCard))
                    return false;

                if ((!Filters.holosite.accepts(gameState, modifiersQuerying, locationToRebuildWith) || !Filters.holosite.accepts(gameState, modifiersQuerying, physicalCard))
                        && (!Filters.Big_One_Asteroid_Cave_Or_Space_Slug_Belly.accepts(gameState, modifiersQuerying, locationToRebuildWith) || !Filters.Big_One_Asteroid_Cave_Or_Space_Slug_Belly.accepts(gameState, modifiersQuerying, physicalCard))
                        && !Filters.sameTitleAs(locationToRebuildWith).accepts(gameState, modifiersQuerying, physicalCard))
                    return false;

                return true;
            }
        };
    }

    /**
     * Filter that accepts cards that are the converted locations under a top location accepted by the specified filter.
     *
     * @param filters the filters
     * @return Filter
     */
    public static Filter convertedLocationUnderTopLocation(final Filter filters) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard topLocation = gameState.getLocationAtTopOfConvertedLocation(physicalCard);
                return (topLocation != null
                        && Filters.and(filters).accepts(gameState, modifiersQuerying, topLocation));
            }
        };
    }

    /**
     * Filter that accepts cards that are converted locations on top of a location accepted by the specified filter.
     *
     * @param filters the filters
     * @return Filter
     */
    public static Filter convertedLocationOnTopOfLocation(final Filter filters) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                List<PhysicalCard> convertedLocations = gameState.getConvertedLocationsUnderTopLocation(physicalCard);
                return Filters.canSpot(convertedLocations, gameState.getGame(), filters);
            }
        };
    }

    //
    //
    // Filters for stacked cards
    //
    //

    /**
     * Filter that accepts cards that are stacked on the specified card.
     *
     * @param card the card
     * @return Filter
     */
    public static Filter stackedOn(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return physicalCard.getStackedOn() != null
                        && Filters.sameCardId(physicalCard.getStackedOn()).accepts(gameState, modifiersQuerying, card);
            }
        };
    }

    /**
     * Filter that accepts cards that are stacked on a card accepted by the specified filter.
     *
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter stackedOn(PhysicalCard source, final Filter filters) {
        return stackedOn(source, null, filters);
    }

    /**
     * Filter that accepts cards that are stacked on a card accepted by the specified filter.
     *
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @param filters the filters
     * @return Filter
     */
    public static Filter stackedOn(PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final Filter filters) {
        final Integer permSourceCardId = source != null ? source.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);
                Collection<PhysicalCard> cards = Filters.filterActive(gameState.getGame(), source, spotOverrides, filters);

                for (PhysicalCard card : cards) {
                    if (Filters.stackedOn(card).accepts(gameState, modifiersQuerying, physicalCard))
                        return true;
                }

                return false;
            }
        };
    }


    /**
     * Filter that accepts cards that are stacked.
     */
    public static final Filter stacked = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.getZone() == Zone.STACKED || physicalCard.getZone() == Zone.STACKED_FACE_DOWN;
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter stacked() {
        return stacked;
    }

    /**
     * Filter that accepts cards that has the specified card stacked on it.
     *
     * @param card the card
     * @return Filter
     */
    public static Filter hasStacked(final PhysicalCard card) {
        return Filters.hasStacked(Filters.sameCardId(card));
    }

    /**
     * Filter that accepts cards that have cards accepted by the specified filter stacked on it.
     *
     * @param filter the filters
     * @return Filter
     */
    public static Filter hasStacked(final Filter filter) {
        return Filters.hasStacked(1, filter);
    }

    /**
     * Filter that accepts cards that have at least a specified number of cards accepted by the specified filter stacked on it.
     *
     * @param filter the filters
     * @return Filter
     */
    public static Filter hasStacked(final int count, final Filter filter) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return (Filters.filterCount(gameState.getStackedCards(physicalCard), gameState.getGame(), count, filter).size() >= count);
            }
        };
    }

    //
    //
    // Filters for Dejarik Rules
    //
    //

    /**
     * Filter that accepts cards that are dejariks/holograms at a holosite.
     */
    public static final Filter dejarikHologramAtHolosite = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.isDejarikHologramAtHolosite();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter dejarikHologramAtHolosite() {
        return dejarikHologramAtHolosite;
    }

    //
    //
    // Filters for playability
    //
    //

    /**
     * Filter that accepts cards that can be deployed.
     * @param sourceCard the card to initiate the action
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param forFree true if playing for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return Filter
     */
    public static Filter deployable(PhysicalCard sourceCard, final Filter specialLocationConditions, final boolean forFree, final float changeInCost) {
        return deployable(sourceCard, false, specialLocationConditions, forFree, changeInCost, null, null, null, null);
    }

    /**
     * Filter that accepts cards that can be deployed.
     * @param sourceCard the card to initiate the action
     * @param includePlayable true if includes playable cards, false if only deployable cards
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param forFree true if playing for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param changeInCostCardFilter the card filter for cards that are affected by the change in cost, or null for all
     * @param deploymentOption specifies special deployment options, or null
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @return Filter
     */
    public static Filter deployable(PhysicalCard sourceCard, final boolean includePlayable, final Filter specialLocationConditions, final boolean forFree, final float changeInCost, final Filter changeInCostCardFilter, final DeploymentOption deploymentOption, final DeploymentRestrictionsOption deploymentRestrictionsOption, final ReactActionOption reactActionOption) {
        final Integer permSourceCardId = sourceCard != null ? sourceCard.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);
                return modifiersQuerying.isDeployable(gameState, sourceCard != null ? sourceCard : physicalCard, physicalCard, includePlayable, specialLocationConditions, forFree, changeInCost, changeInCostCardFilter, deploymentOption, deploymentRestrictionsOption, reactActionOption, null, false, 0);
            }
        };
    }

    /**
     * Filter that accepts cards that can be deployed simultaneously with the specified card.
     * @param sourceCard the card to initiate the action
     * @param cardToDeployWith the card to deploy with simultaneously
     * @param cardToDeployWithForFree true if the card to deploy with deploys for free, otherwise false
     * @param cardToDeployWithChangeInCost change in amount of Force (can be positive or negative) required for the card to deploy with
     * @param forFree true if playing for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return Filter
     */
    public static Filter deployableSimultaneouslyWith(PhysicalCard sourceCard, PhysicalCard cardToDeployWith, final boolean cardToDeployWithForFree, final float cardToDeployWithChangeInCost, final boolean forFree, final float changeInCost) {
        return deployableSimultaneouslyWith(sourceCard, cardToDeployWith, cardToDeployWithForFree, cardToDeployWithChangeInCost, forFree, changeInCost, null, null);
    }

    /**
     * Filter that accepts cards that can be deployed simultaneously with the specified card.
     * @param sourceCard the card to initiate the action
     * @param cardToDeployWith the card to deploy with simultaneously
     * @param cardToDeployWithForFree true if the card to deploy with deploys for free, otherwise false
     * @param cardToDeployWithChangeInCost change in amount of Force (can be positive or negative) required for the card to deploy with
     * @param forFree true if playing for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param deploymentOption specifies special deployment options, or null
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @return Filter
     */
    public static Filter deployableSimultaneouslyWith(PhysicalCard sourceCard, PhysicalCard cardToDeployWith, final boolean cardToDeployWithForFree, final float cardToDeployWithChangeInCost, final boolean forFree, final float changeInCost, final DeploymentOption deploymentOption, final DeploymentRestrictionsOption deploymentRestrictionsOption) {
        final Integer permSourceCardId = sourceCard != null ? sourceCard.getPermanentCardId() : null;
        final Integer permCardToDeployWithCardId = cardToDeployWith.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);
                PhysicalCard cardToDeployWith = gameState.findCardByPermanentId(permCardToDeployWithCardId);
                return modifiersQuerying.isDeployable(gameState, sourceCard != null ? sourceCard : physicalCard, physicalCard, false, null, forFree, changeInCost, null, deploymentOption, deploymentRestrictionsOption, null, cardToDeployWith, cardToDeployWithForFree, cardToDeployWithChangeInCost);
            }
        };
    }

    /**
     * Filter that accepts cards that can be deployed to cards accepted by the specified filter.
     * @param sourceCard the card to initiate the action
     * @param filters the filters
     * @param forFree true if playing for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required   @return Filter
     */
    public static Filter deployableToTarget(PhysicalCard sourceCard, final Filter filters, final boolean forFree, final float changeInCost) {
        return deployableToTarget(sourceCard, filters, false, forFree, changeInCost, null, null, null, null, null);
    }

    /**
     * Filter that accepts cards that can be deployed to cards accepted by the specified filter.
     * @param sourceCard the card to initiate the action
     * @param filters the filters
     * @param includePlayable true if includes playable cards, false if only deployable cards
     * @param forFree true if playing for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param changeInCostCardFilter the card filter for cards that are affected by the change in cost, or null for all
     * @param deploymentOption specifies special deployment options, or null
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param deployAsCaptiveOption specifies the way to deploy the card as a captive, or null
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @return Filter
     */
    public static Filter deployableToTarget(PhysicalCard sourceCard, final Filter filters, final boolean includePlayable, final boolean forFree, final float changeInCost, final Filter changeInCostCardFilter, final DeploymentOption deploymentOption, final DeploymentRestrictionsOption deploymentRestrictionsOption, final DeployAsCaptiveOption deployAsCaptiveOption, final ReactActionOption reactActionOption) {
        final Integer permSourceCardId = sourceCard != null ? sourceCard.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);
                return modifiersQuerying.isDeployableToTarget(gameState, sourceCard != null ? sourceCard : physicalCard, physicalCard, includePlayable, Filters.and(filters), forFree, changeInCost, changeInCostCardFilter, deploymentOption, deploymentRestrictionsOption, deployAsCaptiveOption, reactActionOption, null, false, 0);
            }
        };
    }

    /**
     * Filter that accepts cards that can be deployed simultaneously with the specified card to cards accepted by the
     * specified filter.
     * @param sourceCard the card to initiate the action
     * @param cardToDeployWith the card to deploy with simultaneously
     * @param cardToDeployWithForFree true if the card to deploy with deploys for free, otherwise false
     * @param cardToDeployWithChangeInCost change in amount of Force (can be positive or negative) required for the card to deploy with
     * @param filters the filters
     * @param forFree true if playing for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return Filter
     */
    public static Filter deployableToTargetSimultaneouslyWith(PhysicalCard sourceCard, PhysicalCard cardToDeployWith, final boolean cardToDeployWithForFree, final float cardToDeployWithChangeInCost, final Filter filters, final boolean forFree, final float changeInCost) {
        return deployableToTargetSimultaneouslyWith(sourceCard, cardToDeployWith, cardToDeployWithForFree, cardToDeployWithChangeInCost, filters, forFree, changeInCost, null, null);
    }

    /**
     * Filter that accepts cards that can be deployed simultaneously with the specified card to cards accepted by the
     * specified filter.
     * @param sourceCard the card to initiate the action
     * @param cardToDeployWith the card to deploy with simultaneously
     * @param cardToDeployWithForFree true if the card to deploy with deploys for free, otherwise false
     * @param cardToDeployWithChangeInCost change in amount of Force (can be positive or negative) required for the card to deploy with
     * @param filters the filters
     * @param forFree true if playing for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param deploymentOption specifies special deployment options, or null
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @return Filter
     */
    public static Filter deployableToTargetSimultaneouslyWith(PhysicalCard sourceCard, PhysicalCard cardToDeployWith, final boolean cardToDeployWithForFree, final float cardToDeployWithChangeInCost, final Filter filters, final boolean forFree, final float changeInCost, final DeploymentOption deploymentOption, final DeploymentRestrictionsOption deploymentRestrictionsOption) {
        final Integer permSourceCardId = sourceCard != null ? sourceCard.getPermanentCardId() : null;
        final Integer permCardToDeployWithCardId = cardToDeployWith.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);
                PhysicalCard cardToDeployWith = gameState.findCardByPermanentId(permCardToDeployWithCardId);
                return modifiersQuerying.isDeployableToTarget(gameState, sourceCard != null ? sourceCard : physicalCard, physicalCard, false, Filters.and(filters), forFree, changeInCost, null, deploymentOption, deploymentRestrictionsOption, null, null, cardToDeployWith, cardToDeployWithForFree, cardToDeployWithChangeInCost);
            }
        };
    }

    /**
     * Filter that accepts cards that can be deployed to locations accepted by the specified filter.
     * @param sourceCard the card to initiate the action
     * @param filters the filters
     * @param forFree true if playing for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return Filter
     */
    public static Filter deployableToLocation(PhysicalCard sourceCard, final Filter filters, final boolean forFree, final float changeInCost) {
        return deployableToLocation(sourceCard, filters, false, forFree, changeInCost, null, null, null);
    }

    /**
     * Filter that accepts cards that can be deployed to locations accepted by the specified filter.
     * @param sourceCard the card to initiate the action
     * @param filters the filters
     * @param includePlayable true if includes playable cards, false if only deployable cards
     * @param forFree true if playing for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param deploymentOption specifies special deployment options, or null
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @return Filter
     */
    public static Filter deployableToLocation(PhysicalCard sourceCard, final Filter filters, final boolean includePlayable, final boolean forFree, final float changeInCost, final DeploymentOption deploymentOption, final DeploymentRestrictionsOption deploymentRestrictionsOption, final ReactActionOption reactActionOption) {
        final Integer permSourceCardId = sourceCard != null ? sourceCard.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);
                return modifiersQuerying.isDeployableToTarget(gameState, sourceCard != null ? sourceCard : physicalCard, physicalCard, includePlayable, Filters.locationAndCardsAtLocation(filters), forFree, changeInCost, null, deploymentOption, deploymentRestrictionsOption, null, reactActionOption, null, false, 0);
            }
        };
    }

    /**
     * Filter that accepts cards that can be deployed simultaneously with the specified card to locations accepted by the
     * specified filter.
     * @param sourceCard the card to initiate the action
     * @param cardToDeployWith the card to deploy with simultaneously
     * @param cardToDeployWithForFree true if the card to deploy with deploys for free, otherwise false
     * @param cardToDeployWithChangeInCost change in amount of Force (can be positive or negative) required for the card to deploy with
     * @param filters the filters
     * @param forFree true if playing for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return Filter
     */
    public static Filter deployableToLocationSimultaneouslyWith(PhysicalCard sourceCard, PhysicalCard cardToDeployWith, final boolean cardToDeployWithForFree, final float cardToDeployWithChangeInCost, final Filter filters, final boolean forFree, final float changeInCost) {
        return deployableToLocationSimultaneouslyWith(sourceCard, cardToDeployWith, cardToDeployWithForFree, cardToDeployWithChangeInCost, filters, forFree, changeInCost, null, null);
    }

    /**
     * Filter that accepts cards that can be deployed simultaneously with the specified card to locations accepted by the
     * specified filter.
     * @param sourceCard the card to initiate the action
     * @param cardToDeployWith the card to deploy with simultaneously
     * @param cardToDeployWithForFree true if the card to deploy with deploys for free, otherwise false
     * @param cardToDeployWithChangeInCost change in amount of Force (can be positive or negative) required for the card to deploy with
     * @param filters the filters
     * @param forFree true if playing for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param deploymentOption specifies special deployment options, or null
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @return Filter
     */
    public static Filter deployableToLocationSimultaneouslyWith(PhysicalCard sourceCard, PhysicalCard cardToDeployWith, final boolean cardToDeployWithForFree, final float cardToDeployWithChangeInCost, final Filter filters, final boolean forFree, final float changeInCost, final DeploymentOption deploymentOption, final DeploymentRestrictionsOption deploymentRestrictionsOption) {
        final Integer permSourceCardId = sourceCard != null ? sourceCard.getPermanentCardId() : null;
        final Integer permCardToDeployWithCardId = cardToDeployWith.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);
                PhysicalCard cardToDeployWith = gameState.findCardByPermanentId(permCardToDeployWithCardId);
                return modifiersQuerying.isDeployableToTarget(gameState, sourceCard != null ? sourceCard : physicalCard, physicalCard, false, Filters.locationAndCardsAtLocation(filters), forFree, changeInCost, null, deploymentOption, deploymentRestrictionsOption, null, null, cardToDeployWith, cardToDeployWithForFree, cardToDeployWithChangeInCost);
            }
        };
    }

   /**
     * Filter that accepts cards that can be deployed to the system of the specified name.
     * @param sourceCard the card to initiate the action
     * @param system the name of the system
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param forFree true if playing for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return Filter
     */
    public static Filter deployableToSystem(PhysicalCard sourceCard, final String system, final Filter specialLocationConditions, final boolean forFree, final float changeInCost) {
        return deployableToSystem(sourceCard, system, false, specialLocationConditions, forFree, changeInCost, null, null, null, null);
    }

    /**
     * Filter that accepts cards that can be deployed to the system of the specified name.
     *
     * @param sourceCard the card to initiate the action
     * @param system the name of the system
     * @param includePlayable true if includes playable cards, false if only deployable cards
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param forFree true if playing for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param changeInCostCardFilter the card filter for cards that are affected by the change in cost, or null for all
     * @param deploymentOption specifies special deployment options, or null
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @return Filter
     */
    public static Filter deployableToSystem(PhysicalCard sourceCard, final String system, final boolean includePlayable, final Filter specialLocationConditions, final boolean forFree, final float changeInCost, final Filter changeInCostCardFilter, final DeploymentOption deploymentOption, final DeploymentRestrictionsOption deploymentRestrictionsOption, final ReactActionOption reactActionOption) {
        final Integer permSourceCardId = sourceCard != null ? sourceCard.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);
                return modifiersQuerying.isDeployableToSystem(gameState, sourceCard != null ? sourceCard : physicalCard, physicalCard, includePlayable, system, Filters.any, specialLocationConditions, forFree, changeInCost, changeInCostCardFilter, deploymentOption, deploymentRestrictionsOption, reactActionOption, null, false, 0);
            }
        };
    }
    /**
     * Filter that accepts cards that can be deployed simultaneously with the specified card to the system of the specified name.
     *
     * @param sourceCard the card to initiate the action
     * @param cardToDeployWith the card to deploy with simultaneously
     * @param cardToDeployWithForFree true if the card to deploy with deploys for free, otherwise false
     * @param cardToDeployWithChangeInCost change in amount of Force (can be positive or negative) required for the card to deploy with
     * @param system the name of the system
     * @param forFree true if playing for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return Filter
     */
    public static Filter deployableToSystemSimultaneouslyWith(PhysicalCard sourceCard, PhysicalCard cardToDeployWith, final boolean cardToDeployWithForFree, final float cardToDeployWithChangeInCost, final String system, final boolean forFree, final float changeInCost) {
        return deployableToSystemSimultaneouslyWith(sourceCard, cardToDeployWith, cardToDeployWithForFree, cardToDeployWithChangeInCost, system, forFree, changeInCost, null, null);
    }

    /**
     * Filter that accepts cards that can be deployed simultaneously with the specified card to the system of the specified name.
     *
     * @param sourceCard the card to initiate the action
     * @param cardToDeployWith the card to deploy with simultaneously
     * @param cardToDeployWithForFree true if the card to deploy with deploys for free, otherwise false
     * @param cardToDeployWithChangeInCost change in amount of Force (can be positive or negative) required for the card to deploy with
     * @param system the name of the system
     * @param forFree true if playing for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param deploymentOption specifies special deployment options, or null
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @return Filter
     */
    public static Filter deployableToSystemSimultaneouslyWith(PhysicalCard sourceCard, PhysicalCard cardToDeployWith, final boolean cardToDeployWithForFree, final float cardToDeployWithChangeInCost, final String system, final boolean forFree, final float changeInCost, final DeploymentOption deploymentOption, final DeploymentRestrictionsOption deploymentRestrictionsOption) {
        final Integer permSourceCardId = sourceCard != null ? sourceCard.getPermanentCardId() : null;
        final Integer permCardToDeployWithCardId = cardToDeployWith.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);
                PhysicalCard cardToDeployWith = gameState.findCardByPermanentId(permCardToDeployWithCardId);
                return modifiersQuerying.isDeployableToSystem(gameState, sourceCard != null ? sourceCard : physicalCard, physicalCard, false, system, Filters.any, null, forFree, changeInCost, null, deploymentOption, deploymentRestrictionsOption, null, cardToDeployWith, cardToDeployWithForFree, cardToDeployWithChangeInCost);
            }
        };
    }

    /**
     * Filter that accepts cards that can be deployed as a starting location.
     */
    public static final Filter deployableAsStartingLocation = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            if (physicalCard.getBlueprint().getCardCategory()!=CardCategory.LOCATION)
                return false;

            if (!deployable(physicalCard, null, false, 0).accepts(gameState, modifiersQuerying, physicalCard))
                return false;

            // For Main Power Generators, check that 4th Marker is in Reserve Deck of owner.
            if (Filters.Main_Power_Generators.accepts(gameState, modifiersQuerying, physicalCard)) {
                return !Filters.filterCount(gameState.getReserveDeck(physicalCard.getOwner()), gameState.getGame(), 1, Filters.Fourth_Marker).isEmpty();
            }
            return true;
        }
    };

    /**
     * Filter that accepts cards that are Effects that deploy on another card (when it is able to be deployed).
     */
    public static final Filter effectThatDeploysOnAnotherCard = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.getBlueprint().isEffectThatDeploysOnAnotherCard(gameState.getGame(), physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter effectThatDeploysOnAnotherCard() {
        return effectThatDeploysOnAnotherCard;
    }


    /**
     * Filter that accepts Interrupts that can be played in response to an effect.
     *
     * @param sourceCard the card to initiate the action
     * @return Filter
     */
    public static Filter playable(PhysicalCard sourceCard) {
        final Integer permSourceCardId = sourceCard.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);
                return (physicalCard.getBlueprint().getPlayCardAction(physicalCard.getOwner(), gameState.getGame(), physicalCard, sourceCard, false, 0, null, null, null, null, null, false, 0, Filters.any, null) != null);
            }
        };
    }

    /**
     * Filter that accepts Interrupts that can be played in response to an effect.
     *
     * @param sourceCard the card to initiate the action
     * @param effect the effect to play the Interrupt in response to
     * @return Filter
     */
    public static Filter playableInterruptAsResponse(PhysicalCard sourceCard, final Effect effect) {
        final Integer permSourceCardId = sourceCard.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() == CardCategory.INTERRUPT) {
                    PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);
                    return (physicalCard.getBlueprint().getPlayInterruptAsResponseAction(physicalCard.getOwner(), gameState.getGame(), physicalCard, sourceCard, effect, null) != null);
                }
                return false;
            }
        };
    }

    /**
     * Filter that accepts Interrupts that can be played in response to an effect result.
     *
     * @param sourceCard the card to initiate the action
     * @param effectResult the effect result to play the Interrupt in response to
     * @return Filter
     */
    public static Filter playableInterruptAsResponse(PhysicalCard sourceCard, final EffectResult effectResult) {
        final Integer permSourceCardId = sourceCard.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() == CardCategory.INTERRUPT) {
                    PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);
                    return (physicalCard.getBlueprint().getPlayInterruptAsResponseAction(physicalCard.getOwner(), gameState.getGame(), physicalCard, sourceCard, null, effectResult) != null);
                }
                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that can be played as a Starting Interrupt.
     */
    public static final Filter playableAsStartingInterrupt = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            if (physicalCard.getBlueprint().getCardCategory() == CardCategory.INTERRUPT
                    && (physicalCard.getBlueprint().getCardSubtype() == CardSubtype.STARTING
                    || physicalCard.getBlueprint().getCardSubtype() == CardSubtype.LOST_OR_STARTING
                    || physicalCard.getBlueprint().getCardSubtype() == CardSubtype.USED_OR_STARTING)) {
                return (physicalCard.getBlueprint().getStartingInterruptAction(physicalCard.getOwner(), gameState.getGame(), physicalCard) != null);
            }
            return false;
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter playableAsStartingInterrupt() {
        return playableAsStartingInterrupt;
    }


    //
    //
    // Filters for weapon firing
    //
    //


    /**
     * Filter that accepts weapons (or characters that have permanent weapons) that can be fired by another action for free.
     * @param sourceCard the card to initiate the firing
     * @param extraForceRequired extra Force required to perform the fire weapon action
     * @return Filter
     */
    public static Filter canBeFiredForFree(PhysicalCard sourceCard, final int extraForceRequired) {
        return canBeFiredForFree(sourceCard, extraForceRequired, false);
    }

    /**
     * Filter that accepts weapons (or characters that have permanent weapons) that can be fired by another action for free.
     * @param sourceCard the card to initiate the firing
     * @param extraForceRequired extra Force required to perform the fire weapon action
     * @param ignorePerAttackOrBattleLimit true if per attack/battle firing limit is ignored, otherwise false
     * @return Filter
     */
    public static Filter canBeFiredForFree(PhysicalCard sourceCard, final int extraForceRequired, final boolean ignorePerAttackOrBattleLimit) {
        final Integer permSourceCardId = sourceCard.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() == CardCategory.WEAPON
                        || physicalCard.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                    PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);
                    return physicalCard.getBlueprint().getFireWeaponAction(physicalCard.getOwner(), gameState.getGame(), physicalCard, true, extraForceRequired, sourceCard, false, Filters.none, null, Filters.any, ignorePerAttackOrBattleLimit) != null;
                }
                return false;
            }
        };
    }

    /**
     * Filter that accepts weapons (or characters that have permanent weapons) that can be fired by another action for
     * free at a card accepted by the target filter.
     * @param sourceCard the card to initiate the firing
     * @param extraForceRequired extra Force required to perform the fire weapon action
     * @param targetFilter the target filter  @return Filter
     */
    public static Filter canBeFiredForFreeAt(PhysicalCard sourceCard, final int extraForceRequired, final Filter targetFilter) {
        final Integer permSourceCardId = sourceCard.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() == CardCategory.WEAPON
                        || physicalCard.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                    PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);
                    return physicalCard.getBlueprint().getFireWeaponAction(physicalCard.getOwner(), gameState.getGame(), physicalCard, true, extraForceRequired, sourceCard, false, Filters.none, null, Filters.and(targetFilter), false) != null;
                }
                return false;
            }
        };
    }

    /**
     * Filter that accepts weapons (or characters that have permanent weapons) that can be fired by another action.
     * @param sourceCard the card to initiate the firing
     * @param extraForceRequired extra Force required to perform the fire weapon action
     * @return Filter
     */
    public static Filter canBeFired(PhysicalCard sourceCard, final int extraForceRequired) {
        final Integer permSourceCardId = sourceCard.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() == CardCategory.WEAPON
                        || physicalCard.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                    PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);
                    return physicalCard.getBlueprint().getFireWeaponAction(physicalCard.getOwner(), gameState.getGame(), physicalCard, false, extraForceRequired, sourceCard, false, Filters.none, null, Filters.any, false) != null;
                }
                return false;
            }
        };
    }

    /**
     * Filter that accepts weapons (or characters that have permanent weapons) that can be fired by another action at a
     * card accepted by the target filter.
     * @param sourceCard the card to initiate the firing
     * @param targetFilter the target filter
     * @param extraForceRequired extra Force required to perform the fire weapon action
     * @return Filter
     */
    public static Filter canBeFiredAt(PhysicalCard sourceCard, final Filter targetFilter, final int extraForceRequired) {
        final Integer permSourceCardId = sourceCard.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() == CardCategory.WEAPON
                        || physicalCard.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                    PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);
                    return physicalCard.getBlueprint().getFireWeaponAction(physicalCard.getOwner(), gameState.getGame(), physicalCard, false, extraForceRequired, sourceCard, false, Filters.none, null, Filters.and(targetFilter), false) != null;
                }
                return false;
            }
        };
    }

    /**
     * Filter that accepts weapons (or characters that have permanent weapons) that can be fired by another action.
     * @param sourceCard the card to initiate the firing
     * @param extraForceRequired extra Force required to perform the fire weapon action
     * @param targetedAsCharacter filter for cards may be targeted as characters
     * @return Filter
     */
    public static Filter canBeFired(PhysicalCard sourceCard, final int extraForceRequired, final Filter targetedAsCharacter) {
        final Integer permSourceCardId = sourceCard.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() == CardCategory.WEAPON
                        || physicalCard.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                    PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);
                    return physicalCard.getBlueprint().getFireWeaponAction(physicalCard.getOwner(), gameState.getGame(), physicalCard, false, extraForceRequired, sourceCard, false, targetedAsCharacter, null, Filters.any, false) != null;
                }
                return false;
            }
        };
    }

    /**
     * Filter that accepts weapons (or characters that have permanent weapons) that can be fired by another action at a
     * card accepted by the target filter.
     * @param sourceCard the card to initiate the firing
     * @param targetFilter the target filter
     * @param extraForceRequired extra Force required to perform the fire weapon action
     * @param targetedAsCharacter filter for cards may be targeted as characters
     * @return Filter
     */
    public static Filter canBeFiredAt(PhysicalCard sourceCard, final Filter targetFilter, final int extraForceRequired, final Filter targetedAsCharacter) {
        final Integer permSourceCardId = sourceCard.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() == CardCategory.WEAPON
                        || physicalCard.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                    PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);
                    return physicalCard.getBlueprint().getFireWeaponAction(physicalCard.getOwner(), gameState.getGame(), physicalCard, false, extraForceRequired, sourceCard, false, targetedAsCharacter, null, Filters.and(targetFilter), false) != null;
                }
                return false;
            }
        };
    }


    /**
     * Filter that accepts weapons that may fire repeatedly.
     */
    public static final Filter mayFireRepeatedly = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.mayFireWeaponRepeatedly(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter mayFireRepeatedly() {
        return mayFireRepeatedly;
    }


    //
    //
    // Filters for targeting
    //
    //

    /**
     * Filter that accepts cards that are not prevented from targeting the specified card for an unspecified reason.
     *
     * @param card the card
     * @return Filter
     */
    public static Filter canTargetCard(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return modifiersQuerying.canBeTargetedBy(gameState, card, physicalCard);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    /**
     * Filter that accepts cards that can be targeted by the specified card for an unspecified reason.
     *
     * @param card the card
     * @return Filter
     */
    public static Filter canBeTargetedBy(PhysicalCard card) {
        return canBeTargetedBy(card, TargetingReason.OTHER);
    }

    /**
     * Filter that accepts cards that can be targeted by the specified card for the specified reason.
     *
     * @param card the card
     * @param targetingReason the reason
     * @return Filter
     */
    public static Filter canBeTargetedBy(PhysicalCard card, final TargetingReason targetingReason) {
        final Integer permCardId = card != null ? card.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return modifiersQuerying.canBeTargetedBy(gameState, physicalCard, card, Collections.singleton(targetingReason));
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                return accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    /**
     * Filter that accepts cards that can be targeted by the specified card for the specified reasons.
     *
     * @param card the card
     * @param targetingReasons the reasons
     * @return Filter
     */
    public static Filter canBeTargetedBy(PhysicalCard card, final Set<TargetingReason> targetingReasons) {
        final Integer permCardId = card != null ? card.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return modifiersQuerying.canBeTargetedBy(gameState, physicalCard, card, targetingReasons);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                return accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    /**
     * Filter that accepts cards that can be targeted by the specified permanent weapon for an unspecified reason.
     *
     * @param permanentWeapon the permanent weapon
     * @return Filter
     */
    public static Filter canBeTargetedBy(SwccgBuiltInCardBlueprint permanentWeapon) {
        return canBeTargetedBy(permanentWeapon, TargetingReason.OTHER);
    }

    /**
     * Filter that accepts cards that can be targeted by the specified permanent weapon for the specified reason.
     *
     * @param permanentWeapon the permanent weapon
     * @param targetingReason the reason
     * @return Filter
     */
    public static Filter canBeTargetedBy(final SwccgBuiltInCardBlueprint permanentWeapon, final TargetingReason targetingReason) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.canBeTargetedBy(gameState, physicalCard, permanentWeapon, Collections.singleton(targetingReason));
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                return accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    /**
     * Filter that accepts cards that can be targeted by the specified permanent weapon for an unspecified reason.
     *
     * @param card the card
     * @param permanentWeapon the permanent weapon
     * @return Filter
     */
    public static Filter canBeTargetedBy(PhysicalCard card, SwccgBuiltInCardBlueprint permanentWeapon) {
        if (permanentWeapon != null)
            return canBeTargetedBy(permanentWeapon, TargetingReason.OTHER);
        else
            return canBeTargetedBy(card, TargetingReason.OTHER);
    }

    /**
     * Filter that accepts cards that can be targeted by the specified permanent weapon for the specified reason.
     *
     * @param card the card
     * @param permanentWeapon the permanent weapon
     * @param targetingReason the reason
     * @return Filter
     */
    public static Filter canBeTargetedBy(PhysicalCard card, final SwccgBuiltInCardBlueprint permanentWeapon, final TargetingReason targetingReason) {
        if (permanentWeapon != null)
            return canBeTargetedBy(permanentWeapon, targetingReason);
        else
            return canBeTargetedBy(card, targetingReason);
    }

    /**
     * Filter that accepts cards that can be targeted by weapons as if present.
     */
    public static final Filter canBeTargetedByWeaponAsIfPresent = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.canBeTargetedByWeaponsAsIfPresent(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter canBeTargetedByWeaponAsIfPresent() {
        return canBeTargetedByWeaponAsIfPresent;
    }

    /**
     * Filter that accepts cards that can be targeted by weapons as a starfighter.
     */
    public static final Filter canBeTargetedByWeaponAsStarfighter = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.canBeTargetedByWeaponsAsStarfighter(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter canBeTargetedByWeaponAsStarfighter() {
        return canBeTargetedByWeaponAsStarfighter;
    }

    /**
     * Filter that accepts cards that are not prevented by being targeted by the specified weapon user.
     * @param weaponUser the weapon user
     * @return Filter
     */
    public static Filter canBeTargetedByWeaponUser(PhysicalCard weaponUser) {
        final Integer permWeaponUserCardId = weaponUser.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard weaponUser = gameState.findCardByPermanentId(permWeaponUserCardId);
                return !modifiersQuerying.mayNotBeTargetedByWeaponUser(gameState, physicalCard, weaponUser);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                return accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    /**
     * Filter that accepts cards that are granted to be targeted by a specified card.
     * Examples: Mara Jade may be targeted by Vader's Obsession and Epic Duel,
     *           Bothan Spy may be targeted by Death Star Plans
     *
     * @param card the card
     * @return Filter
     */
    public static Filter grantedMayBeTargetedBy(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return modifiersQuerying.grantedMayBeTargetedBy(gameState, physicalCard, card);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                return accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
            }
        };
    }

    /**
     * Filter that accepts cards that have been targeted by a weapon accepted by the weapon filter this turn.
     * @param weaponFilter the weapon filter
     * @return Filter
     */
    public static Filter hasBeenTargetedByWeaponThisTurn(final Filter weaponFilter) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                Collection<PhysicalCard> weaponsTargetedByThisTurn = modifiersQuerying.weaponsTargetedByThisTurn(physicalCard);
                for (PhysicalCard weapon : weaponsTargetedByThisTurn) {
                    if (Filters.and(weaponFilter).accepts(gameState, modifiersQuerying, weapon)) {
                        return true;
                    }
                }
                Collection<SwccgBuiltInCardBlueprint> permanentWeaponsTargetedByThisTurn = modifiersQuerying.permanentWeaponsTargetedByThisTurn(physicalCard);
                for (SwccgBuiltInCardBlueprint permanentWeapon : permanentWeaponsTargetedByThisTurn) {
                    if (Filters.and(weaponFilter).accepts(gameState, modifiersQuerying, permanentWeapon)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that the current firing weapon can be re-targeted to instead of the current target.
     *
     * @param currentTarget the current target
     * @return Filter
     */
    public static Filter weaponMayRetargetTo(PhysicalCard currentTarget) {
        final Integer permCurrentTargetCardId = currentTarget.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard currentTarget = gameState.findCardByPermanentId(permCurrentTargetCardId);

                if (Filters.sameCardId(currentTarget).accepts(gameState, modifiersQuerying, physicalCard)) {
                    return false;
                }
                WeaponFiringState weaponFiringState = gameState.getWeaponFiringState();
                if (weaponFiringState != null) {
                    RespondableWeaponFiringEffect respondableWeaponFiringEffect = (RespondableWeaponFiringEffect) weaponFiringState.getWeaponFiringEffect();
                    if (!respondableWeaponFiringEffect.isCanceled()) {

                        final Action targetingAction = respondableWeaponFiringEffect.getTargetingAction();
                        Map<Integer, Map<PhysicalCard, Set<TargetingReason>>> targetingMap = targetingAction.getAllPrimaryTargetCards();

                        for (Integer targetGroupId : targetingMap.keySet()) {
                            final Map<PhysicalCard, Set<TargetingReason>> targetingCardMap = targetingMap.get(targetGroupId);
                            final Set<TargetingReason> targetingReasons = targetingCardMap.get(currentTarget);
                            if (targetingReasons != null) {

                                // Determine targeting reasons for new target
                                Map<TargetingReason, Filterable> targetFilterMap = targetingAction.getPrimaryTargetFilter(targetGroupId);
                                Set<TargetingReason> newTargetingReasons = new HashSet<TargetingReason>();
                                for (TargetingReason targetingReason : targetFilterMap.keySet()) {
                                    if (Filters.and(targetFilterMap.get(targetingReason)).acceptsIgnoringOwner(gameState, modifiersQuerying, physicalCard)) {
                                        newTargetingReasons.add(targetingReason);
                                    }
                                }
                                if (!newTargetingReasons.isEmpty()) {

                                    // Determine if card can be targeted
                                    if (weaponFiringState.getPermanentWeaponFiring() != null) {
                                        if (modifiersQuerying.canBeTargetedBy(gameState, physicalCard, weaponFiringState.getPermanentWeaponFiring(), newTargetingReasons)) {
                                            return true;
                                        }
                                    } else {
                                        if (modifiersQuerying.canBeTargetedBy(gameState, physicalCard, weaponFiringState.getCardFiring(), newTargetingReasons)) {
                                            return true;
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
                return false;
            }
        };
    }

    /**
     * Filter that accepts weapons that are currently being fired by the specified card.
     *
     * @param weaponUser the weapon user
     * @return Filter
     */
    public static Filter weaponBeingFiredBy(PhysicalCard weaponUser) {
        final Integer permWeaponUserCardId = weaponUser.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard weaponUser = gameState.findCardByPermanentId(permWeaponUserCardId);

                WeaponFiringState weaponFiringState = gameState.getWeaponFiringState();
                if (weaponFiringState != null) {
                    PhysicalCard cardFiringWeapon = weaponFiringState.getCardFiringWeapon();
                    PhysicalCard weapon = weaponFiringState.getCardFiring();
                    return cardFiringWeapon != null && Filters.sameCardId(weaponUser).accepts(gameState, modifiersQuerying, cardFiringWeapon)
                            && weapon != null && Filters.sameCardId(physicalCard).accepts(gameState, modifiersQuerying, weapon);
                }
                return false;
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                PhysicalCard weaponUser = gameState.findCardByPermanentId(permWeaponUserCardId);

                WeaponFiringState weaponFiringState = gameState.getWeaponFiringState();
                if (weaponFiringState != null) {
                    PhysicalCard cardFiringWeapon = weaponFiringState.getCardFiringWeapon();
                    SwccgBuiltInCardBlueprint permanentWeapon = weaponFiringState.getPermanentWeaponFiring();
                    return cardFiringWeapon != null && Filters.sameCardId(weaponUser).accepts(gameState, modifiersQuerying, cardFiringWeapon)
                            && permanentWeapon != null && Filters.sameCardId(permanentWeapon.getPhysicalCard(gameState.getGame())).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
                }
                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that has built-in permanent weapon accepted by the specified filter.
     *
     * @param filters the filters
     * @return Filter
     */
    public static Filter hasPermanentWeapon(final Filter filters) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                SwccgBuiltInCardBlueprint permanentWeapon = modifiersQuerying.getPermanentWeapon(gameState, physicalCard);
                return (permanentWeapon != null
                        && Filters.and(filters).accepts(gameState, modifiersQuerying, permanentWeapon));
            }
        };
    }

    /**
     * Filter that accepts permanent weapons of the specified card.
     *
     * @param card the card
     * @return Filter
     */
    public static Filter permanentWeaponOf(PhysicalCard card) {
        return Filters.permanentWeaponOf(Filters.sameCardId(card));
    }

    /**
     * Filter that accepts permanent weapons of cards accepted by the specified filter.
     *
     * @param filters the filters
     * @return Filter
     */
    public static Filter permanentWeaponOf(final Filter filters) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return false;
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                PhysicalCard card = builtInCardBlueprint.getPhysicalCard(gameState.getGame());
                return builtInCardBlueprint.isWeapon() && card != null && Filters.and(filters).accepts(gameState, modifiersQuerying, card);
            }
        };
    }


    /**
     * Filter that accepts cards that the specified Interrupt is granted ability to cancel by playing.
     * @param card the Interrupt card
     * @return Filter
     */
    public static Filter grantedToBeCanceledBy(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return modifiersQuerying.mayPlayInterruptToCancelCard(gameState, card, physicalCard);
            }
        };
    }

    //
    //
    // Filters for attacks
    //
    //

    /**
     * Filter that accepts creatures at the specified location that can be attacked by the specified player.
     *
     * @param playerId the player to initiate the attack
     * @param location the location
     * @return Filter
     */
    public static Filter creatureAtLocationCanBeAttackedByPlayer(final String playerId, PhysicalCard location) {
        final Integer permLocationId = location.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard location = gameState.findCardByPermanentId(permLocationId);

                if (!Filters.and(Filters.creature, Filters.at(location)).accepts(gameState, modifiersQuerying, physicalCard))
                    return false;

                return Filters.canSpot(gameState.getGame(), null, Filters.initiallyParticipatesInAttackOnCreature(playerId, physicalCard, true));
            }
        };
    }

    /**
     * Filter that accepts cards that would participate in an attack on a specified creature.
     *
     * @param playerId the player to initiate the attack
     * @param creature the creature
     * @param checkingOnly true if only checking if attack can be initiated, false if getting all cards to participate in the attack
     * @return Filter
     */
    public static Filter initiallyParticipatesInAttackOnCreature(final String playerId, PhysicalCard creature, final boolean checkingOnly) {
        final Integer permCardId = creature.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard creature = gameState.findCardByPermanentId(permCardId);
                if (!physicalCard.getOwner().equals(playerId))
                    return false;

                CardCategory cardCategory = physicalCard.getBlueprint().getCardCategory();

                if (cardCategory == CardCategory.CHARACTER || cardCategory == CardCategory.VEHICLE || cardCategory == CardCategory.STARSHIP
                        || (!checkingOnly && (cardCategory == CardCategory.DEVICE || cardCategory == CardCategory.WEAPON || cardCategory == CardCategory.EFFECT))) {

                    if (!Filters.presentWith(creature).accepts(gameState, modifiersQuerying, physicalCard))
                        return false;

                    if (modifiersQuerying.isProhibitedFromAttackingTarget(gameState, physicalCard, creature))
                        return false;

                    if (checkingOnly && modifiersQuerying.hasParticipatedInAttackOnCreatureThisTurn(physicalCard))
                        return false;

                    return true;
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts non-creatures at the specified location that can be attacked by the specified creature.
     *
     * @param creature the creature
     * @param allowDroidsAndAboardVehicles true if non-parasite attack is not restricted to creature vehicles,
     *                                     non-droid characters, and those not aboard open vehicles, otherwise false
     * @return Filter
     */
    public static Filter nonCreatureCanBeAttackedByCreature(PhysicalCard creature, final boolean allowDroidsAndAboardVehicles) {
        final Integer permCardId = creature.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard creature = gameState.findCardByPermanentId(permCardId);

                if (!Filters.presentWith(creature).accepts(gameState, modifiersQuerying, physicalCard)) {
                    return false;
                }

                if (!allowDroidsAndAboardVehicles || Filters.parasite.accepts(gameState, modifiersQuerying, creature)) {
                    if (Filters.aboard(Filters.vehicle).accepts(gameState, modifiersQuerying, physicalCard)) {
                        return false;
                    }
                }

                if (modifiersQuerying.isProhibitedFromAttackingTarget(gameState, creature, physicalCard)) {
                    return false;
                }

                if (!Filters.parasite.accepts(gameState, modifiersQuerying, creature)
                        && (allowDroidsAndAboardVehicles
                        || Filters.or(Filters.creature_vehicle, Filters.non_droid_character).accepts(gameState, modifiersQuerying, physicalCard))) {
                    return true;
                }

                if (modifiersQuerying.grantedToAttackTarget(gameState, creature, physicalCard)) {
                    return true;
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts cards participating in an attack.
     */
    public static final Filter participatingInAttack = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return gameState.isParticipatingInAttack(physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter participatingInAttack() {
        return participatingInAttack;
    }

    /**
     * Filter that accepts cards participating in an attack and present at the attack location.
     */
    public static final Filter presentInAttack = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            if (!gameState.isParticipatingInAttack(physicalCard))
                return false;

            return present(gameState.getAttackLocation()).accepts(gameState, modifiersQuerying, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter presentInAttack() {
        return presentInAttack;
    }

    /**
     * Filter that accepts cards being attacked by a creature.
     */
    public static final Filter beingAttackedByCreature = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            AttackState attackState = gameState.getAttackState();
            if (attackState != null) {
                if (attackState.isCreaturesAttackingEachOther()) {
                    return attackState.isCardParticipatingInAttack(physicalCard);
                }
                else if (attackState.isNonCreatureAttackingCreature()) {
                    return attackState.getCardsDefending().contains(physicalCard);
                }
            }
            return false;
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter beingAttackedByCreature() {
        return beingAttackedByCreature;
    }

    /**
     * Filter that accepts cards that are placed out of play when eaten by the specified card.
     *
     * @param card the card
     * @return Filter
     */
    public static Filter placedOutOfPlayWhenEatenBy(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return modifiersQuerying.isEatenByPlacedOutOfPlay(gameState, physicalCard, card);
            }
        };
    }

    //
    //
    // Filters for battles
    //
    //

    /**
     * Filter that accepts cards that are excluded from battle.
     */
    public static final Filter excludedFromBattle = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.isExcludedFromBattle(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter excludedFromBattle() {
        return excludedFromBattle;
    }

    /**
     * Filter that accepts cards that are currently eligible to participate in a battle at a specified location.
     *
     * @param location the location
     * @param playerInitiatingBattle the player initiating battle
     * @return Filter
     */
    public static Filter canParticipateInBattleAt(PhysicalCard location, final String playerInitiatingBattle) {
        final Integer permLocationId = location.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard location = gameState.findCardByPermanentId(permLocationId);

                CardCategory cardCategory = physicalCard.getBlueprint().getCardCategory();
                if (physicalCard.isDejarikHologramAtHolosite() || cardCategory == CardCategory.CHARACTER || cardCategory == CardCategory.DEVICE || cardCategory == CardCategory.VEHICLE
                        || cardCategory == CardCategory.STARSHIP || cardCategory == CardCategory.WEAPON || cardCategory == CardCategory.EFFECT) {

                    PhysicalCard cardLocation = modifiersQuerying.getLocationThatCardIsAt(gameState, physicalCard);
                    if (cardLocation == null || !Filters.sameCardId(location).accepts(gameState, modifiersQuerying, cardLocation))
                        return false;

                    if (modifiersQuerying.hasParticipatedInBattleAtOtherLocation(physicalCard, location))
                        return false;

                    if (modifiersQuerying.isExcludedFromBattle(gameState, physicalCard))
                        return false;

                    if (!modifiersQuerying.mayNotBeExcludedFromBattle(gameState, physicalCard)) {
                        if (modifiersQuerying.isProhibitedFromParticipatingInBattle(gameState, physicalCard, playerInitiatingBattle))
                            return false;
                    }

                    return true;
                }
                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that initially would join a battle at the specified location.
     * This ignores whether the cards cannot participate in the battle, since those cards will be excluded
     * when the battle actually starts.
     *
     * @param location the location
     * @return Filter
     */
    public static Filter initiallyParticipatesInBattle(PhysicalCard location) {
        final Integer permLocationId = location.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard location = gameState.findCardByPermanentId(permLocationId);

                CardCategory cardCategory = physicalCard.getBlueprint().getCardCategory();
                if (physicalCard.isDejarikHologramAtHolosite() || cardCategory == CardCategory.CHARACTER || cardCategory==CardCategory.DEVICE || cardCategory == CardCategory.VEHICLE
                        || cardCategory == CardCategory.STARSHIP || cardCategory == CardCategory.WEAPON || cardCategory == CardCategory.EFFECT) {

                    PhysicalCard cardLocation = modifiersQuerying.getLocationThatCardIsAt(gameState, physicalCard);
                    if (cardLocation==null || !Filters.sameCardId(location).accepts(gameState, modifiersQuerying, cardLocation))
                        return false;

                    return true;
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that may initiate battle.
     */
    public static final Filter mayInitiateBattle = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.mayInitiateBattle(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter mayInitiateBattle() {
        return mayInitiateBattle;
    }

    /**
     * Filter that accepts cards that may be battled.
     */
    public static final Filter mayBeBattled = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.mayBeBattled(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter mayBeBattled() {
        return mayBeBattled;
    }

    /**
     * Filter that accepts cards that may not be battled.
     */
    public static final Filter mayNotBeBattled = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.mayBeBattled(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter mayNotBeBattled() {
        return mayNotBeBattled;
    }

    /**
     * Filter that accepts cards that may not be excluded from battle.
     */
    public static final Filter mayNotBeExcludedFromBattle = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.mayNotBeExcludedFromBattle(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter mayNotBeExcludedFromBattle() {
        return mayNotBeExcludedFromBattle;
    }

    /**
     * Filter that accepts cards participating in a battle.
     */
    public static final Filter participatingInBattle = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return gameState.isParticipatingInBattle(physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter participatingInBattle() {
        return participatingInBattle;
    }

    /**
     * Filter that accepts cards participating in a battle initiated by that card's owner.
     */
    public static final Filter participatingInBattleInitiatedByOwner = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return gameState.isDuringBattleInitiatedBy(physicalCard.getOwner())
                    && gameState.isParticipatingInBattle(physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter participatingInBattleInitiatedByOwner() {
        return participatingInBattleInitiatedByOwner;
    }

    /**
     * Filter that accepts cards participating in a battle initiated by the opponent of that card's owner.
     */
    public static final Filter defendingBattle = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return gameState.isDuringBattleInitiatedBy(gameState.getOpponent(physicalCard.getOwner()))
                    && gameState.isParticipatingInBattle(physicalCard);
        }
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
            return defendingBattle.accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()));
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter defendingBattle() {
        return defendingBattle;
    }

    /**
     * Filter that accepts cards that are making a Bombing Run.
     */
    public static final Filter makingBombingRun = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.isMakingBombingRun();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter makingBombingRun() {
        return makingBombingRun;
    }

    /**
     * Filter that accepts cards participating in a Bombing Run battle.
     */
    public static final Filter inBombingRunBattle = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return gameState.isDuringBombingRunBattle()
                    && gameState.isParticipatingInBattle(physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter inBombingRunBattle() {
        return inBombingRunBattle;
    }

    /**
     * Filter that accepts cards participating in a Besieged battle.
     */
    public static final Filter inBesiegedBattle = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return gameState.isDuringBesiegedBattle()
                    && gameState.isParticipatingInBattle(physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter inBesiegedBattle() {
        return inBesiegedBattle;
    }

    /**
     * Filter that accepts cards participating in a Local Trouble battle.
     */
    public static final Filter inLocalTroubleBattle = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return gameState.isDuringLocalTroubleBattle()
                    && gameState.isParticipatingInBattle(physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter inLocalTroubleBattle() {
        return inLocalTroubleBattle;
    }

    /**
     * Filter that accepts cards participating in a battle and present at the battle location.
     */
    public static final Filter presentInBattle = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            if (!gameState.isParticipatingInBattle(physicalCard))
                return false;

            return present(gameState.getBattleLocation()).accepts(gameState, modifiersQuerying, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter presentInBattle() {
        return presentInBattle;
    }

    /**
     * Filter that accepts cards that are in battle with the specified card.
     *
     * @param card the card
     * @return Filter
     */
    public static Filter inBattleWith(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                boolean inBattle = gameState.isParticipatingInBattle(card);

                if (!inBattle)
                    return false;

                return !Filters.sameCardId(card).accepts(gameState, modifiersQuerying, physicalCard)
                        && gameState.isParticipatingInBattle(physicalCard);
            }
        };
    }

    /**
     * Filter that accepts cards that are in battle with a card accepted by the specified filter.
     *
     * @param filters the filters
     * @return Filter
     */
    public static Filter inBattleWith(final Filter filters) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                Collection<PhysicalCard> cards = null;
                if (gameState.isDuringBattle()) {
                    cards = gameState.getBattleState().getAllCardsParticipating();
                }

                if (cards == null || cards.size() < 2 || !cards.contains(physicalCard))
                    return false;

                Filter filterToUse = Filters.and(Filters.not(physicalCard), Filters.or(filters, Filters.hasPermanentAboard(filters), Filters.hasPermanentWeapon(filters)));
                return Filters.canSpot(cards, gameState.getGame(), filterToUse);
            }
        };
    }

    /**
     * Filter that accepts cards that are location at which a battle has not been initiated this turn.
     */
    public static final Filter battleNotOccurredAtLocation = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return !modifiersQuerying.isBattleOccurredAtLocationThisTurn(physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter battleNotOccurredAtLocation() {
        return battleNotOccurredAtLocation;
    }


    /**
     * Filter that accepts cards participating in duel.
     */
    public static final Filter participatingInDuel = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return gameState.isParticipatingInDuel(physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter participatingInDuel() {
        return participatingInDuel;
    }

    /**
     * Filter that accepts cards participating in lightsaber combat.
     */
    public static final Filter participatingInLightsaberCombat = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return gameState.isParticipatingInLightsaberCombat(physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter participatingInLightsaberCombat() {
        return participatingInLightsaberCombat;
    }


    //
    //
    // Filters for Force drains
    //
    //

    /**
     * Filter that accepts cards that cannot participate in a Force drain.
     */
    public static final Filter cannotParticipateInForceDrain = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.hasParticipatedInForceDrainThisTurn(physicalCard)
                    && !modifiersQuerying.hasKeyword(gameState, physicalCard, Keyword.FORCE_DRAIN_MULTI_PARTICIPANT);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter cannotParticipateInForceDrain() {
        return cannotParticipateInForceDrain;
    }

    /**
     * Filter that accepts cards that are granted the ability to Force drain.
     */
    public static final Filter mayForceDrain = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.mayForceDrain(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter mayForceDrain() {
        return mayForceDrain;
    }

    //
    //
    // Filters for card properties
    //
    //

    /**
     * Filter that accepts cards that are 'bluff' cards.
     */
    public static final Filter bluffCard = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.isBluffCard();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter bluffCard() {
        return bluffCard;
    }

    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter combatCard() {
        return combatCard;
    }

    /**
     * Filter that accepts cards that are combat cards.
     */
    public static final Filter combatCard = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.isCombatCard();
        }
    };

    /**
     * Filter that accepts cards that are combat cards that may be used by the specified character.
     *
     * @param character the character
     * @return Filter
     */
    public static Filter combatCardUsableBy(PhysicalCard character) {
        final Integer permCharacterCardId = character.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard character = gameState.findCardByPermanentId(permCharacterCardId);
                return physicalCard.isCombatCard() && modifiersQuerying.mayUseCombatCard(gameState, character, physicalCard);
            }
        };
    }

    /**
     * Filter that accepts cards that are 'insert' cards.
     */
    public static final Filter insertCard = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.isInserted();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter insertCard() {
        return insertCard;
    }

    /**
     * Filter that accepts cards that are 'probe' cards.
     */
    public static final Filter probeCard = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.isProbeCard();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter probeCard() {
        return probeCard;
    }


    /**
     * Filter that accepts cards that are 'hatred' cards.
     */
    public static final Filter hatredCard = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.isHatredCard();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter hatredCard() {
        return hatredCard;
    }

    /**
     * Filter that accepts cards that are 'liberation' cards.
     */
    public static final Filter liberationCard = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.isLiberationCard();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter liberationCard() {
        return liberationCard;
    }

    /**
     * Filter that accepts cards that are Undercover spies.
     */
    public static final Filter undercover_spy = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.isUndercover();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter undercover_spy() {
        return undercover_spy;
    }

    /**
     * Filter that accepts cards that are 'hit'.
     */
    public static final Filter hit = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.isHit();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter hit() {
        return hit;
    }

    /**
     * Filter that accepts cards that are Disarmed.
     */
    public static final Filter Disarmed = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.isDisarmed();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter Disarmed() {
        return Disarmed;
    }

    /**
     * Filter that accepts cards that are 'crashed'.
     */
    public static final Filter crashed = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.isCrashed();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter crashed() {
        return crashed;
    }

    /**
     * Filter that accepts cards that are 'collapsed'.
     */
    public static final Filter collapsed = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.isCollapsed();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter collapsed() {
        return collapsed;
    }

    /**
     * Filter that accepts cards that are 'damaged'.
     */
    public static final Filter damaged = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.isDamaged();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter damaged() {
        return damaged;
    }

    /**
     * Filter that accepts cards that are face down.
     */
    public static final Filter face_down = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return !physicalCard.isDoubleSided() && (physicalCard.getZone().isFaceDown() != physicalCard.isFlipped());
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter faceDown() {
        return face_down;
    }

    /**
     * Filter that accepts cards that are 'concealed'.
     */
    public static final Filter concealed = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.isConcealed();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter concealed() {
        return concealed;
    }

    /**
     * Filter that accepts cards that are suspended.
     */
    public static final Filter suspended = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.isSuspended();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter suspended() {
        return suspended;
    }

    /**
     * Filter that accepts cards that are race destinies.
     */
    public static final Filter raceDestiny = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.getRaceDestinyForPlayer() != null;
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter raceDestiny() {
        return raceDestiny;
    }

    /**
     * Filter that accepts cards that are race destinies for the specified player.
     *
     * @param playerId the player
     * @return Filter
     */
    public static Filter raceDestinyForPlayer(final String playerId) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return playerId.equals(physicalCard.getRaceDestinyForPlayer());
            }
        };
    }

    /**
     * Filter that accepts cards that are top race destinies.
     */
    public static final Filter topRaceDestiny = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return Filters.topRaceDestinyForPlayer(gameState.getDarkPlayer()).accepts(gameState, modifiersQuerying, physicalCard)
                    || Filters.topRaceDestinyForPlayer(gameState.getLightPlayer()).accepts(gameState, modifiersQuerying, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter topRaceDestiny() {
        return topRaceDestiny;
    }

    /**
     * Filter that accepts cards that are top race for the specified player.
     *
     * @param playerId the player
     * @return Filter
     */
    public static Filter topRaceDestinyForPlayer(final String playerId) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (!playerId.equals(physicalCard.getRaceDestinyForPlayer())) {
                    return false;
                }
                PhysicalCard topRaceDestiny = null;
                for (PhysicalCard stackedCard : physicalCard.getStackedOn().getCardsStacked()) {
                    if (Filters.raceDestinyForPlayer(playerId).accepts(gameState, modifiersQuerying, stackedCard)) {
                        topRaceDestiny = stackedCard;
                    }
                }
                return topRaceDestiny != null && topRaceDestiny.getCardId() == physicalCard.getCardId();
            }
        };
    }

    /**
     * Filter that accepts Podracers that have a race total within a specified range.
     *
     * @param rangeStart the lower limit of the range
     * @param rangeEnd the upper limit of the range
     * @return Filter
     */
    public static Filter podracerWithRaceTotalInRange(final float rangeStart, final float rangeEnd) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (Filters.Podracer.accepts(gameState, modifiersQuerying, physicalCard)) {
                    return false;
                }
                float raceTotal = modifiersQuerying.getPodracerRaceTotal(gameState, physicalCard);
                return rangeStart <= raceTotal && raceTotal <= rangeEnd;
            }
        };
    }


    //
    //
    // Filters for captives and escorts
    //
    //

    /**
     * Filter that accepts cards that are captured starships.
     */
    public static final Filter captured_starship = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.isCapturedStarship();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter capturedStarship() {
        return captured_starship;
    }

    /**
     * Filter that accepts cards that are captives.
     */
    public static final Filter captive = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.isCaptive();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter captive() {
        return captive;
    }

    /**
     * Filter that accepts cards that are escorted captives.
     */
    public static final Filter escortedCaptive = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.isCaptive() && !physicalCard.isImprisoned() && physicalCard.getAttachedTo() != null;
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter escortedCaptive() {
        return escortedCaptive;
    }

    /**
     * Filter that accepts cards that are escorted captives of the specified card.
     *
     * @param card the card
     * @return Filter
     */
    public static Filter escortedBy(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return physicalCard.isCaptive() && !physicalCard.isImprisoned() && physicalCard.getAttachedTo() != null
                        && Filters.sameCardId(physicalCard.getAttachedTo()).accepts(gameState, modifiersQuerying, card);
            }
        };
    }

    /**
     * Filter that accepts cards that are escorted captives of a card that is accepted by the specified filter.
     *
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter escortedBy(PhysicalCard source, final Filter filters) {
        return escortedBy(source, null, filters);
    }

    /**
     * Filter that accepts cards that are escorted captives of a card that is accepted by the specified filter.
     *
     * @param source the card that is performing this query
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @param filters the filters
     * @return Filter
     */
    public static Filter escortedBy(PhysicalCard source, final Map<InactiveReason, Boolean> spotOverrides, final Filter filters) {
        final Integer permCardId = source.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard source = gameState.findCardByPermanentId(permCardId);
                Collection<PhysicalCard> cards = Filters.filterActive(gameState.getGame(), source, spotOverrides, filters);

                for (PhysicalCard card : cards) {
                    if (Filters.escortedBy(card).accepts(gameState, modifiersQuerying, physicalCard))
                        return true;
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that are 'imprisoned'.
     */
    public static final Filter imprisoned = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.isImprisoned();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter imprisoned() {
        return imprisoned;
    }

    /**
     * Filter that accepts cards that are 'imprisoned' in the specified prison card.
     *
     * @param card the prison
     * @return Filter
     */
    public static Filter imprisonedIn(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return physicalCard.isImprisoned() && physicalCard.getAttachedTo() != null
                        && Filters.sameCardId(physicalCard.getAttachedTo()).accepts(gameState, modifiersQuerying, card);
            }
        };
    }

    /**
     * Filter that accepts cards that are 'imprisoned' in a prison card accepted by the specified filter.
     *
     * @param filters the filters
     * @return Filter
     */
    public static Filter imprisonedIn(final Filter filters) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (!physicalCard.isImprisoned())
                    return false;

                Collection<PhysicalCard> prisons = Filters.filterTopLocationsOnTable(gameState.getGame(), filters);

                for (PhysicalCard prison : prisons) {
                    if (Filters.imprisonedIn(prison).accepts(gameState, modifiersQuerying, physicalCard))
                        return true;
                }

                return false;
            }
        };
    }


    /**
     * Filter that accepts cards that are 'frozen' captives.
     */
    public static final Filter frozenCaptive = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.isFrozen();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter frozenCaptive() {
        return frozenCaptive;
    }

    /**
     * Filter that accepts cards that are non-frozen captives.
     */
    public static final Filter nonFrozenCaptive = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.isCaptive() && !physicalCard.isFrozen();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter nonFrozenCaptive() {
        return nonFrozenCaptive;
    }

    /**
     * Filter that accepts cards that are non-frozen captives.
     */
    public static final Filter unattendedFrozenCaptive = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.isFrozen() && !physicalCard.isImprisoned() && physicalCard.getAtLocation() != null;
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter unattendedFrozenCaptive() {
        return unattendedFrozenCaptive;
    }

    /**
     * Filter that accepts cards that are escorting captives.
     */
    public static final Filter escort = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return !gameState.getCaptivesOfEscort(physicalCard).isEmpty();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter escort() {
        return escort;
    }

    /**
     * Filter that accepts cards that are escorting the specified captive.
     *
     * @param captive the captive
     * @return Filter
     */
    public static Filter escorting(PhysicalCard captive) {
        return Filters.escorting(Filters.sameCardId(captive));
    }

    /**
     * Filter that accepts cards that are escorting a captive accepted by the captive filter.
     *
     * @param captiveFilter the captive filter
     * @return Filter
     */
    public static Filter escorting(final Filter captiveFilter) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return !Filters.filterCount(gameState.getCaptivesOfEscort(physicalCard), gameState.getGame(), 1, captiveFilter).isEmpty();
            }
        };
    }

    /**
     * Filter that accepts cards that can escort the specified captive card.
     *
     * @param captive the captive
     * @return Filter
     */
    public static Filter canEscortCaptive(PhysicalCard captive) {
        return canEscortCaptive(captive, false);
    }

    /**
     * Filter that accepts cards that can escort the specified captive card.
     *
     * @param captive the captive
     * @param skipWarriorCheck true if checking that escort is a warrior, etc. is skipped, otherwise false
     * @return Filter
     */
    public static Filter canEscortCaptive(PhysicalCard captive, final boolean skipWarriorCheck) {
        final Integer permCaptiveCardId = captive.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard captive = gameState.findCardByPermanentId(permCaptiveCardId);
                return !physicalCard.isLeavingTable() && modifiersQuerying.canEscortCaptive(gameState, physicalCard, captive, skipWarriorCheck);
            }
        };
    }

    /**
     * Filter that accepts cards that can be escorted by the specified escort card.
     *
     * @param escort the escort
     * @return Filter
     */
    public static Filter canBeEscortedBy(PhysicalCard escort) {
        final Integer permEscortCardId = escort.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard escort = gameState.findCardByPermanentId(permEscortCardId);
                return !escort.isLeavingTable() && modifiersQuerying.canEscortCaptive(gameState, escort, physicalCard, false);
            }
        };
    }

    /**
     * Filter that accepts cards that can escort a captive currently escorted by another escort present with it.
     *
     * @param source the card that is performing this query
     * @return Filter
     */
    public static Filter canEscortCaptiveCurrentlyEscortedByPresentWith(PhysicalCard source) {
        final Integer permSourceCardId = source != null ? source.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getOwner().equals(gameState.getLightPlayer())
                        || physicalCard.getBlueprint().getCardCategory() != CardCategory.CHARACTER) {
                    return false;
                }

                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);

                // New escort needs to be able to move to transfer captive
                if (!modifiersQuerying.mayNotBeTransferred(gameState, physicalCard)) {
                    for (PhysicalCard escort : Filters.filterActive(gameState.getGame(), source, Filters.escort())) {
                        // Old escort needs to be able to move to transfer captive
                        if (!modifiersQuerying.mayNotMove(gameState, escort)) {
                            Collection<PhysicalCard> captives = gameState.getCaptivesOfEscort(escort);
                            for (PhysicalCard captive : captives) {
                                // Captive needs to be able to move to transfer
                                if (!modifiersQuerying.mayNotMove(gameState, captive)) {
                                    if (Filters.and(Filters.canEscortCaptive(captive), Filters.presentWith(escort)).accepts(gameState, modifiersQuerying, physicalCard)) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts captives that are not prohibited from being transferred.
     */
    public static final Filter captiveNotProhibitedFromBeingTransferred = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return !modifiersQuerying.mayNotBeTransferred(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter captiveNotProhibitedFromBeingTransferred() {
        return captiveNotProhibitedFromBeingTransferred;
    }

    /**
     * Filter that accepts cards that can release captives.
     */
    public static final Filter canReleaseCaptives = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.getBlueprint().hasKeyword(Keyword.CAN_RELEASE_CAPTIVES);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter canReleaseCaptives() {
        return canReleaseCaptives;
    }

    //
    //
    // Filters for Jedi Testing
    //
    //

    /**
     * Filter that accepts cards that may attempt the specified Jedi Test.
     *
     * @param jediTest the Jedi Test
     * @return Filter
     */
    public static Filter mayAttemptJediTest(PhysicalCard jediTest) {
        final Integer permJediTestCardId = jediTest.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard jediTest = gameState.findCardByPermanentId(permJediTestCardId);

                PhysicalCard apprentice = jediTest.getTargetedCard(gameState, TargetId.JEDI_TEST_APPRENTICE);
                if (apprentice != null && Filters.sameCardId(physicalCard).accepts(gameState, modifiersQuerying, apprentice)) {
                    return !modifiersQuerying.mayNotAttemptJediTests(gameState, physicalCard);
                }
                return false;
            }
        };
    }

    /**
     * Filter that accepts Jedi Tests targeting a mentor accepted by the filter.
     *
     * @param mentorFilter the attempted by filter
     * @return Filter
     */
    public static Filter jediTestTargetingMentor(final Filter mentorFilter) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.JEDI_TEST)
                    return false;

                PhysicalCard mentor = physicalCard.getTargetedCard(gameState, TargetId.JEDI_TEST_MENTOR);
                return mentor != null && Filters.and(mentorFilter).accepts(gameState, modifiersQuerying, mentor);
            }
        };
    }

    /**
     * Filter that accepts Jedi Tests targeting an apprentice accepted by the filter.
     *
     * @param apprenticeFilter the apprentice filter
     * @return Filter
     */
    public static Filter jediTestTargetingApprentice(final Filter apprenticeFilter) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.JEDI_TEST)
                    return false;

                PhysicalCard apprentice = physicalCard.getTargetedCard(gameState, TargetId.JEDI_TEST_APPRENTICE);
                return apprentice != null && Filters.and(apprenticeFilter).accepts(gameState, modifiersQuerying, apprentice);
            }
        };
    }

    /**
     * Filter that accepts cards that are mentors.
     */
    public static final Filter mentor = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return mentorTargetedByJediTest(Filters.any).accepts(gameState, modifiersQuerying, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter mentor() {
        return mentor;
    }

    /**
     * Filter that accepts mentors that are targeted by the specified Jedi Test.
     *
     * @param jediTest the Jedi Test
     * @return Filter
     */
    public static Filter mentorTargetedByJediTest(PhysicalCard jediTest) {
        final Integer permJediTestCardId = jediTest.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard jediTest = gameState.findCardByPermanentId(permJediTestCardId);

                PhysicalCard mentor = jediTest.getTargetedCard(gameState, TargetId.JEDI_TEST_MENTOR);
                return mentor != null && Filters.samePermanentCardId(physicalCard).accepts(gameState, modifiersQuerying, mentor);
            }
        };
    }

    /**
     * Filter that accepts mentors that are targeted by a Jedi Test accepted by the Jedi Test filter.
     *
     * @param jediTestFilter the Jedi Test filter
     * @return Filter
     */
    public static Filter mentorTargetedByJediTest(final Filter jediTestFilter) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                Collection<PhysicalCard> jediTests = Filters.filterActive(gameState.getGame(), null, Filters.and(Filters.Jedi_Test, jediTestFilter));
                for (PhysicalCard jediTest : jediTests) {
                    PhysicalCard mentor = jediTest.getTargetedCard(gameState, TargetId.JEDI_TEST_MENTOR);
                    if (mentor != null && Filters.sameCardId(physicalCard).accepts(gameState, modifiersQuerying, mentor)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that are apprentices.
     */
    public static final Filter apprentice = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            if (physicalCard.getBlueprint().getCardCategory() != CardCategory.CHARACTER)
                return false;

            if (gameState.isApprentice(physicalCard))
                return true;

            return apprenticeTargetedByJediTest(Filters.any).accepts(gameState, modifiersQuerying, physicalCard);

        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter apprentice() {
        return apprentice;
    }

    /**
     * Filter that accepts apprentices that are targeted by the specified Jedi Test.
     *
     * @param jediTest the Jedi Test
     * @return Filter
     */
    public static Filter apprenticeTargetedByJediTest(PhysicalCard jediTest) {
        final Integer permJediTestCardId = jediTest.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard jediTest = gameState.findCardByPermanentId(permJediTestCardId);

                PhysicalCard apprentice = jediTest.getTargetedCard(gameState, TargetId.JEDI_TEST_APPRENTICE);
                return apprentice != null && Filters.samePermanentCardId(physicalCard).accepts(gameState, modifiersQuerying, apprentice);
            }
        };
    }

    /**
     * Filter that accepts apprentices that are targeted by a Jedi Test accepted by the Jedi Test filter.
     *
     * @param jediTestFilter the Jedi Test filter
     * @return Filter
     */
    public static Filter apprenticeTargetedByJediTest(final Filter jediTestFilter) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                Collection<PhysicalCard> jediTests = Filters.filterActive(gameState.getGame(), null, Filters.and(Filters.Jedi_Test, jediTestFilter));
                for (PhysicalCard jediTest : jediTests) {
                    PhysicalCard apprentice = jediTest.getTargetedCard(gameState, TargetId.JEDI_TEST_APPRENTICE);
                    if (apprentice != null && Filters.sameCardId(physicalCard).accepts(gameState, modifiersQuerying, apprentice)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that are targeted by the specified completed Jedi Test.
     *
     * @param jediTest the Jedi Test
     * @return Filter
     */
    public static Filter targetedByCompletedJediTest(PhysicalCard jediTest) {
        final Integer permJediTestCardId = jediTest.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard jediTest = gameState.findCardByPermanentId(permJediTestCardId);

                if (!Filters.completed_Jedi_Test.accepts(gameState, modifiersQuerying, jediTest))
                    return false;

                return jediTest.getTargetedCards(gameState).values().contains(physicalCard);
            }
        };
    }

    /**
     * Filter that accepts cards that are targeted by the specified uncompleted Jedi Test.
     *
     * @param jediTest the Jedi Test
     * @return Filter
     */
    public static Filter targetedByUncompletedJediTest(PhysicalCard jediTest) {
        final Integer permJediTestCardId = jediTest.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard jediTest = gameState.findCardByPermanentId(permJediTestCardId);

                if (!Filters.uncompleted_Jedi_Test.accepts(gameState, modifiersQuerying, jediTest))
                    return false;

                return jediTest.getTargetedCards(gameState).values().contains(physicalCard);
            }
        };
    }

    /**
     * Filter that accepts cards that are completed Jedi Tests.
     */
    public static final Filter completed_Jedi_Test = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return Filters.Jedi_Test.accepts(gameState, modifiersQuerying, physicalCard) && physicalCard.getJediTestStatus() == JediTestStatus.COMPLETED;
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter completed_Jedi_Test() {
        return completed_Jedi_Test;
    }

    /**
     * Filter that accepts cards that are uncompleted Jedi Tests.
     */
    public static final Filter uncompleted_Jedi_Test = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return Filters.Jedi_Test.accepts(gameState, modifiersQuerying, physicalCard) && physicalCard.getJediTestStatus() != JediTestStatus.COMPLETED;
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter uncompleted_Jedi_Test() {
        return uncompleted_Jedi_Test;
    }

    /**
     * Filter that accepts cards that are stacked via Jedi Test #5.
     */
    public static final Filter stackedViaJediTest5 = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.isStackedAsViaJediTest5();

        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter stackedViaJediTest5() {
        return stackedViaJediTest5;
    }


    //
    //
    // Filters for 'blown away'
    //
    //

    /**
     * Filter that accepts locations that are 'blown away'.
     */
    public static final Filter blown_away = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.isBlownAway();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter blown_away() {
        return blown_away;
    }

    //
    //
    // Filters for missing and search parties
    //
    //

    /**
     * Filter that accepts cards that are 'missing'.
     */
    public static final Filter missing = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.isMissing();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter missing() {
        return missing;
    }

    /**
     * Filter that accepts cards that can can join Search Party at the specified site for the specified player.
     *
     * @param playerId the player
     * @param site the site
     * @return Filter
     */
    public static Filter canJoinSearchPartyAt(final String playerId, PhysicalCard site) {
        final Integer permSiteCardId = site.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (!physicalCard.getOwner().equals(playerId))
                    return false;

                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.CHARACTER)
                    return false;

                if (modifiersQuerying.cannotJoinSearchParty(gameState, physicalCard))
                    return false;

                PhysicalCard site = gameState.findCardByPermanentId(permSiteCardId);
                PhysicalCard siteCardIsAt = modifiersQuerying.getLocationThatCardIsAt(gameState, physicalCard);
                if (siteCardIsAt == null || !Filters.sameCardId(site).accepts(gameState, modifiersQuerying, siteCardIsAt))
                    return false;

                return true;
            }
        };
    }

    //
    //
    // Filters for permanent weapons.
    //
    //

    /**
     * Filter that accepts cards that are character weapons (or permanent weapons of a character).
     */
    public static final Filter character_weapon = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return Filters.and(CardType.WEAPON, CardSubtype.CHARACTER).accepts(gameState, modifiersQuerying, physicalCard);
        }
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
            return Filters.permanentWeaponOf(Filters.character).accepts(gameState, modifiersQuerying, builtInCardBlueprint);
        }
    };

    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter character_weapon() {
        return character_weapon;
    }

    /**
     * Filter that accepts cards that have a permanent weapon.
     */
    public static final Filter hasPermanentWeapon = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.getPermanentWeapon(gameState, physicalCard) != null;
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter hasPermanentWeapon() {
        return hasPermanentWeapon;
    }

    /**
     * Filter that accepts cards that have a permanent blaster.
     */
    public static final Filter hasPermanentBlaster = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            SwccgBuiltInCardBlueprint permWeapon = modifiersQuerying.getPermanentWeapon(gameState, physicalCard);
            return permWeapon != null && permWeapon.hasKeyword(Keyword.BLASTER);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter hasPermanentBlaster() {
        return hasPermanentBlaster;
    }

    /**
     * Filter that accepts cards that have a permanent non-unique blaster.
     */
    public static final Filter hasPermanentNonuniqueBlaster = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            SwccgBuiltInCardBlueprint permWeapon = modifiersQuerying.getPermanentWeapon(gameState, physicalCard);
            return permWeapon != null && permWeapon.hasKeyword(Keyword.BLASTER)
                    && (permWeapon.getUniqueness() == null || permWeapon.getUniqueness() != Uniqueness.UNIQUE);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter hasPermanentNonuniqueBlaster() {
        return hasPermanentNonuniqueBlaster;
    }

    /**
     * Filter that accepts cards that have a permanent lightsaber.
     */
    public static final Filter hasPermanentLightsaber = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            SwccgBuiltInCardBlueprint permWeapon = modifiersQuerying.getPermanentWeapon(gameState, physicalCard);
            return permWeapon != null && permWeapon.hasKeyword(Keyword.LIGHTSABER);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter hasPermanentLightsaber() {
        return hasPermanentLightsaber;
    }

    //
    //
    // Filters for cards that specify another card in its "whileInPlay" data.
    //
    //

    /**
     * Filter that accepts cards that are in the "whileInPlay" data of the specified card.
     *
     * @param card the card
     * @return Filter
     */
    public static Filter isInCardInPlayData(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);

                PhysicalCard cardInData = card.getWhileInPlayData() != null ? card.getWhileInPlayData().getPhysicalCard() : null;
                if (cardInData != null) {
                    return Filters.sameCardId(physicalCard).accepts(gameState, modifiersQuerying, cardInData);
                }
                return false;
            }
        };
    }

    //
    //
    // Filters for cards targeting other cards.
    //
    //

    /**
     * Filter that accepts cards that are targeted by the current card being played as the specified target id.
     * @param source the card performing the query
     * @return Filter
     */
    public static Filter targetedByCardBeingPlayed(PhysicalCard source) {
        final Integer permSourceCardId = source.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);

                PlayCardState topPlayCardState = gameState.getTopPlayCardState(source);

                return topPlayCardState != null && TargetingActionUtils.isTargeting(gameState.getGame(), topPlayCardState.getPlayCardAction(), Filters.samePermanentCardId(physicalCard));
            }
        };
    }

    /**
     * Filter that accepts cards that are targeted by the specified card on table.
     * Note: This should not be used within a condition/modifier as it can cause a loop.
     * @param card the card
     * @return Filter
     */
    public static Filter targetedByCardOnTable(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);

                if (!Filters.onTable.accepts(gameState, modifiersQuerying, physicalCard))
                    return false;

                List<PhysicalCard> cardsTargetingCard = modifiersQuerying.getCardsOnTableTargetingCard(gameState, physicalCard);
                return !cardsTargetingCard.isEmpty() && cardsTargetingCard.contains(card);
            }
        };
    }

    /**
     * Filter that accepts cards that are targeted by card on table accepted by the specified filter.
     * Note: This should not be used within a condition/modifier as it can cause a loop.
     * @param filters the filters
     * @return Filter
     */
    public static Filter targetedByCardOnTable(final Filter filters) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return Filters.canSpot(modifiersQuerying.getCardsOnTableTargetingCard(gameState, physicalCard), gameState.getGame(), filters);
            }
        };
    }

    /**
     * Filter that accepts cards that are targeted by the specified card on table as the specified target id.
     *
     * @param card the card
     * @param targetId the target id
     * @return Filter
     */
    public static Filter targetedByCardOnTableAsTargetId(PhysicalCard card, final TargetId targetId) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);

                if (!Filters.onTable.accepts(gameState, modifiersQuerying, physicalCard))
                    return false;

                PhysicalCard targetedCard = card.getTargetedCard(gameState, targetId);
                return targetedCard != null && Filters.samePermanentCardId(physicalCard).accepts(gameState, modifiersQuerying, targetedCard);
            }
        };
    }

    /**
     * Filter that accepts cards being played that are targeting the specified card.
     */
    public static Filter cardBeingPlayedTargeting(PhysicalCard source, PhysicalCard card) {
        return cardBeingPlayedTargeting(source, Filters.samePermanentCardId(card));
    }

    /**
     * Filter that accepts cards being played that are targeting a card accepted by the specified filter.
     * @param source the card performing the query
     * @param filters the filters
     */
    public static Filter cardBeingPlayedTargeting(PhysicalCard source, final Filter filters) {
        final Integer permSourceCardId = source.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);
                PlayCardState topPlayCardState = gameState.getTopPlayCardState(source);
                if (topPlayCardState == null)
                    return false;

                if (physicalCard.getPermanentCardId() != topPlayCardState.getPlayCardAction().getPlayedCard().getPermanentCardId()
                        && (topPlayCardState.getPlayCardAction().getOtherPlayedCard() == null
                        || physicalCard.getPermanentCardId() != topPlayCardState.getPlayCardAction().getOtherPlayedCard().getPermanentCardId())) {
                    return false;
                }

                return !TargetingActionUtils.getCardsTargeted(gameState.getGame(), topPlayCardState.getPlayCardAction(), filters).isEmpty();
            }
        };
    }

    /**
     * Filter that accepts cards on table that are targeting the specified card.
     */
    public static Filter cardOnTableTargeting(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);

                List<PhysicalCard> cardsTargetingCard = modifiersQuerying.getCardsOnTableTargetingCard(gameState, card);
                return cardsTargetingCard.contains(physicalCard);
            }
        };
    }

    /**
     * Filter that accepts cards that are targeted by card on table accepted by the specified filter.
     * @param filters the filters
     * @return Filter
     */
    public static Filter cardOnTableTargeting(final Filter filters) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                for (PhysicalCard cardToCheck : Filters.filterAllOnTable(gameState.getGame(), filters)) {
                    if (modifiersQuerying.getCardsOnTableTargetingCard(gameState, cardToCheck).contains(physicalCard)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    /**
     * Filter that accepts cards on table or being played that are targeting the specified card.
     */
    public static Filter cardTargeting(PhysicalCard source, PhysicalCard card) {
        return Filters.or(Filters.cardBeingPlayedTargeting(source, card), Filters.cardOnTableTargeting(card));
    }

    /**
     * Filter that accepts cards that are targeted by card on table or being played accepted by the specified filter.
     * @param source the card performing the query
     * @param filters the filters
     * @return Filter
     */
    public static Filter cardTargeting(PhysicalCard source, final Filter filters) {
        return Filters.or(Filters.cardBeingPlayedTargeting(source, filters), Filters.cardOnTableTargeting(filters));
    }

    // Gets a filter representing the Utinni Effects that could target a card represented by the input filters.
    public static Filter utinniEffectThatCanTarget(final Filter filters) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {


                // TODO: Fix this

                SwccgCardBlueprint blueprint = physicalCard.getBlueprint();
                if (blueprint.getCardSubtype()!=CardSubtype.UTINNI)
                    return false;
                if (physicalCard.getUtinniEffectStatus() != UtinniEffectStatus.REACHED)
                    return true;

                return false;

                /* TODO: Fix this


                Filter validTargetFilter = blueprint.getValidUtinniEffectTargetFilter(physicalCard.getOwner(), gameState.getGame(), physicalCard);
                return Filters.canSpot(gameState, modifiersQuerying, Filters.and(filters, validTargetFilter));
                */
            }
        };
    }

    /**
     * Filter that accepts locations at which Elis Helrot and Nabrun Leids are not prevented from being used.
     * @param card the card to use to transport
     * @return Filter
     */
    public static Filter notProhibitedFromUsingCardToTransportToOrFromLocation(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return Filters.location.accepts(gameState, modifiersQuerying, physicalCard)
                        && !modifiersQuerying.prohibitedFromUsingCardToTransportToOrFromLocation(gameState, card, physicalCard);
            }
        };
    }

    /**
     * Filter that accepts cards that are allowed to make a Kessel Run even when not a smuggler.
     */
    public static final Filter mayMakeKesselRunInsteadOfSmuggler = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return Filters.character.accepts(gameState, modifiersQuerying, physicalCard)
                    && modifiersQuerying.isAllowedToMakeKesselRunWhenNotSmuggler(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter mayMakeKesselRunInsteadOfSmuggler() {
        return mayMakeKesselRunInsteadOfSmuggler;
    }


    /**
     * Filter that accepts cards that are allowed a Kessel Run being made from Ralltiir.
     */
    public static final Filter kesselRunFromRalltiir = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            if (!Filters.Kessel_Run.accepts(gameState, modifiersQuerying, physicalCard)) {
                return false;
            }
            PhysicalCard cardInData = physicalCard.getWhileInPlayData() != null ? physicalCard.getWhileInPlayData().getPhysicalCard() : null;
            if (cardInData != null) {
                return Filters.Ralltiir_system.accepts(gameState, modifiersQuerying, cardInData);
            }
            return false;
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter kesselRunFromRalltiir() {
        return kesselRunFromRalltiir;
    }


    /**
     * Filter that accepts cards that have 'Asteroid Rules' in effect.
     */
    public static final Filter asteroidRulesInEffect = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.getBlueprint().isSpecialRuleInEffectHere(SpecialRule.ASTEROID_RULES, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter asteroidRulesInEffect() {
        return asteroidRulesInEffect;
    }



    //
    //
    // Filters for Stolen Data Tapes.
    //
    //

    /**
     * Filter that accepts cards that are 'delivered' Stolen Data Tapes.
     */
    public static final Filter delivered_Stolen_Data_Tapes = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return Filters.Stolen_Data_Tapes.accepts(gameState, modifiersQuerying, physicalCard) && physicalCard.getZone() == Zone.SIDE_OF_TABLE;
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter deliveredStolenDataTapes() {
        return delivered_Stolen_Data_Tapes;
    }


    // Gets a filter representing the cards that are related locations (even when not in play) to the input card.
    // Or related locations (even when not in play) to the location the card is "at" if the input card is not a location.
    // For example, this can be used to find a "related location" from Reserve deck.


    /**
     * Filters that accepts cards that are related locations (even when not in play) to the input card.
     * Or related locations (even when not in play) to the location the card is "at" if the input card is not a location.
     * For example, this can be used to find a "related location" from Reserve deck.
     * @param card the card
     * @return Filter
     */
    public static Filter relatedLocationEvenWhenNotInPlay(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION)
                    return false;

                PhysicalCard card = gameState.findCardByPermanentId(permCardId);

                PhysicalCard location = modifiersQuerying.getLocationHere(gameState, card);
                if (location == null)
                    return false;

                // If location is unique, then location with same title is not considered related.
                if (!Filters.other(location).accepts(gameState, modifiersQuerying, physicalCard))
                    return false;

                // Check if locations are part of the same system
                if (Filters.partOfSystem(location.getPartOfSystem()).accepts(gameState, modifiersQuerying, physicalCard))
                    return true;

                return false;
            }
        };
    }

    /**
     * Filters that accepts locations where cards with different card titles that are accepted by the specified filter are
     * "at" that location.
     * @param source the card that is performing this query
     * @param filters the filters
     * @return Filter
     */
    public static Filter hasDifferentCardTitlesAtLocation(PhysicalCard source, final Filter filters) {
        final Integer permSourceCardId = source != null ? source.getPermanentCardId() : null;
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION)
                    return false;

                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);

                Collection<PhysicalCard> cardsAtLocation = Filters.filterActive(gameState.getGame(), source, Filters.and(filters, Filters.at(physicalCard)));
                for (PhysicalCard cardAtLocation : cardsAtLocation) {
                    if (!Filters.filterCount(cardsAtLocation, gameState.getGame(), 1, Filters.not(Filters.sameTitle(cardAtLocation))).isEmpty()) {
                        return true;
                    }
                }

                return false;
            }
        };
    }


    /**
     * Filter that accepts the current battle location where player can have power added in battle due to starships
     * controlling the related system.
     *
     * @param playerId the player
     * @return Filter
     */
    public static Filter locationWherePowerCanBeAddedInBattleFromStarshipsControllingSystem(final String playerId) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION)
                    return false;

                BattleState battleState = gameState.getBattleState();
                if (battleState==null || !Filters.sameCardId(battleState.getBattleLocation()).accepts(gameState, modifiersQuerying, physicalCard))
                    return false;

                // Check Hoth Energy Shield
                if (playerId.equals(gameState.getDarkPlayer())
                        && modifiersQuerying.isLocationUnderHothEnergyShield(gameState, physicalCard))
                    return false;

                return true;
            }
        };
    }


    //
    //
    // This section defines Filters used for movement and deploying.
    //
    //


    // Gets a filter representing the locations that the specified collection of "just lost" cards can all be relocated to.


    /**
     * Filter that accepts cards whose uniqueness on table (or out of play) has not been reached.
     */
    public static final Filter isUniquenessOnTableNotReached = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return !modifiersQuerying.isUniquenessOnTableLimitReached(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter isUniquenessOnTableNotReached() {
        return isUniquenessOnTableNotReached;
    }

    /**
     * Filter that accepts characters, devices, starships, and weapons that can be placed at a location accepted by the specified filter.
     * @param locationFilter the filter
     * @return Filter
     */
    public static Filter canBePlacedAtLocation(final Filter locationFilter) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (!Filters.or(Filters.character, Filters.device, Filters.starship, Filters.weapon).accepts(gameState, modifiersQuerying, physicalCard))
                    return false;

                if (modifiersQuerying.isUniquenessOnTableLimitReached(gameState, physicalCard))
                    return false;

                return Filters.canSpot(gameState.getGame(), physicalCard, TargetingReason.TO_BE_DEPLOYED_ON,
                        Filters.and(Filters.locationAndCardsAtLocation(locationFilter), physicalCard.getBlueprint().getValidPlaceCardTargetFilter(gameState.getGame(), physicalCard)));
            }
        };
    }

    // Gets a filter representing the locations that the specified collection of "just lost" cards can all be relocated to.
    public static Filter locationSelectedLostCardsCanAllRelocateTo(final String playerId, final SwccgGame game, Collection<PhysicalCard> lostCards) {
        final List<Integer> permLostCardIds = new LinkedList<Integer>();
        for (PhysicalCard lostCard : lostCards) {
            permLostCardIds.add(lostCard.getPermanentCardId());
        }
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory()!=CardCategory.LOCATION || permLostCardIds.isEmpty())
                    return false;

                // Check for "Dagobah"
                if (Filters.Dagobah_location.accepts(gameState, modifiersQuerying, physicalCard))
                    return false;

                if (Filters.AhchTo_location.accepts(gameState, modifiersQuerying, physicalCard))
                    return false;

                // Check for "Hoth Energy Shield"
                if (playerId.equals(gameState.getDarkPlayer())
                        && modifiersQuerying.isLocationUnderHothEnergyShield(gameState, physicalCard))
                    return false;

                final List<PhysicalCard> lostCards = new LinkedList<PhysicalCard>();
                for (Integer permLostCardId : permLostCardIds) {
                    lostCards.add(gameState.findCardByPermanentId(permLostCardId));
                }

                // Check if all selected cards can move to that location
                for (PhysicalCard lostCard : lostCards) {
                    if (game.getModifiersQuerying().mayNotMove(game.getGameState(), lostCard)
                            || !lostCard.getBlueprint().getValidMoveTargetFilter(playerId, game, lostCard, false).accepts(gameState, modifiersQuerying, physicalCard))
                        return false;
                }

                return true;
            }
        };
    }

    // Gets a filter representing the starships that the specified collection of "just lost" cards can all be relocated to.
    public static Filter starshipSelectedLostCardsCanAllRelocateTo(final String playerId, final SwccgGame game, Collection<PhysicalCard> lostCards) {
        final List<Integer> permLostCardIds = new LinkedList<Integer>();
        for (PhysicalCard lostCard : lostCards) {
            permLostCardIds.add(lostCard.getPermanentCardId());
        }
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardCategory()!=CardCategory.STARSHIP || permLostCardIds.isEmpty())
                    return false;

                // Check for "Dagobah"
                if (Filters.at(Filters.Dagobah_location).accepts(gameState, modifiersQuerying, physicalCard))
                    return false;

                if (Filters.AhchTo_location.accepts(gameState, modifiersQuerying, physicalCard))
                    return false;

                // Check for "Hoth Energy Shield"
                if (playerId.equals(gameState.getDarkPlayer())
                        && modifiersQuerying.isLocationUnderHothEnergyShield(gameState, modifiersQuerying.getLocationThatCardIsAt(gameState, physicalCard)))
                    return false;

                final List<PhysicalCard> lostCards = new LinkedList<PhysicalCard>();
                for (Integer permLostCardId : permLostCardIds) {
                    lostCards.add(gameState.findCardByPermanentId(permLostCardId));
                }

                // Check if all selected cards (only characters, devices, and weapons allowed) can relocate to capital starship
                for (PhysicalCard lostCard : lostCards) {
                    if (lostCard.getBlueprint().getCardCategory()!=CardCategory.CHARACTER
                            && lostCard.getBlueprint().getCardCategory()!=CardCategory.DEVICE
                            && lostCard.getBlueprint().getCardCategory()!=CardCategory.WEAPON)
                        return false;

                    if (game.getModifiersQuerying().mayNotMove(game.getGameState(), lostCard)
                            || !lostCard.getBlueprint().getValidMoveTargetFilter(playerId, game, lostCard, false).accepts(gameState, modifiersQuerying, physicalCard))
                        return false;
                }

                // Check if there is capacity for all the selected cards aboard the starship
                if (!modifiersQuerying.hasCapacityForCardsToRelocate(gameState, physicalCard, lostCards))
                    return false;

                return true;
            }
        };
    }

    /**
     * Filter that accepts the card that can move as a 'react' by an action from a specified source card.
     *
     * @param sourceCard the source card
     * @param forFree true if the movement is to be free, otherwise false
     * @param asReactAway true if a 'react' away, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return Filter
     */
    public static Filter canMoveAsReactAsActionFromOtherCard(PhysicalCard sourceCard, boolean forFree, float changeInCost, boolean asReactAway) {
        return canMoveAsReactAsActionFromOtherCard(sourceCard, forFree, changeInCost, asReactAway, Filters.any);
    }

    /**
     * Filter that accepts the card that can move as a 'react' by an action from a specified source card to a location
     * accepted by the move target filter.
     *
     * @param sourceCard the source card
     * @param forFree true if the movement is to be free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param asReactAway true if a 'react' away, otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return Filter
     */
    public static Filter canMoveAsReactAsActionFromOtherCard(PhysicalCard sourceCard, final boolean forFree, final float changeInCost, final boolean asReactAway, final Filter moveTargetFilter) {
        final Integer permSourceCardId = sourceCard.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);

                // Check if card can deploy as a 'react' (performed by this card)
                return physicalCard.getBlueprint().getMoveAsReactAction(physicalCard.getOwner(), gameState.getGame(), physicalCard,
                        new ReactActionOption(sourceCard, forFree, changeInCost, asReactAway, null, physicalCard, moveTargetFilter, null, false), moveTargetFilter) != null;
            }
        };
    }

    /**
     * Filter that accepts the card that can join the move as a 'react'.
     */
    public static final Filter isCardEligibleToJoinMoveAsReact = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            MoveAsReactState moveAsReactState = gameState.getMoveAsReactState();
            return moveAsReactState != null && modifiersQuerying.isCardEligibleToJoinMoveAsReact(moveAsReactState.getMovingAsReactEffect().getAction().getPerformingPlayer(),
                    gameState, moveAsReactState.getReactActionOption().getSource(), physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter isCardEligibleToJoinMoveAsReact() {
        return isCardEligibleToJoinMoveAsReact;
    }

    /**
     * Filter that accepts cards that are not prevented from participating in a 'react'.
     */
    public static final Filter notPreventedFromParticipatingInReact = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return !modifiersQuerying.isProhibitedFromParticipatingInReact(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter notPreventedFromParticipatingInReact() {
        return notPreventedFromParticipatingInReact;
    }

    /**
     * Filter that accepts cards that are not prevented from moving.
     */
    public static final Filter notPreventedFromMoving = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return !modifiersQuerying.mayNotMove(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter notPreventedFromMoving() {
        return notPreventedFromMoving;
    }

    /**
     * Filter that accepts cards deploy and move like a starfighter.
     */
    public static final Filter deploysAndMovesLikeStarfighter = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.isDeploysAndMovesLikeStarfighter(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter deploysAndMovesLikeStarfighter() {
        return deploysAndMovesLikeStarfighter;
    }

    /**
     * Filter that accepts cards deploy and move like a starfighter at cloud sectors.
     */
    public static final Filter deploysAndMovesLikeStarfighterAtCloudSectors = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.isDeploysAndMovesLikeStarfighterAtCloudSectors(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter deploysAndMovesLikeStarfighterAtCloudSectors() {
        return deploysAndMovesLikeStarfighterAtCloudSectors;
    }

    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter movesLikeCharacter() {
        return movesLikeCharacter;
    }

    /**
     * Filter that accepts cards that move like a character.
     */
    public static final Filter movesLikeCharacter = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.getBlueprint().isMovesLikeCharacter();
        }
    };

    /**
     * Filter that accepts cards deploy and move like an undercover spy.
     */
    public static final Filter deploysAndMovesLikeUndercoverSpy = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.isDeploysAndMovesLikeUndercoverSpy(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter deploysAndMovesLikeUndercoverSpy() {
        return deploysAndMovesLikeUndercoverSpy;
    }

    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter hasNotPerformedRegularMove() {
        return hasNotPerformedRegularMove;
    }

    /**
     * Filter that accepts cards that have not performed a regular move this turn.
     */
    public static final Filter hasNotPerformedRegularMove = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return !modifiersQuerying.hasPerformedRegularMoveThisTurn(physicalCard);
        }
    };

    /**
     * Gets a filter representing the cards that are physically at the specified location
     * (and not attached to something else at those locations).
     * This is mainly used when determining cards that "move" from a location.
     * If you want a filter for cards "at" a location in the rules sense of the term, use the cardsAtLocation() method instead.
     * @param location the location
     */
    public static Filter atLocation(PhysicalCard location) {
        return Filters.atLocation(Filters.sameCardId(location));
    }

    /**
     * Gets a filter representing the cards that are physically at locations
     * (and not attached to something else at those locations) that fit the input filters.
     * This is mainly used when determining cards that "move" from a location.
     * If you want a filter for cards "at" a location in the rules sense of the term, use the cardsAtLocation() method instead.
     * @param locationFilter the filter
     */
    public static Filter atLocation(final Filter locationFilter) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return physicalCard.getAtLocation() != null && Filters.and(locationFilter).accepts(gameState, modifiersQuerying, physicalCard.getAtLocation());
            }
        };
    }

    // Gets a filter representing the cards that are not prohibited from carrying the specified card.
    public static Filter notProhibitedFromCarrying(PhysicalCard cardToDeploy) {
        final Integer permCardToDeployCardId = cardToDeploy.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard cardToDeploy = gameState.findCardByPermanentId(permCardToDeployCardId);
                return !modifiersQuerying.prohibitedFromCarrying(gameState, physicalCard, cardToDeploy);
            }
        };
    }

    // Gets a filter representing the cards that are not prohibited from being carried by the specified card.
    public static Filter notProhibitedFromBeingCarriedBy(PhysicalCard cardToCarryCard) {
        final Integer permCardId = cardToCarryCard.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard cardToCarryCard = gameState.findCardByPermanentId(permCardId);
                return !modifiersQuerying.prohibitedFromCarrying(gameState, cardToCarryCard, physicalCard);
            }
        };
    }

    // Gets a filter representing the cards that are not prohibited from piloting the specified starship/vehicle.
    public static Filter notProhibitedFromPiloting(PhysicalCard starshipOrVehicle) {
        final Integer permStarshipOrVehicleCardId = starshipOrVehicle.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard starshipOrVehicle = gameState.findCardByPermanentId(permStarshipOrVehicleCardId);
                return !modifiersQuerying.prohibitedFromPiloting(gameState, physicalCard, starshipOrVehicle);
            }
        };
    }

    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter deviceMayNotBeRemovedUnlessDisarmed() {
        return deviceMayNotBeRemovedUnlessDisarmed;
    }

    /**
     * Filter that accepts devices that may not be removed unless attached to character is Disarmed.
     */
    public static final Filter deviceMayNotBeRemovedUnlessDisarmed = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return modifiersQuerying.mayNotRemoveDeviceUnlessDisarmed(gameState, physicalCard);
        }
    };

    // Gets a filter representing the cards that are not prohibited from carrying at least one card represented by the input filter.
    public static Filter notProhibitedFromStealing(PhysicalCard cardToSteal) {
        final Integer permCardId = cardToSteal.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {

                // TODO: Update this code and filterActive to support more combinations

                // TODO: Does this need to check if card can steal??? (or should something else do that?)

                return false;

                /*

                Collection<PhysicalCard> cards = Filters.filterActive(gameState, modifiersQuerying, source, null, TargetingReason.TO_BE_STOLEN, Filters.and(filters));
                for (PhysicalCard card : cards) {
                     if (!modifiersQuerying.prohibitedFromCarrying(gameState, card, physicalCard))
                         return true;
                }
                return false; */
            }
        };
    }

    /**
     * Gets a filter representing the cards that are not prohibited from being stolen and carried by at least one card
     * represented by the input filter.
     * @param source the card that is performing this query
     * @param filters the filters
     */
    public static Filter canBeStolenBy(PhysicalCard source, final Filter filters) {
        final Integer permSourceCardId = source != null ? source.getPermanentCardId() : null;
        return new Filter() {
            private Set<TargetingReason> targetingReasons = Collections.singleton(TargetingReason.TO_BE_STOLEN);
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard source = gameState.findCardByPermanentId(permSourceCardId);

                Collection<PhysicalCard> cardsToStealCard = Filters.filterActive(gameState.getGame(), source, filters);
                for (PhysicalCard cardToStealCard : cardsToStealCard) {
                    if (modifiersQuerying.canBeTargetedBy(gameState, physicalCard, cardToStealCard, targetingReasons)
                            && !modifiersQuerying.prohibitedFromCarrying(gameState, cardToStealCard, physicalCard)) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    /**
     * Gets a filter representing the cards that are not prohibited stealing and carrying the specified card.
     * @param cardToSteal the card to steal
     */
    public static Filter canStealAndCarry(PhysicalCard cardToSteal) {
        final Integer permCardToStealCardId = cardToSteal.getPermanentCardId();
        return new Filter() {
            private Set<TargetingReason> targetingReasons = Collections.singleton(TargetingReason.TO_BE_STOLEN);
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard cardToSteal = gameState.findCardByPermanentId(permCardToStealCardId);

                return (modifiersQuerying.canBeTargetedBy(gameState, cardToSteal, physicalCard, targetingReasons)
                        && !modifiersQuerying.prohibitedFromCarrying(gameState, physicalCard, cardToSteal));
            }
        };
    }

    /**
     * Gets a filter representing the cards that are not prohibited from being attached to (or being in "AT_LOCATION"
     * zone at location) the specified target.
     * @param target the target
     * @return the filter
     */
    public static Filter notProhibitedFromHavingAtTarget(PhysicalCard target) {
        final Integer permTargetCardId = target.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard target = gameState.findCardByPermanentId(permTargetCardId);
                return !modifiersQuerying.isProhibitedFromTarget(gameState, physicalCard, target);
            }
        };
    }

    /**
     * Gets a filter representing the cards that are the specified card is not prohibited from being attached to
     * (or being in "AT_LOCATION" zone at location).
     * @param card the card
     * @return the filter
     */
    public static Filter notProhibitedFromTarget(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);
                return !modifiersQuerying.isProhibitedFromTarget(gameState, card, physicalCard);
            }
        };
    }

    /**
     * Gets a filter representing the cards that are not prohibited from deploying to the specified target.
     * @param target the target
     * @return the filter
     */
    public static Filter notProhibitedFromHavingDeployedTo(PhysicalCard target) {
        final Integer permTargetCardId = target.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard target = gameState.findCardByPermanentId(permTargetCardId);
                return !modifiersQuerying.isProhibitedFromDeployingTo(gameState, physicalCard, target, null);
            }
        };
    }

    /**
     * Gets a filter representing the cards that are the specified card is not prohibited from deploying to.
     * @param cardToDeploy the card to deploy
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @return the filter
     */
    public static Filter notProhibitedFromDeployingTo(PhysicalCard cardToDeploy, final DeploymentRestrictionsOption deploymentRestrictionsOption) {
        final Integer permCardToDeployCardId = cardToDeploy.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard cardToDeploy = gameState.findCardByPermanentId(permCardToDeployCardId);
                return !modifiersQuerying.isProhibitedFromDeployingTo(gameState, cardToDeploy, physicalCard, deploymentRestrictionsOption);
            }
        };
    }

    /**
     * Gets a filter representing the card the specified card is granted the ability to deploy to.
     * @param cardToDeploy the card to deploy
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @return the filter
     */
    public static Filter grantedToDeployTo(PhysicalCard cardToDeploy, final ReactActionOption reactActionOption) {
        final Integer permCardToDeployCardId = cardToDeploy.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard cardToDeploy = gameState.findCardByPermanentId(permCardToDeployCardId);
                return modifiersQuerying.isGrantedToDeployTo(gameState, cardToDeploy, physicalCard, reactActionOption);
            }
        };
    }

    /**
     * Gets a filter representing the cards that are explicitly granted the ability to use the specified device.
     * @param device the device
     * @return the filter
     */
    public static Filter grantedToUseDevice(PhysicalCard device) {
        final Integer permDeviceCardId = device.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard device = gameState.findCardByPermanentId(permDeviceCardId);
                return modifiersQuerying.grantedToUseDevice(gameState, physicalCard, device);
            }
        };
    }

    /**
     * Gets a filter representing the cards that are explicitly granted the ability to use the specified weapon.
     * @param weapon the weapon
     * @return the filter
     */
    public static Filter grantedToUseWeapon(PhysicalCard weapon) {
        final Integer permWeaponCardId = weapon.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard weapon = gameState.findCardByPermanentId(permWeaponCardId);
                return modifiersQuerying.grantedToUseWeapon(gameState, physicalCard, weapon);
            }
        };
    }


    // Gets a filter representing the locations under the Hoth Energy Shield.
    public static Filter underHothEnergyShield() {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return (physicalCard.getBlueprint().getCardCategory()==CardCategory.LOCATION
                        && modifiersQuerying.isLocationUnderHothEnergyShield(gameState, physicalCard));
            }
        };
    }

    /**
     * Filter that accepts locations that are shielded.
     */
    public static final Filter shielded_location = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return underHothEnergyShield().accepts(gameState, modifiersQuerying, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter shieldedLocation() {
        return shielded_location;
    }


    /**
     * Gets a filter representing the devices and weapons that can be transferred to a card accepted by the target filter.
     * @param forFree true if the transfer is to be free, otherwise false
     * @param targetFilter the target filter
     * @return the filter
     */
    public static Filter deviceOrWeaponCanBeTransferredTo(final boolean forFree, final Filter targetFilter) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardToTransfer) {
                if (cardToTransfer.getBlueprint().getCardCategory() != CardCategory.DEVICE && cardToTransfer.getBlueprint().getCardCategory() != CardCategory.WEAPON)
                    return false;

                return cardToTransfer.getBlueprint().getTransferDeviceOrWeaponAction(cardToTransfer.getOwner(), gameState.getGame(), cardToTransfer, forFree, Filters.and(targetFilter)) != null;
            }
        };
    }

    /**
     * Gets a filter representing the cards that a specific card can be transferred to.
     * @param cardToTransfer the card to transfer
     * @param forFree true if the transfer is to be free, otherwise false
     * @return the filter
     */
    public static Filter canTransferDeviceOrWeaponTo(PhysicalCard cardToTransfer, final boolean forFree) {
        final Integer permCardToTransferCardId = cardToTransfer.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard targetCard) {
                PhysicalCard cardToTransfer = gameState.findCardByPermanentId(permCardToTransferCardId);

                if (cardToTransfer.getBlueprint().getCardCategory() != CardCategory.DEVICE && cardToTransfer.getBlueprint().getCardCategory() != CardCategory.WEAPON)
                    return false;

                return cardToTransfer.getBlueprint().getTransferDeviceOrWeaponAction(cardToTransfer.getOwner(), gameState.getGame(), cardToTransfer, forFree, Filters.sameCardId(targetCard)) != null;
            }
        };
    }

    /**
     * Gets a filter representing the cards that a specific card could be transferred to if the only requirement was having
     * enough Force to transfer to that target.
     * This filter is typically used from a getValidTargetFilter() method to handle transfer cost modifiers that only affect
     * when a card is transferred to certain targets.
     * @param cardToTransfer the card to transfer
     * @param playCardOption the play card option, or null
     * @return the filter
     */
    public static Filter canUseForceToTransferToTarget(PhysicalCard cardToTransfer, final PlayCardOption playCardOption) {
        final Integer permCardToTransferCardId = cardToTransfer.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard targetCard) {
                PhysicalCard cardToTransfer = gameState.findCardByPermanentId(permCardToTransferCardId);
                String playerId = cardToTransfer.getOwner();

                float totalTransferCost = modifiersQuerying.getTransferCost(gameState, cardToTransfer, targetCard, playCardOption);
                if (totalTransferCost <= 0) {
                    return true;
                }

                if (totalTransferCost > modifiersQuerying.getForceAvailableToUse(gameState, playerId)) {
                    return false;
                }

                return true;
            }
        };
    }

    /**
     * Gets a filter representing the cards that a specific card could be deployed to if the only requirement was having
     * enough Force to deploy to that target.
     * This filter is typically used from a getValidTargetFilter() method to handle deploy cost modifiers that only affect
     * when a card is deployed to certain targets.
     * @param sourceCard the card to initiate the deployment
     * @param cardToDeploy the card to deploy
     * @param playCardOption the play card option, or null
     * @param forFree true if playing card for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @return the filter
     */
    public static Filter canUseForceToDeployToTarget(PhysicalCard sourceCard, PhysicalCard cardToDeploy, final PlayCardOption playCardOption, final boolean forFree, final float changeInCost, final ReactActionOption reactActionOption) {
        final Integer permSourceCardId = sourceCard.getPermanentCardId();
        final Integer permCardToDeployCardId = cardToDeploy.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard targetCard) {
                PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);
                PhysicalCard cardToDeploy = gameState.findCardByPermanentId(permCardToDeployCardId);

                String playerId = cardToDeploy.getOwner();
                String opponent = gameState.getOpponent(playerId);

                float deployCostForPlayer = modifiersQuerying.getDeployCost(gameState, sourceCard, cardToDeploy, targetCard, false, playCardOption, forFree, changeInCost, reactActionOption, true);

                float deployCostForOpponent = 0;
                boolean useBothForcePiles = modifiersQuerying.isDeployUsingBothForcePiles(gameState, cardToDeploy, targetCard);
                if (useBothForcePiles) {
                    deployCostForOpponent = modifiersQuerying.getDeployCost(gameState, sourceCard, cardToDeploy, targetCard, false, playCardOption, forFree, changeInCost, reactActionOption, false);
                }

                if (deployCostForPlayer <= 0 && deployCostForOpponent <= 0) {
                    return true;
                }

                if (deployCostForPlayer > modifiersQuerying.getForceAvailableToUse(gameState, playerId)) {
                    return false;
                }

                if (useBothForcePiles
                        && deployCostForOpponent > modifiersQuerying.getForceAvailableToUse(gameState, opponent)) {
                    return false;
                }

                return true;
            }
        };
    }

    /**
     * Gets a filter representing the cards that a specific card could be deployed to if the only requirement was having
     * enough Force to deploy to that target.
     * This filter is typically used from a getValidTargetFilter() method to handle deploy cost modifiers that only affect
     * when a card is deployed to certain targets.
     * @param sourceCard the card to initiate the deployment
     * @param starship the starship to deploy
     * @param starshipForFree true if the starship deploys for free, otherwise false
     * @param starshipChangeInCost change in amount of Force (can be positive or negative) required for starship
     * @param pilot the pilot to deploy
     * @param pilotForFree true if the pilot (or driver) deploys for free, otherwise false
     * @param pilotChangeInCost change in amount of Force (can be positive or negative) required for pilot
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @return the filter
     */
    public static Filter canUseForceToDeploySimultaneouslyToTarget(PhysicalCard sourceCard, PhysicalCard starship, final boolean starshipForFree, final float starshipChangeInCost, PhysicalCard pilot, final boolean pilotForFree, final float pilotChangeInCost, final ReactActionOption reactActionOption) {
        final Integer permSourceCardId = sourceCard.getPermanentCardId();
        final Integer permStarshipCardId = starship.getPermanentCardId();
        final Integer permPilotCardId = pilot.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard targetCard) {
                PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);
                PhysicalCard starship = gameState.findCardByPermanentId(permStarshipCardId);
                PhysicalCard pilot = gameState.findCardByPermanentId(permPilotCardId);

                String playerId = starship.getOwner();
                String opponent = gameState.getOpponent(playerId);

                float deployCostForPlayer = modifiersQuerying.getSimultaneousDeployCost(gameState, sourceCard, starship, starshipForFree, starshipChangeInCost, pilot, pilotForFree, pilotChangeInCost, targetCard, reactActionOption, true);

                float deployCostForOpponent = 0;
                boolean useBothForcePiles = modifiersQuerying.isDeployUsingBothForcePiles(gameState, starship, targetCard)
                        || modifiersQuerying.isDeployUsingBothForcePiles(gameState, pilot, targetCard);
                if (useBothForcePiles) {
                    deployCostForOpponent = modifiersQuerying.getSimultaneousDeployCost(gameState, sourceCard, starship, starshipForFree, starshipChangeInCost, pilot, pilotForFree, pilotChangeInCost, targetCard, reactActionOption, false);
                }

                if (deployCostForPlayer <= 0 && deployCostForOpponent <= 0) {
                    return true;
                }

                if (deployCostForPlayer > modifiersQuerying.getForceAvailableToUse(gameState, playerId)) {
                    return false;
                }

                if (useBothForcePiles
                        && deployCostForOpponent > modifiersQuerying.getForceAvailableToUse(gameState, opponent)) {
                    return false;
                }

                return true;
            }
        };
    }

    /**
     * Gets a filter representing the cards that a specific Effect can be relocated to.
     * @param playerId the player to relocate the Effect
     * @param effectToRelocate the Effect to transfer
     * @return the filter
     */
    public static Filter canRelocateEffectTo(final String playerId, PhysicalCard effectToRelocate) {
        final Integer permEffectToRelocateCardId = effectToRelocate.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard effectToRelocate = gameState.findCardByPermanentId(permEffectToRelocateCardId);

                return Filters.and(Filters.Effect, Filters.zone(Zone.ATTACHED)).accepts(gameState, modifiersQuerying, effectToRelocate)
                        && effectToRelocate.getBlueprint().getValidRelocateEffectTargetFilter(playerId, gameState.getGame(), effectToRelocate).accepts(gameState, modifiersQuerying, physicalCard);
            }
        };
    }

    /**
     * Gets a filter representing the cards that an Effect accepted by the specified filter can be relocated to.
     * @param playerId the player to relocate the Effect
     * @param effectFilter the Effect filter
     * @return the filter
     */
    public static Filter canRelocateEffectTo(final String playerId, final Filter effectFilter) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return Filters.canSpotFromAllOnTable(gameState.getGame(), Filters.and(effectFilter, Filters.effectCanBeRelocatedTo(playerId, Filters.sameCardId(physicalCard))));
            }
        };
    }

    /**
     * Gets a filter representing Effects can be relocated to a card accepted by the target filter.
     * @param playerId the player to relocate the Effect
     * @param targetFilter the target filter
     * @return the filter
     */
    public static Filter effectCanBeRelocatedTo(final String playerId, final Filter targetFilter) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return Filters.and(Filters.Effect, Filters.zone(Zone.ATTACHED)).accepts(gameState, modifiersQuerying, physicalCard)
                        && Filters.canSpotFromAllOnTable(gameState.getGame(), Filters.and(targetFilter, Filters.canRelocateEffectTo(playerId, physicalCard)));
            }
        };
    }

    /**
     * Filter that accepts Interrupts and Effects that can be re-targeted to another target on same side of Force.
     * @param sourceCard the card to initiate the re-targeting
     * @return the filter
     */
    public static Filter cardBeingPlayedCanBeRetargetedToSameSideOfForce(PhysicalCard sourceCard) {
        final Integer permSourceCardId = sourceCard.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);

                if (!Filters.or(Filters.Interrupt, Filters.Effect_of_any_Kind).accepts(gameState, modifiersQuerying, physicalCard)) {
                    return false;
                }

                // Check that card is the current card being played
                PlayCardState topPlayCardState = gameState.getTopPlayCardState(sourceCard);
                if (topPlayCardState == null || topPlayCardState.getPlayCardAction().getPlayedCard().getPermanentCardId() != physicalCard.getPermanentCardId()) {
                    return false;
                }

                SwccgGame game = gameState.getGame();
                PlayCardAction playCardAction = topPlayCardState.getPlayCardAction();
                for (Integer targetGroupId : playCardAction.getAllPrimaryTargetCards().keySet()) {
                    Collection<PhysicalCard> targetCards = playCardAction.getPrimaryTargetCards(targetGroupId);
                    boolean isDark = Filters.canSpot(targetCards, game, Filters.owner(game.getDarkPlayer()));
                    boolean isLight = Filters.canSpot(targetCards, game, Filters.owner(game.getLightPlayer()));
                    if (isDark == isLight) {
                        continue;
                    }
                    int minCardsToTarget = playCardAction.getPrimaryMinimumCardsToTarget(targetGroupId);
                    boolean matchPartialModelType = playCardAction.getPrimaryTargetMatchPartialModelType(targetGroupId);
                    Map<InactiveReason, Boolean> spotOverrides = playCardAction.getPrimaryTargetSpotOverrides(targetGroupId);
                    Map<TargetingReason, Filterable> targetReasonFilterMap = playCardAction.getPrimaryTargetFilter(targetGroupId);
                    Collection<PhysicalCard> validTargets = Filters.filterActive(gameState.getGame(), sourceCard, matchPartialModelType, spotOverrides, targetReasonFilterMap);
                    if (Filters.canSpot(validTargets, gameState.getGame(), minCardsToTarget, matchPartialModelType, Filters.and(Filters.owner(isDark ? game.getDarkPlayer() : game.getLightPlayer()),
                            Filters.not(Filters.in(targetCards)), Filters.canBeTargetedBy(sourceCard)))) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts cards currently targeted by the currently played Interrupt or Effect that can be replaced
     * with another target on same side of Force.
     * @param sourceCard the card to initiate the re-targeting
     * @return the filter
     */
    public static Filter cardThatCardBeingPlayedCanBeRetargetedToSameSideOfForceFrom(PhysicalCard sourceCard) {
        final Integer permSourceCardId = sourceCard.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);
                SwccgGame game = gameState.getGame();

                // Check that card is the current card being played
                PlayCardState topPlayCardState = gameState.getTopPlayCardState(sourceCard);
                if (topPlayCardState == null) {
                    return false;
                }
                PlayCardAction playCardAction = topPlayCardState.getPlayCardAction();
                if (playCardAction == null || !Filters.or(Filters.Interrupt, Filters.Effect_of_any_Kind).accepts(game, playCardAction.getPlayedCard())) {
                    return false;
                }

                for (Integer targetGroupId : playCardAction.getAllPrimaryTargetCards().keySet()) {
                    Collection<PhysicalCard> targetCards = playCardAction.getPrimaryTargetCards(targetGroupId);
                    if (!Filters.in(targetCards).accepts(game, physicalCard)) {
                        continue;
                    }
                    boolean isDark = Filters.canSpot(targetCards, game, Filters.owner(game.getDarkPlayer()));
                    boolean isLight = Filters.canSpot(targetCards, game, Filters.owner(game.getLightPlayer()));
                    if (isDark == isLight) {
                        continue;
                    }
                    int minCardsToTarget = playCardAction.getPrimaryMinimumCardsToTarget(targetGroupId);
                    boolean matchPartialModelType = playCardAction.getPrimaryTargetMatchPartialModelType(targetGroupId);
                    Map<InactiveReason, Boolean> spotOverrides = playCardAction.getPrimaryTargetSpotOverrides(targetGroupId);
                    Map<TargetingReason, Filterable> targetReasonFilterMap = playCardAction.getPrimaryTargetFilter(targetGroupId);
                    Collection<PhysicalCard> validTargets = Filters.filterActive(gameState.getGame(), sourceCard, matchPartialModelType, spotOverrides, targetReasonFilterMap);
                    if (Filters.canSpot(validTargets, gameState.getGame(), minCardsToTarget, matchPartialModelType, Filters.and(Filters.owner(isDark ? game.getDarkPlayer() : game.getLightPlayer()),
                            Filters.not(Filters.in(targetCards)), Filters.canBeTargetedBy(sourceCard)))) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that can be targeted by the currently played Interrupt or Effect, instead of the
     * cards in the specified target group, if they are on the same side of Force.
     * @param sourceCard the card to initiate the re-targeting
     * @param targetGroupId the id of the target group to be re-targeted
     * @return the filter
     */
    public static Filter cardThatCardBeingPlayedCanBeRetargetedFromSameSideOfForceTo(PhysicalCard sourceCard, final int targetGroupId) {
        final Integer permSourceCardId = sourceCard.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);
                SwccgGame game = gameState.getGame();

                // Check that card is the current card being played
                PlayCardState topPlayCardState = gameState.getTopPlayCardState(sourceCard);
                if (topPlayCardState == null) {
                    return false;
                }
                PlayCardAction playCardAction = topPlayCardState.getPlayCardAction();
                if (playCardAction == null || !Filters.or(Filters.Interrupt, Filters.Effect_of_any_Kind).accepts(game, playCardAction.getPlayedCard())) {
                    return false;
                }

                Collection<PhysicalCard> targetCards = playCardAction.getPrimaryTargetCards(targetGroupId);
                if (targetCards.contains(physicalCard)) {
                    return false;
                }
                boolean isDark = Filters.canSpot(targetCards, game, Filters.owner(game.getDarkPlayer()));
                boolean isLight = Filters.canSpot(targetCards, game, Filters.owner(game.getLightPlayer()));
                if (isDark == isLight) {
                    return false;
                }
                int minCardsToTarget = playCardAction.getPrimaryMinimumCardsToTarget(targetGroupId);
                boolean matchPartialModelType = playCardAction.getPrimaryTargetMatchPartialModelType(targetGroupId);
                TargetingType targetingType = playCardAction.getPrimaryTargetingType(targetGroupId);
                Map<InactiveReason, Boolean> spotOverrides = playCardAction.getPrimaryTargetSpotOverrides(targetGroupId);
                Map<TargetingReason, Filterable> targetReasonFilterMap = playCardAction.getPrimaryTargetFilter(targetGroupId);
                Collection<PhysicalCard> validTargets = Filters.filterActive(gameState.getGame(), sourceCard, matchPartialModelType, spotOverrides, targetReasonFilterMap);
                validTargets = Filters.filter(validTargets, gameState.getGame(), matchPartialModelType, Filters.and(Filters.owner(isDark ? game.getDarkPlayer() : game.getLightPlayer()), Filters.not(Filters.in(targetCards)), Filters.canBeTargetedBy(sourceCard)));
                if (!validTargets.contains(physicalCard)) {
                    return false;
                }
                if (targetingType == TargetingType.TARGET_CARDS_AT_SAME_LOCATION) {
                    return Filters.canSpot(validTargets, gameState.getGame(), minCardsToTarget, matchPartialModelType, Filters.atSameLocation(physicalCard));
                }
                else {
                    return Filters.canSpot(validTargets, gameState.getGame(), minCardsToTarget, matchPartialModelType, Filters.any);
                }
            }
        };
    }

    /**
     * Filter that accepts Effects on table that can be re-targeted to cards accepted by the retargetToFilter.
     * @param sourceCard the card to initiate the re-targeting
     * @param retargetToFilter the re-target to filter
     * @return the filter
     */
    public static Filter effectCanBeRetargetedTo(PhysicalCard sourceCard, final Filter retargetToFilter) {
        final Integer permSourceCardId = sourceCard.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);

                if (!Filters.Effect_of_any_Kind.accepts(gameState, modifiersQuerying, physicalCard)) {
                    return false;
                }

                Map<TargetId, PhysicalCard> allTargetedCards = physicalCard.getTargetedCards(gameState);
                for (TargetId targetId : allTargetedCards.keySet()) {
                    PhysicalCard targetedCard = allTargetedCards.get(targetId);
                    if (Filters.hasAttached(physicalCard).accepts(gameState, modifiersQuerying, targetedCard)) {
                        continue;
                    }
                    Filter validTargetFilter = Filters.and(physicalCard.getValidTargetedFilter(targetId), retargetToFilter);
                    Map<InactiveReason, Boolean> spotOverrides = physicalCard.getBlueprint().getTargetSpotOverride(targetId);
                    if (Filters.canSpot(gameState.getGame(), sourceCard, spotOverrides, Filters.and(Filters.not(targetedCard), validTargetFilter, Filters.canBeTargetedBy(sourceCard)))) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts Effects on table that can be re-targeted to another target on same side of Force.
     * @param sourceCard the card to initiate the re-targeting
     * @return the filter
     */
    public static Filter effectCanBeRetargetedToSameSideOfForce(PhysicalCard sourceCard) {
        final Integer permSourceCardId = sourceCard.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);

                if (!Filters.Effect_of_any_Kind.accepts(gameState, modifiersQuerying, physicalCard)) {
                    return false;
                }

                Map<TargetId, PhysicalCard> allTargetedCards = physicalCard.getTargetedCards(gameState);
                for (TargetId targetId : allTargetedCards.keySet()) {
                    PhysicalCard targetedCard = allTargetedCards.get(targetId);
                    if (Filters.hasAttached(physicalCard).accepts(gameState, modifiersQuerying, targetedCard)) {
                        continue;
                    }
                    Filter validTargetFilter = physicalCard.getValidTargetedFilter(targetId);
                    Map<InactiveReason, Boolean> spotOverrides = physicalCard.getBlueprint().getTargetSpotOverride(targetId);
                    if (Filters.canSpot(gameState.getGame(), sourceCard, spotOverrides, Filters.and(Filters.owner(targetedCard.getOwner()), Filters.not(targetedCard), validTargetFilter, Filters.canBeTargetedBy(sourceCard)))) {
                        return true;
                    }
                }

                return false;
            }
        };
    }


    /**
     * Filter that accepts cards currently targeted by an Effect accepted by the effectFilter that can be replaced
     * with another target on same side of Force.
     * @param sourceCard the card to initiate the re-targeting
     * @param effectFilter the Effect filter
     * @return the filter
     */
    public static Filter cardThatEffectCanBeRetargetedToSameSideOfForceFrom(PhysicalCard sourceCard, final Filter effectFilter) {
        final Integer permSourceCardId = sourceCard.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);

                Collection<PhysicalCard> effects = Filters.filterActive(gameState.getGame(), sourceCard, Filters.and(Filters.Effect_of_any_Kind, effectFilter));
                for (PhysicalCard effect : effects) {

                    Map<TargetId, PhysicalCard> allTargetedCards = effect.getTargetedCards(gameState);
                    for (TargetId targetId : allTargetedCards.keySet()) {
                        PhysicalCard targetedCard = allTargetedCards.get(targetId);
                        if (!Filters.sameCardId(targetedCard).accepts(gameState, modifiersQuerying, physicalCard)) {
                            continue;
                        }
                        if (Filters.hasAttached(effect).accepts(gameState, modifiersQuerying, targetedCard)) {
                            continue;
                        }
                        Filter validTargetFilter = effect.getValidTargetedFilter(targetId);
                        Map<InactiveReason, Boolean> spotOverrides = effect.getBlueprint().getTargetSpotOverride(targetId);
                        if (Filters.canSpot(gameState.getGame(), sourceCard, spotOverrides, Filters.and(Filters.owner(targetedCard.getOwner()), Filters.not(targetedCard), validTargetFilter, Filters.canBeTargetedBy(sourceCard)))) {
                            return true;
                        }
                    }
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that can be targeted by the specified Effect, instead of the card currently targeted.
     * @param sourceCard the card to initiate the re-targeting
     * @return the filter
     */
    public static Filter cardThatEffectCanBeRetargetedTo(PhysicalCard sourceCard, final Filter effectFilter) {
        final Integer permSourceCardId = sourceCard.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);

                Collection<PhysicalCard> effects = Filters.filterActive(gameState.getGame(), sourceCard, Filters.and(Filters.Effect_of_any_Kind, effectFilter));
                for (PhysicalCard effect : effects) {

                    Map<TargetId, PhysicalCard> allTargetedCards = effect.getTargetedCards(gameState);
                    for (TargetId targetId : allTargetedCards.keySet()) {
                        PhysicalCard currentTarget = effect.getTargetedCard(gameState, targetId);
                        if (Filters.hasAttached(effect).accepts(gameState, modifiersQuerying, currentTarget)) {
                            continue;
                        }
                        if (Filters.and(Filters.not(currentTarget), effect.getValidTargetedFilter(targetId), Filters.canBeTargetedBy(sourceCard)).accepts(gameState, modifiersQuerying, physicalCard)) {
                            return true;
                        }
                    }
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that can be targeted by the specified Effect, instead of the card currently targeted with
     * the specified target id, if they are on the same side of Force.
     * @param sourceCard the card to initiate the re-targeting
     * @param effectCard the Effect
     * @param targetId the target id of the target to be re-targeted
     * @return the filter
     */
    public static Filter cardThatEffectCanBeRetargetedToSameSideOfForceTo(PhysicalCard sourceCard, PhysicalCard effectCard, final TargetId targetId) {
        final Integer permSourceCardId = sourceCard.getPermanentCardId();
        final Integer permEffectCardId = effectCard.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard sourceCard = gameState.findCardByPermanentId(permSourceCardId);
                PhysicalCard effectCard = gameState.findCardByPermanentId(permEffectCardId);

                if (!Filters.Effect_of_any_Kind.accepts(gameState, modifiersQuerying, effectCard)) {
                    return false;
                }

                PhysicalCard currentTarget = effectCard.getTargetedCard(gameState, targetId);
                if (Filters.hasAttached(effectCard).accepts(gameState, modifiersQuerying, currentTarget)) {
                    return false;
                }

                return Filters.and(Filters.owner(currentTarget.getOwner()), Filters.not(currentTarget), effectCard.getValidTargetedFilter(targetId), Filters.canBeTargetedBy(sourceCard)).accepts(gameState, modifiersQuerying, physicalCard);
            }
        };
    }

    /**
     * Gets a filter representing the cards that are in the specified target group of the specified action.
     * @param action the action
     * @param targetGroupId the target group id
     * @return the filter
     */
    public static Filter inActionTargetGroup(final Action action, final int targetGroupId) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                Map<Integer, Map<PhysicalCard, Set<TargetingReason>>> allTargetedCards = action.getAllPrimaryTargetCards();
                if (allTargetedCards.containsKey(targetGroupId)) {
                    for (PhysicalCard card : allTargetedCards.get(targetGroupId).keySet()) {
                        if (card.getPermanentCardId() == physicalCard.getPermanentCardId()) {
                            return true;
                        }
                    }
                }
                return false;
            }
        };
    }

    //
    //
    // This section defines Filters used prepositions (relationships of cards to each other).
    //
    //


    // Gets a filter representing cards that are "on" a planet that has a location that fits the input filters.
    public static Filter onPlanetWithLocation(final Filter... filters) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {

                return false;

                /* TODO

                Collection<PhysicalCard> cards = Filters.filterActive(gameState, modifiersQuerying, filters);
                for (PhysicalCard card : cards) {
                    if (card.getBlueprint().getCardType()==CardType.LOCATION) {
                        String planet = card.getPartOfSystem();
                        if (modifiersQuerying.isOnPlanet(gameState, physicalCard, planet))
                            return true;
                    }
                }
                return false;
                */
            }
        };
    }




    // Gets a filter representing cards that other cards can be "present at" on a planet.
    public static Filter placeToBePresentOnPlanet(final String planet) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.isPlaceToBePresentOnPlanet(gameState, physicalCard, planet);
            }
        };
    }

    // Gets a filter representing the cards that are "at" a site on the input system.
    public static Filter atSiteOfSystem(final String system) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {

                // TODO: Fix this
                return false;


                //return modifiersQuerying.isAtPlanetSite(gameState, physicalCard, system);
            }
        };
    }

    // Gets a filter representing the cards that are "at" locations that fit the input filters or are the locations themselves.
    public static Filter locationAndCardsAtLocation(final Filter filters) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard location;
                if (physicalCard.getBlueprint().getCardCategory()==CardCategory.LOCATION)
                    location = physicalCard;
                else
                    location = modifiersQuerying.getLocationThatCardIsAt(gameState, physicalCard);
                return location!=null && Filters.and(filters).accepts(gameState, modifiersQuerying, location);
            }
        };
    }

    /**
     * Gets a filter that accepts the location that is the innermost marker site
     */
    public static final Filter innermostMarker = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            Filter filter = Filters.marker_site;
            if (!filter.accepts(gameState, modifiersQuerying, physicalCard))
                return false;

            Collection<PhysicalCard> markerSites = Filters.filterTopLocationsOnTable(gameState.getGame(), filter);
            if (Filters.Seventh_Marker.accepts(gameState, modifiersQuerying, physicalCard)) {
                return Filters.filterCount(markerSites, gameState.getGame(), 1, Filters.or(Filters.First_Marker,
                        Filters.Second_Marker, Filters.Third_Marker, Filters.Fourth_Marker, Filters.Fifth_Marker, Filters.Sixth_Marker)).isEmpty();
            }
            if (Filters.Sixth_Marker.accepts(gameState, modifiersQuerying, physicalCard)) {
                return Filters.filterCount(markerSites, gameState.getGame(), 1, Filters.or(Filters.First_Marker,
                        Filters.Second_Marker, Filters.Third_Marker, Filters.Fourth_Marker, Filters.Fifth_Marker)).isEmpty();
            }
            if (Filters.Fifth_Marker.accepts(gameState, modifiersQuerying, physicalCard)) {
                return Filters.filterCount(markerSites, gameState.getGame(), 1, Filters.or(Filters.First_Marker,
                        Filters.Second_Marker, Filters.Third_Marker, Filters.Fourth_Marker)).isEmpty();
            }
            if (Filters.Fourth_Marker.accepts(gameState, modifiersQuerying, physicalCard)) {
                return Filters.filterCount(markerSites, gameState.getGame(), 1, Filters.or(Filters.First_Marker,
                        Filters.Second_Marker, Filters.Third_Marker)).isEmpty();
            }
            if (Filters.Third_Marker.accepts(gameState, modifiersQuerying, physicalCard)) {
                return Filters.filterCount(markerSites, gameState.getGame(), 1, Filters.or(Filters.First_Marker,
                        Filters.Second_Marker)).isEmpty();
            }
            if (Filters.Second_Marker.accepts(gameState, modifiersQuerying, physicalCard)) {
                return Filters.filterCount(markerSites, gameState.getGame(), 1, Filters.First_Marker).isEmpty();
            }
            if (Filters.First_Marker.accepts(gameState, modifiersQuerying, physicalCard)) {
                return true;
            }

            return false;
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter innermostMarker() {
        return innermostMarker;
    }

    /**
     * Gets a filter that accepts the location that is the outermost marker site
     * @param onlyExterior true if outermost exterior marker site, otherwise false
     * @return the filter
     */
    public static Filter outermostMarker(final boolean onlyExterior) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                Filter filter = onlyExterior ? Filters.exterior_marker_site : Filters.marker_site;
                if (!filter.accepts(gameState, modifiersQuerying, physicalCard))
                    return false;

                Collection<PhysicalCard> markerSites = Filters.filterTopLocationsOnTable(gameState.getGame(), filter);
                if (Filters.First_Marker.accepts(gameState, modifiersQuerying, physicalCard)) {
                    return Filters.filterCount(markerSites, gameState.getGame(), 1, Filters.or(Filters.Second_Marker,
                            Filters.Third_Marker, Filters.Fourth_Marker, Filters.Fifth_Marker, Filters.Sixth_Marker, Filters.Seventh_Marker)).isEmpty();
                }
                if (Filters.Second_Marker.accepts(gameState, modifiersQuerying, physicalCard)) {
                    return Filters.filterCount(markerSites, gameState.getGame(), 1, Filters.or(Filters.Third_Marker,
                            Filters.Fourth_Marker, Filters.Fifth_Marker, Filters.Sixth_Marker, Filters.Seventh_Marker)).isEmpty();
                }
                if (Filters.Third_Marker.accepts(gameState, modifiersQuerying, physicalCard)) {
                    return Filters.filterCount(markerSites, gameState.getGame(), 1, Filters.or(Filters.Fourth_Marker,
                            Filters.Fifth_Marker, Filters.Sixth_Marker, Filters.Seventh_Marker)).isEmpty();
                }
                if (Filters.Fourth_Marker.accepts(gameState, modifiersQuerying, physicalCard)) {
                    return Filters.filterCount(markerSites, gameState.getGame(), 1, Filters.or(Filters.Fifth_Marker,
                            Filters.Sixth_Marker, Filters.Seventh_Marker)).isEmpty();
                }
                if (Filters.Fifth_Marker.accepts(gameState, modifiersQuerying, physicalCard)) {
                    return Filters.filterCount(markerSites, gameState.getGame(), 1, Filters.or(Filters.Sixth_Marker,
                            Filters.Seventh_Marker)).isEmpty();
                }
                if (Filters.Sixth_Marker.accepts(gameState, modifiersQuerying, physicalCard)) {
                    return Filters.filterCount(markerSites, gameState.getGame(), 1, Filters.Seventh_Marker).isEmpty();
                }
                if (Filters.Seventh_Marker.accepts(gameState, modifiersQuerying, physicalCard)) {
                    return true;
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts representing cards not ignored during Epic Event calculations.
     */
    public static final Filter notIgnoredDuringEpicEventCalculation = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return !modifiersQuerying.ignoreDuringEpicEventCalculation(gameState, physicalCard);
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter notIgnoredDuringEpicEventCalculation() {
        return notIgnoredDuringEpicEventCalculation;
    }


    //
    //
    // This section defines Filters used for devices.
    //
    //

    // Gets a filter representing the cards that can use a specified device.
    public static Filter canUseDevice(PhysicalCard device) {
        final Integer permDeviceCardId = device.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard device = gameState.findCardByPermanentId(permDeviceCardId);

                if (!device.getBlueprint().getValidToUseDeviceFilter(device.getOwner(), gameState.getGame(), device).accepts(gameState, modifiersQuerying, physicalCard)) {
                    return false;
                }

                if (Filters.sameCardId(device).accepts(gameState, modifiersQuerying, physicalCard)) {
                    return true;
                }

                boolean mayBeUsedByLandedStarship = modifiersQuerying.mayBeUsedByLandedStarship(gameState, device);

                int numDevicesToUseLimit = modifiersQuerying.numDevicesAllowedToUse(gameState, physicalCard, mayBeUsedByLandedStarship);
                List<Integer> otherDevicesUsed = modifiersQuerying.otherDevicesUsed(physicalCard, device);
                if (otherDevicesUsed.size() >= numDevicesToUseLimit)
                    return false;

                return true;
            }
        };
    }

    /**
     * Filter that accepts devices and weapons cards that can be deployed on a character.
     */
    public static final Filter deviceOrWeaponThatCanBeDeployedOnCharacters = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return physicalCard.getBlueprint().canBeDeployedOnCharacter();
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter deviceOrWeaponThatCanBeDeployedOnCharacters() {
        return deviceOrWeaponThatCanBeDeployedOnCharacters;
    }

    //
    //
    // This section defines Filters used for weapons.
    //
    //

    // Gets a filter representing the cards that can use a specified weapon.


    /**
     * Gets a filter representing the cards that can use a specified weapon.
     * @param weapon the weapon
     * @return Filter
     */
    public static Filter canUseWeapon(PhysicalCard weapon) {
        final Integer permWeaponCardId = weapon.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard weapon = gameState.findCardByPermanentId(permWeaponCardId);

                boolean mayBeUsedByLandedStarship = modifiersQuerying.mayBeUsedByLandedStarship(gameState, weapon);

                if ((physicalCard.getBlueprint().getCardCategory()==CardCategory.VEHICLE || physicalCard.getBlueprint().getCardCategory()==CardCategory.STARSHIP)
                    && !modifiersQuerying.isPiloted(gameState, physicalCard, mayBeUsedByLandedStarship)) {
                    return false;
                }

                if (weapon.getBlueprint().getValidToUseWeaponFilter(weapon.getOwner(), gameState.getGame(), weapon).acceptsCount(gameState, modifiersQuerying, physicalCard) == 0) {
                    return false;
                }

                if (Filters.sameCardId(weapon).accepts(gameState, modifiersQuerying, physicalCard)) {
                    return true;
                }

                int numWeaponsToUseLimit = modifiersQuerying.numWeaponsAllowedToUse(gameState, physicalCard, mayBeUsedByLandedStarship);
                List<Integer> otherWeaponsUsed = modifiersQuerying.otherWeaponsUsed(physicalCard, weapon);
                if (otherWeaponsUsed.size() >= numWeaponsToUseLimit)
                    return false;

                if (Filters.squadron.accepts(gameState, modifiersQuerying, physicalCard)
                        && !canModelTypesUseWeapons(gameState, modifiersQuerying, physicalCard, weapon, otherWeaponsUsed)) {
                    return false;
                }

                return true;
            }
        };
    }

    /**
     * Determines if the model types of the specified card can use the specified weapon when the card has already weapons.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param card the card
     * @param weaponToUse the weapon to use
     * @param weaponsAlreadyUsed the card IDs of weapons already used by the card
     * @return true or false
     */
    private static boolean canModelTypesUseWeapons(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card, PhysicalCard weaponToUse, List<Integer> weaponsAlreadyUsed) {
        List<ModelType> unmatchedModelTypes = new ArrayList<ModelType>(card.getBlueprint().getModelTypes());
        List<Integer> unmatchedWeaponIds = new ArrayList<Integer>();
        unmatchedWeaponIds.add(weaponToUse.getCardId());
        unmatchedWeaponIds.addAll(weaponsAlreadyUsed);

        return canModelTypesUseWeaponsInner(gameState, modifiersQuerying, card, unmatchedModelTypes, unmatchedWeaponIds, new ArrayList<String>(), new ArrayList<String>());
    }

    private static boolean canModelTypesUseWeaponsInner(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card, List<ModelType> modelTypes, List<Integer> weaponIds, List<String> validPairs, List<String> invalidPairs) {
        if (weaponIds.isEmpty()) {
            return true;
        }

        for (int i=0; i<modelTypes.size(); ++i) {
            ModelType modelType = modelTypes.get(i);
            for (int j=0; j<weaponIds.size(); ++j) {
                Integer weaponId = weaponIds.get(j);

                // If we already know this pair is not valid, then do not check again
                String pairKey = modelType + "_" + weaponId;
                if (!invalidPairs.contains(pairKey)) {
                    PhysicalCard weapon = gameState.findCardById(weaponId);

                    // Check if valid pair
                    if (validPairs.contains(pairKey)
                            || weapon.getBlueprint().getValidToUseWeaponFilter(weapon.getOwner(), gameState.getGame(), weapon).acceptsSingleModelType(gameState, modifiersQuerying, card, modelType)) {
                        // Remember that this pair works
                        if (!validPairs.contains(pairKey)) {
                            validPairs.add(pairKey);
                        }
                        List<ModelType> unmatchedModelTypes = new ArrayList<ModelType>(modelTypes);
                        unmatchedModelTypes.remove(i);
                        List<Integer> unmatchedWeaponIds = new ArrayList<Integer>(weaponIds);
                        unmatchedWeaponIds.remove(j);
                        if (canModelTypesUseWeaponsInner(gameState, modifiersQuerying, card, unmatchedModelTypes, unmatchedWeaponIds, validPairs, invalidPairs)) {
                            return true;
                        }
                    }
                    else {
                        invalidPairs.add(pairKey);
                    }
                }
            }
        }

        return false;
    }

    /**
     * Gets a filter representing the cards that can use a specified permanent weapon.
     * @param permanentWeapon the permanent weapon
     * @return Filter
     */
    public static Filter canUseWeapon(final SwccgBuiltInCardBlueprint permanentWeapon) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (!Filters.sameCardId(permanentWeapon.getPhysicalCard(gameState.getGame())).accepts(gameState, modifiersQuerying, physicalCard))
                    return false;

                if ((physicalCard.getBlueprint().getCardCategory()==CardCategory.VEHICLE || physicalCard.getBlueprint().getCardCategory()==CardCategory.STARSHIP)
                        && !modifiersQuerying.isPiloted(gameState, physicalCard, false)) {
                    return false;
                }

                int numWeaponsToUseLimit = modifiersQuerying.numWeaponsAllowedToUse(gameState, physicalCard, false);
                List<Integer> otherWeaponsUsed = modifiersQuerying.otherWeaponsUsed(physicalCard, permanentWeapon);
                if (otherWeaponsUsed.size() >= numWeaponsToUseLimit)
                    return false;

                return true;
            }
        };
    }

    /**
     * Gets a filter representing the cards that can fire a specified weapon.
     * @param weapon the weapon
     * @return Filter
     */
    public static Filter canFireWeapon(PhysicalCard weapon) {
        final Integer permWeaponCardId = weapon.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard weapon = gameState.findCardByPermanentId(permWeaponCardId);

                if (!modifiersQuerying.notProhibitedFromFiringWeapons(gameState, physicalCard))
                    return false;

                boolean mayBeUsedByLandedStarship = modifiersQuerying.mayBeUsedByLandedStarship(gameState, weapon);

                if ((physicalCard.getBlueprint().getCardCategory() == CardCategory.VEHICLE || physicalCard.getBlueprint().getCardCategory() == CardCategory.STARSHIP)
                        && !modifiersQuerying.isPiloted(gameState, physicalCard, mayBeUsedByLandedStarship)) {
                    return false;
                }

                if (weapon.getBlueprint().getValidToUseWeaponFilter(weapon.getOwner(), gameState.getGame(), weapon).acceptsCount(gameState, modifiersQuerying, physicalCard) == 0) {
                    return false;
                }

                if (!modifiersQuerying.mayFireAnyNumberOfWeapons(gameState, physicalCard)) {
                    int numWeaponsToUseLimit = modifiersQuerying.numWeaponsAllowedToUse(gameState, physicalCard, mayBeUsedByLandedStarship);
                    List<Integer> otherWeaponsUsed = modifiersQuerying.otherWeaponsUsed(physicalCard, weapon);
                    if (otherWeaponsUsed.size() >= numWeaponsToUseLimit)
                        return false;

                    if (Filters.squadron.accepts(gameState, modifiersQuerying, physicalCard)
                            && !canModelTypesUseWeapons(gameState, modifiersQuerying, physicalCard, weapon, otherWeaponsUsed)) {
                        return false;
                    }
                }

                return true;
            }
        };
    }

    /**
     * Gets a filter representing the cards that can fire a specified permanent weapon.
     * @param permanentWeapon the permanent weapon
     * @return Filter
     */
    public static Filter canFireWeapon(final SwccgBuiltInCardBlueprint permanentWeapon) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (!Filters.sameCardId(permanentWeapon.getPhysicalCard(gameState.getGame())).accepts(gameState, modifiersQuerying, physicalCard))
                    return false;

                if (!modifiersQuerying.notProhibitedFromFiringWeapons(gameState, physicalCard))
                    return false;

                if ((physicalCard.getBlueprint().getCardCategory() == CardCategory.VEHICLE || physicalCard.getBlueprint().getCardCategory() == CardCategory.STARSHIP)
                        && !modifiersQuerying.isPiloted(gameState, physicalCard, false)) {
                    return false;
                }

                if (!modifiersQuerying.mayFireAnyNumberOfWeapons(gameState, physicalCard)) {
                    int numWeaponsToUseLimit = modifiersQuerying.numWeaponsAllowedToUse(gameState, physicalCard, false);
                    List<Integer> otherWeaponsUsed = modifiersQuerying.otherWeaponsUsed(physicalCard, permanentWeapon);
                    if (otherWeaponsUsed.size() >= numWeaponsToUseLimit)
                        return false;
                }

                return true;
            }
        };
    }

    /**
     * Gets a filter representing the weapons that are within range to fire at the location. Also checks if the weapon
     * is not prevented to be used/fired and may be used/fired by the card it is attached to.
     * Example: AT-AT Cannon firing at Main Power Generators
     * @param location the location
     */
    public static Filter canBeFiredAtLocationInRange(PhysicalCard location) {
        final Integer permLocationCardId = location.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard weapon) {
                PhysicalCard location = gameState.findCardByPermanentId(permLocationCardId);

                if (weapon.getBlueprint().getCardCategory() == CardCategory.WEAPON) {
                    PhysicalCard attachedTo = weapon.getAttachedTo();
                    if (attachedTo != null) {
                        if (Filters.and(Filters.canUseWeapon(weapon), Filters.canFireWeapon(weapon)).accepts(gameState, modifiersQuerying, attachedTo)) {
                            if (Filters.sameLocation(weapon).accepts(gameState, modifiersQuerying, location)) {
                                return true;
                            }
                            if (modifiersQuerying.canWeaponTargetAdjacentSite(gameState, weapon)
                                    && Filters.adjacentSite(weapon).accepts(gameState, modifiersQuerying, location)) {
                                return true;
                            }
                            if (modifiersQuerying.canWeaponTargetTwoSitesAway(gameState, weapon)
                                    && Filters.siteWithinDistance(weapon, 2).accepts(gameState, modifiersQuerying, location)) {
                                return true;
                            }
                            if (modifiersQuerying.canWeaponTargetNearestRelatedExteriorSite(gameState, weapon)
                                    && Filters.nearestRelatedExteriorSite(weapon).accepts(gameState, modifiersQuerying, location)) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                PhysicalCard location = gameState.findCardByPermanentId(permLocationCardId);

                if (builtInCardBlueprint.isWeapon()) {
                    PhysicalCard physicalCard = builtInCardBlueprint.getPhysicalCard(gameState.getGame());
                    if (physicalCard != null) {
                        if (Filters.and(Filters.canUseWeapon(builtInCardBlueprint), Filters.canFireWeapon(builtInCardBlueprint)).accepts(gameState, modifiersQuerying, physicalCard)) {

                            if (Filters.sameLocation(physicalCard).accepts(gameState, modifiersQuerying, location)) {
                                return true;
                            }
                            if (modifiersQuerying.canWeaponTargetAdjacentSite(gameState, builtInCardBlueprint)
                                    && Filters.adjacentSite(physicalCard).accepts(gameState, modifiersQuerying, location)) {
                                return true;
                            }
                            if (modifiersQuerying.canWeaponTargetTwoSitesAway(gameState, builtInCardBlueprint)
                                    && Filters.siteWithinDistance(physicalCard, 2).accepts(gameState, modifiersQuerying, location)) {
                                return true;
                            }
                            if (modifiersQuerying.canWeaponTargetNearestRelatedExteriorSite(gameState, builtInCardBlueprint)
                                    && Filters.nearestRelatedExteriorSite(physicalCard).accepts(gameState, modifiersQuerying, location)) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        };
    }

    /**
     * Gets a filter representing a Superlaser that can be fired at the specified planet system.
     * @param planetSystem the planet system
     */
    public static Filter superlaserThatCanFireAtPlanetSystem(PhysicalCard planetSystem) {
        final Integer permPlanetSystemCardId = planetSystem.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard superlaser) {
                PhysicalCard planetSystem = gameState.findCardByPermanentId(permPlanetSystemCardId);

                if (!Filters.planet_system.accepts(gameState, modifiersQuerying, planetSystem))
                    return false;

                if (!Filters.and(Filters.Superlaser, Filters.attachedTo(Filters.and(Filters.Death_Star_system, Filters.isOrbiting(planetSystem.getTitle())))).accepts(gameState, modifiersQuerying, superlaser))
                    return false;

                // Check if weapon cannot be used
                if (modifiersQuerying.mayNotBeUsed(gameState, superlaser))
                    return false;

                // Check if weapon cannot be fired
                if (modifiersQuerying.mayNotBeFired(gameState, superlaser))
                    return false;

                // Check if weapon cannot target planet system
                if (!modifiersQuerying.canBeTargetedBy(gameState, planetSystem, superlaser))
                    return false;

                return true;
            }
        };
    }

    /**
     * Gets a filter representing cards that a card may deploy to only based on presence and Force icons.
     * This is generally called by a getValidDeployTargetFilter and combined with other Filters to figure out
     * valid targets for a card to deploy to.
     * @param cardToDeploy the card to deploy
     */
    public static Filter sufficientPresenceOrForceIconsToDeployTo(PhysicalCard cardToDeploy) {
        final Integer permCardToDeployCardId = cardToDeploy.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard cardToDeploy = gameState.findCardByPermanentId(permCardToDeployCardId);

                if (modifiersQuerying.ignoresLocationDeploymentRestrictions(gameState, cardToDeploy, physicalCard, null, true))
                    return true;

                if (modifiersQuerying.mayDeployToTargetWithoutPresenceOrForceIcons(gameState, physicalCard, cardToDeploy))
                    return true;

                PhysicalCard locationHere = modifiersQuerying.getLocationHere(gameState, physicalCard);
                if (locationHere == null)
                    return false;

                Icon icon;
                if (gameState.getSide(cardToDeploy.getOwner()) == Side.DARK)
                    icon = Icon.DARK_FORCE;
                else
                    icon = Icon.LIGHT_FORCE;

                if (modifiersQuerying.hasIcon(gameState, locationHere, icon))
                    return true;

                if (Filters.canSpot(gameState.getGame(), null, SpotOverride.INCLUDE_UNDERCOVER, Filters.and(Filters.owner(cardToDeploy.getOwner()), Filters.undercover_spy, Filters.at(locationHere))))
                    return true;

                return modifiersQuerying.hasPresenceAt(gameState, cardToDeploy.getOwner(), locationHere, false, null, null);
            }
        };
    }

    /**
     * Gets a filter representing targets that the specified card ignores location deployment restrictions when deploying to.
     * @param cardToDeploy the card to deploy
     */
    public static Filter ignoresLocationDeployRestrictionsWhenDeployingTo(PhysicalCard cardToDeploy) {
        final Integer permCardToDeployCardId = cardToDeploy.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard cardToDeploy = gameState.findCardByPermanentId(permCardToDeployCardId);
                return modifiersQuerying.ignoresLocationDeploymentRestrictions(gameState, cardToDeploy, physicalCard, null, false);
            }
        };
    }

    // Gets a filter representing locations that are part of a system of the specified name.
    public static Filter partOfSystem(final String systemName) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return physicalCard.getBlueprint().getCardCategory()==CardCategory.LOCATION && physicalCard.getPartOfSystem() != null && physicalCard.getPartOfSystem().equals(systemName);
            }
        };
    }

    /**
     * Filter that accepts cards that are sites of the specified starship or vehicle persona.
     *
     * @param starshipOrVehicle the starship/vehicle
     * @param onlyIfRelatedByPersona true if only unique sites are included, false if non-unique sites are also included
     * @return Filter
     */
    public static Filter siteOfStarshipOrVehicle(final Persona starshipOrVehicle, final boolean onlyIfRelatedByPersona) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return (physicalCard.getBlueprint().hasIcon(Icon.STARSHIP_SITE) || physicalCard.getBlueprint().hasIcon(Icon.VEHICLE_SITE))
                        && ((physicalCard.getBlueprint().getRelatedStarshipOrVehiclePersona() != null && physicalCard.getBlueprint().getRelatedStarshipOrVehiclePersona() == starshipOrVehicle)
                        || (!onlyIfRelatedByPersona && (physicalCard.getRelatedStarshipOrVehicle() != null && physicalCard.getRelatedStarshipOrVehicle().getBlueprint().hasPersona(starshipOrVehicle))));
            }
        };
    }

    /**
     * Filter that accepts cards that are sites of the specified starship or vehicle.
     *
     * @param starshipOrVehicle the starship/vehicle
     * @return Filter
     */
    public static Filter siteOfStarshipOrVehicle(PhysicalCard starshipOrVehicle) {
        final Integer permStarshipOrVehicleCardId = starshipOrVehicle.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard starshipOrVehicle = gameState.findCardByPermanentId(permStarshipOrVehicleCardId);

                return (physicalCard.getBlueprint().hasIcon(Icon.STARSHIP_SITE) || physicalCard.getBlueprint().hasIcon(Icon.VEHICLE_SITE))
                        && ((physicalCard.getBlueprint().getRelatedStarshipOrVehiclePersona() != null && starshipOrVehicle.getBlueprint().hasPersona(physicalCard.getBlueprint().getRelatedStarshipOrVehiclePersona()))
                        || (physicalCard.getRelatedStarshipOrVehicle() != null && physicalCard.getRelatedStarshipOrVehicle() == starshipOrVehicle));
            }
        };
    }

    /**
     * Filter that accepts cards that are sites of starships or vehicles accepted by the specified filter.
     *
     * @param filters the filters
     * @return Filter
     */
    public static Filter siteOfStarshipOrVehicle(final Filter filters) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (!Filters.or(Filters.starship_site, Filters.vehicle_site).accepts(gameState, modifiersQuerying, physicalCard)) {
                    return false;
                }

                Collection<PhysicalCard> starshipsAndVehicles = Filters.filterAllOnTable(gameState.getGame(), filters);

                for (PhysicalCard starshipOrVehicle : starshipsAndVehicles) {
                    if ((physicalCard.getBlueprint().getRelatedStarshipOrVehiclePersona() != null && starshipOrVehicle.getBlueprint().hasPersona(physicalCard.getBlueprint().getRelatedStarshipOrVehiclePersona()))
                            || (physicalCard.getRelatedStarshipOrVehicle() != null && Filters.sameCardId(physicalCard.getRelatedStarshipOrVehicle()).accepts(gameState, modifiersQuerying, starshipOrVehicle))) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    /**
     * Filter that accepts cards that the the related starship or vehicle to a related starship or vehicle site.
     *
     * @param starshipOrVehicleSite the starship or vehicle site
     * @return Filter
     */
    public static Filter relatedStarshipOrVehicle(PhysicalCard starshipOrVehicleSite) {
        final Integer permStarshipOrVehicleSiteCardId = starshipOrVehicleSite.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard starshipOrVehicleSite = gameState.findCardByPermanentId(permStarshipOrVehicleSiteCardId);

                return ((starshipOrVehicleSite.getBlueprint().hasIcon(Icon.STARSHIP_SITE) && physicalCard.getBlueprint().getCardCategory() == CardCategory.STARSHIP)
                        || (starshipOrVehicleSite.getBlueprint().hasIcon(Icon.VEHICLE_SITE) && physicalCard.getBlueprint().getCardCategory() == CardCategory.VEHICLE))
                        && ((starshipOrVehicleSite.getBlueprint().getRelatedStarshipOrVehiclePersona() != null && physicalCard.getBlueprint().hasPersona(starshipOrVehicleSite.getBlueprint().getRelatedStarshipOrVehiclePersona()))
                        || (starshipOrVehicleSite.getRelatedStarshipOrVehicle() != null && Filters.sameCardId(starshipOrVehicleSite.getRelatedStarshipOrVehicle()).accepts(gameState, modifiersQuerying, physicalCard)));
            }
        };
    }

    /**
     * Filter that accepts locations that are part of the Renegade planet.
     */
    public static final Filter Renegade_planet_location = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            String renegadePlanet = gameState.getRenegadePlanet();
            if (renegadePlanet == null)
                return false;

            if (!Filters.location.accepts(gameState, modifiersQuerying, physicalCard))
                return false;

            return renegadePlanet.equals(physicalCard.getPartOfSystem());
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter renegadePlanetLocation() {
        return Renegade_planet_location;
    }

    /**
     * Filter that accepts locations that are part of the Subjugated planet.
     */
    public static final Filter Subjugated_planet_location = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            String subjugatedPlanet = gameState.getSubjugatedPlanet();
            if (subjugatedPlanet == null)
                return false;

            if (!Filters.location.accepts(gameState, modifiersQuerying, physicalCard))
                return false;

            return subjugatedPlanet.equals(physicalCard.getPartOfSystem());
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter subjugatedPlanetLocation() {
        return Subjugated_planet_location;
    }


    //
    //
    // This section defines Filters used for Attack Run.
    //
    //

    /**
     * Filter that accepts the card that is the lead starfighter in an Attack Run.
     */
    public static final Filter lead_starfighter_in_Attack_Run = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            EpicEventState epicEventState = gameState.getEpicEventState();
            if (epicEventState != null && epicEventState.getEpicEventType() == EpicEventState.Type.ATTACK_RUN) {
                PhysicalCard leadStarfighter = ((AttackRunState) epicEventState).getLeadStarfighter();
                return leadStarfighter != null && Filters.sameCardId(physicalCard).accepts(gameState, modifiersQuerying, leadStarfighter);
            }
            return false;
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter leadStarfighterInAttackRun() {
        return lead_starfighter_in_Attack_Run;
    }

    /**
     * Filter that accepts the cards that are wingmen in an Attack Run.
     */
    public static final Filter wingmen_in_Attack_Run = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            EpicEventState epicEventState = gameState.getEpicEventState();
            if (epicEventState != null && epicEventState.getEpicEventType() == EpicEventState.Type.ATTACK_RUN) {
                return Filters.in(((AttackRunState) epicEventState).getWingmen()).accepts(gameState, modifiersQuerying, physicalCard);
            }
            return false;
        }
    };
    /**
     * Wrapper method to allow other static filters to access the wrapped filter.
     */
    private static Filter wingmenInAttackRun() {
        return wingmen_in_Attack_Run;
    }


    //
    //
    // Filters for historical information
    //
    //

    /**
     * Filter that accepts cards that were forfeited this turn from a location accepted by the specified filter this turn.
     *
     * @param locationFilter the locationFilter
     * @return Filter
     */
    public static Filter forfeitedFromLocationThisTurn(final Filter locationFilter) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                Map<PhysicalCard, Set<PhysicalCard>> locationsMap = modifiersQuerying.getForfeitedFromLocationsThisTurn();
                for (PhysicalCard location : locationsMap.keySet()) {
                    if (Filters.and(locationFilter).accepts(gameState, modifiersQuerying, location)) {
                        if (locationsMap.get(location).contains(physicalCard)) {
                            return true;
                        }
                    }
                }
                return false;
            }
        };
    }

    //
    //
    // This section defines the always true and always false Filters.
    //
    //

    // Gets a filter that accepts any card (or permanent).
    public static final Filter any = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return true;
        }
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
            return true;
        }
    };

    // Gets a filter that accepts no cards (or permanents).
    public static final Filter none = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            return false;
        }
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
            return false;
        }
    };

    //
    //
    // This section defines Filter logical operations (AND, OR, NOT).
    //
    //

    // Gets a filter that represents cards that meet ALL of the specified filters.
    public static Filter and(final Filterable... filters) {
        Filter[] filtersInt = convertToFilters(filters);
        return andInternal(filtersInt);
    }

    // Gets a filter that represents cards that meet ANY of the specified filters.
    public static Filter or(final Filterable... filters) {
        Filter[] filtersInt = convertToFilters(filters);
        return orInternal(filtersInt);
    }

    /**
     * Filter that accepts cards that are not the specified card.
     *
     * @param card the card
     * @return Filter
     */
    public static Filter not(PhysicalCard card) {
        return Filters.not(Filters.sameCardId(card));
    }

    /**
     * Filter that accepts cards that do not have the specified icon.
     *
     * @param icon the icon
     * @return Filter
     */
    public static Filter not(Icon icon) {
        return Filters.not(Filters.icon(icon));
    }

    /**
     * Filter that accepts cards that are not accepted by all the input filters.
     *
     * @param filters the filters
     * @return Filter
     */
    public static Filter not(final Filter filters) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return !Filters.and(filters).accepts(gameState, modifiersQuerying, physicalCard);
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                return !Filters.and(filters).accepts(gameState, modifiersQuerying, builtInCardBlueprint);
            }
            @Override
            public boolean acceptsIgnoringOwner(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return !Filters.and(filters).acceptsIgnoringOwner(gameState, modifiersQuerying, physicalCard);
            }
            @Override
            public boolean acceptsSingleModelType(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, ModelType modelTypeToCheck) {
                return !Filters.and(filters).acceptsSingleModelType(gameState, modifiersQuerying, physicalCard, modelTypeToCheck);
            }
        };
    }

    /**
     * Filter that accepts cards that are not the specified card.
     *
     * @param card the card
     * @return Filter
     */
    public static Filter except(PhysicalCard card) {
        return Filters.not(card);
    }

    /**
     * Filter that accepts cards that do not have the specified icon.
     *
     * @param icon the icon
     * @return Filter
     */
    public static Filter except(Icon icon) {
        return Filters.except(Filters.icon(icon));
    }

    /**
     * Filter that accepts cards that are not accepted by all the input filters.
     *
     * @param filters the filters
     * @return Filter
     */
    public static Filter except(final Filter filters) {
        return Filters.not(filters);
    }

    /**
     * Filter that accepts cards other than the specified card (and if the specified card is unique, then only cards
     * with a different title than the specified card).
     *
     * @param card the card
     * @return Filter
     */
    public static Filter other(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard card = gameState.findCardByPermanentId(permCardId);

                // If card is unique, then check if card title matches.
                if (card.getBlueprint().getUniqueness()==Uniqueness.UNIQUE
                        && sameTitle(card).accepts(gameState, modifiersQuerying, physicalCard)) {
                    return false;
                }

                return not(card).accepts(gameState, modifiersQuerying, physicalCard);
            }
        };
    }

    //
    //
    // This section defines Filters to match specific cards.
    //
    //

    // Gets a filter representing a specific card.
    public static Filter samePermanentCardId(PhysicalCard card) {
        final Integer permCardId = card.getPermanentCardId();
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return (physicalCard.getPermanentCardId() == permCardId);
            }
        };
    }

    // Gets a filter representing a specific card.
    public static Filter sameCardId(PhysicalCard card) {
        final List<Integer> cardIds = new LinkedList<Integer>();
        cardIds.add(card.getCardId());
        cardIds.addAll(card.getAdditionalCardIds());
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (cardIds.contains(physicalCard.getCardId())) {
                    return true;
                }
                for (Integer cardIdToCheck : physicalCard.getAdditionalCardIds()) {
                    if (cardIds.contains(cardIdToCheck)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    // Gets a filter representing a specific location (including if the location was converted).
    public static Filter sameLocationId(PhysicalCard card) {
        final List<Integer> locationIds = new LinkedList<Integer>();
        locationIds.add(card.getCardId());
        locationIds.addAll(card.getAdditionalCardIds());
        if (card.getZone() == Zone.LOCATIONS || card.getZone() == Zone.CONVERTED_LOCATIONS) {
            return new Filter() {
                @Override
                public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                    if ((physicalCard.getZone() == Zone.LOCATIONS || physicalCard.getZone() == Zone.CONVERTED_LOCATIONS)) {
                        if (locationIds.contains(physicalCard.getCardId())) {
                            return true;
                        }
                        for (Integer cardIdToCheck : physicalCard.getAdditionalCardIds()) {
                            if (locationIds.contains(cardIdToCheck)) {
                                return true;
                            }
                        }
                    }
                    return false;
                }
            };
        }
        else {
            return Filters.none;
        }
    }

    // Gets a filter representing any of a ground of specific locations (including if the location was converted).
    public static Filter sameLocationIds(Collection<PhysicalCard> cards) {
        final List<Integer> locationIds = new LinkedList<Integer>();
        for (PhysicalCard card : cards) {
            if (card.getZone() == Zone.LOCATIONS || card.getZone() == Zone.CONVERTED_LOCATIONS) {
                locationIds.add(card.getCardId());
                locationIds.addAll(card.getAdditionalCardIds());
            }
        }
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getZone() == Zone.LOCATIONS || physicalCard.getZone() == Zone.CONVERTED_LOCATIONS) {
                    if (locationIds.contains(physicalCard.getCardId())) {
                        return true;
                    }
                    for (Integer cardIdToCheck : physicalCard.getAdditionalCardIds()) {
                        if (locationIds.contains(cardIdToCheck)) {
                            return true;
                        }
                    }
                }
                return false;
            }
        };
    }

    // Gets a filter representing a specific built-in.
    public static Filter sameBuiltIn(final SwccgBuiltInCardBlueprint builtIn) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return false;
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                return Filters.sameCardId(builtIn.getPhysicalCard(gameState.getGame())).accepts(gameState, modifiersQuerying, builtInCardBlueprint.getPhysicalCard(gameState.getGame()))
                        && builtIn.getBuiltInId() == builtInCardBlueprint.getBuiltInId();
            }
       };
    }


    // Gets a filter representing the cards in the specified collection.
    public static Filter in(Collection<PhysicalCard> cards) {
        final List<Integer> cardIds = new LinkedList<Integer>();
        for (PhysicalCard card : cards) {
            cardIds.add(card.getCardId());
            cardIds.addAll(card.getAdditionalCardIds());
        }
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (cardIds.contains(physicalCard.getCardId())) {
                    return true;
                }
                for (Integer cardIdToCheck : physicalCard.getAdditionalCardIds()) {
                    if (cardIds.contains(cardIdToCheck)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    //
    //
    // Filtering collections
    //
    //

    /**
     * Determines if a cards accepted by the specified filters can be found in the specified collection.
     *
     * @param cards the cards to filter
     * @param game the game
     * @param filters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(Collection<? extends PhysicalCard> cards, SwccgGame game, Filterable filters) {
        return canSpot(cards, game, 1, true, filters);
    }

    /**
     * Determines if a cards accepted by the specified filters can be found in the specified collection.
     *
     * @param cards the cards to filter
     * @param game the game
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param filters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(Collection<? extends PhysicalCard> cards, SwccgGame game, boolean useAcceptsCount, Filterable filters) {
        return canSpot(cards, game, 1, useAcceptsCount, filters);
    }

    /**
     * Determines if at least a specified number of cards accepted by the specified filters can be found in the specified collection.
     *
     * @param cards the cards to filter
     * @param game the game
     * @param count the number of cards to find
     * @param filters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(Collection<? extends PhysicalCard> cards, SwccgGame game, int count, Filterable filters) {
        return canSpot(cards, game, count, false, filters);
    }

    /**
     * Determines if at least a specified number of cards accepted by the specified filters can be found in the specified collection.
     *
     * @param cards the cards to filter
     * @param game the game
     * @param count the number of cards to find
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param filters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(Collection<? extends PhysicalCard> cards, SwccgGame game, int count, boolean useAcceptsCount, Filterable filters) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        Set<PhysicalCard> result = new HashSet<PhysicalCard>();
        int totalCount = 0;
        for (PhysicalCard card : cards) {
            if (totalCount >= count)
                return true;

            if (!result.contains(card)) {
                if (useAcceptsCount) {
                    int curCount = Filters.and(filters).acceptsCount(gameState, modifiersQuerying, card);
                    if (curCount > 0) {
                        result.add(card);
                        totalCount = Math.min(count, totalCount + curCount);
                    }
                }
                else {
                    if (Filters.and(filters).accepts(gameState, modifiersQuerying, card)) {
                        result.add(card);
                        totalCount++;
                    }
                }
            }
        }
        return totalCount >= count;
    }

    /**
     * Determines if any cards accepted by the specified filters can be found in the specified collection.
     *
     * @param cards the cards to filter
     * @param game the game
     * @param source the source card performing the filtering
     * @param targetFiltersMap the map of targeting reason to filter, the targeting reason can affect which cards can be
     *                         seen by the source card.
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(Collection<? extends PhysicalCard> cards, SwccgGame game, PhysicalCard source, Map<TargetingReason, Filterable> targetFiltersMap) {
        return canSpot(cards, game, source, 1, true, targetFiltersMap);
    }

    /**
     * Determines if any cards accepted by the specified filters can be found in the specified collection.
     *
     * @param cards the cards to filter
     * @param game the game
     * @param source the source card performing the filtering
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param targetFiltersMap the map of targeting reason to filter, the targeting reason can affect which cards can be
     *                         seen by the source card.
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(Collection<? extends PhysicalCard> cards, SwccgGame game, PhysicalCard source, boolean useAcceptsCount, Map<TargetingReason, Filterable> targetFiltersMap) {
        return canSpot(cards, game, source, 1, useAcceptsCount, targetFiltersMap);
    }

    /**
     * Determines if at least a specified number of cards accepted by the specified filters can be found in the specified collection.
     *
     * @param cards the cards to filter
     * @param game the game
     * @param source the source card performing the filtering
     * @param count the number of cards to find
     * @param targetFiltersMap the map of targeting reason to filter, the targeting reason can affect which cards can be
     *                         seen by the source card.
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(Collection<? extends PhysicalCard> cards, SwccgGame game, PhysicalCard source, int count, Map<TargetingReason, Filterable> targetFiltersMap) {
        return canSpot(cards, game, source, count, true, targetFiltersMap);
    }

    /**
     * Determines if at least a specified number of cards accepted by the specified filters can be found in the specified collection.
     *
     * @param cards the cards to filter
     * @param game the game
     * @param source the source card performing the filtering
     * @param count the number of cards to find
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param targetFiltersMap the map of targeting reason to filter, the targeting reason can affect which cards can be
     *                         seen by the source card.
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(Collection<? extends PhysicalCard> cards, SwccgGame game, PhysicalCard source, int count, boolean useAcceptsCount, Map<TargetingReason, Filterable> targetFiltersMap) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        Set<PhysicalCard> result = new HashSet<PhysicalCard>();
        int totalCount = 0;
        for (TargetingReason targetingReason : targetFiltersMap.keySet()) {
            Filter filter = Filters.and(targetFiltersMap.get(targetingReason));
            if (targetingReason != TargetingReason.NONE) {
                filter = Filters.and(filter, Filters.canBeTargetedBy(source, targetingReason));
            }

            for (PhysicalCard card : cards) {
                if (totalCount >= count)
                    return true;

                if (!result.contains(card)) {
                    if (useAcceptsCount) {
                        int curCount = filter.acceptsCount(gameState, modifiersQuerying, card);
                        if (curCount > 0) {
                            result.add(card);
                            totalCount = Math.min(count, totalCount + curCount);
                        }
                    }
                    else {
                        if (filter.accepts(gameState, modifiersQuerying, card)) {
                            result.add(card);
                            totalCount++;
                        }
                    }
                }
            }

            if (totalCount >= count)
                return true;
        }
        return totalCount >= count;
    }

    /**
     * Gets the number of cards accepted by the specified filters can be found in the specified collection.
     *
     * @param cards the cards to filter
     * @param game the game
     * @param filters the filters
     * @return the number of cards found
     */
    public static int count(Collection<? extends PhysicalCard> cards, SwccgGame game, Filterable filters) {
        return count(cards, game, true, filters);
    }

    /**
     * Gets the number of cards accepted by the specified filters can be found in the specified collection.
     *
     * @param cards the cards to filter
     * @param game the game
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param filters the filters
     * @return the number of cards found
     */
    public static int count(Collection<? extends PhysicalCard> cards, SwccgGame game, boolean useAcceptsCount, Filterable filters) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        List<PhysicalCard> result = new LinkedList<PhysicalCard>();
        int totalCount = 0;
        for (PhysicalCard card : cards) {
            if (!result.contains(card)) {
                if (useAcceptsCount) {
                    int curCount = Filters.and(filters).acceptsCount(gameState, modifiersQuerying, card);
                    if (curCount > 0) {
                        result.add(card);
                        totalCount += curCount;
                    }
                }
                else {
                    if (Filters.and(filters).accepts(gameState, modifiersQuerying, card)) {
                        result.add(card);
                        totalCount++;
                    }
                }
            }
        }
        return totalCount;
    }

    /**
     * Gets the number of cards accepted by the specified filters can be found in the specified collection.
     *
     * @param cards the cards to filter
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param targetingReason the reason the source card is targeting
     * @param targetFilters the filters
     * @return the number of cards found
     */
    public static int count(Collection<? extends PhysicalCard> cards, SwccgGame game, PhysicalCard source,
                                                  TargetingReason targetingReason, Filterable targetFilters) {
        return count(cards, game, source, true, targetingReason, targetFilters);
    }

    /**
     * Gets the number of cards accepted by the specified filters can be found in the specified collection.
     *
     * @param cards the cards to filter
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param targetingReason the reason the source card is targeting
     * @param targetFilters the filters
     * @return the number of cards found
     */
    public static int count(Collection<? extends PhysicalCard> cards, SwccgGame game, PhysicalCard source, boolean useAcceptsCount,
                                                  TargetingReason targetingReason, Filterable targetFilters) {
        return count(cards, game, source, useAcceptsCount, Collections.singleton(targetingReason), targetFilters);
    }

    /**
     * Gets the number of cards accepted by the specified filters can be found in the specified collection.
     *
     * @param cards the cards to filter
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param targetingReasons the reasons the source card is targeting
     * @param targetFilters the filters
     * @return the number of cards found
     */
    public static int count(Collection<? extends PhysicalCard> cards, SwccgGame game, PhysicalCard source,
                                                  Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        return count(cards, game, source, true, targetingReasons, targetFilters);
    }

    /**
     * Gets the number of cards accepted by the specified filters can be found in the specified collection.
     *
     * @param cards the cards to filter
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param targetingReasons the reasons the source card is targeting
     * @param targetFilters the filters
     * @return the number of cards found
     */
    public static int count(Collection<? extends PhysicalCard> cards, SwccgGame game, PhysicalCard source, boolean useAcceptsCount,
                                                  Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        Map<TargetingReason, Filterable> targetFiltersMap = new HashMap<TargetingReason, Filterable>();
        for (TargetingReason targetingReason : targetingReasons) {
            targetFiltersMap.put(targetingReason, targetFilters);
        }
        return count(cards, game, source, useAcceptsCount, targetFiltersMap);
    }

    /**
     * Gets the number of cards accepted by the specified filters can be found in the specified collection.
     *
     * @param cards the cards to filter
     * @param game the game
     * @param source the source card performing the filtering
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param targetFiltersMap the map of targeting reason to filter, the targeting reason can affect which cards can be
     *                         seen by the source card.
     * @return the number of cards found
     */
    public static int count(Collection<? extends PhysicalCard> cards, SwccgGame game, PhysicalCard source, boolean useAcceptsCount, Map<TargetingReason, Filterable> targetFiltersMap) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        List<PhysicalCard> result = new LinkedList<PhysicalCard>();
        int totalCount = 0;
        for (TargetingReason targetingReason : targetFiltersMap.keySet()) {
            Filter filter = Filters.and(targetFiltersMap.get(targetingReason));
            if (targetingReason != TargetingReason.NONE) {
                filter = Filters.and(filter, Filters.canBeTargetedBy(source, targetingReason));
            }

            for (PhysicalCard card : cards) {
                if (!result.contains(card)) {
                    if (useAcceptsCount) {
                        int curCount = filter.acceptsCount(gameState, modifiersQuerying, card);
                        if (curCount > 0) {
                            result.add(card);
                            totalCount += curCount;
                        }
                    } else {
                        if (filter.accepts(gameState, modifiersQuerying, card)) {
                            result.add(card);
                            totalCount++;
                        }
                    }
                }
            }
        }
        return totalCount;
    }

    /**
     * Returns the collection of cards existing in the passed in collection that meet the specified filters.
     *
     * @param cards the cards to filter
     * @param game the game
     * @param filters the filters
     * @return the filtered cards
     */
    public static Collection<PhysicalCard> filter(Collection<? extends PhysicalCard> cards, SwccgGame game, Filterable filters) {
        return filter(cards, game, true, filters);
    }

    /**
     * Returns the collection of cards existing in the passed in collection that meet the specified filters.
     *
     * @param cards the cards to filter
     * @param game the game
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param filters the filters
     * @return the filtered cards
     */
    public static Collection<PhysicalCard> filter(Collection<? extends PhysicalCard> cards, SwccgGame game, boolean useAcceptsCount, Filterable filters) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        List<PhysicalCard> result = new LinkedList<PhysicalCard>();
        for (PhysicalCard card : cards) {
            if (!result.contains(card)) {
                if (useAcceptsCount) {
                    int curCount = Filters.and(filters).acceptsCount(gameState, modifiersQuerying, card);
                    if (curCount > 0) {
                        result.add(card);
                    }
                } else {
                    if (Filters.and(filters).accepts(gameState, modifiersQuerying, card)) {
                        result.add(card);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns the collection of cards existing in the passed in collection that meet the specified filters and can be
     * seen by the source card when targeted for specific reasons.
     *
     * @param cards the cards to filter
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param targetingReason the reason the source card is targeting
     * @param targetFilters the filters
     * @return true if matching card found, otherwise false
     */
    public static Collection<PhysicalCard> filter(Collection<? extends PhysicalCard> cards, SwccgGame game, PhysicalCard source,
                                                  TargetingReason targetingReason, Filterable targetFilters) {
        return filter(cards, game, source, true, targetingReason, targetFilters);
    }

    /**
     * Returns the collection of cards existing in the passed in collection that meet the specified filters and can be
     * seen by the source card when targeted for specific reasons.
     *
     * @param cards the cards to filter
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param targetingReason the reason the source card is targeting
     * @param targetFilters the filters
     * @return true if matching card found, otherwise false
     */
    public static Collection<PhysicalCard> filter(Collection<? extends PhysicalCard> cards, SwccgGame game, PhysicalCard source, boolean useAcceptsCount,
                                                  TargetingReason targetingReason, Filterable targetFilters) {
        return filter(cards, game, source, useAcceptsCount, Collections.singleton(targetingReason), targetFilters);
    }

    /**
     * Returns the collection of cards existing in the passed in collection that meet the specified filters and can be
     * seen by the source card when targeted for specific reasons.
     *
     * @param cards the cards to filter
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param targetingReasons the reasons the source card is targeting
     * @param targetFilters the filters
     * @return true if matching card found, otherwise false
     */
    public static Collection<PhysicalCard> filter(Collection<? extends PhysicalCard> cards, SwccgGame game, PhysicalCard source,
                                                  Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        return filter(cards, game, source, true, targetingReasons, targetFilters);
    }

    /**
     * Returns the collection of cards existing in the passed in collection that meet the specified filters and can be
     * seen by the source card when targeted for specific reasons.
     *
     * @param cards the cards to filter
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param targetingReasons the reasons the source card is targeting
     * @param targetFilters the filters
     * @return true if matching card found, otherwise false
     */
    public static Collection<PhysicalCard> filter(Collection<? extends PhysicalCard> cards, SwccgGame game, PhysicalCard source, boolean useAcceptsCount,
                                                  Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        Map<TargetingReason, Filterable> targetFiltersMap = new HashMap<TargetingReason, Filterable>();
        for (TargetingReason targetingReason : targetingReasons) {
            targetFiltersMap.put(targetingReason, targetFilters);
        }
        return filter(cards, game, source, useAcceptsCount, targetFiltersMap);
    }

    /**
     * Returns the collection of cards existing in the passed in collection that meet the specified filters and can be
     * seen by the source card when targeted for specific reasons.
     *
     * @param cards the cards to filter
     * @param game the game
     * @param source the source card performing the filtering
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param targetFiltersMap the map of targeting reason to filter, the targeting reason can affect which cards can be
     *                         seen by the source card.
     * @return the filtered cards
     */
    public static Collection<PhysicalCard> filter(Collection<? extends PhysicalCard> cards, SwccgGame game, PhysicalCard source, boolean useAcceptsCount, Map<TargetingReason, Filterable> targetFiltersMap) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        List<PhysicalCard> result = new LinkedList<PhysicalCard>();
        for (TargetingReason targetingReason : targetFiltersMap.keySet()) {
            Filter filter = Filters.and(targetFiltersMap.get(targetingReason));
            if (targetingReason != TargetingReason.NONE) {
                filter = Filters.and(filter, Filters.canBeTargetedBy(source, targetingReason));
            }

            for (PhysicalCard card : cards) {
                if (!result.contains(card)) {
                    if (useAcceptsCount) {
                        int curCount = filter.acceptsCount(gameState, modifiersQuerying, card);
                        if (curCount > 0) {
                            result.add(card);
                        }
                    } else {
                        if (filter.accepts(gameState, modifiersQuerying, card)) {
                            result.add(card);
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns the collection (up to the first X found) of cards existing in the passed in collection that meet the specified filters.
     *
     * @param cards the cards to filter
     * @param game the game
     * @param count the number of cards to find
     * @param filters the filters
     * @return the filtered cards
     */
    public static Collection<PhysicalCard> filterCount(Collection<? extends PhysicalCard> cards, SwccgGame game, int count, Filterable filters) {
        return filterCount(cards, game, count, true, filters);
    }

    /**
     * Returns the collection (up to the first X found) of cards existing in the passed in collection that meet the specified filters.
     *
     * @param cards the cards to filter
     * @param game the game
     * @param count the number of cards to find
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param filters the filters
     * @return the filtered cards
     */
    public static Collection<PhysicalCard> filterCount(Collection<? extends PhysicalCard> cards, SwccgGame game, int count, boolean useAcceptsCount, Filterable filters) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        List<PhysicalCard> result = new LinkedList<PhysicalCard>();
        int totalCount = 0;
        for (PhysicalCard card : cards) {
            if (totalCount >= count)
                break;

            if (!result.contains(card)) {
                if (useAcceptsCount) {
                    int curCount = Filters.and(filters).acceptsCount(gameState, modifiersQuerying, card);
                    if (curCount > 0) {
                        result.add(card);
                        totalCount = Math.min(count, totalCount + curCount);
                    }
                }
                else {
                    if (Filters.and(filters).accepts(gameState, modifiersQuerying, card)) {
                        result.add(card);
                        totalCount++;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns the collection (up to the first X found) of cards existing in the passed in collection that meet the specified filters.
     *
     * @param cards the cards to filter
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param count the number of cards to find
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param filters the filters
     * @return the filtered cards
     */
    private static Collection<PhysicalCard> filterCount(Collection<? extends PhysicalCard> cards, GameState gameState, ModifiersQuerying modifiersQuerying, int count, boolean useAcceptsCount, Filterable filters) {
        List<PhysicalCard> result = new LinkedList<PhysicalCard>();
        int totalCount = 0;
        for (PhysicalCard card : cards) {
            if (totalCount >= count)
                break;

            if (!result.contains(card)) {
                if (useAcceptsCount) {
                    int curCount = Filters.and(filters).acceptsCount(gameState, modifiersQuerying, card);
                    if (curCount > 0) {
                        result.add(card);
                        totalCount = Math.min(count, totalCount + curCount);
                    }
                }
                else {
                    if (Filters.and(filters).accepts(gameState, modifiersQuerying, card)) {
                        result.add(card);
                        totalCount++;
                    }
                }
            }
        }
        return result;
    }

    //
    //
    // Visiting "active" cards
    //
    //

    /**
     * Determines if an "active" card meeting the specified filters can be found.
     * No spot overrides or targeting reasons specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param filters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(SwccgGame game, PhysicalCard source, Filterable filters) {
        return canSpot(game, source, 1, filters);
    }

    /**
     * Determines if an "active" card meeting the specified filters can be found.
     * No spot overrides or targeting reasons specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param filters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(SwccgGame game, PhysicalCard source, boolean useAcceptsCount, Filterable filters) {
        return canSpot(game, source, 1, useAcceptsCount, filters);
    }

    /**
     * Determines if at least a specified number of "active" cards meeting the specified filters can be found.
     * No spot overrides or targeting reasons specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param count the number of cards to find
     * @param filters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(SwccgGame game, PhysicalCard source, int count, Filterable filters) {
        return canSpot(game, source, count, true, filters);
    }

    /**
     * Determines if at least a specified number of "active" cards meeting the specified filters can be found.
     * No spot overrides or targeting reasons specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param count the number of cards to find
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param filters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(SwccgGame game, PhysicalCard source, int count, boolean useAcceptsCount, Filterable filters) {
        SpotCountFilterCardInPlayVisitor visitor = new SpotCountFilterCardInPlayVisitor(game.getGameState(), game.getModifiersQuerying(), count, useAcceptsCount, convertToFilters(filters));
        return game.getGameState().iterateActiveCards(visitor, game.getModifiersQuerying(), source, null, null);
    }

    /**
     * Determines if an "active" card meeting the specified filters can be found.
     * No targeting reasons specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param filters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(SwccgGame game, PhysicalCard source, Map<InactiveReason, Boolean> spotOverrides, Filterable filters) {
        return canSpot(game, source, 1, spotOverrides, filters);
    }

    /**
     * Determines if an "active" card meeting the specified filters can be found.
     * No targeting reasons specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param filters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(SwccgGame game, PhysicalCard source, boolean useAcceptsCount, Map<InactiveReason, Boolean> spotOverrides, Filterable filters) {
        return canSpot(game, source, 1, useAcceptsCount, spotOverrides, filters);
    }

    /**
     * Determines if at least a specified number of "active" cards meeting the specified filters can be found.
     * No targeting reasons specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param count the number of cards to find
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param filters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(SwccgGame game, PhysicalCard source, int count, Map<InactiveReason, Boolean> spotOverrides, Filterable filters) {
        return canSpot(game, source, count, true, spotOverrides, filters);
    }

    /**
     * Determines if at least a specified number of "active" cards meeting the specified filters can be found.
     * No targeting reasons specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param count the number of cards to find
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param filters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(SwccgGame game, PhysicalCard source, int count, boolean useAcceptsCount, Map<InactiveReason, Boolean> spotOverrides, Filterable filters) {
        SpotCountFilterCardInPlayVisitor visitor = new SpotCountFilterCardInPlayVisitor(game.getGameState(), game.getModifiersQuerying(), count, useAcceptsCount, convertToFilters(filters));
        return game.getGameState().iterateActiveCards(visitor, game.getModifiersQuerying(), source, spotOverrides, null);
    }

    /**
     * Determines if an "active" card meeting the specified filters can be found.
     * No spot overrides specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param targetingReason the reason the source card is targeting
     * @param targetFilters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(SwccgGame game, PhysicalCard source,
                                  TargetingReason targetingReason, Filterable targetFilters) {
        return canSpot(game, source, 1, true, targetingReason, targetFilters);
    }

    /**
     * Determines if an "active" card meeting the specified filters can be found.
     * No spot overrides specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param targetingReason the reason the source card is targeting
     * @param targetFilters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(SwccgGame game, PhysicalCard source, boolean useAcceptsCount,
                                  TargetingReason targetingReason, Filterable targetFilters) {
        return canSpot(game, source, 1, useAcceptsCount, targetingReason, targetFilters);
    }

    /**
     * Determines if at least a specified number of "active" cards meeting the specified filters can be found.
     * No spot overrides specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param count the number of cards to find
     * @param targetingReason the reason the source card is targeting
     * @param targetFilters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(SwccgGame game, PhysicalCard source, int count,
                                  TargetingReason targetingReason, Filterable targetFilters) {
        return canSpot(game, source, count, true, targetingReason, targetFilters);
    }

    /**
     * Determines if at least a specified number of "active" cards meeting the specified filters can be found.
     * No spot overrides specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param count the number of cards to find
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param targetingReason the reason the source card is targeting
     * @param targetFilters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(SwccgGame game, PhysicalCard source, int count, boolean useAcceptsCount,
                                  TargetingReason targetingReason, Filterable targetFilters) {
        return canSpot(game, source, count, useAcceptsCount, Collections.singleton(targetingReason), targetFilters);
    }

    /**
     * Determines if an "active" card meeting the specified filters can be found.
     * No spot overrides specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param targetingReasons the reason the source card is targeting
     * @param targetFilters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(SwccgGame game, PhysicalCard source,
                                  Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        return canSpot(game, source, 1, true, targetingReasons, targetFilters);
    }

    /**
     * Determines if an "active" card meeting the specified filters can be found.
     * No spot overrides specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param targetingReasons the reason the source card is targeting
     * @param targetFilters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(SwccgGame game, PhysicalCard source, boolean useAcceptsCount,
                                  Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        return canSpot(game, source, 1, useAcceptsCount, targetingReasons, targetFilters);
    }

    /**
     * Determines if at least a specified number of "active" cards meeting the specified filters can be found.
     * No spot overrides specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param count the number of cards to find
     * @param targetingReasons the reason the source card is targeting
     * @param targetFilters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(SwccgGame game, PhysicalCard source, int count,
                                  Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        return canSpot(game, source, count, true, targetingReasons, targetFilters);
    }

    /**
     * Determines if at least a specified number of "active" cards meeting the specified filters can be found.
     * No spot overrides specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param count the number of cards to find
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param targetingReasons the reason the source card is targeting
     * @param targetFilters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(SwccgGame game, PhysicalCard source, int count, boolean useAcceptsCount,
                                  Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        return canSpot(game, source, count, useAcceptsCount, null, targetingReasons, targetFilters);
    }

    /**
     * Determines if an "active" card meeting the specified filters can be found.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetingReason the reason the source card is targeting
     * @param targetFilters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(SwccgGame game, PhysicalCard source,
                                  Map<InactiveReason, Boolean> spotOverrides, TargetingReason targetingReason, Filterable targetFilters) {
        return canSpot(game, source, 1, true, spotOverrides, targetingReason, targetFilters);
    }

    /**
     * Determines if an "active" card meeting the specified filters can be found.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetingReason the reason the source card is targeting
     * @param targetFilters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(SwccgGame game, PhysicalCard source, boolean useAcceptsCount,
                                  Map<InactiveReason, Boolean> spotOverrides, TargetingReason targetingReason, Filterable targetFilters) {
        return canSpot(game, source, 1, useAcceptsCount, spotOverrides, targetingReason, targetFilters);
    }

    /**
     * Determines if at least a specified number of "active" cards meeting the specified filters can be found.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param count the number of cards to find
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetingReason the reason the source card is targeting
     * @param targetFilters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(SwccgGame game, PhysicalCard source, int count,
                                  Map<InactiveReason, Boolean> spotOverrides, TargetingReason targetingReason, Filterable targetFilters) {
        return canSpot(game, source, count, true, spotOverrides, targetingReason, targetFilters);
    }

    /**
     * Determines if at least a specified number of "active" cards meeting the specified filters can be found.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param count the number of cards to find
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetingReason the reason the source card is targeting
     * @param targetFilters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(SwccgGame game, PhysicalCard source, int count, boolean useAcceptsCount,
                                  Map<InactiveReason, Boolean> spotOverrides, TargetingReason targetingReason, Filterable targetFilters) {
        return canSpot(game, source, count, useAcceptsCount, spotOverrides, Collections.singleton(targetingReason), targetFilters);
    }

    /**
     * Determines if an "active" card meeting the specified filters can be found.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetingReasons the reason the source card is targeting
     * @param targetFilters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(SwccgGame game, PhysicalCard source,
                                  Map<InactiveReason, Boolean> spotOverrides, Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        return canSpot(game, source, 1, true, spotOverrides, targetingReasons, targetFilters);
    }

    /**
     * Determines if an "active" card meeting the specified filters can be found.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetingReasons the reason the source card is targeting
     * @param targetFilters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(SwccgGame game, PhysicalCard source, boolean useAcceptsCount,
                                  Map<InactiveReason, Boolean> spotOverrides, Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        return canSpot(game, source, 1, useAcceptsCount, spotOverrides, targetingReasons, targetFilters);
    }

    /**
     * Determines if at least a specified number of "active" cards meeting the specified filters can be found.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param count the number of cards to find
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetingReasons the reason the source card is targeting
     * @param targetFilters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(SwccgGame game, PhysicalCard source, int count,
                                  Map<InactiveReason, Boolean> spotOverrides, Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        return canSpot(game, source, count, true, spotOverrides, targetingReasons, targetFilters);
    }

    /**
     * Determines if at least a specified number of "active" cards meeting the specified filters can be found.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param count the number of cards to find
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetingReasons the reason the source card is targeting
     * @param targetFilters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(SwccgGame game, PhysicalCard source, int count, boolean useAcceptsCount,
                                  Map<InactiveReason, Boolean> spotOverrides, Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        Map<TargetingReason, Filterable> targetFiltersMap = new HashMap<TargetingReason, Filterable>();
        for (TargetingReason targetingReason : targetingReasons) {
            targetFiltersMap.put(targetingReason, targetFilters);
        }
        return canSpot(game, source, count, useAcceptsCount, spotOverrides, targetFiltersMap);
    }

    /**
     * Determines if an "active" card meeting the specified filters can be found.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetFiltersMap the map of targeting reason to filter, the targeting reason can affect which cards can be
     *                         seen by the source card.
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(SwccgGame game, PhysicalCard source,
                                  Map<InactiveReason, Boolean> spotOverrides, Map<TargetingReason, Filterable> targetFiltersMap) {
        return canSpot(game, source, 1, spotOverrides, targetFiltersMap);
    }

    /**
     * Determines if an "active" card meeting the specified filters can be found.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetFiltersMap the map of targeting reason to filter, the targeting reason can affect which cards can be
     *                         seen by the source card.
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(SwccgGame game, PhysicalCard source, boolean useAcceptsCount,
                                  Map<InactiveReason, Boolean> spotOverrides, Map<TargetingReason, Filterable> targetFiltersMap) {
        return canSpot(game, source, 1, useAcceptsCount, spotOverrides, targetFiltersMap);
    }

    /**
     * Determines if at least a specified number of "active" cards meeting the specified filters can be found.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param count the number of cards to find
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetFiltersMap the map of targeting reason to filter, the targeting reason can affect which cards can be
     *                         seen by the source card.
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(SwccgGame game, PhysicalCard source, int count,
                                  Map<InactiveReason, Boolean> spotOverrides, Map<TargetingReason, Filterable> targetFiltersMap) {
        return canSpot(game, source, count, true, spotOverrides, targetFiltersMap);
    }

    /**
     * Determines if at least a specified number of "active" cards meeting the specified filters can be found.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetFiltersMap the map of targeting reason to filter, the targeting reason can affect which cards can be
     *                         seen by the source card.
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpot(SwccgGame game, PhysicalCard source, int count, boolean useAcceptsCount,
                                  Map<InactiveReason, Boolean> spotOverrides, Map<TargetingReason, Filterable> targetFiltersMap) {
        SpotCountFilterCardInPlayVisitor visitor = new SpotCountFilterCardInPlayVisitor(game.getGameState(), game.getModifiersQuerying(), count, useAcceptsCount, convertToFilters(targetFiltersMap.values().toArray(new Filterable[targetFiltersMap.values().size()])));
        return game.getGameState().iterateActiveCards(visitor, game.getModifiersQuerying(), source, spotOverrides, targetFiltersMap);
    }

    /**
     * Gets the first "active" card found accepted by the specified filters.
     * No spot overrides or targeting reasons specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param filters the filters
     * @return the card found
     */
    public static PhysicalCard findFirstActive(SwccgGame game, PhysicalCard source, Filterable filters) {
        return findFirstActive(game, source, true, filters);
    }

    /**
     * Gets the first "active" card found accepted by the specified filters.
     * No spot overrides or targeting reasons specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param filters the filters
     * @return the card found
     */
    public static PhysicalCard findFirstActive(SwccgGame game, PhysicalCard source, boolean useAcceptsCount, Filterable filters) {
        SpotFilterCardInPlayVisitor visitor = new SpotFilterCardInPlayVisitor(game.getGameState(), game.getModifiersQuerying(), useAcceptsCount, convertToFilters(filters));
        game.getGameState().iterateActiveCards(visitor, game.getModifiersQuerying(), source, null, null);
        return visitor.getCard();
    }

    /**
     * Gets the first "active" card found accepted by the specified filters.
     * No targeting reasons specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param filters the filters
     * @return the card found
     */
    public static PhysicalCard findFirstActive(SwccgGame game, PhysicalCard source,
                                               Map<InactiveReason, Boolean> spotOverrides, Filterable filters) {
        return findFirstActive(game, source, true, spotOverrides, filters);
    }

    /**
     * Gets the first "active" card found accepted by the specified filters.
     * No targeting reasons specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param filters the filters
     * @return the card found
     */
    public static PhysicalCard findFirstActive(SwccgGame game, PhysicalCard source, boolean useAcceptsCount,
                                               Map<InactiveReason, Boolean> spotOverrides, Filterable filters) {
        SpotFilterCardInPlayVisitor visitor = new SpotFilterCardInPlayVisitor(game.getGameState(), game.getModifiersQuerying(), useAcceptsCount, convertToFilters(filters));
        game.getGameState().iterateActiveCards(visitor, game.getModifiersQuerying(), source, spotOverrides, null);
        return visitor.getCard();
    }

    /**
     * Gets the first "active" card found accepted by the specified filters.
     * No spot overrides specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param targetingReason the reason the source card is targeting
     * @param targetFilters the filters
     * @return the card found
     */
    public static PhysicalCard findFirstActive(SwccgGame game, PhysicalCard source,
                                               TargetingReason targetingReason, Filterable targetFilters) {
        return findFirstActive(game, source, Collections.singleton(targetingReason), targetFilters);
    }

    /**
     * Gets the first "active" card found accepted by the specified filters.
     * No spot overrides specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param targetingReason the reason the source card is targeting
     * @param targetFilters the filters
     * @return the card found
     */
    public static PhysicalCard findFirstActive(SwccgGame game, PhysicalCard source, boolean useAcceptsCount,
                                               TargetingReason targetingReason, Filterable targetFilters) {
        return findFirstActive(game, source, useAcceptsCount, Collections.singleton(targetingReason), targetFilters);
    }

    /**
     * Gets the first "active" card found accepted by the specified filters.
     * No spot overrides specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param targetingReasons the reason the source card is targeting
     * @param targetFilters the filters
     * @return the card found
     */
    public static PhysicalCard findFirstActive(SwccgGame game, PhysicalCard source,
                                               Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        return findFirstActive(game, source, true, targetingReasons, targetFilters);
    }

    /**
     * Gets the first "active" card found accepted by the specified filters.
     * No spot overrides specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param targetingReasons the reason the source card is targeting
     * @param targetFilters the filters
     * @return the card found
     */
    public static PhysicalCard findFirstActive(SwccgGame game, PhysicalCard source, boolean useAcceptsCount,
                                               Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        return findFirstActive(game, source, useAcceptsCount, null, targetingReasons, targetFilters);
    }

    /**
     * Gets the first "active" card found accepted by the specified filters.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetingReason the reason the source card is targeting
     * @param targetFilters the filters
     * @return the card found
     */
    public static PhysicalCard findFirstActive(SwccgGame game, PhysicalCard source,
                                               Map<InactiveReason, Boolean> spotOverrides, TargetingReason targetingReason, Filterable targetFilters) {
        return findFirstActive(game, source, spotOverrides, Collections.singleton(targetingReason), targetFilters);
    }

    /**
     * Gets the first "active" card found accepted by the specified filters.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetingReason the reason the source card is targeting
     * @param targetFilters the filters
     * @return the card found
     */
    public static PhysicalCard findFirstActive(SwccgGame game, PhysicalCard source, boolean useAcceptsCount,
                                               Map<InactiveReason, Boolean> spotOverrides, TargetingReason targetingReason, Filterable targetFilters) {
        return findFirstActive(game, source, useAcceptsCount, spotOverrides, Collections.singleton(targetingReason), targetFilters);
    }

    /**
     * Gets the first "active" card found accepted by the specified filters.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetingReasons the reason the source card is targeting
     * @param targetFilters the filters
     * @return the card found
     */
    public static PhysicalCard findFirstActive(SwccgGame game, PhysicalCard source,
                                               Map<InactiveReason, Boolean> spotOverrides, Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        return findFirstActive(game, source, true, spotOverrides, targetingReasons, targetFilters);
    }

    /**
     * Gets the first "active" card found accepted by the specified filters.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetingReasons the reason the source card is targeting
     * @param targetFilters the filters
     * @return the card found
     */
    public static PhysicalCard findFirstActive(SwccgGame game, PhysicalCard source, boolean useAcceptsCount,
                                               Map<InactiveReason, Boolean> spotOverrides, Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        Map<TargetingReason, Filterable> targetFiltersMap = new HashMap<TargetingReason, Filterable>();
        for (TargetingReason targetingReason : targetingReasons) {
            targetFiltersMap.put(targetingReason, targetFilters);
        }
        return findFirstActive(game, source, useAcceptsCount, spotOverrides, targetFiltersMap);
    }

    /**
     * Gets the first "active" card found accepted by the specified filters.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetFiltersMap the map of targeting reason to filter, the targeting reason can affect which cards can be
     *                         seen by the source card.
     * @return the card found
     */
    public static PhysicalCard findFirstActive(SwccgGame game, PhysicalCard source,
                                               Map<InactiveReason, Boolean> spotOverrides, Map<TargetingReason, Filterable> targetFiltersMap) {
        return findFirstActive(game, source, true, spotOverrides, targetFiltersMap);
    }

    /**
     * Gets the first "active" card found accepted by the specified filters.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetFiltersMap the map of targeting reason to filter, the targeting reason can affect which cards can be
     *                         seen by the source card.
     * @return the card found
     */
    public static PhysicalCard findFirstActive(SwccgGame game, PhysicalCard source, boolean useAcceptsCount,
                                               Map<InactiveReason, Boolean> spotOverrides, Map<TargetingReason, Filterable> targetFiltersMap) {
        SpotFilterCardInPlayVisitor visitor = new SpotFilterCardInPlayVisitor(game.getGameState(), game.getModifiersQuerying(), useAcceptsCount, convertToFilters(targetFiltersMap.values().toArray(new Filterable[targetFiltersMap.values().size()])));
        game.getGameState().iterateActiveCards(visitor, game.getModifiersQuerying(), source, spotOverrides, targetFiltersMap);
        return visitor.getCard();
    }

    /**
     * Gets the "active" cards found accepted by the specified filters.
     * No spot overrides or targeting reasons specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param filters the filters
     * @return the cards found
     */
    public static Collection<PhysicalCard> filterActive(SwccgGame game, PhysicalCard source, Filterable filters) {
        return filterActive(game, source, true, filters);
    }

    /**
     * Gets the "active" cards found accepted by the specified filters.
     * No spot overrides or targeting reasons specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param filters the filters
     * @return the cards found
     */
    public static Collection<PhysicalCard> filterActive(SwccgGame game, PhysicalCard source, boolean useAcceptsCount, Filterable filters) {
        GetCardsMatchingFilterVisitor visitor = new GetCardsMatchingFilterVisitor(game.getGameState(), game.getModifiersQuerying(), useAcceptsCount, convertToFilters(filters));
        game.getGameState().iterateActiveCards(visitor, game.getModifiersQuerying(), source, null, null);
        return visitor.getPhysicalCards();
    }

    /**
     * Gets the "active" cards found accepted by the specified filters.
     * No targeting reasons specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param filters the filters
     * @return the cards found
     */
    public static Collection<PhysicalCard> filterActive(SwccgGame game, PhysicalCard source,
                                                        Map<InactiveReason, Boolean> spotOverrides, Filterable filters) {
        return filterActive(game, source, true, spotOverrides, filters);
    }

    /**
     * Gets the "active" cards found accepted by the specified filters.
     * No targeting reasons specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param filters the filters
     * @return the cards found
     */
    public static Collection<PhysicalCard> filterActive(SwccgGame game, PhysicalCard source, boolean useAcceptsCount,
                                                        Map<InactiveReason, Boolean> spotOverrides, Filterable filters) {
        GetCardsMatchingFilterVisitor visitor = new GetCardsMatchingFilterVisitor(game.getGameState(), game.getModifiersQuerying(), useAcceptsCount, convertToFilters(filters));
        game.getGameState().iterateActiveCards(visitor, game.getModifiersQuerying(), source, spotOverrides, null);
        return visitor.getPhysicalCards();
    }

    /**
     * Gets the "active" cards found accepted by the specified filters.
     * No spot overrides specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param targetingReason the reason the source card is targeting
     * @param targetFilters the filters
     * @return the cards found
     */
    public static Collection<PhysicalCard> filterActive(SwccgGame game, PhysicalCard source,
                                                        TargetingReason targetingReason, Filterable targetFilters) {
        return filterActive(game, source, true, targetingReason, targetFilters);
    }

    /**
     * Gets the "active" cards found accepted by the specified filters.
     * No spot overrides specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param targetingReason the reason the source card is targeting
     * @param targetFilters the filters
     * @return the cards found
     */
    public static Collection<PhysicalCard> filterActive(SwccgGame game, PhysicalCard source, boolean useAcceptsCount,
                                                        TargetingReason targetingReason, Filterable targetFilters) {
        return filterActive(game, source, useAcceptsCount, Collections.singleton(targetingReason), targetFilters);
    }

    /**
     * Gets the "active" cards found accepted by the specified filters.
     * No spot overrides specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param targetingReasons the reasons the source card is targeting
     * @param targetFilters the filters
     * @return the cards found
     */
    public static Collection<PhysicalCard> filterActive(SwccgGame game, PhysicalCard source,
                                                        Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        return filterActive(game, source, true, targetingReasons, targetFilters);
    }

    /**
     * Gets the "active" cards found accepted by the specified filters.
     * No spot overrides specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param targetingReasons the reasons the source card is targeting
     * @param targetFilters the filters
     * @return the cards found
     */
    public static Collection<PhysicalCard> filterActive(SwccgGame game, PhysicalCard source, boolean useAcceptsCount,
                                                        Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        return filterActive(game, source, useAcceptsCount, null, targetingReasons, targetFilters);
    }

    /**
     * Gets the "active" cards found accepted by the specified filters.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetingReason the reason the source card is targeting
     * @param targetFilters the filters
     * @return the cards found
     */
    public static Collection<PhysicalCard> filterActive(SwccgGame game, PhysicalCard source,
                                                        Map<InactiveReason, Boolean> spotOverrides, TargetingReason targetingReason, Filterable targetFilters) {
        return filterActive(game, source, true, spotOverrides, targetingReason, targetFilters);
    }

    /**
     * Gets the "active" cards found accepted by the specified filters.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetingReason the reason the source card is targeting
     * @param targetFilters the filters
     * @return the cards found
     */
    public static Collection<PhysicalCard> filterActive(SwccgGame game, PhysicalCard source, boolean useAcceptsCount,
                                                        Map<InactiveReason, Boolean> spotOverrides, TargetingReason targetingReason, Filterable targetFilters) {
        return filterActive(game, source, useAcceptsCount, spotOverrides, Collections.singleton(targetingReason), targetFilters);
    }

    /**
     * Gets the "active" cards found accepted by the specified filters.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetingReasons the reasons the source card is targeting
     * @param targetFilters the filters
     * @return the cards found
     */
    public static Collection<PhysicalCard> filterActive(SwccgGame game, PhysicalCard source,
                                                        Map<InactiveReason, Boolean> spotOverrides, Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        return filterActive(game, source, true, spotOverrides, targetingReasons, targetFilters);
    }

    /**
     * Gets the "active" cards found accepted by the specified filters.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetingReasons the reasons the source card is targeting
     * @param targetFilters the filters
     * @return the cards found
     */
    public static Collection<PhysicalCard> filterActive(SwccgGame game, PhysicalCard source, boolean useAcceptsCount,
                                                        Map<InactiveReason, Boolean> spotOverrides, Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        Map<TargetingReason, Filterable> targetFiltersMap = new HashMap<TargetingReason, Filterable>();
        for (TargetingReason targetingReason : targetingReasons) {
            targetFiltersMap.put(targetingReason, targetFilters);
        }
        return filterActive(game, source, useAcceptsCount, spotOverrides, targetFiltersMap);
    }

    /**
     * Gets the "active" cards found accepted by the specified filters.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetFiltersMap the map of targeting reason to filter, the targeting reason can affect which cards can be
     *                         seen by the source card.
     * @return the cards found
     */
    public static Collection<PhysicalCard> filterActive(SwccgGame game, PhysicalCard source,
                                                        Map<InactiveReason, Boolean> spotOverrides, Map<TargetingReason, Filterable> targetFiltersMap) {
        return filterActive(game, source, true, spotOverrides, targetFiltersMap);
    }

    /**
     * Gets the "active" cards found accepted by the specified filters.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetFiltersMap the map of targeting reason to filter, the targeting reason can affect which cards can be
     *                         seen by the source card.
     * @return the cards found
     */
    public static Collection<PhysicalCard> filterActive(SwccgGame game, PhysicalCard source, boolean useAcceptsCount,
                                                        Map<InactiveReason, Boolean> spotOverrides, Map<TargetingReason, Filterable> targetFiltersMap) {
        GetCardsMatchingFilterVisitor visitor = new GetCardsMatchingFilterVisitor(game.getGameState(), game.getModifiersQuerying(), useAcceptsCount, convertToFilters(targetFiltersMap.values().toArray(new Filterable[targetFiltersMap.values().size()])));
        game.getGameState().iterateActiveCards(visitor, game.getModifiersQuerying(), source, spotOverrides, targetFiltersMap);
        return visitor.getPhysicalCards();
    }

    /**
     * Counts the number of "active" cards found accepted by the specified filters.
     * No spot overrides or targeting reasons specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param filters the filters
     * @return the number of cards found
     */
    public static int countActive(SwccgGame game, PhysicalCard source, Filterable filters) {
        return countActive(game, source, true, filters);
    }

    /**
     * Counts the number of "active" cards found accepted by the specified filters.
     * No spot overrides or targeting reasons specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param filters the filters
     * @return the number of cards found
     */
    public static int countActive(SwccgGame game, PhysicalCard source, boolean useAcceptsCount, Filterable filters) {
        GetCardsMatchingFilterVisitor visitor = new GetCardsMatchingFilterVisitor(game.getGameState(), game.getModifiersQuerying(), useAcceptsCount, convertToFilters(filters));
        game.getGameState().iterateActiveCards(visitor, game.getModifiersQuerying(), source, null, null);
        return visitor.getCounter();
    }

    /**
     * Counts the number of "active" cards found accepted by the specified filters.
     * No targeting reasons specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param filters the filters
     * @return the number of cards found
     */
    public static int countActive(SwccgGame game, PhysicalCard source,
                                  Map<InactiveReason, Boolean> spotOverrides, Filterable filters) {
        return countActive(game, source, true, spotOverrides, filters);
    }

    /**
     * Counts the number of "active" cards found accepted by the specified filters.
     * No targeting reasons specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param filters the filters
     * @return the number of cards found
     */
    public static int countActive(SwccgGame game, PhysicalCard source, boolean useAcceptsCount,
                                  Map<InactiveReason, Boolean> spotOverrides, Filterable filters) {
        GetCardsMatchingFilterVisitor visitor = new GetCardsMatchingFilterVisitor(game.getGameState(), game.getModifiersQuerying(), useAcceptsCount, convertToFilters(filters));
        game.getGameState().iterateActiveCards(visitor, game.getModifiersQuerying(), source, spotOverrides, null);
        return visitor.getCounter();
    }

    /**
     * Gets the number of "active" cards found accepted by the specified filters.
     * No spot overrides specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param targetingReason the reason the source card is targeting
     * @param targetFilters the filters
     * @return the number of cards found
     */
    public static int countActive(SwccgGame game, PhysicalCard source,
                                  TargetingReason targetingReason, Filterable targetFilters) {
        return countActive(game, source, true, targetingReason, targetFilters);
    }

    /**
     * Gets the number of "active" cards found accepted by the specified filters.
     * No spot overrides specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param targetingReason the reason the source card is targeting
     * @param targetFilters the filters
     * @return the number of cards found
     */
    public static int countActive(SwccgGame game, PhysicalCard source, boolean useAcceptsCount,
                                  TargetingReason targetingReason, Filterable targetFilters) {
        return countActive(game, source, useAcceptsCount, Collections.singleton(targetingReason), targetFilters);
    }

    /**
     * Gets the number of "active" cards found accepted by the specified filters.
     * No spot overrides specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param targetingReasons the reasons the source card is targeting
     * @param targetFilters the filters
     * @return the number of cards found
     */
    public static int countActive(SwccgGame game, PhysicalCard source,
                                  Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        return countActive(game, source, true, targetingReasons, targetFilters);
    }

    /**
     * Gets the number of "active" cards found accepted by the specified filters.
     * No spot overrides specified.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param targetingReasons the reasons the source card is targeting
     * @param targetFilters the filters
     * @return the number of cards found
     */
    public static int countActive(SwccgGame game, PhysicalCard source, boolean useAcceptsCount,
                                  Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        return countActive(game, source, useAcceptsCount, null, targetingReasons, targetFilters);
    }

    /**
     * Gets the number of "active" cards found accepted by the specified filters.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetingReason the reason the source card is targeting
     * @param targetFilters the filters
     * @return the number of cards found
     */
    public static int countActive(SwccgGame game, PhysicalCard source,
                                  Map<InactiveReason, Boolean> spotOverrides, TargetingReason targetingReason, Filterable targetFilters) {
        return countActive(game, source, true, spotOverrides, targetingReason, targetFilters);
    }

    /**
     * Gets the number of "active" cards found accepted by the specified filters.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetingReason the reason the source card is targeting
     * @param targetFilters the filters
     * @return the number of cards found
     */
    public static int countActive(SwccgGame game, PhysicalCard source, boolean useAcceptsCount,
                                  Map<InactiveReason, Boolean> spotOverrides, TargetingReason targetingReason, Filterable targetFilters) {
        return countActive(game, source, useAcceptsCount, spotOverrides, Collections.singleton(targetingReason), targetFilters);
    }

    /**
     * Gets the number of "active" cards found accepted by the specified filters.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetingReasons the reasons the source card is targeting
     * @param targetFilters the filters
     * @return the number of cards found
     */
    public static int countActive(SwccgGame game, PhysicalCard source,
                                  Map<InactiveReason, Boolean> spotOverrides, Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        return countActive(game, source, true, spotOverrides, targetingReasons, targetFilters);
    }

    /**
     * Gets the number of "active" cards found accepted by the specified filters.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetingReasons the reasons the source card is targeting
     * @param targetFilters the filters
     * @return the number of cards found
     */
    public static int countActive(SwccgGame game, PhysicalCard source, boolean useAcceptsCount,
                                  Map<InactiveReason, Boolean> spotOverrides, Set<TargetingReason> targetingReasons, Filterable targetFilters) {
        Map<TargetingReason, Filterable> targetFiltersMap = new HashMap<TargetingReason, Filterable>();
        for (TargetingReason targetingReason : targetingReasons) {
            targetFiltersMap.put(targetingReason, targetFilters);
        }
        return countActive(game, source, useAcceptsCount, spotOverrides, targetFiltersMap);
    }

    /**
     * Gets the number of "active" cards found accepted by the specified filters.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetFiltersMap the filters
     * @return the number of cards found
     */
    public static int countActive(SwccgGame game, PhysicalCard source,
                                  Map<InactiveReason, Boolean> spotOverrides, Map<TargetingReason, Filterable> targetFiltersMap) {
        return countActive(game, source, true, spotOverrides, targetFiltersMap);
    }

    /**
     * Gets the number of "active" cards found accepted by the specified filters.
     *
     * @param game the game
     * @param source the source card performing the query, or null if game is performing the query
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param targetFiltersMap the filters
     * @return the number of cards found
     */
    public static int countActive(SwccgGame game, PhysicalCard source, boolean useAcceptsCount,
                                  Map<InactiveReason, Boolean> spotOverrides, Map<TargetingReason, Filterable> targetFiltersMap) {
        GetCardsMatchingFilterVisitor visitor = new GetCardsMatchingFilterVisitor(game.getGameState(), game.getModifiersQuerying(), useAcceptsCount, convertToFilters(targetFiltersMap.values().toArray(new Filterable[targetFiltersMap.values().size()])));
        game.getGameState().iterateActiveCards(visitor, game.getModifiersQuerying(), source, spotOverrides, targetFiltersMap);
        return visitor.getCounter();
    }

    //
    //
    // Visiting top locations on table
    //
    //

    /**
     * Determines if a card accepted by the specified filters can be found in the top locations on table.
     * @param game the game
     * @param filters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpotFromTopLocationsOnTable(SwccgGame game, Filterable filters) {
        SpotFilterCardInPlayVisitor visitor = new SpotFilterCardInPlayVisitor(game.getGameState(), game.getModifiersQuerying(), false, convertToFilters(filters));
        return game.getGameState().iterateLocationsOnTable(visitor, false);
    }

    /**
     * Determines if at least a specified number of cards accepted by the specified filters can be found in the top locations on table.
     * @param game the game
     * @param count the number of cards to find
     * @param filters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpotFromTopLocationsOnTable(SwccgGame game, int count, Filterable filters) {
        SpotCountFilterCardInPlayVisitor visitor = new SpotCountFilterCardInPlayVisitor(game.getGameState(), game.getModifiersQuerying(), count, false, convertToFilters(filters));
        return game.getGameState().iterateLocationsOnTable(visitor, false);
    }

    /**
     * Gets the first card found accepted by the specified filters found in the top locations on table.
     * @param game the game
     * @param filters the filters
     * @return the card found
     */
    public static PhysicalCard findFirstFromTopLocationsOnTable(SwccgGame game, Filterable filters) {
        SpotFilterCardInPlayVisitor visitor = new SpotFilterCardInPlayVisitor(game.getGameState(), game.getModifiersQuerying(), false, convertToFilters(filters));
        game.getGameState().iterateLocationsOnTable(visitor, false);
        return visitor.getCard();
    }

    /**
     * Gets the cards accepted by the specified filters found in the top locations on table.
     * @param game the game
     * @param filters the filters
     * @return the cards found
     */
    public static Collection<PhysicalCard> filterTopLocationsOnTable(SwccgGame game, Filterable filters) {
        GetCardsMatchingFilterVisitor visitor = new GetCardsMatchingFilterVisitor(game.getGameState(), game.getModifiersQuerying(), false, convertToFilters(filters));
        game.getGameState().iterateLocationsOnTable(visitor, false);
        return visitor.getPhysicalCards();
    }

    /**
     * Gets the number of top locations on table accepted by the input filter.
     * @param game the game
     * @param filters the filters
     * @return the number of cards found
     */
    public static int countTopLocationsOnTable(SwccgGame game, Filterable filters) {
        GetCardsMatchingFilterVisitor visitor = new GetCardsMatchingFilterVisitor(game.getGameState(), game.getModifiersQuerying(), false, convertToFilters(filters));
        game.getGameState().iterateLocationsOnTable(visitor, false);
        return visitor.getCounter();
    }

    //
    //
    // Visiting converted locations on table
    //
    //

    /**
     * Determines if a card accepted by the specified filters can be found in the converted locations on table.
     * @param game the game
     * @param filters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpotFromConvertedLocationsOnTable(SwccgGame game, Filterable filters) {
        SpotFilterCardInPlayVisitor visitor = new SpotFilterCardInPlayVisitor(game.getGameState(), game.getModifiersQuerying(), false, convertToFilters(filters));
        return game.getGameState().iterateLocationsOnTable(visitor, true);
    }

    /**
     * Determines if at least a specified number of cards accepted by the specified filters can be found in converted locations on table.
     * @param game the game
     * @param count the number of cards to find
     * @param filters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpotFromConvertedLocationsOnTable(SwccgGame game, int count, Filterable filters) {
        SpotCountFilterCardInPlayVisitor visitor = new SpotCountFilterCardInPlayVisitor(game.getGameState(), game.getModifiersQuerying(), count, false, convertToFilters(filters));
        return game.getGameState().iterateLocationsOnTable(visitor, true);
    }

    /**
     * Gets the first card found accepted by the specified filters found in the converted locations on table.
     * @param game the game
     * @param filters the filters
     * @return the card found
     */
    public static PhysicalCard findFirstFromConvertedLocationsOnTable(SwccgGame game, Filterable filters) {
        SpotFilterCardInPlayVisitor visitor = new SpotFilterCardInPlayVisitor(game.getGameState(), game.getModifiersQuerying(), false, convertToFilters(filters));
        game.getGameState().iterateLocationsOnTable(visitor, true);
        return visitor.getCard();
    }

    /**
     * Gets the cards accepted by the specified filters found in the converted locations on table.
     * @param game the game
     * @param filters the filters
     * @return the cards found
     */
    public static Collection<PhysicalCard> filterConvertedLocationsOnTable(SwccgGame game, Filterable filters) {
        GetCardsMatchingFilterVisitor visitor = new GetCardsMatchingFilterVisitor(game.getGameState(), game.getModifiersQuerying(), false, convertToFilters(filters));
        game.getGameState().iterateLocationsOnTable(visitor, true);
        return visitor.getPhysicalCards();
    }

    /**
     * Gets the number of converted locations on table accepted by the input filter.
     * @param game the game
     * @param filters the filters
     * @return the number of cards found
     */
    public static int countConvertedLocationsOnTable(SwccgGame game, Filterable filters) {
        GetCardsMatchingFilterVisitor visitor = new GetCardsMatchingFilterVisitor(game.getGameState(), game.getModifiersQuerying(), false, convertToFilters(filters));
        game.getGameState().iterateLocationsOnTable(visitor, true);
        return visitor.getCounter();
    }

    //
    //
    // Visiting all cards on table
    //
    //

    /**
     * Determines if a card accepted by the specified filters can be found on table (regardless of card state).
     * @param game the game
     * @param filters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpotFromAllOnTable(SwccgGame game, Filterable filters) {
        return canSpotFromAllOnTable(game, true, filters);
    }

    /**
     * Determines if a card accepted by the specified filters can be found on table (regardless of card state).
     * @param game the game
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param filters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpotFromAllOnTable(SwccgGame game, boolean useAcceptsCount, Filterable filters) {
        SpotFilterCardInPlayVisitor visitor = new SpotFilterCardInPlayVisitor(game.getGameState(), game.getModifiersQuerying(), useAcceptsCount, convertToFilters(filters));
        return game.getGameState().iterateAllCardsOnTable(visitor, false, false, false);
    }

    /**
     * Determines if at least a specified number of cards accepted by the specified filters can be found on table (regardless of card state).
     * @param game the game
     * @param count the number of cards to find
     * @param filters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpotFromAllOnTable(SwccgGame game, int count, Filterable filters) {
        return canSpotFromAllOnTable(game, count, true, filters);
    }

    /**
     * Determines if at least a specified number of cards accepted by the specified filters can be found on table (regardless of card state).
     * @param game the game
     * @param count the number of cards to find
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param filters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpotFromAllOnTable(SwccgGame game, int count, boolean useAcceptsCount, Filterable filters) {
        SpotCountFilterCardInPlayVisitor visitor = new SpotCountFilterCardInPlayVisitor(game.getGameState(), game.getModifiersQuerying(), count, useAcceptsCount, convertToFilters(filters));
        return game.getGameState().iterateAllCardsOnTable(visitor, false, false, false);
    }

    /**
     * Gets the first card found accepted by the specified filters can be found on table (regardless of card state).
     * @param game the game
     * @param filters the filters
     * @return the card found
     */
    public static PhysicalCard findFirstFromAllOnTable(SwccgGame game, Filterable filters) {
        return findFirstFromAllOnTable(game, true, filters);
    }

    /**
     * Gets the first card found accepted by the specified filters can be found on table (regardless of card state).
     * @param game the game
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param filters the filters
     * @return the card found
     */
    public static PhysicalCard findFirstFromAllOnTable(SwccgGame game, boolean useAcceptsCount, Filterable filters) {
        SpotFilterCardInPlayVisitor visitor = new SpotFilterCardInPlayVisitor(game.getGameState(), game.getModifiersQuerying(), useAcceptsCount, convertToFilters(filters));
        game.getGameState().iterateAllCardsOnTable(visitor, false, false, false);
        return visitor.getCard();
    }

    /**
     * Gets the cards found accepted by the specified filters can be found on table (regardless of card state).
     * @param game the game
     * @param filters the filters
     * @return the cards found
     */
    public static Collection<PhysicalCard> filterAllOnTable(SwccgGame game, Filterable filters) {
        return filterAllOnTable(game, true, filters);
    }

    /**
     * Gets the cards found accepted by the specified filters can be found on table (regardless of card state).
     * @param game the game
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param filters the filters
     * @return the cards found
     */
    public static Collection<PhysicalCard> filterAllOnTable(SwccgGame game, boolean useAcceptsCount, Filterable filters) {
        GetCardsMatchingFilterVisitor visitor = new GetCardsMatchingFilterVisitor(game.getGameState(), game.getModifiersQuerying(), useAcceptsCount, convertToFilters(filters));
        game.getGameState().iterateAllCardsOnTable(visitor, false, false, false);
        return visitor.getPhysicalCards();
    }

    /**
     * Gets the number of cards on table accepted by the input filter.
     * @param game the game
     * @param filters the filters
     * @return the number of cards found
     */
    public static int countAllOnTable(SwccgGame game, Filterable filters) {
        return countAllOnTable(game, true, filters);
    }

    /**
     * Gets the number of cards on table accepted by the input filter.
     * @param game the game
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param filters the filters
     * @return the number of cards found
     */
    public static int countAllOnTable(SwccgGame game, boolean useAcceptsCount, Filterable filters) {
        GetCardsMatchingFilterVisitor visitor = new GetCardsMatchingFilterVisitor(game.getGameState(), game.getModifiersQuerying(), useAcceptsCount, convertToFilters(filters));
        game.getGameState().iterateAllCardsOnTable(visitor, false, false, false);
        return visitor.getCounter();
    }

    //
    //
    // Visiting stacked cards
    //
    //

    /**
     * Determines if a card accepted by the specified filters can be found in the stacked cards.
     * @param game the game
     * @param filters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpotFromStacked(SwccgGame game, Filterable filters) {
        return canSpotFromStacked(game, 1, filters);
    }

    /**
     * Determines if a card accepted by the specified filters can be found in the stacked cards.
     * @param game the game
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param filters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpotFromStacked(SwccgGame game, boolean useAcceptsCount, Filterable filters) {
        return canSpotFromStacked(game, 1, useAcceptsCount, filters);
    }

    /**
     * Determines if at least a specified number of cards accepted by the specified filters can be found in stacked cards.
     * @param game the game
     * @param count the number of cards to find
     * @param filters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpotFromStacked(SwccgGame game, int count, Filterable filters) {
        return canSpotFromStacked(game, count, true, filters);
    }

    /**
     * Determines if at least a specified number of cards accepted by the specified filters can be found in stacked cards.
     * @param game the game
     * @param count the number of cards to find
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param filters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpotFromStacked(SwccgGame game, int count, boolean useAcceptsCount, Filterable filters) {
        SpotCountFilterCardInPlayVisitor visitor = new SpotCountFilterCardInPlayVisitor(game.getGameState(), game.getModifiersQuerying(), count, useAcceptsCount, convertToFilters(filters));
        return game.getGameState().iterateStackedCards(visitor);
    }

    /**
     * Gets the first card found accepted by the specified filters found in the stacked cards.
     * @param game the game
     * @param filters the filters
     * @return the card found
     */
    public static PhysicalCard findFirstFromStacked(SwccgGame game, Filterable filters) {
        return findFirstFromStacked(game, true, filters);
    }

    /**
     * Gets the first card found accepted by the specified filters found in the stacked cards.
     * @param game the game
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param filters the filters
     * @return the card found
     */
    public static PhysicalCard findFirstFromStacked(SwccgGame game, boolean useAcceptsCount, Filterable filters) {
        SpotFilterCardInPlayVisitor visitor = new SpotFilterCardInPlayVisitor(game.getGameState(), game.getModifiersQuerying(), useAcceptsCount, convertToFilters(filters));
        game.getGameState().iterateStackedCards(visitor);
        return visitor.getCard();
    }

    /**
     * Gets the cards accepted by the specified filters found in the stacked cards.
     * @param game the game
     * @param filters the filters
     * @return the cards found
     */
    public static Collection<PhysicalCard> filterStacked(SwccgGame game, Filterable filters) {
        return filterStacked(game, true, filters);
    }

    /**
     * Gets the cards accepted by the specified filters found in the stacked cards.
     * @param game the game
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param filters the filters
     * @return the cards found
     */
    public static Collection<PhysicalCard> filterStacked(SwccgGame game, boolean useAcceptsCount, Filterable filters) {
        GetCardsMatchingFilterVisitor visitor = new GetCardsMatchingFilterVisitor(game.getGameState(), game.getModifiersQuerying(), useAcceptsCount, convertToFilters(filters));
        game.getGameState().iterateStackedCards(visitor);
        return visitor.getPhysicalCards();
    }

    /**
     * Gets the cards accepted by the specified filters found in the stacked cards.
     * @param game the game
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) if any class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter if all classes of that card are accepted by filter
     * @param targetFiltersMap the filters
     * @return the cards found
     */
    public static Collection<PhysicalCard> filterStacked(SwccgGame game, boolean useAcceptsCount, Map<TargetingReason, Filterable> targetFiltersMap) {
        GetCardsMatchingFilterVisitor visitor = new GetCardsMatchingFilterVisitor(game.getGameState(), game.getModifiersQuerying(), useAcceptsCount, convertToFilters(targetFiltersMap.values().toArray(new Filterable[targetFiltersMap.values().size()])));
        game.getGameState().iterateStackedCards(visitor);
        return visitor.getPhysicalCards();
    }

    /**
     * Gets the number of cards accepted by the input filter found in the stacked cards.
     * @param game the game
     * @param filters the filters
     * @return the number of cards found
     */
    public static int countStacked(SwccgGame game, Filterable filters) {
        return countStacked(game, true, filters);
    }

    /**
     * Gets the number of cards accepted by the input filter found in the stacked cards.
     * @param game the game
     * @param useAcceptsCount true if cards are accepted by filter (if they have multiple classes) for each time a class
     *                        contained in that card is accepted by filter (Example: squadrons), otherwise cards are only
     *                        accepted by filter (and only once) if all classes of that card are accepted by filter
     * @param filters the filters
     * @return the number of cards found
     */
    public static int countStacked(SwccgGame game, boolean useAcceptsCount, Filterable filters) {
        GetCardsMatchingFilterVisitor visitor = new GetCardsMatchingFilterVisitor(game.getGameState(), game.getModifiersQuerying(), useAcceptsCount, convertToFilters(filters));
        game.getGameState().iterateStackedCards(visitor);
        return visitor.getCounter();
    }

    //
    //
    // Visiting 'insert' cards
    //
    //

    /**
     * Determines if a card accepted by the specified filters can be found in the 'insert' cards.
     * @param game the game
     * @param filters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpotFromInsertCards(SwccgGame game, Filterable filters) {
        SpotFilterCardInPlayVisitor visitor = new SpotFilterCardInPlayVisitor(game.getGameState(), game.getModifiersQuerying(), false, convertToFilters(filters));
        return game.getGameState().iterateInsertCards(visitor);
    }

    /**
     * Determines if at least a specified number of cards accepted by the specified filters can be found in the 'insert' cards.
     * @param game the game
     * @param count the number of cards to find
     * @param filters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpotFromInsertCards(SwccgGame game, int count, Filterable filters) {
        SpotCountFilterCardInPlayVisitor visitor = new SpotCountFilterCardInPlayVisitor(game.getGameState(), game.getModifiersQuerying(), count, false, convertToFilters(filters));
        return game.getGameState().iterateInsertCards(visitor);
    }

    /**
     * Gets the first card found accepted by the specified filters found in the 'insert' cards.
     * @param game the game
     * @param filters the filters
     * @return the card found
     */
    public static PhysicalCard findFirstFromInsertCards(SwccgGame game, Filterable filters) {
        SpotFilterCardInPlayVisitor visitor = new SpotFilterCardInPlayVisitor(game.getGameState(), game.getModifiersQuerying(), false, convertToFilters(filters));
        game.getGameState().iterateInsertCards(visitor);
        return visitor.getCard();
    }

    /**
     * Gets the cards accepted by the specified filters found in the 'insert' cards.
     * @param game the game
     * @param filters the filters
     * @return the cards found
     */
    public static Collection<PhysicalCard> filterInsertCards(SwccgGame game, Filterable filters) {
        GetCardsMatchingFilterVisitor visitor = new GetCardsMatchingFilterVisitor(game.getGameState(), game.getModifiersQuerying(), false, convertToFilters(filters));
        game.getGameState().iterateInsertCards(visitor);
        return visitor.getPhysicalCards();
    }

    /**
     * Gets the number of 'insert cards' accepted by the input filter.
     * @param game the game
     * @param filters the filters
     * @return the number of cards found
     */
    public static int countInsertCards(SwccgGame game, Filterable filters) {
        GetCardsMatchingFilterVisitor visitor = new GetCardsMatchingFilterVisitor(game.getGameState(), game.getModifiersQuerying(), false, convertToFilters(filters));
        game.getGameState().iterateInsertCards(visitor);
        return visitor.getCounter();
    }

    //
    //
    // Visiting cards for uniqueness purposes
    //
    //

    /**
     * Determines if a card accepted by the specified filters can be found on table (including stacked cards with inactive
     * card state and 'insert' cards) regardless of card state.
     * @param game the game
     * @param filters the filters
     * @return true if matching card found, otherwise false
     */
    public static boolean canSpotForUniquenessChecking(SwccgGame game, Filterable filters) {
        SpotFilterCardInPlayVisitor visitor = new SpotFilterCardInPlayVisitor(game.getGameState(), game.getModifiersQuerying(), false, convertToFilters(filters));
        return game.getGameState().iterateAllCardsOnTable(visitor, true, true, false);
    }

    /**
     * Gets the cards accepted by the specified filters on table (including stacked cards with inactive card state and
     * 'insert' cards) regardless of card state.
     * @param game the game
     * @param filters the filters
     * @return the cards found
     */
    public static Collection<PhysicalCard> filterForUniquenessChecking(SwccgGame game, Filterable filters) {
        GetCardsMatchingFilterVisitor visitor = new GetCardsMatchingFilterVisitor(game.getGameState(), game.getModifiersQuerying(), false, convertToFilters(filters));
        game.getGameState().iterateAllCardsOnTable(visitor, true, true, false);
        return visitor.getPhysicalCards();
    }

    /**
     * Gets the number of cards on table (including stacked cards with inactive card state and 'insert' cards) accepted
     * by the input filter regardless of card state.
     * @param game the game
     * @param filters the filters
     * @return the number of cards found
     */
    public static int countForUniquenessChecking(SwccgGame game, Filterable filters) {
        GetCardsMatchingFilterVisitor visitor = new GetCardsMatchingFilterVisitor(game.getGameState(), game.getModifiersQuerying(), false, convertToFilters(filters));
        game.getGameState().iterateAllCardsOnTable(visitor, true, true, false);
        return visitor.getCounter();
    }

    //
    //
    // This section defines misc private methods used to combine filters, etc.
    //
    //
    private static Filter[] convertToFilters(Filterable... filters) {
        Filter[] filtersInt = new Filter[filters.length];
        for (int i = 0; i < filtersInt.length; i++)
            filtersInt[i] = changeToFilter(filters[i]);
        return filtersInt;
    }

    private static Filter andInternal(final Filter... filters) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                for (Filter filter : filters) {
                    if (!filter.accepts(gameState, modifiersQuerying, physicalCard))
                        return false;
                }
                return true;
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                for (Filter filter : filters) {
                    if (!filter.accepts(gameState, modifiersQuerying, builtInCardBlueprint))
                        return false;
                }
                return true;
            }
            @Override
            public boolean acceptsIgnoringOwner(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                for (Filter filter : filters) {
                    if (!filter.acceptsIgnoringOwner(gameState, modifiersQuerying, physicalCard))
                        return false;
                }
                return true;
            }
            @Override
            public boolean acceptsSingleModelType(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, ModelType modelTypeToCheck) {
                for (Filter filter : filters) {
                    if (!filter.acceptsSingleModelType(gameState, modifiersQuerying, physicalCard, modelTypeToCheck))
                        return false;
                }
                return true;
            }
            @Override
            public int acceptsCount(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                int count = 0;
                for (Filter filter : filters) {
                    int curCount = filter.acceptsCount(gameState, modifiersQuerying, physicalCard);
                    if (curCount == 0) {
                        return 0;
                    }
                    count = Math.max(count, curCount);
                }
                return count;
            }
        };
    }

    private static Filter orInternal(final Filter... filters) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                for (Filter filter : filters) {
                    if (filter.accepts(gameState, modifiersQuerying, physicalCard))
                        return true;
                }
                return false;
            }
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint builtInCardBlueprint) {
                for (Filter filter : filters) {
                    if (filter.accepts(gameState, modifiersQuerying, builtInCardBlueprint))
                        return true;
                }
                return false;
            }
            @Override
            public boolean acceptsIgnoringOwner(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                for (Filter filter : filters) {
                    if (filter.acceptsIgnoringOwner(gameState, modifiersQuerying, physicalCard))
                        return true;
                }
                return false;
            }
            @Override
            public boolean acceptsSingleModelType(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, ModelType modelTypeToCheck) {
                for (Filter filter : filters) {
                    if (filter.acceptsSingleModelType(gameState, modifiersQuerying, physicalCard, modelTypeToCheck))
                        return true;
                }
                return false;
            }
            @Override
            public int acceptsCount(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                int count = 0;
                for (Filter filter : filters) {
                    count = Math.max(count, filter.acceptsCount(gameState, modifiersQuerying, physicalCard));
                }
                return count;
            }
        };
    }

    /**
     * Checks if a piece of text contains a specified word or phrase.
     *
     * @param textBody the text to search within
     * @param wordOrPhrase the word or phase to search for
     * @return Filter
     */
    private static boolean containsWordOrPhrase(String textBody, String wordOrPhrase) {
        if (textBody==null || textBody.isEmpty() || wordOrPhrase==null || wordOrPhrase.isEmpty())
            return false;

        String textBodyLowerCase = textBody.toLowerCase();
        String wordOrPhraseLowerCase = wordOrPhrase.toLowerCase();

        // Check if pattern match
        if (textBodyLowerCase.matches(wordOrPhraseLowerCase)
                || textBodyLowerCase.matches(wordOrPhraseLowerCase + "[^\\w].*")
                || textBodyLowerCase.matches(".*[^\\w]"+ wordOrPhraseLowerCase + "[^\\w].*")
                || textBodyLowerCase.matches(".*[^\\w]"+ wordOrPhraseLowerCase))
            return true;

        return false;
    }

    //
    // Filter shortcuts to be used by the cards for words used in game text.
    //

    public static final Filter _21B = Filters.title(Title._21B);
    public static final Filter _3720_To_1 = Filters.title(Title._3720_To_1);
    public static final Filter _4_LOM = Filters.persona(Persona._4_LOM);
    public static final Filter _5D6RA7 = Filters.title(Title._5D6RA7);
    public static final Filter _8D8 = Filters.title(Title._8D8);
    public static final Filter A_Dangerous_Time = Filters.title(Title.A_Dangerous_Time);
    public static final Filter A_Gift = Filters.title(Title.A_Gift);
    public static final Filter A_Weakness_Can_Be_Found = Filters.title(Title.A_Weakness_Can_Be_Found);
    public static final Filter A_wing = Filters.modelType(ModelType.A_WING);
    public static final Filter A280_Sharpshooter_Rifle = Filters.title(Title.A280_Sharpshooter_Rifle);
    public static final Filter AAT = Filters.modelType(ModelType.AAT);
    public static final Filter AAT_Laser_Cannon = Filters.title(Title.AAT_Laser_Cannon);
    public static final Filter accountant = Filters.keyword(Keyword.ACCOUNTANT);
    public static final Filter Ackbar = Filters.title(Title.Ackbar);
    public static final Filter Activate_The_Droids = Filters.title(Title.Activate_The_Droids);
    public static final Filter admiral = Filters.keyword(Keyword.ADMIRAL);
    public static final Filter Admirals_Order = Filters.type(CardType.ADMIRALS_ORDER);
    public static final Filter Advance_Preparation = Filters.title(Title.Advance_Preparation);
    public static final Filter Advosze = Filters.title(Title.Advosze);
    public static final Filter Agents_In_The_Court = Filters.title(Title.Agents_In_The_Court);
    public static final Filter Agents_Of_Black_Sun = Filters.title(Title.Agents_Of_Black_Sun);
    public static final Filter AhchTo_location = Filters.partOfSystem(Title.Ahch_To);
    public static final Filter Ahsoka = Filters.persona(Persona.AHSOKA);
    public static final Filter Aiiii_Aaa_Agggggggggg = Filters.title(Title.Aiiii_Aaa_Agggggggggg);
    public static final Filter Alderaan_site = Filters.and(Filters.partOfSystem(Title.Alderaan), CardSubtype.SITE);
    public static final Filter Alderaan_system = Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Alderaan));
    public static final Filter Alderaanian = Filters.species(Species.ALDERAANIAN);
    public static final Filter alien = Filters.icon(Icon.ALIEN);
    public static final Filter alien_leader = Filters.and(Filters.icon(Icon.ALIEN), Filters.keyword(Keyword.LEADER));
    public static final Filter All_Too_Easy = Filters.title(Title.All_Too_Easy);
    public static final Filter All_Wings_Report_In = Filters.title(Title.All_Wings_Report_In);
    public static final Filter All_Wrapped_Up = Filters.title(Title.All_Wrapped_Up);
    public static final Filter Alter = Filters.title(Title.Alter);
    public static final Filter Alternatives_To_Fighting = Filters.title(Title.Alternatives_To_Fighting);
    public static final Filter always_immune_to_Alter = Filters.alwaysImmuneToCardTitle(Title.Alter);
    public static final Filter Always_Thinking_With_Your_Stomach = Filters.title(Title.Always_Thinking_With_Your_Stomach);
    public static final Filter Always_Two_There_Are = Filters.title(Title.Always_Two_There_Are);
    public static final Filter ambition_agenda = Filters.agenda(Agenda.AMBITION);
    public static final Filter Amidala = Filters.persona(Persona.AMIDALA);
    public static final Filter Anakin = Filters.persona(Persona.ANAKIN);
    public static final Filter Anakin_Skywalker = Filters.title(Title.Anakin_Skywalker);
    public static final Filter Anakins_Podracer = Filters.title(Title.Anakins_Podracer);
    public static final Filter Anchorhead = Filters.title(Title.Anchorhead);
    public static final Filter And_Now_Youll_Give_It_To_Me = Filters.title(Title.And_Now_Youll_Give_It_To_Me);
    public static final Filter Anger_Fear_Aggression = Filters.title(Title.Anger_Fear_Aggression);
    public static final Filter Anoat_site = Filters.and(Filters.partOfSystem(Title.Anoat), CardSubtype.SITE);
    public static final Filter Anoat_system = Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Anoat));
    public static final Filter any_bounty = Filters.keyword(Keyword.BOUNTY);
    public static final Filter any_Mara = Filters.or(Persona.MARA_JADE, Persona.MARA_SKYWALKER);
    public static final Filter any_model_type = Filters.modelType(ModelType._ANY_);
    public static final Filter Any_Methods_Necessary = Filters.title(Title.Any_Methods_Necessary);
    public static final Filter Ardon_Crell = Filters.title(Title.Ardon_Crell);
    public static final Filter Arleil = Filters.title(Title.Arleil);
    public static final Filter Arnet = Filters.title(Title.Arnet);
    public static final Filter artillery_weapon = Filters.and(CardType.WEAPON, CardSubtype.ARTILLERY);
    public static final Filter Arven = Filters.title(Title.Arven);
    public static final Filter Ascension_Guns = Filters.title(Title.Ascension_Guns);
    public static final Filter As_Good_As_Gone = Filters.title(Title.As_Good_As_Gone);
    public static final Filter Asteroid_Sanctuary = Filters.title(Title.Asteroid_Sanctuary);
    public static final Filter Asteroids_Do_Not_Concern_Me = Filters.title(Title.Asteroids_Do_Not_Concern_Me);
    public static final Filter Asteroid_Field = Filters.title(Title.Asteroid_Field);
    public static final Filter asteroid_sector = Filters.and(Keyword.ASTEROID, CardSubtype.SECTOR);
    public static final Filter astromech_droid = Filters.modelType(ModelType.ASTROMECH);
    public static final Filter Astromech_Shortage = Filters.title(Title.Astromech_Shortage);
    public static final Filter AT_AT = Filters.modelType(ModelType.AT_AT);
    public static final Filter AT_AT_Cannon = Filters.title(Title.AT_AT_Cannon);
    public static final Filter At_Last_We_Will_Have_Revenge = Filters.title(Title.At_Last_We_Will_Have_Revenge);
    public static final Filter At_Peace = Filters.title(Title.At_Peace);
    public static final Filter AT_ST = Filters.modelType(ModelType.AT_ST);
    public static final Filter AT_ST_Dual_Cannon = Filters.title(Title.AT_ST_Dual_Cannon);
    public static final Filter At_Death_Star_Site = Filters.atSiteOfSystem(Title.Death_Star);
    public static final Filter at_Scomp_Link = Filters.or(Filters.presentAt(Filters.icon(Icon.SCOMP_LINK)), Filters.attachedTo(Filters.icon(Icon.SCOMP_LINK)));
    public static final Filter At_Tatooine = Filters.at(Title.Tatooine);
    public static final Filter At_Yavin_4_Site = Filters.atSiteOfSystem(Title.Yavin_4);
    public static final Filter Attack_Run = Filters.title(Title.Attack_Run);
    public static final Filter Audience_Chamber = Filters.title(Title.Audience_Chamber);
    public static final Filter Aurra_Sing = Filters.title(Title.Aurra_Sing);
    public static final Filter automated_weapon = Filters.and(CardType.WEAPON, CardSubtype.AUTOMATED);
    public static final Filter Avarik = Filters.title(Title.Avarik);
    public static final Filter Avenger = Filters.title(Title.Avenger);
    public static final Filter Awwww_Cannot_Get_Your_Ship_Out = Filters.title(Title.Awwww_Cannot_Get_Your_Ship_Out);
    public static final Filter Azure_Angel = Filters.title(Title.Azure_Angel);
    public static final Filter B_wing = Filters.modelType(ModelType.B_WING);
    public static final Filter Back_Door = Filters.title(Title.Back_Door);
    public static final Filter Bacta_Tank = Filters.title(Title.Bacta_Tank);
    public static final Filter Bad_Feeling_Have_I = Filters.title(Title.Bad_Feeling_Have_I);
    public static final Filter bantha = Filters.keyword(Keyword.BANTHA);
    public static final Filter Barada = Filters.title(Title.Barada);
    public static final Filter Barich = Filters.title(Title.Barich);
    public static final Filter battle_droid = Filters.and(ModelType.BATTLE, CardType.DROID);
    public static final Filter Battle_Plains = Filters.title(Title.Battle_Plains);
    public static final Filter Battle_Plan = Filters.title(Title.Battle_Plan);
    public static final Filter battleground_site = Filters.and(CardSubtype.SITE, Filters.battleground());
    public static final Filter battleground_system = Filters.and(CardSubtype.SYSTEM, Filters.battleground());
    public static final Filter battleship = Filters.modelType(ModelType.TRADE_FEDERATION_BATTLESHIP);
    public static final Filter Baze = Filters.title(Title.Baze);
    public static final Filter BB8 = Filters.persona(Persona.BB8);
    public static final Filter Beggar = Filters.title(Title.Beggar);
    public static final Filter Beggars_Canyon = Filters.title(Title.Beggars_Canyon);
    public static final Filter Beru = Filters.title(Title.Beru_Lars);
    public static final Filter Beru_Stew = Filters.title(Title.Beru_Stew);
    public static final Filter Besieged = Filters.title(Title.Besieged);
    public static final Filter Bespin_Cloud_City = Filters.title(Title.Bespin_Cloud_City);
    public static final Filter Bespin_cloud_sector = Filters.and(Filters.partOfSystem(Title.Bespin), Keyword.CLOUD_SECTOR);
    public static final Filter Bespin_location = Filters.partOfSystem(Title.Bespin);
    public static final Filter Bespin_system = Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Bespin));
    public static final Filter Bib = Filters.title(Title.Bib);
    public static final Filter biker_scout = Filters.keyword(Keyword.BIKER_SCOUT);
    public static final Filter Big_One = Filters.title(Title.Big_One);
    public static final Filter Big_One_Asteroid_Cave_Or_Space_Slug_Belly = Filters.or(Filters.title(Title.Big_One_Asteroid_Cave_Or_Space_Slug_Belly), Filters.title(Title.Big_One_Asteroid_Cave), Filters.title(Title.Space_Slug_Belly));
    public static final Filter Biggs = Filters.title(Title.Biggs);
    public static final Filter binary_droid = Filters.modelType(ModelType.BINARY_HYDROPONICS);
    public static final Filter Binders = Filters.title(Title.Binders);
    public static final Filter Bith = Filters.species(Species.BITH);
    public static final Filter Bith_Shuffle = Filters.title(Title.Bith_Shuffle);
    public static final Filter Black_2 = Filters.persona(Persona.BLACK_2);
    public static final Filter Black_3 = Filters.persona(Persona.BLACK_3);
    public static final Filter Black_4 = Filters.title(Title.Black_4);
    public static final Filter Black_5 = Filters.title(Title.Black_5);
    public static final Filter Black_Squadron_pilot = Filters.and(CardCategory.CHARACTER, Icon.PILOT, Filters.or(Keyword.BLACK_SQUADRON, Filters.aboard(Filters.keyword(Keyword.BLACK_SQUADRON))));
    public static final Filter Black_Squadron_tie = Filters.and(Keyword.BLACK_SQUADRON, Filters.tie());
    public static final Filter Black_Sun_agent = Filters.keyword(Keyword.BLACK_SUN_AGENT);
    public static final Filter Blast_The_Door_Kid = Filters.title(Title.Blast_The_Door_Kid);
    public static final Filter Blasted_Droid = Filters.title(Title.Blasted_Droid);
    public static final Filter blaster = Filters.or(Keyword.BLASTER, Keyword.BLASTER_RIFLE, Keyword.DH17_BLASTER);
    public static final Filter Blaster_Deflection = Filters.title(Title.Blaster_Deflection);
    public static final Filter Blaster_Proficiency = Filters.title(Title.Blaster_Proficiency);
    public static final Filter Blaster_Rack = Filters.title(Title.Blaster_Rack);
    public static final Filter blaster_rifle = Filters.keyword(Keyword.BLASTER_RIFLE);
    public static final Filter Blaster_Scope = Filters.title(Title.Blaster_Scope);
    public static final Filter Blizzard_1 = Filters.title(Title.Blizzard_1);
    public static final Filter Blizzard_2 = Filters.title(Title.Blizzard_2);
    public static final Filter Blizzard_Scout_1 = Filters.title(Title.Blizzard_Scout_1);
    public static final Filter blockade_agenda = Filters.agenda(Agenda.BLOCKADE);
    public static final Filter Blockade_Flagship = Filters.persona(Persona.BLOCKADE_FLAGSHIP);
    public static final Filter BlockadeFlagshipBridge = Filters.title(Title.BlockadeFlagshipBridge);
    public static final Filter Blue_Milk = Filters.title(Title.Blue_Milk);
    public static final Filter Blue_Squadron_5 = Filters.title(Title.Blue_Squadron_5);
    public static final Filter Bluffs = Filters.title(Title.Bluffs);
    public static final Filter Bo_Shuda = Filters.title(Title.Bo_Shuda);
    public static final Filter Boba_Fett = Filters.persona(Persona.BOBA_FETT);
    public static final Filter Bodhi = Filters.title(Title.Bodhi);
    public static final Filter Boelo = Filters.title(Title.Boelo);
    public static final Filter bomber = Filters.or(ModelType.B_WING, ModelType.TIE_SA);
    public static final Filter Bombing_Run = Filters.title(Title.Bombing_Run);
    public static final Filter Booma = Filters.title(Title.Booma);
    public static final Filter Boonta_Eve_Podrace = Filters.title(Title.Boonta_Eve_Podrace);
    public static final Filter Boosted_TIE_Cannon = Filters.title(Title.Boosted_TIE_Cannon);
    public static final Filter Booster = Filters.persona(Persona.BOOSTER);
    public static final Filter Boring_Conversation_Anyway = Filters.title(Title.Boring_Conversation_Anyway);
    public static final Filter Boss_Nass = Filters.title(Title.Boss_Nass);
    public static final Filter Boss_Nass_Chambers = Filters.title(Title.Boss_Nass_Chambers);
    public static final Filter Bossk = Filters.persona(Persona.BOSSK);
    public static final Filter Bothan = Filters.species(Species.BOTHAN);
    public static final Filter Bothawui_site = Filters.and(Filters.partOfSystem(Title.Bothawui), CardSubtype.SITE);
    public static final Filter Bounty = Filters.title(Title.Bounty);
    public static final Filter bounty_hunter = Filters.keyword(Keyword.BOUNTY_HUNTER);
    public static final Filter Bow_To_The_First_Order = Filters.title(Title.Bow_To_The_First_Order);
    public static final Filter bowcaster = Filters.keyword(Keyword.BOWCASTER);
    public static final Filter Bowcaster = Filters.title(Title.Bowcaster);
    public static final Filter Brangus_Glee = Filters.title(Title.Brangus_Glee);
    public static final Filter Bravo_1 = Filters.title(Title.Bravo_1);
    public static final Filter Bravo_2 = Filters.title(Title.Bravo_2);
    public static final Filter Bravo_3 = Filters.title(Title.Bravo_3);
    public static final Filter Bravo_4 = Filters.title(Title.Bravo_4);
    public static final Filter Bravo_5 = Filters.title(Title.Bravo_5);
    public static final Filter Bravo_Fighter = Filters.title(Title.Bravo_Fighter);
    public static final Filter Bravo_Squadron_pilot = Filters.and(CardCategory.CHARACTER, Icon.PILOT, Filters.or(Keyword.BRAVO_SQUADRON, Filters.aboard(Filters.keyword(Keyword.BRAVO_SQUADRON))));
    public static final Filter Bravo_Squadron_starfigher = Filters.and(Keyword.BRAVO_SQUADRON, CardSubtype.STARFIGHTER);
    public static final Filter Breached_Defenses = Filters.title(Title.Breached_Defenses);
    public static final Filter Bring_Him_Before_Me = Filters.title(Title.Bring_Him_Before_Me);
    public static final Filter Brisky_Morning_Munchen = Filters.title(Title.Brisky_Morning_Munchen);
    public static final Filter Broken_Concentration = Filters.title(Title.Broken_Concentration);
    public static final Filter Bubo = Filters.title(Title.Bubo);
    public static final Filter Bunker = Filters.title(Title.Bunker);
    public static final Filter C3PO = Filters.persona(Persona.C3PO);
    public static final Filter Cantina = Filters.title(Title.Cantina);
    public static final Filter canyon = Filters.keyword(Keyword.CANYON);
    public static final Filter Capacitors = Filters.title(Title.Capacitors);
    public static final Filter capital_starship = Filters.and(CardType.STARSHIP, CardSubtype.CAPITAL);
    public static final Filter Capital_Support = Filters.title(Title.Capital_Support);
    public static final Filter Captain_Tarpals = Filters.title(Title.Captain_Tarpals);
    public static final Filter Captive_Fury = Filters.title(Title.Captive_Fury);
    public static final Filter Captive_Pursuit = Filters.title(Title.Captive_Pursuit);
    public static final Filter Carbon_Chamber_Testing = Filters.title(Title.Carbon_Chamber_Testing);
    public static final Filter Carbon_Freezing = Filters.title(Title.Carbon_Freezing);
    public static final Filter Carbonite_Chamber = Filters.title(Title.Carbonite_Chamber);
    public static final Filter Carbonite_Chamber_Console = Filters.title(Title.Carbonite_Chamber_Console);
    public static final Filter Careful_Planning = Filters.title(Title.Careful_Planning);
    public static final Filter Carida_system = Filters.title(Title.Carida);
    public static final Filter Cassian = Filters.title(Title.Cassian);
    public static final Filter Cease_Fire = Filters.title(Title.Cease_Fire);
    public static final Filter Cell_2187 = Filters.title(Title.Cell_2187);
    public static final Filter Chandrila_location = Filters.partOfSystem(Title.Chandrila);
    public static final Filter Chandrila_system = Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Chandrila));
    public static final Filter character = Filters.category(CardCategory.CHARACTER);
    public static final Filter Character_with_a_Blaster = Filters.and(CardCategory.CHARACTER, Filters.armedWith(Filters.or(Keyword.BLASTER, Keyword.BLASTER_RIFLE, Keyword.DH17_BLASTER)));
    public static final Filter character_with_a_lightsaber = Filters.and(CardCategory.CHARACTER, Filters.armedWith(Filters.keyword(Keyword.LIGHTSABER)));
    public static final Filter Character_with_a_Nonunique_Blaster = Filters.and(CardCategory.CHARACTER, Filters.armedWith(Filters.and(Filters.or(Keyword.BLASTER, Keyword.BLASTER_RIFLE, Keyword.DH17_BLASTER), Filters.nonUnique())));
    public static final Filter character_with_a_weapon = Filters.and(CardCategory.CHARACTER, Filters.armedWith(Filters.category(CardCategory.WEAPON)));
    public static final Filter character_with_politics = Filters.and(CardCategory.CHARACTER, Filters.politicsMoreThan(0));
    public static final Filter character_without_politics = Filters.and(CardCategory.CHARACTER, Filters.politicsEqualTo(0));
    public static final Filter character_with_permanent_character_weapon = Filters.and(Filters.character, Filters.hasPermanentWeapon());
    public static final Filter character_weapon_or_character_with_permanent_character_weapon = Filters.or(Filters.character_weapon(), Filters.and(Filters.character, Filters.hasPermanentWeapon()));
    public static final Filter Chewie = Filters.persona(Persona.CHEWIE);
    public static final Filter Chief_Bast = Filters.title(Title.Chief_Bast);
    public static final Filter Chief_Chirpa = Filters.title(Title.Chief_Chirpa);
    public static final Filter Chief_Chirpas_Hut = Filters.title(Title.Chief_Chirpas_Hut);
    public static final Filter Chimaera = Filters.title(Title.Chimaera);
    public static final Filter Chiraneau = Filters.title(Title.Chiraneau);
    public static final Filter Chirrut = Filters.title(Title.Chirrut);
    public static final Filter Chopper = Filters.title(Title.Chopper);
    public static final Filter City_In_The_Clouds = Filters.title(Title.City_In_The_Clouds);
    public static final Filter City_Outskirts = Filters.title(Title.City_Outskirts);
    public static final Filter Civil_Disorder = Filters.title(Title.Civil_Disorder);
    public static final Filter Clakdor_VII_site = Filters.and(Filters.partOfSystem(Title.Clakdor_VII), CardSubtype.SITE);
    public static final Filter Clakdor_VII_system = Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Clakdor_VII));
    public static final Filter Clash_Of_Sabers = Filters.title(Title.Clash_Of_Sabers);
    public static final Filter clone = Filters.keyword(Keyword.CLONE_TROOPER);
    public static final Filter Clone_Army = Filters.icon(Icon.CLONE_ARMY);
    public static final Filter Closer = Filters.title(Title.Closer);
    public static final Filter cloud_car = Filters.or(ModelType.TWIN_POD_CLOUD_CAR, ModelType.TALON_I_COMBAT_CLOUD_CAR);
    public static final Filter Cloud_City_battleground_site = Filters.and(Keyword.CLOUD_CITY_LOCATION, CardSubtype.SITE, Filters.battleground());
    public static final Filter Cloud_City_Celebration = Filters.title(Title.Cloud_City_Celebration);
    public static final Filter Cloud_City_location = Filters.and(Keyword.CLOUD_CITY_LOCATION);
    public static final Filter Cloud_City_Occupation = Filters.title(Title.Cloud_City_Occupation);
    public static final Filter Cloud_City_Sabacc = Filters.title(Title.Cloud_City_Sabacc);
    public static final Filter Cloud_City_site = Filters.and(Keyword.CLOUD_CITY_LOCATION, CardSubtype.SITE);
    public static final Filter Cloud_City_trooper = Filters.keyword(Keyword.CLOUD_CITY_TROOPER);
    public static final Filter cloud_sector = Filters.keyword(Keyword.CLOUD_SECTOR);
    public static final Filter Close_Call = Filters.title(Title.Close_Call);
    public static final Filter Collision = Filters.title(Title.Collision);
    public static final Filter Colo_Claw_Fish = Filters.title(Title.Colo_Claw_Fish);
    public static final Filter Combat_Readiness = Filters.title(Title.Combat_Readiness);
    public static final Filter Combat_Response = Filters.title(Title.Combat_Response);
    public static final Filter combat_vehicle = Filters.and(CardType.VEHICLE, CardSubtype.COMBAT);
    public static final Filter Combined_Attack = Filters.title(Title.Combined_Attack);
    public static final Filter Comlink = Filters.title(Title.Comlink);
    public static final Filter commander = Filters.keyword(Keyword.COMMANDER);
    public static final Filter Commence_Primary_Ignition = Filters.title(Title.Commence_Primary_Ignition);
    public static final Filter Commence_Recharging = Filters.title(Title.Commence_Recharging);
    public static final Filter communications_droid = Filters.modelType(ModelType.COMMUNICATIONS);
    public static final Filter Concussion_Grenade = Filters.title(Title.Concussion_Grenade);
    public static final Filter Concussion_Missiles = Filters.title(Title.Concussion_Missiles);
    public static final Filter cannon = Filters.or(Keyword.CANNON, Keyword.ION_CANNON, Keyword.LASER_CANNON);
    public static final Filter Control = Filters.title(Title.Control);
    public static final Filter Coolant_Shaft = Filters.title(Title.Coolant_Shaft);
    public static final Filter Corellia_site = Filters.and(Filters.partOfSystem(Title.Corellia), CardSubtype.SITE);
    public static final Filter Corellia_system = Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Corellia));
    public static final Filter Corellian = Filters.species(Species.CORELLIAN);
    public static final Filter Corellian_corvette = Filters.modelType(ModelType.CORELLIAN_CORVETTE);
    public static final Filter Corellian_Engineering_Corporation = Filters.title(Title.Corellian_Engineering_Corporation);
    public static final Filter Corellian_Slip = Filters.title(Title.Corellian_Slip);
    public static final Filter Coruscant_battleground_site = Filters.and(Filters.partOfSystem(Title.Coruscant), CardSubtype.SITE, Filters.battleground());
    public static final Filter Coruscant_Celebration = Filters.title(Title.Coruscant_Celebration);
    public static final Filter Coruscant_Guard = Filters.keyword(Keyword.CORUSCANT_GUARD);
    public static final Filter Coruscant_location = Filters.partOfSystem(Title.Coruscant);
    public static final Filter Coruscant_site = Filters.and(Filters.partOfSystem(Title.Coruscant), CardSubtype.SITE);
    public static final Filter Coruscant_Imperial_Square = Filters.title(Title.Coruscant_Imperial_Square);
    public static final Filter Coruscant_system = Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Coruscant));
    public static final Filter Corulag = Filters.title(Title.Corulag);
    public static final Filter Corulag_site = Filters.and(Filters.partOfSystem(Title.Corulag), CardSubtype.SITE);
    public static final Filter corvette = Filters.modelType(ModelType.CORELLIAN_CORVETTE);
    public static final Filter Count_Me_In = Filters.title(Title.Count_Me_In);
    public static final Filter Counter_Assault = Filters.title(Title.Counter_Assault);
    public static final Filter Courage_Of_A_Skywalker = Filters.title(Title.Courage_Of_A_Skywalker);
    public static final Filter Court_Of_The_Vile_Gangster = Filters.title(Title.Court_Of_The_Vile_Gangster);
    public static final Filter Cracken = Filters.persona(Persona.CRACKEN);
    public static final Filter Crash_Site_Memorial = Filters.title(Title.Crash_Site_Memorial);
    public static final Filter crashed_vehicle = Filters.and(CardType.VEHICLE, Filters.crashed());
    public static final Filter creature = Filters.type(CardType.CREATURE);
    public static final Filter creature_vehicle = Filters.and(CardType.VEHICLE, CardSubtype.CREATURE);
    public static final Filter Credits_Will_Do_Fine = Filters.title(Title.Credits_Will_Do_Fine);
    public static final Filter cruiser = Filters.or(ModelType.CORELLIAN_REPUBLIC_CRUISER, Keyword.CRUISER, ModelType.DREADNAUGHT_CLASS_HEAVY_CRUISER, ModelType.MON_CALAMARI_STAR_CRUISER);
    public static final Filter Cyborg_Construct = Filters.title(Title.Cyborg_Construct);
    public static final Filter Dagobah_Bog_Clearing = Filters.title(Title.Dagobah_Bog_Clearing);
    public static final Filter Dagobah_Cave = Filters.title(Title.Dagobah_Cave);
    public static final Filter Dagobah_Jungle = Filters.title(Title.Dagobah_Jungle);
    public static final Filter Dagobah_location = Filters.partOfSystem(Title.Dagobah);
    public static final Filter Dagobah_site = Filters.and(Filters.partOfSystem(Title.Dagobah), CardSubtype.SITE);
    public static final Filter Dagobah_Swamp = Filters.title(Title.Dagobah_Swamp);
    public static final Filter Dagobah_system = Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Dagobah));
    public static final Filter Dantooine_Base_Operations = Filters.title(Title.Dantooine_Base_Operations);
    public static final Filter Dantooine_location = Filters.partOfSystem(Title.Dantooine);
    public static final Filter Dantooine_site = Filters.and(Filters.partOfSystem(Title.Dantooine), CardSubtype.SITE);
    public static final Filter Dantooine_system = Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Dantooine));
    public static final Filter Danz_Borin = Filters.title(Title.Danz_Borin);
    public static final Filter Dark_Collaboration = Filters.title(Title.Dark_Collaboration);
    public static final Filter Dark_Deal = Filters.title(Title.Dark_Deal);
    public static final Filter Dark_Forces = Filters.title(Title.Dark_Forces);
    public static final Filter Dark_Jedi = Filters.and(Filters.side(Side.DARK), Filters.character, Filters.abilityMoreThanOrEqualTo(6));
    public static final Filter Dark_Jedi_Presence = Filters.title(Title.Dark_Jedi_Presence);
    public static final Filter Dark_Maneuvers = Filters.title(Title.Dark_Maneuvers);
    public static final Filter Dark_Side = Filters.side(Side.DARK);
    public static final Filter Dark_Strike = Filters.title(Title.Dark_Strike);
    public static final Filter Dark_Waters = Filters.title(Title.Dark_Waters);
    public static final Filter Darklighter_Spin = Filters.title(Title.Darklighter_Spin);
    public static final Filter Dash = Filters.persona(Persona.DASH);
    public static final Filter Dathcha = Filters.title(Title.Dathcha);
    public static final Filter DataVault = Filters.title(Title.DataVault);
    public static final Filter Daughter_Of_Skywalker = Filters.title(Title.Daughter_Of_Skywalker);
    public static final Filter Deactivate_The_Shield_Generator = Filters.title(Title.Deactivate_The_Shield_Generator);
    public static final Filter Dead_Ewok = Filters.title(Title.Dead_Ewok);
    public static final Filter Death_Mark = Filters.title(Title.Death_Mark);
    public static final Filter Death_Squadron = Filters.title(Title.Death_Squadron);
    public static final Filter Death_Star_system = Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Death_Star));
    public static final Filter Death_Star_Central_Core = Filters.title(Title.Death_Star_Central_Core);
    public static final Filter Death_Star_Conference_Room = Filters.title(Title.Death_Star_Conference_Room);
    public static final Filter Death_Star_Gunner = Filters.title(Title.Death_Star_Gunner);
    public static final Filter Death_Star_II_Docking_Bay = Filters.title(Title.Death_Star_II_Docking_Bay);
    public static final Filter Death_Star_II_location = Filters.partOfSystem(Title.Death_Star_II);
    public static final Filter Death_Star_II_sector = Filters.and(Filters.partOfSystem(Title.Death_Star_II), CardSubtype.SECTOR);
    public static final Filter Death_Star_II_site = Filters.and(Filters.partOfSystem(Title.Death_Star_II), CardSubtype.SITE);
    public static final Filter Death_Star_II_system = Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Death_Star_II));
    public static final Filter Death_Star_location = Filters.partOfSystem(Title.Death_Star);
    public static final Filter Death_Star_Plans = Filters.title(Title.Death_Star_Plans);
    public static final Filter Death_Star_Sentry = Filters.title(Title.Death_Star_Sentry);
    public static final Filter Death_Star_site = Filters.and(Filters.partOfSystem(Title.Death_Star), CardSubtype.SITE);
    public static final Filter Death_Star_Trench = Filters.title(Title.Death_Star_Trench);
    public static final Filter Death_Star_Tractor_Beam = Filters.title(Title.Death_Star_Tractor_Beam);
    public static final Filter Death_Star_trooper = Filters.keyword(Keyword.DEATH_STAR_TROOPER);
    public static final Filter death_trooper = Filters.keyword(Keyword.DEATH_TROOPER);
    public static final Filter Deep_Hatred = Filters.title(Title.Deep_Hatred);
    public static final Filter Defensive_Fire = Filters.title(Title.Defensive_Fire);
    public static final Filter Defensive_Perimeter = Filters.title(Title.Defensive_Perimeter);
    public static final Filter Defensive_Shield = Filters.type(CardType.DEFENSIVE_SHIELD);
    public static final Filter dejarik = Filters.keyword(Keyword.DEJARIK);
    public static final Filter Demotion = Filters.title(Title.Demotion);
    public static final Filter Dengar = Filters.persona(Persona.DENGAR);
    public static final Filter Deployable_By_SYCFA = Filters.and(Filters.side(Side.DARK), Filters.or(Filters.Alderaan_system, Filters.title(Title.Jedha_City)));
    public static final Filter Deploys_aboard_Blockade_Flagship = Filters.or(Filters.persona(Persona.BLOCKADE_FLAGSHIP), Filters.locationAndCardsAtLocation(Filters.siteOfStarshipOrVehicle(Persona.BLOCKADE_FLAGSHIP, false)));
    public static final Filter Deploys_aboard_Executor = Filters.or(Filters.persona(Persona.EXECUTOR), Filters.locationAndCardsAtLocation(Filters.siteOfStarshipOrVehicle(Persona.EXECUTOR, false)));
    public static final Filter Deploys_aboard_Home_One = Filters.or(Filters.persona(Persona.HOME_ONE), Filters.locationAndCardsAtLocation(Filters.siteOfStarshipOrVehicle(Persona.HOME_ONE, false)));
    public static final Filter Deploys_at_Ahch_To = Filters.or(Filters.placeToBePresentOnPlanet(Title.Ahch_To), Filters.locationAndCardsAtLocation(Filters.title(Title.Ahch_To)));
    public static final Filter Deploys_on_Ahch_To = Filters.placeToBePresentOnPlanet(Title.Ahch_To);
    public static final Filter Deploys_at_Endor = Filters.or(Filters.placeToBePresentOnPlanet(Title.Endor), Filters.locationAndCardsAtLocation(Filters.title(Title.Endor)));
    public static final Filter Deploys_on_Cloud_City = Filters.locationAndCardsAtLocation(Filters.Cloud_City_site);
    public static final Filter Deploys_on_Coruscant = Filters.placeToBePresentOnPlanet(Title.Coruscant);
    public static final Filter Deploys_at_Coruscant = Filters.or(Filters.placeToBePresentOnPlanet(Title.Coruscant), Filters.locationAndCardsAtLocation(Filters.title(Title.Coruscant)));
    public static final Filter Deploys_on_Dagobah = Filters.placeToBePresentOnPlanet(Title.Dagobah);
    public static final Filter Deploys_at_Death_Star = Filters.or(Filters.placeToBePresentOnPlanet(Title.Death_Star), Filters.locationAndCardsAtLocation(Filters.title(Title.Death_Star)));
    public static final Filter Deploys_on_Death_Star = Filters.placeToBePresentOnPlanet(Title.Death_Star);
    public static final Filter Deploys_at_Death_Star_II = Filters.or(Filters.placeToBePresentOnPlanet(Title.Death_Star_II), Filters.locationAndCardsAtLocation(Filters.title(Title.Death_Star_II)));
    public static final Filter Deploys_on_Death_Star_II = Filters.placeToBePresentOnPlanet(Title.Death_Star_II);
    public static final Filter Deploys_on_Endor = Filters.placeToBePresentOnPlanet(Title.Endor);
    public static final Filter Deploys_at_Hoth = Filters.or(Filters.placeToBePresentOnPlanet(Title.Hoth), Filters.locationAndCardsAtLocation(Filters.title(Title.Hoth)));
    public static final Filter Deploys_on_Hoth = Filters.placeToBePresentOnPlanet(Title.Hoth);
    public static final Filter Deploys_at_Jakku = Filters.or(Filters.placeToBePresentOnPlanet(Title.Jakku), Filters.locationAndCardsAtLocation(Filters.title(Title.Jakku)));
    public static final Filter Deploys_at_Mon_Calamari = Filters.or(Filters.placeToBePresentOnPlanet(Title.Mon_Calamari), Filters.locationAndCardsAtLocation(Filters.title(Title.Mon_Calamari)));
    public static final Filter Deploys_on_Naboo = Filters.placeToBePresentOnPlanet(Title.Naboo);
    public static final Filter Deploys_at_Naboo = Filters.or(Filters.placeToBePresentOnPlanet(Title.Naboo), Filters.locationAndCardsAtLocation(Filters.title(Title.Naboo)));
    public static final Filter Deploys_at_Nal_Hutta = Filters.or(Filters.placeToBePresentOnPlanet(Title.Nal_Hutta), Filters.locationAndCardsAtLocation(Filters.title(Title.Nal_Hutta)));
    public static final Filter Deploys_at_Rebel_Base = Filters.or(Filters.placeToBePresentOnPlanet(Title.Hoth), Filters.locationAndCardsAtLocation(Filters.title(Title.Hoth)), Filters.placeToBePresentOnPlanet(Title.Yavin_4), Filters.locationAndCardsAtLocation(Filters.title(Title.Yavin_4)));
    public static final Filter Deploys_on_Rebel_Base = Filters.or(Filters.placeToBePresentOnPlanet(Title.Hoth), Filters.placeToBePresentOnPlanet(Title.Yavin_4));
    public static final Filter Deploys_at_Roche = Filters.or(Filters.placeToBePresentOnPlanet(Title.Roche), Filters.locationAndCardsAtLocation(Filters.title(Title.Roche)));
    public static final Filter Deploys_at_Sullust = Filters.or(Filters.placeToBePresentOnPlanet(Title.Sullust), Filters.locationAndCardsAtLocation(Filters.title(Title.Sullust)));
    public static final Filter Deploys_at_Tatooine = Filters.or(Filters.placeToBePresentOnPlanet(Title.Tatooine), Filters.locationAndCardsAtLocation(Filters.title(Title.Tatooine)));
    public static final Filter Deploys_on_Tatooine = Filters.placeToBePresentOnPlanet(Title.Tatooine);
    public static final Filter Deploys_to_Jabbas_Palace_site = Filters.locationAndCardsAtLocation(Filters.keyword(Keyword.JABBAS_PALACE_SITE));
    public static final Filter Deploys_at_Yavin_4 = Filters.or(Filters.placeToBePresentOnPlanet(Title.Yavin_4), Filters.locationAndCardsAtLocation(Filters.title(Title.Yavin_4)));
    public static final Filter Deploys_on_Yavin_4 = Filters.placeToBePresentOnPlanet(Title.Yavin_4);
    public static final Filter Derlin = Filters.title(Title.Derlin);
    public static final Filter desert = Filters.keyword(Keyword.DESERT);
    public static final Filter Desert_Heart = Filters.title(Title.Desert_Heart);
    public static final Filter Despair = Filters.title(Title.Despair);
    public static final Filter destroyer_droid = Filters.and(ModelType.DESTROYER, CardType.DROID);
    public static final Filter Detention_Block_Control_Room = Filters.title(Title.Detention_Block_Control_Room);
    public static final Filter Detention_Block_Corridor = Filters.title(Title.Detention_Block_Corridor);
    public static final Filter Devastator = Filters.title(Title.Devastator);
    public static final Filter device = Filters.type(CardType.DEVICE);
    public static final Filter device_that_deploys_on_droids = Filters.keyword(Keyword.DEVICE_THAT_DEPLOYS_ON_DROIDS);
    public static final Filter Dewback = Filters.keyword(Keyword.DEWBACK);
    public static final Filter DH17_blaster = Filters.keyword(Keyword.DH17_BLASTER);
    public static final Filter Dianoga = Filters.title(Title.Dianoga);
    public static final Filter Dice_Ibegon = Filters.title(Title.Dice_Ibegon);
    public static final Filter Dining_Room = Filters.title(Title.Dining_Room);
    public static final Filter Diplomatic_Mission_To_Alderaan = Filters.title(Title.Diplomatic_Mission_To_Alderaan);
    public static final Filter disarmed_character = Filters.and(CardCategory.CHARACTER, Filters.Disarmed());
    public static final Filter disarming_card = Filters.keyword(Keyword.DISARMING_CARD);
    public static final Filter Do_Or_Do_Not = Filters.title(Title.Do_Or_Do_Not);
    public static final Filter Doallyn = Filters.title(Title.Doallyn);
    public static final Filter Docking_And_Repair_Facilities = Filters.title(Title.Docking_And_Repair_Facilities);
    public static final Filter docking_bay = Filters.keyword(Keyword.DOCKING_BAY);
    public static final Filter Docking_Bay_327 = Filters.title(Title.Docking_Bay_327);
    public static final Filter Docking_Bay_94 = Filters.title(Title.Docking_Bay_94);
    public static final Filter Docking_Control_Room_327 = Filters.title(Title.Docking_Control_Room_327);
    public static final Filter Dofine = Filters.persona(Persona.DOFINE);
    public static final Filter Dolphe = Filters.title(Title.Dolphe);
    public static final Filter Dont_Get_Cocky = Filters.title(Title.Dont_Get_Cocky);
    public static final Filter Dont_Underestimate_Our_Chances = Filters.title(Title.Dont_Underestimate_Our_Chances);
    public static final Filter Dooku = Filters.persona(Persona.DOOKU);
    public static final Filter Double_Agent = Filters.title(Title.Double_Agent);
    public static final Filter Double_Back = Filters.title(Title.Double_Back);
    public static final Filter Downtown_Plaza = Filters.title(Title.Downtown_Plaza);
    public static final Filter Dr_Evazan = Filters.title(Title.Dr_Evazan);
    public static final Filter Dreadnaught_class_cruisers = Filters.modelType(ModelType.DREADNAUGHT_CLASS_HEAVY_CRUISER);
    public static final Filter droid = Filters.icon(Icon.DROID);
    public static final Filter droid_control_ship = Filters.keyword(Keyword.DROID_CONTROL_SHIP);
    public static final Filter Droid_Junkheap = Filters.title(Title.Droid_Junkheap);
    public static final Filter Droid_Merchant = Filters.title(Title.Droid_Merchant);
    public static final Filter Droid_Racks = Filters.title(Title.Droid_Racks);
    public static final Filter Droid_Shutdown = Filters.title(Title.Droid_Shutdown);
    public static final Filter droid_starfighter = Filters.modelType(ModelType.DROID_STARFIGHTER);
    public static final Filter Droid_Starfighter_Laser_Cannons = Filters.title(Title.Droid_Starfighter_Laser_Cannons);
    public static final Filter Droid_Workshop = Filters.title(Title.Droid_Workshop);
    public static final Filter Dragonsnake = Filters.title(Title.Dragonsnake);
    public static final Filter Draw_Their_Fire = Filters.title(Title.Draw_Their_Fire);
    public static final Filter Duel_Of_The_Fates = Filters.title(Title.Duel_Of_The_Fates);
    public static final Filter DS_181_3 = Filters.title(Title.DS_181_3);
    public static final Filter DS_181_4 = Filters.title(Title.DS_181_4);
    public static final Filter DS_61_2 = Filters.persona(Persona.DS_61_2);
    public static final Filter DS_61_3 = Filters.persona(Persona.DS_61_3);
    public static final Filter DS_61_5 = Filters.title(Title.DS_61_5);
    public static final Filter Dual_Laser_Cannon = Filters.title(Title.Dual_Laser_Cannon);
    public static final Filter Dune_Sea = Filters.title(Title.Dune_Sea);
    public static final Filter Dungeon = Filters.title(Title.Dungeon);
    public static final Filter Dutch = Filters.persona(Persona.DUTCH);
    public static final Filter Dyer = Filters.title(Title.Dyer);
    public static final Filter Early_Warning_Network = Filters.title(Title.Early_Warning_Network);
    public static final Filter East_Platform = Filters.title(Title.East_Platform);
    public static final Filter Echo_Base_Operations = Filters.title(Title.Echo_Base_Operations);
    public static final Filter Echo_Base_trooper = Filters.keyword(Keyword.ECHO_BASE_TROOPER);
    public static final Filter Echo_Docking_Bay = Filters.title(Title.Echo_Docking_Bay);
    public static final Filter Echo_Med_Lab = Filters.title(Title.Echo_Med_Lab);
    public static final Filter Echo_site = Filters.and(CardSubtype.SITE, Filters.titleContains("Echo"));
    public static final Filter Egregious_Pilot_Error = Filters.title(Title.Egregious_Pilot_Error);
    public static final Filter Effect = Filters.and(CardType.EFFECT, CardSubtype._);
    public static final Filter Effect_of_any_Kind = Filters.type(CardType.EFFECT);
    public static final Filter electropole = Filters.keyword(Keyword.ELECTROPOLE);
    public static final Filter Elis_Helrot = Filters.title(Title.Elis_Helrot);
    public static final Filter Elite_Squadron_Stormtrooper = Filters.title(Title.Elite_Squadron_Stormtrooper);
    public static final Filter Ellberger = Filters.title(Title.Ellberger);
    public static final Filter Ello_Asty = Filters.title(Title.Ello_Asty);
    public static final Filter Ellorrs_Madak = Filters.title(Title.Ellorrs_Madak);
    public static final Filter Emergency_Deployment = Filters.title(Title.Emergency_Deployment);
    public static final Filter Emperor = Filters.persona(Persona.EMPEROR);
    public static final Filter Emperors_Power = Filters.title(Title.Emperors_Power);
    public static final Filter Empires_New_Order = Filters.title(Title.Empires_New_Order);
    public static final Filter Empires_Sinister_Agents = Filters.title(Title.Empires_Sinister_Agents);
    public static final Filter Elom = Filters.species(Species.ELOM);
    public static final Filter enclosed_vehicle = Filters.and(Keyword.ENCLOSED, CardType.VEHICLE);
    public static final Filter End_This_Destructive_Conflict = Filters.title(Title.End_This_Destructive_Conflict);
    public static final Filter Endor_location = Filters.partOfSystem(Title.Endor);
    public static final Filter Endor_Operations = Filters.title(Title.Endor_Operations);
    public static final Filter Endor_Shield = Filters.title(Title.Endor_Shield);
    public static final Filter Endor_site = Filters.and(Filters.partOfSystem(Title.Endor), CardSubtype.SITE);
    public static final Filter Endor_system = Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Endor));
    public static final Filter Energy_Shell_Launchers = Filters.title(Title.Energy_Shell_Launchers);
    public static final Filter Entrance_Cavern = Filters.title(Title.Entrance_Cavern);
    public static final Filter Eopie = Filters.keyword(Keyword.EOPIE);
    public static final Filter Ephant_Mon = Filters.title(Title.Ephant_Mon);
    public static final Filter Epic_Duel = Filters.title(Title.Epic_Duel);
    public static final Filter Eriadu_site = Filters.and(Filters.partOfSystem(Title.Eriadu), CardSubtype.SITE);
    public static final Filter Epic_Event = Filters.type(CardType.EPIC_EVENT);
    public static final Filter Establish_Secret_Base = Filters.title(Title.Establish_Secret_Base);
    public static final Filter Evader = Filters.title(Title.Evader);
    public static final Filter Evacuation_Control = Filters.title(Title.Evacuation_Control);
    public static final Filter Ewok = Filters.species(Species.EWOK);
    public static final Filter Ewok_Bow = Filters.title(Title.Ewok_Bow);
    public static final Filter Ewok_device = Filters.keyword(Keyword.EWOK_DEVICE);
    public static final Filter Ewok_glider = Filters.title(Title.Ewok_Glider);
    public static final Filter Ewok_Rescue = Filters.title(Title.Ewok_Rescue);
    public static final Filter Ewok_Spearman = Filters.title(Title.Ewok_Spearman);
    public static final Filter Ewok_Village = Filters.title(Title.Ewok_Village);
    public static final Filter Ewok_weapon = Filters.keyword(Keyword.EWOK_WEAPON);
    public static final Filter Executor = Filters.persona(Persona.EXECUTOR);
    public static final Filter Executor_site = Filters.siteOfStarshipOrVehicle(Persona.EXECUTOR, false);
    public static final Filter Expand_The_Empire = Filters.title(Title.Expand_The_Empire);
    public static final Filter Explosive_Charge = Filters.title(Title.Explosive_Charge);
    public static final Filter exterior_battleground_site = Filters.and(Filters.icon(Icon.EXTERIOR_SITE), Filters.battleground());
    public static final Filter exterior_Dagobah_site = Filters.and(Icon.EXTERIOR_SITE, Filters.partOfSystem(Title.Dagobah));
    public static final Filter exterior_Endor_site = Filters.and(Icon.EXTERIOR_SITE, Filters.partOfSystem(Title.Endor));
    public static final Filter exterior_Hoth_site = Filters.and(Icon.EXTERIOR_SITE, Filters.partOfSystem(Title.Hoth));
    public static final Filter exterior_marker_site = Filters.and(Icon.EXTERIOR_SITE, Filters.or(Keyword.MARKER_1, Keyword.MARKER_2, Keyword.MARKER_3, Keyword.MARKER_4, Keyword.MARKER_5, Keyword.MARKER_6, Keyword.MARKER_7));
    public static final Filter exterior_Naboo_site = Filters.and(Icon.EXTERIOR_SITE, Filters.partOfSystem(Title.Naboo));
    public static final Filter exterior_planet_site = Filters.and(Icon.EXTERIOR_SITE, Icon.PLANET);
    public static final Filter exterior_site = Filters.icon(Icon.EXTERIOR_SITE);
    public static final Filter exterior_Tatooine_site = Filters.and(Icon.EXTERIOR_SITE, Filters.partOfSystem(Title.Tatooine));
    public static final Filter Eyes_In_The_Dark = Filters.title(Title.Eyes_In_The_Dark);
    public static final Filter Ezra = Filters.title(Title.Ezra);
    public static final Filter Fambaa = Filters.keyword(Keyword.FAMBAA);
    public static final Filter Falcon = Filters.persona(Persona.FALCON);
    public static final Filter Falleens_Fist = Filters.title(Title.Falleens_Fist);
    public static final Filter Fallen_Portal = Filters.title(Title.Fallen_Portal);
    public static final Filter farm = Filters.keyword(Keyword.FARM);
    public static final Filter Fear_Will_Keep_Them_In_Line = Filters.title(Title.Fear_Will_Keep_Them_In_Line);
    public static final Filter Fearless_And_Inventive = Filters.title(Title.Fearless_And_Inventive);
    public static final Filter Fel = Filters.title(Title.Fel);
    public static final Filter Feltipern_Trevagg = Filters.title(Title.Feltipern_Trevagg);
    public static final Filter Fett = Filters.or(Filters.persona(Persona.BOBA_FETT), Filters.title(Title.Jango_Fett));
    public static final Filter female = Filters.and(CardCategory.CHARACTER, Keyword.FEMALE);
    public static final Filter Free_Ride = Filters.title(Title.Free_Ride);
    public static final Filter Fifth_Marker = Filters.keyword(Keyword.MARKER_5);
    public static final Filter Fighters_Coming_In = Filters.title(Title.Fighters_Coming_In);
    public static final Filter Finalizer = Filters.title(Title.Finalizer);
    public static final Filter Finn = Filters.title(Title.Finn);
    public static final Filter First_Marker = Filters.keyword(Keyword.MARKER_1);
    public static final Filter First_Order_character = Filters.and(Icon.FIRST_ORDER, CardCategory.CHARACTER);
    public static final Filter First_Order_leader = Filters.and(Icon.FIRST_ORDER, CardCategory.CHARACTER, Keyword.LEADER);
    public static final Filter First_Order_pilot = Filters.and(Icon.FIRST_ORDER, CardCategory.CHARACTER, Icon.PILOT);
    public static final Filter First_Order_warrior = Filters.and(Icon.FIRST_ORDER, CardCategory.CHARACTER, Icon.WARRIOR);
    public static final Filter Fixer = Filters.title(Title.Fixer);
    public static final Filter Flagship = Filters.title(Title.Flagship);
    public static final Filter Flagship_Operations = Filters.title(Title.Flagship_Operations);
    public static final Filter Floating_Refinery = Filters.title(Title.Floating_Refinery);
    public static final Filter Focused_Attack = Filters.title(Title.Focused_Attack);
    public static final Filter Fondor = Filters.title(Title.Fondor);
    public static final Filter Force_Attuned_character = Filters.and(CardCategory.CHARACTER, Filters.abilityEqualTo(3));
    public static final Filter Force_Lightning = Filters.title(Title.Force_Lightning);
    public static final Filter Force_pike = Filters.keyword(Keyword.FORCE_PIKE);
    public static final Filter Force_Push = Filters.title(Title.Force_Push);
    public static final Filter Force_Projection = Filters.title(Title.Force_Projection);
    public static final Filter Force_Sensitive_character = Filters.and(CardCategory.CHARACTER, Filters.or(Filters.abilityEqualTo(4), Filters.abilityEqualTo(5)));
    public static final Filter forest = Filters.keyword(Keyword.FOREST);
    public static final Filter Fourth_Marker = Filters.keyword(Keyword.MARKER_4);
    public static final Filter freighter = Filters.or(ModelType.FREIGHTER, ModelType.HEAVILY_MODIFIED_FREIGHTER, ModelType.HEAVILY_MODIFIED_LIGHT_FREIGHTER, ModelType.MODIFIED_ACTION_VI_FREIGHTER, ModelType.MODIFIED_CORELLIAN_FREIGHTER, ModelType.MODIFIED_LIGHT_FREIGHTER, ModelType.MODIFIED_VCX_FREIGHTER, ModelType.YV_CLASS_FREIGHTER);
    public static final Filter Friendly_Fire = Filters.title(Title.Friendly_Fire);
    public static final Filter Frozen_Assets = Filters.title(Title.Frozen_Assets);
    public static final Filter Frustration = Filters.title(Title.Frustration);
    public static final Filter Full_Scale_Alert = Filters.title(Title.Full_Scale_Alert);
    public static final Filter Furry_Fury = Filters.title(Title.Furry_Fury);
    public static final Filter fusion_generator = Filters.keyword(Keyword.FUSION_GENERATOR);
    public static final Filter Fusion_Generator_Supply_Tanks = Filters.title(Title.Fusion_Generator_Supply_Tanks);
    public static final Filter FX_droid = Filters.and(Icon.DROID, Filters.titleContains("FX"));
    public static final Filter Gaderffii_Stick = Filters.title(Title.Gaderffii_Stick);
    public static final Filter Gailid = Filters.title(Title.Gailid);
    public static final Filter Galactic_Senate = Filters.title(Title.Galactic_Senate);
    public static final Filter Gall_system = Filters.title(Title.Gall);
    public static final Filter Gamall_Wironicc = Filters.title(Title.Gamall_Wironicc);
    public static final Filter gambler = Filters.keyword(Keyword.GAMBLER);
    public static final Filter Gamorrean = Filters.species(Species.GAMORREAN);
    public static final Filter Gamorrean_Guard = Filters.title(Title.Gamorrean_Guard);
    public static final Filter gangster = Filters.keyword(Keyword.GANGSTER);
    public static final Filter Garrison_Destroyed = Filters.title(Title.Garrison_Destroyed);
    public static final Filter gas_miner = Filters.keyword(Keyword.GAS_MINER);
    public static final Filter general = Filters.keyword(Keyword.GENERAL);
    public static final Filter General_Calrissian = Filters.title(Title.General_Calrissian);
    public static final Filter General_Dodonna = Filters.title(Title.General_Dodonna);
    public static final Filter generic_site = Filters.and(Filters.subtype(CardSubtype.SITE), Filters.generic());
    public static final Filter Get_Alongside_That_One = Filters.title(Title.Get_Alongside_That_One);
    public static final Filter Ghhhk = Filters.title(Title.Ghhhk);
    public static final Filter Ghost = Filters.title(Title.Ghost);
    public static final Filter Gift_Of_The_Mentor = Filters.title(Title.Gift_Of_The_Mentor);
    public static final Filter Glancing_Blow = Filters.title(Title.Glancing_Blow);
    public static final Filter Gold_1 = Filters.persona(Persona.GOLD_1);
    public static final Filter Gold_2 = Filters.title(Title.Gold_2);
    public static final Filter Gold_3 = Filters.title(Title.Gold_3);
    public static final Filter Gold_4 = Filters.title(Title.Gold_4);
    public static final Filter Gold_5 = Filters.title(Title.Gold_5);
    public static final Filter Gold_6 = Filters.title(Title.Gold_6);
    public static final Filter Gold_Squadron_Pilot = Filters.and(CardCategory.CHARACTER, Icon.PILOT, Filters.or(Keyword.GOLD_SQUADRON, Filters.aboard(Filters.keyword(Keyword.GOLD_SQUADRON))));
    public static final Filter Goo_Nee_Tay = Filters.title(Title.Goo_Nee_Tay);
    public static final Filter Graak = Filters.title(Title.Graak);
    public static final Filter grabber = Filters.icon(Icon.GRABBER);
    public static final Filter Graveyard_Of_Giants = Filters.title(Title.Graveyard_Of_Giants);
    public static final Filter Gray_Squadron_2 = Filters.title(Title.Gray_Squadron_2);
    public static final Filter Gray_Squadron_Y_wing = Filters.and(Keyword.GRAY_SQUADRON, ModelType.Y_WING);
    public static final Filter Great_Pit_Of_Carkoon = Filters.title(Title.Great_Pit_Of_Carkoon);
    public static final Filter Great_Warrior = Filters.title(Title.Great_Warrior);
    public static final Filter Greedo = Filters.title(Title.Greedo);
    public static final Filter Green_Leader = Filters.persona(Persona.GREEN_LEADER);
    public static final Filter Green_Squadron_1 = Filters.persona(Persona.GREEN_SQUADRON_1);
    public static final Filter Green_Squadron_3 = Filters.persona(Persona.GREEN_SQUADRON_3);
    public static final Filter Grievous = Filters.persona(Persona.GRIEVOUS);
    public static final Filter Grimtaash = Filters.title(Title.Grimtaash);
    public static final Filter Grond = Filters.title(Title.Grond);
    public static final Filter Gungan = Filters.species(Species.GUNGAN);
    public static final Filter Gungan_Energy_Shield = Filters.title(Title.Gungan_Energy_Shield);
    public static final Filter gunner = Filters.keyword(Keyword.GUNNER);
    public static final Filter Gunray = Filters.persona(Persona.GUNRAY);
    public static final Filter Guri = Filters.title(Title.Guri);
    public static final Filter Haako = Filters.persona(Persona.HAAKO);
    public static final Filter Halt = Filters.title(Title.Halt);
    public static final Filter Hammerhead = Filters.modelType(ModelType.HAMMERHEAD_CORVETTE);
    public static final Filter Han = Filters.persona(Persona.HAN);
    public static final Filter handmaiden = Filters.keyword(Keyword.HANDMAIDEN);
    public static final Filter Hans_Back = Filters.title(Title.Hans_Back);
    public static final Filter Hans_Toolkit = Filters.title(Title.Hans_Toolkit);
    public static final Filter Harc = Filters.title(Title.Harc);
    public static final Filter Harvest = Filters.title(Title.Harvest);
    public static final Filter has_Scomp_link = Filters.icon(Icon.SCOMP_LINK);
    public static final Filter Haven = Filters.title(Title.Haven);
    public static final Filter He_Is_The_Chosen_One = Filters.title(Title.He_Is_The_Chosen_One);
    public static final Filter He_Will_Bring_Balance = Filters.title(Title.He_Will_Bring_Balance);
    public static final Filter Heart_Of_The_Chasm = Filters.title(Title.Heart_Of_The_Chasm);
    public static final Filter Hebsly = Filters.title(Title.Hebsly);
    public static final Filter Hera = Filters.title(Title.Hera);
    public static final Filter Hero_Of_A_Thousand_Devices = Filters.title(Title.Hero_Of_A_Thousand_Devices);
    public static final Filter Heroic_Sacrifice = Filters.title(Title.Heroic_Sacrifice);
    public static final Filter Hewex = Filters.title(Title.Hewex);
    public static final Filter Hidden_Base = Filters.title(Title.Hidden_Base);
    public static final Filter Hidden_Forest_Trail = Filters.title(Title.Hidden_Forest_Trail);
    public static final Filter Hidden_Weapons = Filters.title(Title.Hidden_Weapons);
    public static final Filter Highspeed_Tactics = Filters.title(Title.Highspeed_Tactics);
    public static final Filter His_Name_Is_Anakin = Filters.title(Title.His_Name_Is_Anakin);
    public static final Filter hit_character = Filters.and(CardCategory.CHARACTER, Filters.hit());
    public static final Filter Hit_Racer = Filters.title(Title.Hit_Racer);
    public static final Filter Hobbie = Filters.title(Title.Hobbie);
    public static final Filter hologram = Filters.keyword(Keyword.HOLOGRAM);
    public static final Filter holosite = Filters.keyword(Keyword.HOLOSITE);
    public static final Filter Holotheatre = Filters.title(Title.Holotheatre);
    public static final Filter Home_One = Filters.persona(Persona.HOME_ONE);
    public static final Filter Honor_Of_The_Jedi = Filters.title(Title.Honor_Of_The_Jedi);
    public static final Filter Hopping_Mad = Filters.title(Title.Hopping_Mad);
    public static final Filter Hoth_location = Filters.partOfSystem(Title.Hoth);
    public static final Filter Hoth_Sentry = Filters.title(Title.Hoth_Sentry);
    public static final Filter Hoth_site = Filters.and(Filters.partOfSystem(Title.Hoth), CardSubtype.SITE);
    public static final Filter Hoth_system = Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Hoth));
    public static final Filter Houjix = Filters.title(Title.Houjix);
    public static final Filter Hounds_Tooth = Filters.persona(Persona.HOUNDS_TOOTH);
    public static final Filter How_Did_We_Get_Into_This_Mess = Filters.title(Title.How_Did_We_Get_Into_This_Mess);
    public static final Filter Human_Shield = Filters.title(Title.Human_Shield);
    public static final Filter Hunt_Down_And_Destroy_The_Jedi = Filters.title(Title.Hunt_Down_And_Destroy_The_Jedi);
    public static final Filter Hutt = Filters.species(Species.HUTT);
    public static final Filter Hutt_Bounty = Filters.title(Title.Hutt_Bounty);
    public static final Filter Hutt_Influence = Filters.title(Title.Hutt_Influence);
    public static final Filter Hutt_Trade_Route = Filters.title(Title.Hutt_Trade_Route);
    public static final Filter Hux = Filters.title(Title.Hux);
    public static final Filter Hydroponics_Station = Filters.title(Title.Hydroponics_Station);
    public static final Filter Hyper_Escape = Filters.title(Title.Hyper_Escape);
    public static final Filter Hypo = Filters.title(Title.Hypo);
    public static final Filter I_Am_Your_Father = Filters.title(Title.I_Am_Your_Father);
    public static final Filter I_Can_Save_Him = Filters.title(Title.I_Can_Save_Him);
    public static final Filter I_Did_It = Filters.title(Title.I_Did_It);
    public static final Filter I_Feel_The_Conflict = Filters.title(Title.I_Feel_The_Conflict);
    public static final Filter I_Have_You_Now = Filters.title(Title.I_Have_You_Now);
    public static final Filter I_Know = Filters.title(Title.I_Know);
    public static final Filter I_Shall_Enjoy_Watching_You_Die = Filters.title(Title.I_Shall_Enjoy_Watching_You_Die);
    public static final Filter I_Want_That_Map = Filters.title(Title.I_Want_That_Map);
    public static final Filter I_Want_That_Ship = Filters.title(Title.I_Want_That_Ship);
    public static final Filter I_Will_Find_Them_Quickly_Master = Filters.title(Title.I_Will_Find_Them_Quickly_Master);
    public static final Filter I_Will_Finish_What_You_Started = Filters.title(Title.I_Will_Finish_What_You_Started);
    public static final Filter I_Will_Make_It_Legal = Filters.title(Title.I_Will_Make_It_Legal);
    public static final Filter Ice_Plains = Filters.title(Title.Ice_Plains);
    public static final Filter Ice_Storm = Filters.title(Title.Ice_Storm);
    public static final Filter Id_Just_As_Soon_Kiss_A_Wookiee = Filters.title(Title.Id_Just_As_Soon_Kiss_A_Wookiee);
    public static final Filter If_The_Trace_Was_Correct = Filters.title(Title.If_The_Trace_Was_Correct);
    public static final Filter IG88 = Filters.persona(Persona.IG88);
    public static final Filter Igar = Filters.title(Title.Igar);
    public static final Filter I_Had_No_Choice = Filters.title(Title.I_Had_No_Choice);
    public static final Filter Im_Here_To_Rescue_You = Filters.title(Title.Im_Here_To_Rescue_You);
    public static final Filter Im_On_The_Leader = Filters.title(Title.Im_On_The_Leader);
    public static final Filter Im_With_You_Too = Filters.title(Title.Im_With_You_Too);
    public static final Filter Immediate_Effect = Filters.and(CardType.EFFECT, Filters.subtype(CardSubtype.IMMEDIATE));
    public static final Filter immune_to_Alter = Filters.immuneToCardTitle(Title.Alter);
    public static final Filter immune_to_Control = Filters.immuneToCardTitle(Title.Control);
    public static final Filter immune_to_Sense = Filters.immuneToCardTitle(Title.Sense);
    public static final Filter Imperial = Filters.icon(Icon.IMPERIAL);
    public static final Filter Imperial_Arrest_Order = Filters.title(Title.Imperial_Arrest_Order);
    public static final Filter Imperial_Artillery = Filters.title(Title.Imperial_Artillery);
    public static final Filter Imperial_Atrocity = Filters.title(Title.Imperial_Atrocity);
    public static final Filter Imperial_Barrier = Filters.title(Title.Imperial_Barrier);
    public static final Filter Imperial_City = Filters.title(Title.Imperial_City);
    public static final Filter Imperial_class_Star_Destroyer = Filters.modelType(ModelType.IMPERIAL_CLASS_STAR_DESTROYER);
    public static final Filter Imperial_Code_Cylinder = Filters.title(Title.Imperial_Code_Cylinder);
    public static final Filter Imperial_Control = Filters.title(Title.Imperial_Control);
    public static final Filter Imperial_Decree = Filters.title(Title.Imperial_Decree);
    public static final Filter Imperial_Entanglements = Filters.title(Title.Imperial_Entanglements);
    public static final Filter Imperial_Holotable = Filters.title(Title.Imperial_Holotable);
    public static final Filter Imperial_leader = Filters.and(Filters.icon(Icon.IMPERIAL), Filters.keyword(Keyword.LEADER));
    public static final Filter Imperial_Occupation = Filters.title(Title.Imperial_Occupation);
    public static final Filter Imperial_Outpost = Filters.title(Title.Imperial_Outpost);
    public static final Filter Imperial_Propaganda = Filters.title(Title.Imperial_Propaganda);
    public static final Filter Imperial_Reinforcements = Filters.title(Title.Imperial_Reinforcements);
    public static final Filter Imperial_starship = Filters.and(Side.DARK, CardType.STARSHIP, Filters.not(Filters.or(Icon.INDEPENDENT, Icon.REPUBLIC, Icon.TRADE_FEDERATION, Icon.SEPARATIST, Icon.CLONE_ARMY, Icon.FIRST_ORDER, Icon.RESISTANCE)));
    public static final Filter Imperial_Supply = Filters.title(Title.Imperial_Supply);
    public static final Filter Imperial_Trooper_Guard = Filters.keyword(Keyword.IMPERIAL_TROOPER_GUARD);
    public static final Filter Imperial_Tyranny = Filters.title(Title.Imperial_Tyranny);
    public static final Filter Imperial_veteran = Filters.and(Icon.IMPERIAL, Filters.or(Keyword.LEADER, Filters.and(Filters.or(Keyword.TROOPER, Keyword.STORMTROOPER, Keyword.SNOWTROOPER, Keyword.SANDTROOPER, Keyword.CLOUD_CITY_TROOPER, Keyword.DEATH_STAR_TROOPER, Keyword.IMPERIAL_TROOPER_GUARD, Keyword.BIKER_SCOUT), Filters.not(Filters.keyword(Keyword.CADET)))));
    public static final Filter imprisioned_character = Filters.and(CardCategory.CHARACTER, Filters.imprisoned());
    public static final Filter In_Complete_Control = Filters.title(Title.In_Complete_Control);
    public static final Filter In_The_Hands_Of_The_Empire = Filters.title(Title.In_The_Hands_Of_The_Empire);
    public static final Filter Incinerator = Filters.title(Title.Incinerator);
    public static final Filter Independent_Operation = Filters.title(Title.Independent_Operation);
    public static final Filter infantry_battle_droid = Filters.keyword(Keyword.INFANTRY_BATTLE_DROID);
    public static final Filter Infantry_Mine = Filters.title(Title.Infantry_Mine);
    public static final Filter information_broker = Filters.keyword(Keyword.INFORMATION_BROKER);
    public static final Filter Information_Exchange = Filters.title(Title.Information_Exchange);
    public static final Filter Inner_Strength = Filters.title(Title.Inner_Strength);
    public static final Filter Innocent_Scoundrel = Filters.title(Title.Innocent_Scoundrel);
    public static final Filter Insidious_Prisoner = Filters.title(Title.Insidious_Prisoner);
    public static final Filter Insurrection = Filters.title(Title.Insurrection);
    public static final Filter Intensify_The_Forward_Batteries = Filters.title(Title.Intensify_The_Forward_Batteries);
    public static final Filter interior_mobile_site = Filters.and(Icon.INTERIOR_SITE, Icon.MOBILE);
    public static final Filter interior_Naboo_site = Filters.and(Icon.INTERIOR_SITE, Filters.partOfSystem(Title.Naboo));
    public static final Filter interior_planet_site = Filters.and(Icon.INTERIOR_SITE, Icon.PLANET);
    public static final Filter interior_site = Filters.icon(Icon.INTERIOR_SITE);
    public static final Filter interior_Theed_Palace_site = Filters.and(Icon.INTERIOR_SITE, Filters.keyword(Keyword.THEED_PALACE_SITE));
    public static final Filter interior_vehicle_site = Filters.and(Icon.INTERIOR_SITE, Icon.VEHICLE_SITE);
    public static final Filter interior_Yavin_4_site = Filters.and(Icon.INTERIOR_SITE, Filters.partOfSystem(Title.Yavin_4));
    public static final Filter Interrogation_Array = Filters.title(Title.Interrogation_Array);
    public static final Filter Interrupt = Filters.category(CardCategory.INTERRUPT);
    public static final Filter Into_The_Garbage_Chute_Flyboy = Filters.title(Title.Into_The_Garbage_Chute_Flyboy);
    public static final Filter Into_The_Ventilation_Shaft_Lefty = Filters.title(Title.Into_The_Ventilation_Shaft_Lefty);
    public static final Filter Invasion = Filters.title(Title.Invasion);
    public static final Filter Invisible_Hand = Filters.persona(Persona.INVISIBLE_HAND);
    public static final Filter In_Hand = Filters.zone(Zone.HAND);
    public static final Filter in_play = Filters.onTable();
    public static final Filter In_Sabacc_Hand = Filters.or(Zone.SABACC_HAND, Zone.REVEALED_SABACC_HAND);
    public static final Filter Insignificant_Rebellion = Filters.title(Title.Insignificant_Rebellion);
    public static final Filter ion_cannon = Filters.keyword(Keyword.ION_CANNON);
    public static final Filter Ion_Cannon = Filters.title(Title.Ion_Cannon);
    public static final Filter Irol = Filters.title(Title.Irol);
    public static final Filter ISB_agent = Filters.keyword(Keyword.ISB_AGENT);
    public static final Filter ISB_Operations = Filters.title(Title.ISB_Operations);
    public static final Filter Ishi_Tib = Filters.species(Species.ISHI_TIB);
    public static final Filter IT0 = Filters.title(Title.IT0);
    public static final Filter It_Could_Be_Worse = Filters.title(Title.It_Could_Be_Worse);
    public static final Filter Its_A_Trap = Filters.title(Title.Its_A_Trap);
    public static final Filter Its_An_Older_Code = Filters.title(Title.Its_An_Older_Code);
    public static final Filter Its_Worse = Filters.title(Title.Its_Worse);
    public static final Filter Ive_Got_A_Problem_Here = Filters.title(Title.Ive_Got_A_Problem_Here);
    public static final Filter Jabba = Filters.persona(Persona.JABBA);
    public static final Filter Jabbas_Influence = Filters.title(Title.Jabbas_Influence);
    public static final Filter Jabbas_Palace = Filters.title(Title.Jabbas_Palace);
    public static final Filter Jabbas_Palace_site = Filters.keyword(Keyword.JABBAS_PALACE_SITE);
    public static final Filter Jabbas_Prize = Filters.title(Title.Jabbas_Prize);
    public static final Filter Jabbas_Sail_Barge = Filters.persona(Persona.JABBAS_SAIL_BARGE);
    public static final Filter Jabbas_Space_Cruiser = Filters.title(Title.Jabbas_Space_Cruiser);
    public static final Filter Jabbas_Trophies = Filters.title(Title.Jabbas_Trophies);
    public static final Filter Jakku_battleground = Filters.and(Filters.partOfSystem(Title.Jakku), Filters.battleground());
    public static final Filter Jakku_battleground_site = Filters.and(Filters.partOfSystem(Title.Jakku), CardSubtype.SITE, Filters.battleground());
    public static final Filter Jakku_Landing_Site = Filters.title(Title.Jakku_Landing_Site);
    public static final Filter Jakku_location = Filters.partOfSystem(Title.Jakku);
    public static final Filter Jakku_site = Filters.and(CardSubtype.SITE, Filters.partOfSystem(Title.Jakku));
    public static final Filter Jakku_system = Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Jakku));
    public static final Filter Jango_Fett = Filters.title(Title.Jango_Fett);
    public static final Filter Jar_Jar = Filters.persona(Persona.JAR_JAR);
    public static final Filter Jawa = Filters.species(Species.JAWA);
    public static final Filter Jawa_Blaster = Filters.title(Title.Jawa_Blaster);
    public static final Filter Jawa_Camp = Filters.title(Title.Jawa_Camp);
    public static final Filter Jawa_Ion_Gun = Filters.title(Title.Jawa_Ion_Gun);
    public static final Filter Jawa_Pack = Filters.title(Title.Jawa_Pack);
    public static final Filter Jawa_Siesta = Filters.title(Title.Jawa_Siesta);
    public static final Filter Jawa_weapon = Filters.keyword(Keyword.JAWA_WEAPON);
    public static final Filter Jedha_location = Filters.partOfSystem(Title.Jedha);
    public static final Filter Jedi = Filters.and(Filters.side(Side.LIGHT), Filters.character, Filters.abilityMoreThanOrEqualTo(6));
    public static final Filter Jedi_Council_Chamber = Filters.title(Title.Jedi_Council_Chamber);
    public static final Filter Jedi_Council_member = Filters.keyword(Keyword.JEDI_COUNCIL_MEMBER);
    public static final Filter Jedi_Lightsaber = Filters.title(Title.Jedi_Lightsaber);
    public static final Filter Jedi_Master = Filters.type(CardType.JEDI_MASTER);
    public static final Filter Jedi_Presence = Filters.title(Title.Jedi_Presence);
    public static final Filter Jedi_Test = Filters.type(CardType.JEDI_TEST);
    public static final Filter Jedi_Test_1 = Filters.keyword(Keyword.JEDI_TEST_1);
    public static final Filter Jedi_Test_2 = Filters.keyword(Keyword.JEDI_TEST_2);
    public static final Filter Jedi_Test_3 = Filters.keyword(Keyword.JEDI_TEST_3);
    public static final Filter Jedi_Test_4 = Filters.keyword(Keyword.JEDI_TEST_4);
    public static final Filter Jedi_Test_5 = Filters.keyword(Keyword.JEDI_TEST_5);
    public static final Filter Jedi_Test_6 = Filters.keyword(Keyword.JEDI_TEST_6);
    public static final Filter Jek = Filters.title(Title.Jek);
    public static final Filter Jendon = Filters.persona(Persona.JENDON);
    public static final Filter Jerjerrod = Filters.title(Title.Jerjerrod);
    public static final Filter Jet_Pack = Filters.title(Title.Jet_Pack);
    public static final Filter Jodo = Filters.title(Title.Jodo);
    public static final Filter Judicator = Filters.title(Title.Judicator);
    public static final Filter Jundland_Wastes = Filters.title(Title.Jundland_Wastes);
    public static final Filter jungle = Filters.keyword(Keyword.JUNGLE);
    public static final Filter Juri_Juice = Filters.title(Title.Juri_Juice);
    public static final Filter justice_agenda = Filters.agenda(Agenda.JUSTICE);
    public static final Filter K2SO = Filters.title(Title.K2SO);
    public static final Filter Kaadu = Filters.keyword(Keyword.KAADU);
    public static final Filter Kabe = Filters.title(Title.Kabe);
    public static final Filter KalFalnl_Cndros = Filters.title(Title.KalFalnl_Cndros);
    public static final Filter Kalit = Filters.title(Title.Kalit);
    public static final Filter Kanan = Filters.title(Title.Kanan);
    public static final Filter Karie_Neth = Filters.title(Title.Karie_Neth);
    public static final Filter Kashyyyk_site = Filters.and(Filters.partOfSystem(Title.Kashyyyk), CardSubtype.SITE);
    public static final Filter Kashyyyk_system = Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Kashyyyk));
    public static final Filter Kowakian = Filters.species(Species.KOWAKIAN);
    public static final Filter Keep_Your_Eyes_Open = Filters.title(Title.Keep_Your_Eyes_Open);
    public static final Filter Kessel_system = Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Kessel));
    public static final Filter Kessel_Run = Filters.title(Title.Kessel_Run);
    public static final Filter Kessel_site = Filters.and(Filters.partOfSystem(Title.Kessel), CardSubtype.SITE);
    public static final Filter Ket_Maliss = Filters.title(Title.Ket_Maliss);
    public static final Filter Ketwol = Filters.title(Title.Ketwol);
    public static final Filter Kian = Filters.title(Title.Kian);
    public static final Filter Keir_Santage = Filters.title(Title.Keir_Santage);
    public static final Filter Kiffex_site = Filters.and(Filters.partOfSystem(Title.Kiffex), CardSubtype.SITE);
    public static final Filter Kirdo_III_site = Filters.and(Filters.partOfSystem(Title.Kirdo_III), CardSubtype.SITE);
    public static final Filter Kitonak = Filters.species(Species.KITONAK);
    public static final Filter Klaatu = Filters.title(Title.Klaatu);
    public static final Filter Krayt_Dragon_Bones = Filters.title(Title.Krayt_Dragon_Bones);
    public static final Filter Krennic = Filters.persona(Persona.KRENNIC);
    public static final Filter Kuat_Drive_Yards = Filters.title(Title.Kuat_Drive_Yards);
    public static final Filter Kuat_system = Filters.title(Title.Kuat);
    public static final Filter Kylo = Filters.persona(Persona.KYLO);
    public static final Filter Kylos_Lightsaber = Filters.persona(Persona.KYLOS_LIGHTSABER);
    public static final Filter Lambda_shuttle = Filters.modelType(ModelType.LAMBDA_CLASS_SHUTTLE);
    public static final Filter Lana_Dobreed = Filters.title(Title.Lana_Dobreed);
    public static final Filter Landing_Claw = Filters.title(Title.Landing_Claw);
    public static final Filter Landing_Craft = Filters.modelType(ModelType.SENTINEL_CLASS_LANDING_CRAFT);
    public static final Filter Landing_Platform = Filters.title(Title.Landing_Platform);
    public static final Filter Lando = Filters.persona(Persona.LANDO);
    public static final Filter Lars_Moisture_Farm = Filters.title(Title.Lars_Moisture_Farm);
    public static final Filter laser_cannon = Filters.keyword(Keyword.LASER_CANNON);
    public static final Filter Laser_Cannon_Battery = Filters.title(Title.Laser_Cannon_Battery);
    public static final Filter Laser_Gate = Filters.title(Title.Laser_Gate);
    public static final Filter Lateral_Damage = Filters.title(Title.Lateral_Damage);
    public static final Filter launch_bay = Filters.title(Title.Launch_Bay);
    public static final Filter leader = Filters.keyword(Keyword.LEADER);
    public static final Filter Leave_Them_To_Me = Filters.title(Title.Leave_Them_To_Me);
    public static final Filter Leebo = Filters.title(Title.Leebo);
    public static final Filter Leia = Filters.persona(Persona.LEIA);
    public static final Filter Leia_Of_Alderaan = Filters.title(Title.Leia_Of_Alderaan);
    public static final Filter Let_The_Wookiee_Win = Filters.title(Title.Let_The_Wookiee_Win);
    public static final Filter Let_Them_Make_The_First_Move = Filters.title(Title.Let_Them_Make_The_First_Move);
    public static final Filter Lets_Keep_A_Little_Optimism_Here = Filters.title(Title.Lets_Keep_A_Little_Optimism_Here);
    public static final Filter Levitation_Attack = Filters.title(Title.Levitation_Attack);
    public static final Filter liberated_system = Filters.and(CardSubtype.SYSTEM, Filters.hasStacked(Filters.liberationCard()));
    public static final Filter Liberation = Filters.title(Title.Liberation);
    public static final Filter Lieutenant_Blount = Filters.title(Title.Lieutenant_Blount);
    public static final Filter Lieutenant_Page = Filters.title(Title.Lieutenant_Page);
    public static final Filter Life_Debt = Filters.title(Title.Life_Debt);
    public static final Filter lightsaber = Filters.keyword(Keyword.LIGHTSABER);
    public static final Filter Lightsaber_Proficiency = Filters.title(Title.Lightsaber_Proficiency);
    public static final Filter Limited_Resources = Filters.title(Title.Limited_Resources);
    public static final Filter Lirin_Carn = Filters.title(Title.Lirin_Carn);
    public static final Filter Lobot = Filters.persona(Persona.LOBOT);
    public static final Filter Local_Trouble = Filters.title(Title.Local_Trouble);
    public static final Filter Local_Uprising = Filters.title(Title.Local_Uprising);
    public static final Filter location = Filters.category(CardCategory.LOCATION);
    public static final Filter Logistical_Delay = Filters.title(Title.Logistical_Delay);
    public static final Filter Logray = Filters.title(Title.Logray);
    public static final Filter Lone_Pilot = Filters.title(Title.Lone_Pilot);
    public static final Filter Lone_Rogue = Filters.title(Title.Lone_Rogue);
    public static final Filter Look_Sir_Droids = Filters.title(Title.Look_Sir_Droids);
    public static final Filter Lor_San_Tekka = Filters.title(Title.Lor_San_Tekka);
    public static final Filter Losing_Track = Filters.title(Title.Losing_Track);
    public static final Filter Lost_In_The_Wilderness = Filters.title(Title.Lost_In_The_Wilderness);
    public static final Filter Lower_Passages = Filters.title(Title.Lower_Passages);
    public static final Filter Luke = Filters.persona(Persona.LUKE);
    public static final Filter Lukes_Backpack = Filters.title(Title.Lukes_Backpack);
    public static final Filter Lukes_Lightsaber = Filters.persona(Persona.LUKES_LIGHTSABER);
    public static final Filter Lukes_T16_Skyhopper = Filters.title(Title.Lukes_T16_Skyhopper);
    public static final Filter Lukes_X34_Landspeeder = Filters.title(Title.Lukes_X34_Landspeeder);
    public static final Filter Lumat = Filters.title(Title.Lumat);
    public static final Filter Mace = Filters.persona(Persona.MACE);
    public static final Filter Madakor = Filters.title(Title.Madakor);
    public static final Filter Madine = Filters.title(Title.Madine);
    public static final Filter Magnetic_Suction_Tube = Filters.title(Title.Magnetic_Suction_Tube);
    public static final Filter Main_Corridor = Filters.title(Title.Main_Corridor);
    public static final Filter Main_Power_Generators = Filters.title(Title.Main_Power_Generators);
    public static final Filter maintenance_droid = Filters.modelType(ModelType.MAINTENANCE);
    public static final Filter Major_Panno = Filters.title(Title.Major_Panno);
    public static final Filter Malakili = Filters.title(Title.Malakili);
    public static final Filter Malastare = Filters.title(Title.Malastare);
    public static final Filter male = Filters.and(CardCategory.CHARACTER, Filters.or(Keyword.MALE, Filters.not(Filters.keyword(Keyword.FEMALE))));
    public static final Filter Mandalorian_Armor = Filters.title(Title.Mandalorian_Armor);
    public static final Filter Maneuvering_Flaps = Filters.title(Title.Maneuvering_Flaps);
    public static final Filter Mantellian_Savrip = Filters.title(Title.Mantellian_Savrip);
    public static final Filter Mara_Jade = Filters.persona(Persona.MARA_JADE);
    public static final Filter marker_site = Filters.or(Keyword.MARKER_1, Keyword.MARKER_2, Keyword.MARKER_3, Keyword.MARKER_4, Keyword.MARKER_5, Keyword.MARKER_6, Keyword.MARKER_7);
    public static final Filter Marketplace = Filters.title(Title.Marketplace);
    public static final Filter Marmor = Filters.title(Title.Marmor);
    public static final Filter Marquand = Filters.title(Title.Marquand);
    public static final Filter Mas_Amedda = Filters.title(Title.Mas_Amedda);
    public static final Filter Massassi_Base_Operations = Filters.title(Title.Massassi_Base_Operations);
    public static final Filter Massassi_Headquarters = Filters.title(Title.Massassi_Headquarters);
    public static final Filter Massassi_Ruins = Filters.title(Title.Massassi_Ruins);
    public static final Filter Massassi_Throne_Room = Filters.title(Title.Massassi_Throne_Room);
    public static final Filter Massassi_War_Room = Filters.title(Title.Massassi_War_Room);
    public static final Filter Master_Luke = Filters.title(Title.Master_Luke);
    public static final Filter Maul = Filters.persona(Persona.MAUL);
    public static final Filter Mauls_Lightsaber = Filters.persona(Persona.MAULS_DOUBLE_BLADED_LIGHTSABER);
    public static final Filter Maul_Strikes = Filters.title(Title.Maul_Strikes);
    public static final Filter Maz = Filters.persona(Persona.Maz);
    public static final Filter Mazs_Palace_Location = Filters.keyword(Keyword.MAZS_PALACE_LOCATION);
    public static final Filter Mechanical_Failure = Filters.title(Title.Mechanical_Failure);
    public static final Filter medical_droid = Filters.modelType(ModelType.MEDICAL);
    public static final Filter Meditation_Chamber = Filters.title(Title.Meditation_Chamber);
    public static final Filter medium_transport = Filters.keyword(Keyword.MEDIUM_TRANSPORT);
    public static final Filter Meson_Martinet = Filters.title(Title.Meson_Martinet);
    public static final Filter Mianda = Filters.title(Title.Mianda);
    public static final Filter Miiyoom_Onith = Filters.title(Title.Miiyoom_Onith);
    public static final Filter mine = Filters.keyword(Keyword.MINE);
    public static final Filter miner = Filters.keyword(Keyword.GAS_MINER);
    public static final Filter Mind_What_You_Have_Learned = Filters.title(Title.Mind_What_You_Have_Learned);
    public static final Filter mining_droid = Filters.modelType(ModelType.MINING);
    public static final Filter Mirax = Filters.title(Title.Mirax);
    public static final Filter missile = Filters.keyword(Keyword.MISSILE);
    public static final Filter Mobile_Effect = Filters.and(CardType.EFFECT, Filters.subtype(CardSubtype.MOBILE));
    public static final Filter mobile_site = Filters.and(Icon.MOBILE, CardSubtype.SITE);
    public static final Filter mobile_sector = Filters.and(Icon.MOBILE, CardSubtype.SECTOR);
    public static final Filter mobile_system = Filters.and(Icon.MOBILE, CardSubtype.SYSTEM);
    public static final Filter Mobilization_Points = Filters.title(Title.Mobilization_Points);
    public static final Filter moff = Filters.keyword(Keyword.MOFF);
    public static final Filter Molator = Filters.title(Title.Molator);
    public static final Filter Mon_Calamari_character = Filters.and(Species.MON_CALAMARI, CardCategory.CHARACTER);
    public static final Filter Mon_Calamari_system = Filters.title(Title.Mon_Calamari);
    public static final Filter Mon_Mothma = Filters.persona(Persona.MON_MOTHMA);
    public static final Filter Monnok = Filters.title(Title.Monnok);
    public static final Filter More_Dangerous_Than_You_Realize = Filters.title(Title.More_Dangerous_Than_You_Realize);
    public static final Filter Mos_Eisley = Filters.title(Title.Mos_Eisley);
    public static final Filter Mos_Espa = Filters.title(Title.Mos_Espa);
    public static final Filter Mosep = Filters.title(Title.Mosep);
    public static final Filter Mostly_Armless = Filters.title(Title.Mostly_Armless);
    public static final Filter Motti = Filters.persona(Persona.MOTTI);
    public static final Filter Mountains = Filters.title(Title.Mountains);
    public static final Filter Mournful_Roar = Filters.title(Title.Mournful_Roar);
    public static final Filter mouse_droid = Filters.title(Title.Mouse_Droid);
    public static final Filter Momaw_Nadon = Filters.title(Title.Momaw_Nadon);
    public static final Filter MTT = Filters.modelType(ModelType.MTT);
    public static final Filter musician = Filters.keyword(Keyword.MUSICIAN);
    public static final Filter Mustafar_Location = Filters.and(CardCategory.LOCATION, Filters.partOfSystem(Title.Mustafar));
    public static final Filter Mustafar_site = Filters.and(CardSubtype.SITE, Filters.partOfSystem(Title.Mustafar));
    public static final Filter My_Favorite_Decoration = Filters.title(Title.My_Favorite_Decoration);
    public static final Filter My_Kind_Of_Scum = Filters.title(Title.My_Kind_Of_Scum);
    public static final Filter My_Lord_Is_That_Legal = Filters.title(Title.My_Lord_Is_That_Legal);
    public static final Filter mynock = Filters.title(Title.Mynock);
    public static final Filter Myo = Filters.title(Title.Myo);
    public static final Filter N1_starfighter = Filters.modelType(ModelType.N_1_STARFIGHTER);
    public static final Filter Naboo_location = Filters.partOfSystem(Title.Naboo);
    public static final Filter Naboo_site = Filters.and(Filters.partOfSystem(Title.Naboo), CardSubtype.SITE);
    public static final Filter Naboo_Swamp = Filters.title(Title.Naboo_Swamp);
    public static final Filter Naboo_system = Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Naboo));
    public static final Filter Nabrun_Leids = Filters.title(Title.Nabrun_Leids);
    public static final Filter Nal_Hutta_site = Filters.and(Filters.partOfSystem(Title.Nal_Hutta), CardSubtype.SITE);
    public static final Filter Nal_Hutta_system = Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Nal_Hutta));
    public static final Filter Narrow_Escape = Filters.title(Title.Narrow_Escape);
    public static final Filter Nav_Computer = Filters.icon(Icon.NAV_COMPUTER);
    public static final Filter Neck_And_Neck = Filters.title(Title.Neck_And_Neck);
    public static final Filter Needa = Filters.title(Title.Needa);
    public static final Filter Neimoidian = Filters.species(Species.NEIMOIDIAN);
    public static final Filter Nevar_Yalnal = Filters.title(Title.Nevar_Yalnal);
    public static final Filter Never_Tell_Me_The_Odds = Filters.title(Title.Never_Tell_Me_The_Odds);
    public static final Filter Nien_Nunb = Filters.title(Title.Nien_Nunb);
    public static final Filter Nightclub = Filters.title(Title.Nightclub);
    public static final Filter Nightfall = Filters.title(Title.Nightfall);
    public static final Filter Niima_Outpost_Shipyard = Filters.title(Title.Niima_Outpost_Shipyard);
    public static final Filter Nikto = Filters.species(Species.NIKTO);
    public static final Filter No_Bargain = Filters.title(Title.No_Bargain);
    public static final Filter No_Disintegrations = Filters.title(Title.No_Disintegrations);
    public static final Filter No_Love_For_The_Empire = Filters.title(Title.No_Love_For_The_Empire);
    public static final Filter No_Money_No_Parts_No_Deal = Filters.title(Title.No_Money_No_Parts_No_Deal);
    public static final Filter No_One_To_Stop_Us_This_Time = Filters.title(Title.No_One_To_Stop_Us_This_Time);
    public static final Filter Noble_Sacrifice = Filters.title(Title.Noble_Sacrifice);
    public static final Filter non_alien_character = Filters.and(CardCategory.CHARACTER, Filters.not(Filters.type(CardType.ALIEN)));
    public static final Filter non_battleground_location = Filters.and(CardCategory.LOCATION, Filters.not(Filters.battleground()));
    public static final Filter non_Bespin_location = Filters.and(CardCategory.LOCATION, Filters.not(Filters.partOfSystem(Title.Bespin)));
    public static final Filter non_captive = Filters.not(Filters.captive());
    public static final Filter non_cloud_sector = Filters.and(CardSubtype.SECTOR, Filters.not(Filters.keyword(Keyword.CLOUD_SECTOR)));
    public static final Filter non_creature_vehicle = Filters.and(CardCategory.VEHICLE, Filters.not(Filters.subtype(CardSubtype.CREATURE)));
    public static final Filter non_droid_character = Filters.and(CardCategory.CHARACTER, Filters.not(Filters.type(CardType.DROID)));
    public static final Filter non_Ewok_vehicle = Filters.and(CardCategory.VEHICLE, Filters.not(Filters.keyword(Keyword.EWOK_VEHICLE)));
    public static final Filter non_interior_site = Filters.and(CardSubtype.SITE, Filters.not(Filters.icon(Icon.INTERIOR_SITE)));
    public static final Filter non_Interrupt = Filters.not(Filters.category(CardCategory.INTERRUPT));
    public static final Filter non_Jedi_character = Filters.and(CardCategory.CHARACTER, Filters.not(Filters.and(Filters.side(Side.LIGHT), Filters.abilityMoreThanOrEqualTo(6))));
    public static final Filter non_pilot_character = Filters.and(CardCategory.CHARACTER, Filters.not(Filters.icon(Icon.PILOT)));
    public static final Filter non_Tatooine_location = Filters.and(CardCategory.LOCATION, Filters.not(Filters.partOfSystem(Title.Tatooine)));
    public static final Filter None_Shall_Pass = Filters.title(Title.None_Shall_Pass);
    public static final Filter NOOOOOOOOOOOO = Filters.title(Title.NOOOOOOOOOOOO);
    public static final Filter North_Ridge = Filters.title(Title.North_Ridge);
    public static final Filter ObiWan = Filters.persona(Persona.OBIWAN);
    public static final Filter ObiWans_Journal = Filters.title(Title.ObiWans_Journal);
    public static final Filter Objective = Filters.type(CardType.OBJECTIVE);
    public static final Filter Obsidian_10 = Filters.title(Title.Obsidian_10);
    public static final Filter Off_The_Edge = Filters.title(Title.Off_The_Edge);
    public static final Filter Old_Allies = Filters.title(Title.Old_Allies);
    public static final Filter Old_Ben = Filters.title(Title.Old_Ben);
    public static final Filter Ominous_Rumors = Filters.title(Title.Ominous_Rumors);
    public static final Filter Ommni_Box = Filters.title(Title.Ommni_Box);
    public static final Filter on_Cloud_City = Filters.locationAndCardsAtLocation(Filters.Cloud_City_site);
    public static final Filter On_Endor = Filters.on(Title.Endor);
    public static final Filter On_Hoth = Filters.on(Title.Hoth);
    public static final Filter On_Tatooine = Filters.on(Title.Tatooine);
    public static final Filter One_In_A_Million = Filters.title(Title.One_In_A_Million);
    public static final Filter One_More_Pass = Filters.title(Title.One_More_Pass);
    public static final Filter Onyx_1 = Filters.persona(Persona.ONYX_1);
    public static final Filter Onyx_2 = Filters.title(Title.Onyx_2);
    public static final Filter Oota_Goota_Solo = Filters.title(Title.Oota_Goota_Solo);
    public static final Filter Opee_Sea_Killer = Filters.title(Title.Opee_Sea_Killer);
    public static final Filter Open_Fire = Filters.title(Title.Open_Fire);
    public static final Filter open_vehicle = Filters.and(CardCategory.VEHICLE, Filters.not(Filters.keyword(Keyword.ENCLOSED)));
    public static final Filter Operational_As_Planned = Filters.title(Title.Operational_As_Planned);
    public static final Filter operative = Filters.keyword(Keyword.OPERATIVE);
    public static final Filter Or_Be_Destroyed = Filters.title(Title.Or_Be_Destroyed);
    public static final Filter Orbital_Mine = Filters.title(Title.Orbital_Mine);
    public static final Filter Ord_Mantell_site = Filters.and(Filters.partOfSystem(Title.Ord_Mantell), CardSubtype.SITE);
    public static final Filter Ord_Mantell_system = Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Ord_Mantell));
    public static final Filter order_agenda = Filters.agenda(Agenda.ORDER);
    public static final Filter Order_To_Engage = Filters.title(Title.Order_To_Engage);
    public static final Filter Organized_Attack = Filters.title(Title.Organized_Attack);
    public static final Filter OS_72_10 = Filters.title(Title.OS_72_10);
    public static final Filter Our_First_Catch_Of_The_Day = Filters.title(Title.Our_First_Catch_Of_The_Day);
    public static final Filter Out_Of_Commission = Filters.title(Title.Out_Of_Commission);
    public static final Filter Out_Of_Nowhere = Filters.title(Title.Out_Of_Nowhere);
    public static final Filter Outrider = Filters.title(Title.Outrider);
    public static final Filter Overload = Filters.title(Title.Overload);
    public static final Filter Overseeing_It_Personally = Filters.title(Title.Overseeing_It_Personally);
    public static final Filter Overwhelmed = Filters.title(Title.Overwhelmed);
    public static final Filter Owen = Filters.title(Title.Owen_Lars);
    public static final Filter Ozzel = Filters.title(Title.Ozzel);
    public static final Filter padawan = Filters.keyword(Keyword.PADAWAN);
    public static final Filter Padme = Filters.title(Title.Padme);
    public static final Filter Paige = Filters.title(Title.Paige);
    public static final Filter Palace_Raider = Filters.title(Title.Palace_Raider);
    public static final Filter Palpatine = Filters.persona(Persona.PALPATINE);
    public static final Filter Panaka = Filters.persona(Persona.PANAKA);
    public static final Filter Panic = Filters.title(Title.Panic);
    public static final Filter parasite = Filters.and(CardCategory.CREATURE, Keyword.PARASITE);
    public static final Filter Passenger_Deck = Filters.title(Title.Passenger_Deck);
    public static final Filter Patrol_Craft = Filters.title(Title.Patrol_Craft);
    public static final Filter peace_agenda = Filters.agenda(Agenda.PEACE);
    public static final Filter Perimeter_Patrol = Filters.title(Title.Perimeter_Patrol);
    public static final Filter Phantom = Filters.title(Title.Phantom);
    public static final Filter Phasma = Filters.title(Title.Phasma);
    public static final Filter Phennir = Filters.title(Title.Phennir);
    public static final Filter Piett = Filters.persona(Persona.PIETT);
    public static final Filter pilot = Filters.and(CardCategory.CHARACTER, Icon.PILOT);
    public static final Filter pilot_character = Filters.and(CardCategory.CHARACTER, Icon.PILOT);
    public static final Filter pirate = Filters.keyword(Keyword.PIRATE);
    public static final Filter pit = Filters.keyword(Keyword.PIT);
    public static final Filter Planet_Defender_Ion_Cannon = Filters.title(Title.Planet_Defender_Ion_Cannon);
    public static final Filter planet_location = Filters.and(CardCategory.LOCATION, Icon.PLANET);
    public static final Filter planet_site = Filters.and(CardSubtype.SITE, Icon.PLANET);
    public static final Filter planet_system = Filters.and(CardSubtype.SYSTEM, Icon.PLANET);
    public static final Filter Plastoid_Armor = Filters.title(Title.Plastoid_Armor);
    public static final Filter Plead_My_Case_To_The_Senate = Filters.title(Title.Plead_My_Case_To_The_Senate);
    public static final Filter Podrace_Arena = Filters.title(Title.Podrace_Arena);
    public static final Filter Podracer = Filters.category(CardCategory.PODRACER);
    public static final Filter Podracer_Bay = Filters.title(Title.Podracer_Bay);
    public static final Filter Podracer_Collision = Filters.title(Title.Podracer_Collision);
    public static final Filter Poe = Filters.title(Title.Poe);
    public static final Filter Political_Effect = Filters.and(CardType.EFFECT, CardSubtype.POLITICAL);
    public static final Filter Ponda_Baba = Filters.title(Title.Ponda_Baba);
    public static final Filter Portable_Fusion_Generator = Filters.title(Title.Portable_Fusion_Generator);
    public static final Filter power_droid = Filters.modelType(ModelType.POWER);
    public static final Filter power_harpoon = Filters.title(Title.Power_Harpoon);
    public static final Filter Power_Pivot = Filters.title(Title.Power_Pivot);
    public static final Filter Pray_I_Dont_Alter_It_Any_Further = Filters.title(Title.Pray_I_Dont_Alter_It_Any_Further);
    public static final Filter Precise_Attack = Filters.title(Title.Precise_Attack);
    public static final Filter Prepare_For_A_Surface_Attack = Filters.title(Title.Prepare_For_A_Surface_Attack);
    public static final Filter Prepare_The_Chamber = Filters.title(Title.Prepare_The_Chamber);
    public static final Filter Presence_Of_The_Force = Filters.title(Title.Presence_Of_The_Force);
    public static final Filter Pride_Of_The_Empire = Filters.title(Title.Pride_Of_The_Empire);
    public static final Filter prison = Filters.keyword(Keyword.PRISON);
    public static final Filter probe_droid = Filters.modelType(ModelType.PROBE);
    public static final Filter Probe_Droid = Filters.title(Title.Probe_Droid);
    public static final Filter Probe_Telemetry = Filters.title(Title.Probe_Telemetry);
    public static final Filter Profundity = Filters.title(Title.Profundity);
    public static final Filter Program_Trap = Filters.title(Title.Program_Trap);
    public static final Filter Prophecy_Of_The_Force = Filters.title(Title.Prophecy_Of_The_Force);
    public static final Filter protocol_droid = Filters.modelType(ModelType.PROTOCOL);
    public static final Filter proton_bombs = Filters.title(Title.Proton_Bombs);
    public static final Filter Proton_Torpedoes = Filters.keyword(Keyword.PROTON_TORPEDOES);
    public static final Filter Pulsar_Skate = Filters.persona(Persona.PULSAR_SKATE);
    public static final Filter Quad_Laser_Cannon = Filters.title(Title.Quad_Laser_Cannon);
    public static final Filter Queens_Royal_Starship = Filters.persona(Persona.QUEENS_ROYAL_STARSHIP);
    public static final Filter Quiet_Mining_Colony = Filters.title(Title.Quiet_Mining_Colony);
    public static final Filter Quiggold = Filters.title(Title.Quiggold);
    public static final Filter QuiGon = Filters.persona(Persona.QUIGON);
    public static final Filter Rancor = Filters.title(Title.Rancor);
    public static final Filter R_unit = Filters.or(ModelType.ASTROMECH, ModelType.VEHICLE);
    public static final Filter R2D2 = Filters.persona(Persona.R2D2);
    public static final Filter R2D2_or_has_R2D2_as_permanent_astromech = Filters.or(Filters.persona(Persona.R2D2), Filters.hasPermanentAboard(Filters.persona(Persona.R2D2)));
    public static final Filter Radar_Scanner = Filters.title(Title.Radar_Scanner);
    public static final Filter Radiant_VII = Filters.persona(Persona.RADIANT_VII);
    public static final Filter Raithal_site = Filters.and(Filters.partOfSystem(Title.Raithal), CardSubtype.SITE);
    public static final Filter Raithal_system = Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Raithal));
    public static final Filter Ralltiir_location = Filters.partOfSystem(Title.Ralltiir);
    public static final Filter Ralltiir_Operations = Filters.title(Title.Ralltiir_Operations);
    public static final Filter Ralltiir_site = Filters.and(Filters.partOfSystem(Title.Ralltiir), CardSubtype.SITE);
    public static final Filter Ralltiir_system = Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Ralltiir));
    public static final Filter Rancor_Pit = Filters.title(Title.Rancor_Pit);
    public static final Filter Ravager_Crash_Site = Filters.title(Title.Ravager_Crash_Site);
    public static final Filter Reactor_Core = Filters.title(Title.Reactor_Core);
    public static final Filter Rebel = Filters.icon(Icon.REBEL);
    public static final Filter Rebel_Ambush = Filters.title(Title.Rebel_Ambush);
    public static final Filter Rebel_Artillery = Filters.title(Title.Rebel_Artillery);
    public static final Filter Rebel_Barrier = Filters.title(Title.Rebel_Barrier);
    public static final Filter Rebel_Base_location = Filters.or(Filters.partOfSystem(Title.Yavin_4), Filters.partOfSystem(Title.Hoth));
    public static final Filter Rebel_Base_Occupation = Filters.title(Title.Rebel_Base_Occupation);
    public static final Filter Rebel_Base_site = Filters.and(CardSubtype.SITE, Filters.or(Filters.partOfSystem(Title.Yavin_4), Filters.partOfSystem(Title.Hoth)));
    public static final Filter Rebel_Base_system = Filters.or(Filters.title(Title.Yavin_4), Filters.title(Title.Hoth));
    public static final Filter Rebel_capital_starship = Filters.and(Side.LIGHT, CardType.STARSHIP, CardSubtype.CAPITAL, Filters.not(Filters.or(Icon.INDEPENDENT, Icon.REPUBLIC, Icon.TRADE_FEDERATION, Icon.SEPARATIST, Icon.CLONE_ARMY, Icon.FIRST_ORDER, Icon.RESISTANCE)));
    public static final Filter Rebel_Guard = Filters.title(Title.Rebel_Guard);
    public static final Filter Rebel_Landing_Site = Filters.title(Title.Rebel_Landing_Site);
    public static final Filter Rebel_leader = Filters.and(Icon.REBEL, Keyword.LEADER);
    public static final Filter Rebel_Planners = Filters.title(Title.Rebel_Planners);
    public static final Filter Rebel_pilot = Filters.and(Icon.REBEL, CardCategory.CHARACTER, Icon.PILOT);
    public static final Filter Rebel_Reinforcements = Filters.title(Title.Rebel_Reinforcements);
    public static final Filter Rebel_scout = Filters.and(Icon.REBEL, Keyword.SCOUT);
    public static final Filter Rebel_starfighter = Filters.and(Side.LIGHT, CardSubtype.STARFIGHTER, Filters.not(Filters.or(Icon.INDEPENDENT, Icon.REPUBLIC, Icon.TRADE_FEDERATION, Icon.SEPARATIST, Icon.CLONE_ARMY, Icon.FIRST_ORDER, Icon.RESISTANCE)));
    public static final Filter Rebel_starship = Filters.and(Side.LIGHT, CardType.STARSHIP, Filters.not(Filters.or(Icon.INDEPENDENT, Icon.REPUBLIC, Icon.TRADE_FEDERATION, Icon.SEPARATIST, Icon.CLONE_ARMY, Icon.FIRST_ORDER, Icon.RESISTANCE)));
    public static final Filter Rebel_Strike_Team = Filters.title(Title.Rebel_Strike_Team);
    public static final Filter Rebel_Tech = Filters.title(Title.Rebel_Tech);
    public static final Filter Rebel_veteran = Filters.and(Icon.REBEL, Filters.or(Keyword.LEADER, Filters.and(Filters.or(Keyword.TROOPER, Keyword.ECHO_BASE_TROOPER, Keyword.CLOUD_CITY_TROOPER), Filters.not(Filters.keyword(Keyword.RECRUIT)))));
    public static final Filter rebellion_agenda = Filters.agenda(Agenda.REBELLION);
    public static final Filter Recoil_In_Fear = Filters.title(Title.Recoil_In_Fear);
    public static final Filter Red_1 = Filters.persona(Persona.RED_1);
    public static final Filter Red_2 = Filters.persona(Persona.RED_2);
    public static final Filter Red_3 = Filters.title(Title.Red_3);
    public static final Filter Red_5 = Filters.persona(Persona.RED_5);
    public static final Filter Red_6 = Filters.title(Title.Red_6);
    public static final Filter Red_7 = Filters.title(Title.Red_7);
    public static final Filter Red_8 = Filters.title(Title.Red_8);
    public static final Filter Red_9 = Filters.title(Title.Red_9);
    public static final Filter Red_10 = Filters.title(Title.Red_10);
    public static final Filter Red_Leader = Filters.persona(Persona.RED_LEADER);
    public static final Filter Red_Squadron_pilot = Filters.and(CardCategory.CHARACTER, Icon.PILOT, Filters.or(Keyword.RED_SQUADRON, Filters.piloting(Filters.keyword(Keyword.RED_SQUADRON))));
    public static final Filter Red_Squadron_starfighter = Filters.and(CardSubtype.STARFIGHTER, Keyword.RED_SQUADRON);
    public static final Filter Reegesk = Filters.title(Title.Reegesk);
    public static final Filter refinery = Filters.keyword(Keyword.REFINERY);
    public static final Filter Reflection = Filters.title(Title.Reflection);
    public static final Filter Rendezvous_Point = Filters.title(Title.Rendezvous_Point);
    public static final Filter Rendili_system = Filters.title(Title.Rendili);
    public static final Filter Renegade_system = Filters.and(CardSubtype.SYSTEM, Filters.renegadePlanetLocation());
    public static final Filter Rennek = Filters.title(Title.Rennek);
    public static final Filter Report_To_Lord_Vader = Filters.title(Title.Report_To_Lord_Vader);
    public static final Filter Republic = Filters.icon(Icon.REPUBLIC);
    public static final Filter Republic_character = Filters.and(Filters.icon(Icon.REPUBLIC), CardCategory.CHARACTER);
    public static final Filter Republic_starship = Filters.and(Filters.icon(Icon.REPUBLIC), CardCategory.STARSHIP);
    public static final Filter Res_Luk_Raauf = Filters.title(Title.Res_Luk_Raauf);
    public static final Filter Rescue_The_Princess = Filters.title(Title.Rescue_The_Princess);
    public static final Filter Resistance = Filters.title(Title.Resistance);
    public static final Filter Resistance_Agent = Filters.keyword(Keyword.RESISTANCE_AGENT);
    public static final Filter Resistance_character = Filters.and(CardCategory.CHARACTER, Icon.RESISTANCE);
    public static final Filter Resistance_leader = Filters.and(Icon.RESISTANCE, Keyword.LEADER);
    public static final Filter Resistance_pilot = Filters.and(CardCategory.CHARACTER, Icon.RESISTANCE, Icon.PILOT);
    public static final Filter Responsibility_Of_Command = Filters.title(Title.Responsibility_Of_Command);
    public static final Filter Restore_Freedom_To_The_Galaxy = Filters.title(Title.Restore_Freedom_To_The_Galaxy);
    public static final Filter Restraining_Bolt = Filters.title(Title.Restraining_Bolt);
    public static final Filter Restricted_Deployment = Filters.title(Title.Restricted_Deployment);
    public static final Filter Retract_The_Bridge = Filters.title(Title.Retract_The_Bridge);
    public static final Filter Return_Of_A_Jedi = Filters.title(Title.Return_Of_A_Jedi);
    public static final Filter Revolution = Filters.title(Title.Revolution);
    public static final Filter Rex = Filters.title(Title.Rex);
    public static final Filter Rey = Filters.persona(Persona.REY);
    public static final Filter Reys_Encampment = Filters.title(Title.Reys_Encampment);
    public static final Filter Ric = Filters.persona(Persona.RIC);
    public static final Filter rifle = Filters.or(Keyword.RIFLE, Keyword.BLASTER_RIFLE);
    public static final Filter Rite_Of_Passage = Filters.title(Title.Rite_Of_Passage);
    public static final Filter Roche_system = Filters.title(Title.Roche);
    public static final Filter Rodian = Filters.species(Species.RODIAN);
    public static final Filter Rogue_1 = Filters.title(Title.Rogue_1);
    public static final Filter Rogue_2 = Filters.title(Title.Rogue_2);
    public static final Filter Rogue_3 = Filters.title(Title.Rogue_3);
    public static final Filter Rogue_4 = Filters.title(Title.Rogue_4);
    public static final Filter Rogue_Asteroid = Filters.title(Title.Rogue_Asteroid);
    public static final Filter Rogue_One = Filters.title(Title.Rogue_One);
    public static final Filter Rogue_Squadron_gunner = Filters.and(Keyword.GUNNER, Filters.or(Keyword.ROGUE_SQUADRON, Filters.aboard(Filters.keyword(Keyword.ROGUE_SQUADRON))));
    public static final Filter Rogue_Squadron_pilot = Filters.and(CardCategory.CHARACTER, Icon.PILOT, Filters.or(Keyword.ROGUE_SQUADRON, Filters.piloting(Filters.keyword(Keyword.ROGUE_SQUADRON))));
    public static final Filter Rogue_T47 = Filters.and(Keyword.ROGUE_SQUADRON, ModelType.T_47);
    public static final Filter Ronto = Filters.keyword(Keyword.RONTO);
    public static final Filter Royal_Guard = Filters.keyword(Keyword.ROYAL_GUARD);
    public static final Filter Royal_Naboo_Security = Filters.keyword(Keyword.ROYAL_NABOO_SECURITY);
    public static final Filter Royal_Naboo_Security_Officer = Filters.title(Title.Royal_Naboo_Security_Officer);
    public static final Filter Run_Luke_Run = Filters.title(Title.Run_Luke_Run);
    public static final Filter Rya = Filters.title(Title.Rya);
    public static final Filter Rycar_Ryjerd = Filters.title(Title.Rycar_Ryjerd);
    public static final Filter Rycars_Run = Filters.title(Title.Rycars_Run);
    public static final Filter Saber_1 = Filters.title(Title.Saber_1);
    public static final Filter Saber_2 = Filters.title(Title.Saber_2);
    public static final Filter Saber_3 = Filters.title(Title.Saber_3);
    public static final Filter Saber_Squadron = Filters.keyword(Keyword.SABER_SQUADRON);
    public static final Filter Saber_Squadron_pilot = Filters.and(CardCategory.CHARACTER, Icon.PILOT, Filters.or(Keyword.SABER_SQUADRON, Filters.piloting(Filters.keyword(Keyword.SABER_SQUADRON))));
    public static final Filter Sabine = Filters.title(Title.Sabine);
    public static final Filter Sabotage = Filters.title(Title.Sabotage);
    public static final Filter Saitorr_Kal_Fas = Filters.title(Title.Saitorr_Kal_Fas);
    public static final Filter Salm = Filters.title(Title.Salm);
    public static final Filter sandcrawler = Filters.keyword(Keyword.SANDCRAWLER);
    public static final Filter sandcrawler_site = Filters.keyword(Keyword.SANDCRAWLER_SITE);
    public static final Filter Sando_Aqua_Monster = Filters.title(Title.Sando_Aqua_Monster);
    public static final Filter sandtrooper = Filters.keyword(Keyword.SANDTROOPER);
    public static final Filter Sandwhirl = Filters.title(Title.Sandwhirl);
    public static final Filter Sanity_And_Compassion = Filters.title(Title.Sanity_And_Compassion);
    public static final Filter Sarlacc = Filters.title(Title.Sarlacc);
    public static final Filter Saurin = Filters.species(Species.SAURIN);
    public static final Filter Save_You_It_Can = Filters.title(Title.Save_You_It_Can);
    public static final Filter Saw = Filters.title(Title.Saw);
    public static final Filter Scanning_Crew = Filters.title(Title.Scanning_Crew);
    public static final Filter Scarif_battleground_site = Filters.and(Filters.partOfSystem(Title.Scarif), CardSubtype.SITE, Filters.battleground());
    public static final Filter Scarif_location = Filters.partOfSystem(Title.Scarif);
    public static final Filter Scarif_site = Filters.and(Filters.partOfSystem(Title.Scarif), CardSubtype.SITE);
    public static final Filter Scarif_system = Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Scarif));
    public static final Filter Scarif_Docking_Bay = Filters.title(Title.Scarif_Landing_Pad_Nine);
    public static final Filter Scarif_Turbolift_Complex = Filters.title(Title.Scarif_Turbolift_Complex);
    public static final Filter scavenger = Filters.keyword(Keyword.SCAVENGER);
    public static final Filter Scimitar_1 = Filters.title(Title.Scimitar_1);
    public static final Filter Scimitar_2 = Filters.persona(Persona.SCIMITAR_2);
    public static final Filter Scomp_Link_Access = Filters.title(Title.Scomp_Link_Access);
    public static final Filter scout = Filters.or(Keyword.SCOUT, Keyword.BIKER_SCOUT);
    public static final Filter scout_blaster = Filters.title(Title.Scout_Blaster);
    public static final Filter Scramble = Filters.title(Title.Scramble);
    public static final Filter Scrambled_Transmission = Filters.title(Title.Scrambled_Transmission);
    public static final Filter Scruffy_Looking_Nerf_Herder = Filters.title(Title.Scruffy_Looking_Nerf_Herder);
    public static final Filter Scum_And_Villainy = Filters.title(Title.Scum_And_Villainy);
    public static final Filter Scythe_3 = Filters.title(Title.Scythe_3);
    public static final Filter Scythe_Squadron = Filters.keyword(Keyword.SCYTHE_SQUADRON);
    public static final Filter Sebulba = Filters.title(Title.Sebulba);
    public static final Filter Sebulbas_Podracer = Filters.title(Title.Sebulbas_Podracer);
    public static final Filter Second_Marker = Filters.keyword(Keyword.MARKER_2);
    public static final Filter Secret_Plans = Filters.title(Title.Secret_Plans);
    public static final Filter sector = Filters.subtype(CardSubtype.SECTOR);
    public static final Filter security_droid = Filters.and(CardType.DROID, Filters.modelType(ModelType.SECURITY));
    public static final Filter Security_Precautions = Filters.title(Title.Security_Precautions);
    public static final Filter Security_Tower = Filters.title(Title.Security_Tower);
    public static final Filter seeker = Filters.keyword(Keyword.SEEKER);
    public static final Filter Sei_Taria = Filters.title(Title.Sei_Taria);
    public static final Filter senator = Filters.keyword(Keyword.SENATOR);
    public static final Filter Sense = Filters.title(Title.Sense);
    public static final Filter Set_For_Stun = Filters.title(Title.Set_For_Stun);
    public static final Filter Set_Your_Course_For_Alderaan = Filters.title(Title.Set_Your_Course_For_Alderaan);
    public static final Filter Seventh_Marker = Filters.keyword(Keyword.MARKER_7);
    public static final Filter Sewer = Filters.title(Title.Sewer);
    public static final Filter S_foils = Filters.title(Title.S_foils);
    public static final Filter SFS_Lx93_Laser_Cannons = Filters.title(Title.SFS_Lx93_Laser_Cannons);
    public static final Filter Shield_Gate = Filters.title(Title.Shield_Gate);
    public static final Filter Shmi = Filters.title(Title.Shmi);
    public static final Filter Shocking_Information = Filters.title(Title.Shocking_Information);
    public static final Filter Shocking_Revelation = Filters.title(Title.Shocking_Revelation);
    public static final Filter Shoo_Shoo = Filters.title(Title.Shoo_Shoo);
    public static final Filter Shot_In_The_Dark = Filters.title(Title.Shot_In_The_Dark);
    public static final Filter Shut_Him_Up_Or_Shut_Him_Down = Filters.title(Title.Shut_Him_Up_Or_Shut_Him_Down);
    public static final Filter shuttle = Filters.or(ModelType.LAMBDA_CLASS_SHUTTLE, ModelType.MODIFIED_VCX_SHUTTLE, ModelType.UPSILON_CLASS_SHUTTLE);
    public static final Filter shuttle_vehicle = Filters.and(CardType.VEHICLE, CardSubtype.SHUTTLE);
    public static final Filter Sidious = Filters.persona(Persona.SIDIOUS);
    public static final Filter Sidon = Filters.title(Title.Sidon);
    public static final Filter Sienar_Fleet_Systems = Filters.title(Title.Sienar_Fleet_Systems);
    public static final Filter Silence_Is_Golden = Filters.title(Title.Silence_Is_Golden);
    public static final Filter site = Filters.subtype(CardSubtype.SITE);
    public static final Filter Sith = Filters.type(CardType.SITH);
    public static final Filter Sith_Probe_Droid = Filters.title(Title.Sith_Probe_Droid);
    public static final Filter Sixth_Marker = Filters.keyword(Keyword.MARKER_6);
    public static final Filter skiff = Filters.keyword(Keyword.SKIFF);
    public static final Filter Skrilling = Filters.species(Species.SKRILLING);
    public static final Filter Skywalker = Filters.or(Persona.ANAKIN, Persona.LUKE, Persona.LEIA, Persona.MARA_SKYWALKER, Filters.title(Title.Shmi));
    public static final Filter Skywalkers = Filters.title(Title.Skywalkers);
    public static final Filter Slave_I = Filters.persona(Persona.SLAVE_I);
    public static final Filter Slave_Quarters = Filters.title(Title.Slave_Quarters);
    public static final Filter Smoke_Screen = Filters.title(Title.Smoke_Screen);
    public static final Filter smuggler = Filters.keyword(Keyword.SMUGGLER);
    public static final Filter Snap = Filters.persona(Persona.SNAP);
    public static final Filter Sniper = Filters.title(Title.Sniper);
    public static final Filter Snivvian = Filters.species(Species.SNIVVIAN);
    public static final Filter Snoke = Filters.persona(Persona.SNOKE);
    public static final Filter snowtrooper = Filters.keyword(Keyword.SNOWTROOPER);
    public static final Filter snub_fighter = Filters.and(CardSubtype.STARFIGHTER, Filters.or(ModelType.A_WING, ModelType.B_WING, ModelType.X_WING, ModelType.Y_WING, ModelType.Z_95_HEADHUNTER, ModelType.MODIFIED_Z_95_HEADHUNTER));
    public static final Filter Solo_Han = Filters.title(Title.Solo_Han);
    public static final Filter Sometimes_I_Amaze_Even_Myself = Filters.title(Title.Sometimes_I_Amaze_Even_Myself);
    public static final Filter Someone_Who_Loves_You = Filters.title(Title.Someone_Who_Loves_You);
    public static final Filter Sonic_Bombardment = Filters.title(Title.Sonic_Bombardment);
    public static final Filter Sorry_About_The_Mess = Filters.title(Title.Sorry_About_The_Mess);
    public static final Filter Sound_The_Attack = Filters.title(Title.Sound_The_Attack);
    public static final Filter space_creature = Filters.and(CardType.CREATURE, ModelType.SPACE);
    public static final Filter Space_Slug = Filters.title(Title.Space_Slug);
    public static final Filter Space_Slug_Belly = Filters.title(Title.Space_Slug_Belly);
    public static final Filter space_system = Filters.and(Icon.SPACE, CardSubtype.SYSTEM);
    public static final Filter spaceport_site = Filters.keyword(Keyword.SPACEPORT_SITE);
    public static final Filter Special_Delivery = Filters.title(Title.Special_Delivery);
    public static final Filter Special_Modifications = Filters.title(Title.Special_Modifications);
    public static final Filter speeder = Filters.or(ModelType.SPEEDER_BIKE, Keyword.LANDSPEEDER, Keyword.SANDSPEEDER, Keyword.SNOWSPEEDER);
    public static final Filter speeder_bike = Filters.modelType(ModelType.SPEEDER_BIKE);
    public static final Filter Spice_Mines_Of_Kessel = Filters.title(Title.Spice_Mines_Of_Kessel);
    public static final Filter spy = Filters.keyword(Keyword.SPY);
    public static final Filter squadron = Filters.subtype(CardSubtype.SQUADRON);
    public static final Filter Staging_Areas = Filters.title(Title.Staging_Areas);
    public static final Filter Stalker = Filters.title(Title.Stalker);
    public static final Filter STAP = Filters.modelType(ModelType.STAP);
    public static final Filter Star_Cruiser = Filters.or(ModelType.MON_CALAMARI_STAR_CRUISER);
    public static final Filter Star_Destroyer = Filters.or(ModelType.IMPERIAL_CLASS_STAR_DESTROYER, ModelType.INTERDICTOR_CLASS_STAR_DESTROYER, ModelType.RESURGENT_CLASS_STAR_DESTROYER, ModelType.SUPER_CLASS_STAR_DESTROYER, ModelType.VICTORY_CLASS_STAR_DESTROYER);
    public static final Filter Stardust = Filters.title(Title.Stardust);
    public static final Filter starfighter = Filters.subtype(CardSubtype.STARFIGHTER);
    public static final Filter Starkiller_Base_location = Filters.partOfSystem(Title.Starkiller_Base);
    public static final Filter starship = Filters.type(CardType.STARSHIP);
    public static final Filter starship_cannon = Filters.and(CardType.WEAPON, CardSubtype.STARSHIP, Filters.or(Keyword.CANNON, Keyword.ION_CANNON, Keyword.LASER_CANNON));
    public static final Filter starship_site = Filters.and(CardSubtype.SITE, Icon.STARSHIP_SITE);
    public static final Filter starship_weapon = Filters.and(CardType.WEAPON, CardSubtype.STARSHIP);
    public static final Filter starship_weapon_that_deploys_on_capitals = Filters.keyword(Keyword.STARSHIP_WEAPON_THAT_DEPLOYS_ON_CAPITALS);
    public static final Filter starship_weapon_that_deploys_on_starfighters = Filters.keyword(Keyword.STARSHIP_WEAPON_THAT_DEPLOYS_ON_STARFIGHTERS);
    public static final Filter Starting_Effect = Filters.and(CardType.EFFECT, CardSubtype.STARTING);
    public static final Filter Steady_Steady = Filters.title(Title.Steady_Steady);
    public static final Filter Stolen_Data_Tapes = Filters.title(Title.Stolen_Data_Tapes);
    public static final Filter Stone_Pile = Filters.title(Title.Stone_Pile);
    public static final Filter Stop_Motion = Filters.title(Title.Stop_Motion);
    public static final Filter stormtrooper = Filters.or(Keyword.BIKER_SCOUT, Keyword.DEATH_TROOPER, Keyword.SANDTROOPER, Keyword.SNOWTROOPER, Keyword.STORMTROOPER);
    public static final Filter Stormtrooper_Utility_Belt = Filters.title(Title.Stormtrooper_Utility_Belt);
    public static final Filter Strangle = Filters.title(Title.Strangle);
    public static final Filter Strategic_Reserves = Filters.title(Title.Strategic_Reserves);
    public static final Filter Stunning_Leader = Filters.title(Title.Stunning_Leader);
    public static final Filter Subjugated_system = Filters.and(CardSubtype.SYSTEM, Filters.subjugatedPlanetLocation());
    public static final Filter Sullust_system = Filters.title(Title.Sullust);
    public static final Filter Sunsdown = Filters.title(Title.Sunsdown);
    public static final Filter Superlaser = Filters.title(Title.Superlaser);
    public static final Filter Superlaser_Mark_II = Filters.title(Title.Superlaser_Mark_II);
    public static final Filter superlaser_weapon = Filters.or(Filters.title(Title.Superlaser), Filters.title(Title.Superlaser_Mark_II));
    public static final Filter Surface_Defense_Cannon = Filters.title(Title.Surface_Defense_Cannon);
    public static final Filter Surprise = Filters.title(Title.Surprise);
    public static final Filter Surprise_Assault = Filters.title(Title.Surprise_Assault);
    public static final Filter Surreptitious_Glance = Filters.title(Title.Surreptitious_Glance);
    public static final Filter SW4_Ion_Cannon = Filters.title(Title.SW4_Ion_Cannon);
    public static final Filter swamp = Filters.keyword(Keyword.SWAMP);
    public static final Filter swamp_creature = Filters.and(CardType.CREATURE, ModelType.SWAMP);
    public static final Filter Swing_And_A_Miss = Filters.title(Title.Swing_And_A_Miss);
    public static final Filter swoop = Filters.keyword(Keyword.SWOOP);
    public static final Filter Swoop_Mercenary = Filters.title(Title.Swoop_Mercenary);
    public static final Filter system = Filters.subtype(CardSubtype.SYSTEM);
    public static final Filter system_or_sector = Filters.or(CardSubtype.SYSTEM, CardSubtype.SECTOR);
    public static final Filter Systems_Will_Slip_Through_Your_Fingers = Filters.title(Title.Systems_Will_Slip_Through_Your_Fingers);
    public static final Filter T_16 = Filters.modelType(ModelType.T_16);
    public static final Filter T_47 = Filters.modelType(ModelType.T_47);
    public static final Filter Tactical_Support = Filters.title(Title.Tactical_Support);
    public static final Filter Tagge = Filters.title(Title.Tagge);
    public static final Filter Take_Evasive_Action = Filters.title(Title.Take_Evasive_Action);
    public static final Filter Take_Them_Away = Filters.title(Title.Take_Them_Away);
    public static final Filter Take_Your_Fathers_Place = Filters.title(Title.Take_Your_Fathers_Place);
    public static final Filter Takeel = Filters.title(Title.Takeel);
    public static final Filter Tala_1 = Filters.title(Title.Tala_1);
    public static final Filter Tala_2 = Filters.title(Title.Tala_2);
    public static final Filter Tallon_Roll = Filters.title(Title.Tallon_Roll);
    public static final Filter Talz = Filters.species(Species.TALZ);
    public static final Filter Tanbris = Filters.title(Title.Tanbris);
    public static final Filter Tantive_IV = Filters.title(Title.Tantive_IV);
    public static final Filter Target_The_Main_Generator = Filters.title(Title.Target_The_Main_Generator);
    public static final Filter Targeting_Computer = Filters.title(Title.Targeting_Computer);
    public static final Filter Tarkin = Filters.persona(Persona.TARKIN);
    public static final Filter Tatooine_battleground_site = Filters.and(Filters.partOfSystem(Title.Tatooine), CardSubtype.SITE, Filters.battleground());
    public static final Filter Tatooine_Celebration = Filters.title(Title.Tatooine_Celebration);
    public static final Filter Tatooine_location = Filters.partOfSystem(Title.Tatooine);
    public static final Filter Tatooine_Occupation = Filters.title(Title.Tatooine_Occupation);
    public static final Filter Tatooine_site = Filters.and(Filters.partOfSystem(Title.Tatooine), CardSubtype.SITE);
    public static final Filter Tatooine_system = Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Tatooine));
    public static final Filter tauntaun = Filters.keyword(Keyword.TAUNTAUN);
    public static final Filter Toydarian = Filters.species(Species.TOYDARIAN);
    public static final Filter Tuanul_Village = Filters.title(Title.Tuanul_Village);
    public static final Filter tax_collector = Filters.keyword(Keyword.TAX_COLLECTOR);
    public static final Filter taxation_agenda = Filters.agenda(Agenda.TAXATION);
    public static final Filter Taym_Drengaren = Filters.title(Title.Taym_Drengaren);
    public static final Filter Telsij = Filters.title(Title.Telsij);
    public static final Filter Tempest_1 = Filters.title(Title.Tempest_1);
    public static final Filter Tempest_Scout_1 = Filters.title(Title.Tempest_Scout_1);
    public static final Filter Tempest_Scout_2 = Filters.title(Title.Tempest_Scout_2);
    public static final Filter Tempest_Scout_3 = Filters.title(Title.Tempest_Scout_3);
    public static final Filter Tempest_Scout_4 = Filters.title(Title.Tempest_Scout_4);
    public static final Filter Tempest_Scout_5 = Filters.title(Title.Tempest_Scout_5);
    public static final Filter Tempest_Scout_6 = Filters.title(Title.Tempest_Scout_6);
    public static final Filter Ten_Numb = Filters.title(Title.Ten_Numb);
    public static final Filter Tessek = Filters.title(Title.Tessek);
    public static final Filter Thank_The_Maker = Filters.title(Title.Thank_The_Maker);
    public static final Filter That_Things_Operational = Filters.title(Title.That_Things_Operational);
    public static final Filter Thats_One = Filters.title(Title.Thats_One);
    public static final Filter The_Camp = Filters.title(Title.The_Camp);
    public static final Filter The_Circle_Is_Now_Complete = Filters.title(Title.The_Circle_Is_Now_Complete);
    public static final Filter The_First_Transport_Is_Away = Filters.title(Title.The_First_Transport_Is_Away);
    public static final Filter The_Force_Is_Strong_With_This_One = Filters.title(Title.The_Force_Is_Strong_With_This_One);
    public static final Filter The_Hyperdrive_Generators_Gone = Filters.title(Title.The_Hyperdrive_Generators_Gone);
    public static final Filter The_Phantom_Menace = Filters.title(Title.The_Phantom_Menace);
    public static final Filter The_Planet_That_Its_Farthest_From = Filters.title(Title.The_Planet_That_Its_Farthest_From);
    public static final Filter The_Professor = Filters.title(Title.The_Professor);
    public static final Filter The_Shield_Doors_Must_Be_Closed = Filters.title(Title.The_Shield_Doors_Must_Be_Closed);
    public static final Filter The_Time_To_Fight_Is_Now = Filters.title(Title.The_Time_To_Fight_Is_Now);
    public static final Filter The_Ultimate_Power_In_The_Universe = Filters.title(Title.The_Ultimate_Power_In_The_Universe);
    public static final Filter Theed_Palace_Courtyard = Filters.title(Title.Theed_Palace_Courtyard);
    public static final Filter Theed_Palace_Generator = Filters.title(Title.Theed_Palace_Generator);
    public static final Filter Theed_Palace_Generator_Core = Filters.title(Title.Theed_Palace_Generator_Core);
    public static final Filter Theed_Palace_Hallway = Filters.title(Title.Theed_Palace_Hallway);
    public static final Filter Theed_Palace_site = Filters.keyword(Keyword.THEED_PALACE_SITE);
    public static final Filter Theed_Palace_Throne_Room = Filters.title(Title.Theed_Palace_Throne_Room);
    public static final Filter Their_Fire_Has_Gone_Out_Of_The_Universe = Filters.title(Title.Their_Fire_Has_Gone_Out_Of_The_Universe);
    public static final Filter There_Is_Another = Filters.title(Title.There_Is_Another);
    public static final Filter There_Is_Good_In_Him = Filters.title(Title.There_Is_Good_In_Him);
    public static final Filter There_Is_No_Try = Filters.title(Title.There_Is_No_Try);
    public static final Filter They_Have_No_Idea_Were_Coming = Filters.title(Title.They_Have_No_Idea_Were_Coming);
    public static final Filter Theyre_On_Dantooine = Filters.title(Title.Theyre_On_Dantooine);
    public static final Filter thief = Filters.keyword(Keyword.THIEF);
    public static final Filter Third_Marker = Filters.keyword(Keyword.MARKER_3);
    public static final Filter They_Will_Be_Lost_And_Confused = Filters.title(Title.They_Will_Be_Lost_And_Confused);
    public static final Filter This_Deal_Is_Getting_Worse_All_The_Time = Filters.title(Title.This_Deal_Is_Getting_Worse_All_The_Time);
    public static final Filter This_Is_All_Your_Fault = Filters.title(Title.This_Is_All_Your_Fault);
    public static final Filter This_Is_Just_Wrong = Filters.title(Title.This_Is_Just_Wrong);
    public static final Filter This_Is_More_Like_It = Filters.title(Title.This_Is_More_Like_It);
    public static final Filter This_Is_Some_Rescue = Filters.title(Title.This_Is_Some_Rescue);
    public static final Filter This_Is_Still_Wrong = Filters.title(Title.This_Is_Still_Wrong);
    public static final Filter This_Place_Can_Be_A_Little_Rough = Filters.title(Title.This_Place_Can_Be_A_Little_Rough);
    public static final Filter Thrawn = Filters.persona(Persona.THRAWN);
    public static final Filter Throne_Room = Filters.title(Title.Throne_Room);
    public static final Filter Throw_Me_Another_Charge = Filters.title(Title.Throw_Me_Another_Charge);
    public static final Filter Tibanna_Gas_Miner = Filters.title(Title.Tibanna_Gas_Miner);
    public static final Filter Tibrin_site = Filters.and(Filters.partOfSystem(Title.Tibrin), CardSubtype.SITE);
    public static final Filter TIE = Filters.tie();
    public static final Filter TIE_Advanced_x1 = Filters.modelType(ModelType.TIE_ADVANCED_X1);
    public static final Filter TIE_Assault_Squadron = Filters.title(Title.TIE_Assault_Squadron);
    public static final Filter TIE_Avenger = Filters.modelType(ModelType.TIE_AD);
    public static final Filter TIE_Bomber = Filters.modelType(ModelType.TIE_SA);
    public static final Filter TIE_Defender = Filters.modelType(ModelType.TIE_DEFENDER);
    public static final Filter TIE_Interceptor = Filters.modelType(ModelType.TIE_INTERCEPTOR);
    public static final Filter TIE_ln = Filters.modelType(ModelType.TIE_LN);
    public static final Filter TIE_rc = Filters.modelType(ModelType.TIE_RC);
    public static final Filter TIE_sa = Filters.modelType(ModelType.TIE_SA);
    public static final Filter TIE_sr = Filters.modelType(ModelType.TIE_SR);
    public static final Filter TIE_vn = Filters.modelType(ModelType.TIE_VN);
    public static final Filter TK422 = Filters.title(Title.TK422);
    public static final Filter Tonnika_Sisters = Filters.title(Title.Tonnika_Sisters);
    public static final Filter Too_Cold_For_Speeders = Filters.title(Title.Too_Cold_For_Speeders);
    public static final Filter torpedo = Filters.or(Keyword.PROTON_TORPEDOES);
    public static final Filter Torture = Filters.title(Title.Torture);
    public static final Filter Toche_Station = Filters.title(Title.Toche_Station);
    public static final Filter tractor_beam = Filters.keyword(Keyword.TRACTOR_BEAM);
    public static final Filter trade_agenda = Filters.agenda(Agenda.TRADE);
    public static final Filter Trade_Federation_starfighter = Filters.and(Icon.TRADE_FEDERATION, CardSubtype.STARFIGHTER);
    public static final Filter Trample = Filters.title(Title.Trample);
    public static final Filter Trandoshan = Filters.species(Species.TRANDOSHAN);
    public static final Filter transport = Filters.and(CardType.STARSHIP, Filters.or(ModelType.BYBLOS_G1A_TRANSPORT, ModelType.MODIFIED_TRANSPORT, ModelType.TRANSPORT, Keyword.TRANSPORT_SHIP, ModelType.ZETA_CLASS_TRANSPORT));
    public static final Filter transport_vehicle = Filters.and(CardType.VEHICLE, CardSubtype.TRANSPORT);
    public static final Filter Transmission_Terminated = Filters.title(Title.Transmission_Terminated);
    public static final Filter Trap_Door = Filters.title(Title.Trap_Door);
    public static final Filter Trash_Compactor = Filters.title(Title.Trash_Compactor);
    public static final Filter trooper = Filters.or(Keyword.TROOPER, Keyword.DEATH_TROOPER, Keyword.STORMTROOPER, Keyword.SNOWTROOPER, Keyword.SANDTROOPER, Keyword.ECHO_BASE_TROOPER, Keyword.CLOUD_CITY_TROOPER, Keyword.DEATH_STAR_TROOPER, Keyword.IMPERIAL_TROOPER_GUARD, Keyword.BIKER_SCOUT, Keyword.CLONE_TROOPER);
    public static final Filter Trooper_Assault = Filters.title(Title.Trooper_Assault);
    public static final Filter Trooper_Charge = Filters.title(Title.Trooper_Charge);
    public static final Filter turbolaser_battery = Filters.keyword(Keyword.TURBOLASER_BATTERY);
    public static final Filter Turbolaser_Battery = Filters.title(Title.Turbolaser_Battery);
    public static final Filter Tusken_Breath_Mask = Filters.title(Title.Tusken_Breath_Mask);
    public static final Filter Tusken_Canyon = Filters.title(Title.Tusken_Canyon);
    public static final Filter Tusken_Raider = Filters.species(Species.TUSKEN_RAIDER);
    public static final Filter Tusken_Scavengers = Filters.title(Title.Tusken_Scavengers);
    public static final Filter Twin_Suns_Of_Tatooine = Filters.title(Title.Twin_Suns_Of_Tatooine);
    public static final Filter Tycho = Filters.persona(Persona.TYCHO);
    public static final Filter Tydirium = Filters.title(Title.Tydirium);
    public static final Filter Tyrant = Filters.title(Title.Tyrant);
    public static final Filter Tzizvvt = Filters.title(Title.Tzizvvt);
    public static final Filter Ugnaught = Filters.species(Species.UGNAUGHT);
    public static final Filter Ultimatum = Filters.title(Title.Ultimatum);
    public static final Filter Uncertain_Is_The_Future = Filters.title(Title.Uncertain_Is_The_Future);
    public static final Filter Uncontrollable_Fury = Filters.title(Title.Uncontrollable_Fury);
    public static final Filter Under_Attack = Filters.title(Title.Under_Attack);
    public static final Filter under_nighttime_conditions = Filters.or(Filters.and(CardType.LOCATION, Keyword.NIGHTTIME_CONDITIONS), Filters.at(Filters.keyword(Keyword.NIGHTTIME_CONDITIONS)));
    public static final Filter Undercover = Filters.title(Title.Undercover);
    public static final Filter underground_site = Filters.and(Icon.UNDERGROUND, CardSubtype.SITE);
    public static final Filter underwater_site = Filters.and(Icon.UNDERWATER, CardSubtype.SITE);
    public static final Filter Underworld_Contacts = Filters.title(Title.Underworld_Contacts);
    public static final Filter Unkar_Plutt = Filters.title(Title.Unkar_Plutt);
    public static final Filter Until_We_Win_Or_The_Chances_Are_Spent = Filters.title(Title.Until_We_Win_Or_The_Chances_Are_Spent);
    public static final Filter Uplink_Station = Filters.title(Title.Uplink_Station);
    public static final Filter Upper_Walkway = Filters.title(Title.Upper_Walkway);
    public static final Filter URoRRuRRR = Filters.title(Title.URoRRuRRR);
    public static final Filter Utinni = Filters.title(Title.Utinni);
    public static final Filter Utinni_Effect = Filters.and(CardType.EFFECT, CardSubtype.UTINNI);
    public static final Filter Utinni_Effect_that_retrieves_Force = Filters.and(CardType.EFFECT, CardSubtype.UTINNI, Keyword.UTINNI_EFFECT_THAT_RETRIEVES_FORCE);
    public static final Filter Vader = Filters.persona(Persona.VADER);
    public static final Filter Vaders_Custom_TIE = Filters.persona(Persona.VADERS_CUSTOM_TIE);
    public static final Filter Vaders_Lightsaber = Filters.persona(Persona.VADERS_LIGHTSABER);
    public static final Filter Vaders_Obsession = Filters.title(Title.Vaders_Obsession);
    public static final Filter Valorum = Filters.title(Title.Valorum);
    public static final Filter Vaporator = Filters.title(Title.Vaporator);
    public static final Filter Veers = Filters.persona(Persona.VEERS);
    public static final Filter vehicle = Filters.type(CardType.VEHICLE);
    public static final Filter vehicle_site = Filters.icon(Icon.VEHICLE_SITE);
    public static final Filter vehicle_weapon = Filters.and(CardType.WEAPON, CardSubtype.VEHICLE);
    public static final Filter Vengeance_Of_The_Dark_Prince = Filters.title(Title.Vengeance_Of_The_Dark_Prince);
    public static final Filter Vibro_Ax = Filters.keyword(Keyword.VIBRO_AX);
    public static final Filter Victory_class_Star_Destroyer = Filters.modelType(ModelType.VICTORY_CLASS_STAR_DESTROYER);
    public static final Filter Vigo = Filters.title(Title.Vigo);
    public static final Filter Visage_Of_The_Emperor = Filters.title(Title.Visage_Of_The_Emperor);
    public static final Filter Visored_Vision = Filters.title(Title.Visored_Vision);
    public static final Filter Vul_Tazaene = Filters.title(Title.Vul_Tazaene);
    public static final Filter Wakeelmui_system = Filters.title(Title.Wakeelmui);
    public static final Filter Walker_Barrage = Filters.title(Title.Walker_Barrage);
    public static final Filter Walker_Garrison = Filters.title(Title.Walker_Garrison);
    public static final Filter wampa = Filters.keyword(Keyword.WAMPA);
    public static final Filter Wampa_Cave = Filters.title(Title.Wampa_Cave);
    public static final Filter war_room = Filters.keyword(Keyword.WAR_ROOM);
    public static final Filter warrior = Filters.icon(Icon.WARRIOR);
    public static final Filter Warriors_Courage = Filters.title(Title.Warriors_Courage);
    public static final Filter Watch_Your_Back = Filters.title(Title.Watch_Your_Back);
    public static final Filter Watch_Your_Step = Filters.title(Title.Watch_Your_Step);
    public static final Filter Watto = Filters.title(Title.Watto);
    public static final Filter Wattos_Box = Filters.title(Title.Wattos_Box);
    public static final Filter Wattos_Chance_Cube = Filters.title(Title.Wattos_Chance_Cube);
    public static final Filter Wattos_Junkyard = Filters.title(Title.Wattos_Junkyard);
    public static final Filter Watts = Filters.title(Title.Watts);
    public static final Filter We_Dont_Need_Their_Scum = Filters.title(Title.We_Dont_Need_Their_Scum);
    public static final Filter We_Have_A_Plan = Filters.title(Title.We_Have_A_Plan);
    public static final Filter We_Have_A_Prisoner = Filters.title(Title.We_Have_A_Prisoner);
    public static final Filter We_Need_Your_Help = Filters.title(Title.We_Need_Your_Help);
    public static final Filter wealth_agenda = Filters.agenda(Agenda.WEALTH);
    public static final Filter weapon = Filters.type(CardType.WEAPON);
    public static final Filter weapon_or_character_with_permanent_weapon = Filters.or(CardType.WEAPON, Filters.hasPermanentWeapon());
    public static final Filter Weather_Vane = Filters.title(Title.Weather_Vane);
    public static final Filter Wedge = Filters.persona(Persona.WEDGE);
    public static final Filter Weequay = Filters.species(Species.WEEQUAY);
    public static final Filter Weequay_Guard = Filters.title(Title.Weequay_Guard);
    public static final Filter Well_Guarded = Filters.title(Title.Well_Guarded);
    public static final Filter Well_Handle_This = Filters.title(Title.Well_Handle_This);
    public static final Filter Well_Need_A_New_One = Filters.title(Title.Well_Need_A_New_One);
    public static final Filter Well_Trained_In_The_Jedi_Arts = Filters.title(Title.Well_Trained_In_The_Jedi_Arts);
    public static final Filter Were_All_Gonna_Be_A_Lot_Thinner = Filters.title(Title.Were_All_Gonna_Be_A_Lot_Thinner);
    public static final Filter Were_Doomed = Filters.title(Title.Were_Doomed);
    public static final Filter Were_In_Attack_Position_Now = Filters.title(Title.Were_In_Attack_Position_Now);
    public static final Filter Were_The_Bait = Filters.title(Title.Were_The_Bait);
    public static final Filter What_Is_Thy_Bidding_My_Master = Filters.title(Title.What_Is_Thy_Bidding_My_Master);
    public static final Filter Whiphid = Filters.species(Species.WHIPHID);
    public static final Filter Wild_Karrde = Filters.title(Title.Wild_Karrde);
    public static final Filter Williams = Filters.title(Title.Williams);
    public static final Filter Wioslea = Filters.title(Title.Wioslea);
    public static final Filter Wittin = Filters.title(Title.Wittin);
    public static final Filter womp_rat = Filters.title(Title.Womp_Rat);
    public static final Filter Wookiee = Filters.species(Species.WOOKIEE);
    public static final Filter Wookiee_Roar = Filters.title(Title.Wookiee_Roar);
    public static final Filter Wookiee_Strangle = Filters.title(Title.Wookiee_Strangle);
    public static final Filter Wounded_Wookiee = Filters.title(Title.Wounded_Wookiee);
    public static final Filter Wrong_Turn = Filters.title(Title.Wrong_Turn);
    public static final Filter Wuta = Filters.title(Title.Wuta);
    public static final Filter Why_Didnt_You_Tell_Me = Filters.title(Title.Why_Didnt_You_Tell_Me);
    public static final Filter Xizor = Filters.title(Title.Xizor);
    public static final Filter Xizors_Palace = Filters.title(Title.Xizors_Palace);
    public static final Filter Xizors_Palace_site = Filters.keyword(Keyword.XIZORS_PALACE_SITE);
    public static final Filter X_wing = Filters.modelType(ModelType.X_WING);
    public static final Filter X_wing_Laser_Cannon = Filters.title(Title.X_wing_Laser_Cannon);
    public static final Filter Y_wing = Filters.modelType(ModelType.Y_WING);
    public static final Filter Yaggle_Gakkle = Filters.title(Title.Yaggle_Gakkle);
    public static final Filter Yarkora = Filters.species(Species.YARKORA);
    public static final Filter Yarna_dal_Gargan = Filters.title(Title.Yarna_dal_Gargan);
    public static final Filter Yavin_4_Docking_Bay = Filters.title(Title.Yavin_4_Docking_Bay);
    public static final Filter Yavin_4_location = Filters.and(CardCategory.LOCATION, Filters.partOfSystem(Title.Yavin_4));
    public static final Filter Yavin_4_sector = Filters.and(Filters.partOfSystem(Title.Yavin_4), CardSubtype.SECTOR);
    public static final Filter Yavin_4_Operations = Filters.title(Title.Yavin_4_Operations);
    public static final Filter Yavin_4_site = Filters.and(Filters.partOfSystem(Title.Yavin_4), CardSubtype.SITE);
    public static final Filter Yavin_4_system = Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Yavin_4));
    public static final Filter Yavin_4_War_Room = Filters.title(Title.Yavin_4_War_Room);
    public static final Filter Yavin_Sentry = Filters.title(Title.Yavin_Sentry);
    public static final Filter Yoda = Filters.persona(Persona.YODA);
    public static final Filter Yoda_Stew = Filters.title(Title.Yoda_Stew);
    public static final Filter Yodas_Hope = Filters.title(Title.Yodas_Hope);
    public static final Filter Yodas_Hut = Filters.title(Title.Yodas_Hut);
    public static final Filter Yorr = Filters.title(Title.Yorr);
    public static final Filter You_Are_Beaten = Filters.title(Title.You_Are_Beaten);
    public static final Filter You_Can_Either_Profit_By_This = Filters.title(Title.You_Can_Either_Profit_By_This);
    public static final Filter You_May_Start_Your_Landing = Filters.title(Title.You_May_Start_Your_Landing);
    public static final Filter You_Must_Confront_Vader = Filters.title(Title.You_Must_Confront_Vader);
    public static final Filter You_Rebel_Scum = Filters.title(Title.You_Rebel_Scum);
    public static final Filter You_Truly_Belong_Here_With_Us = Filters.title(Title.You_Truly_Belong_Here_With_Us);
    public static final Filter You_Overestimate_Their_Chances = Filters.title(Title.You_Overestimate_Their_Chances);
    public static final Filter Your_Destiny = Filters.title(Title.Your_Destiny);
    public static final Filter Youre_A_Slave = Filters.title(Title.Youre_A_Slave);
    public static final Filter Youre_All_Clear_Kid = Filters.title(Title.Youre_All_Clear_Kid);
    public static final Filter YT_1300_Transport = Filters.title(Title.YT_1300_Transport);
    public static final Filter Yularen = Filters.title(Title.Yularen);
    public static final Filter Yuzzum = Filters.species(Species.YUZZUM);
    public static final Filter Z_95 = Filters.or(ModelType.MODIFIED_Z_95_HEADHUNTER, ModelType.Z_95_HEADHUNTER);
    public static final Filter Zeb = Filters.title(Title.Zeb);
    public static final Filter Zev = Filters.title(Title.Zev);
    public static final Filter Zuckuss = Filters.persona(Persona.ZUCKUSS);
    public static final Filter Zutton = Filters.title(Title.Zutton);



    private static class SpotFilterCardInPlayVisitor implements PhysicalCardVisitor {
        private GameState _gameState;
        private ModifiersQuerying _modifiersQuerying;
        private boolean _useAcceptsCount;
        private Filter[] _filter;
        private PhysicalCard _card;

        private SpotFilterCardInPlayVisitor(GameState gameState, ModifiersQuerying modifiersQuerying, boolean useAcceptsCount, Filter[] filter) {
            _gameState = gameState;
            _modifiersQuerying = modifiersQuerying;
            _useAcceptsCount = useAcceptsCount;
            _filter = filter;
        }

        @Override
        public boolean visitPhysicalCard(PhysicalCard physicalCard) {
            for (Filter filter : _filter) {
                if (_useAcceptsCount) {
                    int curCount = filter.acceptsCount(_gameState, _modifiersQuerying, physicalCard);
                    if (curCount > 0) {
                        _card = physicalCard;
                        return true;
                    }
                }
                else {
                    if (filter.accepts(_gameState, _modifiersQuerying, physicalCard)) {
                        _card = physicalCard;
                        return true;
                    }
                }
            }
            return false;
        }

        public PhysicalCard getCard() {
            return _card;
        }
    }

    private static class SpotCountFilterCardInPlayVisitor implements PhysicalCardVisitor {
        private GameState _gameState;
        private ModifiersQuerying _modifiersQuerying;
        private int _searchingToSpot;
        private boolean _useAcceptsCount;
        private Filter[] _filter;
        private int _spottedCount;

        private SpotCountFilterCardInPlayVisitor(GameState gameState, ModifiersQuerying modifiersQuerying, int count, boolean useAcceptsCount, Filter[] filter) {
            _gameState = gameState;
            _modifiersQuerying = modifiersQuerying;
            _searchingToSpot = count;
            _useAcceptsCount = useAcceptsCount;
            _filter = filter;
        }

        @Override
        public boolean visitPhysicalCard(PhysicalCard physicalCard) {
            for (Filter filter : _filter) {
                if (_useAcceptsCount) {
                    int curCount = filter.acceptsCount(_gameState, _modifiersQuerying, physicalCard);
                    if (curCount > 0) {
                        _spottedCount = Math.min(_searchingToSpot, _spottedCount + curCount);
                        if (_spottedCount >= _searchingToSpot) {
                            return true;
                        }
                        break;
                    }
                }
                else {
                    if (filter.accepts(_gameState, _modifiersQuerying, physicalCard)) {
                        _spottedCount++;
                        if (_spottedCount >= _searchingToSpot) {
                            return true;
                        }
                        break;
                    }
                }
            }
            return false;
        }

        public int getCounter() {
            return _spottedCount;
        }
    }

    private static class GetCardsMatchingFilterVisitor extends CompletePhysicalCardVisitor {
        private GameState _gameState;
        private ModifiersQuerying _modifiersQuerying;
        private boolean _useAcceptsCount;
        private Filter[] _filter;
        private int _matchingCount;
        private List<PhysicalCard> _physicalCards = new LinkedList<PhysicalCard>();

        private GetCardsMatchingFilterVisitor(GameState gameState, ModifiersQuerying modifiersQuerying, boolean useAcceptsCount, Filter[] filter) {
            _gameState = gameState;
            _modifiersQuerying = modifiersQuerying;
            _useAcceptsCount = useAcceptsCount;
            _filter = filter;
        }

        @Override
        protected void doVisitPhysicalCard(PhysicalCard physicalCard) {
            for (Filter filter : _filter) {
                if (_useAcceptsCount) {
                    int curCount = filter.acceptsCount(_gameState, _modifiersQuerying, physicalCard);
                    if (curCount > 0) {
                        _physicalCards.add(physicalCard);
                        _matchingCount += curCount;
                        return;
                    }
                }
                else {
                    if (filter.accepts(_gameState, _modifiersQuerying, physicalCard)) {
                        _physicalCards.add(physicalCard);
                        _matchingCount++;
                        return;
                    }
                }
            }
        }

        public int getCounter() {
            return _matchingCount;
        }

        public List<PhysicalCard> getPhysicalCards() {
            return _physicalCards;
        }
    }
}
