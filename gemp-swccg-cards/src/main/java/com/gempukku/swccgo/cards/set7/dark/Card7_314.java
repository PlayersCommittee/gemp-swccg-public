package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractTransportVehicle;
import com.gempukku.swccgo.cards.conditions.DrivenCondition;
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
 * Title: Flare-S Racing Swoop
 */
public class Card7_314 extends AbstractTransportVehicle {
    public Card7_314() {
        super(Side.DARK, 5, 2, 0, null, 5, null, 2, "Flare-S Racing Swoop");
        setLore("Attain speeds of up to 600 kph. 3 meters long. Extremely difficult to control at high speeds. Dengar rode a similar swoop when injured in a race with Han at Agrilat.");
        setGameText("May add 1 driver and 1 passenger. Landspeed = driver's ability, and may be used at any time to follow an opponent's vehicle or character that just moved from same site (if within range).");
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
        if (TriggerConditions.movedFromLocation(game, effectResult, opponentsVehicleOrCharacter, Filters.sameSite(self))) {
            MovedResult movedResult = (MovedResult) effectResult;
            final Filter toLocation = Filters.sameLocation(movedResult.getMovedTo());
            if (Filters.movableAsRegularMoveUsingLandspeed(playerId, false, false, false, 0, null, toLocation).accepts(game, self)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Follow using landspeed");
                action.setActionMsg("Have " + GameUtils.getCardLink(self) + " follow using landspeed");
                // Perform result(s)
                action.appendEffect(
                        new MoveCardUsingLandspeedEffect(action, playerId, self, false, toLocation));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
