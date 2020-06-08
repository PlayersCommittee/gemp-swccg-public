package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.InPlayDataSetCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Device
 * Title: Portable Fusion Generator
 */
public class Card4_013 extends AbstractCharacterDevice {
    public Card4_013() {
        super(Side.LIGHT, 3, "Portable Fusion Generator");
        setLore("Lardanis Survival Systems MFCR-200 Power Generator. Used by smugglers and Rebel agents. Highly portable. Powers and charges a wide range of equipment.");
        setGameText("Deploy on any warrior. When that warrior fires a blaster rifle or artillery weapon, adds 1 to each of that weapon's destiny draws. Also, may add 1 to power of one droid present.");
        addIcons(Icon.DAGOBAH);
        addKeywords(Keyword.FUSION_GENERATOR);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.warrior);
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.warrior;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.or(Filters.blaster_rifle, Filters.artillery_weapon), Filters.hasAttached(self), 1));
        modifiers.add(new PowerModifier(self, Filters.isInCardInPlayData(self), new InPlayDataSetCondition(self), 1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        PhysicalCard currentDroid = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getPhysicalCard() : null;
        if (currentDroid != null && TriggerConditions.isTableChanged(game, effectResult)) {
            if (!Filters.and(Filters.droid, Filters.present(self)).accepts(game.getGameState(), game.getModifiersQuerying(), currentDroid)) {
                self.setWhileInPlayData(null);
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter targetFilter = Filters.and(Filters.droid, Filters.present(self));

        // Check condition(s)
        if (GameConditions.cardHasWhileInPlayDataSet(self)) {
            final PhysicalCard target = self.getWhileInPlayData().getPhysicalCard();

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Stop adding 1 to power of " + GameUtils.getFullName(target));
            action.setActionMsg("Stop adding 1 to power of " + GameUtils.getCardLink(target));
            // Perform result(s)
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            self.setWhileInPlayData(null);
                            game.getGameState().sendMessage(GameUtils.getCardLink(self) + " stops adding 1 to " + GameUtils.getCardLink(target) + "'s power");
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        else if (GameConditions.canTarget(game, self, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Add 1 to droid's power");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose droid", targetFilter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Add " + 1 + " to " + GameUtils.getCardLink(targetedCard) + "'s power",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new PassthruEffect(action) {
                                                        @Override
                                                        protected void doPlayEffect(SwccgGame game) {
                                                            self.setWhileInPlayData(new WhileInPlayData(targetedCard));
                                                            game.getGameState().sendMessage(GameUtils.getCardLink(self) + " is now adding 1 to " + GameUtils.getCardLink(targetedCard) + "'s power");
                                                        }
                                                    }
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}