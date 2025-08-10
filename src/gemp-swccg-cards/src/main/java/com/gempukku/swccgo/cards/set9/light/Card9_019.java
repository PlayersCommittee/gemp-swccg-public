package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.PhaseCondition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Character
 * Subtype: Rebel
 * Title: Keir Santage
 */
public class Card9_019 extends AbstractRebel {
    public Card9_019() {
        super(Side.LIGHT, 3, 2, 2, 2, 4, Title.Keir_Santage, Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.U);
        setLore("Rescued from an Imperial detention center by Wedge Antilles. Rogue Squadron veteran. Assigned to Red Squadron at the Battle of Endor. Coordinates with Rebellion procurement.");
        setGameText("Adds 2 to the power of anything he pilots. When at a system, sector, or docking bay, once during each of your deploy phases, subtracts 2 from deploy cost of your unique (•) X-wing deploying here.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT);
        addKeywords(Keyword.ROGUE_SQUADRON, Keyword.RED_SQUADRON);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        final int permCardId = self.getPermanentCardId();
        Condition condition = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                PhysicalCard self = gameState.findCardByPermanentId(permCardId);

                return modifiersQuerying.getUntilEndOfTurnLimitCounter(self, self.getOwner(), self.getCardId(), GameTextActionId.OTHER_CARD_ACTION_DEFAULT).getUsedLimit() < 1;
            }
        };

        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DeployCostToTargetModifier(self, Filters.and(Filters.your(self), Filters.unique, Filters.X_wing),
            new AndCondition(new AtCondition(self, Filters.or(Filters.system, Filters.sector, Filters.docking_bay)), new PhaseCondition(Phase.DEPLOY, self.getOwner()), condition),
                -2, Filters.here(self)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justDeployedToLocation(game, effectResult, self.getOwner(), Filters.and(Filters.your(self), Filters.unique, Filters.X_wing), Filters.and(Filters.here(self), Filters.or(Filters.system, Filters.sector, Filters.docking_bay)))) {
            //need to account for Santage deploying simultaneously with a X-wing
            PhysicalCard card1 = ((PlayCardResult)effectResult).getPlayedCard();
            PhysicalCard card2 = ((PlayCardResult)effectResult).getOtherPlayedCard();
            if(card1 == null || card2 == null || !(Filters.and(card1).accepts(game, self) || Filters.and(card2).accepts(game, self))) {
                game.getModifiersQuerying().getUntilEndOfTurnLimitCounter(self, self.getOwner(), gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT).incrementToLimit(1, 1);
            }
        }
        return super.getGameTextRequiredAfterTriggers(game, effectResult, self, gameTextSourceCardId);
    }
}
