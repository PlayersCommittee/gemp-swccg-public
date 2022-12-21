package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ResetForfeitUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Character
 * Subtype: Alien
 * Title: Zuckuss
 */
public class Card4_107 extends AbstractAlien {
    public Card4_107() {
        super(Side.DARK, 1, 4, 2, 4, 3, "Zuckuss", Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.R);
        setLore("Male Gand. Practitioner of ancient religious findsman vocation. Bounty hunter and scout. Gains surprisingly accurate information through mystical visions during meditation.");
        setGameText("Adds 2 to power of anything he pilots. May move for free as a 'react.' Once during each battle, may use 1 Force to cause one alien of ability < 3 at same site to be forfeit = 0 for remainder of turn. Immune to attrition < 3.");
        addPersona(Persona.ZUCKUSS);
        addIcons(Icon.DAGOBAH, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.BOUNTY_HUNTER, Keyword.SCOUT);
        setSpecies(Species.GAND);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new MayMoveAsReactForFreeModifier(self));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isInBattle(game, self)
                && GameConditions.canUseForce(game, playerId, 1)) {
            Filter targetFilter = Filters.and(Filters.opponents(self), Filters.alien, Filters.abilityLessThan(3), Filters.atSameSite(self));
            if (GameConditions.canTarget(game, self, targetFilter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Reset an alien's forfeit to 0");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerBattleEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose an alien", targetFilter) {
                            @Override
                            protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Pay cost(s)
                                action.appendCost(
                                        new UseForceEffect(action, playerId, 1));
                                // Allow response(s)
                                action.allowResponses("Reset " + GameUtils.getCardLink(targetedCard) + "'s forfeit to 0",
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(final Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new ResetForfeitUntilEndOfTurnEffect(action, targetedCard, 0));
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
