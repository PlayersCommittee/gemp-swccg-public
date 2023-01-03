package com.gempukku.swccgo.cards.set10.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
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
import com.gempukku.swccgo.logic.effects.ModifyPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections II
 * Type: Character
 * Subtype: Alien
 * Title: Dash Rendar
 */
public class Card10_006 extends AbstractAlien {
    public Card10_006() {
        super(Side.LIGHT, 3, 3, 3, 3, 5, "Dash Rendar", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_II, Rarity.PM);
        setLore("Emperor banished Rendar family from Coruscant. Became gambler and smuggler. Brought down AT-AT at the Battle of Hoth. Works for Rebel Alliance from time to time. Corellian.");
        setGameText("Adds 3 to power of anything he pilots. When piloting Outrider, draws one battle destiny if not able to otherwise, and once per battle, may reduce power of one opponent's starship in same battle by power of Outrider for remainder of battle.");
        addPersona(Persona.DASH);
        addIcons(Icon.REFLECTIONS_II, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.GAMBLER, Keyword.SMUGGLER);
        setSpecies(Species.CORELLIAN);
        setMatchingStarshipFilter(Filters.Outrider);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new PilotingCondition(self, Filters.Outrider), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final Filter targetFilter = Filters.and(Filters.opponents(self), Filters.starship, Filters.participatingInBattle);

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isInBattle(game, self)
                && GameConditions.isPiloting(game, self, Filters.Outrider)
                && GameConditions.canTarget(game, self, targetFilter)) {
            PhysicalCard outrider = Filters.findFirstActive(game, self, Filters.Outrider);
            if (outrider != null) {
                final float power = game.getModifiersQuerying().getPower(game.getGameState(), outrider);
                if (power > 0) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                    action.setText("Reduce power of opponent's starship");
                    // Update usage limit(s)
                    action.appendUsage(
                            new OncePerBattleEffect(action));
                    // Choose target(s)
                    action.appendTargeting(
                            new TargetCardOnTableEffect(action, playerId, "Choose starship", targetFilter) {
                                @Override
                                protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                                    action.addAnimationGroup(targetedCard);
                                    // Allow response(s)
                                    action.allowResponses("Reduce power of " + GameUtils.getCardLink(targetedCard) + " by " + GuiUtils.formatAsString(power),
                                            new UnrespondableEffect(action) {
                                                @Override
                                                protected void performActionResults(Action targetingAction) {
                                                    // Perform result(s)
                                                    action.appendEffect(
                                                            new ModifyPowerUntilEndOfBattleEffect(action, targetedCard, -power));
                                                }
                                            }
                                    );
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
