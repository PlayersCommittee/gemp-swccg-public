package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToForfeitCardFromTableResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Capital
 * Title: Liberty
 */
public class Card9_076 extends AbstractCapitalStarship {
    public Card9_076() {
        super(Side.LIGHT, 2, 8, 7, 5, null, 3, 9, "Liberty", Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.R);
        setLore("Winged star cruiser. Military transport for technical units. Afforded special care by the Alliance's top engineers.");
        setGameText("May add 5 pilots, 6 passengers, 1 vehicle, and 3 starfighters. Has ship-docking capability. Permanent pilot provides ability of 2. May go to Used Pile instead of Lost Pile when forfeited (unless 'hit').");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.MON_CALAMARI_STAR_CRUISER);
        setPilotCapacity(5);
        setPassengerCapacity(6);
        setVehicleCapacity(1);
        setStarfighterCapacity(3);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isAboutToBeForfeitedToLostPile(game, effectResult, self)
                && !GameConditions.isHit(game, self)) {
            final AboutToForfeitCardFromTableResult result = (AboutToForfeitCardFromTableResult) effectResult;

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " in Used Pile when forfeited");
            // Perform result(s)
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            result.getForfeitCardEffect().setForfeitToUsedPile();
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
