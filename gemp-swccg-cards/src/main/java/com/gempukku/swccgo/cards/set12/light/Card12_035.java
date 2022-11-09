package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.NoForceLossFromCardModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Jedi Master
 * Title: Yoda, Senior Council Member
 */
public class Card12_035 extends AbstractJediMaster {
    public Card12_035() {
        super(Side.LIGHT, 1, 4, 3, 7, 7, "Yoda, Senior Council Member", Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.R);
        setLore("Senior Jedi Council member. Responsible for the early training of Obi-Wan Kenobi. When Qui-Gon brought Anakin before the Council, Yoda voted not to train the boy.");
        setGameText("Deploys only to Jedi Council Chamber. While at Jedi Council Chamber, you lose no Force from Dagobah: Cave and, during your move phase, may use 4 Force to relocate your other Jedi here to any site you occupy. Immune to attrition.");
        addPersona(Persona.YODA);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
        addKeywords(Keyword.JEDI_COUNCIL_MEMBER);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.locationAndCardsAtLocation(Filters.Jedi_Council_Chamber);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new NoForceLossFromCardModifier(self, Filters.Dagobah_Cave,
                new AtCondition(self, Filters.Jedi_Council_Chamber), self.getOwner()));
        modifiers.add(new ImmuneToAttritionModifier(self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.MOVE)
                && GameConditions.isAtLocation(game, self, Filters.Jedi_Council_Chamber)) {
            final Filter siteYouOccupyFilter = Filters.and(Filters.site, Filters.otherLocation(self), Filters.occupies(playerId));
            Filter jediFilter = Filters.and(Filters.your(self), Filters.other(self), Filters.Jedi, Filters.here(self),
                    Filters.canBeRelocatedToLocation(siteYouOccupyFilter, 4));
            if (GameConditions.canSpot(game, self, jediFilter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Relocate a Jedi here to site you occupy");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Jedi", jediFilter) {
                            @Override
                            protected void cardTargeted(int targetGroupId, final PhysicalCard jediTargeted) {
                                Filter siteToRelocateJedi = Filters.and(siteYouOccupyFilter, Filters.locationCanBeRelocatedTo(jediTargeted, 4));
                                action.appendTargeting(
                                        new ChooseCardOnTableEffect(action, playerId, "Choose site to relocate " + GameUtils.getCardLink(jediTargeted) + " to", siteToRelocateJedi) {
                                            @Override
                                            protected void cardSelected(final PhysicalCard siteSelected) {
                                                action.addAnimationGroup(jediTargeted);
                                                action.addAnimationGroup(siteSelected);
                                                // Pay cost(s)
                                                action.appendCost(
                                                        new PayRelocateBetweenLocationsCostEffect(action, playerId, jediTargeted, siteSelected, 4));
                                                // Allow response(s)
                                                action.allowResponses("Relocate " + GameUtils.getCardLink(jediTargeted) + " to " + GameUtils.getCardLink(siteSelected),
                                                        new UnrespondableEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new RelocateBetweenLocationsEffect(action, jediTargeted, siteSelected));
                                                            }
                                                        });
                                            }
                                        });
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
