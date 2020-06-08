package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractUtinniEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Effect
 * Subtype: Utinni
 * Title: Plastoid Armor
 */
public class Card1_059 extends AbstractUtinniEffect {
    public Card1_059() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, Title.Plastoid_Armor, Uniqueness.UNIQUE);
        setLore("Luke and Han took the armor suits from fallen stormtroopers and used them as both protection and disguise to penetrate the detention cell block aboard the Death Star.");
        setGameText("Deploy on a Death Star site where a stormtrooper was just lost. Target one of your characters not on Death Star. When target reaches Utinni Effect, relocate to target. Target is now 'disguised:' gains spy skill, power and forfeit +2, and armor = 5.");
    }

    @Override
    protected boolean canPlayCardDuringCurrentPhase(String playerId, SwccgGame game, PhysicalCard self) {
        return false;
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justLostFromLocation(game, effectResult, Filters.stormtrooper, Filters.Death_Star_site)) {
            PhysicalCard location = ((LostFromTableResult) effectResult).getFromLocation();

            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.sameCardId(location), null);
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected Filter getGameTextValidUtinniEffectTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, TargetId targetId) {
        return Filters.and(Filters.your(self), Filters.character, Filters.not(Filters.on(Title.Death_Star)));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final GameState gameState = game.getGameState();
        PhysicalCard target = self.getTargetedCard(gameState, TargetId.UTINNI_EFFECT_TARGET_1);

        // Check condition(s)
        if (!GameConditions.isUtinniEffectReached(game, self)
                && TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.isAtLocation(game, self, Filters.sameLocation(target))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Relocate to " + GameUtils.getCardLink(target));
            action.setActionMsg("Relocate " + GameUtils.getCardLink(self) + " to " + GameUtils.getCardLink(target));
            // Update usage limit(s)
            action.appendUsage(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            self.setUtinniEffectStatus(UtinniEffectStatus.REACHED);
                        }
                    }
            );
            // Perform result(s)
            action.appendEffect(
                    new AttachCardFromTableEffect(action, self, target));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter targetAttachedTo = Filters.and(Filters.hasAttached(self), Filters.targetedByCardOnTableAsTargetId(self, TargetId.UTINNI_EFFECT_TARGET_1));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new KeywordModifier(self, targetAttachedTo, Keyword.SPY));
        modifiers.add(new PowerModifier(self, targetAttachedTo, 2));
        modifiers.add(new ForfeitModifier(self, targetAttachedTo, 2));
        modifiers.add(new ResetArmorModifier(self, targetAttachedTo, 5));
        return modifiers;
    }
}