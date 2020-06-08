package com.gempukku.swccgo.cards.set200.dark;

import com.gempukku.swccgo.cards.AbstractAlienImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 0
 * Type: Character
 * Subtype: Alien/Imperial
 * Title: Arica (V)
 */
public class Card200_074 extends AbstractAlienImperial {
    public Card200_074() {
        super(Side.DARK, 1, 5, 4, 5, 7, "Arica", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Mara Jade posed as a dancer at Jabba's Palace in an attempt to complete her master's task and kill Luke. Musician. Spy. Unable to convince Jabba to take her on his skiff.");
        setGameText("Power +1 on Tatooine. Once per game, during your move phase, may relocate Arica to Luke's location. During your control phase, if present at a battleground (and Luke is not), may retrieve 1 Force. Immune to Undercover and attrition < 4.");
        addPersona(Persona.MARA_JADE);
        addIcons(Icon.REFLECTIONS_II, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_0);
        addKeywords(Keyword.FEMALE, Keyword.MUSICIAN, Keyword.SPY);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new OnCondition(self, Title.Tatooine), 1));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Undercover));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ARICA__RELOCATE_TO_LUKES_LOCATION;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isDuringYourPhase(game, self, Phase.MOVE)) {
            PhysicalCard lukesLocation = Filters.findFirstFromTopLocationsOnTable(game, Filters.sameLocationAs(self, Filters.Luke));
            if (lukesLocation != null) {
                if (Filters.locationCanBeRelocatedTo(self, 0).accepts(game, lukesLocation)) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                    action.setText("Relocate to Luke's location");
                    // Update usage limit(s)
                    action.appendUsage(
                            new OncePerGameEffect(action));
                    // Perform result(s)
                    action.appendEffect(
                            new RelocateBetweenLocationsEffect(action, self, lukesLocation));
                    return Collections.singletonList(action);
                }
            }
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.isPresentAt(game, self, Filters.battleground)
                && !GameConditions.canSpot(game, self, Filters.and(Filters.Luke, Filters.presentAt(Filters.battleground)))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve 1 Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
