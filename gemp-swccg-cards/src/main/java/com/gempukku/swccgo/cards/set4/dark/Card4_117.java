package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractMobileEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.MoveMobileEffectEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: Bombing Run
 */
public class Card4_117 extends AbstractMobileEffect {
    public Card4_117() {
        super(Side.DARK, 6, Title.Bombing_Run, Uniqueness.DIAMOND_1);
        setLore("Bombers can sometimes slip past orbital defenses. Making a low-altitude bombing run allows 'surgical strikes,' even in cramped situations such as canyons and city streets.");
        setGameText("Deploy on a non-interior planet site (except Dagobah). May move to an adjacent non-interior site at start of your move phase. Your bombers at a related system you control may move to this site. Bombers must return to system at end of your battle phase (if possible).");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.non_interior_site, Filters.planet_site, Filters.except(Filters.Dagobah_site));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        Filter filter = Filters.and(Filters.adjacentSite(self), Filters.non_interior_site);

        // Check condition(s)
        if (TriggerConditions.isStartOfYourPhase(game, self, effectResult, Phase.MOVE)
                && !GameConditions.mayNotMove(game, self)
                && GameConditions.canSpotLocation(game, filter)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Move to adjacent site");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose where to move " + GameUtils.getCardLink(self), filter) {
                        @Override
                        protected void cardSelected(PhysicalCard site) {
                            action.setActionMsg("Move " + GameUtils.getCardLink(self) + " to " + GameUtils.getCardLink(site));
                            // Perform result(s)
                            action.appendEffect(
                                    new MoveMobileEffectEffect(action, self, site));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}