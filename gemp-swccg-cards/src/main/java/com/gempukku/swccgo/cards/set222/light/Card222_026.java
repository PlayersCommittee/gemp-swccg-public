package com.gempukku.swccgo.cards.set222.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 22
 * Type: Effect
 * Title: Reflection (V)
 */
public class Card222_026 extends AbstractNormalEffect {
    public Card222_026() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, Title.Reflection, Uniqueness.UNIQUE, ExpansionSet.SET_22, Rarity.V);
        setVirtualSuffix(true);
        setLore("It was hard to imagine the enormous losses the Alliance suffered during the Battle of Hoth. Leia contemplated what she could do to help the Rebellion recover.");
        setGameText("Deploy on Rebel leader Leia. She is defense value and immunity to attrition +1. " +
                "While at a battleground, Force drain +1 here and, whenever you initiate a battle with a Rebel of ability < 5, " +
                "retrieve 1 Force (2 if Leia and an Imperial participating). Lost if a Jedi 'communing.' [Immune to Alter].");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
        addIcons(Icon.DAGOBAH, Icon.VIRTUAL_SET_22);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.Rebel_leader, Filters.Leia);
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.and(Filters.Rebel_leader, Filters.Leia);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefenseValueModifier(self, Filters.hasAttached(self), 1));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, Filters.hasAttached(self), 1));
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), new AtCondition(self, Filters.battleground), 1, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new ArrayList<>();
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult, playerId)
                && GameConditions.isAtLocation(game, self, Filters.battleground)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.and(Filters.Rebel, Filters.abilityLessThan(5))))) {
            int numForceToRetrieve = GameConditions.isDuringBattleWithParticipant(game,
                    Filters.and(Filters.Leia, Filters.with(self, Filters.and(Filters.opponents(self), Filters.Imperial)))) ? 2 : 1;

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve " + numForceToRetrieve + " Force");
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, numForceToRetrieve) {
                        @Override
                        public Collection<PhysicalCard> getAdditionalCardsInvolvedInForceRetrieval() {
                            return Filters.filterActive(game, null, Filters.and(Filters.your(self), Filters.participatingInBattle));
                        }

                        @Override
                        public boolean isDueToInitiatingBattle() {
                            return true;
                        }
                    });
            actions.add(action);
        }

        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canSpot(game, self, Filters.Communing)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Make lost");
            action.setActionMsg("Make " + GameUtils.getCardLink(self) + " lost");
            // Perform result(s)
            action.appendEffect(
                    new LoseCardFromTableEffect(action, self));
            actions.add(action);
        }

        return actions;
    }
}
