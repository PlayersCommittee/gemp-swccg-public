package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.LimitForceLossFromForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.MovingResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Card211_011 extends AbstractEpicEventDeployable {
    public Card211_011() {
        super(Side.DARK, PlayCardZoneOption.ATTACHED, Title.Insidious_Prisoner);
        setGameText("If A Stunning Move on table, deploy on 500 Republica. While at a site (even while on a captive), adds one [Dark Side] icon here. While on Coruscant, opponent loses no more than 1 Force from your Force drains here. Once per turn, if a player controls this site, they may have this card (unless on Palpatine) follow their first character to move from here using landspeed (or docking bay transit) to a battleground site. If about to leave table, relocate to 500 Republica (if possible).");
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
        boolean atSiteEvenAsCaptive = Filters.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.Insidious_Prisoner, Filters.at(Filters.site)));
        if (atSiteEvenAsCaptive) {
            modifiers.add(new IconModifier(self, Filters.sameLocation(self), Icon.DARK_FORCE, 1));
        }

        String opponent = game.getOpponent(self.getOwner());
        boolean onCoruscant = Filters.canSpot(game, self, Filters.and(Filters.Insidious_Prisoner, Filters.on(Title.Coruscant)));
        if (onCoruscant) {
            modifiers.add(new LimitForceLossFromForceDrainModifier(self, Filters.here(self), 1, opponent));
        }

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

    private boolean isMovingToBattlegroundSite(SwccgGame game, EffectResult effectResult) {
        PhysicalCard locationMovingTo = ((MovingResult) effectResult).getMovingTo();
        return Filters.battleground_site.accepts(game, locationMovingTo);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerMoving = effectResult.getPerformingPlayerId();
        Filter characterMoving = Filters.and(Filters.character, Filters.your(playerMoving));
        Filter insidiousPrisonersSite = Filters.sameSiteAs(self, Filters.Insidious_Prisoner);

        if (TriggerConditions.movingFromLocation(game, effectResult, characterMoving, insidiousPrisonersSite)
                && GameConditions.controls(game, playerMoving, insidiousPrisonersSite)
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
//                }
            }
        }
        return null;
    }

    // TODO Relocate to 500 Republica text

    // TEST While on coruscant, force drain limit
    // TEST Follow text
}
