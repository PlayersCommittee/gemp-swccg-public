package com.gempukku.swccgo.cards.set216.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.ForfeitCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RestoreCardToNormalEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeModifiedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Character
 * Subtype: Alien
 * Title: Wookiee Warrior
 */
public class Card216_048 extends AbstractAlien {
    public Card216_048() {
        super(Side.LIGHT, 2, 4, 4, 2, 4, "Wookiee Warrior", Uniqueness.RESTRICTED_3, ExpansionSet.SET_16, Rarity.V);
        setLore("");
        setGameText("Power +1 at a Kashyyyk site. While present at a Kashyyyk site, opponent may not cancel or modify your Force drains here. Unless 'hit,' may forfeit in place of your other 'hit' character at same site, restoring that character to normal.");
        setSpecies(Species.WOOKIEE);
        addIcons(Icon.WARRIOR, Icon.EPISODE_I, Icon.VIRTUAL_SET_16);
    }

    @Override
    public final boolean hasSpecialDefenseValueAttribute() {
        return true;
    }

    @Override
    public final float getSpecialDefenseValue() {
        return 4;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition wookieeWarriorPresentAtKashyyykSite = new PresentAtCondition(self, Filters.Kashyyyk_site);
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, wookieeWarriorPresentAtKashyyykSite, 1));
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, Filters.here(self), wookieeWarriorPresentAtKashyyykSite, opponent, playerId));
        modifiers.add(new ForceDrainsMayNotBeModifiedModifier(self, Filters.here(self), wookieeWarriorPresentAtKashyyykSite, opponent, playerId));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        Filter targetFilter = Filters.and(Filters.your(playerId), Filters.other(self), Filters.hit, Filters.character, Filters.atSameSite(self));

        // Check condition(s)
        if (TriggerConditions.isResolvingBattleDamageAndAttrition(game, effectResult, playerId)
                && !GameConditions.isHit(game, self)
                && GameConditions.canTarget(game, self, targetFilter)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Forfeit to restore 'hit' character");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose 'hit' character", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                            action.addAnimationGroup(cardTargeted);
                            // Pay cost(s)
                            action.appendCost(
                                    new ForfeitCardFromTableEffect(action, self));
                            // Allow response(s)
                            action.allowResponses("Restore " + GameUtils.getCardLink(cardTargeted) + " to normal",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new RestoreCardToNormalEffect(action, cardTargeted));
                                        }
                                    });
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
