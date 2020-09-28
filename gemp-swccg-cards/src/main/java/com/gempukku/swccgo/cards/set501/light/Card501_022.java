package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.MoveCardAsRegularMoveEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Character
 * Subtype: Alien
 * Title: Rio Durant
 */
public class Card501_022 extends AbstractAlien {
    public Card501_022() {
        super(Side.LIGHT, 4, 3, 3, 2, 4, "Rio Durant", Uniqueness.UNIQUE);
        setLore("Ardennian smuggler and thief.");
        setGameText("Adds 2 to power of anything he pilots. Matching pilot for any stolen or [Independent] starship. Once during your deploy phase, your smuggler of power < 4 here may move to an adjacent site (using landspeed) as a regular move.");
        addPersona(Persona.RIO);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.WARRIOR, Icon.VIRTUAL_SET_13);
        addKeywords(Keyword.SMUGGLER, Keyword.THIEF);
        setSpecies(Species.ARDENNIAN);
        setMatchingStarshipFilter(Filters.or(Filters.stolen, Filters.icon(Icon.INDEPENDENT)));
        setTestingText("Rio Durant");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canSpot(game, self, Filters.adjacentSite(self))) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Move one of your smugglers.");
            action.appendUsage(
                    new OncePerPhaseEffect(action)
            );
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Move one of your smugglers of power < 4 here", Filters.and(Filters.your(playerId), Filters.smuggler, Filters.powerLessThan(4), Filters.here(self))) {
                        @Override
                        protected void cardSelected(final PhysicalCard smugglerToMove) {
                            action.appendEffect(
                                    new MoveCardAsRegularMoveEffect(action, playerId, smugglerToMove, false, false, Filters.adjacentSite(self))
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}

