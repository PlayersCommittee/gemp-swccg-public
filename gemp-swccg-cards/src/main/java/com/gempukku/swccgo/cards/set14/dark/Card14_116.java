package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyAndChooseInsteadEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Starship
 * Subtype: Starfighter
 * Title: DFS-1015
 */
public class Card14_116 extends AbstractStarfighter {
    public Card14_116() {
        super(Side.DARK, 2, 1, 2, null, 2, null, 3, "DFS-1015", Uniqueness.UNIQUE);
        setLore("Tactical support starfighter that utilizes learning subroutines to maximize the effectiveness of droid starfighter offensive strategies.");
        setGameText("Deploys -1 to same location as your battleship. While with another droid starfighter at a system, once per battle if about to draw a battle destiny here, may instead draw two and choose one.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.TRADE_FEDERATION, Icon.PILOT, Icon.PRESENCE);
        addKeywords(Keyword.DFS_SQUADRON, Keyword.NO_HYPERDRIVE);
        addModelType(ModelType.DROID_STARFIGHTER);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot() {});
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.battleship))));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isAboutToDrawBattleDestiny(game, effectResult, playerId)
                && GameConditions.isInBattleAt(game, self, Filters.system)
                && GameConditions.isInBattleWith(game, self, Filters.and(Filters.other(self), Filters.droid_starfighter))
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canDrawDestinyAndChoose(game, 2)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Draw two and choose one");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DrawDestinyAndChooseInsteadEffect(action, 2, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
