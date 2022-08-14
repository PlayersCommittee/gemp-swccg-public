package com.gempukku.swccgo.cards.set12.dark;

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
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.AboutToRetrieveForceResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Effect
 * Title: Imperial Arrest Order & Secret Plans
 */
public class Card12_135 extends AbstractNormalEffect {
    public Card12_135() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Imperial Arrest Order & Secret Plans", Uniqueness.UNIQUE);
        addComboCardTitles(Title.Imperial_Arrest_Order, Title.Secret_Plans);
        setGameText("Deploy on table. Unique (•) Imperials of ability < 3 are forfeit +1. Nabrun Leids and Elis Helrot are limited to owner's move phase and exterior sites only. Once during each of your deploy phases, may deploy one docking bay from Reserve Deck; reshuffle. When opponent retrieves X cards, opponent must first use X Force or that retrieval is canceled. (Immune to Alter.)");
        addIcons(Icon.CORUSCANT);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.unique, Filters.Imperial, Filters.abilityLessThan(3)), 1));
        modifiers.add(new ModifyGameTextModifier(self, Filters.or(Filters.Nabrun_Leids, Filters.Elis_Helrot), ModifyGameTextType.NABRUN_LEIDS_ELIS_HELROT__LIMIT_USAGE));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.IMPERIAL_ARREST_ORDER_SECRET_PLANS__DOWNLOAD_DOCKING_BAY;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy docking bay from Reserve Deck");
            action.setActionMsg("Deploy a docking bay from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.docking_bay, true));
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