package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Character
 * Subtype: Rebel
 * Title: Major Olander Brit
 */
public class Card9_026 extends AbstractRebel {
    public Card9_026() {
        super(Side.LIGHT, 3, 2, 2, 1, 3, "Major Olander Brit", Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.U);
        setLore("Communications expert. Staff monitors entire spectrum of electromagnetic signals. Politically minded. Followed Panno to the Alliance.");
        setGameText("Power and forfeit +1 while present with Major Panno. When with Major Panno at a site, adds one battle destiny. When present with a Scomp link, cancels Counter Assault at a related site where you have a spy or scout.");
        addIcons(Icon.DEATH_STAR_II, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition presentWithMajorPanno = new PresentWithCondition(self, Filters.Major_Panno);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, presentWithMajorPanno, 1));
        modifiers.add(new ForfeitModifier(self, presentWithMajorPanno, 1));
        modifiers.add(new AddsBattleDestinyModifier(self, new AndCondition(new WithCondition(self, Filters.Major_Panno),
                new AtCondition(self, Filters.site)), 1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Counter_Assault)
                && GameConditions.isDuringForceDrainAt(game, Filters.and(Filters.relatedSite(self),
                Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.or(Filters.spy, Filters.scout)))))
                && GameConditions.isAtScompLink(game, self)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}
