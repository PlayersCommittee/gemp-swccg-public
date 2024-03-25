package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotFireWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Alien
 * Title: Whiz
 */
public class Card304_018 extends AbstractAlien {
    public Card304_018() {
        super(Side.DARK, 2, 2, 1, 2, 4, "Whiz", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Whiz joined Thran's personal guard after it's initial formation. Whiz loves the look of horror on his enemies faces when he disables their weapons.");
        setGameText("While present with Thran, Rebels with blasters may not fire weapons here. If with Thran, Force drain +1 here.");
        addIcons(Icon.WARRIOR);
		addKeywords(Keyword.THRAN_GUARD);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotFireWeaponsModifier(self, Filters.and(Filters.Rebel, Filters.blaster, Filters.here(self)), new PresentWithCondition(self, Filters.Thran)));
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), new WithCondition(self, Filters.Thran), 1, self.getOwner()));
        return modifiers;
    }
}
