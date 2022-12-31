package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.DuringBattleCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyForWeaponFiredByModifier;
import com.gempukku.swccgo.logic.modifiers.MayFireAnyNumberOfWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Starfighter
 * Title: Blue Squadron B-wing
 */
public class Card9_064 extends AbstractStarfighter {
    public Card9_064() {
        super(Side.LIGHT, 3, 5, 4, null, 2, 3, 4, "Blue Squadron B-wing", Uniqueness.RESTRICTED_3, ExpansionSet.DEATH_STAR_II, Rarity.R);
        setLore("B-wings ordered to attack Imperial command starships at Battle of Endor. Armed with armor-penetrating missiles. Maintained by Verpine mechanics.");
        setGameText("Deploy -2 to Roche. Permanent pilot provides ability of 2 and adds 1 to power. May fire two or more weapons during battle. Each of its weapon destiny draws is +2.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.B_WING);
        addKeywords(Keyword.BLUE_SQUADRON);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.Deploys_at_Roche));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(
                new AbstractPermanentPilot(2) {
                    @Override
                    public List<Modifier> getGameTextModifiers(PhysicalCard self) {
                        List<Modifier> modifiers = new LinkedList<Modifier>();
                        modifiers.add(new PowerModifier(self, 1));
                        return modifiers;
                    }});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayFireAnyNumberOfWeaponsModifier(self, new DuringBattleCondition()));
        modifiers.add(new EachWeaponDestinyForWeaponFiredByModifier(self, 2));
        return modifiers;
    }
}
