package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringForceDrainAtCondition;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.cards.effects.usage.NumTimesPerForceDrainEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.ReactActionOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandOrDeployableAsIfFromHandEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotReactFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Droid
 * Title: IM4-099 (Eyeemmfour)
 */
public class Card7_181 extends AbstractDroid {
    public Card7_181() {
        super(Side.DARK, 4, 1, 0, 3, "IM4-099 (Eyeemmfour)", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.F);
        setManeuver(4);
        setLore("Imperial Mark IV Patrol Droid. Identifies criminal activity and transmits information to local authorities. Monitors random comm signals for illegal activity.");
        setGameText("Whenever opponent Force drains at same or adjacent site, may deploy up to four troopers there as a 'react'. When IM4 is present with your trooper, Rebels are deploy +2 to same site and opponent may not 'react' to or from same site.");
        addIcons(Icon.SPECIAL_EDITION);
        addModelType(ModelType.PATROL);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter sameOrAdjacentSite = Filters.sameOrAdjacentSite(self);
        Condition duringOpponentForceDrainAtSameOrAdjacent = new DuringForceDrainAtCondition(sameOrAdjacentSite);
        Condition presentWithYourTrooper = new PresentWithCondition(self, Filters.and(Filters.your(self), Filters.trooper));
        Filter sameSite = Filters.sameSite(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployOtherCardsAsReactToLocationModifier(self, "Deploy a trooper as a 'react'",
                duringOpponentForceDrainAtSameOrAdjacent, playerId, Filters.and(Filters.your(self), Filters.trooper), sameOrAdjacentSite));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Rebel, presentWithYourTrooper, 2, sameSite));
        modifiers.add(new MayNotReactToLocationModifier(self, sameSite, presentWithYourTrooper, opponent));
        modifiers.add(new MayNotReactFromLocationModifier(self, sameSite, presentWithYourTrooper, opponent));
        return modifiers;
    }

    /**
     * Caps the Force-drain trooper react at four deployments per Force drain.
     */
    @Override
    protected List<TriggerAction> getDeployOtherCardsAsReactAction(final String playerId, SwccgGame game, PhysicalCard self) {
        List<TriggerAction> reactTriggerActions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.IM4_099__DEPLOY_TROOPERS_AS_REACT;

        if (!GameConditions.isNumTimesPerForceDrain(game, self, 4, self.getCardId(), gameTextActionId)) {
            return reactTriggerActions;
        }

        final List<ReactActionOption> reactActionOptions = game.getModifiersQuerying().getDeployOtherCardsAsReactOption(playerId, game.getGameState(), self);
        for (ReactActionOption reactActionOption : reactActionOptions) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, self.getCardId(), gameTextActionId);
            action.setRepeatableTrigger(true);
            action.setText(reactActionOption.getActionText());
            action.appendUsage(new NumTimesPerForceDrainEffect(action, 4));

            final ReactActionOption finalReactActionOption = reactActionOption;
            action.appendTargeting(
                    new ChooseCardFromHandOrDeployableAsIfFromHandEffect(action, playerId, reactActionOption.getCardToReactFilter()) {
                        @Override
                        protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
                            Action deployAsReactAction = selectedCard.getBlueprint().getDeployAsReactAction(playerId, game,
                                    selectedCard, finalReactActionOption, finalReactActionOption.getTargetFilter());
                            if (deployAsReactAction != null) {
                                action.appendEffect(
                                        new StackActionEffect(action, deployAsReactAction));
                            }
                        }

                        @Override
                        public String getChoiceText(int numCardsToChoose) {
                            return "Choose trooper to deploy as a 'react'";
                        }
                    }
            );
            reactTriggerActions.add(action);
        }
        return reactTriggerActions;
    }
}