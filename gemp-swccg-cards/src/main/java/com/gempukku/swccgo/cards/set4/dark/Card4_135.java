package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: Visage Of The Emperor
 */
public class Card4_135 extends AbstractNormalEffect {
    public Card4_135() {
        super(Side.DARK, 7, PlayCardZoneOption.ATTACHED, Title.Visage_Of_The_Emperor, Uniqueness.UNIQUE);
        setLore("Palpatine's hologram. Imposing. Ominous. Intimidating. Instrument for the evil Emperor's sinister reach across the galaxy. Used on a secret frequency of the Imperial HoloNet.");
        setGameText("Lose 2 Force to deploy on Executor: Holotheatre or Death Star: Conference Room. At the end of each player's turn, each player must lost 1 Force. Effect canceled if opponent controls this site. (Immune to Alter.)");
        addIcons(Icon.DAGOBAH);
        addKeywords(Keyword.HOLOGRAM);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected StandardEffect getGameTextSpecialDeployCostEffect(Action action, String playerId, SwccgGame game, PhysicalCard self, PhysicalCard target, PlayCardOption playCardOption) {
        return new LoseForceEffect(action, playerId, 2, true);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.Holotheatre, Filters.Death_Star_Conference_Room);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();
        String playerId = self.getOwner();
        // Check condition(s)

            if (TriggerConditions.isEndOfEachTurn(game, effectResult)) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make each player lose 1 Force");
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, playerId, 1));
                action.appendEffect(
                        new LoseForceEffect(action, game.getOpponent(playerId), 1));
                actions.add(action);
            }


        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                    && GameConditions.canBeCanceled(game, self)
                    && GameConditions.controls(game, game.getOpponent(self.getOwner()), SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.sameLocation(self))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Cancel");
            action.setActionMsg("Cancel " + GameUtils.getCardLink(self));
            // Perform result(s)
            action.appendEffect(
                    new CancelCardOnTableEffect(action, self));
            actions.add(action);
        }
        return actions;
    }
}