package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasAttachedCondition;
import com.gempukku.swccgo.cards.effects.TransferDeviceOrWeaponEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
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
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Droid
 * Title: RA-7 (Aray-Seven)
 */
public class Card2_016 extends AbstractDroid {
    public Card2_016() {
        super(Side.LIGHT, 4, 2, 1, 3, "RA-7 (Aray-Seven)", Uniqueness.UNRESTRICTED, ExpansionSet.A_NEW_HOPE, Rarity.C2);
        setLore("The RA line of servant droids has fifth-degree primary programming: low intelligence with capabilities for mental labor only. Common among nobles and high-ranking officials.");
        setGameText("May transfer character weapons (for free) to or from your other characters present. May carry up to four such weapons at one time.");
        addModelType(ModelType.SERVANT);
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        //prevents a 5th character weapon being deployed on (or transferred) to RA-7
        modifiers.add(new MayNotDeployToTargetModifier(self, Filters.character_weapon, new HasAttachedCondition(self, Filters.character_weapon, 4), self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)) {
            final Filter yourCharactersPresent = Filters.and(Filters.your(self), Filters.character, Filters.presentWith(self));
            final Filter characterWeaponsHeld = Filters.and(Filters.character_weapon, Filters.attachedTo(self));
            final Collection<PhysicalCard> transferableWeaponsHeld = Filters.filterAllOnTable(game, false, Filters.and(characterWeaponsHeld, Filters.deviceOrWeaponCanBeTransferredTo(true, yourCharactersPresent)));

            if (!transferableWeaponsHeld.isEmpty()) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Transfer weapon (for free) from RA-7");
                action.setActionMsg("Transfer weapon (for free) from " + GameUtils.getCardLink(self));
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose weapon to transfer (for free)", transferableWeaponsHeld) {
                            @Override
                            protected void cardSelected(final PhysicalCard weapon) {
                                action.appendTargeting(
                                        new ChooseCardOnTableEffect(action, playerId, "Choose where to transfer " + GameUtils.getCardLink(weapon),
                                                Filters.and(yourCharactersPresent, Filters.canTransferDeviceOrWeaponTo(weapon, true))) {
                                            @Override
                                            protected void cardSelected(final PhysicalCard transferTo) {
                                                // Allow response(s)
                                                action.allowResponses("Transfer " + GameUtils.getCardLink(weapon) + " to " + GameUtils.getCardLink(transferTo),
                                                        new UnrespondableEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new TransferDeviceOrWeaponEffect(action, weapon, transferTo, true));
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                );
                            }

                        }
                );
                actions.add(action);
            }
        }

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)) {
            final Filter yourCharactersPresent = Filters.and(Filters.your(self), Filters.character, Filters.presentWith(self));
            final int numberOfWeaponsHeld = Filters.countAllOnTable(game, Filters.and(Filters.character_weapon, Filters.attachedTo(self)));
            final Collection<PhysicalCard> transferableWeapons = Filters.filterAllOnTable(game, false, Filters.and(Filters.character_weapon, Filters.attachedTo(yourCharactersPresent)));

            if (!transferableWeapons.isEmpty() && numberOfWeaponsHeld < 4) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Transfer weapon (for free) to RA-7");
                action.setActionMsg("Transfer weapon (for free) to " + GameUtils.getCardLink(self));
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose weapon to transfer (for free)", transferableWeapons) {
                            @Override
                            protected void cardSelected(final PhysicalCard weapon) {
                                // Allow response(s)
                                action.allowResponses("Transfer " + GameUtils.getCardLink(weapon) + " to " + GameUtils.getCardLink(self),
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new TransferDeviceOrWeaponEffect(action, weapon, self, true, true));
                                            }
                                        }
                                );

                            }
                        }
                );
                actions.add(action);
            }
        }

        return actions;
    }
}
