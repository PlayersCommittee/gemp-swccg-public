package com.gempukku.swccgo.cards.set303.dark;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayUseAnyNumberOfDevicesModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalTrainingDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.EachTrainingDestinyModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Shadow Academy
 * Type: Device
 * Title: Shadow Academy Holocron
 */
public class Card303_010 extends AbstractCharacterDevice {
    public Card303_010() {
        super(Side.DARK, 5, Title.Shadow_Academy_Holocron, Uniqueness.UNIQUE, ExpansionSet.SA, Rarity.V);
        setLore("The Shadow Academy prides itself on a limited collection of powerful Sith relics. This particular holocron focuses on training apprentices who've lost their master.");
        setGameText("Deploy on any character. Adds 2 to training destiny when held by the apprentice.");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.character);
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.character;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
		modifiers.add(new EachTrainingDestinyModifier(self, Filters.jediTestTargetingApprentice(Filters.hasAttached(self)), 2));
        return modifiers;
    }
}