package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: Corrosive Damage
 */
public class Card4_119 extends AbstractNormalEffect {
    public Card4_119() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, "Corrosive Damage", Uniqueness.DIAMOND_1);
        setLore("The interior or a space slug's maw is mildly acidic. Long-term exposure to this corrosive environment can cause considerable damage.");
        setGameText("Deploy on Space Slug Belly. At the end of each player's turn, for every character and starship that player has present, that player must lose 1 Force. Effect canceled if Space Slug lost.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Space_Slug_Belly;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        // Check condition(s)
        if (TriggerConditions.isEndOfEachTurn(game, effectResult)) {
            String currentPlayer = game.getGameState().getCurrentPlayerId();

            int count = Filters.countActive(game, self,
                    Filters.and(Filters.owner(currentPlayer),Filters.or(Filters.character, Filters.starship), Filters.present(self)));
            if (count > 0) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make " + currentPlayer + " lose " + count + " Force");
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, currentPlayer, count));
                actions.add(action);
            }
        }

        // Check condition(s)
        if (TriggerConditions.justLostFromLocation(game, effectResult, Filters.Space_Slug, Filters.relatedBigOne(self))
                && GameConditions.canBeCanceled(game, self)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel");
            action.setActionMsg("Cancel " + GameUtils.getCardLink(self));
            // Perform result(s)
            action.appendEffect(
                    new CancelCardOnTableEffect(action, self));
            actions.add(action);
        }
        return actions;
    }
}