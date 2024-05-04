package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections III
 * Type: Character
 * Subtype: Alien
 * Title: Boba Fett, Bounty Hunter
 */
public class Card13_059 extends AbstractAlien {
    public Card13_059() {
        super(Side.DARK, 3, 5, 7, 3, 6, "Boba Fett, Bounty Hunter", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setArmor(5);
        setLore("The most feared bounty hunter in the galaxy. Justifiably, his fee is as large as his reputation.");
        setGameText("Text: Adds 3 to power of anything he pilots. Adds one battle destiny if with your alien or Imperial (or adds two battle destiny if with your bounty hunter or Vader). Immune to attrition < 5. End of your turn: Use 2 Force to maintain OR Lose 2 Force to place in Used Pile OR Place out of play.");
        addPersona(Persona.BOBA_FETT);
        addIcons(Icon.REFLECTIONS_III, Icon.PILOT, Icon.WARRIOR, Icon.MAINTENANCE);
        addKeywords(Keyword.BOUNTY_HUNTER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new AddsBattleDestinyModifier(self, new WithCondition(self, Filters.and(Filters.your(self), Filters.or(Filters.alien, Filters.Imperial, Filters.bounty_hunter, Filters.Vader))),
                new ConditionEvaluator(1, 2, new WithCondition(self, Filters.and(Filters.your(self), Filters.or(Filters.bounty_hunter, Filters.Vader))))));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }

    @Override
    protected StandardEffect getGameTextMaintenanceMaintainCost(Action action, final String playerId) {
        return new UseForceEffect(action, playerId, 2);
    }

    @Override
    protected StandardEffect getGameTextMaintenanceRecycleCost(Action action, final String playerId) {
        return new LoseForceEffect(action, playerId, 2, true);
    }
}
