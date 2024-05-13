package com.gempukku.swccgo.logic.decisions;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;

import java.util.*;

/**
 * A decision that involves choosing a card type on the User Interface.
 */
public abstract class CardTypeAwaitingDecision extends MultipleChoiceAwaitingDecision {
    private String[] _cardTypes;
    private Map<String, CardType> _cardTypeMap;

    /**
     * Creates a decision that involves choosing a card type on the User Interface.
     * @param game the game
     * @param text the text to show the player making the decision
     */
    public CardTypeAwaitingDecision(SwccgGame game, String text) {
        super(text, (String[]) null);

        // Get all the possible card types
        Set<CardType> cardTypeSet = new HashSet<CardType>();

        // Get all the possible card types for the game format
        List<SwccgCardBlueprint> blueprints = game.getFormat().getAllCardBlueprintsValidInFormat();
        for (SwccgCardBlueprint blueprint : blueprints) {
            for (CardType cardType : blueprint.getCardTypes()) {
                cardTypeSet.add(cardType);
            }
        }
        _cardTypeMap = new HashMap<String, CardType>();
        for (CardType cardType : cardTypeSet) {
            _cardTypeMap.put(cardType.getHumanReadable(), cardType);
        }
        List<String> cardTypeList = new ArrayList<String>(_cardTypeMap.keySet());
        Collections.sort(cardTypeList);
        _cardTypes = new String[cardTypeList.size()];
        cardTypeList.toArray(_cardTypes);
        setPossibleResults(_cardTypes);
    }

    @Override
    protected final void validDecisionMade(int index, String result) {
        cardTypeChosen(_cardTypeMap.get(_cardTypes[index]));
    }

    /**
     * This method is called when the card type is chosen.
     * @param cardType the chosen card type
     */
    protected abstract void cardTypeChosen(CardType cardType);
}
