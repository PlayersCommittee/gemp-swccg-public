package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeModifier;
import com.gempukku.swccgo.logic.modifiers.DestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PlaceInUsedPileWhenCanceledModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Effect
 * Title: Oppressive Enforcement
 */
public class Card7_234 extends AbstractNormalEffect {
    public Card7_234() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Oppressive_Enforcement, Uniqueness.UNIQUE);
        setLore("The Imperial fleet keeps a tight grip on the systems under its control. Abuses and excesses by local citizens are not tolerated.");
        setGameText("Deploy on your side of table. Your Imperial capital starships are each destiny +1. Your Immediate Effects may deploy for free. Whenever opponent cancels your card with Sense or Alter, place that canceled card in Used Pile. (Immune to Alter.)");
        addIcons(Icon.SPECIAL_EDITION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DestinyModifier(self, Filters.and(Filters.your(self), Filters.Imperial_starship, Filters.capital_starship), 1));
        modifiers.add(new DeploysFreeModifier(self, Filters.and(Filters.your(self), Filters.Immediate_Effect)));
        modifiers.add(new PlaceInUsedPileWhenCanceledModifier(self, Filters.your(self), opponent, Filters.or(Filters.Sense, Filters.Alter)));
        return modifiers;
    }
}