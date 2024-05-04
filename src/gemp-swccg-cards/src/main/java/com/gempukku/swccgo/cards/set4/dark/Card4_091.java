package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.evaluators.MinEvaluator;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutCardFromUsedPileInLostPileEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromPileEffect;
import com.gempukku.swccgo.logic.modifiers.MayBeBattledModifier;
import com.gempukku.swccgo.logic.modifiers.MayInitiateBattleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Character
 * Subtype: Droid
 * Title: 4-LOM
 */
public class Card4_091 extends AbstractDroid {
    public Card4_091() {
        super(Side.DARK, 1, 2, 2, 3, "4-LOM", Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.R);
        setArmor(3);
        setLore("Information Broker. Thief. Overrode and reprogrammed its own self-governing software. Convinced Jabba the Hut to upgrade its armor and weaponry. Became a bounty hunter.");
        setGameText("Total power at same site is +1 for each of your alien/droid pairs present. Once during each battle, if present with Zuckuss, may use 1 Force to search any Used Pile and move 1 character there to the Lost Pile. May initiate battle and be battled.");
        addPersona(Persona._4_LOM);
        addIcons(Icon.DAGOBAH, Icon.WARRIOR);
        addKeywords(Keyword.INFORMATION_BROKER, Keyword.THIEF, Keyword.BOUNTY_HUNTER);
        addModelType(ModelType.PROTOCOL);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalPowerModifier(self, Filters.sameSite(self),
                new MinEvaluator(new PresentEvaluator(self, Filters.and(Filters.your(self), Filters.alien)),
                        new PresentEvaluator(self, Filters.and(Filters.your(self), Filters.droid))), self.getOwner()));
        modifiers.add(new MayInitiateBattleModifier(self));
        modifiers.add(new MayBeBattledModifier(self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId._4_LOM__SEARCH_USED_PILE;

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isInBattle(game, self)
                && GameConditions.canUseForce(game, playerId, 1)
                && (GameConditions.canSearchUsedPile(game, playerId, self, gameTextActionId)
                || GameConditions.canSearchOpponentsUsedPile(game, playerId, self, gameTextActionId))
                && GameConditions.isPresentWith(game, self, Filters.Zuckuss)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Search any Used Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new ChooseExistingCardPileEffect(action, playerId, Zone.USED_PILE) {
                        @Override
                        protected void pileChosen(SwccgGame game, final String cardPileOwner, final Zone cardPile) {
                            action.setActionMsg("Search " + cardPileOwner + "'s Used Pile for a character");
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 1));
                            // Perform result(s)
                            action.appendEffect(
                                    new ChooseCardFromPileEffect(action, playerId, cardPile, cardPileOwner, Filters.character) {
                                        @Override
                                        protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
                                            action.appendEffect(
                                                    new PutCardFromUsedPileInLostPileEffect(action, playerId, selectedCard));
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
