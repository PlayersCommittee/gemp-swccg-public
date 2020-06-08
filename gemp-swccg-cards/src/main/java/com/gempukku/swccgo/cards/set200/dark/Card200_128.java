package com.gempukku.swccgo.cards.set200.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.HitCardEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.UsedInterruptModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseCardFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 0
 * Type: Starship
 * Subtype: Starfighter
 * Title: Black 5
 */
public class Card200_128 extends AbstractStarfighter {
    public Card200_128() {
        super(Side.DARK, 3, 2, 2, null, 4, null, 5, Title.Black_5, Uniqueness.UNIQUE);
        setGameText("May add DS-61-5 as pilot. During battle, if about to be lost before the damage segment, is instead 'hit'. If targeted by Watch Your Back!, it is a Used Interrupt. Immune to Don't Get Cocky and attrition < 4.");
        addModelType(ModelType.TIE_LN);
        addIcons(Icon.SPECIAL_EDITION, Icon.VIRTUAL_SET_0);
        addKeywords(Keyword.NO_HYPERDRIVE, Keyword.BLACK_SQUADRON);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.DS_61_5);
    }

    @Override
    protected Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.DS_61_5;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, final int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isAboutToBeLost(game, effectResult, self)
                && GameConditions.isDuringBattle(game)
                && !GameConditions.isDamageSegmentOfBattle(game)) {
            final AboutToLoseCardFromTableResult result = (AboutToLoseCardFromTableResult) effectResult;

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make " + GameUtils.getFullName(self) + " 'hit' instead");
            action.setActionMsg("Make " + GameUtils.getCardLink(self) + " 'hit' instead");
            // Perform result(s)
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            result.getPreventableCardEffect().preventEffectOnCard(self);
                            action.appendEffect(
                                    new HitCardEffect(action, self, self));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new UsedInterruptModifier(self, Filters.and(Filters.Watch_Your_Back, Filters.cardBeingPlayedTargeting(self, self))));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Dont_Get_Cocky));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }
}
