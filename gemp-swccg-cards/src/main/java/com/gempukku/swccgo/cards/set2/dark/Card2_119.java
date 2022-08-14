package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Effect
 * Title: Dark Waters
 */
public class Card2_119 extends AbstractNormalEffect {
    public Card2_119() {
        super(Side.DARK, 2, PlayCardZoneOption.ATTACHED, Title.Dark_Waters);
        setLore("Swamp predators require damp and cluttered environments to enhance their camouflage and stealth. 'Something just moved past my leg.'");
        setGameText("Deploy on any exterior planet site (except Hoth) or any interior vehicle site. Opponent's Force drains are -1 here. (Immune to Alter when a swamp creature is present.)");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.DEPLOYS_ON_SITE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.and(Filters.exterior_planet_site, Filters.except(Filters.Hoth_site)), Filters.interior_vehicle_site);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), -1, opponent));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, new PresentCondition(self, Filters.swamp_creature), Title.Alter));
        return modifiers;
    }
}