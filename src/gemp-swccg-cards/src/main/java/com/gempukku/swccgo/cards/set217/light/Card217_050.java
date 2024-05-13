package com.gempukku.swccgo.cards.set217.light;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.InPlayDataEqualsCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeStolenModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalForceGenerationModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.ChoiceMadeResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Epic Event
 * Title: The Force Is Strong In My Family
 */
public class Card217_050 extends AbstractEpicEventDeployable {
    public Card217_050() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.The_Force_Is_Strong_In_My_Family, Uniqueness.UNRESTRICTED, ExpansionSet.SET_17, Rarity.V);
        setGameText("Deploy on table (only at start of game) and choose: " +
                "My Father Has It: Anakin (and [Episode I] Obi-Wan) " +
                "I Have It: Luke (and [Set 1] Obi-Wan) " +
                "You Have That Power, Too: Rey (and [Episode VII] Luke) " +
                "[Set 16] Anakin is deploy -1. Your total Force generation is +1. You may not deploy Boss Nass' Chambers or Jedi (except Yoda and the chosen characters). If you just initiated battle involving a Skywalker (or if opponent's Sidious just lost from table), may retrieve 1 Force. Your lightsabers may not be stolen.");
        addIcons(Icon.SKYWALKER, Icon.EPISODE_I, Icon.EPISODE_VII, Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_17);
    }

    private static final String MY_FATHER_HAS_IT = "My Father Has It";
    private static final String I_HAVE_IT = "I Have It";
    private static final String YOU_HAVE_THAT_POWER_TOO = "You Have That Power, Too";

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.isDuringStartOfGame(game);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        final String playerId = self.getOwner();
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setPerformingPlayer(self.getOwner());

            action.appendEffect(
                    new PlayoutDecisionEffect(action, playerId, new MultipleChoiceAwaitingDecision("Choose an option", new String[]{MY_FATHER_HAS_IT, I_HAVE_IT, YOU_HAVE_THAT_POWER_TOO}) {
                        @Override
                        protected void validDecisionMade(int index, final String result) {
                            self.setWhileInPlayData(new WhileInPlayData(result));
                            action.appendEffect(
                                    new SendMessageEffect(action, playerId + " chooses "+result));
                            action.appendEffect(new PassthruEffect(action) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    game.getActionsEnvironment().emitEffectResult(new ChoiceMadeResult(playerId, self, result));
                                    String hero = null;
                                    switch(result) {
                                        case MY_FATHER_HAS_IT:
                                            hero = "Anakin";
                                            break;
                                        case I_HAVE_IT:
                                            hero = "Luke";
                                            break;
                                        case YOU_HAVE_THAT_POWER_TOO:
                                            hero = "Rey";
                                            break;
                                    }
                                    game.getModifiersQuerying().setExtraInformationForArchetypeLabel(playerId, hero);
                                }
                            });
                        }
                    }));

            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {

        if (TriggerConditions.justLost(game, effectResult, Filters.and(Filters.opponents(self), Filters.Sidious))
                || (TriggerConditions.battleInitiated(game, effectResult, playerId)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Skywalker))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter myFatherHasItFilter = Filters.or(Filters.Anakin, Filters.and(Icon.EPISODE_I, Filters.ObiWan));
        Filter iHaveItFilter = Filters.or(Filters.Luke, Filters.and(Icon.VIRTUAL_SET_1, Filters.ObiWan));
        Filter youHaveThatPowerTooFilter = Filters.or(Filters.Rey, Filters.and(Icon.EPISODE_VII, Filters.Luke));

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new TotalForceGenerationModifier(self, 1, self.getOwner()));
        modifiers.add(new MayNotDeployModifier(self, Filters.Boss_Nass_Chambers, self.getOwner()));
        modifiers.add(new MayNotDeployModifier(self, Filters.and(Filters.Jedi, Filters.except(Filters.or(Filters.Yoda, myFatherHasItFilter))), new InPlayDataEqualsCondition(self, MY_FATHER_HAS_IT), self.getOwner()));
        modifiers.add(new MayNotDeployModifier(self, Filters.and(Filters.Jedi, Filters.except(Filters.or(Filters.Yoda, iHaveItFilter))), new InPlayDataEqualsCondition(self, I_HAVE_IT), self.getOwner()));
        modifiers.add(new MayNotDeployModifier(self, Filters.and(Filters.Jedi, Filters.except(Filters.or(Filters.Yoda, youHaveThatPowerTooFilter))), new InPlayDataEqualsCondition(self, YOU_HAVE_THAT_POWER_TOO), self.getOwner()));
        modifiers.add(new DeployCostModifier(self, Filters.and(Icon.VIRTUAL_SET_16, Filters.Anakin), -1));
        modifiers.add(new MayNotBeStolenModifier(self, Filters.and(Filters.your(self), Filters.lightsaber)));
        return modifiers;
    }

    @Override
    public String getDisplayableInformation(SwccgGame game, PhysicalCard self) {
        if (self.getWhileInPlayData() == null)
            return null;

        return "Chosen option: " + self.getWhileInPlayData().getTextValue();
    }
}