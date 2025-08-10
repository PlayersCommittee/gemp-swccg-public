package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.*;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 8
 * Type: Character
 * Subtype: Alien
 * Title: Jango Fett, The Assassin
 */
public class Card601_013 extends AbstractAlien {
    public Card601_013() {
        super(Side.DARK, 1, 4, 4, 3, 6, "Jango Fett, The Assassin", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setArmor(5);
        setLore("Trade Federation. Scout.");
        setGameText("Adds 3 to the power of anything he pilots. Adds one battle destiny. SD-17 Homing Missile may deploy on (and is a matching weapon for) him, fires for free, and when fired may go to Lost Pile instead. While piloted by two Fetts (unless your [Block 4] objective on table), Slave I is immune to attrition < 9.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.LEGACY_BLOCK_8, Icon.DAGOBAH, Icon.EPISODE_I);
        addKeywords(Keyword.SCOUT, Keyword.ASSASSIN);
        addPersona(Persona.JANGO_FETT);
        setMatchingStarshipFilter(Filters.Slave_I);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition condition = new AndCondition(new UnlessCondition(new OnTableCondition(self, Filters.and(Filters.your(self), Icon.LEGACY_BLOCK_4, Filters.Objective))),
                new Condition() {
                    @Override
                    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                        return Filters.countActive(gameState.getGame(), self, Filters.and(Filters.Fett, Filters.piloting(Filters.Slave_I)))>=2;
                    }
                });

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.Slave_I, condition, 9));
        modifiers.add(new AddsBattleDestinyModifier(self, 1));
        return modifiers;
    }

    //TODO ignored SD-17 Homing Missile text
}