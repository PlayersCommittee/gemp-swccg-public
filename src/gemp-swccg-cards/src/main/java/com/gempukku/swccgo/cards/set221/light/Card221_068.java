package com.gempukku.swccgo.cards.set221.light;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromOffTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.AboutToBeStolenResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Defensive Shield
 * Title: Only Jedi Carry That Weapon (V)
 */
public class Card221_068 extends AbstractDefensiveShield {
    public Card221_068() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Only Jedi Carry That Weapon", ExpansionSet.SET_21, Rarity.V);
        setVirtualSuffix(true);
        setLore("An elegant weapon for a more civilized age.");
        setGameText("Plays on table. For opponent to steal a weapon from target character using a non-[Episode I] card, must first draw destiny. Unless destiny +1 > target's ability, attempt fails and stealing card is placed out of play. [Episode I] blasters are immune to An Entire Legion Of My Best Troops.");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I, Icon.VIRTUAL_DEFENSIVE_SHIELD);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Icon.EPISODE_I, Filters.blaster), Title.An_Entire_Legion_Of_My_Best_Troops));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(self.getOwner());

        if (TriggerConditions.isAboutToBeStolen(game, effectResult, Filters.and(Filters.character_weapon, Filters.attachedTo(Filters.and(Filters.your(self), Filters.character))))) {
            final AboutToBeStolenResult aboutToStealCardResult = (AboutToBeStolenResult) effectResult;
            final PhysicalCard sourceCard = aboutToStealCardResult.getSourceCard();
            final PhysicalCard weaponToBeStolen = aboutToStealCardResult.getCardToBeStolen();
            final PhysicalCard targetCharacter = weaponToBeStolen.getAttachedTo();
            if (targetCharacter != null
                    && !game.getModifiersQuerying().hasIcon(game.getGameState(), sourceCard, Icon.EPISODE_I)
                    && Filters.canBeTargetedBy(self, TargetingReason.TO_BE_PLACED_OUT_OF_PLAY).accepts(game, sourceCard)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make " + opponent + " draw destiny");
                // Perform result(s)
                action.appendEffect(
                        new DrawDestinyEffect(action, opponent) {
                            @Override
                            protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                return Collections.singletonList(targetCharacter);
                            }
                            @Override
                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                GameState gameState = game.getGameState();
                                if (totalDestiny == null) {
                                    gameState.sendMessage("Result: Failed due to failed destiny draw");
                                    aboutToStealCardResult.getPreventableCardEffect().preventEffectOnCard(weaponToBeStolen);
                                    action.appendEffect(
                                            new PlaceCardOutOfPlayFromOffTableEffect(action, sourceCard));
                                    action.appendEffect(
                                            new PlaceCardOutOfPlayFromTableEffect(action, sourceCard));
                                    return;
                                }

                                float ability = game.getModifiersQuerying().getAbility(game.getGameState(), targetCharacter);
                                gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                gameState.sendMessage("Target's ability: " + GuiUtils.formatAsString(ability));
                                if ((totalDestiny + 1) > ability) {
                                    gameState.sendMessage("Result: Succeeded");
                                }
                                else {
                                    gameState.sendMessage("Result: Failed");
                                    aboutToStealCardResult.getPreventableCardEffect().preventEffectOnCard(weaponToBeStolen);
                                    action.appendEffect(
                                            new PlaceCardOutOfPlayFromOffTableEffect(action, sourceCard));
                                    action.appendEffect(
                                            new PlaceCardOutOfPlayFromTableEffect(action, sourceCard));
                                }
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}