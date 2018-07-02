package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForRemainderOfGameData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameActionProxyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitValueToUseWhenForfeitedModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseCardFromTableResult;

import java.util.*;

/**
 * Set: Set 0
 * Type: Effect
 * Title: K'lor'slug (V)
 */
public class Card200_042 extends AbstractNormalEffect {
    public Card200_042() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "K'lor'slug", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Dejarik hologram of venomous swamp creature from Noe'ha'on. Keen senses of smell and vision. Dangerous hunter. Lays eggs - hundreds of ravenously hungry hatchlings.");
        setGameText("Deploy on table. If your character, starship, or vehicle in battle is about to be lost before the damage segment, it is instead lost at end of battle (if forfeited, forfeit for 0). [Immune to Alter]");
        addIcons(Icon.VIRTUAL_SET_0);
        addKeywords(Keyword.DEJARIK, Keyword.HOLOGRAM);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, final int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isAboutToBeLost(game, effectResult, Filters.and(Filters.your(self), Filters.or(Filters.character, Filters.starship, Filters.vehicle), Filters.participatingInBattle))
                && GameConditions.isDuringBattle(game)
                && !GameConditions.isDamageSegmentOfBattle(game)) {
            final AboutToLoseCardFromTableResult result = (AboutToLoseCardFromTableResult) effectResult;
            final PhysicalCard cardToBeLost = result.getCardToBeLost();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make " + GameUtils.getFullName(cardToBeLost) + " lost at end of battle");
            action.setActionMsg("Make " + GameUtils.getCardLink(cardToBeLost) + " lost at end of battle instead");
            // Perform result(s)
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            result.getPreventableCardEffect().preventEffectOnCard(cardToBeLost);
                            action.appendEffect(
                                    new AddUntilEndOfBattleModifierEffect(action, new ForfeitValueToUseWhenForfeitedModifier(self, cardToBeLost, 0), null));
                            List<PhysicalCard> cardsToMakeLost = self.getForRemainderOfGameData().get(gameTextSourceCardId) != null ? self.getForRemainderOfGameData().get(gameTextSourceCardId).getPhysicalCards() : null;
                            if (cardsToMakeLost == null) {
                                self.getForRemainderOfGameData().put(gameTextSourceCardId, new ForRemainderOfGameData(new ArrayList<PhysicalCard>()));
                                cardsToMakeLost = self.getForRemainderOfGameData().get(gameTextSourceCardId).getPhysicalCards();
                            }
                            cardsToMakeLost.add(cardToBeLost);
                            final int permCardId = self.getPermanentCardId();
                            action.appendEffect(
                                    new AddUntilEndOfGameActionProxyEffect(action,
                                            new AbstractActionProxy() {
                                                @Override
                                                public List<TriggerAction> getRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult) {
                                                    List<TriggerAction> actions = new LinkedList<TriggerAction>();
                                                    PhysicalCard self = game.findCardByPermanentId(permCardId);

                                                    GameTextActionId gameTextActionId2 = GameTextActionId.OTHER_CARD_ACTION_2;

                                                    // Check condition(s)
                                                    if (TriggerConditions.battleEnded(game, effectResult)
                                                            || TriggerConditions.battleCanceled(game, effectResult)) {
                                                        List<PhysicalCard> cardsToMakeLost2 = self.getForRemainderOfGameData().get(gameTextSourceCardId) != null ? self.getForRemainderOfGameData().get(gameTextSourceCardId).getPhysicalCards() : null;
                                                        if (cardsToMakeLost2 != null) {
                                                            for (Iterator<PhysicalCard> iterator = cardsToMakeLost2.iterator(); iterator.hasNext(); ) {
                                                                PhysicalCard cardToMakeLost2 = iterator.next();
                                                                if (!Filters.onTable.accepts(game, cardToMakeLost2)) {
                                                                    iterator.remove();
                                                                }
                                                            }

                                                            if (!cardsToMakeLost2.isEmpty()) {

                                                                final RequiredGameTextTriggerAction action2 = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId2);
                                                                action2.setRepeatableTrigger(true);
                                                                action2.setText("Make character, starship, or vehicle lost");
                                                                // Choose target(s)
                                                                action2.appendTargeting(
                                                                        new ChooseCardOnTableEffect(action, game.getGameState().getCurrentPlayerId(), "Choose character, starship, or vehicle", cardsToMakeLost2) {
                                                                            @Override
                                                                            protected void cardSelected(final PhysicalCard cardToMakeLost3) {
                                                                                action2.addAnimationGroup(cardToMakeLost3);
                                                                                action2.setText("Make " + GameUtils.getCardLink(cardToMakeLost3) + " lost");
                                                                                // Perform result(s)
                                                                                action2.appendEffect(
                                                                                        new LoseCardFromTableEffect(action2, cardToMakeLost3, true));
                                                                            }
                                                                            @Override
                                                                            protected boolean getUseShortcut() {
                                                                                return true;
                                                                            }
                                                                        }
                                                                );
                                                                actions.add(action2);
                                                            }
                                                        }
                                                    }
                                                    return actions;
                                                }
                                            }
                                    ));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}