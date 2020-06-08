package com.gempukku.swccgo.cards.set102.light;

import com.gempukku.swccgo.cards.AbstractShuttleVehicle;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.TriggeringResultEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextLandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.BullseyedResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Jedi Pack)
 * Type: Vehicle
 * Subtype: Shuttle
 * Title: Luke's T-16 Skyhopper
 */
public class Card102_004 extends AbstractShuttleVehicle {
    public Card102_004() {
        super(Side.LIGHT, 4, 2, 2, 5, null, 4, Title.Lukes_T16_Skyhopper, Uniqueness.UNIQUE);
        setLore("Enclosed vehicle used by Luke Skywalker for his early pilot training. Often raced with Biggs Darklighter through Beggar's Canyon.");
        setGameText("Requires 1 pilot to use. May carry 1 passenger. May move as a 'react.' May 'bullseye' one Womp Rat per turn. *Landspeed = 4, OR 1 character may shuttle to or from same site for free.");
        addIcons(Icon.PREMIUM);
        addKeywords(Keyword.ENCLOSED);
        addModelType(ModelType.T_16);
        setPilotCapacity(1);
        setPassengerCapacity(1);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextLandspeedModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter targetFilter = Filters.and(Filters.womp_rat, Filters.presentWith(self));

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canTarget(game, self, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("'Bullseye' a Womp Rat");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Target Womp Rat to 'bullseye'", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                            action.addAnimationGroup(cardTargeted);
                            // Allow response(s)
                            action.allowResponses("'Bullseye' " + GameUtils.getCardLink(cardTargeted),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            game.getGameState().sendMessage(GameUtils.getCardLink(cardTargeted) + " was 'bullseyed' by " + GameUtils.getCardLink(self));
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new TriggeringResultEffect(action, new BullseyedResult(cardTargeted, self)));
                                        }
                                    });
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
