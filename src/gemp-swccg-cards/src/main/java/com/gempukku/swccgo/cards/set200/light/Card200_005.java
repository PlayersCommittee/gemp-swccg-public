package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotModifyBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotModifyTotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotResetBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotResetTotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 0
 * Type: Character          .
 * Subtype: Rebel
 * Title: Chewie (V)
 */
public class Card200_005 extends AbstractRebel {
    public Card200_005() {
        super(Side.LIGHT, 1, 3, 6, 2, 6, "Chewie", Uniqueness.UNIQUE, ExpansionSet.SET_0, Rarity.V);
        setVirtualSuffix(true);
        setLore("Loyal Wookiee companion of Captain Han Solo. Co-pilot of the Millennium Falcon. Leia referred to him as a 'walking carpet.'");
        setGameText("[Pilot] 3. While piloting Falcon, it is maneuver +2, Chewie draws one battle destiny if unable to otherwise, and your battle destiny draws (and total battle destiny) at same system may not be modified, canceled, or reset by opponent.");
        addPersona(Persona.CHEWIE);
        addIcons(Icon.PREMIUM, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_0);
        setSpecies(Species.WOOKIEE);
        setMatchingStarshipFilter(Filters.Falcon);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Condition whilePilotingFalcon = new PilotingCondition(self, Filters.Falcon);
        Filter sameSystem = Filters.sameSystem(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ManeuverModifier(self, Filters.Falcon, whilePilotingFalcon, 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, whilePilotingFalcon, 1));
        modifiers.add(new MayNotModifyBattleDestinyModifier(self, sameSystem, playerId, whilePilotingFalcon, opponent));
        modifiers.add(new MayNotCancelBattleDestinyModifier(self, sameSystem, playerId, whilePilotingFalcon, opponent));
        modifiers.add(new MayNotResetBattleDestinyModifier(self, sameSystem, playerId, whilePilotingFalcon, opponent));
        modifiers.add(new MayNotModifyTotalBattleDestinyModifier(self, sameSystem, playerId, whilePilotingFalcon, opponent));
        modifiers.add(new MayNotResetTotalBattleDestinyModifier(self, sameSystem, playerId, whilePilotingFalcon, opponent));
        return modifiers;
    }
}
