package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.*;

/**
 * The effect result that is emitted when cards are excluded from battle.
 */
public class ExcludedFromBattleResult extends EffectResult {
    private List<PhysicalCard> _allCardsExcluded = new ArrayList<PhysicalCard>();
    private List<PhysicalCard> _excludedByRule = new ArrayList<PhysicalCard>();
    private Map<PhysicalCard, PhysicalCard> _excludedByCard = new HashMap<PhysicalCard, PhysicalCard>();
    private SwccgBuiltInCardBlueprint _excludedByPermanentWeapon;
    private PhysicalCard _cardFiringWeapon;

    /**
     * Creates an effect result that is emitted when cards are excluded from battle by rule.
     * @param performingPlayerId the player performing the exclusion
     * @param cardsExcluded the cards excluded from battle by rule
     */
    public ExcludedFromBattleResult(String performingPlayerId, Collection<PhysicalCard> cardsExcluded) {
        super(Type.EXCLUDED_FROM_BATTLE, performingPlayerId);
        _excludedByRule.addAll(cardsExcluded);
        _allCardsExcluded.addAll(_excludedByRule);
    }

    /**
     * Creates an effect result that is emitted when cards are excluded from battle.
     * @param performingPlayerId the player performing the exclusion
     * @param sourceCard the cards excluding the cards from battle
     * @param cardsExcluded the cards excluded from battle
     * @param excludedByPermanentWeapon the permanent weapon that performed the excluding, or null
     * @param cardFiringWeapon the card that fired the weapon that performed the excluding, or null
     */
    public ExcludedFromBattleResult(String performingPlayerId, PhysicalCard sourceCard, List<PhysicalCard> cardsExcluded, SwccgBuiltInCardBlueprint excludedByPermanentWeapon, PhysicalCard cardFiringWeapon) {
        super(Type.EXCLUDED_FROM_BATTLE, performingPlayerId);
        for (PhysicalCard cardExcluded : cardsExcluded) {
            _excludedByCard.put(cardExcluded, sourceCard);
        }
        _allCardsExcluded.addAll(_excludedByCard.keySet());
        _excludedByPermanentWeapon = excludedByPermanentWeapon;
        _cardFiringWeapon = cardFiringWeapon;
    }

    /**
     * Creates an effect result that is emitted when cards are excluded from battle.
     * @param performingPlayerId the player performing the exclusion
     * @param cardsExcludedByCard the map of cards causing the exclusion from battle to the cards excluded from battle
     * @param excludedByPermanentWeapon the permanent weapon that performed the excluding, or null
     * @param cardFiringWeapon the card that fired the weapon that performed the excluding, or null
     */
    public ExcludedFromBattleResult(String performingPlayerId, Map<PhysicalCard, List<PhysicalCard>> cardsExcludedByCard, SwccgBuiltInCardBlueprint excludedByPermanentWeapon, PhysicalCard cardFiringWeapon) {
        super(Type.EXCLUDED_FROM_BATTLE, performingPlayerId);
        for (PhysicalCard sourceCard : cardsExcludedByCard.keySet()) {
            for (PhysicalCard cardExcluded : cardsExcludedByCard.get(sourceCard)) {
                _excludedByCard.put(cardExcluded, sourceCard);
            }
        }
        _allCardsExcluded.addAll(_excludedByCard.keySet());
        _excludedByPermanentWeapon = excludedByPermanentWeapon;
        _cardFiringWeapon = cardFiringWeapon;
    }

    /**
     * Gets the cards that were excluded from battle.
     * @return the cards that were excluded from battle
     */
    public Collection<PhysicalCard> getCardsExcluded() {
        return _allCardsExcluded;
    }

    /**
     * Gets the card that performed the excluding.
     * @param cardExcluded the card that was excluded
     * @return the card that performed the excluding
     */
    public PhysicalCard getExcludedByCard(PhysicalCard cardExcluded) {
        return _excludedByCard.get(cardExcluded);
    }

    /**
     * Gets the permanent weapon that performed the excluding, or null.
     * @return the permanent weapon that performed the excluding, or null
     */
    public SwccgBuiltInCardBlueprint getExcludedByPermanentWeapon() {
        return _excludedByPermanentWeapon;
    }

    /**
     * Gets the card that fired the weapon that performed the excluding, or null
     * @return the card that fired the weapon that performed the excluding, or null
     */
    public PhysicalCard getCardFiringWeapon() {
        return _cardFiringWeapon;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Excluded " + GameUtils.getAppendedNames(_allCardsExcluded) + " from battle";
    }
}
