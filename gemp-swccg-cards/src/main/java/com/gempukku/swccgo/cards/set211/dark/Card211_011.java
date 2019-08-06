package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.AttachedCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.LimitForceLossFromForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;
import com.gempukku.swccgo.logic.timing.results.MovingResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Epic Event
 * Title: Insidious Prisoner
 */
public class Card211_011 extends AbstractEpicEventDeployable {
    public Card211_011() {
        super(Side.DARK, PlayCardZoneOption.ATTACHED, Title.Insidious_Prisoner);
        setGameText("If A Stunning Move on table, deploy on 500 Republica. While at a site (even while on a captive), adds one [Dark Side] icon here. While on Coruscant, opponent loses no more than 1 Force from your Force drains here. Opponent's Force generation is -1 here. Once per turn, if a player controls this site, they may have this card (unless on Palpatine) follow their first character to move from here using landspeed (or docking bay transit) to a battleground site. If about to leave table, relocate to 500 Republica (if possible).");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_11);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, Filters.A_Stunning_Move);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters._500_Republica;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        // Note: This card cannot currently be a captive - it is a broken link.
        //       The verbiage about captives leaves the door open for a light side counterpart.
        Condition atSiteEvenAsACaptive = new AtCondition(self, SpotOverride.INCLUDE_CAPTIVE, self, Filters.site);
        modifiers.add(new IconModifier(self, Filters.sameLocation(self), atSiteEvenAsACaptive, Icon.DARK_FORCE, 1));

        String opponent = game.getOpponent(self.getOwner());
        Condition insidiousPrisonerOnCoruscant = new AttachedCondition(self, Filters.Coruscant_location);
        modifiers.add(new LimitForceLossFromForceDrainModifier(self, Filters.hasAttached(self), insidiousPrisonerOnCoruscant, 1, opponent));

        modifiers.add(new ForceGenerationModifier(self, Filters.hasAttached(self), -1, game.getOpponent(self.getOwner())));

        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Used for determining the first character that moves from Insidious Prisoner's site in a turn
        if (TriggerConditions.isStartOfEachTurn(game, effectResult)) {
            self.setWhileInPlayData(null);
        }

        // Relocate back to 500 Republica if about to leave table
        if (TriggerConditions.isAboutToLeaveTable(game, effectResult, self)) {
            GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
            PhysicalCard coruscant500Republica = Filters.findFirstFromTopLocationsOnTable(game, Filters._500_Republica);
            if (coruscant500Republica != null) {
                final AboutToLeaveTableResult result = (AboutToLeaveTableResult) effectResult;
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Relocate to 500 Republica");
                action.setActionMsg("Relocate " + GameUtils.getCardLink(self) + " to " + GameUtils.getCardLink(coruscant500Republica));
                action.addAnimationGroup(coruscant500Republica);
                // Perform result(s)
                action.appendEffect(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                result.getPreventableCardEffect().preventEffectOnCard(self);
                                for (PhysicalCard attachedCards : game.getGameState().getAllAttachedRecursively(self)) {
                                    result.getPreventableCardEffect().preventEffectOnCard(attachedCards);
                                }
                            }
                        });
                action.appendEffect(
                        new AttachCardFromTableEffect(action, self, coruscant500Republica));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    private boolean isMovingToBattlegroundSite(SwccgGame game, EffectResult effectResult) {
        PhysicalCard locationMovingTo = ((MovingResult) effectResult).getMovingTo();
        return Filters.battleground_site.accepts(game, locationMovingTo);
    }

    private boolean movingContainsNonUndercoverSpies(SwccgGame game, PhysicalCard source, Filter charactersMoving) {
        PhysicalCard nonUCSpyMoving = Filters.findFirstActive(game, source, Filters.and(charactersMoving, Filters.not(Filters.undercover_spy)));
        if (nonUCSpyMoving != null) {
            return true;
        } else {
            return false;
        }
    }

    private List<OptionalGameTextTriggerAction> followCharacterMovingFromInsidiousPrisoner(String playerMoving, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        Filter charactersMoving = Filters.and(Filters.character, Filters.not(Filters.undercover_spy), Filters.your(playerMoving));
        Filter insidiousPrisonersSite = Filters.sameSiteAs(self, Filters.Insidious_Prisoner);

        if (TriggerConditions.movingFromLocation(game, effectResult, charactersMoving, insidiousPrisonersSite)
                && GameConditions.controls(game, playerMoving, insidiousPrisonersSite)
                && (effectResult.getType() != EffectResult.Type.RELOCATING_BETWEEN_LOCATIONS)
                && (movingContainsNonUndercoverSpies(game, self, charactersMoving))
                && (isMovingToBattlegroundSite(game, effectResult))) {
            if (!GameConditions.cardHasWhileInPlayDataEquals(self, playerMoving)) {
                self.setWhileInPlayData(new WhileInPlayData(playerMoving));
                // Check condition(s)
                MovingResult movedResult = (MovingResult) effectResult;
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Follow character moving from same site");
                action.setActionMsg("Have " + GameUtils.getCardLink(self) + " follow " + GameUtils.getCardLink(movedResult.getCardMoving()));
                // Perform result(s)
                action.appendEffect(
                        new AttachCardFromTableEffect(action, self, movedResult.getMovingTo()));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        return followCharacterMovingFromInsidiousPrisoner(playerId, game, effectResult, self, gameTextSourceCardId);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getOpponentsCardGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        return followCharacterMovingFromInsidiousPrisoner(playerId, game, effectResult, self, gameTextSourceCardId);
    }
}
