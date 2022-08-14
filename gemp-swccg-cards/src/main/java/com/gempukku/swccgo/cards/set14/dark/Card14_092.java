package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Character
 * Subtype: Droid
 * Title: Tank Commander
 */
public class Card14_092 extends AbstractDroid {
    public Card14_092() {
        super(Side.DARK, 3, 2, 1, 3, "Tank Commander");
        setArmor(3);
        setLore("AATs require officer battle droids to command them and relay orders from the Droid Control Ship. Programmed with sophisticated tank warfare tactics.");
        setGameText("Adds 3 to power of any AAT he pilots. While piloting an AAT, forfeit +2, draws one battle destiny if unable to otherwise, and your other [Presence] droids at this site are immune to attrition < 4.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.PILOT, Icon.PRESENCE);
        addKeywords(Keyword.COMMANDER, Keyword.OFFICER_BATTLE_DROID);
        addModelType(ModelType.BATTLE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingAAT = new PilotingCondition(self, Filters.AAT);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3, Filters.AAT));
        modifiers.add(new ForfeitModifier(self, pilotingAAT, 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, pilotingAAT, 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Filters.your(self), Filters.other(self),
                Icon.PRESENCE, Filters.droid, Filters.atSameSite(self)), pilotingAAT, 4));
        return modifiers;
    }
}
