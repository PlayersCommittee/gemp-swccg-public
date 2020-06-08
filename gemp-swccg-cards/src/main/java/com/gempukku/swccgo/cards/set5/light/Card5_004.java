package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.DuringForceDrainAtCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Character
 * Subtype: Alien
 * Title: Kebyc
 */
public class Card5_004 extends AbstractAlien {
    public Card5_004() {
        super(Side.LIGHT, 3, 3, 1, 2, 5, "Kebyc", Uniqueness.UNIQUE);
        setLore("Senior accountant for the Cloud City Miner's Guild. Administrates the annual dues and fights corruption among her fellow guild members.");
        setGameText("When at a site and opponent is losing Force from Force drains at cloud sectors on same planet, lost Force must come from Reserve Deck, if possible.");
        addIcons(Icon.CLOUD_CITY);
        addKeywords(Keyword.ACCOUNTANT, Keyword.FEMALE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SpecialFlagModifier(self, new AndCondition(new AtCondition(self, Filters.site),
                new DuringForceDrainAtCondition(Filters.relatedCloudSector(self))), ModifierFlag.FORCE_DRAIN_LOST_FROM_RESERVE_DECK,
                game.getOpponent(self.getOwner())));
        return modifiers;
    }
}
