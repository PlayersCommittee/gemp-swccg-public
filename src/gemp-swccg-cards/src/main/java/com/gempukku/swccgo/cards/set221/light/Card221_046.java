package com.gempukku.swccgo.cards.set221.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.AddDestinyToAttritionEffect;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardInLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Character
 * Subtype: Alien
 * Title: Anakin Skywalker, Junkyard Slave
 */
public class Card221_046 extends AbstractAlien {
    public Card221_046() {
        super(Side.LIGHT, 6, 4, 2, 4, 6, "Anakin Skywalker, Junkyard Slave", Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setLore("Mechanic.");
        setGameText("[Pilot] 2. Deploys only to Skywalker Hut or Watto's Junkyard. During battle, may cause a 'credit' to be lost to choose: if with Obi-Wan, add one destiny to total power; if with Qui-Gon, add one battle destiny; if with Shmi, add one destiny to attrition.");
        addPersona(Persona.ANAKIN);
        addIcons(Icon.EPISODE_I, Icon.PILOT, Icon.TATOOINE, Icon.VIRTUAL_SET_21);
        addKeywords(Keyword.SLAVE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.Skywalker_Hut, Filters.Wattos_Junkyard);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringBattleWithParticipant(game, self)
                && GameConditions.canSpot(game, self, Filters.hasStacked(Filters.creditCard))) {

            PhysicalCard credits = Filters.findFirstActive(game, self, Filters.hasStacked(Filters.creditCard));

            if (credits != null) {
                final String ATTRITION = "Add one destiny to attrition";
                final String POWER = "Add one destiny to total power";
                final String BATTLE_DESTINY = "Add one battle destiny";

                List<String> possible = new LinkedList<>();
                if (GameConditions.isDuringBattleWithParticipant(game, Filters.Shmi)
                        && GameConditions.canAddDestinyDrawsToAttrition(game, playerId)) {
                    possible.add(ATTRITION);
                }
                if (GameConditions.isDuringBattleWithParticipant(game, Filters.ObiWan)
                        && GameConditions.canAddDestinyDrawsToPower(game, playerId)) {
                    possible.add(POWER);
                }
                if (GameConditions.isDuringBattleWithParticipant(game, Filters.QuiGon)
                        && GameConditions.canAddBattleDestinyDraws(game, self)) {
                    possible.add(BATTLE_DESTINY);
                }


                if (possible.size() > 0) {
                    final String[] possibleResults = possible.toArray(new String[0]);

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                    action.setText("Place credit in Lost Pile");
                    // Update usage limit(s)
                    action.appendUsage(
                            new OncePerBattleEffect(action));
                    // Pay Costs
                    action.appendTargeting(
                            new ChooseStackedCardEffect(action, playerId, credits, Filters.any, true) {
                                @Override
                                protected void cardSelected(PhysicalCard selectedCard) {
                                    action.appendCost(
                                            new PutStackedCardInLostPileEffect(action, playerId, selectedCard, false));

                                    action.appendEffect(
                                            new PlayoutDecisionEffect(action, playerId, new MultipleChoiceAwaitingDecision("Choose option", possibleResults) {
                                                @Override
                                                protected void validDecisionMade(int index, String result) {
                                                    switch (result) {
                                                        case ATTRITION:
                                                            action.appendEffect(
                                                                    new AddDestinyToAttritionEffect(action, 1, playerId));
                                                            break;
                                                        case POWER:
                                                            action.appendEffect(
                                                                    new AddDestinyToTotalPowerEffect(action, 1, playerId));
                                                            break;
                                                        case BATTLE_DESTINY:
                                                            action.appendEffect(
                                                                    new AddBattleDestinyEffect(action, 1, playerId));
                                                            break;
                                                        default:
                                                    }
                                                }
                                            }));
                                }
                            });
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}
