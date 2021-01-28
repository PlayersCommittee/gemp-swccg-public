package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AllAbilityOnTableProvidedByCondition;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Effect
 * Title: Scum And Villainy
 */
public class Card6_149 extends AbstractNormalEffect {
    public Card6_149() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.Scum_And_Villainy, Uniqueness.UNIQUE);
        setLore("A relief in Nal Hutta sandstone. Hand-carved by slaves of the great Hutt artist Dreyba. Commissioned by Jabba to illustrate his vast influence. Titled 'Ne Ganna Dateel Jabba.'");
        setGameText("Deploy on Audience Chamber. While all your ability on table is provided by aliens and independent starship pilots, your aliens and starships deploy -1 and you retrieve 2 Force whenever you initiate battle. (Immune to Alter if you control at least three Jabba's Palace sites.)");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        Filter filter = Filters.Audience_Chamber;
        if (game.getModifiersQuerying().hasGameTextModification(game.getGameState(), self, ModifyGameTextType.LEGACY__SCUM_AND_VILLAINY__MAY_DEPLOY_ON_SLAVING_CAMP_HEADQUARTERS))
            filter = Filters.or(filter, Filters.title(Title.Slaving_Camp_Headquarters));
        //if (game.getModifiersQuerying().hasGameTextModification(game.getGameState(), self, ModifyGameTextType.LEGACY__SCUM_AND_VILLAINY__MAY_DEPLOY_ON_JABBAS_SAIL_BARGE))
        //filter = Filters.or(filter, Filters.Jabbas_Sail_Barge);
        return filter;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, Filters.and(Filters.your(playerId), Filters.or(Filters.alien, Filters.starship)),
                new AllAbilityOnTableProvidedByCondition(self, playerId, Filters.or(Filters.alien, Filters.and(Icon.INDEPENDENT, Filters.starship),
                        Filters.piloting(Filters.and(Icon.INDEPENDENT, Filters.starship)))), -1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult, playerId)
                && GameConditions.isAllAbilityOnTableProvidedBy(game, self, playerId, Filters.or(Filters.alien,
                Filters.and(Icon.INDEPENDENT, Filters.starship), Filters.piloting(Filters.and(Icon.INDEPENDENT, Filters.starship))))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve 2 Force");
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 2) {
                        @Override
                        public Collection<PhysicalCard> getAdditionalCardsInvolvedInForceRetrieval() {
                            return Filters.filterActive(game, null, Filters.and(Filters.your(playerId), Filters.participatingInBattle));
                        }
                        @Override
                        public boolean isDueToInitiatingBattle() {
                            return true;
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, new ControlsCondition(playerId, 3, Filters.Jabbas_Palace_site), Title.Alter));
        return modifiers;
    }
}