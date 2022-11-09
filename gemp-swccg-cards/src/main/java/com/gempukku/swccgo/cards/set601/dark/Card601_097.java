package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.CancelForceDrainBonusesFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotAddIconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 7
 * Type: Starship
 * Subtype: Capital
 * Title: Victory
 */
public class Card601_097 extends AbstractCapitalStarship {
    public Card601_097() {
        super(Side.DARK, 3, 6, 5, 5, null, 4, 6, "Victory", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setLore("Commissioned by the Old Republic at end of the Clone Wars, Rendili StarDrive's Victory-class starship is atmosphere-capable but has a low sublight speed.");
        setGameText("May add 4 pilots and 6 passengers.  Permanent pilot provides ability of 4.  Opponent's starships may not add [Light Side] or add to Force drains.  While opponent's [Reflections II] objective on table, immune to attrition.");
        addIcons(Icon.A_NEW_HOPE, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.LEGACY_BLOCK_7);
        addModelType(ModelType.VICTORY_CLASS_STAR_DESTROYER);
        setPilotCapacity(4);
        setPassengerCapacity(6);
        setAsLegacy(true);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(4) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionModifier(self, new OnTableCondition(self, Filters.and(Filters.opponents(self), Icon.REFLECTIONS_II, Filters.Objective))));
        modifiers.add(new CancelForceDrainBonusesFromCardModifier(self, Filters.and(Filters.opponents(self), Filters.starship)));
        modifiers.add(new MayNotAddIconModifier(self, Filters.and(Filters.opponents(self), Filters.starship), Icon.LIGHT_FORCE));
        return modifiers;
    }
}
