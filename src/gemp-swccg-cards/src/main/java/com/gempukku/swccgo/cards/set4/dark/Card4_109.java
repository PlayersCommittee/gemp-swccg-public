package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractCreature;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextFerocityModifier;
import com.gempukku.swccgo.logic.modifiers.MayAttackTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DefeatedResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Creature
 * Title: Dragonsnake
 */
public class Card4_109 extends AbstractCreature {
    public Card4_109() {
        super(Side.DARK, 3, 4, null, 3, 0, Title.Dragonsnake, Uniqueness.DIAMOND_1, ExpansionSet.DAGOBAH, Rarity.R);
        setLore("One of Dagobah's deadliest predators. Has razor-sharp fins, powerful constrictor coils and large fangs. Feeds on almost anything. Often mistaken for a swamp slug, due to its size.");
        setGameText("* Ferocity = 3 + destiny. Habitat: swamps, jungles and Dark Waters. May attack droids. Defeated droids are relocated to an adjacent exterior site (opponent of victim chooses).");
        addModelType(ModelType.SWAMP);
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected Filter getGameTextHabitatFilter(String playerId, final SwccgGame game, final PhysicalCard self) {
        return Filters.or(Filters.swamp, Filters.jungle, Filters.sameSiteAs(self, Filters.Dark_Waters));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextFerocityModifier(self, 3, 1));
        modifiers.add(new MayAttackTargetModifier(self, Filters.droid));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDefeatedBy(game, effectResult, Filters.droid, self)) {
            final PhysicalCard victim = ((DefeatedResult) effectResult).getCardDefeated();
            Filter adjacentSiteFilter = Filters.and(Filters.adjacentSite(self), Filters.locationCanBeRelocatedTo(victim, true, false, true, 0, false));
            if (GameConditions.canSpot(game, self, adjacentSiteFilter)) {
                final String opponentOfVictim = game.getOpponent(victim.getOwner());

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.skipInitialMessageAndAnimation();
                action.setText("Relocate defeated droid");
                action.setActionMsg(null);
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, opponentOfVictim, "Choose site to relocate " + GameUtils.getCardLink(victim) + " to", adjacentSiteFilter) {
                            @Override
                            protected boolean getUseShortcut() {
                                return false;
                            }
                            @Override
                            protected void cardSelected(final PhysicalCard adjacentSite) {
                                game.getGameState().sendMessage(opponentOfVictim + " chooses to have " + GameUtils.getCardLink(victim) + " relocated to " + GameUtils.getCardLink(adjacentSite));
                                // Perform result(s)
                                action.appendEffect(
                                        new RelocateBetweenLocationsEffect(action, victim, adjacentSite));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
