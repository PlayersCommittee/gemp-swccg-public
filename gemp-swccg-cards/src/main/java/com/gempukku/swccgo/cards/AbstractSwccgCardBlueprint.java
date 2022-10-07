package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.cards.actions.PlayDejarikAction;
import com.gempukku.swccgo.cards.actions.ReturnCardToHandAction;
import com.gempukku.swccgo.cards.effects.UseDeviceEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.*;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.*;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.*;

import java.util.*;

/**
 * The abstract class for the base implementation of an SWCCG card blueprint.
 */
public abstract class AbstractSwccgCardBlueprint implements SwccgCardBlueprint {
    private Side _side;
    private CardCategory _cardCategory;
    private Set<CardType> _cardTypes = new HashSet<CardType>();
    private CardSubtype _cardSubtype;
    private Uniqueness _uniqueness;
    private String _title;
    private List<String> _comboCardTitles = new ArrayList<String>();
    private Float _destiny;
    private Float _alternateDestiny;
    private int _alternateDestinyCost;
    private String _lore;
    private String _gameText;
    private String _testingText;
    private boolean _frontOfDoubleSidedCard;
    private Set<Keyword> _keywords = new HashSet<Keyword>();
    private Map<Icon, Integer> _icons = new HashMap<Icon, Integer>();
    private List<ModelType> _modelTypes = new ArrayList<ModelType>();
    private Set<Persona> _personas = new HashSet<Persona>();
    private Set<String> _immuneToCardTitles = new HashSet<String>();
    private Set<String> _immuneToOwnersCardTitles = new HashSet<String>();
    private boolean _immuneToOpponentsObjective;
    private boolean _deployUsingBothForcePiles;
    private boolean _hasCharacterPersonaOnlyWhileOnTable;
    private boolean _isComboCard;
    private boolean _isAlwaysStolen;
    private boolean _hasAlternateImageSuffix;
    private boolean _hasVirtualSuffix;
    private boolean _mayNotBePlacedInReserveDeck;
    private boolean _doesNotCountTowardDeckLimit;
    private boolean _isLegacy;
    private boolean _excludeFromDeckBuilder;
    private boolean _isHorizontal;
    private ExpansionSet _expansionSet;
    private Rarity _rarity;

    /**
     * Creates an SWCCG card blueprint.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param title the title
     * @param uniqueness the uniqueness
     * @param expansionSet the expansion set
     * @param rarity the rarity
     */
    protected AbstractSwccgCardBlueprint(Side side, Float destiny, String title, Uniqueness uniqueness, ExpansionSet expansionSet, Rarity rarity) {
        _side = side;
        _destiny = destiny;
        _alternateDestiny = destiny;
        _title = title;
        _uniqueness = uniqueness;
        _expansionSet = expansionSet;
        _rarity = rarity;
    }

    @Override
    public Side getSide() {
        return _side;
    }

    @Override
    public CardCategory getCardCategory() {
        return _cardCategory;
    }

    /**
     * Sets the card category.
     * @param cardCategory the card category
     */
    protected final void setCardCategory(CardCategory cardCategory) {
        _cardCategory = cardCategory;
    }

    @Override
    public Set<CardType> getCardTypes() {
        return _cardTypes;
    };

    @Override
    public boolean isCardType(CardType cardType) {
        return _cardTypes.contains(cardType);
    }

    /**
     * Resets the card type to the specified value.
     * @param cardType the card type to set
     */
    protected final void resetCardType(CardType cardType) {
        _cardTypes.clear();
        _cardTypes.add(cardType);
    }

    /**
     * Adds a card type.
     * @param cardType the card type to add
     */
    protected final void addCardType(CardType cardType) {
        _cardTypes.add(cardType);
    }

    @Override
    public CardSubtype getCardSubtype() {
        return _cardSubtype;
    }

    /**
     * Sets the card subtype
     * @param subtype the subtype
     */
    protected final void setCardSubtype(CardSubtype subtype) {
        _cardSubtype = subtype;
    }

    @Override
    public Uniqueness getUniqueness() {
        return _uniqueness;
    }

    @Override
    public String getTitle() {
        return _title;
    }

    @Override
    public List<String> getTitles() {
        if (!_comboCardTitles.isEmpty())
            return Collections.unmodifiableList(_comboCardTitles);
        else
            return Collections.singletonList(_title);
    }

    /**
     * Adds the specified combo card titles.
     * @param titles the titles
     */
    protected final void addComboCardTitles(String... titles) {
        _comboCardTitles.addAll(Arrays.asList(titles));
    }

    @Override
    public Float getDestiny() {
        return _destiny;
    }

    /**
     * Sets the specified 2nd destiny value. (Example: For BB-8)
     * @param destiny the 2nd destiny value
     */
    protected final void setAlternateDestiny(double destiny) {
        setAlternateDestiny((float) destiny);
    }

    protected final void setAlternateDestinyCost(int cost) {
        _alternateDestinyCost = cost;
    }

    public int getAlternateDestinyCost() {
        return _alternateDestinyCost;
    }

    /**
     * Sets the specified 2nd destiny value. (Example: For R2-D2)
     * @param destiny the 2nd destiny value
     */
    protected final void setAlternateDestiny(float destiny) {
        _alternateDestiny = destiny;
    }

    @Override
    public Float getAlternateDestiny() {
        if (_alternateDestiny != null)
            return _alternateDestiny;
        else
            return _destiny;
    }

    @Override
    public final String getLore() {
        return _lore;
    }

    /**
     * Sets the lore.
     * @param lore the lore
     */
    protected final void setLore(String lore) {
        _lore = lore;
    }

    @Override
    public final String getGameText() {
        return _gameText;
    }

    /**
     * Sets the game text.
     * @param text the game text
     */
    protected final void setGameText(String text) {
        _gameText = text;
    }
    
    /**
     * Sets the testing text to show.
     * @param text the testing text
     */
    protected final void setTestingText(String text) {
        _testingText = text;
    }

    /**
     * Gets the testing text.
     * @return the testing text
     */
    @Override
    public final String getTestingText() {
        return _testingText;
    }

    @Override
    public final boolean isFrontOfDoubleSidedCard() {
        return _frontOfDoubleSidedCard;
    }

    /**
     * Sets whether the card is front side of a double-sided card.
     * @param value true or false
     */
    protected final void setFrontOfDoubleSidedCard(boolean value) {
        _frontOfDoubleSidedCard = value;
    }

    /**
     * Gets the Dark side game text of a location card.
     * @return the Dark side game text
     */
    @Override
    public String getLocationDarkSideGameText() {
        throw new UnsupportedOperationException("This method, getLocationDarkSideGameText(), should not be called on this card: " + _title);
    }

    /**
     * Gets the Light side game text of a location card.
     * @return the Light side game text
     */
    @Override
    public String getLocationLightSideGameText() {
        throw new UnsupportedOperationException("This method, getLocationLightSideGameText(), should not be called on this card: " + _title);
    }

    @Override
    public boolean hasKeyword(Keyword keyword) {
        return _keywords.contains(keyword);
    }

    /**
     * Adds the specified keywords.
     * @param keywords the keywords
     */
    protected final void addKeywords(Keyword... keywords) {
        for (Keyword keyword : keywords)
            addKeyword(keyword);
    }

    /**
     * Adds the specified keyword.
     * @param keyword the keyword
     */
    protected final void addKeyword(Keyword keyword) {
        _keywords.add(keyword);
    }

    @Override
    public boolean hasIcon(Icon icon) {
        return _icons.containsKey(icon) && _icons.get(icon) > 0;
    }

    @Override
    public int getIconCount(Icon icon) {
        Integer count = _icons.get(icon);
        if (count == null)
            return 0;
        else
            return count;
    }

    /**
     * Adds the specified icons.
     * @param icons the icons
     */
    protected final void addIcons(Icon... icons) {
        for (Icon icon : icons)
            addIcon(icon);
    }

    /**
     * Adds the specified icon.
     * @param icon the icon
     */
    protected final void addIcon(Icon icon) {
        _icons.put(icon, 1);
    }

    /**
     * Adds the specified number of the specified icon.
     * @param icon the icon
     * @param number the number to add
     */
    protected final void addIcon(Icon icon, int number) {
        _icons.put(icon, number);
    }

    /**
     * Gets the model types of the card.
     * @return the model types
     */
    @Override
    public List<ModelType> getModelTypes() {
        return _modelTypes;
    }

    /**
     * Adds the specified model types.
     * @param types the model types
     */
    protected final void addModelTypes(ModelType... types) {
        for (ModelType type : types)
            addModelType(type);
    }

    /**
     * Adds the specified model type.
     * @param type the model type
     */
    public final void addModelType(ModelType type) {
        _modelTypes.add(type);
    }

    @Override
    public final boolean hasCharacterPersonaOnlyWhileOnTable() {
        return _hasCharacterPersonaOnlyWhileOnTable;
    }

    /**
     * Sets whether the card has a persona only while on table.
     * @param value true or false
     */
    protected final void setCharacterPersonaOnlyWhileOnTable(boolean value) {
        _hasCharacterPersonaOnlyWhileOnTable = value;
    }

    @Override
    public boolean hasPersona(Persona persona) {
        return _personas.contains(persona);
    }

    @Override
    public Set<Persona> getPersonas() {
        return Collections.unmodifiableSet(_personas);
    }

    /**
     * Adds the specified personas.
     * @param personas the personas
     */
    protected final void addPersonas(Persona... personas) {
        for (Persona persona : personas)
            addPersona(persona);
    }

    /**
     * Adds the specified persona.
     * @param persona the personas
     */
    protected final void addPersona(Persona persona) {
        _personas.add(persona);
    }

    @Override
    public boolean isImmuneToCardTitle(String title) {
        return _immuneToCardTitles.contains(title);
    }

    /**
     * Adds a card title that this card is always immune to.
     * @param title the card title
     */
    protected final void addImmuneToCardTitle(String title) {
        _immuneToCardTitles.add(title);
    }

    @Override
    public boolean isImmuneToOwnersCardTitle(String title) {
        return isImmuneToCardTitle(title) || _immuneToOwnersCardTitles.contains(title);
    }

    /**
     * Adds a card title that this card is always immune to when owned by same owner.
     * @param title the card title
     */
    protected final void addImmuneToOwnersCardTitle(String title) {
        _immuneToOwnersCardTitles.add(title);
    }

    @Override
    public boolean isImmuneToOpponentsObjective() {
        return _immuneToOpponentsObjective;
    }

    /**
     * Adds a filter that accepts cards this card is always immune to.
     * @param immune true if immune to opponents objective, otherwise false
     */
    protected final void setImmuneToOpponentsObjective(boolean immune) {
        _immuneToOpponentsObjective = immune;
    }

    @Override
    public boolean isDeployUsingBothForcePiles() {
        return _deployUsingBothForcePiles;
    }

    /**
     * Sets if card's deploy cost requires Force from both Force piles to be used.
     * @param usingBoth true if both Force piles, otherwise false
     */
    public void setDeployUsingBothForcePiles(boolean usingBoth) {
        _deployUsingBothForcePiles = usingBoth;
    }

    @Override
    public boolean isComboCard() {
        return _isComboCard || getTitles().size() > 1;
    }

    /**
     * Sets the card as a combo card.
     * @param comboCard true if a combo card, otherwise false
     */
    public void setComboCard(boolean comboCard) {
        _isComboCard = comboCard;
    }

    @Override
    public boolean isAlwaysStolen() {
        return _isAlwaysStolen;
    }

    /**
     * Sets the card as always considered to be stolen.
     * @param stolen true if always considered to be stolen, otherwise false
     */
    public void setAlwaysStolen(boolean stolen) {
        _isAlwaysStolen = stolen;
    }


    @Override
    public boolean hasAlternateImageSuffix() {
        return _hasAlternateImageSuffix;
    }

    /**
     * Sets the card as an alternate image card having an "(AI)" suffix in the title.
     * @param value true if title has an "(AI)" suffix, otherwise false
     */
    public void setAlternateImageSuffix(boolean value) {
        _hasAlternateImageSuffix = value;
    }

    @Override
    public boolean hasVirtualSuffix() {
        return _hasVirtualSuffix;
    }

    /**
     * Sets the card as a virtual card having a "(V)" suffix in the title.
     * @param value true if title has a "(V)" suffix, otherwise false
     */
    public void setVirtualSuffix(boolean value) {
        _hasVirtualSuffix = value;
    }

    @Override
    public boolean isDoesNotCountTowardDeckLimit() {
        return _doesNotCountTowardDeckLimit;
    }

    /**
     * Sets if this card is does not count toward deck limit.
     * @param value true if card is does not count toward deck limit, otherwise false
     */
    public void setDoesNotCountTowardDeckLimit(boolean value) {
        _doesNotCountTowardDeckLimit = value;
    }

    @Override
    public boolean isMayNotBePlacedInReserveDeck() {
        return _mayNotBePlacedInReserveDeck;
    }

    /**
     * Sets if the card may not be placed in Reserve Deck.
     * @param value true if card may not be placed in Reserve Deck, otherwise false
     */
    public void setMayNotBePlacedInReserveDeck(boolean value) {
        _mayNotBePlacedInReserveDeck = value;
    }

    @Override
    public Integer getReplacementCountForSquadron() {
        throw new UnsupportedOperationException("This method, getReplacementCountForSquadron(), should not be called on this card: " + _title);
    }

    @Override
    public Filter getReplacementFilterForSquadron() {
        throw new UnsupportedOperationException("This method, getReplacementFilterForSquadron(), should not be called on this card: " + _title);
    }

    /**
     * Determines if the card type cannot be canceled.
     * @return true if card type may not be canceled, otherwise false
     */
    @Override
    public boolean isCardTypeMayNotBeCanceled() {
        return _cardCategory == CardCategory.CHARACTER || _cardCategory == CardCategory.CREATURE
                || _cardCategory == CardCategory.DEFENSIVE_SHIELD || _cardCategory == CardCategory.STARSHIP
                || _cardCategory == CardCategory.LOCATION || _cardCategory == CardCategory.OBJECTIVE
                || _cardCategory == CardCategory.PODRACER || _cardCategory == CardCategory.VEHICLE
                || _cardCategory == CardCategory.WEAPON;
    }

    /**
     * Determines if this has a species attribute.
     * @return true if has attribute, otherwise false
     */
    @Override
    public boolean hasSpeciesAttribute() {
        return false;
    }

    /**
     * Gets the species, or null.
     * @return the species, or null if no species
     */
    @Override
    public Species getSpecies() {
        throw new UnsupportedOperationException("This method, getSpecies(), should not be called on this card: " + _title);
    }

    /**
     * Determines if this has a power attribute.
     * @return true if has attribute, otherwise false
     */
    @Override
    public boolean hasPowerAttribute() {
        return false;
    }

    /**
     * Gets the power.
     * @return the power
     */
    @Override
    public Float getPower() {
        throw new UnsupportedOperationException("This method, getPower(), should not be called on this card: " + _title);
    }

    /**
     * Determines if this has an ability attribute.
     * @return true if has attribute, otherwise false
     */
    @Override
    public boolean hasAbilityAttribute() {
        return false;
    }

    /**
     * Gets the ability.
     * @return the ability
     */
    @Override
    public Float getAbility() {
        throw new UnsupportedOperationException("This method, getAbility(), should not be called on this card: " + _title);
    }

    /**
     * Determines if this has a politics attribute.
     * @return true if has attribute, otherwise false
     */
    @Override
    public boolean hasPoliticsAttribute() {
        return false;
    }

    /**
     * Gets the politics value.
     * @return the politics value
     */
    @Override
    public float getPolitics() {
        throw new UnsupportedOperationException("This method, getPolitics(), should not be called on this card: " + _title);
    }

    /**
     * Determines if this has a landspeed attribute.
     * @return true if has attribute, otherwise false
     */
    @Override
    public boolean hasLandspeedAttribute() {
        return false;
    }

    /**
     * Gets the landspeed.
     * @return the landspeed
     */
    @Override
    public Float getLandspeed() {
        throw new UnsupportedOperationException("This method, getLandspeed(), should not be called on this card: " + _title);
    }

    /**
     * Determines if this has a forfeit attribute.
     * @return true if has attribute, otherwise false
     */
    @Override
    public boolean hasForfeitAttribute() {
        return false;
    }

    /**
     * Gets the forfeit value.
     * @return the forfeit value
     */
    @Override
    public Float getForfeit() {
        throw new UnsupportedOperationException("This method, getForfeit(), should not be called on this card: " + _title);
    }

    /**
     * Determines if this has an armor attribute.
     * @return true if has attribute, otherwise false
     */
    @Override
    public boolean hasArmorAttribute() {
        return false;
    }

    /**
     * Gets the armor.
     * @return the armor
     */
    @Override
    public Float getArmor() {
        if (hasArmorAttribute()) {
            return null;
        }

        throw new UnsupportedOperationException("This method, getArmor(), should not be called on this card: " + _title);
    }

    /**
     * Determines if this has a maneuver attribute.
     * @return true if has attribute, otherwise false
     */
    @Override
    public boolean hasManeuverAttribute() {
        return false;
    }

    /**
     * Gets the maneuver.
     * @return the maneuver.
     */
    @Override
    public Float getManeuver() {
        throw new UnsupportedOperationException("This method, getManeuver(), should not be called on this card: " + _title);
    }

    /**
     * Determines if has hyperspeed attribute
     * @return true if has attribute, otherwise false
     */
    @Override
    public boolean hasHyperspeedAttribute() {
        return false;
    }

    /**
     * Gets the hyperspeed.
     * @return the hyperspeed
     */
    @Override
    public Float getHyperspeed() {
        throw new UnsupportedOperationException("This method, getHyperspeed(), should not be called on this card: " + _title);
    }

    /**
     * Determines if this has ferocity attribute.
     * @return true if has attribute, otherwise false
     */
    @Override
    public boolean hasFerocityAttribute() {
        return false;
    }

    /**
     * Gets the ferocity.
     * @return the ferocity
     */
    @Override
    public Float getFerocity() {
        throw new UnsupportedOperationException("This method, getFerocity(), should not be called on this card: " + _title);
    }

    /**
     * Determines if this has a special defense value attribute.
     * @return true if has attribute, otherwise false
     */
    @Override
    public boolean hasSpecialDefenseValueAttribute() {
        return false;
    }

    /**
     * Gets the special defense value.
     * @return the special defense value
     */
    @Override
    public float getSpecialDefenseValue() {
        throw new UnsupportedOperationException("This method, getSpecialDefenseValue(), should not be called on this card: " + _title);
    }

    /**
     * Gets the deploy cost.
     * @return the deploy cost
     */
    @Override
    public Float getDeployCost() {
        throw new UnsupportedOperationException("This method, getDeployCost(), should not be called on this card: " + _title);
    }

    /**
     * Determines if has immunity to attrition attribute.
     * @return true if has attribute, otherwise false
     */
    @Override
    public boolean hasImmunityToAttritionAttribute() {
        return false;
    }

    /**
     * Gets a filter for the cards that are matching characters for this.
     * @return the filter
     */
    @Override
    public Filter getMatchingCharacterFilter() {
        throw new UnsupportedOperationException("This method, getMatchingCharacterFilter(), should not be called on this card: " + _title);
    }

    /**
     * Gets a filter for the cards that are matching pilots (or drivers) for this.
     * @return the filter
     */
    @Override
    public Filter getMatchingPilotFilter() {
        throw new UnsupportedOperationException("This method, getMatchingPilotFilter(), should not be called on this card: " + _title);
    }

    /**
     * Gets a filter for the cards that are matching starships for this.
     * @return the filter
     */
    @Override
    public Filter getMatchingStarshipFilter() {
        throw new UnsupportedOperationException("This method, getMatchingStarshipFilter(), should not be called on this card: " + _title);
    }

    /**
     * Gets a filter for the cards that are matching vehicles for this.
     * @return the filter
     */
    @Override
    public Filter getMatchingVehicleFilter() {
        throw new UnsupportedOperationException("This method, getMatchingVehicleFilter(), should not be called on this card: " + _title);
    }

    /**
     * Gets a filter for the cards that are matching weapons for this.
     * @return the filter
     */
    @Override
    public Filter getMatchingWeaponFilter() {
        throw new UnsupportedOperationException("This method, getMatchingWeaponFilter(), should not be called on this card: " + _title);
    }

    /**
     * Gets the title of the matching system for this. Used for Operatives to determine matching system.
     * @return the system name, or null
     */
    @Override
    public String getMatchingSystem() {
        throw new UnsupportedOperationException("This method, getMatchingSystem(), should not be called on this card: " + _title);
    }

    /**
     * Determines if the generic location may be part of the specified system.
     * @param system the system name
     * @return true if generic location may be part of system, otherwise false
     */
    @Override
    public boolean mayNotBePartOfSystem(SwccgGame game, String system) {
        throw new UnsupportedOperationException("This method, mayNotBePartOfSystem(), should not be called on this card: " + _title);
    }

    /**
     * Determines if a special rule is in effect at this location.
     * @param rule the special rule
     * @param self this location
     * @return true if rule is in effect at this location, otherwise false
     */
    @Override
    public boolean isSpecialRuleInEffectHere(SpecialRule rule, PhysicalCard self) {
        throw new UnsupportedOperationException("This method, isSpecialRuleInEffectHere(), should not be called on this card: " + _title);
    }

    /**
     * Gets the card blueprint of the permanent weapon.
     * @param self the card
     * @return the card blueprint
     */
    @Override
    public SwccgBuiltInCardBlueprint getPermanentWeapon(PhysicalCard self) {
        return null;
    }

    /**
     * Gets the card blueprints of permanent pilots and astromechs aboard.
     * @return list of card blueprints
     */
    @Override
    public List<SwccgBuiltInCardBlueprint> getPermanentsAboard(PhysicalCard self) {
        return Collections.emptyList();
    }

    /**
     * Gets a filter for the cards that are valid to be pilots (or drivers) of the specified card.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forDeployment true if checking for deployment, otherwise false
     * @return the filter
     */
    @Override
    public Filter getValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self, boolean forDeployment) {
        throw new UnsupportedOperationException("This method, getValidPilotFilter(), should not be called on this card: " + _title);
    }

    /**
     * Gets a filter for the cards that are valid to be passengers of the specified card.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forDeployment true if checking for deployment, otherwise false
     * @return the filter
     */
    @Override
    public Filter getValidPassengerFilter(String playerId, SwccgGame game, PhysicalCard self, boolean forDeployment) {
        throw new UnsupportedOperationException("This method, getValidPassengerFilter(), should not be called on this card: " + _title);
    }


    /**
     * Gets the pilot capacity.
     * @return the pilot capacity
     */
    @Override
    public int getPilotCapacity() {
        throw new UnsupportedOperationException("This method, getPilotCapacity(), should not be called on this card: " + _title);
    }

    /**
     * Gets the pilot or passenger capacity.
     * @return the pilot or passenger capacity
     */
    @Override
    public int getPilotOrPassengerCapacity() {
        throw new UnsupportedOperationException("This method, getPilotOrPassengerCapacity(), should not be called on this card: " + _title);
    }

    /**
     * Gets the passenger capacity.
     * @return the passenger capacity.
     */
    @Override
    public int getPassengerCapacity() {
        throw new UnsupportedOperationException("This method, getPassengerCapacity(), should not be called on this card: " + _title);
    }

    /**
     * Gets the astromech capacity.
     * @return the astromech capacity
     */
    @Override
    public int getAstromechCapacity() {
        throw new UnsupportedOperationException("This method, getAstromechCapacity(), should not be called on this card: " + _title);
    }

    /**
     * Gets the vehicle capacity.
     * @return the vehicle capacity
     */
    @Override
    public int getVehicleCapacity() {
        throw new UnsupportedOperationException("This method, getVehicleCapacity(), should not be called on this card: " + _title);
    }

    /**
     * Gets the filter for cards that can go in a vehicle slot.
     * @return the vehicle capacity filter
     */
    @Override
    public Filter getVehicleCapacityFilter() {
        throw new UnsupportedOperationException("This method, getVehicleCapacityFilter(), should not be called on this card: " + _title);
    }

    /**
     * Gets the starfighter or TIE capacity.
     * @return the starfighter or TIE capacity
     */
    @Override
    public int getStarfighterOrTIECapacity() {
        throw new UnsupportedOperationException("This method, getStarfighterOrTIECapacity(), should not be called on this card: " + _title);
    }

    /**
     * Gets the filter for cards that can go in a starfighter or TIE slot.
     * @return the starfighter or TIE capacity filter
     */
    @Override
    public Filter getStarfighterOrTIECapacityFilter() {
        throw new UnsupportedOperationException("This method, getStarfighterOrTIECapacityFilter(), should not be called on this card: " + _title);
    }

    /**
     * Gets the capital starship capacity.
     * @return the capital starship capacity
     */
    @Override
    public int getCapitalStarshipCapacity() {
        throw new UnsupportedOperationException("This method, getCapitalStarshipCapacity(), should not be called on this card: " + _title);
    }

    /**
     * Gets the filter for cards that can go in a capital starship capacity slot.
     * @return the capital starship capacity filter
     */
    @Override
    public Filter getCapitalStarshipCapacityFilter() {
        throw new UnsupportedOperationException("This method, getCapitalStarshipCapacityFilter(), should not be called on this card: " + _title);
    }

    /**
     * Gets the persona of the starship or vehicle the site is related to.
     * @return the vehicle or starship persona the site is related to, otherwise null
     */
    @Override
    public Persona getRelatedStarshipOrVehiclePersona() {
        throw new UnsupportedOperationException("This method, getRelatedStarshipOrVehiclePersona(), should not be called on this card: " + _title);
    }

    /**
     * Gets the parsec number of the system.
     * @return the parsec number
     */
    @Override
    public int getParsec() {
        throw new UnsupportedOperationException("This method, getParsec(), should not be called on this card: " + _title);
    }

    /**
     * Gets the name of the system the location deploys orbiting.
     * @return the system name, or null
     */
    @Override
    public String getDeploysOrbitingSystem() {
        throw new UnsupportedOperationException("This method, getDeploysOrbitingSystem(), should not be called on this card: " + _title);
    }

    /**
     * Gets the action playing this card as a starting interrupt.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action
     */
    @Override
    public PlayCardAction getStartingInterruptAction(String playerId, SwccgGame game, PhysicalCard self) {
        throw new UnsupportedOperationException("This method, getStartingInterruptAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the action playing this Interrupt in response to an effect or an effect result.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param sourceCard the card to initiate the action
     * @param effect the effect to response to
     * @param effectResult the effect result to response to
     * @return the action
     */
    @Override
    public PlayCardAction getPlayInterruptAsResponseAction(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard sourceCard, Effect effect, EffectResult effectResult) {
        throw new UnsupportedOperationException("This method, getPlayInterruptAsResponseAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the play card action for the card if it can be played. If the card can be played in multiple ways, then
     * this will return an action that has the player choose which way to play the card, then that plays the card that
     * way.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param sourceCard the card to initiate the deployment
     * @param forFree true if playing card for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param deploymentOption specifies special deployment options, or null
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param deployAsCaptiveOption specifies the way to deploy the card as a captive, or null
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @param cardToDeployWith the card to deploy with simultaneously, or null
     * @param cardToDeployWithForFree true if the card to deploy with deploys for free, otherwise false
     * @param cardToDeployWithChangeInCost change in amount of Force (can be positive or negative) required for the card to deploy with
     * @param deployTargetFilter the filter for where the card can be played
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null         @return the play card action
     */
    @Override
    public PlayCardAction getPlayCardAction(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard sourceCard, boolean forFree, float changeInCost, DeploymentOption deploymentOption, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption, ReactActionOption reactActionOption, PhysicalCard cardToDeployWith, boolean cardToDeployWithForFree, float cardToDeployWithChangeInCost, Filter deployTargetFilter, Filter specialLocationConditions) {
        throw new UnsupportedOperationException("This method, getPlayCardAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the play card actions for each way the card can be played by the specified player.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param sourceCard the card to initiate the deployment
     * @param forFree true if playing card for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param deploymentOption specifies special deployment options, or null
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param deployAsCaptiveOption specifies the way to deploy the card as a captive, or null
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @param cardToDeployWith the card to deploy with simultaneously, or null
     * @param cardToDeployWithForFree true if the card to deploy with deploys for free, otherwise false
     * @param cardToDeployWithChangeInCost change in amount of Force (can be positive or negative) required for the card to deploy with
     * @param deployTargetFilter the filter for where the card can be played
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null         @return the play card actions
     */
    @Override
    public List<PlayCardAction> getPlayCardActions(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard sourceCard, boolean forFree, float changeInCost, DeploymentOption deploymentOption, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption, ReactActionOption reactActionOption, PhysicalCard cardToDeployWith, boolean cardToDeployWithForFree, float cardToDeployWithChangeInCost, Filter deployTargetFilter, Filter specialLocationConditions) {
        throw new UnsupportedOperationException("This method, getPlayCardActions(), should not be called on this card: " + _title);
    }

    /**
     * Determines if the card is an Effect that deploys on another card.
     * @param game the game
     * @param self the card
     * @return true if card is an Effect that deploys on another card
     */
    @Override
    public boolean isEffectThatDeploysOnAnotherCard(SwccgGame game, PhysicalCard self) {
        return false;
    }

    /**
     * Gets the play card action for the card if it can be deployed as a dejarik/hologram to a holosite.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param sourceCard the card to initiate the deployment
     * @param forFree true if playing card for free, otherwise false
     * @return the play card actions
     */
    @Override
    public PlayCardAction getDeployDejarikAction(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard sourceCard, boolean forFree) {
        // Only a dejarik or hologram can be deployed using "Dejarik Rules"
        if (!Filters.or(Filters.dejarik, Filters.hologram).accepts(game.getGameState(), game.getModifiersQuerying(), self))
            return null;

        // Check requirements for deploying
        if (!checkDeployDejarikRequirements(playerId, game, self, sourceCard, forFree))
            return null;

        return new PlayDejarikAction(sourceCard, self, forFree);
    }

    /**
     * Gets the play card action for the location card if it can be deployed to the specified system.
     * @param playerId the player
     * @param game the game
     * @param self the location card
     * @param sourceCard the card to initiate the deployment
     * @param system the system name
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @return the play card action
     */
    @Override
    public PlayCardAction getPlayLocationToSystemAction(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard sourceCard, String system, Filter specialLocationConditions) {
        throw new UnsupportedOperationException("This method, getPlayLocationToSystemAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the valid filter for targets to deploy to when the specified card is deployed simultaneously with the specified pilot, driver, or passenger.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param sourceCard the card to initiate the deployment
     * @param forFree true if playing card for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param character the pilot, driver, or passenger
     * @param characterForFree true if playing pilot, driver, or passenger for free, otherwise false
     * @param characterChangeInCost change in amount of Force (can be positive or negative) required to deploy pilot, driver, or passenger
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param reactActionOption a 'react' action option, or null if not a 'react'   @return the filter
     */
    @Override
    public Filter getValidDeployTargetWithPilotOrPassengerFilter(String playerId, final SwccgGame game, final PhysicalCard self, PhysicalCard sourceCard, boolean forFree, float changeInCost, PhysicalCard character, boolean characterForFree, float characterChangeInCost, DeploymentRestrictionsOption deploymentRestrictionsOption, ReactActionOption reactActionOption) {
        throw new UnsupportedOperationException("This method, getValidDeployTargetWithPilotOrPassengerFilter(), should not be called on this card: " + _title);
    }

    /**
     * This method is for checking which starships or vehicles the character can deploy to as pilot simultaneously regardless
     * of location and deploy cost. It is to be used by other methods (that factor in the valid locations and deploy costs)
     * to figure out which starship/vehicles and pilots/drivers can deploy simultaneously.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the filter
     */
    @Override
    public Filter getValidSimultaneouslyDeployingStarshipOrVehicleToAnyLocationFilter(String playerId, final SwccgGame game, final PhysicalCard self) {
        throw new UnsupportedOperationException("This method, getValidSimultaneouslyDeployingStarshipOrVehicleToAnyLocationFilter(), should not be called on this card: " + _title);
    }

    /**
     * This method is for checking which locations a character can deploy to as pilot, driver, or passenger simultaneously regardless of deploy cost.
     * It is to be used by other methods (that factor in the valid starships/vehicles and deploy costs) to figure out which
     * starship/vehicles and pilots/drivers/passengers can deploy simultaneously.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param sourceCard the card to initiate the deployment
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @return the filter
     */
    @Override
    public Filter getValidLocationForSimultaneouslyDeployingAsPilotOrPassengerFilter(String playerId, final SwccgGame game, final PhysicalCard self, PhysicalCard sourceCard, DeploymentRestrictionsOption deploymentRestrictionsOption, ReactActionOption reactActionOption) {
        throw new UnsupportedOperationException("This method, getValidLocationForSimultaneouslyDeployingAsPilotOrPassengerFilter(), should not be called on this card: " + _title);
    }

    /**
     * This method is used for getting the filter for starships or vehicles that can be related to the non-unique startship
     * or vehicle site.
     * @return the filter
     */
    @Override
    public Filter getRelatedStarshipOrVehicleFilter() {
        throw new UnsupportedOperationException("This method, getRelatedStarshipOrVehicleFilter(), should not be called on this card: " + _title);
    }

    /**
     * Gets the valid filter for targets to transfer the card to another card during character replacement.
     * @param game the game
     * @param self the card
     * @return the filter
     */
    @Override
    public Filter getValidTransferDuringCharacterReplacementTargetFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.none;
    }

    /**
     * Gets the valid filter for targets to place the card when the specified card is placed from off table.
     * @param game the game
     * @param self the card
     * @return the filter
     */
    @Override
    public Filter getValidPlaceCardTargetFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.none;
    }

    /**
     * Gets the valid filter for targets to relocate the Effect when the specified Effect is relocated.
     * @param playerId the player to relocate the Effect
     * @param game the game
     * @param self the card
     * @return the filter
     */
    @Override
    public Filter getValidRelocateEffectTargetFilter(final String playerId, final SwccgGame game, final PhysicalCard self) {
        return Filters.none;
    }

    /**
     * Gets the valid target filter that the card can remain attached to after the attached to card is crossed-over.
     * @param game the game
     * @param self the card
     * @return the filter
     */
    @Override
    public Filter getValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self) {
        throw new UnsupportedOperationException("This method, getValidTargetFilterToRemainAttachedToAfterCrossingOver(), should not be called on this card: " + _title);
    }

    /**
     * Gets the valid target filter that the card can remain attached to. If the card becomes attached to a card that is
     * not accepted by this filter, then the attached card will be lost by rule.
     * @param game the game
     * @param self the card
     * @return the filter
     */
    @Override
    public Filter getValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        throw new UnsupportedOperationException("This method, getValidTargetFilterToRemainAttachedTo(), should not be called on this card: " + _title);
    }

    /**
     * Gets the valid target filter that the specified Utinni Effect may remain targeting. If the card targeted by the Utinni
     * Effect becomes not accepted by this filter, then the Utinni Effect will be lost by rule.
     * @param game the game
     * @param self the card
     * @param targetId the Utinni Effect target id
     * @return the filter
     */
    @Override
    public Filter getValidUtinniEffectTargetFilterToRemainTargeting(SwccgGame game, PhysicalCard self, TargetId targetId) {
        throw new UnsupportedOperationException("This method, getValidUtinniEffectTargetFilterToRemainTargeting(), should not be called on this card: " + _title);
    }

    /**
     * Gets the habitat filter for the specified creature card.
     * @param game the game
     * @param self the card
     * @return the filter
     */
    @Override
    public Filter getHabitatFilter(final SwccgGame game, final PhysicalCard self) {
        throw new UnsupportedOperationException("This method, getHabitatFilter(), should not be called on this card: " + _title);
    }

    /**
     * Determines if this creature's habitat includes aboard a starship.
     * @return true if has attribute, otherwise false
     */
    @Override
    public boolean habitatIncludesAboardStarship() {
        throw new UnsupportedOperationException("This method, habitatIncludesAboardStarship(), should not be called on this card: " + _title);
    }

    /**
     * Gets the only play card zone option for this card. This is only set by cards that are only played to one zone.
     * @return the play card zone option, or null
     */
    @Override
    public PlayCardZoneOption getSinglePlayCardZoneOption() {
        throw new UnsupportedOperationException("This method, getDefaultPlayCardZoneOption(), should not be called on this card: " + _title);
    }

    /**
     * Gets the transfer device or weapon action if the device or weapon can be transferred by the specified player.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param transferTargetFilter the filter for where the card can be transferred
     * @return the transfer device or weapon actions
     */
    @Override
    public Action getTransferDeviceOrWeaponAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, Filter transferTargetFilter) {
        throw new UnsupportedOperationException("This method, getTransferDeviceOrWeaponAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the target filter for where a device or weapon can be transferred by the specified player.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param playCardOption the play card option, or null
     * @param forFree true if transferring for free, otherwise false
     * @param transferTargetFilter the filter for where the card can be transferred
     * @return the target filter
     */
    @Override
    public Filter getValidTransferDeviceOrWeaponTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PlayCardOption playCardOption, boolean forFree, Filter transferTargetFilter) {
        throw new UnsupportedOperationException("This method, getValidTransferDeviceOrWeaponTargetFilter(), should not be called on this card: " + _title);
    }

    /**
     * Gets the deploy as 'react' action for the card if it can deploy as a 'react'.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param reactActionFromOtherCard a 'react' action if 'react' originates from another card, or null
     * @param deployTargetFilter the filter for where the card can deploy
     * @return the action
     */
    @Override
    public Action getDeployAsReactAction(String playerId, SwccgGame game, PhysicalCard self, ReactActionOption reactActionFromOtherCard, Filter deployTargetFilter) {
        throw new UnsupportedOperationException("This method, getDeployAsReactAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the fire weapon action for the weapon (or card with permanent weapon) if it can be fired. If the weapon can
     * be fired in multiple ways, then this will return an action that has the player choose which way to fire the card,
     * then fires the card that way.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if playing card for free, otherwise false
     * @param extraForceRequired the extra amount of Force required to perform the fire weapon
     * @param sourceCard true if firing from another action allowing firing, otherwise false
     * @param repeatedFiring true if this is a repeated firing, otherwise false
     * @param targetedAsCharacter filter for cards may be targeted as characters
     * @param defenseValueAsCharacter defense value to use for cards may be targeted as characters, or null
     * @param fireAtTargetFilter the filter for where the card can be played  @return the fire weapon action
     * @param ignorePerAttackOrBattleLimit true if per attack/battle firing limit is ignored, otherwise false
     */
    @Override
    public FireWeaponAction getFireWeaponAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        throw new UnsupportedOperationException("This method, getFireWeaponAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the fire weapon actions for each way the weapon (or card with permanent weapon) can be fired.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if playing card for free, otherwise false
     * @param extraForceRequired the extra amount of Force required to perform the fire weapon
     * @param sourceCard true if firing from another action allowing firing, otherwise false
     * @param repeatedFiring true if this is a repeated firing, otherwise false
     * @param targetedAsCharacter filter for cards may be targeted as characters
     * @param defenseValueAsCharacter defense value to use for cards may be targeted as characters, or null
     * @param fireAtTargetFilter the filter for where the card can be played  @return the fire weapon action
     * @param ignorePerAttackOrBattleLimit true if per attack/battle firing limit is ignored, otherwise false
     */
    @Override
    public List<FireWeaponAction> getFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        throw new UnsupportedOperationException("This method, getFireWeaponActions(), should not be called on this card: " + _title);
    }

    /**
     * Gets the move as 'react' action for the card if it can move as a 'react'.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param reactActionFromOtherCard a 'react' action if 'react' originates from another card, or null
     * @param moveTargetFilter the filter for where the card can move
     * @return the action
     */
    @Override
    public Action getMoveAsReactAction(String playerId, SwccgGame game, PhysicalCard self, ReactActionOption reactActionFromOtherCard, Filter moveTargetFilter) {
        throw new UnsupportedOperationException("This method, getMoveAsReactAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the move away action for a card if it can move away.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required to perform the movement
     * @param asReact true if moving as a 'react', otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the action
     */
    @Override
    public Action getMoveAwayAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, float changeInCost, boolean asReact, Filter moveTargetFilter) {
        throw new UnsupportedOperationException("This method, getMoveAwayAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the regular move action for the card if it can move as a regular move.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required to perform the movement
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the action
     */
    @Override
    public Action getRegularMoveAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, float changeInCost, boolean skipPhaseCheck, boolean asAdditionalMove, Filter moveTargetFilter) {
        throw new UnsupportedOperationException("This method, getRegularMoveAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the move using landspeed action for the card if it can move using landspeed.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required to perform the movement
     * @param asReact true if moving as a 'react', otherwise false
     * @param asMoveAway true if moving as a move away, otherwise false
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @param landspeedOverride the specified landspeed to use for this movement, or null if using normal landspeed
     * @param moveTargetFilter the filter for where the card can move
     * @return the action
     */
    @Override
    public Action getMoveUsingLandspeedAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, float changeInCost, boolean asReact, boolean asMoveAway, boolean skipPhaseCheck, boolean asAdditionalMove, Integer landspeedOverride, Filter moveTargetFilter) {
        throw new UnsupportedOperationException("This method, getMoveUsingLandspeedAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the move using hyperspeed action for the card if it can move using hyperspeed.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param asReact true if moving as a 'react', otherwise false
     * @param asMoveAway true if moving as a move away, otherwise false
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the action, or null
     */
    @Override
    public Action getMoveUsingHyperspeedAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asReact, boolean asMoveAway, boolean skipPhaseCheck, boolean asAdditionalMove, Filter moveTargetFilter) {
        throw new UnsupportedOperationException("This method, getMoveUsingHyperspeedAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the move without using hyperspeed action for the card if it can move without using hyperspeed.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param asReact true if moving as a 'react', otherwise false
     * @param asMoveAway true if moving as a move away, otherwise false
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the action, or null
     */
    @Override
    public Action getMoveWithoutUsingHyperspeedAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asReact, boolean asMoveAway, boolean skipPhaseCheck, boolean asAdditionalMove, Filter moveTargetFilter) {
        throw new UnsupportedOperationException("This method, getMoveWithoutUsingHyperspeedAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the move using sector movement action for the card if it can move using sector movement.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param asReact true if moving as a 'react', otherwise false
     * @param asMoveAway true if moving as a move away, otherwise false
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the action, or null
     */
    @Override
    public Action getMoveUsingSectorMovementAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asReact, boolean asMoveAway, boolean skipPhaseCheck, boolean asAdditionalMove, Filter moveTargetFilter) {
        throw new UnsupportedOperationException("This method, getMoveUsingSectorMovementAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the move using sector movement action for the card if it can move using sector movement during 'escape' from
     * Death Star II being 'blown away'.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    @Override
    public Action getMoveUsingEscapeFromDeathStarIIMovementAction(String playerId, SwccgGame game, PhysicalCard self) {
        throw new UnsupportedOperationException("This method, getMoveUsingEscapeFromDeathStarIIMovementAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the land action for the card if it can land.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param asReact true if moving as a 'react', otherwise false
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the action, or null
     */
    @Override
    public Action getLandAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asReact, boolean skipPhaseCheck, boolean asAdditionalMove, Filter moveTargetFilter) {
        throw new UnsupportedOperationException("This method, getLandAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the take off action for the card if it can take off.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param asReact true if moving as a 'react', otherwise false
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the action, or null
     */
    @Override
    public Action getTakeOffAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asReact, boolean skipPhaseCheck, boolean asAdditionalMove, Filter moveTargetFilter) {
        throw new UnsupportedOperationException("This method, getTakeOffAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the move action for a bomber at the start of a Bombing Run.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @return the action, or null
     */
    @Override
    public Action getMoveToStartBombingRunAction(String playerId, SwccgGame game, PhysicalCard self, boolean skipPhaseCheck, boolean asAdditionalMove) {
        throw new UnsupportedOperationException("This method, getMoveToStartBombingRunAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the move action for a bomber at the end of a Bombing Run.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @return the action, or null
     */
    @Override
    public Action getMoveToEndBombingRunAction(String playerId, SwccgGame game, PhysicalCard self, boolean skipPhaseCheck, boolean asAdditionalMove) {
        throw new UnsupportedOperationException("This method, getMoveToEndBombingRunAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the move action for a starfighter at the start of an Attack Run.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    @Override
    public Action getMoveAtStartOfAttackRunAction(String playerId, SwccgGame game, PhysicalCard self) {
        throw new UnsupportedOperationException("This method, getMoveAtStartOfAttackRunAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the move action for a starfighter at the end of an Attack Run.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    @Override
    public Action getMoveAtEndOfAttackRunAction(String playerId, SwccgGame game, PhysicalCard self) {
        throw new UnsupportedOperationException("This method, getMoveAtEndOfAttackRunAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the action to move to the related starship/vehicle from a starship/vehicle site.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @return the action, or null
     */
    @Override
    public Action getMoveToRelatedStarshipOrVehicleAction(String playerId, SwccgGame game, PhysicalCard self, boolean skipPhaseCheck, boolean asAdditionalMove) {
        throw new UnsupportedOperationException("This method, getMoveToRelatedStarshipOrVehicleAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the action to move to a related starship/vehicle site from the starship/vehicle.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @return the action, or null
     */
    @Override
    public Action getMoveToRelatedStarshipOrVehicleSiteAction(String playerId, SwccgGame game, PhysicalCard self, boolean skipPhaseCheck, boolean asAdditionalMove) {
        throw new UnsupportedOperationException("This method, getMoveToRelatedStarshipOrVehicleSiteAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the action to enter a starship/vehicle site from the site the starship or vehicle is present at.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param asReact true if moving as a 'react', otherwise false
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false  @return the action, or null
     * @param moveTargetFilter the filter for where the card can move
     * @return the action, or null
     */
    @Override
    public Action getEnterStarshipOrVehicleSiteAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asReact, boolean skipPhaseCheck, boolean asAdditionalMove, Filter moveTargetFilter) {
        throw new UnsupportedOperationException("This method, getEnterStarshipOrVehicleSiteAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the action to exit a starship/vehicle site to the site the starship or vehicle is present at.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param asReact true if moving as a 'react', otherwise false
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the action, or null
     */
    @Override
    public Action getExitStarshipOrVehicleSiteAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asReact, boolean skipPhaseCheck, boolean asAdditionalMove, Filter moveTargetFilter) {
        throw new UnsupportedOperationException("This method, getExitStarshipOrVehicleSiteAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the action to shuttle a character/vehicle to/from a capital starship.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the action, or null
     */
    @Override
    public Action getShuttleAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean skipPhaseCheck, boolean asAdditionalMove, Filter moveTargetFilter) {
        throw new UnsupportedOperationException("This method, getShuttleAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the action to shuttle characters from a site to a starship at the related system using a shuttle vehicle.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    @Override
    public Action getShuttleUpUsingShuttleVehicleAction(String playerId, SwccgGame game, PhysicalCard self) {
        throw new UnsupportedOperationException("This method, getShuttleUpUsingShuttleVehicleAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the action to shuttle characters to a site from a starship at the related system using a shuttle vehicle.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    @Override
    public Action getShuttleDownUsingShuttleVehicleAction(String playerId, SwccgGame game, PhysicalCard self) {
        throw new UnsupportedOperationException("This method, getShuttleDownUsingShuttleVehicleAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the action to ship-dock a starship with another starship.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @return the action, or null
     */
    @Override
    public Action getShipdockAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree) {
        throw new UnsupportedOperationException("This method, getShipdockAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the action to embark on a card (or to a location).
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the action, or null
     */
    @Override
    public Action getEmbarkAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, Filter moveTargetFilter) {
        throw new UnsupportedOperationException("This method, getEmbarkAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the action to disembark off of a card (or from a location).
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param asJumpOff true if disembarking as "jump off" vehicle, otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the action, or null
     */
    @Override
    public Action getDisembarkAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asJumpOff, Filter moveTargetFilter) {
        throw new UnsupportedOperationException("This method, getDisembarkAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the action to move between the capacity slots of a card.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    @Override
    public Action getMoveBetweenCapacitySlotsAction(String playerId, SwccgGame game, PhysicalCard self) {
        throw new UnsupportedOperationException("This method, getMoveBetweenCapacitySlotsAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the action to move between ship-docked starships.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    @Override
    public Action getMoveBetweenDockedStarshipsAction(String playerId, SwccgGame game, PhysicalCard self) {
        throw new UnsupportedOperationException("This method, getMoveBetweenDockedStarshipsAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the action to deliver an escorted captive to prison.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    @Override
    public Action getDeliverCaptiveToPrisonAction(String playerId, SwccgGame game, PhysicalCard self) {
        throw new UnsupportedOperationException("This method, getDeliverCaptiveToPrisonAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the action to take an imprisoned captive from prison into custody.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    @Override
    public Action getTakeImprisonedCaptiveIntoCustodyAction(String playerId, SwccgGame game, PhysicalCard self) {
        throw new UnsupportedOperationException("This method, getTakeImprisonedCaptiveIntoCustodyAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the action to leave an escorted 'frozen' captive as 'unattended'.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    @Override
    public Action getLeaveFrozenCaptiveUnattendedAction(String playerId, SwccgGame game, PhysicalCard self) {
        throw new UnsupportedOperationException("This method, getLeaveFrozenCaptiveUnattendedAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the action to take an 'unattended frozen' captive into custody.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    @Override
    public Action getTakeUnattendedFrozenCaptiveIntoCustodyAction(String playerId, SwccgGame game, PhysicalCard self) {
        throw new UnsupportedOperationException("This method, getTakeUnattendedFrozenCaptiveIntoCustodyAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets the action for when 'insert' card is revealed.
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    @Override
    public Action getInsertCardRevealedAction(SwccgGame game, PhysicalCard self) {
        throw new UnsupportedOperationException("This method, getInsertCardRevealedAction(), should not be called on this card: " + _title);
    }

    /**
     * Gets a filter for the cards the specified card is not prohibited from moving to.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param allowTrench true if moving to Death Star: Trench is not prevented by "Trench Rules" for this movement
     * @return the filter
     */
    @Override
    public Filter getValidMoveTargetFilter(String playerId, final SwccgGame game, final PhysicalCard self, boolean allowTrench) {
        throw new UnsupportedOperationException("This method, getValidMoveTargetFilter(), should not be called on this card: " + _title);
    }

    /**
     * Gets the Utinni Effect target ids used by the Utinni Effect.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the Utinni Effect target ids
     */
    @Override
    public List<TargetId> getUtinniEffectTargetIds(String playerId, SwccgGame game, PhysicalCard self) {
        throw new UnsupportedOperationException("This method, getUtinniEffectTargetIds(), should not be called on this card: " + _title);
    }

    /**
     * Gets a filter for the cards the specified Utinni Effect may target (not including to deploy on).
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param deployTarget the card to deploy the Utinni Effect on
     * @param targetId the Utinni Effect target id
     * @return the filter
     */
    @Override
    public Filter getValidUtinniEffectTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, TargetId targetId) {
        throw new UnsupportedOperationException("This method, getValidUtinniEffectTargetFilter(), should not be called on this card: " + _title);
    }

    /**
     * Gets a filter for the cards that can be the mentor for the specified Jedi Test.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param deployTarget the card to deploy the Jedi Test on
     * @return the filter
     */
    @Override
    public Filter getValidJediTestMentorTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget) {
        throw new UnsupportedOperationException("This method, getValidJediTestMentorTargetFilter(), should not be called on this card: " + _title);
    }

    /**
     * Gets a filter for the cards that can be the apprentice for the specified Jedi Test.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param deployTarget the card to deploy the Jedi Test on
     * @param mentor the mentor for the apprentice
     * @param isDeployFromHand true if for an apprentice being deployed from hand
     * @return the filter
     */
    @Override
    public Filter getValidJediTestApprenticeTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, PhysicalCard mentor, boolean isDeployFromHand) {
        throw new UnsupportedOperationException("This method, getValidJediTestApprenticeTargetFilter(), should not be called on this card: " + _title);
    }

    @Override
    public DrawDestinyEffect getDrawWeaponDestinyEffect(GameState gameState, AbstractAction action, PhysicalCard self, PhysicalCard target, PhysicalCard cardFiringWeapon) {
        throw new UnsupportedOperationException("This method, getDrawWeaponDestinyEffect(), should not be called on this card: " + _title);
    }

    @Override
    public void weaponDestinyDrawComplete(SwccgGame game, final AbstractAction action, final PhysicalCard self, final PhysicalCard target,
                                          PhysicalCard cardFiringWeapon, List<PhysicalCard> destinyCardDraws, List<Integer> destinyDrawValues, Integer totalDestiny) {
        throw new UnsupportedOperationException("This method, weaponDestinyDrawComplete(), should not be called on this card: " + _title);
    }

    @Override
    public void weaponFireWasSuccessful(SwccgGame game, AbstractAction action, PhysicalCard self, PhysicalCard target, PhysicalCard cardFiringWeapon) {
        throw new UnsupportedOperationException("This method, weaponFireWasSuccessful(), should not be called on this card: " + _title);
    }

    @Override
    public int getNumWeaponDestinyDraws(GameState gameState, PhysicalCard self, PhysicalCard target) {
        throw new UnsupportedOperationException("This method, getNumWeaponDestinyDraws(), should not be called on this card: " + _title);
    }

    @Override
    public Statistic getStatisticTargetedWhenFiring() {
        throw new UnsupportedOperationException("This method, getStatisticTargetedWhenFiring(), should not be called on this card: " + _title);
    }

    @Override
    public Filter getValidFireAtTargetFilter(String playerId, SwccgGame game, PhysicalCard self, boolean isRepeatedFiring, boolean forFree, int changeInCost) {
        throw new UnsupportedOperationException("This method, getValidFireAtTargetFilter(), should not be called on this card: " + _title);
    }

    @Override
    public Filter getValidAtLocationForFireAtTargetFilter(String playerId, SwccgGame game, PhysicalCard self) {
        throw new UnsupportedOperationException("This method, getValidAtLocationForFireAtTargetFilter(), should not be called on this card: " + _title);
    }

    @Override
    public Filter getValidToUseDeviceFilter(String playerId, final SwccgGame game, final PhysicalCard self) {
        throw new UnsupportedOperationException("This method, getValidToUseDeviceFilter(), should not be called on this card: " + _title);
    }

    /**
     * Gets a filter for the cards that are valid to use the specified weapon.
     * @param playerId the player
     * @param game the game
     * @param self the weapon
     * @return the filter
     */
    @Override
    public Filter getValidToUseWeaponFilter(String playerId, final SwccgGame game, final PhysicalCard self) {
        throw new UnsupportedOperationException("This method, getValidToUseWeaponFilter(), should not be called on this card: " + _title);
    }

    /**
     * Determines if the weapon is fired by a character present rather than the card it is attached to.
     * @return true or false
     */
    @Override
    public boolean isFiredByCharacterPresentOrHere() {
        return false;
    }

    @Override
    public TractorBeamAction getTractorBeamAction(SwccgGame game, PhysicalCard self) {
        return null;
    }

    @Override
    public MagneticSuctionTubeAction getMagneticSuctionTubeAction(SwccgGame game, PhysicalCard self){
        return null;
    };

    /**
     * Determines if the card is inactive due to specific conditions even when the card would normally be active.
     * @param game the game
     * @param self the weapon
     * @return true if card is considered inactive instead of active, otherwise false
     */
    @Override
    public boolean isInactiveInsteadOfActive(final SwccgGame game, final PhysicalCard self) {
        return false;
    }

    @Override
    public boolean isMovesLikeCharacter() {
        return false;
    }

    /**
     * Determines if this deploys and moves like a starfighter.
     * @return true if deploys and moves like a starfighter, otherwise false
     */
    @Override
    public boolean isDeploysAndMovesLikeStarfighter() {
        return false;
    }

    /**
     * Determines if this deploys and moves like a starfighter at cloud sectors.
     * @return true if deploys and moves like a starfighter at cloud sectors, otherwise false
     */
    @Override
    public boolean isDeploysAndMovesLikeStarfighterAtCloudSectors() {
        return false;
    }

    @Override
    public boolean isVehicleSlotOfStarshipCompatible() {
        return false;
    }

    /**
     * Determines if this type of card is deployed or played
     * @return true if card is "deployed", false if card is "played"
     */
    @Override
    public boolean isCardTypeDeployed() {
        return false;
    }

    @Override
    public boolean canBeDeployedOnCharacter() {
        return false;
    }

    /**
     * Gets the system name for the location.
     * @return the system name
     */
    @Override
    public String getSystemName() {
        throw new UnsupportedOperationException("This method, getSystemName(), should not be called on this card: " + _title);
    }

    /**
     * Gets displayable information about the card.
     * @param game the game
     * @param self the card
     * @return displayable information about the card, or null
     */
    @Override
    public String getDisplayableInformation(SwccgGame game, PhysicalCard self) {
        return null;
    }

    /**
     * Determines if the card deploys as an undercover spy.
     * @param game the game
     * @param self the card
     * @return the owner of the zone
     */
    @Override
    public boolean isOnlyDeploysAsUndercoverSpy(SwccgGame game, PhysicalCard self) {
        return false;
    }

    /**
     * Determines if the card deploys as a captured prisoner.
     * @param game the game
     * @param self the card
     * @return the owner of the zone
     */
    @Override
    public boolean isOnlyDeploysAsEscortedCaptive(SwccgGame game, PhysicalCard self) {
        return false;
    }

    /**
     * Determines if the card may deploy as an undercover spy.
     * @param game the game
     * @param self the card
     * @return true or false
     */
    @Override
    public boolean mayDeployAsUndercoverSpy(SwccgGame game, PhysicalCard self) {
        return false;
    }

    /**
     * Gets the spot override to use when deploying the card with the specified play card option id.
     * @param playCardOptionId the play card option id
     * @return the spot override
     */
    @Override
    public Map<InactiveReason, Boolean> getDeployTargetSpotOverride(PlayCardOptionId playCardOptionId) {
        return null;
    }

    /**
     * Gets the spot override to use when targeting a card with the specified target id.
     * @param targetId thetarget id
     * @return the spot override
     */
    @Override
    public Map<InactiveReason, Boolean> getTargetSpotOverride(TargetId targetId) {
        return null;
    }

    /**
     * Determines if the card can be deploy simultaneously as attached.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return true if card cannot be deploy simultaneously as attached, otherwise false
     */
    @Override
    public boolean mayDeploySimultaneouslyAsAttachedRequirements(String playerId, SwccgGame game, PhysicalCard self) {
        return false;
    }

    /**
     * Determines if the card can be played.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param playCardOption the play card option, or null
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @return true if card can be played, otherwise false
     */
    protected boolean checkPlayRequirements(String playerId, SwccgGame game, PhysicalCard self, DeploymentRestrictionsOption deploymentRestrictionsOption, PlayCardOption playCardOption, ReactActionOption reactActionOption) {
        return playerId.equals(self.getOwner()) && !self.getZone().isInPlay()
                && !game.getModifiersQuerying().isPlayingCardProhibited(game.getGameState(), self, false)
                && !game.getModifiersQuerying().isPlayingCardTitleTurnLimitReached(game.getGameState(), self)
                && (reactActionOption == null || !game.getModifiersQuerying().isProhibitedFromParticipatingInReact(game.getGameState(), self));
    }

    /**
     * Determines if the card can be played using "Dejarik Rules".
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param sourceCard the card to initiate the deployment
     * @param forFree true if deploy cost is ignored, otherwise false
     * @return true if card can be played, otherwise false
     */
    protected boolean checkDeployDejarikRequirements(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard sourceCard, boolean forFree) {
        return playerId.equals(self.getOwner()) && self.getZone() == Zone.HAND
                && !game.getModifiersQuerying().isPlayingCardProhibited(game.getGameState(), self, true)
                && !game.getModifiersQuerying().isPlayingCardTitleTurnLimitReached(game.getGameState(), self)
                && !game.getModifiersQuerying().isUniquenessOnTableLimitReached(game.getGameState(), self)
                && Filters.canSpotFromTopLocationsOnTable(game, Filters.holosite)
                && GameConditions.canUseForceToDeployCard(game, self, sourceCard, null, forFree, 0, true);
    }

    /**
     * Gets effects (to be performed in order) that set any targeted cards when the card is being deployed.
     * @param action the action to perform the effect
     * @param playerId the performing player
     * @param game the game
     * @param self the card
     * @param target the target to where the card is being deployed, or null (if side of table or card pile)
     * @param playCardOption the play card option chosen
     * @return the targeting effects, or null
     */
    @Override
    public List<TargetingEffect> getTargetCardsWhenDeployedEffects(Action action, String playerId, SwccgGame game, PhysicalCard self, PhysicalCard target, PlayCardOption playCardOption) {
        return null;
    }

    /**
     * This method is called during deployment after the targets have been selected so the target filters can be updated
     * to support re-targeting.
     * @param action the action to perform the effect
     * @param game the game
     * @param self the card
     */
    @Override
    public void updateTargetFiltersAfterTargetsChosen(Action action, SwccgGame game, PhysicalCard self) {
    }

    /**
     * This method is called during deployment after the card is on table so the target filters can be updated
     * to support re-targeting.
     * @param game the game
     * @param self the card
     */
    @Override
    public void updateTargetFiltersAfterOnTable(SwccgGame game, PhysicalCard self) {
    }

    /**
     * Gets a special deploy cost (instead of using Force) that is used to deploy the card.
     * @param action the action to perform the effect
     * @param playerId the performing player
     * @param game the game
     * @param self the card
     * @param target the target to where the card is being deployed, or null (if side of table or card pile)
     * @param playCardOption the play card option chosen
     * @return the special deploy cost, or null
     */
    @Override
    public StandardEffect getSpecialDeployCostEffect(Action action, String playerId, SwccgGame game, PhysicalCard self, PhysicalCard target, PlayCardOption playCardOption) {
        return null;
    }

    /**
     * Gets the game-rule actions that can be performed by the specified player by clicking on the top card of a card pile.
     * @param playerId the player
     * @param game the game
     * @param self the top card of the card pile
     * @return the actions
     */
    @Override
    public final List<Action> getCardPilePhaseActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<Action> actions = new LinkedList<Action>();
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        // Check condition(s)
        if (GameConditions.canActivateForceWithCardPileAction(playerId, gameState, modifiersQuerying, self)) {

            final CardPileAction action = new CardPileAction(playerId, self) {
                @Override
                public String getText() {
                    return "Activate Force";
                }
            };
            // (Time-saver) Check if player wants to activate "all Force-generation Force" at once or one at a time
            final int maxToActivate = Math.min((int) Math.floor(gameState.getPlayersTotalForceGeneration(playerId) - modifiersQuerying.getForceActivatedThisTurn(playerId, true)),
                    gameState.getReserveDeckSize(playerId));
            // Perform result(s)
            action.appendTargeting(
                    new PlayoutDecisionEffect(action, playerId,
                            new IntegerAwaitingDecision("Choose amount of Force to activate", 0, maxToActivate, maxToActivate) {
                                @Override
                                public void decisionMade(final int result) throws DecisionResultInvalidException {
                                    if (result == 0) {
                                        action.appendTargeting(
                                                new FailCostEffect(action));
                                    }
                                    else if (result == 1) {
                                        action.appendEffect(
                                                new ActivateForceEffect(action, playerId, result, true));
                                    }
                                    else {
                                        // Confirm with opponent max number of Force to allow player to activate before giving opponent a chance to perform an action
                                        action.appendTargeting(
                                                new PlayoutDecisionEffect(action, game.getOpponent(playerId),
                                                        new IntegerAwaitingDecision("Choose amount of Force to allow opponent to activate without you performing a top-level action", 1, maxToActivate, maxToActivate) {
                                                            @Override
                                                            public void decisionMade(int result2) throws DecisionResultInvalidException {
                                                                final int result3 = (result2 > 0) ? Math.min(result, result2) : result;
                                                                action.appendEffect(
                                                                        new ActivateForceEffect(action, playerId, result3, true));
                                                                // If amount of Force opponent is allowing player to activate is less then player chose to activate,
                                                                // then send player a message that not allow Force initially chooses were activated
                                                                if (result3 < result) {
                                                                    action.appendEffect(
                                                                            new PassthruEffect(action) {
                                                                                @Override
                                                                                protected void doPlayEffect(SwccgGame game) {
                                                                                    game.getUserFeedback().sendAwaitingDecision(playerId,
                                                                                            new MultipleChoiceAwaitingDecision("Opponent chose to interrupt Force activation to have an opportunity to perform a top-level action after " + result3 + " Force was activated", new String[]{"OK"}) {
                                                                                                @Override
                                                                                                protected void validDecisionMade(int index, String result) {
                                                                                                }
                                                                                            }
                                                                                    );
                                                                                }
                                                                            }
                                                                    );
                                                                }
                                                            }
                                                        }
                                                )
                                        );
                                    }
                                }
                            }
                    )
            );
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canDrawCardWithCardPileAction(playerId, game, self)) {

            CardPileAction action = new CardPileAction(playerId, self) {
                @Override
                public String getText() {
                    return "Draw card into hand from Force Pile";
                }
            };
            // Perform result(s)
            action.appendEffect(
                    new DrawCardIntoHandFromForcePileEffect(action, playerId));
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canPerformSpecialBattlegroundDownload(playerId, game, self)) {

            CardPileAction action = new SpecialBattlegroundDownloadAction(playerId, self);
            // Perform result(s)
            action.appendCost(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            modifiersQuerying.performedSpecialBattlegroundDownload(playerId);
                            gameState.activatedCard(playerId, self);
                            gameState.sendMessage(playerId + " targets to deploy a unique battleground (not already on table) from Reserve Deck");
                        }
                    }
            );
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.unique, Filters.location, Filters.not(Filters.sameTitleAs(self, Filters.onTable))), Filters.battleground, true));
            // Since the user interface will only show a pop-up for Reserve Deck if there are multiple actions,
            // put this action in the list twice. The user interface will only actually show the first item in the popup.
            actions.add(action);
            if (actions.size() == 1) {
                actions.add(action);
            }
        }
        // Check condition(s)
        if (GameConditions.canPerformSpecialPlaytestingUpload(playerId, game, self)) {

            CardPileAction action = new SpecialPlaytestingUploadAction(playerId, self);
            // Perform result(s)
            action.appendCost(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            gameState.activatedCard(playerId, self);
                            gameState.sendMessage(playerId + " targets to examine Reserve Deck and optionally take cards into hand");
                        }
                    }
            );
            action.appendEffect(
                    new TakeCardsIntoHandFromReserveDeckEffect(action, playerId, 0, Integer.MAX_VALUE, false));
            // Since the user interface will only show a pop-up for Reserve Deck if there are multiple actions,
            // put this action in the list twice. The user interface will only actually show the first item in the popup.
            actions.add(action);
            if (actions.size() == 1) {
                actions.add(action);
            }
        }
        return actions;
    }

    /**
     * Gets the top-level actions that can be performed by the specified player.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the actions
     */
    @Override
    public List<Action> getTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        List<Action> actions = new LinkedList<Action>();

        // Add 'Dejarik Rules' actions
        actions.addAll(getTopLevelDejarikRulesActions(playerId, game, self));

        return actions;
    }

    /**
     * Gets the top-level actions that can be performed by the specified player during an Attack Run.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the actions
     */
    @Override
    public List<Action> getTopLevelAttackRunActions(String playerId, SwccgGame game, PhysicalCard self) {
        return new LinkedList<Action>();
    }

    /**
     * Gets the top-level 'Dejarik Rules' actions.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the actions
     */
    private List<Action> getTopLevelDejarikRulesActions(String playerId, SwccgGame game, PhysicalCard self) {
        List<Action> actions = new LinkedList<Action>();

        // Deploy card using 'Dejarik Rules'
        if (GameConditions.isPhaseForPlayer(game, Phase.DEPLOY, playerId)) {
            Action deployDejarikAction = getDeployDejarikAction(playerId, game, self, self, false);
            if (deployDejarikAction != null) {
                actions.add(deployDejarikAction);
            }
        }

        // Return card to hand using 'Dejarik Rules'
        if (self.isDejarikHologramAtHolosite()
                && !GameConditions.isDuringBattle(game)
                && !GameConditions.isDuringAttack(game)
                && GameConditions.controls(game, playerId, self.getAtLocation())) {
            actions.add(new ReturnCardToHandAction(self, playerId));
        }

        return actions;
    }

    /**
     * Gets the top-level actions for opponent's card that can be performed by the specified player.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the actions
     */
    @Override
    public List<Action> getOpponentsCardTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        return Collections.emptyList();
    }

    /**
     * Gets the required "before" triggers for the specified effect if the card is 'outside of deck' during start of game.
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public List<TriggerAction> getRequiredOutsideOfDeckBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self) {
        return Collections.emptyList();
    }

    /**
     * Gets the required "before" triggers when the specified Interrupt itself is being played.
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public List<TriggerAction> getRequiredInterruptPlayedTriggers(SwccgGame game, Effect effect, PhysicalCard self) {
        return Collections.emptyList();
    }

    /**
     * Gets the required "before" triggers for the specified effect.
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public List<TriggerAction> getRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self) {
        return Collections.emptyList();
    }

    /**
     * Gets the optional "before" triggers for the specified effect that can be performed by the specified player.
     * @param playerId the player
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public List<TriggerAction> getOptionalBeforeTriggers(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        return Collections.emptyList();
    }

    @Override
    public List<TriggerAction> getOpponentsCardOptionalBeforeTriggers(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        return Collections.emptyList();
    }

    /**
     * Gets the optional "before" actions for the specified effect that can be performed by the specified player. This includes
     * actions like playing the card from hand.
     * @param playerId the player
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public List<Action> getOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        return Collections.emptyList();
    }

    /**
     * Gets the required "after" triggers for the specified effect result if the card is 'outside of deck' during start of game.
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public List<TriggerAction> getRequiredOutsideOfDeckAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        return Collections.emptyList();
    }

    /**
     * Gets the required "after" triggers for the specified effect result.
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        return Collections.emptyList();
    }

    /**
     * Gets the required "after" triggers when the specified card is drawn for destiny.
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public List<TriggerAction> getRequiredDrawnAsDestinyTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        return Collections.emptyList();
    }

    /**
     * Gets the optional "after" triggers when the specified card is drawn for destiny that can be performed by the specified player.
     * @param playerId the player
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public List<TriggerAction> getOptionalDrawnAsDestinyTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        return Collections.emptyList();
    }

    /**
     * Gets the optional "after" triggers for the specified effect result that can be performed by the specified player.
     * @param playerId the player
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public List<TriggerAction> getOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        return Collections.emptyList();
    }

    @Override
    public List<TriggerAction> getOpponentsCardOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        return Collections.emptyList();
    }

    /**
     * Gets the deploy other cards as 'react' action for the card if it can deploy other cards as a 'react'.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    protected List<TriggerAction> getDeployOtherCardsAsReactAction(final String playerId, SwccgGame game, PhysicalCard self) {

        List<TriggerAction> reactTriggerActions = new LinkedList<>();

        final List<ReactActionOption> reactActionOptions = game.getModifiersQuerying().getDeployOtherCardsAsReactOption(playerId, game.getGameState(), self);
        for (ReactActionOption reactActionOption : reactActionOptions) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, self.getCardId(), GameTextActionId.OTHER_CARD_ACTION_REACT_DEPLOY_OTHER_CARDS);
            action.setRepeatableTrigger(true);
            action.setText(reactActionOption.getActionText());
            // Update usage limit(s)
            if (self.getBlueprint().getCardCategory() == CardCategory.DEVICE) {
                action.appendUsage(
                        new UseDeviceEffect(action, self));
            }

            final ReactActionOption finalReactActionOption = reactActionOption;
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardFromHandOrDeployableAsIfFromHandEffect(action, playerId, reactActionOption.getCardToReactFilter()) {
                        @Override
                        protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
                            // Perform result(s)
                            Action deployAsReactAction = selectedCard.getBlueprint().getDeployAsReactAction(playerId, game,
                                    selectedCard, finalReactActionOption, finalReactActionOption.getTargetFilter());
                            if (deployAsReactAction != null) {
                                action.appendEffect(
                                        new StackActionEffect(action, deployAsReactAction));
                            }
                        }

                        @Override
                        public String getChoiceText(int numCardsToChoose) {
                            return "Choose card to deploy as a 'react'";
                        }
                    }
            );
            reactTriggerActions.add(action);
        }

        return reactTriggerActions;
    }

    /**
     * Gets the move other cards as 'react' action for the card if it can allow other cards to move as a 'react'.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    protected TriggerAction getMoveOtherCardsAsReactAction(final String playerId, final SwccgGame game, PhysicalCard self) {

        final ReactActionOption reactActionOption = game.getModifiersQuerying().getMoveOtherCardsAsReactOption(playerId, game.getGameState(), self);
        if (reactActionOption != null) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, self.getCardId(), GameTextActionId.OTHER_CARD_ACTION_REACT_MOVE_OTHER_CARDS);
            action.setRepeatableTrigger(true);
            action.setText(reactActionOption.getActionText());
            // Update usage limit(s)
            if (self.getBlueprint().getCardCategory() == CardCategory.DEVICE) {
                action.appendUsage(
                        new UseDeviceEffect(action, self));
            }
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose card to move as a 'react'", reactActionOption.getCardToReactFilter()) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            // Perform result(s)
                            Action moveAsReactAction = selectedCard.getBlueprint().getMoveAsReactAction(playerId, game,
                                    selectedCard, reactActionOption, reactActionOption.getTargetFilter());
                            if (moveAsReactAction != null) {
                                action.appendEffect(
                                        new StackActionEffect(action, moveAsReactAction));
                            }
                        }
                    }
            );
            return action;
        }

        return null;
    }

    /**
     * Gets the move other cards away as 'react' action for the card if it can allow other cards to move away as a 'react'.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    protected TriggerAction getMoveOtherCardsAwayAsReactAction(final String playerId, final SwccgGame game, PhysicalCard self) {

        final ReactActionOption reactActionOption = game.getModifiersQuerying().getMoveOtherCardsAwayAsReactOption(playerId, game.getGameState(), self);
        if (reactActionOption != null) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, self.getCardId(), GameTextActionId.OTHER_CARD_ACTION_REACT_MOVE_AWAY_OTHER_CARDS);
            action.setRepeatableTrigger(true);
            action.setText(reactActionOption.getActionText());
            // Update usage limit(s)
            if (self.getBlueprint().getCardCategory() == CardCategory.DEVICE) {
                action.appendUsage(
                        new UseDeviceEffect(action, self));
            }
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose card to move away as a 'react'", reactActionOption.getCardToReactFilter()) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            // Perform result(s)
                            Action moveAsReactAction = selectedCard.getBlueprint().getMoveAsReactAction(playerId, game,
                                    selectedCard, reactActionOption, reactActionOption.getTargetFilter());
                            if (moveAsReactAction != null) {
                                action.appendEffect(
                                        new StackActionEffect(action, moveAsReactAction));
                            }
                        }
                    }
            );
            return action;
        }

        return null;
    }

    /**
     * Gets the optional "after" actions for the specified effect result that can be performed by the specified player.
     * This includes actions like playing the card from hand.
     * @param playerId the player
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public List<Action> getOptionalAfterActions(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        return Collections.emptyList();
    }

    /**
     * Gets the required "after" triggers from a card when that card is 'blown away'.
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public List<TriggerAction> getBlownAwayRequiredTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        return null;
    }

    /**
     * Gets the required triggers from a card when that card leaves table.
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public List<TriggerAction> getLeavesTableRequiredTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        return null;
    }

    /**
     * Gets the optional triggers from a card when that card leaves table.
     * @param playerId the owner of the card
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public List<TriggerAction> getLeavesTableOptionalTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        return null;
    }

    /**
     * Gets the optional triggers from an opponent's card when that card leaves table.
     * @param playerId the owner of the card
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public List<TriggerAction> getOpponentsCardLeavesTableOptionalTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        return null;
    }

    /**
     * Gets the optional triggers from an opponent's card when that card leaves table.
     * @param playerId the owner of the card
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public List<TriggerAction> getLostFromLifeForceOptionalTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        return null;
    }

    /**
     * Gets the optional triggers from a card when that card is lost from life force.
     * @param playerId the owner of the card
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public List<TriggerAction> getOpponentsCardLostFromLifeForceOptionalTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        return null;
    }

    /**
     * Gets modifiers that are from this card that are in effect while the card is active in play.
     * @param game the game
     * @param self the card
     * @return the modifiers
     */
    @Override
    public List<Modifier> getWhileInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.emptyList();
    }

    /**
     * Gets modifiers that are from this card that are in effect while the card is out of play.
     *
     * @param game the game
     * @param self the card
     * @return the modifiers
     */
    @Override
    public List<Modifier> getWhileOutOfPlayModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.emptyList();
    }

    /**
     * Gets modifiers that are from this card that are in effect while the card is stacked (face up) on another card.
     * @param game the game
     * @param self the card
     * @return the modifiers
     */
    @Override
    public List<Modifier> getWhileStackedModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.emptyList();
    }

    /**
     * Gets modifiers to the card itself that are always in effect.
     * @param game the game
     * @param self the card
     * @return the modifiers
     */
    @Override
    public List<Modifier> getAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.emptyList();
    }

    /**
     * Gets a filter for the cards that are valid duel participants in duels initiated by this card.
     * @param side the side of the Force
     * @param game the game
     * @param self the card
     * @return the filter
     */
    @Override
    public Filter getValidDuelParticipant(Side side, SwccgGame game, final PhysicalCard self) {
        throw new UnsupportedOperationException("This method, getValidDuelParticipant(), should not be called on this card: " + _title);
    }

    /**
     * Gets the map of cards that can participate against each other in an epic duel initiated by this card.
     * @param game the game
     * @param self the card
     * @param darkSideParticipantFilter the filter for dark side participants
     * @param lightSideParticipantFilter the filter for light side participants
     * @return a map of duel pairings, with the key as a dark side character, and the values as light side characters that
     * can be dueled by that character.
     */
    @Override
    public Map<PhysicalCard, Collection<PhysicalCard>> getInitiateEpicDuelMatchup(SwccgGame game, PhysicalCard self, Filter darkSideParticipantFilter, Filter lightSideParticipantFilter) {
        throw new UnsupportedOperationException("This method, getInitiateEpicDuelMatchup(), should not be called on this card: " + _title);
    }

    /**
     * Gets duel directions provided by this card.
     * @param game the game
     * @return the duel directions provided by this card
     */
    @Override
    public DuelDirections getDuelDirections(SwccgGame game) {
        throw new UnsupportedOperationException("This method, getDuelDirections(), should not be called on this card: " + _title);
    }

    @Override
    public boolean isLegacy() {
        return _isLegacy;
    }

    /**
     * Sets the card as a legacy card
     * @param value true if this is a legacy card, otherwise false
     */
    public void setAsLegacy(boolean value) {
        _isLegacy = value;
    }

    @Override
    public boolean excludeFromDeckBuilder() {
        return _excludeFromDeckBuilder;
    }

    @Override
    public boolean isHorizontal() {
        return _isHorizontal;
    }

    public void setAsHorizontal(boolean isHorizontal) {
        _isHorizontal = isHorizontal;
    }

    @Override
    public ExpansionSet getExpansionSet() {
        return _expansionSet;
    }

    @Override
    public Rarity getRarity() {
        return _rarity;
    }

    /**
     * Hides the card from the deck builder
     */
    public void hideFromDeckBuilder() {
        _excludeFromDeckBuilder = true;
    }

    public boolean playableAsStartingInterrupt(SwccgGame game, PhysicalCard self) {
        return (getCardCategory() == CardCategory.INTERRUPT
                && (getCardSubtype() == CardSubtype.STARTING
                    || getCardSubtype() == CardSubtype.USED_OR_STARTING
                    || getCardSubtype() == CardSubtype.LOST_OR_STARTING));
    }
}
