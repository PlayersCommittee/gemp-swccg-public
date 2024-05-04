package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeFlippedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Character
 * Subtype: Rebel
 * Title: Lieutenant Blount
 */
public class Card9_021 extends AbstractRebel {
    public Card9_021() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, Title.Lieutenant_Blount, Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.R);
        setLore("Wingman of Colonel Cracken. Spy and scout. Former agent of the Imperial Security Bureau. Defected and joined Rebel Intelligence. Seasoned combat veteran.");
        setGameText("Adds 2 to power of anything he pilots. When piloting Tala 2, draws one battle destiny if not able to otherwise. While he occupies any Coruscant location, Empire's Sinister Agents is flipped and ISB Operations may not be flipped.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.SPY, Keyword.SCOUT);
        setMatchingStarshipFilter(Filters.Tala_2);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new PilotingCondition(self, Filters.Tala_2), 1));
        modifiers.add(new MayNotBeFlippedModifier(self, new OccupiesCondition(self, Filters.Coruscant_location), Filters.ISB_Operations));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.occupiesLocation(game, self, Filters.Coruscant_location)) {
            PhysicalCard objective = Filters.findFirstActive(game, self, Filters.Empires_Sinister_Agents);
            if (objective != null
                    && GameConditions.canBeFlipped(game, objective)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Flip Empire's Sinister Agents");
                action.setActionMsg("Flip " + GameUtils.getCardLink(objective));
                // Perform result(s)
                action.appendEffect(
                        new FlipCardEffect(action, objective));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
