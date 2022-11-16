package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtRandomCardsInOpponentsHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.MayBeBattledModifier;
import com.gempukku.swccgo.logic.modifiers.MayForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayInitiateBattleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Character
 * Subtype: Droid
 * Title: Probe Droid
 */
public class Card3_090 extends AbstractDroid {
    public Card3_090() {
        super(Side.DARK, 2, 2, 2, 5, Title.Probe_Droid, Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.C2);
        setArmor(3);
        setLore("Arakyd Viper probe droid. Has sensors specifically designed to detect traces of Rebel activity. Equipped with an auto-destruct mechanism. A highly durable spy droid.");
        setGameText("Deploys only if a Star Destroyer on table. Once during each of your control phases, may peek at X cards randomly selected from opponent's hand, where X = number of [Light Side Force] icons at same site. May Force drain, initiate battle and be battled.");
        addIcons(Icon.HOTH);
        addKeywords(Keyword.SPY);
        addModelType(ModelType.PROBE);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.canSpot(game, self, Filters.Star_Destroyer);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.hasHand(game, opponent)
                && GameConditions.isAtLocation(game, self, Filters.site)) {
            int forceIcons = GameConditions.getNumForceIconsHere(game, self, false, true);
            if (forceIcons > 0
                    && GameConditions.numCardsInHand(game, opponent) >= forceIcons) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Peek at " + forceIcons + " random cards in opponents hand");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new PeekAtRandomCardsInOpponentsHandEffect(action, playerId, forceIcons));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayForceDrainModifier(self));
        modifiers.add(new MayInitiateBattleModifier(self));
        modifiers.add(new MayBeBattledModifier(self));
        return modifiers;
    }
}
