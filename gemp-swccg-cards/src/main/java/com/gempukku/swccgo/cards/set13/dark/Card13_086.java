package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceRetrievalEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.AboutToRetrieveForceResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Defensive Shield
 * Title: Secret Plans
 */
public class Card13_086 extends AbstractDefensiveShield {
    public Card13_086() {
        super(Side.DARK, Title.Secret_Plans);
        setLore("Imperial computer systems are equipped with complex algorithms designed to prevent access by unauthorized users.");
        setGameText("Plays on table. If opponent is about to retrieve X Force, opponent must first use X Force or that retrieval is canceled. (X is equal to the full amount of Force allowed to be retrieved, regardless of the number of cards in opponent's Lost Pile.)");
        addIcons(Icon.REFLECTIONS_III);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.isAboutToRetrieveForce(game, effectResult, opponent)) {
            AboutToRetrieveForceResult result = (AboutToRetrieveForceResult) effectResult;
            if (result.getSourceCard() == null || !game.getModifiersQuerying().isForceRetrievalImmuneToSecretPlans(game.getGameState(), result.getSourceCard())) {
                final float amountOfForce = result.getAmountOfForceToRetrieve();

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Use Force or cancel retrieval");
                action.setActionMsg("Make " + opponent + " use " + GuiUtils.formatAsString(amountOfForce) + " Force or Force retrieval is canceled");
                if (GameConditions.canUseForce(game, opponent, amountOfForce)) {
                    // Ask player to Use Force or retrieval is canceled
                    action.appendEffect(
                            new PlayoutDecisionEffect(action, opponent,
                                    new YesNoDecision("Do you want to use " + GuiUtils.formatAsString(amountOfForce) + " Force to proceed with Force retrieval?") {
                                        @Override
                                        protected void yes() {
                                            action.appendEffect(
                                                    new UseForceEffect(action, opponent, amountOfForce));
                                        }

                                        @Override
                                        protected void no() {
                                            action.appendEffect(
                                                    new CancelForceRetrievalEffect(action));
                                        }
                                    }
                            )
                    );
                } else {
                    action.appendEffect(
                            new CancelForceRetrievalEffect(action));
                }
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}