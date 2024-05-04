package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotInitiateBattleAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Effect
 * Title: Desilijic Tattoo
 */
public class Card7_225 extends AbstractNormalEffect {
    public Card7_225() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, "Desilijic Tattoo", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.U);
        setLore("Jabba the Hutt's clan requires all members to carry ritual scarring identifying them forever as heirs to the Nal Hutta legacy. These tattoos are considered marks of power.");
        setGameText("Deploy on your Hutt. Once during each of your deploy phases, may deploy one Jabba's Influence or Death Mark from Reserve Deck; reshuffle. While at Audience Chamber, no battles or Force drains may take place here unless a Rebel present. (Immune to Alter.)");
        addIcons(Icon.SPECIAL_EDITION);
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.Hutt);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.DESILIJIC_TATTOO__UPLOAD_JABBAS_INFLUENCE_OR_DEATH_MARK;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy a Jabba's Influence or Death Mark from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.Jabbas_Influence, Filters.Death_Mark), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atAudienceChamberAndNoRebelPresent = new AndCondition(new AtCondition(self, Filters.Audience_Chamber),
                new UnlessCondition(new PresentCondition(self, Filters.Rebel)));
        Filter here = Filters.here(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotInitiateBattleAtLocationModifier(self, here, atAudienceChamberAndNoRebelPresent));
        modifiers.add(new MayNotForceDrainAtLocationModifier(self, here, atAudienceChamberAndNoRebelPresent));
        return modifiers;
    }
}