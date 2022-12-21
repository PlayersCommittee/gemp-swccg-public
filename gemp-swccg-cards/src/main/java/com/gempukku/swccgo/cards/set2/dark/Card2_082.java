package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotExistAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Alien
 * Title: Advosze
 */
public class Card2_082 extends AbstractAlien {
    public Card2_082() {
        super(Side.DARK, 2, 3, 2, 2, 2, Title.Advosze, Uniqueness.RESTRICTED_3, ExpansionSet.A_NEW_HOPE, Rarity.C2);
        setLore("Typical of male Advosze, Bom Vimdin despises those of his own species. Pessimistic and territorial, he is a lone smuggler who prefers to work for corrupt officials.");
        setGameText("Adds 2 to power of anything he pilots. Whenever you deploy a weapon or device from hand at same location, activate 1 Force as a 'kickback.' Limit of one Advosze per location.");
        addIcons(Icon.A_NEW_HOPE, Icon.PILOT);
        addKeywords(Keyword.SMUGGLER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployToLocationModifier(self, Filters.sameLocationAs(self, Filters.and(Filters.other(self), Filters.Advosze))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new MayNotExistAtLocationModifier(self, Filters.sameLocationAs(self, Filters.and(Filters.other(self), Filters.Advosze))));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.justDeployedFromHandToLocation(game, effectResult, Filters.and(Filters.your(self), Filters.or(Filters.weapon, Filters.device)), Filters.sameLocation(self))
                && GameConditions.canActivateForce(game, playerId)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Activate 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new ActivateForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
