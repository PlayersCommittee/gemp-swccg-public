package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.conditions.CardsInHandFewerThanCondition;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 8
 * Type: Objective
 * Title: Wookiee Slaving Operation / Indentured To The Empire
 */
public class Card601_029 extends AbstractObjective {
    public Card601_029() {
        super(Side.DARK, 0, Title.Wookiee_Slaving_Operation);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Kashyyyk system, Slaving Camp Headquarters, and [Block 8] Special Delivery.\n" +
                "For remainder of game, your Trandoshans are slavers. Scum And Villainy may deploy on Slaving Camp Headquarters and may not be canceled while you occupy that site. While you have < 13 cards in hand, your non-unique slavers are immune to Grimtaash.\n" +
                "Flip this card if your slavers control two Kashyyyk battlegrounds and opponent controls no Kashyyyk locations.");
        addIcons(Icon.CLOUD_CITY, Icon.LEGACY_BLOCK_8);
        setAsLegacy(true);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Kashyyyk_system, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Kashyyyk system to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.title(Title.Slaving_Camp_Headquarters), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Slaving Camp Headquarters to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.and(Icon.LEGACY_BLOCK_8, Filters.Special_Delivery), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose [Block 8] Special Delivery to deploy";
                    }
                });
        return action;
    }



    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        //your Trandoshans are slavers
        modifiers.add(new KeywordModifier(self, Filters.species(Species.TRANDOSHAN), Keyword.SLAVER));
        //Scum And Villainy may deploy on Slaving Camp Headquarters and
        modifiers.add(new ScumAndVillainyMayDeployAttachedModifier(self, Filters.Slaving_Camp_Headquarters));
        //may not be canceled while you occupy that site.
        modifiers.add(new MayNotBeCanceledModifier(self, Filters.Scum_And_Villainy, new OccupiesCondition(self.getOwner(), Filters.Slaving_Camp_Headquarters)));
        //While you have < 13 cards in hand, your non-unique slavers are immune to Grimtaash.
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.your(self), Filters.non_unique, Filters.slaver),
                new CardsInHandFewerThanCondition(self.getOwner(), 13), Title.Grimtaash));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        //Flip this card if your slavers control two Kashyyyk battlegrounds and opponent controls no Kashyyyk locations.

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.controlsWith(game, self, playerId, 2, Filters.and(Filters.Kashyyyk_location, Filters.battleground),
                        SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.slaver)
                && !GameConditions.controls(game, opponent, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Kashyyyk_location)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}