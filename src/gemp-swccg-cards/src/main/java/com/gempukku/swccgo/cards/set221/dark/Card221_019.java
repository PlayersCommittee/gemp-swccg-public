package com.gempukku.swccgo.cards.set221.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 21
 * Type: Starship
 * Subtype: Capital
 * Title: First Light
 */
public class Card221_019 extends AbstractCapitalStarship {
    public Card221_019() {
        super(Side.DARK, 2, 5, 5, 9, null, 3, 7, "First Light", Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setLore("Crimson Dawn.");
        setGameText("May add 3 pilots and 6 passengers. Permanent pilot provides ability of 3. Deploys and moves like a starfighter. Your total battle destiny here is +1 for each gangster aboard. Immune to attrition < 5.");
        addIcons(Icon.INDEPENDENT, Icon.SCOMP_LINK, Icon.PILOT, Icon.NAV_COMPUTER, Icon.VIRTUAL_SET_21);
        addModelType(ModelType.NAUUR_CLASS_YACHT);
        setPilotCapacity(3);
        setPassengerCapacity(6);
        addPersona(Persona.FIRST_LIGHT);
    }

    @Override
    public boolean isDeploysLikeStarfighter() {
        return true;
    }

    @Override
    public boolean isMovesLikeStarfighter() {
        return true;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        List<AbstractPermanentAboard> permanentsAboard = new ArrayList<>();
        permanentsAboard.add(new AbstractPermanentPilot(3) {});
        return permanentsAboard;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new TotalBattleDestinyModifier(self, new OnTableEvaluator(self, Filters.and(Filters.gangster, Filters.aboard(self))), self.getOwner()));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }
}
