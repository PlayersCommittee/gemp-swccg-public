package com.gempukku.swccgo.cards.set305.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.cards.effects.usage.NumTimesPerPhaseEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.common.GameTextActionId;
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
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: A Better Tomorrow
 * Type: Character
 * Subtype: Alien
 * Title: Hikaru Lap'lamiz
 */
public class Card305_017 extends AbstractAlien {
    public Card305_017() {
        super(Side.LIGHT, 1, 5, 4, 5, 7, "Hikaru Lap'lamiz", Uniqueness.UNIQUE, ExpansionSet.ABT, Rarity.R);
        setLore("Hikaru experienced the same coma that befell his father, Kamjin. After their meeting in the World Between Worlds, Hikaru returned with mysterious new powers.");
        setGameText("[Pilot] 2. During your move phase, may use 4 Force to relocate your other Jedi here to any site you occupy. During your deploy phase, may use 3 Force to relocate Hikaru from here to same site as a [CSP] character.");
        addIcons(Icon.ABT);
        addPersona(Persona.HIKARU);
        addIcons(Icon.PILOT, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)) {
            final Filter sameSiteAsCSPCharacter = Filters.sameSiteAs(self, Filters.CSP_character);
            Filter hikaruFilter = Filters.and(Filters.Hikaru, Filters.here(self), Filters.canBeRelocatedToLocation(sameSiteAsCSPCharacter, 3));
            if (GameConditions.canSpot(game, self, hikaruFilter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Relocate Hikaru to another site");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Hikaru", hikaruFilter) {
                            @Override
                            protected void cardTargeted(int targetGroupId, final PhysicalCard HikaruTargeted) {
                                Filter siteToRelocateHikaru = Filters.and(sameSiteAsCSPCharacter, Filters.locationCanBeRelocatedTo(HikaruTargeted, 3));
                                action.appendTargeting(
                                        new ChooseCardOnTableEffect(action, playerId, "Choose site to relocate " + GameUtils.getCardLink(HikaruTargeted) + " to", siteToRelocateHikaru) {
                                            @Override
                                            protected void cardSelected(final PhysicalCard siteSelected) {
                                                action.addAnimationGroup(HikaruTargeted);
                                                action.addAnimationGroup(siteSelected);
                                                // Pay cost(s)
                                                action.appendCost(
                                                        new PayRelocateBetweenLocationsCostEffect(action, playerId, HikaruTargeted, siteSelected, 3));
                                                // Allow response(s)
                                                action.allowResponses("Relocate " + GameUtils.getCardLink(HikaruTargeted) + " to " + GameUtils.getCardLink(siteSelected),
                                                        new UnrespondableEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new RelocateBetweenLocationsEffect(action, HikaruTargeted, siteSelected));
                                                            }
                                                        });
                                            }
                                        });
                            }
                        }
                );
                actions.add(action);
            }
        }

        gameTextActionId = GameTextActionId.HIKARU_LAPLAMIZ_ONE_PER_MOVE_PHASE;

        // Check condition(s)
        if (GameConditions.isNumTimesDuringYourPhase(game, self, playerId, 10000, gameTextSourceCardId, Phase.MOVE)) {
            final Filter siteYouOccupyFilter = Filters.and(Filters.site, Filters.otherLocation(self), Filters.occupies(playerId));
            Filter jediFilter = Filters.and(Filters.your(self), Filters.other(self), Filters.Jedi, Filters.here(self),
                    Filters.canBeRelocatedToLocation(siteYouOccupyFilter, 4));
            if (GameConditions.canSpot(game, self, jediFilter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Relocate a Jedi here to site you occupy");
                // Update usage limit(s)
                action.appendUsage(
                        new NumTimesPerPhaseEffect(action, 10000));
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
                actions.add(action);
            }
        }
        return actions;
    }
}
