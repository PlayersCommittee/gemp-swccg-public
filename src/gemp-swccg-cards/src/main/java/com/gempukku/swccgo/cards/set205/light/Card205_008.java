package com.gempukku.swccgo.cards.set205.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Set 5
 * Type: Starship
 * Subtype: Capital
 * Title: Redemption (V)
 */
public class Card205_008 extends AbstractCapitalStarship {
    public Card205_008() {
        super(Side.LIGHT, 1, 5, 3, 6, null, 4, 6, "Redemption", Uniqueness.UNIQUE, ExpansionSet.SET_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("Nebulon-B frigate used as a mobile medical facility. Extra cargo space and weapon batteries have been modified to allow for more armor and more recovery areas.");
        setGameText("May add 4 pilots and 4 passengers. Permanent pilot provides ability of 2. When deployed, may deploy a non-[Maintenance] Rebel aboard from your Lost Pile.");
        addIcons(Icon.CLOUD_CITY, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_5);
        addModelType(ModelType.MODIFIED_NEBULON_B_FRIGATE);
        setPilotCapacity(4);
        setPassengerCapacity(4);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggersEvenIfUnpiloted(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.REDEMPTION__DOWNLOAD_REBEL_FROM_LOST_PILE;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId, true)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Rebel from Lost Pile");
            action.setActionMsg("Deploy a non-[Maintenance] Rebel aboard " + GameUtils.getCardLink(self) + " from Lost Pile");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToTargetFromLostPileEffect(action, Filters.and(Filters.Rebel, Filters.not(Icon.MAINTENANCE)), Filters.sameCardId(self), false));
            return Collections.singletonList(action);
        }
        return null;
    }
}
