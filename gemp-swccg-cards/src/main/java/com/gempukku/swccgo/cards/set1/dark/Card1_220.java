package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractUtinniEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.CantStealModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotApplyAbilityForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Effect
 * Subtype: Utinni
 * Title: Juri Juice
 */
public class Card1_220 extends AbstractUtinniEffect {
    public Card1_220() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.Juri_Juice, Uniqueness.UNIQUE);
        setLore("Popular beverage served in many cantinas and tapcafes. Has intoxicating effect on many species. Favorite drink of Kabe, Chadra-Fan thief of Mos Eisley.");
        setGameText("Deploy on any alien if Cantina, Mos Eisley or Jabba's Sail Barge on table. That alien may not use ability in battles (if Kabe, she also cannot 'steal'). Utinni Effect canceled by moving that alien to one of those sites without 'driving' a vehicle.");
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.alien;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.alien;
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.canSpot(game, self, Filters.or(Filters.Cantina, Filters.Mos_Eisley, Filters.Jabbas_Sail_Barge, Filters.siteOfStarshipOrVehicle(Persona.JABBAS_SAIL_BARGE, false)));
    }

    @Override
    public List<TargetId> getUtinniEffectTargetIds(String playerId, SwccgGame game, PhysicalCard self) {
        return Collections.emptyList();
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter attachedTo = Filters.hasAttached(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotApplyAbilityForBattleDestinyModifier(self, attachedTo));
        modifiers.add(new CantStealModifier(self, Filters.and(Filters.Kabe, attachedTo)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (!GameConditions.cardHasWhileInPlayDataSet(self)) {

            // Check condition(s)
            PhysicalCard attachedTo = self.getAttachedTo();
            if (attachedTo != null
                    && TriggerConditions.movedUsingLandspeed(game, effectResult, Filters.and(Filters.vehicle, Filters.hasDriving(attachedTo)))) {
                // Set Utinni Effect info which just means that the alien drove a vehicle
                game.getGameState().sendMessage(GameUtils.getCardLink(attachedTo) + " drove a vehicle, so " + GameUtils.getCardLink(self) + " cannot be canceled using its game text");
                game.getGameState().activatedCard(null, self);
                self.setWhileInPlayData(new WhileInPlayData());
                return null;
            }

            // Check condition(s)
            if (TriggerConditions.isTableChanged(game, effectResult)
                    && !GameConditions.cardHasWhileInPlayDataSet(self)
                    && GameConditions.isAtLocation(game, self, Filters.or(Filters.Cantina, Filters.Mos_Eisley,
                    Filters.sameSiteAs(self, Filters.Jabbas_Sail_Barge), Filters.siteOfStarshipOrVehicle(Persona.JABBAS_SAIL_BARGE, false)))
                    && GameConditions.canBeCanceled(game, self)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Cancel");
                action.setActionMsg("Cancel " + GameUtils.getCardLink(self));
                // Perform result(s)
                action.appendEffect(
                        new CancelCardOnTableEffect(action, self));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}