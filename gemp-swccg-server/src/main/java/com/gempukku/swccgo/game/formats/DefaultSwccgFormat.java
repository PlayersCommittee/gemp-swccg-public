package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.cards.packs.RarityReader;
import com.gempukku.swccgo.cards.packs.SetRarity;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.vo.SwccgDeck;

import java.util.*;

public class DefaultSwccgFormat implements SwccgFormat {
    private SwccgCardBlueprintLibrary _library;
    private String _name;
    private boolean _downloadBattlegroundRule;
    private boolean _jpSealedRule;
    private boolean _playtesting;
    private int _requiredDeckSize = 60;
    private int _defaultGameTimerMinutes = 60;
    private List<String> _bannedIcons = new ArrayList<String>();
    private List<String> _bannedCards = new ArrayList<String>();
    private List<String> _restrictedCards = new ArrayList<String>();
    private List<String> _validCards = new ArrayList<String>();
    private List<Integer> _validSets = new ArrayList<Integer>();
    private List<String> _bannedRarity = new ArrayList<String>();
    private Map<Integer, SetRarity> _rarity = new HashMap<Integer, SetRarity>();
    private List<SwccgCardBlueprint> _allCardBlueprints;
    private String _tenetsLink;

    public DefaultSwccgFormat(SwccgCardBlueprintLibrary library, String name, boolean downloadBattlegroundRule, boolean jpSealedRule, boolean playtesting) {
        _library = library;
        _name = name;
        _downloadBattlegroundRule = downloadBattlegroundRule;
        _jpSealedRule = jpSealedRule;
        _playtesting = playtesting;

        RarityReader rarityReader = new RarityReader();
        for (int i = 1; i < (1 + CardCounts.FULL_SETS_CARD_COUNTS.length); i++) {
            _rarity.put(i, rarityReader.getSetRarity(String.valueOf(i)));
        }
        for (int i = 101; i < (101 + CardCounts.PREMIUM_SETS_CARD_COUNTS.length); i++) {
            _rarity.put(i, rarityReader.getSetRarity(String.valueOf(i)));
        }
        for (int i = 200; i < (200 + CardCounts.VIRTUAL_SETS_CARD_COUNTS.length); i++) {
            _rarity.put(i, rarityReader.getSetRarity(String.valueOf(i)));
        }
        for (int i = 301; i < (301 + CardCounts.VIRTUAL_PREMIUM_SETS_CARD_COUNTS.length); i++) {
            _rarity.put(i, rarityReader.getSetRarity(String.valueOf(i)));
        }
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public boolean hasDownloadBattlegroundRule() {
        return _downloadBattlegroundRule;
    }

    @Override
    public boolean hasJpSealedRule() {
        return _jpSealedRule;
    }

    @Override
    public boolean isPlaytesting() {
        return _playtesting;
    }

    @Override
    public List<Integer> getValidSets() {
        return Collections.unmodifiableList(_validSets);
    }

    @Override
    public List<String> getBannedCards() {
        return Collections.unmodifiableList(_bannedCards);
    }

    @Override
    public List<String> getBannedIcons() {
        return Collections.unmodifiableList(_bannedIcons);
    }

    @Override
    public List<String> getBannedRarities() {
        return Collections.unmodifiableList(_bannedRarity);
    }

    @Override
    public List<String> getRestrictedCards() {
        return Collections.unmodifiableList(_restrictedCards);
    }

    @Override
    public List<String> getValidCards() {
        return Collections.unmodifiableList(_validCards);
    }

    @Override
    public int getRequiredDeckSize() {
        return _requiredDeckSize;
    }

    protected void setRequiredDeckSize(int requiredDeckSize) {
        _requiredDeckSize = requiredDeckSize;
    }

    @Override
    public int getDefaultGameTimerMinutes() {
        return _defaultGameTimerMinutes;
    }

    protected void setDefaultGameTimerMinutes(int defaultGameTimerMinutes) {
        _defaultGameTimerMinutes = defaultGameTimerMinutes;
    }



    protected void addBannedCard(String baseBlueprintId) {
        if (baseBlueprintId.contains("-")) {
            String[] parts = baseBlueprintId.split("_");
            String set = parts[0];
            int from = Integer.parseInt(parts[1].split("-")[0]);
            int to = Integer.parseInt(parts[1].split("-")[1]);
            for (int i = from; i <= to; i++)
                _bannedCards.add(set + "_" + i);
        } else
            _bannedCards.add(baseBlueprintId);
    }

    public void addTenetsLink(String tenetsLink) {
        _tenetsLink = tenetsLink;
    }

    public String getTenetsLink() {
        return _tenetsLink;
    }

    protected void addRestrictedCard(String baseBlueprintId) {
        if (baseBlueprintId.contains("-")) {
            String[] parts = baseBlueprintId.split("_");
            String set = parts[0];
            int from = Integer.parseInt(parts[1].split("-")[0]);
            int to = Integer.parseInt(parts[1].split("-")[1]);
            for (int i = from; i <= to; i++)
                _restrictedCards.add(set + "_" + i);
        } else
            _restrictedCards.add(baseBlueprintId);
    }

    protected void addValidCard(String baseBlueprintId) {
        if (baseBlueprintId.contains("-")) {
            String[] parts = baseBlueprintId.split("_");
            String set = parts[0];
            int from = Integer.parseInt(parts[1].split("-")[0]);
            int to = Integer.parseInt(parts[1].split("-")[1]);
            for (int i = from; i <= to; i++)
                _validCards.add(set + "_" + i);
        } else
            _validCards.add(baseBlueprintId);
    }

    protected void addBannedIcon(String iconString) {
        Icon icon = Icon.getIconFromName(iconString);
        if (icon != null) {
            _bannedIcons.add(iconString);
        }
    }

    protected void addBannedRarity(String bannedRarity) {
        Rarity rarity = Rarity.getRarityFromString(bannedRarity);
        if (rarity != null) {
            _bannedRarity.add(bannedRarity);
        }

    }

    protected void addValidSet(int setNo) {
        _validSets.add(setNo);
    }

    @Override
    public void validateCard(String blueprintId, boolean skipIfNotExists) throws DeckInvalidException {
        blueprintId = _library.getBaseBlueprintId(blueprintId);
        SwccgCardBlueprint blueprint = _library.getSwccgoCardBlueprint(blueprintId);
        if (blueprint == null) {
            if (skipIfNotExists)
                return;

            throw new DeckInvalidException("<span class=\"validate-invalid-card\">" + blueprintId + "</span> card not permitted in this format.");
        }

        String fullName = GameUtils.getFullName(_library.getSwccgoCardBlueprint(blueprintId));

        if (blueprint.getCardCategory() == CardCategory.GAME_AID)
            throw new DeckInvalidException("Deck contains card type, from card <span class=\"validate-invalid-card-type\">" + fullName + "</span>, not permitted in this format.");

        if (_validCards.contains(blueprintId))
            return;

        if (!_validSets.isEmpty() && !isValidInSets(blueprintId))
            throw new DeckInvalidException("Deck contains card, <span class=\"validate-invalid-card-set\">" + fullName + "</span>, from set banned in this format.");

        // Banned icons
        for (String iconName : _bannedIcons) {
            Icon icon = Icon.getIconFromName(iconName);
            if (icon != null && blueprint.hasIcon(icon))
                throw new DeckInvalidException("<span class=\"validate-invalid-icon\">" + icon.getHumanReadable() + "</span> icon, from card <span class=\"validate-invalid-icon-cardname\">"+fullName+"</span> not permitted in this format.");
        }

        // Banned rarity
        for (String rarityName: _bannedRarity) {
            Rarity rarity = Rarity.getRarityFromString(rarityName);
            for (int validSet : _validSets)
                if (blueprintId.startsWith(validSet + "_")
                        || _library.hasAlternateInSet(blueprintId, validSet)) {
                    SetRarity setRarity = _rarity.get(validSet);
                    if (setRarity.getCardRarity(blueprintId).equals(rarity))
                        throw new DeckInvalidException("Deck contains a card with a banned rarity: <span class=\"validate-invalid-rarity\">" + fullName + "</span>");
                }
        }

        // Banned cards
        Set<String> allAlternates = _library.getAllAlternates(blueprintId);
        for (String bannedBlueprintId : _bannedCards) {
            if (bannedBlueprintId.equals(blueprintId) || (allAlternates != null && allAlternates.contains(bannedBlueprintId)))
                throw new DeckInvalidException("Deck contains a copy of banned card: <span class=\"validate-invalid-card\">" + fullName + "</span>");
        }
    }

    private boolean isValidInSets(String blueprintId) throws DeckInvalidException {
        for (int validSet : _validSets)
            if (blueprintId.startsWith(validSet + "_")
                    || _library.hasAlternateInSet(blueprintId, validSet))
                return true;
        return false;
    }

    @Override
    public void validateDeck(SwccgDeck deck) throws DeckInvalidException {
        try {
            // Deck
            int dark = 0;
            int light = 0;
            int numObjectives = 0;
            int numStartingEffects = 0;
            int numFlipFalcons = 0;
            int numMythrol = 0;
            int numJabbasPrize = 0;
            int numCCT = 0;

            for (String blueprintId : deck.getCards()) {
                SwccgCardBlueprint card = _library.getSwccgoCardBlueprint(blueprintId);
                if (card.getSide() == Side.DARK)
                    dark++;
                else if (card.getSide() == Side.LIGHT)
                    light++;
                else
                    throw new DeckInvalidException("Deck contains a card that is neither Dark nor Light.");

                if (card.getCardCategory()== CardCategory.OBJECTIVE)
                    numObjectives++;

                if (card.getCardCategory()== CardCategory.EFFECT && card.getCardSubtype()== CardSubtype.STARTING)
                    numStartingEffects++;

                if(Title.The_Falcon_Junkyard_Garbage.equals(card.getTitle()))
                    numFlipFalcons++;

                if(Title.The_Mythrol.equals(card.getTitle()))
                    numMythrol++;

                if (Title.Jabbas_Prize.equals(card.getTitle()))
                    numJabbasPrize++;

                if (Title.Carbon_Chamber_Testing.equals(card.getTitle()))
                    numCCT++;

                if (card.isDoesNotCountTowardDeckLimit())
                    throw new DeckInvalidException("Deck contains card that does not count towards deck limit");
            }
            for (String blueprintId : deck.getCardsOutsideDeck()) {
                SwccgCardBlueprint card = _library.getSwccgoCardBlueprint(blueprintId);
                if (card.getSide() == Side.DARK)
                    dark++;
                else if (card.getSide() == Side.LIGHT)
                    light++;
                else
                    throw new DeckInvalidException("Deck contains a card that is neither Dark nor Light.");
            }
            if (light > 0 && dark > 0)
                throw new DeckInvalidException("Deck contains both light side and dark side cards");

            if (numObjectives > 1)
                throw new DeckInvalidException("Deck contains more than one Objective");

            if (numStartingEffects > 1)
                throw new DeckInvalidException("Deck contains more than one Starting Effect");

            if (numFlipFalcons > 1)
                throw new DeckInvalidException("Deck contains more than one The Falcon, Junkyard Garbage");

            if (numMythrol > 1)
                throw new DeckInvalidException("Deck contains more than one of The Mythrol");

            if (numJabbasPrize > 1)
                throw new DeckInvalidException("Deck contains more than one Jabba's Prize");

            if (numJabbasPrize == 1 && numCCT == 0)
                throw new DeckInvalidException("Deck contains Jabba's Prize but not Carbon Chamber Testing");

            if (deck.getCardsOutsideDeck().size() > 50)
                throw new DeckInvalidException("Deck specifies more than 50 cards as 'outside of deck'");

            // Verify that all cards are valid
            for (String card : deck.getCards())
                validateCard(card, false);

            for (String card : deck.getCardsOutsideDeck())
                validateCard(card, false);

            // Card count in deck
            Map<String, Integer> cardCountByName = new HashMap<String, Integer>();
            Map<String, Integer> cardCountByBaseBlueprintId = new HashMap<String, Integer>();

            for (String blueprintId : deck.getCards())
                processCardCounts(blueprintId, cardCountByName, cardCountByBaseBlueprintId);

            // Restricted cards
            for (String blueprintId : _restrictedCards) {
                Integer count = cardCountByBaseBlueprintId.get(blueprintId);
                if (count != null && count > 1)
                    throw new DeckInvalidException("Deck contains more than one copy of an restricted card: " + GameUtils.getFullName(_library.getSwccgoCardBlueprint(blueprintId)));
            }

            if (deck.getCards().size() != _requiredDeckSize)
                throw new DeckInvalidException("Deck contains <span class=\"validate-deck-size\">" + deck.getCards().size() + "</span> cards, however <span class=\"validate-required-deck-size\">" + _requiredDeckSize + "</span> cards are required.");

        } catch (IllegalArgumentException exp) {
            throw new DeckInvalidException("Deck contains unrecognizable card");
        }
    }


    @Override
    public List<SwccgCardBlueprint> getAllCardBlueprintsValidInFormat() {
        if (_allCardBlueprints != null) {
            return _allCardBlueprints;
        }
        _allCardBlueprints = new ArrayList<SwccgCardBlueprint>();

        // Add cards valid for the format
        addCardsToDefaultCollection(_library, CardCounts.FULL_SETS_CARD_COUNTS, 1, _allCardBlueprints);
        addCardsToDefaultCollection(_library, CardCounts.PREMIUM_SETS_CARD_COUNTS, 101, _allCardBlueprints);
        addCardsToDefaultCollection(_library, CardCounts.VIRTUAL_SETS_CARD_COUNTS, 200, _allCardBlueprints);
        addCardsToDefaultCollection(_library, CardCounts.VIRTUAL_PREMIUM_SETS_CARD_COUNTS, 301, _allCardBlueprints);
        addCardsToDefaultCollection(_library, CardCounts.DREAM_CARD_SETS_CARD_COUNTS, 401, _allCardBlueprints);
        addCardsToDefaultCollection(_library, CardCounts.PLAYTESTING_SETS_CARD_COUNTS, 501, _allCardBlueprints);

        return _allCardBlueprints;
    }

    /**
     * Adds all the cards that are valid for the format.
     * @param library the card blueprint library
     * @param cardSetCounts the counts of cards in each set
     * @param setIndexOffset the set number of the first array item in cardSetCounts
     * @param cardBlueprints the list of card blueprints valid for this format
     */
    private void addCardsToDefaultCollection(SwccgCardBlueprintLibrary library, int[] cardSetCounts, int setIndexOffset, List<SwccgCardBlueprint> cardBlueprints) {
        for (int i = 0; i < cardSetCounts.length; i++) {
            int setNum = setIndexOffset + i;
            for (int j = 1; j <= cardSetCounts[i]; j++) {
                String blueprintId = setNum + "_" + j;
                try {
                    validateCard(blueprintId, true);
                    SwccgCardBlueprint blueprint = library.getSwccgoCardBlueprint(blueprintId);
                    if (blueprint != null) {
                        cardBlueprints.add(blueprint);
                    }
                }
                catch (DeckInvalidException exp) {
                }
                catch (IllegalArgumentException exp) {
                }
            }
        }
    }

    private void processCardCounts(String blueprintId, Map<String, Integer> cardCountByName, Map<String, Integer> cardCountByBaseBlueprintId) {
        SwccgCardBlueprint cardBlueprint = _library.getSwccgoCardBlueprint(blueprintId);
        increaseCount(cardCountByName, cardBlueprint.getTitle());
        increaseCount(cardCountByBaseBlueprintId, _library.getBaseBlueprintId(blueprintId));
    }

    private void increaseCount(Map<String, Integer> counts, String name) {
        Integer count = counts.get(name);
        if (count == null) {
            counts.put(name, 1);
        } else {
            counts.put(name, count + 1);
        }
    }
}
