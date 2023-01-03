package com.gempukku.swccgo.cards.set216.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.effects.CancelTargetingEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitFromForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitToForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.TargetingActionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Character
 * Subtype: Alien
 * Title: Ketwol (V)
 */
public class Card216_034 extends AbstractAlien {
    public Card216_034() {
        super(Side.LIGHT, 3, 2, 2, 2, 4, Title.Ketwol, Uniqueness.UNIQUE, ExpansionSet.SET_16, Rarity.V);
        setVirtualSuffix(true);
        setLore("Pacithhip scout. From an unknown system on the Outer Rim. Spends most of his time talking to pilots and travelers at local docking bays.");
        setGameText("[Pilot] 2. Your docking bay transit is free when moving to or from same site. If piloting a freighter or starfighter, may lose 1 Force to cancel an attempt by opponent to target that starship to be captured, 'hit,' or lost.");
        addPersona(Persona.KETWOL);
        setSpecies(Species.PACITHHIP);
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.VIRTUAL_SET_16);
        addKeywords(Keyword.SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter sameSite = Filters.sameSite(self);

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DockingBayTransitFromForFreeModifier(self, sameSite, playerId));
        modifiers.add(new DockingBayTransitToForFreeModifier(self, sameSite, playerId));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(String playerId, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        Filter filter = Filters.and(Filters.or(Filters.freighter, Filters.starfighter), Filters.hasPiloting(self));

        Collection<TargetingReason> targetingReasons = Arrays.asList(TargetingReason.TO_BE_HIT, TargetingReason.TO_BE_LOST, TargetingReason.TO_BE_CAPTURED);

        if (TriggerConditions.isTargetedForReason(game, effect, game.getOpponent(playerId), filter, targetingReasons)) {
            final RespondableEffect respondableEffect = (RespondableEffect) effect;
            final List<PhysicalCard> cardsTargeted = TargetingActionUtils.getCardsTargetedForReason(game, respondableEffect.getTargetingAction(), targetingReasons, filter);
            if (!cardsTargeted.isEmpty()) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Cancel targeting");
                // Pay costs
                action.appendCost(
                        new LoseForceEffect(action, playerId, 1)
                );
                action.appendEffect(
                        new CancelTargetingEffect(action, respondableEffect));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
