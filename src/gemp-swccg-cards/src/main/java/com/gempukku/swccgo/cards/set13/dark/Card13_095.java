package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromOffTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.AboutToBeStolenResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Defensive Shield
 * Title: Weapon Of A Sith
 */
public class Card13_095 extends AbstractDefensiveShield {
    public Card13_095() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE,"Weapon Of A Sith", ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("In order to use his double-bladed weapon Maul trained under Darth Sidious for years.");
        setGameText("Plays on table. For opponent to steal a weapon from target character using a non-[Episode I] card, must first draw destiny. If destiny +1 > target's ability, weapon stolen. Otherwise, attempt fails and stealing card is placed out of play.");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
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
                    && !game.getModifiersQuerying().hasIcon(game.getGameState(), sourceCard, Icon.EPISODE_I)) {

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