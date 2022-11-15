package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MoveCostFromLocationUsingLandspeedModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Droid
 * Title: Security Battle Droid
 */
public class Card12_118 extends AbstractDroid {
    public Card12_118() {
        super(Side.DARK, 3, 2, 2, 3, "Security Battle Droid", Uniqueness.UNRESTRICTED, ExpansionSet.CORUSCANT, Rarity.C);
        setArmor(3);
        setLore("Manufactured by the Baktoid Combat Automata, battle droids are used by the Trade Federation throughout the galaxy in order to secure and protect sites of strategic importance.");
        setGameText("Opponent's characters require +1 Force to move from same site using their landspeed. While with another battle droid at a site, draws one battle destiny if unable to otherwise.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.PRESENCE);
        addKeywords(Keyword.SECURITY_BATTLE_DROID);
        addModelType(ModelType.BATTLE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MoveCostFromLocationUsingLandspeedModifier(self, Filters.and(Filters.opponents(self), Filters.character), 1, Filters.sameSite(self)));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new AndCondition(new WithCondition(self, Filters.battle_droid),
                new AtCondition(self, Filters.site)), 1));
        return modifiers;
    }
}
