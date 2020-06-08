package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ForfeitCardFromTableUsingForfeitValueEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitedToUsedPileModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.*;

/**
 * Set: Death Star II
 * Type: Effect
 * Title: Superficial Damage
 */
public class Card9_043 extends AbstractNormalEffect {
    public Card9_043() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Superficial Damage", Uniqueness.UNIQUE);
        setLore("Veteran Rebel engineers know that weapon systems positioned for adequate coverage are vulnerable themselves.");
        setGameText("Deploy on table. Each turn, each of your characters, vehicles, and starships may forfeit one of its weapons (except a lightsaber) using forfeit value = 3. Also, your forfeited weapons go to Used Pile. (Immune to Alter.)");
        addIcons(Icon.DEATH_STAR_II);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitedToUsedPileModifier(self, Filters.and(Filters.your(self), Filters.weapon)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isStartOfEachTurn(game, effectResult)) {
            self.setWhileInPlayData(null);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isResolvingBattleDamageAndAttrition(game, effectResult, playerId)
                && (GameConditions.isBattleDamageRemaining(game, playerId) || GameConditions.isAttritionRemaining(game, playerId))) {
            Collection<PhysicalCard> cardsWithAttachedWeaponToForfeit = Filters.filterActive(game, self,
                    Filters.and(Filters.your(self), Filters.or(Filters.character, Filters.starship, Filters.vehicle), Filters.participatingInBattle,
                            Filters.hasAttached(Filters.and(Filters.your(self), Filters.weapon, Filters.except(Filters.lightsaber)))));
            if (!cardsWithAttachedWeaponToForfeit.isEmpty()) {
                List<PhysicalCard> cardsToAlreadyForfeitWeapon = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getPhysicalCards() : null;
                if (cardsToAlreadyForfeitWeapon != null) {
                    cardsWithAttachedWeaponToForfeit = Filters.filter(cardsWithAttachedWeaponToForfeit, game, Filters.not(Filters.in(cardsToAlreadyForfeitWeapon)));
                }
                if (!cardsWithAttachedWeaponToForfeit.isEmpty()) {
                    Collection<PhysicalCard> weaponsToForfeit = Filters.filterActive(game, self,
                            Filters.and(Filters.your(self), Filters.weapon, Filters.except(Filters.lightsaber), Filters.attachedTo(Filters.in(cardsWithAttachedWeaponToForfeit))));
                    if (!weaponsToForfeit.isEmpty()) {

                        final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                        action.setText("Forfeit a weapon");
                        // Choose target(s)
                        action.appendTargeting(
                                new ChooseCardOnTableEffect(action, playerId, "Choose weapon to forfeit", weaponsToForfeit) {
                                    @Override
                                    protected void cardSelected(PhysicalCard selectedCard) {
                                        action.addAnimationGroup(selectedCard);
                                        action.setActionMsg("Forfeit " + GameUtils.getCardLink(selectedCard));
                                        // Remember card that forfeited weapon
                                        PhysicalCard attachedTo = selectedCard.getAttachedTo();
                                        if (attachedTo != null) {
                                            List<PhysicalCard> cardsToAlreadyForfeitWeapon = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getPhysicalCards() : null;
                                            if (cardsToAlreadyForfeitWeapon == null) {
                                                self.setWhileInPlayData(new WhileInPlayData(new ArrayList<PhysicalCard>()));
                                                cardsToAlreadyForfeitWeapon = self.getWhileInPlayData().getPhysicalCards();
                                            }
                                            cardsToAlreadyForfeitWeapon.add(attachedTo);
                                        }
                                        // Perform result(s)
                                        action.appendEffect(
                                                new ForfeitCardFromTableUsingForfeitValueEffect(action, selectedCard, 3));
                                    }
                                }
                        );
                        return Collections.singletonList(action);
                    }
                }
            }
        }
        return null;
    }
}