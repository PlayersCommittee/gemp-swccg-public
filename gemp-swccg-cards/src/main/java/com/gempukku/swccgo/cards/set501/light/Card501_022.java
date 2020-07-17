package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.MoveCardAsRegularMoveEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardsInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

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
        setGameText("Adds 2 to power and 1 to maneuver of anything he pilots. During your deploy phase, may “smuggle” one of your smugglers of power < 4 here (move them to an adjacent site as a regular move). If about to be lost, place all weapons on him in Used pile.");
        addPersona(Persona.RIO);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.WARRIOR, Icon.VIRTUAL_SET_13);
        addKeywords(Keyword.SMUGGLER, Keyword.THIEF);
        setSpecies(Species.ARDENNIAN);
        setTestingText("Rio Durant");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ManeuverModifier(self, 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canSpot(game, self, Filters.adjacentSite(self))) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("'Smuggle' one of your characters.");
            action.appendUsage(
                    new OncePerPhaseEffect(action)
            );
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "'Smuggle' one of your smugglers of power < 4 here", Filters.and(Filters.your(playerId), Filters.smuggler, Filters.powerLessThan(4), Filters.here(self))) {
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

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isAboutToLeaveTable(game, effectResult, self)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place all weapons on him in used pile");
            action.appendEffect(
                    new PlaceCardsInUsedPileFromTableEffect(action, Filters.filterActive(game, self, Filters.and(Filters.attachedTo(self), Filters.weapon)))
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}

