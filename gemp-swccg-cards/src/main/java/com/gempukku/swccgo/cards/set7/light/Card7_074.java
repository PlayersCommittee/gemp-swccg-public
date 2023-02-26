package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.ChangePlayedInterruptSubtypeEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.actions.PlayCardState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LookedAtCardsInCardPileResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Effect
 * Title: Scrambled Transmission
 */
public class Card7_074 extends AbstractNormalEffect {
    public Card7_074() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Scrambled_Transmission, Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.U);
        setLore("The Rebel Alliance employs sophisticated jamming technology to block Imperial communications.");
        setGameText("Deploy on your side of table. Shocking Information is immune to Sense. Any interrupt that examines cards in your Used or Force Pile is lost. Once during each of your control phases, may take one Shocking Information into hand from Reserve Deck; reshuffle. (Immune to Alter.)");
        addIcons(Icon.SPECIAL_EDITION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.SCRAMBLED_TRANSMISSION__UPLOAD_SHOCKING_INFORMATION;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Shocking Information into hand from Reserve Deck");
            action.setActionMsg("Take Shocking Information into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Shocking_Information, true));
            actions.add(action);
        }


        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Shocking_Information, Title.Sense));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {

        if (TriggerConditions.justLookedAtCardsInCardPile(game, effectResult, self.getOwner(), Zone.USED_PILE)
                || TriggerConditions.justLookedAtCardsInCardPile(game, effectResult, self.getOwner(), Zone.FORCE_PILE)) {

            PhysicalCard source = ((LookedAtCardsInCardPileResult)effectResult).getSource();

            PlayCardState playCardState = game.getGameState().getTopPlayCardState(self);

            if (playCardState != null
                    && Filters.Interrupt.accepts(game, playCardState.getPlayCardAction().getPlayedCard())
                    && source!=null
                    && Filters.sameCardId(source).accepts(game, playCardState.getPlayCardAction().getPlayedCard())) {
                PlayInterruptAction playInterruptAction = (PlayInterruptAction) playCardState.getPlayCardAction();

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Make "+ GameUtils.getCardLink(playCardState.getPlayCardAction().getPlayedCard()) + " a Lost Interrupt");
                action.appendEffect(
                        new ChangePlayedInterruptSubtypeEffect(action, playInterruptAction, CardSubtype.LOST));
                return Collections.singletonList(action);
            }
        }

        return null;
    }
}