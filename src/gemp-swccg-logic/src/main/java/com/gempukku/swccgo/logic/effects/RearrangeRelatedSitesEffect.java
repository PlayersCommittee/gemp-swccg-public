package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * This effect will atomically reorder a parameterized group of related sites.
 * Converted stacks stay under the same top; cards stay attached.
 */
public class RearrangeRelatedSitesEffect extends AbstractSuccessfulEffect {

    /**
     * Creates the rearranging-sites effect. Implementation is incoming.
     * @param action the action performing this effect
     */
    public RearrangeRelatedSitesEffect(Action action) {
        super(action);
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        // TODO: reorder the parameterized related-site group
    }
}
