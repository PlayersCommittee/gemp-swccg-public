var GempSwccgDeckBuildingUI = Class.extend({
    comm:null,

    deckDiv:null,

    manageDecksDiv:null,

    collectionDiv:null,

    normalCollectionDiv:null,
    normalCollectionGroup:null,

    selectionFunc:null,
    drawDeckLabelDiv:null,
    drawDeckDiv:null,
    outsideDeckLabelDiv:null,
    outsideDeckDiv:null,

    deckGroup:null,
    outsideDeckGroup:null,

    start:0,
    count:18,
    filter:null,

    deckName:null,
    sampleDeck:false,

    filterDirty:false,
    deckDirty:false,

    checkDirtyInterval:1000,

    deckListDialog:null,
    libraryDeckListDialog:null,
    selectionDialog:null,
    selectionGroup:null,
    packSelectionId:null,

    cardFilter:null,

    collectionType:null,

    init:function () {
        var that = this;

        this.comm = new GempSwccgCommunication("/gemp-swccg-server", that.processError);

        this.collectionType = "default";

        this.cardFilter = new CardFilter($("#collectionDiv"), $("#collectionDiv"),
                function (filter, start, count, callback) {
                    that.comm.getCollection(that.collectionType, filter, start, count, function (xml) {
                        callback(xml);
                    }, {
                        "404":function () {
                            alert("You don't have collection of that type.");
                        }
                    });
                },
                function () {
                    that.clearCollection();
                },
                function (elem, type, blueprintId, testingText, backSideTestingText, count) {
                    that.addCardToCollection(type, blueprintId, testingText, backSideTestingText, count, elem.getAttribute("side"), elem.getAttribute("contents"));
                },
                function () {
                    that.finishCollection();
                });

        this.deckDiv = $("#deckDiv");

        this.manageDecksDiv = $("<div id='manageDecks'></div>");

        var collectionSelect = $("<select id='collectionSelect'></select>");
        collectionSelect.css({"float":"right", width:"120px"});
        collectionSelect.append("<option value='default'>All cards</option>");
        collectionSelect.append("<option value='permanent'>My cards</option>");
        this.manageDecksDiv.append(collectionSelect);

        var newDeckBut = $("<button title='New deck'><span class='ui-icon ui-icon-document'></span></button>").button();
        this.manageDecksDiv.append(newDeckBut);

        var saveDeckBut = $("<button title='Save deck'><span class='ui-icon ui-icon-disk'></span></button>").button();
        this.manageDecksDiv.append(saveDeckBut);

        var renameDeckBut = $("<button title='Rename deck'><span class='ui-icon ui-icon-tag'></span></button>").button();
        this.manageDecksDiv.append(renameDeckBut);

        var copyDeckBut = $("<button title='Copy deck to new'><span class='ui-icon ui-icon-copy'></span></button>").button();
        this.manageDecksDiv.append(copyDeckBut);

        var deckListBut = $("<button title='Deck list'><span class='ui-icon ui-icon-suitcase'></span></button>").button();
        this.manageDecksDiv.append(deckListBut);

        var deckLibraryBut = $("<button title='Sample decks'><span class='ui-icon ui-icon-cart'></span></button>").button();
        this.manageDecksDiv.append(deckLibraryBut);

        var importDeckBut = $("<button title='Import Deck'><span class='ui-icon ui-icon-script'></span></button>").button();
        this.manageDecksDiv.append(importDeckBut);

        // Hidden file-input field for browsing for decks on the user's computer
        var browseInputDeckInput = $("<input type=file id='browseInputDeckInput' style='display:none'>");
        this.manageDecksDiv.append(browseInputDeckInput);

        this.manageDecksDiv.append("<span id='editingDeck'>New deck</span>");

        this.deckDiv.append(this.manageDecksDiv);

        newDeckBut.click(
                function () {
                    that.deckName = null;
                    that.sampleDeck = false;
                    $("#editingDeck").text("New deck");
                    that.clearDeck();
                });

        saveDeckBut.click(
                function () {
                    if (that.deckName == null || that.sampleDeck) {
                        var newDeckName = prompt("Enter the name of the deck", "");
                        if (newDeckName == null)
                            return;
                        if (newDeckName.length < 3 || newDeckName.length > 40)
                            alert("Deck name has to have at least 3 characters and at most 40 characters.");
                        else {
                            that.deckName = newDeckName;
                            that.sampleDeck = false;
                            $("#editingDeck").text(newDeckName);
                            that.saveDeck(true);
                        }
                    } else {
                        that.saveDeck(false);
                    }
                });

        renameDeckBut.click(
                function () {
                    if (that.deckName == null) {
                        alert("You can't rename this deck, since it's not named (saved) yet.");
                        return;
                    }
                    var newDeckName = prompt("Enter new name for the deck", "");
                    if (newDeckName == null)
                        return;
                    if (newDeckName.length < 3 || newDeckName.length > 40) {
                        alert("Deck name has to have at least 3 characters and at most 40 characters.");
                    }
                    else if (that.sampleDeck) {
                        that.deckName = newDeckName;
                        that.sampleDeck = false;
                        $("#editingDeck").text(newDeckName);
                        that.saveDeck(true);
                    }
                    else {
                        var oldDeckName = that.deckName;
                        that.deckName = newDeckName;
                        $("#editingDeck").text(newDeckName);
                        that.comm.renameDeck(oldDeckName, newDeckName,
                                function () {
                                    if (confirm("Do you wish to save this deck?"))
                                        that.saveDeck(false);
                                }, {
                            "404":function () {
                                alert("Couldn't find the deck to rename on the server.");
                            }
                        });
                    }
                });

        copyDeckBut.click(
                function () {
                    that.deckName = null;
                    that.sampleDeck = false;
                    $("#editingDeck").text("New deck");
                });

        deckListBut.click(
                function () {
                    that.loadDeckList();
                });

        deckLibraryBut.click(
                function () {
                    that.loadLibraryDeckList();
                });

        importDeckBut.click(
                function () {
                    that.importDeck();
                });


        this.collectionDiv = $("#collectionDiv");

        $("#collectionSelect").change(
                function () {
                    that.collectionType = that.getCollectionType();
                    that.cardFilter.getCollection();
                });

        this.normalCollectionDiv = $("<div></div>");
        this.normalCollectionGroup = new NormalCardGroup(this.normalCollectionDiv, function (card) {
            return true;
        });
        this.normalCollectionGroup.maxCardHeight = 200;
        this.collectionDiv.append(this.normalCollectionDiv);

        this.drawDeckLabelDiv = $("<div id='deckZoneLabel'>Cards in deck</div>");
        this.drawDeckDiv = $("<div></div>");
        this.drawDeckDiv.click(
                function () {
                    that.selectionFunc = that.addCardToDeckAndLayout;
                });
        this.deckGroup = new NormalCardGroup(this.drawDeckDiv, function (card) {
            return (card.zone == "deck");
        });
        this.deckGroup.maxCardHeight = 250;

        this.outsideDeckLabelDiv = $("<div id='outsideDeckZoneLabel'> Cards outside of deck (e.g. Defensive Shields, 'Hidden' Base, etc.)</div>");
        this.outsideDeckDiv = $("<div></div>");
        this.outsideDeckDiv.click(
                function () {
                    that.selectionFunc = that.addCardToOutsideDeckAndLayout;
                });
        this.outsideDeckGroup = new NormalCardGroup(this.outsideDeckDiv, function (card) {
            return (card.zone == "outsideDeck");
        });
        this.outsideDeckGroup.maxCardHeight = 100;

        this.bottomBarDiv = $("<div></div>");
        this.bottomBarDiv.css({overflow:"auto"});
        this.bottomBarDiv.append("<div id='deckStats'></div>");
        this.deckDiv.append(this.bottomBarDiv);

        this.deckDiv.append(this.drawDeckLabelDiv);
        this.deckDiv.append(this.drawDeckDiv);
        this.deckDiv.append(this.outsideDeckLabelDiv);
        this.deckDiv.append(this.outsideDeckDiv);

        this.selectionFunc = this.addCardToDeckAndLayout;

        $("body").click(
                function (event) {
                    return that.clickCardFunction(event);
                });
        $("body").mousedown(
                function (event) {
                    return that.dragStartCardFunction(event);
                });
        $("body").mouseup(
                function (event) {
                    return that.dragStopCardFunction(event);
                });

        var width = $(window).width();
        var height = $(window).height();

        this.infoDialog = $("<div></div>")
                .dialog({
            autoOpen:false,
            closeOnEscape:true,
            resizable:false,
            title:"Card information"
        });

        var swipeOptions = {
            threshold:20,
            swipeUp:function (event) {
                that.infoDialog.prop({ scrollTop:that.infoDialog.prop("scrollHeight") });
                return false;
            },
            swipeDown:function (event) {
                that.infoDialog.prop({ scrollTop:0 });
                return false;
            }
        };
        this.infoDialog.swipe(swipeOptions);

        this.getCollectionTypes();

        this.cardFilter.getCollection();

        this.checkDeckStatsDirty();
    },

    getCollectionType:function () {
        return $("#collectionSelect option:selected").prop("value");
    },

    getCollectionTypes:function () {
        var that = this;
        this.comm.getCollectionTypes(
                function (xml) {
                    var root = xml.documentElement;
                    if (root.tagName == "collections") {
                        var collections = root.getElementsByTagName("collection");
                        for (var i = 0; i < collections.length; i++) {
                            var collection = collections[i];
                            $("#collectionSelect").append("<option value='" + collection.getAttribute("type") + "'>" + collection.getAttribute("name") + "</option>");
                        }
                    }
                });
    },

    loadDeckList:function () {
        var that = this;
        this.comm.getDecks(function (xml) {
            if (that.deckListDialog == null) {
                that.deckListDialog = $("<div></div>")
                        .dialog({
                    title:"Your decks",
                    autoOpen:false,
                    closeOnEscape:true,
                    resizable:true,
                    width:400,
                    height:400,
                    modal:true
                });
            }
            that.deckListDialog.html("");

            var root = xml.documentElement;
            if (root.tagName == "decks") {
                var darkDecks = root.getElementsByTagName("darkDeck");
                that.generateDeckRow(darkDecks, "[DARK] ", false);
                var lightDecks = root.getElementsByTagName("lightDeck");
                that.generateDeckRow(lightDecks, "[LIGHT] ", false);
                var otherDecks = root.getElementsByTagName("otherDeck");
                that.generateDeckRow(otherDecks, "[UNKNOWN] ", false);
            }

            that.deckListDialog.dialog("open");
        });
    },

    loadLibraryDeckList:function () {
        var that = this;
        this.comm.getLibraryDecks(function (xml) {
            if (that.libraryDeckListDialog == null) {
                that.libraryDeckListDialog = $("<div></div>")
                        .dialog({
                    title:"Sample decks",
                    autoOpen:false,
                    closeOnEscape:true,
                    resizable:true,
                    width:400,
                    height:400,
                    modal:true
                });
            }
            that.libraryDeckListDialog.html("");

            var root = xml.documentElement;
            if (root.tagName == "decks") {
                var darkDecks = root.getElementsByTagName("darkDeck");
                that.generateDeckRow(darkDecks, "[DARK] ", true);
                var lightDecks = root.getElementsByTagName("lightDeck");
                that.generateDeckRow(lightDecks, "[LIGHT] ", true);
                var otherDecks = root.getElementsByTagName("otherDeck");
                that.generateDeckRow(otherDecks, "[UNKNOWN] ", true);
            }

            that.libraryDeckListDialog.dialog("open");
        });
    },

    generateDeckRow:function (decks, prefix, sampleDeck) {
        var that = this;
        for (var i = 0; i < decks.length; i++) {
            var deck = decks[i];
            var deckName = decks[i].childNodes[0].nodeValue;
            var openDeckBut = $("<button title='Open deck'><span class='ui-icon ui-icon-folder-open'></span></button>").button();
            var deckListBut = $("<button title='Deck list'><span class='ui-icon ui-icon-clipboard'></span></button>").button();
            var deleteDeckBut = $("<button title='Delete deck'><span class='ui-icon ui-icon-trash'></span></button>").button();
            var exportDeckBut = $("<button title='Export deck'><span class='ui-icon ui-icon-arrowthickstop-1-s'></span></button>").button();
            //var importDeckBut = $("<button title='Import deck'><span class='ui-icon ui-icon-script'></span></button>").button();


            var deckElem = $("<div class='deckItem'></div>");
            deckElem.append(openDeckBut);
            deckElem.append(deckListBut);
            deckElem.append(exportDeckBut);
            //deckElem.append(importDeckBut);
            if (!sampleDeck) {
                deckElem.append(deleteDeckBut);
            }
            deckElem.append($("<div/>").text(prefix + deckName).html());

            if (sampleDeck) {
                that.libraryDeckListDialog.append(deckElem);
            }
            else {
                that.deckListDialog.append(deckElem);
            }

            openDeckBut.click(
                    (function (deckName) {
                        return function () {
                            if (sampleDeck) {
                                that.comm.getLibraryDeck(deckName,
                                        function (xml) {
                                            that.setupDeck(xml, deckName, sampleDeck);
                                        });
                            }
                            else {
                                that.comm.getDeck(deckName,
                                        function (xml) {
                                            that.setupDeck(xml, deckName, sampleDeck);
                                        });
                            }
                        };
                    })(deckName));

            exportDeckBut.click(
                                (function (deckName) {
                                    return function () {
                                        if (sampleDeck) {
                                            that.comm.getLibraryDeck(deckName,
                                                    function (xml) {
                                                        that.exportDeck(deckName, xml);
                                                    });
                                        }
                                        else {
                                            that.comm.getDeck(deckName,
                                                    function (xml) {
                                                        that.exportDeck(deckName, xml);
                                                    });
                                        }
                                    };
                                })(deckName));

            deckListBut.click(
                    (function (deckName) {
                           if (sampleDeck) {
                                return function () {
                                    window.open('/gemp-swccg-server/deck/libraryHtml?deckName=' + encodeURIComponent(deckName), "_blank");
                                    }
                            }
                            else {
                                return function () {
                                    window.open('/gemp-swccg-server/deck/html?deckName=' + encodeURIComponent(deckName), "_blank");
                                    }
                            }
                    })(deckName));

            if (!sampleDeck) {
                deleteDeckBut.click(
                        (function (deckName) {
                            return function () {
                                if (confirm("Are you sure you want to delete this deck?")) {
                                    that.comm.deleteDeck(deckName,
                                            function () {
                                                if (that.deckName == deckName) {
                                                    that.deckName = null;
                                                    $("#editingDeck").text("New deck");
                                                    that.clearDeck();
                                                }

                                                that.loadDeckList();
                                            });
                                }
                            };
                        })(deckName));
            }
        }
    },

    clickCardFunction:function (event) {
        var that = this;

        var tar = $(event.target);
        if (tar.length == 1 && tar[0].tagName == "A")
            return true;

        if (!this.successfulDrag && this.infoDialog.dialog("isOpen")) {
            this.infoDialog.dialog("close");
            event.stopPropagation();
            return false;
        }

        if (tar.hasClass("actionArea")) {
            var selectedCardElem = tar.closest(".card");
            if (event.which == 1) {
                if (!this.successfulDrag) {
                    if (event.shiftKey) {
                        this.displayCardInfo(selectedCardElem.data("card"));
                    } else if (selectedCardElem.hasClass("cardInCollection")) {
                        var cardData = selectedCardElem.data("card");
                        this.selectionFunc(cardData.blueprintId, cardData.testingText);
                        cardData.tokens = {count:(parseInt(cardData.tokens["count"]) - 1)};
                        layoutTokens(selectedCardElem);
                    } else if (selectedCardElem.hasClass("packInCollection")) {
                        if (confirm("Would you like to open this pack/box?")) {
                            this.comm.openPack(this.getCollectionType(), selectedCardElem.data("card").blueprintId, function () {
                                that.cardFilter.getCollection();
                            }, {
                                "404":function () {
                                    alert("You have no pack of this type in your collection.");
                                }
                            });
                        }
                    } else if (selectedCardElem.hasClass("cardToSelect")) {
                        this.comm.openSelectionPack(this.getCollectionType(), this.packSelectionId, selectedCardElem.data("card").blueprintId, function () {
                            that.cardFilter.getCollection();
                        }, {
                            "404":function () {
                                alert("You have no pack/box of this type in your collection or that selection is not available for this pack/box.");
                            }
                        });
                        this.selectionDialog.dialog("close");
                    } else if (selectedCardElem.hasClass("selectionInCollection")) {
                        var selectionDialogResize = function () {
                            var width = that.selectionDialog.width() + 10;
                            var height = that.selectionDialog.height() + 10;
                            that.selectionGroup.setBounds(2, 2, width - 2 * 2, height - 2 * 2);
                        };

                        if (this.selectionDialog == null) {
                            this.selectionDialog = $("<div></div>")
                                    .dialog({
                                title:"Choose one",
                                autoOpen:false,
                                closeOnEscape:true,
                                resizable:true,
                                width:400,
                                height:200,
                                modal:true
                            });

                            this.selectionGroup = new NormalCardGroup(this.selectionDialog, function (card) {
                                return true;
                            }, false);

                            this.selectionDialog.bind("dialogresize", selectionDialogResize);
                        }
                        this.selectionDialog.html("");
                        var cardData = selectedCardElem.data("card");
                        this.packSelectionId = cardData.blueprintId;
                        var selection = selectedCardElem.data("selection");
                        var blueprintIds = selection.split("|");
                        for (var i = 0; i < blueprintIds.length; i++) {
                            var card = new Card(blueprintIds[i], cardData.testingText, cardData.backSideTestingText, "selection", "selection" + i, "player");
                            var cardDiv = createCardDiv(card.imageUrl, card.testingText, null, card.isFoil(), false, card.isPack(), card.incomplete);
                            cardDiv.data("card", card);
                            cardDiv.addClass("cardToSelect");
                            this.selectionDialog.append(cardDiv);
                        }
                        openSizeDialog(that.selectionDialog);
                        selectionDialogResize();
                    } else if (selectedCardElem.hasClass("cardInDeck") || selectedCardElem.hasClass("cardOutsideDeck")) {
                        this.removeCardFromDeckOrOutsideDeck(selectedCardElem);
                    }
                    event.stopPropagation();
                }
            }
            return false;
        }
        return true;
    },

    dragCardData:null,
    dragStartX:null,
    dragStartY:null,
    successfulDrag:null,

    dragStartCardFunction:function (event) {
        this.successfulDrag = false;
        var tar = $(event.target);
        if (tar.hasClass("actionArea")) {
            var selectedCardElem = tar.closest(".card");
            if (event.which == 1) {
                this.dragCardData = selectedCardElem.data("card");
                this.dragStartX = event.clientX;
                this.dragStartY = event.clientY;
                return false;
            }
        }
        return true;
    },

    dragStopCardFunction:function (event) {
        if (this.dragCardData != null) {
            if (this.dragStartY - event.clientY >= 20) {
                this.displayCardInfo(this.dragCardData);
                this.successfulDrag = true;
            }
            this.dragCardData = null;
            this.dragStartX = null;
            this.dragStartY = null;
            return false;
        }
        return true;
    },

    displayCardInfo:function (card) {
        var that = this;
        this.infoDialog.html("");
        this.infoDialog.html("<div style='scroll: auto'></div>");
        var floatCardDiv = $("<div style='float: left;'></div>");
        var cardDiv = createFullCardDiv(card.imageUrl, card.testingText, card.foil, card.horizontal, card.isPack());

        // Check if card div needs to be inverted
        this.infoDialog.cardImageRotation = 0;
        this.infoDialog.cardImageFlipped = false;
        $(cardDiv).click(
                function(event) {
                    // Check if need to show other card image if the image has two sides
                    if (card.backSideImageUrl != null && !card.backSideImageUrl.includes("CardBack")) {
                        that.infoDialog.cardImageFlipped = !that.infoDialog.cardImageFlipped;
                        if (that.infoDialog.cardImageFlipped) {
                            $(cardDiv).find("div.fullcard img").attr('src', card.backSideImageUrl);
                            if (card.backSideTestingText != null) {
                                $(cardDiv).find("div.testingTextOverlay").html(card.backSideTestingText.replace(/\|/g, "<br/>"));
                                $(cardDiv).find("div.testingTextOverlay").attr('display', "block");
                            }
                            else {
                                $(cardDiv).find("div.testingTextOverlay").attr('display', "none");
                            }
                        }
                        else {
                            $(cardDiv).find("div.fullcard img").attr('src', card.imageUrl);
                            if (card.testingText != null) {
                                $(cardDiv).find("div.testingTextOverlay").html(card.testingText.replace(/\|/g, "<br/>"));
                                $(cardDiv).find("div.testingTextOverlay").attr('display', "block");
                            }
                            else {
                                $(cardDiv).find("div.testingTextOverlay").attr('display', "none");
                            }
                        }
                    }
                    // Otherwise rotate the image
                    else {
                        that.infoDialog.cardImageRotation = (that.infoDialog.cardImageRotation + 180) % 360;
                        $(cardDiv).rotate(that.infoDialog.cardImageRotation);
                    }
                    event.stopPropagation();
                });
        floatCardDiv.append(cardDiv);

        this.infoDialog.append(floatCardDiv);

        var windowWidth = $(window).width();
        var windowHeight = $(window).height();

        var horSpace = 30;
        var vertSpace = 45;

        if (card.horizontal) {
            // 500x360
            this.infoDialog.dialog({width:Math.min(500 + horSpace, windowWidth), height:Math.min(380 + vertSpace, windowHeight)});
        } else {
            // 360x500
            this.infoDialog.dialog({width:Math.min(360 + horSpace, windowWidth), height:Math.min(520 + vertSpace, windowHeight)});
        }
        this.infoDialog.dialog("open");
    },

    getDeckContents:function () {
        var result = "";
        var cards = new Array();
        $(".card", this.drawDeckDiv).each(
                function () {
                    cards.push($(this).data("card").blueprintId);
                });
        result += cards;
        result += "|";

        var outsideDeckCards = new Array();
        $(".card", this.outsideDeckDiv).each(
                function () {
                    outsideDeckCards.push($(this).data("card").blueprintId);
                });
        result += outsideDeckCards;

        return result;
    },

    saveDeck:function (reloadList) {
        var that = this;

        var deckContents = this.getDeckContents();
        if (deckContents == null)
            alert("Deck must contain 60 cards");
        else
            this.comm.saveDeck(this.deckName, deckContents, function (xml) {
                that.deckModified(false);
                alert("Deck was saved");
            }, {
                "400":function () {
                    alert("Invalid deck format.");
                }
            });
    },

    addCardToContainer:function (blueprintId, testingText, backSideTestingText, zone, container, tokens) {
        var card = new Card(blueprintId, testingText, backSideTestingText, zone, "deck", "player");
        var cardDiv = createCardDiv(card.imageUrl, card.testingText, null, card.isFoil(), tokens, card.isPack(), card.incomplete);
        cardDiv.data("card", card);
        container.append(cardDiv);
        return cardDiv;
    },

    addCardToDeckAndLayout:function (blueprintId, testingText, backSideTestingText) {
        var that = this;

        this.addCardToDeck(blueprintId, testingText, backSideTestingText);
        that.deckGroup.layoutCards();

        that.deckModified(true);
    },

    addCardToOutsideDeckAndLayout:function (blueprintId, testingText, backSideTestingText) {
        var that = this;

        var cardDiv = this.addCardToContainer(blueprintId, testingText, backSideTestingText, "outsideDeck", that.outsideDeckDiv, false);
        cardDiv.addClass("cardOutsideDeck");
        that.outsideDeckGroup.layoutCards()
        that.deckDirty = true;
        that.deckModified(true);
    },

    deckModified:function (value) {
        var name = (this.deckName == null) ? "New deck" : this.deckName;
        if (this.sampleDeck) {
            name = "[Sample Deck] " + name;
        }

        if (value)
            $("#editingDeck").text(name + " - modified");
        else
            $("#editingDeck").text(name);
    },

    addCardToDeck:function (blueprintId, testingText, backSideTestingText) {
        var that = this;
        var added = false;
        $(".card.cardInDeck", this.drawDeckDiv).each(
                function () {
                    var cardData = $(this).data("card");
                    if (cardData.blueprintId == blueprintId) {
                        var attDiv = that.addCardToContainer(blueprintId, testingText, backSideTestingText, "attached", that.drawDeckDiv, false);
                        cardData.attachedCards.push(attDiv);
                        added = true;
                    }
                });
        if (!added) {
            var div = this.addCardToContainer(blueprintId, testingText, backSideTestingText, "deck", this.drawDeckDiv, false)
            div.addClass("cardInDeck");
        }

        this.deckDirty = true;
    },

    checkDeckStatsDirty:function () {
        if (this.deckDirty) {
            this.deckDirty = false;
            this.updateDeckStats();
        } else {
            var that = this;
            setTimeout(
                    function () {
                        that.checkDeckStatsDirty();
                    }, that.checkDirtyInterval);
        }
    },

    updateDeckStats:function () {
        var that = this;
        var deckContents = this.getDeckContents();
        if (deckContents != null) {
            this.comm.getDeckStats(deckContents,
                    function (html) {
                        $("#deckStats").html(html);
                        setTimeout(
                                function () {
                                    that.checkDeckStatsDirty();
                                }, that.checkDirtyInterval);
                    }, {
                "400":function () {
                    alert("Invalid deck for getting stats.");
                }
            });
        } else {
            $("#deckStats").html("Deck has no cards");
            setTimeout(
                    function () {
                        that.checkDeckStatsDirty();
                    }, that.checkDirtyInterval);
        }
    },

    removeCardFromDeckOrOutsideDeck:function (cardDiv) {
        var cardData = cardDiv.data("card");
        if (cardData.attachedCards.length > 0) {
            cardData.attachedCards[0].remove();
            cardData.attachedCards.splice(0, 1);
        } else {
            cardDiv.remove();
        }
        var cardInCollectionElem = null;
        $(".card", this.normalCollectionDiv).each(
                function () {
                    var tempCardData = $(this).data("card");
                    if (tempCardData.blueprintId == cardData.blueprintId)
                        cardInCollectionElem = $(this);
                });
        if (cardInCollectionElem != null) {
            var cardInCollectionData = cardInCollectionElem.data("card");
            cardInCollectionData.tokens = {count:(parseInt(cardInCollectionData.tokens["count"]) + 1)};
            layoutTokens(cardInCollectionElem);
        }

        this.layoutDeck();
        this.deckDirty = true;
        this.deckModified(true);
    },

    clearDeck:function () {
        $(".cardInDeck").each(
                function () {
                    var cardData = $(this).data("card");
                    for (var i = 0; i < cardData.attachedCards.length; i++)
                        cardData.attachedCards[i].remove();
                });
        $(".cardInDeck").remove();
        $(".cardOutsideDeck").remove();

        this.layoutUI(false);

        this.deckDirty = true;
    },

    setupDeck:function (xml, deckName, sampleDeck) {
        var root = xml.documentElement;
        if (root.tagName == "deck") {
            this.clearDeck();
            this.deckName = deckName;
            this.sampleDeck = sampleDeck;
            if (sampleDeck) {
                $("#editingDeck").text("[Sample Deck] " + deckName);
            }
            else {
                $("#editingDeck").text(deckName);
            }
            var cards = root.getElementsByTagName("card");
            for (var i = 0; i < cards.length; i++)
                this.addCardToDeck(cards[i].getAttribute("blueprintId"), cards[i].getAttribute("testingText"), cards[i].getAttribute("backSideTestingText"));

            var cardsOutsideDeck = root.getElementsByTagName("cardOutsideDeck");
            for (var i = 0; i < cardsOutsideDeck.length; i++)
                this.addCardToContainer(cardsOutsideDeck[i].getAttribute("blueprintId"), cardsOutsideDeck[i].getAttribute("testingText"), cardsOutsideDeck[i].getAttribute("backSideTestingText"), "outsideDeck", this.outsideDeckDiv, false).addClass("cardOutsideDeck");

            this.layoutUI(false);

            this.cardFilter.getCollection();
        }
        this.deckModified(false);
    },

    clearCollection:function () {
        $(".card", this.normalCollectionDiv).remove();
    },

    addCardToCollection:function (type, blueprintId, testingText, backSideTestingText, count, side, contents) {
        if (type == "pack") {
            if (blueprintId.substr(0, 3) == "(S)") {
                var card = new Card(blueprintId, null, null, "pack", "collection", "player");
                card.tokens = {"count":count};
                var cardDiv = createCardDiv(card.imageUrl, card.testingText, null, false, true, true, false, card.incomplete);
                cardDiv.data("card", card);
                cardDiv.data("selection", contents);
                cardDiv.addClass("selectionInCollection");
            } else {
                var card = new Card(blueprintId, null, null, "pack", "collection", "player");
                card.tokens = {"count":count};
                var cardDiv = createCardDiv(card.imageUrl, card.testingText, null, false, true, true, false, card.incomplete);
                cardDiv.data("card", card);
                cardDiv.addClass("packInCollection");
            }
            this.normalCollectionDiv.append(cardDiv);
        } else if (type == "card") {
            var card = new Card(blueprintId, testingText, backSideTestingText, "card", "collection", "player");
            var countInDeck = 0;
            $(".card", this.deckDiv).each(
                    function () {
                        var tempCardData = $(this).data("card");
                        if (blueprintId == tempCardData.blueprintId)
                            countInDeck++;
                    });
            card.tokens = {"count":count - countInDeck};
            var cardDiv = createCardDiv(card.imageUrl, card.testingText, null, card.isFoil(), true, false, card.incomplete);
            cardDiv.data("card", card);
            cardDiv.addClass("cardInCollection");
            this.normalCollectionDiv.append(cardDiv);
        }
    },

    finishCollection:function () {
        this.normalCollectionGroup.layoutCards();
    },

    layoutUI:function (layoutDivs) {
        if (layoutDivs) {
            var manageHeight = 23;

            var padding = 5;
            var collectionWidth = this.collectionDiv.width();
            var collectionHeight = this.collectionDiv.height();

            var deckWidth = this.deckDiv.width();
            var deckHeight = this.deckDiv.height() - (manageHeight + padding);

            var rowHeight = Math.floor((deckHeight - 6 * padding) / 10);

            this.manageDecksDiv.css({position:"absolute", left:padding, top:padding, width:deckWidth, height:manageHeight});

            this.drawDeckLabelDiv.css({ position:"absolute", left:padding * 2 + 10, top:manageHeight + 2 * padding + 10, width:deckWidth - 2 * padding - 10, height:10 });
            this.drawDeckDiv.css({ position:"absolute", left:padding * 2, top:manageHeight + 2 * padding, width:deckWidth - 2 * padding, height:(5 * (deckHeight - 2 * padding - 50) / 6) - padding });
            this.deckGroup.setBounds(0, 0, deckWidth - padding, (5 * (deckHeight - 2 * padding - 50) / 6) - padding);

            this.outsideDeckLabelDiv.css({ position:"absolute", left:padding * 2 + 10, top:manageHeight + 2 * padding + (5 * (deckHeight - 2 * padding - 50) / 6) + padding + 10, width:deckWidth - 2 * padding - 10, height:10 });
            this.outsideDeckDiv.css({ position:"absolute", left:padding * 2, top:manageHeight + 2 * padding + (5 * (deckHeight - 2 * padding - 50) / 6) + padding, width:deckWidth - 2 * padding, height:((deckHeight - 2 * padding - 50) / 6) - padding });
            this.outsideDeckGroup.setBounds(0, 0, deckWidth - padding, ((deckHeight - 2 * padding - 50) / 6) - padding);

            this.bottomBarDiv.css({ position:"absolute", left:padding * 2, top:manageHeight + padding + deckHeight - 50, width:deckWidth - 2 * padding, height:50 });

            this.cardFilter.layoutUi(padding, 0, collectionWidth - padding, 335);
            this.normalCollectionDiv.css({ position:"absolute", left:padding, top:375, width:collectionWidth - padding, height:collectionHeight - 375 });

            this.normalCollectionGroup.setBounds(padding, 0, collectionWidth, collectionHeight - 375);
        } else {
            this.layoutDeck();
            this.normalCollectionGroup.layoutCards();
        }
    },

    layoutDeck:function () {
        this.outsideDeckGroup.layoutCards();
        this.deckGroup.layoutCards();
    },

    processError:function (xhr, ajaxOptions, thrownError) {
        if (thrownError != "abort")
            alert("There was a problem during communication with server");
    },

    importDeck:function() {
        var that = this;

        // Register for change events on our hidden file-input <input type=file> element
        // We use this hidden element to open the file-picker
        $('#browseInputDeckInput').change('change', onInputChangeHandler);

        function unbindFileHandler() {
            $('#browseInputDeckInput').unbind('change', onInputChangeHandler);
        }

        function saveImportedNewDeck(xml, deckName) {
            var sampleDeck = false;

            // Load the deck locally, just as if we had gotten it from the server
            var xmlDocument = jQuery.parseXML(xml);
            that.setupDeck(xmlDocument, deckName, sampleDeck);

            // Give the screen a chance to re-layout before importing the deck
            setTimeout(function(){
                that.saveDeck(true);
            }, 1000);
        }

        function onInputChangeHandler() {

            // Immediately unsubscribe from the event so these don't stack up
            unbindFileHandler();

            // Get the 'file' object that the user picked (if any)
            var file = document.getElementById("browseInputDeckInput").files[0];
            if (file) {

                // Read the file as text.  Once we've read it, parse it back into an XML document
                // just as if we had gotten that XML document from the server
                var reader = new FileReader();
                reader.readAsText(file, "UTF-8");
                reader.onload = function (evt) {

                    // Finished reading. Release the file in case the user browses to the same file again later
                    $('#browseInputDeckInput').val('');

                    // Get the xml that we just read from the file
                    var importedXml = evt.target.result;

                    // Build a default deck name (Remove the extension from the deck name (if any))
                    var deckName = file.name;
                    var indexOfDot = deckName.indexOf('.');
                    deckName = deckName.substr(0, indexOfDot);


                    // See if we already have a deck with this name. Ask if they want to overwrite
                    that.comm.getDeck(deckName, function (deckFromServerXml) {
                        // See if we have a deck with that name with any cards in it
                        if (deckFromServerXml.firstChild && deckFromServerXml.firstChild.childNodes.length > 0) {
                            var result = confirm("You already have a deck of this name. Do you want to overwrite it?");
                            if (result) {
                                saveImportedNewDeck(importedXml, deckName);
                            }
                        } else {
                            saveImportedNewDeck(importedXml, deckName);
                        }
                    });

                }
                reader.onerror = function (evt) {
                    // Finished reading (albeit an error). Release the file in case the user browses to the same file again later
                    $('#browseInputDeckInput').val('');

                    alert("Deck import failed.");
                }
            }
        }

        // After we have registered, click on the hidden file-input element
        $('#browseInputDeckInput').get(0).click();

    },

    exportDeck: function(deckName, xml) {

        console.log("exportDeck hit!");

        // Need to fire up a confirm button so that we can do a proper 'click'
        var result = confirm("Export Deck?");
        if ( result ) {

            var xmlString = (new XMLSerializer()).serializeToString(xml);
            var elementId = "testExporterButtonId";
            var exportedDeckName = deckName + "_export.txt";

            var element = $('<a id=testExporterButtonId style="display:none">ClickMe</a>');
            element.attr('href', 'data:application/json;charset=utf-8,' + encodeURIComponent(xmlString));
            //element.attr('href', 'data:application/json;charset=utf-8,testdata123456');
            element.attr('download', exportedDeckName);

            var deckElem = $("<div class='deckItem'></div>");
            deckElem.append(element);

            setTimeout(function(){
              element.get(0).click();

              setTimeout(function() {
                element.remove();
              }, 2000);

            }, 2000);

        }

    }
});
