package com.gempukku.swccgo.cards.set206.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.MoveCardAsRegularMoveEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetManeuverModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 6
 * Type: Starship
 * Subtype: Starfighter
 * Title: The Falcon
 */
public class Card206_008 extends AbstractStarfighter {
    public Card206_008() {
        super(Side.LIGHT, 2, 3, 3, null, 4, 6, 7, "The Falcon", Uniqueness.UNIQUE);
        setGameText("May add 2 pilots and 2 passengers. Once per game, during your move phase, may make an additional move. While [Episode VII] Chewie, [Episode VII] Han, or Rey piloting, maneuver = 6 and immune to attrition < 5.");
        addPersona(Persona.FALCON);
        addIcons(Icon.EPISODE_VII, Icon.NAV_COMPUTER, Icon.RESISTANCE, Icon.SCOMP_LINK);
        addModelType(ModelType.HEAVILY_MODIFIED_LIGHT_FREIGHTER);
        setPilotCapacity(2);
        setPassengerCapacity(2);
        setMatchingPilotFilter(Filters.or(Filters.Chewie, Filters.Han, Filters.Rey));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.THE_FALCON__MAKE_ADDITIONAL_MOVE;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isDuringYourPhase(game, self, Phase.MOVE)
                && Filters.movableAsAdditionalMove(playerId).accepts(game, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Make an additional move");
            action.setActionMsg("Have " + GameUtils.getCardLink(self) + " make an additional move");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new MoveCardAsRegularMoveEffect(action, playerId, self, false, true, Filters.any));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingCondition = new HasPilotingCondition(self, Filters.or(Filters.and(Filters.Chewie, Icon.EPISODE_VII),
                Filters.and(Filters.Han, Icon.EPISODE_VII), Filters.Rey));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ResetManeuverModifier(self, pilotingCondition, 6));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, pilotingCondition, 5));
        return modifiers;
    }
}
