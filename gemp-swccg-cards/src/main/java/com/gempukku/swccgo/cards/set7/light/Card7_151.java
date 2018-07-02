package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractTransportVehicle;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DrivenCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.AbilityOfDriverEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.MoveCardUsingLandspeedEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextLandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.MovedResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Vehicle
 * Subtype: Transport
 * Title: Air-2 Racing Swoop
 */
public class Card7_151 extends AbstractTransportVehicle {
    public Card7_151() {
        super(Side.LIGHT, 5, 2, 0, null, 5, null, 2, "Air-2 Racing Swoop");
        setLore("Features maneuvering flaps and repulsorlift engines. High speed and sensitive controls make swoops hard to drive. Outracing slavers on Bonadan, Han escaped on an Air-2.");
        setGameText("May add 1 driver and 1 passenger. *Landspeed = Driver's ability, and once per turn, may follow an opponent's vehicle or character that just moved from same site (if within range).");
        addIcons(Icon.SPECIAL_EDITION);
        addKeywords(Keyword.SWOOP);
        setDriverCapacity(1);
        setPassengerCapacity(1);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextLandspeedModifier(self, new DrivenCondition(self), new AbilityOfDriverEvaluator(self)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        Filter opponentsVehicleOrCharacter = Filters.and(Filters.opponents(self), Filters.or(Filters.vehicle, Filters.character));

        // Check condition(s)
        if (TriggerConditions.movedFromLocation(game, effectResult, opponentsVehicleOrCharacter, Filters.sameSite(self))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)) {
            MovedResult movedResult = (MovedResult) effectResult;
            final Filter toLocation = Filters.sameLocation(movedResult.getMovedTo());
            if (Filters.movableAsRegularMoveUsingLandspeed(playerId, false, false, false, 0, null, toLocation).accepts(game, self)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Follow using landspeed");
                action.setActionMsg("Have " + GameUtils.getCardLink(self) + " follow using landspeed");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new MoveCardUsingLandspeedEffect(action, playerId, self, false, toLocation));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
