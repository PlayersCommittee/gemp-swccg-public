package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
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
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MoveCostUsingLandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Character
 * Subtype: Droid
 * Title: OWO-1 With Backup
 */
public class Card14_084 extends AbstractDroid {
    public Card14_084() {
        super(Side.DARK, 1, 6, 6, 5, "OWO-1 With Backup", Uniqueness.UNIQUE, ExpansionSet.THEED_PALACE, Rarity.R);
        setArmor(5);
        setComboCard(true);
        setLore("Trade Federation battle droid leader assigned to take a squad and destroy what was left of the Jedi ambassadors. His mission went unaccomplished.");
        setGameText("Requires +2 Force to use Landspeed. Power +1 for each opponent's Jedi present. While with a battle droid at a site, draws two battle destiny if unable to otherwise. Opponent's Force drains are -1 at adjacent sites.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.PRESENCE);
        addKeywords(Keyword.LEADER);
        addModelType(ModelType.BATTLE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MoveCostUsingLandspeedModifier(self, 2));
        modifiers.add(new PowerModifier(self, new PresentEvaluator(self, Filters.and(Filters.opponents(self), Filters.Jedi, Filters.present(self)))));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new AndCondition(new WithCondition(self, Filters.battle_droid),
                new AtCondition(self, Filters.site)), 2));
        modifiers.add(new ForceDrainModifier(self, Filters.adjacentSite(self), -1, game.getOpponent(self.getOwner())));
        return modifiers;
    }
}
