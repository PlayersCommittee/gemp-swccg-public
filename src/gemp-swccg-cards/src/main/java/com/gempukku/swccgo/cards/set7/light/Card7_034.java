package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ChooseToMoveAwayOrBeLostEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Neb Dulo
 */
public class Card7_034 extends AbstractAlien {
    public Card7_034() {
        super(Side.LIGHT, 3, 3, 2, 2, 3, "Neb Dulo", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.U);
        setLore("Disciple of Davrilat, a complicated religion based on the sanctity of harmonics. Originally from the desert planet Tocoya. Strong protective instincts.");
        setGameText("Power +3 when present with your musician. When a battle was just initiated where present, may choose one opponent's character of ability < X to move away for free (or that character is lost), where X = number of your musicians present.");
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new PresentWithCondition(self, Filters.and(Filters.your(self), Filters.musician)), 3));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.wherePresent(self))) {
            int numMusicians = Filters.countActive(game, self,
                    Filters.and(Filters.your(self), Filters.musician, Filters.present(self)));
            if (numMusicians > 0) {
                Filter filter = Filters.and(Filters.opponents(self), Filters.character, Filters.abilityLessThan(numMusicians), Filters.present(self));
                TargetingReason targetingReason = TargetingReason.TO_BE_LOST;
                if (GameConditions.canTarget(game, self, targetingReason, filter)
                        && GameConditions.canSpot(game, self, Filters.adjacentSite(self))) {

                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setText("Make a character move away or be lost");
                    // Choose target(s)
                    action.appendTargeting(
                            new TargetCardOnTableEffect(action, playerId, "Target character to move away or be lost", targetingReason, filter) {
                                @Override
                                protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                                    action.addAnimationGroup(cardTargeted);
                                    // Allow response(s)
                                    action.allowResponses("Make " + GameUtils.getCardLink(cardTargeted) + " move away or be lost",
                                            new RespondableEffect(action) {
                                                @Override
                                                protected void performActionResults(Action targetingAction) {
                                                    // Get the targeted card(s) from the action using the targetGroupId.
                                                    // This needs to be done in case the target(s) were changed during the responses.
                                                    final PhysicalCard character = targetingAction.getPrimaryTargetCard(targetGroupId);

                                                    // Perform result(s)
                                                    action.appendEffect(
                                                            new ChooseToMoveAwayOrBeLostEffect(action, game.getOpponent(playerId), character, true));
                                                }
                                            });
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}
