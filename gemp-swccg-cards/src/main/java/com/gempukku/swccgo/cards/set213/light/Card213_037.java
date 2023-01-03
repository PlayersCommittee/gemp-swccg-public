package com.gempukku.swccgo.cards.set213.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.conditions.UtinniEffectCompletedCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.TakeStackedCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.GenerateNoForceModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Character
 * Subtype: Rebel
 * Title: Han... Solo
 */
public class Card213_037 extends AbstractAlien {
    public Card213_037() {
        super(Side.LIGHT, 1, 2, 3, 3, 5, "Han... Solo", Uniqueness.UNIQUE, ExpansionSet.SET_13, Rarity.V);
        setLore("Corellian gambler, smuggler, and thief.");
        setGameText("Adds 2 to power and maneuver of anything he pilots or drives. If exactly one 'coaxium' card here, may take it into hand. While piloting Falcon and you have completed a Kessel Run, adds one battle destiny and opponent generates no Force here.");
        addPersona(Persona.HAN);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_13);
        addKeywords(Keyword.SMUGGLER, Keyword.GAMBLER, Keyword.THIEF);
        setSpecies(Species.CORELLIAN);
        setMatchingStarshipFilter(Filters.Falcon);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ManeuverModifier(self, Filters.hasPiloting(self), 2));

        Condition condition = new AndCondition(new PilotingCondition(self, Filters.Falcon), new UtinniEffectCompletedCondition(self.getOwner(), Filters.Kessel_Run));

        modifiers.add(new GenerateNoForceModifier(self, Filters.here(self), condition, game.getOpponent(self.getOwner())));
        modifiers.add(new AddsBattleDestinyModifier(self, condition, 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        if (GameConditions.canSpot(game, self, Filters.Kessel_Run)
                && GameConditions.isHere(game, self, Filters.Kessel_Run)
                && Filters.countStacked(game, Filters.and(Filters.coaxiumCard, Filters.stackedOn(self, Filters.Kessel_Run))) == 1) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Take 'coaxium' card into hand");
            // Perform result(s)
            action.appendEffect(
                    new TakeStackedCardIntoHandEffect(action, playerId, Filters.Kessel_Run, Filters.coaxiumCard)
            );

            return Collections.singletonList(action);
        }
        return null;
    }
}