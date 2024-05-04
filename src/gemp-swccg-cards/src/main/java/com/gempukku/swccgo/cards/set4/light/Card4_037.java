package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotApplyAbilityForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: Scramble
 */
public class Card4_037 extends AbstractNormalEffect {
    public Card4_037() {
        super(Side.LIGHT, 3, PlayCardZoneOption.OPPONENTS_SIDE_OF_TABLE, Title.Scramble, Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.U);
        setLore("Pilots at ground bases throughout the Empire were immediately recalled to their ships to assist in the capture of the Rebels fleeing Echo Base on Hoth.");
        setGameText("Deploy on opponent's side of table. Opponent's pilots (except Vader) present at sites may not use ability to draw battle destiny. Effect canceled when opponent has no pilots at sites.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotApplyAbilityForBattleDestinyModifier(self, Filters.and(Filters.opponents(self),
                Filters.pilot, Filters.except(Filters.Vader), Filters.presentAt(Filters.site), Filters.canBeTargetedBy(self))));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeCanceled(game, self)
                && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.opponents(self), Filters.pilot, Filters.at(Filters.site)))) {

            // Perform result(s)
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Cancel");
            action.setActionMsg("Cancel " + GameUtils.getCardLink(self));
            action.appendEffect(
                    new CancelCardOnTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}