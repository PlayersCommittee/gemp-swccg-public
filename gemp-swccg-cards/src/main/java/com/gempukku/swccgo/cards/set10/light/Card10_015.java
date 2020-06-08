package com.gempukku.swccgo.cards.set10.light;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.UseDeviceEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifiersMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.LostIfAboutToBeStolenModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeDisarmedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.*;

/**
 * Set: Reflections II
 * Type: Device
 * Title: Obi-Wan's Journal
 */
public class Card10_015 extends AbstractCharacterDevice {
    public Card10_015() {
        super(Side.LIGHT, 2, "Obi-Wan's Journal", Uniqueness.UNIQUE);
        setLore("Written by Obi-Wan Kenobi. Used by Luke to construct his lightsaber. Contained instructions on building required tools as well. Keyed to self-destruct if not opened by Luke.");
        setGameText("Deploy on Luke or Obi-Wan. Your characters present armed with a unique (•) lightsaber Weapon card may not be Disarmed, once per battle may cancel a weapon destiny just drawn, and that lightsaber's Force drain modifiers may not be canceled. Lost if about to be stolen.");
        addIcons(Icon.REFLECTIONS_II);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.or(Filters.Luke, Filters.ObiWan));
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.any;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeDisarmedModifier(self, Filters.and(Filters.your(self), Filters.character, Filters.present(self), Filters.hasAttached(Filters.and(Filters.unique, Filters.lightsaber)))));
        modifiers.add(new ForceDrainModifiersMayNotBeCanceledModifier(self, Filters.and(Filters.unique, Filters.lightsaber, Filters.attachedTo(Filters.and(Filters.your(self), Filters.character, Filters.present(self))))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new LostIfAboutToBeStolenModifier(self));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (!GameConditions.isDuringBattle(game)) {
            self.setWhileInPlayData(null);
            return null;
        }

        // Check condition(s)
        if (TriggerConditions.isWeaponDestinyJustDrawn(game, effectResult)
                && GameConditions.isDuringBattle(game)
                && GameConditions.canCancelDestiny(game, playerId)) {
            BattleState battleState = game.getGameState().getBattleState();
            List<PhysicalCard> cardsToAlreadyUseDevice = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getPhysicalCards() : null;
            if (cardsToAlreadyUseDevice == null) {
                cardsToAlreadyUseDevice = new ArrayList<PhysicalCard>();
                self.setWhileInPlayData(new WhileInPlayData(cardsToAlreadyUseDevice));
            }
            Collection<PhysicalCard> characters = Filters.filter(battleState.getAllCardsParticipating(), game, Filters.and(Filters.your(self), Filters.character, Filters.present(self),
                    Filters.not(Filters.in(cardsToAlreadyUseDevice)), Filters.hasAttached(Filters.and(Filters.unique, Filters.lightsaber)), Filters.canUseDevice(self)));
            if (!characters.isEmpty()) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Cancel weapon destiny");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose character with unique lightsaber", characters) {
                            @Override
                            protected void cardSelected(PhysicalCard selectedCard) {
                                action.addAnimationGroup(selectedCard);
                                self.getWhileInPlayData().getPhysicalCards().add(selectedCard);
                                action.setActionMsg("Have " + GameUtils.getCardLink(selectedCard) + " cancel weapon destiny");
                                // Update usage limit(s)
                                action.appendUsage(
                                        new UseDeviceEffect(action, selectedCard, self));
                                // Perform result(s)
                                action.appendEffect(
                                        new CancelDestinyEffect(action));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }

        return null;
    }
}