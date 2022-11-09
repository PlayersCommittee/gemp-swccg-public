package com.gempukku.swccgo.cards.set203.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 3
 * Type: Starship
 * Subtype: Capital
 * Title: Falleen's Fist
 */
public class Card203_035 extends AbstractCapitalStarship {
    public Card203_035() {
        super(Side.DARK, 2, 9, 6, 12, null, null, 8, Title.Falleens_Fist, Uniqueness.UNIQUE, ExpansionSet.SET_3, Rarity.V);
        setGameText("Deploys -3 if your Black Sun agent is at a related site. May add 3 pilots and 6 passengers. Permanent pilots provide total ability of 4. Immune to attrition < 8.");
        addIcon(Icon.PILOT, 2);
        addIcons(Icon.INDEPENDENT, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_3);
        addKeywords(Keyword.NO_HYPERDRIVE);
        addModelType(ModelType.SKYHOOK_PLATFORM);
        setPilotCapacity(3);
        setPassengerCapacity(6);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -3, Filters.relatedLocationTo(self, Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.Black_Sun_agent)))));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        List<AbstractPermanentAboard> permanentsAboard = new ArrayList<AbstractPermanentAboard>();
        permanentsAboard.add(new AbstractPermanentPilot(2) {});
        permanentsAboard.add(new AbstractPermanentPilot(2) {});
        return permanentsAboard;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 8));
        return modifiers;
    }
}
