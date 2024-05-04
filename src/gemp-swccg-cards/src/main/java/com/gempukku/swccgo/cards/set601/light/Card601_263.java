package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.AtLeastNumberOfAlienSpeciesOnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotTargetToBeCapturedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 4
 * Type: Effect
 * Title: Ellorrs Madak (V)
 */
public class Card601_263 extends AbstractNormalEffect {
    public Card601_263() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE,  Title.Ellorrs_Madak, Uniqueness.UNRESTRICTED, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("Like many Duros, Madak has natural piloting and navigation skill. Former scout. Freelance instructor. Makes runs to important trade worlds Celanon, Byblos and Yaga Minor.");
        setGameText("Deploy on table.  Your ••• aliens are deploy -1.  Your Rep is immune to attrition and may not be captured.  While you have alien characters of five different species on table, your Force drains and battle destiny draws are +1, and opponent's battle destiny draws are -1. (Immune to Alter.)");
        addIcons(Icon.LEGACY_BLOCK_4);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(final SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        final PhysicalCard rep = game.getGameState().getRep(playerId);
        Filter repFilter = Filters.none;
        if (rep != null) {
            repFilter = Filters.sameTitle(rep);
        }

        Condition fiveDifferentSpeciesCondition = new AtLeastNumberOfAlienSpeciesOnTableCondition(game, self, 5);

        modifiers.add(new DeployCostModifier(self, Filters.and(Filters.your(self), Filters.uniqueness(Uniqueness.RESTRICTED_3), Filters.alien), -1));
        modifiers.add(new ImmuneToAttritionModifier(self, repFilter));
        modifiers.add(new MayNotTargetToBeCapturedModifier(self, repFilter));
        modifiers.add(new ForceDrainModifier(self, Filters.any, fiveDifferentSpeciesCondition, 1, playerId));
        modifiers.add(new EachBattleDestinyModifier(self, Filters.any, fiveDifferentSpeciesCondition, 1, playerId, true));
        modifiers.add(new EachBattleDestinyModifier(self, Filters.any, fiveDifferentSpeciesCondition, -1, opponent, true));

        return modifiers;
    }
}