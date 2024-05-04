package com.gempukku.swccgo.cards.set205.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AttemptToBlowAwayDeathStarIICondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelImmunityToAttritionUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.EpicEventDestinyDrawModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 5
 * Type: Starship
 * Subtype: Starfighter
 * Title: Tycho In Green Squadron 3
 */
public class Card205_010 extends AbstractStarfighter {
    public Card205_010() {
        super(Side.LIGHT, 2, 3, 3, null, 5, 4, 5, "Tycho In Green Squadron 3", Uniqueness.UNIQUE, ExpansionSet.SET_5, Rarity.V);
        setLore("Flown by Tycho Celchu at the Battle of Endor. Modified canopy improves pilot vision in tight confines. Assigned to fly top cover for Millennium Falcon.");
        setGameText("May deploy as a 'react'. Permanent pilot is •Tycho, who provides ability of 2 and adds 3 to his [Death Star II] Epic Event destiny draws. During battle, may target a starfighter; target's immunity to attrition is canceled.");
        addPersona(Persona.GREEN_SQUADRON_3);
        addIcons(Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_5);
        addModelType(ModelType.A_WING);
        addKeywords(Keyword.GREEN_SQUADRON);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactModifier(self));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(
                new AbstractPermanentPilot(Persona.TYCHO, 2) {
                    @Override
                    public List<Modifier> getGameTextModifiers(PhysicalCard self) {
                        String playerId = self.getOwner();

                        List<Modifier> modifiers = new LinkedList<Modifier>();
                        modifiers.add(new EpicEventDestinyDrawModifier(self, playerId, Icon.DEATH_STAR_II, new AttemptToBlowAwayDeathStarIICondition(self), 3));
                        return modifiers;
                    }});
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter targetFilter = Filters.and(Filters.starfighter, Filters.participatingInBattle, Filters.hasAnyImmunityToAttrition);

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isInBattle(game, self)
                && GameConditions.canTarget(game, self, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Cancel immunity to attrition");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose starfighter", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard) + "'s immunity to attrition",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(final Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new CancelImmunityToAttritionUntilEndOfBattleEffect(action, targetedCard,
                                                            "Cancels " + GameUtils.getCardLink(targetedCard) + "'s immunity to attrition"));
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
