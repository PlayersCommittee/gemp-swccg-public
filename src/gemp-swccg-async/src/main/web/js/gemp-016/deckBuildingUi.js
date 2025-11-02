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
    
    autoZoom: null,
    cardInfoDialog: null,


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
                    that.addCardToCollection(type, blueprintId, testingText, backSideTestingText, count, elem.getAttribute("side"), elem.getAttribute("contents"), elem.getAttribute("horizontal"));
                },
                function () {
                    that.finishCollection();
                });
        
        this.autoZoom = new AutoZoom("autoZoomInDeckbuilder");

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

        var validateDeckBut = $("<button title='Validate Deck'><span class='ui-icon ui-icon-check'></span></button>").button();
        this.manageDecksDiv.append(validateDeckBut);
        
        // Charlie Code
        var addLightShieldsBut = $("<button title='Add Light Shields'><span class='ui-icon ui-icon-squaresmall-plus'></span></button>").button();
        this.manageDecksDiv.append(addLightShieldsBut);

        // Charlie Code
        var addDarkShieldsBut = $("<button title='Add Dark Shields'><span class='ui-icon ui-icon-circlesmall-plus'></span></button>").button();
        this.manageDecksDiv.append(addDarkShieldsBut);

        if(this.autoZoom.autoZoomToggle != null) {
            this.autoZoom.autoZoomToggle.appendTo(this.manageDecksDiv);
        }
        
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

        validateDeckBut.click(
                function () {
                    that.validateDeck();
                });
        
        // Charlie Code
        addLightShieldsBut.click(
            function () {
                that.loadShields("LIGHT");
            });


        // Charlie Code
        addDarkShieldsBut.click(
            function () {
                that.loadShields("DARK");
            });
        

        this.collectionDiv = $("#collectionDiv");

        $("#collectionSelect").change(
                function () {
                    that.collectionType = that.getCollectionType();
                    that.cardFilter.getCollection();
                });

        this.normalCollectionDiv = $("<div id=\"normal-collection\"></div>");
        this.normalCollectionGroup = new NormalCardGroup(this.normalCollectionDiv, function (card) {
            return true;
        });
        this.normalCollectionGroup.maxCardHeight = 200;
        this.collectionDiv.append(this.normalCollectionDiv);

        this.drawDeckLabelDiv = $("<div id='deckZoneLabel'>Cards in deck</div>");
        this.drawDeckDiv = $("<div id=\"cards-in-deck\"></div>");
        this.drawDeckDiv.click(
                function () {
                    that.selectionFunc = that.addCardToDeckAndLayout;
                });
        this.deckGroup = new NormalCardGroup(this.drawDeckDiv, function (card) {
            return (card.zone == "deck");
        });
        this.deckGroup.maxCardHeight = 250;

        this.outsideDeckLabelDiv = $("<div id='outsideDeckZoneLabel'> Cards outside of deck (e.g. Defensive Shields, 'Hidden' Base, etc.)</div>");
        this.outsideDeckDiv = $("<div id=\"cards-outside-deck\"></div>");
        this.outsideDeckDiv.click(
                function () {
                    that.selectionFunc = that.addCardToOutsideDeckAndLayout;
                });
        this.outsideDeckGroup = new NormalCardGroup(this.outsideDeckDiv, function (card) {
            return (card.zone == "outsideDeck");
        });
        this.outsideDeckGroup.maxCardHeight = 100;

        this.bottomBarDiv = $("<div id=\"bottom-bar-container\"></div>");
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
        $("body")[0].addEventListener("contextmenu",
            function (event) {
                if(!that.clickCardFunction(event))
                {
                    event.preventDefault();
                    return false;
                }
                return true;
            });
        $('body').unbind('mouseover');
        $("body").mouseover(
            function (event) {
                return that.autoZoom.handleMouseOver(event.originalEvent, 
                   that.dragCardId != null, that.cardInfoDialog.isOpen());
            });

        $('body').unbind('mouseout');
        $("body").mouseout(
            function (event) {
                return that.autoZoom.handleMouseOut(event.originalEvent);
            });
        
        $("body").mousedown(
                function (event) {
                    that.autoZoom.handleMouseDown(event.originalEvent);
                    
                    return that.dragStartCardFunction(event);
                });
        $("body").mouseup(
                function (event) {
                    return that.dragStopCardFunction(event);
                });
        
        $('body').unbind('keydown');
        $("body").keydown(
            function (event) {
                return that.autoZoom.handleKeyDown(event.originalEvent);
            });

        $('body').unbind('keyup');
        $("body").keyup(
            function (event) {
                return that.autoZoom.handleKeyUp(event.originalEvent);
            });


        this.cardInfoDialog = new CardInfoDialog(window.innerWidth, window.innerHeight);

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

    /*
     * Popup box that is displayed when the user clicks on the "validate deck" button.
     */
    validateDeck:function () {
        var that = this;
        /* Create the validatedeck dialog box if it does not already exist */
        if (that.validateDeckDialog == null) {
            that.validateDeckDialog = $("<div id=\"validate-deck-dialog\"></div>").dialog({
                title:"Validate Deck",
                autoOpen:false,
                closeOnEscape:true,
                resizable:true,
                width:800,
                height:600,
                modal:true
            });
        } // if
        that.validateDeckDialog.html("");
        that.validateDeckDialog.dialog("open");

        var deckContents = this.getDeckContents();
        console.log("deckContents: [",deckContents,"] (",typeof deckContents,")");
        if ((deckContents != null) && (deckContents != "|")) {
            this.comm.getDeckStats(deckContents,
                    function (html) {
                        $("#validate-deck-dialog").html(html);
                        setTimeout(
                                function () {
                                    that.checkDeckStatsDirty();
                                }, that.checkDirtyInterval);
                    }, {
                "400":function () {
                    $("#validate-deck-dialog").html("<span id=\"deckstats-invalid-deck\">Invalid deck for getting stats.</span>");
                }
            });
        } else {
            $("#validate-deck-dialog").html("<span id=\"deckstats-deck-has-no-cards\">Deck has no cards</span>");
            setTimeout(
                    function () {
                        that.checkDeckStatsDirty();
                    }, that.checkDirtyInterval);
        }





    }, // validateDeck function


    generateDeckRow:function (decks, prefix, sampleDeck) {
        var that = this;
        for (var i = 0; i < decks.length; i++) {
            var deck = decks[i];

            // Sanity-check the deck
            if (!decks[i].childNodes || (decks[i].childNodes.length == 0)) {
                // This deck is messed up.  Just skip it
                continue;
            }


            var deckName = decks[i].childNodes[0].nodeValue;
            var deckDownloadName = deckName + ".txt";
            var openDeckBut = $("<button title='Open deck'><span class='ui-icon ui-icon-folder-open'></span></button>").button();
            var deckListBut = $("<button title='Deck list'><span class='ui-icon ui-icon-clipboard'></span></button>").button();
            var deleteDeckBut = $("<button title='Delete deck'><span class='ui-icon ui-icon-trash'></span></button>").button();
            var exportDeckBut = $("<button title='Export deck'><span class='ui-icon ui-icon-arrowthickstop-1-s'></span></button>").button();
            var hiddenDeckDownloadLink = $('<a style="height: 0px; width: 0px; display: inline-block"></a>');

            var deckElem = $("<div class='deckItem'></div>");
            deckElem.append(openDeckBut);
            deckElem.append(deckListBut);
            deckElem.append(exportDeckBut);
            deckElem.append(hiddenDeckDownloadLink);
            if (!sampleDeck) {
                deckElem.append(deleteDeckBut);
            }
            deckElem.append($("<div/>").text(prefix + deckName).html());

            // Set up deck download button
            var deckLink = this.comm.getPrettyDeckLink(encodeURIComponent(deckName));
            hiddenDeckDownloadLink.attr('href', deckLink);
            hiddenDeckDownloadLink.attr('download', deckDownloadName);

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
                    (function (hiddenDeckDownloadLink) {
                        return function() {
                            /* Call to the DeckRequestHandler getDeck */
                            hiddenDeckDownloadLink[0].click();
                        };
                    })(hiddenDeckDownloadLink));
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

        if (!this.successfulDrag && this.cardInfoDialog.isOpen()) {
            this.cardInfoDialog.mouseUp();
            event.stopPropagation();
            return false;
        }

        if (tar.hasClass("actionArea")) {
            var selectedCardElem = tar.closest(".card");
            if (event.which >= 1) {
                if (!this.successfulDrag) {
                    if (event.shiftKey || event.which > 1) {
                        this.cardInfoDialog.showCard(selectedCardElem.data("card"));
                        return false;
                    } else if (selectedCardElem.hasClass("cardInCollection")) {
                        var cardData = selectedCardElem.data("card");
                        this.selectionFunc(cardData.blueprintId, cardData.testingText, cardData.backSideTestingText, cardData.horizontal);
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
                            var card = new Card(blueprintIds[i], cardData.testingText, cardData.backSideTestingText, cardData.horizontal, "selection", "selection" + i, "player");
                            var cardDiv = Card.CreateCardDiv(card.imageUrl, card.testingText, null, card.isFoil(), false, card.isPack(), card.incomplete);
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
                this.cardInfoDialog.showCard(this.dragCardData);
                this.successfulDrag = true;
            }
            this.dragCardData = null;
            this.dragStartX = null;
            this.dragStartY = null;
            return false;
        }
        return true;
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
        else if(this.deckName.length > 45)
            alert("Deck name cannot be longer than 45 characters.");
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

    addCardToContainer:function (blueprintId, testingText, backSideTestingText, horizontal, zone, container, tokens) {
        var card = new Card(blueprintId, testingText, backSideTestingText, horizontal, zone, "deck", "player");
        var cardDiv = Card.CreateCardDiv(card.imageUrl, card.testingText, null, card.isFoil(), tokens, card.isPack(), card.incomplete);
        cardDiv.data("card", card);
        container.append(cardDiv);
        return cardDiv;
    },

    addCardToDeckAndLayout:function (blueprintId, testingText, backSideTestingText, horizontal) {
        var that = this;

        this.addCardToDeck(blueprintId, testingText, backSideTestingText, horizontal);
        that.deckGroup.layoutCards();

        that.deckModified(true);
    },

    addCardToOutsideDeckAndLayout:function (blueprintId, testingText, backSideTestingText, horizontal) {
        var that = this;
        var cardDiv = this.addCardToContainer(blueprintId, testingText, backSideTestingText, horizontal, "outsideDeck", that.outsideDeckDiv, false);
        cardDiv.addClass("cardOutsideDeck");
        that.outsideDeckGroup.layoutCards()
        that.deckDirty = true;
        that.deckModified(true);
    },

    // Charlie Code
    loadShields: function (side) {
        $(".cardOutsideDeck").remove();
        var that = this;
        var shieldUrl = "/gemp-swccg-server/collection/default?participantId=null&filter=side%3A" + side + "+format%3Aall+cardType%3ADEFENSIVE_SHIELD+sort%3Aname%2Cset%2CcardType+product%3Acard&start=0&count=100&_=1726509590294"
        this.comm.loadShields(shieldUrl, function (xml) {
              var $xml = $(xml);
              var blueprintIds = $xml.find('card').map(function() {
                  return $(this).attr('blueprintId');
              }).get();
              for (let blueprintId of blueprintIds) {
                  var cardDiv = that.addCardToContainer(blueprintId, null, null, false, "outsideDeck", that.outsideDeckDiv, false);
                  cardDiv.addClass("cardOutsideDeck");
              };
              if (side === "LIGHT") {
                var addMythrol = that.addCardToContainer("200_16", null, null, false, "outsideDeck", that.outsideDeckDiv, false);
                addMythrol.addClass("cardOutsideDeck");
              };
              that.outsideDeckGroup.layoutCards();
              that.deckDirty = true;
              that.deckModified(true);
          }, {
                "400":function ()
                {
                    alert("Could not locate shields");
                }
          });
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

    addCardToDeck:function (blueprintId, testingText, backSideTestingText, horizontal) {
        var that = this;
        var added = false;
        $(".card.cardInDeck", this.drawDeckDiv).each(
                function () {
                    var cardData = $(this).data("card");
                    if (cardData.blueprintId == blueprintId) {
                        var attDiv = that.addCardToContainer(blueprintId, testingText, backSideTestingText, horizontal, "attached", that.drawDeckDiv, false);
                        cardData.attachedCards.push(attDiv);
                        added = true;
                    }
                });
        if (!added) {
            var div = this.addCardToContainer(blueprintId, testingText, backSideTestingText, horizontal, "deck", this.drawDeckDiv, false)
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
                    $("#deckStats").html("<span id=\"deckstats-invalid-deck\">Invalid deck for getting stats.</span>");
                }
            });
        } else {
            $("#deckStats").html("<span id=\"deckstats-deck-has-no-cards\">Deck has no cards</span>");
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
                this.addCardToDeck(cards[i].getAttribute("blueprintId"), cards[i].getAttribute("testingText"), cards[i].getAttribute("backSideTestingText"), cards[i].getAttribute("horizontal"));

            var cardsOutsideDeck = root.getElementsByTagName("cardOutsideDeck");
            for (var i = 0; i < cardsOutsideDeck.length; i++)
                this.addCardToContainer(cardsOutsideDeck[i].getAttribute("blueprintId"), cardsOutsideDeck[i].getAttribute("testingText"), cardsOutsideDeck[i].getAttribute("backSideTestingText"), cardsOutsideDeck[i].getAttribute("horizontal"), "outsideDeck", this.outsideDeckDiv, false).addClass("cardOutsideDeck");

            this.layoutUI(false);

            this.cardFilter.getCollection();
        }
        this.deckModified(false);
    },

    clearCollection:function () {
        $(".card", this.normalCollectionDiv).remove();
    },

    addCardToCollection:function (type, blueprintId, testingText, backSideTestingText, count, side, contents, horizontal) {
        if (type == "pack") {
            if (blueprintId.substr(0, 3) == "(S)") {
                var card = new Card(blueprintId, null, null, horizontal, "pack", "selection", "player");
                card.tokens = {"count":count};
                var cardDiv = Card.CreateCardDiv(card.imageUrl, card.testingText, null, false, true, true, false, card.incomplete);
                cardDiv.data("card", card);
                cardDiv.data("selection", contents);
                cardDiv.addClass("selectionInCollection");
            } else {
                var card = new Card(blueprintId, null, null, horizontal, "pack", "collection", "player");
                card.tokens = {"count":count};
                var cardDiv = Card.CreateCardDiv(card.imageUrl, card.testingText, null, false, true, true, false, card.incomplete);
                cardDiv.data("card", card);
                cardDiv.addClass("packInCollection");
            }
            this.normalCollectionDiv.append(cardDiv);
        } else if (type == "card") {
            var card = new Card(blueprintId, testingText, backSideTestingText, horizontal, "card", "collection", "player");
            var countInDeck = 0;
            $(".card", this.deckDiv).each(
                    function () {
                        var tempCardData = $(this).data("card");
                        if (blueprintId == tempCardData.blueprintId)
                            countInDeck++;
                    });
            card.tokens = {"count":count - countInDeck};
            var cardDiv = Card.CreateCardDiv(card.imageUrl, card.testingText, null, card.isFoil(), true, false, card.incomplete);
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

                    // If we couldn't get the deck name for some reason, generate a random name.
                    if (!deckName || deckName.length == 0) {
                        deckName = "importedDeck" + Math.floor(Math.random() * 100000)
                    }


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

    }
});
