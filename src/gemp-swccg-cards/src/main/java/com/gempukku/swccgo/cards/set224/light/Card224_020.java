package com.gempukku.swccgo.cards.set224.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.ExcludeFromBattleEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ExcludedFromBattleModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotFireWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 24
 * Type: Character
 * Subtype: Rebel
 * Title: Son Of Skywalker (V)
 */
public class Card224_020 extends AbstractRebel {
    public Card224_020() {
        super(Side.LIGHT, 1, 5, 5, 5, 8, "Son Of Skywalker", Uniqueness.UNIQUE, ExpansionSet.SET_24, Rarity.V);
        setLore("Luke Skywalker. Son of Anakin. Seeker of Yoda. Levitator of rocks. Ignorer of advice. Incapable of impossible. Reckless is he.");
        setGameText("[Pilot] 2. During battle, characters 'hit' by Luke may not fire weapons. May [download] Anakin's Lightsaber here. If a battle just initiated at same site, unless a vehicle here, may target a Dark Jedi here; for remainder of battle, exclude all other characters. Immune to attrition < 4.");
        addIcons(Icon.CLOUD_CITY, Icon.DAGOBAH, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_24);
        addPersona(Persona.LUKE);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justHitBy(game, effectResult, Filters.character, self)
                && GameConditions.isDuringBattleWithParticipant(game, self)) {

            final PhysicalCard card = ((HitResult) effectResult).getCardHit();

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Prevent " + GameUtils.getCardLink(card) + " from firing weapons");
            action.setActionMsg("Prevent " + GameUtils.getCardLink(card) + " from firing weapons");
            action.appendEffect(
                    new AddUntilEndOfBattleModifierEffect(action,
                        new MayNotFireWeaponsModifier(self, card),
                        "Prevents " + GameUtils.getCardLink(card) + " from firing weapons")
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.SON_OF_SKYWALKER_V__DOWNLOAD_ANAKINS_LIGHTSABER;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.ANAKINS_LIGHTSABER)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Anakin's Lightsaber from Reserve Deck");
            action.setActionMsg("Deploy Anakin's Lightsaber to Luke's location from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.persona(Persona.ANAKINS_LIGHTSABER), Filters.atSameLocation(self), true, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        TargetingReason targetingReason = TargetingReason.TO_BE_EXCLUDED_FROM_BATTLE;

        Filter darkJediFilter = Filters.and(Filters.Dark_Jedi, Filters.atSameLocation(self), Filters.participatingInBattle);
        Filter otherCharactersFilter = Filters.and(Filters.character, Filters.participatingInBattle, Filters.not(self));
        Collection<PhysicalCard> otherCharacters = Filters.filterActive(game, self, otherCharactersFilter);

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.sameSite(self))
                && !GameConditions.canSpot(game, self, Filters.and(Filters.vehicle, Filters.atSameLocation((self))))
                && GameConditions.canTarget(game, self, darkJediFilter)
                && otherCharacters.size() > 1) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Exclude characters from battle");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose a Dark Jedi to battle", darkJediFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            Filter exclusionFilter = Filters.and(Filters.character, Filters.participatingInBattle, Filters.canBeTargetedBy(self, targetingReason), Filters.not(self), Filters.not(targetedCard));
                            Collection<PhysicalCard> cardsToExclude = Filters.filterActive(game, self, exclusionFilter);
                            action.addAnimationGroup(cardsToExclude);
                            // Allow response(s)
                            action.allowResponses("Exclude all characters (except " + GameUtils.getCardLink(self) + " and " + GameUtils.getCardLink(targetedCard) + ") from battle",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ExcludeFromBattleEffect(action, cardsToExclude));
                                            action.appendEffect(
                                                    new AddUntilEndOfBattleModifierEffect(action,
                                                            new ExcludedFromBattleModifier(self, exclusionFilter), null));
                                        }
                                    }
                            );
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
