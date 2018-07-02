package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Character
 * Subtype: Alien
 * Title: Grugnak
 */
public class Card11_003 extends AbstractAlien {
    public Card11_003() {
        super(Side.LIGHT, 2, 3, 2, 2, 3, "Grugnak", Uniqueness.UNIQUE);
        setLore("Elderly Ugnaught who despised the Empire for occupying his home on Cloud City. Helped the Rebellion secretly move personnel to aid the resistance movement.");
        setGameText("While on Cloud City, once during each of your deploy phases may use or lose X Force to relocate any one of your characters at same site to a related site, where X equals ability of your character being relocated.");
        setSpecies(Species.UGNAUGHT);
        addIcons(Icon.TATOOINE);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.DEPLOY)
                && GameConditions.isOnCloudCity(game, self)) {
            final Filter relatedSite = Filters.relatedSite(self);
            Filter characterFilter = Filters.and(Filters.your(self), Filters.character, Filters.atSameSite(self), Filters.canBeRelocatedToLocation(relatedSite, true, 0));
            if (GameConditions.canSpot(game, self, characterFilter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Relocate a character here to related site");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose character", characterFilter) {
                            @Override
                            protected void cardTargeted(int targetGroupId, final PhysicalCard characterTargeted) {
                                Filter siteToRelocateToFilter = Filters.and(relatedSite, Filters.locationCanBeRelocatedTo(characterTargeted, 0));
                                action.appendTargeting(
                                        new ChooseCardOnTableEffect(action, playerId, "Choose site to relocate " + GameUtils.getCardLink(characterTargeted) + " to", siteToRelocateToFilter) {
                                            @Override
                                            protected void cardSelected(final PhysicalCard siteSelected) {
                                                action.addAnimationGroup(characterTargeted);
                                                action.addAnimationGroup(siteSelected);
                                                float ability = game.getModifiersQuerying().getAbility(game.getGameState(), characterTargeted);
                                                // Pay cost(s)
                                                List<StandardEffect> effectsToChoose = new ArrayList<StandardEffect>();
                                                effectsToChoose.add(new PayRelocateBetweenLocationsCostEffect(action, playerId, characterTargeted, siteSelected, ability));
                                                effectsToChoose.add(new LoseForceEffect(action, playerId, ability, true));
                                                action.appendCost(
                                                        new ChooseEffectEffect(action, playerId, effectsToChoose));
                                                // Allow response(s)
                                                action.allowResponses("Relocate " + GameUtils.getCardLink(characterTargeted) + " to " + GameUtils.getCardLink(siteSelected),
                                                        new UnrespondableEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new RelocateBetweenLocationsEffect(action, characterTargeted, siteSelected));
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
