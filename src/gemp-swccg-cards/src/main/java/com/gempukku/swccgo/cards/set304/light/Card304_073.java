package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AllAbilityOnTableProvidedByCondition;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
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
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Effect
 * Title: Hive Of Scum And Villainy
 */
public class Card304_073 extends AbstractNormalEffect {
    public Card304_073() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.Hive_of_Scum_and_Villainy, Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("");
        setGameText("Deploy on Claudius's Throne Room. While all your ability on table is provided by aliens and independent starship pilots, your aliens and starships deploy -1 and you retrieve 2 Force whenever you initiate battle. (Immune to Alter if you control at least three Claudius's Palace sites.)");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        Collection<PhysicalCard> hiveOfScumAndVillainyMayDeployAttached = game.getModifiersQuerying().getActiveCardsAffectedByModifier(game.getGameState(), ModifierType.HIVE_OF_SCUM_AND_VILLAINY_MAY_DEPLOY_HERE);
        return Filters.or(Filters.Claudius_Throne_Room, Filters.in(hiveOfScumAndVillainyMayDeployAttached));
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
        modifiers.add(new ImmuneToTitleModifier(self, new ControlsCondition(playerId, 3, Filters.Claudius_Palace_site), Title.Alter));
        return modifiers;
    }
}