package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.*;

/**
 * Set: Block 3
 * Type: Effect
 * Title: Hutt Bounty (V)
 */
public class Card601_007 extends AbstractNormalEffect {
    public Card601_007() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, Title.Hutt_Bounty, Uniqueness.UNIQUE);
        setLore("'Chissaa, picha gawanki Chewbacca. Yupon cogorato kama walpa kyess kashung kawa Wookiee.'");
        setGameText("Deploy on Jabba. Your battle destiny draws are +1. At related sites you control, your Force generation is +1. During battle here, may make a non-Jedi character present with Jabba power = 0.");
        addIcons(Icon.JABBAS_PALACE, Icon.LEGACY_BLOCK_3);
        addKeywords(Keyword.BOUNTY, Keyword.DEPLOYS_ON_CHARACTERS);
        setVirtualSuffix(true);
        setAsLegacy(true);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Jabba;
    }


    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachBattleDestinyModifier(self, 1, self.getOwner()));
        modifiers.add(new ForceGenerationModifier(self, Filters.and(Filters.relatedSite(self), Filters.controls(self.getOwner())), 1, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringBattleAt(game, Filters.here(self))
                && GameConditions.canSpot(game, self, Filters.and(Filters.participatingInBattle, Filters.non_Jedi_character, Filters.presentWith(self, Filters.Jabba)))
        ) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Make a non-Jedi character power 0");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target a non-Jedi character with Jabba",
                    Filters.and(Filters.participatingInBattle, Filters.non_Jedi_character, Filters.presentWith(self, Filters.Jabba))) {
                @Override
                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                    // Perform result(s)
                    action.allowResponses(new RespondableEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                            action.appendEffect(
                                    new ResetPowerEffect(action, finalTarget, 0));
                        }
                    });
                }
            });
            actions.add(action);
        }

        return actions;
    }
}