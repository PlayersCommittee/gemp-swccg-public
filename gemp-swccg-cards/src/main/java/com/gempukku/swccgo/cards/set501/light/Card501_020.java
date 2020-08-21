package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.conditions.UntinniEffectCompletedCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.choose.TakeStackedCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Character
 * Subtype: Rebel
 * Title: Han... Solo
 */
public class Card501_020 extends AbstractRebel {
    public Card501_020() {
        super(Side.LIGHT, 1, 2, 3, 3, 5, "Han... Solo", Uniqueness.UNIQUE);
        setLore("Smuggler, gambler, and thief. Correlian.");
        setGameText("Adds 2 to power and maneuver of anything he pilots or drives. While piloting Falcon and Kessel Run on table (or completed), adds a battle destiny and opponent generates no Force here. If exactly 1 card stacked on Kessel Run, may take it into hand.");
        addPersona(Persona.HAN);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_13);
        addKeywords(Keyword.SMUGGLER, Keyword.GAMBLER, Keyword.THIEF);
        setSpecies(Species.CORELLIAN);
        setMatchingStarshipFilter(Filters.Falcon);
        setTestingText("Han... Solo");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ManeuverModifier(self, Filters.hasPiloting(self), 2));

        Condition condition = new AndCondition(new PilotingCondition(self, Filters.Falcon), new OrCondition(new OnTableCondition(self, Filters.Kessel_Run), new UntinniEffectCompletedCondition(self.getOwner(), Filters.Kessel_Run)));

        modifiers.add(new GenerateNoForceModifier(self, Filters.here(self), condition, game.getOpponent(self.getOwner())));
        modifiers.add(new AddsBattleDestinyModifier(self, condition, 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        if (GameConditions.canSpot(game, self, Filters.Kessel_Run)
                && Filters.countStacked(game, Filters.stackedOn(self, Filters.Kessel_Run)) == 1) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Take stacked card into hand");
            // Perform result(s)
            action.appendEffect(
                    new TakeStackedCardIntoHandEffect(action, playerId, Filters.Kessel_Run)
            );

            return Collections.singletonList(action);
        }
        return null;
    }
}