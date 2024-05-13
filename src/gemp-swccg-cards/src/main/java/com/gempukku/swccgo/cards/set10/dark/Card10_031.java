package com.gempukku.swccgo.cards.set10.dark;

import com.gempukku.swccgo.cards.AbstractAlienImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.BreakCoverEffect;
import com.gempukku.swccgo.logic.effects.FireWeaponEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections II
 * Type: Character
 * Subtype: Alien/Imperial
 * Title: Arica
 */
public class Card10_031 extends AbstractAlienImperial {
    public Card10_031() {
        super(Side.DARK, 1, 5, 4, 5, 7, "Arica", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_II, Rarity.PM);
        setLore("Mara Jade posed as a dancer at Jabba's Palace in an attempt to complete her master's task and kill Luke. Musician. Spy. Unable to convince Jabba to take her on his skiff.");
        setGameText("Deploys only to a site as an Undercover spy. While present, reduces Luke's forfeit and immunity to attrition by 2 here. During opponent's control phase, Arica may 'break cover' to fire one weapon (for free). Immune to attrition < 4.");
        addPersona(Persona.MARA_JADE);
        addIcons(Icon.REFLECTIONS_II, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.FEMALE, Keyword.MUSICIAN, Keyword.SPY);
        setDeploysAsUndercoverSpy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition whilePresent = new PresentCondition(self);
        Filter lukeHere = Filters.and(Filters.Luke, Filters.here(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, lukeHere, whilePresent, -2));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, lukeHere, whilePresent, -2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter weaponFilter = Filters.and(Filters.weapon, Filters.attachedTo(self), Filters.canBeFiredForFree(self, 0));

        // Check condition(s)
        if (GameConditions.isOnceDuringOpponentsPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.isUndercover(game, self)
                && GameConditions.canSpot(game, self, weaponFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("'Break cover' to fire a weapon");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose weapon to fire", weaponFilter) {
                        @Override
                        protected void cardSelected(final PhysicalCard weapon) {
                            action.addAnimationGroup(weapon);
                            // Pay cost(s)
                            action.appendEffect(
                                    new BreakCoverEffect(action, self));
                            // Allow response(s)
                            action.allowResponses("Fire " + GameUtils.getCardLink(weapon),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new FireWeaponEffect(action, weapon, true, Filters.any));
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
