package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.UseDeviceEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Device
 * Title: Hidden Compartment
 */
public class Card6_050 extends AbstractCharacterDevice {
    public Card6_050() {
        super(Side.LIGHT, 5, "Hidden Compartment");
        setLore("Standard astromech internal cargo area measures 20 centimeters by 8 centimeters. Some models have a custom compressed-air launcher for shooting flares.");
        setGameText("Deploy on any R-unit droid. At start of a battle, you may 'react' by deploying one character weapon (at normal use of the Force) from Reserve Deck on a warrior present; reshuffle.");
        addIcons(Icon.JABBAS_PALACE);
        addKeywords(Keyword.DEVICE_THAT_DEPLOYS_ON_DROIDS);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.R_unit);
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.R_unit;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.HIDDEN_COMPARTMENT__DOWNLOAD_CHARACTER_WEAPON_AS_REACT;
        Filter filter = Filters.and(Filters.your(self), Filters.warrior, Filters.present(self));

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult, opponent)
                && GameConditions.isInBattle(game, self)
                && GameConditions.isDuringBattleWithParticipant(game, filter)
                && GameConditions.canUseDevice(game, self)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, true)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy character weapon as 'react' from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new UseDeviceEffect(action, self));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.character_weapon, filter, false, true, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}