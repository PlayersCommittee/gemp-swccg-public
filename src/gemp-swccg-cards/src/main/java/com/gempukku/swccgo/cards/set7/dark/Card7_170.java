package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.OnCloudCityCondition;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DestinyWhenDrawnForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Chyler
 */
public class Card7_170 extends AbstractAlien {
    public Card7_170() {
        super(Side.DARK, 2, 2, 2, 2, 3, "Chyler", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.U);
        setLore("Placed several ISB agents and Aqualish in the Cloud City miner's guild. Promised a position when the Empire took over Cloud City. She's still waiting.");
        setGameText("While on Cloud City, adds 4 to destiny of each of your miners drawn for battle destiny and adds 1 to your Force drains at related cloud sectors where you have a miner or refinery (adds 2 if both).");
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR);
        addKeywords(Keyword.FEMALE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition onCloudCityCondition = new OnCloudCityCondition(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DestinyWhenDrawnForBattleDestinyModifier(self, Filters.and(Filters.your(self), Filters.miner), onCloudCityCondition, 4));
        modifiers.add(new ForceDrainModifier(self, Filters.and(Filters.relatedCloudSector(self), Filters.sameLocationAs(self,
                Filters.and(Filters.your(self), Filters.or(Filters.miner, Filters.refinery)))), onCloudCityCondition,
                new CardMatchesEvaluator(1, 2, Filters.and(Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.miner)),
                        Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.refinery)))), self.getOwner()));
        return modifiers;
    }
}
