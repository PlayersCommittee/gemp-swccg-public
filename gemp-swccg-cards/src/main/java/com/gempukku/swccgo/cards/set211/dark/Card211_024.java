package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.BattleResultDeterminedResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 11
 * Type: Starship
 * Subtype: Capital
 * Title: Vengeance V
 */
public class Card211_024 extends AbstractCapitalStarship {
    public Card211_024() {
        super(Side.DARK, 2, 7, 8, 6, null, 3, 8, "Vengeance", Uniqueness.UNIQUE);
        setLore("Part of Darth Vader's Death Squadron. Support vessel for the fleet's starfighters. Cargo areas converted into hangar space for additional TIE fighters.");
        setGameText("May add 6 pilots, 8 passengers, 1 vehicle, and 6 TIEs. Permanent pilot provides ability of 2. Whenever you win a battle here, opponent loses 1 Force.");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_11);
        addModelType(ModelType.IMPERIAL_CLASS_STAR_DESTROYER);
        setPilotCapacity(6);
        setPassengerCapacity(8);
        setVehicleCapacity(1);
        setTIECapacity(6);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.battleResultDetermined(game, effectResult)
                && GameConditions.isDuringBattleAt(game, Filters.here(self))) {
            BattleResultDeterminedResult result = (BattleResultDeterminedResult) effectResult;
            String loser = result.getLoser();
            if (loser != null) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make " + loser + " lose 1 Force");
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, loser, 1));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}