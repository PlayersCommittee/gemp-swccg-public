package com.gempukku.swccgo.cards.set219.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnActionProxyEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.NoBattleDamageModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Character
 * Subtype: Alien
 * Title: Fennec Shand
 */
public class Card219_006 extends AbstractAlien {
    public Card219_006() {
        super(Side.DARK, 2, 3, 4, 2, 4, "Fennec Shand", Uniqueness.UNIQUE, ExpansionSet.SET_19, Rarity.V);
        setArmor(4);
        setLore("Female assassin, bounty hunter, and mercenary.");
        setGameText("If you just initiated battle here, battle damage against you here this turn is canceled. " +
                    "Once per turn, may target opponent's non-'hit' character here. If Fennec on table when target lost this turn, opponent loses 1 Force.");
        addPersona(Persona.FENNEC_SHAND);
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_19);
        addKeywords(Keyword.FEMALE, Keyword.ASSASSIN, Keyword.BOUNTY_HUNTER, Keyword.MERCENARY);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, playerId, Filters.here(self))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Prevent battle damage");
            // Perform result(s)
            action.appendEffect(
                    new AddUntilEndOfBattleModifierEffect(action, new NoBattleDamageModifier(self, Filters.here(self), playerId), "Battle damage against you this battle is canceled"));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        Filter opponentsNonHitCharacterHere = Filters.and(Filters.opponents(playerId), Filters.character, Filters.here(self), Filters.not(Filters.hit));
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTarget(game, self, opponentsNonHitCharacterHere)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Target character");
            action.appendUsage(
                    new OncePerTurnEffect(action)
            );
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Target character", opponentsNonHitCharacterHere) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.allowResponses("Target " + GameUtils.getCardLink(targetedCard),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new AddUntilEndOfTurnActionProxyEffect(action,
                                                            new AbstractActionProxy() {
                                                                @Override
                                                                public List<TriggerAction> getRequiredAfterTriggers(final SwccgGame game, final EffectResult effectResult) {
                                                                    List<TriggerAction> actions = new LinkedList<>();
                                                                    if (TriggerConditions.justLost(game, effectResult, targetedCard)
                                                                            && GameConditions.canSpot(game, self, Filters.Fennec_Shand)
                                                                            && GameConditions.isOncePerTurn(game, self, self.getCardId())) {
                                                                        final RequiredGameTextTriggerAction action2 = new RequiredGameTextTriggerAction(self, self.getCardId());
                                                                        action2.setSingletonTrigger(true);
                                                                        action2.appendUsage(new OncePerTurnEffect(action2));
                                                                        action2.appendEffect(
                                                                                new LoseForceEffect(action, game.getOpponent(playerId), 1));
                                                                        actions.add(action2);

                                                                    }
                                                                    return actions;
                                                                }
                                                            })
                                            );
                                        }
                                    }
                            );

                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
