package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployStackedCardToTargetEffect;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeAboardModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Cloud City
 * Type: Starship
 * Subtype: Capital
 * Title: Redemption
 */
public class Card5_087 extends AbstractCapitalStarship {
    public Card5_087() {
        super(Side.LIGHT, 1, 6, 4, 5, null, 4, 6, "Redemption", Uniqueness.UNIQUE);
        setLore("Nebulon-B frigate used as a mobile medical facility. Extra cargo space and weapon batteries have been modified to allow for more armor and more recovery areas.");
        setGameText("May add 4 pilots and 4 passengers. Has ship-docking capability. Permanent pilot provides ability of 1. Your medical droids and Bacta Tank 'patients' may deploy aboard for free.");
        addIcons(Icon.CLOUD_CITY, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.MODIFIED_NEBULON_B_FRIGATE);
        setPilotCapacity(4);
        setPassengerCapacity(4);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeAboardModifier(self, Filters.and(Filters.your(self), Filters.medical_droid), self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActionsEvenIfUnpiloted(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isPhaseForPlayer(game, Phase.DEPLOY, playerId)) {
            PhysicalCard bactaTank = Filters.findFirstActive(game, self, Filters.Bacta_Tank);
            if (bactaTank != null) {
                List<PhysicalCard> patients = game.getGameState().getStackedCards(bactaTank);
                if (!patients.isEmpty()) {
                    PhysicalCard patient = patients.get(0);
                    if (Filters.deployableToTarget(self, Filters.sameCardId(self), true, 0).accepts(game, patient)) {

                        final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                        action.setText("Deploy Bacta Tank 'patient' aboard");
                        // Perform result(s)
                        action.appendEffect(
                                new DeployStackedCardToTargetEffect(action, patient, Filters.sameCardId(self), true));
                        return Collections.singletonList(action);
                    }
                }
            }
        }
        return null;
    }
}
