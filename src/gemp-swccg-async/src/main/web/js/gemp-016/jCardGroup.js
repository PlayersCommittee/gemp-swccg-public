var CardGroup = Class.extend({
    container:null,
    x:null,
    y:null,
    width:null,
    height:null,
    belongTestFunc:null,
    padding:3,
    maxCardHeight:210,
    maxCardWidth:150,
    descDiv:null,
    locationIndex:null,
    bottomPlayerId:null,

    init:function (container, belongTest, createDiv, locationIndex, bottomPlayerId) {
        this.container = container;
        this.belongTestFunc = belongTest;
        this.locationIndex = locationIndex;
        this.bottomPlayerId = bottomPlayerId;

        if (createDiv === undefined || createDiv) {
            this.descDiv = $("<div class='ui-widget-content'></div>");
            this.descDiv.css({"border-radius":"0px"});

            container.append(this.descDiv);
        }
    },

    getCardElems:function () {
        var cardsToLayout = new Array();
        var that = this;
        $(".card", this.container).each(function (index) {
            var card = $(this).data("card");
            if (that.belongTestFunc(card)) {
                cardsToLayout.push($(this));
            }
        });
        return cardsToLayout;
    },

    cardBelongs:function (cardData) {
        return this.belongTestFunc(cardData);
    },

    setBounds:function (x, y, width, height) {
        this.x = x + 3;
        this.y = y + 3;
        this.width = width - 6;
        this.height = height - 6;
        if (this.descDiv != null) {
            this.descDiv.css({left:x + "px", top:y + "px", width:width, height:height, position:"absolute"});
            this.descDiv.css({display:""});
        }
        this.layoutCards();
    },

    hide:function () {
        if (this.descDiv != null)
            this.descDiv.css({display:"none"});
    },

    layoutCards:function () {
        alert("This should be overridden by the extending classes");
    },

    layoutCard:function (cardElem, x, y, width, height, index, cardData) {

        if (cardData != null) {
            var rotateToInvertValue = 0;
            if (cardData.inverted) {
                rotateToInvertValue = 180;
            }
            var rotateToSidewaysValue = 0;
            if (cardData.sideways) {
                rotateToSidewaysValue = 90;
            }

            var callRotate = false;

            // Check if card needs to be inverted from how it currently is
            if (cardData.zone != "ATTACHED" && cardData.zone != "STACKED" && cardData.zone != "STACKED_FACE_DOWN") {
                if (this.bottomPlayerId === undefined || this.bottomPlayerId == null || cardData.owner === undefined || cardData.owner == null) {
                    if (cardData.inverted!=cardData.upsideDown) {
                        callRotate = true;
                        rotateToInvertValue = (rotateToInvertValue + 180) % 360;
                        cardData.inverted = !cardData.inverted;
                    }
                } else if ((this.bottomPlayerId==cardData.owner) != (cardData.inverted==cardData.upsideDown)) {
                    callRotate = true;
                    rotateToInvertValue = (rotateToInvertValue + 180) % 360;
                    cardData.inverted = !cardData.inverted;
                }
            }

            // Check if card needs to be rotated sideways from how it currently is
            if (cardData.sideways!=cardData.onSide) {
                callRotate = true;
                rotateToSidewaysValue = (rotateToSidewaysValue + 90) % 180;
                cardData.sideways = !cardData.sideways;
            }

            // Call rotate if needed
            if (callRotate) {
                $(cardElem).rotate(rotateToInvertValue + rotateToSidewaysValue);
            }
        }

        layoutCardElem(cardElem, x, y, width, height, index, cardData);

        layoutTokens(cardElem);
    }
});


var NormalCardGroup = CardGroup.extend({

    init:function (container, belongTest, createDiv, locationIndex, bottomPlayerId) {
        this._super(container, belongTest, createDiv, locationIndex, bottomPlayerId);
    },

    layoutCards:function () {
        var cardsToLayout = this.getCardElems();
        if (cardsToLayout.length == 0) {
            return;
        }

        var proportionsArray = this.getCardsWithAttachmentWidthProportion(cardsToLayout);

        var rows = 0;
        var result = false;
        do {
            rows++;
            result = this.layoutInRowsIfPossible(cardsToLayout, proportionsArray, rows);
        } while (!result);
    },

    getAttachedCardsWidth:function (maxDimension, cardData) {
        var result = 0;
        for (var i = 0; i < cardData.attachedCards.length; i++) {
            var attachedCardData = cardData.attachedCards[i].data("card");
            result += attachedCardData.getWidthForMaxDimension(maxDimension);
            result += this.getAttachedCardsWidth(maxDimension, attachedCardData);
        }
        return result;
    },

    getCardsWithAttachmentWidthProportion:function (cardsToLayout) {
        var proportionsArray = new Array();
        for (var cardIndex in cardsToLayout) {
            var cardData = cardsToLayout[cardIndex].data("card");
            var cardWithAttachmentWidth = cardData.getWidthForMaxDimension(1000);
            cardWithAttachmentWidth += this.getAttachedCardsWidth(1000, cardData) * 0.2;
            proportionsArray.push(cardWithAttachmentWidth / 1000);
        }
        return proportionsArray;
    },

    layoutInRowsIfPossible:function (cardsToLayout, proportionsArray, rowCount) {
        if (rowCount == 1) {
            var oneRowHeight = this.getHeightForLayoutInOneRow(proportionsArray);
            if (oneRowHeight * 2 + this.padding > this.height) {
                this.layoutInRow(cardsToLayout, oneRowHeight);
                return true;
            } else {
                return false;
            }
        } else {
            if (this.tryIfCanLayoutInRows(rowCount, proportionsArray)) {
                this.layoutInRows(rowCount, cardsToLayout);
                return true;
            } else {
                return false;
            }
        }
    },

    getHeightForLayoutInOneRow:function (proportionsArray) {
        var totalWidth = 0;
        for (var cardIndex in proportionsArray)
            totalWidth += proportionsArray[cardIndex] * this.height;

        var widthWithoutPadding = this.width - (this.padding * (proportionsArray.length - 1));
        if (totalWidth > widthWithoutPadding) {
            return Math.floor(this.height / (totalWidth / widthWithoutPadding));
        } else {
            return this.height;
        }
    },

    tryIfCanLayoutInRows:function (rowCount, proportionsArray) {
        var rowHeight = (this.height - (this.padding * (rowCount - 1))) / rowCount;
        if (this.maxCardHeight != null)
            rowHeight = Math.min(this.maxCardHeight, rowHeight);
        var totalWidth = 0;
        var row = 0;
        for (var cardIndex in proportionsArray) {
            var cardWidthWithAttachments = proportionsArray[cardIndex] * rowHeight;
            totalWidth += cardWidthWithAttachments;
            if (totalWidth > this.width) {
                row++;
                if (row >= rowCount)
                    return false;
                totalWidth = cardWidthWithAttachments;
            }
            totalWidth += this.padding;
        }
        return true;
    },

    layoutAttached:function (cardData, y, height, layoutVars) {
        for (var i = 0; i < cardData.attachedCards.length; i++) {
            var attachedCardData = cardData.attachedCards[i].data("card");
            var attachedCardWidth = attachedCardData.getWidthForMaxDimension(height);
            this.layoutAttached(attachedCardData, y, height, layoutVars);
            this.layoutCard(cardData.attachedCards[i], this.x + layoutVars.x, this.y + y, attachedCardWidth, attachedCardData.getHeightForWidth(attachedCardWidth), layoutVars.index, attachedCardData);
            layoutVars.x += Math.floor(attachedCardWidth * 0.2);
            layoutVars.index++;
        }
    },

    layoutInRow:function (cardsToLayout, height) {
        if (this.maxCardHeight != null)
            height = Math.min(this.maxCardHeight, height);
        var layoutVars = {};
        layoutVars.x = 0;
        var y = Math.floor((this.height - height) / 2);

        for (var cardIndex in cardsToLayout) {
            layoutVars.index = 10;
            var cardElem = cardsToLayout[cardIndex];
            var cardData = cardElem.data("card");
            var cardWidth = cardData.getWidthForMaxDimension(height);

            this.layoutAttached(cardData, y, height, layoutVars)

            this.layoutCard(cardElem, this.x + layoutVars.x, this.y + y, cardWidth, cardData.getHeightForWidth(cardWidth), layoutVars.index, cardData);
            layoutVars.x += Math.floor(cardWidth);
            layoutVars.x += this.padding;
        }
    },

    layoutInRows:function (rowCount, cardsToLayout) {
        var rowHeight = (this.height - ((rowCount - 1) * this.padding)) / rowCount;
        if (this.maxCardHeight != null)
            rowHeight = Math.min(this.maxCardHeight, rowHeight);
        var yBias = Math.floor((this.height - (rowHeight * rowCount) - (this.padding * (rowCount - 1))) / 2);
        var layoutVars = {};
        layoutVars.x = 0;
        var row = 0;
        var y = yBias;

        for (var cardIndex in cardsToLayout) {
            layoutVars.index = 10;
            var cardElem = cardsToLayout[cardIndex];
            var cardData = cardElem.data("card");
            var cardWidth = cardData.getWidthForMaxDimension(rowHeight);

            var attachmentWidths = this.getAttachedCardsWidth(rowHeight, cardData) * 0.2;
            var cardWidthWithAttachments = cardWidth + attachmentWidths;
            if (layoutVars.x + cardWidthWithAttachments > this.width) {
                row++;
                layoutVars.x = 0;
                y = yBias + row * (rowHeight + this.padding);
            }

            this.layoutAttached(cardData, y, rowHeight, layoutVars);
            this.layoutCard(cardElem, this.x + layoutVars.x, this.y + y, cardWidth, cardData.getHeightForWidth(cardWidth), layoutVars.index, cardData);
            layoutVars.x += Math.floor(cardWidth);
            if (layoutVars.x > this.width)
                return false;
            layoutVars.x += this.padding;
        }

        return true;
    }
});

var CardPileGroup = NormalCardGroup.extend({

    init:function (container, belongTest, createDiv, locationIndex, bottomPlayerId) {
        this._super(container, belongTest, createDiv, locationIndex, bottomPlayerId);
    },

    setBounds:function (x, y, width, height) {
        this.x = x + 6;
        this.y = y + 3;
        this.width = width - 12;
        this.height = height - 10;
        if (this.descDiv != null) {
            this.descDiv.css({left:x + "px", top:y + "px", width:width, height:height, position:"absolute"});
            this.descDiv.css({display:""});
        }
        this.layoutCards();
    }
});

var TableCardGroup = CardGroup.extend({

    /**
     * Initializes variables
     */
     init:function (container, belongTest, createDiv, locationIndex, bottomPlayerId) {
        this._super(container, belongTest, createDiv, locationIndex, bottomPlayerId);
        this.heightPadding = 1;
        this.widthPadding = 5;
        this.columnWidthToAttachedHeightAboveRatio = 0.17;
        this.cardScale = 350 / 490;
    },

   /**
    * Performs laying out the cards in the group.
    */
    layoutCards:function () {
        // Get the cards to layout
        var cardsToLayout = this.getCardElems();
        if (cardsToLayout.length == 0) {
            return;
        }

        var columnCount = 0;
        if (((this.width / this.height) / 1) > (this.maxCardWidth / this.maxCardHeight)) {
            columns = Math.floor(cardsToLayout.length / 3);
        }

        // Attempt to layout the cards in as few columns as possible, until enough columns to layout cards nicely
        var result = false;
        do {
            columnCount++;
            result = this.layoutInColumnsIfPossible(cardsToLayout, columnCount);
        } while (!result);
    },

   /**
    * Get the total height the attached cards extend above (and below) the specified card.
    * @param {Card} cardData the card
    * @param {Number} columnWidth the column width
    * @param {Number} totalHeightSoFar the combined height of the specified card and any attached cards looked at so far
    * @return {Number} the total height of the attached cards
    */
    getAttachedCardsHeight:function (cardData, columnWidth, totalHeightNotAboveSoFar) {
        var result = 0;
        for (var i = 0; i < cardData.attachedCards.length; i++) {
            var attachedCardData = cardData.attachedCards[i].data("card");
            var attachedCardHeight = attachedCardData.getHeightForColumnWidth(columnWidth);
            var attachedCardHeightAbove = Math.min(attachedCardHeight, (columnWidth * this.columnWidthToAttachedHeightAboveRatio));
            result += attachedCardHeightAbove;
            var attachedCardHeightBelow = Math.max(0, attachedCardHeight - attachedCardHeightAbove - totalHeightNotAboveSoFar);
            result += attachedCardHeightBelow;
            totalHeightNotAboveSoFar += attachedCardHeightBelow;
            result += this.getAttachedCardsHeight(attachedCardData, columnWidth, totalHeightNotAboveSoFar);
        }
        return result;
    },

   /**
    * Attempts to layout cards in the specified number of columns.
    * @param {Array} cardsToLayout the cards to layout
    * @param {Number} columnCount the number of columns in which to layout cards
    * @return {Boolean} true if layout was performed, otherwise false
    */
    layoutInColumnsIfPossible:function (cardsToLayout, columnCount) {
        // Determine column width if layout in multiple columns
        var columnWidth = this.getWidthForLayoutInColumns(cardsToLayout, columnCount);

        if (columnCount == 1) {
            // If only one card, or column width is wide enough, then just layout in one column
            if (cardsToLayout.length == 1 || (columnWidth * 2.5 > this.width)) {
                var xOffset = Math.max(this.widthPadding, Math.floor((this.width - columnWidth) / 2));
                this.layoutInColumn(cardsToLayout, columnWidth, xOffset);
                return true;
            } else {
                return false;
            }
        } else {
            // If same number of cards as columns, or cards can layout nicely, then layout cards
            if (cardsToLayout.length == columnCount || (columnWidth * columnCount * 2.5 > this.width)) {
                this.layoutInColumns(cardsToLayout, columnWidth, columnCount);
                return true;
            } else {
                return false;
            }
        }
    },

   /**
    * Determine the layout using the specified number of columns is valid.
    * @param {Array} cardsToLayout the cards to layout
    * @param {Number} columnCount the number of columns in which to layout cards
    * @return {Number} the column width, or 0 if not valid
    */
    getWidthForLayoutInColumns:function (cardsToLayout, columnCount) {
        var columnWidth = Math.min(this.maxCardWidth, (this.width - (this.widthPadding * columnCount)) / columnCount);
        var maxVerticalCardWidth = Math.min(this.maxCardWidth, columnWidth * this.cardScale);
        var numColumnsRemainingToLayout = columnCount;
        var numCardsRemainingToLayout = cardsToLayout.length;
        var largestTotalCardHeight = 0;

        var numCardsInColumn = 0;
        var totalCardHeight = this.heightPadding;
        var overlappedHeight = 0;

        for (var cardIndex in cardsToLayout) {
            totalCardHeight -= overlappedHeight;
            var cardElem = cardsToLayout[cardIndex];
            var cardData = cardElem.data("card");
            var cardHeight = cardData.getHeightForColumnWidth(maxVerticalCardWidth);
            var cardWidth = cardData.getWidthForHeight(cardHeight);
            var attachmentHeights = this.getAttachedCardsHeight(cardData, maxVerticalCardWidth, cardHeight);
            var cardHeightWithAttachments = cardHeight + attachmentHeights;
            totalCardHeight += cardHeightWithAttachments;
            overlappedHeight = (cardHeight / 2);
            numCardsInColumn++;

            var cardsToPutInColumn = Math.ceil(numCardsRemainingToLayout / numColumnsRemainingToLayout);

            if (numCardsInColumn >= cardsToPutInColumn) {
                largestTotalCardHeight = Math.max(largestTotalCardHeight, totalCardHeight);
                numColumnsRemainingToLayout--;
                numCardsRemainingToLayout -= numCardsInColumn;
                numCardsInColumn = 0;
                totalCardHeight = this.heightPadding;
                overlappedHeight = 0;
            }
        }

        if (largestTotalCardHeight > this.height) {
            return Math.min(Math.floor(columnWidth / (largestTotalCardHeight / this.height)), this.maxCardWidth);
        } else {
            return Math.min(columnWidth, this.maxCardWidth);
        }
    },

   /**
    * Layout the cards in a single column.
    * @param {Array} cardsToLayout the cards to layout
    * @param {Number} the column width
    * @param {Number} the x-offset for the column
    */
    layoutInColumn:function (cardsToLayout, columnWidth, xOffset) {
        var maxVerticalCardWidth = Math.min(this.maxCardWidth, columnWidth * this.cardScale);
        var totalCardHeight = this.heightPadding;
        var overlappedHeight = 0;

        // Determine the total height of the all the cards
        for (var cardIndex in cardsToLayout) {
            totalCardHeight -= overlappedHeight;
            var cardElem = cardsToLayout[cardIndex];
            var cardData = cardElem.data("card");
            var cardHeight = cardData.getHeightForColumnWidth(maxVerticalCardWidth);
            var cardWidth = cardData.getWidthForHeight(cardHeight);
            var attachmentHeights = this.getAttachedCardsHeight(cardData, maxVerticalCardWidth, cardHeight);
            var cardHeightWithAttachments = cardHeight + attachmentHeights;
            totalCardHeight += cardHeightWithAttachments;
            overlappedHeight = (cardHeight / 2);
        }

        // Initialize layout variables
        var layoutVars = {};
        layoutVars.index = 10;
        layoutVars.x = xOffset;
        layoutVars.y = Math.floor((this.height - (totalCardHeight)) / 2);

        // Layout the cards
        for (var cardIndex in cardsToLayout) {
            var cardElem = cardsToLayout[cardIndex];
            var cardData = cardElem.data("card");
            var cardHeight = cardData.getHeightForColumnWidth(maxVerticalCardWidth);
            var cardWidth = cardData.getWidthForHeight(cardHeight);
            var attachmentHeights = this.getAttachedCardsHeight(cardData, maxVerticalCardWidth, cardHeight);
            var cardHeightWithAttachments = cardHeight + attachmentHeights;

            // Layout the card (and attached cards)
            this.layoutAttached(cardData, maxVerticalCardWidth, layoutVars)
            this.layoutCard(cardElem, this.x + layoutVars.x, this.y + layoutVars.y, cardWidth, cardHeight, layoutVars.index, cardData);
            layoutVars.index++;

            // Update layout variables
            layoutVars.index++;
            layoutVars.x += (maxVerticalCardWidth / 10);
            layoutVars.y += (Math.floor(cardHeight) / 2);
        }
    },

   /**
    * Layout the attached cards.
    * @param {Card} cardData the card
    * @param {Number} the column width
    * @param {Object} the layout variables
    */
    layoutAttached:function (cardData, columnWidth, layoutVars) {
        for (var i = 0; i < cardData.attachedCards.length; i++) {
            var attachedCardData = cardData.attachedCards[i].data("card");
            var attachedCardHeight = attachedCardData.getHeightForColumnWidth(columnWidth);
            var attachedCardWidth = attachedCardData.getWidthForHeight(attachedCardHeight);

            // Layout cards attached to this card
            this.layoutAttached(attachedCardData, columnWidth, layoutVars);

            // Layout the card
            this.layoutCard(cardData.attachedCards[i], this.x + layoutVars.x, this.y + layoutVars.y, attachedCardWidth, attachedCardHeight, layoutVars.index, attachedCardData);

            // Update layout variables
            layoutVars.index++;
            layoutVars.y += Math.floor(columnWidth * this.columnWidthToAttachedHeightAboveRatio);
        }
    },

   /**
    * Layout the cards in a specified number of columns.
    * @param {Array} cardsToLayout the cards to layout
    * @param {Number} the column width
    * @param {Number} columnCount the number of columns in which to layout cards
    */
    layoutInColumns:function (cardsToLayout, columnWidth, columnCount) {
        var numCardsRemainingToLayout = cardsToLayout.length;
        var numColumnsRemainingToLayout = columnCount;

        var numCardsInColumn = 0;
        var cardsToLayoutInColumn = new Array();
        var maxCardsPerColumn = Math.ceil(numCardsRemainingToLayout / numColumnsRemainingToLayout);
        var xOffset = Math.max(this.widthPadding, Math.floor((this.width - (columnWidth * columnCount) - (this.widthPadding * columnCount)) / 2));

        for (var cardIndex in cardsToLayout) {
            var cardElem = cardsToLayout[cardIndex];
            cardsToLayoutInColumn.push(cardElem);
            numCardsInColumn++;
            var cardsToPutInColumn = Math.ceil(numCardsRemainingToLayout / numColumnsRemainingToLayout);

            if (numCardsInColumn >= cardsToPutInColumn) {
                // Layout the cards in a column
                this.layoutInColumn(cardsToLayoutInColumn, columnWidth, xOffset);
                xOffset += (columnWidth + this.widthPadding)
                numColumnsRemainingToLayout--;
                numCardsRemainingToLayout -= numCardsInColumn;
                numCardsInColumn = 0;
                cardsToLayoutInColumn = new Array();
            }
        }

        if (cardsToLayoutInColumn.length > 0) {
            xOffset += (columnWidth + this.widthPadding)
            this.layoutInColumn(cardsToLayoutInColumn, columnWidth, xOffset);
        }
    }
});

function layoutCardElem(cardElem, x, y, width, height, index) {
    var cardData = cardElem.data("card");
    x = Math.floor(x);
    y = Math.floor(y);
    width = Math.floor(width);
    height = Math.floor(height);

    if (cardData != null && cardData.frozen)
        $(".frozenOverlay", cardElem).css({position:"absolute", left:0 + "px", top:0 + "px", width:width, height:height});
    else
        $(".frozenOverlay", cardElem).css({position:"absolute", left:0 + "px", top:0 + "px", width:0, height:0});

    if (cardData != null && cardData.suspended)
        $(".suspendedOverlay", cardElem).css({position:"absolute", left:0 + "px", top:0 + "px", width:width, height:height});
    else
        $(".suspendedOverlay", cardElem).css({position:"absolute", left:0 + "px", top:0 + "px", width:0, height:0});

    if (cardData != null && cardData.collapsed)
        $(".collapsedOverlay", cardElem).css({position:"absolute", left:0 + "px", top:0 + "px", width:width, height:height});
    else
        $(".collapsedOverlay", cardElem).css({position:"absolute", left:0 + "px", top:0 + "px", width:0, height:0});

    if (cardData != null && cardData.testingText != null)
        $(".testingTextOverlay", cardElem).css({position:"absolute", left:(width * 0.1) + "px", top:(height * 0.3) + "px", width:(width * 0.8), height:(height * 0.4)});
    else
        $(".testingTextOverlay", cardElem).css({position:"absolute", left:0 + "px", top:0 + "px", width:0, height:0});

    if (cardElem.css("left") == (x + "px") && cardElem.css("top") == (y + "px")
        && cardElem.css("width") == (width + "px") && cardElem.css("height") == (height + "px")
        && cardElem.css("zIndex") == index)
        return;

    cardElem.css({position:"absolute", left:x + "px", top:y + "px", width:width, height:height, zIndex:index });

    $(".incompleteOverlay", cardElem).css({position:"absolute", left:0 + "px", top:0 + "px", width:width, height:height});
    $(".foilOverlay", cardElem).css({position:"absolute", left:0 + "px", top:0 + "px", width:width, height:height});

    var maxDimension = Math.max(width, height);
    var borderWidth = Math.floor(maxDimension / 30);

    var borderOverlay = $(".borderOverlay", cardElem);
    if (borderOverlay.hasClass("noBorder"))
        borderWidth = 0;
    borderOverlay.css({position:"absolute", left:0 + "px", top:0 + "px", width:width - 2 * borderWidth, height:height - 2 * borderWidth, "border-width":borderWidth + "px"});

    var sizeListeners = cardElem.data("sizeListeners");
    if (sizeListeners != null)
        for (var i = 0; i < sizeListeners.length; i++)
            sizeListeners[i].sizeChanged(cardElem, width, height);
}

function layoutTokens(cardElem) {
    var tokenOverlay = $(".tokenOverlay", cardElem);

    if (tokenOverlay.length > 0) {
        var width = cardElem.width();
        var height = cardElem.height();
        var maxDimension = Math.max(width, height);

        var tokenSize = Math.floor(maxDimension / 13) * 2;

        // Remove all existing tokens
        $(".token", tokenOverlay).remove();

        var tokens = cardElem.data("card").tokens;
        if (tokens != null) {
            var tokenInColumnMax = 10;
            var tokenColumns = 0;

            for (var token in tokens)
                if (tokens.hasOwnProperty(token) && tokens[token] > 0) {
                    tokenColumns += (1 + Math.floor((tokens[token] - 1) / tokenInColumnMax));
                }

            var tokenIndex = 1;
            for (var token in tokens)
                if (tokens.hasOwnProperty(token)) {
                    if (token == "count") {
                        var tokenElem = $("<div class='cardCount token'>" + tokens[token] + "</div>").css({position:"absolute", left:((width - 20) / 2) + "px", top:((height - 18) / 2) + "px"});
                        tokenOverlay.append(tokenElem);
                    }
                }
        }
    }
}

