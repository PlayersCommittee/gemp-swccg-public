package com.gempukku.swccgo.cards.set10.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.MoveCardAsRegularMoveEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Set: Reflections II
 * Type: Objective
 * Title: Agents of Black Sun / Vengeance of the Dark Prince
 */
public class Card10_029 extends AbstractObjective {
    public Card10_029() {
        super(Side.DARK, 0, Title.Agents_Of_Black_Sun);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Imperial City (With Xizor there) and Coruscant system. For remainder of game, your aliens with 'Black Sun' in lore, bounty hunters, and information brokers are Black Sun Agents. You may not deploy cards with ability except Black Sun Agents, Emperor, or Independent starships. During your control phase, each of your bounty hunters may make a regular move to an adjacent site where there is a bounty. Scanning Crew may not be played. Flip this card if Xizor is at a battleground site and Luke is not at a battleground site.");
        addIcons(Icon.REFLECTIONS_II);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter yourBlackSunAgents = Filters.and(Filters.your(self), Filters.or(Filters.and(Filters.alien, Filters.loreContains("Black Sun")), Filters.bounty_hunter, Filters.information_broker));
        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new KeywordModifier(self, yourBlackSunAgents, Keyword.BLACK_SUN_AGENT));
        modifiers.add(new MayNotDeployModifier(self, Filters.and(Filters.hasAbilityOrHasPermanentPilotWithAbility,
                Filters.not(Filters.or(Filters.Black_Sun_agent, yourBlackSunAgents, Filters.Emperor, Filters.and(Icon.INDEPENDENT, Filters.starship)))), self.getOwner()));
        modifiers.add(new MayNotPlayModifier(self, Filters.Scanning_Crew));
        return modifiers;
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Imperial_City, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Imperial City to deploy";
                    }
                });

        List<PhysicalCard> reserveDeck = game.getGameState().getReserveDeck(playerId);
        if (!Filters.filter(reserveDeck, game, Filters.and(Icon.LEGACY_BLOCK_4, Filters.No_Bargain)).isEmpty()
                && !Filters.filter(reserveDeck, game, Filters.and(Icon.LEGACY_BLOCK_4, Filters.title("Shada"))).isEmpty()) {
            // Legacy Shada + No Bargain (V)
            action.appendRequiredEffect(new DeployCardToLocationFromReserveDeckEffect(action, Filters.or(Filters.Xizor, Filters.and(Icon.LEGACY_BLOCK_4, Filters.title("Shada"))), Filters.Imperial_City, true, false) {
                @Override
                public String getChoiceText() {
                    return "Choose Xizor or Shada to deploy";
                }

                @Override
                protected void cardDeployed(PhysicalCard card) {
                    if (Filters.and(Icon.LEGACY_BLOCK_4, Filters.title("Shada")).accepts(game, card)) {
                        action.insertEffect(
                                new DeployCardFromReserveDeckEffect(action, Filters.and(Icon.LEGACY_BLOCK_4, Filters.No_Bargain), true, false) {
                                    @Override
                                    public String getChoiceText() {
                                        return "Choose No Bargain (V) to deploy";
                                    }
                                });
                    }
                }
            });

        } else {
            action.appendRequiredEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.Xizor, Filters.Imperial_City, true, false) {
                        @Override
                        public String getChoiceText() {
                            return "Choose Xizor to deploy";
                        }
                    });
        }
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Coruscant_system, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Coruscant system to deploy";
                    }
                });
        return action;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)) {
            Collection<PhysicalCard> bountyHunters = Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.bounty_hunter, Filters.at(Filters.site)));
            if (!bountyHunters.isEmpty()) {
                List<PhysicalCard> validBountyHunter = new ArrayList<PhysicalCard>();
                for (PhysicalCard bountyHunter : bountyHunters) {
                    if (Filters.movableAsRegularMove(playerId, false, 0, false, Filters.and(Filters.adjacentSite(bountyHunter), Filters.sameSiteAs(self, Filters.any_bounty))).accepts(game, bountyHunter)) {
                        validBountyHunter.add(bountyHunter);
                    }
                }
                if (!validBountyHunter.isEmpty()) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                    action.setText("Move bounty hunter to a bounty");
                    // Choose target(s)
                    action.appendTargeting(
                            new ChooseCardOnTableEffect(action, playerId, "Choose bounty hunter to move", Filters.in(validBountyHunter)) {
                                @Override
                                protected void cardSelected(PhysicalCard bountyHunter) {
                                    action.addAnimationGroup(bountyHunter);
                                    action.setActionMsg("Move " + GameUtils.getCardLink(bountyHunter) + " to an adjacent site where there is a bounty");
                                    // Perform result(s)
                                    action.appendEffect(
                                            new MoveCardAsRegularMoveEffect(action, playerId, bountyHunter, false, false, Filters.and(Filters.adjacentSite(bountyHunter), Filters.sameSiteAs(self, Filters.any_bounty))));
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        Filter atBattlegroundSite = Filters.at(Filters.battleground_site);

        Filter xizorFilter = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.LEGACY__TREAT_XIZOR_AS_SHADA) ? Filters.title("Shada") : Filters.Xizor;

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(xizorFilter, atBattlegroundSite))) {
            Filter lukeFilter = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.REFLECTIONS_II_OBJECTIVE__TARGETS_REY_INSTEAD_OF_LUKE) ? Filters.Rey
                    : GameConditions.hasGameTextModification(game, self, ModifyGameTextType.REFLECTIONS_II_OBJECTIVE__TARGETS_ANAKIN_INSTEAD_OF_LUKE) ? Filters.Anakin
                    : Filters.Luke;
            if (!GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(lukeFilter, atBattlegroundSite))) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Flip");
                action.setActionMsg(null);
                // Perform result(s)
                action.appendEffect(
                        new FlipCardEffect(action, self));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}