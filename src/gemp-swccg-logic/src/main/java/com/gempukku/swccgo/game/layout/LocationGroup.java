package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.SnapshotData;
import com.gempukku.swccgo.logic.timing.Snapshotable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a part of the location layout where the cards meeting a specified filter deploy.
 */
public class LocationGroup implements Snapshotable<LocationGroup> {

    private String _humanReadable;
    // Filters for locations in this group
    private Filter _filters;
    // Cards in order (left to right)
    private List<List<PhysicalCard>> _cardsInGroup = new LinkedList<List<PhysicalCard>>();
    // Cards deployed between consecutive stacks. Gap i sits between stack i and stack i+1.
    private List<List<PhysicalCard>> _betweenGaps = new LinkedList<List<PhysicalCard>>();

    /**
     * Needed to generate snapshot.
     */
    public LocationGroup() {
    }

    @Override
    public void generateSnapshot(LocationGroup selfSnapshot, SnapshotData snapshotData) {
        LocationGroup snapshot = selfSnapshot;

        // Set each field
        snapshot._humanReadable = _humanReadable;
        snapshot._filters = _filters;
        for (List<PhysicalCard> groupList : _cardsInGroup) {
            List<PhysicalCard> snapShotList = new LinkedList<PhysicalCard>();
            snapshot._cardsInGroup.add(snapShotList);
            for (PhysicalCard card : groupList) {
                snapShotList.add(snapshotData.getDataForSnapshot(card));
            }
        }
        for (List<PhysicalCard> gap : _betweenGaps) {
            List<PhysicalCard> snapShotGap = new LinkedList<PhysicalCard>();
            snapshot._betweenGaps.add(snapShotGap);
            for (PhysicalCard card : gap) {
                snapShotGap.add(snapshotData.getDataForSnapshot(card));
            }
        }
    }

    /**
     * Creates a location group for locations accepted by the specified filter.
     * @param humanReadable the name of the group
     * @param filters the filter
     */
    public LocationGroup(String humanReadable, Filter filters) {
        _humanReadable = humanReadable;
        _filters = filters;
    }

    /**
     * Gets the name of the group
     * @return the name
     */
    public String getHumanReadable() {
        return _humanReadable;
    }

    /**
     * Gets the filter for the group.
     * @return the filter
     */
    public Filter getFilters()
    {
        return _filters;
    }

    /**
     * Determines if this group is enabled for cards to deploy to it.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @return true or false
     */
    public boolean isGroupEnabled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return true;
    }

    /**
     * Gets the location zone index of a location in this group. This determines the left-to-right ordering on the table
     * relative to other groups (lower index is left of a higher index).
     * @return the location index for a location in the group.
     */
    public Integer getLocationZoneIndex() {
        List<PhysicalCard> topCards = getTopCardsInGroup();
        if (topCards.isEmpty())
            return null;

        return topCards.get(0).getLocationZoneIndex();
    }


    /**
     * Gets the locations in the group in order (left to right). Within each sub-list, the top location
     * is first.
     * @return the locations
     */
    public List<List<PhysicalCard>> getCardsInGroup() {
        return _cardsInGroup;
    }

    /**
     * Gets the top locations in the group in order (left to right).
     * @return the top locations
     */
    public List<PhysicalCard> getTopCardsInGroup() {
        List<PhysicalCard> topCardsInGroup = new LinkedList<PhysicalCard>();
        for (List<PhysicalCard> locationStack : _cardsInGroup) {
            if (!locationStack.isEmpty()) {
                topCardsInGroup.add(locationStack.get(0));
            }
        }
        return topCardsInGroup;
    }

    /**
     * Gets the converted locations in the group in order (left to right).
     * @return the converted locations
     */
    public List<List<PhysicalCard>> getConvertedCardsInGroup() {
        List<List<PhysicalCard>> nonTopCardsInGroup = new LinkedList<List<PhysicalCard>>();
        for (List<PhysicalCard> locationStack : _cardsInGroup) {
            if (!locationStack.isEmpty()) {
                List<PhysicalCard> nonTopCards = new LinkedList<PhysicalCard>();
                nonTopCards.addAll(locationStack.subList(1, locationStack.size()));
                nonTopCardsInGroup.add(nonTopCards);
            }
        }
        return nonTopCardsInGroup;
    }

    /**
     * Converts (or rebuild) the old location with the new location.
     * @param newLocation the new location
     * @param oldLocation the old location
     */
    public void convertOrRebuildLocation(PhysicalCard newLocation, PhysicalCard oldLocation) {
        for (List<PhysicalCard> locationStack : _cardsInGroup) {
            if (!locationStack.isEmpty() && locationStack.get(0).getCardId() == oldLocation.getCardId()) {
                // Set the inverted value of the location to the same at the previous top location
                newLocation.setInverted(locationStack.get(0).isInverted());
                locationStack.add(0, newLocation);
                // The collapsed and inverted value of the new location is set for any converted locations
                for (PhysicalCard locationInStack : locationStack) {
                    locationInStack.setCollapsed(newLocation.isCollapsed());
                    locationInStack.setInverted(newLocation.isInverted());
                }
                return;
            }
        }
    }

    /**
     * Adds the location (not conversion) to the location group in the specified place in the group.
     * @param index the count (from the left) where to insert the location.
     * @param card the location
     */
    public void addLocation(int index, PhysicalCard card) {
        addLocation(index, card, true);
    }

    /**
     * Adds a location stack. When inserting into an existing gap that already has
     * between-sites cards, existingGapCardsStayRightOfNewSite chooses A-B-gate-D
     * (true) versus A-gate-B-D (false).
     */
    public void addLocation(int index, PhysicalCard card, boolean existingGapCardsStayRightOfNewSite) {
        LinkedList<PhysicalCard> cardStack = new LinkedList<PhysicalCard>();
        cardStack.add(card);
        int n = _cardsInGroup.size();
        if (index >= n) {
            _cardsInGroup.add(cardStack);
            if (n >= 1) {
                _betweenGaps.add(new LinkedList<PhysicalCard>());
            }
            return;
        }
        if (index < 0) {
            index = 0;
        }
        _cardsInGroup.add(index, cardStack);
        if (n == 0) {
            return;
        }
        if (index == 0) {
            _betweenGaps.add(0, new LinkedList<PhysicalCard>());
            return;
        }
        List<PhysicalCard> oldGap = _betweenGaps.get(index - 1);
        List<PhysicalCard> leftGap = new LinkedList<PhysicalCard>();
        List<PhysicalCard> rightGap = new LinkedList<PhysicalCard>();
        if (existingGapCardsStayRightOfNewSite) {
            rightGap.addAll(oldGap);
        }
        else {
            leftGap.addAll(oldGap);
        }
        _betweenGaps.set(index - 1, leftGap);
        _betweenGaps.add(index, rightGap);
    }

    /**
     * Removes the location from the group (if it exists).
     * @param location the location
     * @return true if location found and removed, otherwise false
     */
    public boolean removeLocation(PhysicalCard location) {
        for (int s = 0; s < _cardsInGroup.size(); s++) {
            List<PhysicalCard> locationStack = _cardsInGroup.get(s);
            Iterator<PhysicalCard> iterator = locationStack.iterator();

            while (iterator.hasNext()) {
                PhysicalCard card = iterator.next();
                if (card == location) {
                    iterator.remove();
                    if (locationStack.isEmpty()) {
                        _cardsInGroup.remove(s);
                        if (_cardsInGroup.isEmpty()) {
                            _betweenGaps.clear();
                        }
                        else if (s == 0) {
                            if (!_betweenGaps.isEmpty()) {
                                _betweenGaps.remove(0);
                            }
                        }
                        else if (s >= _betweenGaps.size()) {
                            if (!_betweenGaps.isEmpty()) {
                                _betweenGaps.remove(_betweenGaps.size() - 1);
                            }
                        }
                        else {
                            // Drop both gaps adjacent to the removed stack. Gates bound
                            // to that site leave the row (orphaned) instead of merging
                            // onto a neighbor pair they were never between.
                            _betweenGaps.remove(s);
                            _betweenGaps.remove(s - 1);
                            _betweenGaps.add(s - 1, new LinkedList<PhysicalCard>());
                        }
                    }
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Reorders the location stacks in this group. Each stack stays together, so
     * converted locations remain under the same top site. newOrder is a
     * permutation of 0..size-1 describing the new left-to-right order of the
     * current stacks.
     * @param newOrder permutation of current stack indexes
     * @return true if the stacks were reordered; false if newOrder is not a valid permutation
     */
    public boolean reorderStacks(List<Integer> newOrder) {
        int size = _cardsInGroup.size();
        if (newOrder == null || newOrder.size() != size) {
            return false;
        }
        boolean[] seen = new boolean[size];
        for (Integer index : newOrder) {
            if (index == null || index < 0 || index >= size || seen[index]) {
                return false;
            }
            seen[index] = true;
        }
        List<PhysicalCard> oldTops = getTopCardsInGroup();
        List<List<PhysicalCard>> oldGaps = new LinkedList<List<PhysicalCard>>();
        for (List<PhysicalCard> gap : _betweenGaps) {
            oldGaps.add(new LinkedList<PhysicalCard>(gap));
        }
        List<List<PhysicalCard>> reordered = new LinkedList<List<PhysicalCard>>();
        for (Integer index : newOrder) {
            reordered.add(_cardsInGroup.get(index));
        }
        _cardsInGroup.clear();
        _cardsInGroup.addAll(reordered);
        rebindGapsAfterReorder(oldTops, oldGaps);
        return true;
    }

    /**
     * After a stack permutation, put each old gap's cards on the first hop from
     * the left-er of that pair toward the right-er in the new order.
     */
    private void rebindGapsAfterReorder(List<PhysicalCard> oldTops, List<List<PhysicalCard>> oldGaps) {
        List<PhysicalCard> newTops = getTopCardsInGroup();
        int gapCount = Math.max(0, newTops.size() - 1);
        List<List<PhysicalCard>> newGaps = new LinkedList<List<PhysicalCard>>();
        for (int i = 0; i < gapCount; i++) {
            newGaps.add(new LinkedList<PhysicalCard>());
        }
        for (int i = 0; i < oldGaps.size(); i++) {
            if (i + 1 >= oldTops.size()) {
                break;
            }
            int ia = indexOfTop(newTops, oldTops.get(i));
            int ib = indexOfTop(newTops, oldTops.get(i + 1));
            if (ia < 0 || ib < 0 || newGaps.isEmpty()) {
                continue;
            }
            int lo = Math.min(ia, ib);
            if (lo >= newGaps.size()) {
                lo = newGaps.size() - 1;
            }
            newGaps.get(lo).addAll(oldGaps.get(i));
        }
        _betweenGaps.clear();
        _betweenGaps.addAll(newGaps);
    }

    public List<List<PhysicalCard>> getBetweenGaps() {
        return _betweenGaps;
    }

    public List<PhysicalCard> getAllBetweenSiteCards() {
        List<PhysicalCard> cards = new LinkedList<PhysicalCard>();
        for (List<PhysicalCard> gap : _betweenGaps) {
            cards.addAll(gap);
        }
        return cards;
    }

    public boolean containsBetweenSiteCard(PhysicalCard card) {
        return getLeftSiteOfBetweenCard(card) != null;
    }

    public boolean addBetweenSiteCard(PhysicalCard leftSite, PhysicalCard rightSite, PhysicalCard card) {
        List<PhysicalCard> tops = getTopCardsInGroup();
        int iLeft = indexOfTop(tops, leftSite);
        int iRight = indexOfTop(tops, rightSite);
        if (iLeft < 0 || iRight < 0 || Math.abs(iLeft - iRight) != 1) {
            return false;
        }
        int gapIndex = Math.min(iLeft, iRight);
        while (_betweenGaps.size() <= gapIndex) {
            _betweenGaps.add(new LinkedList<PhysicalCard>());
        }
        _betweenGaps.get(gapIndex).add(card);
        return true;
    }

    public boolean removeBetweenSiteCard(PhysicalCard card) {
        for (List<PhysicalCard> gap : _betweenGaps) {
            if (gap.remove(card)) {
                return true;
            }
        }
        return false;
    }

    public PhysicalCard getLeftSiteOfBetweenCard(PhysicalCard card) {
        List<PhysicalCard> tops = getTopCardsInGroup();
        for (int i = 0; i < _betweenGaps.size(); i++) {
            for (PhysicalCard gate : _betweenGaps.get(i)) {
                if (gate.getCardId() == card.getCardId()) {
                    if (i < tops.size()) {
                        return tops.get(i);
                    }
                    return null;
                }
            }
        }
        return null;
    }

    public PhysicalCard getRightSiteOfBetweenCard(PhysicalCard card) {
        List<PhysicalCard> tops = getTopCardsInGroup();
        for (int i = 0; i < _betweenGaps.size(); i++) {
            for (PhysicalCard gate : _betweenGaps.get(i)) {
                if (gate.getCardId() == card.getCardId()) {
                    if (i + 1 < tops.size()) {
                        return tops.get(i + 1);
                    }
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Visual columns in this group: each location stack, then each between-sites
     * card in the gap to its right as its own column.
     */
    public List<List<PhysicalCard>> getVisualColumns() {
        List<List<PhysicalCard>> cols = new LinkedList<List<PhysicalCard>>();
        for (int i = 0; i < _cardsInGroup.size(); i++) {
            cols.add(_cardsInGroup.get(i));
            if (i < _betweenGaps.size()) {
                for (PhysicalCard gate : _betweenGaps.get(i)) {
                    List<PhysicalCard> col = new LinkedList<PhysicalCard>();
                    col.add(gate);
                    cols.add(col);
                }
            }
        }
        return cols;
    }

    public List<PhysicalCard> getVisualRowCards() {
        List<PhysicalCard> row = new LinkedList<PhysicalCard>();
        for (List<PhysicalCard> col : getVisualColumns()) {
            if (!col.isEmpty()) {
                row.add(col.get(0));
            }
        }
        return row;
    }

    /**
     * Reorders stacks so the given top locations appear in that left-to-right
     * order. Converted cards stay in the same stack under the same top. If
     * newTopOrder is a subset of this group's tops, only those stacks swap
     * among the slots they currently occupy; other stacks stay put. An empty
     * order does nothing.
     * @param newTopOrder requested left-to-right order of top location cards
     * @return true if applied or already matched; false if a card is not a top in this group or the list has duplicates
     */
    public boolean reorderTopLocations(List<? extends PhysicalCard> newTopOrder) {
        if (newTopOrder == null || newTopOrder.isEmpty()) {
            return true;
        }
        List<Integer> permutation = permutationForTopOrder(newTopOrder);
        if (permutation == null) {
            return false;
        }
        return reorderStacks(permutation);
    }

    /**
     * What the top row would look like after reorderTopLocations, without changing
     * the group. Returns null if the order is not valid.
     * @param newTopOrder requested left-to-right order of top location cards
     * @return the resulting top cards, or null if the order is invalid
     */
    public List<PhysicalCard> previewTopLocations(List<? extends PhysicalCard> newTopOrder) {
        List<PhysicalCard> tops = getTopCardsInGroup();
        if (newTopOrder == null || newTopOrder.isEmpty()) {
            return new ArrayList<PhysicalCard>(tops);
        }
        List<Integer> permutation = permutationForTopOrder(newTopOrder);
        if (permutation == null) {
            return null;
        }
        List<PhysicalCard> preview = new ArrayList<PhysicalCard>();
        for (Integer index : permutation) {
            preview.add(tops.get(index));
        }
        return preview;
    }

    private List<Integer> permutationForTopOrder(List<? extends PhysicalCard> newTopOrder) {
        List<PhysicalCard> tops = getTopCardsInGroup();
        if (hasDuplicateTops(newTopOrder)) {
            return null;
        }
        for (PhysicalCard card : newTopOrder) {
            if (indexOfTop(tops, card) < 0) {
                return null;
            }
        }
        List<Integer> selectedSlots = new ArrayList<Integer>();
        for (int i = 0; i < tops.size(); ++i) {
            if (containsTop(newTopOrder, tops.get(i))) {
                selectedSlots.add(i);
            }
        }
        if (selectedSlots.size() != newTopOrder.size()) {
            return null;
        }
        List<Integer> permutation = new ArrayList<Integer>();
        for (int i = 0; i < tops.size(); ++i) {
            permutation.add(i);
        }
        for (int i = 0; i < selectedSlots.size(); ++i) {
            permutation.set(selectedSlots.get(i), indexOfTop(tops, newTopOrder.get(i)));
        }
        return permutation;
    }

    private int indexOfTop(List<PhysicalCard> tops, PhysicalCard card) {
        for (int i = 0; i < tops.size(); ++i) {
            if (tops.get(i).getCardId() == card.getCardId()) {
                return i;
            }
        }
        return -1;
    }

    private boolean containsTop(List<? extends PhysicalCard> cards, PhysicalCard card) {
        return indexOfTop(new LinkedList<PhysicalCard>(cards), card) >= 0;
    }

    private boolean hasDuplicateTops(List<? extends PhysicalCard> cards) {
        for (int i = 0; i < cards.size(); ++i) {
            for (int j = i + 1; j < cards.size(); ++j) {
                if (cards.get(i).getCardId() == cards.get(j).getCardId()) {
                    return true;
                }
            }
        }
        return false;
    }
}