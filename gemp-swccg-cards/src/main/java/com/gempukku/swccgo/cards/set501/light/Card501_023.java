package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.StackCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Character
 * Subtype: Droid
 * Title: L3-37 (El-tree Tree-seven)
 */
public class Card501_023 extends AbstractDroid {
    public Card501_023() {
        super(Side.LIGHT, 3, 2, 2, 4, " L3-37 (El-tree Tree-seven)", Uniqueness.UNIQUE);
        setArmor(4);
        setLore("Female Smuggler.");
        setGameText("While aboard a starship (or if 'stacked' on it), adds 1 to power, maneuver, hyperspeed, and immunity to attrition, and if she is about to be lost, may 'stack' on that starship. During battle against a droid, may add its printed power to your total power.");
        addIcons(Icon.PILOT, Icon.NAV_COMPUTER, Icon.VIRTUAL_SET_13);
        addKeywords(Keyword.SMUGGLER, Keyword.FEMALE);
        addModelTypes(ModelType.CUSTOM_PILOT_DROID);
        setTestingText("L3-37 (El-tree Tree-seven)");
    }

    @Override
    protected List<Modifier> getGameTextWhileStackedModifiers(SwccgGame game, PhysicalCard self) {
        Filter starshipStackedOn = Filters.and(Filters.starship, Filters.hasStacked(self));

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, starshipStackedOn, 1));
        modifiers.add(new ManeuverModifier(self, starshipStackedOn, 1));
        modifiers.add(new HyperspeedModifier(self, starshipStackedOn, 1));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, starshipStackedOn, 1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter starshipAboard = Filters.and(Filters.starship, Filters.hasAboard(self));

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, starshipAboard, 1));
        modifiers.add(new ManeuverModifier(self, starshipAboard, 1));
        modifiers.add(new HyperspeedModifier(self, starshipAboard, 1));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, starshipAboard, 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if ((TriggerConditions.isAboutToBeLost(game, effectResult, self)
                || TriggerConditions.isAboutToBeForfeitedToLostPile(game, effectResult, self))
                && GameConditions.isAboardAnyStarship(game, self)) {
            final AboutToLeaveTableResult result = (AboutToLeaveTableResult) effectResult;
            final PhysicalCard cardToBeLost = result.getCardAboutToLeaveTable();
            final PhysicalCard starship = Filters.findFirstActive(game, self, Filters.hasAboard(self));
            if (starship != null) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Stack on " + GameUtils.getCardLink(starship));
                action.setActionMsg("Stack on " + GameUtils.getCardLink(starship));
                action.appendEffect(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                result.getPreventableCardEffect().preventEffectOnCard(cardToBeLost);
                                action.appendEffect(
                                        new StackCardFromTableEffect(action, cardToBeLost, starship));
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.opponents(playerId), Filters.droid))
                && GameConditions.isInBattle(game, self)
                && GameConditions.isOncePerBattle(game, self, gameTextSourceCardId)
                && GameConditions.canTarget(game, self, Filters.and(Filters.opponents(playerId), Filters.droid))) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Add a droid's power to your total power");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose a droid", Filters.and(Filters.opponents(playerId), Filters.droid)) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.allowResponses(new UnrespondableEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    action.appendEffect(
                                            new AddUntilEndOfBattleModifierEffect(action,
                                                    new TotalPowerModifier(self, Filters.battleLocation, targetedCard.getBlueprint().getPower(), playerId),
                                                    "Add " + GameUtils.getCardLink(targetedCard) + "'s power of " + targetedCard.getBlueprint().getPower() + " to your total power.")
                                    );
                                }
                            });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
