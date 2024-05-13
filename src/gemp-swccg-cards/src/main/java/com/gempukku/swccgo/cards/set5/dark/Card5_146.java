package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromUsedPileOnTopOfCardPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromUsedPileEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Used
 * Title: Levitation Attack
 */
public class Card5_146 extends AbstractUsedInterrupt {
    public Card5_146() {
        super(Side.DARK, 4, Title.Levitation_Attack, Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.U);
        setLore("Vader used his mastery of the Force to levitate objects from around the room. This calculated attack was designed to break Luke's spirit as well as his body.");
        setGameText("If a battle was just initiated where you have a character of ability > 3 present, search your Used Pile for one device which deploys on a character. Add device's destiny number to your total power, then place device on top of Used Pile.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.LEVITATION_ATTACK__SEARCH_FOR_DEVICE;

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.character, Filters.abilityMoreThan(3), Filters.presentInBattle))
                && GameConditions.canSearchUsedPile(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Search Used Pile for device");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ChooseCardFromUsedPileEffect(action, playerId, Filters.and(Filters.device, Filters.deviceOrWeaponThatCanBeDeployedOnCharacters)) {
                                        @Override
                                        protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
                                            float destiny = game.getModifiersQuerying().getDestiny(game.getGameState(), selectedCard);
                                            action.appendEffect(
                                                    new ModifyTotalPowerUntilEndOfBattleEffect(action, destiny, playerId,
                                                            "Adds " + GuiUtils.formatAsString(destiny) + " to total power"));
                                            action.appendEffect(
                                                    new PutCardFromUsedPileOnTopOfCardPileEffect(action, selectedCard, Zone.USED_PILE, false));
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}