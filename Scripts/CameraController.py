
import bge
import BlenderData_pb2
import udpControllerV10


def test():

	cont = bge.logic.getCurrentController()
	own = cont.owner
	sens = cont.sensors['Property']

	if own['active'] is True:
		own.worldPosition.z = 2

		#contact java

		#make dino interactable with other dinos AIs

	if own['active'] is False:
		own.worldPosition.z = -2

def changeCamera():

	cont = bge.logic.getCurrentController()
	scene = bge.logic.getCurrentScene()
	keyboard = bge.logic.keyboard

	obj = cont.owner

	oneKey = bge.logic.KX_INPUT_JUST_RELEASED == keyboard.events[bge.events.ONEKEY]
	twoKey = bge.logic.KX_INPUT_JUST_RELEASED == keyboard.events[bge.events.TWOKEY]
	threeKey = bge.logic.KX_INPUT_JUST_RELEASED == keyboard.events[bge.events.THREEKEY]

	TKey = bge.logic.KX_INPUT_JUST_RELEASED == keyboard.events[bge.events.TKEY]
	YKey = bge.logic.KX_INPUT_JUST_RELEASED == keyboard.events[bge.events.YKEY]

	if oneKey:
		print("Change to cam 1")
            
	if twoKey:
		print("Change to cam 2")

	if threeKey:
		print("Change to cam 3")

	if TKey:
		currentCamera = scene.active_camera
		currentCam_lookAt = currentCamera.parent
		currentDino = currentCam_lookAt.parent
		currentDino['active'] = False

		scene.active_camera = "Default_Camera"
		newCamera = scene.active_camera
		camera_lookAt = newCamera.parent
		newDino = camera_lookAt.parent
		newDino['active'] = True
		

	if YKey:

		currentCamera = scene.active_camera
		currentCam_lookAt = currentCamera.parent
		currentDino = currentCam_lookAt.parent
		currentDino['active'] = False
		name = "Raptor"
				
		scene.active_camera = "Camera_Raptor"
		newCamera = scene.active_camera
		camera_lookAt = newCamera.parent
		newDino = camera_lookAt.parent
		newDino['active'] = True

	
	#currentCamera = scene.active_camera
	#currentCam_lookAt = currentCamera.parent
	#currentDino = currentCam_lookAt.parent

	#currentDino['X'] = currentDino.worldPosition.x
	#currentDino['Y'] = currentDino.worldPosition.y

	#sendPlayerPosition(currentDino)

def sendPlayerPosition():

	scene = bge.logic.getCurrentScene()
	
	currentCamera = scene.active_camera
	currentCam_lookAt = currentCamera.parent
	currentPlayer = currentCam_lookAt.parent
	
	player_position = BlenderData_pb2.BlenderData()

	playerPositionUpdate = player_position.command.add()
	playerPositionUpdate.commandType = "PLAYERPOSITION"
	playerPositionUpdate.uniqueObjectName = "Player"
	
	posX = currentPlayer.worldPosition.x
	posY = currentPlayer.worldPosition.y
	playerPositionUpdate.posX = posX
	playerPositionUpdate.posY = posY

	#print("Sending Position" + str(posX) + str(posY))

	if "Mosquito" in currentPlayer.name:
		playerPositionUpdate.dinoType = 3

	elif "Raptor" in currentPlayer.name:
		playerPositionUpdate.dinoType = 2

	udpControllerV10.sendPlayerPosition(player_position)
	


