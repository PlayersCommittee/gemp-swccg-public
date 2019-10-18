package com.gempukku.swccgo.logic.decisions;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;

import java.util.*;

/**
 * A decision that involves choosing a card title on the User Interface.
 */
public abstract class CardTitleAwaitingDecision extends MultipleChoiceAwaitingDecision {
    private String[] _cardTitles;

    /**
     * Creates a decision that involves choosing a card title on the User Interface.
     * @param game the game
     * @param text the text to show the player making the decision
     * @param cardCategoryFilter the card category to include, or null if all card categories included
     */
    public CardTitleAwaitingDecision(SwccgGame game, String text, CardCategory cardCategoryFilter) {
        super(text, (String[]) null);

        Set<String> cardTitleSet = new HashSet<String>();

        // Get all the possible card titles for the game format
        List<SwccgCardBlueprint> blueprints = game.getFormat().getAllCardBlueprintsValidInFormat();
        for (SwccgCardBlueprint blueprint : blueprints) {
            if (cardCategoryFilter != null && blueprint.getCardCategory() != cardCategoryFilter)
                continue;

            cardTitleSet.addAll(blueprint.getTitles());
        }

        List<String> cardTitleList = new ArrayList<String>(cardTitleSet);
        Collections.sort(cardTitleList);
        _cardTitles = new String[cardTitleList.size()];
        cardTitleList.toArray(_cardTitles);
        setPossibleResults(_cardTitles);
    }

    @Override
    protected final void validDecisionMade(int index, String result) {
        cardTitleChosen(_cardTitles[index]);
    }

    /**
     * This method is called when the card title is chosen.
     * @param cardTitle the chosen card title
     */
    protected abstract void cardTitleChosen(String cardTitle);
}
