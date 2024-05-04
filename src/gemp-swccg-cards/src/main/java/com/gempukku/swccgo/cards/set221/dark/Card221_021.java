package com.gempukku.swccgo.cards.set221.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeReducedModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Effect
 * Title: Hutt Influence (V)
 */
public class Card221_021 extends AbstractNormalEffect {
    public Card221_021() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Hutt_Influence, Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setVirtualSuffix(true);
        setLore("Jabba's criminal empire extends to all reaches of the Outer Rim.");
        setGameText("Deploy on table. I Must Be Allowed To Speak may only target opponent's locations (even if converted). At Tatooine battlegrounds where you have an alien and a gambler, gangster, or Dug, Force drains may not be canceled or reduced by opponent. [Immune to Alter.]");
        addIcons(Icon.PREMIUM, Icon.VIRTUAL_SET_21);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        Filter locationFilter = Filters.and(Filters.Tatooine_location, Filters.battleground, Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.alien, Filters.with(self, Filters.and(Filters.your(self), Filters.or(Filters.gangster, Filters.gambler, Filters.species(Species.DUG)))))));

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotBeTargetedByModifier(self, Filters.and(Filters.site, Filters.your(self), Filters.not(Filters.convertedLocationOnTopOfLocation(Filters.opponents(self)))), Filters.title(Title.I_Must_Be_Allowed_To_Speak)));
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, locationFilter, opponent, null));
        modifiers.add(new ForceDrainsMayNotBeReducedModifier(self, locationFilter, opponent, null));
        return modifiers;
    }
}