package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Alien
 * Title: Corellian
 */
public class Card2_006 extends AbstractAlien {
    public Card2_006() {
        super(Side.LIGHT, 3, 1, 1, 1, 3, "Corellian", Uniqueness.RESTRICTED_3);
        setLore("Brindy Truchong is a typical female Corellian smuggler. Her goal in Mos Eisley is to find a quick means of providing supplies to the Rebellion.");
        setGameText("Once per turn, one weapon or device is deploy -1 onto a Rebel at same site.");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.FEMALE, Keyword.SMUGGLER);
        setSpecies(Species.CORELLIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        final int permCardId = self.getPermanentCardId();
        modifiers.add(new DeployCostToTargetModifier(self, Filters.or(Filters.weapon, Filters.device),
                new Condition() {
                    @Override
                    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                        PhysicalCard self = gameState.findCardByPermanentId(permCardId);

                        return modifiersQuerying.getUntilEndOfTurnLimitCounter(self, null, self.getCardId(), GameTextActionId.OTHER_CARD_ACTION_DEFAULT).getUsedLimit() < 1;
                    }
                }, -1, Filters.and(Filters.Rebel, Filters.atSameSite(self))));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        Filter weaponOrDevice = Filters.or(Filters.weapon, Filters.device);
        Filter rebelAtSameSite = Filters.and(Filters.Rebel, Filters.atSameSite(self));
        if (TriggerConditions.justDeployedToTarget(game, effectResult, weaponOrDevice, rebelAtSameSite)
                || TriggerConditions.justTransferredDeviceOrWeaponToTarget(game, effectResult, weaponOrDevice, rebelAtSameSite)) {
            game.getModifiersQuerying().getUntilEndOfTurnLimitCounter(self, null, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT).incrementToLimit(1, 1);
        }
        return null;
    }
}
