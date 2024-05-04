package com.gempukku.swccgo.cards.set9.light;

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
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Capital
 * Title: Luminous
 */
public class Card9_077 extends AbstractCapitalStarship {
    public Card9_077() {
        super(Side.LIGHT, 3, 3, 1, 3, null, 5, 4, "Luminous", Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.U);
        setLore("Medium transport with boosted hyperdrive. Armor and capacity diminished. Crew trained for reconnaissance and swift intergalatic transport.");
        setGameText("Deploys and moves like a starfigher. May add 1 pilot and 6 passengers. Has ship-docking capability. Permanent pilot provides ability of 2. May use Covert Landing like a shuttle or starfighter.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addKeywords(Keyword.MEDIUM_TRANSPORT);
        addModelType(ModelType.TRANSPORT);
        setPilotCapacity(1);
        setPassengerCapacity(6);
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
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayBeTargetedByModifier(self, Title.Covert_Landing));
        return modifiers;
    }
}
