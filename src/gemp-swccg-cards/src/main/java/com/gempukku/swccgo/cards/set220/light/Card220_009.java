package com.gempukku.swccgo.cards.set220.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.ImprisonedOnlyCondition;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTransferredModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveGameTextCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 20
 * Type: Character
 * Subtype: Rebel
 * Title: Prisoner 2187 (V)
 */
public class Card220_009 extends AbstractRebel {
    public Card220_009() {
        super(Side.LIGHT, 1, 4, 4, 3, 6, Title.Prisoner_2187, Uniqueness.UNIQUE, ExpansionSet.SET_20, Rarity.V);
        setVirtualSuffix(true);
        setLore("Princess Leia Organa. Alderaanian senator. Targeted by Vader for capture and interrogation. The Dark Lord of the Sith wanted her alive.");
        setGameText("Draws one battle destiny if unable to otherwise. While present at a Death Star site, Force drain +1 where you have a Rebel stormtrooper. While on Death Star (even if imprisoned), Leia may not be transferred and her gametext may not be canceled.");
        addPersona(Persona.LEIA);
        addIcons(Icon.PREMIUM, Icon.WARRIOR, Icon.A_NEW_HOPE, Icon.VIRTUAL_SET_20);
        addKeywords(Keyword.SENATOR, Keyword.FEMALE);
        setSpecies(Species.ALDERAANIAN);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileInactiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Condition onDeathStarAndImprisonedCondition = new AndCondition(new ImprisonedOnlyCondition(self), new OnCondition(self, Title.Death_Star));

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotHaveGameTextCanceledModifier(self, onDeathStarAndImprisonedCondition));
        modifiers.add(new MayNotBeTransferredModifier(self, onDeathStarAndImprisonedCondition));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition onDeathStarCondition = new OnCondition(self, Title.Death_Star);

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, 1));

        modifiers.add(new ForceDrainModifier(self, Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.Rebel, Filters.stormtrooper)),
                new PresentAtCondition(self, Filters.Death_Star_site), 1, playerId));

        modifiers.add(new MayNotHaveGameTextCanceledModifier(self, onDeathStarCondition));
        modifiers.add(new MayNotBeTransferredModifier(self, onDeathStarCondition));
        return modifiers;
    }
}
