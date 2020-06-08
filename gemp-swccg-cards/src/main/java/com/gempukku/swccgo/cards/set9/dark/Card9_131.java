package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfWeaponFiringModifierEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetDefenseValueModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.*;

/**
 * Set: Death Star II
 * Type: Effect
 * Title: Royal Escort
 */
public class Card9_131 extends AbstractNormalEffect {
    public Card9_131() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Royal Escort", Uniqueness.UNIQUE);
        setLore("When away from the Imperial Palace on Coruscant, the Emperor is protected by legions of troops. Typically this force includes soldiers trained to fight in the local environment.");
        setGameText("Deploy on table. Each of your non-unique troopers on Endor or Death Star II is forfeit +1. When opponent just used a weapon to target your character aboard a piloted vehicle, that character may use that vehicle's defense value.");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.non_unique, Filters.trooper,
                Filters.or(Filters.on(Title.Endor), Filters.on(Title.Death_Star_II))), 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, final SwccgGame game, final Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTargetedByWeapon(game, effect, Filters.and(Filters.your(self), Filters.character), Filters.any)) {
            Collection<PhysicalCard> characters = game.getGameState().getWeaponFiringState().getTargets();
            final Map<PhysicalCard, PhysicalCard> characterVehicleMap = new HashMap<PhysicalCard, PhysicalCard>();
            for (PhysicalCard character : characters) {
                PhysicalCard vehicle = Filters.findFirstActive(game, self,
                        Filters.and(Filters.piloted, Filters.vehicle, Filters.hasAboardExceptRelatedSites(character)));
                if (vehicle != null) {
                    characterVehicleMap.put(character, vehicle);
                }
            }

            if (!characterVehicleMap.isEmpty()) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Have character" + GameUtils.s(characterVehicleMap.keySet()) + " use vehicle's defense value");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardsOnTableEffect(action, playerId, "Choose characters to use vehicle's defense value'", 1, Integer.MAX_VALUE, Filters.in(characterVehicleMap.keySet())) {
                            @Override
                            protected void cardsTargeted(int targetGroupId, final Collection<PhysicalCard> characters) {
                                action.addAnimationGroup(characters);
                                // Allow response(s)
                                action.allowResponses("Have " + GameUtils.getAppendedNames(characters) + " use vehicle's defense value",
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                for (PhysicalCard character : characters) {
                                                    float defenseValue = game.getModifiersQuerying().getDefenseValue(game.getGameState(), characterVehicleMap.get(character));
                                                    action.appendEffect(
                                                            new AddUntilEndOfWeaponFiringModifierEffect(action,
                                                                    new ResetDefenseValueModifier(self, character, defenseValue),
                                                                    "Resets " + GameUtils.getCardLink(character) + "'s defense value to " + GuiUtils.formatAsString(defenseValue)));
                                                }
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