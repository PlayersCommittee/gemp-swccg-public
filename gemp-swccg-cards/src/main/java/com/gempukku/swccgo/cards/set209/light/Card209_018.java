package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Set: Set 9
 * Type: Effect
 * Title: Stardust
 */
public class Card209_018 extends AbstractNormalEffect {
    public Card209_018() {
        super(Side.LIGHT, 6, PlayCardZoneOption.ATTACHED, Title.Stardust, Uniqueness.UNIQUE);
        setLore("");
        setGameText("Deploy on Data Vault. At any time, may relocate Stardust to your spy present. During your control phase, if on your spy at a battleground you occupy, opponent loses 2 Force. If about to leave table, relocate to Data Vault (if possible). [Immune to Alter.]");
        addIcons(Icon.VIRTUAL_SET_9);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.DataVault;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        Filter yourSpyFilter = Filters.and(Filters.your(self), Filters.spy, Filters.presentWith(self), Filters.not(Filters.hasAttached(Filters.Stardust)));

        // As far as I can tell, this Reason text does not get used anywhere, but it's needed for the SpotOverride
        Set<TargetingReason> targetingReasonSet = new HashSet<TargetingReason>();
        targetingReasonSet.add(TargetingReason.TO_RELOCATE_STARDUST_TO);

        // Check condition(s)
        if (GameConditions.canTarget(game, self, false, SpotOverride.INCLUDE_UNDERCOVER, yourSpyFilter)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Relocate to your spy");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", SpotOverride.INCLUDE_UNDERCOVER, targetingReasonSet, yourSpyFilter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Relocate " + GameUtils.getCardLink(self) + " to " + GameUtils.getCardLink(targetedCard),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new AttachCardFromTableEffect(action, self, targetedCard));
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    // jcTODO: 2) Control phase force loss (copy from Tat Occ?)
    // jcTODO: 3) Relocate to DataVault as getGameTextRequiredAfterTriggers

}