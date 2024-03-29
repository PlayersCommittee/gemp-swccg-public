#!/usr/bin/env python3

from requests import Session
import xmltodict
from pprint import PrettyPrinter
from time import sleep



def get_game_hall():
  ##
  ## Get game hall
  ##
  print("Getting Game Hall")
  response = session.get(
      url=gemp_base_url+'gemp-swccg-server/hall',
      headers={'Referer': gemp_base_url+'gemp-swccg/hall.html'}
  )

  hallxml = {}
  hallxml['hall'] = {}
  hallxml['hall']['table'] = []
  try:
    hallxml = xmltodict.parse(response.text)
  except Exception as e:
    print(e)

  print("Hall:")
  previous_id = 0
  if 'hall' in hallxml:
    if 'table' in hallxml['hall']:
      for t in hallxml['hall']['table']:
        print(t, type(t))
        try:
            row_id            = t["@id"]
            action            = t["@action"]
            format            = t["@format"]
            gameId            = t["@gameId"]
            players           = t["@players"].split(",")
            playing           = t["@playing"]
            status            = t["@status"]
            statusDescription = t["@statusDescription"]
            tournament        = t["@tournament"]
            watchable         = t["@watchable"]

            if row_id != previous_id:
              print("  * "+row_id+":")
              print("    * action...........:",action)
              print("    * format...........:",format)
              print("    * gameId...........:",gameId)
              for p in players:
                if username in p:
                  table_created = True
                  if status == "WAITING":
                    waiting_to_play = True
                  elif status == "PLAYING":
                    playing = True
                  elif status == "FINISHED":
                    finished = True
              print("    * players..........:",players[0])
              if len(players) > 1:
                print("                        ",players[1])
              print("    * playing..........:",playing)
              print("    * status...........:",status)
              print("    * statusDescription:",statusDescription)
              print("    * tournament.......:",tournament)
              print("    * watchable........:",watchable)
              print("")

              previous_id = row_id

        except Exception as e:
          print(e)
          #print(h)
          pp.pprint(t)
          pp.pprint(hallxml['hall']['table'])
          print("")




pp = PrettyPrinter(indent=4)

table_created = False
waiting_to_play = False
playing = False
game_id = ""
finished = False





session = Session()

gemp_base_url = 'http://0.0.0.0:8080/'
# If doing local testing, instead use the below line and
# provide the appropriate port:
#gemp_base_url = 'http://localhost:17010/'

# HEAD requests ask for *just* the headers, which is all you need to grab the
# session cookie
session.head(gemp_base_url)

username = "test1"
password = 'test'

##
## Login to server
##
print("Logging in to server with username "+username)
response = session.post(
    url=gemp_base_url + 'gemp-swccg-server/login',
    data={'login':username, 'password':password},
    headers={'Referer': gemp_base_url+'gemp-swccg/'}
)
dots = "............................................."
print(response.text)
print("Login response headers:")
for h in response.headers:
  print("  * "+h+dots[30-len(h)]+": "+response.headers[h])
if "Set-Cookie" not in response.headers:
  print("Set-Cookie header not found... Login failed. Bailing out.")
  exit(1)
print("")



##
## Start Game Server
##
print("Starting Game Server")
response = session.post(
    url=gemp_base_url+'gemp-swccg-server/admin/shutdown',
    headers={'Referer': gemp_base_url+'gemp-swccg/hall.html'},
    data={'enabled':'false'}
)
print("Game Server Start Response Headers:")
for h in response.headers:
  print("  * "+h+dots[30-len(h)]+": "+response.headers[h])
if response.ok:
  print("Game Server Started")
else:
  print("Game Server NOT started!... Bailing out.")
  print(response.status_code)
  exit(1)
print("")


## GET GAME HALL
get_game_hall()


##
## Create Table
##
if table_created and not finished:
  print("Table found. Not creating.")
else:
  print("Table not found.... CREATING.")
  response = session.post(
    url=gemp_base_url+'gemp-swccg-server/hall',
    data={
      'format': "open",
      'deckName': "AOBS",
      'sampleDeck': "",
      'tableDesc': "Testing is fun", # maxlen 50
      'isPrivate': "0",
    },
    headers={'Referer': gemp_base_url+'gemp-swccg/hall.html'}
  )

print("sleeping...")
sleep(10)
print("continue...")

## GET GAME HALL
get_game_hall()


#if not finished:
#  print("cancelling the game")
#  response = session.post(
#    url=gemp_base_url+"gemp-swccg-server/game/"+game_id+"/cancel",
#    data={
#      'participantId': username,
#    },
#    headers={'Referer': gemp_base_url+'gemp-swccg/game.html'}
#  )

