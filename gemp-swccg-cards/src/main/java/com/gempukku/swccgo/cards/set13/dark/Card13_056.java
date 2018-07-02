package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardAboardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Set: Reflections III
 * Type: Vehicle
 * Subtype: Combat
 * Title: Blizzard 4
 */
public class Card13_056 extends AbstractCombatVehicle {
    public Card13_056() {
        super(Side.DARK, 4, 5, 8, 4, null, 1, 5, "Blizzard 4", Uniqueness.UNIQUE);
        setLore("Staffed by an experienced crew. Blizzard 4's role in all engagements is to seek out new terrain uncovered by the initial offense thrust and unload troops to hold it. Enclosed.");
        setGameText("May add 6 passengers. Permanent pilots provide ability of 4. When deployed, you may deploy (for free) an Imperial warrior aboard from your Reserve Deck; reshuffle. End of your turn: Use 1 Force to maintain OR Place out of play.");
        addModelType(ModelType.AT_AT);
        addIcons(Icon.REFLECTIONS_III, Icon.SCOMP_LINK, Icon.MAINTENANCE);
        addIcon(Icon.PILOT, 2);
        addKeywords(Keyword.ENCLOSED);
        setPassengerCapacity(6);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        List<AbstractPermanentAboard> permanentsAboard = new ArrayList<AbstractPermanentAboard>();
        permanentsAboard.add(new AbstractPermanentPilot(2) {});
        permanentsAboard.add(new AbstractPermanentPilot(2) {});
        return permanentsAboard;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggersEvenIfUnpiloted(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.BLIZZARD_4__DOWNLOAD_IMPERIAL_WARRIOR;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy an Imperial warrior from Reserve Deck");
            action.setActionMsg("Deploy an Imperial warrior aboard " + GameUtils.getCardLink(self) + " from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardAboardFromReserveDeckEffect(action, Filters.and(Filters.Imperial, Filters.warrior), Filters.sameCardId(self), true, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected StandardEffect getGameTextMaintenanceMaintainCost(Action action, final String playerId) {
        return new UseForceEffect(action, playerId, 1);
    }
}
