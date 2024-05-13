package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.TargetedByTractorBeamCondition;
import com.gempukku.swccgo.cards.conditions.TargetedByWeaponCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: I Want That Ship
 */
public class Card4_124 extends AbstractNormalEffect {
    public Card4_124() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.I_Want_That_Ship, Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.R);
        setLore("Like nerf herders herding nerfs, Imperial commanders often use TIE fighters to drive fleeing Rebel ships into tractor beam range.");
        setGameText("Deploy on one opponent's unique (•) starship at a location you do not occupy. When starship is targeted by any tractor beam or ion cannon, subtract 2 from maneuver. If starship is captured or lost, lose Effect and opponent must lose 3 Force (5 if Falcon).");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        String playerId = self.getOwner();
        return Filters.and(Filters.opponents(self), Filters.unique, Filters.starship, Filters.at(Filters.not(Filters.occupies(playerId))));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter starship = Filters.hasAttached(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ManeuverModifier(self, starship, new OrCondition(new TargetedByTractorBeamCondition(starship),
                new TargetedByWeaponCondition(starship, Filters.ion_cannon)), -2));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.starship;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersWhenInactiveInPlay(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check inactive condition
        if (!GameConditions.isOnlyCaptured(game, self))
            return null;

        String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.captured(game, effectResult, Filters.hasAttached(self))) {
            int amountOfForce = Filters.Falcon.accepts(game, self.getAttachedTo()) ? 5 : 3;

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make " + opponent + " lose " + amountOfForce + " Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseCardFromTableEffect(action, self));
            action.appendEffect(
                    new LoseForceEffect(action, opponent, amountOfForce));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLeavesTableRequiredTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, self)) {
            PhysicalCard lostFromAttachedTo = ((LostFromTableResult) effectResult).getFromAttachedTo();
            if (lostFromAttachedTo != null && Filters.inLostPile.accepts(game, lostFromAttachedTo)) {
                int amountOfForce = Filters.Falcon.accepts(game, lostFromAttachedTo) ? 5 : 3;

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make " + opponent + " lose " + amountOfForce + " Force");
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, opponent, amountOfForce));
                return Collections.singletonList(action);
            }
        }

        return null;
    }
}