package com.gempukku.swccgo.cards.set217.dark;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.FailCostEffect;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Epic Event
 * Title: Revenge Of The Sith
 */
public class Card217_020 extends AbstractEpicEventDeployable {
    public Card217_020() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Revenge_Of_The_Sith);
        setGameText("Deploy on table (only at start of game) and choose an apprentice: " +
                "Maul: Deploy Desert Landing Site. " +
                "Dooku: Deploy Invisible Hand: Bridge. " +
                "Vader: Deploy Vader's Castle. " +
                "You may not deploy Dark Jedi except [Episode I] Sidious and the chosen apprentice. Your [Episode I] Sidious and the chosen apprentice gain [Sith]. " +
                "A Sith Legend, Always Two There Are, and Sith are destiny +2. " +
                "If a Jedi was just lost from same location as your Dark Jedi, opponent loses 1 Force. " +
                "Opponent may not cancel or reduce Force drains at their battlegrounds where you have a Dark Jedi.");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_17);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.isDuringStartOfGame(game);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new ArrayList<>();

        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setPerformingPlayer(self.getOwner());

            final String MAUL = "Maul";
            final String DOOKU = "Dooku";
            final String VADER = "Vader";
            final String NO_VALID_CHOICE = "No valid choice";

            List<PhysicalCard> reserveDeck = game.getGameState().getReserveDeck(self.getOwner());
            List<String> possible = new LinkedList<>();
            if (!Filters.filter(reserveDeck, game, Filters.Desert_Landing_Site).isEmpty()) {
                possible.add(MAUL);
            }
            if (!Filters.filter(reserveDeck, game, Filters.Invisible_Hand_Bridge).isEmpty()) {
                possible.add(DOOKU);
            }
            if (!Filters.filter(reserveDeck, game, Filters.Vaders_Castle).isEmpty()) {
                possible.add(VADER);
            }
            if (possible.size() == 0)
                possible.add(NO_VALID_CHOICE);


            String[] possibleResults = possible.toArray(new String[0]);

            action.appendTargeting(
                    new PlayoutDecisionEffect(action, self.getOwner(), new MultipleChoiceAwaitingDecision("Choose an apprentice", possibleResults) {
                        @Override
                        protected void validDecisionMade(int index, String result) {
                            Filter siteFilter = null;
                            Filter apprenticeFilter = null;

                            switch (result) {
                                case MAUL:
                                    siteFilter = Filters.Desert_Landing_Site;
                                    apprenticeFilter = Filters.Maul;
                                    break;
                                case DOOKU:
                                    siteFilter = Filters.Invisible_Hand_Bridge;
                                    apprenticeFilter = Filters.Dooku;
                                    break;
                                case VADER:
                                    siteFilter = Filters.Vaders_Castle;
                                    apprenticeFilter = Filters.Vader;
                                    break;
                                case NO_VALID_CHOICE:
                                    action.appendEffect(new FailCostEffect(action));
                                    return;
                            }
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, siteFilter, false)
                            );
                            action.appendEffect(
                                    new AddUntilEndOfGameModifierEffect(action,
                                            new KeywordModifier(self, apprenticeFilter, Keyword.SITH_APPRENTICE), " chooses " + result + " as the apprentice")
                            );
                            action.appendEffect(
                                    new SetWhileInPlayDataEffect(action, self, new WhileInPlayData(result))
                            );

                            game.getModifiersQuerying().setExtraInformationForArchetypeLabel(self.getOwner(), result);
                        }
                    })
            );

            actions.add(action);
        }

        String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.justLostFromLocation(game, effectResult, Filters.Jedi, Filters.sameSiteAs(self, Filters.Dark_Jedi))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make opponent lose 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new MayNotPlayModifier(self, Filters.and(Filters.Dark_Jedi, Filters.except(Filters.and(Icon.EPISODE_I, Filters.Sidious)), Filters.except(Filters.Sith_Apprentice)), self.getOwner()));
        modifiers.add(new AddCardTypeModifier(self, Filters.or(Filters.and(Filters.your(self), Icon.EPISODE_I, Filters.Sidious), Filters.Sith_Apprentice), CardType.SITH));
        modifiers.add(new DestinyModifier(self, Filters.or(Filters.A_Sith_Legend, Filters.Always_Two_There_Are, Filters.Sith), 2));
        modifiers.add(new ForceDrainsMayNotBeReducedModifier(self, Filters.and(Filters.opponents(self), Filters.battleground, Filters.occupiesWith(playerId, self, Filters.Dark_Jedi)), opponent, playerId));
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, Filters.and(Filters.opponents(self), Filters.battleground, Filters.occupiesWith(playerId, self, Filters.Dark_Jedi)), opponent, playerId));
        return modifiers;
    }

    @Override
    public String getDisplayableInformation(SwccgGame game, PhysicalCard self) {
        if (self.getWhileInPlayData() == null)
            return null;

        return "Chosen Apprentice is " + self.getWhileInPlayData().getTextValue();
    }
}
