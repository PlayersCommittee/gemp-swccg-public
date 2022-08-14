package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceRetrievalEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.AboutToRetrieveForceResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Effect
 * Title: Secret Plans
 */
public class Card7_240 extends AbstractNormalEffect {
    public Card7_240() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Secret_Plans, Uniqueness.UNIQUE);
        setLore("Imperial computer systems are equipped with complex algorithms designed to prevent access by unauthorized users.");
        setGameText("Deploy on your side of table. Once during each of your control phases, may take one Shocking Revelation into hand from Reserve Deck; reshuffle. Also, whenever opponent retrieves X cards, opponent must first use X Force or that retrieval is canceled. (Immune to Alter.)");
        addIcons(Icon.SPECIAL_EDITION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.SECRET_PLANS__UPLOAD_SHOCKING_REVELATION;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a Shocking Revelation into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Shocking_Revelation, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.isAboutToRetrieveForce(game, effectResult, opponent)) {
            AboutToRetrieveForceResult result = (AboutToRetrieveForceResult) effectResult;
            if (result.getSourceCard() == null || !game.getModifiersQuerying().isForceRetrievalImmuneToSecretPlans(game.getGameState(), result.getSourceCard())) {
                final float amountOfForce = game.getModifiersQuerying().getVariableValue(game.getGameState(), self, Variable.X, result.getAmountOfForceToRetrieve());

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Use Force or cancel retrieval");
                action.setActionMsg("Make " + opponent + " use " + GuiUtils.formatAsString(amountOfForce) + " Force or Force retrieval is cancel");
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