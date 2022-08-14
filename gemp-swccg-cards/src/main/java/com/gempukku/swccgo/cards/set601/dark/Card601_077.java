package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 3
 * Type: Effect
 * Title: Emperor's Power (V)
 */
public class Card601_077 extends AbstractNormalEffect {
    public Card601_077() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, Title.Emperors_Power, Uniqueness.UNIQUE);
        setLore("From his throne room aboard the second Death Star, Emperor Palpatine monitors activity throughout the galaxy.");
        setGameText("Deploy on Emperor. Jedi are power -1 and deploy +1. During your control phase, if Emperor present at a battleground site with Vader (or two Imperial Council Members), opponent loses 1 Force.");
        addIcons(Icon.DEATH_STAR_II, Icon.LEGACY_BLOCK_3);
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
        setVirtualSuffix(true);
        setAsLegacy(true);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Emperor;
    }


    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.Jedi, -1));
        modifiers.add(new DeployCostModifier(self, Filters.Jedi, 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
            && GameConditions.isPresentAt(game, self, Filters.battleground_site)
                && (GameConditions.isPresentWith(game, self, Filters.Vader)
//                    || GameConditions.isPresentWith(game, self, Filters.Imperial_Council_Member, 2)
        )
        ) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Opponent loses 1 Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));

            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        if (TriggerConditions.isEndOfYourPhase(game, effectResult, Phase.CONTROL, playerId)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.isPresentAt(game, self, Filters.battleground_site)
                && (GameConditions.isPresentWith(game, self, Filters.Vader)
//                    || GameConditions.isPresentWith(game, self, Filters.Imperial_Council_Member, 2)
        )
        ) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setPerformingPlayer(playerId);
                action.setText("Opponent loses 1 Force");
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, opponent, 1));
                actions.add(action);

        }

        return actions;
    }
}