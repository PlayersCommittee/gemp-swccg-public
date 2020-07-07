package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ReturnCardToHandFromTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextLandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Character
 * Subtype: Imperial
 * Title: Eighth Brother
 */
public class Card501_005 extends AbstractImperial {
    public Card501_005() {
        super(Side.DARK, 2, 4, 3, 5, 5, "Eighth Brother", Uniqueness.UNIQUE);
        setLore("Terrelian Jango Jumper. Inquisitor");
        setGameText("May move as a 'react' to same site as a 'Hatred' card. During your move phase, unless alone or with Maul, may return one of your Inquisitors here (and all cards on them) to owner's hand. While armed with Inquisitor Lightsaber, may 'fly' (landspeed = 2).");
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_13);
        addKeyword(Keyword.INQUISITOR);
        setSpecies(Species.TERRELIAN_JANGO_JUMPER);
        setTestingText("Eighth Brother");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        Filter siteWithHatredCardStacked = Filters.and(Filters.site, Filters.or(Filters.hasStacked(Filters.hatredCard), Filters.sameSiteAs(self, Filters.and(Filters.character, Filters.hasStacked(Filters.hatredCard)))));
        modifiers.add(new MayMoveAsReactToLocationModifier(self, siteWithHatredCardStacked));
        modifiers.add(new DefinedByGameTextLandspeedModifier(self, new ArmedWithCondition(self, Filters.Inquisitor_Lightsaber), 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.MOVE)
                && !GameConditions.isAlone(game, self)
                && !GameConditions.isWith(game, self, Filters.Maul)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Return Inquisitor To Hand");
            action.appendUsage(
                    new OncePerPhaseEffect(action)
            );
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose target Inquisitor", Filters.and(Filters.here(self), Filters.inquisitor)) {
                        @Override
                        protected void cardTargeted(int targetGroupId, PhysicalCard targetedCard) {
                            new ReturnCardToHandFromTableEffect(action, targetedCard, Zone.HAND);
                        }
                    }
            );

            return Collections.singletonList(action);
        }
        return null;
    }
}