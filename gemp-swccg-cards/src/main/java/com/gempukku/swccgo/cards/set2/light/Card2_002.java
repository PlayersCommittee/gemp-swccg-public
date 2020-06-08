package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.evaluators.HandSizeEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Alien
 * Title: Brainiac
 */
public class Card2_002 extends AbstractAlien {
    public Card2_002() {
        super(Side.LIGHT, Math.PI, null, null, 3, null, "Brainiac", Uniqueness.UNIQUE);
        setLore("BoShek nicknamed this male Siniteen 'Brainiac' due to his ability to calculate hyperspace coordinates in his head. 'He's the brains, sweetheart!'");
        setGameText("* Power = v(3(X-Y)+2(A-B)+ p) (minimum power=1). X = Dark Side hand cards; Y = Light Side hand cards; A = total number of Dark icons in play; B = total number of Light icons in play; p?= 3.141592653589793238462643383...");
        addIcons(Icon.A_NEW_HOPE, Icon.NAV_COMPUTER);
        setSpecies(Species.SINITEEN);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(final SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        final int permCardId = self.getPermanentCardId();
        modifiers.add(new DefinedByGameTextPowerModifier(self,
                new BaseEvaluator() {
                    @Override
                    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                        PhysicalCard self = gameState.findCardByPermanentId(permCardId);

                        int totalDarkIcons = 0;
                        int totalLightIcons = 0;
                        for (PhysicalCard card : Filters.filterActive(game, self, Filters.in_play)) {
                            totalDarkIcons += modifiersQuerying.getIconCount(gameState, card, Icon.DARK_FORCE);
                            totalLightIcons += modifiersQuerying.getIconCount(gameState, card, Icon.LIGHT_FORCE);
                        }
                        double value = Math.max(1.0, (3 * (gameState.getHand(gameState.getDarkPlayer()).size() - gameState.getHand(gameState.getLightPlayer()).size())) +
                                (2 * (totalDarkIcons - totalLightIcons)) + Math.PI);
                        return (float) Math.max(1.0, Math.sqrt(value));
                    }
                }));
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, new HandSizeEvaluator(game.getLightPlayer())));
        modifiers.add(new DefinedByGameTextForfeitModifier(self, new HandSizeEvaluator(game.getDarkPlayer())));
        return modifiers;
    }
}
