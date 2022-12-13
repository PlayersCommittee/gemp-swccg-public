package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeAboardModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotReactFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Imperial
 * Title: Lieutenant Arnet
 */
public class Card8_101 extends AbstractImperial {
    public Card8_101() {
        super(Side.DARK, 2, 2, 2, 2, 3, Title.Arnet, Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.U);
        setLore("Veteran AT-ST pilot assigned to coordinate the surprise counterattack in the Battle of Endor. Commanded a prototype AT-ST in General Veers' assault upon Hoth.");
        setGameText("Deploys free aboard Blizzard Scout 1. Adds 2 to power of any combat vehicle he pilots (or 3 if an AT-ST). When piloting Tempest Scout 1, draws one battle destiny if not able to otherwise. Opponent may not 'react' to or from same site.");
        addIcons(Icon.ENDOR, Icon.PILOT);
        setMatchingVehicleFilter(Filters.or(Filters.Blizzard_Scout_1, Filters.Tempest_Scout_1));
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeAboardModifier(self, Filters.Blizzard_Scout_1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(2, 3, Filters.AT_ST), Filters.combat_vehicle));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new PilotingCondition(self, Filters.Tempest_Scout_1), 1));
        modifiers.add(new MayNotReactToLocationModifier(self, Filters.sameSite(self), opponent));
        modifiers.add(new MayNotReactFromLocationModifier(self, Filters.sameSite(self), opponent));
        return modifiers;
    }
}
