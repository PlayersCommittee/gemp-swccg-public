package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
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
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Alien
 * Title: Sykes Jade, Mercenary
 */
public class Card304_142 extends AbstractAlien {
    public Card304_142() {
        super(Side.DARK, 3, 5, 7, 5, 6, "Sykes Jade, Mercenary ", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setArmor(5);
        setLore("Sykes has hidden his true self under the guise of a mercenary for years. He's long been employed by Scholae Palatinae and Kamjin for jobs that shouldn't be connected to the clan.");
        setGameText("Text: Adds 3 to power of anything he pilots. Adds one battle destiny if with your alien or Imperial (or adds two battle destiny if with your [CSP] or Kamjin). Immune to attrition < 5. End of your turn: Use 2 Force to maintain OR Lose 2 Force to place in Used Pile OR Place out of play.");
        addPersona(Persona.SYKES);
        addIcons(Icon.CSP);
        addIcon(Icon.WARRIOR, 2);
        setSpecies(Species.ANZATI);
        addKeywords(Keyword.MALE, Keyword.MERCENARY);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, new WithCondition(self, Filters.and(Filters.your(self), Filters.or(Filters.alien, Filters.Imperial, Filters.CSP_character, Filters.Kamjin))),
                new ConditionEvaluator(1, 2, new WithCondition(self, Filters.and(Filters.your(self), Filters.or(Filters.CSP_character, Filters.Kamjin))))));
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
