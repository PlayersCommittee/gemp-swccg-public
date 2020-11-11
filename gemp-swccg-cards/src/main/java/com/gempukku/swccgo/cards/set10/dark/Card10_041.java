package com.gempukku.swccgo.cards.set10.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections II
 * Type: Character
 * Subtype: Droid
 * Title: Guri
 */
public class Card10_041 extends AbstractDroid {
    public Card10_041() {
        super(Side.DARK, 2, 6, 6, 7, Title.Guri, Uniqueness.UNIQUE);
        setArmor(5);
        setLore("Human-replica droid. Programmed to function as Xizor's personal bodyguard and assassin. Black Sun agent. Cost 9 million credits. Worth every decicred.");
        setGameText("Adds 2 to power of anything she pilots. When present with Xizor, he may not be targeted by weapons. While Vader not here, opponent may draw no more than one battle destiny here. Immune to purchase, Restraining Bolt, and attrition < 5.");
        addIcons(Icon.REFLECTIONS_II, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.FEMALE, Keyword.BLACK_SUN_AGENT, Keyword.BODYGUARD, Keyword.ASSASSIN);
        addModelTypes(ModelType.ASSASSIN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, Filters.Xizor, new PresentWithCondition(self, Filters.Xizor)));
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, Filters.here(self), new NotCondition(new HereCondition(self, Filters.Vader)),
                1, game.getOpponent(self.getOwner())));
        modifiers.add(new MayNotBePurchasedModifier(self));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Restraining_Bolt));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }
}
