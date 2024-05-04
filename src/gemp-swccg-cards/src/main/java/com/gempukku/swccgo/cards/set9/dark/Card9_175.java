package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.DuringBattleCondition;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
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
import com.gempukku.swccgo.logic.modifiers.MayFireOneWeaponTwicePerBattleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Starfighter
 * Title: TIE Interceptor
 */
public class Card9_175 extends AbstractStarfighter {
    public Card9_175() {
        super(Side.DARK, 2, 2, 3, null, 4, null, 3, "TIE Interceptor", Uniqueness.UNRESTRICTED, ExpansionSet.DEATH_STAR_II, Rarity.C);
        setLore("Advanced TIE design. Assigned to elite squadrons, such as the 181st. Developed with enhanced weapons and maneuverability to defeat the Incom X-wing design.");
        setGameText("Permanent pilot provides ability of 1. Power +1 when present with an X-wing. During each battle, may fire one starship weapon aboard twice.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT);
        addKeywords(Keyword.NO_HYPERDRIVE);
        addModelType(ModelType.TIE_INTERCEPTOR);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new PresentWithCondition(self, Filters.X_wing), 1));
        modifiers.add(new MayFireOneWeaponTwicePerBattleModifier(self, new DuringBattleCondition(), Filters.starship_weapon));
        return modifiers;
    }
}
