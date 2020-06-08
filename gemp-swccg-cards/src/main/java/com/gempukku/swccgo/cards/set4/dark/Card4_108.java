package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractCreature;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.DetachParasiteEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextFerocityModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ParasiteTargetModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ParasiteAttachedResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Creature
 * Title: Bog-wing
 */
public class Card4_108 extends AbstractCreature {
    public Card4_108() {
        super(Side.DARK, 5, 2, null, 2, 0, "Bog-wing");
        setLore("Avian jungle-dweller. Fiercely territorial. Uses powerful talons to pick up and bear off victims. Carries up to nine times it's own body weight. Feeds primarily on root lizards and vine snakes.");
        setGameText("* Ferocity = destiny -1. Habitat: Dagobah. Parasite: Character (that can move). Relocate Bog-wing and host up to two sites away (opponent of victim chooses). Bog-wing then detaches.");
        addModelType(ModelType.AVIAN);
        addIcons(Icon.DAGOBAH, Icon.SELECTIVE_CREATURE);
        addKeyword(Keyword.PARASITE);
    }

    @Override
    protected Filter getGameTextHabitatFilter(String playerId, final SwccgGame game, final PhysicalCard self) {
        return Filters.Dagobah_site;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextFerocityModifier(self, -1, 1));
        modifiers.add(new ParasiteTargetModifier(self, Filters.characterCanBeMovedByBogWing(self)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justAttachedParasiteToHost(game, effectResult, self, Filters.any)) {
            final PhysicalCard victim = ((ParasiteAttachedResult) effectResult).getHost();
            Filter siteFilter = Filters.and(Filters.siteWithinDistance(self, 2), Filters.locationCanBeRelocatedTo(self, true, false, true, 0, false), Filters.locationCanBeRelocatedTo(victim, true, false, true, 0, false));
            if (GameConditions.canSpot(game, self, siteFilter)) {
                final String opponentOfVictim = game.getOpponent(victim.getOwner());

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.skipInitialMessageAndAnimation();
                action.setText("Relocate Bog-wing and host");
                action.setActionMsg(null);
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, opponentOfVictim, "Choose site to relocate " + GameUtils.getCardLink(self) + " and " + GameUtils.getCardLink(victim) + " to", siteFilter) {
                            @Override
                            protected boolean getUseShortcut() {
                                return true;
                            }
                            @Override
                            protected void cardSelected(final PhysicalCard targetSite) {
                                game.getGameState().cardAffectsCard(opponentOfVictim, self, targetSite);
                                game.getGameState().sendMessage(opponentOfVictim + " chooses to have " + GameUtils.getCardLink(self) + " and " + GameUtils.getCardLink(victim) + " relocated to " + GameUtils.getCardLink(targetSite));
                                // Perform result(s)
                                action.appendEffect(
                                        new RelocateBetweenLocationsEffect(action, victim, targetSite));
                                action.appendEffect(
                                        new DetachParasiteEffect(action, self));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
            else {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.skipInitialMessageAndAnimation();
                action.setText("Detach Bog-wing");
                action.setActionMsg(null);
                // Perform result(s)
                action.appendEffect(
                        new DetachParasiteEffect(action, self));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
