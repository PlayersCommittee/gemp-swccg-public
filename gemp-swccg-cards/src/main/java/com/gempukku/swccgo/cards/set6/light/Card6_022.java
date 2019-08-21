package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Kalit
 */
public class Card6_022 extends AbstractAlien {
    public Card6_022() {
        super(Side.LIGHT, 2, 3, 2, 2, 3, Title.Kalit, Uniqueness.UNIQUE);
        setLore("Jawa leader. Seeking to peacefully settle a long-standing disagreement with his rival, Wittin. Wants Jabba to mediate their talks.");
        setGameText("Deploys only on Tatooine. Your Jawa Siesta is not unique(•), is doubled, deploys free (or for 6 Force from each player) and cummulatively affects your Jawas' forfeit. While at Audience Chamber or Jawa Camp, all your other Jawas are power +2.");
        addIcons(Icon.JABBAS_PALACE);
        addKeywords(Keyword.LEADER);
        setSpecies(Species.JAWA);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Tatooine;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atAudienceChamberOrJawaCamp = new AtCondition(self, Filters.or(Filters.Jawa_Camp, Filters.Audience_Chamber));
        Filter yourOtherJawa = Filters.and(Filters.your(self), Filters.other(self), Filters.Jawa);
        Filter jawaSiesta = Filters.Jawa_Siesta;

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new NotUniqueModifier(self, jawaSiesta));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Jawa_Siesta, ModifyGameTextType.JAWA__SIESTA_MODIFIED_BY_KALIT));
        modifiers.add(new PowerModifier(self, yourOtherJawa, atAudienceChamberOrJawaCamp, 2));
        return modifiers;
    }
}
