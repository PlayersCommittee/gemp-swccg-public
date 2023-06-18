package com.gempukku.swccgo.cards.set221.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostForceResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 21
 * Type: Effect
 * Title: A Remote Planet (V)
 */
public class Card221_044 extends AbstractNormalEffect {
    public Card221_044() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, "A Remote Planet", Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setVirtualSuffix(true);
        setLore("In unfamiliar and potentially hostile territory, Qui-Gon knew that success would require patience and caution.");
        setGameText("If Credits Will Do Fine on table, deploy on Mos Espa. Opponent's first Force lost to a Force drain here is stacked on Credits Will Do Fine. If you just deployed Amidala, Jar Jar, or a Jedi here, may [upload] Either Way, You Win.");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I, Icon.TATOOINE, Icon.VIRTUAL_SET_21);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, Filters.Credits_Will_Do_Fine);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Mos_Espa;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.A_REMOTE_PLANET_V__UPLOAD_EITHER_WAY_YOU_WIN;

        // Check condition(s)
        if (TriggerConditions.justDeployedTo(game, effectResult, playerId, Filters.or(Filters.Amidala, Filters.Jar_Jar, Filters.Jedi), Filters.here(self))
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Either Way, You Win into hand");
            action.setActionMsg("Take Either Way, You Win into hand from Reserve Deck");

            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.title("Either Way, You Win"), true));

            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justLostForceFromForceDrainAt(game, effectResult, game.getOpponent(self.getOwner()), Filters.here(self), true)) {
            PhysicalCard credits = Filters.findFirstActive(game, self, Filters.Credits_Will_Do_Fine);

            if (credits != null) {
                LostForceResult lostForceResult = (LostForceResult) effectResult;
                PhysicalCard cardToStack = lostForceResult.getCardLost();
                if (cardToStack != null) {
                    RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                    action.appendEffect(
                            new StackOneCardFromLostPileEffect(action, cardToStack, credits, true, true, true));
                    return Collections.singletonList(action);
                }
            }
        }

        return null;
    }
}