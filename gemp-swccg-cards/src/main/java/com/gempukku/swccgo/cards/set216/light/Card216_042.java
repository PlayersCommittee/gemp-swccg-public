package com.gempukku.swccgo.cards.set216.light;

import com.gempukku.swccgo.cards.AbstractAlienRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PreventEffectOnCardEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.CardTitleAwaitingDecision;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.ReturnCardToHandFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayUnlessImmuneToSpecificTitleModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Character
 * Subtype: Alien/Republic
 * Title: Tarfful
 */
public class Card216_042 extends AbstractAlienRepublic {
    public Card216_042() {
        super(Side.LIGHT, 3, 4, 6, 2, 5, "Tarfful", Uniqueness.UNIQUE, ExpansionSet.SET_16, Rarity.V);
        setLore("Wookiee leader.");
        setGameText("If a battle was just initiated at same site, may name an Interrupt; for remainder of battle, Interrupts with that title may not be played unless they are [Immune to Sense]. Once per game, if Yoda about to be lost, may place him in owner's hand instead.");
        setSpecies(Species.WOOKIEE);
        addKeywords(Keyword.LEADER);
        addIcons(Icon.WARRIOR, Icon.EPISODE_I, Icon.VIRTUAL_SET_16);
    }

    @Override
    public final boolean hasSpecialDefenseValueAttribute() {
        return true;
    }

    @Override
    public final float getSpecialDefenseValue() {
        return 4;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();

        //Once per game, if Yoda about to be lost, may take him into hand instead.
        GameTextActionId gameTextActionId = GameTextActionId.TARFFUL__TAKE_YODA_INTO_HAND;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && (TriggerConditions.isAboutToBeLostIncludingAllCardsSituation(game, effectResult, Filters.Yoda)
                || TriggerConditions.isAboutToBeForfeitedToLostPile(game, effectResult, Filters.Yoda))) {

            PhysicalCard yoda = ((AboutToLeaveTableResult) effectResult).getCardAboutToLeaveTable();
            if (yoda != null) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Return Yoda to hand");
                action.setActionMsg("Return " + GameUtils.getCardLink(yoda) + " to hand");
                action.appendUsage(
                        new OncePerGameEffect(action));
                action.appendEffect(
                        new ReturnCardToHandFromTableEffect(action, yoda));
                action.appendEffect(
                        new PreventEffectOnCardEffect(action, ((AboutToLeaveTableResult) effectResult).getPreventableCardEffect(), yoda, null));
                actions.add(action);
            }
        }

        //If a battle was just initiated here, may name an Interrupt; Interrupts with that title may not be played for remainder of battle (unless it is [Immune to Sense]).
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.sameSite(self))
                && GameConditions.isDuringBattleWithParticipant(game, self)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);

            action.appendTargeting(
                    new PlayoutDecisionEffect(action, playerId,
                            new CardTitleAwaitingDecision(game, "Choose an Interrupt card title", CardCategory.INTERRUPT) {
                                @Override
                                protected void cardTitleChosen(final String cardTitle) {
                                    action.appendEffect(new AddUntilEndOfBattleModifierEffect(action,
                                            new MayNotPlayUnlessImmuneToSpecificTitleModifier(self, Filters.and(CardType.INTERRUPT, Filters.title(cardTitle)), Title.Sense),
                                            "For remainder of battle, " + cardTitle + " may not be played unless it is immune to Sense"));
                                }
                            }
                    )
            );

            actions.add(action);
        }
        return actions;
    }
}
