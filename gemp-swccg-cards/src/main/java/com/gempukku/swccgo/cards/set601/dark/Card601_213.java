package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.CalculationTotalModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 2
 * Type: Vehicle
 * Subtype: Combat
 * Title: Blizzard 2 (V)
 */
public class Card601_213 extends AbstractCombatVehicle {
    public Card601_213() {
        super(Side.DARK, 2, 6, 6, 8, null, 1, 6, Title.Blizzard_2, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("AT-AT commanded by the treacherous General Nevar before he was assassinated. Fortified with an extra layer of armor by the paranoid general. Enclosed.");
        setGameText("May add 1 pilot and 8 passengers. Permanent pilot aboard provides ability of 2. During your control phase, may [upload] one Trample or Walker Barrage. Walker Barrage total is +2. Immune to attrition < 4.");
        addModelType(ModelType.AT_AT);
        addIcons(Icon.HOTH, Icon.PILOT, Icon.SCOMP_LINK, Icon.LEGACY_BLOCK_2);
        addKeywords(Keyword.ENCLOSED);
        setPilotCapacity(1);
        setPassengerCapacity(8);
        setAsLegacy(true);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CalculationTotalModifier(self, Filters.and(Filters.Walker_Barrage, Filters.cardBeingPlayedTargeting(self, self)), 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.BLIZZARD_2__UPLOAD_TRAMPLE_OR_WALKER_BARRAGE;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a Trample or Walker Barrage into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Trample, Filters.Walker_Barrage), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
