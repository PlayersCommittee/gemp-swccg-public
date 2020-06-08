package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Theed Palace
 * Type: Effect
 * Title: Naboo Celebration
 */
public class Card14_034 extends AbstractNormalEffect {
    public Card14_034() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Naboo Celebration", Uniqueness.UNIQUE);
        setLore("The heroic actions of Amidala's forces and Boss Nass' army saved Naboo from the Trade Federation. Definitely a cause for celebration!");
        setGameText("Deploy on table. If Amidala at a Theed Palace site (or Boss Nass at an exterior Naboo site), opponent's cards with ability deploy +2 there. Once during battle at a Naboo site, if you just drew a Gungan or Royal Naboo Security for battle destiny, opponent loses 1 Force.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition condition = new OrCondition(new AtCondition(self, Filters.Amidala, Filters.Theed_Palace_site),
                new AtCondition(self, Filters.Boss_Nass, Filters.exterior_Naboo_site));
        Filter sameSiteAsAmidala = Filters.and(Filters.Theed_Palace_site, Filters.sameSiteAs(self, Filters.Amidala));
        Filter sameSiteAsBossNass = Filters.and(Filters.exterior_Naboo_site, Filters.sameSiteAs(self, Filters.Boss_Nass));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.hasAbilityOrHasPermanentPilotWithAbility),
                condition, 2, Filters.or(sameSiteAsAmidala, sameSiteAsBossNass)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.isDestinyCardMatchTo(game, Filters.or(Filters.Gungan, Filters.Royal_Naboo_Security))
                && GameConditions.isDuringBattleAt(game, Filters.Naboo_site)
                && GameConditions.isOncePerBattle(game, self, gameTextSourceCardId)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make " + opponent + " lose 1 Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}