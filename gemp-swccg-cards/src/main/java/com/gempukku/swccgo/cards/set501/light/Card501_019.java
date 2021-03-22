package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.*;

/**
 * Set: Set 15
 * Type: Effect
 * Title: A Power Loss
 */
public class Card501_019 extends AbstractNormalEffect {
    public Card501_019() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "A Power Loss", Uniqueness.UNIQUE);
        setLore("");
        setGameText("Deploy on Central Core; opponent may stack up to 4 cards from their hand face-up here. If you just won a battle at a Death Star site, place a card stacked here in opponent's Used Pile. If no cards stacked here; power 'shut down.’ [Immune to Alter.]");
        addIcons(Icon.VIRTUAL_SET_15);
        addImmuneToCardTitle(Title.Alter);
        setTestingText("A Power Loss");
        excludeFromDeckBuilder();
    }
}
