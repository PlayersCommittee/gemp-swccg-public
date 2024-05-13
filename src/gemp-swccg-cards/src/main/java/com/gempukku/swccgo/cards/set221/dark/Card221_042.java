package com.gempukku.swccgo.cards.set221.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
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
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotAddDestinyDrawsToPowerModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawMoreThanBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 21
 * Type: Starship
 * Subtype: Starfighter
 * Title: Zuckuss And 4-LOM In Mist Hunter
 */
public class Card221_042 extends AbstractStarfighter {
    public Card221_042() {
        super(Side.DARK, 2, 5, 5, null, 5, 5, 7, "Zuckuss And 4-LOM In Mist Hunter", Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setComboCard(true);
        setLore("Commissioned by a group of Gand venture capitalists headed by Zuckuss. Manufactured by Byblos Drive Yards. Uses repulsorlift technology developed for combat cloud cars.");
        setGameText("May 1 pilot. Permanent pilots are •Zuckuss, who provides ability of 4, and •4-LOM. Players may not add power destinies or draw more than one battle destiny here. Immune to attrition < 5.");
        addPersonas(Persona.MIST_HUNTER);
        addIcons(Icon.INDEPENDENT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_21);
        addIcon(Icon.PILOT, 2);
        addModelType(ModelType.BYBLOS_G1A_TRANSPORT);
        setPilotCapacity(1);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        List<AbstractPermanentAboard> permanentsAboard = new ArrayList<>();
        permanentsAboard.add(new AbstractPermanentPilot(Persona.ZUCKUSS, 4) {
        });
        permanentsAboard.add(new AbstractPermanentPilot(Persona._4_LOM, 0) {
        });
        return permanentsAboard;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotAddDestinyDrawsToPowerModifier(self, new InBattleCondition(self), playerId));
        modifiers.add(new MayNotAddDestinyDrawsToPowerModifier(self, new InBattleCondition(self), opponent));
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, Filters.here(self), 1, playerId));
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, Filters.here(self), 1, opponent));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }
}
