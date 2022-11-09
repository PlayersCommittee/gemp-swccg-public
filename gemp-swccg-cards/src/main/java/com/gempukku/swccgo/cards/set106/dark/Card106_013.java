package com.gempukku.swccgo.cards.set106.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.evaluators.PerTIEEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Official Tournament Sealed Deck)
 * Type: Starship
 * Subtype: Capital
 * Title: Dreadnaught-Class Heavy Cruiser
 */
public class Card106_013 extends AbstractCapitalStarship {
    public Card106_013() {
        super(Side.DARK, 3, 5, 5, 4, null, 3, 5, "Dreadnaught-Class Heavy Cruiser", Uniqueness.UNRESTRICTED, ExpansionSet.OTSD, Rarity.PM);
        setLore("Introduced during the Clone Wars. 600 meters long. Some have cargo space converted to TIE hanger bays. Still used to maintain an Imperial presence in the Outer Rim.");
        setGameText("May add 3 pilots, 6 passengers and 4 TIEs. Has ship-docking capability. Permanent pilots provide total ability of 2. Turbolaser Battery may deploy aboard. Your TIEs present are each power +1.");
        addIcons(Icon.PREMIUM, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addIcon(Icon.PILOT, 2);
        addModelType(ModelType.DREADNAUGHT_CLASS_HEAVY_CRUISER);
        setPilotCapacity(3);
        setPassengerCapacity(6);
        setTIECapacity(4);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        List<AbstractPermanentAboard> permanentsAboard = new ArrayList<AbstractPermanentAboard>();
        permanentsAboard.add(new AbstractPermanentPilot(1) {});
        permanentsAboard.add(new AbstractPermanentPilot(1) {});
        return permanentsAboard;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToTargetModifier(self, Filters.and(Filters.your(self), Filters.turbolaser_battery), self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(self), Filters.TIE, Filters.present(self)), new PerTIEEvaluator(1)));
        return modifiers;
    }
}
