package com.gempukku.swccgo.cards.set303.dark;

import com.gempukku.swccgo.cards.AbstractDarkJediMasterImperial;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CaptureCharacterOnTableEffect;
import com.gempukku.swccgo.logic.effects.HitCardEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToBeHitResult;
import com.gempukku.swccgo.logic.timing.results.CaptureCharacterResult;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.HideFromBattleEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Shadow Academy
 * Type: Character
 * Subtype: Dark Jedi Master/Imperial
 * Title: Master Kamjin Lap'lamiz, Justicar
 */
public class Card303_021 extends AbstractDarkJediMasterImperial {
    public Card303_021() {
        super(Side.DARK, 6, 6, 6, 7, 8, "Master Kamjin Lap'lamiz, Justicar", Uniqueness.UNIQUE, ExpansionSet.SA, Rarity.R);
        setVirtualSuffix(true);
        setLore("Retreating from the spotlight as Emperor of Scholae Palatinae, Kamjin has assumed the role of Justicar within the Brotherhood. While early in his tenure he has made it a point of eliminating clones.");
        setGameText("Adds 3 to anything he pilots. May use 3 force to 'hide' (be excluded) from a battle. Characters about to be 'hit' by Kamjin may be captured instead. If Kamjin just seized a Councilor or Jedi opponent loses 2 Force.");
        addPersona(Persona.KAMJIN);
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.DARK_COUNCILOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isAboutToBeHitBy(game, effectResult, Filters.and(Filters.character, Filters.canBeTargetedBy(self, TargetingReason.TO_BE_CAPTURED)), self)) {
            AboutToBeHitResult aboutToBeHitResult = (AboutToBeHitResult) effectResult;
            PhysicalCard cardToBeHit = aboutToBeHitResult.getCardToBeHit();

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Capture " + GameUtils.getFullName(cardToBeHit) + " instead");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", SpotOverride.INCLUDE_ALL, TargetingReason.TO_BE_CAPTURED, cardToBeHit) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                            action.addAnimationGroup(cardTargeted);
                            // Allow response(s)
                            action.allowResponses("Capture " + GameUtils.getCardLink(cardTargeted) + " instead",
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalCharacter = targetingAction.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            ((AboutToBeHitResult) effectResult).getPreventableCardEffect().preventEffectOnCard(finalCharacter);
                                            action.appendEffect(
                                                    new CaptureCharacterOnTableEffect(action, finalCharacter));
                                        }
                                    });
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        final String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.seizedBy(game, effectResult, Filters.or(Filters.Jedi, Filters.Councilor), self)) {
            int numForce = 2;

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Make opponent lose " + numForce + " Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, numForce));
            return Collections.singletonList(action);
        }
        return null;
    }
	
	@Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        // Card action 1
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_3;

        // Check condition(s)
        if (GameConditions.isInBattle(game, self)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 3)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("'Force Cloak' from battle");
            action.setActionMsg("'Force Cloak' " + GameUtils.getCardLink(self) + " from battle");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 3));
            // Perform result(s)
            action.appendEffect(
                    new HideFromBattleEffect(action, self));
            actions.add(action);
        }
		return actions;
	}
}
