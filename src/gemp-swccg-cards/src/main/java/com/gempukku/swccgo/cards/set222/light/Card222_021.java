package com.gempukku.swccgo.cards.set222.light;

import com.gempukku.swccgo.cards.AbstractResistance;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToAttritionEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 22
 * Type: Character
 * Subtype: Resistance
 * Title: General Poe Dameron
 */
public class Card222_021 extends AbstractResistance {
    public Card222_021() {
        super(Side.LIGHT, 1, 4, 3, 3, 6, "General Poe Dameron", Uniqueness.UNIQUE, ExpansionSet.SET_22, Rarity.V);
        setGameText("Adds 3 to anything he pilots. Draws battle destiny if unable to otherwise. " +
                "During battle, if opponent has a greater total number of characters and piloted starships here than you, may add one destiny to attrition. " +
                "Immune to attrition < 3.");
        addIcons(Icon.EPISODE_VII, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_22);
        addKeywords(Keyword.GENERAL);
        addPersona(Persona.POE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isDuringBattleWithParticipant(game, self)
                && GameConditions.canAddDestinyDrawsToAttrition(game, playerId)) {

            Filter filter = Filters.or(Filters.character, Filters.and(Filters.piloted, Filters.starship));
            int numOpponentsCharactersStarships = Filters.countActive(game, self, Filters.and(filter, Filters.opponents(playerId), Filters.participatingInBattle));
            int numYourCharactersStarships = Filters.countActive(game, self, Filters.and(filter, Filters.your(playerId), Filters.participatingInBattle));

            if (numOpponentsCharactersStarships > numYourCharactersStarships) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Add one destiny to attrition");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerBattleEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new AddDestinyToAttritionEffect(action, 1));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
