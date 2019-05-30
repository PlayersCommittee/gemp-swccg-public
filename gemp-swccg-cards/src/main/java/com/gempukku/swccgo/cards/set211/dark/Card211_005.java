package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.ChooseEffectEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromBottomOfUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Ree-Yees
 */
public class Card211_005 extends AbstractAlien {
    public Card211_005() {
        super(Side.DARK, 3, 3, 3, 3, 3, Title.ReeYees, Uniqueness.UNIQUE);
        setLore("Gran convicted of murder. Exiled from his homeworld. Smuggler and bounty hunter. Slowly going insane. Fond of making things explode. Plotting to kill Jabba.");
        setGameText("Non-[Maintenance] Bounty hunters here may not be targeted by Rebel Barrier or Clash of Sabers. Whenever you draw a destiny of 3, may choose: activate 1 Force, draw bottom card of Used Pile, or place a card from hand on top of Used Pile.");
        addIcons(Icon.JABBAS_PALACE, Icon.VIRTUAL_SET_11, Icon.WARRIOR);
        addKeywords(Keyword.SMUGGLER, Keyword.BOUNTY_HUNTER);
        setSpecies(Species.GRAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Filter nonMaintenanceBountyHuntersHere = Filters.and(Filters.bounty_hunter, Filters.not(Icon.MAINTENANCE), Filters.here(self));
        modifiers.add(new ImmuneToTitleModifier(self, nonMaintenanceBountyHuntersHere, Title.Rebel_Barrier));
        modifiers.add(new ImmuneToTitleModifier(self, nonMaintenanceBountyHuntersHere, Title.Clash_Of_Sabers));
        return modifiers;
    }

    private boolean canPerformReeYeesAction(SwccgGame game, String playerId) {
        return (GameConditions.canUseForce(game, playerId, 1)
                || (GameConditions.hasUsedPile(game, playerId))
                || (GameConditions.hasHand(game, playerId)));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.isDestinyValueEqualTo(game, 3)
                && canPerformReeYeesAction(game, playerId)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("May choose action.");
            // Perform result(s)
            List<StandardEffect> effectsToChoose = new ArrayList<StandardEffect>();
            effectsToChoose.add(new ActivateForceEffect(action, playerId, 1));
            effectsToChoose.add(new DrawCardIntoHandFromBottomOfUsedPileEffect(action, playerId));
            effectsToChoose.add(new PutCardFromHandOnUsedPileEffect(action, playerId));
            action.appendEffect(
                    new ChooseEffectEffect(action, playerId, effectsToChoose));
            return Collections.singletonList(action);
        }
        return null;
    }
}