#!/usr/bin/env python3

import json
import os.path
from pathlib import Path
import sqlite3
import re
import os
from colorama import (
  init,
  Fore,
  Back,
  Style
)

import urllib



def parse_side_json(side_json):

  global valid_cards
  global allowed
  global verboten

  for card in side_json['cards']:
    front = card['front']
    subtype = ""
    if "subType" in front:
      subtype = front["subType"]

    icons = []
    if 'icons' in card:
      icons = card['icons']
    if 'icons' in front:
      icons = front['icons']

    includecard = True

    
    if 'lightSideIcons' in front:
      ##
      ## Locations must have both light and dark icons.
      ##
      if front['lightSideIcons'] > 0 and front['darkSideIcons'] > 0:
        includecard=True
      else:
        includecard=False
        #print("  - NO FORCE: "+front['title'])

    
    ##
    ## Although these cards have no force generation, 
    ## create an exception that allows them to be used with a Sandcrawler and Suction Tube.
    ##
    if front['type'] == 'Location':
      if 'Sandcrawler' in front['title']:
        includecard=True

    
    ##
    ## Exception for Rancor pit.
    ## Rancor pit can be, possibly, 
    ## the only way to counter the Kalit+Luke's Hunting Rifle combo.
    ## Or at least it is the funniest.
    ##
    if 'Rancor' in front['title']:
      includecard=True



    
    ##
    ## No point in Jedi Tests
    ##
    if 'Jedi Test' in front['type']:
      includecard=False




    
    ##
    ## ALL Jawas are welcome
    ##
    if 'Character' in front['type']:
      if 'Alien' in subtype:
        if 'characteristics' in front:
          if 'Jawa' in front['characteristics']:
            includecard=True
          else:
            includecard=False
        else:
          includecard=False
      elif 'Droid' in subtype:
        includecard=True
      else:
        includecard=False


    
    if ('Starship' in front['type']):
      ##
      ## Unique named pilots, like "Zuckuss In Mist Hunter", are not permitted.
      ##
      if " In " in front['title']:
        includecard=False
      ##
      ## No ships with permanent pilots are allowed.
      ## While we could assume that the "permanent pilot" is a Jawa,
      ## it is unlikely that the Jawas were able to steal any of the ships that have them.
      ##
      if "Pilot" in icons:
        includecard=False
      ##
      ## No capital starships permitted
      ##
      if "Capital" in subtype:
        includecard=False
      ##
      ## No TIE Fighters, X-Wings, A-Wings, X-Wings, U-Wings, or B-Wings.
      ##
      if ("TIE" in subtype) or ("X-Wing" in subtype) or ("A-Wing" in subtype) or ("B-Wing" in subtype) or ("U-Wing" in subtype) or ("Blue Squadron" in front['title']) or ("Zeta-Class" in subtype):
        includecard=False


      ##
      ## no ships with permanent pilots
      ##
      if "Permanent pilot" in front['gametext']:
        includecard=False


    
    if front['type'] == 'Vehicle':
      ##
      ## No combat vehicles permitted.
      ## Only allow Creature and Transport vehicles
      ##
      if "Combat" in subtype:
        includecard=False
      ##
      ## No Ewok Vehicles
      ##
      if "Ewok" in front['title']:
        includecard=False

    
    if front['type'] == 'Effect':
      ##
      ## Political Effects require senators
      ## There are no Jawa sentors, so these are useless.
      ##
      if "Political" in subtype:
        includecard=False


    
    if 'Weapon' in front['type']:
      ##
      ## While not strictly "banned", these weapons are useless to a Jawa
      ##
      if "Death Star" in subtype:
        includecard=False
      if "Superlaser" in front['title']:
        includecard=False
      if "TIE" in front['title']:
        includecard=False
      if "SFS" in front['title']:
        includecard=False
      if "-wing" in front['title']:
        includecard=False

      if "AAT" in front['title']:
        includecard=False

      if "AT-AT Cannon" in front["title"]:
        includecard=False
      if "AT-ST Dual Cannon" in front["title"]:
        includecard=False

      if "STAP" in front['title']:
        includecard=False
      if "Stolen Stormtrooper Blaster Rifle" in front['title']:
        includecard=False
      if "Energy Shell" in front['title']:
        includecard=False
      if "Proton Torpedos" in front['title']:
        includecard=False
      if "Ewok" in front['title']:
        includecard=False
      if "'s Blaster" in front['title']:
        includecard=False
      if "'s Lightsaber" in front['title']:
        includecard=False
      if "'s Shoto Lightsaber" in front['title']:
        includecard=False
      if "Double-Bladed Lightsaber" in front['title']:
        includecard=False
      if "Battle Droid" in front['title']:
        includecard=False
      if "Droid Starfighter" in front['title']:
        includecard=False
      if "Tarpals" in front['title']:
        includecard=False
      if "Electropole" in front['title']:
        includecard=False
      if "Chewbacca's Bowcaster" in front['title']:
        includecard=False
      if "Darksaber" in front['title']:
        includecard=False
      if "F-110" in front['title']:
        includecard=False
      if "Stun Rifle" in front['title']:
        includecard=False
      if "Gaderffi Stick" in front['title']:
        includecard=False
      if "Gamorrean" in front['title']:
        includecard=False
      if "Turbolaser" in front['title']:
        includecard=False
      if "Neural Inhibitor" in front['title']:
        includecard=False
      if "Ion Canon" in front['title']:
        includecard=False
      if "Luke's Blaster Pistol" in front['title']:
        includecard=False
      if "Orbital Mine" in front['title']:
        includecard=False
      if "Zuckuss' Snare Rifle" in front['title']:
        includecard=False

      ##
      ## Bossk's Mortar Gun can be deployed on a Warrior 
      ##
      if "Bossk's Mortar Gun" in front['title']:
        includecard=True
      ##
      ## Dengar's Blaster Carbine can be deployed on a Warrior 
      ##
      if "Dengar's Blaster Carbine" in front['title']:
        includecard=True
      ##
      ## Dengar's Modified Riot Gun can not be deployed
      ##
      if "Dengar's Modified Riot Gun" in front['title']:
        includecard=False



    
    if 'Droid' in subtype:
      ##
      ## Permanent Weapons are only allowed on Jawas.
      ## No Droids with "Permanent weapon" permitted.
      ##
      if "Permanent weapon" in icons:
        includecard=False
      ##
      ## No droids which are immune to restraining bolts are permitted.
      ## Any droid which has presence is immune to a restraining bolt.
      ##
      if "Presence" in icons:
        includecard=False


    ##
    ## While not neccesarily explicitly Jawas, they CAN be Jawas if the right objective in play.
    ## As Light Side has Offworld Jawas available, 
    ## don't make it a *thing* and just assume the alien rabble/mob are always Jawas.
    ##
    if ('Alien Rabble' in front['title']) or ('Alien Mob' in front['title']):
      includecard=True


    cardtype = subtype + " " + front['type']
    if (subtype == ""):
      cardtype = front['type']

    setid = card['set']
    setabbr = sets[setid]
    side = card['side'].replace("Light", "LS").replace("Dark", "DS")
    if includecard:
      print(Fore.GREEN+"  * "+Fore.GREEN+side+" "+Fore.WHITE+"["+Fore.BLUE+setabbr+Fore.WHITE+"] "+Fore.WHITE+"("+Fore.CYAN+cardtype+Fore.WHITE+") "+Fore.GREEN+front['title'])
      valid_cards.append(card['gempId'])
      allowed.append(side+" "+"["+setabbr+"] "+"("+cardtype+") <a href=\""+front['imageUrl']+"\">"+front['title']+"</a>")
    else:
      print(Fore.RED+"  - "+Fore.RED+side+" "+Fore.WHITE+"["+Fore.MAGENTA+setabbr+Fore.WHITE+"] "+Fore.WHITE+"("+Fore.YELLOW+cardtype+Fore.WHITE+") "+Fore.RED+front['title'])
      verboten.append(side+" "+"["+setabbr+"] "+"("+cardtype+") <a href=\""+front['imageUrl']+"\">"+front['title']+"</a>")
      verboten_for_gemp.append(card['gempId'])


init()

global sets
sets = {}
global valid_cards
valid_cards=[]
global allowed
allowed = []
global verboten
verboten = []
global set_ids_for_gemp
set_ids_for_gemp = []
global verboten_for_gemp
verboten_for_gemp = []



from urllib.request import urlopen

source_json_urls = {
  "Dark":  {"url":"https://raw.githubusercontent.com/swccgpc/swccg-card-json/main/Dark.json"},
  "Light": {"url":"https://raw.githubusercontent.com/swccgpc/swccg-card-json/main/Light.json"},
  "sets":  {"url":"https://raw.githubusercontent.com/swccgpc/swccg-card-json/main/sets.json"},
}

print("\nLoading JSON Files:")
for n in source_json_urls:
  url = source_json_urls[n]['url']
  print("  * ["+n+"]: "+url)
  with urlopen(url) as response:
    response_json = json.loads(response.read())
    response_code = response.status
    if (response_code == 200):
      #print(type(response_json))
      source_json_urls[n]['json'] = response_json
    else:
      raise Exception("Failed to load json")
print("")


print("Parsing sets json")

for s in source_json_urls['sets']['json']:
  print("  * [",s['id'],"]:",s['abbr'])
  setid = s['id']
  sets[setid] = s['abbr']
  if (setid not in ["200d", "1000d"]):
    if int(setid) < 1000:
      set_ids_for_gemp.append(int(setid))


print("Parsing Dark json")
parse_side_json(source_json_urls['Dark']['json'])

print("Parsing Light json")
parse_side_json(source_json_urls['Light']['json'])

print("Sorting collections")

valid_cards.sort()
set_ids_for_gemp.sort()
allowed.sort()
verboten.sort()
verboten_for_gemp.sort()


print("Updating swccgFormats.json for gemp")

swccgFormats_json_file = "gemp-swccg-server/src/main/resources/swccgFormats.json"
swccgFormats_json = None
##
## Load the existing swccgFormats.json file in to memory
##
with open(swccgFormats_json_file) as fh:
  ##
  ## File is read in as a list.
  ## Join the list as a string.
  ## Parse the json string to create a dict
  ##
  swccgFormats_json_data = "".join(fh.readlines())
  swccgFormats_json = json.loads(swccgFormats_json_data)

##
## Set the Utinni banned card list in the existing swccgFormats.json data
##
if type(swccgFormats_json) != None:
  i = 0
  for f in swccgFormats_json:
    if "utinni" in f['name'].lower():
      print(f)
      swccgFormats_json[i]['banned'] = verboten_for_gemp
      swccgFormats_json[i]['set'] = set_ids_for_gemp
    i = i + 1

##
## Update the swccgFormats.json file
##
with open("gemp-swccg-server/src/main/resources/swccgFormats.json", "w") as fh:
  fh.write(json.dumps(swccgFormats_json, indent=4, sort_keys=True))

print("\ndone.\n")